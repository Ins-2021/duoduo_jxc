<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-input
          v-model="queryParams.keyword"
          placeholder="客户名称/手机号"
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
        <el-button class="filter-item" type="primary" @click="handleQuery">查询</el-button>
        <el-button class="filter-item" type="primary" plain @click="handleAdd">新增充值</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border style="width: 100%">
        <el-table-column prop="rechargeId" label="ID" width="80" />
        <el-table-column prop="customerName" label="客户名称" min-width="150" />
        <el-table-column prop="amount" label="充值金额" min-width="120">
          <template #default="scope">
            <span style="color: #67c23a; font-weight: bold">{{ scope.row.amount?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="giftAmount" label="赠送金额" min-width="120">
          <template #default="scope">
            <span style="color: #e6a23c">{{ scope.row.giftAmount?.toFixed(2) || '0.00' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="balanceAfter" label="充值后余额" min-width="120" />
        <el-table-column prop="payMethod" label="支付方式" min-width="100">
          <template #default="scope">
            <el-tag>{{ scope.row.payMethod || '现金' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="充值时间" width="160" />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
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

    <el-dialog title="客户充值" v-model="dialogVisible" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="客户" prop="customerId">
          <el-select v-model="form.customerId" placeholder="请选择客户" filterable style="width: 100%">
            <el-option v-for="item in customerList" :key="item.customerId" :label="item.customerName" :value="item.customerId" />
          </el-select>
        </el-form-item>
        <el-form-item label="充值金额" prop="amount">
          <el-input-number v-model="form.amount" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="赠送金额" prop="giftAmount">
          <el-input-number v-model="form.giftAmount" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="支付方式" prop="payMethod">
          <el-select v-model="form.payMethod" placeholder="请选择支付方式" style="width: 100%">
            <el-option label="现金" value="现金" />
            <el-option label="微信" value="微信" />
            <el-option label="支付宝" value="支付宝" />
            <el-option label="银行卡" value="银行卡" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" type="textarea" :rows="2" />
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
import { ElMessage } from 'element-plus'
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
  dateRange: [] as string[]
})

const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const form = reactive({
  customerId: undefined as number | undefined,
  amount: 0,
  giftAmount: 0,
  payMethod: '现金',
  remark: ''
})
const rules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  amount: [{ required: true, message: '请输入充值金额', trigger: 'blur' }]
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
  form.amount = 0
  form.giftAmount = 0
  form.payMethod = '现金'
  form.remark = ''
  dialogVisible.value = true
}

const submitForm = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      ElMessage.success('充值成功')
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
