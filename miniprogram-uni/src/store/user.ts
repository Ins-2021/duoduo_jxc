import { defineStore } from 'pinia'
import { ref } from 'vue'
import { loginByWechat, getUserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(uni.getStorageSync('token') || '')
  const userInfo = ref<any>({})
  const roles = ref<string[]>([])

  const setToken = (t: string) => {
    token.value = t
    uni.setStorageSync('token', t)
  }

  const loginByPhone = async (code: string) => {
    const res = await loginByWechat(code)
    if (res.success) {
      setToken(res.data.token)
      roles.value = res.data.roles || []
      userInfo.value = res.data.userInfo || {}
    }
    return res
  }

  const fetchUserInfo = async () => {
    const res = await getUserInfo()
    if (res.success) {
      userInfo.value = res.data
      roles.value = res.data.roles || []
    }
  }

  const logout = () => {
    token.value = ''
    userInfo.value = {}
    roles.value = []
    uni.removeStorageSync('token')
    uni.reLaunch({ url: '/pages/common/login/index' })
  }

  const isLoggedIn = () => !!token.value

  return { token, userInfo, roles, setToken, loginByPhone, fetchUserInfo, logout, isLoggedIn }
})
