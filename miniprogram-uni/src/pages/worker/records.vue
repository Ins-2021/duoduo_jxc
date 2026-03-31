<template>
  <view class="container">
    <view class="header">
      <view class="stats-row">
        <view class="stat-item">
          <text class="stat-value">{{ totalQuantity }}</text>
          <text class="stat-label">今日件数</text>
        </view>
        <view class="stat-item">
          <text class="stat-value income">¥{{ totalAmount }}</text>
          <text class="stat-label">今日收入</text>
        </view>
      </view>
    </view>
    
    <view class="filter-bar">
      <picker mode="date" :value="selectedDate" @change="onDateChange">
        <view class="date-picker">
          <text>{{ formatDate(selectedDate) }}</text>
          <text class="arrow">▼</text>
        </view>
      </picker>
    </view>

    <view class="record-list">
      <view class="record-item" v-for="item in records" :key="item.recordId" @click="showDetail(item)">
        <view class="record-main">
          <view class="record-left">
            <view class="bundle-info">
              <text class="bundle-no">{{ item.bundleNo }}</text>
              <text class="process-tag">{{ item.processName }}</text>
            </view>
            <text class="time">{{ formatTime(item.scanAt) }}</text>
          </view>
          <view class="record-right">
            <text class="quantity">×{{ item.quantity }}</text>
            <text class="amount">¥{{ item.amount || 0 }}</text>
          </view>
        </view>
        <view class="record-status">
          <text :class="['status', item.status]">{{ getStatusText(item.status) }}</text>
        </view>
      </view>
      
      <view v-if="records.length === 0" class="empty">
        <text>暂无计件记录</text>
      </view>
    </view>

    <view class="load-more" v-if="hasMore" @click="loadMore">
      <text>{{ loading ? '加载中...' : '加载更多' }}</text>
    </view>

    <view class="detail-popup" v-if="showDetailPopup" @click="showDetailPopup = false">
      <view class="detail-content" @click.stop>
        <view class="detail-header">
          <text class="detail-title">计件详情</text>
          <text class="close-btn" @click="showDetailPopup = false">×</text>
        </view>
        <view class="detail-body" v-if="currentRecord">
          <view class="detail-row">
            <text class="label">扎包号</text>
            <text class="value">{{ currentRecord.bundleNo }}</text>
          </view>
          <view class="detail-row">
            <text class="label">工序</text>
            <text class="value">{{ currentRecord.processName }}</text>
          </view>
          <view class="detail-row">
            <text class="label">数量</text>
            <text class="value highlight">{{ currentRecord.quantity }} 件</text>
          </view>
          <view class="detail-row">
            <text class="label">单价</text>
            <text class="value">¥{{ currentRecord.price || 0 }}/件</text>
          </view>
          <view class="detail-row">
            <text class="label">金额</text>
            <text class="value income">¥{{ currentRecord.amount || 0 }}</text>
          </view>
          <view class="detail-row">
            <text class="label">扫码时间</text>
            <text class="value">{{ formatTime(currentRecord.scanAt) }}</text>
          </view>
          <view class="detail-row">
            <text class="label">状态</text>
            <text :class="['value', 'status-text', currentRecord.status]">{{ getStatusText(currentRecord.status) }}</text>
          </view>
        </view>
        <view class="detail-footer">
          <view class="action-btn" v-if="currentRecord?.status === 'PENDING'" @click="handleConfirm">
            确认
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getTodayRecords, getScanRecords, confirmRecord } from '@/api/scan'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const records = ref<any[]>([])
const selectedDate = ref(new Date().toISOString().split('T')[0])
const loading = ref(false)
const hasMore = ref(false)
const pageNum = ref(1)
const showDetailPopup = ref(false)
const currentRecord = ref<any>(null)

const totalQuantity = computed(() => {
  return records.value.reduce((sum, item) => sum + (item.quantity || 0), 0)
})

const totalAmount = computed(() => {
  const total = records.value.reduce((sum, item) => sum + (item.amount || 0), 0)
  return total.toFixed(2)
})

onMounted(() => {
  loadRecords()
})

const loadRecords = async () => {
  loading.value = true
  try {
    const workerId = userStore.userInfo?.id
    if (!workerId) return

    const res: any = await getTodayRecords(workerId)
    records.value = res.data || []
  } catch {
    records.value = []
  } finally {
    loading.value = false
  }
}

const loadMore = async () => {
  if (loading.value || !hasMore.value) return
  loading.value = true
  pageNum.value++
  try {
    const workerId = userStore.userInfo?.id
    const res: any = await getScanRecords({
      workerId,
      pageNum: pageNum.value,
      pageSize: 20
    })
    if (res.data?.length > 0) {
      records.value = [...records.value, ...res.data]
    } else {
      hasMore.value = false
    }
  } finally {
    loading.value = false
  }
}

const onDateChange = (e: any) => {
  selectedDate.value = e.detail.value
  pageNum.value = 1
  loadRecords()
}

const showDetail = (item: any) => {
  currentRecord.value = item
  showDetailPopup.value = true
}

const handleConfirm = async () => {
  if (!currentRecord.value) return
  try {
    await confirmRecord(currentRecord.value.recordId)
    currentRecord.value.status = 'CONFIRMED'
    uni.showToast({ title: '确认成功', icon: 'success' })
    showDetailPopup.value = false
  } catch {
    uni.showToast({ title: '确认失败', icon: 'none' })
  }
}

const formatDate = (date: string) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getMonth() + 1}月${d.getDate()}日`
}

const formatTime = (time: string) => {
  if (!time) return ''
  const d = new Date(time)
  return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    PENDING: '待确认',
    CONFIRMED: '已确认',
    REJECTED: '已拒绝'
  }
  return map[status] || status
}
</script>

<style scoped>
.container {
  background: #f5f5f5;
  min-height: 100vh;
}
.header {
  background: linear-gradient(135deg, #1890ff, #096dd9);
  padding: 30rpx;
  color: #fff;
}
.stats-row {
  display: flex;
  justify-content: space-around;
}
.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.stat-value {
  font-size: 48rpx;
  font-weight: bold;
}
.stat-value.income {
  color: #fff;
}
.stat-label {
  font-size: 24rpx;
  opacity: 0.8;
  margin-top: 8rpx;
}
.filter-bar {
  background: #fff;
  padding: 20rpx 30rpx;
}
.date-picker {
  display: flex;
  align-items: center;
  gap: 10rpx;
  font-size: 28rpx;
  color: #333;
}
.arrow {
  font-size: 20rpx;
  color: #999;
}
.record-list {
  padding: 20rpx;
}
.record-item {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
}
.record-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.record-left {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}
.bundle-info {
  display: flex;
  align-items: center;
  gap: 12rpx;
}
.bundle-no {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
}
.process-tag {
  font-size: 22rpx;
  color: #1890ff;
  background: #e6f7ff;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
}
.time {
  font-size: 24rpx;
  color: #999;
}
.record-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4rpx;
}
.quantity {
  font-size: 28rpx;
  color: #666;
}
.amount {
  font-size: 32rpx;
  font-weight: bold;
  color: #52c41a;
}
.record-status {
  margin-top: 12rpx;
  padding-top: 12rpx;
  border-top: 1rpx solid #f5f5f5;
}
.status {
  font-size: 24rpx;
  padding: 4rpx 16rpx;
  border-radius: 20rpx;
}
.status.CONFIRMED {
  color: #52c41a;
  background: #f6ffed;
}
.status.PENDING {
  color: #faad14;
  background: #fffbe6;
}
.status.REJECTED {
  color: #f5222d;
  background: #fff2f0;
}
.empty {
  text-align: center;
  color: #999;
  padding: 100rpx;
  font-size: 28rpx;
}
.load-more {
  text-align: center;
  padding: 30rpx;
  color: #1890ff;
  font-size: 28rpx;
}
.detail-popup {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  z-index: 999;
}
.detail-content {
  width: 100%;
  background: #fff;
  border-radius: 24rpx 24rpx 0 0;
  max-height: 70vh;
}
.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f5f5f5;
}
.detail-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}
.close-btn {
  font-size: 48rpx;
  color: #999;
  line-height: 1;
}
.detail-body {
  padding: 30rpx;
}
.detail-row {
  display: flex;
  justify-content: space-between;
  padding: 16rpx 0;
}
.detail-row .label {
  color: #999;
  font-size: 28rpx;
}
.detail-row .value {
  color: #333;
  font-size: 28rpx;
}
.detail-row .value.highlight {
  color: #1890ff;
  font-weight: bold;
}
.detail-row .value.income {
  color: #52c41a;
  font-weight: bold;
}
.status-text.CONFIRMED {
  color: #52c41a;
}
.status-text.PENDING {
  color: #faad14;
}
.status-text.REJECTED {
  color: #f5222d;
}
.detail-footer {
  padding: 30rpx;
  border-top: 1rpx solid #f5f5f5;
}
.action-btn {
  background: linear-gradient(135deg, #1890ff, #096dd9);
  color: #fff;
  text-align: center;
  padding: 24rpx;
  border-radius: 50rpx;
  font-size: 30rpx;
}
</style>
