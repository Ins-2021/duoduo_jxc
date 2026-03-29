import http from '@/utils/request'

export function loginByWechat(code: string) {
  return http.post('/auth/login/wechat', { code })
}

export function getUserInfo() {
  return http.get('/auth/user/info')
}
