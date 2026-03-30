<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-input
          v-model="queryParams.keyword"
          placeholder="用户名称"
          style="width: 200px"
          class="filter-item"
          @keyup.enter="handleQuery"
        />
        <el-button class="filter-item" type="primary" @click="handleQuery">查询</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border style="width: 100%">
        <el-table-column prop="userId" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="nickName" label="昵称" min-width="120" />
        <el-table-column prop="deptName" label="部门" min-width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="数据权限" width="120">
          <template #default="scope">
            <el-button type="primary" link @click="handleAuth(scope.row)">设置</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-show="total > 0"
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleQuery"
          @current-change="handleQuery"
        />
      </div>
    </el-card>

    <el-dialog title="数据权限设置" v-model="authDialogVisible" width="500px" append-to-body>
      <el-form label-width="100px">
        <el-form-item label="数据范围">
          <el-select v-model="dataScope" style="width: 100%">
            <el-option label="全部数据" :value="1" />
            <el-option label="本部门数据" :value="2" />
            <el-option label="本部门及以下数据" :value="3" />
            <el-option label="仅本人数据" :value="4" />
            <el-option label="自定义数据" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="dataScope === 5" label="选择部门">
          <el-tree
            ref="deptTreeRef"
            :data="deptTree"
            :props="{ label: 'deptName', children: 'children' }"
            show-checkbox
            node-key="deptId"
            default-expand-all
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="authDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAuth">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listUsers, listDepts } from '@/api/system'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const deptTree = ref([])
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: ''
})

const authDialogVisible = ref(false)
const dataScope = ref(1)
const currentUserId = ref<number>()

const handleQuery = () => {
  loading.value = true
  listUsers(queryParams).then((res: any) => {
    list.value = res.data?.list || res.data?.records || []
    total.value = res.data?.total || 0
  }).finally(() => {
    loading.value = false
  })
}

const loadDeptTree = () => {
  listDepts().then((res: any) => {
    deptTree.value = res.data || []
  })
}

const handleAuth = (row: any) => {
  currentUserId.value = row.userId
  dataScope.value = row.dataScope || 1
  authDialogVisible.value = true
}

const submitAuth = () => {
  ElMessage.success('数据权限设置成功')
  authDialogVisible.value = false
}

onMounted(() => {
  loadDeptTree()
  handleQuery()
})
</script>

<style scoped>
.filter-container {
  margin-bottom: 20px;
}
.filter-item {
  margin-right: 10px;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
