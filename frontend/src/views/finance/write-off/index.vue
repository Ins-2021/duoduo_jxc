<template>
  <div class="write-off-page">
    <el-card>
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="核销单号">
          <el-input v-model="queryForm.writeOffNo" placeholder="核销单号" clearable />
        </el-form-item>
        <el-form-item label="客户/供应商">
          <el-input v-model="queryForm.partnerName" placeholder="客户/供应商名称" clearable />
        </el-form-item>
        <el-form-item label="核销类型">
          <el-select v-model="queryForm.writeOffType" placeholder="核销类型" clearable>
            <el-option label="应收核销" value="应收核销" />
            <el-option label="应付核销" value="应付核销" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="table-operations">
        <el-button type="primary" @click="handleAdd">新增核销</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" border>
        <el-table-column prop="writeOffNo" label="核销单号" width="180" />
        <el-table-column prop="writeOffType" label="核销类型" width="100" />
        <el-table-column prop="partnerName" label="客户/供应商" width="150" />
        <el-table-column prop="writeOffDate" label="核销日期" width="120" />
        <el-table-column prop="writeOffAmount" label="核销金额" width="120">
          <template #default="{ row }">
            {{ formatMoney(row.writeOffAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="discountAmount" label="优惠金额" width="120">
          <template #default="{ row }">
            {{ formatMoney(row.discountAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="100" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button v-perm="'finance:write-off:delete'" link type="danger" @click="handleDelete(row)">删除</el-button>
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
        <el-form-item label="核销类型">
          <el-select v-model="formData.writeOffType">
            <el-option label="应收核销" value="应收核销" />
            <el-option label="应付核销" value="应付核销" />
          </el-select>
        </el-form-item>
        <el-form-item label="客户/供应商">
          <el-input v-model="formData.partnerName" />
        </el-form-item>
        <el-form-item label="核销日期">
          <el-date-picker v-model="formData.writeOffDate" type="date" />
        </el-form-item>
        <el-form-item label="核销金额">
          <el-input-number v-model="formData.writeOffAmount" :precision="2" :min="0" />
        </el-form-item>
        <el-form-item label="优惠金额">
          <el-input-number v-model="formData.discountAmount" :precision="2" :min="0" />
        </el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="formData.paymentMethod">
            <el-option label="现金" value="现金" />
            <el-option label="银行转账" value="银行转账" />
            <el-option label="微信支付" value="微信支付" />
            <el-option label="支付宝" value="支付宝" />
          </el-select>
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
import { writeOffApi } from '@/api/finance'

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  writeOffNo: '',
  partnerName: '',
  writeOffType: ''
})

const formData = reactive({
  writeOffId: undefined,
  writeOffType: '',
  partnerName: '',
  writeOffDate: '',
  writeOffAmount: 0,
  discountAmount: 0,
  paymentMethod: '现金',
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
    const res = await writeOffApi.pageList(queryForm)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const handleReset = () => {
  queryForm.writeOffNo = ''
  queryForm.partnerName = ''
  queryForm.writeOffType = ''
  queryForm.pageNum = 1
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增核销'
  Object.assign(formData, {
    writeOffId: undefined,
    writeOffType: '',
    partnerName: '',
    writeOffDate: new Date().toISOString().slice(0, 10),
    writeOffAmount: 0,
    discountAmount: 0,
    paymentMethod: '现金',
    remark: ''
  })
  dialogVisible.value = true
}

const handleView = async (row: any) => {
  try {
    const res = await writeOffApi.getById(row.writeOffId)
    dialogTitle.value = '查看核销'
    Object.assign(formData, res.data)
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const handleSubmit = async () => {
  try {
    if (formData.writeOffId) {
      await writeOffApi.update(formData)
      ElMessage.success('更新成功')
    } else {
      await writeOffApi.create(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    handleQuery()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该核销单吗？', '提示', { type: 'warning' })
    await writeOffApi.delete(row.writeOffId)
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
