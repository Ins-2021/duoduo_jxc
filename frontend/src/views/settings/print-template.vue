<template>
  <div class="print-template-container">
    <el-card class="pt-card" shadow="never">
      <div class="pt-toolbar">
        <div class="pt-left">
          <el-button type="primary" size="small" v-perm="'settings:print-template:edit'" @click="openCreate">新增</el-button>
          <el-checkbox v-model="settings.printNoDialog" v-perm="'settings:print-template:edit'" @change="saveSettings">
            打印不显示弹窗
          </el-checkbox>
        </div>
        <div class="pt-right">
          <el-checkbox v-model="query.showDisabled" @change="loadData">显示停用</el-checkbox>
          <el-input v-model="query.templateName" size="small" placeholder="模板名称" style="width: 180px" clearable />
          <el-select v-model="query.bizType" size="small" placeholder="全部类型" style="width: 180px" clearable>
            <el-option label="全部类型" value="" />
            <el-option v-for="t in bizTypeOptions" :key="t" :label="t" :value="t" />
          </el-select>
          <el-button size="small" @click="loadData">搜索</el-button>
          <el-button size="small" @click="openSettings">
            <el-icon><Setting /></el-icon>
          </el-button>
        </div>
      </div>

      <el-table :data="tableData" border style="width: 100%" v-loading="loading" :cell-style="cellStyle">
        <el-table-column prop="templateName" label="模板名称" min-width="150">
          <template #default="scope">
            <el-button link type="primary" @click="goDesigner(scope.row)">
              {{ scope.row.templateName }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="bizType" label="业务类型" align="center" min-width="110" />
        <el-table-column prop="paperType" label="纸张" align="center" min-width="110" />
        <el-table-column prop="paperWidthMm" label="纸张宽mm" align="center" width="100" />
        <el-table-column prop="paperHeightMm" label="纸张高mm" align="center" width="100" />
        <el-table-column label="默认" align="center" width="80">
          <template #default="scope">
            <span class="blue-text">{{ scope.row.isDefault === 1 ? '是' : '否' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="停用" align="center" width="80">
          <template #default="scope">
            <span>{{ scope.row.enabled === 1 ? '否' : '是' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="相关操作" align="center" width="280">
          <template #default="scope">
            <el-button type="primary" size="small" v-perm="'settings:print-template:edit'" @click="goDesigner(scope.row)">设计</el-button>
            <el-button size="small" v-perm="'settings:print-template:view'" @click="openTestPrint(scope.row)">测试打印</el-button>
            <el-button size="small" v-perm="'settings:print-template:edit'" @click="copyDesign(scope.row)">复制设计</el-button>
            <el-button size="small" :disabled="scope.row.isDefault === 1" v-perm="'settings:print-template:edit'" @click="setDefault(scope.row)">设为默认</el-button>
            <el-button size="small" v-perm="'settings:print-template:edit'" @click="toggleEnabled(scope.row)">
              {{ scope.row.enabled === 1 ? '停用' : '启用' }}
            </el-button>
            <el-button size="small" v-perm="'settings:print-template:edit'" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pt-pagination">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :page-sizes="[10, 30, 50, 100]"
          layout="prev, pager, next, jumper, total, sizes"
          :total="total"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <el-dialog title="新增" v-model="createVisible" width="520px" class="pt-dialog">
      <el-form :model="createForm" label-width="90px">
        <el-form-item label="模板名称">
          <el-input v-model="createForm.templateName" size="small" />
        </el-form-item>
        <el-form-item label="业务类型">
          <el-select v-model="createForm.bizType" size="small" style="width: 100%">
            <el-option v-for="t in bizTypeOptions" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="纸张">
          <el-select v-model="createForm.paperType" size="small" style="width: 100%" @change="syncPaperSize">
            <el-option v-for="p in paperOptions" :key="p.paperType" :label="p.paperType" :value="p.paperType" />
          </el-select>
        </el-form-item>
        <el-form-item label="宽(mm)">
          <el-input-number v-model="createForm.paperWidthMm" size="small" :min="1" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="高(mm)">
          <el-input-number v-model="createForm.paperHeightMm" size="small" :min="1" controls-position="right" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="pt-dialog-footer">
          <el-button type="primary" size="small" :loading="creating" @click="create">保存并设计</el-button>
          <el-button size="small" @click="createVisible = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="设置" v-model="settingsVisible" width="420px">
      <el-form :model="settings" label-width="140px">
        <el-form-item label="打印不显示弹窗">
          <el-switch v-model="settings.printNoDialog" @change="saveSettings" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="pt-dialog-footer">
          <el-button size="small" @click="settingsVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="测试打印" v-model="testVisible" width="720px">
      <el-form label-width="90px">
        <el-form-item label="模板">
          <el-input :model-value="testRow?.templateName" disabled />
        </el-form-item>
        <el-form-item label="数据(JSON)">
          <el-input v-model="testDataJson" type="textarea" :rows="12" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="pt-dialog-footer">
          <el-button type="primary" size="small" :loading="testing" @click="doTestPrint">预览并打印</el-button>
          <el-button size="small" @click="testVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Setting } from '@element-plus/icons-vue'
import {
  pagePrintTemplate,
  deletePrintTemplate,
  copyPrintTemplate,
  setDefaultPrintTemplate,
  setPrintTemplateEnabled,
  savePrintTemplate,
  getPrintTemplate,
  getPrintTemplateSettings,
  savePrintTemplateSettings
} from '@/api/print-template'
import { getSystemSettings } from '@/api/settings'
import { openPrintWindow, renderFabricDesignToDataUrl } from '@/utils/print-template-render'

const router = useRouter()

const loading = ref(false)
const total = ref(0)
const tableData = ref<any[]>([])

const bizTypeOptions = [
  '标签',
  '销售',
  '销售预定',
  '销售退货',
  '零售',
  '零售退货',
  '进货预定',
  '进货',
  '进货退货',
  '盘点',
  '调拨',
  '资金调整',
  '付款单',
  '收款单',
  '其他入库',
  '其他出库',
  '积分兑换',
  '财务对账'
]

const paperOptions = [
  { paperType: 'A4', w: 210, h: 297 },
  { paperType: '二等分', w: 210, h: 140 },
  { paperType: '小票（58mm）', w: 58, h: 160 },
  { paperType: '小票（80mm）', w: 80, h: 160 },
  { paperType: '30 * 20mm', w: 30, h: 20 },
  { paperType: '40 * 70mm', w: 40, h: 70 },
  { paperType: '40 * 60mm', w: 40, h: 60 },
  { paperType: '80 * 50mm', w: 80, h: 50 },
  { paperType: '60 * 40mm', w: 60, h: 40 },
  { paperType: '40 * 30mm', w: 40, h: 30 }
]

const query = reactive({
  pageNum: 1,
  pageSize: 30,
  templateName: '',
  bizType: '',
  showDisabled: false
})

const settings = reactive({
  printNoDialog: false
})

const settingsVisible = ref(false)
const openSettings = () => {
  settingsVisible.value = true
}

const loadSettings = async () => {
  try {
    const res = await getPrintTemplateSettings()
    settings.printNoDialog = !!res?.data?.printNoDialog
  } catch {}
}

const saveSettings = async () => {
  try {
    await savePrintTemplateSettings({ printNoDialog: settings.printNoDialog })
  } catch {}
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await pagePrintTemplate({
      pageNum: query.pageNum,
      pageSize: query.pageSize,
      templateName: query.templateName || undefined,
      bizType: query.bizType || undefined,
      showDisabled: query.showDisabled
    } as any)
    tableData.value = res?.data?.list || []
    total.value = res?.data?.total || 0
  } catch {
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const cellStyle = ({ column }: any) => {
  const prop = column?.property
  if (prop === 'templateName') {
    return { padding: '6px 8px' }
  }
  return {}
}

const goDesigner = (row: any) => {
  router.push(`/settings/print-template/designer/${row.templateId}`)
}

const testVisible = ref(false)
const testing = ref(false)
const testRow = ref<any>(null)
const testDataJson = ref(
  JSON.stringify(
    {
      billNo: 'XS202603240001',
      billDate: '2026-03-24',
      storeName: '衣多多',
      customerName: '默认客户',
      operator: '管理员',
      remark: '测试打印数据',
      totalAmount: 1200,
      items: [{ name: '商品A', qty: 2, price: 100, amount: 200 }]
    },
    null,
    2
  )
)

const openTestPrint = (row: any) => {
  testRow.value = row
  testVisible.value = true
}

const doTestPrint = async () => {
  if (!testRow.value) return
  testing.value = true
  try {
    const res = await getPrintTemplate(testRow.value.templateId)
    const designJson = res?.data?.designJson
    if (!designJson) {
      ElMessage.error('模板未设计')
      return
    }
    let data: any = {}
    try {
      data = JSON.parse(testDataJson.value || '{}')
    } catch {
      ElMessage.error('JSON 格式错误')
      return
    }
    const rendered = await renderFabricDesignToDataUrl(designJson, data, { dpi: 200 })
    openPrintWindow(rendered.dataUrl, rendered.wMm, rendered.hMm)
  } catch {} finally {
    testing.value = false
  }
}

const copyDesign = async (row: any) => {
  try {
    await copyPrintTemplate(row.templateId)
    ElMessage.success('复制成功')
    loadData()
  } catch {}
}

const setDefault = async (row: any) => {
  try {
    await setDefaultPrintTemplate(row.templateId)
    ElMessage.success('设置成功')
    loadData()
  } catch {}
}

const toggleEnabled = async (row: any) => {
  try {
    const enabled = row.enabled === 1 ? 0 : 1
    await setPrintTemplateEnabled(row.templateId, enabled)
    ElMessage.success('操作成功')
    loadData()
  } catch {}
}

const remove = async (row: any) => {
  ElMessageBox.confirm('确认删除该模板吗？', '提示', { type: 'warning' }).then(async () => {
    try {
      await deletePrintTemplate(row.templateId)
      ElMessage.success('删除成功')
      loadData()
    } catch {}
  })
}

const createVisible = ref(false)
const creating = ref(false)
const defaultPaperType = ref('A4')
const createForm = reactive({
  templateName: '',
  bizType: '标签',
  paperType: 'A4',
  paperWidthMm: 210,
  paperHeightMm: 297
})

const syncPaperSize = () => {
  const p = paperOptions.find(x => x.paperType === createForm.paperType)
  if (!p) return
  createForm.paperWidthMm = p.w
  createForm.paperHeightMm = p.h
}

const openCreate = () => {
  createForm.templateName = ''
  createForm.bizType = '标签'
  createForm.paperType = defaultPaperType.value
  syncPaperSize()
  createVisible.value = true
}

const create = async () => {
  creating.value = true
  try {
    const res = await savePrintTemplate({
      templateName: createForm.templateName,
      bizType: createForm.bizType,
      paperType: createForm.paperType,
      paperWidthMm: createForm.paperWidthMm,
      paperHeightMm: createForm.paperHeightMm,
      enabled: 1,
      isDefault: 0
    } as any)
    const id = res?.data?.templateId || res?.data
    ElMessage.success('保存成功')
    createVisible.value = false
    loadData()
    if (id) {
      router.push(`/settings/print-template/designer/${id}`)
    }
  } catch {} finally {
    creating.value = false
  }
}

onMounted(() => {
  loadSettings()
  getSystemSettings()
    .then(res => {
      const v = String(res?.data?.defaultPaper || '')
      if (v === '241-2') defaultPaperType.value = '二等分'
      else if (v === '80mm') defaultPaperType.value = '小票（80mm）'
      else if (v === '58mm') defaultPaperType.value = '小票（58mm）'
      else defaultPaperType.value = 'A4'
    })
    .catch(() => {})
  loadData()
})
</script>

<style scoped>
.print-template-container {
  height: 100%;
}

.pt-card :deep(.el-card__body) {
  padding: 10px;
}

.pt-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.pt-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pt-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.pt-pagination {
  margin-top: 10px;
  display: flex;
  justify-content: flex-start;
}

.blue-text {
  color: #409EFF;
}

.pt-dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
