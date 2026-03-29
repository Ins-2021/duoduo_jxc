<template>
  <view class="container">
    <view class="user-card">
      <view class="avatar"><image src="/static/default-avatar.png" mode="aspectFill" /></view>
      <view class="info">
        <text class="name">{{ userInfo.name || '未登录' }}</text>
        <text class="dept">{{ userInfo.departmentName || '' }}</text>
      </view>
    </view>

    <view class="stats-section">
      <view class="section-title">今日统计</view>
      <view class="stats-grid">
        <view class="stat-item">
          <text class="stat-value">{{ todayStats.total }}</text>
          <text class="stat-label">质检总数</text>
        </view>
        <view class="stat-item">
          <text class="stat-value pass">{{ todayStats.passed }}</text>
          <text class="stat-label">合格</text>
        </view>
        <view class="stat-item">
          <text class="stat-value fail">{{ todayStats.failed }}</text>
          <text class="stat-label">不合格</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ todayStats.passRate }}%</text>
          <text class="stat-label">合格率</text>
        </view>
      </view>
    </view>

    <view class="quick-actions">
      <view class="section-title">快捷操作</view>
      <view class="action-grid">
        <view class="action-item" @click="goTo('/pages/inspector/check')">
          <text class="action-icon">📷</text>
          <text class="action-text">扫码质检</text>
        </view>
        <view class="action-item" @click="goTo('/pages/inspector/first-article')">
          <text class="action-icon">✅</text>
          <text class="action-text">首件确认</text>
        </view>
        <view class="action-item" @click="goTo('/pages/inspector/rework')">
          <text class="action-icon">🔄</text>
          <text class="action-text">返工处理</text>
        </view>
        <view class="action-item" @click="goTo('/pages/inspector/history')">
          <text class="action-icon">📋</text>
          <text class="action-text">质检记录</text>
        </view>
      </view>
    </view>

    <view class="todo-section">
      <view class="section-title">待办事项</view>
      <view v-if="pendingFirstArticles.length > 0 || pendingDefects.length > 0">
        <view v-for="item in pendingFirstArticles" :key="'fa-' + item.id" class="todo-item" @click="goTo('/pages/inspector/first-article')">
          <text class="todo-tag tag-warn">首件</text>
          <text class="todo-content">{{ item.styleNo }} - {{ item.color }}/{{ item.size }}</text>
          <text class="todo-arrow">›</text>
        </view>
        <view v-for="item in pendingDefects" :key="'df-' + item.id" class="todo-item" @click="goTo('/pages/inspector/rework')">
          <text class="todo-tag tag-danger">缺陷</text>
          <text class="todo-content">{{ item.bundleNo }} - {{ item.defectType }}</text>
          <text class="todo-arrow">›</text>
        </view>
      </view>
      <view v-else class="empty-state">
        <text>暂无待办事项</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { getTodayStats, getPendingFirstArticles } from '@/api/inspector'

const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)

const todayStats = ref({ total: 0, passed: 0, failed: 0, passRate: '0.0' })
const pendingFirstArticles = ref<any[]>([])
const pendingDefects = ref<any[]>([])

const loadTodayStats = async () => {
  try {
    const res = await getTodayStats()
    todayStats.value = res.data || { total: 0, passed: 0, failed: 0, passRate: '0.0' }
  } catch {}
}

const loadPendingFirstArticles = async () => {
  try {
    const res = await getPendingFirstArticles()
    const list = res.data || []
    pendingFirstArticles.value = list
    pendingDefects.value = (res.data?.defects || []) as any[]
  } catch {}
}

onShow(() => {
  userInfo.value = userStore.userInfo
  loadTodayStats()
  loadPendingFirstArticles()
})

const goTo = (url: string) => { uni.navigateTo({ url }) }
</script>

<style scoped>
.container { padding: 24rpx; }
.user-card {
  display: flex; align-items: center; gap: 24rpx;
  background: linear-gradient(135deg, #1890ff, #096dd9);
  border-radius: 16rpx; padding: 30rpx; margin-bottom: 24rpx;
}
.avatar image { width: 100rpx; height: 100rpx; border-radius: 50%; }
.info { display: flex; flex-direction: column; }
.name { font-size: 32rpx; font-weight: 600; color: #fff; }
.dept { font-size: 24rpx; color: rgba(255,255,255,0.8); margin-top: 6rpx; }
.section-title { font-size: 30rpx; font-weight: 600; color: #333; margin-bottom: 20rpx; }
.stats-section {
  background: #fff; border-radius: 16rpx; padding: 30rpx; margin-bottom: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16rpx; text-align: center; }
.stat-value { display: block; font-size: 40rpx; font-weight: 700; color: #333; }
.stat-value.pass { color: #52c41a; }
.stat-value.fail { color: #ff4d4f; }
.stat-label { display: block; font-size: 24rpx; color: #999; margin-top: 8rpx; }
.quick-actions {
  background: #fff; border-radius: 16rpx; padding: 30rpx; margin-bottom: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.action-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20rpx; text-align: center; }
.action-item { display: flex; flex-direction: column; align-items: center; gap: 10rpx; }
.action-icon { font-size: 48rpx; }
.action-text { font-size: 24rpx; color: #333; }
.todo-section {
  background: #fff; border-radius: 16rpx; padding: 30rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.todo-item {
  display: flex; align-items: center; gap: 16rpx;
  padding: 20rpx 0; border-bottom: 1rpx solid #f0f0f0;
}
.todo-item:last-child { border-bottom: none; }
.todo-tag {
  font-size: 22rpx; padding: 4rpx 12rpx; border-radius: 6rpx; color: #fff;
}
.tag-warn { background: #faad14; }
.tag-danger { background: #ff4d4f; }
.todo-content { flex: 1; font-size: 28rpx; color: #333; }
.todo-arrow { font-size: 32rpx; color: #ccc; }
.empty-state { text-align: center; padding: 40rpx 0; color: #999; font-size: 28rpx; }
</style>
