<template>
  <view class="container">
    <view v-if="list.length > 0" class="list-section">
      <view v-for="item in list" :key="item.id" class="list-item" @click="openDetail(item)">
        <view class="item-header">
          <text class="bundle-no">{{ item.bundleNo }}</text>
          <text class="status-tag status-rework">待返工</text>
        </view>
        <view class="item-info">
          <text class="info-text">款号：{{ item.styleNo }}</text>
          <text class="info-text">颜色尺码：{{ item.color }} / {{ item.size }}</text>
        </view>
        <view class="defect-tags">
          <text v-for="d in item.defects" :key="d" class="defect-tag">{{ getDefectLabel(d) }}</text>
        </view>
        <view class="item-footer">
          <text class="time">{{ item.createTime }}</text>
          <text class="arrow">处理 ›</text>
        </view>
      </view>
    </view>

    <view v-else class="empty-state">
      <text>暂无待返工任务</text>
    </view>

    <!-- 返工详情弹窗 -->
    <uni-popup ref="reworkPopup" type="bottom" :safe-area="true">
      <view v-if="currentItem" class="detail-panel">
        <view class="panel-header">
          <text class="panel-title">返工处理</text>
          <text class="panel-close" @click="closeDetail">×</text>
        </view>
        <view class="detail-row"><text class="label">扎包号</text><text>{{ currentItem.bundleNo }}</text></view>
        <view class="detail-row"><text class="label">款号</text><text>{{ currentItem.styleNo }}</text></view>
        <view class="detail-row"><text class="label">颜色尺码</text><text>{{ currentItem.color }} / {{ currentItem.size }}</text></view>
        <view class="detail-row"><text class="label">缺陷类型</text>
          <text>{{ (currentItem.defects || []).map((d: string) => getDefectLabel(d)).join('、') }}</text>
        </view>
        <view class="form-group">
          <text class="form-label">分配返工人</text>
          <input v-model="formData.workerName" class="form-input" placeholder="请输入返工人姓名" />
        </view>
        <view class="form-group">
          <text class="form-label">返工要求</text>
          <textarea v-model="formData.requirement" class="form-textarea" placeholder="请输入返工要求" maxlength="200" />
        </view>
        <view class="form-group">
          <text class="form-label">备注</text>
          <textarea v-model="formData.remark" class="form-textarea" placeholder="请输入备注" maxlength="200" />
        </view>
        <button class="submit-btn" :loading="submitting" @click="onSubmit">提交返工</button>
      </view>
    </uni-popup>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getReworkList, submitRework } from '@/api/inspector'

const list = ref<any[]>([])
const reworkPopup = ref()
const currentItem = ref<any>(null)
const submitting = ref(false)

const defectMap: Record<string, string> = {
  size_mismatch: '尺寸不符', color_diff: '色差', thread: '线头',
  stain: '污渍', damage: '破损', craft: '工艺问题'
}
const getDefectLabel = (key: string) => defectMap[key] || key

const formData = reactive({
  workerName: '',
  requirement: '',
  remark: ''
})

const loadList = async () => {
  try {
    const res = await getReworkList({ status: 'pending' })
    list.value = res.data || []
  } catch {}
}

const openDetail = (item: any) => {
  currentItem.value = item
  formData.workerName = ''
  formData.requirement = ''
  formData.remark = ''
  reworkPopup.value?.open()
}

const closeDetail = () => { reworkPopup.value?.close() }

const onSubmit = async () => {
  if (!formData.workerName.trim()) {
    uni.showToast({ title: '请输入返工人', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    await submitRework({
      id: currentItem.value.id,
      workerName: formData.workerName,
      requirement: formData.requirement,
      remark: formData.remark
    })
    uni.showToast({ title: '提交成功', icon: 'success' })
    closeDetail()
    loadList()
  } catch {} finally {
    submitting.value = false
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
.bundle-no { font-size: 32rpx; font-weight: 600; color: #333; }
.status-tag { font-size: 22rpx; padding: 4rpx 16rpx; border-radius: 6rpx; color: #fff; }
.status-rework { background: #faad14; }
.item-info { display: flex; flex-direction: column; gap: 8rpx; margin-bottom: 16rpx; }
.info-text { font-size: 26rpx; color: #666; }
.defect-tags { display: flex; flex-wrap: wrap; gap: 12rpx; margin-bottom: 16rpx; }
.defect-tag {
  font-size: 22rpx; padding: 4rpx 16rpx; border-radius: 6rpx;
  background: #fff2f0; color: #ff4d4f; border: 1rpx solid #ffccc7;
}
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
.detail-row .label { color: #999; flex-shrink: 0; margin-right: 20rpx; }
.form-group { margin: 24rpx 0; }
.form-label { display: block; font-size: 28rpx; font-weight: 600; color: #333; margin-bottom: 12rpx; }
.form-input, .form-textarea { width: 100%; font-size: 28rpx; padding: 20rpx; background: #f5f5f5; border-radius: 12rpx; box-sizing: border-box; }
.form-textarea { height: 120rpx; }
.submit-btn {
  background: #1890ff; color: #fff; font-size: 32rpx;
  border-radius: 12rpx; height: 88rpx; line-height: 88rpx; border: none; margin-top: 20rpx;
}
.empty-state { text-align: center; padding: 100rpx 0; color: #999; font-size: 28rpx; }
</style>
