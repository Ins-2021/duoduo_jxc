<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <div class="left">
          <el-button v-perm="'system:role:add'" type="primary" @click="openCreate">新增角色</el-button>
          <el-button @click="load">刷新</el-button>
        </div>
      </div>

      <el-table :data="rows" stripe>
        <el-table-column prop="roleId" label="ID" width="90" />
        <el-table-column prop="roleKey" label="标识" width="160" />
        <el-table-column prop="roleName" label="名称" />
        <el-table-column prop="dataScope" label="数据范围">
          <template #default="{ row }">
            <span v-if="row.dataScope === '1'">全部</span>
            <span v-else-if="row.dataScope === '2'">自定义</span>
            <span v-else-if="row.dataScope === '3'">本部门</span>
            <span v-else-if="row.dataScope === '4'">本部门及以下</span>
            <span v-else-if="row.dataScope === '5'">仅本人</span>
            <span v-else>全部</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button v-perm="'system:role:edit'" link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button v-perm="'system:role:grant'" link @click="openGrant(row)">授权菜单</el-button>
            <el-button v-perm="'system:role:delete'" link type="danger" @click="doDelete(row.roleId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增角色 -->
    <el-dialog v-model="createVisible" title="新增角色" width="520px">
      <el-form :model="createForm" label-width="90px">
        <el-form-item label="标识">
          <el-input v-model="createForm.roleKey" />
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model="createForm.roleName" />
        </el-form-item>
        <el-form-item label="数据范围">
          <el-select v-model="createForm.dataScope" style="width: 240px">
            <el-option label="全部数据权限" value="1" />
            <el-option label="自定数据权限" value="2" />
            <el-option label="本部门数据权限" value="3" />
            <el-option label="本部门及以下数据权限" value="4" />
            <el-option label="仅本人数据权限" value="5" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="doCreate">确定</el-button>
      </template>
    </el-dialog>

    <!-- 编辑角色 -->
    <el-dialog v-model="editVisible" title="编辑角色" width="520px">
      <el-form :model="editForm" label-width="90px">
        <el-form-item label="名称">
          <el-input v-model="editForm.roleName" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status" style="width: 240px">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="数据范围">
          <el-select v-model="editForm.dataScope" style="width: 240px">
            <el-option label="全部数据权限" value="1" />
            <el-option label="自定数据权限" value="2" />
            <el-option label="本部门数据权限" value="3" />
            <el-option label="本部门及以下数据权限" value="4" />
            <el-option label="仅本人数据权限" value="5" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="doEdit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 授权菜单 - 若依风格树形勾选 -->
    <el-dialog v-model="grantVisible" title="分配菜单权限" width="680px" :close-on-click-modal="false">
      <div class="grant-header">
        <span class="grant-role-name">{{ grantRoleName }}</span>
        <div class="grant-actions">
          <el-button size="small" @click="expandAllMenus">展开/折叠</el-button>
          <el-button size="small" @click="toggleCheckAll">全选/全不选</el-button>
          <el-button size="small" @click="checkHalfToAll">勾选父节点下的全部子节点</el-button>
        </div>
      </div>

      <div class="grant-search">
        <el-input
          v-model="grantKeyword"
          placeholder="输入菜单名称过滤"
          clearable
          prefix-icon="Search"
          style="width: 300px"
        />
      </div>

      <div class="grant-tree-wrapper">
        <el-tree
          ref="menuTreeRef"
          :data="grantMenuTree"
          :props="{ label: 'menuName', children: 'children' }"
          :default-expand-all="true"
          show-checkbox
          node-key="menuId"
          :filter-node-method="filterMenuNode"
          :check-strictly="false"
          class="grant-tree"
        >
          <template #default="{ node, data }">
            <span class="tree-node-label">
              <el-icon v-if="data.icon" style="margin-right: 4px; vertical-align: middle;">
                <component :is="data.icon" />
              </el-icon>
              <span class="tree-node-name">{{ data.menuName }}</span>
              <el-tag
                v-if="data.menuType"
                :type="data.menuType === 'M' ? 'info' : data.menuType === 'C' ? 'success' : 'warning'"
                size="small"
                style="margin-left: 6px;"
              >
                {{ data.menuType === 'M' ? '目录' : data.menuType === 'C' ? '菜单' : '按钮' }}
              </el-tag>
            </span>
          </template>
        </el-tree>
      </div>

      <div class="grant-footer-info">
        <span>已选 <strong>{{ checkedCount }}</strong> 项</span>
      </div>

      <template #footer>
        <el-button @click="grantVisible = false">取消</el-button>
        <el-button type="primary" :loading="grantSaving" @click="doGrant">确定保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { ElTree } from 'element-plus'
import { assignRoleMenus, createRole, deleteRole, getRoleMenuIds, getMenuTree, listRoles, updateRole } from '@/api/system'

interface MenuNode {
  menuId: number
  parentId: number
  menuName: string
  menuType: string
  icon?: string
  perms?: string
  children?: MenuNode[]
}

// ==================== 角色列表 ====================
const rows = ref<any[]>([])
const createVisible = ref(false)
const editVisible = ref(false)
const grantVisible = ref(false)
const currentRoleId = ref<number>(0)

const createForm = reactive<any>({
  roleKey: '',
  roleName: '',
  dataScope: '1'
})

const editForm = reactive<any>({
  roleName: '',
  status: 1,
  dataScope: '1'
})

const load = async () => {
  const res: any = await listRoles()
  rows.value = res.data || []
}

const openCreate = () => {
  createForm.roleKey = ''
  createForm.roleName = ''
  createForm.dataScope = '1'
  createVisible.value = true
}

const doCreate = async () => {
  await createRole({ roleKey: createForm.roleKey, roleName: createForm.roleName, dataScope: createForm.dataScope })
  createVisible.value = false
  ElMessage.success('创建成功')
  await load()
}

const openEdit = (row: any) => {
  currentRoleId.value = row.roleId
  editForm.roleName = row.roleName
  editForm.status = row.status
  editForm.dataScope = row.dataScope || '1'
  editVisible.value = true
}

const doEdit = async () => {
  await updateRole(currentRoleId.value, { roleName: editForm.roleName, status: editForm.status, dataScope: editForm.dataScope })
  editVisible.value = false
  ElMessage.success('保存成功')
  await load()
}

const doDelete = async (roleId: number) => {
  await ElMessageBox.confirm('确认删除该角色？', '提示', { type: 'warning' })
  await deleteRole(roleId)
  ElMessage.success('已删除')
  await load()
}

// ==================== 授权菜单 ====================
const menuTreeRef = ref<InstanceType<typeof ElTree>>()
const grantMenuTree = ref<MenuNode[]>([])
const grantRoleName = ref('')
const grantKeyword = ref('')
const grantSaving = ref(false)
const checkedCount = ref(0)

// 搜索过滤
watch(grantKeyword, (val) => {
  menuTreeRef.value?.filter(val)
})

const filterMenuNode = (value: string, data: MenuNode) => {
  if (!value) return true
  return data.menuName.includes(value)
}

// 展开全部/折叠全部
const expandAllMenus = () => {
  const tree = menuTreeRef.value
  if (!tree) return
  const nodes = tree.store._getAllNodes()
  const allExpanded = nodes.every((n: any) => n.expanded)
  nodes.forEach((n: any) => { n.expanded = !allExpanded })
}

// 全选 / 全不选
const toggleCheckAll = () => {
  const tree = menuTreeRef.value
  if (!tree) return
  const allKeys = flattenMenuIds(grantMenuTree.value)
  const checkedKeys = tree.getCheckedKeys(false) as number[]
  if (checkedKeys.length >= allKeys.length) {
    tree.setCheckedKeys([])
  } else {
    tree.setCheckedKeys(allKeys)
  }
}

// 勾选父节点时全选所有子节点
const checkHalfToAll = () => {
  const tree = menuTreeRef.value
  if (!tree) return
  const halfChecked = tree.getHalfCheckedKeys() as number[]
  const checked = tree.getCheckedKeys(false) as number[]
  tree.setCheckedKeys([...checked, ...halfChecked])
}

// 收集所有菜单 ID（叶子节点）
const flattenMenuIds = (nodes: MenuNode[]): number[] => {
  const ids: number[] = []
  const walk = (list: MenuNode[]) => {
    for (const n of list) {
      if (!n.children || n.children.length === 0) {
        ids.push(n.menuId)
      } else {
        walk(n.children)
      }
    }
  }
  walk(nodes)
  return ids
}

// 收集所有菜单 ID（含非叶子节点，用于 setCheckedKeys）
const flattenAllMenuIds = (nodes: MenuNode[]): number[] => {
  const ids: number[] = []
  const walk = (list: MenuNode[]) => {
    for (const n of list) {
      ids.push(n.menuId)
      if (n.children?.length) walk(n.children)
    }
  }
  walk(nodes)
  return ids
}

const openGrant = async (row: any) => {
  currentRoleId.value = row.roleId
  grantRoleName.value = row.roleName || row.roleKey
  grantKeyword.value = ''
  grantVisible.value = true

  // 加载菜单树
  const treeRes: any = await getMenuTree()
  grantMenuTree.value = treeRes.data || []

  // 加载已授权菜单
  const menuRes: any = await getRoleMenuIds(row.roleId)
  const checkedMenuIds: number[] = menuRes.data || []

  await nextTick()
  if (menuTreeRef.value && checkedMenuIds.length > 0) {
    menuTreeRef.value.setCheckedKeys(checkedMenuIds)
  }
}

// 监听勾选数
watch(grantVisible, (val) => {
  if (val) {
    const updateCount = () => {
      const tree = menuTreeRef.value
      if (tree) {
        checkedCount.value = tree.getCheckedKeys(false).length
      }
    }
    // 每次勾选变化后更新计数
    const timer = setInterval(updateCount, 300)
    const stop = () => clearInterval(timer)
    // 当对话框关闭时停止
    watch(grantVisible, (v) => { if (!v) stop() })
  }
})

const doGrant = async () => {
  const tree = menuTreeRef.value
  if (!tree) return
  grantSaving.value = true
  try {
    // 获取所有勾选 + 半勾选（父节点）的 ID
    const checkedKeys = tree.getCheckedKeys(false) as number[]
    const halfCheckedKeys = tree.getHalfCheckedKeys() as number[]
    const allKeys = [...checkedKeys, ...halfCheckedKeys]
    await assignRoleMenus(currentRoleId.value, { menuIds: allKeys })
    grantVisible.value = false
    ElMessage.success('授权成功')
  } finally {
    grantSaving.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.grant-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.grant-role-name {
  font-size: 16px;
  font-weight: 600;
}

.grant-actions {
  display: flex;
  gap: 8px;
}

.grant-search {
  margin-bottom: 12px;
}

.grant-tree-wrapper {
  max-height: 450px;
  overflow-y: auto;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 8px;
}

.grant-tree {
  background: transparent;
}

.tree-node-label {
  display: inline-flex;
  align-items: center;
  font-size: 14px;
}

.tree-node-name {
  vertical-align: middle;
}

.grant-footer-info {
  margin-top: 8px;
  font-size: 13px;
  color: #909399;
  text-align: right;
}
</style>
