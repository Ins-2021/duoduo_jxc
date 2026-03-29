<template>
  <view class="stats-card">
    <view class="card-title" v-if="title">{{ title }}</view>
    <view class="stats-row">
      <view class="stat-item" v-for="item in items" :key="item.label">
        <text class="value" :style="{ color: item.color || '#1890ff' }">{{ item.value }}</text>
        <text class="label">{{ item.label }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  title?: string
  items: { label: string; value: string | number; color?: string }[]
}>(), { title: '' })
</script>

<style scoped>
.stats-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.card-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 24rpx;
}
.stats-row {
  display: flex;
  justify-content: space-around;
}
.stat-item { text-align: center; }
.stat-item .value {
  display: block;
  font-size: 40rpx;
  font-weight: 700;
}
.stat-item .label {
  display: block;
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
}
</style>
