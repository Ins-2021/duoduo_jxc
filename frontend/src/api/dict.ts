import request from '@/utils/request'

export function getDict(key: string) {
  return request({
    url: `/dict/${key}`,
    method: 'get'
  })
}

