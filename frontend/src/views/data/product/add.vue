<template>
  <div class="app-container">
    <!-- 顶部固定操作栏 -->
    <div class="sticky-header">
      <span class="page-title">{{ isEdit ? '编辑商品' : '新增商品' }}</span>
      <div class="sticky-actions">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </div>
    </div>

    <el-card class="box-card">
      <el-tabs v-model="activeTab" @tab-click="scrollToSection">
        <el-tab-pane label="基本信息" name="basic"></el-tab-pane>
        <el-tab-pane label="属性" name="attr"></el-tab-pane>
        <el-tab-pane label="价格&条码" name="price"></el-tab-pane>
        <el-tab-pane label="库存设置" name="stock"></el-tab-pane>
      </el-tabs>

      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="top" class="product-form">
        
        <!-- 基本信息 -->
        <div id="section-basic" class="form-section">
          <h3 class="section-title">基本信息</h3>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="商品名称" prop="spuName">
                <el-input v-model="temp.spuName" placeholder="请输入商品名称" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="商品分类" prop="categoryId">
                <el-tree-select
                  v-model="temp.categoryId"
                  :data="categoryTree"
                  :props="{ label: 'label', value: 'id' }"
                  check-strictly
                  placeholder="请选择商品分类"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="商品编号">
                <el-input v-model="temp.productCode" placeholder="请输入商品编号" @blur="validateProductCode" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="规格型号">
                <el-input v-model="temp.spec" placeholder="请输入规格型号" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="单位">
                <el-input v-model="temp.unit" placeholder="如：件、个" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="默认仓库">
                <el-select v-model="temp.defaultWarehouseId" placeholder="请选择默认仓库" style="width: 100%">
                  <el-option v-for="w in warehouseList" :key="w.warehouseId" :label="w.warehouseName" :value="w.warehouseId" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="商品品牌">
                <el-select v-model="temp.brandId" placeholder="请选择品牌" style="width: 100%" clearable>
                  <!-- 假设有个品牌列表 -->
                  <el-option label="默认品牌" :value="1" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="兑换积分">
                <el-input-number v-model="temp.exchangePoints" :min="0" style="width: 100%" controls-position="right" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="商品货位">
                <el-input v-model="temp.productLocation" placeholder="请输入商品货位" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="库存预警">
                <el-input-number v-model="temp.warningQty" :min="0" style="width: 100%" controls-position="right" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="备注信息">
                <el-input v-model="temp.remark" placeholder="请输入备注信息" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 属性 -->
        <div id="section-attr" class="form-section">
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
            <div style="display: flex; align-items: center; gap: 10px;">
              <h3 class="section-title" style="margin: 0; border-left: 4px solid #409EFF; padding-left: 10px;">属性</h3>
              <el-checkbox v-model="enableAttr" label="启用属性" @change="handleEnableAttrChange" />
              <el-tooltip content="启用后可以为商品设置多个规格属性（如颜色、尺码等）" placement="right">
                <el-icon><QuestionFilled /></el-icon>
              </el-tooltip>
            </div>
            <div v-if="enableAttr">
              <el-button type="primary" link icon="Refresh" @click="fetchAttributes">刷新属性</el-button>
              <el-button type="primary" link icon="Plus" @click="router.push('/data/attribute')">新增属性</el-button>
            </div>
          </div>
          
          <div v-if="enableAttr" class="sku-picker">
            <el-table :data="attributeList" border style="margin-bottom: 20px;">
              <el-table-column label="属性名称" width="200">
                <template #default="{ row }">
                  <el-checkbox v-model="row.checked" :label="row.name" @change="handleAttrCheck(row)" />
                </template>
              </el-table-column>
              <el-table-column label="属性选项">
                <template #default="{ row }">
                  <el-checkbox-group v-model="row.selectedOptions" :disabled="!row.checked" @change="handleOptionCheck">
                    <el-checkbox v-for="opt in row.options" :key="opt.id" :label="opt.value" :value="opt.value" />
                  </el-checkbox-group>
                </template>
              </el-table-column>
            </el-table>

            <div v-if="temp.skuList.length > 0" style="margin-bottom: 10px; display: flex; justify-content: flex-end; gap: 10px;">
              <el-input v-model="batchValues.skuCodePrefix" placeholder="条码前缀" size="small" style="width: 120px;" />
              <el-button size="small" @click="handleBatchGenerateSkuCode">批量生成条码</el-button>
              <el-input-number v-model="batchValues.purchasePrice" :precision="2" :step="0.1" :min="0" placeholder="进货价" size="small" style="width: 100px;" controls-position="right" />
              <el-input-number v-model="batchValues.wholesalePrice" :precision="2" :step="0.1" :min="0" placeholder="批发价" size="small" style="width: 100px;" controls-position="right" />
              <el-input-number v-model="batchValues.retailPrice" :precision="2" :step="0.1" :min="0" placeholder="零售价" size="small" style="width: 100px;" controls-position="right" />
              <el-button size="small" type="primary" @click="handleBatchSetPrices">批量设置</el-button>
            </div>
            <el-table v-if="temp.skuList.length > 0" :data="temp.skuList" border size="small" style="margin-bottom: 20px;">
              <el-table-column v-if="temp.attr1Name" :label="temp.attr1Name" prop="attr1" width="120" align="center" />
              <el-table-column v-if="temp.attr2Name" :label="temp.attr2Name" prop="attr2" width="120" align="center" />
              <el-table-column label="条码/编码" min-width="150">
                <template #default="{ row }">
                  <el-input v-model="row.skuCode" placeholder="条码/编码" size="small"/>
                </template>
              </el-table-column>
              <el-table-column label="进货价" width="150">
                <template #default="{ row }">
                  <el-input-number v-model="row.purchasePrice" :precision="2" :step="0.1" size="small" style="width: 100%;" controls-position="right"/>
                </template>
              </el-table-column>
              <el-table-column label="批发价" width="150">
                <template #default="{ row }">
                  <el-input-number v-model="row.wholesalePrice" :precision="2" :step="0.1" size="small" style="width: 100%;" controls-position="right"/>
                </template>
              </el-table-column>
              <el-table-column label="零售价" width="150">
                <template #default="{ row }">
                  <el-input-number v-model="row.retailPrice" :precision="2" :step="0.1" size="small" style="width: 100%;" controls-position="right"/>
                </template>
              </el-table-column>
              <el-table-column label="预警库存" width="150">
                <template #default="{ row }">
                  <el-input-number v-model="row.warningQty" :min="0" size="small" style="width: 100%;" controls-position="right"/>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <!-- 价格&条码 -->
        <div id="section-price" class="form-section">
          <h3 class="section-title">价格&条码</h3>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="进货价">
                <el-input-number v-model="temp.purchasePrice" :precision="2" :step="0.1" :min="0" style="width: 100%" controls-position="right" placeholder="请输入进货价" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="批发价">
                <el-input-number v-model="temp.wholesalePrice" :precision="2" :step="0.1" :min="0" style="width: 100%" controls-position="right" placeholder="请输入批发价" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="零售价">
                <el-input-number v-model="temp.retailPrice" :precision="2" :step="0.1" :min="0" style="width: 100%" controls-position="right" placeholder="请输入零售价" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 库存设置 -->
        <div id="section-stock" class="form-section" v-if="!isEdit">
          <h3 class="section-title">库存设置</h3>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="所属仓库">
                <el-select v-model="temp.defaultWarehouseId" placeholder="请选择仓库" style="width: 100%">
                  <el-option v-for="w in warehouseList" :key="w.warehouseId" :label="w.warehouseName" :value="w.warehouseId" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="期初库存数量">
                <el-input-number v-model="temp.initialStockQty" :min="0" style="width: 100%" controls-position="right" placeholder="期初库存数量" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="期初成本价">
                <el-input-number v-model="temp.initialCostPrice" :precision="2" :step="0.1" :min="0" style="width: 100%" controls-position="right" placeholder="期初成本价" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 图片附件 -->
        <div class="form-section">
          <h3 class="section-title">上传图片附件</h3>
          <!-- 简单占位，暂不实现完整的上传 -->
          <div class="upload-placeholder">
            <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
            <div class="el-upload__text">点击上传(最多5张)</div>
          </div>
        </div>

      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductDetail, addProduct, updateProduct, getProductCategoryTree, checkProductCode } from '@/api/product'
import { getWarehouseList } from '@/api/warehouse'
import { getAttributeList } from '@/api/attribute'
import type { ProductAttributeDTO } from '@/types'
import { ElMessage } from 'element-plus'
import { Plus, QuestionFilled } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)
const copyFromId = computed(() => route.query.copyFrom as string | undefined)
const activeTab = ref('basic')

const categoryTree = ref([])
const warehouseList = ref<any[]>([])
const attributeList = ref<ExtAttribute[]>([])

const temp = reactive({
  spuId: undefined as any,
  spuName: '',
  categoryId: undefined as any,
  productCode: '',
  spec: '',
  unit: '件',
  defaultWarehouseId: undefined as any,
  brandId: undefined as any,
  exchangePoints: 0,
  productLocation: '',
  warningQty: 0,
  remark: '',
  purchasePrice: 0,
  wholesalePrice: 0,
  retailPrice: 0,
  initialStockQty: 0,
  initialCostPrice: 0,
  initialTotalAmount: 0,
  status: 1,
  attr1Name: '颜色',
  attr2Name: '尺码',
  skuList: [] as any[]
})

interface ExtAttribute extends ProductAttributeDTO {
  checked?: boolean
  selectedOptions?: string[]
}
const enableAttr = ref(false)

const batchValues = reactive({
  skuCodePrefix: '',
  purchasePrice: undefined as number | undefined,
  wholesalePrice: undefined as number | undefined,
  retailPrice: undefined as number | undefined
})

const handleBatchGenerateSkuCode = () => {
  const prefix = batchValues.skuCodePrefix || temp.productCode || 'SKU'
  temp.skuList.forEach((sku, index) => {
    // 简单的递增生成规则：前缀 + 序号补零
    sku.skuCode = `${prefix}${String(index + 1).padStart(3, '0')}`
  })
  ElMessage.success('批量生成条码成功')
}

const handleBatchSetPrices = () => {
  let updated = false
  temp.skuList.forEach(sku => {
    if (batchValues.purchasePrice !== undefined) {
      sku.purchasePrice = batchValues.purchasePrice
      updated = true
    }
    if (batchValues.wholesalePrice !== undefined) {
      sku.wholesalePrice = batchValues.wholesalePrice
      updated = true
    }
    if (batchValues.retailPrice !== undefined) {
      sku.retailPrice = batchValues.retailPrice
      updated = true
    }
  })
  if (updated) {
    ElMessage.success('批量设置价格成功')
  } else {
    ElMessage.warning('请先输入要批量设置的价格')
  }
}

const handleEnableAttrChange = (val: any) => {
  if (!val) {
    temp.skuList = []
  } else {
    handleOptionCheck()
  }
}

const handleAttrCheck = (row: ExtAttribute) => {
  const checkedAttrs = attributeList.value.filter(a => a.checked)
  if (checkedAttrs.length > 2) {
    import('element-plus').then(({ ElMessage }) => {
      ElMessage.warning('最多只能选择2个属性')
    })
    row.checked = false
    return
  }
  if (!row.checked) {
    row.selectedOptions = []
  }
  handleOptionCheck()
}

const handleOptionCheck = () => {
  const checkedAttrs = attributeList.value.filter(a => a.checked)
  if (checkedAttrs.length === 0) {
    temp.skuList = []
    temp.attr1Name = ''
    temp.attr2Name = ''
    return
  }

  const attr1 = checkedAttrs[0]
  const attr2 = checkedAttrs[1]

  temp.attr1Name = attr1?.name || ''
  temp.attr2Name = attr2?.name || ''

  const opts1 = attr1?.selectedOptions?.length ? attr1.selectedOptions : []
  const opts2 = attr2?.selectedOptions?.length ? attr2.selectedOptions : []

  if (opts1.length === 0 && opts2.length === 0) {
    temp.skuList = []
    return
  }

  const newSkuList: any[] = []
  const createSku = (o1: string, o2: string) => ({
    skuCode: '',
    attr1: o1 === '' ? undefined : o1,
    attr2: o2 === '' ? undefined : o2,
    purchasePrice: temp.purchasePrice || 0,
    retailPrice: temp.retailPrice || 0,
    wholesalePrice: temp.wholesalePrice || 0,
    warningQty: temp.warningQty || 0,
    weight: 0,
    status: 1
  })

  if (opts1.length > 0 && opts2.length > 0) {
    opts1.forEach(o1 => {
      opts2.forEach(o2 => {
        let existing = temp.skuList.find(s => s.attr1 === o1 && s.attr2 === o2)
        newSkuList.push(existing || createSku(o1, o2))
      })
    })
  } else if (opts1.length > 0) {
    opts1.forEach(o1 => {
      let existing = temp.skuList.find(s => s.attr1 === o1 && !s.attr2)
      newSkuList.push(existing || createSku(o1, ''))
    })
  } else if (opts2.length > 0) {
    opts2.forEach(o2 => {
      let existing = temp.skuList.find(s => !s.attr1 && s.attr2 === o2)
      newSkuList.push(existing || createSku('', o2))
    })
  }

  temp.skuList = newSkuList
}

const normalizeSpecsFromSkuList = () => {
  const hasAttr = temp.skuList.some(s => s.attr1 || s.attr2)
  enableAttr.value = hasAttr

  if (hasAttr) {
    const colors = new Set<string>()
    const sizes = new Set<string>()
    temp.skuList.forEach((sku: any) => {
      if (sku?.attr1) colors.add(String(sku.attr1))
      if (sku?.attr2) sizes.add(String(sku.attr2))
    })

    if (!temp.attr1Name && colors.size > 0) {
      temp.attr1Name = '颜色'
    }
    if (!temp.attr2Name && sizes.size > 0) {
      temp.attr2Name = '尺码'
    }

    // 如果历史属性在系统中已经被删除，为了保证回显，临时把它塞进 attributeList
    if (temp.attr1Name && !attributeList.value.find(a => a.name === temp.attr1Name)) {
      attributeList.value.push({ name: temp.attr1Name, checked: false, selectedOptions: [], options: [] })
    }
    if (temp.attr2Name && !attributeList.value.find(a => a.name === temp.attr2Name)) {
      attributeList.value.push({ name: temp.attr2Name, checked: false, selectedOptions: [], options: [] })
    }

    attributeList.value.forEach((attr: ExtAttribute) => {
      if (attr.name === temp.attr1Name) {
        attr.checked = true
        attr.selectedOptions = Array.from(colors)
        // 把历史选中的值补充到 options 中展示
        Array.from(colors).forEach(c => {
          if (!attr.options?.find(o => o.value === c)) {
            attr.options = attr.options || []
            attr.options.push({ value: c } as any)
          }
        })
      } else if (attr.name === temp.attr2Name) {
        attr.checked = true
        attr.selectedOptions = Array.from(sizes)
        // 把历史选中的值补充到 options 中展示
        Array.from(sizes).forEach(s => {
          if (!attr.options?.find(o => o.value === s)) {
            attr.options = attr.options || []
            attr.options.push({ value: s } as any)
          }
        })
      } else {
        attr.checked = false
        attr.selectedOptions = []
      }
    })
  } else {
    temp.attr1Name = ''
    temp.attr2Name = ''
  }
}

const rules = {
  spuName: [{ required: true, message: '商品名称必填', trigger: 'blur' }],
  categoryId: [{ required: true, message: '商品分类必选', trigger: 'change' }]
}

const dataForm = ref(null)

const scrollToSection = (tab: any) => {
  const id = `section-${tab.paneName}`
  const el = document.getElementById(id)
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

const fetchCategoryTree = async () => {
  const res = await getProductCategoryTree()
  categoryTree.value = res.data || []
}

const fetchWarehouses = async () => {
  try {
    const res = await getWarehouseList({ pageNum: 1, pageSize: 500 })
    warehouseList.value = res.data?.list || []
  } catch (e) {
    warehouseList.value = []
  }
}

const fetchAttributes = async () => {
  try {
    const res = await getAttributeList()
    attributeList.value = (res.data || []).map(a => ({...a, checked: false, selectedOptions: []}))
    if (temp.skuList && temp.skuList.length > 0) {
      normalizeSpecsFromSkuList()
    }
  } catch (e) {
    console.error(e)
  }
}

const loadData = async () => {
  if (isEdit.value) {
    const res = await getProductDetail(Number(route.params.id))
    Object.assign(temp, res.data)
    if (!temp.skuList) temp.skuList = []
    normalizeSpecsFromSkuList()
    return
  }
  if (copyFromId.value) {
    const res = await getProductDetail(Number(copyFromId.value))
    Object.assign(temp, res.data)
    temp.spuId = undefined
    temp.skuList = (temp.skuList || []).map((sku: any) => ({
      ...sku,
      skuId: undefined,
      skuCode: ''
    }))
    normalizeSpecsFromSkuList()
  }
}

const productCodeValidating = ref(false)

const validateProductCode = async () => {
  const code = (temp.productCode || '').trim()
  if (!code) return true
  productCodeValidating.value = true
  try {
    const excludeId = isEdit.value ? temp.spuId : undefined
    const res = await checkProductCode(code, excludeId)
    if (res.data) {
      ElMessage.error('商品编号已存在，请使用其他编号')
      return false
    }
    return true
  } catch {
    return true
  } finally {
    productCodeValidating.value = false
  }
}

const handleSave = () => {
  (dataForm.value as any).validate(async (valid: boolean) => {
    if (!valid) return
    const codeValid = await validateProductCode()
    if (!codeValid) return

    // 组装提交数据
    const submitData = { ...temp }
    if (!enableAttr.value || submitData.skuList.length === 0) {
      // 如果未启用多属性，或者没有生成任何 SKU，则默认生成一个无属性的基础 SKU
      submitData.skuList = [{
        skuCode: submitData.productCode || '',
        purchasePrice: submitData.purchasePrice || 0,
        retailPrice: submitData.retailPrice || 0,
        wholesalePrice: submitData.wholesalePrice || 0,
        warningQty: submitData.warningQty || 0,
        weight: 0,
        status: 1
      }]
      submitData.attr1Name = ''
      submitData.attr2Name = ''
    }

    try {
      if (isEdit.value) {
        await updateProduct(submitData)
        ElMessage.success('更新成功')
      } else {
        await addProduct(submitData)
        ElMessage.success('创建成功')
      }
      router.back()
    } catch (e: any) {
      console.error(e)
      ElMessage.error(e.message || '添加商品失败')
    }
  })
}

const handleCancel = () => {
  router.back()
}

onMounted(() => {
  fetchCategoryTree()
  fetchWarehouses()
  fetchAttributes()
  loadData()
})
</script>

<style scoped>
.app-container {
  height: 100%;
  overflow-y: auto;
  position: relative;
}
.sticky-header {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 50px;
  padding: 0 20px;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.06);
}
.page-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}
.sticky-actions {
  display: flex;
  gap: 8px;
}
.box-card {
  margin-top: 0;
}
.form-section {
  margin-bottom: 40px;
  padding: 20px;
  background-color: #f9fafc;
  border-radius: 8px;
}
.section-title {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 16px;
  font-weight: bold;
  border-left: 4px solid #409EFF;
  padding-left: 10px;
}
.sku-cell {
  display: flex;
  flex-direction: column;
  gap: 5px;
}
.upload-placeholder {
  width: 120px;
  height: 120px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #8c939d;
}
.upload-placeholder:hover {
  border-color: #409eff;
}
.avatar-uploader-icon {
  font-size: 28px;
  margin-bottom: 10px;
}
</style>
