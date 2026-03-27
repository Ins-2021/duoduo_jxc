<template>
  <div class="system-layout">
    <div class="system-tabs">
      <el-menu
        mode="horizontal"
        router
        :default-active="route.path"
        class="system-tabs-menu"
      >
        <el-menu-item index="/system/users" v-perm="'system:user:view'">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/system/roles" v-perm="'system:role:view'">
          <el-icon><Avatar /></el-icon>
          <span>角色管理</span>
        </el-menu-item>
        <el-menu-item index="/system/menus" v-perm="'system:menu:view'">
          <el-icon><Menu /></el-icon>
          <span>菜单管理</span>
        </el-menu-item>
        <el-menu-item index="/system/depts" v-perm="'system:dept:view'">
          <el-icon><OfficeBuilding /></el-icon>
          <span>部门管理</span>
        </el-menu-item>
        <el-menu-item index="/system/posts" v-perm="'system:post:view'">
          <el-icon><Postcard /></el-icon>
          <span>岗位管理</span>
        </el-menu-item>
      </el-menu>
    </div>
    <div class="system-body">
      <router-view />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import { User, Avatar, Menu, OfficeBuilding, Postcard } from '@element-plus/icons-vue'

const route = useRoute()
</script>

<style scoped>
.system-layout {
  background: #f5f7fa;
  min-height: calc(100vh - 90px);
}

.system-tabs {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 12px;
}

.system-tabs-menu {
  border-bottom: none;
}

.system-body {
  padding: 12px;
}
</style>

