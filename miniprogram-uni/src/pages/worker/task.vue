<template>
  <view class="container">
    <view class="tabs">
      <view
        class="tab-item"
        :class="{ active: currentTab === tab.key }"
        v-for="tab in tabs"
        :key="tab.key"
        @click="switchTab(tab.key)"
      >
        <text>{{ tab.label }}</text>
        <text class="badge" v-if="tab.count > 0">{{ tab.count }}</text>
      </view>
    </view>
    <view class="task-list">
      <view class="task-card" v-for="item in taskList" :key="item.id" @click="goDetail(item)">
        <view class="task-header">
          <text class="style-code">{{ item.styleCode }}</text>
          <view class="status-tag" :class="'status-' + item.status">
            {{ statusMap[item.status] || item.status }}
          </view>
        </view>
        <view class="task-body">
          <view class="task-row">
            <text class="label">颜色/尺码</text>
            <text class="value">{{ item.color }} / {{ item.size }}</text>
          </view>
          <view class="task-row">
            <text class="label">工序</text>
            <text class="value">{{ item.processName }}</text>
          </view>
          <view class="task-row">
            <text class="label">数量</text>
            <text class="value">{{ item.completedQuantity || 0 }} / {{ item.totalQuantity }}</text>
          </view>
        </view>
        <view class="progress-bar">
          <view class="progress-inner" :style="{ width: progressPercent(item) + '%' }"></view>
        </view>
      </view>
      <view v-if="taskList.length === 0" class="empty">暂无任务</view>
    </view>
    <view class="load-more" v-if="hasMore" @click="loadMore">
      <text>{{ loading ? '加载中...' : '加载更多' }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getMyTasks } from '@/api/worker'

const tabs = ref([
  { key: 'pending', label: '待进行', count: 0 },
  { key: 'in_progress', label: '进行中', count: 0 },
  { key: 'completed', label: '已完成', count: 0 }
])

const statusMap: Record<string, string> = {
  pending: '待进行',
  in_progress: '进行中',
  completed: '已完成'
}

const currentTab = ref('pending')
const taskList = ref<any[]>([])
const page = ref(1)
const pageSize = 20
const hasMore = ref(true)
const loading = ref(false)

onShow(() => {
  page.value = 1
  taskList.value = []
  hasMore.value = true
  loadTasks()
})

const switchTab = (key: string) => {
  currentTab.value = key
  page.value = 1
  taskList.value = []
  hasMore.value = true
  loadTasks()
}

const loadTasks = async () => {
  if (loading.value) return
  loading.value = true
  try {
    const res: any = await getMyTasks({ status: currentTab.value, page: page.value, pageSize })
    if (res.success) {
      const list = res.data?.list || res.data || []
      taskList.value = page.value === 1 ? list : [...taskList.value, ...list]
      hasMore.value = list.length >= pageSize
    }
  } catch {
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  if (!hasMore.value || loading.value) return
  page.value++
  loadTasks()
}

const progressPercent = (item: any) => {
  if (!item.totalQuantity) return 0
  return Math.round(((item.completedQuantity || 0) / item.totalQuantity) * 100)
}

const goDetail = (item: any) => {
  uni.navigateTo({ url: `/pages/worker/piecework?taskId=${item.id}` })
}
</script>

<style scoped>
.tabs {
  display: flex;
  background: #fff;
  border-radius: 16rpx;
  padding: 8rpx;
  margin-bottom: 20rpx;
}
.tab-item {
  flex: 1;
  text-align: center;
  padding: 20rpx 0;
  font-size: 28rpx;
  color: #666;
  border-radius: 12rpx;
  position: relative;
}
.tab-item.active {
  background: #1890ff;
  color: #fff;
  font-weight: 600;
}
.badge {
  position: absolute;
  top: 8rpx;
  right: 20rpx;
  background: #ff4d4f;
  color: #fff;
  font-size: 20rpx;
  padding: 2rpx 10rpx;
  border-radius: 20rpx;
  min-width: 32rpx;
}
.tab-item.active .badge {
  background: rgba(255,255,255,0.3);
}
.task-list { display: flex; flex-direction: column; gap: 20rpx; }
.task-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}
.style-code { font-size: 30rpx; font-weight: 600; color: #333; }
.status-tag {
  font-size: 24rpx;
  padding: 4rpx 16rpx;
  border-radius: 20rpx;
}
.status-pending { background: #fff7e6; color: #fa8c16; }
.status-in_progress { background: #e6f7ff; color: #1890ff; }
.status-completed { background: #f6ffed; color: #52c41a; }
.task-body { margin-bottom: 16rpx; }
.task-row {
  display: flex;
  justify-content: space-between;
  padding: 8rpx 0;
  font-size: 26rpx;
}
.task-row .label { color: #999; }
.task-row .value { color: #333; }
.progress-bar {
  height: 8rpx;
  background: #f0f0f0;
  border-radius: 4rpx;
  overflow: hidden;
}
.progress-inner {
  height: 100%;
  background: linear-gradient(90deg, #1890ff, #36cfc9);
  border-radius: 4rpx;
  transition: width 0.3s;
}
.empty { text-align: center; color: #999; padding: 80rpx; font-size: 28rpx; }
.load-more {
  text-align: center;
  padding: 30rpx;
  color: #1890ff;
  font-size: 28rpx;
}
</style>
