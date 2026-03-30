<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-input
          v-model="queryParams.keyword"
          placeholder="角色名称"
          style="width: 200px"
          class="filter-item"
          @keyup.enter="handleQuery"
        />
        <el-button class="filter-item" type="primary" @click="handleQuery">查询</el-button>
        <el-button class="filter-item" type="primary" plain @click="handleAdd">新增</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border style="width: 100%">
        <el-table-column prop="roleId" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" min-width="150" />
        <el-table-column prop="roleCode" label="角色编码" min-width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="250">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="warning" link @click="handlePermission(scope.row)">权限</el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
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

    <el-dialog title="分配权限" v-model="permDialogVisible" width="500px" append-to-body>
      <el-tree
        ref="treeRef"
        :data="menuTree"
        :props="{ label: 'menuName', children: 'children' }"
        show-checkbox
        node-key="menuId"
        default-expand-all
      />
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPermission">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { listRoles, createRole, updateRole, deleteRole } from '@/api/system'

const loading = ref(false)
const list = ref([])
const menuTree = ref([])
const queryParams = reactive({
  pageNum: 1,
  pageSize: 100,
  keyword: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const permDialogVisible = ref(false)
const formRef = ref<FormInstance>()
const treeRef = ref()
const currentRoleId = ref<number>()
const form = reactive({
  roleId: undefined as number | undefined,
  roleName: '',
  roleCode: '',
  status: 1,
  remark: ''
})
const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }]
}

const handleQuery = () => {
  loading.value = true
  listRoles().then((res: any) => {
    list.value = res.data || []
  }).finally(() => {
    loading.value = false
  })
}

const resetForm = () => {
  form.roleId = undefined
  form.roleName = ''
  form.roleCode = ''
  form.status = 1
  form.remark = ''
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增角色'
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  resetForm()
  form.roleId = row.roleId
  form.roleName = row.roleName
  form.roleCode = row.roleCode
  form.status = row.status
  form.remark = row.remark
  dialogTitle.value = '编辑角色'
  dialogVisible.value = true
}

const handlePermission = (row: any) => {
  currentRoleId.value = row.roleId
  permDialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确认要删除角色"${row.roleName}"吗？`, '提示', {
    type: 'warning'
  }).then(() => {
    deleteRole(row.roleId).then(() => {
      ElMessage.success('删除成功')
      handleQuery()
    })
  })
}

const submitForm = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      if (form.roleId) {
        updateRole(form.roleId, {
          roleName: form.roleName,
          dataScope: '1',
          status: form.status
        }).then(() => {
          ElMessage.success('修改成功')
          dialogVisible.value = false
          handleQuery()
        })
      } else {
        createRole({
          roleName: form.roleName,
          roleKey: form.roleCode,
          status: form.status
        }).then(() => {
          ElMessage.success('新增成功')
          dialogVisible.value = false
          handleQuery()
        })
      }
    }
  })
}

const submitPermission = () => {
  ElMessage.success('权限分配成功')
  permDialogVisible.value = false
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
</style>
