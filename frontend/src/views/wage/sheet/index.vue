<template>
  <div class="payroll-sheet-page">
    <el-card>
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="核算月份">
          <el-input v-model="queryForm.yearMonth" placeholder="如: 2026-03" clearable />
        </el-form-item>
        <el-form-item label="部门ID">
          <el-input-number v-model="queryForm.departmentId" :min="1" placeholder="部门" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="状态" clearable>
            <el-option label="草稿" :value="0" />
            <el-option label="待审核" :value="1" />
            <el-option label="已审核" :value="2" />
            <el-option label="已发放" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="table-operations">
        <el-button type="primary" v-perm="'wage:sheet:add'" @click="handleAdd">生成工资单</el-button>
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
        <el-table-column prop="sheetNo" label="工资单号" width="160" />
        <el-table-column prop="yearMonth" label="核算月份" width="100" />
        <el-table-column prop="departmentName" label="部门" width="120" />
        <el-table-column prop="employeeCount" label="人数" width="80" />
        <el-table-column prop="totalBaseWage" label="基本工资" width="120">
          <template #default="{ row }">{{ formatMoney(row.totalBaseWage) }}</template>
        </el-table-column>
        <el-table-column prop="totalPieceWage" label="计件工资" width="120">
          <template #default="{ row }">{{ formatMoney(row.totalPieceWage) }}</template>
        </el-table-column>
        <el-table-column prop="totalPayable" label="应发金额" width="130">
          <template #default="{ row }">{{ formatMoney(row.totalPayable) }}</template>
        </el-table-column>
        <el-table-column prop="totalDeduction" label="扣款金额" width="130">
          <template #default="{ row }">{{ formatMoney(row.totalDeduction) }}</template>
        </el-table-column>
        <el-table-column prop="totalActual" label="实发金额" width="130">
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
          <template #default="{ row }">
            <span>{{ payMethodMap[row.payMethod] || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
            <el-button v-perm="'wage:sheet:edit'" link type="warning" @click="handleSubmit(row)" v-if="row.status === 0">提交</el-button>
            <el-button v-perm="'wage:sheet:audit'" link type="success" @click="handleAudit(row, 1)" v-if="row.status === 1">审核</el-button>
            <el-button v-perm="'wage:sheet:audit'" link type="danger" @click="handleAudit(row, 0)" v-if="row.status === 1">驳回</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="650px">
      <el-form :model="formData" label-width="120px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="核算月份" required>
              <el-input v-model="formData.yearMonth" placeholder="如: 2026-03" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门ID">
              <el-input-number v-model="formData.departmentId" :min="0" style="width: 100%" />
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
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="发放方式">
              <el-select v-model="formData.payMethod">
                <el-option label="银行代发" :value="0" />
                <el-option label="现金发放" :value="1" />
                <el-option label="混合" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreateSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPayrollSheetPage, createPayrollSheet, deletePayrollSheet, submitPayrollSheet, auditPayrollSheet, updatePayrollSheetStatus } from '@/api/wage'

const router = useRouter()
const statusMap: Record<number, string> = { 0: '草稿', 1: '待审核', 2: '已审核', 3: '已发放' }
const statusTag = (status: number) => status === 0 ? 'info' : status === 1 ? 'warning' : status === 2 ? 'success' : ''
const payMethodMap: Record<number, string> = { 0: '银行代发', 1: '现金发放', 2: '混合' }

const queryForm = reactive({ pageNum: 1, pageSize: 10, yearMonth: '', departmentId: undefined as number | undefined, status: undefined as number | undefined })
const formData = reactive({
  yearMonth: '', departmentId: undefined as number | undefined,
  startDate: '', endDate: '', payMethod: 0, remark: ''
})
const stats = reactive({ totalPayable: 0, totalDeduction: 0, totalActual: 0, employeeCount: 0 })
const tableData = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')

const formatMoney = (value: number) => value != null ? `¥${Number(value).toFixed(2)}` : '¥0.00'

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getPayrollSheetPage(queryForm)
    tableData.value = res.data?.list || []; total.value = res.data?.total || 0
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

const handleReset = () => { queryForm.yearMonth = ''; queryForm.departmentId = undefined; queryForm.status = undefined; queryForm.pageNum = 1; handleQuery() }

const handleAdd = () => {
  dialogTitle.value = '生成工资单'
  formData.yearMonth = ''; formData.departmentId = undefined; formData.startDate = ''; formData.endDate = ''; formData.payMethod = 0; formData.remark = ''
  dialogVisible.value = true
}

const handleCreateSubmit = async () => {
  try {
    await createPayrollSheet(formData); ElMessage.success('工资单生成成功')
    dialogVisible.value = false; handleQuery()
  } catch { ElMessage.error('生成失败') }
}

const handleDetail = (row: any) => { router.push(`/wage/sheet/detail/${row.sheetId}`) }

const handleSubmit = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要提交该工资单审核吗？', '提示', { type: 'warning' })
    await submitPayrollSheet(row.sheetId); ElMessage.success('提交成功'); handleQuery()
  } catch (e) { if (e !== 'cancel') ElMessage.error('操作失败') }
}

const handleAudit = async (row: any, approved: number) => {
  try {
    const action = approved === 1 ? '审核通过' : '驳回'
    await ElMessageBox.confirm(`确定要${action}该工资单吗？`, '提示', { type: 'warning' })
    await auditPayrollSheet(row.sheetId, approved); ElMessage.success(`${action}成功`); handleQuery()
  } catch (e) { if (e !== 'cancel') ElMessage.error('操作失败') }
}

const handlePay = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要发放该工资单吗？发放后不可撤回。', '提示', { type: 'warning' })
    await updatePayrollSheetStatus(row.sheetId, 3); ElMessage.success('发放成功'); handleQuery()
  } catch (e) { if (e !== 'cancel') ElMessage.error('操作失败') }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该工资单吗？', '提示', { type: 'warning' })
    await deletePayrollSheet(row.sheetId); ElMessage.success('删除成功'); handleQuery()
  } catch (e) { if (e !== 'cancel') ElMessage.error('删除失败') }
}

onMounted(() => handleQuery())
</script>

<style scoped>
.table-operations { margin-bottom: 16px; }
</style>
