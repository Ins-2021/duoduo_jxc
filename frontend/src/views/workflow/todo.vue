<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <div class="left">
          <el-button type="primary" @click="load">刷新</el-button>
        </div>
      </div>

      <el-table :data="rows" stripe>
        <el-table-column prop="taskId" label="任务ID" width="220" />
        <el-table-column prop="taskName" label="任务名称" />
        <el-table-column prop="nodeKey" label="节点Key" width="180" />
        <el-table-column prop="procInstId" label="实例ID" width="220" />
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row.procInstId)">详情</el-button>
            <el-button link type="success" @click="openApprove(row)">同意</el-button>
            <el-button link type="danger" @click="openReject(row)">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="approveVisible" title="同意" width="520px">
      <el-form label-width="80px">
        <el-form-item label="意见">
          <el-input v-model="comment" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button type="primary" @click="doApprove">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="rejectVisible" title="驳回" width="520px">
      <el-form label-width="80px">
        <el-form-item label="类型">
          <el-select v-model="rejectType" style="width: 240px">
            <el-option label="驳回到首节点" value="TO_START" />
            <el-option label="驳回到上一节点" value="TO_PREV" />
          </el-select>
        </el-form-item>
        <el-form-item label="意见">
          <el-input v-model="comment" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" @click="doReject">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { approveTask, listTodoTasks, rejectTask } from '@/api/workflow'

const router = useRouter()

const rows = ref<any[]>([])

const approveVisible = ref(false)
const rejectVisible = ref(false)
const currentTaskId = ref<string>('')
const comment = ref('')
const rejectType = ref('TO_START')

const load = async () => {
  const res: any = await listTodoTasks()
  rows.value = res.data || []
}

const openDetail = (procInstId: string) => {
  router.push(`/workflow/instance/${procInstId}`)
}

const openApprove = (row: any) => {
  currentTaskId.value = row.taskId
  comment.value = ''
  approveVisible.value = true
}

const openReject = (row: any) => {
  currentTaskId.value = row.taskId
  comment.value = ''
  rejectType.value = 'TO_START'
  rejectVisible.value = true
}

const doApprove = async () => {
  await approveTask(currentTaskId.value, { comment: comment.value })
  approveVisible.value = false
  ElMessage.success('已同意')
  await load()
}

const doReject = async () => {
  await rejectTask(currentTaskId.value, { comment: comment.value, rejectType: rejectType.value })
  rejectVisible.value = false
  ElMessage.success('已驳回')
  await load()
}

onMounted(load)
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
