import { defineStore } from 'pinia'
import { getProfile, getRouters } from '@/api/auth'

const ACCESS_TOKEN_KEY = 'dd_access_token'
const REFRESH_TOKEN_KEY = 'dd_refresh_token'
const PERMS_KEY = 'dd_perms'
const PROFILE_KEY = 'dd_profile'

type UserProfile = {
  userId?: number
  username?: string
  realName?: string
  perms?: string[]
  roles?: string[]
}

export type UserMenuItem = {
  menuId: number
  parentId: number
  menuName: string
  orderNum: number
  path: string
  component: string
  routeName: string
  icon: string
  menuType: string
  visible: number
  status: number
  perms: string
  children?: UserMenuItem[]
}

const readJson = <T>(key: string, fallback: T): T => {
  const raw = localStorage.getItem(key)
  if (!raw) return fallback
  try {
    return JSON.parse(raw) as T
  } catch {
    return fallback
  }
}

export const useUserStore = defineStore('user', {
  state: () => ({
    initialized: false,
    accessToken: '',
    refreshToken: '',
    perms: [] as string[],
    profile: {} as UserProfile,
    userMenus: [] as UserMenuItem[],
    menusLoaded: false
  }),
  getters: {
    displayName(state) {
      return state.profile.realName || state.profile.username || '管理员'
    }
  },
  actions: {
    initialize() {
      if (this.initialized) {
        return
      }
      this.accessToken = sessionStorage.getItem(ACCESS_TOKEN_KEY) || ''
      this.refreshToken = sessionStorage.getItem(REFRESH_TOKEN_KEY) || ''
      this.perms = readJson<string[]>(PERMS_KEY, [])
      this.profile = readJson<UserProfile>(PROFILE_KEY, {})
      this.initialized = true
    },
    setTokens(accessToken: string, refreshToken: string) {
      this.accessToken = accessToken || ''
      this.refreshToken = refreshToken || ''
      if (this.accessToken) {
        sessionStorage.setItem(ACCESS_TOKEN_KEY, this.accessToken)
      } else {
        sessionStorage.removeItem(ACCESS_TOKEN_KEY)
      }
      if (this.refreshToken) {
        sessionStorage.setItem(REFRESH_TOKEN_KEY, this.refreshToken)
      } else {
        sessionStorage.removeItem(REFRESH_TOKEN_KEY)
      }
    },
    setAccessToken(token: string) {
      this.accessToken = token || ''
      if (this.accessToken) {
        sessionStorage.setItem(ACCESS_TOKEN_KEY, this.accessToken)
        return
      }
      sessionStorage.removeItem(ACCESS_TOKEN_KEY)
    },
    setRefreshToken(token: string) {
      this.refreshToken = token || ''
      if (this.refreshToken) {
        sessionStorage.setItem(REFRESH_TOKEN_KEY, this.refreshToken)
        return
      }
      sessionStorage.removeItem(REFRESH_TOKEN_KEY)
    },
    setPerms(perms: string[]) {
      this.perms = Array.isArray(perms) ? perms : []
      localStorage.setItem(PERMS_KEY, JSON.stringify(this.perms))
    },
    setProfile(profile: UserProfile) {
      this.profile = profile || {}
      localStorage.setItem(PROFILE_KEY, JSON.stringify(this.profile))
      this.setPerms(profile?.perms || [])
    },
    clearAuth() {
      this.accessToken = ''
      this.refreshToken = ''
      this.perms = []
      this.profile = {}
      this.userMenus = []
      this.menusLoaded = false
      sessionStorage.removeItem(ACCESS_TOKEN_KEY)
      sessionStorage.removeItem(REFRESH_TOKEN_KEY)
      localStorage.removeItem(PERMS_KEY)
      localStorage.removeItem(PROFILE_KEY)
    },
    hasPerm(perm: string) {
      if (!perm) {
        return true
      }
      const perms = this.perms || []
      return perms.includes(perm)
    },
    async loadUserMenus() {
      if (this.menusLoaded) return
      try {
        // 同时刷新权限列表，确保新增的按钮权限能立即生效
        const [routersRes, profileRes]: any = await Promise.all([
          getRouters(),
          getProfile()
        ])
        this.userMenus = routersRes.data || []
        if (profileRes?.data) {
          this.setProfile(profileRes.data)
        }
        this.menusLoaded = true
      } catch {
        this.userMenus = []
        this.menusLoaded = false
      }
    },
    async refreshPerms() {
      try {
        const res: any = await getProfile()
        if (res?.data?.perms) {
          this.setPerms(res.data.perms)
        }
      } catch {
        // ignore
      }
    }
  }
})
