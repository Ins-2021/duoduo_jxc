<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-input
          v-model="queryParams.keyword"
          placeholder="客户名称"
          style="width: 200px"
          class="filter-item"
          @keyup.enter="handleQuery"
        />
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px" class="filter-item">
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
        <el-button class="filter-item" type="primary" @click="handleQuery">查询</el-button>
        <el-button class="filter-item" type="primary" plain @click="handleAdd" v-perm="['data:customer:add']">新增</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border style="width: 100%">
        <el-table-column prop="customerId" label="ID" width="80" />
        <el-table-column prop="customerName" label="客户名称" min-width="150" />
        <el-table-column prop="contactName" label="联系人" min-width="120" />
        <el-table-column prop="contactPhone" label="手机号" min-width="120" />
        <el-table-column prop="address" label="地址" min-width="180" show-overflow-tooltip />
        <el-table-column prop="level" label="等级" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.level === 'vip' ? 'warning' : 'info'" effect="dark" size="small">
              {{ scope.row.level === 'vip' ? 'VIP客户' : '普通客户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)" v-perm="['data:customer:edit']">编辑</el-button>
            <el-button
              :type="scope.row.status === 1 ? 'danger' : 'success'"
              link
              @click="handleToggleStatus(scope.row)"
              v-perm="['data:customer:edit']"
            >
              {{ scope.row.status === 1 ? '停用' : '启用' }}
            </el-button>
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

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="客户名称" prop="customerName">
          <el-input v-model="form.customerName" placeholder="请输入客户名称" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="form.contactName" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="手机号" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="联系地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入联系地址" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="客户等级" prop="level">
          <el-radio-group v-model="form.level">
            <el-radio value="normal">普通客户</el-radio>
            <el-radio value="vip">VIP客户</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
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
import { getCustomerList, addCustomer, updateCustomer, updateCustomerStatus } from '@/api/customer'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: undefined
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const form = reactive({
  customerId: undefined as number | undefined,
  customerName: '',
  contactName: '',
  contactPhone: '',
  address: '',
  level: 'normal',
  status: 1
})
const rules = {
  customerName: [{ required: true, message: '请输入客户名称', trigger: 'blur' }]
}

const handleQuery = () => {
  loading.value = true
  getCustomerList(queryParams).then((res: any) => {
    list.value = res.data.list
    total.value = res.data.total
  }).finally(() => {
    loading.value = false
  })
}

const resetForm = () => {
  form.customerId = undefined
  form.customerName = ''
  form.contactName = ''
  form.contactPhone = ''
  form.address = ''
  form.level = 'normal'
  form.status = 1
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增客户'
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  resetForm()
  form.customerId = row.customerId
  form.customerName = row.customerName
  form.contactName = row.contactName
  form.contactPhone = row.contactPhone
  form.address = row.address
  form.level = row.level || 'normal'
  form.status = row.status
  dialogTitle.value = '编辑客户'
  dialogVisible.value = true
}

const handleToggleStatus = (row: any) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = row.status === 1 ? '停用' : '启用'
  ElMessageBox.confirm(`确认要${action}客户"${row.customerName}"吗？`, '提示', {
    type: 'warning'
  }).then(() => {
    updateCustomerStatus(row.customerId, newStatus).then(() => {
      ElMessage.success(`${action}成功`)
      handleQuery()
    })
  })
}

const submitForm = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      if (form.customerId) {
        updateCustomer(form).then(() => {
          ElMessage.success('修改成功')
          dialogVisible.value = false
          handleQuery()
        })
      } else {
        addCustomer(form).then(() => {
          ElMessage.success('新增成功')
          dialogVisible.value = false
          handleQuery()
        })
      }
    }
  })
}

onMounted(() => {
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
