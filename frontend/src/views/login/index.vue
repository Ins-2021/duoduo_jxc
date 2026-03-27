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
    const res: any = await login(form)
    const data = res.data
    userStore.setTokens(data.accessToken, data.refreshToken)
    const profile: any = await getProfile()
    userStore.setProfile(profile.data || {})
    ElMessage.success('登录成功')
    router.replace('/home')
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
