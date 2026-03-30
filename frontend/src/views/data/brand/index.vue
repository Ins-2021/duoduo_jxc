<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-input
          v-model="queryParams.keyword"
          placeholder="品牌名称"
          style="width: 200px"
          class="filter-item"
          @keyup.enter="handleQuery"
        />
        <el-button class="filter-item" type="primary" @click="handleQuery">查询</el-button>
        <el-button class="filter-item" type="primary" plain @click="handleAdd">新增</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border style="width: 100%">
        <el-table-column prop="brandId" label="ID" width="80" />
        <el-table-column prop="brandName" label="品牌名称" min-width="150" />
        <el-table-column prop="brandCode" label="品牌编码" min-width="120" />
        <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
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

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="品牌名称" prop="brandName">
          <el-input v-model="form.brandName" placeholder="请输入品牌名称" />
        </el-form-item>
        <el-form-item label="品牌编码" prop="brandCode">
          <el-input v-model="form.brandCode" placeholder="请输入品牌编码" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getBrandList, addBrand, updateBrand, deleteBrand } from '@/api/product'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const form = reactive({
  brandId: undefined as number | undefined,
  brandName: '',
  brandCode: '',
  remark: ''
})
const rules = {
  brandName: [{ required: true, message: '请输入品牌名称', trigger: 'blur' }]
}

const handleQuery = () => {
  loading.value = true
  getBrandList(queryParams).then((res: any) => {
    list.value = res.data?.list || res.data?.records || []
    total.value = res.data?.total || 0
  }).finally(() => {
    loading.value = false
  })
}

const resetForm = () => {
  form.brandId = undefined
  form.brandName = ''
  form.brandCode = ''
  form.remark = ''
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增品牌'
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  resetForm()
  form.brandId = row.brandId
  form.brandName = row.brandName
  form.brandCode = row.brandCode
  form.remark = row.remark
  dialogTitle.value = '编辑品牌'
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确认要删除品牌"${row.brandName}"吗？`, '提示', {
    type: 'warning'
  }).then(() => {
    deleteBrand(row.brandId).then(() => {
      ElMessage.success('删除成功')
      handleQuery()
    })
  })
}

const submitForm = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      if (form.brandId) {
        updateBrand(form).then(() => {
          ElMessage.success('修改成功')
          dialogVisible.value = false
          handleQuery()
        })
      } else {
        addBrand(form).then(() => {
          ElMessage.success('新增成功')
          dialogVisible.value = false
          handleQuery()
        })
      }
    }
  })
}

onMounted(() => {
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
