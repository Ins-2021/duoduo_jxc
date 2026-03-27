<template>
  <div class="matrix-wrap">
    <div class="matrix-toolbar">
      <div class="batch-edit-area">
        <span class="toolbar-label">批量修改:</span>
        <el-select v-model="batchEditForm.field" size="small" style="width: 120px; margin-right: 10px;" placeholder="选择字段">
          <el-option label="批发价" value="wholesalePrice" />
          <el-option label="零售价格" value="retailPrice" />
        </el-select>
        <el-input-number v-model="batchEditForm.value" :min="0" :precision="2" size="small" controls-position="right" style="width: 120px; margin-right: 10px;" placeholder="输入价格" />
        <el-button size="small" type="primary" :disabled="!selectedRows.length" @click="applyBatchPrice">
          应用到选中行
        </el-button>
      </div>
      <div class="summary-area">
        <span class="total-qty">合计数量：{{ totalQty }}</span>
      </div>
    </div>

    <el-table 
      :data="rowList" 
      border 
      size="small" 
      height="480"
      @selection-change="handleSelectionChange"
      @select="handleSelect"
    >
      <el-table-column type="selection" width="45" align="center" />
      
      <el-table-column label="商品信息" min-width="140" prop="name">
        <template #default="{ row }">
          <span>{{ row.name }}</span>
        </template>
      </el-table-column>
      
      <el-table-column label="颜色" width="100" prop="attributes">
        <template #default="{ row }">
          <span>{{ row.color }}</span>
        </template>
      </el-table-column>

      <el-table-column v-for="size in sizeList" :key="size" :label="size" width="90" align="center">
        <template #default="{ row }">
          <el-input-number
            v-if="getSkuId(row, size) !== null"
            :model-value="getQty(getSkuId(row, size) as number)"
            :min="0"
            :max="99999"
            controls-position="right"
            size="small"
            style="width: 76px"
            @update:model-value="(v: number) => setQty(getSkuId(row, size) as number, v)"
          />
          <span v-else>—</span>
        </template>
      </el-table-column>

      <template v-for="col in extColumns" :key="col.prop">
        <el-table-column
          v-if="col.visible"
          :prop="col.prop"
          :label="col.label"
          :min-width="col.minWidth"
          :width="col.width"
          align="center"
        >
          <template #default="{ row }">
            <template v-if="col.prop === 'wholesalePrice' || col.prop === 'retailPrice'">
              <el-input-number 
                v-model="row.firstProduct[col.prop]" 
                :min="0" 
                :precision="2" 
                controls-position="right" 
                size="small" 
                style="width: 100%" 
                @change="(val: number) => onPriceChange(row, col.prop, val)"
              />
            </template>
            <span v-else>{{ row.firstProduct[col.prop] }}</span>
          </template>
        </el-table-column>
      </template>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch, reactive } from 'vue'
import { ElMessage } from 'element-plus'

type ProductItem = {
  id: number
  name: string
  attributes?: string
  [key: string]: any
}

type ColumnConfig = {
  prop: string
  label: string
  visible: boolean
  minWidth?: number
  width?: number
}

const props = defineProps<{
  products: ProductItem[]
  modelValue: Record<number, number>
  columns?: ColumnConfig[]
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', v: Record<number, number>): void
  (e: 'update:products', v: ProductItem[]): void
}>()

const local = ref<Record<number, number>>({})
const selectedRows = ref<any[]>([])

watch(
  () => props.modelValue,
  v => {
    local.value = { ...(v || {}) }
  },
  { immediate: true, deep: true }
)

const extColumns = computed(() => {
  if (props.columns && props.columns.length > 0) {
    return props.columns.filter(c => 
      c.prop !== 'selection' && 
      c.prop !== 'selectedQty' && 
      c.prop !== 'image' && 
      c.prop !== 'name' && 
      c.prop !== 'attributes'
    )
  }
  return []
})

const parseAttributes = (attributes?: string) => {
  const [color, size] = String(attributes || '').split('|')
  return { color: (color || '').trim(), size: (size || '').trim() }
}

const sizeList = computed(() => {
  const set = new Set<string>()
  props.products.forEach(p => {
    const { size } = parseAttributes(p.attributes)
    if (size) set.add(size)
  })
  return Array.from(set)
})

const rowList = computed(() => {
  const map = new Map<string, { key: string; name: string; color: string; firstProduct: ProductItem }>()
  props.products.forEach(p => {
    const { color } = parseAttributes(p.attributes)
    const key = `${p.name}||${color}`
    if (!map.has(key)) {
      map.set(key, { key, name: p.name, color, firstProduct: { ...p } })
    }
  })
  return Array.from(map.values())
})

const getSkuId = (row: { name: string; color: string }, size: string) => {
  const hit = props.products.find(p => {
    const parsed = parseAttributes(p.attributes)
    return p.name === row.name && parsed.color === row.color && parsed.size === size
  })
  return hit ? hit.id : null
}

const getQty = (skuId: number) => {
  return Number(local.value[skuId] || 0)
}

const setQty = (skuId: number, qty: number) => {
  const next = { ...local.value }
  if (!qty) {
    delete next[skuId]
  } else {
    next[skuId] = qty
  }
  local.value = next
  emit('update:modelValue', next)
}

const totalQty = computed(() => {
  return Object.values(local.value).reduce((sum, v) => sum + (Number(v) || 0), 0)
})

const handleSelectionChange = (val: any[]) => {
  selectedRows.value = val
}

const handleSelect = (selection: any[], row: any) => {
  const isSelected = selection.includes(row)
  if (isSelected) {
    // 自动为选中的行设置数量 1（如果当前全部尺码都是 0）
    const sizes = sizeList.value
    let hasQty = false
    for (const size of sizes) {
      const skuId = getSkuId(row, size)
      if (skuId && getQty(skuId) > 0) {
        hasQty = true
        break
      }
    }
    if (!hasQty && sizes.length > 0) {
      for (const size of sizes) {
        const skuId = getSkuId(row, size)
        if (skuId) {
          setQty(skuId, 1)
          break // 只给第一个有效尺码加1
        }
      }
    }
  }
}

const onPriceChange = (row: any, field: string, val: number) => {
  const updatedProducts = [...props.products]
  updatedProducts.forEach(p => {
    const parsed = parseAttributes(p.attributes)
    if (p.name === row.name && parsed.color === row.color) {
      p[field] = val
    }
  })
  emit('update:products', updatedProducts)
}

// 批量修改价格逻辑
const batchEditForm = reactive({
  field: 'wholesalePrice',
  value: 0
})

const applyBatchPrice = () => {
  if (!selectedRows.value.length) return
  const updatedProducts = [...props.products]
  let count = 0
  
  selectedRows.value.forEach(row => {
    updatedProducts.forEach(p => {
      const parsed = parseAttributes(p.attributes)
      if (p.name === row.name && parsed.color === row.color) {
        p[batchEditForm.field] = batchEditForm.value
        count++
      }
    })
  })
  
  emit('update:products', updatedProducts)
  ElMessage.success(`已批量修改 ${selectedRows.value.length} 个款号/颜色的价格`)
}
</script>

<style scoped>
.matrix-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  background: #f8f9fa;
  padding: 8px 12px;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}
.batch-edit-area {
  display: flex;
  align-items: center;
}
.toolbar-label {
  font-size: 13px;
  color: #606266;
  margin-right: 10px;
  font-weight: bold;
}
.summary-area .total-qty {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
}
.matrix-wrap :deep(.el-table__header-wrapper th) {
  background: #f5f7fa;
  color: #606266;
  font-weight: 600;
}
</style>
