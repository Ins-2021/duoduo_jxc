<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-input
          v-model="queryParams.storeCode"
          placeholder="门店编码"
          style="width: 150px"
          class="filter-item"
          @keyup.enter="handleQuery"
        />
        <el-input
          v-model="queryParams.storeName"
          placeholder="门店名称"
          style="width: 150px"
          class="filter-item"
          @keyup.enter="handleQuery"
        />
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px" class="filter-item">
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
        <el-button class="filter-item" type="primary" @click="handleQuery">查询</el-button>
        <el-button class="filter-item" type="primary" plain @click="handleAdd" v-perm="['data:store:add']">新增</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border style="width: 100%">
        <el-table-column prop="storeId" label="ID" width="80" />
        <el-table-column prop="storeCode" label="门店编码" width="120" />
        <el-table-column prop="storeName" label="门店名称" />
        <el-table-column prop="contactName" label="联系人" width="100" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)" v-perm="['data:store:edit']">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)" v-perm="['data:store:delete']">删除</el-button>
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

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="550px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="门店编码" prop="storeCode">
              <el-input v-model="form.storeCode" placeholder="请输入门店编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="门店名称" prop="storeName">
              <el-input v-model="form.storeName" placeholder="请输入门店名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactName">
              <el-input v-model="form.contactName" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
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
import { getStorePage, createStore, updateStore, deleteStore as deleteStoreApi } from '@/api/store'
import type { StoreDTO } from '@/api/store'

const loading = ref(false)
const list = ref<StoreDTO[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  storeCode: '',
  storeName: '',
  status: undefined as number | undefined
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const form = reactive<StoreDTO>({
  storeId: 0,
  storeCode: '',
  storeName: '',
  address: '',
  phone: '',
  contactName: '',
  status: 1,
  remark: ''
})

const rules = {
  storeCode: [{ required: true, message: '请输入门店编码', trigger: 'blur' }],
  storeName: [{ required: true, message: '请输入门店名称', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getStorePage(queryParams)
    list.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetForm = () => {
  form.storeId = 0
  form.storeCode = ''
  form.storeName = ''
  form.address = ''
  form.phone = ''
  form.contactName = ''
  form.status = 1
  form.remark = ''
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增门店'
  dialogVisible.value = true
}

const handleEdit = (row: StoreDTO) => {
  resetForm()
  Object.assign(form, row)
  dialogTitle.value = '编辑门店'
  dialogVisible.value = true
}

const handleDelete = async (row: StoreDTO) => {
  try {
    await ElMessageBox.confirm(`确定要删除门店【${row.storeName}】吗？`, '提示', {
      type: 'warning'
    })
    await deleteStoreApi(row.storeId)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    console.error(error)
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  try {
    if (form.storeId) {
      await updateStore(form)
      ElMessage.success('修改成功')
    } else {
      await createStore(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.filter-container {
  margin-bottom: 16px;
}
.filter-item {
  margin-right: 10px;
}
.pagination-container {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
