import request from '@/utils/request'
import type { SystemSettingsDTO } from '@/types'

export function getSystemSettings() {
  return request<SystemSettingsDTO>({
    url: '/settings',
    method: 'get'
  })
}

export function saveSystemSettings(data: Partial<SystemSettingsDTO>) {
  return request({
    url: '/settings',
    method: 'put',
    data
  })
}
