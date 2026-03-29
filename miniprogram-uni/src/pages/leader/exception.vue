<template>
  <view class="container">
    <view class="filter-bar">
      <view
        v-for="tab in typeTabs"
        :key="tab.value"
        class="filter-item"
        :class="{ active: currentType === tab.value }"
        @click="currentType = tab.value"
      >
        <text>{{ tab.label }}</text>
      </view>
    </view>

    <view v-if="filteredExceptions.length" class="exception-list">
      <view v-for="item in filteredExceptions" :key="item.id" class="exception-card" @click="showDetail(item)">
        <view class="exception-header">
          <view class="type-tag" :class="item.typeClass">{{ item.typeName }}</view>
          <text class="time">{{ item.time }}</text>
        </view>
        <text class="exception-title">{{ item.title }}</text>
        <text class="exception-desc">{{ item.description }}</text>
        <view class="exception-status">
          <text :class="'status-' + item.status">{{ item.statusText }}</text>
        </view>
      </view>
    </view>
    <view v-else class="empty-state">
      <text class="empty-text">暂无异常记录</text>
    </view>

    <view v-if="showModal" class="modal-mask" @click="showModal = false">
      <view class="modal-content" @click.stop>
        <text class="modal-title">异常详情</text>
        <view class="detail-row">
          <text class="detail-label">异常类型</text>
          <text class="detail-value">{{ currentException?.typeName }}</text>
        </view>
        <view class="detail-row">
          <text class="detail-label">关联订单</text>
          <text class="detail-value">{{ currentException?.orderNo }}</text>
        </view>
        <view class="detail-row">
          <text class="detail-label">描述</text>
          <text class="detail-value">{{ currentException?.description }}</text>
        </view>
        <view class="form-group">
          <text class="form-label">处理方式</text>
          <picker :range="handleMethods" @change="onMethodChange">
            <view class="picker-value">{{ selectedMethod || '请选择处理方式' }}</view>
          </picker>
        </view>
        <view class="form-group">
          <text class="form-label">备注</text>
          <textarea v-model="handleRemark" placeholder="请输入处理备注" class="textarea" :maxlength="200" />
        </view>
        <button class="submit-btn" @click="submitHandle">提交处理</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getExceptionList, handleException } from '@/api/leader'

const typeTabs = [
  { label: '全部', value: 'all' },
  { label: '质量异常', value: 'quality' },
  { label: '物料异常', value: 'material' },
  { label: '设备异常', value: 'equipment' },
  { label: '延期预警', value: 'delay' }
]

const currentType = ref('all')
const handleMethods = ['返工处理', '更换物料', '设备维修', '调整排期', '其他']
const selectedMethod = ref('')
const handleRemark = ref('')
const showModal = ref(false)
const currentException = ref<any>(null)

const exceptionData = ref([
  { id: 1, typeName: '质量异常', typeClass: 'quality', type: 'quality', title: '缝制线迹歪斜', description: '订单PO2026032901扎包ZB003中发现多件缝制线迹歪斜', orderNo: 'PO2026032901', time: '2026-03-29 09:30', status: 'pending', statusText: '待处理' },
  { id: 2, typeName: '物料异常', typeClass: 'material', type: 'material', title: '面料色差', description: '订单PO2026032902面料存在色差问题', orderNo: 'PO2026032902', time: '2026-03-29 10:15', status: 'pending', statusText: '待处理' },
  { id: 3, typeName: '设备异常', typeClass: 'equipment', type: 'equipment', title: '缝纫机故障', description: '3号工位平缝机针位偏移', orderNo: '-', time: '2026-03-29 11:00', status: 'processing', statusText: '处理中' },
  { id: 4, typeName: '延期预警', typeClass: 'delay', type: 'delay', title: '订单PO2026032701可能延期', description: '当前完成进度与交期要求存在差距', orderNo: 'PO2026032701', time: '2026-03-29 08:00', status: 'pending', statusText: '待处理' }
])

const filteredExceptions = computed(() => {
  if (currentType.value === 'all') return exceptionData.value
  return exceptionData.value.filter(e => e.type === currentType.value)
})

const showDetail = (item: any) => {
  currentException.value = item
  showModal.value = true
}

const onMethodChange = (e: any) => {
  selectedMethod.value = handleMethods[Number(e.detail.value)]
}

const submitHandle = () => {
  if (!selectedMethod.value) {
    uni.showToast({ title: '请选择处理方式', icon: 'none' })
    return
  }
  uni.showToast({ title: '处理成功', icon: 'success' })
  showModal.value = false
  selectedMethod.value = ''
  handleRemark.value = ''
}
</script>

<style scoped>
.container { padding: 20rpx; background: #f5f5f5; min-height: 100vh; }
.filter-bar { display: flex; flex-wrap: wrap; gap: 12rpx; margin-bottom: 20rpx; background: #fff; border-radius: 16rpx; padding: 16rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05); }
.filter-item { padding: 12rpx 24rpx; border-radius: 24rpx; font-size: 26rpx; color: #666; }
.filter-item.active { background: #1890ff; color: #fff; }
.exception-list { display: flex; flex-direction: column; gap: 20rpx; }
.exception-card { background: #fff; border-radius: 16rpx; padding: 30rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05); }
.exception-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12rpx; }
.type-tag { padding: 4rpx 16rpx; border-radius: 8rpx; font-size: 22rpx; color: #fff; }
.type-tag.quality { background: #ff4d4f; }
.type-tag.material { background: #faad14; }
.type-tag.equipment { background: #722ed1; }
.type-tag.delay { background: #fa8c16; }
.time { font-size: 24rpx; color: #999; }
.exception-title { display: block; font-size: 30rpx; font-weight: 600; color: #333; margin-bottom: 8rpx; }
.exception-desc { display: block; font-size: 26rpx; color: #999; margin-bottom: 12rpx; }
.status-pending { color: #ff4d4f; font-size: 26rpx; }
.status-processing { color: #1890ff; font-size: 26rpx; }
.status-done { color: #52c41a; font-size: 26rpx; }
.modal-mask { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); z-index: 999; display: flex; align-items: center; justify-content: center; }
.modal-content { background: #fff; border-radius: 20rpx; padding: 40rpx; width: 80%; max-height: 80vh; overflow-y: auto; }
.modal-title { display: block; font-size: 34rpx; font-weight: 600; color: #333; margin-bottom: 30rpx; text-align: center; }
.detail-row { display: flex; justify-content: space-between; padding: 16rpx 0; border-bottom: 1rpx solid #f0f0f0; }
.detail-label { font-size: 26rpx; color: #999; }
.detail-value { font-size: 26rpx; color: #333; max-width: 60%; text-align: right; }
.form-group { margin-top: 24rpx; }
.form-label { display: block; font-size: 26rpx; color: #666; margin-bottom: 12rpx; }
.picker-value { padding: 16rpx 20rpx; background: #fafafa; border-radius: 12rpx; font-size: 28rpx; color: #333; border: 1rpx solid #eee; }
.textarea { width: 100%; padding: 16rpx 20rpx; background: #fafafa; border-radius: 12rpx; font-size: 28rpx; border: 1rpx solid #eee; height: 120rpx; box-sizing: border-box; }
.submit-btn { background: #1890ff; color: #fff; border-radius: 12rpx; padding: 20rpx 0; font-size: 30rpx; font-weight: 600; border: none; margin-top: 30rpx; }
.empty-state { padding: 80rpx; text-align: center; }
.empty-text { font-size: 26rpx; color: #ccc; }
</style>
