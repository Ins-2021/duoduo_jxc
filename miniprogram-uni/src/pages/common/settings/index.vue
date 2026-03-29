<template>
  <view class="container">
    <view class="settings-section">
      <view class="setting-item" @click="clearCache">
        <text class="setting-text">清除缓存</text>
        <text class="setting-value">{{ cacheSize }}</text>
        <text class="arrow">></text>
      </view>
      <view class="setting-item">
        <text class="setting-text">当前版本</text>
        <text class="setting-value">v1.0.0</text>
      </view>
    </view>
    <view class="settings-section">
      <view class="setting-item" @click="checkUpdate">
        <text class="setting-text">检查更新</text>
        <text class="arrow">></text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const cacheSize = ref('0 KB')

const clearCache = () => {
  uni.showModal({
    title: '确认清除',
    content: '确认清除本地缓存？',
    success: (res) => {
      if (res.confirm) {
        uni.clearStorageSync()
        cacheSize.value = '0 KB'
        uni.showToast({ title: '清除成功', icon: 'success' })
      }
    }
  })
}

const checkUpdate = () => {
  uni.showToast({ title: '已是最新版本', icon: 'none' })
}
</script>

<style scoped>
.settings-section {
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 24rpx;
  overflow: hidden;
}
.setting-item {
  display: flex;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f5f5f5;
}
.setting-item:last-child { border-bottom: none; }
.setting-text { flex: 1; font-size: 30rpx; color: #333; }
.setting-value { font-size: 26rpx; color: #999; margin-right: 16rpx; }
.arrow { color: #ccc; font-size: 28rpx; }
</style>
