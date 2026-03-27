<template>
  <div class="receipt-page">
    <el-card>
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="收款单号">
          <el-input v-model="queryForm.receiptNo" placeholder="收款单号" clearable />
        </el-form-item>
        <el-form-item label="客户">
          <el-input v-model="queryForm.customerName" placeholder="客户名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="状态" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="已审核" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="table-operations">
        <el-button type="primary" @click="handleAdd">新增收款</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" border>
        <el-table-column prop="receiptNo" label="收款单号" width="180" />
        <el-table-column prop="customerName" label="客户名称" width="150" />
        <el-table-column prop="receiptDate" label="收款日期" width="120" />
        <el-table-column prop="receiptAmount" label="收款金额" width="120">
          <template #default="{ row }">
            {{ formatMoney(row.receiptAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="收款方式" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">待审核</el-tag>
            <el-tag v-else type="success">已审核</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button v-perm="'finance:receipt:edit'" link type="success" @click="handleApprove(row)" v-if="row.status === 0">审核</el-button>
            <el-button v-perm="'finance:receipt:delete'" link type="danger" @click="handleDelete(row)" v-if="row.status === 0">删除</el-button>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="客户">
          <el-input v-model="formData.customerName" />
        </el-form-item>
        <el-form-item label="收款日期">
          <el-date-picker v-model="formData.receiptDate" type="date" />
        </el-form-item>
        <el-form-item label="收款金额">
          <el-input-number v-model="formData.receiptAmount" :precision="2" :min="0" />
        </el-form-item>
        <el-form-item label="收款方式">
          <el-select v-model="formData.paymentMethod">
            <el-option label="现金" value="现金" />
            <el-option label="银行转账" value="银行转账" />
            <el-option label="微信支付" value="微信支付" />
            <el-option label="支付宝" value="支付宝" />
          </el-select>
        </el-form-item>
        <el-form-item label="银行账户">
          <el-input v-model="formData.bankAccount" />
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
import { receiptApi } from '@/api/finance'

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  receiptNo: '',
  customerName: '',
  status: undefined
})

const formData = reactive({
  receiptId: undefined,
  customerName: '',
  receiptDate: '',
  receiptAmount: 0,
  paymentMethod: '现金',
  bankAccount: '',
  remark: ''
})

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')

const formatMoney = (value: number) => {
  return value ? `¥${value.toFixed(2)}` : '¥0.00'
}

const handleQuery = async () => {
  try {
    const res = await receiptApi.pageList(queryForm)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const handleReset = () => {
  queryForm.receiptNo = ''
  queryForm.customerName = ''
  queryForm.status = undefined
  queryForm.pageNum = 1
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增收款'
  Object.assign(formData, {
    receiptId: undefined,
    customerName: '',
    receiptDate: new Date(),
    receiptAmount: 0,
    paymentMethod: '现金',
    bankAccount: '',
    remark: ''
  })
  dialogVisible.value = true
}

const handleView = async (row: any) => {
  try {
    const res = await receiptApi.getById(row.receiptId)
    dialogTitle.value = '查看收款'
    Object.assign(formData, res.data)
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const handleSubmit = async () => {
  try {
    if (formData.receiptId) {
      await receiptApi.update(formData)
      ElMessage.success('更新成功')
    } else {
      await receiptApi.create(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    handleQuery()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleApprove = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要审核该收款单吗？', '提示', { type: 'warning' })
    await receiptApi.approve(row.receiptId)
    ElMessage.success('审核成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('审核失败')
    }
  }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该收款单吗？', '提示', { type: 'warning' })
    await receiptApi.delete(row.receiptId)
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
