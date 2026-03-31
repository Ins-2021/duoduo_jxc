<template>
  <view class="container">
    <view class="report-header">
      <view class="report-icon-wrap">
        <text class="report-icon">{{ report?.icon }}</text>
      </view>
      <view class="report-info">
        <text class="report-title">{{ report?.title }}</text>
        <text class="report-period">{{ report?.period }}</text>
      </view>
    </view>

    <view class="summary-section">
      <text class="summary-text">{{ report?.summary }}</text>
    </view>

    <view class="data-section">
      <view class="section-title">关键指标</view>
      <view class="metric-grid">
        <view class="metric-item" v-for="(item, idx) in metrics" :key="idx">
          <text class="metric-label">{{ item.label }}</text>
          <text class="metric-value">{{ item.value }}</text>
          <text class="metric-change" :class="item.trend">{{ item.change || '-' }}</text>
        </view>
      </view>
    </view>

    <view class="detail-section">
      <view class="section-title">详细分析</view>
      <view class="detail-list">
        <view class="detail-item" v-for="(item, idx) in details" :key="idx">
          <view class="detail-header">
            <text class="detail-title">{{ item.title }}</text>
            <text class="detail-value">{{ item.value }}</text>
          </view>
          <view class="detail-progress" v-if="item.progress !== undefined">
            <view class="progress-bar">
              <view class="progress-fill" :style="{ width: item.progress + '%' }"></view>
            </view>
            <text class="progress-text">{{ item.progress }}%</text>
          </view>
          <text class="detail-desc">{{ item.description }}</text>
        </view>
      </view>
    </view>

    <view class="action-section">
      <view class="section-title">建议措施</view>
      <view class="action-list">
        <view class="action-item" v-for="(item, idx) in actions" :key="idx">
          <text class="action-num">{{ idx + 1 }}</text>
          <text class="action-text">{{ item }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const report = ref<any>(null)
const metrics = ref<any[]>([])
const details = ref<any[]>([])
const actions = ref<string[]>([])

onLoad((options: any) => {
  if (options.data) {
    try {
      const data = JSON.parse(decodeURIComponent(options.data))
      report.value = data
      generateDetailData(data)
    } catch (e) {
      console.error('Failed to parse report data:', e)
    }
  }
})

const generateDetailData = (reportData: any) => {
  metrics.value = [
    { label: '完成数量', value: '350件', change: '↑ 12%', trend: 'up' },
    { label: '合格率', value: '97.5%', change: '↑ 2.1%', trend: 'up' },
    { label: '平均效率', value: '89%', change: '↑ 5%', trend: 'up' },
    { label: '目标达成', value: '87.5%', change: '↓ 2.5%', trend: 'down' }
  ]

  details.value = [
    { title: '产量分析', value: '350件', progress: 87.5, description: '今日产量较昨日增加35件，主要得益于生产线优化' },
    { title: '质量分析', value: '97.5%', progress: 97.5, description: '合格率保持在较高水平，返工率控制在3%以内' },
    { title: '效率分析', value: '89%', progress: 89, description: '平均效率较上周提升5%，平缝工序表现最佳' }
  ]

  actions.value = [
    '继续保持当前生产节奏，确保按时完成订单',
    '关注返工工序，分析不良原因并改进',
    '加强首件确认流程，减少批量不良',
    '优化人员排班，提高高峰时段产能'
  ]
}
</script>

<style scoped>
.container { padding: 20rpx; background: #f5f5f5; min-height: 100vh; }
.report-header { display: flex; align-items: center; gap: 16rpx; background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 16rpx; }
.report-icon-wrap { width: 80rpx; height: 80rpx; border-radius: 16rpx; background: rgba(114,46,209,0.1); display: flex; align-items: center; justify-content: center; }
.report-icon { font-size: 40rpx; }
.report-info { flex: 1; display: flex; flex-direction: column; gap: 4rpx; }
.report-title { font-size: 32rpx; font-weight: 600; color: #333; }
.report-period { font-size: 24rpx; color: #999; }
.summary-section { background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 16rpx; }
.summary-text { font-size: 28rpx; color: #666; line-height: 1.6; }
.section-title { font-size: 28rpx; font-weight: 600; color: #333; margin-bottom: 16rpx; padding-left: 16rpx; border-left: 4rpx solid #722ed1; }
.data-section, .detail-section, .action-section { background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 16rpx; }
.metric-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16rpx; }
.metric-item { background: #fafafa; border-radius: 12rpx; padding: 20rpx; text-align: center; }
.metric-label { display: block; font-size: 24rpx; color: #999; margin-bottom: 8rpx; }
.metric-value { display: block; font-size: 32rpx; font-weight: 600; color: #333; margin-bottom: 4rpx; }
.metric-change { display: block; font-size: 22rpx; }
.metric-change.up { color: #52c41a; }
.metric-change.down { color: #ff4d4f; }
.detail-list { display: flex; flex-direction: column; gap: 16rpx; }
.detail-item { padding: 16rpx; background: #fafafa; border-radius: 12rpx; }
.detail-header { display: flex; justify-content: space-between; margin-bottom: 12rpx; }
.detail-title { font-size: 26rpx; color: #666; }
.detail-value { font-size: 26rpx; font-weight: 600; color: #333; }
.detail-progress { display: flex; align-items: center; gap: 12rpx; margin-bottom: 8rpx; }
.progress-bar { flex: 1; height: 8rpx; background: #e8e8e8; border-radius: 4rpx; overflow: hidden; }
.progress-fill { height: 100%; background: #722ed1; border-radius: 4rpx; }
.progress-text { font-size: 22rpx; color: #722ed1; font-weight: 600; }
.detail-desc { font-size: 24rpx; color: #999; line-height: 1.5; }
.action-list { display: flex; flex-direction: column; gap: 12rpx; }
.action-item { display: flex; align-items: flex-start; gap: 12rpx; }
.action-num { width: 36rpx; height: 36rpx; border-radius: 50%; background: #722ed1; color: #fff; font-size: 22rpx; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.action-text { font-size: 26rpx; color: #666; line-height: 1.5; }
</style>
