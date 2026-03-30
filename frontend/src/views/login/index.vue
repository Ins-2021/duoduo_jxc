<template>
  <div class="login">
    <el-card class="card">
      <div class="title">系统登录</div>
      <el-form @submit.prevent>
        <el-form-item label="账号">
          <el-input 
            v-model="form.username" 
            autocomplete="username" 
            placeholder="请输入用户名"
            @keyup.enter="doLogin"
          />
        </el-form-item>
        <el-form-item label="密码">
          <el-input 
            v-model="form.password" 
            type="password" 
            autocomplete="current-password" 
            show-password 
            placeholder="请输入密码"
            @keyup.enter="doLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="rememberMe">记住我</el-checkbox>
        </el-form-item>
        <el-button type="primary" style="width: 100%" @click="doLogin" :loading="loading">登录</el-button>
      </el-form>
      <div v-if="errorMessage" class="error-message">
        <el-icon><Warning /></el-icon>
        {{ errorMessage }}
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Warning } from '@element-plus/icons-vue'
import { login, getProfile } from '@/api/auth'
import pinia from '@/store'
import { useUserStore } from '@/store/user'

const router = useRouter()
const loading = ref(false)
const errorMessage = ref('')
const rememberMe = ref(false)
const userStore = useUserStore(pinia)

const form = reactive({
  username: '',
  password: ''
})

// 页面加载时恢复记住的用户名
onMounted(() => {
  const savedUsername = localStorage.getItem('remembered_username')
  if (savedUsername) {
    form.username = savedUsername
    rememberMe.value = true
  }
  
  if (import.meta.env.DEV && !form.username) {
    form.username = 'admin'
    form.password = 'admin123'
  }
})

const doLogin = async () => {
  // 表单验证
  if (!form.username.trim()) {
    errorMessage.value = '请输入用户名'
    return
  }
  if (!form.password.trim()) {
    errorMessage.value = '请输入密码'
    return
  }
  
  errorMessage.value = ''
  loading.value = true
  
  try {
    console.log('Starting login...')
    const res: any = await login(form)
    console.log('Login response:', res)
    const data = res.data
    
    // 保存记住的用户名
    if (rememberMe.value) {
      localStorage.setItem('remembered_username', form.username)
    } else {
      localStorage.removeItem('remembered_username')
    }
    
    userStore.setTokens(data.accessToken, data.refreshToken)
    // 获取用户权限信息
    const profileRes: any = await getProfile()
    if (profileRes?.data) {
      userStore.setProfile(profileRes.data)
    }
    ElMessage.success('登录成功')
    router.replace('/home')
  } catch (error: any) {
    console.error('Login error:', error)
    // 显示详细错误信息
    if (error.response?.data?.message) {
      errorMessage.value = error.response.data.message
    } else if (error.message) {
      errorMessage.value = error.message
    } else {
      errorMessage.value = '登录失败，请检查用户名和密码'
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login {
  height: 100vh;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card {
  width: 420px;
}

.title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  text-align: center;
}

.error-message {
  margin-top: 16px;
  padding: 10px;
  background: #fef0f0;
  border-radius: 4px;
  color: #f56c6c;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.tip {
  margin-top: 12px;
  color: #909399;
  font-size: 12px;
}
</style>
