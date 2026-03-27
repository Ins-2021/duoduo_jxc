<template>
  <div class="designer">
    <el-card>
      <div class="toolbar">
        <div class="left">
          <el-button @click="goBack">返回</el-button>
          <el-button type="primary" @click="save">保存</el-button>
          <el-button @click="validate">校验</el-button>
          <el-button type="success" @click="publish">发布</el-button>
        </div>
        <div class="right">
          <span class="label">操作人ID</span>
          <el-input-number v-model="operatorId" :min="0" :step="1" />
        </div>
      </div>
    </el-card>

    <div class="body">
      <div ref="canvasRef" class="canvas" />
      <div class="side">
        <el-card>
          <div class="side-title">节点配置</div>
          <div v-if="selectedType !== 'bpmn:UserTask'" class="hint">
            请选择一个审批节点（UserTask）进行配置
          </div>
          <el-form v-else label-width="90px">
            <el-form-item label="节点名称">
              <el-input v-model="selectedName" @change="updateNodeName" />
            </el-form-item>
            <el-form-item label="审批人ID">
              <el-input
                v-model="approverIdsText"
                placeholder="多个用逗号分隔，例如 1,2,3"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="applyApprovers">应用到节点</el-button>
            </el-form-item>
            <div class="hint">
              节点会写入 documentation：dd-config:{"approverUserIds":[...]}
            </div>
          </el-form>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Modeler from 'bpmn-js/lib/Modeler'
import 'bpmn-js/dist/assets/diagram-js.css'
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn.css'
import { getWorkflowModel, publishWorkflowModel, saveWorkflowModel, validateWorkflowModel } from '@/api/workflow'

const route = useRoute()
const router = useRouter()

const operatorId = ref<number>(1)
const modelId = Number(route.params.id)

const canvasRef = ref<HTMLDivElement>()
let modeler: any = null

const selectedType = ref('')
const selectedName = ref('')
const approverIdsText = ref('')
let selectedElement: any = null

const goBack = () => {
  router.push('/workflow/models')
}

const parseApproverIds = (text: string) => {
  return text
    .split(',')
    .map(s => s.trim())
    .filter(Boolean)
    .map(s => Number(s))
    .filter(n => Number.isFinite(n) && n >= 0)
}

const load = async () => {
  const res: any = await getWorkflowModel(modelId)
  const xml = res.data?.bpmnXml
  if (!xml) {
    ElMessage.error('模型XML为空')
    return
  }
  await modeler.importXML(xml)
  const canvas = modeler.get('canvas')
  canvas.zoom('fit-viewport')
}

const save = async () => {
  const { xml } = await modeler.saveXML({ format: true })
  await saveWorkflowModel(modelId, { bpmnXml: xml }, operatorId.value)
  ElMessage.success('保存成功')
}

const validate = async () => {
  await validateWorkflowModel(modelId)
  ElMessage.success('校验通过')
}

const publish = async () => {
  await publishWorkflowModel(modelId, { publishedBy: operatorId.value })
  ElMessage.success('发布成功')
}

const updateNodeName = () => {
  if (!selectedElement) return
  const modeling = modeler.get('modeling')
  modeling.updateProperties(selectedElement, { name: selectedName.value })
}

const readDdConfig = (bo: any) => {
  const docs = bo?.documentation || []
  for (const d of docs) {
    const txt = (d?.text || '').trim()
    if (txt.startsWith('dd-config:')) {
      const json = txt.slice('dd-config:'.length).trim()
      try {
        return JSON.parse(json)
      } catch {
        return null
      }
    }
  }
  return null
}

const writeDdConfig = (bo: any, cfg: any) => {
  const moddle = modeler.get('moddle')
  const txt = `dd-config:${JSON.stringify(cfg)}`
  const doc = moddle.create('bpmn:Documentation', { text: txt })
  bo.documentation = [doc]
}

const applyApprovers = () => {
  if (!selectedElement) return
  const ids = parseApproverIds(approverIdsText.value)
  const bo = selectedElement.businessObject
  writeDdConfig(bo, { approverUserIds: ids })
  ElMessage.success('已写入节点配置')
}

const onSelectionChanged = (e: any) => {
  selectedElement = e.newSelection?.[0]
  if (!selectedElement) {
    selectedType.value = ''
    selectedName.value = ''
    approverIdsText.value = ''
    return
  }
  const bo = selectedElement.businessObject
  selectedType.value = bo?.$type || ''
  selectedName.value = bo?.name || ''
  const cfg = readDdConfig(bo)
  const ids = cfg?.approverUserIds || []
  approverIdsText.value = Array.isArray(ids) ? ids.join(',') : ''
}

onMounted(async () => {
  modeler = new Modeler({
    container: canvasRef.value
  })
  modeler.on('selection.changed', onSelectionChanged)
  await load()
})

onBeforeUnmount(() => {
  if (modeler) {
    modeler.destroy()
    modeler = null
  }
})
</script>

<style scoped>
.designer {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.label {
  margin-right: 8px;
  color: #606266;
}

.right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.body {
  display: flex;
  gap: 12px;
}

.canvas {
  flex: 1;
  height: calc(100vh - 210px);
  background: #fff;
  border: 1px solid #e6e6e6;
}

.side {
  width: 360px;
}

.side-title {
  font-weight: 600;
  margin-bottom: 12px;
}

.hint {
  color: #909399;
  font-size: 12px;
  line-height: 18px;
}
</style>

