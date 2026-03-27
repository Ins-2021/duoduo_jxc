<template>
  <div class="income-expense-page">
    <el-card>
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="收支类型">
          <el-select v-model="queryForm.expenseType" placeholder="收支类型" clearable>
            <el-option label="收入" value="收入" />
            <el-option label="支出" value="支出" />
          </el-select>
        </el-form-item>
        <el-form-item label="收支日期">
          <el-date-picker
            v-model="queryForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="queryForm.remark" placeholder="备注" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="table-operations">
        <el-button type="primary" @click="handleAdd">新增收支</el-button>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-cards">
        <el-card class="stat-card">
          <div class="stat-title">总收入</div>
          <div class="stat-value income">{{ formatMoney(stats.totalIncome) }}</div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-title">总支出</div>
          <div class="stat-value expense">{{ formatMoney(stats.totalExpense) }}</div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-title">净额</div>
          <div class="stat-value" :class="stats.net >= 0 ? 'income' : 'expense'">
            {{ formatMoney(stats.net) }}
          </div>
        </el-card>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" border>
        <el-table-column prop="expenseNo" label="收支单号" width="180" />
        <el-table-column prop="expenseType" label="收支类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.expenseType === '收入'" type="success">收入</el-tag>
            <el-tag v-else type="danger">支出</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="expenseDate" label="收支日期" width="120" />
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">
            <span :class="row.expenseType === '收入' ? 'income-text' : 'expense-text'">
              {{ row.expenseType === '收入' ? '+' : '-' }}{{ formatMoney(row.amount) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="expenseCategory" label="收支科目" width="150" />
        <el-table-column prop="paymentMethod" label="支付方式" width="100" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button v-perm="'finance:income-expense:delete'" link type="danger" @click="handleDelete(row)">删除</el-button>
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
        <el-form-item label="收支类型">
          <el-select v-model="formData.expenseType">
            <el-option label="收入" value="收入" />
            <el-option label="支出" value="支出" />
          </el-select>
        </el-form-item>
        <el-form-item label="收支日期">
          <el-date-picker v-model="formData.expenseDate" type="date" />
        </el-form-item>
        <el-form-item label="金额">
          <el-input-number v-model="formData.amount" :precision="2" :min="0" />
        </el-form-item>
        <el-form-item label="收支科目">
          <el-select v-model="formData.expenseCategory">
            <el-option label="销售收入" value="销售收入" />
            <el-option label="其他收入" value="其他收入" />
            <el-option label="采购支出" value="采购支出" />
            <el-option label="工资支出" value="工资支出" />
            <el-option label="房租水电" value="房租水电" />
            <el-option label="其他支出" value="其他支出" />
          </el-select>
        </el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="formData.paymentMethod">
            <el-option label="现金" value="现金" />
            <el-option label="银行转账" value="银行转账" />
            <el-option label="微信支付" value="微信支付" />
            <el-option label="支付宝" value="支付宝" />
          </el-select>
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
import { incomeExpenseApi } from '@/api/finance'

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  expenseType: '',
  dateRange: null,
  remark: ''
})

const formData = reactive({
  expenseId: undefined,
  expenseType: '收入',
  expenseDate: '',
  amount: 0,
  expenseCategory: '',
  paymentMethod: '现金',
  remark: ''
})

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')

const stats = reactive({
  totalIncome: 0,
  totalExpense: 0,
  net: 0
})

const formatMoney = (value: number) => {
  return value ? `¥${value.toFixed(2)}` : '¥0.00'
}

const calculateStats = () => {
  stats.totalIncome = tableData.value
    .filter((item: any) => item.expenseType === '收入')
    .reduce((sum: number, item: any) => sum + item.amount, 0)
  stats.totalExpense = tableData.value
    .filter((item: any) => item.expenseType === '支出')
    .reduce((sum: number, item: any) => sum + item.amount, 0)
  stats.net = stats.totalIncome - stats.totalExpense
}

const handleQuery = async () => {
  try {
    const res = await incomeExpenseApi.pageList(queryForm)
    tableData.value = res.data.list
    total.value = res.data.total
    calculateStats()
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const handleReset = () => {
  queryForm.expenseType = ''
  queryForm.dateRange = null
  queryForm.remark = ''
  queryForm.pageNum = 1
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增收支'
  Object.assign(formData, {
    expenseId: undefined,
    expenseType: '收入',
    expenseDate: new Date(),
    amount: 0,
    expenseCategory: '',
    paymentMethod: '现金',
    remark: ''
  })
  dialogVisible.value = true
}

const handleView = async (row: any) => {
  try {
    const res = await incomeExpenseApi.getById(row.expenseId)
    dialogTitle.value = '查看收支'
    Object.assign(formData, res.data)
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const handleSubmit = async () => {
  try {
    if (formData.expenseId) {
      await incomeExpenseApi.update(formData)
      ElMessage.success('更新成功')
    } else {
      await incomeExpenseApi.create(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    handleQuery()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该收支记录吗？', '提示', { type: 'warning' })
    await incomeExpenseApi.delete(row.expenseId)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
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

.stats-cards {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.stat-card {
  flex: 1;
  text-align: center;
}

.stat-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
}

.stat-value.income {
  color: #67c23a;
}

.stat-value.expense {
  color: #f56c6c;
}

.income-text {
  color: #67c23a;
}

.expense-text {
  color: #f56c6c;
}
</style>
