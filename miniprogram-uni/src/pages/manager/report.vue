<template>
  <view class="container">
    <view class="filter-bar">
      <view
        v-for="tab in typeTabs"
        :key="tab.value"
        class="filter-item"
        :class="{ active: currentType === tab.value }"
        @click="currentType = tab.value"
      >
        <text>{{ tab.label }}</text>
      </view>
    </view>

    <view v-if="filteredReports.length" class="report-list">
      <view v-for="item in filteredReports" :key="item.id" class="report-card" @click="viewDetail(item)">
        <view class="report-header">
          <view class="report-type-icon">{{ item.icon }}</view>
          <view class="report-info">
            <text class="report-title">{{ item.title }}</text>
            <text class="report-period">{{ item.period }}</text>
          </view>
          <text class="report-arrow">›</text>
        </view>
        <view class="report-summary">
          <text>{{ item.summary }}</text>
        </view>
        <view class="report-meta">
          <text>生成时间: {{ item.createdAt }}</text>
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
import { getReports } from '@/api/manager'

const typeTabs = [
  { label: '全部', value: 'all' },
  { label: '生产报表', value: 'production' },
  { label: '质量报表', value: 'quality' },
  { label: '效率报表', value: 'efficiency' }
]

const currentType = ref('all')

const reportData = ref([
  { id: 1, type: 'production', icon: '📊', title: '生产日报', period: '2026-03-29', summary: '今日产量350件，完成率87.5%，合格率97.5%', createdAt: '2026-03-29 18:00' },
  { id: 2, type: 'production', icon: '📊', title: '生产周报', period: '2026-03-23 ~ 2026-03-29', summary: '本周产量2100件，较上周增长12%', createdAt: '2026-03-29 18:00' },
  { id: 3, type: 'production', icon: '📊', title: '生产月报', period: '2026年3月', summary: '本月产量8500件，目标完成率92%', createdAt: '2026-03-30 08:00' },
  { id: 4, type: 'quality', icon: '✅', title: '质量周报', period: '2026-03-23 ~ 2026-03-29', summary: '本周合格率97.5%，主要不良: 跳线(35%)、毛边(28%)', createdAt: '2026-03-29 18:00' },
  { id: 5, type: 'quality', icon: '✅', title: '质量月报', period: '2026年3月', summary: '本月合格率97.2%，返工率2.8%', createdAt: '2026-03-30 08:00' },
  { id: 6, type: 'efficiency', icon: '⚡', title: '效率周报', period: '2026-03-23 ~ 2026-03-29', summary: '平均效率89%，最高工序: 平缝(95%)', createdAt: '2026-03-29 18:00' },
  { id: 7, type: 'efficiency', icon: '⚡', title: '效率月报', period: '2026年3月', summary: '月均效率87%，同比提升5%', createdAt: '2026-03-30 08:00' }
])

const filteredReports = computed(() => {
  if (currentType.value === 'all') return reportData.value
  return reportData.value.filter(r => r.type === currentType.value)
})

const viewDetail = (item: any) => {
  uni.navigateTo({
    url: `/pages/manager/report-detail?data=${encodeURIComponent(JSON.stringify(item))}`
  })
}
</script>

<style scoped>
.container { padding: 20rpx; background: #f5f5f5; min-height: 100vh; }
.filter-bar { display: flex; gap: 16rpx; margin-bottom: 20rpx; background: #fff; border-radius: 16rpx; padding: 16rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05); }
.filter-item { flex: 1; text-align: center; padding: 16rpx 0; border-radius: 12rpx; font-size: 26rpx; color: #666; }
.filter-item.active { background: #722ed1; color: #fff; font-weight: 600; }
.report-list { display: flex; flex-direction: column; gap: 16rpx; }
.report-card { background: #fff; border-radius: 16rpx; padding: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05); }
.report-header { display: flex; align-items: center; gap: 16rpx; margin-bottom: 12rpx; }
.report-type-icon { font-size: 44rpx; }
.report-info { flex: 1; display: flex; flex-direction: column; gap: 4rpx; }
.report-title { font-size: 30rpx; font-weight: 600; color: #333; }
.report-period { font-size: 24rpx; color: #999; }
.report-arrow { font-size: 40rpx; color: #ccc; }
.report-summary { font-size: 26rpx; color: #666; margin-bottom: 12rpx; line-height: 1.6; }
.report-meta { font-size: 22rpx; color: #ccc; }
.empty-state { padding: 80rpx; text-align: center; }
.empty-text { font-size: 26rpx; color: #ccc; }
</style>
