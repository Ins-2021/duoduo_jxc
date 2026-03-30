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
          <el-input-number v-model="template.paperWidthMm" size="small" :min="1" controls-position="right" style="width: 120px" />
          <span class="split">×</span>
          <el-input-number v-model="template.paperHeightMm" size="small" :min="1" controls-position="right" style="width: 120px" />
        </div>
      </div>

      <div class="top-right">
        <el-checkbox v-model="removeMark">将标记移除</el-checkbox>
        <el-checkbox v-model="snapToGrid">吸附网格</el-checkbox>
        <el-input-number v-model="gridSizeMm" size="small" :min="0.1" :max="10" :step="0.5" controls-position="right" style="width: 120px" />
        <el-select v-model="zoom" size="small" style="width: 110px">
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
          <div class="tool-item" draggable="true" @dragstart="onToolDragStart($event, 'text')" @click="addElement('text')">文本</div>
          <div class="tool-item" draggable="true" @dragstart="onToolDragStart($event, 'textarea')" @click="addElement('textarea')">多行文本</div>
          <div class="tool-item" draggable="true" @dragstart="onToolDragStart($event, 'barcode')" @click="addElement('barcode')">条码</div>
          <div class="tool-item" draggable="true" @dragstart="onToolDragStart($event, 'qrcode')" @click="addElement('qrcode')">二维码</div>
          <div class="tool-item" draggable="true" @dragstart="onToolDragStart($event, 'table')" @click="addElement('table')">表格</div>
          <div class="tool-item" draggable="true" @dragstart="onToolDragStart($event, 'image')" @click="addElement('image')">图片</div>
          <div class="tool-item" draggable="true" @dragstart="onToolDragStart($event, 'line')" @click="addElement('line')">线条</div>
          <div class="tool-item" draggable="true" @dragstart="onToolDragStart($event, 'rect')" @click="addElement('rect')">矩形</div>
        </div>

        <div class="left-title">常用</div>
        <div class="field-grid">
          <el-button size="small" @click="addPreset('单据日期')">单据日期</el-button>
          <el-button size="small" @click="addPreset('单据编号')">单据编号</el-button>
          <el-button size="small" @click="addPreset('制单人')">制单人</el-button>
          <el-button size="small" @click="addPreset('备注信息')">备注信息</el-button>
        </div>
      </div>

      <div class="canvas-wrap" @pointerdown.self="clearSelect" @dragover.prevent @drop="onCanvasDrop">
        <div class="canvas-stage">
          <div class="page" :style="pageStyle" ref="pageRef">
            <div
              v-for="el in doc.elements"
              :key="el.id"
              class="el-box"
              :class="{ selected: el.id === selectedId }"
              :style="elementStyle(el)"
              @pointerdown.stop="onElementPointerDown($event, el)"
              @dblclick.stop="onElementDblClick(el)"
            >
              <template v-if="el.type === 'text' || el.type === 'textarea'">
                <div class="el-text" :style="textStyle(el)">{{ el.text }}</div>
              </template>
              <template v-else-if="el.type === 'barcode' || el.type === 'qrcode'">
                <div class="el-placeholder">{{ el.type === 'barcode' ? '条码' : '二维码' }}</div>
              </template>
              <template v-else-if="el.type === 'line'">
                <div class="el-line" />
              </template>
              <template v-else-if="el.type === 'rect'">
                <div class="el-rect" />
              </template>
              <template v-else>
                <div class="el-placeholder">{{ typeLabel(el.type) }}</div>
              </template>

              <div v-if="el.id === selectedId" class="resize-handle tl" @pointerdown.stop="onResizePointerDown($event, el, 'tl')" />
              <div v-if="el.id === selectedId" class="resize-handle tr" @pointerdown.stop="onResizePointerDown($event, el, 'tr')" />
              <div v-if="el.id === selectedId" class="resize-handle bl" @pointerdown.stop="onResizePointerDown($event, el, 'bl')" />
              <div v-if="el.id === selectedId" class="resize-handle br" @pointerdown.stop="onResizePointerDown($event, el, 'br')" />
            </div>
          </div>
        </div>
      </div>

      <div class="right-panel">
        <div class="right-title">属性</div>

        <div class="prop-block">
          <div class="prop-title">页面</div>
          <el-form label-width="90px" size="small">
            <el-form-item label="宽(mm)">
              <el-input-number v-model="template.paperWidthMm" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="高(mm)">
              <el-input-number v-model="template.paperHeightMm" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-form>
        </div>

        <div class="prop-block" v-if="selectedEl">
          <div class="prop-title">元素</div>
          <el-form label-width="90px" size="small">
            <el-form-item label="类型">
              <el-input :model-value="typeLabel(selectedEl.type)" disabled />
            </el-form-item>
            <el-form-item label="X(mm)">
              <el-input-number v-model="selectedEl.x" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="Y(mm)">
              <el-input-number v-model="selectedEl.y" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="宽(mm)">
              <el-input-number v-model="selectedEl.w" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="高(mm)">
              <el-input-number v-model="selectedEl.h" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="文字" v-if="selectedEl.type === 'text' || selectedEl.type === 'textarea'">
              <el-input v-model="selectedEl.text" :type="selectedEl.type === 'textarea' ? 'textarea' : 'text'" :rows="3" />
            </el-form-item>
            <el-form-item label="字号" v-if="selectedEl.type === 'text' || selectedEl.type === 'textarea'">
              <el-input-number v-model="selectedEl.fontSize" :min="6" :max="60" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="加粗" v-if="selectedEl.type === 'text' || selectedEl.type === 'textarea'">
              <el-switch v-model="selectedEl.bold" />
            </el-form-item>
          </el-form>
          <div class="prop-actions">
            <el-button size="small" @click="removeSelected">删除元素</el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="designer-bottom">
      <div class="bottom-left">
        <div class="bottom-elements">
          <span class="muted">元素</span>
          <div class="elements-list" ref="elementsListRef">
            <div
              v-for="(el, idx) in doc.elements"
              :key="el.id"
              class="elements-item"
              :class="{ active: el.id === selectedId }"
              @click="selectedId = el.id"
            >
              <span class="drag-handle">⋮⋮</span>
              <span class="elements-index">{{ idx + 1 }}</span>
              <span class="elements-name">{{ elementLabel(el) }}</span>
            </div>
          </div>
        </div>
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
import { getPrintTemplate, savePrintTemplate } from '@/api/print-template'
import Sortable from 'sortablejs'

type ElementType = 'text' | 'textarea' | 'barcode' | 'qrcode' | 'table' | 'image' | 'line' | 'rect'

type DesignElement = {
  id: string
  type: ElementType
  x: number
  y: number
  w: number
  h: number
  text: string
  fontSize: number
  bold: boolean
}

const route = useRoute()
const router = useRouter()
const pageRef = ref<HTMLElement | null>(null)
const elementsListRef = ref<HTMLElement | null>(null)

const template = reactive({
  templateId: 0,
  templateName: '',
  bizType: '',
  paperType: 'A4',
  paperWidthMm: 210,
  paperHeightMm: 297,
  enabled: 1,
  isDefault: 0
})

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

const syncPaperByType = () => {
  const p = paperOptions.find(x => x.paperType === template.paperType)
  if (!p) return
  template.paperWidthMm = p.w
  template.paperHeightMm = p.h
}

const zoomOptions = [50, 75, 100, 125, 150]
const zoom = ref(100)
const removeMark = ref(false)
const snapToGrid = ref(true)
const gridSizeMm = ref(1)

const doc = reactive<{ elements: DesignElement[] }>({ elements: [] })

const selectedId = ref<string>('')
const selectedEl = computed(() => doc.elements.find(e => e.id === selectedId.value) || null)

const pxPerMm = computed(() => 3 * (zoom.value / 100))
const pageStyle = computed(() => {
  const w = template.paperWidthMm * pxPerMm.value
  const h = template.paperHeightMm * pxPerMm.value
  return {
    width: w + 'px',
    height: h + 'px'
  }
})

const elementStyle = (el: DesignElement) => {
  return {
    left: el.x * pxPerMm.value + 'px',
    top: el.y * pxPerMm.value + 'px',
    width: el.w * pxPerMm.value + 'px',
    height: el.h * pxPerMm.value + 'px'
  }
}

const textStyle = (el: DesignElement) => {
  return {
    fontSize: Math.max(8, el.fontSize) + 'px',
    fontWeight: el.bold ? '700' : '400'
  }
}

const typeLabel = (t: ElementType) => {
  if (t === 'text') return '文本'
  if (t === 'textarea') return '多行文本'
  if (t === 'barcode') return '条码'
  if (t === 'qrcode') return '二维码'
  if (t === 'table') return '表格'
  if (t === 'image') return '图片'
  if (t === 'line') return '线条'
  if (t === 'rect') return '矩形'
  return t
}

const normalizeMm = (val: number) => {
  if (!snapToGrid.value) return +val.toFixed(2)
  const g = gridSizeMm.value || 1
  return +(Math.round(val / g) * g).toFixed(2)
}

const addElement = (type: ElementType, pos?: { x: number; y: number }) => {
  const id = 'e_' + Date.now() + '_' + Math.random().toString(16).slice(2)
  const el: DesignElement = {
    id,
    type,
    x: normalizeMm(pos?.x ?? 10),
    y: normalizeMm(pos?.y ?? 10),
    w: type === 'line' ? 60 : 50,
    h: type === 'line' ? 1 : 12,
    text: type === 'text' ? '文本' : type === 'textarea' ? '多行文本' : '',
    fontSize: 12,
    bold: false
  }
  doc.elements.push(el)
  selectedId.value = id
}

const addPreset = (label: string) => {
  addElement('text')
  const el = selectedEl.value
  if (el) el.text = label
}

const elementLabel = (el: DesignElement) => {
  if (el.type === 'text' || el.type === 'textarea') {
    return (el.text || '文本').slice(0, 16)
  }
  return typeLabel(el.type)
}

const toolDragType = ref<ElementType | ''>('')
const onToolDragStart = (e: DragEvent, type: ElementType) => {
  toolDragType.value = type
  try {
    e.dataTransfer?.setData('text/plain', type)
  } catch {}
}

const onCanvasDrop = (e: DragEvent) => {
  const type = (e.dataTransfer?.getData('text/plain') as ElementType) || toolDragType.value
  if (!type || !pageRef.value) return
  const rect = pageRef.value.getBoundingClientRect()
  const xPx = e.clientX - rect.left
  const yPx = e.clientY - rect.top
  const xMm = xPx / pxPerMm.value
  const yMm = yPx / pxPerMm.value
  addElement(type, { x: Math.max(0, xMm), y: Math.max(0, yMm) })
}

const onElementDblClick = (el: DesignElement) => {
  selectedId.value = el.id
}

const clearSelect = () => {
  selectedId.value = ''
}

const onElementPointerDown = (ev: PointerEvent, el: DesignElement) => {
  selectedId.value = el.id
  const startX = ev.clientX
  const startY = ev.clientY
  const origin = { x: el.x, y: el.y }

  const onMove = (e: PointerEvent) => {
    const dx = (e.clientX - startX) / pxPerMm.value
    const dy = (e.clientY - startY) / pxPerMm.value
    el.x = Math.max(0, normalizeMm(origin.x + dx))
    el.y = Math.max(0, normalizeMm(origin.y + dy))
  }
  const onUp = () => {
    window.removeEventListener('pointermove', onMove)
    window.removeEventListener('pointerup', onUp)
  }
  window.addEventListener('pointermove', onMove)
  window.addEventListener('pointerup', onUp)
}

const onResizePointerDown = (ev: PointerEvent, el: DesignElement, dir: 'tl' | 'tr' | 'bl' | 'br') => {
  selectedId.value = el.id
  const startX = ev.clientX
  const startY = ev.clientY
  const origin = { x: el.x, y: el.y, w: el.w, h: el.h }

  const onMove = (e: PointerEvent) => {
    const dx = (e.clientX - startX) / pxPerMm.value
    const dy = (e.clientY - startY) / pxPerMm.value
    if (dir === 'br') {
      el.w = Math.max(1, normalizeMm(origin.w + dx))
      el.h = Math.max(1, normalizeMm(origin.h + dy))
      return
    }
    if (dir === 'tr') {
      el.w = Math.max(1, normalizeMm(origin.w + dx))
      el.h = Math.max(1, normalizeMm(origin.h - dy))
      el.y = Math.max(0, normalizeMm(origin.y + dy))
      return
    }
    if (dir === 'bl') {
      el.w = Math.max(1, normalizeMm(origin.w - dx))
      el.h = Math.max(1, normalizeMm(origin.h + dy))
      el.x = Math.max(0, normalizeMm(origin.x + dx))
      return
    }
    el.w = Math.max(1, normalizeMm(origin.w - dx))
    el.h = Math.max(1, normalizeMm(origin.h - dy))
    el.x = Math.max(0, normalizeMm(origin.x + dx))
    el.y = Math.max(0, normalizeMm(origin.y + dy))
  }
  const onUp = () => {
    window.removeEventListener('pointermove', onMove)
    window.removeEventListener('pointerup', onUp)
  }
  window.addEventListener('pointermove', onMove)
  window.addEventListener('pointerup', onUp)
}

const removeSelected = () => {
  if (!selectedId.value) return
  const idx = doc.elements.findIndex(e => e.id === selectedId.value)
  if (idx >= 0) doc.elements.splice(idx, 1)
  selectedId.value = ''
}

const saving = ref(false)

const buildDesignJson = () => {
  return JSON.stringify({
    version: 1,
    paper: {
      paperType: template.paperType,
      paperWidthMm: template.paperWidthMm,
      paperHeightMm: template.paperHeightMm
    },
    elements: doc.elements
  })
}

const save = async () => {
  saving.value = true
  try {
    await savePrintTemplate({
      templateId: template.templateId,
      templateName: template.templateName,
      bizType: template.bizType,
      paperType: template.paperType,
      paperWidthMm: template.paperWidthMm,
      paperHeightMm: template.paperHeightMm,
      enabled: template.enabled,
      isDefault: template.isDefault,
      designJson: buildDesignJson()
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

const preview = () => {
  ElMessage.info('功能即将上线，敬请期待')
}

const backToList = () => {
  router.push('/settings/print-template')
}

const load = async () => {
  const id = Number(route.params.id)
  if (!id) {
    backToList()
    return
  }
  try {
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
    doc.elements.splice(0, doc.elements.length)
    if (data.designJson) {
      const parsed = JSON.parse(data.designJson)
      const list = Array.isArray(parsed?.elements) ? parsed.elements : []
      list.forEach((e: any) => {
        doc.elements.push({
          id: String(e.id || ('e_' + Date.now() + '_' + Math.random().toString(16).slice(2))),
          type: e.type as ElementType,
          x: Number(e.x || 0),
          y: Number(e.y || 0),
          w: Number(e.w || 10),
          h: Number(e.h || 10),
          text: String(e.text || ''),
          fontSize: Number(e.fontSize || 12),
          bold: !!e.bold
        })
      })
    }
  } catch {
    backToList()
  }
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
    if (selectedId.value) {
      e.preventDefault()
      removeSelected()
    }
    return
  }
  if (selectedEl.value && ['arrowup', 'arrowdown', 'arrowleft', 'arrowright'].includes(key)) {
    e.preventDefault()
    const step = e.shiftKey ? 5 : 1
    const el = selectedEl.value
    if (!el) return
    if (key === 'arrowup') el.y = Math.max(0, normalizeMm(el.y - step))
    if (key === 'arrowdown') el.y = Math.max(0, normalizeMm(el.y + step))
    if (key === 'arrowleft') el.x = Math.max(0, normalizeMm(el.x - step))
    if (key === 'arrowright') el.x = Math.max(0, normalizeMm(el.x + step))
  }
}

onMounted(() => {
  load()
  if (elementsListRef.value) {
    Sortable.create(elementsListRef.value, {
      handle: '.drag-handle',
      animation: 150,
      onEnd: (evt) => {
        const oldIndex = evt.oldIndex
        const newIndex = evt.newIndex
        if (oldIndex == null || newIndex == null || oldIndex === newIndex) return
        const moved = doc.elements.splice(oldIndex, 1)[0]
        doc.elements.splice(newIndex, 0, moved)
      }
    })
  }
  window.addEventListener('keydown', onKeydown)
})

watch(
  () => route.params.id,
  () => {
    load()
  }
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
  width: 140px;
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

.page {
  background: #fff;
  box-shadow: 0 0 0 1px rgba(0,0,0,0.08), 0 8px 24px rgba(0,0,0,0.08);
  position: relative;
}

.el-box {
  position: absolute;
  border: 1px dashed transparent;
  box-sizing: border-box;
}

.el-box.selected {
  border-color: #409EFF;
}

.el-text {
  width: 100%;
  height: 100%;
  color: #303133;
  display: flex;
  align-items: center;
}

.el-placeholder {
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  border: 1px solid #ebeef5;
  color: #909399;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

.el-line {
  width: 100%;
  height: 100%;
  background: #303133;
}

.el-rect {
  width: 100%;
  height: 100%;
  border: 1px solid #303133;
}

.resize-handle {
  width: 8px;
  height: 8px;
  background: #409EFF;
  position: absolute;
}

.resize-handle.tl { left: -4px; top: -4px; cursor: nwse-resize; }
.resize-handle.tr { right: -4px; top: -4px; cursor: nesw-resize; }
.resize-handle.bl { left: -4px; bottom: -4px; cursor: nesw-resize; }
.resize-handle.br { right: -4px; bottom: -4px; cursor: nwse-resize; }

.right-panel {
  width: 260px;
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

.bottom-elements {
  display: flex;
  align-items: center;
  gap: 10px;
}

.elements-list {
  display: flex;
  align-items: center;
  gap: 8px;
  max-width: 560px;
  overflow: auto;
}

.elements-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 26px;
  padding: 0 8px;
  border: 1px solid #ebeef5;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
  user-select: none;
}

.elements-item.active {
  border-color: #409EFF;
}

.drag-handle {
  cursor: grab;
  color: #909399;
}

.elements-index {
  color: #909399;
  font-size: 12px;
}

.elements-name {
  color: #303133;
  font-size: 12px;
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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
