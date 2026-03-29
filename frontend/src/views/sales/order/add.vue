<template>
  <div class="add-booking-container">
    <el-card class="box-card">
      <div class="top-bar">
        <div class="left-actions">
          <span class="page-title"><el-icon><Tickets /></el-icon> 销售单</span>
          <el-button type="primary" size="small" @click="openSelectProduct">选择商品</el-button>
          <el-button size="small">导入商品</el-button>
          <el-button size="small">扫码录入</el-button>
          <el-button size="small">选择模板</el-button>
          
          <div class="price-type">
            <span class="label">销售价格：</span>
            <el-radio-group v-model="formData.priceType">
              <el-radio label="wholesale">批发价</el-radio>
              <el-radio label="retail">零售价</el-radio>
            </el-radio-group>
          </div>
        </div>
        <div class="right-actions">
          <el-button type="primary" @click="saveOrder">保存 (ctrl+Q)</el-button>
          <el-button @click="handleRefresh">刷新</el-button>
          <el-button>历史单据</el-button>
        </div>
      </div>

      <div class="basic-info-bar">
        <div class="info-item customer-item">
          <el-icon><User /></el-icon>
          <span class="label">客户</span>
          <div class="customer-select-wrapper">
            <el-select
              v-model="formData.customer"
              placeholder="请选择客户"
              popper-class="customer-select-popper"
              filterable
              style="width: 260px;"
              @visible-change="onCustomerVisibleChange"
            >
              <template #header>
                <div class="customer-dropdown-header">
                  <el-input
                    v-model="customerQuery.keyword"
                    placeholder="关键字搜索"
                    clearable
                    size="small"
                    @input="resetCustomerPage"
                  >
                    <template #prefix>
                      <el-icon><Search /></el-icon>
                    </template>
                  </el-input>
                </div>
              </template>

              <el-option
                v-for="c in pagedCustomers"
                :key="c.id"
                :label="c.name"
                :value="c.id"
              />

              <template #footer>
                <div class="customer-dropdown-footer">
                  <div class="customer-actions">
                    <el-button link type="primary" @mousedown.prevent @click="refreshCustomers">
                      <el-icon><Refresh /></el-icon>
                      刷新客户
                    </el-button>
                    <el-button link type="primary" @mousedown.prevent @click="openAddCustomerDialog">
                      <el-icon><Plus /></el-icon>
                      新增客户
                    </el-button>
                  </div>
                  <div class="customer-pager">
                    <el-button
                      size="small"
                      :disabled="customerPager.page <= 1"
                      @mousedown.prevent
                      @click="customerPrevPage"
                    >
                      <el-icon><ArrowLeft /></el-icon>
                    </el-button>
                    <el-button
                      size="small"
                      :disabled="customerPager.page >= customerTotalPages"
                      @mousedown.prevent
                      @click="customerNextPage"
                    >
                      <el-icon><ArrowRight /></el-icon>
                    </el-button>
                    <span class="customer-total">共 {{ filteredCustomers.length }} 条</span>
                  </div>
                </div>
              </template>
            </el-select>

            <el-button
              class="quick-add-btn"
              type="primary"
              size="small"
              @click="openAddCustomerDialog"
            >
              快速添加
            </el-button>
          </div>
        </div>
        
        <div class="info-item">
          <span class="label">单据日期</span>
          <el-date-picker
            v-model="formData.date"
            type="datetime"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="选择时间"
          />
        </div>
        
        <div class="info-item">
          <span class="label">单据编号</span>
          <el-input v-model="formData.orderNo" readonly />
        </div>
        
        <div class="info-item">
          <el-checkbox v-model="formData.needDelivery" border>送货</el-checkbox>
        </div>
      </div>

      <div class="detail-table-area">
        <el-table :data="formData.details" border style="width: 100%" class="custom-table" show-summary :summary-method="getSummaries">
          <el-table-column type="index" label="序号" width="50" align="center" />
          <el-table-column label="操作" width="80" align="center">
            <template #default="scope">
              <el-icon class="action-icon add-icon" @click="addRow(scope.$index)"><CirclePlusFilled /></el-icon>
              <el-icon class="action-icon remove-icon" @click="removeRow(scope.$index)"><RemoveFilled /></el-icon>
            </template>
          </el-table-column>
          <el-table-column label="图片" width="80" align="center" />
          <el-table-column label="*商品信息" min-width="200" align="center">
            <template #default="scope">
              <el-autocomplete
                v-if="enableQuickProductDropdown"
                v-model="scope.row.productInfo"
                :fetch-suggestions="fetchProductSuggestions"
                trigger-on-focus
                clearable
                placeholder="搜索名称、编号、条形码"
                style="width: 100%;"
                class="product-selector-input"
                @select="(item: any) => onSelectProductSuggestion(item, scope.row)"
              >
                <template #append>
                  <el-button type="success" @click="openSelectProduct">选择</el-button>
                </template>
              </el-autocomplete>
              <el-input 
                v-else 
                v-model="scope.row.productInfo" 
                placeholder="搜索名称、编号、条形码" 
                readonly 
                class="product-selector-input"
                @click="openSelectProduct"
              >
                <template #append>
                  <el-button type="success" @click="openSelectProduct">选择</el-button>
                </template>
              </el-input>
            </template>
          </el-table-column>
          <el-table-column v-if="enableSizeColor" label="颜色" width="120" align="center">
            <template #default="scope">
              <span>{{ scope.row.color }}</span>
            </template>
          </el-table-column>
          <el-table-column v-if="!enableSizeColor" label="辅助属性" width="100" align="center" />
          <el-table-column v-if="!enableSizeColor" label="规格型号" width="100" align="center" />
          <el-table-column v-if="!enableSizeColor" label="单位" width="80" align="center" />
          <el-table-column v-if="!enableSizeColor" label="商品批次" width="100" align="center" />
          
          <el-table-column v-if="enableSizeColor" v-for="(label, idx) in sizeColumnLabels" :key="'col'+(idx+1)" :label="label" width="88" align="center">
            <template #default="scope">
              <el-input v-model="scope.row['col'+(idx+1)]" size="small" class="qty-input" @input="updateRowQty(scope.row)" />
            </template>
          </el-table-column>

          <el-table-column label="*数量" width="100" align="center">
            <template #default="scope">
              <el-input v-if="enableSizeColor" v-model="scope.row.qty" readonly class="qty-input" />
              <el-input v-else v-model="scope.row.qty" class="qty-input" />
            </template>
          </el-table-column>
          <el-table-column label="*单价(元)" width="100" align="center">
            <template #default="scope">
              <el-input v-model="scope.row.unitPrice" />
            </template>
          </el-table-column>
          <el-table-column label="金额(元)" width="120" align="center">
            <template #default="scope">
              <el-input :value="calculateAmount(scope.row)" readonly />
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="footer-area">
        <div class="footer-left">
          <div class="form-row">
            <span class="label">制单人</span>
            <el-select v-model="formData.creator" placeholder="管理员" style="width: 150px;">
              <el-option label="管理员" value="admin" />
            </el-select>
          </div>
          <div class="form-row" style="flex: 1;">
            <span class="label">备注信息</span>
            <el-input v-model="formData.remark" placeholder="请输入备注信息" />
          </div>
          <div class="upload-area">
            <div class="upload-btn">
              <el-icon size="24"><Plus /></el-icon>
              <span>上传附件</span>
            </div>
          </div>
        </div>
        
        <div class="footer-right">
          <div class="summary-row">
            <div class="summary-item">
              <span class="label">折扣率(%)</span>
              <el-input-number v-model="formData.discountRate" :min="0" :max="100" controls-position="right" style="width: 100px;" />
            </div>
            <div class="summary-item">
              <span class="label">折后金额</span>
              <el-input v-model="formData.discountedAmount" readonly style="width: 120px;" />
            </div>
            <div class="summary-item">
              <span class="label">其他费用</span>
              <el-input v-model="formData.otherFee" style="width: 100px;" />
            </div>
            <div class="summary-item">
              <span class="label">结算账户</span>
              <el-select v-model="formData.account" placeholder="请选择结算账户" style="width: 150px;">
                <el-option label="现金" value="cash" />
              </el-select>
            </div>
          </div>
          
          <div class="summary-row total-row">
            <div class="total-amount">
              总计金额：<span class="amount-value">{{ finalTotalAmount }}</span>
            </div>
            <div class="summary-item">
              <span class="label">定金</span>
              <el-input v-model="formData.deposit" style="width: 150px;" />
            </div>
          </div>
          
          <div class="summary-row action-row">
            <el-checkbox v-model="formData.printAfterSave">保存后打印</el-checkbox>
            <el-button type="primary" size="large" @click="saveOrder" style="width: 150px;">保存 (ctrl+Q)</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <el-dialog v-model="selectProductVisible" width="95%" top="5vh" :show-close="false" class="select-product-dialog">
      <template #header>
        <div class="select-product-header">
          <span>选择商品</span>
          <el-icon class="dialog-close" @click="selectProductVisible = false"><Close /></el-icon>
        </div>
      </template>

      <div class="select-product-body">
        <el-container>
          <el-aside width="220px" class="category-aside">
            <div class="category-title">商品分类</div>
            <el-tree
              :data="categoryTree"
              node-key="id"
              :props="{ children: 'children', label: 'label' }"
              default-expand-all
              highlight-current
              @node-click="onCategoryClick"
            />
          </el-aside>

          <el-main class="product-main">
            <div class="product-toolbar">
              <div class="toolbar-left">
                <el-button type="success" size="small" @click="addProductVisible = true">添加商品</el-button>
                <el-button icon="Setting" circle size="small" @click="openProductSetting" />
              </div>
              <div class="toolbar-right">
                <el-input v-model="productSearch.keyword" placeholder="搜索编号、名称、规格、条码、属性、备注" clearable style="width: 320px" />
                <el-button type="primary" size="small">查询</el-button>
                <el-button size="small">高级搜索</el-button>
              </div>
            </div>

            <SizeColorMatrixPicker v-if="enableSizeColor" :products="filteredProducts" v-model="matrixQtyMap" :columns="productColumns" @update:products="onProductsUpdate" />
            <el-table
              v-else
              :data="filteredProducts"
              border
              style="width: 100%"
              height="520"
              size="small"
              @selection-change="onProductSelectionChange"
            >
              <template v-for="col in productColumns" :key="col.prop">
                <el-table-column
                  v-if="col.visible"
                  :prop="col.prop"
                  :label="col.label"
                  :type="col.type"
                  :width="col.width"
                  :min-width="col.minWidth"
                  align="center"
                >
                  <template #default="scope" v-if="col.prop === 'selectedQty'">
                    <el-input-number v-model="scope.row.selectedQty" :min="1" :max="99999" controls-position="right" size="small" style="width: 72px" />
                  </template>
                  <template #default="scope" v-else-if="col.prop === 'name'">
                    <el-link type="primary" :underline="false">{{ scope.row.name }}</el-link>
                  </template>
                  <template #default v-else-if="col.prop === 'image'">
                    —
                  </template>
                </el-table-column>
              </template>
            </el-table>

            <div class="product-pagination">
              <el-pagination
                v-model:current-page="productPager.page"
                v-model:page-size="productPager.pageSize"
                :page-sizes="[10, 20, 30, 50]"
                layout="prev, pager, next, jumper, total, sizes"
                :total="filteredProducts.length"
              />
            </div>
          </el-main>
        </el-container>
      </div>

      <template #footer>
        <div class="select-product-footer">
          <el-button type="primary" @click="confirmSelectProducts">选中</el-button>
          <el-button @click="selectProductVisible = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="productSettingVisible" width="640px" :show-close="false" class="product-setting-dialog">
      <template #header>
        <div class="select-product-header">
          <span>显示字段设置</span>
          <el-icon class="dialog-close" @click="productSettingVisible = false"><Close /></el-icon>
        </div>
      </template>

      <div class="setting-tips">
        <p>按住上下拖拽可以改变字段顺序。</p>
        <p>注意：字段配置名称为空的行，以及商品名称、数量这些关键行，不要隐藏和不要改变位置。</p>
      </div>

      <el-table
        :data="productSettingColumns"
        row-key="prop"
        border
        size="small"
        ref="productSettingTableRef"
      >
        <el-table-column label="排序" width="60" align="center">
          <template #default="scope">
            <el-icon v-if="!isProductColumnLocked(scope.row)" class="drag-handle" style="cursor: move; font-size: 16px;"><Sort /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="originalLabel" label="字段名称" width="150" />
        <el-table-column label="字段配置">
          <template #default="scope">
            <el-input v-model="scope.row.label" size="small" :disabled="!scope.row.originalLabel" />
          </template>
        </el-table-column>
        <el-table-column label="显示" width="80" align="center">
          <template #default="scope">
            <el-checkbox v-model="scope.row.visible" :disabled="isProductColumnLocked(scope.row) || !scope.row.originalLabel" />
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="productSettingVisible = false">取消</el-button>
          <el-button type="primary" @click="saveProductSetting">保存</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="addCustomerVisible" width="680px" top="8vh" :show-close="false" class="add-customer-dialog">
      <template #header>
        <div class="select-product-header">
          <span>新增客户</span>
          <el-icon class="dialog-close" @click="addCustomerVisible = false"><Close /></el-icon>
        </div>
      </template>
      <div class="add-customer-body">
        <el-form :model="addCustomerForm" label-position="right" label-width="88px" class="add-customer-form">
          <div class="form-section">
            <div class="section-title">基础信息</div>
            <div class="section-body">
              <div class="form-row-grid">
                <el-form-item>
                  <template #label><span class="required-label">客户名称：</span></template>
                  <el-input v-model="addCustomerForm.name" placeholder="请输入客户名称" />
                </el-form-item>
                <el-form-item label="客户编号：">
                  <el-input v-model="addCustomerForm.code" placeholder="请输入客户编号" />
                </el-form-item>
              </div>
              <div class="form-row-grid">
                <el-form-item label="客户分类：">
                  <div class="inline-with-link">
                    <el-select v-model="addCustomerForm.category" placeholder="请选择客户分类" style="flex: 1;">
                      <el-option v-for="item in customerCategories" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                    <el-link type="primary" :underline="false" class="inline-link" @click="handleAddCustomerCategory">新增</el-link>
                  </div>
                </el-form-item>
                <el-form-item label="客户等级：">
                  <div class="inline-with-link">
                    <el-select v-model="addCustomerForm.level" placeholder="普通客户" style="flex: 1;">
                      <el-option v-for="item in customerLevels" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                    <el-link type="primary" :underline="false" class="inline-link" @click="handleSetCustomerLevel">设置</el-link>
                  </div>
                </el-form-item>
              </div>
              <div class="form-row-grid">
                <el-form-item>
                  <template #label><span class="warning-label">期初欠款：</span></template>
                  <el-input v-model="addCustomerForm.initialArrears" placeholder="请输入期初欠款" />
                </el-form-item>
                <div />
              </div>
            </div>
          </div>

          <div class="form-section">
            <div class="section-title">联系信息</div>
            <div class="section-body">
              <div class="form-row-grid">
                <el-form-item label="联系人：">
                  <el-input v-model="addCustomerForm.contactName" placeholder="请输入联系人" />
                </el-form-item>
                <el-form-item label="联系电话：">
                  <el-input v-model="addCustomerForm.contactPhone" placeholder="请输入联系电话" />
                </el-form-item>
              </div>
              <div class="form-row-grid">
                <el-form-item label="客户地址：" class="span-2">
                  <el-input v-model="addCustomerForm.address" placeholder="请输入客户地址" />
                </el-form-item>
              </div>
            </div>
          </div>

          <div class="form-section">
            <div class="section-title">财务信息</div>
            <div class="section-body">
              <div class="form-row-grid">
                <el-form-item label="开户银行：">
                  <el-input v-model="addCustomerForm.bankName" placeholder="请输入开户银行" />
                </el-form-item>
                <el-form-item label="银行账号：">
                  <el-input v-model="addCustomerForm.bankAccount" placeholder="请输入银行账号" />
                </el-form-item>
              </div>
              <div class="form-row-grid">
                <el-form-item label="客户税号：">
                  <el-input v-model="addCustomerForm.taxNo" placeholder="请输入客户税号" />
                </el-form-item>
                <div />
              </div>
            </div>
          </div>

          <div class="form-section">
            <div class="section-title">其他信息</div>
            <div class="section-body">
              <div class="form-row-grid">
                <el-form-item label="客户生日：">
                  <el-date-picker
                    v-model="addCustomerForm.birthday"
                    type="date"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                    placeholder="请输入客户生日"
                    style="width: 100%;"
                  />
                </el-form-item>
                <el-form-item label="社交账号：">
                  <el-input v-model="addCustomerForm.socialAccount" placeholder="请输入社交账号" />
                </el-form-item>
              </div>
              <div class="form-row-grid">
                <el-form-item label="邮箱地址：">
                  <el-input v-model="addCustomerForm.email" placeholder="请输入邮箱地址" />
                </el-form-item>
                <el-form-item label="备注信息：">
                  <el-input v-model="addCustomerForm.remark" placeholder="请输入备注信息" />
                </el-form-item>
              </div>
              <div class="form-row-grid">
                <el-form-item label="默认：">
                  <el-radio-group v-model="addCustomerForm.isDefault">
                    <el-radio :value="false">否</el-radio>
                    <el-radio :value="true">是</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="状态：">
                  <el-radio-group v-model="addCustomerForm.status">
                    <el-radio value="enabled">启用</el-radio>
                    <el-radio value="disabled">停用</el-radio>
                  </el-radio-group>
                </el-form-item>
              </div>
            </div>
          </div>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="confirmAddCustomer">保存</el-button>
          <el-button @click="addCustomerVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog v-model="addProductVisible" title="新增商品" width="680px" append-to-body>
      <el-form :model="addProductForm" label-width="100px" class="add-product-form">
        <div class="form-row-grid">
          <el-form-item label="商品名称" required>
            <el-input v-model="addProductForm.name" placeholder="请输入商品名称" />
          </el-form-item>
          <el-form-item label="商品分类">
            <el-select v-model="addProductForm.category" style="width: 100%">
              <el-option label="上衣" value="上衣" />
              <el-option label="裤子" value="裤子" />
              <el-option label="冰丝塑袖" value="冰丝塑袖" />
            </el-select>
          </el-form-item>
        </div>
        <div class="form-row-grid">
          <el-form-item label="颜色规格">
            <el-input v-model="addProductForm.attributes" placeholder="如：黑色|2XL" />
          </el-form-item>
          <el-form-item label="商品条码">
            <el-input v-model="addProductForm.barcode" placeholder="请输入条形码" />
          </el-form-item>
        </div>
        <div class="form-row-grid">
          <el-form-item label="商品编号">
            <el-input v-model="addProductForm.productCode" placeholder="请输入编号" />
          </el-form-item>
          <el-form-item label="规格型号">
            <el-input v-model="addProductForm.spec" placeholder="请输入规格" />
          </el-form-item>
        </div>
        <div class="form-row-grid">
          <el-form-item label="品牌">
            <el-input v-model="addProductForm.brand" placeholder="请输入品牌" />
          </el-form-item>
          <el-form-item label="单位">
            <el-input v-model="addProductForm.unit" placeholder="如：件、个" />
          </el-form-item>
        </div>
        <div class="form-row-grid">
          <el-form-item label="批发价">
            <el-input-number v-model="addProductForm.wholesalePrice" :min="0" :precision="2" style="width: 100%" controls-position="right" />
          </el-form-item>
          <el-form-item label="进货价格">
            <el-input-number v-model="addProductForm.purchasePrice" :min="0" :precision="2" style="width: 100%" controls-position="right" />
          </el-form-item>
        </div>
        <div class="form-row-grid">
          <el-form-item label="零售价格">
            <el-input-number v-model="addProductForm.retailPrice" :min="0" :precision="2" style="width: 100%" controls-position="right" />
          </el-form-item>
          <el-form-item label="备注信息">
            <el-input v-model="addProductForm.remark" placeholder="请输入备注" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addProductVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmAddProduct">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, computed, ref, nextTick, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Sortable from 'sortablejs'
import { createSalesOrder } from '@/api/sales'
import { getSystemSettings } from '@/api/settings'
import { addProduct, getProductDetail, getProductList } from '@/api/product'
import { getViewConfig, saveViewConfig } from '@/api/ui'
import SizeColorMatrixPicker from '@/components/SizeColorMatrixPicker.vue'
import Decimal from 'decimal.js'

const handleRefresh = () => {
  window.location.reload()
}

const router = useRouter()

const enableSizeColor = ref(false)
const enableQuickProductDropdown = ref(false)
const allowNewProductOnBilling = ref(false)
const sizeLabels = ref<string[]>([])
const defaultSizeColumnLabels = Array.from({ length: 10 }).map((_, i) => String(i + 1))

const sizeColumnLabels = computed(() => {
  if (!enableSizeColor.value) return defaultSizeColumnLabels
  return sizeLabels.value.length ? sizeLabels.value : defaultSizeColumnLabels
})

const sizeColumnCount = computed(() => sizeColumnLabels.value.length)

const ensureRowCols = (row: any) => {
  for (let i = 1; i <= sizeColumnCount.value; i++) {
    const key = `col${i}`
    if (!(key in row)) row[key] = ''
  }
}

const generateEmptyRows = (count = 8) => {
  const colCount = sizeColumnCount.value
  return Array.from({ length: count }).map(() => {
    const cols: Record<string, string> = {}
    for (let i = 1; i <= colCount; i++) {
      cols[`col${i}`] = ''
    }
    return {
      skuId: undefined,
      spuId: undefined,
      productInfo: '',
      color: '',
      unitPrice: '',
      qty: '',
      ...cols
    }
  })
}

const formData = reactive({
  priceType: 'wholesale',
  customer: '',
  date: new Date(),
  orderNo: '自动生成',
  needDelivery: false,
  details: generateEmptyRows(),
  creator: 'admin',
  remark: '',
  discountRate: 100,
  discountedAmount: '0.00',
  otherFee: 0,
  account: '',
  deposit: 0,
  printAfterSave: true
})

const updateRowQty = (row: any) => {
  let sum = 0
  for (let i = 1; i <= sizeColumnCount.value; i++) {
    sum += Number(row[`col${i}`]) || 0
  }
  row.qty = sum ? String(sum) : ''
}

const addRow = (index: number) => {
  formData.details.splice(index + 1, 0, generateEmptyRows(1)[0])
}

const removeRow = (index: number) => {
  if (formData.details.length > 1) {
    formData.details.splice(index, 1)
  }
}

watch(
  sizeColumnCount,
  () => {
    formData.details.forEach(ensureRowCols)
  },
  { immediate: true }
)

onMounted(async () => {
  try {
    const res = await getSystemSettings()
    enableSizeColor.value = !!res?.data?.enableSizeColor || res?.data?.printClothesWholesaleLayout === '是'
    enableQuickProductDropdown.value = res?.data?.enableQuickProductDropdown === '是'
    allowNewProductOnBilling.value = res?.data?.allowNewProductOnBilling === '是'
  } catch {
    enableSizeColor.value = false
    enableQuickProductDropdown.value = false
    allowNewProductOnBilling.value = false
  }
  await refreshProductColumns()
})

const fetchProductSuggestions = async (queryString: string, cb: (items: any[]) => void) => {
  try {
    const res = await getProductList({ pageNum: 1, pageSize: 20, spuName: queryString || undefined })
    const list = (res?.data?.list || []).map((p: any) => ({ value: p.spuName, spuId: p.spuId }))
    cb(list)
  } catch {
    cb([])
  }
}

const onSelectProductSuggestion = async (item: any, row: any) => {
  if (!item?.spuId) return
  const res = await getProductDetail(Number(item.spuId))
  const spu = res?.data
  const sku = spu?.skuList?.[0]
  row.spuId = spu?.spuId
  row.skuId = sku?.skuId
  row.color = ''
  row.productInfo = spu?.spuName || row.productInfo
  if (!row.unitPrice && sku?.wholesalePrice != null) {
    row.unitPrice = String(sku.wholesalePrice)
  }
}

const calculateAmount = (row: any) => {
  const qty = new Decimal(row.qty || 0)
  const price = new Decimal(row.unitPrice || 0)
  return qty.times(price).toFixed(2)
}

const getSummaries = (param: any) => {
  const { columns, data } = param
  const sums: string[] = []
  columns.forEach((column: any, index: number) => {
    if (index === 2) {
      sums[index] = '合计'
      return
    }
    // 仅针对数量和金额列进行求和展示，实际业务中可扩展
    if (column.label === '*数量') {
      const totalQty = data.reduce((sum: Decimal, row: any) => sum.plus(row.qty || 0), new Decimal(0))
      sums[index] = totalQty.isZero() ? '' : totalQty.toString()
    } else if (column.label === '金额(元)') {
      const totalAmt = data.reduce((sum: Decimal, row: any) => {
        const qty = new Decimal(row.qty || 0)
        const price = new Decimal(row.unitPrice || 0)
        return sum.plus(qty.times(price))
      }, new Decimal(0))
      sums[index] = totalAmt.isZero() ? '' : totalAmt.toFixed(2)
    } else {
      sums[index] = ''
    }
  })
  return sums
}

const finalTotalAmount = computed(() => {
  const totalAmt = formData.details.reduce((sum: Decimal, row: any) => {
    const qty = new Decimal(row.qty || 0)
    const price = new Decimal(row.unitPrice || 0)
    return sum.plus(qty.times(price))
  }, new Decimal(0))
  
  const discountRate = new Decimal(formData.discountRate || 0).dividedBy(100)
  const discounted = totalAmt.times(discountRate)
  formData.discountedAmount = discounted.toFixed(2)
  
  const otherFee = new Decimal(formData.otherFee || 0)
  const final = discounted.plus(otherFee)
  return final.toFixed(2)
})

const saveOrder = async () => {
  const validDetails = formData.details.filter((item: any) => item.productInfo && Number(item.qty) > 0)
  if (validDetails.length === 0) {
    ElMessage.warning('请选择商品并填写数量')
    return
  }

  const newProductCache = new Map<string, { spuId: number; skuId: number }>()
  const ensureProductIds = async (item: any) => {
    const spuId = Number(item.spuId)
    const skuId = Number(item.skuId)
    if (spuId && skuId) return { spuId, skuId }
    const name = String(item.productInfo || '').trim()
    const cacheKey = `${name}|${item.color || ''}|${item.unitPrice || ''}`
    if (!name) {
      throw new Error('商品信息不能为空')
    }
    if (!allowNewProductOnBilling.value) {
      throw new Error('请先选择已存在商品，或在系统参数开启“新商品开单”')
    }
    if (newProductCache.has(cacheKey)) return newProductCache.get(cacheKey)!
    const code = `AUTO${Date.now()}${Math.floor(Math.random() * 1000)}`
    const createRes = await addProduct({
      spuName: name,
      unit: '件',
      status: 1,
      skuList: [
        {
          skuCode: code,
          attr1: '',
          attr2: '',
          purchasePrice: 0,
          retailPrice: 0,
          wholesalePrice: Number(item.unitPrice) || 0,
          weight: 0,
          status: 1
        }
      ]
    })
    const createdSpuId = Number(createRes?.data)
    const detailRes = await getProductDetail(createdSpuId)
    const createdSkuId = Number(detailRes?.data?.skuList?.[0]?.skuId)
    const ids = { spuId: createdSpuId, skuId: createdSkuId }
    newProductCache.set(cacheKey, ids)
    item.spuId = createdSpuId
    item.skuId = createdSkuId
    return ids
  }
  
  const details = []
  try {
    for (const item of validDetails) {
      const ids = await ensureProductIds(item)
      details.push({
        spuId: ids.spuId,
        skuId: ids.skuId,
        qty: Number(item.qty),
        unitPrice: Number(item.unitPrice),
        lineAmount: Number(calculateAmount(item))
      })
    }
  } catch (e: any) {
    ElMessage.error(e?.message || '商品处理失败')
    return
  }

  const submitData = {
      orderType: 1, // 批发单(销售单)
      docDate: typeof formData.date === 'string' ? formData.date : (formData.date as Date).toISOString().split('T')[0],
      customerId: formData.customer ? Number(formData.customer) : undefined,
    remark: formData.remark,
    totalAmount: Number(finalTotalAmount.value),
    discountAmount: Number(new Decimal(finalTotalAmount.value).minus(new Decimal(formData.discountedAmount)).toFixed(2)),
    actualAmount: Number(formData.discountedAmount),
    otherFee: Number(formData.otherFee || 0),
    paidAmount: Number(formData.deposit || 0),
    details
  }
  
  try {
    await createSalesOrder(submitData)
    ElMessage.success('单据保存成功！')
    router.back()
  } catch (e) {
    console.error(e)
  }
}

type CustomerItem = {
  id: string
  name: string
}

const customerQuery = reactive({
  keyword: ''
})

const customerPager = reactive({
  page: 1,
  pageSize: 5
})

const customers = ref<CustomerItem[]>([])

const filteredCustomers = computed(() => {
  const keyword = customerQuery.keyword.trim()
  if (!keyword) return customers.value
  return customers.value.filter(c => c.name.includes(keyword))
})

const customerTotalPages = computed(() => {
  return Math.max(1, Math.ceil(filteredCustomers.value.length / customerPager.pageSize))
})

const pagedCustomers = computed(() => {
  const start = (customerPager.page - 1) * customerPager.pageSize
  return filteredCustomers.value.slice(start, start + customerPager.pageSize)
})

const resetCustomerPage = () => {
  customerPager.page = 1
}

const customerPrevPage = () => {
  if (customerPager.page > 1) customerPager.page -= 1
}

const customerNextPage = () => {
  if (customerPager.page < customerTotalPages.value) customerPager.page += 1
}

const refreshCustomers = () => {
  ElMessage.success('客户已刷新')
}

const addCustomerVisible = ref(false)
const customerCategories = ref([{ label: '默认分类', value: 'default' }])
const customerLevels = ref([
  { label: '普通客户', value: 'normal' },
  { label: 'VIP客户', value: 'vip' }
])

const handleAddCustomerCategory = () => {
  import('element-plus').then(({ ElMessageBox, ElMessage }) => {
    ElMessageBox.prompt('请输入客户分类名称', '新增客户分类', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    }).then(({ value }) => {
      if (value) {
        const newVal = 'cat_' + Date.now()
        customerCategories.value.push({ label: value, value: newVal })
        addCustomerForm.category = newVal
        ElMessage.success('新增成功')
      }
    }).catch(() => {})
  })
}

const handleSetCustomerLevel = () => {
  import('element-plus').then(({ ElMessageBox, ElMessage }) => {
    ElMessageBox.prompt('请输入客户等级名称', '新增客户等级', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    }).then(({ value }) => {
      if (value) {
        const newVal = 'lvl_' + Date.now()
        customerLevels.value.push({ label: value, value: newVal })
        addCustomerForm.level = newVal
        ElMessage.success('新增成功')
      }
    }).catch(() => {})
  })
}

const addCustomerForm = reactive({
  name: '',
  code: '',
  category: '',
  level: 'normal',
  initialArrears: '',
  contactName: '',
  contactPhone: '',
  address: '',
  bankName: '',
  bankAccount: '',
  taxNo: '',
  birthday: '',
  socialAccount: '',
  email: '',
  remark: '',
  isDefault: false,
  status: 'enabled'
})

const openAddCustomerDialog = () => {
  addCustomerForm.name = ''
  addCustomerForm.code = ''
  addCustomerForm.category = ''
  addCustomerForm.level = 'normal'
  addCustomerForm.initialArrears = ''
  addCustomerForm.contactName = ''
  addCustomerForm.contactPhone = ''
  addCustomerForm.address = ''
  addCustomerForm.bankName = ''
  addCustomerForm.bankAccount = ''
  addCustomerForm.taxNo = ''
  addCustomerForm.birthday = ''
  addCustomerForm.socialAccount = ''
  addCustomerForm.email = ''
  addCustomerForm.remark = ''
  addCustomerForm.isDefault = false
  addCustomerForm.status = 'enabled'
  addCustomerVisible.value = true
}

const confirmAddCustomer = () => {
  const name = addCustomerForm.name.trim()
  if (!name) {
    ElMessage.warning('请输入客户名称')
    return
  }
  const id = `c_${Date.now()}`
  customers.value.unshift({ id, name })
  formData.customer = id
  resetCustomerPage()
  addCustomerVisible.value = false
}

const onCustomerVisibleChange = (visible: boolean) => {
  if (visible) {
    resetCustomerPage()
  }
}

type ProductItem = {
  id: number
  name: string
  attributes: string
  spec: string
  brand: string
  unit: string
  wholesalePrice: string
  purchasePrice: string
  retailPrice: string
  stock: number
  category: string
  barcode: string
  productCode: string
  defaultWarehouse: string
  location: string
  exchangePoints: number
  stockUnit: string
  stockValue: string
  remark: string
  selectedQty: number
}

const selectProductVisible = ref(false)
const selectedProducts = ref<ProductItem[]>([])
const selectedCategoryId = ref<number | null>(null)
const matrixQtyMap = ref<Record<number, number>>({})
const PRODUCT_SELECT_VIEW_KEY = 'sales_order_add_select_product'

const categoryTree = ref([
  {
    id: 0,
    label: '全部分类',
    children: [
      { id: 1, label: '上衣' },
      { id: 2, label: '裤子' },
      { id: 3, label: '快选添加' }
    ]
  }
])

const addProductVisible = ref(false)
const addProductForm = reactive({
  name: '',
  category: '上衣',
  attributes: '',
  barcode: '',
  productCode: '',
  spec: '',
  brand: '',
  unit: '件',
  wholesalePrice: 0,
  purchasePrice: 0,
  retailPrice: 0,
  remark: ''
})

const confirmAddProduct = () => {
  if (!addProductForm.name) {
    ElMessage.warning('请输入商品名称')
    return
  }
  const newProduct: ProductItem = {
    id: Date.now(),
    name: addProductForm.name,
    attributes: addProductForm.attributes,
    spec: addProductForm.spec || '—',
    brand: addProductForm.brand || '无',
    unit: addProductForm.unit || '件',
    wholesalePrice: String(addProductForm.wholesalePrice),
    purchasePrice: String(addProductForm.purchasePrice),
    retailPrice: String(addProductForm.retailPrice),
    stock: 0,
    category: addProductForm.category,
    barcode: addProductForm.barcode || `SP${Date.now()}`,
    productCode: addProductForm.productCode || `SP${Date.now()}`,
    defaultWarehouse: '主仓库',
    location: '',
    exchangePoints: 0,
    stockUnit: addProductForm.unit || '件',
    stockValue: '0.00',
    remark: addProductForm.remark,
    selectedQty: 1
  }
  allProducts.value.unshift(newProduct)
  ElMessage.success('添加商品成功')
  addProductVisible.value = false
  Object.assign(addProductForm, {
    name: '',
    category: '上衣',
    attributes: '',
    barcode: '',
    productCode: '',
    spec: '',
    brand: '',
    unit: '件',
    wholesalePrice: 0,
    purchasePrice: 0,
    retailPrice: 0,
    remark: ''
  })
}

const productSearch = reactive({
  keyword: ''
})

const productPager = reactive({
  page: 1,
  pageSize: 30
})

const allProducts = ref<ProductItem[]>([
  { id: 1, name: '小冰棉', attributes: '黑色|2XL', spec: '—', brand: '无', unit: '件', wholesalePrice: '9', purchasePrice: '8', retailPrice: '12', stock: 0, category: '冰丝塑袖', barcode: '000000018', productCode: 'SP0000018', defaultWarehouse: '主仓库', location: 'A-01', exchangePoints: 0, stockUnit: '件', stockValue: '0.00', remark: '', selectedQty: 1 },
  { id: 2, name: '小冰棉', attributes: '黑色|3XL', spec: '—', brand: '无', unit: '件', wholesalePrice: '9', purchasePrice: '8', retailPrice: '12', stock: 0, category: '冰丝塑袖', barcode: '000000018', productCode: 'SP0000018', defaultWarehouse: '主仓库', location: 'A-01', exchangePoints: 0, stockUnit: '件', stockValue: '0.00', remark: '', selectedQty: 1 },
  { id: 3, name: '小冰棉', attributes: '白色|2XL', spec: '—', brand: '无', unit: '件', wholesalePrice: '9', purchasePrice: '8', retailPrice: '12', stock: -10, category: '冰丝塑袖', barcode: '000000018', productCode: 'SP0000018', defaultWarehouse: '主仓库', location: 'A-01', exchangePoints: 0, stockUnit: '件', stockValue: '0.00', remark: '', selectedQty: 1 }
])

type ColumnConfig = {
  prop: string
  originalLabel: string
  label: string
  visible: boolean
  type?: string
  width?: number
  minWidth?: number
}

const productColumns = ref<ColumnConfig[]>([
  { prop: 'selection', originalLabel: '', label: '', visible: true, type: 'selection', width: 55 },
  { prop: 'selectedQty', originalLabel: '数量', label: '数量', visible: true, width: 80 },
  { prop: 'image', originalLabel: '商品图像', label: '图像', visible: true, width: 70 },
  { prop: 'name', originalLabel: '商品名称', label: '商品名称', visible: true, minWidth: 120 },
  { prop: 'attributes', originalLabel: '辅助属性', label: '辅助属性', visible: true, width: 120 },
  { prop: 'productCode', originalLabel: '商品编号', label: '商品编号', visible: false, width: 110 },
  { prop: 'spec', originalLabel: '规格型号', label: '规格型号', visible: true, width: 120 },
  { prop: 'brand', originalLabel: '商品品牌', label: '品牌', visible: true, width: 90 },
  { prop: 'unit', originalLabel: '单位', label: '单位', visible: true, width: 70 },
  { prop: 'wholesalePrice', originalLabel: '批发价', label: '批发价', visible: true, width: 110 },
  { prop: 'retailPrice', originalLabel: '零售价', label: '零售价格', visible: false, width: 110 },
  { prop: 'stock', originalLabel: '可用库存', label: '库存', visible: true, width: 80 },
  { prop: 'category', originalLabel: '商品分类', label: '分类', visible: true, width: 110 },
  { prop: 'barcode', originalLabel: '条形码', label: '条形码', visible: true, width: 120 },
  { prop: 'defaultWarehouse', originalLabel: '默认仓库', label: '默认仓库', visible: false, width: 110 },
  { prop: 'location', originalLabel: '商品货位', label: '商品货位', visible: false, width: 100 },
  { prop: 'exchangePoints', originalLabel: '兑换积分', label: '兑换积分', visible: false, width: 100 },
  { prop: 'stockUnit', originalLabel: '库存单位', label: '库存单位', visible: false, width: 100 },
  { prop: 'stockValue', originalLabel: '库存价值', label: '库存价值', visible: false, width: 100 },
  { prop: 'remark', originalLabel: '备注信息', label: '备注信息', visible: true, width: 120 }
])

const productSettingVisible = ref(false)
const productSettingColumns = ref<ColumnConfig[]>([])
const productSettingTableRef = ref<any>(null)
let productSortable: Sortable | null = null
let productSettingBeforeDrag: ColumnConfig[] = []

const isProductColumnLocked = (col: ColumnConfig) => {
  return col.prop === 'selection' || col.prop === 'selectedQty' || col.prop === 'name'
}

const refreshProductColumns = async () => {
  const res = await getViewConfig(PRODUCT_SELECT_VIEW_KEY)
  const cols = (res.data.columns || []) as ColumnConfig[]
  if (cols.length) {
    productColumns.value = JSON.parse(JSON.stringify(cols))
  }
}

const openProductSetting = async () => {
  const res = await getViewConfig(PRODUCT_SELECT_VIEW_KEY)
  productSettingColumns.value = JSON.parse(JSON.stringify(res.data.columns || productColumns.value))
  productSettingVisible.value = true

  nextTick(() => {
    const tbody = productSettingTableRef.value?.$el?.querySelector('.el-table__body-wrapper tbody')
    if (!tbody) return
    if (productSortable) productSortable.destroy()

    productSortable = Sortable.create(tbody, {
      handle: '.drag-handle',
      animation: 150,
      onStart: () => {
        productSettingBeforeDrag = JSON.parse(JSON.stringify(productSettingColumns.value))
      },
      onEnd: ({ newIndex, oldIndex }) => {
        if (newIndex === undefined || oldIndex === undefined) return
        const moving = productSettingColumns.value[oldIndex]
        const target = productSettingColumns.value[newIndex]
        if (!moving || !target) return
        if (isProductColumnLocked(moving) || isProductColumnLocked(target)) {
          productSettingColumns.value = productSettingBeforeDrag
          return
        }
        const row = productSettingColumns.value.splice(oldIndex, 1)[0]
        productSettingColumns.value.splice(newIndex, 0, row)
      }
    })
  })
}

const saveProductSetting = async () => {
  await saveViewConfig(PRODUCT_SELECT_VIEW_KEY, productSettingColumns.value)
  await refreshProductColumns()
  productSettingVisible.value = false
  ElMessage.success('保存成功')
}

const filteredProducts = computed(() => {
  const keyword = productSearch.keyword.trim()
  let list = allProducts.value

  if (selectedCategoryId.value !== null && selectedCategoryId.value !== 0) {
    const node = categoryTree.value[0].children.find(c => c.id === selectedCategoryId.value)
    if (node) {
      list = list.filter(p => p.category === node.label)
    }
  }

  if (keyword) {
    list = list.filter(p => {
      const haystack = `${p.name} ${p.attributes} ${p.spec} ${p.barcode} ${p.category}`.toLowerCase()
      return haystack.includes(keyword.toLowerCase())
    })
  }

  return list
})

const onProductsUpdate = (updated: ProductItem[]) => {
  allProducts.value = updated
}

const openSelectProduct = () => {
  selectProductVisible.value = true
}

const onProductSelectionChange = (rows: ProductItem[]) => {
  selectedProducts.value = rows
}

const onCategoryClick = (node: any) => {
  selectedCategoryId.value = node?.id ?? null
}

const confirmSelectProducts = () => {
  const parseAttributes = (attributes: string) => {
    const [color, size] = String(attributes || '').split('|')
    return { color: (color || '').trim(), size: (size || '').trim() }
  }

  if (enableSizeColor.value) {
    const selectedEntries = Object.entries(matrixQtyMap.value).filter(([, v]) => (Number(v) || 0) > 0)
    if (!selectedEntries.length) {
      selectProductVisible.value = false
      return
    }

    const selectedSkus = selectedEntries
      .map(([id, qty]) => ({ p: allProducts.value.find(x => x.id === Number(id)), qty: Number(qty) || 0 }))
      .filter(x => !!x.p && x.qty > 0) as Array<{ p: ProductItem; qty: number }>

    const sizes: string[] = []
    selectedSkus.forEach(({ p }) => {
      const { size } = parseAttributes(p.attributes)
      if (size && !sizes.includes(size)) sizes.push(size)
    })
    sizeLabels.value = sizes
    formData.details.forEach(ensureRowCols)

    const groups = new Map<string, { name: string; color: string; unitPrice: string; sizeQty: Record<string, number> }>()
    selectedSkus.forEach(({ p, qty }) => {
      const { color, size } = parseAttributes(p.attributes)
      const key = `${p.name}||${color}`
      const unitPrice = formData.priceType === 'retail' ? p.retailPrice : p.wholesalePrice
      if (!groups.has(key)) {
        groups.set(key, { name: p.name, color, unitPrice, sizeQty: {} })
      }
      const g = groups.get(key)!
      g.sizeQty[size] = (g.sizeQty[size] || 0) + qty
    })

    Array.from(groups.values()).forEach(g => {
      const productInfo = `${g.name}`.trim()
      // 查找是否已经存在相同的商品和颜色
      let targetRow = formData.details.find((r: any) => r.productInfo === productInfo && r.color === g.color)
      
      if (!targetRow) {
        const emptyIndex = formData.details.findIndex((r: any) => !String(r.productInfo || '').trim())
        targetRow = emptyIndex >= 0 ? formData.details[emptyIndex] : generateEmptyRows(1)[0]
        if (emptyIndex < 0) {
          formData.details.push(targetRow)
        }
      }

      ensureRowCols(targetRow)
      targetRow.productInfo = productInfo
      targetRow.color = g.color
      targetRow.unitPrice = g.unitPrice
      
      Object.entries(g.sizeQty).forEach(([size, q]) => {
        const idx = sizeLabels.value.indexOf(size)
        if (idx >= 0) {
          targetRow[`col${idx + 1}`] = q ? String(q) : ''
        }
      })
      updateRowQty(targetRow)
    })

    matrixQtyMap.value = {}
    selectProductVisible.value = false
    return
  }

  if (!selectedProducts.value.length) {
    selectProductVisible.value = false
    return
  }

  selectedProducts.value.forEach(p => {
    const emptyIndex = formData.details.findIndex((r: any) => !String(r.productInfo || '').trim())
    const row: any = emptyIndex >= 0 ? formData.details[emptyIndex] : generateEmptyRows(1)[0]

    row.productInfo = `${p.name} ${p.attributes}`
    row.unitPrice = formData.priceType === 'retail' ? p.retailPrice : p.wholesalePrice
    row.qty = String(p.selectedQty)
    for (let i = 1; i <= sizeColumnCount.value; i++) {
      row[`col${i}`] = ''
    }

    if (emptyIndex < 0) {
      formData.details.push(row)
    }
  })

  selectProductVisible.value = false
}
</script>

<style scoped>
.add-booking-container {
  height: 100%;
}
.box-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  border-radius: 0;
}
:deep(.el-card__body) {
  padding: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 顶部操作栏 */
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 44px;
  padding: 0 16px;
  border-bottom: 1px solid #e6e6e6;
  background: #fff;
}
.left-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}
.page-title {
  font-size: 16px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 5px;
  margin-right: 15px;
}
.price-type {
  margin-left: 20px;
  display: flex;
  align-items: center;
}
.price-type .label {
  font-size: 14px;
  color: #606266;
  margin-right: 10px;
}

/* 基础信息区 */
.basic-info-bar {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 8px 16px;
  border-bottom: 1px solid #ebeef5;
  background: #fff;
}
.info-item {
  display: flex;
  align-items: center;
  gap: 10px;
}
.info-item .label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}
.customer-item {
  font-weight: bold;
}

.customer-select-wrapper {
  position: relative;
  display: inline-flex;
  align-items: center;
}

.quick-add-btn {
  position: absolute;
  right: 6px;
  top: 50%;
  transform: translateY(-50%);
  height: 28px;
  padding: 0 10px;
  border-radius: 14px;
  font-size: 12px;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.15s ease;
  z-index: 2;
}

.customer-select-wrapper:hover .quick-add-btn {
  opacity: 1;
  pointer-events: auto;
}

.customer-select-wrapper :deep(.el-input__wrapper) {
  padding-right: 90px;
}

.customer-dropdown-header {
  padding: 8px 10px 6px;
  border-bottom: 1px solid #ebeef5;
}

.customer-dropdown-footer {
  padding: 8px 10px 10px;
  border-top: 1px solid #ebeef5;
}

.customer-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 6px;
  padding-bottom: 8px;
}

.customer-actions :deep(.el-button.is-link) {
  padding: 0;
  font-size: 14px;
}

.customer-pager {
  display: flex;
  align-items: center;
  gap: 8px;
}

.customer-total {
  color: #909399;
  font-size: 13px;
  margin-left: 6px;
}

.add-customer-body {
  padding: 12px 10px 0;
}

:deep(.customer-select-popper.el-popper) {
  border-radius: 6px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
}

:deep(.customer-select-popper .el-select-dropdown__list) {
  padding: 6px 0;
}

:deep(.customer-select-popper .el-select-dropdown__item) {
  height: 44px;
  line-height: 44px;
  font-size: 16px;
}

:deep(.customer-select-popper .el-select-dropdown__item.selected) {
  font-weight: 600;
}

/* 表格区 */
.detail-table-area {
  flex: 1;
  overflow: auto;
  background: #fff;
}
.action-icon {
  font-size: 18px;
  cursor: pointer;
  margin: 0 3px;
}
.add-icon { color: #67c23a; }
.remove-icon { color: #f56c6c; }
.qty-input :deep(.el-input__wrapper) {
  padding: 0 2px;
}
.qty-input :deep(.el-input__inner) {
  text-align: center;
}

.product-selector-input :deep(.el-input-group__append) {
  background-color: #2ec45c;
  color: white;
  border: 1px solid #2ec45c;
  padding: 0 15px;
  cursor: pointer;
  display: none;
}
.product-selector-input.is-focus :deep(.el-input-group__append),
.product-selector-input:hover :deep(.el-input-group__append) {
  display: flex;
}
.product-selector-input :deep(.el-input-group__append .el-button) {
  color: white;
  border: none;
  background: transparent;
  padding: 0;
  margin: 0;
}
.product-selector-input :deep(.el-input-group__append:hover) {
  opacity: 0.9;
}

/* 底部汇总区 */
.footer-area {
  display: flex;
  justify-content: space-between;
  border-top: 1px solid #ebeef5;
  padding: 12px 16px;
  background: #fff;
}
.footer-left {
  flex: 1;
  max-width: 400px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.form-row {
  display: flex;
  align-items: center;
  gap: 10px;
}
.form-row .label {
  width: 60px;
  color: #606266;
  font-size: 14px;
}
.upload-area {
  margin-top: 10px;
}
.upload-btn {
  width: 80px;
  height: 80px;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  cursor: pointer;
  font-size: 12px;
}
.upload-btn:hover {
  border-color: #409eff;
  color: #409eff;
}

.footer-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 15px;
}
.summary-row {
  display: flex;
  align-items: center;
  gap: 15px;
}
.summary-item {
  display: flex;
  align-items: center;
  gap: 8px;
}
.summary-item .label {
  color: #606266;
  font-size: 14px;
}
.total-row {
  justify-content: flex-end;
  width: 100%;
}
.total-amount {
  font-size: 14px;
  color: #303133;
}
.amount-value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}
.action-row {
  justify-content: flex-end;
  width: 100%;
}

.add-booking-container :deep(.el-table__header-wrapper th) {
  background: #f5f7fa;
  color: #606266;
  font-weight: 600;
}
.add-booking-container :deep(.el-table__row td) {
  padding: 6px 0;
}
.add-booking-container :deep(.el-input__wrapper) {
  box-shadow: none;
}
.add-booking-container :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset;
}

.select-product-dialog :deep(.el-dialog__header) {
  padding: 0;
  margin: 0;
}
.select-product-dialog :deep(.el-dialog) {
  border-radius: 0;
}
.select-product-dialog :deep(.el-dialog__body) {
  padding: 0;
}
.select-product-dialog :deep(.el-dialog__footer) {
  padding: 10px 16px;
  border-top: 1px solid #ebeef5;
}
.select-product-header {
  height: 44px;
  background-color: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  font-size: 14px;
}
.dialog-close {
  cursor: pointer;
}
.select-product-body {
  padding: 0;
}
.category-aside {
  border-right: 1px solid #ebeef5;
  padding: 10px;
  background: #fff;
}
.category-title {
  font-weight: bold;
  margin-bottom: 10px;
}
.product-main {
  padding: 10px;
  background: #fff;
}
.product-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}
.product-main :deep(.el-table__header-wrapper th) {
  background: #f5f7fa;
  color: #606266;
  font-weight: 600;
}
.product-main :deep(.el-table__row td) {
  padding: 6px 0;
  font-size: 12px;
}
.product-main :deep(.el-table .cell) {
  white-space: nowrap;
}
.product-main :deep(.el-input-number--small .el-input__wrapper) {
  padding-left: 6px;
  padding-right: 6px;
}
.toolbar-left, .toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}
.product-pagination {
  margin-top: 10px;
  display: flex;
  justify-content: flex-start;
}
.select-product-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.product-setting-dialog :deep(.el-dialog__body) {
  padding-top: 10px;
  padding-bottom: 10px;
}

.add-customer-dialog :deep(.el-dialog) {
  border-radius: 4px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.add-customer-dialog :deep(.el-dialog__body) {
  padding: 15px;
  background-color: #f0f2f5;
  max-height: 65vh;
  overflow-y: auto;
}

.add-customer-dialog :deep(.el-dialog__footer) {
  padding: 15px 0;
  background-color: #fff;
  border-top: 1px solid #ebeef5;
}

.add-customer-dialog .dialog-footer {
  justify-content: center;
  gap: 20px;
}

.add-customer-dialog .dialog-footer .el-button {
  width: 90px;
  height: 36px;
  border-radius: 18px;
  font-size: 14px;
}

.add-customer-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-section {
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0,21,41,.05);
  overflow: hidden;
  border: none;
}

.section-title {
  font-weight: bold;
  font-size: 15px;
  color: #303133;
  padding: 12px 20px;
  border-bottom: 1px solid #ebeef5;
}

.section-body {
  padding: 20px 20px 5px;
}

.form-row-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  column-gap: 40px;
  align-items: start;
}

.span-2 {
  grid-column: span 2;
}

.inline-with-link {
  display: flex;
  align-items: center;
  gap: 15px;
  width: 100%;
}

.inline-link {
  font-size: 14px;
  white-space: nowrap;
}

.required-label {
  color: #606266;
  font-size: 14px;
  font-weight: normal;
}

.required-label::before {
  content: '*';
  color: #f56c6c;
  margin-right: 4px;
}

.warning-label {
  color: #e6a23c;
  font-size: 14px;
  font-weight: bold;
}

.add-customer-form :deep(.el-form-item) {
  margin-bottom: 20px;
  align-items: center;
}

.add-customer-form :deep(.el-form-item__label) {
  font-size: 14px;
  font-weight: normal;
  color: #606266;
  padding-right: 12px;
}

.add-customer-form :deep(.el-input__wrapper),
.add-customer-form :deep(.el-select__wrapper),
.add-customer-form :deep(.el-date-editor.el-input__wrapper) {
  min-height: 32px;
  border: none;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  border-radius: 4px;
}

.add-customer-form :deep(.el-input__wrapper.is-focus),
.add-customer-form :deep(.el-select__wrapper.is-focused) {
  box-shadow: 0 0 0 1px #409eff inset;
}

.add-customer-form :deep(.el-input__inner),
.add-customer-form :deep(.el-select__placeholder),
.add-customer-form :deep(.el-select__selected-item),
.add-customer-form :deep(.el-radio__label) {
  font-size: 14px;
}

.add-customer-form :deep(.el-radio-group) {
  display: flex;
  align-items: center;
  gap: 18px;
}
</style>
