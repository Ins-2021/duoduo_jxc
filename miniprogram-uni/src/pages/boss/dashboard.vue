<template>
  <view class="container">
    <view class="kpi-grid">
      <view class="kpi-card">
        <text class="kpi-value">{{ kpi.monthOutput }}</text>
        <text class="kpi-label">本月产量</text>
        <text class="kpi-unit">件</text>
        <view class="kpi-trend up">
          <text>↑ 12%</text>
        </view>
      </view>
      <view class="kpi-card">
        <text class="kpi-value">{{ kpi.monthRevenue }}</text>
        <text class="kpi-label">本月产值</text>
        <text class="kpi-unit">万元</text>
        <view class="kpi-trend up">
          <text>↑ 8%</text>
        </view>
      </view>
      <view class="kpi-card">
        <text class="kpi-value">{{ kpi.qualityRate }}</text>
        <text class="kpi-label">合格率</text>
        <text class="kpi-unit">%</text>
        <view class="kpi-trend up">
          <text>↑ 1.2%</text>
        </view>
      </view>
      <view class="kpi-card">
        <text class="kpi-value">{{ kpi.onTimeRate }}</text>
        <text class="kpi-label">准时交付率</text>
        <text class="kpi-unit">%</text>
        <view class="kpi-trend down">
          <text>↓ 2.1%</text>
        </view>
      </view>
    </view>

    <view class="section">
      <text class="section-title">生产进度概览</text>
      <view v-if="productionProgress.length" class="progress-list">
        <view v-for="item in productionProgress" :key="item.id" class="progress-card">
          <view class="progress-top">
            <view class="progress-left">
              <text class="order-no">{{ item.orderNo }}</text>
              <text class="customer">{{ item.customer }} | {{ item.styleNo }}</text>
            </view>
            <view class="delay-tag" :class="{ delayed: item.delayed }">
              <text>{{ item.delayed ? '延期' : '正常' }}</text>
            </view>
          </view>
          <view class="progress-bar-wrap">
            <view class="progress-bar" :style="{ width: item.percent + '%' }" :class="{ done: item.percent === 100, delayed: item.delayed }" />
          </view>
          <view class="progress-bottom">
            <text class="progress-text">完成 {{ item.completed }}/{{ item.total }}件</text>
            <text class="percent-text">{{ item.percent }}%</text>
          </view>
          <view class="deadline-row">
            <text>交期: {{ item.deadline }}</text>
          </view>
        </view>
      </view>
      <view v-else class="empty-state">
        <text class="empty-text">暂无生产数据</text>
      </view>
    </view>

    <view class="section">
      <view class="section-header">
        <text class="section-title">预警信息</text>
        <text class="more" @click="goTo('/pages/boss/alert')">查看全部</text>
      </view>
      <view v-if="alertList.length" class="alert-list">
        <view v-for="item in alertList" :key="item.id" class="alert-card" :class="'alert-' + item.level">
          <view class="alert-header">
            <view class="alert-level-tag" :class="'level-' + item.level">
              <text>{{ item.levelText }}</text>
            </view>
            <text class="alert-time">{{ item.time }}</text>
          </view>
          <text class="alert-title">{{ item.title }}</text>
          <text class="alert-desc">{{ item.description }}</text>
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
import { getDashboard, getKpi, getProductionProgress, getAlerts } from '@/api/boss'

const kpi = ref({
  monthOutput: 8500,
  monthRevenue: 425,
  qualityRate: 97.2,
  onTimeRate: 94.5
})

const productionProgress = ref([
  { id: 1, orderNo: 'PO2026032901', customer: '优衣库', styleNo: 'A001', completed: 120, total: 200, percent: 60, deadline: '2026-04-15', delayed: false },
  { id: 2, orderNo: 'PO2026032902', customer: 'ZARA', styleNo: 'B002', completed: 80, total: 150, percent: 53, deadline: '2026-04-10', delayed: true },
  { id: 3, orderNo: 'PO2026032801', customer: 'H&M', styleNo: 'G007', completed: 240, total: 300, percent: 80, deadline: '2026-04-20', delayed: false },
  { id: 4, orderNo: 'PO2026032501', customer: 'Nike', styleNo: 'E005', completed: 100, total: 300, percent: 33, deadline: '2026-03-28', delayed: true },
  { id: 5, orderNo: 'PO2026032703', customer: '优衣库', styleNo: 'C003', completed: 100, total: 100, percent: 100, deadline: '2026-04-01', delayed: false }
])

const alertList = ref([
  { id: 1, title: '订单PO2026032501已延期2天', description: 'Nike订单E005款，已完成33%，交期已过2天', level: 'high', levelText: '紧急', time: '2026-03-29 10:30' },
  { id: 2, title: '订单PO2026032902交期临近', description: 'ZARA订单B002款，交期仅剩12天，完成率53%', level: 'high', levelText: '紧急', time: '2026-03-29 09:00' },
  { id: 3, title: '物料库存预警', description: '面料B002库存仅剩50米，预计3天内耗尽', level: 'medium', levelText: '重要', time: '2026-03-28 16:00' },
  { id: 4, title: '合格率下降', description: '本周合格率96.8%，低于目标值97%', level: 'medium', levelText: '重要', time: '2026-03-28 18:00' },
  { id: 5, title: '人员出勤率偏低', description: '今日出勤率83.3%，缝制二组2人请假', level: 'low', levelText: '一般', time: '2026-03-29 08:30' }
])

onShow(() => {})

const goTo = (url: string) => { uni.navigateTo({ url }) }
</script>

<style scoped>
.container { padding: 20rpx; background: #f5f5f5; min-height: 100vh; }
.kpi-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16rpx; margin-bottom: 20rpx; }
.kpi-card {
  background: #fff; border-radius: 16rpx; padding: 24rpx; position: relative;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05); overflow: hidden;
}
.kpi-value { display: block; font-size: 44rpx; font-weight: 700; color: #333; }
.kpi-label { display: block; font-size: 24rpx; color: #999; margin-top: 8rpx; }
.kpi-unit { position: absolute; top: 24rpx; right: 24rpx; font-size: 24rpx; color: #ccc; }
.kpi-trend { margin-top: 8rpx; }
.kpi-trend text { font-size: 22rpx; font-weight: 600; }
.kpi-trend.up text { color: #52c41a; }
.kpi-trend.down text { color: #ff4d4f; }
.section { background: #fff; border-radius: 16rpx; padding: 30rpx; margin-bottom: 20rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05); }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20rpx; }
.section-title { font-size: 30rpx; font-weight: 600; color: #333; display: block; margin-bottom: 20rpx; }
.section-header .section-title { margin-bottom: 0; }
.more { font-size: 26rpx; color: #cf1322; }
.progress-list { display: flex; flex-direction: column; gap: 16rpx; }
.progress-card { background: #fafafa; border-radius: 12rpx; padding: 20rpx; }
.progress-top { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12rpx; }
.progress-left { display: flex; flex-direction: column; gap: 4rpx; }
.order-no { font-size: 28rpx; font-weight: 600; color: #333; }
.customer { font-size: 24rpx; color: #999; }
.delay-tag { font-size: 22rpx; padding: 4rpx 16rpx; border-radius: 8rpx; }
.delay-tag:not(.delayed) { color: #52c41a; background: rgba(82,196,26,0.1); }
.delay-tag.delayed { color: #ff4d4f; background: rgba(255,77,79,0.1); }
.progress-bar-wrap { height: 14rpx; background: #e8e8e8; border-radius: 7rpx; overflow: hidden; margin-bottom: 10rpx; }
.progress-bar { height: 100%; background: linear-gradient(90deg, #1890ff, #36cfc9); border-radius: 7rpx; transition: width 0.3s; }
.progress-bar.done { background: linear-gradient(90deg, #52c41a, #73d13d); }
.progress-bar.delayed { background: linear-gradient(90deg, #ff4d4f, #ff7875); }
.progress-bottom { display: flex; justify-content: space-between; margin-bottom: 8rpx; }
.progress-text { font-size: 24rpx; color: #999; }
.percent-text { font-size: 24rpx; color: #1890ff; font-weight: 600; }
.deadline-row { font-size: 22rpx; color: #ccc; }
.alert-list { display: flex; flex-direction: column; gap: 16rpx; }
.alert-card { border-radius: 12rpx; padding: 20rpx; border-left: 6rpx solid; }
.alert-high { background: rgba(255,77,79,0.05); border-left-color: #ff4d4f; }
.alert-medium { background: rgba(250,173,20,0.05); border-left-color: #faad14; }
.alert-low { background: rgba(24,144,255,0.05); border-left-color: #1890ff; }
.alert-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8rpx; }
.alert-level-tag { font-size: 22rpx; padding: 2rpx 12rpx; border-radius: 6rpx; }
.level-high { color: #ff4d4f; background: rgba(255,77,79,0.1); }
.level-medium { color: #faad14; background: rgba(250,173,20,0.1); }
.level-low { color: #1890ff; background: rgba(24,144,255,0.1); }
.alert-time { font-size: 22rpx; color: #ccc; }
.alert-title { display: block; font-size: 28rpx; font-weight: 500; color: #333; margin-bottom: 6rpx; }
.alert-desc { display: block; font-size: 24rpx; color: #999; line-height: 1.5; }
.empty-state { padding: 40rpx; text-align: center; }
.empty-text { font-size: 26rpx; color: #ccc; }
</style>
