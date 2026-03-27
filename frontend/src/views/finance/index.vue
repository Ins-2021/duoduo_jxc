<template>
  <div class="finance-container">
    <el-card class="box-card">
      <div class="filter-container">
        <el-input v-model="listQuery.accountName" placeholder="账户名称" style="width: 200px;" class="filter-item" @keyup.enter="handleFilter" />
        <el-button class="filter-item" type="primary" icon="Search" @click="handleFilter">
          搜索
        </el-button>
        <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="Plus" v-perm="'finance:account:add'" @click="handleCreate">
          添加账户
        </el-button>
      </div>

      <el-table
        v-loading="listLoading"
        :data="list"
        border
        fit
        highlight-current-row
        style="width: 100%; margin-top: 20px;"
      >
        <el-table-column label="账户ID" prop="accountId" align="center" width="100">
        </el-table-column>
        <el-table-column label="账户名称" prop="accountName" min-width="150px">
        </el-table-column>
        <el-table-column label="余额" prop="balance" width="150px" align="center">
        </el-table-column>
        <el-table-column label="状态" class-name="status-col" width="100">
          <template #default="{row}">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ financeAccountStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-show="total>0"
        :total="total"
        v-model:current-page="listQuery.pageNum"
        v-model:page-size="listQuery.pageSize"
        @current-change="getList"
        style="margin-top: 20px; text-align: right;"
      />
    </el-card>

    <el-dialog title="新增账户" v-model="dialogFormVisible" width="400px">
      <el-form ref="dataForm" :model="temp" label-position="left" label-width="100px" style="width: 100%;">
        <el-form-item label="账户名称" prop="accountName" :rules="[{ required: true, message: '必填', trigger: 'blur' }]">
          <el-input v-model="temp.accountName" />
        </el-form-item>
        <el-form-item label="初始余额" prop="balance">
          <el-input-number v-model="temp.balance" :precision="2" :step="100" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button type="primary" @click="createData">确认</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getAccountList, addAccount } from '@/api/finance'
import { getDict } from '@/api/dict'
import { ElMessage } from 'element-plus'

const list = ref([])
const total = ref(0)
const listLoading = ref(true)
const listQuery = reactive({
  pageNum: 1,
  pageSize: 10,
  accountName: undefined
})

const dialogFormVisible = ref(false)
const dataForm = ref(null)
const financeAccountStatusMap = ref<Record<string, string>>({})

const financeAccountStatusLabel = (value: any) => {
  const v = value == null ? '' : String(value)
  return financeAccountStatusMap.value[v] ?? v
}

const temp = reactive({
  accountName: '',
  balance: 0,
  status: 1
})

const getList = async () => {
  listLoading.value = true
  try {
    const res = await getAccountList(listQuery)
    list.value = res.data.list
    total.value = res.data.total
  } finally {
    listLoading.value = false
  }
}

const handleFilter = () => {
  listQuery.pageNum = 1
  getList()
}

const handleCreate = () => {
  temp.accountName = ''
  temp.balance = 0
  dialogFormVisible.value = true
}

const createData = () => {
  (dataForm.value as any).validate(async (valid: boolean) => {
    if (valid) {
      await addAccount(temp)
      dialogFormVisible.value = false
      ElMessage.success('创建成功')
      getList()
    }
  })
}

onMounted(() => {
  getDict('finance_account_status')
    .then((res: any) => {
      const map: Record<string, string> = {}
      ;(res.data || []).forEach((i: any) => {
        map[String(i.value)] = i.label
      })
      financeAccountStatusMap.value = map
    })
    .catch(() => {
      financeAccountStatusMap.value = {}
    })
    .finally(() => {
      getList()
    })
})
</script>

<style scoped>
.filter-container {
  padding-bottom: 10px;
}
</style>
