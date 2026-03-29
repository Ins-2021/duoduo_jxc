<template>
  <view class="role-container">
    <view class="title">请选择角色</view>
    <view class="role-list">
      <view
        class="role-item"
        v-for="role in roles"
        :key="role.code"
        @click="selectRole(role.code)"
      >
        <view class="role-icon">{{ role.icon }}</view>
        <text class="role-name">{{ role.name }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { useRoleStore } from '@/store/role'

const roleStore = useRoleStore()

const roles = [
  { code: 'worker', name: '缝制工', icon: '🧵' },
  { code: 'cutter', name: '裁床工', icon: '✂️' },
  { code: 'inspector', name: '质检员', icon: '🔍' },
  { code: 'warehouse', name: '仓库员', icon: '📦' },
  { code: 'leader', name: '班组长', icon: '📋' },
  { code: 'manager', name: '车间主任', icon: '📊' },
  { code: 'boss', name: '经营者', icon: '💼' }
]

const selectRole = (roleCode: string) => {
  roleStore.setRole(roleCode)
  uni.switchTab({ url: '/pages/worker/index' })
}
</script>

<style scoped>
.role-container { padding: 40rpx; }
.title {
  font-size: 36rpx;
  font-weight: 600;
  color: #333;
  text-align: center;
  margin-bottom: 60rpx;
}
.role-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30rpx;
}
.role-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx 20rpx;
  background: #fff;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.role-item:active { background: #e6f7ff; }
.role-icon { font-size: 60rpx; margin-bottom: 16rpx; }
.role-name { font-size: 28rpx; color: #333; }
</style>
