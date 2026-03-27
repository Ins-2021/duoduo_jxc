<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-input
          v-model="queryParams.keyword"
          placeholder="仓库名称"
          style="width: 200px"
          class="filter-item"
          @keyup.enter="handleQuery"
        />
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px" class="filter-item">
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
        <el-button class="filter-item" type="primary" @click="handleQuery">查询</el-button>
        <el-button class="filter-item" type="primary" plain @click="handleAdd" v-perm="['data:warehouse:add']">新增</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border style="width: 100%">
        <el-table-column prop="warehouseId" label="ID" width="80" />
        <el-table-column prop="warehouseName" label="仓库名称" />
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
            <el-button type="primary" link @click="handleEdit(scope.row)" v-perm="['data:warehouse:edit']">编辑</el-button>
            <el-button
              :type="scope.row.status === 1 ? 'danger' : 'success'"
              link
              @click="handleToggleStatus(scope.row)"
              v-perm="['data:warehouse:edit']"
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

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="仓库名称" prop="warehouseName">
          <el-input v-model="form.warehouseName" placeholder="请输入仓库名称" />
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
import { getWarehouseList, addWarehouse, updateWarehouse, updateWarehouseStatus } from '@/api/warehouse'

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
  warehouseId: undefined as number | undefined,
  warehouseName: '',
  status: 1
})
const rules = {
  warehouseName: [{ required: true, message: '请输入仓库名称', trigger: 'blur' }]
}

const handleQuery = () => {
  loading.value = true
  getWarehouseList(queryParams).then((res: any) => {
    list.value = res.data.list
    total.value = res.data.total
  }).finally(() => {
    loading.value = false
  })
}

const resetForm = () => {
  form.warehouseId = undefined
  form.warehouseName = ''
  form.status = 1
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增仓库'
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  resetForm()
  form.warehouseId = row.warehouseId
  form.warehouseName = row.warehouseName
  form.status = row.status
  dialogTitle.value = '编辑仓库'
  dialogVisible.value = true
}

const handleToggleStatus = (row: any) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = row.status === 1 ? '停用' : '启用'
  ElMessageBox.confirm(`确认要${action}仓库"${row.warehouseName}"吗？`, '提示', {
    type: 'warning'
  }).then(() => {
    updateWarehouseStatus(row.warehouseId, newStatus).then(() => {
      ElMessage.success(`${action}成功`)
      handleQuery()
    })
  })
}

const submitForm = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      if (form.warehouseId) {
        updateWarehouse(form).then(() => {
          ElMessage.success('修改成功')
          dialogVisible.value = false
          handleQuery()
        })
      } else {
        addWarehouse(form).then(() => {
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
