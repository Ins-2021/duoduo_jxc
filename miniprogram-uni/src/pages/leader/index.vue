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
      <view class="stat-item" @click="goTo('/pages/leader/assign')">
        <text class="stat-value">{{ overview.totalTasks }}</text>
        <text class="stat-label">总任务</text>
      </view>
      <view class="stat-item">
        <text class="stat-value highlight">{{ overview.pendingAssign }}</text>
        <text class="stat-label">待分配</text>
      </view>
      <view class="stat-item">
        <text class="stat-value success">{{ overview.completed }}</text>
        <text class="stat-label">已完成</text>
      </view>
    </view>

    <view class="section">
      <text class="section-title">快捷操作</text>
      <view class="quick-actions">
        <view class="action-item" @click="goTo('/pages/leader/assign')">
          <text class="action-icon">📋</text>
          <text>任务分配</text>
        </view>
        <view class="action-item" @click="goTo('/pages/leader/progress')">
          <text class="action-icon">📊</text>
          <text>生产进度</text>
        </view>
        <view class="action-item" @click="goTo('/pages/leader/exception')">
          <text class="action-icon">⚠️</text>
          <text>异常处理</text>
        </view>
        <view class="action-item" @click="goTo('/pages/leader/approval')">
          <text class="action-icon">✅</text>
          <text>审批待办</text>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-header">
        <text class="section-title">今日生产进度</text>
        <text class="more" @click="goTo('/pages/leader/progress')">查看全部</text>
      </view>
      <view v-if="todayProgress.length" class="progress-list">
        <view v-for="item in todayProgress" :key="item.id" class="progress-card">
          <view class="progress-top">
            <text class="order-no">{{ item.orderNo }}</text>
            <text class="style-no">{{ item.styleNo }}</text>
          </view>
          <view class="progress-bar-wrap">
            <view class="progress-bar" :style="{ width: item.percent + '%' }" />
          </view>
          <view class="progress-bottom">
            <text class="progress-text">{{ item.completed }}/{{ item.total }}</text>
            <text class="percent-text">{{ item.percent }}%</text>
          </view>
        </view>
      </view>
      <view v-else class="empty-state">
        <text class="empty-text">暂无生产任务</text>
      </view>
    </view>

    <view class="section">
      <view class="section-header">
        <text class="section-title">待审批事项</text>
        <text class="more" @click="goTo('/pages/leader/approval')">查看全部</text>
      </view>
      <view v-if="pendingApprovals.length" class="approval-list">
        <view v-for="item in pendingApprovals" :key="item.id" class="approval-item">
          <view class="approval-left">
            <text class="approval-type">{{ item.type }}</text>
            <text class="approval-no">{{ item.no }}</text>
          </view>
          <text class="approval-time">{{ item.time }}</text>
        </view>
      </view>
      <view v-else class="empty-state">
        <text class="empty-text">暂无待审批事项</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { getOverview } from '@/api/leader'

const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)

const overview = ref({ totalTasks: 12, pendingAssign: 3, completed: 8 })

const todayProgress = ref([
  { id: 1, orderNo: 'PO2026032901', styleNo: '款号A001', completed: 120, total: 200, percent: 60 },
  { id: 2, orderNo: 'PO2026032902', styleNo: '款号B002', completed: 80, total: 150, percent: 53 },
  { id: 3, orderNo: 'PO2026032803', styleNo: '款号C003', completed: 50, total: 100, percent: 50 }
])

const pendingApprovals = ref([
  { id: 1, type: '请假审批', no: 'LV20260329001', time: '09:30' },
  { id: 2, type: '加班申请', no: 'OT20260329001', time: '10:15' }
])

onShow(() => {
  userInfo.value = userStore.userInfo
})

const goTo = (url: string) => { uni.navigateTo({ url }) }
</script>

<style scoped>
.container { padding: 20rpx; background: #f5f5f5; min-height: 100vh; }
.user-card {
  display: flex; align-items: center; gap: 20rpx;
  background: linear-gradient(135deg, #1890ff, #096dd9);
  border-radius: 16rpx; padding: 30rpx; margin-bottom: 20rpx;
}
.avatar image { width: 80rpx; height: 80rpx; border-radius: 50%; }
.info { display: flex; flex-direction: column; gap: 6rpx; }
.name { font-size: 32rpx; font-weight: 600; color: #fff; }
.dept { font-size: 24rpx; color: rgba(255,255,255,0.8); }
.stats-grid {
  display: flex; background: #fff; border-radius: 16rpx;
  padding: 30rpx 0; margin-bottom: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.stat-item { flex: 1; text-align: center; }
.stat-value { display: block; font-size: 44rpx; font-weight: 700; color: #333; }
.stat-value.highlight { color: #faad14; }
.stat-value.success { color: #52c41a; }
.stat-label { display: block; font-size: 24rpx; color: #999; margin-top: 8rpx; }
.section { background: #fff; border-radius: 16rpx; padding: 30rpx; margin-bottom: 20rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05); }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20rpx; }
.section-title { font-size: 30rpx; font-weight: 600; color: #333; }
.more { font-size: 26rpx; color: #1890ff; }
.quick-actions { display: flex; justify-content: space-around; }
.action-item { display: flex; flex-direction: column; align-items: center; gap: 8rpx; }
.action-icon { font-size: 48rpx; }
.action-item text:last-child { font-size: 24rpx; color: #666; }
.progress-list { display: flex; flex-direction: column; gap: 20rpx; }
.progress-card { background: #fafafa; border-radius: 12rpx; padding: 20rpx; }
.progress-top { display: flex; justify-content: space-between; margin-bottom: 12rpx; }
.order-no { font-size: 28rpx; font-weight: 600; color: #333; }
.style-no { font-size: 26rpx; color: #999; }
.progress-bar-wrap { height: 16rpx; background: #e8e8e8; border-radius: 8rpx; overflow: hidden; }
.progress-bar { height: 100%; background: linear-gradient(90deg, #1890ff, #36cfc9); border-radius: 8rpx; transition: width 0.3s; }
.progress-bottom { display: flex; justify-content: space-between; margin-top: 8rpx; }
.progress-text { font-size: 24rpx; color: #999; }
.percent-text { font-size: 24rpx; color: #1890ff; font-weight: 600; }
.approval-list { display: flex; flex-direction: column; gap: 16rpx; }
.approval-item {
  display: flex; justify-content: space-between; align-items: center;
  padding: 16rpx 20rpx; background: #fafafa; border-radius: 12rpx;
}
.approval-left { display: flex; flex-direction: column; gap: 4rpx; }
.approval-type { font-size: 28rpx; color: #333; font-weight: 500; }
.approval-no { font-size: 24rpx; color: #999; }
.approval-time { font-size: 24rpx; color: #999; }
.empty-state { padding: 40rpx; text-align: center; }
.empty-text { font-size: 26rpx; color: #ccc; }
</style>
