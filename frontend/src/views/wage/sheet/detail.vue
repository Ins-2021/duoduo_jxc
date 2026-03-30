<template>
  <div class="payroll-sheet-detail-page">
    <el-page-header @back="goBack" style="margin-bottom: 16px;">
      <template #content>
        <span>工资单详情</span>
      </template>
    </el-page-header>

    <el-card style="margin-bottom: 16px;">
      <template #header>基本信息</template>
      <el-descriptions :column="4" border>
        <el-descriptions-item label="工资单号">{{ sheet.sheetNo }}</el-descriptions-item>
        <el-descriptions-item label="核算月份">{{ sheet.yearMonth }}</el-descriptions-item>
        <el-descriptions-item label="部门">{{ sheet.departmentName }}</el-descriptions-item>
        <el-descriptions-item label="发放人数">{{ sheet.employeeCount }}人</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="sheet.status === 0 ? 'info' : sheet.status === 1 ? 'warning' : sheet.status === 2 ? 'success' : ''">
            {{ statusMap[sheet.status] || '未知' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发放方式">{{ payMethodMap[sheet.payMethod] || '-' }}</el-descriptions-item>
        <el-descriptions-item label="起始日期">{{ sheet.startDate }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ sheet.endDate }}</el-descriptions-item>
        <el-descriptions-item label="基本工资">¥{{ formatMoney(sheet.totalBaseWage) }}</el-descriptions-item>
        <el-descriptions-item label="计件工资">¥{{ formatMoney(sheet.totalPieceWage) }}</el-descriptions-item>
        <el-descriptions-item label="应发总额">¥{{ formatMoney(sheet.totalPayable) }}</el-descriptions-item>
        <el-descriptions-item label="扣款总额">¥{{ formatMoney(sheet.totalDeduction) }}</el-descriptions-item>
        <el-descriptions-item label="实发总额">
          <span style="color: #f56c6c; font-weight: bold;">¥{{ formatMoney(sheet.totalActual) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ sheet.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>工资明细</span>
          <div>
            <el-button v-if="sheet.status === 2" type="primary" size="small" @click="handlePay">发放工资</el-button>
            <el-button size="small" @click="handlePrint">打印工资条</el-button>
          </div>
        </div>
      </template>
      <el-table :data="detailData" border v-loading="detailLoading" show-summary :summary-method="getSummary">
        <el-table-column prop="employeeCode" label="工号" width="80" fixed="left" />
        <el-table-column prop="employeeName" label="姓名" width="90" fixed="left" />
        <el-table-column prop="departmentName" label="部门" width="100" />
        <el-table-column prop="positionName" label="岗位" width="90" />
        <el-table-column prop="costType" label="用工性质" width="90" />
        <el-table-column prop="baseWage" label="基本工资" width="100">
          <template #default="{ row }">{{ formatMoney(row.baseWage) }}</template>
        </el-table-column>
        <el-table-column prop="pieceWage" label="计件工资" width="100">
          <template #default="{ row }">{{ formatMoney(row.pieceWage) }}</template>
        </el-table-column>
        <el-table-column prop="hourWage" label="计时工资" width="100">
          <template #default="{ row }">{{ formatMoney(row.hourWage) }}</template>
        </el-table-column>
        <el-table-column prop="overtimeWage" label="加班工资" width="100">
          <template #default="{ row }">{{ formatMoney(row.overtimeWage) }}</template>
        </el-table-column>
        <el-table-column prop="bonus" label="奖金" width="80">
          <template #default="{ row }">{{ formatMoney(row.bonus) }}</template>
        </el-table-column>
        <el-table-column prop="allowance" label="津贴" width="80">
          <template #default="{ row }">{{ formatMoney(row.allowance) }}</template>
        </el-table-column>
        <el-table-column prop="totalPayable" label="应发合计" width="110">
          <template #default="{ row }">
            <span style="color: #409eff; font-weight: bold;">{{ formatMoney(row.totalPayable) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="personalTax" label="个税" width="80">
          <template #default="{ row }">{{ formatMoney(row.personalTax) }}</template>
        </el-table-column>
        <el-table-column prop="socialInsurance" label="社保" width="80">
          <template #default="{ row }">{{ formatMoney(row.socialInsurance) }}</template>
        </el-table-column>
        <el-table-column prop="housingFund" label="公积金" width="80">
          <template #default="{ row }">{{ formatMoney(row.housingFund) }}</template>
        </el-table-column>
        <el-table-column prop="totalDeduction" label="扣款合计" width="100">
          <template #default="{ row }">{{ formatMoney(row.totalDeduction) }}</template>
        </el-table-column>
        <el-table-column prop="totalActual" label="实发合计" width="110" fixed="right">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold;">{{ formatMoney(row.totalActual) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPayrollSheetDetail, getPayrollSheetDetailPage, updatePayrollSheetStatus } from '@/api/wage'

const route = useRoute()
const router = useRouter()
const statusMap: Record<number, string> = { 0: '草稿', 1: '待审核', 2: '已审核', 3: '已发放' }
const payMethodMap: Record<number, string> = { 0: '银行代发', 1: '现金发放', 2: '混合' }

const sheetId = Number(route.params.id)
const sheet = ref<any>({})
const detailData = ref<any[]>([])
const detailLoading = ref(false)

const formatMoney = (value: number) => value != null ? `¥${Number(value).toFixed(2)}` : '¥0.00'

const goBack = () => router.push('/wage/sheet')

const getSummary = (param: any) => {
  const { columns, data } = param
  const sums: string[] = []
  const moneyProps = ['baseWage', 'pieceWage', 'hourWage', 'overtimeWage', 'bonus', 'allowance', 'totalPayable', 'personalTax', 'socialInsurance', 'housingFund', 'totalDeduction', 'totalActual']
  columns.forEach((_col: any, index: number) => {
    if (index === 0) { sums[index] = '合计'; return }
    if (index === 1 || index === 2 || index === 3 || index === 4) { sums[index] = ''; return }
    const prop = moneyProps.find(p => columns[index]?.property === p)
    if (prop) {
      sums[index] = formatMoney(data.reduce((sum: number, row: any) => sum + Number(row[prop] || 0), 0))
    } else {
      sums[index] = ''
    }
  })
  return sums
}

const loadData = async () => {
  try {
    const res = await getPayrollSheetDetail(sheetId)
    sheet.value = res.data || {}
    const detailRes = await getPayrollSheetDetailPage({ sheetId, pageNum: 1, pageSize: 500 })
    detailData.value = detailRes.data?.list || []
  } catch { ElMessage.error('加载数据失败') }
}

const handlePay = async () => {
  try {
    await ElMessageBox.confirm('确定要发放该工资单吗？发放后不可撤回。', '提示', { type: 'warning' })
    await updatePayrollSheetStatus(sheetId, 3)
    ElMessage.success('发放成功')
    sheet.value.status = 3
  } catch (e) { if (e !== 'cancel') ElMessage.error('操作失败') }
}

const handlePrint = () => { ElMessage.info('功能即将上线，敬请期待') }

onMounted(() => { if (sheetId) loadData() })
</script>
