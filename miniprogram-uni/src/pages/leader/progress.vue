<template>
  <view class="container">
    <view class="filter-bar">
      <view
        v-for="tab in statusTabs"
        :key="tab.value"
        class="filter-item"
        :class="{ active: currentStatus === tab.value }"
        @click="currentStatus = tab.value"
      >
        <text>{{ tab.label }}</text>
      </view>
    </view>

    <view v-if="filteredProgress.length" class="progress-group">
      <view v-for="order in filteredProgress" :key="order.id" class="order-card">
        <view class="order-header">
          <text class="order-no">{{ order.orderNo }}</text>
          <text class="style-no">{{ order.styleNo }}</text>
        </view>
        <view class="progress-bar-wrap">
          <view class="progress-bar" :style="progressStyle(order.percent)" :class="order.percent === 100 ? 'done' : ''" />
        </view>
        <view class="progress-bottom">
          <text class="progress-text">完成 {{ order.completed }}/{{ order.total }}</text>
          <text class="percent-text">{{ order.percent }}%</text>
        </view>
        <view class="order-steps">
          <view v-for="(step, idx) in order.steps" :key="idx" class="step-item" :class="{ active: step.active, done: step.done }">
            <view class="step-dot" />
            <text class="step-name">{{ step.name }}</text>
          </view>
        </view>
      </view>
    </view>
    <view v-else class="empty-state">
      <text class="empty-text">暂无生产进度数据</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getProgressList } from '@/api/leader'

const statusTabs = [
  { label: '全部', value: 'all' },
  { label: '进行中', value: 'in_progress' },
  { label: '已完成', value: 'completed' }
]

const currentStatus = ref('all')

const progressData = ref([
  {
    id: 1, orderNo: 'PO2026032901', styleNo: '款号A001', completed: 120, total: 200, percent: 60, status: 'in_progress',
    steps: [
      { name: '裁剪', active: false, done: true },
      { name: '缝制', active: true, done: false },
      { name: '质检', active: false, done: false },
      { name: '包装', active: false, done: false }
    ]
  },
  {
    id: 2, orderNo: 'PO2026032902', styleNo: '款号B002', completed: 80, total: 150, percent: 53, status: 'in_progress',
    steps: [
      { name: '裁剪', active: false, done: true },
      { name: '缝制', active: true, done: false },
      { name: '质检', active: false, done: false },
      { name: '包装', active: false, done: false }
    ]
  },
  {
    id: 3, orderNo: 'PO2026032703', styleNo: '款号C003', completed: 100, total: 100, percent: 100, status: 'completed',
    steps: [
      { name: '裁剪', active: false, done: true },
      { name: '缝制', active: false, done: true },
      { name: '质检', active: false, done: true },
      { name: '包装', active: false, done: true }
    ]
  }
])

const progressStyle = (percent: number) => ({ width: `${percent}%` })

const filteredProgress = computed(() => {
  if (currentStatus.value === 'all') return progressData.value
  return progressData.value.filter(o => o.status === currentStatus.value)
})
</script>

<style scoped>
.container { padding: 20rpx; background: #f5f5f5; min-height: 100vh; }
.filter-bar { display: flex; gap: 16rpx; margin-bottom: 20rpx; background: #fff; border-radius: 16rpx; padding: 16rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05); }
.filter-item {
  flex: 1; text-align: center; padding: 16rpx 0; border-radius: 12rpx;
  font-size: 28rpx; color: #666; transition: all 0.2s;
}
.filter-item.active { background: #1890ff; color: #fff; font-weight: 600; }
.progress-group { display: flex; flex-direction: column; gap: 20rpx; }
.order-card { background: #fff; border-radius: 16rpx; padding: 30rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05); }
.order-header { display: flex; justify-content: space-between; margin-bottom: 16rpx; }
.order-no { font-size: 30rpx; font-weight: 600; color: #333; }
.style-no { font-size: 26rpx; color: #999; }
.progress-bar-wrap { height: 16rpx; background: #e8e8e8; border-radius: 8rpx; overflow: hidden; margin-bottom: 12rpx; }
.progress-bar { height: 100%; background: linear-gradient(90deg, #1890ff, #36cfc9); border-radius: 8rpx; transition: width 0.3s; }
.progress-bar.done { background: linear-gradient(90deg, #52c41a, #73d13d); }
.progress-bottom { display: flex; justify-content: space-between; margin-bottom: 20rpx; }
.progress-text { font-size: 26rpx; color: #999; }
.percent-text { font-size: 26rpx; color: #1890ff; font-weight: 600; }
.order-steps { display: flex; gap: 20rpx; padding-top: 16rpx; border-top: 1rpx solid #f0f0f0; }
.step-item { display: flex; flex-direction: column; align-items: center; gap: 8rpx; }
.step-dot { width: 20rpx; height: 20rpx; border-radius: 50%; background: #ddd; }
.step-item.active .step-dot { background: #1890ff; box-shadow: 0 0 0 6rpx rgba(24,144,255,0.2); }
.step-item.done .step-dot { background: #52c41a; }
.step-name { font-size: 22rpx; color: #999; }
.step-item.active .step-name { color: #1890ff; font-weight: 600; }
.step-item.done .step-name { color: #52c41a; }
.empty-state { padding: 80rpx; text-align: center; }
.empty-text { font-size: 26rpx; color: #ccc; }
</style>
