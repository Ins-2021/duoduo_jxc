<template>
  <view class="container">
    <view class="filter-bar">
      <view
        v-for="tab in statusTabs"
        :key="tab.value"
        class="filter-item"
        :class="{ active: currentStatus === tab.value }"
        @click="currentStatus = tab.value"
      >
        <text>{{ tab.label }}</text>
      </view>
    </view>

    <view v-if="filteredApprovals.length" class="approval-list">
      <view v-for="item in filteredApprovals" :key="item.id" class="approval-card" @click="showDetail(item)">
        <view class="approval-header">
          <view class="type-tag" :class="item.typeClass">{{ item.typeName }}</view>
          <text class="status" :class="'status-' + item.status">{{ item.statusText }}</text>
        </view>
        <text class="approval-title">{{ item.title }}</text>
        <view class="approval-meta">
          <text>单号: {{ item.no }}</text>
          <text>申请人: {{ item.applicant }}</text>
        </view>
        <text class="approval-time">{{ item.time }}</text>
      </view>
    </view>
    <view v-else class="empty-state">
      <text class="empty-text">暂无审批记录</text>
    </view>

    <view v-if="showModal" class="modal-mask" @click="showModal = false">
      <view class="modal-content" @click.stop>
        <text class="modal-title">审批详情</text>
        <view class="detail-row">
          <text class="detail-label">审批类型</text>
          <text class="detail-value">{{ currentApproval?.typeName }}</text>
        </view>
        <view class="detail-row">
          <text class="detail-label">单号</text>
          <text class="detail-value">{{ currentApproval?.no }}</text>
        </view>
        <view class="detail-row">
          <text class="detail-label">申请人</text>
          <text class="detail-value">{{ currentApproval?.applicant }}</text>
        </view>
        <view class="detail-row">
          <text class="detail-label">申请事由</text>
          <text class="detail-value">{{ currentApproval?.reason }}</text>
        </view>
        <view class="detail-row">
          <text class="detail-label">申请时间</text>
          <text class="detail-value">{{ currentApproval?.time }}</text>
        </view>
        <view v-if="currentApproval?.status === 'pending'" class="action-buttons">
          <button class="btn-reject" @click="handleApprove('reject')">驳回</button>
          <button class="btn-approve" @click="handleApprove('approve')">通过</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getApprovalList, approve } from '@/api/leader'

const statusTabs = [
  { label: '全部', value: 'all' },
  { label: '待审批', value: 'pending' },
  { label: '已通过', value: 'approved' },
  { label: '已驳回', value: 'rejected' }
]

const currentStatus = ref('all')
const showModal = ref(false)
const currentApproval = ref<any>(null)

const approvalData = ref([
  { id: 1, typeName: '请假审批', typeClass: 'leave', type: 'leave', title: '张三请假申请', no: 'LV20260329001', applicant: '张三', reason: '家中有事需请假1天', time: '2026-03-29 09:30', status: 'pending', statusText: '待审批' },
  { id: 2, typeName: '加班申请', typeClass: 'overtime', type: 'overtime', title: '李四加班申请', no: 'OT20260329001', applicant: '李四', reason: '订单紧急需加班赶工', time: '2026-03-29 10:15', status: 'pending', statusText: '待审批' },
  { id: 3, typeName: '物料申请', typeClass: 'material', type: 'material', title: '王五物料领用', no: 'MA20260328001', applicant: '王五', reason: '缝线不足需补充', time: '2026-03-28 14:00', status: 'approved', statusText: '已通过' },
  { id: 4, typeName: '请假审批', typeClass: 'leave', type: 'leave', title: '赵六请假申请', no: 'LV20260327001', applicant: '赵六', reason: '身体不适需请假', time: '2026-03-27 16:00', status: 'rejected', statusText: '已驳回' }
])

const filteredApprovals = computed(() => {
  if (currentStatus.value === 'all') return approvalData.value
  return approvalData.value.filter(a => a.status === currentStatus.value)
})

const showDetail = (item: any) => {
  currentApproval.value = item
  showModal.value = true
}

const handleApprove = (action: string) => {
  const msg = action === 'approve' ? '确认通过该审批？' : '确认驳回该审批？'
  uni.showModal({
    title: '提示',
    content: msg,
    success: (res) => {
      if (res.confirm) {
        uni.showToast({ title: action === 'approve' ? '审批通过' : '已驳回', icon: 'success' })
        showModal.value = false
      }
    }
  })
}
</script>

<style scoped>
.container { padding: 20rpx; background: #f5f5f5; min-height: 100vh; }
.filter-bar { display: flex; gap: 16rpx; margin-bottom: 20rpx; background: #fff; border-radius: 16rpx; padding: 16rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05); }
.filter-item { flex: 1; text-align: center; padding: 16rpx 0; border-radius: 12rpx; font-size: 26rpx; color: #666; }
.filter-item.active { background: #1890ff; color: #fff; font-weight: 600; }
.approval-list { display: flex; flex-direction: column; gap: 20rpx; }
.approval-card { background: #fff; border-radius: 16rpx; padding: 30rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05); }
.approval-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12rpx; }
.type-tag { padding: 4rpx 16rpx; border-radius: 8rpx; font-size: 22rpx; color: #fff; }
.type-tag.leave { background: #1890ff; }
.type-tag.overtime { background: #faad14; }
.type-tag.material { background: #722ed1; }
.status-pending { color: #faad14; font-size: 26rpx; font-weight: 600; }
.status-approved { color: #52c41a; font-size: 26rpx; }
.status-rejected { color: #ff4d4f; font-size: 26rpx; }
.approval-title { display: block; font-size: 30rpx; font-weight: 600; color: #333; margin-bottom: 8rpx; }
.approval-meta { display: flex; gap: 40rpx; font-size: 24rpx; color: #999; margin-bottom: 8rpx; }
.approval-time { font-size: 24rpx; color: #ccc; }
.modal-mask { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); z-index: 999; display: flex; align-items: center; justify-content: center; }
.modal-content { background: #fff; border-radius: 20rpx; padding: 40rpx; width: 80%; max-height: 80vh; overflow-y: auto; }
.modal-title { display: block; font-size: 34rpx; font-weight: 600; color: #333; margin-bottom: 30rpx; text-align: center; }
.detail-row { display: flex; justify-content: space-between; padding: 16rpx 0; border-bottom: 1rpx solid #f0f0f0; }
.detail-label { font-size: 26rpx; color: #999; }
.detail-value { font-size: 26rpx; color: #333; max-width: 60%; text-align: right; }
.action-buttons { display: flex; gap: 20rpx; margin-top: 40rpx; }
.action-buttons button { flex: 1; border-radius: 12rpx; padding: 20rpx 0; font-size: 30rpx; font-weight: 600; border: none; }
.btn-approve { background: #1890ff; color: #fff; }
.btn-reject { background: #ff4d4f; color: #fff; }
.empty-state { padding: 80rpx; text-align: center; }
.empty-text { font-size: 26rpx; color: #ccc; }
</style>
