import { Canvas, FabricObject } from 'fabric'
import JsBarcode from 'jsbarcode'
import QRCode from 'qrcode'

type AnyRecord = Record<string, any>

function tokenizePath(path: string): Array<string | number> {
  const tokens: Array<string | number> = []
  const re = /([^[.\]]+)|\[(\d+)\]/g
  let m: RegExpExecArray | null
  while ((m = re.exec(path))) {
    if (m[1] != null) tokens.push(m[1])
    else if (m[2] != null) tokens.push(Number(m[2]))
  }
  return tokens
}

function getByPath(data: any, path: string): any {
  if (data == null) return undefined
  const tokens = tokenizePath(path)
  let cur = data
  for (const t of tokens) {
    if (cur == null) return undefined
    cur = (cur as any)[t as any]
  }
  return cur
}

function formatValue(val: any): string {
  if (val == null) return ''
  if (typeof val === 'string') return val
  if (typeof val === 'number') return String(val)
  if (typeof val === 'boolean') return val ? '是' : '否'
  if (val instanceof Date) return val.toISOString()
  return String(val)
}

export function renderTemplateText(text: string, data: AnyRecord): string {
  if (!text) return ''
  return text.replace(/\{\{\s*([^}]+?)\s*\}\}/g, (_all, expr) => {
    const v = getByPath(data, String(expr))
    return formatValue(v)
  })
}

async function buildBarcodeDataUrl(value: string) {
  const cv = document.createElement('canvas')
  JsBarcode(cv, value || '1234567890', { displayValue: false, margin: 0 })
  return cv.toDataURL('image/png')
}

async function buildQrDataUrl(value: string) {
  return QRCode.toDataURL(value || 'https://example.com', { margin: 0, width: 240 })
}

function loadImage(src: string) {
  return new Promise<HTMLImageElement>((resolve, reject) => {
    const img = new Image()
    img.onload = () => resolve(img)
    img.onerror = reject
    img.src = src
  })
}

async function applyBindingsToCanvas(c: Canvas, data: AnyRecord) {
  const objs = c.getObjects()
  for (const obj of objs as any[]) {
    const kind = obj?.data?.kind
    if (obj.type === 'textbox') {
      const raw = obj?.data?.rawText ?? obj.text ?? ''
      const rendered = renderTemplateText(String(raw), data)
      obj.text = rendered
      obj.setCoords()
      continue
    }
    if (kind === 'barcode' || kind === 'qrcode') {
      const raw = obj?.data?.rawValue ?? obj?.data?.value ?? ''
      const rendered = renderTemplateText(String(raw), data)
      const dataUrl = kind === 'barcode' ? await buildBarcodeDataUrl(rendered) : await buildQrDataUrl(rendered)
      const imgEl = await loadImage(dataUrl)
      obj.setElement(imgEl)
      obj.data = { ...obj.data, value: rendered }
      obj.setCoords()
      continue
    }
  }
  c.requestRenderAll()
}

export function openPrintWindow(dataUrl: string, wMm: number, hMm: number) {
  const win = window.open('', '_blank')
  if (!win) return
  win.document.open()
  win.document.write(`
    <html>
      <head>
        <meta charset="utf-8">
        <style>
          @page { size: ${wMm}mm ${hMm}mm; margin: 0; }
          html, body { margin: 0; padding: 0; }
          .wrap { width: ${wMm}mm; height: ${hMm}mm; overflow: hidden; }
          img { width: 100%; height: 100%; object-fit: contain; }
        </style>
      </head>
      <body>
        <div class="wrap">
          <img src="${dataUrl}" />
        </div>
        <script>
          window.onload = function() { window.focus(); window.print(); }
        <\/script>
      </body>
    </html>
  `)
  win.document.close()
}

export async function renderFabricDesignToDataUrl(designJson: string, data: AnyRecord, opts?: { dpi?: number }) {
  const dpi = opts?.dpi ?? 200
  const pxPerMm = 96 / 25.4
  const parsed = JSON.parse(designJson || '{}')

  if (parsed?.engine !== 'fabric' || !parsed?.canvas) {
    throw new Error('Unsupported template engine')
  }

  const canvasEl = document.createElement('canvas')
  if (!FabricObject.customProperties.includes('data')) {
    FabricObject.customProperties.push('data')
  }
  const c = new Canvas(canvasEl, { selection: false })
  const wMm = Number(parsed?.paper?.paperWidthMm || parsed?.paperWidthMm || 210)
  const hMm = Number(parsed?.paper?.paperHeightMm || parsed?.paperHeightMm || 297)
  c.setWidth(wMm * pxPerMm)
  c.setHeight(hMm * pxPerMm)
  c.backgroundColor = parsed?.bg || '#ffffff'

  await c.loadFromJSON(parsed.canvas)

  const objs = c.getObjects() as any[]
  for (const obj of objs) {
    if (obj.type === 'textbox') {
      obj.data = { ...(obj.data || {}), rawText: obj?.data?.rawText ?? obj.text ?? '' }
    }
    if (obj?.data?.kind === 'barcode' || obj?.data?.kind === 'qrcode') {
      obj.data = { ...(obj.data || {}), rawValue: obj?.data?.rawValue ?? obj?.data?.value ?? '' }
    }
  }

  await applyBindingsToCanvas(c, data || {})

  const pxPerMmPrint = dpi / 25.4
  const multiplier = pxPerMmPrint / pxPerMm
  const dataUrl = c.toDataURL({ format: 'png', multiplier })
  c.dispose()

  return { dataUrl, wMm, hMm }
}

