<script setup lang="ts">
import { onErrorCaptured } from 'vue'
import { ElMessage } from 'element-plus'

onErrorCaptured((error, instance, info) => {
  console.error('[App ErrorBoundary]', error, info)
  ElMessage.error('页面渲染出错，请刷新重试')
  return false
})
</script>

<template>
  <router-view />
</template>

<style>
#app {
  width: 100%;
  min-width: 1200px;
  height: 100vh;
  margin: 0;
  padding: 0;
}
</style>
