<template>
  <el-card shadow="never">
    <!-- 工具栏 -->
    <div class="toolbar" style="display: flex; justify-content: space-between; margin-bottom: 20px;">
      <div class="left-actions">
        <el-button type="primary" v-perm="'production:quality:add'" @click="handleAdd">新增质检</el-button>
      </div>
      <div class="right-search" style="display: flex; gap: 10px;">
        <el-select v-model="queryParams.type" placeholder="质检类型" clearable style="width: 150px">
          <el-option label="首件检验(FAI)" value="FAI"/>
          <el-option label="过程检验(IPQC)" value="IPQC"/>
          <el-option label="最终检验(FQC)" value="FQC"/>
        </el-select>
        <el-select v-model="queryParams.result" placeholder="检验结果" clearable style="width: 120px">
          <el-option label="合格" value="pass"/>
          <el-option label="不合格" value="fail"/>
          <el-option label="返工" value="rework"/>
        </el-select>
        <el-input v-model="queryParams.keyword" placeholder="生产单号/扎包号" clearable style="width: 200px" @keyup.enter="handleQuery"/>
        <el-button type="primary" @click="handleQuery">查询</el-button>
      </div>
    </div>

    <!-- 质量统计 -->
    <el-row :gutter="16" style="margin-bottom: 16px;">
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="今日检验数" :value="statistics.todayCount"/>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="合格率" :value="statistics.passRate">
            <template #suffix>%</template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="返工率" :value="statistics.reworkRate">
            <template #suffix>%</template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="报废率" :value="statistics.scrapRate">
            <template #suffix>%</template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading" border>
      <el-table-column prop="qcNo" label="质检单号" width="160"/>
      <el-table-column prop="type" label="质检类型" width="120">
        <template #default="{row}">
          <el-tag size="small">{{ getQCTypeName(row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="orderNo" label="生产单号" width="160"/>
      <el-table-column prop="bundleNo" label="扎包号" width="140"/>
      <el-table-column prop="processName" label="工序" width="100"/>
      <el-table-column prop="inspectQuantity" label="检验数量" width="100"/>
      <el-table-column prop="passQuantity" label="合格数量" width="100"/>
      <el-table-column prop="failQuantity" label="不合格数量" width="120">
        <template #default="{row}">
          <span v-if="row.failQuantity > 0" style="color: #f56c6c; font-weight: bold;">{{ row.failQuantity }}</span>
          <span v-else>0</span>
        </template>
      </el-table-column>
      <el-table-column prop="result" label="检验结果" width="100">
        <template #default="{row}">
          <el-tag :type="getResultType(row.result)" size="small">{{ getResultName(row.result) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="inspectorName" label="检验员" width="100"/>
      <el-table-column prop="createdAt" label="检验时间" width="160"/>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{row}">
          <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
          <el-button link type="primary" v-perm="'production:rework:add'" @click="handleRework(row)" v-if="row.result === 'fail' || row.result === 'rework'">返工</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div style="margin-top: 20px; display: flex; justify-content: flex-end;">
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

defineOptions({ name: 'ProductionQualityList' })

const router = useRouter()
const loading = ref(false)
const total = ref(0)
const tableData = ref<any[]>([])

const queryParams = reactive({
  page: 1,
  size: 10,
  type: '',
  result: '',
  keyword: ''
})

const statistics = reactive({
  todayCount: 0,
  passRate: 0,
  reworkRate: 0,
  scrapRate: 0
})

const getQCTypeName = (type: string) => {
  const map: Record<string, string> = {
    FAI: '首件检验(FAI)',
    IPQC: '过程检验(IPQC)',
    FQC: '最终检验(FQC)'
  }
  return map[type] || type
}

const getResultType = (result: string) => {
  const map: Record<string, string> = {
    pass: 'success',
    fail: 'danger',
    rework: 'warning'
  }
  return map[result] || 'info'
}

const getResultName = (result: string) => {
  const map: Record<string, string> = {
    pass: '合格',
    fail: '不合格',
    rework: '返工'
  }
  return map[result] || result
}

const handleQuery = () => {
  loading.value = true
  setTimeout(() => {
    statistics.todayCount = 45
    statistics.passRate = 95.5
    statistics.reworkRate = 3.2
    statistics.scrapRate = 1.3

    tableData.value = [
      { id: 1, qcNo: 'QC20260329001', type: 'FAI', orderNo: 'PO20260329001', bundleNo: 'B260329001', processName: '裁剪', inspectQuantity: 1, passQuantity: 1, failQuantity: 0, result: 'pass', inspectorName: '张三', createdAt: '2026-03-29 09:30:00' },
      { id: 2, qcNo: 'QC20260329002', type: 'IPQC', orderNo: 'PO20260328001', bundleNo: 'B260328012', processName: '缝纫', inspectQuantity: 50, passQuantity: 48, failQuantity: 2, result: 'rework', inspectorName: '李四', createdAt: '2026-03-29 10:15:00' }
    ]
    total.value = 2
    loading.value = false
  }, 500)
}

const handleAdd = () => {
  ElMessage.info('新增质检功能开发中')
}

const handleDetail = (row: any) => {
  ElMessage.info(`查看质检单 ${row.qcNo} 详情`)
}

const handleRework = (row: any) => {
  router.push(`/production/rework?qcNo=${row.qcNo}`)
}

onMounted(() => {
  handleQuery()
})
</script>
