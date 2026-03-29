<template>
  <view class="container">
    <view class="scan-section">
      <view class="scan-input-wrap">
        <input
          v-model="scanCode"
          class="scan-input"
          placeholder="请扫描或输入物料编码"
          focus
          confirm-type="search"
          @confirm="onScan"
        />
        <view class="scan-btn" @click="onScan">查询</view>
      </view>
    </view>

    <view v-if="materialInfo" class="material-info">
      <view class="info-header">物料信息</view>
      <view class="info-row"><text class="label">物料编码</text><text>{{ materialInfo.code }}</text></view>
      <view class="info-row"><text class="label">物料名称</text><text>{{ materialInfo.name }}</text></view>
      <view class="info-row"><text class="label">规格</text><text>{{ materialInfo.spec }}</text></view>
      <view class="info-row"><text class="label">当前库存</text><text>{{ materialInfo.stock }}</text></view>
    </view>

    <view v-if="materialInfo" class="form-section">
      <view class="form-group">
        <text class="form-label">出库数量</text>
        <input v-model="formData.quantity" type="number" class="form-input" placeholder="请输入出库数量" />
      </view>

      <view class="form-group">
        <text class="form-label">领用人</text>
        <input v-model="formData.receiver" class="form-input" placeholder="请输入领用人姓名" />
      </view>

      <view class="form-group">
        <text class="form-label">出库类型</text>
        <radio-group @change="(e: any) => formData.type = e.detail.value">
          <label class="radio-item"><radio value="production" :checked="formData.type === 'production'" />生产领料</label>
          <label class="radio-item"><radio value="return" :checked="formData.type === 'return'" />退货出库</label>
          <label class="radio-item"><radio value="other" :checked="formData.type === 'other'" />其他</label>
        </radio-group>
      </view>

      <view class="form-group">
        <text class="form-label">备注</text>
        <textarea v-model="formData.remark" class="form-textarea" placeholder="请输入备注" maxlength="200" />
      </view>

      <button class="submit-btn" :loading="submitting" @click="onSubmit">提交出库</button>
    </view>

    <view v-if="!materialInfo && !loading" class="empty-state">
      <text>请扫描物料编码进行出库</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { getStockList, outbound } from '@/api/warehouse'

const scanCode = ref('')
const materialInfo = ref<any>(null)
const loading = ref(false)
const submitting = ref(false)

const formData = reactive({
  quantity: '',
  receiver: '',
  type: 'production',
  remark: ''
})

const onScan = async () => {
  if (!scanCode.value.trim()) {
    uni.showToast({ title: '请输入物料编码', icon: 'none' })
    return
  }
  loading.value = true
  try {
    const res = await getStockList({ code: scanCode.value.trim() })
    const list = res.data || []
    materialInfo.value = list.length > 0 ? list[0] : null
    if (!materialInfo.value) {
      uni.showToast({ title: '未找到该物料', icon: 'none' })
    }
  } catch {
    materialInfo.value = null
  } finally {
    loading.value = false
  }
}

const onSubmit = async () => {
  if (!formData.quantity) {
    uni.showToast({ title: '请输入出库数量', icon: 'none' })
    return
  }
  if (!formData.receiver.trim()) {
    uni.showToast({ title: '请输入领用人', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    await outbound({
      materialCode: scanCode.value,
      quantity: Number(formData.quantity),
      receiver: formData.receiver,
      type: formData.type,
      remark: formData.remark
    })
    uni.showToast({ title: '出库成功', icon: 'success' })
    resetForm()
  } catch {} finally {
    submitting.value = false
  }
}

const resetForm = () => {
  scanCode.value = ''
  materialInfo.value = null
  formData.quantity = ''
  formData.receiver = ''
  formData.type = 'production'
  formData.remark = ''
}
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
.material-info, .form-section {
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
.form-group { margin-bottom: 28rpx; }
.form-label { display: block; font-size: 28rpx; font-weight: 600; color: #333; margin-bottom: 12rpx; }
.form-input, .form-textarea {
  width: 100%; font-size: 28rpx; padding: 20rpx;
  background: #f5f5f5; border-radius: 12rpx; box-sizing: border-box;
}
.form-textarea { height: 140rpx; }
.radio-item {
  display: inline-flex; align-items: center; gap: 8rpx;
  font-size: 28rpx; margin-right: 32rpx; margin-top: 12rpx;
}
.submit-btn {
  background: #1890ff; color: #fff; font-size: 32rpx;
  border-radius: 12rpx; height: 88rpx; line-height: 88rpx; border: none;
}
.empty-state { text-align: center; padding: 100rpx 0; color: #999; font-size: 28rpx; }
</style>
