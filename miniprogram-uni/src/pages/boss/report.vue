<template>
  <view class="container">
    <view class="type-filter">
      <view
        v-for="tab in typeTabs"
        :key="tab.value"
        class="type-item"
        :class="{ active: currentType === tab.value }"
        @click="currentType = tab.value"
      >
        <text>{{ tab.icon }}</text>
        <text>{{ tab.label }}</text>
      </view>
    </view>

    <view v-if="filteredReports.length" class="report-list">
      <view v-for="item in filteredReports" :key="item.id" class="report-card" @click="viewDetail(item)">
        <view class="report-header">
          <view class="report-icon-wrap">
            <text class="report-icon">{{ item.icon }}</text>
          </view>
          <view class="report-info">
            <text class="report-title">{{ item.title }}</text>
            <text class="report-period">{{ item.period }}</text>
          </view>
        </view>
        <view class="report-body">
          <view class="data-row" v-for="(row, idx) in item.dataRows" :key="idx">
            <text class="data-label">{{ row.label }}</text>
            <text class="data-value" :class="row.trend">{{ row.value }}</text>
          </view>
        </view>
        <view class="report-footer">
          <text class="report-time">生成时间: {{ item.createdAt }}</text>
          <text class="report-arrow">查看详情 ›</text>
        </view>
      </view>
    </view>
    <view v-else class="empty-state">
      <text class="empty-text">暂无报表数据</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getReports, getReportDetail } from '@/api/boss'

const typeTabs = [
  { label: '全部', value: 'all', icon: '📋' },
  { label: '经营', value: 'business', icon: '💰' },
  { label: '生产', value: 'production', icon: '🏭' },
  { label: '质量', value: 'quality', icon: '✅' },
  { label: '效率', value: 'efficiency', icon: '⚡' }
]

const currentType = ref('all')

const reportData = ref([
  {
    id: 1, type: 'business', icon: '💰', title: '月度经营报表', period: '2026年3月',
    createdAt: '2026-03-30 08:00',
    dataRows: [
      { label: '月产值', value: '425万元', trend: 'up' },
      { label: '月产量', value: '8500件', trend: 'up' },
      { label: '利润率', value: '18.5%', trend: 'up' },
      { label: '订单完成率', value: '92%', trend: 'down' }
    ]
  },
  {
    id: 2, type: 'production', icon: '🏭', title: '生产月报', period: '2026年3月',
    createdAt: '2026-03-30 08:00',
    dataRows: [
      { label: '总产量', value: '8500件', trend: 'up' },
      { label: '目标完成率', value: '92%', trend: 'down' },
      { label: '在制订单', value: '8单', trend: '' },
      { label: '平均生产周期', value: '12天', trend: 'up' }
    ]
  },
  {
    id: 3, type: 'quality', icon: '✅', title: '质量月报', period: '2026年3月',
    createdAt: '2026-03-30 08:00',
    dataRows: [
      { label: '合格率', value: '97.2%', trend: 'up' },
      { label: '返工率', value: '2.8%', trend: 'down' },
      { label: '客户投诉', value: '2次', trend: '' },
      { label: '主要不良', value: '跳线35%', trend: '' }
    ]
  },
  {
    id: 4, type: 'efficiency', icon: '⚡', title: '效率月报', period: '2026年3月',
    createdAt: '2026-03-30 08:00',
    dataRows: [
      { label: '人均产出', value: '340件/人', trend: 'up' },
      { label: '设备利用率', value: '89%', trend: 'up' },
      { label: '材料利用率', value: '95.5%', trend: 'up' },
      { label: '加班工时', value: '120小时', trend: 'down' }
    ]
  },
  {
    id: 5, type: 'business', icon: '💰', title: '季度经营报表', period: '2026年Q1',
    createdAt: '2026-04-01 08:00',
    dataRows: [
      { label: '季度产值', value: '1250万元', trend: 'up' },
      { label: '季度产量', value: '25000件', trend: 'up' },
      { label: '平均利润率', value: '17.8%', trend: 'up' },
      { label: '客户满意度', value: '96.5%', trend: 'up' }
    ]
  }
])

const filteredReports = computed(() => {
  if (currentType.value === 'all') return reportData.value
  return reportData.value.filter(r => r.type === currentType.value)
})

const viewDetail = (item: any) => {
  uni.navigateTo({
    url: `/pages/boss/report-detail?data=${encodeURIComponent(JSON.stringify(item))}`
  })
}
</script>

<style scoped>
.container { padding: 20rpx; background: #f5f5f5; min-height: 100vh; }
.type-filter { display: flex; gap: 12rpx; margin-bottom: 20rpx; background: #fff; border-radius: 16rpx; padding: 16rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05); overflow-x: auto; }
.type-item {
  flex: 1; display: flex; flex-direction: column; align-items: center; gap: 4rpx;
  padding: 12rpx 8rpx; border-radius: 12rpx; min-width: 0;
}
.type-item text:first-child { font-size: 36rpx; }
.type-item text:last-child { font-size: 22rpx; color: #666; }
.type-item.active { background: #cf1322; }
.type-item.active text { color: #fff; }
.report-list { display: flex; flex-direction: column; gap: 16rpx; }
.report-card { background: #fff; border-radius: 16rpx; padding: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05); }
.report-header { display: flex; align-items: center; gap: 16rpx; margin-bottom: 20rpx; }
.report-icon-wrap {
  width: 72rpx; height: 72rpx; border-radius: 16rpx; background: rgba(207,19,34,0.08);
  display: flex; align-items: center; justify-content: center;
}
.report-icon { font-size: 36rpx; }
.report-info { flex: 1; display: flex; flex-direction: column; gap: 4rpx; }
.report-title { font-size: 30rpx; font-weight: 600; color: #333; }
.report-period { font-size: 24rpx; color: #999; }
.report-body { display: grid; grid-template-columns: 1fr 1fr; gap: 16rpx; margin-bottom: 20rpx; padding: 16rpx 0; border-top: 1rpx solid #f0f0f0; border-bottom: 1rpx solid #f0f0f0; }
.data-row { display: flex; justify-content: space-between; }
.data-label { font-size: 24rpx; color: #999; }
.data-value { font-size: 26rpx; color: #333; font-weight: 600; }
.data-value.up { color: #52c41a; }
.data-value.down { color: #ff4d4f; }
.report-footer { display: flex; justify-content: space-between; align-items: center; }
.report-time { font-size: 22rpx; color: #ccc; }
.report-arrow { font-size: 24rpx; color: #cf1322; }
.empty-state { padding: 80rpx; text-align: center; }
.empty-text { font-size: 26rpx; color: #ccc; }
</style>
