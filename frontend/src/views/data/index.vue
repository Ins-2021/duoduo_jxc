<template>
  <div class="product-page">
    <aside class="category-panel">
      <div class="category-header">
        <span class="category-title">商品分类</span>
        <el-button link type="primary" size="small" @click="editCategory">编辑</el-button>
      </div>
      <div class="category-body">
        <el-tree
          :data="categoryTree"
          :props="{ label: 'label', children: 'children' }"
          node-key="id"
          default-expand-all
          highlight-current
          @node-click="handleCategoryClick"
        >
          <template #default="{ node, data }">
            <div class="category-node">
              <el-icon class="category-icon">
                <Folder v-if="data.children && data.children.length > 0" />
                <Document v-else />
              </el-icon>
              <span>{{ node.label }}</span>
            </div>
          </template>
        </el-tree>
      </div>
    </aside>

    <section class="list-panel">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-button type="primary" class="btn-primary" @click="handleCreate">新增商品</el-button>
          <el-dropdown>
            <el-button class="btn-default">
              批量 <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>批量操作</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button class="btn-default">导入</el-button>
        </div>
        <div class="toolbar-right">
          <el-checkbox v-model="listQuery.showDisabled" @change="handleFilter">显示停用</el-checkbox>
          <el-input v-model="listQuery.spuName" placeholder="商品名称" class="query-input" clearable @keyup.enter="handleFilter" />
          <el-input v-model="listQuery.productCode" placeholder="商品编号" class="query-input" clearable @keyup.enter="handleFilter" />
          <el-input v-model="listQuery.spec" placeholder="规格型号" class="query-input" clearable @keyup.enter="handleFilter" />
          <el-button class="btn-default" @click="handleFilter">搜索</el-button>
          <el-button class="btn-default">导出</el-button>
          <el-button link type="primary" class="expand-btn">展开筛选 <el-icon><ArrowDown /></el-icon></el-button>
          <el-button :icon="Setting" circle class="setting-btn" @click="showColumnSettings = true"></el-button>
        </div>
      </div>

      <div class="table-wrap">
        <el-table
          v-loading="listLoading"
          :data="list"
          border
          height="100%"
          class="product-table"
          :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold' }"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="42" align="center" />

          <el-table-column v-if="checkColumn('商品名称')" :label="getColumnLabel('商品名称')" prop="spuName" min-width="170" align="center" sortable>
            <template #default="{row}">
              <span class="name-link" @click="handleUpdate(row)">{{ row.spuName }}</span>
            </template>
          </el-table-column>
          <el-table-column v-if="checkColumn('图像')" :label="getColumnLabel('图像')" align="center" width="68" sortable>
            <template #default="{row}">
              <el-image class="product-image" :src="row.imageUrls" fit="cover">
                <template #error>
                  <div class="image-slot">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </template>
          </el-table-column>
          <el-table-column v-if="checkColumn('商品编号')" :label="getColumnLabel('商品编号')" prop="productCode" min-width="100" align="center" sortable />
          <el-table-column v-if="checkColumn('规格型号')" :label="getColumnLabel('规格型号')" prop="spec" min-width="92" align="center" sortable />
          <el-table-column v-if="checkColumn('当前库存')" :label="getColumnLabel('当前库存')" prop="currentStock" min-width="78" align="center" sortable />
          <el-table-column v-if="checkColumn('单位')" :label="getColumnLabel('单位')" prop="unit" width="62" align="center" sortable />
          <el-table-column v-if="checkColumn('进货价格')" :label="getColumnLabel('进货价格')" prop="purchasePrice" min-width="84" align="center" sortable>
            <template #default="{row}">{{ Number(row.purchasePrice || 0).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column v-if="checkColumn('批发价格')" :label="getColumnLabel('批发价格')" prop="wholesalePrice" min-width="84" align="center" sortable>
            <template #default="{row}">{{ Number(row.wholesalePrice || 0).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column v-if="checkColumn('零售价格')" :label="getColumnLabel('零售价格')" prop="retailPrice" min-width="84" align="center" sortable>
            <template #default="{row}">{{ Number(row.retailPrice || 0).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column v-if="checkColumn('商品分类')" :label="getColumnLabel('商品分类')" prop="categoryName" min-width="88" align="center" sortable />
          <el-table-column v-if="checkColumn('条形码')" :label="getColumnLabel('条形码')" min-width="92" align="center" sortable>
            <template #default="{row}">
              {{ row.skuList && row.skuList.length > 0 ? row.skuList[0].skuCode : '' }}
            </template>
          </el-table-column>
          <el-table-column v-if="checkColumn('默认仓库')" :label="getColumnLabel('默认仓库')" prop="defaultWarehouseId" min-width="90" align="center" sortable />
          <el-table-column v-if="checkColumn('商品品牌')" :label="getColumnLabel('商品品牌')" prop="brandName" min-width="86" align="center" sortable />
          <el-table-column v-if="checkColumn('兑换积分')" :label="getColumnLabel('兑换积分')" prop="exchangePoints" min-width="76" align="center" sortable />
          <el-table-column v-if="checkColumn('商品货位')" :label="getColumnLabel('商品货位')" prop="productLocation" min-width="86" align="center" sortable />
          <el-table-column v-if="checkColumn('库存预警')" :label="getColumnLabel('库存预警')" prop="warningQty" min-width="76" align="center" sortable />
          <el-table-column v-if="checkColumn('备注信息')" :label="getColumnLabel('备注信息')" prop="remark" min-width="130" align="center" show-overflow-tooltip sortable />

          <el-table-column v-if="checkColumn('相关操作')" :label="getColumnLabel('相关操作')" align="center" width="145" fixed="right">
            <template #default="{row}">
              <el-button type="primary" size="small" class="op-btn op-edit" @click="handleUpdate(row)">修改</el-button>
              <el-button size="small" class="op-btn" @click="handleCopy(row)">复制</el-button>
              <el-button size="small" class="op-btn" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="footer-bar">
          <el-pagination
            v-show="total>0"
            :total="total"
            v-model:current-page="listQuery.pageNum"
            v-model:page-size="listQuery.pageSize"
            :page-sizes="[10, 20, 30, 50]"
            layout="prev, pager, next, jumper, total, sizes"
            @size-change="getList"
            @current-change="getList"
          />
        </div>
      </div>
    </section>

    <el-dialog v-model="showColumnSettings" width="560px" :show-close="false" class="column-setting-dialog">
      <div class="column-dialog-header">
        <span>显示字段设置</span>
        <el-icon class="column-dialog-close" @click="showColumnSettings = false"><Close /></el-icon>
      </div>
      <el-table :data="allColumns" border size="small" :show-header="false">
        <el-table-column width="42" align="center">
          <template #default>
            <el-icon class="drag-icon"><Rank /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="label" min-width="130" />
        <el-table-column prop="customLabel" min-width="190">
          <template #default="{row}">
            <el-input v-model="row.customLabel" size="small" />
          </template>
        </el-table-column>
        <el-table-column width="52" align="center">
          <template #default="{row}">
            <el-checkbox v-model="row.visible" />
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <div class="column-dialog-footer">
          <el-button type="primary" @click="saveColumnSettings">保存</el-button>
          <el-button @click="showColumnSettings = false">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getProductList, deleteProduct, getProductCategoryTree } from '@/api/product'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture, ArrowDown, Close, Folder, Document, Setting, Rank } from '@element-plus/icons-vue'

const router = useRouter()

const list = ref([])
const total = ref(0)
const listLoading = ref(true)
const listQuery = reactive({
  pageNum: 1,
  pageSize: 30,
  spuName: undefined,
  productCode: undefined,
  spec: undefined,
  categoryId: undefined,
  showDisabled: false
})

const categoryTree = ref<any[]>([])

const fetchCategoryTree = async () => {
  try {
    const res = await getProductCategoryTree()
    categoryTree.value = [
      { id: 0, label: '全部分类', children: res.data || [] }
    ]
  } catch (e) {
    console.error(e)
  }
}

const handleCategoryClick = (data: any) => {
  listQuery.categoryId = data.id && Number(data.id) > 0 ? data.id : undefined
  handleFilter()
}

const editCategory = () => {
  router.push('/data/category')
}

const selectedRows = ref([])

// 动态列设置
const showColumnSettings = ref(false)
const allColumns = ref([
  { label: '商品名称', customLabel: '商品名称', visible: true },
  { label: '图像', customLabel: '图像', visible: true },
  { label: '商品编号', customLabel: '商品编号', visible: true },
  { label: '规格型号', customLabel: '规格型号', visible: true },
  { label: '当前库存', customLabel: '当前库存', visible: true },
  { label: '单位', customLabel: '单位', visible: true },
  { label: '商品品牌', customLabel: '商品品牌', visible: false },
  { label: '进货价格', customLabel: '进货价格', visible: true },
  { label: '批发价格', customLabel: '批发价格', visible: true },
  { label: '零售价格', customLabel: '零售价格', visible: true },
  { label: '兑换积分', customLabel: '兑换积分', visible: false },
  { label: '商品分类', customLabel: '商品分类', visible: true },
  { label: '条形码', customLabel: '条形码', visible: true },
  { label: '默认仓库', customLabel: '默认仓库', visible: true },
  { label: '商品货位', customLabel: '商品货位', visible: false },
  { label: '库存预警', customLabel: '库存预警', visible: false },
  { label: '备注信息', customLabel: '备注信息', visible: true },
  { label: '相关操作', customLabel: '相关操作', visible: true }
])

const checkColumn = (label: string) => {
  const col = allColumns.value.find(c => c.label === label)
  return col ? col.visible : false
}

const getColumnLabel = (label: string) => {
  const col = allColumns.value.find(c => c.label === label)
  return col ? (col.customLabel || col.label) : label
}

const saveColumnSettings = () => {
  showColumnSettings.value = false
  localStorage.setItem('product_list_columns', JSON.stringify(allColumns.value))
}

const loadColumnSettings = () => {
  const saved = localStorage.getItem('product_list_columns')
  if (saved) {
    try {
      const parsed = JSON.parse(saved)
      parsed.forEach((pCol: any) => {
        const col = allColumns.value.find(c => c.label === pCol.label)
        if (col) {
          col.visible = pCol.visible
          col.customLabel = pCol.customLabel
        }
      })
    } catch (e) {}
  }
}

const handleSelectionChange = (val: any) => {
  selectedRows.value = val
}

const getList = async () => {
  listLoading.value = true
  try {
    const res = await getProductList({
      ...listQuery,
      status: listQuery.showDisabled ? undefined : 1
    })
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
  router.push('/data/product/add')
}

const handleUpdate = (row: any) => {
  router.push(`/data/product/edit/${row.spuId}`)
}

const handleCopy = (row: any) => {
  router.push({ path: '/data/product/add', query: { copyFrom: row.spuId } })
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确认删除该商品吗?', '提示', {
    type: 'warning'
  }).then(async () => {
    await deleteProduct(row.spuId)
    ElMessage.success('删除成功')
    getList()
  })
}

onMounted(() => {
  fetchCategoryTree()
  loadColumnSettings()
  getList()
})
</script>

<style scoped>
.product-page {
  height: calc(100vh - 84px);
  min-height: 680px;
  display: flex;
  gap: 8px;
  padding: 0;
  background: #f7f7f9;
}

.category-panel {
  width: 170px;
  background: #fff;
  border: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
}

.category-header {
  height: 40px;
  border-bottom: 1px solid #ebeef5;
  padding: 0 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.category-title {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
}

.category-body {
  flex: 1;
  overflow: auto;
  padding: 6px 0;
}

.category-node {
  display: flex;
  align-items: center;
  font-size: 12px;
}

.category-icon {
  margin-right: 4px;
  color: #909399;
  font-size: 14px;
}

.list-panel {
  flex: 1;
  background: #fff;
  border: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.toolbar {
  min-height: 44px;
  border-bottom: 1px solid #ebeef5;
  padding: 6px 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 8px;
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-primary,
.btn-default {
  height: 28px;
  padding: 0 12px;
  font-size: 12px;
  border-radius: 2px;
}

.query-input {
  width: 120px;
}

.expand-btn {
  font-size: 12px;
  padding: 0 4px;
}

.setting-btn {
  width: 28px;
  height: 28px;
}

.table-wrap {
  flex: 1;
  min-height: 0;
  padding: 8px 10px 6px;
  display: flex;
  flex-direction: column;
}

.product-table {
  width: 100%;
  font-size: 12px;
}

.name-link {
  color: #409eff;
  cursor: pointer;
}

.product-image {
  width: 34px;
  height: 34px;
}

.image-slot {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  background: #f5f7fa;
}

.op-btn {
  height: 22px;
  line-height: 20px;
  padding: 0 8px;
  border-radius: 3px;
  font-size: 12px;
}

.op-edit {
  background: #409eff;
  border-color: #409eff;
}

.footer-bar {
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  margin-top: 6px;
}

.column-dialog-header {
  height: 40px;
  margin: -20px -20px 16px;
  padding: 0 14px;
  background: #20a8ff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 18px;
}

.column-dialog-close {
  cursor: pointer;
  font-size: 20px;
}

.drag-icon {
  cursor: move;
  color: #606266;
}

.column-dialog-footer {
  text-align: center;
}

:deep(.el-dialog__header) {
  display: none;
}

:deep(.el-tree-node__content) {
  height: 30px;
  padding-right: 6px;
}

:deep(.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content) {
  background: #f0f7ff;
  color: #409eff;
}

:deep(.product-table .el-table__header-wrapper th.el-table__cell) {
  padding: 6px 0;
}

:deep(.product-table .el-table__body-wrapper td.el-table__cell) {
  padding: 6px 0;
}

:deep(.el-pagination) {
  font-size: 12px;
}

:deep(.column-setting-dialog .el-dialog__body) {
  padding-top: 0;
}
</style>
