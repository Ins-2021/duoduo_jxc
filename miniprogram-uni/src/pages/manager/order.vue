<template>
  <view class="container">
    <view class="search-bar">
      <view class="search-input">
        <text class="search-icon">🔍</text>
        <input v-model="keyword" placeholder="搜索订单号/款号/客户" @confirm="handleSearch" />
      </view>
    </view>

    <view class="filter-bar">
      <picker :range="statusOptions" range-key="label" @change="onStatusChange">
        <view class="filter-btn">
          <text>{{ currentStatusLabel }}</text>
        </view>
      </picker>
      <picker mode="date" @change="onStartDateChange">
        <view class="filter-btn">
          <text>{{ startDate || '开始日期' }}</text>
        </view>
      </picker>
      <picker mode="date" @change="onEndDateChange">
        <view class="filter-btn">
          <text>{{ endDate || '结束日期' }}</text>
        </view>
      </picker>
    </view>

    <view v-if="filteredOrders.length" class="order-list">
      <view v-for="order in filteredOrders" :key="order.id" class="order-card">
        <view class="order-header">
          <view class="order-left">
            <text class="order-no">{{ order.orderNo }}</text>
            <text class="customer">{{ order.customer }}</text>
          </view>
          <text class="order-status" :class="'status-' + order.status">{{ order.statusText }}</text>
        </view>
        <view class="order-info">
          <view class="info-row">
            <text class="info-label">款号</text>
            <text class="info-value">{{ order.styleNo }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">数量</text>
            <text class="info-value">{{ order.quantity }}件</text>
          </view>
          <view class="info-row">
            <text class="info-label">交期</text>
            <text class="info-value" :class="{ warning: order.isUrgent }">{{ order.deadline }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">完成进度</text>
            <text class="info-value">{{ order.completed }}/{{ order.quantity }}件</text>
          </view>
        </view>
        <view class="progress-bar-wrap">
          <view class="progress-bar" :style="{ width: order.percent + '%' }" :class="{ done: order.percent === 100 }" />
        </view>
      </view>
    </view>
    <view v-else class="empty-state">
      <text class="empty-text">暂无订单数据</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getOrders } from '@/api/manager'

const keyword = ref('')
const currentStatus = ref('')
const startDate = ref('')
const endDate = ref('')

const statusOptions = [
  { label: '全部状态', value: '' },
  { label: '待生产', value: 'pending' },
  { label: '生产中', value: 'in_progress' },
  { label: '已完成', value: 'completed' },
  { label: '已延期', value: 'delayed' }
]

const currentStatusLabel = computed(() => statusOptions.find(s => s.value === currentStatus.value)?.label || '全部状态')

const orderData = ref([
  { id: 1, orderNo: 'PO2026032901', customer: '优衣库', styleNo: 'A001', quantity: 200, completed: 120, percent: 60, deadline: '2026-04-15', status: 'in_progress', statusText: '生产中', isUrgent: false },
  { id: 2, orderNo: 'PO2026032902', customer: 'ZARA', styleNo: 'B002', quantity: 150, completed: 80, percent: 53, deadline: '2026-04-10', status: 'in_progress', statusText: '生产中', isUrgent: true },
  { id: 3, orderNo: 'PO2026033001', customer: 'H&M', styleNo: 'D004', quantity: 120, completed: 0, percent: 0, deadline: '2026-04-20', status: 'pending', statusText: '待生产', isUrgent: false },
  { id: 4, orderNo: 'PO2026032703', customer: '优衣库', styleNo: 'C003', quantity: 100, completed: 100, percent: 100, deadline: '2026-04-01', status: 'completed', statusText: '已完成', isUrgent: false },
  { id: 5, orderNo: 'PO2026032501', customer: 'Nike', styleNo: 'E005', quantity: 300, completed: 100, percent: 33, deadline: '2026-03-28', status: 'delayed', statusText: '已延期', isUrgent: true }
])

const filteredOrders = computed(() => {
  let result = orderData.value
  if (keyword.value) {
    const kw = keyword.value.toLowerCase()
    result = result.filter(o => o.orderNo.toLowerCase().includes(kw) || o.styleNo.toLowerCase().includes(kw) || o.customer.toLowerCase().includes(kw))
  }
  if (currentStatus.value) {
    result = result.filter(o => o.status === currentStatus.value)
  }
  return result
})

const onStatusChange = (e: any) => { currentStatus.value = statusOptions[Number(e.detail.value)].value }
const onStartDateChange = (e: any) => { startDate.value = e.detail.value }
const onEndDateChange = (e: any) => { endDate.value = e.detail.value }
const handleSearch = () => {}
</script>

<style scoped>
.container { padding: 20rpx; background: #f5f5f5; min-height: 100vh; }
.search-bar { margin-bottom: 16rpx; }
.search-input {
  display: flex; align-items: center; gap: 12rpx;
  background: #fff; border-radius: 16rpx; padding: 16rpx 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.search-icon { font-size: 28rpx; }
.search-input input { flex: 1; font-size: 28rpx; }
.filter-bar { display: flex; gap: 12rpx; margin-bottom: 20rpx; }
.filter-btn {
  flex: 1; background: #fff; border-radius: 12rpx; padding: 16rpx;
  text-align: center; font-size: 24rpx; color: #666;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.order-list { display: flex; flex-direction: column; gap: 16rpx; }
.order-card { background: #fff; border-radius: 16rpx; padding: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05); }
.order-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16rpx; }
.order-left { display: flex; flex-direction: column; gap: 4rpx; }
.order-no { font-size: 30rpx; font-weight: 600; color: #333; }
.customer { font-size: 24rpx; color: #999; }
.order-status { font-size: 24rpx; padding: 4rpx 16rpx; border-radius: 8rpx; white-space: nowrap; }
.status-pending { color: #faad14; background: rgba(250,173,20,0.1); }
.status-in_progress { color: #1890ff; background: rgba(24,144,255,0.1); }
.status-completed { color: #52c41a; background: rgba(82,196,26,0.1); }
.status-delayed { color: #ff4d4f; background: rgba(255,77,79,0.1); }
.order-info { display: grid; grid-template-columns: 1fr 1fr; gap: 12rpx; margin-bottom: 16rpx; }
.info-row { display: flex; gap: 8rpx; }
.info-label { font-size: 24rpx; color: #999; }
.info-value { font-size: 24rpx; color: #333; }
.info-value.warning { color: #ff4d4f; }
.progress-bar-wrap { height: 10rpx; background: #e8e8e8; border-radius: 5rpx; overflow: hidden; }
.progress-bar { height: 100%; background: linear-gradient(90deg, #722ed1, #b37feb); border-radius: 5rpx; transition: width 0.3s; }
.progress-bar.done { background: linear-gradient(90deg, #52c41a, #73d13d); }
.empty-state { padding: 80rpx; text-align: center; }
.empty-text { font-size: 26rpx; color: #ccc; }
</style>
