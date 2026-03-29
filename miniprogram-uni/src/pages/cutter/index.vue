<template>
  <view class="container">
    <view class="user-card">
      <view class="avatar"><image src="/static/default-avatar.png" mode="aspectFill" /></view>
      <view class="info">
        <text class="name">{{ userInfo.name || '未登录' }}</text>
        <text class="dept">{{ userInfo.departmentName || '' }}</text>
      </view>
    </view>
    <stats-card :items="todayStatsItems" title="今日统计" />
    <view class="quick-actions">
      <view class="action-item" @click="goTo('/pages/cutter/task')">
        <text class="action-icon">📋</text>
        <text>裁床任务</text>
      </view>
      <view class="action-item" @click="goTo('/pages/cutter/input')">
        <text class="action-icon">✂️</text>
        <text>裁床录入</text>
      </view>
      <view class="action-item" @click="goTo('/pages/cutter/bundle')">
        <text class="action-icon">📦</text>
        <text>分包管理</text>
      </view>
    </view>
    <view class="pending-section">
      <view class="section-header">
        <text class="section-title">待处理任务</text>
        <text class="section-more" @click="goTo('/pages/cutter/task')">查看全部 ›</text>
      </view>
      <view class="task-list">
        <view class="task-item" v-for="item in pendingTasks" :key="item.id" @click="goTo('/pages/cutter/input?taskId=' + item.id)">
          <view class="task-info">
            <text class="style-code">{{ item.styleCode }}</text>
            <text class="style-desc">{{ item.styleName || '' }}</text>
          </view>
          <view class="task-meta">
            <text class="quantity">{{ item.totalQuantity || 0 }}件</text>
            <text class="status-tag pending">待裁剪</text>
          </view>
        </view>
        <view v-if="pendingTasks.length === 0" class="empty">暂无待处理任务</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import StatsCard from '@/components/stats-card/stats-card.vue'
import { getCuttingTasks, getTodayStats } from '@/api/cutter'

const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)

const todayStats = ref({ cutCount: 0, totalQuantity: 0, bundleCount: 0 })
const todayStatsItems = computed(() => [
  { label: '裁剪次数', value: todayStats.value.cutCount },
  { label: '裁剪数量', value: todayStats.value.totalQuantity },
  { label: '生成扎包', value: todayStats.value.bundleCount }
])

const pendingTasks = ref<any[]>([])

onShow(async () => {
  userInfo.value = userStore.userInfo
  try {
    const statsRes: any = await getTodayStats()
    if (statsRes.success) todayStats.value = statsRes.data || todayStats.value
  } catch {}
  try {
    const taskRes: any = await getCuttingTasks({ status: 'pending' })
    if (taskRes.success) pendingTasks.value = (taskRes.data || []).slice(0, 5)
  } catch {}
})

const goTo = (url: string) => { uni.navigateTo({ url }) }
</script>

<style scoped>
.pending-section { margin-top: 20rpx; }
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}
.section-title { font-size: 30rpx; font-weight: 600; color: #333; }
.section-more { font-size: 26rpx; color: #1890ff; }
.task-list { display: flex; flex-direction: column; gap: 16rpx; }
.task-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.task-info { display: flex; flex-direction: column; gap: 4rpx; }
.style-code { font-size: 28rpx; font-weight: 600; color: #333; }
.style-desc { font-size: 24rpx; color: #999; }
.task-meta { display: flex; align-items: center; gap: 16rpx; }
.quantity { font-size: 28rpx; color: #333; }
.status-tag {
  font-size: 22rpx;
  padding: 4rpx 14rpx;
  border-radius: 20rpx;
}
.status-tag.pending { background: #fff7e6; color: #fa8c16; }
.empty { text-align: center; color: #999; padding: 60rpx; font-size: 28rpx; }
</style>
