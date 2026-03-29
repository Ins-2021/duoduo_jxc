<template>
  <div class="piece-price-page">
    <el-card>
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="产品款号">
          <el-input v-model="queryForm.styleCode" placeholder="产品款号" clearable />
        </el-form-item>
        <el-form-item label="工序编码">
          <el-input v-model="queryForm.processCode" placeholder="工序编码" clearable />
        </el-form-item>
        <el-form-item label="工价类型">
          <el-select v-model="queryForm.priceType" placeholder="工价类型" clearable>
            <el-option label="标准工价" value="standard" />
            <el-option label="员工工价" value="employee" />
            <el-option label="批次工价" value="batch" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="table-operations">
        <el-button type="primary" v-perm="'wage:price:add'" @click="handleAdd">新增工价</el-button>
      </div>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="styleCode" label="产品款号" width="120" />
        <el-table-column prop="styleName" label="产品名称" width="150" />
        <el-table-column prop="processCode" label="工序编码" width="100" />
        <el-table-column prop="processName" label="工序名称" width="120" />
        <el-table-column prop="employeeName" label="员工姓名" width="100">
          <template #default="{ row }">
            {{ row.employeeName || '通用' }}
          </template>
        </el-table-column>
        <el-table-column prop="unitPrice" label="计件单价" width="120">
          <template #default="{ row }">
            <span style="color: #67c23a; font-weight: bold;">¥{{ Number(row.unitPrice).toFixed(4) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="priceType" label="工价类型" width="100">
          <template #default="{ row }">
            <el-tag :type="priceTypeTag(row.priceType)">{{ priceTypeMap[row.priceType] || row.priceType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="effectiveDate" label="生效日期" width="110" />
        <el-table-column prop="expireDate" label="失效日期" width="110" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button v-perm="'wage:price:edit'" link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-perm="'wage:price:delete'" link type="danger" @click="handleDelete(row)">删除</el-button>
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
      <el-form :model="formData" label-width="120px" :disabled="dialogTitle === '查看工价'">
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
          <el-col :span="12">
            <el-form-item label="计件单价" required>
              <el-input-number v-model="formData.unitPrice" :precision="4" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位">
              <el-input v-model="formData.unit" placeholder="如: 件/片" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="工价类型">
              <el-select v-model="formData.priceType" placeholder="请选择">
                <el-option label="标准工价" value="standard" />
                <el-option label="员工工价" value="employee" />
                <el-option label="批次工价" value="batch" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="员工姓名">
              <el-input v-model="formData.employeeName" placeholder="员工工价时填写" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="生效日期">
              <el-date-picker v-model="formData.effectiveDate" type="date" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="失效日期">
              <el-date-picker v-model="formData.expireDate" type="date" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button v-if="dialogTitle !== '查看工价'" type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPiecePricePage, getPiecePriceDetail, createPiecePrice, updatePiecePrice, deletePiecePrice } from '@/api/wage'

const priceTypeMap: Record<string, string> = { standard: '标准工价', employee: '员工工价', batch: '批次工价' }
const priceTypeTag = (type: string) => type === 'employee' ? 'warning' : type === 'batch' ? 'danger' : 'success'

const queryForm = reactive({ pageNum: 1, pageSize: 10, styleCode: '', processCode: '', priceType: '' })
const formData = reactive({
  priceId: undefined as number | undefined,
  styleCode: '', styleName: '', processCode: '', processName: '',
  employeeName: '', unitPrice: 0, unit: '件',
  priceType: 'standard', effectiveDate: '', expireDate: '', remark: ''
})
const tableData = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getPiecePricePage(queryForm)
    tableData.value = res.data?.list || []; total.value = res.data?.total || 0
  } catch { ElMessage.error('查询失败') }
  finally { loading.value = false }
}

const handleReset = () => { queryForm.styleCode = ''; queryForm.processCode = ''; queryForm.priceType = ''; queryForm.pageNum = 1; handleQuery() }

const resetForm = () => Object.assign(formData, {
  priceId: undefined, styleCode: '', styleName: '', processCode: '', processName: '',
  employeeName: '', unitPrice: 0, unit: '件', priceType: 'standard', effectiveDate: '', expireDate: '', remark: ''
})

const handleAdd = () => { dialogTitle.value = '新增工价'; resetForm(); dialogVisible.value = true }
const handleView = async (row: any) => {
  dialogTitle.value = '查看工价'
  try { const res = await getPiecePriceDetail(row.priceId); Object.assign(formData, res.data) } catch { ElMessage.error('查询失败') }
  dialogVisible.value = true
}
const handleEdit = async (row: any) => {
  dialogTitle.value = '编辑工价'
  try { const res = await getPiecePriceDetail(row.priceId); Object.assign(formData, res.data) } catch { ElMessage.error('查询失败') }
  dialogVisible.value = true
}
const handleSubmit = async () => {
  try {
    if (formData.priceId) { await updatePiecePrice(formData.priceId, formData); ElMessage.success('更新成功') }
    else { await createPiecePrice(formData); ElMessage.success('创建成功') }
    dialogVisible.value = false; handleQuery()
  } catch { ElMessage.error('操作失败') }
}
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该工价记录吗？', '提示', { type: 'warning' })
    await deletePiecePrice(row.priceId); ElMessage.success('删除成功'); handleQuery()
  } catch (e) { if (e !== 'cancel') ElMessage.error('删除失败') }
}

onMounted(() => handleQuery())
</script>

<style scoped>
.table-operations { margin-bottom: 16px; }
</style>
