<template>
  <view class="container">
    <!-- 任务列表 -->
    <view v-if="!currentTask && taskList.length > 0" class="list-section">
      <view v-for="task in taskList" :key="task.id" class="list-item" @click="openTask(task)">
        <view class="item-header">
          <text class="task-name">{{ task.taskName }}</text>
          <text class="status-tag" :class="getStatusClass(task.status)">{{ getStatusText(task.status) }}</text>
        </view>
        <view class="item-info">
          <text class="info-text">仓库：{{ task.warehouseName }}</text>
          <text class="info-text">创建时间：{{ task.createTime }}</text>
        </view>
        <view class="item-footer">
          <text class="count-info">共 {{ task.itemCount }} 项</text>
          <text class="arrow">开始盘点 ›</text>
        </view>
      </view>
    </view>

    <view v-if="!currentTask && taskList.length === 0" class="empty-state">
      <text>暂无待盘点任务</text>
    </view>

    <!-- 盘点详情 -->
    <view v-if="currentTask" class="inventory-detail">
      <view class="detail-header">
        <text class="back-btn" @click="closeTask">‹ 返回</text>
        <text class="detail-title">{{ currentTask.taskName }}</text>
      </view>

      <view class="detail-info">
        <text>仓库：{{ currentTask.warehouseName }}</text>
        <text>创建时间：{{ currentTask.createTime }}</text>
      </view>

      <view class="item-list">
        <view v-for="(item, idx) in inventoryItems" :key="item.id" class="inventory-item">
          <view class="inv-item-header">
            <text class="inv-style">{{ item.styleNo }} - {{ item.color }}/{{ item.size }}</text>
          </view>
          <view class="inv-row">
            <text class="inv-label">系统数量</text>
            <text class="inv-value">{{ item.systemQuantity }}</text>
          </view>
          <view class="inv-row">
            <text class="inv-label">实际数量</text>
            <input
              v-model="item.actualQuantity"
              type="number"
              class="inv-input"
              placeholder="请输入"
              @input="calcDiff(item)"
            />
          </view>
          <view class="inv-row">
            <text class="inv-label">差异</text>
            <text class="inv-value" :class="getDiffClass(item)">{{ getDiffText(item) }}</text>
          </view>
        </view>
      </view>

      <button class="submit-btn" :loading="submitting" @click="onSubmit">提交盘点结果</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getInventoryTasks, submitInventory } from '@/api/warehouse'

const taskList = ref<any[]>([])
const currentTask = ref<any>(null)
const inventoryItems = ref<any[]>([])
const submitting = ref(false)

const loadTasks = async () => {
  try {
    const res = await getInventoryTasks({ status: 'pending' })
    taskList.value = res.data || []
  } catch {}
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = { pending: '待盘点', in_progress: '盘点中', completed: '已完成' }
  return map[status] || status
}

const getStatusClass = (status: string) => {
  const map: Record<string, string> = { pending: 'status-pending', in_progress: 'status-progress', completed: 'status-done' }
  return map[status] || ''
}

const openTask = (task: any) => {
  currentTask.value = task
  inventoryItems.value = (task.items || []).map((item: any) => ({
    ...item,
    actualQuantity: '',
    diff: 0
  }))
}

const closeTask = () => {
  currentTask.value = null
  inventoryItems.value = []
  loadTasks()
}

const calcDiff = (item: any) => {
  const actual = Number(item.actualQuantity) || 0
  item.diff = actual - item.systemQuantity
}

const getDiffText = (item: any) => {
  const diff = item.diff
  if (diff === 0) return '0'
  return diff > 0 ? `+${diff}` : String(diff)
}

const getDiffClass = (item: any) => {
  if (item.diff === 0) return 'diff-normal'
  return item.diff > 0 ? 'diff-more' : 'diff-less'
}

const onSubmit = async () => {
  if (inventoryItems.value.some(item => item.actualQuantity === '')) {
    uni.showToast({ title: '请填写所有实际数量', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    await submitInventory({
      taskId: currentTask.value.id,
      items: inventoryItems.value.map(item => ({
        id: item.id,
        actualQuantity: Number(item.actualQuantity)
      }))
    })
    uni.showToast({ title: '盘点提交成功', icon: 'success' })
    closeTask()
  } catch {} finally {
    submitting.value = false
  }
}

onShow(() => {
  if (!currentTask.value) loadTasks()
})
</script>

<style scoped>
.container { padding: 24rpx; }
.list-section { display: flex; flex-direction: column; gap: 20rpx; }
.list-item {
  background: #fff; border-radius: 16rpx; padding: 28rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.item-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; }
.task-name { font-size: 32rpx; font-weight: 600; color: #333; }
.status-tag { font-size: 22rpx; padding: 4rpx 16rpx; border-radius: 6rpx; color: #fff; }
.status-pending { background: #faad14; }
.status-progress { background: #1890ff; }
.status-done { background: #52c41a; }
.item-info { display: flex; flex-direction: column; gap: 8rpx; margin-bottom: 12rpx; }
.info-text { font-size: 26rpx; color: #666; }
.item-footer { display: flex; justify-content: space-between; align-items: center; }
.count-info { font-size: 26rpx; color: #999; }
.arrow { font-size: 26rpx; color: #1890ff; }
.inventory-detail { }
.detail-header { display: flex; align-items: center; gap: 20rpx; margin-bottom: 24rpx; }
.back-btn { font-size: 32rpx; color: #1890ff; padding: 8rpx 0; }
.detail-title { font-size: 34rpx; font-weight: 600; color: #333; }
.detail-info {
  display: flex; flex-direction: column; gap: 8rpx;
  background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 24rpx;
  font-size: 28rpx; color: #666;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.item-list { display: flex; flex-direction: column; gap: 16rpx; margin-bottom: 30rpx; }
.inventory-item {
  background: #fff; border-radius: 16rpx; padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.inv-item-header { margin-bottom: 16rpx; }
.inv-style { font-size: 28rpx; font-weight: 600; color: #333; }
.inv-row { display: flex; align-items: center; justify-content: space-between; padding: 12rpx 0; border-bottom: 1rpx solid #f5f5f5; }
.inv-row:last-child { border-bottom: none; }
.inv-label { font-size: 26rpx; color: #999; }
.inv-value { font-size: 28rpx; color: #333; font-weight: 600; }
.inv-input {
  font-size: 28rpx; padding: 8rpx 16rpx; background: #f5f5f5;
  border-radius: 8rpx; text-align: right; width: 200rpx;
}
.diff-normal { color: #52c41a; }
.diff-more { color: #1890ff; }
.diff-less { color: #ff4d4f; }
.submit-btn {
  background: #1890ff; color: #fff; font-size: 32rpx;
  border-radius: 12rpx; height: 88rpx; line-height: 88rpx; border: none;
}
.empty-state { text-align: center; padding: 100rpx 0; color: #999; font-size: 28rpx; }
</style>
