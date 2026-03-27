<template>
  <div class="settings-layout">
    <div class="settings-tabs">
      <el-menu
        mode="horizontal"
        router
        :default-active="route.path"
        class="settings-tabs-menu"
      >
        <el-menu-item v-for="t in tabs" :key="t.path" :index="t.path">{{ t.label }}</el-menu-item>
      </el-menu>
    </div>
    <div class="settings-body">
      <router-view />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { routes } from '@/router'
import pinia from '@/store'
import { useUserStore } from '@/store/user'

const route = useRoute()
const userStore = useUserStore(pinia)
userStore.initialize()

const tabs = computed(() => {
  const rootRoute = routes.find(item => item.path === '/')
  const settingsRoute = (rootRoute?.children || []).find(item => item.path === 'settings')
  return (settingsRoute?.children || [])
    .filter(item => !(item.meta as any)?.hidden)
    .filter(item => {
      const perm = (item.meta as any)?.perm
      return !perm || userStore.hasPerm(String(perm))
    })
    .map(item => ({
      path: `/settings/${item.path}`,
      label: String((item.meta as any)?.title || '')
    }))
})
</script>

<style scoped>
.settings-layout {
  background: #f5f7fa;
  min-height: calc(100vh - 90px);
}

.settings-tabs {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 12px;
}

.settings-tabs-menu {
  border-bottom: none;
}

.settings-body {
  padding: 12px;
}
</style>
