<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>销售日报</span>
        </div>
      </template>

      <div class="filter-container">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          class="filter-item"
          @change="handleQuery"
          style="width: 260px; margin-right: 10px;"
        />
        <el-button class="filter-item" type="primary" @click="handleQuery">查询</el-button>
        <el-button class="filter-item" type="success" plain @click="handleExport">导出 CSV</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border style="width: 100%">
        <el-table-column prop="reportDate" label="日期" width="120" />
        <el-table-column prop="orderCount" label="销售单数" />
        <el-table-column prop="totalAmount" label="销售总额" />
        <el-table-column prop="discountAmount" label="优惠金额" />
        <el-table-column prop="actualAmount" label="折后金额" />
        <el-table-column prop="refundAmount" label="退款金额" />
        <el-table-column prop="netAmount" label="净销售额" />
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-show="total > 0"
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleQuery"
          @current-change="handleQuery"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getSalesDailyReport } from '@/api/report'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const dateRange = ref<[string, string] | null>(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  startDate: undefined as string | undefined,
  endDate: undefined as string | undefined
})

const handleQuery = () => {
  if (dateRange.value && dateRange.value.length === 2) {
    queryParams.startDate = dateRange.value[0]
    queryParams.endDate = dateRange.value[1]
  } else {
    queryParams.startDate = undefined
    queryParams.endDate = undefined
  }
  
  loading.value = true
  getSalesDailyReport(queryParams).then((res: any) => {
    list.value = res.data.list
    total.value = res.data.total
  }).finally(() => {
    loading.value = false
  })
}

const handleExport = () => {
  if (list.value.length === 0) {
    ElMessage.warning('没有可导出的数据')
    return
  }
  
  // 简易 CSV 导出
  const headers = ['日期', '销售单数', '销售总额', '优惠金额', '折后金额', '退款金额', '净销售额']
  const rows = list.value.map((item: any) => [
    item.reportDate,
    item.orderCount,
    item.totalAmount,
    item.discountAmount,
    item.actualAmount,
    item.refundAmount,
    item.netAmount
  ])
  
  const csvContent = [
    headers.join(','),
    ...rows.map(row => row.join(','))
  ].join('\n')
  
  const blob = new Blob(['\uFEFF' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('url')
  const url = URL.createObjectURL(blob)
  
  const a = document.createElement('a')
  a.setAttribute('href', url)
  a.setAttribute('download', `销售日报_${new Date().toISOString().split('T')[0]}.csv`)
  a.style.visibility = 'hidden'
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.filter-container {
  margin-bottom: 20px;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
