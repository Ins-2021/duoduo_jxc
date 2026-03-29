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
      <view class="section-title">库存概览</view>
      <view class="stats-grid">
        <view class="stat-item">
          <text class="stat-value">{{ overview.skuCount }}</text>
          <text class="stat-label">SKU数</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ overview.totalStock }}</text>
          <text class="stat-label">总库存</text>
        </view>
        <view class="stat-item">
          <text class="stat-value warn">{{ overview.warningCount }}</text>
          <text class="stat-label">库存预警</text>
        </view>
      </view>
    </view>

    <view class="quick-actions">
      <view class="section-title">快捷操作</view>
      <view class="action-grid">
        <view class="action-item" @click="goTo('/pages/warehouse/in')">
          <text class="action-icon">📥</text>
          <text class="action-text">成品入库</text>
        </view>
        <view class="action-item" @click="goTo('/pages/warehouse/out')">
          <text class="action-icon">📤</text>
          <text class="action-text">物料出库</text>
        </view>
        <view class="action-item" @click="goTo('/pages/warehouse/stock')">
          <text class="action-icon">🔍</text>
          <text class="action-text">库存查询</text>
        </view>
        <view class="action-item" @click="goTo('/pages/warehouse/inventory')">
          <text class="action-icon">📝</text>
          <text class="action-text">盘点任务</text>
        </view>
      </view>
    </view>

    <view class="todo-section">
      <view class="section-title">待处理事项</view>
      <view v-if="pendingList.length > 0">
        <view v-for="item in pendingList" :key="item.id" class="todo-item" @click="handlePending(item)">
          <text class="todo-tag" :class="getTagClass(item.type)">{{ getTagText(item.type) }}</text>
          <text class="todo-content">{{ item.title }}</text>
          <text class="todo-time">{{ item.createTime }}</text>
        </view>
      </view>
      <view v-else class="empty-state">
        <text>暂无待处理事项</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { getOverview, getPendingList } from '@/api/warehouse'

const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)

const overview = ref({ skuCount: 0, totalStock: 0, warningCount: 0 })
const pendingList = ref<any[]>([])

const loadOverview = async () => {
  try {
    const res = await getOverview()
    overview.value = res.data || { skuCount: 0, totalStock: 0, warningCount: 0 }
  } catch {}
}

const loadPendingList = async () => {
  try {
    const res = await getPendingList()
    pendingList.value = res.data || []
  } catch {}
}

const getTagText = (type: string) => {
  const map: Record<string, string> = { inbound: '入库', outbound: '出库', inventory: '盘点', warning: '预警' }
  return map[type] || type
}

const getTagClass = (type: string) => {
  const map: Record<string, string> = { inbound: 'tag-in', outbound: 'tag-out', inventory: 'tag-inv', warning: 'tag-warn' }
  return map[type] || ''
}

const handlePending = (item: any) => {
  const routeMap: Record<string, string> = {
    inbound: '/pages/warehouse/in',
    outbound: '/pages/warehouse/out',
    inventory: '/pages/warehouse/inventory',
    warning: '/pages/warehouse/stock'
  }
  const url = routeMap[item.type]
  if (url) uni.navigateTo({ url })
}

onShow(() => {
  userInfo.value = userStore.userInfo
  loadOverview()
  loadPendingList()
})

const goTo = (url: string) => { uni.navigateTo({ url }) }
</script>

<style scoped>
.container { padding: 24rpx; }
.user-card {
  display: flex; align-items: center; gap: 24rpx;
  background: linear-gradient(135deg, #722ed1, #531dab);
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
.stats-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16rpx; text-align: center; }
.stat-value { display: block; font-size: 44rpx; font-weight: 700; color: #333; }
.stat-value.warn { color: #ff4d4f; }
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
.todo-tag { font-size: 22rpx; padding: 4rpx 12rpx; border-radius: 6rpx; color: #fff; flex-shrink: 0; }
.tag-in { background: #52c41a; }
.tag-out { background: #1890ff; }
.tag-inv { background: #722ed1; }
.tag-warn { background: #ff4d4f; }
.todo-content { flex: 1; font-size: 28rpx; color: #333; }
.todo-time { font-size: 24rpx; color: #999; flex-shrink: 0; }
.empty-state { text-align: center; padding: 40rpx 0; color: #999; font-size: 28rpx; }
</style>
