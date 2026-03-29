<template>
  <view class="container">
    <view class="filter-bar">
      <view
        class="filter-item"
        :class="{ active: currentStatus === item.value }"
        v-for="item in statusFilters"
        :key="item.value"
        @click="switchStatus(item.value)"
      >
        <text>{{ item.label }}</text>
      </view>
    </view>
    <view class="task-list">
      <view class="task-card" v-for="item in taskList" :key="item.id" @click="goToInput(item)">
        <view class="task-header">
          <text class="style-code">{{ item.styleCode }}</text>
          <view class="status-tag" :class="'status-' + item.status">
            {{ statusMap[item.status] || item.status }}
          </view>
        </view>
        <view class="task-body">
          <view class="task-row">
            <text class="label">款名</text>
            <text class="value">{{ item.styleName || '-' }}</text>
          </view>
          <view class="task-row">
            <text class="label">颜色/尺码</text>
            <text class="value">{{ item.color }} / {{ item.size }}</text>
          </view>
          <view class="task-row">
            <text class="label">计划数量</text>
            <text class="value">{{ item.planQuantity || 0 }}件</text>
          </view>
          <view class="task-row">
            <text class="label">已裁数量</text>
            <text class="value highlight">{{ item.cutQuantity || 0 }}件</text>
          </view>
        </view>
        <view class="progress-bar">
          <view class="progress-inner" :style="{ width: progressPercent(item) + '%' }"></view>
        </view>
      </view>
      <view v-if="taskList.length === 0" class="empty">暂无裁床任务</view>
    </view>
    <view class="load-more" v-if="hasMore" @click="loadMore">
      <text>{{ loading ? '加载中...' : '加载更多' }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getCuttingTasks } from '@/api/cutter'

const statusFilters = [
  { label: '全部', value: '' },
  { label: '待裁剪', value: 'pending' },
  { label: '裁剪中', value: 'in_progress' },
  { label: '已完成', value: 'completed' }
]

const statusMap: Record<string, string> = {
  pending: '待裁剪',
  in_progress: '裁剪中',
  completed: '已完成'
}

const currentStatus = ref('')
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

const switchStatus = (status: string) => {
  currentStatus.value = status
  page.value = 1
  taskList.value = []
  hasMore.value = true
  loadTasks()
}

const loadTasks = async () => {
  if (loading.value) return
  loading.value = true
  try {
    const params: any = { page: page.value, pageSize }
    if (currentStatus.value) params.status = currentStatus.value
    const res: any = await getCuttingTasks(params)
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
  const total = item.planQuantity || 0
  if (!total) return 0
  return Math.round(((item.cutQuantity || 0) / total) * 100)
}

const goToInput = (item: any) => {
  uni.navigateTo({ url: `/pages/cutter/input?taskId=${item.id}` })
}
</script>

<style scoped>
.filter-bar {
  display: flex;
  gap: 16rpx;
  padding: 16rpx 0;
  margin-bottom: 20rpx;
  overflow-x: auto;
}
.filter-item {
  padding: 12rpx 28rpx;
  background: #f5f5f5;
  border-radius: 30rpx;
  font-size: 26rpx;
  color: #666;
  white-space: nowrap;
}
.filter-item.active {
  background: #1890ff;
  color: #fff;
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
.task-row .value.highlight { color: #1890ff; font-weight: 600; }
.progress-bar {
  height: 8rpx;
  background: #f0f0f0;
  border-radius: 4rpx;
  overflow: hidden;
}
.progress-inner {
  height: 100%;
  background: linear-gradient(90deg, #722ed1, #1890ff);
  border-radius: 4rpx;
  transition: width 0.3s;
}
.empty { text-align: center; color: #999; padding: 80rpx; font-size: 28rpx; }
.load-more { text-align: center; padding: 30rpx; color: #1890ff; font-size: 28rpx; }
</style>
