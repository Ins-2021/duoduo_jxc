<template>
  <view class="container">
    <view class="user-card">
      <view class="avatar"><image src="/static/default-avatar.png" mode="aspectFill" /></view>
      <view class="info">
        <text class="name">{{ userInfo.name || '未登录' }}</text>
        <text class="dept">经营总览</text>
      </view>
    </view>

    <view class="kpi-grid">
      <view class="kpi-card output">
        <text class="kpi-value">{{ kpi.monthOutput }}</text>
        <text class="kpi-label">本月产量</text>
        <text class="kpi-unit">件</text>
      </view>
      <view class="kpi-card revenue">
        <text class="kpi-value">{{ kpi.monthRevenue }}</text>
        <text class="kpi-label">本月产值</text>
        <text class="kpi-unit">万元</text>
      </view>
      <view class="kpi-card quality">
        <text class="kpi-value">{{ kpi.qualityRate }}</text>
        <text class="kpi-label">合格率</text>
        <text class="kpi-unit">%</text>
      </view>
      <view class="kpi-card delivery">
        <text class="kpi-value">{{ kpi.onTimeRate }}</text>
        <text class="kpi-label">准时交付率</text>
        <text class="kpi-unit">%</text>
      </view>
    </view>

    <view class="section">
      <view class="section-header">
        <text class="section-title">生产进度概览</text>
        <text class="more" @click="goTo('/pages/boss/dashboard')">查看看板</text>
      </view>
      <view v-if="productionProgress.length" class="progress-list">
        <view v-for="item in productionProgress" :key="item.id" class="progress-card">
          <view class="progress-top">
            <text class="order-no">{{ item.orderNo }}</text>
            <text class="delay-tag" v-if="item.delayed">延期</text>
          </view>
          <view class="progress-bar-wrap">
            <view class="progress-bar" :style="{ width: item.percent + '%' }" :class="{ done: item.percent === 100, delayed: item.delayed }" />
          </view>
          <view class="progress-bottom">
            <text class="progress-text">{{ item.customer }} | {{ item.styleNo }}</text>
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
        <text class="section-title">预警信息</text>
        <text class="more" @click="goTo('/pages/boss/alert')">查看全部</text>
      </view>
      <view v-if="alertList.length" class="alert-list">
        <view v-for="item in alertList" :key="item.id" class="alert-item">
          <view class="alert-dot" :class="'level-' + item.level" />
          <view class="alert-info">
            <text class="alert-title">{{ item.title }}</text>
            <text class="alert-time">{{ item.time }}</text>
          </view>
        </view>
      </view>
      <view v-else class="empty-state">
        <text class="empty-text">暂无预警信息</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { getKpi, getProductionProgress, getAlerts } from '@/api/boss'

const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)

const kpi = ref({
  monthOutput: 8500,
  monthRevenue: 425,
  qualityRate: 97.2,
  onTimeRate: 94.5
})

const productionProgress = ref([
  { id: 1, orderNo: 'PO2026032901', customer: '优衣库', styleNo: 'A001', percent: 60, delayed: false },
  { id: 2, orderNo: 'PO2026032902', customer: 'ZARA', styleNo: 'B002', percent: 53, delayed: true },
  { id: 3, orderNo: 'PO2026032801', customer: 'H&M', styleNo: 'G007', percent: 80, delayed: false },
  { id: 4, orderNo: 'PO2026032501', customer: 'Nike', styleNo: 'E005', percent: 33, delayed: true },
  { id: 5, orderNo: 'PO2026032703', customer: '优衣库', styleNo: 'C003', percent: 100, delayed: false }
])

const alertList = ref([
  { id: 1, title: '订单PO2026032501已延期2天', level: 'high', time: '今天 10:30' },
  { id: 2, title: '订单PO2026032902交期临近', level: 'medium', time: '今天 09:00' },
  { id: 3, title: '物料库存预警: 面料B002库存不足', level: 'medium', time: '昨天 16:00' },
  { id: 4, title: '合格率低于目标值', level: 'low', time: '昨天 18:00' }
])

onShow(() => { userInfo.value = userStore.userInfo })

const goTo = (url: string) => { uni.navigateTo({ url }) }
</script>

<style scoped>
.container { padding: 20rpx; background: #f5f5f5; min-height: 100vh; }
.user-card {
  display: flex; align-items: center; gap: 20rpx;
  background: linear-gradient(135deg, #cf1322, #a8071a);
  border-radius: 16rpx; padding: 30rpx; margin-bottom: 20rpx;
}
.avatar image { width: 80rpx; height: 80rpx; border-radius: 50%; }
.info { display: flex; flex-direction: column; gap: 6rpx; }
.name { font-size: 32rpx; font-weight: 600; color: #fff; }
.dept { font-size: 24rpx; color: rgba(255,255,255,0.8); }
.kpi-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16rpx; margin-bottom: 20rpx; }
.kpi-card { border-radius: 16rpx; padding: 24rpx; position: relative; overflow: hidden; }
.kpi-card.output { background: linear-gradient(135deg, #1890ff, #096dd9); }
.kpi-card.revenue { background: linear-gradient(135deg, #faad14, #d48806); }
.kpi-card.quality { background: linear-gradient(135deg, #52c41a, #389e0d); }
.kpi-card.delivery { background: linear-gradient(135deg, #722ed1, #531dab); }
.kpi-value { display: block; font-size: 48rpx; font-weight: 700; color: #fff; }
.kpi-label { display: block; font-size: 24rpx; color: rgba(255,255,255,0.8); margin-top: 8rpx; }
.kpi-unit { position: absolute; top: 24rpx; right: 24rpx; font-size: 24rpx; color: rgba(255,255,255,0.6); }
.section { background: #fff; border-radius: 16rpx; padding: 30rpx; margin-bottom: 20rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05); }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20rpx; }
.section-title { font-size: 30rpx; font-weight: 600; color: #333; }
.more { font-size: 26rpx; color: #cf1322; }
.progress-list { display: flex; flex-direction: column; gap: 16rpx; }
.progress-card { background: #fafafa; border-radius: 12rpx; padding: 20rpx; }
.progress-top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12rpx; }
.order-no { font-size: 28rpx; font-weight: 600; color: #333; }
.delay-tag { font-size: 22rpx; color: #ff4d4f; background: rgba(255,77,79,0.1); padding: 2rpx 12rpx; border-radius: 8rpx; }
.progress-bar-wrap { height: 14rpx; background: #e8e8e8; border-radius: 7rpx; overflow: hidden; margin-bottom: 10rpx; }
.progress-bar { height: 100%; background: linear-gradient(90deg, #1890ff, #36cfc9); border-radius: 7rpx; transition: width 0.3s; }
.progress-bar.done { background: linear-gradient(90deg, #52c41a, #73d13d); }
.progress-bar.delayed { background: linear-gradient(90deg, #ff4d4f, #ff7875); }
.progress-bottom { display: flex; justify-content: space-between; }
.progress-text { font-size: 24rpx; color: #999; }
.percent-text { font-size: 24rpx; color: #1890ff; font-weight: 600; }
.alert-list { display: flex; flex-direction: column; gap: 16rpx; }
.alert-item { display: flex; align-items: center; gap: 16rpx; padding: 16rpx 20rpx; background: #fafafa; border-radius: 12rpx; }
.alert-dot { width: 16rpx; height: 16rpx; border-radius: 50%; flex-shrink: 0; }
.alert-dot.level-high { background: #ff4d4f; }
.alert-dot.level-medium { background: #faad14; }
.alert-dot.level-low { background: #1890ff; }
.alert-info { flex: 1; }
.alert-title { display: block; font-size: 26rpx; color: #333; }
.alert-time { display: block; font-size: 22rpx; color: #ccc; margin-top: 4rpx; }
.empty-state { padding: 40rpx; text-align: center; }
.empty-text { font-size: 26rpx; color: #ccc; }
</style>
