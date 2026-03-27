<template>
  <div class="workflow-layout">
    <div class="workflow-tabs">
      <el-menu
        mode="horizontal"
        router
        :default-active="route.path"
        class="workflow-tabs-menu"
      >
        <el-menu-item index="/workflow/models">流程设计</el-menu-item>
        <el-menu-item index="/workflow/binding">流程绑定</el-menu-item>
        <el-menu-item index="/workflow/todo">我的待办</el-menu-item>
        <el-menu-item index="/workflow/done">我的已办</el-menu-item>
        <el-menu-item index="/workflow/mine">我发起的</el-menu-item>
      </el-menu>
    </div>
    <div class="workflow-body">
      <router-view />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<style scoped>
.workflow-layout {
  background: #f5f7fa;
  min-height: calc(100vh - 90px);
}

.workflow-tabs {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 12px;
}

.workflow-tabs-menu {
  border-bottom: none;
}

.workflow-body {
  padding: 12px;
}
</style>

