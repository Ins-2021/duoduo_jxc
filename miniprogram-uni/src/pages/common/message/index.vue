<template>
  <view class="container">
    <view class="message-list">
      <view class="message-item" v-for="item in messages" :key="item.id">
        <view class="msg-dot" v-if="!item.read"></view>
        <view class="msg-content">
          <text class="msg-title">{{ item.title }}</text>
          <text class="msg-desc">{{ item.content }}</text>
          <text class="msg-time">{{ item.createdAt }}</text>
        </view>
      </view>
      <view v-if="messages.length === 0" class="empty">暂无消息</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const messages = ref<any[]>([])
</script>

<style scoped>
.message-list { background: #fff; border-radius: 16rpx; overflow: hidden; }
.message-item {
  display: flex;
  padding: 30rpx;
  border-bottom: 1rpx solid #f5f5f5;
  position: relative;
}
.msg-dot {
  position: absolute;
  top: 36rpx;
  left: 16rpx;
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background: #ff4d4f;
}
.msg-content { flex: 1; margin-left: 12rpx; }
.msg-title { font-size: 30rpx; color: #333; display: block; }
.msg-desc { font-size: 26rpx; color: #999; display: block; margin-top: 8rpx; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.msg-time { font-size: 24rpx; color: #ccc; display: block; margin-top: 8rpx; }
.empty { text-align: center; color: #999; padding: 120rpx; font-size: 28rpx; }
</style>
