<template>
  <view class="login-container">
    <view class="logo-section">
      <view class="logo-circle">
        <text class="logo-text">衣</text>
      </view>
      <text class="app-name">衣多多生产管理</text>
      <text class="app-desc">智能工厂管理系统</text>
    </view>

    <view class="login-section">
      <view class="login-btn" open-type="getPhoneNumber" @getphonenumber="handlePhoneLogin">
        <text class="btn-icon">📱</text>
        <text class="btn-text">微信授权登录</text>
      </view>

      <!-- 开发调试：跳过登录直接进入 -->
      <view class="dev-login" @click="devLogin">
        <text class="dev-text">[DEV] 跳过登录</text>
      </view>

      <view class="tips">
        <text>登录即表示同意</text>
        <text class="link">《用户协议》</text>
        <text>和</text>
        <text class="link">《隐私政策》</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { useUserStore } from '@/store/user'
import { useRoleStore } from '@/store/role'

const userStore = useUserStore()
const roleStore = useRoleStore()

const handlePhoneLogin = async (e: any) => {
  if (e.detail.code) {
    try {
      const res = await userStore.loginByPhone(e.detail.code)
      if (res.success) {
        const roles = userStore.roles
        if (roles.length > 1) {
          uni.navigateTo({ url: '/pages/common/role/select' })
        } else if (roles.length === 1) {
          roleStore.setRole(roles[0])
          uni.switchTab({ url: '/pages/worker/index' })
        }
      }
    } catch (err) {
      uni.showToast({ title: '登录失败', icon: 'none' })
    }
  }
}

// 开发模式：跳过登录
const devLogin = () => {
  userStore.roles = ['worker']
  userStore.userInfo = { name: '测试用户', departmentName: '缝制车间' }
  uni.setStorageSync('token', 'dev-token')
  roleStore.setRole('worker')
  uni.switchTab({ url: '/pages/worker/index' })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60rpx;
  background: linear-gradient(180deg, #e6f7ff 0%, #f5f5f5 100%);
}
.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 120rpx;
}
.logo-circle {
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #1890ff, #096dd9);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30rpx;
  box-shadow: 0 8rpx 24rpx rgba(24,144,255,0.3);
}
.logo-text {
  font-size: 72rpx;
  color: #fff;
  font-weight: 700;
}
.app-name {
  font-size: 44rpx;
  font-weight: 700;
  color: #333;
  margin-bottom: 12rpx;
}
.app-desc {
  font-size: 28rpx;
  color: #999;
}
.login-section { width: 100%; }
.login-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1890ff, #096dd9);
  color: #fff;
  padding: 28rpx;
  border-radius: 60rpx;
  font-size: 32rpx;
  box-shadow: 0 8rpx 24rpx rgba(24,144,255,0.3);
}
.btn-icon { font-size: 36rpx; margin-right: 16rpx; }
.tips {
  text-align: center;
  margin-top: 40rpx;
  font-size: 24rpx;
  color: #999;
}
.tips .link { color: #1890ff; }
.dev-login {
  text-align: center;
  margin-top: 30rpx;
  padding: 20rpx;
}
.dev-text { font-size: 24rpx; color: #ccc; }
</style>
