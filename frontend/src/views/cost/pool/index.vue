<template>
  <div class="cost-pool-page">
    <el-card>
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="费用编号">
          <el-input v-model="queryForm.poolNo" placeholder="费用编号" clearable />
        </el-form-item>
        <el-form-item label="核算期间">
          <el-input v-model="queryForm.accountPeriod" placeholder="如: 2026-03" clearable />
        </el-form-item>
        <el-form-item label="费用类型">
          <el-select v-model="queryForm.costType" placeholder="费用类型" clearable>
            <el-option label="材料费" :value="1" />
            <el-option label="人工费" :value="2" />
            <el-option label="制造费用" :value="3" />
            <el-option label="其他" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="状态" clearable>
            <el-option label="待归集" :value="0" />
            <el-option label="已归集" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="table-operations">
        <el-button type="primary" v-perm="'cost:pool:add'" @click="handleAdd">新增成本池</el-button>
      </div>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="poolNo" label="费用编号" width="160" />
        <el-table-column prop="accountPeriod" label="核算期间" width="110" />
        <el-table-column prop="costType" label="费用类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ costTypeMap[row.costType] || '未知' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="styleCode" label="款式编码" width="120" />
        <el-table-column prop="styleName" label="款式名称" width="150" />
        <el-table-column prop="totalAmount" label="费用总额" width="130">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ Number(row.totalAmount || 0).toFixed(4) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="allocatedAmount" label="已分摊金额" width="130">
          <template #default="{ row }">
            ¥{{ Number(row.allocatedAmount || 0).toFixed(4) }}
          </template>
        </el-table-column>
        <el-table-column prop="remainingAmount" label="待分摊金额" width="130">
          <template #default="{ row }">
            ¥{{ Number(row.remainingAmount || 0).toFixed(4) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '已归集' : '待归集' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button v-perm="'cost:pool:edit'" link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-perm="'cost:pool:delete'" link type="danger" @click="handleDelete(row)">删除</el-button>
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
      <el-form :model="formData" label-width="120px" :disabled="dialogTitle === '查看成本池'">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="费用编号" required>
              <el-input v-model="formData.poolNo" placeholder="请输入费用编号" />
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
            <el-form-item label="成本中心ID">
              <el-input-number v-model="formData.costCenterId" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="款式编码">
              <el-input v-model="formData.styleCode" placeholder="款式编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="款式名称">
              <el-input v-model="formData.styleName" placeholder="款式名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="费用总额" required>
              <el-input-number v-model="formData.totalAmount" :precision="4" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="已分摊">
              <el-input-number v-model="formData.allocatedAmount" :precision="4" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="待分摊">
              <el-input-number v-model="formData.remainingAmount" :precision="4" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button v-if="dialogTitle !== '查看成本池'" type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCostPoolPage, getCostPoolDetail, createCostPool, updateCostPool, deleteCostPool } from '@/api/cost'

const costTypeMap: Record<number, string> = { 1: '材料费', 2: '人工费', 3: '制造费用', 4: '其他' }

const queryForm = reactive({ pageNum: 1, pageSize: 10, poolNo: '', accountPeriod: '', costType: undefined as number | undefined, status: undefined as number | undefined })
const formData = reactive({
  poolId: undefined as number | undefined,
  poolNo: '', accountPeriod: '', costType: 1,
  costCenterId: undefined as number | undefined,
  styleId: undefined as number | undefined,
  styleCode: '', styleName: '',
  productionId: undefined as number | undefined,
  totalAmount: 0, allocatedAmount: 0, remainingAmount: 0,
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
    const res = await getCostPoolPage(queryForm)
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch { ElMessage.error('查询失败') }
  finally { loading.value = false }
}

const handleReset = () => {
  queryForm.poolNo = ''; queryForm.accountPeriod = ''; queryForm.costType = undefined; queryForm.status = undefined
  queryForm.pageNum = 1; handleQuery()
}

const resetForm = () => Object.assign(formData, {
  poolId: undefined, poolNo: '', accountPeriod: '', costType: 1,
  costCenterId: undefined, styleId: undefined, styleCode: '', styleName: '',
  productionId: undefined, totalAmount: 0, allocatedAmount: 0, remainingAmount: 0,
  status: 0, remark: ''
})

const handleAdd = () => { dialogTitle.value = '新增成本池'; resetForm(); dialogVisible.value = true }
const handleView = async (row: any) => {
  dialogTitle.value = '查看成本池'
  try { const res = await getCostPoolDetail(row.poolId); Object.assign(formData, res.data) } catch { ElMessage.error('查询失败') }
  dialogVisible.value = true
}
const handleEdit = async (row: any) => {
  dialogTitle.value = '编辑成本池'
  try { const res = await getCostPoolDetail(row.poolId); Object.assign(formData, res.data) } catch { ElMessage.error('查询失败') }
  dialogVisible.value = true
}
const handleSubmit = async () => {
  try {
    if (formData.poolId) { await updateCostPool(formData.poolId, formData); ElMessage.success('更新成功') }
    else { await createCostPool(formData); ElMessage.success('创建成功') }
    dialogVisible.value = false; handleQuery()
  } catch { ElMessage.error('操作失败') }
}
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该成本池吗？', '提示', { type: 'warning' })
    await deleteCostPool(row.poolId); ElMessage.success('删除成功'); handleQuery()
  } catch (e) { if (e !== 'cancel') ElMessage.error('删除失败') }
}

onMounted(() => handleQuery())
</script>

<style scoped>
.table-operations { margin-bottom: 16px; }
</style>
