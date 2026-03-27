<template>
  <div class="tags-view-wrapper">
    <div class="tags-left-actions">
      <el-icon class="action-btn scroll-btn" :class="{ disabled: !canScrollLeft }" @click="scrollLeft"><DArrowLeft /></el-icon>
      <el-icon class="action-btn home-btn" @click="goToHome"><HomeFilled /></el-icon>
    </div>
    
    <div ref="tagsScrollRef" class="tags-scroll-container" @scroll="updateScrollButtons">
      <div 
        v-for="tag in visitedViews" 
        :key="tag.path"
        class="tag-item"
        :class="{ active: isActive(tag) }"
        @click="goToTag(tag)"
      >
        <span class="tag-title">{{ tag.title }}</span>
        <el-icon v-if="!tag.affix" class="close-icon" @click.stop="closeTag(tag)"><Close /></el-icon>
      </div>
    </div>

    <div class="tags-right-actions">
      <el-icon class="action-btn scroll-btn" :class="{ disabled: !canScrollRight }" @click="scrollRight"><DArrowRight /></el-icon>
      <el-icon class="action-btn"><ArrowDown /></el-icon>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

interface TagView {
  path: string
  title: string
  fullPath: string
  affix?: boolean
}

const visitedViews = ref<TagView[]>([])
const tagsScrollRef = ref<HTMLDivElement | null>(null)
const canScrollLeft = ref(false)
const canScrollRight = ref(false)

// 判断当前标签是否激活
const isActive = (tag: TagView) => {
  return tag.path === route.path
}

// 回到首页
const goToHome = () => {
  router.push('/home')
}

// 添加当前路由到页签
const addView = () => {
  const { path, meta, fullPath } = route
  if (path === '/') return // 忽略根路径重定向
  
  if (!visitedViews.value.some(v => v.path === path)) {
    visitedViews.value.push({
      path,
      fullPath,
      title: (meta.title as string) || '未命名',
    })
  }
}

const updateScrollButtons = () => {
  const el = tagsScrollRef.value
  if (!el) {
    canScrollLeft.value = false
    canScrollRight.value = false
    return
  }

  const maxScrollLeft = Math.max(0, el.scrollWidth - el.clientWidth)
  canScrollLeft.value = el.scrollLeft > 0
  canScrollRight.value = el.scrollLeft < maxScrollLeft - 1
}

const scrollByAmount = (direction: -1 | 1) => {
  const el = tagsScrollRef.value
  if (!el) return
  if (direction === -1 && !canScrollLeft.value) return
  if (direction === 1 && !canScrollRight.value) return

  const amount = Math.max(120, Math.floor(el.clientWidth * 0.6))
  el.scrollTo({ left: el.scrollLeft + direction * amount, behavior: 'smooth' })
}

const scrollLeft = () => scrollByAmount(-1)
const scrollRight = () => scrollByAmount(1)

const scrollActiveIntoView = () => {
  nextTick(() => {
    const el = tagsScrollRef.value
    if (!el) return
    const active = el.querySelector('.tag-item.active') as HTMLElement | null
    if (active) active.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'nearest' })
    updateScrollButtons()
  })
}

// 点击页签跳转
const goToTag = (tag: TagView) => {
  router.push(tag.fullPath)
}

// 关闭页签
const closeTag = (tag: TagView) => {
  const index = visitedViews.value.findIndex(v => v.path === tag.path)
  visitedViews.value.splice(index, 1)
  
  // 如果关闭的是当前激活的页签，则跳转到前一个或后一个页签
  if (isActive(tag)) {
    const latestView = visitedViews.value[visitedViews.value.length - 1]
    if (latestView) {
      router.push(latestView.fullPath)
    } else {
      router.push('/home')
    }
  }
  scrollActiveIntoView()
}

// 监听路由变化自动添加页签
watch(() => route.path, () => {
  addView()
  scrollActiveIntoView()
})

onMounted(() => {
  addView()
  updateScrollButtons()
  window.addEventListener('resize', updateScrollButtons)
  scrollActiveIntoView()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', updateScrollButtons)
})
</script>

<style scoped>
.tags-view-wrapper {
  height: 100%;
  display: flex;
  align-items: center;
  background-color: #f5f7f9;
  border-bottom: 1px solid #dcdfe6;
}

.tags-left-actions, .tags-right-actions {
  display: flex;
  align-items: center;
  padding: 0 10px;
  background-color: #fff;
  height: 100%;
  border-right: 1px solid #dcdfe6;
}
.tags-right-actions {
  border-right: none;
  border-left: 1px solid #dcdfe6;
}

.action-btn {
  font-size: 14px;
  color: #909399;
  cursor: pointer;
  padding: 0 5px;
}
.action-btn.scroll-btn {
  width: 28px;
  height: 28px;
  padding: 0;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  border-radius: 4px;
}
.action-btn.disabled {
  cursor: not-allowed;
  color: #c0c4cc;
}
.action-btn:hover {
  color: #409eff;
}
.action-btn.scroll-btn:hover {
  background-color: #ecf5ff;
}
.action-btn.disabled:hover {
  color: #c0c4cc;
}
.action-btn.scroll-btn.disabled:hover {
  background-color: transparent;
}
.home-btn {
  font-size: 16px;
  margin-left: 5px;
}

.tags-scroll-container {
  flex: 1;
  display: flex;
  align-items: flex-end;
  height: 100%;
  overflow-x: auto;
  overflow-y: hidden;
  padding: 0 10px;
  /* 隐藏滚动条 */
  scrollbar-width: none;
  -ms-overflow-style: none;
}
.tags-scroll-container::-webkit-scrollbar {
  display: none;
}

.tag-item {
  display: inline-flex;
  align-items: center;
  height: 32px;
  line-height: 32px;
  padding: 0 15px;
  margin-right: 5px;
  background-color: #fff;
  color: #606266;
  font-size: 13px;
  cursor: pointer;
  border-radius: 4px 4px 0 0;
  border: 1px solid #e4e7ed;
  border-bottom: none;
  position: relative;
  top: 1px;
  flex: 0 0 auto;
  max-width: 220px;
}

.tag-item:hover {
  color: #409eff;
}

.tag-item.active {
  background-color: #fff;
  color: #303133;
  font-weight: bold;
  border-bottom: 1px solid #fff;
}
.tag-item.active::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #409eff;
}

.close-icon {
  margin-left: 8px;
  font-size: 12px;
  border-radius: 50%;
  width: 14px;
  height: 14px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
.close-icon:hover {
  background-color: #f56c6c;
  color: #fff;
}

.tag-title {
  flex: 1 1 auto;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
