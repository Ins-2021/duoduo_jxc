<template>
  <view class="profile-container">
    <view class="user-card">
      <view class="avatar">
        <image :src="userInfo.avatar || ''" mode="aspectFill" />
      </view>
      <view class="info">
        <text class="name">{{ userInfo.name || '未登录' }}</text>
        <text class="dept">{{ userInfo.departmentName || '' }}</text>
      </view>
    </view>

    <view class="menu-section">
      <view class="menu-item" @click="goTo('/pages/common/message/index')">
        <text class="menu-icon">💬</text>
        <text class="menu-text">消息中心</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @click="goTo('/pages/common/settings/index')">
        <text class="menu-icon">⚙️</text>
        <text class="menu-text">设置</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @click="switchRole">
        <text class="menu-icon">🔄</text>
        <text class="menu-text">切换角色</text>
        <text class="menu-arrow">></text>
      </view>
    </view>

    <view class="menu-section">
      <view class="menu-item logout" @click="handleLogout">
        <text class="menu-text">退出登录</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onShow } from 'vue'
import { onShow as uniOnShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { useRoleStore } from '@/store/role'

const userStore = useUserStore()
const roleStore = useRoleStore()
const userInfo = ref(userStore.userInfo)

uniOnShow(() => {
  userInfo.value = userStore.userInfo
})

const goTo = (url: string) => { uni.navigateTo({ url }) }

const switchRole = () => { uni.navigateTo({ url: '/pages/common/role/select' }) }

const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确认退出登录？',
    success: (res) => {
      if (res.confirm) userStore.logout()
    }
  })
}
</script>

<style scoped>
.profile-container { padding: 24rpx; }
.user-card {
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, #1890ff, #096dd9);
  border-radius: 16rpx;
  padding: 40rpx 30rpx;
  margin-bottom: 24rpx;
  color: #fff;
}
.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 30rpx;
  border: 4rpx solid rgba(255,255,255,0.5);
  background: #fff;
}
.avatar image { width: 100%; height: 100%; }
.info { display: flex; flex-direction: column; gap: 8rpx; }
.name { font-size: 36rpx; font-weight: 600; }
.dept { font-size: 26rpx; opacity: 0.8; }
.menu-section {
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 24rpx;
  overflow: hidden;
}
.menu-item {
  display: flex;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f5f5f5;
}
.menu-item:last-child { border-bottom: none; }
.menu-icon { font-size: 36rpx; margin-right: 20rpx; }
.menu-text { flex: 1; font-size: 30rpx; color: #333; }
.menu-arrow { color: #ccc; font-size: 28rpx; }
.logout { justify-content: center; }
.logout .menu-text { color: #ff4d4f; text-align: center; }
</style>
