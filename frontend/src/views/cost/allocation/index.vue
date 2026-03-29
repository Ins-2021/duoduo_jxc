<template>
  <div class="cost-allocation-page">
    <el-card>
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="核算期间">
          <el-input v-model="queryForm.accountPeriod" placeholder="如: 2026-03" clearable />
        </el-form-item>
        <el-form-item label="分摊单号">
          <el-input v-model="queryForm.allocationNo" placeholder="分摊单号" clearable />
        </el-form-item>
        <el-form-item label="产品款号">
          <el-input v-model="queryForm.styleCode" placeholder="产品款号" clearable />
        </el-form-item>
        <el-form-item label="费用类型">
          <el-select v-model="queryForm.costType" placeholder="费用类型" clearable>
            <el-option label="材料费" :value="1" />
            <el-option label="人工费" :value="2" />
            <el-option label="制造费用" :value="3" />
            <el-option label="其他" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="table-operations">
        <el-button type="primary" v-perm="'cost:allocation:add'" @click="handleAdd">新增成本分摊</el-button>
      </div>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="allocationNo" label="分摊单号" width="160" />
        <el-table-column prop="accountPeriod" label="核算期间" width="100" />
        <el-table-column prop="costType" label="费用类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ costTypeMap[row.costType] || '未知' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fromCenterId" label="来源中心ID" width="110" />
        <el-table-column prop="toCenterId" label="目标中心ID" width="110" />
        <el-table-column prop="styleCode" label="产品款号" width="120" />
        <el-table-column prop="styleName" label="产品名称" width="150" />
        <el-table-column prop="allocateMethod" label="分摊方法" width="100">
          <template #default="{ row }">
            <el-tag>{{ methodMap[row.allocateMethod] || '未知' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="allocateBase" label="分摊基数" width="110">
          <template #default="{ row }">{{ Number(row.allocateBase || 0).toFixed(4) }}</template>
        </el-table-column>
        <el-table-column prop="totalBase" label="总基数" width="110">
          <template #default="{ row }">{{ Number(row.totalBase || 0).toFixed(4) }}</template>
        </el-table-column>
        <el-table-column prop="allocateRate" label="分摊比例" width="100">
          <template #default="{ row }">
            {{ row.allocateRate != null ? `${(Number(row.allocateRate) * 100).toFixed(2)}%` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="allocateAmount" label="分摊金额" width="130">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ Number(row.allocateAmount || 0).toFixed(4) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button v-perm="'cost:allocation:edit'" link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-perm="'cost:allocation:delete'" link type="danger" @click="handleDelete(row)">删除</el-button>
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
      <el-form :model="formData" label-width="120px" :disabled="dialogTitle === '查看成本分摊'">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="分摊单号">
              <el-input v-model="formData.allocationNo" placeholder="自动生成" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="核算期间" required>
              <el-input v-model="formData.accountPeriod" placeholder="如: 2026-03" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="费用类型" required>
              <el-select v-model="formData.costType" placeholder="请选择">
                <el-option label="材料费" :value="1" />
                <el-option label="人工费" :value="2" />
                <el-option label="制造费用" :value="3" />
                <el-option label="其他" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分摊方法" required>
              <el-select v-model="formData.allocateMethod" placeholder="请选择">
                <el-option label="产量法" :value="1" />
                <el-option label="工时法" :value="2" />
                <el-option label="机器时法" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="来源中心ID">
              <el-input-number v-model="formData.fromCenterId" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目标中心ID">
              <el-input-number v-model="formData.toCenterId" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="产品款号">
              <el-input v-model="formData.styleCode" placeholder="款式编码" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="分摊基数">
              <el-input-number v-model="formData.allocateBase" :precision="4" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总基数">
              <el-input-number v-model="formData.totalBase" :precision="4" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="分摊比例">
              <el-input-number v-model="formData.allocateRate" :precision="4" :min="0" :max="1" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分摊金额">
              <el-input-number v-model="formData.allocateAmount" :precision="4" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button v-if="dialogTitle !== '查看成本分摊'" type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCostAllocationPage, getCostAllocationDetail, createCostAllocation, updateCostAllocation, deleteCostAllocation } from '@/api/cost'

const costTypeMap: Record<number, string> = { 1: '材料费', 2: '人工费', 3: '制造费用', 4: '其他' }
const methodMap: Record<number, string> = { 1: '产量法', 2: '工时法', 3: '机器时法' }

const queryForm = reactive({ pageNum: 1, pageSize: 10, accountPeriod: '', allocationNo: '', styleCode: '', costType: undefined as number | undefined })
const formData = reactive({
  allocationId: undefined as number | undefined,
  allocationNo: '', accountPeriod: '',
  poolId: undefined as number | undefined,
  costType: 1, fromCenterId: undefined as number | undefined, toCenterId: undefined as number | undefined,
  styleId: undefined as number | undefined,
  styleCode: '', productionId: undefined as number | undefined,
  allocateMethod: 1, allocateBase: 0, totalBase: 0,
  allocateRate: 0, allocateAmount: 0,
  status: 0, remark: ''
})
const tableData = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getCostAllocationPage(queryForm)
    tableData.value = res.data?.list || []; total.value = res.data?.total || 0
  } catch { ElMessage.error('查询失败') }
  finally { loading.value = false }
}

const handleReset = () => { queryForm.accountPeriod = ''; queryForm.allocationNo = ''; queryForm.styleCode = ''; queryForm.costType = undefined; queryForm.pageNum = 1; handleQuery() }

const resetForm = () => Object.assign(formData, {
  allocationId: undefined, allocationNo: '', accountPeriod: '',
  poolId: undefined, costType: 1, fromCenterId: undefined, toCenterId: undefined,
  styleId: undefined, styleCode: '', productionId: undefined,
  allocateMethod: 1, allocateBase: 0, totalBase: 0,
  allocateRate: 0, allocateAmount: 0, status: 0, remark: ''
})

const handleAdd = () => { dialogTitle.value = '新增成本分摊'; resetForm(); dialogVisible.value = true }
const handleView = async (row: any) => {
  dialogTitle.value = '查看成本分摊'
  try { const res = await getCostAllocationDetail(row.allocationId); Object.assign(formData, res.data) } catch { ElMessage.error('查询失败') }
  dialogVisible.value = true
}
const handleEdit = async (row: any) => {
  dialogTitle.value = '编辑成本分摊'
  try { const res = await getCostAllocationDetail(row.allocationId); Object.assign(formData, res.data) } catch { ElMessage.error('查询失败') }
  dialogVisible.value = true
}
const handleSubmit = async () => {
  try {
    if (formData.allocationId) { await updateCostAllocation(formData.allocationId, formData); ElMessage.success('更新成功') }
    else { await createCostAllocation(formData); ElMessage.success('创建成功') }
    dialogVisible.value = false; handleQuery()
  } catch { ElMessage.error('操作失败') }
}
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该成本分摊记录吗？', '提示', { type: 'warning' })
    await deleteCostAllocation(row.allocationId); ElMessage.success('删除成功'); handleQuery()
  } catch (e) { if (e !== 'cancel') ElMessage.error('删除失败') }
}

onMounted(() => handleQuery())
</script>

<style scoped>
.table-operations { margin-bottom: 16px; }
</style>
