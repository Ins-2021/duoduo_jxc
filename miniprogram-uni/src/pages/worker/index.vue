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
      <view class="action-item" @click="goTo('/pages/worker/piecework')">
        <text class="action-icon">📷</text>
        <text>扫码计件</text>
      </view>
      <view class="action-item" @click="goTo('/pages/worker/task')">
        <text class="action-icon">📋</text>
        <text>我的任务</text>
      </view>
      <view class="action-item" @click="goTo('/pages/worker/wage')">
        <text class="action-icon">💰</text>
        <text>工资查询</text>
      </view>
    </view>
    <view class="wage-card">
      <view class="card-header">
        <text class="title">本月工资</text>
        <text class="month">{{ currentMonth }}</text>
      </view>
      <view class="wage-amount">¥{{ monthWage.amount || '0.00' }}</view>
      <view class="wage-detail">
        <text>计件 {{ monthWage.count || 0 }} 次</text>
        <text>数量 {{ monthWage.quantity || 0 }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import StatsCard from '@/components/stats-card/stats-card.vue'

const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)

const currentMonth = computed(() => {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
})

const todayStats = ref({ count: 0, quantity: 0, amount: 0 })
const todayStatsItems = computed(() => [
  { label: '计件数', value: todayStats.value.count },
  { label: '总数量', value: todayStats.value.quantity },
  { label: '计件金额', value: `¥${todayStats.value.amount}` }
])

const monthWage = ref({ amount: 0, count: 0, quantity: 0 })

onShow(() => { userInfo.value = userStore.userInfo })

const goTo = (url: string) => { uni.navigateTo({ url }) }
</script>

<style scoped>
.wage-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}
.card-header .title { font-size: 30rpx; font-weight: 600; color: #333; }
.card-header .month { font-size: 26rpx; color: #999; }
.wage-amount { font-size: 48rpx; font-weight: 700; color: #52c41a; margin-bottom: 12rpx; }
.wage-detail {
  display: flex;
  gap: 40rpx;
  font-size: 26rpx;
  color: #999;
}
</style>
