<template>
  <el-card shadow="never">
    <!-- 工具栏 -->
    <div class="toolbar" style="display: flex; justify-content: space-between; margin-bottom: 20px;">
      <div class="left-actions">
        <el-button type="primary" v-perm="'production:order:add'" @click="handleAdd">新建生产单</el-button>
        <el-button v-perm="'production:order:import'" @click="handleImport">从销售单导入</el-button>
      </div>
      <div class="right-search" style="display: flex; gap: 10px;">
        <el-select v-model="queryParams.status" placeholder="订单状态" clearable style="width: 150px">
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value"/>
        </el-select>
        <el-date-picker v-model="queryParams.dateRange" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期" style="width: 240px"/>
        <el-input v-model="queryParams.keyword" placeholder="生产单号/款号" clearable style="width: 200px" @keyup.enter="handleQuery"/>
        <el-button type="primary" @click="handleQuery">查询</el-button>
      </div>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading" border>
      <el-table-column prop="orderNo" label="生产单号" width="160">
        <template #default="{row}">
          <el-link type="primary" @click="handleDetail(row)">{{ row.orderNo }}</el-link>
        </template>
      </el-table-column>
      <el-table-column prop="styleCode" label="款号" width="120"/>
      <el-table-column prop="styleName" label="品名" width="150"/>
      <el-table-column prop="totalQuantity" label="总数量" width="100"/>
      <el-table-column prop="completedQuantity" label="完成数" width="100"/>
      <el-table-column prop="progress" label="进度" width="180">
        <template #default="{row}">
          <el-progress :percentage="row.progress" :stroke-width="10"/>
        </template>
      </el-table-column>
      <el-table-column prop="factoryName" label="生产工厂" width="150"/>
      <el-table-column prop="planStartDate" label="计划开始" width="120"/>
      <el-table-column prop="planEndDate" label="计划完成" width="120"/>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{row}">
          <el-tag :type="getStatusType(row.status)">{{ getStatusName(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="priority" label="优先级" width="100">
        <template #default="{row}">
          <el-tag :type="getPriorityType(row.priority)" size="small">{{ getPriorityName(row.priority) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{row}">
          <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
          <el-button link type="primary" v-perm="'production:order:edit'" @click="handleEdit(row)" v-if="row.status === 'draft'">编辑</el-button>
          <el-button link type="primary" v-perm="'production:order:issue'" @click="handleIssue(row)" v-if="row.status === 'draft'">下发</el-button>
          <el-button link type="primary" v-perm="'production:order:complete'" @click="handleComplete(row)" v-if="row.status === 'in_progress'">完工</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
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
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProductionOrderList } from '@/api/production'

defineOptions({ name: 'ProductionOrderList' })

const router = useRouter()
const loading = ref(false)
const total = ref(0)
const tableData = ref<any[]>([])

const queryParams = reactive({
  page: 1,
  size: 10,
  status: '',
  keyword: '',
  dateRange: []
})

const statusOptions = [
  { label: '草稿', value: 'draft' },
  { label: '待开工', value: 'pending' },
  { label: '生产中', value: 'in_progress' },
  { label: '已完成', value: 'completed' },
]

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    draft: 'info',
    pending: 'warning',
    in_progress: 'primary',
    completed: 'success'
  }
  return map[status] || 'info'
}

const getStatusName = (status: string) => {
  const map: Record<string, string> = {
    draft: '草稿',
    pending: '待开工',
    in_progress: '生产中',
    completed: '已完成'
  }
  return map[status] || status
}

const getPriorityType = (priority: string) => {
  const map: Record<string, string> = {
    low: 'info',
    medium: 'primary',
    high: 'warning',
    urgent: 'danger'
  }
  return map[priority] || 'info'
}

const getPriorityName = (priority: string) => {
  const map: Record<string, string> = {
    low: '低',
    medium: '中',
    high: '高',
    urgent: '紧急'
  }
  return map[priority] || priority
}

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getProductionOrderList(queryParams)
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
    ElMessage.error('获取生产订单列表失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  router.push('/production/order/add')
}

const handleImport = () => {
  ElMessage.info('从销售单导入功能开发中')
}

const handleDetail = (row: any) => {
  router.push(`/production/order/detail/${row.id}`)
}

const handleEdit = (row: any) => {
  router.push(`/production/order/edit/${row.id}`)
}

const handleIssue = (row: any) => {
  ElMessage.success(`生产单 ${row.orderNo} 已下发`)
  handleQuery()
}

const handleComplete = (row: any) => {
  ElMessage.success(`生产单 ${row.orderNo} 已完工`)
  handleQuery()
}

onMounted(() => {
  handleQuery()
})
</script>
