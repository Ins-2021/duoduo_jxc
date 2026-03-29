<template>
  <div class="barcode-page">
    <el-card>
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="条码内容">
          <el-input v-model="queryForm.keyword" placeholder="条码内容" clearable />
        </el-form-item>
        <el-form-item label="条码类型">
          <el-select v-model="queryForm.barcodeType" placeholder="全部" clearable>
            <el-option label="SKU码" value="SKU" />
            <el-option label="箱码" value="BOX" />
            <el-option label="批次码" value="BATCH" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="table-operations">
        <el-button type="primary" @click="handleGenerate">生成条码</el-button>
        <el-button type="warning" @click="handleScan">扫码解析</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" border>
        <el-table-column prop="barcodeContent" label="条码内容" width="250" />
        <el-table-column prop="barcodeType" label="条码类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.barcodeType === 'SKU'" type="primary">SKU码</el-tag>
            <el-tag v-else-if="row.barcodeType === 'BOX'" type="warning">箱码</el-tag>
            <el-tag v-else-if="row.barcodeType === 'BATCH'" type="success">批次码</el-tag>
            <el-tag v-else type="info">{{ row.barcodeType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="refType" label="关联类型" width="100" />
        <el-table-column prop="refId" label="关联ID" width="100" />
        <el-table-column prop="printed" label="打印状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.printed === 1" type="success">已打印</el-tag>
            <el-tag v-else type="info">未打印</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="生成时间" width="180" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handlePrint(row)">打印</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryForm.pageNum"
        v-model:page-size="queryForm.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </el-card>

    <!-- 生成条码对话框 -->
    <el-dialog v-model="generateVisible" title="生成条码" width="500px">
      <el-form :model="generateForm" label-width="100px">
        <el-form-item label="条码类型">
          <el-select v-model="generateForm.barcodeType">
            <el-option label="SKU码" value="SKU" />
            <el-option label="箱码" value="BOX" />
            <el-option label="批次码" value="BATCH" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联ID">
          <el-input-number v-model="generateForm.refId" :min="1" />
        </el-form-item>
        <el-form-item label="生成数量">
          <el-input-number v-model="generateForm.count" :min="1" :max="100" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateVisible = false">取消</el-button>
        <el-button type="primary" @click="submitGenerate">生成</el-button>
      </template>
    </el-dialog>

    <!-- 扫码解析对话框 -->
    <el-dialog v-model="scanVisible" title="扫码解析" width="500px">
      <el-form label-width="80px">
        <el-form-item label="条码内容">
          <el-input v-model="scanContent" placeholder="输入或扫描条码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitScan">解析</el-button>
        </el-form-item>
      </el-form>
      <div v-if="scanResult" style="margin-top: 16px;">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="条码内容">{{ scanResult.barcodeContent }}</el-descriptions-item>
          <el-descriptions-item label="条码类型">{{ scanResult.barcodeType }}</el-descriptions-item>
          <el-descriptions-item label="关联类型">{{ scanResult.refType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="关联ID">{{ scanResult.refId || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import type { BarcodeDTO, BarcodeGenerateRequest } from '@/api/barcode'
import { generateBarcode, parseBarcode, printBarcode } from '@/api/barcode'

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  barcodeType: ''
})

const generateForm = reactive<BarcodeGenerateRequest>({
  barcodeType: 'SKU',
  refType: 'SKU',
  refId: 0,
  count: 1
})

const tableData = ref<BarcodeDTO[]>([])
const total = ref(0)
const generateVisible = ref(false)
const scanVisible = ref(false)
const scanContent = ref('')
const scanResult = ref<BarcodeDTO | null>(null)

const handleQuery = async () => {
  try {
    const res = await request({
      url: '/inventory/barcode/rule/page',
      method: 'get',
      params: { pageNum: 1, pageSize: 1000 }
    })
    // TODO: 条码列表分页查询接口待后端补充，暂用空数据
    tableData.value = []
    total.value = 0
  } catch {
    ElMessage.error('查询失败')
  }
}

const handleReset = () => {
  queryForm.keyword = ''
  queryForm.barcodeType = ''
  queryForm.pageNum = 1
  handleQuery()
}

const handleGenerate = () => {
  Object.assign(generateForm, {
    barcodeType: 'SKU', refType: 'SKU', refId: 0, count: 1
  })
  generateVisible.value = true
}

const submitGenerate = async () => {
  try {
    const res = await generateBarcode(generateForm)
    ElMessage.success(`成功生成 ${res.data.length} 个条码`)
    generateVisible.value = false
    // 刷新列表
    handleQuery()
  } catch {
    ElMessage.error('生成失败')
  }
}

const handleScan = () => {
  scanContent.value = ''
  scanResult.value = null
  scanVisible.value = true
}

const submitScan = async () => {
  if (!scanContent.value) {
    ElMessage.warning('请输入条码内容')
    return
  }
  try {
    const res = await parseBarcode(scanContent.value)
    scanResult.value = res.data
  } catch {
    ElMessage.error('解析失败')
  }
}

const handlePrint = async (row: BarcodeDTO) => {
  try {
    await printBarcode(row.barcodeId!)
    ElMessage.success('打印标记成功')
    handleQuery()
  } catch {
    ElMessage.error('打印失败')
  }
}

const handleDelete = async (row: BarcodeDTO) => {
  try {
    await ElMessageBox.confirm('确定要删除该条码吗？', '提示', { type: 'warning' })
    await request({ url: `/inventory/barcode/${row.barcodeId}`, method: 'delete' })
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.table-operations {
  margin-bottom: 16px;
}
</style>
