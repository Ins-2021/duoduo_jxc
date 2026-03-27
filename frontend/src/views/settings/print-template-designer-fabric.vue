<template>
  <div class="designer-root">
    <div class="designer-top">
      <div class="top-left">
        <el-dropdown>
          <span class="top-menu">菜单 <el-icon><ArrowDown /></el-icon></span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="backToList">返回列表</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>

        <div class="top-field">
          <span class="label">模板名称</span>
          <el-input v-model="template.templateName" size="small" style="width: 220px" />
        </div>

        <div class="top-field">
          <span class="label">纸张</span>
          <el-select v-model="template.paperType" size="small" style="width: 180px" @change="syncPaperByType">
            <el-option v-for="p in paperOptions" :key="p.paperType" :label="p.paperType" :value="p.paperType" />
          </el-select>
        </div>

        <div class="top-field">
          <span class="label">宽(mm)</span>
          <el-input-number v-model="template.paperWidthMm" size="small" :min="1" controls-position="right" style="width: 120px" @change="resizeCanvas" />
          <span class="split">×</span>
          <el-input-number v-model="template.paperHeightMm" size="small" :min="1" controls-position="right" style="width: 120px" @change="resizeCanvas" />
        </div>
      </div>

      <div class="top-right">
        <el-checkbox v-model="snapToGrid">吸附网格</el-checkbox>
        <el-input-number v-model="gridSizeMm" size="small" :min="0.5" :max="10" :step="0.5" controls-position="right" style="width: 120px" />
        <el-switch v-model="drawMode" active-text="画笔" inactive-text="选择" @change="applyDrawMode" />
        <el-select v-model="zoom" size="small" style="width: 110px" @change="applyZoom">
          <el-option v-for="z in zoomOptions" :key="z" :label="z + '%'" :value="z" />
        </el-select>
        <el-button size="small" @click="backToList">
          <el-icon><Close /></el-icon>
        </el-button>
      </div>
    </div>

    <div class="designer-body">
      <div class="left-panel">
        <div class="left-title">工具</div>
        <div class="tool-grid">
          <div class="tool-item" draggable="true" @dragstart="onToolDragStart($event, 'text')" @click="addText()">文本</div>
          <div class="tool-item" draggable="true" @dragstart="onToolDragStart($event, 'rect')" @click="addRect()">矩形</div>
          <div class="tool-item" draggable="true" @dragstart="onToolDragStart($event, 'line')" @click="addLine()">线条</div>
          <div class="tool-item" draggable="true" @dragstart="onToolDragStart($event, 'barcode')" @click="addBarcode()">条码</div>
          <div class="tool-item" draggable="true" @dragstart="onToolDragStart($event, 'qrcode')" @click="addQrCode()">二维码</div>
          <div class="tool-item" draggable="true" @dragstart="onToolDragStart($event, 'image')" @click="addImage()">图片</div>
        </div>

        <div class="left-title">常用</div>
        <div class="field-grid">
          <el-button size="small" @click="addText('单据日期')">单据日期</el-button>
          <el-button size="small" @click="addText('单据编号')">单据编号</el-button>
          <el-button size="small" @click="addText('制单人')">制单人</el-button>
          <el-button size="small" @click="addText('备注信息')">备注信息</el-button>
        </div>

        <div class="left-title">对齐</div>
        <div class="align-grid">
          <el-button size="small" @click="align('left')">左对齐</el-button>
          <el-button size="small" @click="align('hcenter')">水平居中</el-button>
          <el-button size="small" @click="align('right')">右对齐</el-button>
          <el-button size="small" @click="align('top')">上对齐</el-button>
          <el-button size="small" @click="align('vcenter')">垂直居中</el-button>
          <el-button size="small" @click="align('bottom')">下对齐</el-button>
        </div>
      </div>

      <div class="canvas-wrap" ref="canvasWrapRef" @dragover.prevent @drop="onCanvasDrop">
        <div class="canvas-stage">
          <div class="canvas-shell" :style="shellStyle">
            <canvas ref="canvasEl" />
          </div>
        </div>
      </div>

      <div class="right-panel">
        <div class="right-title">属性</div>

        <div class="prop-block">
          <div class="prop-title">页面</div>
          <el-form label-width="90px" size="small">
            <el-form-item label="宽(mm)">
              <el-input-number v-model="template.paperWidthMm" :min="1" controls-position="right" style="width: 100%" @change="resizeCanvas" />
            </el-form-item>
            <el-form-item label="高(mm)">
              <el-input-number v-model="template.paperHeightMm" :min="1" controls-position="right" style="width: 100%" @change="resizeCanvas" />
            </el-form-item>
            <el-form-item label="背景">
              <el-color-picker v-model="pageBg" size="small" @change="applyBg" />
            </el-form-item>
          </el-form>
        </div>

        <div class="prop-block" v-if="active">
          <div class="prop-title">元素</div>
          <el-form label-width="90px" size="small">
            <el-form-item label="类型">
              <el-input :model-value="activeTypeLabel" disabled />
            </el-form-item>
            <el-form-item label="X(mm)">
              <el-input-number v-model="activeMm.x" :min="0" controls-position="right" style="width: 100%" @change="applyActiveFromMm" />
            </el-form-item>
            <el-form-item label="Y(mm)">
              <el-input-number v-model="activeMm.y" :min="0" controls-position="right" style="width: 100%" @change="applyActiveFromMm" />
            </el-form-item>
            <el-form-item label="宽(mm)">
              <el-input-number v-model="activeMm.w" :min="1" controls-position="right" style="width: 100%" @change="applyActiveFromMm" />
            </el-form-item>
            <el-form-item label="高(mm)">
              <el-input-number v-model="activeMm.h" :min="1" controls-position="right" style="width: 100%" @change="applyActiveFromMm" />
            </el-form-item>
            <el-form-item label="旋转" v-if="canRotate">
              <el-input-number v-model="activeAngle" :min="-180" :max="180" controls-position="right" style="width: 100%" @change="applyAngle" />
            </el-form-item>
            <el-form-item label="文字" v-if="isTextActive">
              <el-input v-model="activeText" type="textarea" :rows="3" @input="applyText" />
            </el-form-item>
            <el-form-item label="字号" v-if="isTextActive">
              <el-input-number v-model="activeFontSize" :min="6" :max="80" controls-position="right" style="width: 100%" @change="applyTextStyle" />
            </el-form-item>
            <el-form-item label="加粗" v-if="isTextActive">
              <el-switch v-model="activeBold" @change="applyTextStyle" />
            </el-form-item>
            <el-form-item label="内容" v-if="isCodeActive">
              <el-input v-model="activeCodeValue" @input="applyCodeValue" />
            </el-form-item>
          </el-form>
          <div class="prop-actions">
            <el-button size="small" @click="bringForward">上移</el-button>
            <el-button size="small" @click="sendBackward">下移</el-button>
            <el-button size="small" @click="removeActive">删除</el-button>
          </div>
        </div>

        <div class="prop-block">
          <div class="prop-title">预览数据</div>
          <el-input v-model="previewDataJson" type="textarea" :rows="10" />
          <div class="prop-actions" style="margin-top: 10px;">
            <el-button size="small" @click="insertField('billNo')">插入单据编号</el-button>
            <el-button size="small" @click="insertField('billDate')">插入单据日期</el-button>
            <el-button size="small" @click="insertField('storeName')">插入门店</el-button>
          </div>
          <div class="prop-actions" style="margin-top: 10px;">
            <el-button size="small" @click="insertField('customerName')">插入客户</el-button>
            <el-button size="small" @click="insertField('operator')">插入制单人</el-button>
            <el-button size="small" @click="insertField('remark')">插入备注</el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="designer-bottom">
      <div class="bottom-left">
        <span class="muted">fabric</span>
      </div>
      <div class="bottom-right">
        <el-button size="small" @click="preview">预览(ctrl+p)</el-button>
        <el-button size="small" :loading="saving" @click="save">保存(ctrl+s)</el-button>
        <el-button type="primary" size="small" :loading="saving" @click="saveAndExit">保存并退出</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowDown, Close } from '@element-plus/icons-vue'
import { Canvas, Rect, Textbox, Line, Image as FabricImage, PencilBrush, FabricObject } from 'fabric'
import JsBarcode from 'jsbarcode'
import QRCode from 'qrcode'
import { getPrintTemplate, savePrintTemplate } from '@/api/print-template'
import { openPrintWindow as openPrintWindowByMm, renderFabricDesignToDataUrl } from '@/utils/print-template-render'
import { initAligningGuidelines } from '@/utils/fabric-guidelines'

type ToolType = 'text' | 'rect' | 'line' | 'barcode' | 'qrcode' | 'image'

const route = useRoute()
const router = useRouter()

const canvasEl = ref<HTMLCanvasElement | null>(null)
const canvasWrapRef = ref<HTMLElement | null>(null)
const fabricCanvas = ref<Canvas | null>(null)

const pxPerMm = 96 / 25.4

const paperOptions = [
  { paperType: 'A4', w: 210, h: 297 },
  { paperType: '二等分', w: 210, h: 140 },
  { paperType: '小票（58mm）', w: 58, h: 160 },
  { paperType: '小票（80mm）', w: 80, h: 160 },
  { paperType: '30 * 20mm', w: 30, h: 20 },
  { paperType: '40 * 70mm', w: 40, h: 70 },
  { paperType: '40 * 60mm', w: 40, h: 60 },
  { paperType: '80 * 50mm', w: 80, h: 50 },
  { paperType: '60 * 40mm', w: 60, h: 40 },
  { paperType: '40 * 30mm', w: 40, h: 30 }
]

const template = reactive({
  templateId: 0,
  templateName: '',
  bizType: '',
  paperType: 'A4',
  paperWidthMm: 210,
  paperHeightMm: 297,
  enabled: 1,
  isDefault: 0,
  designJson: ''
})

const zoomOptions = [50, 75, 100, 125, 150]
const zoom = ref(100)
const snapToGrid = ref(true)
const gridSizeMm = ref(1)
const drawMode = ref(false)
const pageBg = ref('#ffffff')
const previewDataJson = ref(
  JSON.stringify(
    {
      billNo: 'XS202603240001',
      billDate: '2026-03-24',
      storeName: '衣多多',
      customerName: '默认客户',
      operator: '管理员',
      remark: '预览数据',
      totalAmount: 1200,
      items: [{ name: '商品A', qty: 2, price: 100, amount: 200 }]
    },
    null,
    2
  )
)

const shellStyle = computed(() => {
  return {
    width: template.paperWidthMm * (pxPerMm * (zoom.value / 100)) + 'px',
    height: template.paperHeightMm * (pxPerMm * (zoom.value / 100)) + 'px'
  }
})

const toolDragType = ref<ToolType | ''>('')
const onToolDragStart = (e: DragEvent, type: ToolType) => {
  toolDragType.value = type
  try {
    e.dataTransfer?.setData('text/plain', type)
  } catch {}
}

const syncPaperByType = () => {
  const p = paperOptions.find(x => x.paperType === template.paperType)
  if (!p) return
  template.paperWidthMm = p.w
  template.paperHeightMm = p.h
  resizeCanvas()
}

const applyZoom = () => {
  const c = fabricCanvas.value
  if (!c) return
  c.setZoom(zoom.value / 100)
  c.setViewportTransform([zoom.value / 100, 0, 0, zoom.value / 100, 0, 0])
  c.requestRenderAll()
}

const applyDrawMode = () => {
  const c = fabricCanvas.value
  if (!c) return
  c.isDrawingMode = drawMode.value
  c.selection = !drawMode.value
  c.forEachObject((o: any) => {
    o.selectable = !drawMode.value
    o.evented = !drawMode.value
  })
  if (c.isDrawingMode) {
    const brush = new PencilBrush(c as any)
    brush.width = 2
    brush.color = '#303133'
    c.freeDrawingBrush = brush
  }
  c.requestRenderAll()
}

const applyBg = () => {
  const c = fabricCanvas.value
  if (!c) return
  c.backgroundColor = pageBg.value
  c.requestRenderAll()
}

const insertField = (field: string) => {
  const c = fabricCanvas.value
  if (!c) return
  const obj: any = c.getActiveObject()
  if (!obj || obj.type !== 'textbox') return
  const cur = String(obj.text || '')
  const next = cur + `{{${field}}}`
  obj.text = next
  obj.data = { ...(obj.data || {}), rawText: obj?.data?.rawText ?? next }
  c.requestRenderAll()
  active.value = obj
  updateActivePanel()
}

const resizeCanvas = () => {
  const c = fabricCanvas.value
  const el = canvasEl.value
  if (!c || !el) return
  const w = template.paperWidthMm * pxPerMm
  const h = template.paperHeightMm * pxPerMm
  c.setWidth(w)
  c.setHeight(h)
  applyZoom()
}

const mm = (px: number) => +(px / pxPerMm).toFixed(2)
const px = (mmVal: number) => +(mmVal * pxPerMm).toFixed(2)

const normalizeMm = (v: number) => {
  if (!snapToGrid.value) return +v.toFixed(2)
  const g = gridSizeMm.value || 1
  return +(Math.round(v / g) * g).toFixed(2)
}

const normalizePx = (v: number) => px(normalizeMm(mm(v)))

const decorateObject = (obj: any) => {
  obj.set({
    selectable: true,
    evented: true,
    hasControls: true,
    hasBorders: true,
    transparentCorners: false,
    cornerStyle: 'circle',
    cornerColor: '#409EFF',
    borderColor: '#409EFF',
    cornerSize: 8,
    padding: 2
  })
  return obj
}

const active = ref<any>(null)
const activeMm = reactive({ x: 0, y: 0, w: 10, h: 10 })
const activeText = ref('')
const activeFontSize = ref(12)
const activeBold = ref(false)
const activeAngle = ref(0)
const activeCodeValue = ref('')

const activeTypeLabel = computed(() => {
  const obj = active.value
  if (!obj) return ''
  const kind = obj?.data?.kind
  if (kind === 'barcode') return '条码'
  if (kind === 'qrcode') return '二维码'
  if (obj.type === 'textbox') return '文本'
  if (obj.type === 'rect') return '矩形'
  if (obj.type === 'line') return '线条'
  if (obj.type === 'image') return '图片'
  return obj.type
})

const isTextActive = computed(() => !!active.value && active.value.type === 'textbox')
const isCodeActive = computed(() => !!active.value && (active.value?.data?.kind === 'barcode' || active.value?.data?.kind === 'qrcode'))
const canRotate = computed(() => !!active.value && active.value.type !== 'line')

const updateActivePanel = () => {
  const obj = active.value
  if (!obj) return
  obj.setCoords()
  const bounds = obj.getBoundingRect()
  activeMm.x = mm(bounds.left)
  activeMm.y = mm(bounds.top)
  activeMm.w = Math.max(1, mm(bounds.width))
  activeMm.h = Math.max(1, mm(bounds.height))
  activeAngle.value = Math.round(obj.angle || 0)
  if (obj.type === 'textbox') {
    activeText.value = obj.text || ''
    activeFontSize.value = Number(obj.fontSize || 12)
    activeBold.value = String(obj.fontWeight || 'normal') === '700' || String(obj.fontWeight || 'normal') === 'bold'
  }
  if (obj?.data?.kind === 'barcode' || obj?.data?.kind === 'qrcode') {
    activeCodeValue.value = obj?.data?.value || ''
  }
}

const applyActiveFromMm = () => {
  const c = fabricCanvas.value
  const obj = active.value
  if (!c || !obj) return
  const xPx = px(activeMm.x)
  const yPx = px(activeMm.y)
  const wPx = px(activeMm.w)
  const hPx = px(activeMm.h)
  obj.set({ left: xPx, top: yPx })
  if (obj.type === 'textbox') {
    obj.set({ width: wPx, height: hPx })
  } else if (obj.type === 'rect') {
    obj.set({ width: wPx, height: hPx, scaleX: 1, scaleY: 1 })
  } else if (obj.type === 'image') {
    const iw = obj.width || 1
    const ih = obj.height || 1
    obj.set({ scaleX: wPx / iw, scaleY: hPx / ih })
  } else if (obj.type === 'line') {
    obj.set({ scaleX: 1, scaleY: 1 })
  }
  obj.setCoords()
  c.requestRenderAll()
}

const applyText = () => {
  const c = fabricCanvas.value
  const obj = active.value
  if (!c || !obj || obj.type !== 'textbox') return
  obj.text = activeText.value
  c.requestRenderAll()
}

const applyTextStyle = () => {
  const c = fabricCanvas.value
  const obj = active.value
  if (!c || !obj || obj.type !== 'textbox') return
  obj.set({ fontSize: activeFontSize.value, fontWeight: activeBold.value ? '700' : '400' })
  c.requestRenderAll()
}

const applyAngle = () => {
  const c = fabricCanvas.value
  const obj = active.value
  if (!c || !obj) return
  obj.set({ angle: activeAngle.value })
  obj.setCoords()
  c.requestRenderAll()
  updateActivePanel()
}

const buildBarcodeDataUrl = async (value: string) => {
  const cv = document.createElement('canvas')
  JsBarcode(cv, value || '1234567890', { displayValue: false, margin: 0 })
  return cv.toDataURL('image/png')
}

const buildQrDataUrl = async (value: string) => {
  return QRCode.toDataURL(value || 'https://example.com', { margin: 0, width: 240 })
}

const loadImage = (src: string) => {
  return new Promise<HTMLImageElement>((resolve, reject) => {
    const img = new Image()
    img.onload = () => resolve(img)
    img.onerror = reject
    img.src = src
  })
}

const addText = (text?: string, at?: { x: number; y: number }) => {
  const c = fabricCanvas.value
  if (!c) return
  const obj = decorateObject(
    new Textbox(text || '文本', {
    left: at?.x ?? 40,
    top: at?.y ?? 40,
    width: 200,
    fontSize: 14,
    fill: '#303133',
    fontWeight: '400'
  })
  )
  ;(obj as any).data = { kind: 'text' }
  c.add(obj)
  c.setActiveObject(obj)
  c.requestRenderAll()
}

const addRect = (at?: { x: number; y: number }) => {
  const c = fabricCanvas.value
  if (!c) return
  const obj = decorateObject(
    new Rect({
    left: at?.x ?? 60,
    top: at?.y ?? 60,
    width: 220,
    height: 120,
    fill: 'rgba(0,0,0,0)',
    stroke: '#303133',
    strokeWidth: 1
  })
  )
  ;(obj as any).data = { kind: 'rect' }
  c.add(obj)
  c.setActiveObject(obj)
  c.requestRenderAll()
}

const addLine = (at?: { x: number; y: number }) => {
  const c = fabricCanvas.value
  if (!c) return
  const obj = decorateObject(
    new Line([0, 0, 240, 0], {
    left: at?.x ?? 60,
    top: at?.y ?? 120,
    stroke: '#303133',
    strokeWidth: 1
  })
  )
  ;(obj as any).data = { kind: 'line' }
  c.add(obj)
  c.setActiveObject(obj)
  c.requestRenderAll()
}

const addBarcode = async (at?: { x: number; y: number }) => {
  const c = fabricCanvas.value
  if (!c) return
  const dataUrl = await buildBarcodeDataUrl('1234567890')
  const imgEl = await loadImage(dataUrl)
  const obj = decorateObject(new FabricImage(imgEl, { left: at?.x ?? 60, top: at?.y ?? 60 }))
  obj.scaleToWidth(240)
  ;(obj as any).data = { kind: 'barcode', value: '1234567890' }
  c.add(obj)
  c.setActiveObject(obj)
  c.requestRenderAll()
}

const addQrCode = async (at?: { x: number; y: number }) => {
  const c = fabricCanvas.value
  if (!c) return
  const dataUrl = await buildQrDataUrl('https://example.com')
  const imgEl = await loadImage(dataUrl)
  const obj = decorateObject(new FabricImage(imgEl, { left: at?.x ?? 60, top: at?.y ?? 60 }))
  obj.scaleToWidth(180)
  ;(obj as any).data = { kind: 'qrcode', value: 'https://example.com' }
  c.add(obj)
  c.setActiveObject(obj)
  c.requestRenderAll()
}

const addImage = async (at?: { x: number; y: number }) => {
  const c = fabricCanvas.value
  if (!c) return
  const svg = encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="300" height="180"><rect width="100%" height="100%" fill="#f5f7fa"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" font-size="20" fill="#909399">图片</text></svg>')
  const dataUrl = `data:image/svg+xml;charset=utf-8,${svg}`
  const imgEl = await loadImage(dataUrl)
  const obj = decorateObject(new FabricImage(imgEl, { left: at?.x ?? 60, top: at?.y ?? 60 }))
  obj.scaleToWidth(240)
  ;(obj as any).data = { kind: 'image' }
  c.add(obj)
  c.setActiveObject(obj)
  c.requestRenderAll()
}

const onCanvasDrop = async (e: DragEvent) => {
  const c = fabricCanvas.value
  const wrap = canvasWrapRef.value
  if (!c || !wrap) return
  const type = (e.dataTransfer?.getData('text/plain') as ToolType) || toolDragType.value
  if (!type) return
  const rect = wrap.getBoundingClientRect()
  const xScreen = e.clientX - rect.left
  const yScreen = e.clientY - rect.top
  const vt = c.viewportTransform || [1, 0, 0, 1, 0, 0]
  const xCanvas = (xScreen - vt[4]) / vt[0]
  const yCanvas = (yScreen - vt[5]) / vt[3]
  const x = normalizePx(xCanvas)
  const y = normalizePx(yCanvas)
  if (type === 'text') addText('文本', { x, y })
  if (type === 'rect') addRect({ x, y })
  if (type === 'line') addLine({ x, y })
  if (type === 'barcode') await addBarcode({ x, y })
  if (type === 'qrcode') await addQrCode({ x, y })
  if (type === 'image') await addImage({ x, y })
}

const applyCodeValue = async () => {
  const c = fabricCanvas.value
  const obj = active.value
  if (!c || !obj) return
  const kind = obj?.data?.kind
  if (kind !== 'barcode' && kind !== 'qrcode') return
  const value = activeCodeValue.value || ''
  const dataUrl = kind === 'barcode' ? await buildBarcodeDataUrl(value) : await buildQrDataUrl(value)
  const imgEl = await loadImage(dataUrl)
  obj.setElement(imgEl)
  ;(obj as any).data = { ...obj.data, value }
  obj.setCoords()
  c.requestRenderAll()
}

const removeActive = () => {
  const c = fabricCanvas.value
  if (!c) return
  const obj = c.getActiveObject()
  if (!obj) return
  c.remove(obj)
  c.discardActiveObject()
  active.value = null
  c.requestRenderAll()
}

const bringForward = () => {
  const c = fabricCanvas.value
  if (!c) return
  const obj = c.getActiveObject()
  if (!obj) return
  c.bringObjectForward(obj as any)
  c.requestRenderAll()
}

const sendBackward = () => {
  const c = fabricCanvas.value
  if (!c) return
  const obj = c.getActiveObject()
  if (!obj) return
  c.sendObjectBackwards(obj as any)
  c.requestRenderAll()
}

const align = (mode: 'left' | 'hcenter' | 'right' | 'top' | 'vcenter' | 'bottom') => {
  const c = fabricCanvas.value
  if (!c) return
  const objs = c.getActiveObjects()
  if (!objs || objs.length === 0) return
  const w = c.getWidth()
  const h = c.getHeight()
  objs.forEach(o => {
    o.setCoords()
    const br = o.getBoundingRect()
    if (mode === 'left') o.set({ left: 0 })
    if (mode === 'right') o.set({ left: w - br.width })
    if (mode === 'hcenter') o.set({ left: (w - br.width) / 2 })
    if (mode === 'top') o.set({ top: 0 })
    if (mode === 'bottom') o.set({ top: h - br.height })
    if (mode === 'vcenter') o.set({ top: (h - br.height) / 2 })
    o.setCoords()
  })
  c.requestRenderAll()
  updateActivePanel()
}

const initFabric = () => {
  if (!canvasEl.value) return
  if (!FabricObject.customProperties.includes('data')) {
    FabricObject.customProperties.push('data')
  }
  const c = new Canvas(canvasEl.value, {
    selection: true,
    preserveObjectStacking: true
  })
  fabricCanvas.value = c
  initAligningGuidelines(c)
  c.backgroundColor = pageBg.value
  resizeCanvas()
  applyDrawMode()
  applyZoom()

  c.on('selection:created', () => {
    active.value = c.getActiveObject()
    updateActivePanel()
  })
  c.on('selection:updated', () => {
    active.value = c.getActiveObject()
    updateActivePanel()
  })
  c.on('selection:cleared', () => {
    active.value = null
  })
  c.on('object:modified', () => {
    active.value = c.getActiveObject()
    updateActivePanel()
  })
  c.on('object:moving', (opt) => {
    const t: any = opt.target
    if (!t) return
    t.set({ left: normalizePx(t.left || 0), top: normalizePx(t.top || 0) })
  })
  c.on('object:scaling', (opt) => {
    const t: any = opt.target
    if (!t) return
    if (!snapToGrid.value) return
    t.setCoords()
    const br = t.getBoundingRect()
    const wMm = normalizeMm(mm(br.width))
    const hMm = normalizeMm(mm(br.height))
    if (t.type === 'image') {
      const iw = t.width || 1
      const ih = t.height || 1
      t.set({ scaleX: px(wMm) / iw, scaleY: px(hMm) / ih })
    }
    if (t.type === 'rect' || t.type === 'textbox') {
      t.set({ scaleX: 1, scaleY: 1 })
      t.set({ width: px(wMm), height: px(hMm) })
    }
  })
}

const load = async () => {
  const id = Number(route.params.id)
  if (!id) {
    backToList()
    return
  }
  const res = await getPrintTemplate(id)
  const data = res?.data || {}
  template.templateId = data.templateId
  template.templateName = data.templateName || ''
  template.bizType = data.bizType || ''
  template.paperType = data.paperType || 'A4'
  template.paperWidthMm = data.paperWidthMm || 210
  template.paperHeightMm = data.paperHeightMm || 297
  template.enabled = data.enabled ?? 1
  template.isDefault = data.isDefault ?? 0
  template.designJson = data.designJson || ''

  if (!fabricCanvas.value) {
    initFabric()
  } else {
    resizeCanvas()
  }

  const c = fabricCanvas.value
  if (!c) return
  c.clear()
  c.backgroundColor = pageBg.value

  if (!template.designJson) {
    c.requestRenderAll()
    return
  }

  try {
    const parsed = JSON.parse(template.designJson)
    if (parsed?.engine === 'fabric' && parsed?.canvas) {
      await c.loadFromJSON(parsed.canvas)
      c.backgroundColor = parsed?.bg || pageBg.value
      pageBg.value = c.backgroundColor as string
      applyZoom()
      c.requestRenderAll()
      return
    }
  } catch {}

  c.requestRenderAll()
}

const saving = ref(false)

const buildDesignJson = () => {
  const c = fabricCanvas.value
  if (!c) return ''
  const json = c.toJSON()
  return JSON.stringify({
    version: 1,
    engine: 'fabric',
    pxPerMm,
    paper: {
      paperType: template.paperType,
      paperWidthMm: template.paperWidthMm,
      paperHeightMm: template.paperHeightMm
    },
    bg: c.backgroundColor,
    canvas: json
  })
}

const save = async () => {
  const c = fabricCanvas.value
  if (!c) return
  saving.value = true
  try {
    const designJson = buildDesignJson()
    template.designJson = designJson
    await savePrintTemplate({
      templateId: template.templateId,
      templateName: template.templateName,
      bizType: template.bizType,
      paperType: template.paperType,
      paperWidthMm: template.paperWidthMm,
      paperHeightMm: template.paperHeightMm,
      enabled: template.enabled,
      isDefault: template.isDefault,
      designJson
    } as any)
    ElMessage.success('保存成功')
  } catch {} finally {
    saving.value = false
  }
}

const saveAndExit = async () => {
  await save()
  backToList()
}

const preview = async () => {
  const c = fabricCanvas.value
  if (!c) return
  let data: any = {}
  try {
    data = JSON.parse(previewDataJson.value || '{}')
  } catch {
    ElMessage.error('预览数据 JSON 格式错误')
    return
  }
  try {
    const designJson = buildDesignJson()
    const rendered = await renderFabricDesignToDataUrl(designJson, data, { dpi: 200 })
    openPrintWindowByMm(rendered.dataUrl, rendered.wMm, rendered.hMm)
  } catch {}
}

const backToList = () => {
  router.push('/settings/print-template')
}

const onKeydown = (e: KeyboardEvent) => {
  const key = e.key.toLowerCase()
  if ((e.ctrlKey || e.metaKey) && key === 's') {
    e.preventDefault()
    save()
    return
  }
  if ((e.ctrlKey || e.metaKey) && key === 'p') {
    e.preventDefault()
    preview()
    return
  }
  if (key === 'delete' || key === 'backspace') {
    const c = fabricCanvas.value
    if (!c) return
    const obj = c.getActiveObject()
    if (!obj) return
    e.preventDefault()
    removeActive()
  }
}

onMounted(() => {
  load()
  window.addEventListener('keydown', onKeydown)
})

watch(
  () => route.params.id,
  () => load()
)
</script>

<style scoped>
.designer-root {
  height: calc(100vh - 90px);
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.designer-top {
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 12px;
}

.top-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.top-menu {
  cursor: pointer;
  font-size: 13px;
  color: #303133;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.top-field {
  display: flex;
  align-items: center;
  gap: 6px;
}

.top-field .label {
  font-size: 12px;
  color: #606266;
}

.split {
  color: #909399;
  font-size: 12px;
}

.top-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.designer-body {
  flex: 1;
  display: flex;
  min-height: 0;
}

.left-panel {
  width: 160px;
  background: #fff;
  border-right: 1px solid #e6e6e6;
  padding: 10px;
  overflow: auto;
}

.left-title {
  font-size: 12px;
  color: #909399;
  margin: 8px 0;
}

.tool-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.tool-item {
  height: 46px;
  border: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #303133;
  cursor: pointer;
  background: #fff;
  user-select: none;
}

.tool-item:hover {
  border-color: #409EFF;
  color: #409EFF;
}

.field-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.align-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.canvas-wrap {
  flex: 1;
  min-width: 0;
  overflow: auto;
  background: #dfe3ea;
}

.canvas-stage {
  min-height: 100%;
  padding: 16px 0 40px 0;
  display: flex;
  justify-content: center;
}

.canvas-shell {
  background: #fff;
  box-shadow: 0 0 0 1px rgba(0,0,0,0.08), 0 8px 24px rgba(0,0,0,0.08);
}

.right-panel {
  width: 280px;
  background: #fff;
  border-left: 1px solid #e6e6e6;
  padding: 10px;
  overflow: auto;
}

.right-title {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.prop-block {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 10px;
  margin-bottom: 10px;
}

.prop-title {
  font-size: 13px;
  color: #303133;
  margin-bottom: 8px;
  font-weight: 600;
}

.prop-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.designer-bottom {
  height: 44px;
  background: #fff;
  border-top: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 12px;
}

.muted {
  color: #909399;
  font-size: 12px;
}

.bottom-right {
  display: flex;
  gap: 10px;
}
</style>
