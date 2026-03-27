<template>
  <div class="system-params-container">
    <div class="anchor-tabs">
      <el-tabs v-model="activeTab" @tab-click="scrollToSection">
        <el-tab-pane label="基础信息" name="basic"></el-tab-pane>
        <el-tab-pane label="开单" name="billing"></el-tab-pane>
        <el-tab-pane label="偏好" name="preference"></el-tab-pane>
        <el-tab-pane label="打印尺寸" name="print"></el-tab-pane>
        <el-tab-pane label="远程打印" name="remote-print"></el-tab-pane>
        <el-tab-pane label="零售配置" name="retail"></el-tab-pane>
        <el-tab-pane label="首页配置" name="home"></el-tab-pane>
        <el-tab-pane label="其他" name="other"></el-tab-pane>
      </el-tabs>
    </div>

    <div class="form-content" ref="scrollContainer">
      <el-form :model="formData" label-width="180px" class="settings-form">
        <div class="section-panel" id="basic">
          <div class="section-header">
            <span class="title">基础信息</span>
            <div class="actions">
              <el-button link type="info" v-perm="'settings:system-params:edit'">系统初始化【保留商品】</el-button>
              <el-button link type="info" v-perm="'settings:system-params:edit'">系统初始化【全清】</el-button>
            </div>
          </div>
          <div class="section-body">
            <el-form-item label="系统名称：">
              <el-input v-model="formData.systemName" style="width: 300px" />
            </el-form-item>
            <el-form-item label="公司名称：">
              <el-input v-model="formData.companyName" style="width: 300px" />
            </el-form-item>
            <el-form-item label="公司电话：">
              <el-input v-model="formData.companyPhone" style="width: 300px" />
            </el-form-item>
            <el-form-item label="公司地址：">
              <el-input v-model="formData.companyAddress" style="width: 300px" />
            </el-form-item>
          </div>
        </div>

        <div class="section-panel" id="billing">
          <div class="section-header">
            <span class="title">开单</span>
          </div>
          <div class="section-body">
            <el-form-item label="制单人可选：">
              <el-select v-model="formData.creatorSelectable" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
            </el-form-item>
            <el-form-item label="自动审核：">
              <el-select v-model="formData.autoAudit" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-tooltip content="自动审核配置" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="启用批次：">
              <el-select v-model="formData.enableBatch" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-button link type="primary" style="margin-left: 10px">行业切分指引</el-button>
            </el-form-item>
            <el-form-item label="启用保质期：">
              <el-select v-model="formData.enableShelfLife" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-tooltip content="保质期配置" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="启用条码：">
              <el-select v-model="formData.enableBarcode" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
            </el-form-item>
            <el-form-item label="启用收发存：">
              <el-select v-model="formData.enableInventory" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-tooltip content="收发存配置" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="销售打出外带交货金额：">
              <el-select v-model="formData.salesCarryAmount" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-tooltip content="说明" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="进货时自动外带交货金额：">
              <el-select v-model="formData.purchaseCarryAmount" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-tooltip content="说明" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="禁止零库存出库：">
              <el-select v-model="formData.forbidZeroStockOut" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-tooltip content="说明" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="隐藏单据审批人：">
              <el-select v-model="formData.hideApprover" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-tooltip content="说明" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="允许修改单价：">
              <el-select v-model="formData.allowEditPrice" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-tooltip content="说明" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="开单业务员：">
              <el-select v-model="formData.billingSalesman" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-tooltip content="说明" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="售价低于进价提示：">
              <el-select v-model="formData.warnLowPrice" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-tooltip content="说明" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="销售税率：">
              <el-input v-model="formData.salesTaxRate" style="width: 100px" />
              <el-checkbox v-model="formData.enableSalesTax" style="margin-left: 15px">启用税率</el-checkbox>
              <el-checkbox v-model="formData.salesIncludeTax" style="margin-left: 15px">销售含税</el-checkbox>
              <el-tooltip content="说明" placement="right" style="margin-left: 10px">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="进货税率：">
              <el-input v-model="formData.purchaseTaxRate" style="width: 100px" />
              <el-checkbox v-model="formData.enablePurchaseTax" style="margin-left: 15px">启用税率</el-checkbox>
              <el-checkbox v-model="formData.purchaseIncludeTax" style="margin-left: 15px">进货含税</el-checkbox>
              <el-tooltip content="说明" placement="right" style="margin-left: 10px">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
          </div>
        </div>

        <div class="section-panel" id="preference">
          <div class="section-header">
            <span class="title">偏好</span>
          </div>
          <div class="section-body">
            <el-form-item label="价格小数位数：">
              <el-select v-model="formData.priceDecimals" style="width: 200px">
                <el-option label="2位" value="2" />
                <el-option label="3位" value="3" />
                <el-option label="4位" value="4" />
              </el-select>
              <div class="form-hint error-hint">修改小数位数只能从少改多，如满多小数位系统将根据四舍五入计算金额，请谨慎操作！</div>
            </el-form-item>
            <el-form-item label="数量小数位数：">
              <el-select v-model="formData.qtyDecimals" style="width: 200px">
                <el-option label="0位" value="0" />
                <el-option label="1位" value="1" />
                <el-option label="2位" value="2" />
                <el-option label="3位" value="3" />
              </el-select>
            </el-form-item>
            <el-form-item label="件数取整默认位：">
              <el-input v-model="formData.pieceRound" style="width: 200px" />
              <el-tooltip content="说明" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="外接网盘下载地址：">
              <el-select v-model="formData.cloudDriveUrl" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-tooltip content="说明" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="打印设备选择可用设备：">
              <el-select v-model="formData.printerSelectable" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-tooltip content="说明" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="开单展示图片默认展开：">
              <el-select v-model="formData.billingShowImage" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-tooltip content="说明" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="开单扫码默认添加：">
              <el-select v-model="formData.billingScanAdd" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-tooltip content="开启后，扫码录入商品时默认直接添加到单据明细中。" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="库存预警默认值：">
              <el-input-number v-model="formData.inventoryWarningDefault" :min="0" controls-position="right" style="width: 200px" />
              <el-tooltip content="添加商品时会读取此处设置，自动填充到库存预警输入框中。" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="开启商品下拉菜单：">
              <el-select v-model="formData.enableQuickProductDropdown" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-tooltip content="开启后，开单时点击商品信息框，会出现商品便捷选择下拉菜单，提升开单效率。" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="开启新商品开单：">
              <el-select v-model="formData.allowNewProductOnBilling" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-tooltip content="开启后，开单界面中可以输入新商品后直接开单，无需先到商品管理中添加。" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="开启商品条码可重复：">
              <el-select v-model="formData.allowDuplicateBarcode" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-tooltip content="开启后，商品添加时允许存在重复的条形码。" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="开启商品名称可重复：">
              <el-select v-model="formData.allowDuplicateNameSpec" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-tooltip content="开启后，商品添加时允许存在相同的名称及规格条目。" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
          </div>
        </div>

        <div class="section-panel" id="print">
          <div class="section-header">
            <span class="title">打印</span>
          </div>
          <div class="section-body">
            <el-form-item label="默认打印纸张：">
              <el-select v-model="formData.defaultPaper" style="width: 200px">
                <el-option label="A4 210mmx297mm" value="A4" />
                <el-option label="(241-2) 210mmx140mm" value="241-2" />
                <el-option label="小票（80mm）" value="80mm" />
                <el-option label="小票（58mm）" value="58mm" />
              </el-select>
              <el-tooltip content="全局打印纸张设置，新建打印模板时会作为默认纸张。" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="服装批发格式：">
              <el-select v-model="formData.printClothesWholesaleLayout" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-tooltip content="适用于服装行业，开启后打印单据尺码将作为单独列进行横向排列。" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="纺织行业格式：">
              <el-select v-model="formData.printTextileStyleLayout" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-tooltip content="适用于纺织行业，开启后打印单据款号将作为单独列进行横向排列。" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="报表批打模式：">
              <el-select v-model="formData.reportBatchPrint" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-tooltip content="说明" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="连续打印格式：">
              <el-select v-model="formData.continuousPrint" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-tooltip content="说明" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
          </div>
        </div>

        <div class="section-panel" id="remote-print">
          <div class="section-header">
            <span class="title">远程打印</span>
          </div>
          <div class="section-body">
            <el-form-item label="可连账户：">
              <el-select v-model="formData.remotePrintAccount" style="width: 200px" placeholder="请选择打印账户">
                <el-option label="账户1" value="acc1" />
              </el-select>
              <el-tooltip content="说明" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="收银账户：">
              <el-select v-model="formData.remoteCashAccount" style="width: 200px" placeholder="请选择收银账户">
                <el-option label="账户1" value="acc1" />
              </el-select>
              <el-tooltip content="说明" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
          </div>
        </div>

        <div class="section-panel" id="other">
          <div class="section-header">
            <span class="title">其他配置</span>
          </div>
          <div class="section-body">
            <el-form-item label="商品多发货计算：">
              <el-select v-model="formData.multiDeliveryCalc" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-tooltip content="说明" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="时区：">
              <el-select v-model="formData.timezone" style="width: 200px">
                <el-option label="中国北京" value="Asia/Shanghai" />
              </el-select>
            </el-form-item>
          </div>
        </div>

        <div class="section-panel" id="retail">
          <div class="section-header">
            <span class="title">零售配置</span>
          </div>
          <div class="section-body">
            <el-form-item label="零售权限：">
              <el-select v-model="formData.retailPermission" style="width: 200px">
                <el-option label="收银台" value="收银台" />
              </el-select>
              <el-tooltip content="说明" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="积分比例：">
              <el-input v-model="formData.pointsRatio" style="width: 100px" />
              <el-checkbox v-model="formData.enablePoints" style="margin-left: 15px">启用积分</el-checkbox>
              <el-tooltip content="说明" placement="right" style="margin-left: 10px">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="自动打印小票：">
              <el-select v-model="formData.autoPrintReceipt" style="width: 200px">
                <el-option label="是" value="是" />
                <el-option label="否" value="否" />
              </el-select>
              <el-tooltip content="说明" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="默认客户：">
              <el-select v-model="formData.defaultCustomer" style="width: 200px">
                <el-option label="无" value="无" />
              </el-select>
              <el-tooltip content="说明" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="默认资金账户：">
              <el-select v-model="formData.defaultAccount" style="width: 200px">
                <el-option label="现金" value="现金" />
              </el-select>
              <el-tooltip content="说明" placement="right">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
          </div>
        </div>

        <div class="section-panel" id="home">
          <div class="section-header">
            <span class="title">首页配置</span>
          </div>
          <div class="section-body">
            <el-form-item label="图表天数：">
              <el-input v-model="formData.chartDays" style="width: 200px" />
              <span class="form-hint" style="margin-left: 10px; color: #999;">销售趋势</span>
            </el-form-item>
            <el-form-item label="公告信息：">
              <el-input 
                v-model="formData.noticeInfo" 
                type="textarea" 
                :rows="4" 
                style="width: 400px"
                placeholder="可在系统设置里设置公告信息" 
              />
            </el-form-item>
          </div>
        </div>
      </el-form>
    </div>

    <div class="bottom-actions">
      <el-button @click="onCancel" style="width: 100px; border-radius: 20px;">取消</el-button>
      <el-button type="primary" @click="onSave" :loading="saving" style="width: 100px; border-radius: 20px;" v-perm="'settings:system-params:edit'">保存设置</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { QuestionFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getSystemSettings, saveSystemSettings } from '@/api/settings'

const activeTab = ref('basic')
const scrollContainer = ref<HTMLElement | null>(null)
const saving = ref(false)

const formData = ref({
  systemName: '衣多多服饰',
  companyName: '衣多多服饰',
  companyPhone: '15210629985',
  companyAddress: '北京市',
  
  creatorSelectable: '是',
  autoAudit: '是',
  enableBatch: '否',
  enableShelfLife: '否',
  enableBarcode: '否',
  enableInventory: '否',
  salesCarryAmount: '是',
  purchaseCarryAmount: '是',
  forbidZeroStockOut: '否',
  hideApprover: '否',
  allowEditPrice: '是',
  billingSalesman: '否',
  warnLowPrice: '是',
  salesTaxRate: '0',
  enableSalesTax: false,
  salesIncludeTax: false,
  purchaseTaxRate: '0',
  enablePurchaseTax: false,
  purchaseIncludeTax: false,
  
  priceDecimals: '2',
  qtyDecimals: '3',
  pieceRound: '2',
  cloudDriveUrl: '是',
  printerSelectable: '是',
  billingShowImage: '是',
  billingScanAdd: '是',
  inventoryWarningDefault: 0,
  enableQuickProductDropdown: '是',
  allowDuplicateBarcode: '否',
  allowDuplicateNameSpec: '是',
  allowNewProductOnBilling: '否',
  
  defaultPaper: 'A4',
  reportBatchPrint: '是',
  continuousPrint: '是',
  printClothesWholesaleLayout: '否',
  printTextileStyleLayout: '否',
  
  remotePrintAccount: '',
  remoteCashAccount: '',
  
  multiDeliveryCalc: '是',
  timezone: 'Asia/Shanghai',
  
  retailPermission: '收银台',
  pointsRatio: '1',
  enablePoints: false,
  autoPrintReceipt: '是',
  defaultCustomer: '无',
  defaultAccount: '现金',
  
  chartDays: '7',
  noticeInfo: '可在系统设置里设置公告信息',
  
  enableSizeColor: false
})

const scrollToSection = (tab: any) => {
  const el = document.getElementById(tab.props.name)
  if (el && scrollContainer.value) {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

const load = async () => {
  try {
    const res = await getSystemSettings()
    if (res?.data) {
      Object.assign(formData.value, res.data)
    }
  } catch (error) {
    console.error('加载系统配置失败', error)
  }
}

const onCancel = () => {
}

const onSave = async () => {
  saving.value = true
  try {
    await saveSystemSettings(formData.value)
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败', error)
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  load()
})
</script>

<style scoped>
.system-params-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
  background-color: #f5f7fa;
}

.anchor-tabs {
  background: #fff;
  padding: 0 20px;
  position: sticky;
  top: 0;
  z-index: 10;
}

:deep(.el-tabs__nav-wrap::after) {
  height: 1px;
}

.form-content {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
  padding-bottom: 80px;
}

.section-panel {
  background: #fff;
  border-radius: 4px;
  margin-bottom: 15px;
  border: 1px solid #ebeef5;
}

.section-header {
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-header .title {
  font-weight: bold;
  font-size: 14px;
  color: #303133;
}

.section-body {
  padding: 20px;
}

.help-icon {
  margin-left: 8px;
  color: #909399;
  cursor: pointer;
  vertical-align: middle;
}

.error-hint {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
  line-height: 1.2;
}

.bottom-actions {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 12px 0;
  text-align: center;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.05);
  z-index: 10;
}
</style>
