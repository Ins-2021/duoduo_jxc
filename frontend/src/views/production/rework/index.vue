<template>
  <el-card shadow="never">
    <!-- 工具栏 -->
    <div class="toolbar" style="display: flex; justify-content: space-between; margin-bottom: 20px;">
      <div class="left-actions">
        <el-button type="primary" v-perm="'production:rework:add'" @click="handleAdd">新增返工</el-button>
      </div>
      <div class="right-search" style="display: flex; gap: 10px;">
        <el-select v-model="queryParams.status" placeholder="返工状态" clearable style="width: 150px">
          <el-option label="待处理" value="pending"/>
          <el-option label="处理中" value="in_progress"/>
          <el-option label="已完成" value="completed"/>
        </el-select>
        <el-input v-model="queryParams.keyword" placeholder="返工单号/扎包号" clearable style="width: 200px" @keyup.enter="handleQuery"/>
        <el-button type="primary" @click="handleQuery">查询</el-button>
      </div>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading" border>
      <el-table-column prop="reworkNo" label="返工单号" width="160"/>
      <el-table-column prop="bundleNo" label="扎包号" width="140"/>
      <el-table-column prop="processName" label="返工工序" width="120"/>
      <el-table-column prop="defectType" label="缺陷类型" width="120"/>
      <el-table-column prop="quantity" label="返工数量" width="100"/>
      <el-table-column prop="originalWorkerName" label="原操作员" width="120"/>
      <el-table-column prop="reworkWorkerName" label="返工操作员" width="120"/>
      <el-table-column prop="deductionAmount" label="扣款金额" width="120">
        <template #default="{row}">
          <span v-if="row.deductionAmount > 0" style="color: #f56c6c; font-weight: bold;">-¥{{ row.deductionAmount.toFixed(2) }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{row}">
          <el-tag :type="getStatusType(row.status)">{{ getStatusName(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="160"/>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{row}">
          <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
          <el-button link type="primary" v-perm="'production:rework:complete'" @click="handleComplete(row)" v-if="row.status === 'in_progress'">完成</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div style="margin-top: 20px; display: flex; justify-content: flex-end;">
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

defineOptions({ name: 'ProductionReworkList' })

const route = useRoute()
const loading = ref(false)
const total = ref(0)
const tableData = ref<any[]>([])

const queryParams = reactive({
  page: 1,
  size: 10,
  status: '',
  keyword: (route.query.qcNo as string) || ''
})

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    pending: 'info',
    in_progress: 'warning',
    completed: 'success'
  }
  return map[status] || 'info'
}

const getStatusName = (status: string) => {
  const map: Record<string, string> = {
    pending: '待处理',
    in_progress: '处理中',
    completed: '已完成'
  }
  return map[status] || status
}

const handleQuery = () => {
  loading.value = true
  setTimeout(() => {
    tableData.value = [
      { id: 1, reworkNo: 'RW20260329001', bundleNo: 'B260328012', processName: '缝纫', defectType: '跳线', quantity: 2, originalWorkerName: '王五', reworkWorkerName: '王五', deductionAmount: 5.0, status: 'in_progress', createdAt: '2026-03-29 10:30:00' },
      { id: 2, reworkNo: 'RW20260328005', bundleNo: 'B260328005', processName: '整烫', defectType: '污渍', quantity: 1, originalWorkerName: '赵六', reworkWorkerName: '钱七', deductionAmount: 10.0, status: 'completed', createdAt: '2026-03-28 15:20:00' }
    ]
    total.value = 2
    loading.value = false
  }, 500)
}

const handleAdd = () => {
  ElMessage.info('功能即将上线，敬请期待')
}

const handleDetail = (row: any) => {
  ElMessage.info(`查看返工单 ${row.reworkNo} 详情`)
}

const handleComplete = (row: any) => {
  ElMessageBox.confirm(
    `确认返工单 ${row.reworkNo} 已处理完成吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('操作成功')
    handleQuery()
  }).catch(() => {})
}

onMounted(() => {
  handleQuery()
})
</script>
