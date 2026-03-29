<template>
  <view class="container">
    <view class="bundle-header">
      <view class="header-row">
        <text class="bundle-no">{{ bundleData.bundleNo || '-' }}</text>
        <view class="status-tag" :class="'status-' + bundleData.status">
          {{ statusMap[bundleData.status] || bundleData.status || '-' }}
        </view>
      </view>
      <view class="header-row">
        <text class="print-btn" @click="handlePrint">🖨️ 打印标签</text>
      </view>
    </view>

    <view class="info-card">
      <view class="info-row"><text class="label">款号</text><text class="value">{{ bundleData.styleCode || '-' }}</text></view>
      <view class="info-row"><text class="label">款名</text><text class="value">{{ bundleData.styleName || '-' }}</text></view>
      <view class="info-row"><text class="label">颜色</text><text class="value">{{ bundleData.color || '-' }}</text></view>
      <view class="info-row"><text class="label">尺码</text><text class="value">{{ bundleData.size || '-' }}</text></view>
      <view class="info-row"><text class="label">扎包数量</text><text class="value highlight">{{ bundleData.quantity || 0 }}件</text></view>
      <view class="info-row"><text class="label">订单号</text><text class="value">{{ bundleData.orderNo || '-' }}</text></view>
      <view class="info-row"><text class="label">创建时间</text><text class="value">{{ bundleData.createTime || '-' }}</text></view>
      <view class="info-row"><text class="label">创建人</text><text class="value">{{ bundleData.createBy || '-' }}</text></view>
    </view>

    <view class="process-section" v-if="processRecords.length > 0">
      <view class="section-title">工序流转记录</view>
      <view class="process-list">
        <view class="process-item" v-for="item in processRecords" :key="item.id">
          <view class="process-left">
            <view class="dot-line">
              <view class="dot" :class="{ done: item.status === 'completed' }"></view>
            </view>
            <view class="process-info">
              <text class="process-name">{{ item.processName }}</text>
              <text class="process-worker">{{ item.workerName || '-' }} · {{ item.createTime || '' }}</text>
            </view>
          </view>
          <view class="process-right">
            <text class="process-quantity">{{ item.completedQuantity || 0 }}/{{ item.quantity }}</text>
            <view class="process-status" :class="'ps-' + item.status">
              {{ statusMap[item.status] || item.status }}
            </view>
          </view>
        </view>
      </view>
    </view>

    <view v-if="!bundleData.bundleNo && !loading" class="empty">扎包信息不存在</view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getBundleDetail, printBundleLabel } from '@/api/cutter'

const loading = ref(false)
const bundleId = ref('')
const bundleData = ref<any>({})
const processRecords = ref<any[]>([])

const statusMap: Record<string, string> = {
  pending: '待处理',
  in_progress: '进行中',
  completed: '已完成',
  cutting: '裁剪中',
  sewn: '已缝制',
  packed: '已打包'
}

onLoad((options: any) => {
  bundleId.value = options?.id || ''
  if (bundleId.value) loadDetail()
})

const loadDetail = async () => {
  loading.value = true
  try {
    const [detailRes]: any[] = await Promise.all([
      getBundleDetail(bundleId.value)
    ])
    if (detailRes.success) bundleData.value = detailRes.data || {}
    processRecords.value = detailRes.data?.records || []
  } catch {
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const handlePrint = async () => {
  try {
    await printBundleLabel(bundleId.value)
    uni.showToast({ title: '打印指令已发送', icon: 'success' })
  } catch {
    uni.showToast({ title: '打印失败', icon: 'none' })
  }
}
</script>

<style scoped>
.bundle-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
  margin-bottom: 16rpx;
}
.header-row { display: flex; align-items: center; gap: 16rpx; }
.bundle-no { font-size: 34rpx; font-weight: 700; color: #333; }
.status-tag {
  font-size: 24rpx;
  padding: 4rpx 16rpx;
  border-radius: 20rpx;
}
.status-pending { background: #fff7e6; color: #fa8c16; }
.status-in_progress { background: #e6f7ff; color: #1890ff; }
.status-completed { background: #f6ffed; color: #52c41a; }
.status-cutting { background: #f9f0ff; color: #722ed1; }
.print-btn {
  background: #f0f5ff;
  padding: 12rpx 24rpx;
  border-radius: 20rpx;
  font-size: 26rpx;
  color: #1890ff;
}
.info-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.info-row {
  display: flex;
  justify-content: space-between;
  padding: 14rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}
.info-row:last-child { border-bottom: none; }
.info-row .label { font-size: 28rpx; color: #999; }
.info-row .value { font-size: 28rpx; color: #333; }
.info-row .value.highlight { color: #722ed1; font-weight: 600; }
.process-section { margin-bottom: 24rpx; }
.section-title { font-size: 30rpx; font-weight: 600; color: #333; margin-bottom: 16rpx; }
.process-list { background: #fff; border-radius: 16rpx; overflow: hidden; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05); }
.process-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
  border-bottom: 1rpx solid #f5f5f5;
}
.process-item:last-child { border-bottom: none; }
.process-left { display: flex; align-items: flex-start; gap: 16rpx; flex: 1; }
.dot-line { display: flex; flex-direction: column; align-items: center; padding-top: 10rpx; }
.dot {
  width: 16rpx; height: 16rpx;
  border-radius: 50%;
  background: #ddd;
}
.dot.done { background: #52c41a; }
.process-info { display: flex; flex-direction: column; gap: 4rpx; }
.process-name { font-size: 28rpx; color: #333; font-weight: 500; }
.process-worker { font-size: 24rpx; color: #999; }
.process-right { display: flex; flex-direction: column; align-items: flex-end; gap: 4rpx; }
.process-quantity { font-size: 26rpx; color: #666; }
.process-status {
  font-size: 22rpx;
  padding: 2rpx 12rpx;
  border-radius: 10rpx;
}
.ps-completed { background: #f6ffed; color: #52c41a; }
.ps-in_progress { background: #e6f7ff; color: #1890ff; }
.ps-pending { background: #f5f5f5; color: #999; }
.empty { text-align: center; color: #999; padding: 80rpx; font-size: 28rpx; }
</style>
