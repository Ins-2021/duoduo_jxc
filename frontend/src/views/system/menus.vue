<template>
  <div class="page">
    <el-card>
      <el-form :inline="true" class="toolbar">
        <el-form-item label="菜单名称">
          <el-input v-model="query.menuName" placeholder="请输入菜单名称" clearable style="width: 220px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部" style="width: 140px">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="显示">
          <el-select v-model="query.visible" clearable placeholder="全部" style="width: 140px">
            <el-option label="显示" :value="1" />
            <el-option label="隐藏" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="applyQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
        <el-form-item class="toolbar-right">
          <el-button v-perm="'system:menu:add'" type="primary" @click="openCreateRoot">新增</el-button>
          <el-button @click="toggleExpand">{{ expandAll ? '折叠' : '展开' }}</el-button>
          <el-button @click="load">刷新</el-button>
        </el-form-item>
      </el-form>

      <el-table
        ref="tableRef"
        :data="displayTree"
        stripe
        row-key="menuId"
        :expand-row-keys="expandedRowKeys"
        :tree-props="{ children: 'children' }"
      >
        <el-table-column prop="menuName" label="菜单名称" min-width="220" />
        <el-table-column prop="icon" label="图标" width="100">
          <template #default="{ row }">
            <el-icon v-if="row.icon" style="vertical-align: middle; margin-right: 4px;">
              <component :is="row.icon" />
            </el-icon>
            <span v-if="row.icon" style="font-size: 12px; color: #909399;">{{ row.icon }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderNum" label="排序" width="90" />
        <el-table-column label="类型" width="110">
          <template #default="{ row }">
            <el-tag v-if="row.menuType === 'M'" type="info">目录</el-tag>
            <el-tag v-else-if="row.menuType === 'C'" type="success">菜单</el-tag>
            <el-tag v-else type="warning">按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="perms" label="权限标识" min-width="220" />
        <el-table-column prop="path" label="路由地址" min-width="200" />
        <el-table-column prop="component" label="组件路径" min-width="220" />
        <el-table-column label="显示" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.visible === 1" type="success">显示</el-tag>
            <el-tag v-else type="info">隐藏</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">启用</el-tag>
            <el-tag v-else type="danger">停用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button v-perm="'system:menu:add'" link type="primary" @click="openCreateChild(row)">新增</el-button>
            <el-button v-perm="'system:menu:edit'" link @click="openEdit(row)">编辑</el-button>
            <el-button v-perm="'system:menu:delete'" link type="danger" @click="doDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="720px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="上级菜单">
          <el-select v-model="form.parentId" filterable style="width: 420px">
            <el-option v-for="o in parentOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="菜单类型">
          <el-radio-group v-model="form.menuType">
            <el-radio value="M">目录</el-radio>
            <el-radio value="C">菜单</el-radio>
            <el-radio value="F">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称">
          <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.orderNum" :min="0" :step="1" />
        </el-form-item>
        <el-form-item v-if="form.menuType !== 'F'" label="路由地址">
          <el-input v-model="form.path" placeholder="例如 /system/menus" />
        </el-form-item>
        <el-form-item v-if="form.menuType !== 'F'" label="组件路径">
          <el-input v-model="form.component" placeholder="例如 system/menus" />
        </el-form-item>
        <el-form-item v-if="form.menuType !== 'F'" label="路由名称">
          <el-input v-model="form.routeName" placeholder="例如 SystemMenus" />
        </el-form-item>
        <el-form-item v-if="form.menuType !== 'F'" label="图标">
          <el-popover placement="bottom-start" :width="460" trigger="click">
            <template #reference>
              <el-input v-model="form.icon" placeholder="点击选择图标" readonly style="width: 300px; cursor: pointer;">
                <template #prefix>
                  <el-icon v-if="form.icon"><component :is="form.icon" /></el-icon>
                </template>
              </el-input>
            </template>
            <div class="icon-picker">
              <el-input v-model="iconSearch" placeholder="搜索图标..." clearable size="small" style="margin-bottom: 8px;" />
              <el-scrollbar max-height="300px">
                <div class="icon-grid">
                  <div
                    v-for="icon in filteredIcons"
                    :key="icon"
                    class="icon-item"
                    :class="{ active: form.icon === icon }"
                    @click="form.icon = icon"
                  >
                    <el-icon :size="20"><component :is="icon" /></el-icon>
                    <span class="icon-name">{{ icon }}</span>
                  </div>
                </div>
              </el-scrollbar>
            </div>
          </el-popover>
        </el-form-item>
        <el-form-item label="权限标识">
          <el-input v-model="form.perms" placeholder="例如 system:menu:view" />
        </el-form-item>
        <el-form-item label="是否显示">
          <el-radio-group v-model="form.visible">
            <el-radio :value="1">显示</el-radio>
            <el-radio :value="0">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as Icons from '@element-plus/icons-vue'
import { createMenu, deleteMenu, getMenuTree, updateMenu } from '@/api/system'

type MenuNode = {
  menuId: number
  parentId: number
  menuName: string
  orderNum?: number
  path?: string
  component?: string
  routeName?: string
  icon?: string
  menuType: 'M' | 'C' | 'F'
  visible?: number
  status?: number
  perms?: string
  children?: MenuNode[]
}

const rawTree = ref<MenuNode[]>([])
const displayTree = ref<MenuNode[]>([])
const expandAll = ref(true)
const expandedRowKeys = ref<number[]>([])
const tableRef = ref()

const query = reactive<{ menuName: string; status: number | null; visible: number | null }>({
  menuName: '',
  status: null,
  visible: null
})

const dialogVisible = ref(false)
const dialogMode = ref<'create' | 'edit'>('create')
const editingMenuId = ref<number>(0)

const form = reactive<any>({
  parentId: 0,
  menuType: 'C',
  menuName: '',
  orderNum: 0,
  path: '',
  component: '',
  routeName: '',
  icon: '',
  perms: '',
  visible: 1,
  status: 1
})

const dialogTitle = computed(() => (dialogMode.value === 'create' ? '新增菜单' : '编辑菜单'))

const iconSearch = ref('')
const allIcons = Object.keys(Icons).filter(name => /^[A-Z]/.test(name))
const filteredIcons = computed(() => {
  const keyword = iconSearch.value.trim().toLowerCase()
  if (!keyword) return allIcons
  return allIcons.filter(name => name.toLowerCase().includes(keyword))
})

const flattenIds = (nodes: MenuNode[]) => {
  const ids: number[] = []
  const walk = (list: MenuNode[]) => {
    for (const n of list) {
      ids.push(n.menuId)
      if (n.children && n.children.length) {
        walk(n.children)
      }
    }
  }
  walk(nodes)
  return ids
}

const filterTree = (nodes: MenuNode[]): MenuNode[] => {
  const name = query.menuName.trim().toLowerCase()
  const status = query.status
  const visible = query.visible

  const match = (n: MenuNode) => {
    const okName = !name || (n.menuName || '').toLowerCase().includes(name)
    const okStatus = status == null || n.status === status
    const okVisible = visible == null || n.visible === visible
    return okName && okStatus && okVisible
  }

  const walk = (list: MenuNode[]): MenuNode[] => {
    const res: MenuNode[] = []
    for (const n of list) {
      const children = n.children ? walk(n.children) : []
      if (match(n) || children.length > 0) {
        res.push({ ...n, children })
      }
    }
    return res
  }
  return walk(nodes)
}

const load = async () => {
  const res: any = await getMenuTree()
  rawTree.value = res.data || []
  displayTree.value = filterTree(rawTree.value)
  if (expandAll.value) {
    expandedRowKeys.value = flattenIds(displayTree.value)
  }
}

onMounted(load)

const applyQuery = () => {
  displayTree.value = filterTree(rawTree.value)
  if (expandAll.value) {
    expandedRowKeys.value = flattenIds(displayTree.value)
  }
}

const resetQuery = () => {
  query.menuName = ''
  query.status = null
  query.visible = null
  applyQuery()
}

const toggleExpand = () => {
  expandAll.value = !expandAll.value
  expandedRowKeys.value = expandAll.value ? flattenIds(displayTree.value) : []
}

const parentOptions = computed(() => {
  const list: Array<{ value: number; label: string }> = [{ value: 0, label: '主目录' }]
  const walk = (nodes: MenuNode[], level: number) => {
    for (const n of nodes) {
      const prefix = level <= 0 ? '' : new Array(level).fill('—').join('')
      list.push({ value: n.menuId, label: `${prefix}${n.menuName}` })
      if (n.children && n.children.length) {
        walk(n.children, level + 1)
      }
    }
  }
  walk(rawTree.value, 0)
  return list.filter(o => o.value !== editingMenuId.value)
})

const resetForm = () => {
  form.parentId = 0
  form.menuType = 'C'
  form.menuName = ''
  form.orderNum = 0
  form.path = ''
  form.component = ''
  form.routeName = ''
  form.icon = ''
  form.perms = ''
  form.visible = 1
  form.status = 1
}

const openCreateRoot = () => {
  dialogMode.value = 'create'
  editingMenuId.value = 0
  resetForm()
  form.parentId = 0
  dialogVisible.value = true
}

const openCreateChild = (row: MenuNode) => {
  dialogMode.value = 'create'
  editingMenuId.value = 0
  resetForm()
  form.parentId = row.menuId
  form.menuType = 'C'
  dialogVisible.value = true
}

const openEdit = (row: MenuNode) => {
  dialogMode.value = 'edit'
  editingMenuId.value = row.menuId
  form.parentId = row.parentId ?? 0
  form.menuType = row.menuType || 'C'
  form.menuName = row.menuName || ''
  form.orderNum = row.orderNum ?? 0
  form.path = row.path || ''
  form.component = row.component || ''
  form.routeName = row.routeName || ''
  form.icon = row.icon || ''
  form.perms = row.perms || ''
  form.visible = row.visible ?? 1
  form.status = row.status ?? 1
  dialogVisible.value = true
}

const normalizeSubmitPayload = () => {
  const payload: any = {
    parentId: form.parentId,
    menuType: form.menuType,
    menuName: form.menuName,
    orderNum: form.orderNum,
    perms: form.perms,
    visible: form.visible,
    status: form.status
  }
  if (form.menuType !== 'F') {
    payload.path = form.path
    payload.component = form.component
    payload.routeName = form.routeName
    payload.icon = form.icon
  } else {
    payload.path = null
    payload.component = null
    payload.routeName = null
    payload.icon = null
  }
  return payload
}

const submit = async () => {
  const payload = normalizeSubmitPayload()
  if (!payload.menuName || !String(payload.menuName).trim()) {
    ElMessage.error('请输入菜单名称')
    return
  }
  if (!payload.menuType) {
    ElMessage.error('请选择菜单类型')
    return
  }
  if (payload.parentId == null) {
    ElMessage.error('请选择上级菜单')
    return
  }

  if (dialogMode.value === 'create') {
    await createMenu(payload)
    dialogVisible.value = false
    ElMessage.success('创建成功')
    await load()
    return
  }

  await updateMenu(editingMenuId.value, payload)
  dialogVisible.value = false
  ElMessage.success('保存成功')
  await load()
}

const doDelete = async (row: MenuNode) => {
  await ElMessageBox.confirm(`确认删除菜单「${row.menuName}」？`, '提示', { type: 'warning' })
  await deleteMenu(row.menuId)
  ElMessage.success('已删除')
  await load()
}
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 12px;
}

.toolbar-right {
  margin-left: auto;
}

.icon-picker {
  padding: 4px;
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 4px;
}

.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 6px 4px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.icon-item:hover {
  background-color: #f0f7ff;
}

.icon-item.active {
  background-color: #ecf5ff;
  border: 1px solid #409eff;
}

.icon-name {
  font-size: 10px;
  color: #909399;
  text-align: center;
  word-break: break-all;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
