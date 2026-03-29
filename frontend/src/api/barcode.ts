import request from '@/utils/request'

export interface BarcodeRuleDTO {
  ruleId?: number
  ruleName?: string
  ruleType?: string
  prefix?: string
  dateFormat?: string
  seqLength?: number
  ruleExpression?: string
  example?: string
  isDefault?: number
  isActive?: number
}

export interface BarcodeDTO {
  barcodeId?: number
  barcodeContent?: string
  refType?: string
  refId?: number
  ruleId?: number
  barcodeType?: string
  printed?: number
}

export interface BarcodeGenerateRequest {
  barcodeType: string
  refType?: string
  refId: number
  count?: number
  ruleId?: number
}

// ---- 条码记录 ----

export function generateBarcode(data: BarcodeGenerateRequest) {
  return request({
    url: '/inventory/barcode/generate',
    method: 'post',
    data
  })
}

export function parseBarcode(content: string) {
  return request({
    url: '/inventory/barcode/parse',
    method: 'get',
    params: { content }
  })
}

export function printBarcode(id: number) {
  return request({
    url: `/inventory/barcode/print/${id}`,
    method: 'post'
  })
}

export function printBarcodeBatch(ids: number[]) {
  return request({
    url: '/inventory/barcode/print/batch',
    method: 'post',
    data: ids
  })
}

// ---- 条码规则 ----

export function getRulePage(params: { pageNum: number; pageSize: number; keyword?: string; ruleType?: string }) {
  return request({
    url: '/inventory/barcode/rule/page',
    method: 'get',
    params
  })
}

export function getRuleDetail(id: number) {
  return request({
    url: `/inventory/barcode/rule/${id}`,
    method: 'get'
  })
}

export function addRule(data: Omit<BarcodeRuleDTO, 'ruleId'>) {
  return request({
    url: '/inventory/barcode/rule',
    method: 'post',
    data
  })
}

export function updateRule(id: number, data: BarcodeRuleDTO) {
  return request({
    url: `/inventory/barcode/rule/${id}`,
    method: 'put',
    data
  })
}

export function deleteRule(id: number) {
  return request({
    url: `/inventory/barcode/rule/${id}`,
    method: 'delete'
  })
}
