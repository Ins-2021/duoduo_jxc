<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <div class="left">
          <el-button type="primary" @click="load">刷新</el-button>
        </div>
      </div>

      <el-divider content-position="left">发起流程（测试入口）</el-divider>
      <el-form label-width="90px" style="max-width: 720px">
        <el-form-item label="bizType">
          <el-input v-model="startForm.bizType" placeholder="例如 DEMO / PURCHASE_ORDER" />
        </el-form-item>
        <el-form-item label="bizId">
          <el-input v-model="startForm.bizId" placeholder="例如 10001" />
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="startForm.title" placeholder="例如 采购单 PO2026xxx" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="start">发起</el-button>
        </el-form-item>
      </el-form>

      <el-divider content-position="left">我发起的</el-divider>
      <el-table :data="rows" stripe>
        <el-table-column prop="procInstId" label="实例ID" width="220" />
        <el-table-column prop="bizType" label="bizType" width="140" />
        <el-table-column prop="bizId" label="bizId" width="140" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <span>{{ wfInstanceStatusLabel(row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row.procInstId)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getDict } from '@/api/dict'
import { listMyWorkflowInstances, startWorkflowInstance } from '@/api/workflow'

const router = useRouter()

const rows = ref<any[]>([])
const wfInstanceStatusMap = ref<Record<string, string>>({})

const wfInstanceStatusLabel = (value: any) => {
  const v = value == null ? '' : String(value)
  return wfInstanceStatusMap.value[v] ?? v
}

const startForm = reactive({
  bizType: 'DEMO',
  bizId: '10001',
  title: '演示单据 10001'
})

const load = async () => {
  const res: any = await listMyWorkflowInstances()
  rows.value = res.data || []
}

const start = async () => {
  const res: any = await startWorkflowInstance({
    bizType: startForm.bizType,
    bizId: startForm.bizId,
    title: startForm.title
  })
  ElMessage.success('已发起')
  openDetail(res.data.procInstId)
}

const openDetail = (procInstId: string) => {
  router.push(`/workflow/instance/${procInstId}`)
}

onMounted(async () => {
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

.left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.label {
  color: #606266;
}
</style>
