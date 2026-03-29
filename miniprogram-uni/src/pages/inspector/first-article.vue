<template>
  <view class="container">
    <view v-if="list.length > 0" class="list-section">
      <view v-for="item in list" :key="item.id" class="list-item" @click="openDetail(item)">
        <view class="item-header">
          <text class="style-no">{{ item.styleNo }}</text>
          <text class="status-tag" :class="getStatusClass(item.status)">{{ getStatusText(item.status) }}</text>
        </view>
        <view class="item-info">
          <text class="info-text">颜色：{{ item.color }}</text>
          <text class="info-text">尺码：{{ item.size }}</text>
          <text class="info-text">工序：{{ item.processName }}</text>
        </view>
        <view class="item-footer">
          <text class="time">{{ item.createTime }}</text>
          <text class="arrow">查看详情 ›</text>
        </view>
      </view>
    </view>

    <view v-else class="empty-state">
      <text>暂无待确认首件</text>
    </view>

    <!-- 详情弹窗 -->
    <uni-popup ref="detailPopup" type="bottom" :safe-area="true">
      <view v-if="currentItem" class="detail-panel">
        <view class="panel-header">
          <text class="panel-title">首件确认详情</text>
          <text class="panel-close" @click="closeDetail">×</text>
        </view>
        <view class="detail-row"><text class="label">款号</text><text>{{ currentItem.styleNo }}</text></view>
        <view class="detail-row"><text class="label">颜色</text><text>{{ currentItem.color }}</text></view>
        <view class="detail-row"><text class="label">尺码</text><text>{{ currentItem.size }}</text></view>
        <view class="detail-row"><text class="label">工序</text><text>{{ currentItem.processName }}</text></view>
        <view class="detail-row"><text class="label">生产人</text><text>{{ currentItem.workerName }}</text></view>
        <view class="detail-row"><text class="label">提交时间</text><text>{{ currentItem.createTime }}</text></view>
        <view class="form-group">
          <text class="form-label">备注</text>
          <textarea v-model="remark" class="form-textarea" placeholder="请输入备注" maxlength="200" />
        </view>
        <view class="btn-group">
          <button class="btn btn-confirm" :loading="confirmLoading" @click="onConfirm">确认通过</button>
          <button class="btn btn-reject" :loading="rejectLoading" @click="onReject">驳回</button>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getPendingFirstArticles, confirmFirstArticle } from '@/api/inspector'

const list = ref<any[]>([])
const detailPopup = ref()
const currentItem = ref<any>(null)
const remark = ref('')
const confirmLoading = ref(false)
const rejectLoading = ref(false)

const loadList = async () => {
  try {
    const res = await getPendingFirstArticles()
    list.value = res.data || []
  } catch {}
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = { pending: '待确认', approved: '已确认', rejected: '已驳回' }
  return map[status] || status
}

const getStatusClass = (status: string) => {
  const map: Record<string, string> = { pending: 'status-pending', approved: 'status-pass', rejected: 'status-reject' }
  return map[status] || ''
}

const openDetail = (item: any) => {
  currentItem.value = item
  remark.value = ''
  detailPopup.value?.open()
}

const closeDetail = () => { detailPopup.value?.close() }

const onConfirm = async () => {
  if (!currentItem.value) return
  confirmLoading.value = true
  try {
    await confirmFirstArticle(currentItem.value.id, { remark: remark.value, action: 'confirm' })
    uni.showToast({ title: '确认成功', icon: 'success' })
    closeDetail()
    loadList()
  } catch {} finally {
    confirmLoading.value = false
  }
}

const onReject = async () => {
  if (!currentItem.value) return
  rejectLoading.value = true
  try {
    await confirmFirstArticle(currentItem.value.id, { remark: remark.value, action: 'reject' })
    uni.showToast({ title: '已驳回', icon: 'success' })
    closeDetail()
    loadList()
  } catch {} finally {
    rejectLoading.value = false
  }
}

onShow(() => { loadList() })
</script>

<style scoped>
.container { padding: 24rpx; }
.list-section { display: flex; flex-direction: column; gap: 20rpx; }
.list-item {
  background: #fff; border-radius: 16rpx; padding: 28rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.item-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; }
.style-no { font-size: 32rpx; font-weight: 600; color: #333; }
.status-tag {
  font-size: 22rpx; padding: 4rpx 16rpx; border-radius: 6rpx; color: #fff;
}
.status-pending { background: #faad14; }
.status-pass { background: #52c41a; }
.status-reject { background: #ff4d4f; }
.item-info { display: flex; gap: 24rpx; margin-bottom: 16rpx; }
.info-text { font-size: 26rpx; color: #666; }
.item-footer { display: flex; justify-content: space-between; align-items: center; }
.time { font-size: 24rpx; color: #999; }
.arrow { font-size: 26rpx; color: #1890ff; }
.detail-panel {
  background: #fff; border-radius: 24rpx 24rpx 0 0; padding: 30rpx; max-height: 80vh; overflow-y: auto;
}
.panel-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30rpx; }
.panel-title { font-size: 34rpx; font-weight: 600; color: #333; }
.panel-close { font-size: 40rpx; color: #999; padding: 0 10rpx; }
.detail-row {
  display: flex; justify-content: space-between; padding: 16rpx 0;
  border-bottom: 1rpx solid #f5f5f5; font-size: 28rpx; color: #333;
}
.detail-row .label { color: #999; }
.form-group { margin: 24rpx 0; }
.form-label { display: block; font-size: 28rpx; font-weight: 600; color: #333; margin-bottom: 12rpx; }
.form-textarea { width: 100%; font-size: 28rpx; padding: 20rpx; background: #f5f5f5; border-radius: 12rpx; height: 140rpx; box-sizing: border-box; }
.btn-group { display: flex; gap: 20rpx; margin-top: 30rpx; }
.btn {
  flex: 1; height: 88rpx; line-height: 88rpx; text-align: center;
  font-size: 30rpx; border-radius: 12rpx; border: none;
}
.btn-confirm { background: #52c41a; color: #fff; }
.btn-reject { background: #ff4d4f; color: #fff; }
.empty-state { text-align: center; padding: 100rpx 0; color: #999; font-size: 28rpx; }
</style>
