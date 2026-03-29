<template>
  <view class="container">
    <view class="header">
      <text class="title">{{ displayMonth }} 工资明细</text>
      <text class="total">合计: ¥{{ totalAmount }}</text>
    </view>
    <view class="detail-list">
      <view class="date-group" v-for="group in groupedRecords" :key="group.date">
        <view class="date-header">
          <text class="date-text">{{ group.date }}</text>
          <text class="date-total">+¥{{ group.total }}</text>
        </view>
        <view class="record-item" v-for="item in group.records" :key="item.id">
          <view class="record-left">
            <text class="bundle-no">{{ item.bundleNo }}</text>
            <text class="process-name">{{ item.processName }}</text>
          </view>
          <view class="record-right">
            <text class="quantity">×{{ item.quantity }}</text>
            <text class="amount">¥{{ item.amount }}</text>
          </view>
        </view>
      </view>
      <view v-if="groupedRecords.length === 0" class="empty">暂无明细记录</view>
    </view>
    <view class="load-more" v-if="hasMore" @click="loadMore">
      <text>{{ loading ? '加载中...' : '加载更多' }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getWageDetail } from '@/api/worker'

const month = ref('')
const displayMonth = computed(() => {
  if (!month.value) return ''
  const [y, m] = month.value.split('-')
  return `${y}年${parseInt(m)}月`
})

const records = ref<any[]>([])
const page = ref(1)
const pageSize = 20
const hasMore = ref(true)
const loading = ref(false)

const totalAmount = computed(() => {
  return records.value.reduce((sum, item) => sum + (item.amount || 0), 0).toFixed(2)
})

const groupedRecords = computed(() => {
  const map = new Map<string, any[]>()
  for (const item of records.value) {
    const date = item.date || item.createTime?.slice(0, 10) || '未知日期'
    if (!map.has(date)) map.set(date, [])
    map.get(date)!.push(item)
  }
  return Array.from(map.entries()).map(([date, recs]) => ({
    date,
    records: recs,
    total: recs.reduce((s: number, r: any) => s + (r.amount || 0), 0).toFixed(2)
  }))
})

onLoad((options: any) => {
  month.value = options?.month || ''
  page.value = 1
  records.value = []
  hasMore.value = true
  loadRecords()
})

const loadRecords = async () => {
  if (loading.value) return
  loading.value = true
  try {
    const res: any = await getWageDetail({ month: month.value, page: page.value, pageSize })
    if (res.success) {
      const list = res.data?.list || res.data || []
      records.value = page.value === 1 ? list : [...records.value, ...list]
      hasMore.value = list.length >= pageSize
    }
  } catch {
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  if (!hasMore.value || loading.value) return
  page.value++
  loadRecords()
}
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  margin-bottom: 20rpx;
}
.title { font-size: 34rpx; font-weight: 600; color: #333; }
.total { font-size: 30rpx; font-weight: 600; color: #52c41a; }
.detail-list { display: flex; flex-direction: column; gap: 24rpx; }
.date-group {
  background: #fff;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.date-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 24rpx;
  background: #fafafa;
  border-bottom: 1rpx solid #f0f0f0;
}
.date-text { font-size: 28rpx; font-weight: 600; color: #333; }
.date-total { font-size: 28rpx; color: #52c41a; font-weight: 600; }
.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 24rpx;
  border-bottom: 1rpx solid #f5f5f5;
}
.record-item:last-child { border-bottom: none; }
.record-left { display: flex; flex-direction: column; gap: 4rpx; }
.bundle-no { font-size: 28rpx; color: #333; }
.process-name { font-size: 24rpx; color: #999; }
.record-right { display: flex; gap: 20rpx; }
.quantity { font-size: 28rpx; color: #666; }
.amount { font-size: 28rpx; color: #52c41a; font-weight: 600; }
.empty { text-align: center; color: #999; padding: 80rpx; font-size: 28rpx; }
.load-more { text-align: center; padding: 30rpx; color: #1890ff; font-size: 28rpx; }
</style>
