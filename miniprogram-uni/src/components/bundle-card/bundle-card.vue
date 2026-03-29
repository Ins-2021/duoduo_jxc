<template>
  <view class="bundle-card" @click="handleClick">
    <view class="card-header">
      <text class="bundle-no">{{ bundle.bundleNo }}</text>
      <view class="bundle-status" :class="bundle.status">{{ getStatusText(bundle.status) }}</view>
    </view>
    <view class="card-body">
      <view class="info-row">
        <text class="label">款号：</text>
        <text class="value">{{ bundle.styleCode }}</text>
      </view>
      <view class="info-row">
        <text class="label">颜色/尺码：</text>
        <text class="value">{{ bundle.color }} / {{ bundle.size }}</text>
      </view>
      <view class="info-row">
        <text class="label">数量：</text>
        <text class="value highlight">{{ bundle.quantity }}</text>
      </view>
    </view>
    <view class="card-footer" v-if="showActions">
      <view class="footer-btn detail" @click.stop="handleDetail">详情</view>
      <view class="footer-btn action" @click.stop="handleAction">操作</view>
    </view>
  </view>
</template>

<script setup lang="ts">
const props = withDefaults(defineProps<{
  bundle: any
  showActions?: boolean
}>(), { showActions: true })

const emit = defineEmits<{
  click: [bundle: any]
  detail: [bundle: any]
  action: [bundle: any]
}>()

const handleClick = () => emit('click', props.bundle)
const handleDetail = () => emit('detail', props.bundle)
const handleAction = () => emit('action', props.bundle)

const getStatusText = (status: string) => {
  const map: Record<string, string> = { pending: '待开工', producing: '生产中', completed: '已完成' }
  return map[status] || status
}
</script>

<style scoped>
.bundle-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}
.bundle-no {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
}
.bundle-status {
  font-size: 24rpx;
  padding: 4rpx 16rpx;
  border-radius: 20rpx;
}
.bundle-status.pending { background: #fff7e6; color: #fa8c16; }
.bundle-status.producing { background: #e6f7ff; color: #1890ff; }
.bundle-status.completed { background: #f6ffed; color: #52c41a; }
.card-body { margin-bottom: 16rpx; }
.info-row {
  display: flex;
  justify-content: space-between;
  font-size: 26rpx;
  color: #666;
  line-height: 2;
}
.info-row .label { color: #999; }
.info-row .value.highlight { color: #1890ff; font-weight: 600; }
.card-footer {
  display: flex;
  gap: 20rpx;
  border-top: 1rpx solid #f0f0f0;
  padding-top: 16rpx;
}
.footer-btn {
  flex: 1;
  text-align: center;
  padding: 12rpx 0;
  border-radius: 8rpx;
  font-size: 26rpx;
}
.footer-btn.detail { background: #f5f5f5; color: #666; }
.footer-btn.action { background: #1890ff; color: #fff; }
</style>
