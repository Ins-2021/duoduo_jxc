<template>
  <div>
    <el-card shadow="never">
      <div style="display: flex; gap: 12px; align-items: center; margin-bottom: 12px;">
        <el-input v-model="keyword" placeholder="搜索页面名称/标识" clearable style="max-width: 260px;" @keyup.enter="fetchViews" />
        <el-button type="primary" @click="fetchViews">搜索</el-button>
        <el-button @click="refresh">刷新</el-button>
      </div>

      <el-table :data="views" border style="width: 100%" v-loading="loading">
        <el-table-column prop="viewName" label="表单名称" min-width="220" />
        <el-table-column prop="viewKey" label="表单标识" min-width="220" />
        <el-table-column prop="scene" label="场景" width="120" align="center" />
        <el-table-column prop="version" label="版本" width="100" align="center" />
        <el-table-column label="操作" width="220" align="center">
          <template #default="scope">
            <el-button type="primary" link v-perm="'settings:field-settings:edit'" @click="openEdit(scope.row)">编辑</el-button>
            <el-button type="danger" link v-perm="'settings:field-settings:edit'" @click="handleReset(scope.row)">恢复默认</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="editVisible" :title="editTitle" width="680px" custom-class="column-setting-dialog">
      <div class="setting-tips">
        <p>按住上下拖拽可以改变字段顺序。</p>
      </div>

      <el-table :data="editColumns" row-key="prop" border size="small" ref="settingTableRef">
        <el-table-column label="排序" width="60" align="center">
          <template #default>
            <el-icon class="drag-handle" style="cursor: move; font-size: 16px;"><Sort /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="originalLabel" label="字段名称" width="180" />
        <el-table-column label="字段配置">
          <template #default="scope">
            <el-input v-model="scope.row.label" size="small" :disabled="isLocked(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column label="显示" width="80" align="center">
          <template #default="scope">
            <el-checkbox v-model="scope.row.visible" :disabled="isLocked(scope.row)" />
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editVisible = false">取消</el-button>
          <el-button type="primary" @click="saveEdit">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted, computed } from 'vue'
import Sortable from 'sortablejs'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getViewConfig, listViews, resetViewConfig, saveViewConfig } from '@/api/ui'

type ColumnConfig = {
  prop: string
  originalLabel: string
  label: string
  visible: boolean
  type?: string
  width?: number
  minWidth?: number
  fixed?: any
}

const keyword = ref('')
const loading = ref(false)
const views = ref<any[]>([])

const fetchViews = async () => {
  loading.value = true
  try {
    const res = await listViews({ keyword: keyword.value || undefined })
    views.value = res.data || []
  } finally {
    loading.value = false
  }
}

const refresh = async () => {
  keyword.value = ''
  await fetchViews()
}

const editVisible = ref(false)
const editViewKey = ref('')
const editViewName = ref('')
const editColumns = ref<ColumnConfig[]>([])
const settingTableRef = ref<any>(null)
let sortableInstance: Sortable | null = null

const editTitle = computed(() => (editViewName.value ? `字段设置 - ${editViewName.value}` : '字段设置'))

const isLocked = (row: any) => {
  if (!row?.originalLabel) return true
  if (row?.type === 'selection') return true
  if (row?.prop === 'actions') return true
  return false
}

const openEdit = async (row: any) => {
  editViewKey.value = row.viewKey
  editViewName.value = row.viewName
  const res = await getViewConfig(editViewKey.value)
  editColumns.value = JSON.parse(JSON.stringify(res.data.columns || []))
  editVisible.value = true

  nextTick(() => {
    const tbody = settingTableRef.value.$el.querySelector('.el-table__body-wrapper tbody')
    if (sortableInstance) sortableInstance.destroy()
    sortableInstance = Sortable.create(tbody, {
      handle: '.drag-handle',
      animation: 150,
      onEnd: ({ newIndex, oldIndex }) => {
        if (newIndex === undefined || oldIndex === undefined) return
        const targetRow = editColumns.value.splice(oldIndex, 1)[0]
        editColumns.value.splice(newIndex, 0, targetRow)
      }
    })
  })
}

const saveEdit = async () => {
  await saveViewConfig(editViewKey.value, editColumns.value)
  ElMessage.success('保存成功')
  editVisible.value = false
  await fetchViews()
}

const handleReset = async (row: any) => {
  await ElMessageBox.confirm(`确认恢复【${row.viewName}】为默认配置吗?`, '提示', { type: 'warning' })
  await resetViewConfig(row.viewKey)
  ElMessage.success('已恢复默认')
}

onMounted(() => {
  fetchViews()
})
</script>

<style scoped>
.setting-tips {
  margin-bottom: 10px;
  color: #909399;
  font-size: 12px;
}
</style>

