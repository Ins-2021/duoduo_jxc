<template>
  <div class="login">
    <el-card class="card">
      <div class="title">系统登录</div>
      <el-form @submit.prevent>
        <el-form-item label="账号">
          <el-input v-model="form.username" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" autocomplete="current-password" show-password />
        </el-form-item>
        <el-button type="primary" style="width: 100%" @click="doLogin" :loading="loading">登录</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, getProfile } from '@/api/auth'
import pinia from '@/store'
import { useUserStore } from '@/store/user'

const router = useRouter()
const loading = ref(false)
const userStore = useUserStore(pinia)

const form = reactive({
  username: '',
  password: ''
})

if (import.meta.env.DEV) {
  form.username = 'admin'
  form.password = 'admin123'
}

const doLogin = async () => {
  loading.value = true
  try {
    console.log('Starting login...')
    const res: any = await login(form)
    console.log('Login response:', res)
    const data = res.data
    console.log('Login data:', { accessToken: data.accessToken ? 'exists' : 'empty', refreshToken: data.refreshToken ? 'exists' : 'empty' })
    userStore.setTokens(data.accessToken, data.refreshToken)
    // 获取用户权限信息
    console.log('Fetching profile...')
    const profileRes: any = await getProfile()
    console.log('Profile response:', profileRes)
    if (profileRes?.data) {
      userStore.setProfile(profileRes.data)
    }
    ElMessage.success('登录成功')
    console.log('Navigating to /home')
    router.replace('/home')
  } catch (error) {
    console.error('Login error:', error)
    ElMessage.error('登录失败')
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
}

.tip {
  margin-top: 12px;
  color: #909399;
  font-size: 12px;
}
</style>
