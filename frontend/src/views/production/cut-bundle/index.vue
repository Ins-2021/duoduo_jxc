<template>
  <el-card shadow="never">
    <div class="toolbar" style="display: flex; justify-content: space-between; margin-bottom: 20px;">
      <div class="left-actions">
        <el-button type="primary" v-perm="'production:cut-bundle:add'" @click="handleAdd">新建扎包</el-button>
      </div>
      <div class="right-search" style="display: flex; gap: 10px;">
        <el-input v-model="queryParams.keyword" placeholder="扎包号/裁床单号" clearable style="width: 200px" @keyup.enter="handleQuery"/>
        <el-button type="primary" @click="handleQuery">查询</el-button>
      </div>
    </div>

    <el-table :data="tableData" v-loading="loading" border>
      <el-table-column prop="bundleNo" label="扎包号" width="160" />
      <el-table-column prop="cuttingPlanId" label="裁床单ID" width="120" />
      <el-table-column prop="size" label="尺码" width="100" />
      <el-table-column prop="color" label="颜色" width="100" />
      <el-table-column prop="quantity" label="数量" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{row}">
          <el-tag :type="getStatusType(row.status)">{{ getStatusName(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{row}">
          <el-button link type="primary" v-perm="'production:cut-bundle:edit'" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" v-perm="'production:cut-bundle:delete'" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top: 20px; display: flex; justify-content: flex-end;">
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCutBundleList, deleteCutBundle } from '@/api/production'

defineOptions({ name: 'CutBundleList' })

const loading = ref(false)
const total = ref(0)
const tableData = ref<any[]>([])

const queryParams = reactive({
  page: 1,
  size: 10,
  keyword: ''
})

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    pending: 'info',
    assigned: 'primary',
    in_progress: 'warning',
    completed: 'success',
    abnormal: 'danger'
  }
  return map[status] || 'info'
}

const getStatusName = (status: string) => {
  const map: Record<string, string> = {
    pending: '待分配',
    assigned: '已分配',
    in_progress: '进行中',
    completed: '已完成',
    abnormal: '异常'
  }
  return map[status] || status
}

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getCutBundleList(queryParams)
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  ElMessage.info('开发中')
}

const handleEdit = (row: any) => {
  ElMessage.info('开发中')
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确认删除该记录吗？', '提示', { type: 'warning' }).then(async () => {
    try {
      await deleteCutBundle(row.bundleId)
      ElMessage.success('删除成功')
      handleQuery()
    } catch (e) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  handleQuery()
})
</script>
