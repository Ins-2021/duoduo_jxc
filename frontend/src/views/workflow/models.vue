<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <div class="left">
          <el-button type="primary" @click="openCreate">新建流程</el-button>
          <el-button @click="load">刷新</el-button>
        </div>
        <div class="right">
          <span class="label">操作人ID</span>
          <el-input-number v-model="operatorId" :min="0" :step="1" />
        </div>
      </div>

      <el-table :data="rows" stripe>
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="modelKey" label="Key" width="200" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="category" label="分类" width="160" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <span>{{ wfModelStatusLabel(row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240">
          <template #default="{ row }">
            <el-button link type="primary" @click="goDesigner(row.id)">编辑</el-button>
            <el-button link @click="doValidate(row.id)">校验</el-button>
            <el-button link type="success" @click="doPublish(row.id)">发布</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="createVisible" title="新建流程" width="520px">
      <el-form :model="createForm" label-width="90px">
        <el-form-item label="流程Key">
          <el-input v-model="createForm.modelKey" placeholder="例如: purchase_approve" />
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model="createForm.name" placeholder="例如: 采购审批流程" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="createForm.category" placeholder="可选" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="doCreate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getDict } from '@/api/dict'
import {
  createWorkflowModel,
  listWorkflowModels,
  publishWorkflowModel,
  validateWorkflowModel
} from '@/api/workflow'

const router = useRouter()

const operatorId = ref<number>(1)
const rows = ref<any[]>([])
const wfModelStatusMap = ref<Record<string, string>>({})

const wfModelStatusLabel = (value: any) => {
  const v = value == null ? '' : String(value)
  return wfModelStatusMap.value[v] ?? v
}

const createVisible = ref(false)
const createForm = reactive({
  modelKey: '',
  name: '',
  category: ''
})

const load = async () => {
  const res: any = await listWorkflowModels()
  rows.value = res.data || []
}

const openCreate = () => {
  createForm.modelKey = ''
  createForm.name = ''
  createForm.category = ''
  createVisible.value = true
}

const doCreate = async () => {
  const res: any = await createWorkflowModel(
    { modelKey: createForm.modelKey, name: createForm.name, category: createForm.category },
    operatorId.value
  )
  createVisible.value = false
  ElMessage.success('创建成功')
  goDesigner(res.data)
}

const goDesigner = (id: number) => {
  router.push(`/workflow/designer/${id}`)
}

const doValidate = async (id: number) => {
  await validateWorkflowModel(id)
  ElMessage.success('校验通过')
}

const doPublish = async (id: number) => {
  await publishWorkflowModel(id, { publishedBy: operatorId.value })
  ElMessage.success('发布成功')
  await load()
}

onMounted(async () => {
  try {
    const res: any = await getDict('wf_model_status')
    const map: Record<string, string> = {}
    ;(res.data || []).forEach((i: any) => {
      map[String(i.value)] = i.label
    })
    wfModelStatusMap.value = map
  } catch {
    wfModelStatusMap.value = {}
  }
  load()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
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
</style>
