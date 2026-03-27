<template>
  <div class="forbidden">
    <el-result icon="warning" title="无权限" sub-title="你没有权限访问该页面">
      <template #extra>
        <el-button type="primary" @click="goHome">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()
const goHome = () => {
  router.replace('/home')
}
</script>

<style scoped>
.forbidden {
  padding: 40px;
}
</style>

