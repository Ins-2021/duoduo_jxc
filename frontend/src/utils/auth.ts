import pinia from '@/store'
import { useUserStore } from '@/store/user'

const ACCESS_TOKEN_KEY = 'dd_access_token'
const REFRESH_TOKEN_KEY = 'dd_refresh_token'
const PERMS_KEY = 'dd_perms'

export function getAccessToken() {
  const userStore = useUserStore(pinia)
  userStore.initialize()
  return userStore.accessToken || sessionStorage.getItem(ACCESS_TOKEN_KEY) || ''
}

export function setAccessToken(token: string) {
  const userStore = useUserStore(pinia)
  userStore.initialize()
  userStore.setAccessToken(token)
}

export function getRefreshToken() {
  const userStore = useUserStore(pinia)
  userStore.initialize()
  return userStore.refreshToken || sessionStorage.getItem(REFRESH_TOKEN_KEY) || ''
}

export function setRefreshToken(token: string) {
  const userStore = useUserStore(pinia)
  userStore.initialize()
  userStore.setRefreshToken(token)
}

export function clearAuth() {
  const userStore = useUserStore(pinia)
  userStore.initialize()
  userStore.clearAuth()
}

export function setPerms(perms: string[]) {
  const userStore = useUserStore(pinia)
  userStore.initialize()
  userStore.setPerms(perms || [])
}

export function getPerms(): string[] {
  const userStore = useUserStore(pinia)
  userStore.initialize()
  const perms = userStore.perms || []
  if (perms.length) return perms
  const raw = localStorage.getItem(PERMS_KEY)
  if (!raw) return []
  try {
    const v = JSON.parse(raw)
    return Array.isArray(v) ? v : []
  } catch {
    return []
  }
}

export function hasPerm(perm: string) {
  const userStore = useUserStore(pinia)
  userStore.initialize()
  return userStore.hasPerm(perm)
}
