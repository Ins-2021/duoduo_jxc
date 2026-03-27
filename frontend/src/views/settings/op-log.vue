<template>
  <div class="oplog-container">
    <el-card class="oplog-card" shadow="never">
      <div class="toolbar">
        <div class="left">
          <el-button size="small" v-perm="'settings:op-log:edit'" @click="clearLogs">清空</el-button>
        </div>
        <div class="right">
          <el-input v-model="queryParams.content" size="small" placeholder="操作内容" style="width: 220px" clearable />
          <el-select v-model="queryParams.operatorId" size="small" placeholder="全部制单人" style="width: 180px" clearable>
            <el-option label="全部制单人" :value="null" />
            <el-option v-for="item in staffOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
          <el-date-picker
            v-model="queryParams.startDate"
            type="datetime"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="开始时间"
            size="small"
            style="width: 220px"
          />
          <el-date-picker
            v-model="queryParams.endDate"
            type="datetime"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="结束时间"
            size="small"
            style="width: 220px"
          />
          <el-button size="small" @click="loadData">搜索</el-button>
        </div>
      </div>

      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="storeName" label="所属门店" align="center" width="120" />
        <el-table-column prop="createTime" label="操作时间" align="center" width="170" />
        <el-table-column prop="content" label="操作内容" align="center" />
        <el-table-column prop="operatorName" label="职员" align="center" width="120" />
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 30, 50, 100]"
          layout="prev, pager, next, jumper, total, sizes"
          :total="total"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

type OptionItem = { label: string; value: number }

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)

const staffOptions = ref<OptionItem[]>([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 30,
  content: '',
  operatorId: null as number | null,
  startDate: '',
  endDate: ''
})

const loadStaffOptions = async () => {
  const res = await request.get('/options/staff')
  if (res.code === 200) {
    staffOptions.value = res.data || []
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/op-log/page', { params: queryParams })
    if (res.code === 200) {
      tableData.value = res.data.list
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

const clearLogs = async () => {
  try {
    await ElMessageBox.confirm('确定要清空操作日志吗？', '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  const res = await request.delete('/op-log/clear')
  if (res.code === 200) {
    ElMessage.success('清空成功')
    queryParams.pageNum = 1
    loadData()
  }
}

onMounted(async () => {
  await loadStaffOptions()
  await loadData()
})
</script>

<style scoped>
.oplog-container {
  height: 100%;
}

.oplog-card :deep(.el-card__body) {
  padding: 10px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.toolbar .right {
  display: flex;
  gap: 10px;
  align-items: center;
}

.pagination-container {
  margin-top: 10px;
  display: flex;
  justify-content: flex-start;
}
</style>
