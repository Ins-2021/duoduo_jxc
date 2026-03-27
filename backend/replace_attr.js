const fs = require('fs');
const path = '../frontend/src/views/data/product/add.vue';
let content = fs.readFileSync(path, 'utf8');

const oldTemplate = `<h3 class="section-title">属性 (SKU设置)</h3>
          <div class="sku-picker">
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 20px;">
              <div>
                <div style="margin-bottom: 8px; display: flex; align-items: center; gap: 8px;">
                  <span>属性1:</span>
                  <el-select v-model="temp.attr1Name" filterable allow-create default-first-option placeholder="属性1名称(如颜色)" size="small" style="width: 150px;">
                    <el-option v-for="attr in attributeList" :key="attr.id" :label="attr.name" :value="attr.name" />
                  </el-select>
                </div>
                <el-select v-model="specColors" multiple filterable allow-create default-first-option style="width: 100%;" :placeholder="\`请选择或输入\${temp.attr1Name || '属性1'}值\`">
                  <el-option v-for="opt in getOptionsForAttr(temp.attr1Name)" :key="opt" :label="opt" :value="opt" />
                </el-select>
              </div>
              <div>
                <div style="margin-bottom: 8px; display: flex; align-items: center; gap: 8px;">
                  <span>属性2:</span>
                  <el-select v-model="temp.attr2Name" filterable allow-create default-first-option placeholder="属性2名称(如尺码)" size="small" style="width: 150px;">
                    <el-option v-for="attr in attributeList" :key="attr.id" :label="attr.name" :value="attr.name" />
                  </el-select>
                </div>
                <el-select v-model="specSizes" multiple filterable allow-create default-first-option style="width: 100%;" :placeholder="\`请选择或输入\${temp.attr2Name || '属性2'}值\`">
                  <el-option v-for="opt in getOptionsForAttr(temp.attr2Name)" :key="opt" :label="opt" :value="opt" />
                </el-select>
              </div>
            </div>

            <el-table v-if="specSizes.length > 0 || specColors.length > 0" :data="specColorRows" border size="small" style="margin-bottom: 20px;">
              <el-table-column :label="temp.attr1Name || '属性1'" width="120" align="center" v-if="specColors.length > 0">
                <template #default="{ row }">
                  <span>{{ row.color }}</span>
                </template>
              </el-table-column>
              <!-- 只有颜色没有尺码的情况 -->
              <template v-if="specSizes.length === 0">
                <el-table-column label="属性配置" min-width="200">
                  <template #default="{ row }">
                    <div v-if="getSku(row.color, '')" class="sku-cell">
                      <el-input v-model="getSku(row.color, '')!.skuCode" placeholder="条码/编码" size="small"/>
                    </div>
                    <el-button v-else size="small" @click="ensureSku(row.color, '')">添加</el-button>
                  </template>
                </el-table-column>
              </template>
              
              <el-table-column v-for="size in specSizes" :key="size" :label="size" min-width="150" align="center">
                <template #default="{ row }">
                  <div v-if="getSku(row.color || '', size)" class="sku-cell">
                    <el-input v-model="getSku(row.color || '', size)!.skuCode" placeholder="条码/编码" size="small"/>
                    <el-input-number v-model="getSku(row.color || '', size)!.retailPrice" :precision="2" :step="0.1" size="small" placeholder="零售价" style="width: 100%; margin-top: 5px;"/>
                  </div>
                  <el-button v-else size="small" @click="ensureSku(row.color || '', size)">添加</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>`;

const newTemplate = `<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
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

            <el-table v-if="temp.skuList.length > 0" :data="temp.skuList" border size="small" style="margin-bottom: 20px;">
              <el-table-column v-if="temp.attr1Name" :label="temp.attr1Name" prop="attr1" width="120" align="center" />
              <el-table-column v-if="temp.attr2Name" :label="temp.attr2Name" prop="attr2" width="120" align="center" />
              <el-table-column label="条码/编码" min-width="150">
                <template #default="{ row }">
                  <el-input v-model="row.skuCode" placeholder="条码/编码" size="small"/>
                </template>
              </el-table-column>
              <el-table-column label="零售价" width="150">
                <template #default="{ row }">
                  <el-input-number v-model="row.retailPrice" :precision="2" :step="0.1" size="small" style="width: 100%;" controls-position="right"/>
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
              <el-table-column label="预警库存" width="150">
                <template #default="{ row }">
                  <el-input-number v-model="row.warningQty" :min="0" size="small" style="width: 100%;" controls-position="right"/>
                </template>
              </el-table-column>
            </el-table>
          </div>`;

content = content.replace(oldTemplate, newTemplate);

const oldScript = `const specColors = ref<string[]>([])
const specSizes = ref<string[]>([])

// 获取对应属性名称的选项
const getOptionsForAttr = (attrName: string) => {
  if (!attrName) return []
  const attr = attributeList.value.find(a => a.name === attrName)
  if (!attr || !attr.options) return []
  return attr.options.map(opt => opt.value).filter(Boolean) as string[]
}

const specColorRows = computed(() => {
  if (specColors.value.length === 0) {
    return [{ color: '' }]
  }
  return specColors.value.map(color => ({ color }))
})

const getSku = (color: string, size: string) => {
  return temp.skuList.find((s: any) => String(s.attr1 || '') === String(color) && String(s.attr2 || '') === String(size))
}

const ensureSku = (color: string, size: string) => {
  const existing = getSku(color, size)
  if (existing) return existing
  const sku = {
    skuCode: '',
    attr1: color,
    attr2: size,
    warningQty: temp.warningQty || 0,
    purchasePrice: temp.purchasePrice || 0,
    retailPrice: temp.retailPrice || 0,
    wholesalePrice: temp.wholesalePrice || 0,
    weight: 0,
    status: 1
  }
  temp.skuList.push(sku)
  return sku
}

const normalizeSpecsFromSkuList = () => {
  const colors = new Set<string>()
  const sizes = new Set<string>()
  temp.skuList.forEach((sku: any) => {
    if (sku?.attr1) colors.add(String(sku.attr1))
    if (sku?.attr2) sizes.add(String(sku.attr2))
  })
  specColors.value = Array.from(colors)
  specSizes.value = Array.from(sizes)

  // 如果后端没有返回 attr1Name 和 attr2Name，并且存在颜色和尺码的值，则赋默认值
  if (!temp.attr1Name && specColors.value.length > 0) {
    temp.attr1Name = '颜色'
  }
  if (!temp.attr2Name && specSizes.value.length > 0) {
    temp.attr2Name = '尺码'
  }
}`;

const newScript = `interface ExtAttribute extends ProductAttributeDTO {
  checked?: boolean
  selectedOptions?: string[]
}
const enableAttr = ref(false)

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

    attributeList.value.forEach((attr: ExtAttribute) => {
      if (attr.name === temp.attr1Name) {
        attr.checked = true
        attr.selectedOptions = Array.from(colors)
      } else if (attr.name === temp.attr2Name) {
        attr.checked = true
        attr.selectedOptions = Array.from(sizes)
      } else {
        attr.checked = false
        attr.selectedOptions = []
      }
    })
  } else {
    temp.attr1Name = ''
    temp.attr2Name = ''
  }
}`;

content = content.replace(oldScript, newScript);

content = content.replace("import { Plus } from '@element-plus/icons-vue'", "import { Plus, QuestionFilled } from '@element-plus/icons-vue'");
content = content.replace("const attributeList = ref<ProductAttributeDTO[]>([])", "const attributeList = ref<ExtAttribute[]>([])");

const oldFetchAttributes = `const fetchAttributes = async () => {
  try {
    const res = await getAttributeList()
    attributeList.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}`;
const newFetchAttributes = `const fetchAttributes = async () => {
  try {
    const res = await getAttributeList()
    attributeList.value = (res.data || []).map(a => ({...a, checked: false, selectedOptions: []}))
    if (temp.skuList && temp.skuList.length > 0) {
      normalizeSpecsFromSkuList()
    }
  } catch (e) {
    console.error(e)
  }
}`;
content = content.replace(oldFetchAttributes, newFetchAttributes);

fs.writeFileSync(path, content, 'utf8');
