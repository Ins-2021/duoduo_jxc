<template>
  <div class="app-container">
    <el-card shadow="never">
      <!-- 搜索表单 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
        <el-form-item label="岗位编码" prop="postCode">
          <el-input
            v-model="queryParams.postCode"
            placeholder="请输入岗位编码"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="岗位名称" prop="postName">
          <el-input
            v-model="queryParams.postName"
            placeholder="请输入岗位名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="岗位状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="Plus"
            @click="handleAdd"
            v-perm="'system:post:add'"
          >新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-perm="'system:post:delete'"
          >删除</el-button>
        </el-col>
      </el-row>

      <!-- 岗位表格 -->
      <el-table v-loading="loading" :data="postList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="岗位编号" align="center" prop="postId" />
        <el-table-column label="岗位编码" align="center" prop="postCode" />
        <el-table-column label="岗位名称" align="center" prop="postName" />
        <el-table-column label="岗位排序" align="center" prop="postSort" />
        <el-table-column label="状态" align="center" prop="status">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-perm="'system:post:edit'">修改</el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-perm="'system:post:delete'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-show="total > 0"
        :total="total"
        v-model:current-page="queryParams.current"
        v-model:page-size="queryParams.size"
        @size-change="getList"
        @current-change="getList"
        class="mt-4"
        layout="total, sizes, prev, pager, next, jumper"
      />
    </el-card>

    <!-- 添加或修改岗位对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="postRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="岗位名称" prop="postName">
          <el-input v-model="form.postName" placeholder="请输入岗位名称" />
        </el-form-item>
        <el-form-item label="岗位编码" prop="postCode">
          <el-input v-model="form.postCode" placeholder="请输入编码名称" />
        </el-form-item>
        <el-form-item label="岗位顺序" prop="postSort">
          <el-input-number v-model="form.postSort" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="岗位状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, toRefs, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pagePosts, getPost, createPost, updatePost, deletePost } from '@/api/system'

const postList = ref<any[]>([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref<number[]>([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref('')
const postRef = ref()

const data = reactive({
  form: {} as any,
  queryParams: {
    current: 1,
    size: 10,
    postCode: undefined,
    postName: undefined,
    status: undefined
  },
  rules: {
    postName: [{ required: true, message: '岗位名称不能为空', trigger: 'blur' }],
    postCode: [{ required: true, message: '岗位编码不能为空', trigger: 'blur' }],
    postSort: [{ required: true, message: '岗位顺序不能为空', trigger: 'blur' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  pagePosts(queryParams.value).then((res: any) => {
    postList.value = res.data.records
    total.value = res.data.total
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    postId: undefined,
    postCode: undefined,
    postName: undefined,
    postSort: 0,
    status: 1,
    remark: undefined
  }
  if (postRef.value) {
    postRef.value.resetFields()
  }
}

function handleQuery() {
  queryParams.value.current = 1
  getList()
}

function resetQuery() {
  queryParams.value.postCode = undefined
  queryParams.value.postName = undefined
  queryParams.value.status = undefined
  handleQuery()
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map(item => item.postId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '添加岗位'
}

function handleUpdate(row: any) {
  reset()
  const postId = row.postId || ids.value[0]
  getPost(postId).then((res: any) => {
    form.value = res.data
    open.value = true
    title.value = '修改岗位'
  })
}

function submitForm() {
  postRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.postId) {
        updatePost(form.value).then(() => {
          ElMessage.success('修改成功')
          open.value = false
          getList()
        })
      } else {
        createPost(form.value).then(() => {
          ElMessage.success('新增成功')
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(row: any) {
  const postIds = row.postId ? [row.postId] : ids.value
  ElMessageBox.confirm('是否确认删除所选数据项?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return deletePost(postIds.join(','))
  }).then(() => {
    getList()
    ElMessage.success('删除成功')
  }).catch(() => {})
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.mb8 {
  margin-bottom: 8px;
}
.mt-4 {
  margin-top: 16px;
}
</style>