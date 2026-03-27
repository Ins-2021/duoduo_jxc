<template>
  <div class="payment-page">
    <el-card>
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="付款单号">
          <el-input v-model="queryForm.paymentNo" placeholder="付款单号" clearable />
        </el-form-item>
        <el-form-item label="供应商">
          <el-input v-model="queryForm.supplierName" placeholder="供应商名称" clearable />
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
        <el-button type="primary" v-perm="'finance:payment:add'" @click="handleAdd">新增付款</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" border>
        <el-table-column prop="paymentNo" label="付款单号" width="180" />
        <el-table-column prop="supplierName" label="供应商名称" width="150" />
        <el-table-column prop="paymentDate" label="付款日期" width="120" />
        <el-table-column prop="paymentAmount" label="付款金额" width="120">
          <template #default="{ row }">
            {{ formatMoney(row.paymentAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="付款方式" width="100" />
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
            <el-button v-perm="'finance:payment:edit'" link type="success" @click="handleApprove(row)" v-if="row.status === 0">审核</el-button>
            <el-button v-perm="'finance:payment:delete'" link type="danger" @click="handleDelete(row)" v-if="row.status === 0">删除</el-button>
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
        <el-form-item label="供应商">
          <el-input v-model="formData.supplierName" />
        </el-form-item>
        <el-form-item label="付款日期">
          <el-date-picker v-model="formData.paymentDate" type="date" />
        </el-form-item>
        <el-form-item label="付款金额">
          <el-input-number v-model="formData.paymentAmount" :precision="2" :min="0" />
        </el-form-item>
        <el-form-item label="付款方式">
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
import { paymentApi } from '@/api/finance'

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  paymentNo: '',
  supplierName: '',
  status: undefined
})

const formData = reactive({
  paymentId: undefined,
  supplierName: '',
  paymentDate: '',
  paymentAmount: 0,
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
    const res = await paymentApi.pageList(queryForm)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const handleReset = () => {
  queryForm.paymentNo = ''
  queryForm.supplierName = ''
  queryForm.status = undefined
  queryForm.pageNum = 1
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增付款'
  Object.assign(formData, {
    paymentId: undefined,
    supplierName: '',
    paymentDate: new Date(),
    paymentAmount: 0,
    paymentMethod: '现金',
    bankAccount: '',
    remark: ''
  })
  dialogVisible.value = true
}

const handleView = async (row: any) => {
  try {
    const res = await paymentApi.getById(row.paymentId)
    dialogTitle.value = '查看付款'
    Object.assign(formData, res.data)
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const handleSubmit = async () => {
  try {
    if (formData.paymentId) {
      await paymentApi.update(formData)
      ElMessage.success('更新成功')
    } else {
      await paymentApi.create(formData)
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
    await ElMessageBox.confirm('确定要审核该付款单吗？', '提示', { type: 'warning' })
    await paymentApi.approve(row.paymentId)
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
    await ElMessageBox.confirm('确定要删除该付款单吗？', '提示', { type: 'warning' })
    await paymentApi.delete(row.paymentId)
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
