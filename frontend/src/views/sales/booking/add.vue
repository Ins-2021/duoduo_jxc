<template>
  <div class="add-booking-container">
    <el-card class="box-card">
      <div class="top-bar">
        <div class="left-actions">
          <span class="page-title"><el-icon><Tickets /></el-icon> 销售预定单</span>
          <el-button type="primary" size="small" @click="openSelectProduct">选择商品</el-button>
          <el-button size="small" @click="handleNotImplemented">导入商品</el-button>
          <el-button size="small" @click="handleNotImplemented">扫码录入</el-button>
          <el-button size="small" @click="handleNotImplemented">选择模板</el-button>
          
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
          <el-button @click="handleNotImplemented">历史单据</el-button>
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
              <el-input 
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
                <el-radio-group v-model="formData.priceType" size="small" style="margin-left: 12px;">
                  <el-radio-button label="wholesale">批发价</el-radio-button>
                  <el-radio-button label="retail">零售价</el-radio-button>
                </el-radio-group>
              </div>
              <div class="toolbar-right">
                <el-input v-model="productSearch.keyword" placeholder="搜索编号、名称、规格、条码、属性、备注" clearable style="width: 320px" @keyup.enter="onSearchProducts" />
                <el-button type="primary" size="small" @click="onSearchProducts">查询</el-button>
                <el-button size="small" @click="toggleAdvancedSearch">{{ productSearch.advanced.visible ? '收起搜索' : '高级搜索' }}</el-button>
              </div>
            </div>

            <div v-if="productSearch.advanced.visible" class="advanced-search-panel">
              <el-form :inline="true" size="small" label-width="70px">
                <el-form-item label="商品分类">
                  <el-select v-model="productSearch.advanced.categoryId" placeholder="全部分类" clearable style="width: 150px">
                    <el-option v-for="cat in flatCategories" :key="cat.id" :label="cat.label" :value="cat.id" />
                  </el-select>
                </el-form-item>
                <el-form-item label="品牌">
                  <el-input v-model="productSearch.advanced.brand" placeholder="请输入品牌" clearable style="width: 140px" />
                </el-form-item>
                <el-form-item label="价格范围">
                  <el-input v-model="productSearch.advanced.minPrice" placeholder="最低价" clearable style="width: 90px" />
                  <span style="margin: 0 4px; color: #999;">~</span>
                  <el-input v-model="productSearch.advanced.maxPrice" placeholder="最高价" clearable style="width: 90px" />
                </el-form-item>
                <el-form-item label="仅看有货">
                  <el-checkbox v-model="productSearch.advanced.hasStock" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="onSearchProducts">搜索</el-button>
                  <el-button @click="resetAdvancedSearch">重置</el-button>
                </el-form-item>
              </el-form>
            </div>

            <SizeColorMatrixPicker v-if="enableSizeColor" :products="filteredProducts" v-model="matrixQtyMap" :columns="productColumns" @update:products="onProductsUpdate" />
            <el-table
              v-else
              ref="productTableRef"
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
                :total="productTotal"
                @current-change="fetchProducts"
                @size-change="onPageSizeChange"
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
            <el-select v-model="addProductForm.categoryId" placeholder="请选择分类" style="width: 100%">
              <el-option v-for="cat in flatCategories" :key="cat.id" :label="cat.label" :value="cat.id" />
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
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import Sortable from 'sortablejs'
import { createSalesOrder, getSalesOrderDetail, updateSalesOrder } from '@/api/sales'
import { getSystemSettings } from '@/api/settings'
import { getCustomerOptions } from '@/api/options'
import { getProductSkuPage, getProductCategoryTree, addProduct as addProductApi } from '@/api/product'
import type { ProductSkuSelectDTO } from '@/types'
import SizeColorMatrixPicker from '@/components/SizeColorMatrixPicker.vue'

const handleRefresh = () => {
  window.location.reload()
}

const handleNotImplemented = () => {
  ElMessage.info('功能即将上线，敬请期待')
}

const router = useRouter()
const route = useRoute()
const isEdit = computed(() => !!route.params.id)
const editingOrderId = computed(() => route.params.id ? Number(route.params.id) : null)

const enableSizeColor = ref(false)
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

watch(
  () => formData.priceType,
  (type) => {
    const wholesaleCol = productColumns.value.find(c => c.prop === 'wholesalePrice')
    const retailCol = productColumns.value.find(c => c.prop === 'retailPrice')
    if (wholesaleCol) wholesaleCol.visible = type === 'wholesale'
    if (retailCol) retailCol.visible = type === 'retail'
  }
)

const fetchCustomers = async () => {
  try {
    const res = await getCustomerOptions()
    customers.value = res.data.map((c: any) => ({ id: c.value, name: c.label }))
  } catch (e) {
    console.error(e)
  }
}

const loadOrderData = async (orderId: number) => {
  try {
    const res = await getSalesOrderDetail(orderId)
    if (res?.data) {
      const order = res.data
      formData.customer = order.customerId || ''
      formData.date = order.docDate ? new Date(order.docDate) : new Date()
      formData.orderNo = order.docNo || '自动生成'
      formData.remark = order.remark || ''
      formData.discountRate = 100
      formData.otherFee = order.otherFee || 0
      formData.deposit = order.paidAmount || 0
      formData.priceType = 'wholesale'

      if (order.details?.length) {
        if (enableSizeColor.value) {
          // 服装批发格式：按 商品名称 + 颜色 分组，尺码分布到列
          const sizes: string[] = []
          const groups = new Map<string, { name: string; color: string; unitPrice: string; sizeQty: Record<string, number>; skuMap: Map<number, number> }>()

          order.details.forEach((d: any) => {
            const spuName = d.spuName || ''
            const color = (d.attr1 || '').trim()
            const size = (d.attr2 || '').trim()
            const key = `${spuName}||${color}`

            if (!groups.has(key)) {
              groups.set(key, { name: spuName, color, unitPrice: d.unitPrice || '', sizeQty: {}, skuMap: new Map() })
            }
            const g = groups.get(key)!
            g.sizeQty[size] = d.qty || 0
            if (d.skuId) g.skuMap.set(d.skuId, d.qty || 0)
            if (size && !sizes.includes(size)) sizes.push(size)
          })

          sizeLabels.value = sizes.length ? sizes : defaultSizeColumnLabels
          formData.details = []

          Array.from(groups.values()).forEach(g => {
            const row = generateEmptyRows(1)[0]
            row.productInfo = g.name
            row.color = g.color
            row.unitPrice = g.unitPrice
            row.spuId = undefined // SKU 级别，SPU 无法精确映射

            Object.entries(g.sizeQty).forEach(([size, qty]) => {
              const idx = sizeLabels.value.indexOf(size)
              if (idx >= 0) {
                row[`col${idx + 1}`] = qty ? String(qty) : ''
              }
            })
            updateRowQty(row)
            formData.details.push(row)
          })

          // 补齐空行
          while (formData.details.length < 8) {
            formData.details.push(...generateEmptyRows(1))
          }
        } else {
          // 普通模式：每个 SKU 一行
          formData.details = order.details.map((d: any) => ({
            detailId: d.detailId,
            skuId: d.skuId,
            spuId: d.spuId,
            productInfo: `${d.spuName || ''} ${d.attr1 || ''}${d.attr1 && d.attr2 ? '|' : ''}${d.attr2 || ''}`.trim(),
            color: '',
            unitPrice: d.unitPrice || '',
            qty: d.qty || '',
            lineAmount: d.lineAmount || '',
            discountAmount: d.discountAmount || '',
          }))
          while (formData.details.length < 8) {
            formData.details.push(...generateEmptyRows(1))
          }
        }
      }
    }
  } catch (e) {
    console.error('加载订单数据失败', e)
    ElMessage.error('加载订单数据失败')
  }
}

onMounted(async () => {
  try {
    const res = await getSystemSettings()
    enableSizeColor.value = !!res?.data?.enableSizeColor || res?.data?.printClothesWholesaleLayout === '是'
  } catch {
    enableSizeColor.value = false
  }
  await fetchCustomers()
  await fetchCategories()
  await fetchProducts()
  if (isEdit.value && editingOrderId.value) {
    await loadOrderData(editingOrderId.value)
  }
})

const calculateAmount = (row: any) => {
  const qty = Number(row.qty) || 0
  const price = Number(row.unitPrice) || 0
  return (qty * price).toFixed(2)
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
      const totalQty = data.reduce((sum: number, row: any) => sum + (Number(row.qty) || 0), 0)
      sums[index] = totalQty ? String(totalQty) : ''
    } else if (column.label === '金额(元)') {
      const totalAmt = data.reduce((sum: number, row: any) => {
        return sum + (Number(row.qty) || 0) * (Number(row.unitPrice) || 0)
      }, 0)
      sums[index] = totalAmt ? totalAmt.toFixed(2) : ''
    } else {
      sums[index] = ''
    }
  })
  return sums
}

const finalTotalAmount = computed(() => {
  const totalAmt = formData.details.reduce((sum: number, row: any) => {
    return sum + (Number(row.qty) || 0) * (Number(row.unitPrice) || 0)
  }, 0)
  
  const discounted = totalAmt * (formData.discountRate / 100)
  formData.discountedAmount = discounted.toFixed(2)
  
  const final = discounted + Number(formData.otherFee)
  return final.toFixed(2)
})

const saveOrder = async () => {
  const validDetails = formData.details.filter((item: any) => item.productInfo && Number(item.qty) > 0)
  if (validDetails.length === 0) {
    ElMessage.warning('请选择商品并填写数量')
    return
  }
  
  const submitData: any = {
    orderType: 3,
    docDate: formData.date,
    customerId: formData.customer ? Number(formData.customer) : undefined,
    remark: formData.remark,
    totalAmount: finalTotalAmount.value,
    discountAmount: (parseFloat(finalTotalAmount.value) - parseFloat(formData.discountedAmount)).toFixed(2),
    actualAmount: formData.discountedAmount,
    otherFee: formData.otherFee,
    paidAmount: formData.deposit,
    details: validDetails.map((item: any) => ({
      detailId: item.detailId || undefined,
      spuId: item.spuId || undefined,
      skuId: item.skuId || undefined,
      qty: Number(item.qty),
      unitPrice: Number(item.unitPrice),
      lineAmount: calculateAmount(item)
    }))
  }
  
  try {
    if (isEdit.value) {
      submitData.orderId = editingOrderId.value
      await updateSalesOrder(submitData)
      ElMessage.success('单据修改成功！')
    } else {
      await createSalesOrder(submitData)
      ElMessage.success('单据保存成功！')
    }
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
const selectedCategoryNode = ref<any>(null)
const matrixQtyMap = ref<Record<number, number>>({})
const productTotal = ref(0)
const productLoading = ref(false)
const productTableRef = ref<any>(null)
const selectedProductMap = ref<Map<number, ProductItem>>(new Map())

const categoryTree = ref<any[]>([])

const flatCategories = computed(() => {
  const result: { id: number; label: string }[] = []
  const walk = (nodes: any[]) => {
    for (const node of nodes) {
      result.push({ id: node.id, label: node.label })
      if (node.children?.length) walk(node.children)
    }
  }
  if (categoryTree.value.length && categoryTree.value[0].children) {
    walk(categoryTree.value[0].children)
  }
  return result
})

const addProductVisible = ref(false)
const addProductForm = reactive({
  name: '',
  categoryId: undefined as number | undefined,
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

const confirmAddProduct = async () => {
  if (!addProductForm.name) {
    ElMessage.warning('请输入商品名称')
    return
  }
  try {
    await addProductApi({
      spuName: addProductForm.name,
      categoryId: addProductForm.categoryId,
      spec: addProductForm.spec || undefined,
      unit: addProductForm.unit || undefined,
      brand: addProductForm.brand || undefined,
      wholesalePrice: addProductForm.wholesalePrice || undefined,
      purchasePrice: addProductForm.purchasePrice || undefined,
      retailPrice: addProductForm.retailPrice || undefined,
      remark: addProductForm.remark || undefined,
    } as any)
    ElMessage.success('添加商品成功')
    addProductVisible.value = false
    Object.assign(addProductForm, {
      name: '',
      categoryId: undefined,
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
    await fetchProducts()
  } catch (e) {
    console.error(e)
  }
}

const productSearch = reactive({
  keyword: '',
  advanced: {
    visible: false,
    categoryId: undefined as number | undefined,
    brand: '',
    minPrice: '',
    maxPrice: '',
    hasStock: false
  }
})

const productPager = reactive({
  page: 1,
  pageSize: 30
})

const allProducts = ref<ProductItem[]>([])

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

const openProductSetting = () => {
  productSettingColumns.value = JSON.parse(JSON.stringify(productColumns.value))
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

const saveProductSetting = () => {
  productColumns.value = JSON.parse(JSON.stringify(productSettingColumns.value))
  productSettingVisible.value = false
}

const filteredProducts = computed(() => allProducts.value)

const fetchCategories = async () => {
  try {
    const res = await getProductCategoryTree()
    if (res?.data) {
      categoryTree.value = [
        { id: 0, label: '全部分类', children: res.data }
      ]
    }
  } catch (e) {
    console.error('获取商品分类失败', e)
  }
}

const fetchProducts = async () => {
  productLoading.value = true
  try {
    const params: any = {
      pageNum: productPager.page,
      pageSize: productPager.pageSize,
    }
    if (selectedCategoryId.value && selectedCategoryId.value !== 0) {
      // 收集当前分类及所有子分类 ID，实现级联分类查询
      if (selectedCategoryNode.value && selectedCategoryNode.value.children?.length) {
        params.categoryIds = collectCategoryIds(selectedCategoryNode.value)
      } else {
        params.categoryId = selectedCategoryId.value
      }
    }
    const keyword = productSearch.keyword.trim()
    if (keyword) {
      params.keyword = keyword
    }
    // 高级搜索参数
    const adv = productSearch.advanced
    if (adv.categoryId) {
      params.categoryId = adv.categoryId
    }
    if (adv.brand?.trim()) {
      params.brand = adv.brand.trim()
    }
    if (adv.minPrice !== '') {
      params.minPrice = Number(adv.minPrice)
    }
    if (adv.maxPrice !== '') {
      params.maxPrice = Number(adv.maxPrice)
    }
    if (adv.hasStock) {
      params.hasStock = true
    }
    const res = await getProductSkuPage(params)
    if (res?.data) {
      allProducts.value = (res.data.list || []).map((p: ProductSkuSelectDTO) => ({
        id: p.id,
        spuId: p.spuId,
        name: p.name || '',
        attributes: p.attributes || '',
        spec: p.spec || '',
        brand: p.brand || '',
        unit: p.unit || '',
        wholesalePrice: p.wholesalePrice != null ? String(p.wholesalePrice) : '0',
        purchasePrice: p.purchasePrice != null ? String(p.purchasePrice) : '0',
        retailPrice: p.retailPrice != null ? String(p.retailPrice) : '0',
        stock: p.stock ?? 0,
        category: p.category || '',
        barcode: p.barcode || '',
        productCode: p.productCode || '',
        defaultWarehouse: p.defaultWarehouse || '',
        location: p.location || '',
        exchangePoints: p.exchangePoints || 0,
        stockUnit: p.stockUnit || '',
        stockValue: p.stockValue != null ? String(p.stockValue) : '0',
        remark: p.remark || '',
        selectedQty: 1,
      }))
      productTotal.value = res.data.total || 0
      // 翻页后恢复之前已勾选的商品
      nextTick(() => {
        restoreProductSelection()
      })
    }
  } catch (e) {
    console.error('获取商品列表失败', e)
  } finally {
    productLoading.value = false
  }
}

const onSearchProducts = () => {
  productPager.page = 1
  fetchProducts()
}

const onPageSizeChange = () => {
  productPager.page = 1
  fetchProducts()
}

const toggleAdvancedSearch = () => {
  productSearch.advanced.visible = !productSearch.advanced.visible
}

const resetAdvancedSearch = () => {
  productSearch.advanced.categoryId = undefined
  productSearch.advanced.brand = ''
  productSearch.advanced.minPrice = ''
  productSearch.advanced.maxPrice = ''
  productSearch.advanced.hasStock = false
  productPager.page = 1
  fetchProducts()
}

const onProductsUpdate = (updated: ProductItem[]) => {
  allProducts.value = updated
}

const openSelectProduct = () => {
  selectProductVisible.value = true
  selectedProductMap.value.clear()
  selectedProducts.value = []
}

const onProductSelectionChange = (rows: ProductItem[]) => {
  selectedProducts.value = rows
  // 将当前页已勾选的商品同步到 map（保留其他页的勾选）
  const currentPageIds = new Set(allProducts.value.map(p => p.id))
  for (const [id] of selectedProductMap.value) {
    if (!currentPageIds.has(id)) {
      // 当前页不包含此商品，保留在 map 中
    }
  }
  // 先移除当前页所有项，再加入当前选中项
  for (const id of currentPageIds) {
    selectedProductMap.value.delete(id)
  }
  for (const row of rows) {
    selectedProductMap.value.set(row.id, row)
  }
}

const restoreProductSelection = () => {
  if (!productTableRef.value) return
  nextTick(() => {
    allProducts.value.forEach(p => {
      if (selectedProductMap.value.has(p.id)) {
        productTableRef.value.toggleRowSelection(p, true)
      }
    })
  })
}

const collectCategoryIds = (node: any): number[] => {
  const ids: number[] = [node.id]
  if (node.children?.length) {
    for (const child of node.children) {
      ids.push(...collectCategoryIds(child))
    }
  }
  return ids
}

const onCategoryClick = (node: any) => {
  selectedCategoryId.value = node?.id ?? null
  selectedCategoryNode.value = node?.id === 0 ? null : node
  productPager.page = 1
  fetchProducts()
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
    selectedProductMap.value.clear()
    selectProductVisible.value = false
    return
  }

  if (!selectedProductMap.value.size) {
    selectProductVisible.value = false
    return
  }

  Array.from(selectedProductMap.value.values()).forEach(p => {
    const emptyIndex = formData.details.findIndex((r: any) => !String(r.productInfo || '').trim())
    const row: any = emptyIndex >= 0 ? formData.details[emptyIndex] : generateEmptyRows(1)[0]

    row.productInfo = `${p.name} ${p.attributes}`
    row.unitPrice = formData.priceType === 'retail' ? p.retailPrice : p.wholesalePrice
    row.color = ''
    row.qty = String(p.selectedQty)
    for (let i = 1; i <= sizeColumnCount.value; i++) {
      row[`col${i}`] = ''
    }

    if (emptyIndex < 0) {
      formData.details.push(row)
    }
  })

  selectedProductMap.value.clear()
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
.advanced-search-panel {
  padding: 10px 12px;
  margin-bottom: 10px;
  background: #fafafa;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}
.advanced-search-panel .el-form-item {
  margin-bottom: 4px;
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
