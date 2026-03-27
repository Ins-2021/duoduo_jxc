<template>
  <div class="receivable-page">
    <el-card>
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="客户">
          <el-input v-model="queryForm.customerName" placeholder="客户名称" clearable />
        </el-form-item>
        <el-form-item label="单据编号">
          <el-input v-model="queryForm.documentNo" placeholder="单据编号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="状态" clearable>
            <el-option label="未核销" :value="0" />
            <el-option label="部分核销" :value="1" />
            <el-option label="已核销" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="table-operations">
        <el-button type="primary" @click="handleAdd">新增应收</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" border>
        <el-table-column prop="receivableNo" label="应收编号" width="180" />
        <el-table-column prop="customerName" label="客户名称" width="150" />
        <el-table-column prop="documentType" label="单据类型" width="120" />
        <el-table-column prop="documentNo" label="单据编号" width="180" />
        <el-table-column prop="receivableAmount" label="应收金额" width="120">
          <template #default="{ row }">
            {{ formatMoney(row.receivableAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="paidAmount" label="已收金额" width="120">
          <template #default="{ row }">
            {{ formatMoney(row.paidAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="remainingAmount" label="剩余金额" width="120">
          <template #default="{ row }">
            {{ formatMoney(row.remainingAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">未核销</el-tag>
            <el-tag v-else-if="row.status === 1" type="info">部分核销</el-tag>
            <el-tag v-else type="success">已核销</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dueDate" label="到期日" width="120" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button v-perm="'finance:receivable:edit'" link type="primary" @click="handleWriteOff(row)" :disabled="row.status === 2">核销</el-button>
            <el-button v-perm="'finance:receivable:delete'" link type="danger" @click="handleDelete(row)">删除</el-button>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="客户">
          <el-input v-model="formData.customerName" />
        </el-form-item>
        <el-form-item label="单据类型">
          <el-input v-model="formData.documentType" />
        </el-form-item>
        <el-form-item label="单据编号">
          <el-input v-model="formData.documentNo" />
        </el-form-item>
        <el-form-item label="应收金额">
          <el-input-number v-model="formData.receivableAmount" :precision="2" :min="0" />
        </el-form-item>
        <el-form-item label="到期日">
          <el-date-picker v-model="formData.dueDate" type="date" />
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

    <!-- 核销对话框 -->
    <el-dialog v-model="writeOffDialogVisible" title="应收核销" width="500px">
      <el-form :model="writeOffForm" label-width="100px">
        <el-form-item label="应收金额">
          <el-input :value="formatMoney(currentRow?.receivableAmount)" disabled />
        </el-form-item>
        <el-form-item label="已收金额">
          <el-input :value="formatMoney(currentRow?.paidAmount)" disabled />
        </el-form-item>
        <el-form-item label="剩余金额">
          <el-input :value="formatMoney(currentRow?.remainingAmount)" disabled />
        </el-form-item>
        <el-form-item label="核销金额">
          <el-input-number v-model="writeOffForm.amount" :precision="2" :min="0" :max="currentRow?.remainingAmount" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="writeOffDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleWriteOffSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { receivableApi } from '@/api/finance'

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  customerName: '',
  documentNo: '',
  status: undefined
})

const formData = reactive({
  receivableId: undefined,
  customerName: '',
  documentType: '',
  documentNo: '',
  receivableAmount: 0,
  dueDate: '',
  remark: ''
})

const writeOffForm = reactive({
  amount: 0
})

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const writeOffDialogVisible = ref(false)
const currentRow = ref(null)

const formatMoney = (value: number) => {
  return value ? `¥${value.toFixed(2)}` : '¥0.00'
}

const handleQuery = async () => {
  try {
    const res = await receivableApi.pageList(queryForm)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const handleReset = () => {
  queryForm.customerName = ''
  queryForm.documentNo = ''
  queryForm.status = undefined
  queryForm.pageNum = 1
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增应收'
  Object.assign(formData, {
    receivableId: undefined,
    customerName: '',
    documentType: '',
    documentNo: '',
    receivableAmount: 0,
    dueDate: '',
    remark: ''
  })
  dialogVisible.value = true
}

const handleView = async (row: any) => {
  try {
    const res = await receivableApi.getById(row.receivableId)
    dialogTitle.value = '查看应收'
    Object.assign(formData, res.data)
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const handleSubmit = async () => {
  try {
    if (formData.receivableId) {
      await receivableApi.update(formData)
      ElMessage.success('更新成功')
    } else {
      await receivableApi.create(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    handleQuery()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleWriteOff = (row: any) => {
  currentRow.value = row
  writeOffForm.amount = row.remainingAmount
  writeOffDialogVisible.value = true
}

const handleWriteOffSubmit = async () => {
  try {
    await receivableApi.writeOff(currentRow.value.receivableId, writeOffForm.amount)
    ElMessage.success('核销成功')
    writeOffDialogVisible.value = false
    handleQuery()
  } catch (error) {
    ElMessage.error('核销失败')
  }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该应收账款吗？', '提示', { type: 'warning' })
    await receivableApi.delete(row.receivableId)
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
