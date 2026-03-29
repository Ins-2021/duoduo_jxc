import request from '@/utils/request'
import type { PrintTemplateDTO, PageQuery } from '@/types'

export interface PrintTemplateQuery extends PageQuery {
  templateName?: string
  bizType?: string
  showDisabled?: boolean
}

export function pagePrintTemplate(params: PrintTemplateQuery) {
  return request({
    url: '/print-template/page',
    method: 'get',
    params
  })
}

export function getPrintTemplate(id: number) {
  return request({
    url: `/print-template/${id}`,
    method: 'get'
  })
}

export function savePrintTemplate(data: Partial<PrintTemplateDTO>) {
  return request({
    url: '/print-template',
    method: 'post',
    data
  })
}

export function deletePrintTemplate(id: number) {
  return request({
    url: `/print-template/${id}`,
    method: 'delete'
  })
}

export function copyPrintTemplate(id: number) {
  return request({
    url: `/print-template/${id}/copy`,
    method: 'post'
  })
}

export function setDefaultPrintTemplate(id: number) {
  return request({
    url: `/print-template/${id}/set-default`,
    method: 'post'
  })
}

export function setPrintTemplateEnabled(id: number, enabled: number) {
  return request({
    url: `/print-template/${id}/enabled`,
    method: 'post',
    data: { enabled }
  })
}

export function getPrintTemplateSettings() {
  return request({
    url: '/print-template/settings',
    method: 'get'
  })
}

export function savePrintTemplateSettings(data: { printNoDialog: boolean }) {
  return request({
    url: '/print-template/settings',
    method: 'put',
    data
  })
}
