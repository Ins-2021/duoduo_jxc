<template>
  <view class="container">
    <view class="offline-tip" v-if="isOffline">
      <text>⚠️ 离线模式，数据将缓存后自动同步</text>
    </view>
    <scan-input v-model="scanCode" placeholder="请扫描或输入订单条码" @confirm="handleScan" />
    <view class="form-section" v-if="taskInfo">
      <view class="form-card">
        <view class="form-title">裁床信息</view>
        <view class="form-row"><text class="label">款号</text><text class="value">{{ taskInfo.styleCode }}</text></view>
        <view class="form-row"><text class="label">款名</text><text class="value">{{ taskInfo.styleName || '-' }}</text></view>
        <view class="form-row"><text class="label">颜色</text>
          <picker :range="colorOptions" @change="onColorPick">
            <text class="value picker-value">{{ form.color || '请选择' }} ›</text>
          </picker>
        </view>
        <view class="form-row"><text class="label">尺码</text>
          <picker :range="sizeOptions" @change="onSizePick">
            <text class="value picker-value">{{ form.size || '请选择' }} ›</text>
          </picker>
        </view>
        <view class="form-row"><text class="label">计划数量</text><text class="value">{{ taskInfo.planQuantity || 0 }}</text></view>
        <view class="form-row">
          <text class="label">裁剪数量</text>
          <input class="form-input" type="number" v-model="form.quantity" placeholder="请输入" />
        </view>
        <view class="form-row">
          <text class="label">层数</text>
          <input class="form-input" type="number" v-model="form.layers" placeholder="请输入" />
        </view>
        <view class="form-row">
          <text class="label">用布长度(米)</text>
          <input class="form-input" type="digit" v-model="fabricLength" placeholder="请输入" />
        </view>
        <view class="form-row">
          <text class="label">备注</text>
          <input class="form-input" type="text" v-model="form.remark" placeholder="选填" />
        </view>
      </view>
      <view class="submit-section">
        <view class="submit-btn" :class="{ disabled: submitting }" @click="handleSubmit">
          {{ submitting ? '提交中...' : '提交裁床记录' }}
        </view>
      </view>
    </view>
    <view v-if="!taskInfo && !loading" class="empty">请扫描订单条码获取裁床信息</view>
    <view class="records-section" v-if="todayRecords.length > 0">
      <view class="section-title">今日裁床记录</view>
      <view class="record-list">
        <view class="record-item" v-for="item in todayRecords" :key="item.id">
          <view class="record-left">
            <text class="style-code">{{ item.styleCode }}</text>
            <text class="record-desc">{{ item.color }} / {{ item.size }} · {{ item.layers }}层</text>
          </view>
          <view class="record-right">
            <text class="quantity">{{ item.quantity }}件</text>
            <text class="time">{{ item.createTime?.slice(11, 16) || '' }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import ScanInput from '@/components/scan-input/scan-input.vue'
import { getCuttingTaskDetail, submitCuttingInput, getTodayRecords } from '@/api/cutter'
import { useOfflineStore } from '@/store/offline'

const offlineStore = useOfflineStore()
const isOffline = ref(offlineStore.isOffline)
const scanCode = ref('')
const loading = ref(false)
const submitting = ref(false)
const taskId = ref('')

const taskInfo = ref<any>(null)
const colorOptions = ref<string[]>([])
const sizeOptions = ref<string[]>([])
const form = ref({ color: '', size: '', quantity: '', layers: '', remark: '' })
const fabricLength = ref('')
const todayRecords = ref<any[]>([])

onLoad((options: any) => {
  if (options?.taskId) {
    taskId.value = options.taskId
    loadTaskInfo(options.taskId)
  }
})

onShow(() => { loadTodayRecords() })

const handleScan = async (code: string) => {
  if (!code) return
  loading.value = true
  try {
    const res: any = await getCuttingTaskDetail(code)
    if (res.success) {
      taskInfo.value = res.data
      colorOptions.value = res.data.colors || []
      sizeOptions.value = res.data.sizes || []
      if (colorOptions.value.length > 0) form.value.color = colorOptions.value[0]
      if (sizeOptions.value.length > 0) form.value.size = sizeOptions.value[0]
    } else {
      uni.showToast({ title: res.message || '未找到任务', icon: 'none' })
    }
  } catch {
    uni.showToast({ title: '获取任务信息失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const loadTaskInfo = async (id: string) => {
  loading.value = true
  try {
    const res: any = await getCuttingTaskDetail(id)
    if (res.success) {
      taskInfo.value = res.data
      colorOptions.value = res.data.colors || []
      sizeOptions.value = res.data.sizes || []
    }
  } catch {
    uni.showToast({ title: '获取任务信息失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const onColorPick = (e: any) => { form.value.color = colorOptions.value[e.detail.value] }
const onSizePick = (e: any) => { form.value.size = sizeOptions.value[e.detail.value] }

const handleSubmit = async () => {
  if (!form.value.quantity || !form.value.layers) {
    uni.showToast({ title: '请填写裁剪数量和层数', icon: 'none' }); return
  }
  submitting.value = true
  try {
    const data = {
      taskId: taskInfo.value?.id || taskId.value,
      color: form.value.color,
      size: form.value.size,
      quantity: Number(form.value.quantity),
      layers: Number(form.value.layers),
      fabricLength: fabricLength.value ? Number(fabricLength.value) : null,
      remark: form.value.remark
    }
    if (isOffline.value) {
      await offlineStore.addQueue('cutterRecord', data)
      uni.showToast({ title: '已保存到本地', icon: 'none' })
    } else {
      await submitCuttingInput(data)
      uni.showToast({ title: '提交成功', icon: 'success' })
    }
    form.value = { color: '', size: '', quantity: '', layers: '', remark: '' }
    fabricLength.value = ''
    loadTodayRecords()
  } catch {
    uni.showToast({ title: '提交失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}

const loadTodayRecords = async () => {
  try {
    const res: any = await getTodayRecords()
    if (res.success) todayRecords.value = res.data || []
  } catch {}
}
</script>

<style scoped>
.offline-tip {
  background: #fff7e6;
  color: #fa8c16;
  padding: 16rpx 24rpx;
  border-radius: 8rpx;
  margin-bottom: 20rpx;
  font-size: 26rpx;
  text-align: center;
}
.form-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin: 20rpx 0;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.form-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 20rpx;
}
.form-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}
.form-row:last-child { border-bottom: none; }
.form-row .label { font-size: 28rpx; color: #999; flex-shrink: 0; margin-right: 20rpx; }
.form-row .value { font-size: 28rpx; color: #333; }
.picker-value { color: #1890ff; }
.form-input {
  flex: 1;
  text-align: right;
  font-size: 28rpx;
  color: #333;
}
.submit-section { margin: 30rpx 0; }
.submit-btn {
  background: linear-gradient(135deg, #722ed1, #531dab);
  color: #fff;
  text-align: center;
  padding: 28rpx;
  border-radius: 60rpx;
  font-size: 32rpx;
  box-shadow: 0 8rpx 24rpx rgba(114,46,209,0.3);
}
.submit-btn.disabled { opacity: 0.6; }
.records-section { margin-top: 30rpx; }
.section-title { font-size: 30rpx; font-weight: 600; color: #333; margin-bottom: 16rpx; }
.record-list { display: flex; flex-direction: column; gap: 12rpx; }
.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-radius: 12rpx;
  padding: 20rpx 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
}
.record-left { display: flex; flex-direction: column; gap: 4rpx; }
.style-code { font-size: 28rpx; color: #333; font-weight: 500; }
.record-desc { font-size: 24rpx; color: #999; }
.record-right { display: flex; flex-direction: column; align-items: flex-end; gap: 4rpx; }
.quantity { font-size: 28rpx; color: #722ed1; font-weight: 600; }
.time { font-size: 22rpx; color: #bbb; }
.empty { text-align: center; color: #999; padding: 80rpx; font-size: 28rpx; }
</style>
