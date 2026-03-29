<template>
  <div class="payroll-period-page">
    <el-card>
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="期间单号">
          <el-input v-model="queryForm.sheetNo" placeholder="期间单号" clearable />
        </el-form-item>
        <el-form-item label="年月">
          <el-input v-model="queryForm.yearMonth" placeholder="如: 2026-03" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="状态" clearable>
            <el-option label="草稿" :value="0" />
            <el-option label="待审核" :value="1" />
            <el-option label="已审核" :value="2" />
            <el-option label="已发放" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="起始日期">
          <el-date-picker v-model="startDateRange" type="daterange" start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="endDateRange" type="daterange" start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="table-operations">
        <el-button type="primary" v-perm="'wage:sheet:add'" @click="handleAdd">新增工资期间</el-button>
      </div>

      <el-row :gutter="16" style="margin-bottom: 16px;">
        <el-col :span="6">
          <el-card shadow="hover">
            <el-statistic title="应发总额" :value="stats.totalPayable" :precision="2" prefix="¥" />
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <el-statistic title="扣款总额" :value="stats.totalDeduction" :precision="2" prefix="¥" />
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <el-statistic title="实发总额" :value="stats.totalActual" :precision="2" prefix="¥" />
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <el-statistic title="发放人数" :value="stats.employeeCount" />
          </el-card>
        </el-col>
      </el-row>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="sheetNo" label="期间单号" width="180" />
        <el-table-column prop="yearMonth" label="年月" width="100" />
        <el-table-column prop="startDate" label="起始日期" width="110" />
        <el-table-column prop="endDate" label="结束日期" width="110" />
        <el-table-column prop="employeeCount" label="人数" width="80" />
        <el-table-column prop="totalBaseWage" label="基本工资" width="110">
          <template #default="{ row }">{{ formatMoney(row.totalBaseWage) }}</template>
        </el-table-column>
        <el-table-column prop="totalPieceWage" label="计件工资" width="110">
          <template #default="{ row }">{{ formatMoney(row.totalPieceWage) }}</template>
        </el-table-column>
        <el-table-column prop="totalHourWage" label="计时工资" width="110">
          <template #default="{ row }">{{ formatMoney(row.totalHourWage) }}</template>
        </el-table-column>
        <el-table-column prop="totalPayable" label="应发金额" width="120">
          <template #default="{ row }">
            <span style="color: #409eff; font-weight: bold;">{{ formatMoney(row.totalPayable) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalDeduction" label="扣款金额" width="120">
          <template #default="{ row }">{{ formatMoney(row.totalDeduction) }}</template>
        </el-table-column>
        <el-table-column prop="totalActual" label="实发金额" width="120">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold;">{{ formatMoney(row.totalActual) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)">{{ statusMap[row.status] || '未知' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payMethod" label="发放方式" width="100">
          <template #default="{ row }">{{ row.payMethod || '-' }}</template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button v-perm="'wage:sheet:edit'" link type="primary" @click="handleEdit(row)" v-if="row.status === 0">编辑</el-button>
            <el-button v-perm="'wage:sheet:edit'" link type="warning" @click="handleSubmit(row)" v-if="row.status === 0">提交</el-button>
            <el-button v-perm="'wage:sheet:audit'" link type="success" @click="handleAudit(row, true)" v-if="row.status === 1">审核</el-button>
            <el-button v-perm="'wage:sheet:audit'" link type="danger" @click="handleAudit(row, false)" v-if="row.status === 1">驳回</el-button>
            <el-button v-perm="'wage:sheet:edit'" link type="success" @click="handlePay(row)" v-if="row.status === 2">发放</el-button>
            <el-button v-perm="'wage:sheet:delete'" link type="danger" @click="handleDelete(row)" v-if="row.status === 0">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="650px">
      <el-form :model="formData" label-width="120px" :disabled="dialogTitle === '查看工资期间'">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="年月" required>
              <el-input v-model="formData.yearMonth" placeholder="如: 2026-03" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发放方式">
              <el-input v-model="formData.payMethod" placeholder="如: 银行代发" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="起始日期">
              <el-date-picker v-model="formData.startDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期">
              <el-date-picker v-model="formData.endDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button v-if="dialogTitle !== '查看工资期间'" type="primary" @click="handleFormSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getPayrollPeriodPage, getPayrollPeriodDetail,
  createPayrollPeriod, updatePayrollPeriod, deletePayrollPeriod,
  updatePayrollPeriodStatus, submitPayrollPeriod, auditPayrollPeriod
} from '@/api/wage'

const statusMap: Record<number, string> = { 0: '草稿', 1: '待审核', 2: '已审核', 3: '已发放' }
const statusTag = (status: number) => status === 0 ? 'info' : status === 1 ? 'warning' : status === 2 ? 'success' : ''

const queryForm = reactive({
  pageNum: 1, pageSize: 10,
  sheetNo: '', yearMonth: '', status: undefined as number | undefined,
  startDateFrom: undefined as string | undefined, startDateTo: undefined as string | undefined,
  endDateFrom: undefined as string | undefined, endDateTo: undefined as string | undefined
})
const startDateRange = ref<string[] | null>(null)
const endDateRange = ref<string[] | null>(null)
const formData = reactive({
  id: undefined as number | undefined,
  yearMonth: '', startDate: '', endDate: '', payMethod: '', remark: ''
})
const stats = reactive({ totalPayable: 0, totalDeduction: 0, totalActual: 0, employeeCount: 0 })
const tableData = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')

const formatMoney = (value: number) => value != null ? `¥${Number(value).toFixed(2)}` : '¥0.00'

const resetForm = () => Object.assign(formData, {
  id: undefined, yearMonth: '', startDate: '', endDate: '', payMethod: '', remark: ''
})

const handleQuery = async () => {
  loading.value = true
  try {
    if (startDateRange.value?.length === 2) {
      queryForm.startDateFrom = startDateRange.value[0]
      queryForm.startDateTo = startDateRange.value[1]
    } else {
      queryForm.startDateFrom = undefined
      queryForm.startDateTo = undefined
    }
    if (endDateRange.value?.length === 2) {
      queryForm.endDateFrom = endDateRange.value[0]
      queryForm.endDateTo = endDateRange.value[1]
    } else {
      queryForm.endDateFrom = undefined
      queryForm.endDateTo = undefined
    }
    const res = await getPayrollPeriodPage(queryForm)
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
    stats.totalPayable = 0; stats.totalDeduction = 0; stats.totalActual = 0; stats.employeeCount = 0
    tableData.value.forEach((row: any) => {
      stats.totalPayable += Number(row.totalPayable || 0)
      stats.totalDeduction += Number(row.totalDeduction || 0)
      stats.totalActual += Number(row.totalActual || 0)
      stats.employeeCount += Number(row.employeeCount || 0)
    })
  } catch { ElMessage.error('查询失败') }
  finally { loading.value = false }
}

const handleReset = () => {
  queryForm.sheetNo = ''; queryForm.yearMonth = ''; queryForm.status = undefined; queryForm.pageNum = 1
  startDateRange.value = null; endDateRange.value = null
  handleQuery()
}

const handleAdd = () => { dialogTitle.value = '新增工资期间'; resetForm(); dialogVisible.value = true }

const handleView = async (row: any) => {
  dialogTitle.value = '查看工资期间'
  try {
    const res = await getPayrollPeriodDetail(row.id)
    Object.assign(formData, res.data)
  } catch { ElMessage.error('查询失败') }
  dialogVisible.value = true
}

const handleEdit = async (row: any) => {
  dialogTitle.value = '编辑工资期间'
  try {
    const res = await getPayrollPeriodDetail(row.id)
    Object.assign(formData, res.data)
  } catch { ElMessage.error('查询失败') }
  dialogVisible.value = true
}

const handleFormSubmit = async () => {
  try {
    if (formData.id) {
      await updatePayrollPeriod(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await createPayrollPeriod(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false; handleQuery()
  } catch { ElMessage.error('操作失败') }
}

const handleSubmit = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要提交该工资期间审核吗？', '提示', { type: 'warning' })
    await submitPayrollPeriod(row.id); ElMessage.success('提交成功'); handleQuery()
  } catch (e) { if (e !== 'cancel') ElMessage.error('操作失败') }
}

const handleAudit = async (row: any, approved: boolean) => {
  try {
    const action = approved ? '审核通过' : '驳回'
    await ElMessageBox.confirm(`确定要${action}该工资期间吗？`, '提示', { type: 'warning' })
    await auditPayrollPeriod(row.id, approved); ElMessage.success(`${action}成功`); handleQuery()
  } catch (e) { if (e !== 'cancel') ElMessage.error('操作失败') }
}

const handlePay = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要发放该工资期间吗？发放后不可撤回。', '提示', { type: 'warning' })
    await updatePayrollPeriodStatus(row.id, 3); ElMessage.success('发放成功'); handleQuery()
  } catch (e) { if (e !== 'cancel') ElMessage.error('操作失败') }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该工资期间吗？', '提示', { type: 'warning' })
    await deletePayrollPeriod(row.id); ElMessage.success('删除成功'); handleQuery()
  } catch (e) { if (e !== 'cancel') ElMessage.error('删除失败') }
}

onMounted(() => handleQuery())
</script>

<style scoped>
.table-operations { margin-bottom: 16px; }
</style>
