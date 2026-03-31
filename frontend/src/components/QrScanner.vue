<template>
  <div class="qr-scanner">
    <div v-if="!scanning" class="scanner-placeholder">
      <el-button type="primary" @click="startScanning">
        <el-icon><Camera /></el-icon>
        开启摄像头扫码
      </el-button>
    </div>
    <div v-else class="scanner-container">
      <video ref="videoRef" class="scanner-video" autoplay playsinline></video>
      <canvas ref="canvasRef" style="display: none;"></canvas>
      <div class="scanner-overlay">
        <div class="scanner-frame"></div>
      </div>
      <div class="scanner-actions">
        <el-button @click="stopScanning">取消</el-button>
        <el-button type="primary" @click="captureAndDecode">扫码</el-button>
      </div>
    </div>
    <div v-if="error" class="scanner-error">
      <el-alert type="error" :title="error" show-icon closable @close="error = ''" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onUnmounted } from 'vue'
import { Camera } from '@element-plus/icons-vue'

const emit = defineEmits<{
  (e: 'scanned', result: string): void
  (e: 'error', message: string): void
}>()

const videoRef = ref<HTMLVideoElement | null>(null)
const canvasRef = ref<HTMLCanvasElement | null>(null)
const scanning = ref(false)
const error = ref('')
let stream: MediaStream | null = null
let animationId: number | null = null

const startScanning = async () => {
  try {
    stream = await navigator.mediaDevices.getUserMedia({
      video: { facingMode: 'environment' }
    })
    if (videoRef.value) {
      videoRef.value.srcObject = stream
      scanning.value = true
      error.value = ''
    }
  } catch (e: any) {
    error.value = '无法访问摄像头: ' + (e.message || '请检查权限设置')
    emit('error', error.value)
  }
}

const stopScanning = () => {
  if (stream) {
    stream.getTracks().forEach(track => track.stop())
    stream = null
  }
  if (animationId) {
    cancelAnimationFrame(animationId)
    animationId = null
  }
  scanning.value = false
}

const captureAndDecode = async () => {
  if (!videoRef.value || !canvasRef.value) return

  const video = videoRef.value
  const canvas = canvasRef.value
  const ctx = canvas.getContext('2d')
  if (!ctx) return

  canvas.width = video.videoWidth
  canvas.height = video.videoHeight
  ctx.drawImage(video, 0, 0, canvas.width, canvas.height)

  try {
    const jsQR = await import('jsqr')
    const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height)
    const code = jsQR.default(imageData.data, imageData.width, imageData.height)
    
    if (code) {
      emit('scanned', code.data)
      stopScanning()
    } else {
      error.value = '未识别到二维码，请对准后重试'
    }
  } catch (e: any) {
    error.value = '二维码解析失败'
    emit('error', error.value)
  }
}

onUnmounted(() => {
  stopScanning()
})

defineExpose({
  startScanning,
  stopScanning
})
</script>

<style scoped>
.qr-scanner {
  width: 100%;
}
.scanner-placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
  background: #f5f7fa;
  border-radius: 8px;
}
.scanner-container {
  position: relative;
  width: 100%;
  max-width: 400px;
  margin: 0 auto;
}
.scanner-video {
  width: 100%;
  border-radius: 8px;
}
.scanner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 60px;
  display: flex;
  justify-content: center;
  align-items: center;
  pointer-events: none;
}
.scanner-frame {
  width: 200px;
  height: 200px;
  border: 2px solid #409eff;
  border-radius: 8px;
  box-shadow: 0 0 0 9999px rgba(0, 0, 0, 0.3);
}
.scanner-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 10px;
}
.scanner-error {
  margin-top: 10px;
}
</style>
