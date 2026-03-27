import request from '@/utils/request'
import type { UiViewSummaryDTO } from '@/types'

export function listViews(params?: { keyword?: string }) {
  return request({
    url: '/ui/views',
    method: 'get',
    params
  })
}

export function getViewConfig(viewKey: string) {
  return request({
    url: '/ui/view-config',
    method: 'get',
    params: { viewKey }
  })
}

export function saveViewConfig(viewKey: string, columns: Record<string, unknown>[]) {
  return request({
    url: '/ui/view-config',
    method: 'put',
    data: { viewKey, columns }
  })
}

export function resetViewConfig(viewKey: string) {
  return request({
    url: '/ui/view-config/reset',
    method: 'post',
    data: { viewKey }
  })
}
