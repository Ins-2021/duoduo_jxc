<template>
  <div class="bundle-label">
    <div class="label-content" ref="labelRef">
      <div class="label-header">
        <div class="label-title">{{ bundleNo }}</div>
        <div class="label-qr" v-if="qrCodeUrl">
          <img :src="qrCodeUrl" alt="二维码" />
        </div>
      </div>
      <div class="label-body">
        <div class="label-row">
          <span class="label-key">款号:</span>
          <span class="label-value">{{ styleName || '-' }}</span>
        </div>
        <div class="label-row">
          <span class="label-key">颜色:</span>
          <span class="label-value">{{ color || '-' }}</span>
          <span class="label-key">尺码:</span>
          <span class="label-value">{{ size || '-' }}</span>
        </div>
        <div class="label-row">
          <span class="label-key">数量:</span>
          <span class="label-value quantity">{{ quantity }}</span>
        </div>
        <div class="label-row" v-if="processName">
          <span class="label-key">工序:</span>
          <span class="label-value">{{ processName }}</span>
        </div>
      </div>
      <div class="label-footer">
        <div class="label-date">{{ formatDate(new Date()) }}</div>
      </div>
    </div>
    <div class="label-actions">
      <el-button type="primary" @click="handlePrint" :loading="printing">
        <el-icon><Printer /></el-icon>
        打印标签
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Printer } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const props = defineProps<{
  bundleId?: number
  bundleNo: string
  qrCodeNo?: string
  styleName?: string
  color?: string
  size?: string
  quantity?: number
  processName?: string
}>()

const emit = defineEmits<{
  (e: 'print'): void
}>()

const labelRef = ref<HTMLElement | null>(null)
const printing = ref(false)

const qrCodeUrl = computed(() => {
  if (!props.qrCodeNo) return null
  return `/api/mes/qrcode/image/${props.qrCodeNo}?size=120`
})

const formatDate = (date: Date) => {
  return date.toISOString().split('T')[0]
}

const handlePrint = async () => {
  printing.value = true
  try {
    const printWindow = window.open('', '_blank')
    if (!printWindow) {
      ElMessage.warning('请允许弹出窗口')
      return
    }

    printWindow.document.write(`
      <!DOCTYPE html>
      <html>
      <head>
        <title>标签打印 - ${props.bundleNo}</title>
        <style>
          * { margin: 0; padding: 0; box-sizing: border-box; }
          body { font-family: 'Microsoft YaHei', sans-serif; }
          .label {
            width: 80mm;
            height: 50mm;
            padding: 3mm;
            border: 1px solid #000;
            page-break-after: always;
          }
          .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 1px dashed #000;
            padding-bottom: 2mm;
            margin-bottom: 2mm;
          }
          .title {
            font-size: 14pt;
            font-weight: bold;
          }
          .qr img {
            width: 25mm;
            height: 25mm;
          }
          .body {
            font-size: 10pt;
          }
          .row {
            display: flex;
            margin: 1mm 0;
          }
          .key {
            width: 12mm;
            color: #666;
          }
          .value {
            flex: 1;
          }
          .quantity {
            font-weight: bold;
            font-size: 12pt;
            color: #f56c6c;
          }
          .footer {
            border-top: 1px dashed #000;
            padding-top: 2mm;
            margin-top: 2mm;
            font-size: 8pt;
            color: #999;
            text-align: right;
          }
          @media print {
            body { margin: 0; }
            .label { border: none; }
          }
        </style>
      </head>
      <body>
        <div class="label">
          <div class="header">
            <div class="title">${props.bundleNo}</div>
            ${props.qrCodeNo ? `<div class="qr"><img src="${qrCodeUrl.value}" /></div>` : ''}
          </div>
          <div class="body">
            <div class="row">
              <span class="key">款号:</span>
              <span class="value">${props.styleName || '-'}</span>
            </div>
            <div class="row">
              <span class="key">颜色:</span>
              <span class="value">${props.color || '-'}</span>
              <span class="key">尺码:</span>
              <span class="value">${props.size || '-'}</span>
            </div>
            <div class="row">
              <span class="key">数量:</span>
              <span class="value quantity">${props.quantity || 0}</span>
            </div>
            ${props.processName ? `<div class="row"><span class="key">工序:</span><span class="value">${props.processName}</span></div>` : ''}
          </div>
          <div class="footer">
            ${formatDate(new Date())}
          </div>
        </div>
      </body>
      </html>
    `)

    printWindow.document.close()
    printWindow.focus()
    printWindow.print()
    printWindow.close()

    emit('print')
    ElMessage.success('打印成功')
  } catch (e) {
    ElMessage.error('打印失败')
  } finally {
    printing.value = false
  }
}
</script>

<style scoped>
.bundle-label {
  display: inline-block;
}
.label-content {
  width: 240px;
  padding: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #fff;
}
.label-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px dashed #dcdfe6;
  padding-bottom: 8px;
  margin-bottom: 8px;
}
.label-title {
  font-size: 16px;
  font-weight: bold;
}
.label-qr img {
  width: 60px;
  height: 60px;
}
.label-body {
  font-size: 13px;
}
.label-row {
  display: flex;
  margin: 4px 0;
}
.label-key {
  width: 40px;
  color: #909399;
}
.label-value {
  flex: 1;
}
.label-value.quantity {
  font-weight: bold;
  font-size: 15px;
  color: #f56c6c;
}
.label-footer {
  border-top: 1px dashed #dcdfe6;
  padding-top: 8px;
  margin-top: 8px;
  font-size: 11px;
  color: #909399;
  text-align: right;
}
.label-actions {
  margin-top: 10px;
  text-align: center;
}
</style>
