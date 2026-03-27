<template>
  <div class="transaction-page">
    <el-card>
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="业务单号">
          <el-input v-model="queryForm.businessNo" placeholder="业务单号" clearable />
        </el-form-item>
        <el-form-item label="交易类型">
          <el-select v-model="queryForm.transactionType" placeholder="交易类型" clearable>
            <el-option label="收款" value="收款" />
            <el-option label="付款" value="付款" />
            <el-option label="核销应收" value="核销应收" />
            <el-option label="核销应付" value="核销应付" />
            <el-option label="收入" value="收入" />
            <el-option label="支出" value="支出" />
          </el-select>
        </el-form-item>
        <el-form-item label="交易日期">
          <el-date-picker
            v-model="queryForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" border>
        <el-table-column prop="transactionNo" label="流水号" width="180" />
        <el-table-column prop="transactionType" label="交易类型" width="120" />
        <el-table-column prop="businessType" label="业务类型" width="120" />
        <el-table-column prop="businessNo" label="业务单号" width="180" />
        <el-table-column prop="transactionDate" label="交易日期" width="120" />
        <el-table-column prop="incomeAmount" label="收入金额" width="120">
          <template #default="{ row }">
            {{ row.incomeAmount ? `+${formatMoney(row.incomeAmount)}` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="expenseAmount" label="支出金额" width="120">
          <template #default="{ row }">
            {{ row.expenseAmount ? `${formatMoney(row.expenseAmount)}` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="balance" label="余额" width="120">
          <template #default="{ row }">
            {{ formatMoney(row.balance) }}
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="100" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryForm.pageNum"
        v-model:page-size="queryForm.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="交易类型">
          <el-select v-model="formData.transactionType">
            <el-option label="收款" value="收款" />
            <el-option label="付款" value="付款" />
            <el-option label="收入" value="收入" />
            <el-option label="支出" value="支出" />
          </el-select>
        </el-form-item>
        <el-form-item label="业务类型">
          <el-input v-model="formData.businessType" />
        </el-form-item>
        <el-form-item label="业务单号">
          <el-input v-model="formData.businessNo" />
        </el-form-item>
        <el-form-item label="交易日期">
          <el-date-picker v-model="formData.transactionDate" type="date" />
        </el-form-item>
        <el-form-item label="收入金额">
          <el-input-number v-model="formData.incomeAmount" :precision="2" :min="0" />
        </el-form-item>
        <el-form-item label="支出金额">
          <el-input-number v-model="formData.expenseAmount" :precision="2" :min="0" />
        </el-form-item>
        <el-form-item label="支付方式">
          <el-input v-model="formData.paymentMethod" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { transactionApi } from '@/api/finance'

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  businessNo: '',
  transactionType: '',
  dateRange: null
})

const formData = reactive({
  transactionId: undefined,
  transactionType: '',
  businessType: '',
  businessNo: '',
  transactionDate: '',
  incomeAmount: 0,
  expenseAmount: 0,
  paymentMethod: '',
  remark: ''
})

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')

const formatMoney = (value: number) => {
  return value ? `¥${value.toFixed(2)}` : '¥0.00'
}

const handleQuery = async () => {
  try {
    const res = await transactionApi.pageList(queryForm)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const handleReset = () => {
  queryForm.businessNo = ''
  queryForm.transactionType = ''
  queryForm.dateRange = null
  queryForm.pageNum = 1
  handleQuery()
}

const handleView = async (row: any) => {
  try {
    const res = await transactionApi.getById(row.transactionId)
    dialogTitle.value = '查看流水'
    Object.assign(formData, res.data)
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const handleSubmit = async () => {
  try {
    dialogVisible.value = false
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.table-operations {
  margin-bottom: 16px;
}
</style>
