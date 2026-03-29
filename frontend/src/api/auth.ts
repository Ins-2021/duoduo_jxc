import request from '@/utils/request'
import type {
  LoginRequest, RefreshTokenRequest,
  ChangePasswordRequest, CurrentUserResponse, MenuTreeNode,
} from '@/types'

export function login(data: LoginRequest) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function refreshToken(data: RefreshTokenRequest) {
  return request({
    url: '/auth/refresh',
    method: 'post',
    data
  })
}

export function logout(refreshToken?: string) {
  return request({
    url: '/auth/logout',
    method: 'post',
    params: refreshToken ? { refreshToken } : undefined
  })
}

export function changePassword(data: ChangePasswordRequest) {
  return request({
    url: '/system/profile/change-password',
    method: 'post',
    data
  })
}

export function getProfile() {
  return request({
    url: '/system/profile',
    method: 'get'
  })
}

export function getRouters() {
  return request({
    url: '/system/menus/routers',
    method: 'get'
  })
}
