<template>
  <view class="container">
    <view class="filter-section">
      <view class="filter-row">
        <picker mode="date" :value="dateRange.start" @change="(e: any) => dateRange.start = e.detail.value">
          <view class="picker-item">{{ dateRange.start || '开始日期' }}</view>
        </picker>
        <text class="filter-sep">至</text>
        <picker mode="date" :value="dateRange.end" @change="(e: any) => dateRange.end = e.detail.value">
          <view class="picker-item">{{ dateRange.end || '结束日期' }}</view>
        </picker>
      </view>
      <view class="result-filter">
        <view
          v-for="r in resultOptions" :key="r.value"
          class="filter-tag"
          :class="{ active: currentResult === r.value }"
          @click="currentResult = r.value"
        >{{ r.label }}</view>
      </view>
    </view>

    <view v-if="list.length > 0" class="list-section">
      <view v-for="item in list" :key="item.id" class="list-item">
        <view class="item-header">
          <text class="bundle-no">{{ item.bundleNo }}</text>
          <text class="result-tag" :class="getResultClass(item.result)">{{ getResultText(item.result) }}</text>
        </view>
        <view class="item-info">
          <text class="info-text">款号：{{ item.styleNo }}</text>
          <text class="info-text">颜色尺码：{{ item.color }} / {{ item.size }}</text>
        </view>
        <view class="item-stats">
          <text>检验数量：{{ item.checkQuantity }}</text>
          <text>合格数量：{{ item.passQuantity }}</text>
        </view>
        <view class="item-footer">
          <text class="time">{{ item.checkTime }}</text>
          <text class="inspector">质检员：{{ item.inspectorName }}</text>
        </view>
      </view>
    </view>

    <view v-if="!loading && list.length === 0" class="empty-state">
      <text>暂无质检记录</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getCheckHistory } from '@/api/inspector'

const list = ref<any[]>([])
const loading = ref(false)

const today = new Date().toISOString().split('T')[0]
const dateRange = reactive({ start: today, end: today })
const currentResult = ref('all')

const resultOptions = [
  { label: '全部', value: 'all' },
  { label: '合格', value: 'pass' },
  { label: '返工', value: 'rework' },
  { label: '报废', value: 'scrap' }
]

const getResultText = (result: string) => {
  const map: Record<string, string> = { pass: '合格', rework: '返工', scrap: '报废' }
  return map[result] || result
}

const getResultClass = (result: string) => {
  const map: Record<string, string> = { pass: 'res-pass', rework: 'res-rework', scrap: 'res-scrap' }
  return map[result] || ''
}

const loadList = async () => {
  loading.value = true
  try {
    const params: any = { startDate: dateRange.start, endDate: dateRange.end }
    if (currentResult.value !== 'all') params.result = currentResult.value
    const res = await getCheckHistory(params)
    list.value = res.data || []
  } catch {} finally {
    loading.value = false
  }
}

watch([() => dateRange.start, () => dateRange.end, currentResult], () => { loadList() })

onShow(() => { loadList() })
</script>

<style scoped>
.container { padding: 24rpx; }
.filter-section {
  background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.filter-row { display: flex; align-items: center; gap: 16rpx; margin-bottom: 20rpx; }
.picker-item {
  flex: 1; text-align: center; font-size: 26rpx; padding: 16rpx;
  background: #f5f5f5; border-radius: 8rpx; color: #333;
}
.filter-sep { font-size: 26rpx; color: #999; }
.result-filter { display: flex; gap: 16rpx; flex-wrap: wrap; }
.filter-tag {
  font-size: 26rpx; padding: 8rpx 24rpx; border-radius: 24rpx;
  background: #f5f5f5; color: #666;
}
.filter-tag.active { background: #1890ff; color: #fff; }
.list-section { display: flex; flex-direction: column; gap: 20rpx; }
.list-item {
  background: #fff; border-radius: 16rpx; padding: 28rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.item-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; }
.bundle-no { font-size: 30rpx; font-weight: 600; color: #333; }
.result-tag { font-size: 22rpx; padding: 4rpx 16rpx; border-radius: 6rpx; color: #fff; }
.res-pass { background: #52c41a; }
.res-rework { background: #faad14; }
.res-scrap { background: #ff4d4f; }
.item-info { display: flex; flex-direction: column; gap: 8rpx; margin-bottom: 12rpx; }
.info-text { font-size: 26rpx; color: #666; }
.item-stats { display: flex; gap: 32rpx; font-size: 26rpx; color: #999; margin-bottom: 12rpx; }
.item-footer { display: flex; justify-content: space-between; align-items: center; }
.time { font-size: 24rpx; color: #999; }
.inspector { font-size: 24rpx; color: #999; }
.empty-state { text-align: center; padding: 100rpx 0; color: #999; font-size: 28rpx; }
</style>
