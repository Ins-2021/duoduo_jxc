import axios from 'axios'
import { ElMessage } from 'element-plus'
import pinia from '@/store'
import { useUserStore } from '@/store/user'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api/v1',
  timeout: 50000
}) as any

// Request interceptor
service.interceptors.request.use(
  config => {
    const userStore = useUserStore(pinia)
    userStore.initialize()
    const token = userStore.accessToken
    if (token) {
      config.headers = config.headers || {}
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// Response interceptor
service.interceptors.response.use(
  response => {
    const res = response.data
    // If the custom code is not 200, it is judged as an error.
    if (res.code !== 200) {
      ElMessage({
        message: res.msg || 'Error',
        type: 'error',
        duration: 5 * 1000
      })
      return Promise.reject(new Error(res.msg || 'Error'))
    } else {
      return res
    }
  },
  error => {
    const status = error?.response?.status
    if (status === 401) {
      const userStore = useUserStore(pinia)
      userStore.clearAuth()
      if (location.pathname !== '/login') {
        location.href = '/login'
      }
    }
    if (status === 403) {
      ElMessage({
        message: '无权限',
        type: 'error',
        duration: 5 * 1000
      })
      return Promise.reject(error)
    }
    ElMessage({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
