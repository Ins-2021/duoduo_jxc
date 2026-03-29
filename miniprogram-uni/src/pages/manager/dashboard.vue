<template>
  <view class="container">
    <view class="stats-grid">
      <view class="stat-card">
        <text class="stat-value">{{ stats.waiting }}</text>
        <text class="stat-label">待开工</text>
      </view>
      <view class="stat-card">
        <text class="stat-value highlight">{{ stats.inProgress }}</text>
        <text class="stat-label">生产中</text>
      </view>
      <view class="stat-card">
        <text class="stat-value success">{{ stats.completed }}</text>
        <text class="stat-label">已完成</text>
      </view>
      <view class="stat-card">
        <text class="stat-value info">{{ stats.todayOutput }}</text>
        <text class="stat-label">今日产量</text>
      </view>
    </view>

    <view class="kanban">
      <view class="kanban-column">
        <view class="column-header pending">
          <text class="column-title">待开工</text>
          <text class="column-count">{{ waitingOrders.length }}</text>
        </view>
        <scroll-view class="column-body" scroll-y>
          <view v-for="order in waitingOrders" :key="order.id" class="order-card">
            <text class="card-order-no">{{ order.orderNo }}</text>
            <text class="card-style-no">{{ order.styleNo }}</text>
            <view class="card-quantity">
              <text>{{ order.quantity }}件</text>
              <text class="card-deadline">交期: {{ order.deadline }}</text>
            </view>
            <view class="card-process">
              <text class="process-label">当前工序:</text>
              <text class="process-name">{{ order.currentProcess }}</text>
            </view>
          </view>
          <view v-if="!waitingOrders.length" class="column-empty">
            <text>暂无</text>
          </view>
        </scroll-view>
      </view>

      <view class="kanban-column">
        <view class="column-header progress">
          <text class="column-title">生产中</text>
          <text class="column-count">{{ progressOrders.length }}</text>
        </view>
        <scroll-view class="column-body" scroll-y>
          <view v-for="order in progressOrders" :key="order.id" class="order-card">
            <text class="card-order-no">{{ order.orderNo }}</text>
            <text class="card-style-no">{{ order.styleNo }}</text>
            <view class="progress-bar-wrap">
              <view class="progress-bar" :style="{ width: order.percent + '%' }" />
            </view>
            <view class="card-quantity">
              <text>{{ order.completed }}/{{ order.quantity }}件</text>
              <text class="percent-text">{{ order.percent }}%</text>
            </view>
            <view class="card-process">
              <text class="process-label">当前工序:</text>
              <text class="process-name">{{ order.currentProcess }}</text>
            </view>
          </view>
          <view v-if="!progressOrders.length" class="column-empty">
            <text>暂无</text>
          </view>
        </scroll-view>
      </view>

      <view class="kanban-column">
        <view class="column-header done">
          <text class="column-title">已完成</text>
          <text class="column-count">{{ doneOrders.length }}</text>
        </view>
        <scroll-view class="column-body" scroll-y>
          <view v-for="order in doneOrders" :key="order.id" class="order-card">
            <text class="card-order-no">{{ order.orderNo }}</text>
            <text class="card-style-no">{{ order.styleNo }}</text>
            <view class="progress-bar-wrap">
              <view class="progress-bar done" style="width: 100%" />
            </view>
            <view class="card-quantity">
              <text>{{ order.quantity }}件</text>
              <text class="percent-text success">100%</text>
            </view>
            <view class="card-process">
              <text class="process-label">完成时间:</text>
              <text class="process-name">{{ order.completedTime }}</text>
            </view>
          </view>
          <view v-if="!doneOrders.length" class="column-empty">
            <text>暂无</text>
          </view>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getDashboard } from '@/api/manager'

const stats = ref({ waiting: 3, inProgress: 5, completed: 8, todayOutput: 350 })

const waitingOrders = ref([
  { id: 1, orderNo: 'PO2026033001', styleNo: '款号D004', quantity: 120, deadline: '2026-04-15', currentProcess: '裁剪' },
  { id: 2, orderNo: 'PO2026033002', styleNo: '款号E005', quantity: 80, deadline: '2026-04-18', currentProcess: '备料' },
  { id: 3, orderNo: 'PO2026033003', styleNo: '款号F006', quantity: 200, deadline: '2026-04-20', currentProcess: '裁剪' }
])

const progressOrders = ref([
  { id: 4, orderNo: 'PO2026032901', styleNo: '款号A001', quantity: 200, completed: 120, percent: 60, currentProcess: '缝制' },
  { id: 5, orderNo: 'PO2026032902', styleNo: '款号B002', quantity: 150, completed: 80, percent: 53, currentProcess: '缝制' },
  { id: 6, orderNo: 'PO2026032801', styleNo: '款号G007', quantity: 300, completed: 240, percent: 80, currentProcess: '质检' },
  { id: 7, orderNo: 'PO2026032802', styleNo: '款号H008', quantity: 100, completed: 30, percent: 30, currentProcess: '缝制' },
  { id: 8, orderNo: 'PO2026032701', styleNo: '款号I009', quantity: 160, completed: 100, percent: 63, currentProcess: '缝制' }
])

const doneOrders = ref([
  { id: 9, orderNo: 'PO2026032703', styleNo: '款号C003', quantity: 100, completedTime: '2026-03-28' },
  { id: 10, orderNo: 'PO2026032501', styleNo: '款号J010', quantity: 80, completedTime: '2026-03-27' },
  { id: 11, orderNo: 'PO2026032401', styleNo: '款号K011', quantity: 150, completedTime: '2026-03-26' }
])

onShow(() => {})
</script>

<style scoped>
.container { padding: 20rpx; background: #f5f5f5; min-height: 100vh; }
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12rpx; margin-bottom: 20rpx; }
.stat-card { background: #fff; border-radius: 12rpx; padding: 20rpx 10rpx; text-align: center; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05); }
.stat-value { display: block; font-size: 36rpx; font-weight: 700; color: #333; }
.stat-value.highlight { color: #faad14; }
.stat-value.success { color: #52c41a; }
.stat-value.info { color: #1890ff; }
.stat-label { display: block; font-size: 22rpx; color: #999; margin-top: 6rpx; }
.kanban { display: flex; gap: 12rpx; height: calc(100vh - 260rpx); }
.kanban-column { flex: 1; background: #f0f0f0; border-radius: 12rpx; display: flex; flex-direction: column; overflow: hidden; }
.column-header { padding: 16rpx; display: flex; justify-content: space-between; align-items: center; }
.column-header.pending { background: #fff7e6; }
.column-header.progress { background: #e6f7ff; }
.column-header.done { background: #f6ffed; }
.column-title { font-size: 26rpx; font-weight: 600; }
.column-header.pending .column-title { color: #d46b08; }
.column-header.progress .column-title { color: #096dd9; }
.column-header.done .column-title { color: #389e0d; }
.column-count { font-size: 24rpx; background: rgba(0,0,0,0.06); padding: 2rpx 12rpx; border-radius: 20rpx; }
.column-body { flex: 1; padding: 12rpx; }
.order-card { background: #fff; border-radius: 12rpx; padding: 16rpx; margin-bottom: 12rpx; box-shadow: 0 1rpx 6rpx rgba(0,0,0,0.05); }
.card-order-no { display: block; font-size: 26rpx; font-weight: 600; color: #333; margin-bottom: 4rpx; }
.card-style-no { display: block; font-size: 22rpx; color: #999; margin-bottom: 12rpx; }
.card-quantity { display: flex; justify-content: space-between; font-size: 22rpx; color: #666; margin-bottom: 8rpx; }
.card-deadline { font-size: 22rpx; color: #faad14; }
.percent-text { font-size: 22rpx; color: #1890ff; font-weight: 600; }
.percent-text.success { color: #52c41a; }
.progress-bar-wrap { height: 10rpx; background: #e8e8e8; border-radius: 5rpx; overflow: hidden; margin-bottom: 8rpx; }
.progress-bar { height: 100%; background: linear-gradient(90deg, #1890ff, #36cfc9); border-radius: 5rpx; }
.progress-bar.done { background: linear-gradient(90deg, #52c41a, #73d13d); }
.card-process { display: flex; gap: 8rpx; font-size: 22rpx; }
.process-label { color: #999; }
.process-name { color: #722ed1; font-weight: 500; }
.column-empty { text-align: center; padding: 40rpx; color: #ccc; font-size: 24rpx; }
</style>
