<template>
  <div class="piece-record-page">
    <el-card>
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="员工姓名">
          <el-input v-model="queryForm.employeeName" placeholder="员工姓名" clearable />
        </el-form-item>
        <el-form-item label="产品款号">
          <el-input v-model="queryForm.styleCode" placeholder="产品款号" clearable />
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker v-model="queryForm.dateRange" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="queryForm.auditStatus" placeholder="审核状态" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="已审核" :value="1" />
            <el-option label="已驳回" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="table-operations">
        <el-button type="primary" v-perm="'wage:piece:add'" @click="handleAdd">新增计件</el-button>
        <el-button type="success" @click="handleSummary">汇总查询</el-button>
      </div>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="recordNo" label="记录单号" width="160" />
        <el-table-column prop="recordDate" label="日期" width="110" />
        <el-table-column prop="employeeCode" label="工号" width="80" />
        <el-table-column prop="employeeName" label="员工姓名" width="100" />
        <el-table-column prop="styleCode" label="产品款号" width="110" />
        <el-table-column prop="styleName" label="产品名称" width="130" />
        <el-table-column prop="processCode" label="工序编码" width="90" />
        <el-table-column prop="processName" label="工序名称" width="90" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="qualifiedQuantity" label="合格数" width="80" />
        <el-table-column prop="defectQuantity" label="次品数" width="80" />
        <el-table-column prop="unitPrice" label="单价" width="100">
          <template #default="{ row }">¥{{ Number(row.unitPrice).toFixed(4) }}</template>
        </el-table-column>
        <el-table-column prop="wageAmount" label="金额" width="110">
          <template #default="{ row }">
            <span style="color: #67c23a; font-weight: bold;">{{ formatMoney(row.wageAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="workshopName" label="车间" width="100" />
        <el-table-column prop="auditStatus" label="审核状态" width="90">
          <template #default="{ row }">
            <el-tag :type="auditTag(row.auditStatus)">{{ auditMap[row.auditStatus] || '未知' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button v-perm="'wage:piece:edit'" link type="primary" @click="handleEdit(row)" v-if="row.auditStatus === 0">编辑</el-button>
            <el-button v-perm="'wage:piece:audit'" link type="success" @click="handleAudit(row, 1)" v-if="row.auditStatus === 0">审核</el-button>
            <el-button v-perm="'wage:piece:audit'" link type="warning" @click="handleAudit(row, 2)" v-if="row.auditStatus === 0">驳回</el-button>
            <el-button v-perm="'wage:piece:delete'" link type="danger" @click="handleDelete(row)" v-if="row.auditStatus === 0">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" label-width="120px" :disabled="dialogTitle === '查看计件记录'">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="记录日期" required>
              <el-date-picker v-model="formData.recordDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="员工ID" required>
              <el-input-number v-model="formData.employeeId" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="员工姓名">
              <el-input v-model="formData.employeeName" placeholder="员工姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车间ID">
              <el-input-number v-model="formData.workshopId" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="产品款号" required>
              <el-input v-model="formData.styleCode" placeholder="产品款号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="产品名称">
              <el-input v-model="formData.styleName" placeholder="产品名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="工序编码" required>
              <el-input v-model="formData.processCode" placeholder="工序编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工序名称">
              <el-input v-model="formData.processName" placeholder="工序名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="总数量" required>
              <el-input-number v-model="formData.quantity" :precision="0" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="合格数量">
              <el-input-number v-model="formData.qualifiedQuantity" :precision="0" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="次品数量">
              <el-input-number v-model="formData.defectQuantity" :precision="0" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="计件单价" required>
              <el-input-number v-model="formData.unitPrice" :precision="4" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工资金额">
              <el-input-number v-model="formData.wageAmount" :precision="4" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button v-if="dialogTitle !== '查看计件记录'" type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="summaryVisible" title="计件工资汇总" width="80%">
      <el-table :data="summaryData" border v-loading="summaryLoading">
        <el-table-column prop="employeeName" label="员工姓名" width="100" />
        <el-table-column prop="styleCode" label="产品款号" width="110" />
        <el-table-column prop="processName" label="工序" width="100" />
        <el-table-column prop="totalQuantity" label="总数量" width="100" />
        <el-table-column prop="avgUnitPrice" label="平均单价" width="100">
          <template #default="{ row }">¥{{ Number(row.avgUnitPrice).toFixed(4) }}</template>
        </el-table-column>
        <el-table-column prop="totalWage" label="总金额" width="130">
          <template #default="{ row }">
            <span style="color: #67c23a; font-weight: bold;">{{ formatMoney(row.totalWage) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPieceRecordPage, getPieceRecordDetail, createPieceRecord, updatePieceRecord, deletePieceRecord, auditPieceRecord, getPieceRecordSummary } from '@/api/wage'

const auditMap: Record<number, string> = { 0: '待审核', 1: '已审核', 2: '已驳回' }
const auditTag = (status: number) => status === 0 ? 'warning' : status === 1 ? 'success' : 'danger'

const queryForm = reactive({ pageNum: 1, pageSize: 10, employeeName: '', styleCode: '', dateRange: null as string[] | null, auditStatus: undefined as number | undefined })
const formData = reactive({
  id: undefined as number | undefined,
  recordDate: '', employeeId: undefined as number | undefined, employeeName: '',
  employeeCode: '',
  styleId: undefined as number | undefined,
  styleCode: '', styleName: '',
  processCode: '', processName: '',
  productionId: undefined as number | undefined, productionNo: '',
  quantity: 0, qualifiedQuantity: 0, defectQuantity: 0,
  unitPrice: 0, wageAmount: 0,
  workshopId: undefined as number | undefined, workshopName: '',
  remark: ''
})
const tableData = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const summaryVisible = ref(false)
const summaryData = ref<any[]>([])
const summaryLoading = ref(false)

const formatMoney = (value: number) => value != null ? `¥${Number(value).toFixed(2)}` : '¥0.00'

const handleQuery = async () => {
  loading.value = true
  try {
    const params: any = { pageNum: queryForm.pageNum, pageSize: queryForm.pageSize, employeeName: queryForm.employeeName, styleCode: queryForm.styleCode, auditStatus: queryForm.auditStatus }
    if (queryForm.dateRange?.length === 2) { params.recordDateFrom = queryForm.dateRange[0]; params.recordDateTo = queryForm.dateRange[1] }
    const res = await getPieceRecordPage(params)
    tableData.value = res.data?.list || []; total.value = res.data?.total || 0
  } catch { ElMessage.error('查询失败') }
  finally { loading.value = false }
}

const handleReset = () => {
  queryForm.employeeName = ''; queryForm.styleCode = ''; queryForm.dateRange = null; queryForm.auditStatus = undefined; queryForm.pageNum = 1
  handleQuery()
}

const resetForm = () => Object.assign(formData, {
  id: undefined, recordDate: '', employeeId: undefined, employeeName: '', employeeCode: '',
  styleId: undefined, styleCode: '', styleName: '',
  processCode: '', processName: '',
  productionId: undefined, productionNo: '',
  quantity: 0, qualifiedQuantity: 0, defectQuantity: 0,
  unitPrice: 0, wageAmount: 0,
  workshopId: undefined, workshopName: '', remark: ''
})

const handleAdd = () => { dialogTitle.value = '新增计件'; resetForm(); dialogVisible.value = true }
const handleView = async (row: any) => {
  dialogTitle.value = '查看计件记录'
  try { const res = await getPieceRecordDetail(row.id); Object.assign(formData, res.data) } catch { ElMessage.error('查询失败') }
  dialogVisible.value = true
}
const handleEdit = async (row: any) => {
  dialogTitle.value = '编辑计件记录'
  try { const res = await getPieceRecordDetail(row.id); Object.assign(formData, res.data) } catch { ElMessage.error('查询失败') }
  dialogVisible.value = true
}
const handleSubmit = async () => {
  try {
    if (formData.id) { await updatePieceRecord(formData.id, formData); ElMessage.success('更新成功') }
    else { await createPieceRecord(formData); ElMessage.success('创建成功') }
    dialogVisible.value = false; handleQuery()
  } catch { ElMessage.error('操作失败') }
}
const handleAudit = async (row: any, status: number) => {
  try {
    const action = status === 1 ? '审核' : '驳回'
    await ElMessageBox.confirm(`确定要${action}该计件记录吗？`, '提示', { type: 'warning' })
    await auditPieceRecord(row.id, status)
    ElMessage.success(`${action}成功`); handleQuery()
  } catch (e) { if (e !== 'cancel') ElMessage.error('操作失败') }
}
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该计件记录吗？', '提示', { type: 'warning' })
    await deletePieceRecord(row.id); ElMessage.success('删除成功'); handleQuery()
  } catch (e) { if (e !== 'cancel') ElMessage.error('删除失败') }
}
const handleSummary = async () => {
  summaryVisible.value = true; summaryLoading.value = true
  try {
    const params: any = {}
    if (queryForm.dateRange?.length === 2) { params.recordDateFrom = queryForm.dateRange[0]; params.recordDateTo = queryForm.dateRange[1] }
    if (queryForm.employeeName) params.employeeName = queryForm.employeeName
    if (queryForm.styleCode) params.styleCode = queryForm.styleCode
    const res = await getPieceRecordSummary(params)
    summaryData.value = res.data || []
  } catch { ElMessage.error('查询失败') }
  finally { summaryLoading.value = false }
}

onMounted(() => handleQuery())
</script>

<style scoped>
.table-operations { margin-bottom: 16px; }
</style>
