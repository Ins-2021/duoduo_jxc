<template>
  <div class="quotation-container">
    <el-card shadow="never">
      <div class="toolbar" style="display: flex; justify-content: space-between; margin-bottom: 20px;">
        <div><el-button type="primary">新增报价单</el-button></div>
        <div style="display: flex; gap: 10px;">
          <el-input v-model="queryParams.keyword" placeholder="报价单号" clearable style="width: 200px" />
          <el-button type="primary" @click="handleQuery">查询</el-button>
        </div>
      </div>
      <el-table v-loading="loading" :data="tableData" border>
        <el-table-column prop="quotationNo" label="报价单号" />
        <el-table-column prop="customerName" label="客户" />
        <el-table-column prop="finalAmount" label="最终金额" />
        <el-table-column prop="quotationStatus" label="状态" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleConvert(row)">转订单</el-button>
            <el-button link type="primary">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getQuotationList, convertQuotation } from '@/api/quotation'

const loading = ref(false)
const tableData = ref([])
const queryParams = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getQuotationList(queryParams)
    tableData.value = res.data.list || []
  } catch (e) {} finally { loading.value = false }
}

const handleConvert = async (row: any) => {
  try {
    await convertQuotation(row.quotationId)
    ElMessage.success('转订单成功')
    handleQuery()
  } catch (e) {}
}

onMounted(() => handleQuery())
</script>
