import request from '@/utils/request'

export function downloadImportTemplate(bizType: string) {
  return request({
    url: `/api/import-export/template/${bizType}`,
    method: 'get',
    responseType: 'blob'
  })
}

