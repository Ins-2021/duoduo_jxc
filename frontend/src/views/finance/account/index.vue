<template>
  <div class="finance-account-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>财务账户管理</span>
          <el-button type="primary" @click="handleAdd">新增账户</el-button>
        </div>
      </template>

      <!-- 余额汇总 -->
      <el-row :gutter="20" class="summary-row">
        <el-col :span="8">
          <el-card shadow="hover" class="summary-card">
            <div class="summary-label">账户总数</div>
            <div class="summary-value">{{ summary.accountCount || 0 }}</div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="summary-card">
            <div class="summary-label">总余额</div>
            <div class="summary-value amount">¥{{ formatMoney(summary.totalBalance) }}</div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="summary-card">
            <div class="summary-label">启用账户</div>
            <div class="summary-value">{{ enabledCount }}</div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="账户名称">
          <el-input v-model="queryForm.accountName" placeholder="账户名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="accountId" label="账户ID" width="80" />
        <el-table-column prop="accountName" label="账户名称" min-width="150" />
        <el-table-column prop="balance" label="当前余额" width="150">
          <template #default="{ row }">
            <span class="balance-amount">¥{{ formatMoney(row.balance) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handleTransactions(row)">交易流水</el-button>
            <el-button link type="warning" @click="handleAdjust(row)">调整余额</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="账户名称" prop="accountName">
          <el-input v-model="formData.accountName" placeholder="请输入账户名称" />
        </el-form-item>
        <el-form-item label="初始余额" prop="balance" v-if="!formData.accountId">
          <el-input-number v-model="formData.balance" :precision="2" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 账户详情对话框 -->
    <el-dialog v-model="detailVisible" title="账户详情" width="700px">
      <div v-if="accountDetail" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="账户名称">{{ accountDetail.accountName }}</el-descriptions-item>
          <el-descriptions-item label="当前余额">
            <span class="balance-amount">¥{{ formatMoney(accountDetail.balance) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="accountDetail.status === 1 ? 'success' : 'info'">
              {{ accountDetail.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="总交易数">{{ accountDetail.transactionCount || 0 }} 笔</el-descriptions-item>
          <el-descriptions-item label="累计收入">¥{{ formatMoney(accountDetail.totalIncome) }}</el-descriptions-item>
          <el-descriptions-item label="累计支出">¥{{ formatMoney(accountDetail.totalExpense) }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ accountDetail.createTime }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ accountDetail.updateTime }}</el-descriptions-item>
        </el-descriptions>

        <div class="recent-transactions" v-if="accountDetail.recentTransactions && accountDetail.recentTransactions.length > 0">
          <h4>最近交易记录</h4>
          <el-table :data="accountDetail.recentTransactions" border size="small">
            <el-table-column prop="transactionNo" label="流水号" width="150" />
            <el-table-column prop="type" label="类型" width="80">
              <template #default="{ row }">
                <el-tag :type="row.type === 1 ? 'success' : 'danger'" size="small">
                  {{ row.type === 1 ? '收入' : '支出' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="amount" label="金额" width="120">
              <template #default="{ row }">
                <span :class="row.type === 1 ? 'income' : 'expense'">
                  {{ row.type === 1 ? '+' : '-' }}¥{{ formatMoney(row.amount) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="balance" label="余额" width="120">
              <template #default="{ row }">
                ¥{{ formatMoney(row.balance) }}
              </template>
            </el-table-column>
            <el-table-column prop="category" label="分类" width="100" />
            <el-table-column prop="createTime" label="时间" width="150" />
          </el-table>
        </div>
      </div>
    </el-dialog>

    <!-- 交易流水对话框 -->
    <el-dialog v-model="transactionsVisible" title="交易流水" width="900px">
      <el-form :inline="true" :model="transQueryForm" class="search-form">
        <el-form-item label="交易类型">
          <el-select v-model="transQueryForm.type" placeholder="全部" clearable style="width: 120px">
            <el-option label="收入" :value="1" />
            <el-option label="支出" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="transQueryForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleTransQuery">查询</el-button>
          <el-button @click="handleTransReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="transactionsData" border v-loading="transLoading">
        <el-table-column prop="transactionNo" label="流水号" width="180" />
        <el-table-column prop="type" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'success' : 'danger'" size="small">
              {{ row.type === 1 ? '收入' : '支出' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">
            <span :class="row.type === 1 ? 'income' : 'expense'">
              {{ row.type === 1 ? '+' : '-' }}¥{{ formatMoney(row.amount) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="balance" label="余额" width="120">
          <template #default="{ row }">
            ¥{{ formatMoney(row.balance) }}
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="billType" label="单据类型" width="120" />
        <el-table-column prop="billNo" label="单据编号" width="150" />
        <el-table-column prop="remark" label="备注" min-width="150" />
        <el-table-column prop="createTime" label="时间" width="160" />
      </el-table>

      <el-pagination
        v-model:current-page="transQueryForm.pageNum"
        v-model:page-size="transQueryForm.pageSize"
        :total="transTotal"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleTransQuery"
        @current-change="handleTransQuery"
      />
    </el-dialog>

    <!-- 调整余额对话框 -->
    <el-dialog v-model="adjustVisible" title="调整余额" width="500px">
      <el-form :model="adjustForm" :rules="adjustRules" ref="adjustRef" label-width="100px">
        <el-form-item label="当前余额">
          <span class="balance-amount">¥{{ formatMoney(currentBalance) }}</span>
        </el-form-item>
        <el-form-item label="调整金额" prop="amount">
          <el-input-number v-model="adjustForm.amount" :precision="2" style="width: 100%" />
          <div class="form-tip">正数表示增加余额，负数表示减少余额</div>
        </el-form-item>
        <el-form-item label="调整后余额">
          <span class="balance-amount">¥{{ formatMoney(currentBalance + (adjustForm.amount || 0)) }}</span>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="adjustForm.remark" type="textarea" :rows="3" placeholder="请输入调整原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAdjustSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { accountApi } from '@/api/finance'
import type { FinanceAccountDTO, FinanceAccountDetailDTO, FinanceTransactionDTO } from '@/types'

const loading = ref(false)
const transLoading = ref(false)
const tableData = ref<FinanceAccountDTO[]>([])
const total = ref(0)
const summary = ref({ accountCount: 0, totalBalance: 0, accounts: [] })
const currentAccountId = ref<number | null>(null)
const currentBalance = ref(0)

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  accountName: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const formData = reactive<Partial<FinanceAccountDTO>>({
  accountId: undefined,
  accountName: '',
  balance: 0,
  status: 1
})

const formRules = {
  accountName: [{ required: true, message: '请输入账户名称', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const detailVisible = ref(false)
const accountDetail = ref<FinanceAccountDetailDTO | null>(null)

const transactionsVisible = ref(false)
const transactionsData = ref<FinanceTransactionDTO[]>([])
const transTotal = ref(0)
const transQueryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  type: undefined as number | undefined,
  dateRange: null as Date[] | null
})

const adjustVisible = ref(false)
const adjustRef = ref()
const adjustForm = reactive({
  amount: 0,
  remark: ''
})
const adjustRules = {
  amount: [{ required: true, message: '请输入调整金额', trigger: 'blur' }],
  remark: [{ required: true, message: '请输入调整原因', trigger: 'blur' }]
}

const enabledCount = computed(() => {
  return summary.value.accounts?.filter((a: any) => a.status === 1).length || 0
})

const formatMoney = (value: number | undefined) => {
  return value ? Number(value).toFixed(2) : '0.00'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await accountApi.pageList(queryForm)
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const loadSummary = async () => {
  try {
    const res = await accountApi.getSummary()
    summary.value = res.data || { accountCount: 0, totalBalance: 0, accounts: [] }
  } catch (error) {
    console.error('加载汇总数据失败', error)
  }
}

const handleQuery = () => {
  queryForm.pageNum = 1
  loadData()
}

const handleReset = () => {
  queryForm.accountName = ''
  queryForm.pageNum = 1
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = '新增账户'
  formData.accountId = undefined
  formData.accountName = ''
  formData.balance = 0
  formData.status = 1
  dialogVisible.value = true
}

const handleEdit = (row: FinanceAccountDTO) => {
  dialogTitle.value = '编辑账户'
  formData.accountId = row.accountId
  formData.accountName = row.accountName
  formData.balance = row.balance
  formData.status = row.status
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    if (formData.accountId) {
      await accountApi.update(formData as FinanceAccountDTO)
      ElMessage.success('修改成功')
    } else {
      await accountApi.create(formData as FinanceAccountDTO)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
    loadSummary()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = (row: FinanceAccountDTO) => {
  ElMessageBox.confirm(`确定删除账户 "${row.accountName}" 吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await accountApi.delete(row.accountId!)
      ElMessage.success('删除成功')
      loadData()
      loadSummary()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const handleView = async (row: FinanceAccountDTO) => {
  try {
    const res = await accountApi.getDetail(row.accountId!)
    accountDetail.value = res.data
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

const handleTransactions = (row: FinanceAccountDTO) => {
  currentAccountId.value = row.accountId!
  transactionsVisible.value = true
  handleTransQuery()
}

const handleTransQuery = async () => {
  if (!currentAccountId.value) return
  
  transLoading.value = true
  try {
    const params = {
      pageNum: transQueryForm.pageNum,
      pageSize: transQueryForm.pageSize,
      type: transQueryForm.type,
      startDate: transQueryForm.dateRange?.[0] ? formatDate(transQueryForm.dateRange[0]) : undefined,
      endDate: transQueryForm.dateRange?.[1] ? formatDate(transQueryForm.dateRange[1]) : undefined
    }
    const res = await accountApi.getTransactions(currentAccountId.value, params)
    transactionsData.value = res.data.list || []
    transTotal.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载交易流水失败')
  } finally {
    transLoading.value = false
  }
}

const handleTransReset = () => {
  transQueryForm.type = undefined
  transQueryForm.dateRange = null
  transQueryForm.pageNum = 1
  handleTransQuery()
}

const handleAdjust = async (row: FinanceAccountDTO) => {
  currentAccountId.value = row.accountId!
  currentBalance.value = Number(row.balance) || 0
  adjustForm.amount = 0
  adjustForm.remark = ''
  adjustVisible.value = true
}

const handleAdjustSubmit = async () => {
  const valid = await adjustRef.value?.validate().catch(() => false)
  if (!valid) return

  if (!currentAccountId.value) return

  try {
    await accountApi.adjustBalance(currentAccountId.value, adjustForm)
    ElMessage.success('余额调整成功')
    adjustVisible.value = false
    loadData()
    loadSummary()
  } catch (error) {
    ElMessage.error('余额调整失败')
  }
}

const formatDate = (date: Date) => {
  return date.toISOString().split('T')[0]
}

onMounted(() => {
  loadData()
  loadSummary()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.summary-row {
  margin-bottom: 20px;
}

.summary-card {
  text-align: center;
}

.summary-label {
  color: #666;
  font-size: 14px;
  margin-bottom: 8px;
}

.summary-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.summary-value.amount {
  color: #409eff;
}

.search-form {
  margin: 20px 0;
}

.balance-amount {
  font-weight: bold;
  color: #409eff;
}

.detail-content {
  .recent-transactions {
    margin-top: 20px;
    
    h4 {
      margin-bottom: 10px;
      color: #333;
    }
  }
}

.income {
  color: #67c23a;
}

.expense {
  color: #f56c6c;
}

.form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
</style>
