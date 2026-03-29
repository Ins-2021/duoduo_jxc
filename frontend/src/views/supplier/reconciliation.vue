<template>
  <div class="recon-page">
    <el-card>
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="供应商/对账单号" clearable />
        </el-form-item>
        <el-form-item label="对账状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable>
            <el-option label="未对账" :value="0" />
            <el-option label="已对账" :value="1" />
            <el-option label="已确认" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="table-operations">
        <el-button type="primary" @click="handleAdd">新增对账单</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" border>
        <el-table-column prop="reconNo" label="对账单号" width="200" />
        <el-table-column prop="supplierName" label="供应商" width="150" />
        <el-table-column prop="startDate" label="对账开始" width="120" />
        <el-table-column prop="endDate" label="对账结束" width="120" />
        <el-table-column prop="totalAmount" label="应付金额" width="120" align="right">
          <template #default="{ row }">
            {{ row.totalAmount != null ? row.totalAmount.toFixed(4) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="paidAmount" label="已付金额" width="120" align="right">
          <template #default="{ row }">
            {{ row.paidAmount != null ? row.paidAmount.toFixed(4) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="unpaidAmount" label="未付金额" width="120" align="right">
          <template #default="{ row }">
            {{ row.unpaidAmount != null ? row.unpaidAmount.toFixed(4) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">未对账</el-tag>
            <el-tag v-else-if="row.status === 1" type="info">已对账</el-tag>
            <el-tag v-else type="success">已确认</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="success" @click="handleConfirm(row)" v-if="row.status < 2">确认</el-button>
            <el-button link type="danger" @click="handleDelete(row)" v-if="row.status === 0">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryForm.pageNum"
        v-model:page-size="queryForm.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="供应商ID">
          <el-input-number v-model="formData.supplierId" :min="1" />
        </el-form-item>
        <el-form-item label="供应商名称">
          <el-input v-model="formData.supplierName" placeholder="供应商名称" />
        </el-form-item>
        <el-form-item label="对账开始">
          <el-date-picker v-model="formData.startDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="对账结束">
          <el-date-picker v-model="formData.endDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="应付金额">
          <el-input-number v-model="formData.totalAmount" :precision="4" :step="0.0001" />
        </el-form-item>
        <el-form-item label="已付金额">
          <el-input-number v-model="formData.paidAmount" :precision="4" :step="0.0001" />
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
import { getReconList, addRecon, updateRecon, confirmRecon, deleteRecon } from '@/api/supplierReconciliation'
import type { SupplierReconciliationDTO } from '@/api/supplierReconciliation'

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: undefined
})

const formData = reactive<SupplierReconciliationDTO>({
  reconciliationId: undefined,
  reconNo: '',
  supplierId: undefined,
  supplierName: '',
  startDate: '',
  endDate: '',
  totalAmount: 0,
  paidAmount: 0,
  unpaidAmount: 0,
  status: 0,
  remark: ''
})

const tableData = ref<SupplierReconciliationDTO[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')

const handleQuery = async () => {
  try {
    const res = await getReconList(queryForm)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch {
    ElMessage.error('查询失败')
  }
}

const handleReset = () => {
  queryForm.keyword = ''
  queryForm.status = undefined
  queryForm.pageNum = 1
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增对账单'
  Object.assign(formData, {
    reconciliationId: undefined, reconNo: '', supplierId: undefined, supplierName: '',
    startDate: '', endDate: '', totalAmount: 0, paidAmount: 0, unpaidAmount: 0,
    status: 0, remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row: SupplierReconciliationDTO) => {
  dialogTitle.value = '编辑对账单'
  Object.assign(formData, { ...row })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    if (formData.reconciliationId) {
      await updateRecon(formData.reconciliationId, formData)
      ElMessage.success('更新成功')
    } else {
      await addRecon(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    handleQuery()
  } catch {
    ElMessage.error('操作失败')
  }
}

const handleConfirm = async (row: SupplierReconciliationDTO) => {
  try {
    await ElMessageBox.confirm('确定要确认该对账单吗？', '提示', { type: 'warning' })
    await confirmRecon(row.reconciliationId!)
    ElMessage.success('确认成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('确认失败')
    }
  }
}

const handleDelete = async (row: SupplierReconciliationDTO) => {
  try {
    await ElMessageBox.confirm('确定要删除该对账单吗？', '提示', { type: 'warning' })
    await deleteRecon(row.reconciliationId!)
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
</style>
