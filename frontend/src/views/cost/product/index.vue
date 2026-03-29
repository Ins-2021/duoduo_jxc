<template>
  <div class="product-cost-page">
    <el-card>
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="产品款号">
          <el-input v-model="queryForm.styleCode" placeholder="产品款号" clearable />
        </el-form-item>
        <el-form-item label="核算期间">
          <el-input v-model="queryForm.accountPeriod" placeholder="如: 2026-03" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="table-operations">
        <el-button type="primary" v-perm="'cost:product:add'" @click="handleAdd">新增产品成本</el-button>
      </div>

      <el-table :data="tableData" border v-loading="loading" show-summary :summary-method="getSummary">
        <el-table-column prop="styleCode" label="产品款号" width="120" />
        <el-table-column prop="styleName" label="产品名称" width="150" />
        <el-table-column prop="accountPeriod" label="核算期间" width="100" />
        <el-table-column prop="outputQuantity" label="完工数量" width="100" />
        <el-table-column prop="materialCost" label="材料成本" width="120">
          <template #default="{ row }">{{ formatMoney(row.materialCost) }}</template>
        </el-table-column>
        <el-table-column prop="laborCost" label="人工成本" width="120">
          <template #default="{ row }">{{ formatMoney(row.laborCost) }}</template>
        </el-table-column>
        <el-table-column prop="overheadCost" label="制造费用" width="120">
          <template #default="{ row }">{{ formatMoney(row.overheadCost) }}</template>
        </el-table-column>
        <el-table-column prop="totalCost" label="总成本" width="130">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold;">{{ formatMoney(row.totalCost) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="unitCost" label="单位成本" width="120">
          <template #default="{ row }">
            <span style="color: #409eff; font-weight: bold;">{{ formatMoney(row.unitCost) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button v-perm="'cost:product:edit'" link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-perm="'cost:product:delete'" link type="danger" @click="handleDelete(row)">删除</el-button>
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
      <el-form :model="formData" label-width="120px" :disabled="dialogTitle === '查看产品成本'">
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
            <el-form-item label="核算期间">
              <el-input v-model="formData.accountPeriod" placeholder="如: 2026-03" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="完工数量">
              <el-input-number v-model="formData.outputQuantity" :precision="0" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="left">成本明细</el-divider>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="材料成本">
              <el-input-number v-model="formData.materialCost" :precision="2" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="人工成本">
              <el-input-number v-model="formData.laborCost" :precision="2" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="制造费用">
              <el-input-number v-model="formData.overheadCost" :precision="2" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总成本">
              <el-input :model-value="formatMoney(totalCostComputed)" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button v-if="dialogTitle !== '查看产品成本'" type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProductCostPage, getProductCostDetail, createProductCost, updateProductCost, deleteProductCost } from '@/api/cost'

const queryForm = reactive({ pageNum: 1, pageSize: 10, styleCode: '', accountPeriod: '' })
const formData = reactive({
  productCostId: undefined as number | undefined,
  styleCode: '', styleName: '', accountPeriod: '', outputQuantity: 0,
  materialCost: 0, laborCost: 0, overheadCost: 0, totalCost: 0, remark: ''
})
const tableData = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')

const totalCostComputed = computed(() => (Number(formData.materialCost) + Number(formData.laborCost) + Number(formData.overheadCost)))

const formatMoney = (value: number) => value != null ? `¥${Number(value).toFixed(2)}` : '¥0.00'

const getSummary = (param: any) => {
  const { columns, data } = param
  const sums: string[] = []
  columns.forEach((_col: any, index: number) => {
    if (index === 0) { sums[index] = '合计'; return }
    if (index === 1) { sums[index] = ''; return }
    const prop = ['materialCost', 'laborCost', 'overheadCost', 'totalCost', 'unitCost', 'outputQuantity'].find(p => columns[index]?.property === p)
    if (prop) {
      const val = data.reduce((sum: number, row: any) => sum + Number(row[prop] || 0), 0)
      sums[index] = prop === 'outputQuantity' ? String(val) : formatMoney(val)
    } else {
      sums[index] = ''
    }
  })
  return sums
}

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getProductCostPage(queryForm)
    tableData.value = res.data?.list || []; total.value = res.data?.total || 0
  } catch { ElMessage.error('查询失败') }
  finally { loading.value = false }
}

const handleReset = () => { queryForm.styleCode = ''; queryForm.accountPeriod = ''; queryForm.pageNum = 1; handleQuery() }

const resetForm = () => Object.assign(formData, {
  productCostId: undefined, styleCode: '', styleName: '', accountPeriod: '', outputQuantity: 0,
  materialCost: 0, laborCost: 0, overheadCost: 0, totalCost: 0, remark: ''
})

const handleAdd = () => { dialogTitle.value = '新增产品成本'; resetForm(); dialogVisible.value = true }
const handleView = async (row: any) => {
  dialogTitle.value = '查看产品成本'
  try { const res = await getProductCostDetail(row.productCostId); Object.assign(formData, res.data) } catch { ElMessage.error('查询失败') }
  dialogVisible.value = true
}
const handleEdit = async (row: any) => {
  dialogTitle.value = '编辑产品成本'
  try { const res = await getProductCostDetail(row.productCostId); Object.assign(formData, res.data) } catch { ElMessage.error('查询失败') }
  dialogVisible.value = true
}
const handleSubmit = async () => {
  try {
    formData.totalCost = totalCostComputed.value
    if (formData.productCostId) { await updateProductCost(formData.productCostId, formData); ElMessage.success('更新成功') }
    else { await createProductCost(formData); ElMessage.success('创建成功') }
    dialogVisible.value = false; handleQuery()
  } catch { ElMessage.error('操作失败') }
}
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该产品成本记录吗？', '提示', { type: 'warning' })
    await deleteProductCost(row.productCostId); ElMessage.success('删除成功'); handleQuery()
  } catch (e) { if (e !== 'cancel') ElMessage.error('删除失败') }
}

onMounted(() => handleQuery())
</script>

<style scoped>
.table-operations { margin-bottom: 16px; }
</style>
