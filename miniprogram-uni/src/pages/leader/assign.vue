<template>
  <view class="container">
    <view class="section">
      <text class="section-title">待分配扎包</text>
      <view v-if="bundleList.length" class="bundle-list">
        <view v-for="item in bundleList" :key="item.id" class="bundle-item" @click="toggleSelect(item.id)">
          <view class="checkbox" :class="{ checked: selectedIds.includes(item.id) }">
            <text v-if="selectedIds.includes(item.id)">✓</text>
          </view>
          <view class="bundle-info">
            <text class="bundle-no">{{ item.bundleNo }}</text>
            <text class="bundle-detail">{{ item.orderNo }} | {{ item.styleNo }} | {{ item.quantity }}件</text>
          </view>
        </view>
      </view>
      <view v-else class="empty-state">
        <text class="empty-text">暂无待分配扎包</text>
      </view>
    </view>

    <view class="section">
      <text class="section-title">分配信息</text>
      <view class="form-group">
        <text class="form-label">选择员工</text>
        <picker :range="workerNames" @change="onWorkerChange">
          <view class="picker-value">{{ selectedWorker || '请选择员工' }}</view>
        </picker>
      </view>
      <view class="form-group">
        <text class="form-label">要求完成时间</text>
        <picker mode="date" @change="onDateChange">
          <view class="picker-value">{{ dueDate || '请选择日期' }}</view>
        </picker>
      </view>
      <view class="form-group">
        <text class="form-label">备注</text>
        <textarea v-model="remark" placeholder="请输入备注" class="textarea" :maxlength="200" />
      </view>
    </view>

    <view class="selected-info" v-if="selectedIds.length">
      <text>已选择 {{ selectedIds.length }} 个扎包</text>
    </view>

    <button class="submit-btn" :disabled="!canSubmit" @click="handleSubmit">确认分配</button>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getPendingBundles, getWorkerList, assignTask } from '@/api/leader'

const bundleList = ref([
  { id: 1, bundleNo: 'ZB20260329001', orderNo: 'PO2026032901', styleNo: 'A001', quantity: 50 },
  { id: 2, bundleNo: 'ZB20260329002', orderNo: 'PO2026032901', styleNo: 'A001', quantity: 50 },
  { id: 3, bundleNo: 'ZB20260329003', orderNo: 'PO2026032902', styleNo: 'B002', quantity: 30 },
  { id: 4, bundleNo: 'ZB20260329004', orderNo: 'PO2026032902', styleNo: 'B002', quantity: 30 }
])

const workerList = ref([
  { id: 1, name: '张三', workerNo: 'W001' },
  { id: 2, name: '李四', workerNo: 'W002' },
  { id: 3, name: '王五', workerNo: 'W003' }
])

const workerNames = computed(() => workerList.value.map(w => `${w.name}(${w.workerNo})`))
const selectedIds = ref<number[]>([])
const selectedWorker = ref('')
const selectedWorkerId = ref<number>(0)
const dueDate = ref('')
const remark = ref('')

const canSubmit = computed(() => selectedIds.value.length > 0 && selectedWorkerId.value > 0)

const toggleSelect = (id: number) => {
  const idx = selectedIds.value.indexOf(id)
  if (idx > -1) {
    selectedIds.value.splice(idx, 1)
  } else {
    selectedIds.value.push(id)
  }
}

const onWorkerChange = (e: any) => {
  const idx = Number(e.detail.value)
  selectedWorker.value = workerNames.value[idx]
  selectedWorkerId.value = workerList.value[idx].id
}

const onDateChange = (e: any) => {
  dueDate.value = e.detail.value
}

const handleSubmit = () => {
  uni.showModal({
    title: '确认分配',
    content: `将 ${selectedIds.value.length} 个扎包分配给 ${selectedWorker.value}？`,
    success: (res) => {
      if (res.confirm) {
        uni.showToast({ title: '分配成功', icon: 'success' })
        selectedIds.value = []
        selectedWorker.value = ''
        selectedWorkerId.value = 0
        dueDate.value = ''
        remark.value = ''
      }
    }
  })
}
</script>

<style scoped>
.container { padding: 20rpx; background: #f5f5f5; min-height: 100vh; }
.section { background: #fff; border-radius: 16rpx; padding: 30rpx; margin-bottom: 20rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05); }
.section-title { font-size: 30rpx; font-weight: 600; color: #333; margin-bottom: 20rpx; display: block; }
.bundle-list { display: flex; flex-direction: column; gap: 16rpx; }
.bundle-item { display: flex; align-items: center; gap: 20rpx; padding: 16rpx 20rpx; background: #fafafa; border-radius: 12rpx; }
.checkbox {
  width: 40rpx; height: 40rpx; border: 2rpx solid #ddd; border-radius: 8rpx;
  display: flex; align-items: center; justify-content: center;
  font-size: 24rpx; color: #fff; flex-shrink: 0;
}
.checkbox.checked { background: #1890ff; border-color: #1890ff; }
.bundle-info { display: flex; flex-direction: column; gap: 4rpx; }
.bundle-no { font-size: 28rpx; font-weight: 500; color: #333; }
.bundle-detail { font-size: 24rpx; color: #999; }
.form-group { margin-bottom: 24rpx; }
.form-label { display: block; font-size: 26rpx; color: #666; margin-bottom: 12rpx; }
.picker-value {
  padding: 16rpx 20rpx; background: #fafafa; border-radius: 12rpx;
  font-size: 28rpx; color: #333; border: 1rpx solid #eee;
}
.textarea {
  width: 100%; padding: 16rpx 20rpx; background: #fafafa; border-radius: 12rpx;
  font-size: 28rpx; color: #333; border: 1rpx solid #eee; box-sizing: border-box;
  height: 150rpx;
}
.selected-info {
  text-align: center; font-size: 26rpx; color: #1890ff; padding: 20rpx 0;
}
.submit-btn {
  background: #1890ff; color: #fff; border-radius: 16rpx; padding: 24rpx 0;
  font-size: 32rpx; font-weight: 600; border: none; margin-top: 20rpx;
}
.submit-btn[disabled] { background: #ccc; }
.empty-state { padding: 40rpx; text-align: center; }
.empty-text { font-size: 26rpx; color: #ccc; }
</style>
