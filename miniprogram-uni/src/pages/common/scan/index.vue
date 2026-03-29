<template>
  <view class="container">
    <view class="scan-area">
      <view class="scan-frame">
        <view class="corner top-left"></view>
        <view class="corner top-right"></view>
        <view class="corner bottom-left"></view>
        <view class="corner bottom-right"></view>
        <view class="scan-line"></view>
      </view>
      <text class="scan-tip">将条码放入框内，即可自动扫描</text>
    </view>
    <view class="action-bar">
      <view class="action-btn" @click="openFlash">
        <text>{{ flashOn ? '关闭闪光灯' : '打开闪光灯' }}</text>
      </view>
      <view class="action-btn" @click="manualInput">
        <text>手动输入</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const flashOn = ref(false)

onLoad(() => {
  startScan()
})

const startScan = () => {
  uni.scanCode({
    scanType: ['qrCode', 'barCode'],
    success: (res) => {
      const pages = getCurrentPages()
      const prevPage = pages[pages.length - 2] as any
      if (prevPage && prevPage.$vm && prevPage.$vm.onScanResult) {
        prevPage.$vm.onScanResult(res.result)
      }
      uni.navigateBack()
    }
  })
}

const openFlash = () => { flashOn.value = !flashOn.value }
const manualInput = () => { uni.navigateBack() }
</script>

<style scoped>
.scan-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 200rpx;
}
.scan-frame {
  width: 500rpx;
  height: 500rpx;
  position: relative;
}
.corner {
  position: absolute;
  width: 60rpx;
  height: 60rpx;
  border-color: #1890ff;
  border-style: solid;
}
.top-left { top: 0; left: 0; border-width: 4rpx 0 0 4rpx; }
.top-right { top: 0; right: 0; border-width: 4rpx 4rpx 0 0; }
.bottom-left { bottom: 0; left: 0; border-width: 0 0 4rpx 4rpx; }
.bottom-right { bottom: 0; right: 0; border-width: 0 4rpx 4rpx 0; }
.scan-line {
  position: absolute;
  top: 0;
  left: 20rpx;
  right: 20rpx;
  height: 4rpx;
  background: #1890ff;
  animation: scan 2s infinite;
}
@keyframes scan {
  0% { top: 0; }
  50% { top: 100%; }
  100% { top: 0; }
}
.scan-tip {
  margin-top: 60rpx;
  color: #999;
  font-size: 28rpx;
}
.action-bar {
  display: flex;
  justify-content: center;
  gap: 60rpx;
  margin-top: 100rpx;
}
.action-btn {
  padding: 20rpx 40rpx;
  border-radius: 40rpx;
  background: rgba(255,255,255,0.2);
  color: #fff;
  font-size: 28rpx;
}
</style>
