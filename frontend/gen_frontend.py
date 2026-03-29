import os

base_dir = "/Users/zxh/mycoding/duoduo_jxc/frontend/src"

pages = {
    "style": {
        "api": """import request from '@/utils/request'

export function getStyleList(params: any) {
  return request({ url: '/api/style/list', method: 'get', params })
}
export function getStyleDetail(id: number) {
  return request({ url: `/api/style/${id}`, method: 'get' })
}
export function createStyle(data: any) {
  return request({ url: '/api/style', method: 'post', data })
}
export function updateStyle(id: number, data: any) {
  return request({ url: `/api/style/${id}`, method: 'put', data })
}
export function deleteStyle(id: number) {
  return request({ url: `/api/style/${id}`, method: 'delete' })
}
""",
        "views": {
            "index.vue": """<template>
  <div class="style-container">
    <el-card shadow="never">
      <div class="toolbar" style="display: flex; justify-content: space-between; margin-bottom: 20px;">
        <div>
          <el-button type="primary">新增款式</el-button>
        </div>
        <div style="display: flex; gap: 10px;">
          <el-input v-model="queryParams.keyword" placeholder="款式名称/款号" clearable style="width: 200px" />
          <el-button type="primary" @click="handleQuery">查询</el-button>
        </div>
      </div>
      <el-table v-loading="loading" :data="tableData" border>
        <el-table-column prop="styleNo" label="款号" />
        <el-table-column prop="styleName" label="款式名称" />
        <el-table-column prop="season" label="季节" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default>
            <el-button link type="primary">编辑</el-button>
            <el-button link type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 20px; display: flex; justify-content: flex-end;">
        <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize" :total="total" layout="total, prev, pager, next" @current-change="handleQuery" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getStyleList } from '@/api/style'

defineOptions({ name: 'StyleManager' })

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryParams = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getStyleList(queryParams)
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (e) {} finally {
    loading.value = false
  }
}

onMounted(() => { handleQuery() })
</script>
"""
        }
    },
    "process": {
        "api": """import request from '@/utils/request'

export function getProcessList(params: any) { return request({ url: '/api/process/list', method: 'get', params }) }
export function getProcessDetail(id: number) { return request({ url: `/api/process/${id}`, method: 'get' }) }
export function createProcess(data: any) { return request({ url: '/api/process', method: 'post', data }) }
export function updateProcess(id: number, data: any) { return request({ url: `/api/process/${id}`, method: 'put', data }) }
export function deleteProcess(id: number) { return request({ url: `/api/process/${id}`, method: 'delete' }) }
""",
        "views": {
            "index.vue": """<template>
  <div class="process-container">
    <el-card shadow="never">
      <div class="toolbar" style="display: flex; justify-content: space-between; margin-bottom: 20px;">
        <div><el-button type="primary">新增工序</el-button></div>
        <div style="display: flex; gap: 10px;">
          <el-input v-model="queryParams.keyword" placeholder="工序名称" clearable style="width: 200px" />
          <el-button type="primary" @click="handleQuery">查询</el-button>
        </div>
      </div>
      <el-table v-loading="loading" :data="tableData" border>
        <el-table-column prop="processCode" label="工序编码" />
        <el-table-column prop="processName" label="工序名称" />
        <el-table-column prop="processType" label="类型" />
        <el-table-column prop="standardPrice" label="标准单价" />
        <el-table-column label="操作" width="150">
          <template #default>
            <el-button link type="primary">编辑</el-button>
            <el-button link type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 20px; display: flex; justify-content: flex-end;">
        <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize" :total="total" layout="total, prev, pager, next" @current-change="handleQuery" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getProcessList } from '@/api/process'

defineOptions({ name: 'ProcessManager' })

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryParams = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getProcessList(queryParams)
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (e) {} finally {
    loading.value = false
  }
}

onMounted(() => { handleQuery() })
</script>
"""
        }
    },
    "quality": {
        "api": """import request from '@/utils/request'

export function getQualityStandardList(params: any) { return request({ url: '/api/qualitystandard/list', method: 'get', params }) }
export function getQualityCheckList(params: any) { return request({ url: '/api/qualitycheck/list', method: 'get', params }) }
""",
        "views": {
            "standard.vue": """<template>
  <div class="quality-container">
    <el-card shadow="never">
      <div class="toolbar" style="display: flex; justify-content: space-between; margin-bottom: 20px;">
        <div><el-button type="primary">新增标准</el-button></div>
        <div style="display: flex; gap: 10px;">
          <el-input v-model="queryParams.keyword" placeholder="标准名称" clearable style="width: 200px" />
          <el-button type="primary" @click="handleQuery">查询</el-button>
        </div>
      </div>
      <el-table v-loading="loading" :data="tableData" border>
        <el-table-column prop="standardName" label="标准名称" />
        <el-table-column prop="standardType" label="类型" />
        <el-table-column prop="passStandard" label="合格标准" />
        <el-table-column label="操作" width="150">
          <template #default>
            <el-button link type="primary">编辑</el-button>
            <el-button link type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getQualityStandardList } from '@/api/quality'

const loading = ref(false)
const tableData = ref([])
const queryParams = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getQualityStandardList(queryParams)
    tableData.value = res.data.list || []
  } catch (e) {} finally { loading.value = false }
}
onMounted(() => handleQuery())
</script>
""",
            "check.vue": """<template>
  <div class="quality-check-container">
    <el-card shadow="never">
      <div class="toolbar" style="display: flex; justify-content: space-between; margin-bottom: 20px;">
        <div><el-button type="primary">新增质检记录</el-button></div>
        <div style="display: flex; gap: 10px;">
          <el-input v-model="queryParams.keyword" placeholder="质检单号" clearable style="width: 200px" />
          <el-button type="primary" @click="handleQuery">查询</el-button>
        </div>
      </div>
      <el-table v-loading="loading" :data="tableData" border>
        <el-table-column prop="checkNo" label="质检单号" />
        <el-table-column prop="result" label="结果" />
        <el-table-column prop="checkQuantity" label="检验数量" />
        <el-table-column prop="qualifiedQuantity" label="合格数量" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getQualityCheckList } from '@/api/quality'

const loading = ref(false)
const tableData = ref([])
const queryParams = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getQualityCheckList(queryParams)
    tableData.value = res.data.list || []
  } catch (e) {} finally { loading.value = false }
}
onMounted(() => handleQuery())
</script>
"""
        }
    },
    "quotation": {
        "api": """import request from '@/utils/request'

export function getQuotationList(params: any) { return request({ url: '/api/quotation/list', method: 'get', params }) }
export function convertQuotation(id: number) { return request({ url: `/api/quotation/${id}/convert`, method: 'post' }) }
""",
        "views": {
            "index.vue": """<template>
  <div class="quotation-container">
    <el-card shadow="never">
      <div class="toolbar" style="display: flex; justify-content: space-between; margin-bottom: 20px;">
        <div><el-button type="primary">新增报价单</el-button></div>
        <div style="display: flex; gap: 10px;">
          <el-input v-model="queryParams.keyword" placeholder="报价单号" clearable style="width: 200px" />
          <el-button type="primary" @click="handleQuery">查询</el-button>
        </div>
      </div>
      <el-table v-loading="loading" :data="tableData" border>
        <el-table-column prop="quotationNo" label="报价单号" />
        <el-table-column prop="customerName" label="客户" />
        <el-table-column prop="finalAmount" label="最终金额" />
        <el-table-column prop="quotationStatus" label="状态" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleConvert(row)">转订单</el-button>
            <el-button link type="primary">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getQuotationList, convertQuotation } from '@/api/quotation'

const loading = ref(false)
const tableData = ref([])
const queryParams = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getQuotationList(queryParams)
    tableData.value = res.data.list || []
  } catch (e) {} finally { loading.value = false }
}

const handleConvert = async (row: any) => {
  try {
    await convertQuotation(row.quotationId)
    ElMessage.success('转订单成功')
    handleQuery()
  } catch (e) {}
}

onMounted(() => handleQuery())
</script>
"""
        }
    }
}

for module, config in pages.items():
    # Write API
    with open(f"{base_dir}/api/{module}.ts", "w") as f:
        f.write(config["api"])
    
    # Write Views
    for filename, content in config["views"].items():
        with open(f"{base_dir}/views/{module}/{filename}", "w") as f:
            f.write(content)

print("Frontend files generated successfully.")
