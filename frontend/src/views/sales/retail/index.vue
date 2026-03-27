<template>
  <div class="retail-container" :class="{ 'fullscreen': isFullscreen }">
    <!-- 顶部导航栏 -->
    <div class="retail-header">
      <div class="header-left">
        <el-button link @click="goBack" class="header-btn">
          <el-icon><ArrowLeft /></el-icon> 返回
        </el-button>
        <el-button link @click="toggleFullscreen" class="header-btn">
          <el-icon><FullScreen /></el-icon> {{ isFullscreen ? '退出全屏 [U]' : '全屏 [U]' }}
        </el-button>
      </div>
      <div class="header-right">
        <el-button link @click="refreshData" class="header-btn">
          <el-icon><Refresh /></el-icon> 刷新 [R]
        </el-button>
        <el-button link @click="openSettings" class="header-btn">
          <el-icon><Setting /></el-icon> 设置 [S]
        </el-button>
        <el-button link @click="showShortcutsHelp = true" class="header-btn">
          <el-icon><QuestionFilled /></el-icon> 快捷键帮助 [F2]
        </el-button>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="retail-main">
      <!-- 左侧：购物车面板 -->
      <div class="cart-panel">
        <div class="cart-header">
          <div class="customer-select">
            <span class="label">客户：</span>
            <el-select v-model="customerId" placeholder="选择客户 [K]" style="width: 200px" filterable clearable>
              <el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </div>
          <div class="price-type">
            <el-select v-model="priceType" size="small" style="width: 100px;">
              <el-option label="零售价" value="retail" />
              <el-option label="批发价" value="wholesale" />
            </el-select>
            <el-button link class="clear-btn" @click="clearCart">
              <el-icon><Delete /></el-icon> 清空
            </el-button>
          </div>
        </div>

        <div class="order-info">
          <div class="info-row">
            <span class="label">业务日期：</span>
            <el-date-picker
              v-model="orderDate"
              type="datetime"
              size="small"
              style="width: 160px"
              value-format="YYYY-MM-DD HH:mm:ss"
            />
          </div>
          <div class="info-row">
            <span class="label">制单人：</span>
            <el-select v-model="creator" size="small" style="width: 120px">
              <el-option label="管理员" value="admin" />
            </el-select>
          </div>
        </div>

        <div class="cart-body">
          <div v-if="cartItems.length === 0" class="empty-cart">
            <el-icon class="empty-icon"><ShoppingCart /></el-icon>
            <p>扫码/点击购物商品，加入购物车结账</p>
          </div>
          <div v-else class="cart-items">
            <!-- 购物车项渲染 -->
            <div v-for="(item, index) in cartItems" :key="index" class="cart-item">
              <div class="item-name">{{ item.name }} ({{ item.skuName }})</div>
              <div class="item-price">¥{{ item.price.toFixed(2) }}</div>
              <div class="item-actions">
                <el-input-number v-model="item.qty" size="small" :min="1" />
                <el-button link type="danger" @click="removeItem(index)">
                  <el-icon><Close /></el-icon>
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <div class="cart-footer">
          <div class="remark-row">
            <el-button class="remark-btn">备注 B</el-button>
            <span class="points">积分：0</span>
          </div>
          <div class="summary-row">
            <div class="summary-item">
              <span class="label">件数：</span>
              <span class="value">{{ totalQty }}</span>
            </div>
            <div class="summary-item">
              <span class="label">合计：</span>
              <span class="value">{{ totalAmount }}</span>
            </div>
          </div>
          <div class="summary-row highlight">
            <div class="summary-item">
              <span class="label">优惠：</span>
              <span class="value text-danger">¥{{ discountAmount.toFixed(2) }}</span>
            </div>
            <div class="summary-item">
              <span class="label">应收：</span>
              <span class="value text-danger amount">¥{{ actualAmount.toFixed(2) }}</span>
              <el-icon class="edit-icon"><EditPen /></el-icon>
            </div>
          </div>
          <div class="action-row">
            <el-button class="action-btn" @click="fetchHoldOrders">取单 Q</el-button>
            <el-button class="action-btn" @click="holdOrder">挂单 G</el-button>
            <el-button type="primary" class="checkout-btn" @click="checkout">立即结算 SPACE</el-button>
          </div>
        </div>
      </div>

      <!-- 右侧：商品选择面板 -->
      <div class="product-panel">
        <div class="product-header">
          <el-input
            v-model="searchKeyword"
            placeholder="输入商品名称/简拼/货号/条形码 查询商品 [F]"
            class="search-input"
            ref="searchInputRef"
            clearable
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button icon="Menu" circle class="view-toggle-btn" />
        </div>

        <div class="product-body">
          <div class="product-grid">
            <div v-for="product in displayProducts" :key="product.id" class="product-card" @click="selectProduct(product)">
              <div class="product-img">
                <el-image v-if="settings.showImage && product.image" :src="product.image" fit="cover" />
                <div v-else class="img-placeholder">
                  <el-icon><Picture /></el-icon>
                </div>
              </div>
              <div class="product-info">
                <div class="product-name">{{ product.name }}</div>
                <div class="product-sku" v-if="product.skuCode">{{ product.skuCode }}</div>
                <div class="product-stock" v-if="settings.showStock">库存 {{ product.stock }} 件</div>
                <div class="product-price-row">
                  <span class="price">¥ {{ (priceType === 'retail' ? product.retailPrice : product.wholesalePrice).toFixed(2) }}</span>
                  <el-button v-if="product.hasMultiSku" type="primary" link class="select-sku-btn">选属性</el-button>
                  <el-button v-else type="primary" circle size="small" class="add-btn"><el-icon><Plus /></el-icon></el-button>
                </div>
              </div>
            </div>
          </div>
          
          <div class="category-sidebar">
            <div 
              v-for="cat in categories" 
              :key="cat.id" 
              class="category-item"
              :class="{ active: currentCategory === cat.id }"
              @click="currentCategory = cat.id"
            >
              {{ cat.name }}
            </div>
          </div>
        </div>

        <div class="product-footer">
          <el-button class="footer-btn">优惠 Y</el-button>
          <el-button class="footer-btn">抹零 M</el-button>
          <div class="barcode-scan">
            <el-icon><Crop /></el-icon> 请扫描商品条码或者串码 [C]
          </div>
        </div>
      </div>
    </div>

    <!-- 快捷键帮助弹窗 -->
    <el-dialog v-model="showShortcutsHelp" title="快捷键帮助" width="400px" center>
      <div class="shortcuts-list">
        <div class="shortcut-item"><span>将光标快速定位到搜索框中</span><strong>F</strong></div>
        <div class="shortcut-item"><span>将光标快速定位到扫码框中</span><strong>C</strong></div>
        <div class="shortcut-item"><span>高级搜索</span><strong>W</strong></div>
        <div class="shortcut-item"><span>开启关闭扫码</span><strong>O</strong></div>
        <div class="shortcut-item"><span>选择客户</span><strong>K</strong></div>
        <div class="shortcut-item"><span>挂单</span><strong>G</strong></div>
        <div class="shortcut-item"><span>取单</span><strong>Q</strong></div>
        <div class="shortcut-item"><span>优惠</span><strong>Y</strong></div>
        <div class="shortcut-item"><span>抹零</span><strong>M</strong></div>
        <div class="shortcut-item"><span>整单备注</span><strong>B</strong></div>
        <div class="shortcut-item"><span>系统设置</span><strong>S</strong></div>
        <div class="shortcut-item"><span>刷新页面</span><strong>R</strong></div>
        <div class="shortcut-item"><span>快捷键帮助</span><strong>F2</strong></div>
        <div class="shortcut-item"><span>立即结算</span><strong>SPACE 空格</strong></div>
        <div class="shortcut-item"><span>确认结算</span><strong>ENTER 回车</strong></div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showShortcutsHelp = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 收银设置弹窗 -->
    <el-dialog v-model="showSettings" title="收银设置" width="500px">
      <div class="settings-form">
        <h4 class="settings-title">基础设置</h4>
        <div class="settings-row">
          <span class="label">收银台标题</span>
          <el-input v-model="settings.title" style="width: 300px" clearable />
        </div>
        
        <h4 class="settings-title">商品显示</h4>
        <div class="settings-row">
          <span class="label">显示商品图片</span>
          <el-switch v-model="settings.showImage" />
        </div>
        <div class="settings-row">
          <span class="label">显示商品库存</span>
          <el-switch v-model="settings.showStock" />
        </div>
        <div class="settings-row">
          <span class="label">隐藏售罄商品</span>
          <el-switch v-model="settings.hideSoldOut" />
        </div>
        
        <h4 class="settings-title">开单设置</h4>
        <div class="settings-row">
          <span class="label">禁止零库存出库</span>
          <el-switch v-model="settings.forbidZeroStock" />
        </div>
        <div class="settings-row">
          <span class="label">默认客户</span>
          <el-select v-model="settings.defaultCustomer" placeholder="默认客户" style="width: 300px">
            <el-option label="散客" value="default" />
          </el-select>
        </div>
        <div class="settings-row">
          <span class="label">默认结算账户</span>
          <el-select v-model="settings.defaultAccount" placeholder="现金" style="width: 300px">
            <el-option label="现金" value="cash" />
            <el-option label="微信" value="wechat" />
            <el-option label="支付宝" value="alipay" />
          </el-select>
        </div>
        <div class="settings-row">
          <span class="label">默认仓库</span>
          <el-select v-model="settings.defaultWarehouse" placeholder="默认仓库" style="width: 300px">
            <el-option label="主仓库" value="1" />
          </el-select>
        </div>
        <div class="settings-row">
          <span class="label">开启积分</span>
          <el-switch v-model="settings.enablePoints" />
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="saveSettings">保存</el-button>
          <el-button @click="showSettings = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="showHoldOrderDialog" title="挂单列表" width="720px">
      <div v-if="heldOrders.length > 0" class="hold-order-list">
        <div v-for="order in heldOrders" :key="order.id" class="hold-order-item">
          <div class="hold-order-main">
            <div class="hold-order-title">
              <span>挂单 {{ order.createdAt }}</span>
              <span class="hold-order-customer">{{ order.customerName || '散客' }}</span>
            </div>
            <div class="hold-order-meta">
              <span>件数 {{ order.totalQty }}</span>
              <span>应收 ¥{{ Number(order.actualAmount || 0).toFixed(2) }}</span>
              <span>{{ order.items.length }} 个商品</span>
            </div>
          </div>
          <div class="hold-order-actions">
            <el-button type="primary" link @click="restoreHoldOrder(order.id)">取单</el-button>
            <el-button type="danger" link @click="removeHoldOrder(order.id)">删除</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无挂单数据" />
    </el-dialog>

    <!-- 多规格商品选择弹窗 -->
    <el-dialog v-model="showSkuSelect" title="选择商品" width="600px">
      <div v-if="currentSelectProduct" class="sku-select-container">
        <div class="sku-product-info">
          <div class="img-box">
            <el-icon class="placeholder"><Picture /></el-icon>
          </div>
          <div class="info">
            <div class="name">{{ currentSelectProduct.name }}</div>
            <div class="code">条形码：{{ currentSelectProduct.barcode || '-' }}</div>
            <div class="code">编号：{{ currentSelectProduct.id }}</div>
          </div>
        </div>
        
        <div class="sku-attrs">
          <div class="attr-group" v-if="attr1Options.length > 0">
            <div class="attr-name">规格1</div>
            <div class="attr-values">
              <div 
                v-for="val in attr1Options" 
                :key="val"
                class="attr-val"
                :class="{ active: selectedAttr1 === val }"
                @click="selectAttr1(val)"
              >
                {{ val }}
              </div>
            </div>
          </div>
          
          <div class="attr-group" v-if="attr2Options.length > 0">
            <div class="attr-name">规格2</div>
            <div class="attr-values">
              <div 
                v-for="val in attr2Options" 
                :key="val"
                class="attr-val"
                :class="{ active: selectedAttr2 === val }"
                @click="selectAttr2(val)"
              >
                {{ val }}
              </div>
            </div>
          </div>
        </div>

        <div v-if="currentSelectedSku" class="sku-selected-info">
          <span>条码 {{ currentSelectedSku.skuCode || '-' }}</span>
          <span>库存 {{ currentSelectedSku.stock || 0 }}</span>
          <span>售价 ¥{{ Number(priceType === 'retail' ? currentSelectedSku.retailPrice : currentSelectedSku.wholesalePrice).toFixed(2) }}</span>
        </div>
        
        <div class="sku-actions">
          <el-button type="primary" class="confirm-sku-btn" @click="confirmSkuSelection">我选好了 下一步</el-button>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showSkuSelect = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { 
  ArrowLeft, FullScreen, Refresh, Setting, QuestionFilled, 
  ArrowRight, Delete, ShoppingCart, Close, EditPen, 
  Search, Picture, Plus, Menu, Crop
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { createSalesOrder } from '@/api/sales'
import { getCustomerOptions } from '@/api/options'
import { getProductList, getProductCategoryTree } from '@/api/product'

const router = useRouter()
const searchInputRef = ref()

// 状态控制
const isFullscreen = ref(false)
const showShortcutsHelp = ref(false)
const showSettings = ref(false)
const showSkuSelect = ref(false)
const showHoldOrderDialog = ref(false)

const customerId = ref<number | undefined>(undefined)
const customers = ref<any[]>([])
const HOLD_ORDER_STORAGE_KEY = 'jxc-retail-held-orders'
const RETAIL_SETTINGS_STORAGE_KEY = 'jxc-retail-settings'
const DEFAULT_CUSTOMER_VALUE = 'default'

const applyDefaultCustomer = () => {
  if (customerId.value) return
  if (settings.defaultCustomer === DEFAULT_CUSTOMER_VALUE) {
    customerId.value = undefined
    return
  }
  const hit = customers.value.find(item => String(item.id) === String(settings.defaultCustomer))
  customerId.value = hit ? hit.id : undefined
}

const fetchCustomers = async () => {
  try {
    const res = await getCustomerOptions()
    customers.value = res.data.map((c: any) => ({ id: c.value, name: c.label }))
    applyDefaultCustomer()
  } catch (e) {
    console.error(e)
  }
}
const priceType = ref('retail')
const orderDate = ref(new Date().toISOString().replace('T', ' ').substring(0, 19))
const creator = ref('admin')
const searchKeyword = ref('')
const currentCategory = ref('all')

// 购物车数据
const cartItems = ref<any[]>([])
const heldOrders = ref<any[]>([])

// 设置数据
const settings = reactive({
  title: '收银台',
  showImage: true,
  showStock: true,
  hideSoldOut: false,
  forbidZeroStock: false,
  defaultCustomer: 'default',
  defaultAccount: 'cash',
  defaultWarehouse: '1',
  enablePoints: false
})

// 商品分类数据
const categories = ref<any[]>([
  { id: 'all', name: '全部商品' }
])

const fetchCategories = async () => {
  try {
    const res = await getProductCategoryTree()
    const tree = res.data || []
    // 将树形结构展平或只取第一层，零售收银为了简单通常展平展示，或者只取顶级分类
    // 这里简单处理，将树形转为平级列表
    const flattenCategories: any[] = []
    const flatten = (nodes: any[]) => {
      nodes.forEach(node => {
        flattenCategories.push({ id: node.id, name: node.label || node.name })
        if (node.children && node.children.length > 0) {
          flatten(node.children)
        }
      })
    }
    flatten(tree)
    categories.value = [
      { id: 'all', name: '全部商品' },
      ...flattenCategories
    ]
  } catch (e) {
    console.error(e)
  }
}

const mockProducts = ref<any[]>([])
// 获取商品列表
const fetchProducts = async () => {
  try {
    const res = await getProductList({
      pageNum: 1,
      pageSize: 50, // 零售端可以适当加大或者做无限滚动
      spuName: searchKeyword.value || undefined,
      categoryId: currentCategory.value !== 'all' ? currentCategory.value : undefined
    })
    
    // 转换后端数据为前端所需格式
    mockProducts.value = res.data.list.map((p: any) => {
      const hasMultiSku = p.skuList && p.skuList.length > 1
      const defaultSku = p.skuList?.[0] || {}
      const totalStock = (p.skuList || []).reduce((sum: number, sku: any) => sum + Number(sku.stock || 0), 0)
      return {
        id: p.spuId,
        name: p.spuName,
        skuCode: defaultSku.skuCode || '',
        stock: totalStock,
        retailPrice: defaultSku.retailPrice || 0,
        wholesalePrice: defaultSku.wholesalePrice || 0,
        hasMultiSku,
        skuList: p.skuList || []
      }
    })
  } catch (e) {
    console.error(e)
  }
}

// 监听搜索关键词和分类变化
watch([searchKeyword, currentCategory], () => {
  fetchProducts()
})

const displayProducts = computed(() => {
  let list = mockProducts.value
  if (searchKeyword.value) {
    list = list.filter(p => p.name.includes(searchKeyword.value) || (p.skuCode && p.skuCode.includes(searchKeyword.value)))
  }
  if (settings.hideSoldOut) {
    list = list.filter(p => p.stock > 0)
  }
  return list
})

// 计算属性
const totalQty = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + (item.qty || 0), 0)
})

const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + (item.qty * item.price), 0)
})

const discountAmount = ref(0)
const actualAmount = computed(() => {
  return totalAmount.value - discountAmount.value
})

const currentSelectProduct = ref<any>(null)
const selectedAttr1 = ref('')
const selectedAttr2 = ref('')

const attr1Options = computed(() => {
  const skuList = currentSelectProduct.value?.skuList || []
  const filtered = selectedAttr2.value ? skuList.filter((sku: any) => (sku.attr2 || '') === selectedAttr2.value) : skuList
  return getUniqueAttrs(filtered, 'attr1')
})

const attr2Options = computed(() => {
  const skuList = currentSelectProduct.value?.skuList || []
  const filtered = selectedAttr1.value ? skuList.filter((sku: any) => (sku.attr1 || '') === selectedAttr1.value) : skuList
  return getUniqueAttrs(filtered, 'attr2')
})

const currentSelectedSku = computed(() => {
  const skuList = currentSelectProduct.value?.skuList || []
  return skuList.find((sku: any) => {
    const attr1Matched = !selectedAttr1.value || (sku.attr1 || '') === selectedAttr1.value
    const attr2Matched = !selectedAttr2.value || (sku.attr2 || '') === selectedAttr2.value
    return attr1Matched && attr2Matched
  }) || null
})

// 方法
const goBack = () => {
  router.back()
}

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
    isFullscreen.value = true
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen()
      isFullscreen.value = false
    }
  }
}

const refreshData = async () => {
  await Promise.all([fetchCategories(), fetchProducts(), fetchCustomers()])
  ElMessage.success('数据已刷新')
}

const openSettings = () => {
  showSettings.value = true
}

const loadSettings = () => {
  try {
    const raw = localStorage.getItem(RETAIL_SETTINGS_STORAGE_KEY)
    if (!raw) return
    Object.assign(settings, JSON.parse(raw))
  } catch (error) {
    console.error(error)
  }
}

const saveSettings = () => {
  localStorage.setItem(RETAIL_SETTINGS_STORAGE_KEY, JSON.stringify({ ...settings }))
  applyDefaultCustomer()
  showSettings.value = false
  ElMessage.success('设置已保存')
}

const clearCart = () => {
  cartItems.value = []
  discountAmount.value = 0
}

const removeItem = (index: number) => {
  cartItems.value.splice(index, 1)
}

const selectProduct = (product: any) => {
  if (product.hasMultiSku) {
    currentSelectProduct.value = product
    // 默认选中第一个SKU的属性
    if (product.skuList && product.skuList.length > 0) {
      selectedAttr1.value = product.skuList[0].attr1 || ''
      selectedAttr2.value = product.skuList[0].attr2 || ''
    }
    showSkuSelect.value = true
  } else {
    addToCart(product)
  }
}

const getUniqueAttrs = (skuList: any[], attrKey: string) => {
  if (!skuList) return []
  const attrs = skuList.map(sku => sku[attrKey]).filter(val => val)
  return Array.from(new Set(attrs))
}

const selectAttr1 = (value: string) => {
  selectedAttr1.value = value
  if (selectedAttr2.value && !attr2Options.value.includes(selectedAttr2.value)) {
    selectedAttr2.value = attr2Options.value[0] || ''
  }
}

const selectAttr2 = (value: string) => {
  selectedAttr2.value = value
  if (selectedAttr1.value && !attr1Options.value.includes(selectedAttr1.value)) {
    selectedAttr1.value = attr1Options.value[0] || ''
  }
}

const confirmSkuSelection = () => {
  if (currentSelectProduct.value) {
    const selectedSku = currentSelectedSku.value
    if (!selectedSku) {
      ElMessage.warning('请选择有效规格')
      return
    }
    const skuName = `${selectedSku.attr1 || ''} ${selectedSku.attr2 || ''}`.trim() || '默认规格'
    addToCart(currentSelectProduct.value, selectedSku, skuName)
    showSkuSelect.value = false
  }
}

const addToCart = (product: any, selectedSku?: any, skuName = '默认规格') => {
  const targetSku = selectedSku || product.skuList?.[0]
  if (!targetSku?.skuId) {
    ElMessage.warning('商品缺少可用规格')
    return
  }
  if (settings.forbidZeroStock && Number(targetSku.stock || 0) <= 0) {
    ElMessage.warning('当前规格库存不足')
    return
  }
  const price = priceType.value === 'retail'
    ? Number(targetSku.retailPrice ?? product.retailPrice ?? 0)
    : Number(targetSku.wholesalePrice ?? product.wholesalePrice ?? 0)
  const skuId = targetSku.skuId
  
  const existingItem = cartItems.value.find(i => i.spuId === product.id && i.skuId === skuId)
  
  if (existingItem) {
    existingItem.qty++
  } else {
    cartItems.value.push({
      spuId: product.id,
      skuId: skuId,
      name: product.name,
      skuName,
      skuCode: targetSku.skuCode || '',
      price,
      qty: 1
    })
  }
}

const loadHeldOrders = () => {
  try {
    const raw = localStorage.getItem(HOLD_ORDER_STORAGE_KEY)
    heldOrders.value = raw ? JSON.parse(raw) : []
  } catch (error) {
    console.error(error)
    heldOrders.value = []
  }
}

const persistHeldOrders = () => {
  localStorage.setItem(HOLD_ORDER_STORAGE_KEY, JSON.stringify(heldOrders.value))
}

const holdOrder = () => {
  if (cartItems.value.length === 0) {
    ElMessage.warning('购物车为空，无法挂单')
    return
  }
  heldOrders.value.unshift({
    id: `${Date.now()}`,
    createdAt: orderDate.value,
    customerId: customerId.value,
    customerName: customers.value.find(item => item.id === customerId.value)?.name || '',
    priceType: priceType.value,
    discountAmount: discountAmount.value,
    actualAmount: actualAmount.value,
    totalQty: totalQty.value,
    items: cartItems.value.map(item => ({ ...item }))
  })
  persistHeldOrders()
  ElMessage.success('挂单成功')
  clearCart()
}

const fetchHoldOrders = () => {
  loadHeldOrders()
  if (heldOrders.value.length === 0) {
    ElMessage.info('暂无挂单数据')
    return
  }
  showHoldOrderDialog.value = true
}

const restoreHoldOrder = (orderId: string) => {
  const order = heldOrders.value.find(item => item.id === orderId)
  if (!order) {
    ElMessage.warning('挂单不存在或已失效')
    return
  }
  cartItems.value = order.items.map((item: any) => ({ ...item }))
  customerId.value = order.customerId
  priceType.value = order.priceType || 'retail'
  discountAmount.value = Number(order.discountAmount || 0)
  orderDate.value = order.createdAt || orderDate.value
  heldOrders.value = heldOrders.value.filter(item => item.id !== orderId)
  persistHeldOrders()
  showHoldOrderDialog.value = false
  ElMessage.success('取单成功')
}

const removeHoldOrder = (orderId: string) => {
  heldOrders.value = heldOrders.value.filter(item => item.id !== orderId)
  persistHeldOrders()
  if (heldOrders.value.length === 0) {
    showHoldOrderDialog.value = false
  }
  ElMessage.success('挂单已删除')
}

const checkout = async () => {
  if (cartItems.value.length === 0) {
    ElMessage.warning('请先添加商品')
    return
  }
  
  const submitData = {
    orderType: 2, // 2: 零售单
    docDate: orderDate.value,
    customerId: customerId.value,
    remark: '',
    totalAmount: totalAmount.value.toFixed(2),
    discountAmount: discountAmount.value.toFixed(2),
    actualAmount: actualAmount.value.toFixed(2),
    otherFee: 0,
    paidAmount: actualAmount.value.toFixed(2), // 零售默认全额付款
    details: cartItems.value.map(item => ({
      spuId: item.spuId,
      skuId: item.skuId,
      qty: item.qty,
      unitPrice: item.price,
      lineAmount: (item.qty * item.price).toFixed(2)
    }))
  }
  
  try {
    await createSalesOrder(submitData)
    ElMessage.success(`结算成功，实收：¥${actualAmount.value.toFixed(2)}`)
    clearCart()
  } catch (e) {
    console.error(e)
  }
}

const handleFullscreenChange = () => {
  isFullscreen.value = !!document.fullscreenElement
}

// 快捷键处理
const handleKeydown = (e: KeyboardEvent) => {
  // 如果在输入框中，不触发全局快捷键 (除非是特定的组合键)
  const target = e.target as HTMLElement
  const isInput = target.tagName === 'INPUT' || target.tagName === 'TEXTAREA'

  if (!isInput) {
    switch (e.key.toUpperCase()) {
      case 'F':
        e.preventDefault()
        searchInputRef.value?.focus()
        break
      case 'U':
        e.preventDefault()
        toggleFullscreen()
        break
      case 'R':
        e.preventDefault()
        refreshData()
        break
      case 'S':
        e.preventDefault()
        openSettings()
        break
      case 'F2':
        e.preventDefault()
        showShortcutsHelp.value = true
        break
      case 'G':
        e.preventDefault()
        holdOrder()
        break
      case 'Q':
        e.preventDefault()
        fetchHoldOrders()
        break
      case ' ': // SPACE
        e.preventDefault()
        checkout()
        break
    }
  }
}

onMounted(() => {
  loadSettings()
  window.addEventListener('keydown', handleKeydown)
  
  document.addEventListener('fullscreenchange', handleFullscreenChange)
  
  loadHeldOrders()
  fetchCategories()
  fetchProducts()
  fetchCustomers()
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
  document.removeEventListener('fullscreenchange', handleFullscreenChange)
})
</script>

<style scoped>
.retail-container {
  height: calc(100vh - 84px); /* 减去顶部 header 和 tags view 的高度 */
  display: flex;
  flex-direction: column;
  background-color: #f0f2f5;
  overflow: hidden;
  
  &.fullscreen {
    height: 100vh;
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    min-width: 1200px;
    z-index: 9999;
  }
}

.retail-header {
  height: 48px;
  background-color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  border-bottom: 1px solid #ebeef5;
}

.header-btn {
  color: #606266;
  font-size: 14px;
  margin-right: 16px;
  
  .el-icon {
    margin-right: 4px;
  }
  
  &:hover {
    color: #409eff;
  }
}

.retail-main {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 左侧购物车面板 */
.cart-panel {
  width: 360px;
  background-color: #fff;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #ebeef5;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  z-index: 10;
}

.cart-header {
  padding: 15px;
  border-bottom: 1px solid #ebeef5;
  
  .customer-select {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 18px;
    font-weight: bold;
    cursor: pointer;
    margin-bottom: 12px;
    
    .icon-right {
      color: #409eff;
    }
  }
  
  .price-type {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .clear-btn {
      color: #909399;
      &:hover { color: #f56c6c; }
    }
  }
}

.order-info {
  padding: 10px 15px;
  background-color: #fafafa;
  border-bottom: 1px solid #ebeef5;
  font-size: 13px;
  color: #606266;
  
  .info-row {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .label {
      width: 70px;
    }
  }
}

.cart-body {
  flex: 1;
  overflow-y: auto;
  background-color: #f8f9fa;
  
  .empty-cart {
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #909399;
    font-size: 14px;
    
    .empty-icon {
      font-size: 64px;
      color: #dcdfe6;
      margin-bottom: 16px;
    }
  }
  
  .cart-items {
    padding: 10px;
    
    .cart-item {
      background: #fff;
      padding: 12px;
      border-radius: 4px;
      margin-bottom: 10px;
      box-shadow: 0 1px 3px rgba(0,0,0,0.05);
      
      .item-name {
        font-size: 14px;
        font-weight: 500;
        margin-bottom: 8px;
        color: #303133;
      }
      
      .item-price {
        color: #f56c6c;
        font-weight: bold;
        margin-bottom: 8px;
      }
      
      .item-actions {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }
    }
  }
}

.cart-footer {
  background-color: #fff;
  border-top: 1px solid #ebeef5;
  padding: 15px;
  
  .remark-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
    
    .remark-btn {
      width: 100px;
    }
    
    .points {
      color: #606266;
      font-size: 14px;
      font-weight: bold;
    }
  }
  
  .summary-row {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
    font-size: 14px;
    color: #303133;
    
    .summary-item {
      flex: 1;
      display: flex;
      align-items: center;
    }
    
    .value {
      font-weight: bold;
    }
    
    &.highlight {
      margin-bottom: 20px;
      
      .text-danger {
        color: #f56c6c;
      }
      
      .amount {
        font-size: 20px;
      }
      
      .edit-icon {
        color: #f56c6c;
        margin-left: 8px;
        cursor: pointer;
      }
    }
  }
  
  .action-row {
    display: flex;
    gap: 10px;
    
    .action-btn {
      flex: 1;
      color: #409eff;
      border-color: #b3d8ff;
      background-color: #ecf5ff;
    }
    
    .checkout-btn {
      flex: 2;
      font-weight: bold;
    }
  }
}

/* 右侧商品面板 */
.product-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #fff;
}

.product-header {
  height: 60px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #ebeef5;
  
  .search-input {
    flex: 1;
    margin-right: 20px;
    
    :deep(.el-input__wrapper) {
      background-color: #f5f7fa;
      border-radius: 20px;
      box-shadow: none;
    }
  }
  
  .view-toggle-btn {
    border: none;
    font-size: 20px;
    color: #909399;
  }
}

.product-body {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.product-grid {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  align-content: start;
}

.product-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  
  &:hover {
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    transform: translateY(-2px);
  }
  
  .product-img {
    height: 140px;
    background-color: #f5f7fa;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .img-placeholder {
      font-size: 40px;
      color: #dcdfe6;
    }
  }
  
  .product-info {
    padding: 12px;
    
    .product-name {
      font-size: 14px;
      font-weight: 500;
      color: #303133;
      margin-bottom: 4px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    
    .product-sku {
      font-size: 12px;
      color: #909399;
      margin-bottom: 4px;
    }
    
    .product-stock {
      font-size: 12px;
      color: #909399;
      margin-bottom: 8px;
    }
    
    .product-price-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: auto;
      
      .price {
        font-size: 16px;
        font-weight: bold;
        color: #303133;
      }
      
      .select-sku-btn {
        padding: 0;
        border-radius: 12px;
        border: 1px solid #409eff;
        padding: 2px 10px;
        font-size: 12px;
      }
    }
  }
}

.category-sidebar {
  width: 120px;
  background-color: #fafafa;
  border-left: 1px solid #ebeef5;
  overflow-y: auto;
  
  .category-item {
    height: 50px;
    line-height: 50px;
    text-align: center;
    font-size: 14px;
    color: #606266;
    cursor: pointer;
    border-bottom: 1px solid #ebeef5;
    
    &:hover {
      color: #409eff;
    }
    
    &.active {
      background-color: #e6f1fc;
      color: #409eff;
      font-weight: bold;
      border-right: 3px solid #409eff;
    }
  }
}

.product-footer {
  height: 60px;
  border-top: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  padding: 0 20px;
  background-color: #fafafa;
  
  .footer-btn {
    margin-right: 15px;
    width: 100px;
  }
  
  .barcode-scan {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #fff;
    border: 1px solid #dcdfe6;
    border-radius: 20px;
    height: 40px;
    color: #909399;
    font-size: 14px;
    
    .el-icon {
      margin-right: 8px;
      font-size: 18px;
    }
  }
}

/* 弹窗样式 */
.shortcuts-list {
  .shortcut-item {
    display: flex;
    justify-content: space-between;
    padding: 10px 20px;
    border-bottom: 1px solid #ebeef5;
    
    &:last-child {
      border-bottom: none;
    }
    
    strong {
      color: #303133;
      background: #f4f4f5;
      padding: 2px 8px;
      border-radius: 4px;
      border: 1px solid #dcdfe6;
    }
  }
}

.settings-form {
  .settings-title {
    margin: 15px 0 10px;
    color: #303133;
    font-size: 16px;
    border-left: 4px solid #409eff;
    padding-left: 10px;
    
    &:first-child {
      margin-top: 0;
    }
  }
  
  .settings-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 20px;
    border-bottom: 1px solid #ebeef5;
    
    .label {
      color: #606266;
      font-size: 14px;
    }
  }
}

.sku-select-container {
  .sku-product-info {
    display: flex;
    margin-bottom: 20px;
    padding-bottom: 20px;
    border-bottom: 1px solid #ebeef5;
    
    .img-box {
      width: 100px;
      height: 100px;
      background: #f5f7fa;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 20px;
      border-radius: 4px;
      
      .placeholder {
        font-size: 40px;
        color: #dcdfe6;
      }
    }
    
    .info {
      flex: 1;
      
      .name {
        font-size: 18px;
        font-weight: bold;
        color: #303133;
        margin-bottom: 10px;
      }
      
      .code {
        color: #909399;
        font-size: 13px;
        margin-bottom: 5px;
      }
    }
  }
  
  .sku-attrs {
    .attr-group {
      margin-bottom: 20px;
      
      .attr-name {
        font-size: 14px;
        color: #606266;
        margin-bottom: 10px;
      }
      
      .attr-values {
        display: flex;
        flex-wrap: wrap;
        gap: 10px;
        
        .attr-val {
          padding: 8px 20px;
          background: #f4f4f5;
          border-radius: 4px;
          cursor: pointer;
          font-size: 13px;
          color: #606266;
          border: 1px solid transparent;
          
          &:hover {
            color: #409eff;
          }
          
          &.active {
            background: #ecf5ff;
            color: #409eff;
            border-color: #b3d8ff;
          }
        }
      }
    }
  }
  
  .sku-selected-info {
    display: flex;
    justify-content: space-between;
    gap: 12px;
    margin-top: 20px;
    padding: 12px 16px;
    background: #f5f7fa;
    border-radius: 6px;
    color: #606266;
    font-size: 13px;
  }

  .sku-actions {
    margin-top: 30px;
    text-align: center;
    
    .confirm-sku-btn {
      width: 200px;
      height: 40px;
    }
  }
}

.hold-order-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.hold-order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
}

.hold-order-main {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.hold-order-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 600;
  color: #303133;
}

.hold-order-customer {
  color: #909399;
  font-weight: 400;
}

.hold-order-meta {
  display: flex;
  gap: 16px;
  color: #606266;
  font-size: 13px;
}

.hold-order-actions {
  display: flex;
  gap: 12px;
}
</style>
