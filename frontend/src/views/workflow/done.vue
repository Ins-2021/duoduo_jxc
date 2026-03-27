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
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { listDoneTasks } from '@/api/workflow'

const rows = ref<any[]>([])

const load = async () => {
  const res: any = await listDoneTasks()
  rows.value = res.data || []
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
