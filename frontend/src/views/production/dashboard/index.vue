<template>
  <div class="app-container">
    <!-- 筛选栏 -->
    <el-card shadow="never" style="margin-bottom: 16px;">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-select v-model="queryParams.factoryId" placeholder="选择工厂" clearable style="width: 100%">
            <el-option v-for="item in factoryOptions" :key="item.id" :label="item.name" :value="item.id"/>
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="queryParams.status" placeholder="订单状态" clearable style="width: 100%">
            <el-option label="待开工" value="pending"/>
            <el-option label="生产中" value="in_progress"/>
            <el-option label="已完成" value="completed"/>
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="handleQuery" :loading="loading">查询</el-button>
          <el-button @click="handleRefresh" :loading="loading">刷新</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="16" style="margin-bottom: 16px;">
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="生产中订单" :value="statistics.inProgressCount">
            <template #suffix>
              <span style="font-size: 14px;">单</span>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="今日产量" :value="statistics.todayOutput">
            <template #suffix>
              <span style="font-size: 14px;">件</span>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="本周产量" :value="statistics.weekOutput">
            <template #suffix>
              <span style="font-size: 14px;">件</span>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="本月产量" :value="statistics.monthOutput">
            <template #suffix>
              <span style="font-size: 14px;">件</span>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <!-- 订单看板 -->
    <el-card shadow="never">
      <template #header>生产看板</template>
      <div style="display: flex; gap: 16px; overflow-x: auto; min-height: 400px;" v-loading="loading">
        <!-- 待开工列 -->
        <div style="flex: 1; min-width: 300px; background: #f5f7fa; border-radius: 4px; padding: 12px;">
          <div style="font-weight: bold; margin-bottom: 12px; display: flex; justify-content: space-between;">
            <span>待开工</span>
            <el-badge :value="pendingOrders.length" type="info"/>
          </div>
          <div v-for="order in pendingOrders" :key="order.id" style="background: white; padding: 12px; border-radius: 4px; margin-bottom: 8px; cursor: pointer; box-shadow: 0 2px 4px rgba(0,0,0,0.05);" @click="handleOrderDetail(order)">
            <div style="font-weight: bold;">{{ order.orderNo }}</div>
            <div style="color: #909399; font-size: 12px; margin-top: 4px;">{{ order.styleCode }} - {{ order.styleName }}</div>
            <div style="display: flex; justify-content: space-between; margin-top: 8px;">
              <span>数量：{{ order.totalQuantity }}</span>
              <el-tag size="small" :type="getPriorityType(order.priority)">{{ getPriorityName(order.priority) }}</el-tag>
            </div>
          </div>
        </div>

        <!-- 生产中列 -->
        <div style="flex: 1; min-width: 300px; background: #fdf6ec; border-radius: 4px; padding: 12px;">
          <div style="font-weight: bold; margin-bottom: 12px; display: flex; justify-content: space-between;">
            <span>生产中</span>
            <el-badge :value="inProgressOrders.length" type="warning"/>
          </div>
          <div v-for="order in inProgressOrders" :key="order.id" style="background: white; padding: 12px; border-radius: 4px; margin-bottom: 8px; cursor: pointer; box-shadow: 0 2px 4px rgba(0,0,0,0.05);" @click="handleOrderDetail(order)">
            <div style="font-weight: bold;">{{ order.orderNo }}</div>
            <div style="color: #909399; font-size: 12px; margin-top: 4px;">{{ order.styleCode }} - {{ order.styleName }}</div>
            <div style="margin-top: 8px;">
              <el-progress :percentage="order.progress" :stroke-width="6"/>
            </div>
            <div style="display: flex; justify-content: space-between; margin-top: 8px; font-size: 12px;">
              <span>完成：{{ order.completedQuantity }}/{{ order.totalQuantity }}</span>
              <span>{{ order.currentProcess }}</span>
            </div>
          </div>
        </div>

        <!-- 已完成列 -->
        <div style="flex: 1; min-width: 300px; background: #f0f9eb; border-radius: 4px; padding: 12px;">
          <div style="font-weight: bold; margin-bottom: 12px; display: flex; justify-content: space-between;">
            <span>已完成</span>
            <el-badge :value="completedOrders.length" type="success"/>
          </div>
          <div v-for="order in completedOrders" :key="order.id" style="background: white; padding: 12px; border-radius: 4px; margin-bottom: 8px; cursor: pointer; box-shadow: 0 2px 4px rgba(0,0,0,0.05);" @click="handleOrderDetail(order)">
            <div style="font-weight: bold;">{{ order.orderNo }}</div>
            <div style="color: #909399; font-size: 12px; margin-top: 4px;">{{ order.styleCode }} - {{ order.styleName }}</div>
            <div style="display: flex; justify-content: space-between; margin-top: 8px;">
              <span>数量：{{ order.totalQuantity }}</span>
              <el-tag size="small" type="success">已完成</el-tag>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'

defineOptions({ name: 'ProductionDashboard' })

const router = useRouter()
const loading = ref(false)

const queryParams = reactive({
  factoryId: '',
  status: ''
})

const factoryOptions = [
  { id: 1, name: '一号工厂' },
  { id: 2, name: '二号工厂' }
]

const statistics = reactive({
  inProgressCount: 0,
  todayOutput: 0,
  weekOutput: 0,
  monthOutput: 0
})

const pendingOrders = ref<any[]>([])
const inProgressOrders = ref<any[]>([])
const completedOrders = ref<any[]>([])

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

const handleQuery = () => {
  loading.value = true
  setTimeout(() => {
    // Mock data
    statistics.inProgressCount = 12
    statistics.todayOutput = 450
    statistics.weekOutput = 2800
    statistics.monthOutput = 12500

    pendingOrders.value = [
      { id: 1, orderNo: 'PO20260329002', styleCode: 'S002', styleName: '夏季连衣裙', totalQuantity: 500, priority: 'medium' },
      { id: 2, orderNo: 'PO20260329003', styleCode: 'S003', styleName: '秋季卫衣', totalQuantity: 800, priority: 'urgent' }
    ]

    inProgressOrders.value = [
      { id: 3, orderNo: 'PO20260328001', styleCode: 'S001', styleName: '春季新款T恤', totalQuantity: 1000, completedQuantity: 650, progress: 65, currentProcess: '缝纫' },
      { id: 4, orderNo: 'PO20260328002', styleCode: 'S004', styleName: '男士夹克', totalQuantity: 300, completedQuantity: 100, progress: 33, currentProcess: '裁剪' }
    ]

    completedOrders.value = [
      { id: 5, orderNo: 'PO20260325001', styleCode: 'S005', styleName: '休闲裤', totalQuantity: 2000 }
    ]

    loading.value = false
  }, 500)
}

const handleRefresh = () => {
  handleQuery()
}

const handleOrderDetail = (order: any) => {
  router.push(`/production/order/detail/${order.id}`)
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}
</style>
