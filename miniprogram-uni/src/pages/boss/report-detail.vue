<template>
  <view class="container">
    <view class="report-header">
      <view class="report-title-wrap">
        <text class="report-icon">{{ report?.icon }}</text>
        <view class="report-info">
          <text class="report-title">{{ report?.title }}</text>
          <text class="report-period">{{ report?.period }}</text>
        </view>
      </view>
      <view class="report-time">生成时间: {{ report?.createdAt }}</view>
    </view>

    <view class="chart-section" v-if="chartData.length">
      <view class="section-title">趋势图表</view>
      <view class="chart-placeholder">
        <qiun-ux-canvas v-if="chartData.length" :canvasId="'chart'" :type="'line'" :opts="chartOpts" :chartData="chartDataObj" />
      </view>
    </view>

    <view class="data-section">
      <view class="section-title">详细数据</view>
      <view class="data-table">
        <view class="table-header">
          <text class="table-cell">指标名称</text>
          <text class="table-cell">数值</text>
          <text class="table-cell">环比</text>
        </view>
        <view class="table-row" v-for="(row, idx) in dataTable" :key="idx">
          <text class="table-cell">{{ row.label }}</text>
          <text class="table-cell">{{ row.value }}</text>
          <text class="table-cell" :class="row.trend">{{ row.change || '-' }}</text>
        </view>
      </view>
    </view>

    <view class="insight-section" v-if="insights.length">
      <view class="section-title">数据洞察</view>
      <view class="insight-list">
        <view class="insight-item" v-for="(item, idx) in insights" :key="idx">
          <text class="insight-icon">{{ item.type === 'up' ? '📈' : item.type === 'down' ? '📉' : '💡' }}</text>
          <text class="insight-text">{{ item.text }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const report = ref<any>(null)
const chartData = ref<any[]>([])
const dataTable = ref<any[]>([])
const insights = ref<any[]>([])

const chartOpts = ref({
  color: ['#cf1322', '#52c41a', '#1890ff'],
  padding: [15, 10, 0, 15],
  legend: {},
  xAxis: { disableGrid: true },
  yAxis: { data: [{ min: 0 }] },
  extra: { line: { type: 'curve', width: 2, activeType: 'hollow' } }
})

const chartDataObj = computed(() => ({
  categories: chartData.value.map(d => d.label),
  series: [{ name: '数值', data: chartData.value.map(d => d.value) }]
}))

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
  if (reportData.dataRows) {
    dataTable.value = reportData.dataRows.map((row: any) => ({
      label: row.label,
      value: row.value,
      change: row.trend === 'up' ? '↑ 上升' : row.trend === 'down' ? '↓ 下降' : '-',
      trend: row.trend
    }))
  }

  chartData.value = [
    { label: '第1周', value: Math.floor(Math.random() * 100 + 50) },
    { label: '第2周', value: Math.floor(Math.random() * 100 + 50) },
    { label: '第3周', value: Math.floor(Math.random() * 100 + 50) },
    { label: '第4周', value: Math.floor(Math.random() * 100 + 50) }
  ]

  insights.value = [
    { type: 'up', text: '本月整体表现良好，核心指标均有所提升' },
    { type: 'down', text: '部分指标存在波动，建议关注细节优化' },
    { type: 'info', text: '建议保持当前策略，持续跟踪关键指标' }
  ]
}
</script>

<style scoped>
.container { padding: 20rpx; background: #f5f5f5; min-height: 100vh; }
.report-header { background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 16rpx; }
.report-title-wrap { display: flex; align-items: center; gap: 16rpx; margin-bottom: 12rpx; }
.report-icon { font-size: 48rpx; }
.report-info { display: flex; flex-direction: column; gap: 4rpx; }
.report-title { font-size: 32rpx; font-weight: 600; color: #333; }
.report-period { font-size: 24rpx; color: #999; }
.report-time { font-size: 22rpx; color: #ccc; }
.section-title { font-size: 28rpx; font-weight: 600; color: #333; margin-bottom: 16rpx; padding-left: 16rpx; border-left: 4rpx solid #cf1322; }
.chart-section, .data-section, .insight-section { background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 16rpx; }
.chart-placeholder { height: 400rpx; display: flex; align-items: center; justify-content: center; background: #fafafa; border-radius: 12rpx; }
.data-table { border: 1rpx solid #f0f0f0; border-radius: 12rpx; overflow: hidden; }
.table-header { display: flex; background: #fafafa; padding: 16rpx 0; border-bottom: 1rpx solid #f0f0f0; }
.table-row { display: flex; padding: 16rpx 0; border-bottom: 1rpx solid #f0f0f0; }
.table-row:last-child { border-bottom: none; }
.table-cell { flex: 1; text-align: center; font-size: 26rpx; color: #333; }
.table-header .table-cell { font-weight: 600; color: #666; }
.table-cell.up { color: #52c41a; }
.table-cell.down { color: #ff4d4f; }
.insight-list { display: flex; flex-direction: column; gap: 12rpx; }
.insight-item { display: flex; align-items: center; gap: 12rpx; padding: 16rpx; background: #fafafa; border-radius: 12rpx; }
.insight-icon { font-size: 32rpx; }
.insight-text { font-size: 26rpx; color: #666; line-height: 1.5; }
</style>
