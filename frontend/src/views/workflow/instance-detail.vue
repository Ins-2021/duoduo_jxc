<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <el-button @click="goBack">返回</el-button>
        <el-button @click="load">刷新</el-button>
      </div>

      <el-descriptions title="流程实例" :column="2" border>
        <el-descriptions-item label="实例ID">{{ procInstId }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ wfInstanceStatusLabel(detail?.instance?.status) }}</el-descriptions-item>
        <el-descriptions-item label="bizType">{{ detail?.instance?.bizType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="bizId">{{ detail?.instance?.bizId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ detail?.instance?.title || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <div class="grid">
      <el-card>
        <div class="section-title">当前任务</div>
        <el-table :data="detail?.activeTasks || []" stripe>
          <el-table-column prop="taskId" label="任务ID" width="220" />
          <el-table-column prop="taskName" label="任务名称" />
          <el-table-column prop="nodeKey" label="节点Key" width="180" />
          <el-table-column prop="assignee" label="办理人" width="140" />
        </el-table>
      </el-card>

      <el-card>
        <div class="section-title">流转记录</div>
        <el-table :data="detail?.actionLogs || []" stripe>
          <el-table-column prop="action" label="动作" width="120" />
          <el-table-column prop="operatorId" label="操作人" width="120" />
          <el-table-column prop="comment" label="意见" />
          <el-table-column prop="createdAt" label="时间" width="180" />
        </el-table>
      </el-card>
    </div>

    <el-card>
      <div class="section-title">流程图</div>
      <div ref="canvasRef" class="canvas" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import NavigatedViewer from 'bpmn-js/lib/NavigatedViewer'
import 'bpmn-js/dist/assets/diagram-js.css'
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn.css'
import { getWorkflowDiagram, getWorkflowDiagramState, getWorkflowInstanceDetail } from '@/api/workflow'
import { getDict } from '@/api/dict'

const route = useRoute()
const router = useRouter()

const procInstId = String(route.params.procInstId)
const detail = ref<any>(null)
const wfInstanceStatusMap = ref<Record<string, string>>({})

const wfInstanceStatusLabel = (value: any) => {
  const v = value == null ? '' : String(value)
  if (!v) return '-'
  return wfInstanceStatusMap.value[v] ?? v
}

const canvasRef = ref<HTMLDivElement>()
let viewer: any = null

const goBack = () => {
  router.push('/workflow/mine')
}

const applyMarkers = async (state: any) => {
  const canvas = viewer.get('canvas')
  const active: string[] = state?.activeNodeKeys || []
  const finished: string[] = state?.finishedNodeKeys || []
  for (const id of finished) {
    canvas.addMarker(id, 'dd-finished')
  }
  for (const id of active) {
    canvas.addMarker(id, 'dd-active')
  }
}

const load = async () => {
  const [d1, d2, d3]: any = await Promise.all([
    getWorkflowInstanceDetail(procInstId),
    getWorkflowDiagram(procInstId),
    getWorkflowDiagramState(procInstId)
  ])
  detail.value = d1.data
  const xml = d2.data
  await viewer.importXML(xml)
  const canvas = viewer.get('canvas')
  canvas.zoom('fit-viewport')
  await applyMarkers(d3.data)
}

onMounted(async () => {
  viewer = new NavigatedViewer({
    container: canvasRef.value
  })
  try {
    const res: any = await getDict('wf_instance_status')
    const map: Record<string, string> = {}
    ;(res.data || []).forEach((i: any) => {
      map[String(i.value)] = i.label
    })
    wfInstanceStatusMap.value = map
  } catch {
    wfInstanceStatusMap.value = {}
  }
  await load()
})

onBeforeUnmount(() => {
  if (viewer) {
    viewer.destroy()
    viewer = null
  }
})
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.section-title {
  font-weight: 600;
  margin-bottom: 12px;
}

.canvas {
  height: 520px;
  border: 1px solid #e6e6e6;
  background: #fff;
}
</style>

<style>
.dd-active:not(.djs-connection) .djs-visual > :nth-child(1) {
  stroke: #409eff !important;
  stroke-width: 4px !important;
}

.dd-finished:not(.djs-connection) .djs-visual > :nth-child(1) {
  stroke: #67c23a !important;
  stroke-width: 3px !important;
}
</style>
