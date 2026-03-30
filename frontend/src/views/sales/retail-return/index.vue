<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-input
          v-model="queryParams.keyword"
          placeholder="退货单号/客户名称"
          style="width: 200px"
          class="filter-item"
          @keyup.enter="handleQuery"
        />
        <el-date-picker
          v-model="queryParams.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          style="width: 240px"
          class="filter-item"
        />
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px" class="filter-item">
          <el-option label="待审核" :value="0" />
          <el-option label="已审核" :value="1" />
          <el-option label="已完成" :value="2" />
        </el-select>
        <el-button class="filter-item" type="primary" @click="handleQuery">查询</el-button>
        <el-button class="filter-item" type="primary" plain @click="handleAdd">新增退货</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border style="width: 100%">
        <el-table-column prop="returnId" label="ID" width="80" />
        <el-table-column prop="docNo" label="退货单号" min-width="150" />
        <el-table-column prop="customerName" label="客户名称" min-width="120" />
        <el-table-column prop="totalAmount" label="退货金额" min-width="120">
          <template #default="scope">
            <span style="color: #f56c6c; font-weight: bold">{{ scope.row.totalAmount?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 0 ? 'warning' : scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 0 ? '待审核' : scope.row.status === 1 ? '已审核' : '已完成' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column prop="remark" label="退货原因" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" link @click="handleDetail(scope.row)">详情</el-button>
            <el-button v-if="scope.row.status === 0" type="success" link @click="handleAudit(scope.row)">审核</el-button>
            <el-button v-if="scope.row.status === 0" type="danger" link @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-show="total > 0"
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleQuery"
          @current-change="handleQuery"
        />
      </div>
    </el-card>

    <el-dialog title="新增退货单" v-model="dialogVisible" width="800px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户" prop="customerId">
              <el-select v-model="form.customerId" placeholder="请选择客户" filterable style="width: 100%">
                <el-option v-for="item in customerList" :key="item.customerId" :label="item.customerName" :value="item.customerId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="退货日期" prop="returnDate">
              <el-date-picker v-model="form.returnDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="退货原因" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入退货原因" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getCustomerList } from '@/api/customer'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const customerList = ref([])
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: undefined as number | undefined,
  dateRange: [] as string[]
})

const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const form = reactive({
  customerId: undefined as number | undefined,
  returnDate: '',
  remark: ''
})
const rules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  returnDate: [{ required: true, message: '请选择退货日期', trigger: 'change' }]
}

const handleQuery = () => {
  loading.value = true
  setTimeout(() => {
    list.value = []
    total.value = 0
    loading.value = false
  }, 300)
}

const loadCustomerList = () => {
  getCustomerList({ pageNum: 1, pageSize: 100 }).then((res: any) => {
    customerList.value = res.data?.list || []
  })
}

const handleAdd = () => {
  form.customerId = undefined
  form.returnDate = new Date().toISOString().split('T')[0]
  form.remark = ''
  dialogVisible.value = true
}

const handleDetail = (row: any) => {
  ElMessage.info('查看详情: ' + row.docNo)
}

const handleAudit = (row: any) => {
  ElMessageBox.confirm('确认审核该退货单？', '提示', {
    type: 'warning'
  }).then(() => {
    ElMessage.success('审核成功')
    handleQuery()
  })
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确认删除退货单"${row.docNo}"？`, '提示', {
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    handleQuery()
  })
}

const submitForm = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      ElMessage.success('创建成功')
      dialogVisible.value = false
      handleQuery()
    }
  })
}

onMounted(() => {
  loadCustomerList()
  handleQuery()
})
</script>

<style scoped>
.filter-container {
  margin-bottom: 20px;
}
.filter-item {
  margin-right: 10px;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
