<script setup lang="ts">
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'

onLaunch(() => {
  console.log('App Launch')
})

onShow(() => {
  console.log('App Show')
})

onHide(() => {
  console.log('App Hide')
})
</script>

<style>
/* 全局样式 */
page {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  font-size: 28rpx;
  color: #333;
  background-color: #f5f5f5;
}

.container {
  padding: 24rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 20rpx;
}

/* 统计卡片 */
.stats-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.stats-row {
  display: flex;
  justify-content: space-around;
}

.stat-item {
  text-align: center;
}

.stat-item .value {
  display: block;
  font-size: 40rpx;
  font-weight: 700;
  color: #1890ff;
}

.stat-item .label {
  display: block;
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
}

/* 快捷操作 */
.quick-actions {
  display: flex;
  justify-content: space-around;
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx 0;
  margin-bottom: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
}

.action-item text {
  font-size: 24rpx;
  color: #666;
}

/* 用户卡片 */
.user-card {
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, #1890ff, #096dd9);
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 24rpx;
  color: #fff;
}

.user-card .avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 24rpx;
  border: 4rpx solid rgba(255, 255, 255, 0.5);
}

.user-card .avatar image {
  width: 100%;
  height: 100%;
}

.user-card .info {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.user-card .name {
  font-size: 36rpx;
  font-weight: 600;
}

.user-card .dept {
  font-size: 24rpx;
  opacity: 0.8;
}
</style>
