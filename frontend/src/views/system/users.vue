<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <div class="left">
          <el-button v-perm="'system:user:add'" type="primary" @click="openCreate">新增用户</el-button>
          <el-button @click="load">刷新</el-button>
        </div>
      </div>

      <el-table :data="rows" stripe>
        <el-table-column prop="userId" label="ID" width="90" />
        <el-table-column prop="username" label="账号" width="160" />
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button v-perm="'system:user:edit'" link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button v-perm="'system:user:resetpwd'" link @click="openReset(row)">重置密码</el-button>
            <el-button v-perm="'system:user:grant'" link @click="openGrant(row)">分配角色</el-button>
            <el-button v-perm="'system:user:delete'" link type="danger" @click="doDelete(row.userId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="createVisible" title="新增用户" width="520px">
      <el-form :model="createForm" label-width="90px">
        <el-form-item label="账号">
          <el-input v-model="createForm.username" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="createForm.realName" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="createForm.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="部门">
          <el-tree-select
            v-model="createForm.deptId"
            :data="deptOptions"
            :props="{ value: 'deptId', label: 'deptName', children: 'children' }"
            value-key="deptId"
            placeholder="请选择归属部门"
            check-strictly
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="doCreate">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="editVisible" title="编辑用户" width="520px">
      <el-form :model="editForm" label-width="90px">
        <el-form-item label="姓名">
          <el-input v-model="editForm.realName" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status" style="width: 240px">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门">
          <el-tree-select
            v-model="editForm.deptId"
            :data="deptOptions"
            :props="{ value: 'deptId', label: 'deptName', children: 'children' }"
            value-key="deptId"
            placeholder="请选择归属部门"
            check-strictly
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="doEdit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="resetVisible" title="重置密码" width="520px">
      <el-form label-width="90px">
        <el-form-item label="新密码">
          <el-input v-model="resetPasswordText" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetVisible = false">取消</el-button>
        <el-button type="primary" @click="doReset">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="grantVisible" title="分配角色" width="520px">
      <el-form label-width="90px">
        <el-form-item label="角色">
          <el-select v-model="selectedRoleIds" multiple style="width: 360px">
            <el-option v-for="r in roleOptions" :key="r.roleId" :label="r.roleName" :value="r.roleId" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="grantVisible = false">取消</el-button>
        <el-button type="primary" @click="doGrant">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  assignUserRoles,
  createUser,
  deleteUser,
  listRoles,
  listUsers,
  listDepts,
  resetPassword,
  updateUser
} from '@/api/system'

const rows = ref<any[]>([])
const roleOptions = ref<any[]>([])
const deptOptions = ref<any[]>([])

const createVisible = ref(false)
const editVisible = ref(false)
const resetVisible = ref(false)
const grantVisible = ref(false)

const currentUserId = ref<number>(0)

const createForm = reactive<any>({
  username: '',
  realName: '',
  password: '',
  deptId: undefined
})

const editForm = reactive<any>({
  realName: '',
  status: 1,
  deptId: undefined
})

const resetPasswordText = ref('')
const selectedRoleIds = ref<number[]>([])

const load = async () => {
  const res: any = await listUsers()
  rows.value = res.data || []
}

const loadRoles = async () => {
  const res: any = await listRoles()
  roleOptions.value = res.data || []
}

const loadDepts = async () => {
  const res: any = await listDepts()
  deptOptions.value = res.data || []
}

const openCreate = async () => {
  await loadDepts()
  createForm.username = ''
  createForm.realName = ''
  createForm.password = ''
  createForm.deptId = undefined
  createVisible.value = true
}

const doCreate = async () => {
  await createUser({
    username: createForm.username,
    realName: createForm.realName,
    password: createForm.password,
    deptId: createForm.deptId
  })
  createVisible.value = false
  ElMessage.success('创建成功')
  await load()
}

const openEdit = async (row: any) => {
  await loadDepts()
  currentUserId.value = row.userId
  editForm.realName = row.realName
  editForm.status = row.status
  editForm.deptId = row.deptId
  editVisible.value = true
}

const doEdit = async () => {
  await updateUser(currentUserId.value, { realName: editForm.realName, status: editForm.status, deptId: editForm.deptId })
  editVisible.value = false
  ElMessage.success('保存成功')
  await load()
}

const openReset = (row: any) => {
  currentUserId.value = row.userId
  resetPasswordText.value = ''
  resetVisible.value = true
}

const doReset = async () => {
  await resetPassword(currentUserId.value, { newPassword: resetPasswordText.value })
  resetVisible.value = false
  ElMessage.success('已重置')
}

const openGrant = async (row: any) => {
  currentUserId.value = row.userId
  selectedRoleIds.value = []
  await loadRoles()
  grantVisible.value = true
}

const doGrant = async () => {
  await assignUserRoles(currentUserId.value, { roleIds: selectedRoleIds.value })
  grantVisible.value = false
  ElMessage.success('已分配')
}

const doDelete = async (userId: number) => {
  await ElMessageBox.confirm('确认删除该用户？', '提示', { type: 'warning' })
  await deleteUser(userId)
  ElMessage.success('已删除')
  await load()
}

onMounted(async () => {
  await load()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
</style>
