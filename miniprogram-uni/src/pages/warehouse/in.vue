<template>
  <view class="container">
    <view class="scan-section">
      <view class="scan-input-wrap">
        <input
          v-model="bundleNo"
          class="scan-input"
          placeholder="请扫描或输入扎包号"
          focus
          confirm-type="search"
          @confirm="onScan"
        />
        <view class="scan-btn" @click="onScan">查询</view>
      </view>
    </view>

    <view v-if="bundleInfo" class="bundle-info">
      <view class="info-header">扎包信息</view>
      <view class="info-row"><text class="label">款号</text><text>{{ bundleInfo.styleNo }}</text></view>
      <view class="info-row"><text class="label">颜色</text><text>{{ bundleInfo.color }}</text></view>
      <view class="info-row"><text class="label">尺码</text><text>{{ bundleInfo.size }}</text></view>
      <view class="info-row"><text class="label">数量</text><text>{{ bundleInfo.quantity }}</text></view>
      <view class="info-row"><text class="label">质检状态</text>
        <text :class="bundleInfo.qualityStatus === 'pass' ? 'status-pass' : 'status-fail'">
          {{ bundleInfo.qualityStatus === 'pass' ? '已通过' : '未通过' }}
        </text>
      </view>
    </view>

    <view v-if="bundleInfo && bundleInfo.qualityStatus === 'pass'" class="form-section">
      <view class="form-group">
        <text class="form-label">入库仓库</text>
        <picker :range="warehouses" range-key="name" @change="onWarehouseChange">
          <view class="picker-value">{{ selectedWarehouseName || '请选择仓库' }}</view>
        </picker>
      </view>

      <view class="form-group">
        <text class="form-label">库位</text>
        <input v-model="formData.location" class="form-input" placeholder="请输入库位编号" />
      </view>

      <view class="form-group">
        <text class="form-label">备注</text>
        <textarea v-model="formData.remark" class="form-textarea" placeholder="请输入备注" maxlength="200" />
      </view>

      <button class="submit-btn" :loading="submitting" @click="onSubmit">提交入库</button>
    </view>

    <view v-if="bundleInfo && bundleInfo.qualityStatus !== 'pass'" class="warn-tip">
      <text>该扎包质检未通过，无法入库</text>
    </view>

    <view class="today-section">
      <view class="section-title">今日入库记录</view>
      <view v-if="todayRecords.length > 0">
        <view v-for="item in todayRecords" :key="item.id" class="record-item">
          <view class="record-info">
            <text class="record-no">{{ item.bundleNo }}</text>
            <text class="record-detail">{{ item.styleNo }} - {{ item.color }}/{{ item.size }} × {{ item.quantity }}</text>
          </view>
          <text class="record-time">{{ item.createTime }}</text>
        </view>
      </view>
      <view v-else class="empty-state">
        <text>暂无今日入库记录</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getBundleInfo } from '@/api/inspector'
import { inbound, getTodayRecords } from '@/api/warehouse'

const bundleNo = ref('')
const bundleInfo = ref<any>(null)
const submitting = ref(false)
const todayRecords = ref<any[]>([])

const warehouses = ref([
  { id: 1, name: '成品仓A' },
  { id: 2, name: '成品仓B' },
  { id: 3, name: '半成品仓' }
])
const selectedWarehouseName = ref('')

const formData = reactive({
  warehouseId: '',
  location: '',
  remark: ''
})

const onScan = async () => {
  if (!bundleNo.value.trim()) {
    uni.showToast({ title: '请输入扎包号', icon: 'none' })
    return
  }
  try {
    const res = await getBundleInfo(bundleNo.value.trim())
    bundleInfo.value = res.data
  } catch {
    bundleInfo.value = null
  }
}

const onWarehouseChange = (e: any) => {
  const idx = Number(e.detail.value)
  formData.warehouseId = String(warehouses.value[idx].id)
  selectedWarehouseName.value = warehouses.value[idx].name
}

const loadTodayRecords = async () => {
  try {
    const res = await getTodayRecords('in')
    todayRecords.value = res.data || []
  } catch {}
}

const onSubmit = async () => {
  if (!formData.warehouseId) {
    uni.showToast({ title: '请选择仓库', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    await inbound({
      bundleNo: bundleNo.value,
      warehouseId: formData.warehouseId,
      location: formData.location,
      remark: formData.remark
    })
    uni.showToast({ title: '入库成功', icon: 'success' })
    bundleNo.value = ''
    bundleInfo.value = null
    formData.warehouseId = ''
    formData.location = ''
    formData.remark = ''
    selectedWarehouseName.value = ''
    loadTodayRecords()
  } catch {} finally {
    submitting.value = false
  }
}

onShow(() => { loadTodayRecords() })
</script>

<style scoped>
.container { padding: 24rpx; }
.scan-section { margin-bottom: 24rpx; }
.scan-input-wrap {
  display: flex; gap: 16rpx;
  background: #fff; border-radius: 12rpx; padding: 16rpx 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.scan-input { flex: 1; font-size: 28rpx; height: 72rpx; }
.scan-btn {
  background: #1890ff; color: #fff; font-size: 28rpx;
  padding: 0 32rpx; border-radius: 12rpx;
  display: flex; align-items: center;
}
.bundle-info, .form-section {
  background: #fff; border-radius: 16rpx; padding: 30rpx; margin-bottom: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.info-header { font-size: 30rpx; font-weight: 600; color: #333; margin-bottom: 20rpx; }
.info-row {
  display: flex; justify-content: space-between;
  padding: 12rpx 0; font-size: 28rpx; color: #333;
  border-bottom: 1rpx solid #f5f5f5;
}
.info-row:last-child { border-bottom: none; }
.info-row .label { color: #999; }
.status-pass { color: #52c41a; }
.status-fail { color: #ff4d4f; }
.warn-tip {
  background: #fff7e6; border-radius: 16rpx; padding: 30rpx; margin-bottom: 24rpx;
  text-align: center; color: #faad14; font-size: 28rpx;
  border: 1rpx solid #ffe58f;
}
.form-group { margin-bottom: 28rpx; }
.form-label { display: block; font-size: 28rpx; font-weight: 600; color: #333; margin-bottom: 12rpx; }
.form-input, .form-textarea {
  width: 100%; font-size: 28rpx; padding: 20rpx;
  background: #f5f5f5; border-radius: 12rpx; box-sizing: border-box;
}
.form-textarea { height: 140rpx; }
.picker-value {
  font-size: 28rpx; padding: 20rpx; background: #f5f5f5;
  border-radius: 12rpx; color: #333;
}
.submit-btn {
  background: #1890ff; color: #fff; font-size: 32rpx;
  border-radius: 12rpx; height: 88rpx; line-height: 88rpx; border: none;
}
.today-section {
  background: #fff; border-radius: 16rpx; padding: 30rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}
.section-title { font-size: 30rpx; font-weight: 600; color: #333; margin-bottom: 20rpx; }
.record-item {
  display: flex; justify-content: space-between; align-items: center;
  padding: 16rpx 0; border-bottom: 1rpx solid #f5f5f5;
}
.record-item:last-child { border-bottom: none; }
.record-no { font-size: 28rpx; font-weight: 600; color: #333; }
.record-detail { font-size: 24rpx; color: #666; display: block; margin-top: 6rpx; }
.record-time { font-size: 24rpx; color: #999; flex-shrink: 0; }
.empty-state { text-align: center; padding: 40rpx 0; color: #999; font-size: 28rpx; }
</style>
