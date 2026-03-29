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
      <view class="info-row"><text class="label">颜色尺码</text><text>{{ bundleInfo.color }} / {{ bundleInfo.size }}</text></view>
      <view class="info-row"><text class="label">数量</text><text>{{ bundleInfo.quantity }}</text></view>
      <view class="info-row"><text class="label">工序</text><text>{{ bundleInfo.processName }}</text></view>
    </view>

    <view v-if="bundleInfo" class="form-section">
      <view class="form-group">
        <text class="form-label">检验类型</text>
        <radio-group @change="onTypeChange">
          <label class="radio-item"><radio value="sampling" :checked="formData.checkType === 'sampling'" />抽检</label>
          <label class="radio-item"><radio value="full" :checked="formData.checkType === 'full'" />全检</label>
        </radio-group>
      </view>

      <view class="form-group">
        <text class="form-label">检验数量</text>
        <input v-model="formData.checkQuantity" type="number" class="form-input" placeholder="请输入检验数量" />
      </view>

      <view class="form-group">
        <text class="form-label">合格数量</text>
        <input v-model="formData.passQuantity" type="number" class="form-input" placeholder="请输入合格数量" />
      </view>

      <view class="form-group">
        <text class="form-label">质检结果</text>
        <radio-group @change="onResultChange">
          <label class="radio-item"><radio value="pass" :checked="formData.result === 'pass'" color="#52c41a" />合格</label>
          <label class="radio-item"><radio value="rework" :checked="formData.result === 'rework'" color="#faad14" />返工</label>
          <label class="radio-item"><radio value="scrap" :checked="formData.result === 'scrap'" color="#ff4d4f" />报废</label>
        </radio-group>
      </view>

      <view class="form-group">
        <text class="form-label">缺陷类型</text>
        <checkbox-group @change="onDefectChange">
          <label v-for="d in defectTypes" :key="d.value" class="checkbox-item">
            <checkbox :value="d.value" :checked="formData.defects.includes(d.value)" />
            {{ d.label }}
          </label>
        </checkbox-group>
      </view>

      <view class="form-group">
        <text class="form-label">备注</text>
        <textarea v-model="formData.remark" class="form-textarea" placeholder="请输入备注信息" maxlength="200" />
      </view>

      <view class="form-group">
        <text class="form-label">拍照上传</text>
        <view class="upload-area">
          <view v-for="(img, idx) in formData.images" :key="idx" class="upload-item">
            <image :src="img" mode="aspectFill" class="upload-img" />
            <view class="upload-del" @click="removeImage(idx)">×</view>
          </view>
          <view v-if="formData.images.length < 5" class="upload-add" @click="chooseImage">+</view>
        </view>
      </view>

      <button class="submit-btn" :loading="submitting" @click="onSubmit">提交质检</button>
    </view>

    <view v-if="!bundleInfo && !loading" class="empty-state">
      <text>请扫描扎包号进行质检</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { getBundleInfo, submitQualityCheck } from '@/api/inspector'

const bundleNo = ref('')
const bundleInfo = ref<any>(null)
const loading = ref(false)
const submitting = ref(false)

const defectTypes = [
  { label: '尺寸不符', value: 'size_mismatch' },
  { label: '色差', value: 'color_diff' },
  { label: '线头', value: 'thread' },
  { label: '污渍', value: 'stain' },
  { label: '破损', value: 'damage' },
  { label: '工艺问题', value: 'craft' }
]

const formData = reactive({
  checkType: 'sampling',
  checkQuantity: '',
  passQuantity: '',
  result: 'pass',
  defects: [] as string[],
  remark: '',
  images: [] as string[]
})

const onScan = async () => {
  if (!bundleNo.value.trim()) {
    uni.showToast({ title: '请输入扎包号', icon: 'none' })
    return
  }
  loading.value = true
  try {
    const res = await getBundleInfo(bundleNo.value.trim())
    bundleInfo.value = res.data
    formData.checkQuantity = String(bundleInfo.value.quantity || '')
  } catch {
    bundleInfo.value = null
  } finally {
    loading.value = false
  }
}

const onTypeChange = (e: any) => { formData.checkType = e.detail.value }
const onResultChange = (e: any) => { formData.result = e.detail.value }
const onDefectChange = (e: any) => { formData.defects = e.detail.value }

const chooseImage = () => {
  uni.chooseImage({
    count: 5 - formData.images.length,
    sizeType: ['compressed'],
    success: (res) => {
      formData.images.push(...res.tempFilePaths)
    }
  })
}

const removeImage = (idx: number) => { formData.images.splice(idx, 1) }

const onSubmit = async () => {
  if (!formData.checkQuantity) {
    uni.showToast({ title: '请输入检验数量', icon: 'none' })
    return
  }
  if (!formData.passQuantity) {
    uni.showToast({ title: '请输入合格数量', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    await submitQualityCheck({
      bundleNo: bundleNo.value,
      ...formData,
      checkQuantity: Number(formData.checkQuantity),
      passQuantity: Number(formData.passQuantity)
    })
    uni.showToast({ title: '提交成功', icon: 'success' })
    resetForm()
  } catch {} finally {
    submitting.value = false
  }
}

const resetForm = () => {
  bundleNo.value = ''
  bundleInfo.value = null
  formData.checkType = 'sampling'
  formData.checkQuantity = ''
  formData.passQuantity = ''
  formData.result = 'pass'
  formData.defects = []
  formData.remark = ''
  formData.images = []
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
.form-group { margin-bottom: 28rpx; }
.form-label { display: block; font-size: 28rpx; font-weight: 600; color: #333; margin-bottom: 16rpx; }
.form-input, .form-textarea {
  width: 100%; font-size: 28rpx; padding: 20rpx;
  background: #f5f5f5; border-radius: 12rpx; box-sizing: border-box;
}
.form-textarea { height: 160rpx; }
.radio-item, .checkbox-item {
  display: inline-flex; align-items: center; gap: 8rpx;
  font-size: 28rpx; margin-right: 32rpx; margin-top: 12rpx;
}
.upload-area { display: flex; flex-wrap: wrap; gap: 16rpx; }
.upload-item { position: relative; width: 160rpx; height: 160rpx; }
.upload-img { width: 160rpx; height: 160rpx; border-radius: 12rpx; }
.upload-del {
  position: absolute; top: -10rpx; right: -10rpx;
  width: 36rpx; height: 36rpx; background: #ff4d4f; color: #fff;
  border-radius: 50%; font-size: 24rpx; text-align: center; line-height: 36rpx;
}
.upload-add {
  width: 160rpx; height: 160rpx; background: #f5f5f5; border-radius: 12rpx;
  display: flex; align-items: center; justify-content: center;
  font-size: 48rpx; color: #ccc;
}
.submit-btn {
  background: #1890ff; color: #fff; font-size: 32rpx;
  border-radius: 12rpx; height: 88rpx; line-height: 88rpx; border: none;
}
.empty-state { text-align: center; padding: 100rpx 0; color: #999; font-size: 28rpx; }
</style>
