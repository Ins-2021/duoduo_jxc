<template>
  <div class="app-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="面料编码">
          <el-input v-model="queryParams.fabricCode" placeholder="请输入面料编码" clearable />
        </el-form-item>
        <el-form-item label="面料名称">
          <el-input v-model="queryParams.fabricName" placeholder="请输入面料名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <el-button type="primary" @click="handleAdd">新增面料</el-button>
      </template>

      <el-table v-loading="loading" :data="tableData" border>
        <el-table-column type="index" label="序号" width="55" />
        <el-table-column prop="fabricCode" label="面料编码" />
        <el-table-column prop="fabricName" label="面料名称" />
        <el-table-column prop="fabricType" label="面料类型" />
        <el-table-column prop="composition" label="成分" />
        <el-table-column prop="color" label="颜色" />
        <el-table-column prop="unit" label="单位" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        @change="getList"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="面料编码" prop="fabricCode">
          <el-input v-model="form.fabricCode" placeholder="请输入面料编码" />
        </el-form-item>
        <el-form-item label="面料名称" prop="fabricName">
          <el-input v-model="form.fabricName" placeholder="请输入面料名称" />
        </el-form-item>
        <el-form-item label="面料类型" prop="fabricType">
          <el-select v-model="form.fabricType" placeholder="请选择面料类型">
            <el-option label="针织" value="针织" />
            <el-option label="梭织" value="梭织" />
            <el-option label="毛织" value="毛织" />
          </el-select>
        </el-form-item>
        <el-form-item label="成分" prop="composition">
          <el-input v-model="form.composition" placeholder="如：棉60% 涤纶40%" />
        </el-form-item>
        <el-form-item label="门幅" prop="width">
          <el-input v-model="form.width" placeholder="如：150cm" />
        </el-form-item>
        <el-form-item label="克重" prop="weight">
          <el-input v-model="form.weight" placeholder="如：200g/㎡" />
        </el-form-item>
        <el-form-item label="颜色" prop="color">
          <el-input v-model="form.color" placeholder="请输入颜色" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-select v-model="form.unit" placeholder="请选择单位">
            <el-option label="米" value="米" />
            <el-option label="千克" value="千克" />
          </el-select>
        </el-form-item>
        <el-form-item label="成本价" prop="costPrice">
          <el-input-number v-model="form.costPrice" :precision="2" :min="0" />
        </el-form-item>
        <el-form-item label="销售价" prop="salePrice">
          <el-input-number v-model="form.salePrice" :precision="2" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getFabricList, createFabric, updateFabric, deleteFabric } from '@/api/fabric'

const queryParams = reactive({
  pageNum: 1,
  pageSize: 20,
  fabricCode: '',
  fabricName: ''
})

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const dialogVisible = ref(false)
const dialogTitle = ref('新增面料')
const formRef = ref<FormInstance>()
const form = reactive({
  fabricId: null as number | null,
  fabricCode: '',
  fabricName: '',
  fabricType: '',
  composition: '',
  width: '',
  weight: '',
  color: '',
  unit: '米',
  costPrice: 0,
  salePrice: 0,
  status: 1,
  remark: ''
})

const rules = {
  fabricCode: [{ required: true, message: '请输入面料编码', trigger: 'blur' }],
  fabricName: [{ required: true, message: '请输入面料名称', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getFabricList(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.fabricCode = ''
  queryParams.fabricName = ''
  handleQuery()
}

const resetForm = () => {
  Object.assign(form, {
    fabricId: null,
    fabricCode: '',
    fabricName: '',
    fabricType: '',
    composition: '',
    width: '',
    weight: '',
    color: '',
    unit: '米',
    costPrice: 0,
    salePrice: 0,
    status: 1,
    remark: ''
  })
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增面料'
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  resetForm()
  dialogTitle.value = '编辑面料'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确认删除该面料?', '警告', {
    type: 'warning'
  }).then(() => {
    deleteFabric(row.fabricId).then(() => {
      ElMessage.success('删除成功')
      getList()
    })
  })
}

const handleSubmit = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      const api = form.fabricId ? updateFabric(form.fabricId, form) : createFabric(form)
      api.then(() => {
        ElMessage.success('保存成功')
        dialogVisible.value = false
        getList()
      })
    }
  })
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
</style>
