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
          <el-button type="primary" @click="handleQuery" :loading="loading">查询</el-button>
          <el-button @click="handleRefresh" :loading="loading">
            <el-icon><Refresh /></el-icon>
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="16" style="margin-bottom: 16px;">
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-title">生产中订单</div>
          <div class="stat-value">{{ statistics.inProgressCount }}</div>
          <div class="stat-unit">单</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-title">今日产量</div>
          <div class="stat-value">{{ statistics.todayOutput }}</div>
          <div class="stat-unit">件</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-title">本周产量</div>
          <div class="stat-value">{{ statistics.weekOutput }}</div>
          <div class="stat-unit">件</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-title">本月产量</div>
          <div class="stat-value">{{ statistics.monthOutput }}</div>
          <div class="stat-unit">件</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card warning">
          <div class="stat-title">延期订单</div>
          <div class="stat-value">{{ statistics.delayedCount }}</div>
          <div class="stat-unit">单</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-title">质检合格率</div>
          <div class="stat-value">{{ statistics.qualityPassRate }}%</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 订单看板 -->
    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>生产看板</span>
          <el-tag v-if="!loading" type="info" size="small">
            数据更新时间: {{ lastUpdateTime }}
          </el-tag>
        </div>
      </template>
      
      <div class="kanban-container" v-loading="loading">
        <!-- 待开工列 -->
        <div class="kanban-column pending">
          <div class="column-header">
            <span class="column-title">待开工</span>
            <el-badge :value="pendingOrders.length" type="info"/>
          </div>
          <div class="column-body">
            <div v-for="order in pendingOrders" :key="order.orderId" 
                 class="order-card" @click="handleOrderDetail(order)">
              <div class="order-no">{{ order.orderNo }}</div>
              <div class="order-style">{{ order.styleCode }} - {{ order.styleName }}</div>
              <div class="order-info">
                <span>数量：{{ order.totalQuantity }}</span>
                <el-tag size="small" :type="getPriorityType(order.priority)">
                  {{ getPriorityName(order.priority) }}
                </el-tag>
              </div>
              <div class="order-deadline">
                交期：{{ order.deadline }}
              </div>
            </div>
            <el-empty v-if="pendingOrders.length === 0" description="暂无待开工订单" :image-size="60" />
          </div>
        </div>

        <!-- 生产中列 -->
        <div class="kanban-column in-progress">
          <div class="column-header">
            <span class="column-title">生产中</span>
            <el-badge :value="inProgressOrders.length" type="warning"/>
          </div>
          <div class="column-body">
            <div v-for="order in inProgressOrders" :key="order.orderId" 
                 class="order-card" :class="{ delayed: order.delayDays > 0 }"
                 @click="handleOrderDetail(order)">
              <div class="order-no">
                {{ order.orderNo }}
                <el-tag v-if="order.delayDays > 0" type="danger" size="small">
                  延期{{ order.delayDays }}天
                </el-tag>
              </div>
              <div class="order-style">{{ order.styleCode }} - {{ order.styleName }}</div>
              <el-progress :percentage="order.progress" :stroke-width="6" style="margin-top: 8px;"/>
              <div class="order-progress-text">
                完成：{{ order.completedQuantity }}/{{ order.totalQuantity }}
              </div>
              <div class="order-current">
                当前工序：{{ order.currentProcess }}
              </div>
            </div>
            <el-empty v-if="inProgressOrders.length === 0" description="暂无生产中订单" :image-size="60" />
          </div>
        </div>

        <!-- 已完成列 -->
        <div class="kanban-column completed">
          <div class="column-header">
            <span class="column-title">已完成</span>
            <el-badge :value="completedOrders.length" type="success"/>
          </div>
          <div class="column-body">
            <div v-for="order in completedOrders" :key="order.orderId" 
                 class="order-card" @click="handleOrderDetail(order)">
              <div class="order-no">{{ order.orderNo }}</div>
              <div class="order-style">{{ order.styleCode }} - {{ order.styleName }}</div>
              <div class="order-info">
                <span>数量：{{ order.totalQuantity }}</span>
                <el-tag size="small" type="success">已完成</el-tag>
              </div>
            </div>
            <el-empty v-if="completedOrders.length === 0" description="暂无已完成订单" :image-size="60" />
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProductionStatistics, getProductionOrders } from '@/api/production/dashboard'
import type { ProductionStatistics, ProductionOrderCard } from '@/api/production/dashboard'

defineOptions({ name: 'ProductionDashboard' })

const router = useRouter()
const loading = ref(false)
const lastUpdateTime = ref('')

const queryParams = reactive({
  factoryId: undefined as number | undefined
})

const factoryOptions = ref<{ id: number; name: string }[]>([
  { id: 1, name: '一号工厂' },
  { id: 2, name: '二号工厂' }
])

const statistics = ref<ProductionStatistics>({
  inProgressCount: 0,
  pendingCount: 0,
  todayOutput: 0,
  weekOutput: 0,
  monthOutput: 0,
  delayedCount: 0,
  qualityPassRate: 0
})

const pendingOrders = ref<ProductionOrderCard[]>([])
const inProgressOrders = ref<ProductionOrderCard[]>([])
const completedOrders = ref<ProductionOrderCard[]>([])

let refreshTimer: number | null = null

const loadData = async () => {
  loading.value = true
  try {
    const [statsRes, ordersRes] = await Promise.all([
      getProductionStatistics(queryParams.factoryId),
      getProductionOrders(queryParams.factoryId)
    ])
    
    statistics.value = statsRes
    pendingOrders.value = ordersRes.pending || []
    inProgressOrders.value = ordersRes.inProgress || []
    completedOrders.value = ordersRes.completed || []
    
    lastUpdateTime.value = new Date().toLocaleString()
  } catch (e: any) {
    console.error('加载看板数据失败:', e)
    ElMessage.error(e.message || '加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleQuery = () => loadData()
const handleRefresh = () => loadData()

const handleOrderDetail = (order: ProductionOrderCard) => {
  router.push(`/production/order/detail/${order.orderId}`)
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

onMounted(() => {
  loadData()
  // 每5分钟自动刷新
  refreshTimer = window.setInterval(loadData, 5 * 60 * 1000)
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
})
</script>

<style scoped>
.stat-card {
  text-align: center;
  padding: 16px;
}
.stat-card .stat-title {
  font-size: 14px;
  color: #909399;
}
.stat-card .stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin: 8px 0 4px;
}
.stat-card .stat-unit {
  font-size: 12px;
  color: #c0c4cc;
}
.stat-card.warning .stat-value {
  color: #f56c6c;
}

.kanban-container {
  display: flex;
  gap: 16px;
  min-height: 500px;
}

.kanban-column {
  flex: 1;
  min-width: 300px;
  background: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
}

.column-header {
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.column-title {
  font-weight: bold;
  font-size: 14px;
}

.column-body {
  padding: 12px;
  max-height: 600px;
  overflow-y: auto;
}

.order-card {
  background: #fff;
  padding: 12px;
  border-radius: 6px;
  margin-bottom: 8px;
  cursor: pointer;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  transition: all 0.2s;
}

.order-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.order-card.delayed {
  border-left: 3px solid #f56c6c;
}

.order-no {
  font-weight: bold;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-style {
  color: #909399;
  font-size: 12px;
  margin-top: 4px;
}

.order-info {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 12px;
}

.order-progress-text {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.order-current {
  font-size: 12px;
  color: #606266;
  margin-top: 4px;
}

.order-deadline {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>
