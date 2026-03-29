<template>
  <view class="container">
    <view class="user-card">
      <view class="avatar"><image src="/static/default-avatar.png" mode="aspectFill" /></view>
      <view class="info">
        <text class="name">{{ userInfo.name || '未登录' }}</text>
        <text class="dept">{{ userInfo.departmentName || '缝制车间' }}</text>
      </view>
    </view>

    <view class="stats-grid">
      <view class="stat-card">
        <text class="stat-value">{{ dashboard.activeOrders }}</text>
        <text class="stat-label">生产中订单</text>
      </view>
      <view class="stat-card">
        <text class="stat-value highlight">{{ dashboard.todayOutput }}</text>
        <text class="stat-label">今日产量</text>
      </view>
      <view class="stat-card">
        <text class="stat-value success">{{ dashboard.weekOutput }}</text>
        <text class="stat-label">本周产量</text>
      </view>
      <view class="stat-card">
        <text class="stat-value info">{{ dashboard.qualityRate }}%</text>
        <text class="stat-label">合格率</text>
      </view>
    </view>

    <view class="section">
      <text class="section-title">快捷操作</text>
      <view class="quick-actions">
        <view class="action-item" @click="goTo('/pages/manager/dashboard')">
          <text class="action-icon">📊</text>
          <text>生产看板</text>
        </view>
        <view class="action-item" @click="goTo('/pages/manager/order')">
          <text class="action-icon">📦</text>
          <text>订单管理</text>
        </view>
        <view class="action-item" @click="goTo('/pages/manager/staff')">
          <text class="action-icon">👥</text>
          <text>人员管理</text>
        </view>
        <view class="action-item" @click="goTo('/pages/manager/report')">
          <text class="action-icon">📈</text>
          <text>报表中心</text>
        </view>
      </view>
    </view>

    <view class="section">
      <text class="section-title">今日概览</text>
      <view class="overview-list">
        <view class="overview-item" v-for="item in todayOverview" :key="item.label">
          <text class="overview-label">{{ item.label }}</text>
          <text class="overview-value" :class="item.colorClass">{{ item.value }}</text>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-header">
        <text class="section-title">生产订单动态</text>
        <text class="more" @click="goTo('/pages/manager/order')">查看全部</text>
      </view>
      <view v-if="recentOrders.length" class="order-list">
        <view v-for="order in recentOrders" :key="order.id" class="order-item">
          <view class="order-top">
            <text class="order-no">{{ order.orderNo }}</text>
            <text class="order-status" :class="'status-' + order.status">{{ order.statusText }}</text>
          </view>
          <view class="order-middle">
            <text class="style-no">{{ order.styleNo }}</text>
            <text class="quantity">{{ order.completed }}/{{ order.total }}</text>
          </view>
          <view class="progress-bar-wrap">
            <view class="progress-bar" :style="{ width: order.percent + '%' }" />
          </view>
        </view>
      </view>
      <view v-else class="empty-state">
        <text class="empty-text">暂无生产订单</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { getDashboard } from '@/api/manager'

const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)

const dashboard = ref({
  activeOrders: 8,
  todayOutput: 350,
  weekOutput: 2100,
  qualityRate: 97.5
})

const todayOverview = ref([
  { label: '在岗人数', value: '25/30', colorClass: '' },
  { label: '今日异常', value: '2', colorClass: 'warning' },
  { label: '待审批', value: '3', colorClass: 'info' },
  { label: '物料预警', value: '1', colorClass: 'danger' }
])

const recentOrders = ref([
  { id: 1, orderNo: 'PO2026032901', styleNo: '款号A001', completed: 120, total: 200, percent: 60, status: 'in_progress', statusText: '生产中' },
  { id: 2, orderNo: 'PO2026032902', styleNo: '款号B002', completed: 80, total: 150, percent: 53, status: 'in_progress', statusText: '生产中' },
  { id: 3, orderNo: 'PO2026032703', styleNo: '款号C003', completed: 100, total: 100, percent: 100, status: 'completed', statusText: '已完成' }
])

onShow(() => { userInfo.value = userStore.userInfo })

const goTo = (url: string) => { uni.navigateTo({ url }) }
</script>

<style scoped>
.container { padding: 20rpx; background: #f5f5f5; min-height: 100vh; }
.user-card {
  display: flex; align-items: center; gap: 20rpx;
  background: linear-gradient(135deg, #722ed1, #531dab);
  border-radius: 16rpx; padding: 30rpx; margin-bottom: 20rpx;
}
.avatar image { width: 80rpx; height: 80rpx; border-radius: 50%; }
.info { display: flex; flex-direction: column; gap: 6rpx; }
.name { font-size: 32rpx; font-weight: 600; color: #fff; }
.dept { font-size: 24rpx; color: rgba(255,255,255,0.8); }
.stats-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16rpx; margin-bottom: 20rpx; }
.stat-card {
  background: #fff; border-radius: 16rpx; padding: 24rpx; text-align: center;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.stat-value { display: block; font-size: 44rpx; font-weight: 700; color: #333; }
.stat-value.highlight { color: #faad14; }
.stat-value.success { color: #52c41a; }
.stat-value.info { color: #1890ff; }
.stat-label { display: block; font-size: 24rpx; color: #999; margin-top: 8rpx; }
.section { background: #fff; border-radius: 16rpx; padding: 30rpx; margin-bottom: 20rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05); }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20rpx; }
.section-title { font-size: 30rpx; font-weight: 600; color: #333; display: block; margin-bottom: 20rpx; }
.section-header .section-title { margin-bottom: 0; }
.more { font-size: 26rpx; color: #722ed1; }
.quick-actions { display: flex; justify-content: space-around; }
.action-item { display: flex; flex-direction: column; align-items: center; gap: 8rpx; }
.action-icon { font-size: 48rpx; }
.action-item text:last-child { font-size: 24rpx; color: #666; }
.overview-list { display: grid; grid-template-columns: 1fr 1fr; gap: 16rpx; }
.overview-item { display: flex; justify-content: space-between; padding: 16rpx 20rpx; background: #fafafa; border-radius: 12rpx; }
.overview-label { font-size: 26rpx; color: #999; }
.overview-value { font-size: 28rpx; font-weight: 600; color: #333; }
.overview-value.warning { color: #faad14; }
.overview-value.info { color: #1890ff; }
.overview-value.danger { color: #ff4d4f; }
.order-list { display: flex; flex-direction: column; gap: 20rpx; }
.order-item { background: #fafafa; border-radius: 12rpx; padding: 20rpx; }
.order-top { display: flex; justify-content: space-between; margin-bottom: 8rpx; }
.order-no { font-size: 28rpx; font-weight: 600; color: #333; }
.order-status { font-size: 24rpx; padding: 2rpx 12rpx; border-radius: 8rpx; }
.status-in_progress { color: #1890ff; background: rgba(24,144,255,0.1); }
.status-completed { color: #52c41a; background: rgba(82,196,26,0.1); }
.status-pending { color: #faad14; background: rgba(250,173,20,0.1); }
.order-middle { display: flex; justify-content: space-between; margin-bottom: 12rpx; }
.style-no { font-size: 26rpx; color: #999; }
.quantity { font-size: 26rpx; color: #666; }
.progress-bar-wrap { height: 12rpx; background: #e8e8e8; border-radius: 6rpx; overflow: hidden; }
.progress-bar { height: 100%; background: linear-gradient(90deg, #722ed1, #b37feb); border-radius: 6rpx; transition: width 0.3s; }
.empty-state { padding: 40rpx; text-align: center; }
.empty-text { font-size: 26rpx; color: #ccc; }
</style>
