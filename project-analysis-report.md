# 多多进销存系统深度分析报告

**分析日期：** 2026-03-27
**分析方法：** 基于实际代码实现 + 行业标准ERP能力对比
**项目完成度：** 约50%
**修正记录：** 经第三方验证后修正（核心特性2/3描述失实；核销功能误判；OAuth2描述偏差）

---

## 一、项目做什么的

### 1.1 产品定位
面向中小微商贸企业的**进销存ERP系统**，核心定位：
- **目标客户**：服装鞋帽、快消品、五金建材等批零兼营企业
- **核心价值**：多维矩阵录入、精细化欠货追踪、业财一体、库存管控
- **业务范围**：销售管理（批发/零售/预定）、采购管理、库存管理、财务管理

### 1.2 核心特性

> ⚠️ **经代码验证，以下描述存在偏差**

| # | 特性名称 | 报告原述 | 代码验证结论 |
|---|---------|---------|------------|
| 1 | 多维矩阵录入 | 完整实现 | ✅ **基本成立**：前端 SizeColorMatrixPicker → 扁平化为 SKU 明细行 → 后端 `SalesOrderDetail` 存储，链路完整 |
| 2 | 精细化欠货追踪 | 预定→分批发货→未转数量追踪 | ❌ **不成立**：`unfulfilledQty` 仅在创建预订单时写入初始值，`convertToSales()` 直接标记完成（全量转单），从不扣减未转数量；分批发货能力不存在 |
| 3 | 极简业财一体 | 业务单据自动生成财务流水 | ❌ **不成立**：`auditOrder()` 仅改状态+启流程，`SalesOrderService` 与 `ReceivableService/FinanceTransactionService` 完全独立，无任何调用；业务单据不触发任何财务记录生成 |
| 4 | 批零双线并行 | B2B/B2C 场景隔离 | ⚠️ **部分成立**：`orderType` 字段存在，前端有独立零售页面，但后端 `SalesOrderServiceImpl` 对 WHOLESALE(1) 和 RETAIL(2) 处理逻辑完全相同；零售特有的日结/收银/对账均未实现 |

#### 1.2.1 多维矩阵录入（已实现 ✅）
前端 `SizeColorMatrixPicker` 选择颜色尺码组合 → `confirmSelectProducts()` 扁平化为 `details[{skuId, qty, unitPrice}]` → POST `/sales` → `SalesOrderDTO.details` → `SalesOrderDetail`（扁平行）存储，链路完整。

#### 1.2.2 精细化欠货追踪（未实现 ❌）
- `unfulfilledQty` 字段仅在 `createOrder(orderType=3)` 时初始化为 `qty`，从不更新
- `convertToSales()` 以原始 details 全量创建新销售单，不支持按实际发货量部分转换
- 预订单状态直接由 10 → 40，绕过了"部分发货(30)"状态
- 欠货追踪所需的"预定→发货→扣减未转数量→判断是否完成"链路不存在

#### 1.2.3 极简业财一体（未实现 ❌）
- `auditOrder()` 只执行三件事：改 status 10→30、记审核时间、启动 Flowable 实例
- **不生成任何财务记录**：无 `Receivable`、无 `FinanceTransaction`、无库存流水
- `SalesOrder` 的 `settleStatus`、`paidAmount` 字段无人写入，始终为 null/0
- 财务模块（Receivable/Payable/FinanceTransaction）与业务模块完全隔离，无联动

#### 1.2.4 批零双线并行（部分实现 ⚠️）
- `SalesOrderTypeEnum`：WHOLESALE(1) / RETAIL(2) / BOOKING(3) 三种类型定义完整
- 前端有独立零售页面（购物车 UI，与批发单 add.vue 不同）
- 但后端 `createOrder/updateOrder/auditOrder` 对 orderType 1 和 2 的处理**完全相同**
- 仅 docNo 前缀略有区分（统一用"XS"，预订单用"YD"）
- 零售单需要的日结汇总、门店收银、定期对账等业务逻辑不存在

---

## 二、基于什么技术实现

### 2.1 前端技术栈
- **框架**: Vue 3.5 + TypeScript 5.9 + Vite 8.0
- **UI库**: Element Plus 2.13
- **状态管理**: Pinia 3.0
- **特色库**: 
  - bpmn-js（流程设计器）
  - ECharts（图表展示）
  - Fabric.js（打印模板设计）
  - QRCode、JsBarcode（条码生成）

### 2.2 后端技术栈
- **框架**: Spring Boot 3.2.4 + Java 21
- **安全**: Spring Security + OAuth2 Resource Server + JWT
- **ORM**: MyBatis-Plus 3.5.5
- **工作流**: Flowable 7.0（BPMN 2.0）
- **数据库**: MySQL
- **缓存**: Redis
- **工具**: Lombok、MapStruct、Fastjson2

**安全架构说明：**
- 使用 **OAuth2 Resource Server + JWT** 进行认证授权
- Token发放依赖外部系统或自实现（AuthController）
- 虽然pom.xml引入了authorization-server依赖，但未配置完整的认证中心
- JwtKeyConfig配置了JWT签名密钥对（RSA 2048位）

### 2.3 架构特点
- 前后端分离架构
- RESTful API设计
- OAuth2 + JWT认证
- RBAC权限模型
- 工作流引擎集成

---

## 二.五、项目亮点

在指出问题之前，需要肯定项目的优点。该项目在技术架构和功能集成上有以下亮点：

### 2.5.1 完整的工作流引擎集成

**技术实现：**
- Flowable 7.0 + BPMN 2.0标准
- 前端集成bpmn-js可视化流程设计器
- 支持流程模型管理、业务绑定、审批流转
- **WorkflowFlowableEventListener** 监听流程事件并回调业务逻辑

**行业对比：**
- 大多数进销存系统没有工作流功能
- 少数有工作流的也多是简单状态流转
- 该项目实现了完整的BPMN流程设计能力

**应用场景：**
- 销售单审批流程
- 采购单审批流程
- 库存调拨审批
- 自定义业务流程

**审批回调实现：**
```java
// WorkflowFlowableEventListener.java:217-227
private void handleProcessCompleted(FlowableEvent event) {
    // 流程完成时回调
    if (Objects.equals(inst.getBizType(), "SALES_ORDER")) {
        // 更新销售单状态为已审核
        SalesOrder order = new SalesOrder();
        order.setOrderId(orderId);
        order.setStatus(30); // 已审核
        order.setAuditTime(LocalDateTime.now());
        salesOrderMapper.updateById(order);
    }
}
```

**已实现的回调逻辑：**
- ✅ 流程启动：创建 WfInstance 记录
- ✅ 任务创建：创建 WfTask 记录，分配审批人
- ✅ 任务完成：更新 WfTask 状态
- ✅ 流程完成：更新业务单据状态

**缺失的回调逻辑：**
- ❌ 审批通过后：不扣减库存、不生成应收
- ❌ 审批驳回后：不处理驳回逻辑
- ❌ 退货审批：不处理退货相关的财务和库存

**代码位置：**
- 后端：`backend/src/main/java/com/duoduo/jxc/workflow/`
- 前端：`frontend/src/views/workflow/`
- 监听器：`backend/src/main/java/com/duoduo/jxc/workflow/WorkflowFlowableEventListener.java`

### 2.5.2 专业的打印模板设计

**技术实现：**
- Fabric.js可视化拖拽设计
- 支持自定义纸张尺寸
- 支持条码、二维码生成
- 支持模板保存和复用

**功能特点：**
- 可视化设计，无需编码
- 支持销售单、采购单等多种单据类型
- 支持套打（发货单、入库单等）

**代码位置：**
- 后端：`backend/src/main/java/com/duoduo/jxc/controller/PrintTemplateController.java`
- 前端：`frontend/src/views/settings/print-template-designer-fabric.vue`

### 2.5.3 完善的权限体系

**技术实现：**
- OAuth2 Resource Server（JWT Token认证）
- 自实现Token发放（AuthController）
- RBAC角色权限模型
- 数据权限框架（CustomDataPermissionHandler）

**权限粒度：**
- ✅ 菜单权限
- ✅ 功能权限（按钮级别）
- ⚠️ 数据权限（框架已搭建，但实现较基础）
- ✅ API权限（`@PreAuthorize`）

**对比优势：**
- 实现了完整的RBAC权限模型
- JWT Token认证，无状态设计
- 数据权限框架已搭建（基于MyBatis-Plus DataPermissionHandler）
- 通过data_auth字段预留了数据授权扩展点

**实现详情：**
```java
// SecurityConfig.java - OAuth2 Resource Server配置
http.oauth2ResourceServer(oauth2 -> oauth2
    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

// CustomDataPermissionHandler.java - 数据权限处理
public class CustomDataPermissionHandler implements DataPermissionHandler {
    @Override
    public Expression getSqlSegment(Expression where, String mappedStatementId) {
        // 通过DataScopeContextHolder获取权限SQL片段
    }
}
```

**代码位置：**
- 认证：`backend/src/main/java/com/duoduo/jxc/security/`
- 权限：`backend/src/main/java/com/duoduo/jxc/service/rbac/`

### 2.5.4 现代化技术栈

**后端：**
- Spring Boot 3.2.4（最新稳定版）
- Java 21（LTS版本）
- MyBatis-Plus 3.5.5
- 完整的异常处理体系（BizCode枚举）

**前端：**
- Vue 3.5（Composition API）
- TypeScript 5.9（类型安全）
- Vite 8.0（快速构建）
- Pinia 3.0（状态管理）

**架构优势：**
- 前后端分离，易于维护和扩展
- RESTful API设计，接口清晰
- 代码规范，注释完整

### 2.5.5 其他亮点

1. **多维度商品管理**
   - SPU/SKU架构
   - 商品属性值管理
   - 支持矩阵录入（颜色×尺码）

2. **完整的退货管理**
   - SalesReturnOrderServiceImpl (437行)
   - 支持 originSalesId 关联原销售单
   - 支持部分退货（returnedQtyMap）
   - getSourceOrder() 获取源销售单信息
   - 自动计算可退货数量（qty - returnedQty）

3. **前端UI完整**
   - 76个Vue文件
   - 完整的销售、采购、库存、财务模块UI
   - 列字段配置保存

4. **代码质量**
   - 375个Java文件，架构清晰
   - 统一的DTO转换（MapStruct）
   - 统一的异常处理
   - 操作日志记录

### 2.5.6 与同类项目对比

| 功能特性 | 本项目 | 一般进销存 | 高端ERP |
|---------|-------|----------|---------|
| 工作流引擎 | ✅ Flowable | ❌ 无 | ✅ 有 |
| 打印模板设计 | ✅ Fabric.js | ❌ 固定模板 | ✅ 有 |
| 认证方式 | ✅ JWT Resource Server | ⚠️ 简单认证 | ✅ OAuth2 Server |
| 权限粒度 | ✅ RBAC+数据权限框架 | ⚠️ 仅菜单 | ✅ 行列级 |
| 技术栈 | ✅ 最新 | ⚠️ 较老 | ✅ 较新 |

**说明：**
- 认证方式：本项目使用OAuth2 Resource Server + JWT，Token发放自实现，适合中小规模应用
- 权限粒度：数据权限框架已搭建，但实现较基础，需要进一步完善

**结论：** 项目在**架构搭建和技术选型**上质量较高，具备良好的扩展基础，主要问题在于**业务逻辑填充不够**。

---

## 三、存在哪些问题

### 3.1 数据库设计问题（严重）

#### 3.1.1 库存表过于简陋

**当前实现：**
```sql
CREATE TABLE jxc_inventory (
  inventory_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  warehouse_id BIGINT NOT NULL,
  sku_id BIGINT NOT NULL,
  qty INT DEFAULT 0,
  create_time DATETIME,
  update_time DATETIME
);
```

**问题分析：**
- ❌ 缺少 `batch_no`（批次号）- 无法追溯批次
- ❌ 缺少 `production_date`（生产日期）- 无法管理效期
- ❌ 缺少 `expiry_date`（失效日期）- 无法预警临期商品
- ❌ 缺少 `available_qty`（可用库存）- 无法区分锁定库存
- ❌ 缺少 `locked_qty`（锁定库存）- 预订单无法锁定库存
- ❌ 缺少 `cost_price`（成本价）- 无法核算库存成本
- ❌ 缺少 `location`（货位）- 无法快速拣货

**行业标准对比：**
主流进销存系统（如金蝶、用友、企云记）库存表都包含批次、效期、可用库存、成本价等字段。

#### 3.1.2 客户表缺失关键字段

**当前实现：**
```java
public class Customer {
    private Long customerId;
    private String customerName;
    private String contactName;
    private String contactPhone;
    private String address;
    private String level;
    private BigDecimal initialArrears;
    private Integer status;
}
```

**问题分析：**
- ❌ 缺少 `points`（积分）- PRD要求支持积分兑换
- ❌ 缺少 `balance`（储值余额）- PRD要求支持储值消费
- ❌ 缺少 `credit_limit`（信用额度）- 无法控制超额赊销
- ❌ 缺少 `credit_days`（账期天数）- 无法管理应收账款账龄
- ❌ 缺少 `level_id`（客户等级ID）- 无法实现差异化定价
- ❌ 缺少 `salesperson_id`（业务员ID）- 无法实现客户池隔离

**影响：**
- 无法实现PRD承诺的客户储值与积分功能
- 无法实现客户信用控制
- 无法实现客户池隔离（业务员只能看自己的客户）

#### 3.1.3 应收应付表设计不合理

**当前实现：**
```java
public class Receivable {
    private Long receivableId;
    private String billNo;
    private Long customerId;
    private BigDecimal amount;
    private BigDecimal receivedAmount;
    private BigDecimal remainingAmount;
    private Integer status;
}
```

**问题分析：**
- ❌ 缺少 `bill_type`（源单据类型）- 无法区分销售单/退货单产生的应收
- ❌ 缺少 `bill_id`（源单据ID）- 无法追溯具体单据
- ❌ 缺少核销明细表 - 无法记录核销历史
- ❌ 缺少账龄分析字段 - 无法做应收账龄分析

#### 3.1.4 缺少批次管理表

**问题：** 整个数据库中没有批次管理相关的表结构。

**影响：**
- 无法实现批次追溯
- 无法实现先进先出（FIFO）/先失效先出（FEFO）
- 无法管理临期商品

---

### 3.2 业务逻辑实现不完整（严重）

#### 3.2.1 库存服务过于简单

**当前实现：**
```java
@Service
public class InventoryServiceImpl {
    
    @Override
    public PageResult<InventoryDTO> pageQuery(InventoryQuery query) {
        // 只有分页查询，35行代码
    }
}
```

**缺少核心功能：**
- ❌ 库存扣减/增加的事务控制
- ❌ 负库存校验（PRD要求：开启"不允许负库存"时需加锁）
- ❌ 库存锁定/解锁机制（预订单需要锁定库存）
- ❌ 批次管理（FIFO/FEFO策略）
- ❌ 效期预警
- ❌ 库存成本核算（移动加权平均/FIFO）
- ❌ 库存盘点差异处理
- ❌ 库存调拨逻辑

**代码位置：** `backend/src/main/java/com/duoduo/jxc/service/impl/InventoryServiceImpl.java`

#### 3.2.2 预订单转销售逻辑缺失

**当前实现：**
```java
@Transactional(rollbackFor = Exception.class)
public void convertToSales(Long bookingOrderId) {
    SalesOrderDTO booking = getDetail(bookingOrderId);
    // 创建新的销售单
    booking.setOrderType(1);
    booking.setOrderId(null);
    createOrder(booking);
    
    // 直接将预订单状态改为"已完成"
    SalesOrder updateBooking = new SalesOrder();
    updateBooking.setOrderId(bookingOrderId);
    updateBooking.setStatus(40);
    updateById(updateBooking);
}
```

**问题分析：**
1. ❌ 没有检查预订单是否全部发货（`unfulfilled_qty`应该为0）
2. ❌ 没有更新预订单明细的 `unfulfilled_qty`
3. ❌ 不支持分批发货的场景（PRD核心功能）
4. ❌ 没有库存锁定/解锁逻辑
5. ❌ 没有生成应收账款

**正确逻辑应该是：**
```java
@Transactional
public void convertBookingToSales(Long bookingOrderId, List<DeliveryDetail> deliveryDetails) {
    // 1. 查询预订单及其明细
    SalesOrderDTO booking = getDetail(bookingOrderId);
    
    // 2. 校验发货明细合法性
    validateDeliveryDetails(booking.getDetails(), deliveryDetails);
    
    // 3. 更新预订单明细的unfulfilled_qty
    updateUnfulfilledQty(bookingOrderId, deliveryDetails);
    
    // 4. 判断是否全部发货
    boolean allDelivered = checkAllDelivered(bookingOrderId);
    
    // 5. 创建销售单
    Long salesOrderId = createSalesOrder(booking, deliveryDetails);
    
    // 6. 扣减库存
    deductInventory(deliveryDetails);
    
    // 7. 生成应收账款
    createReceivable(salesOrderId);
    
    // 8. 如果全部发货，更新预订单状态为"已完成"
    if (allDelivered) {
        updateBookingStatus(bookingOrderId, 40);
    } else {
        updateBookingStatus(bookingOrderId, 30); // 部分发货
    }
}
```

**代码位置：** `backend/src/main/java/com/duoduo/jxc/service/impl/SalesOrderServiceImpl.java:192-210`

#### 3.2.3 财务核销功能不够完善

**实际实现情况：**

项目已有核销基础功能：

```java
// ✅ 已存在的核销功能
WriteOffController (54行) - 核销单CRUD接口
WriteOffServiceImpl (91行) - 核销单基础服务（分页查询、详情、创建、删除）
ReceivableServiceImpl.writeOff() (106-125行) - 应收核销方法
PayableServiceImpl.writeOff() (106-125行) - 应付核销方法
```

**已有的核销能力：**
- ✅ 核销单的基础CRUD
- ✅ 应收/应付的核销方法（更新状态和金额）
- ✅ 核销金额校验
- ✅ 核销后状态更新（未核销/部分核销/已核销）

**PRD要求与实际差距：**

| PRD要求 | 实现状态 | 说明 |
|--------|---------|------|
| 支持手动核销 | ✅ 已实现 | 可指定核销金额 |
| 支持"自动核销" | ❌ 未实现 | 缺少自动匹配算法 |
| 核销明细记录 | ❌ 未实现 | 缺少核销明细表 |
| 优惠抹零处理 | ❌ 未实现 | 无相关字段 |
| 预收款核销 | ❌ 未实现 | 缺少预收款管理 |

**缺少的核心功能：**
- ❌ 自动核销算法（按时间优先/金额优先自动匹配应收单和收款单）
- ❌ 核销明细表（无法追溯哪些应收单核销了哪些收款单）
- ❌ 优惠抹零处理
- ❌ 预收款核销
- ❌ 账龄分析支持

**当前核销方法实现：**
```java
// ReceivableServiceImpl.writeOff() - 106-125行
@Transactional(rollbackFor = Exception.class)
public void writeOff(Long id, BigDecimal amount) {
    Receivable entity = super.getById(id);
    if (entity.getRemainingAmount().compareTo(amount) < 0) {
        throw new BusinessException("核销金额不能大于剩余金额");
    }
    
    entity.setReceivedAmount(entity.getReceivedAmount().add(amount));
    entity.setRemainingAmount(entity.getRemainingAmount().subtract(amount));
    
    if (entity.getRemainingAmount().compareTo(BigDecimal.ZERO) == 0) {
        entity.setStatus(2); // 已核销
    } else {
        entity.setStatus(1); // 部分核销
    }
    updateById(entity);
}
```

**问题：**
- 只能手动指定核销金额，缺少自动匹配
- 没有记录核销明细（哪些收款单核销了哪些应收单）
- 缺少与收款单的关联

**优先级调整：** P0 → P1（功能存在但不完善）

**应该完善的核销服务：**
```java
@Service
public class WriteOffServiceImpl {
    
    public void autoWriteOff(Long customerId, WriteOffStrategy strategy) {
        // 按策略自动匹配应收单和收款单
        // 1. 查询客户的应收单（按时间/金额排序）
        // 2. 查询客户的收款单
        // 3. 按策略匹配核销
        // 4. 记录核销明细
        // 5. 更新应收单状态
    }
    
    public void manualWriteOff(List<Long> receivableIds, List<Long> receiptIds) {
        // 手动指定核销
        // 1. 校验金额匹配
        // 2. 记录核销明细
        // 3. 更新应收单状态
    }
}
```

#### 3.2.4 销售单审核逻辑不完整

**当前实现：**
```java
@Transactional(rollbackFor = Exception.class)
public void auditOrder(Long orderId) {
    // 只更新状态，没有实际业务逻辑
    SalesOrder order = new SalesOrder();
    order.setOrderId(orderId);
    order.setStatus(30);
    order.setAuditBy(exist.getOperatorId());
    order.setAuditTime(LocalDateTime.now());
    updateById(order);
    
    // 启动工作流（如果有）
    if (binding != null && binding.getEnabled() == 1) {
        workflowService.startInstance(req);
    }
}
```

**缺少逻辑：**
- ❌ 没有扣减库存
- ❌ 没有生成应收账款
- ❌ 没有更新客户的欠款余额
- ❌ 没有记录库存流水

---

### 3.3 前后端功能不匹配（中等）

#### 3.3.1 前端有UI，后端无支持

**示例1：商品批次**
```vue
<!-- frontend/src/views/sales/order/add.vue:179 -->
<el-table-column label="商品批次" width="100" align="center" />
```
- 前端显示了"商品批次"列
- 但数据库没有批次字段
- 后端没有批次管理服务

**示例2：兑换积分**
```vue
<!-- frontend/src/views/sales/order/add.vue -->
<el-table-column label="兑换积分" width="100" />
```
- 前端显示"兑换积分"
- 但客户表没有积分字段
- 后端没有积分管理服务

#### 3.3.2 矩阵录入功能不完整

**前端实现：**
```vue
<!-- frontend/src/views/sales/order/add.vue:181-185 -->
<el-table-column v-for="(label, idx) in sizeColumnLabels" :label="label">
  <el-input v-model="scope.row['col'+(idx+1)]" @input="updateRowQty(scope.row)" />
</el-table-column>
```

**问题：**
1. ❌ 后端没有矩阵数据的持久化（矩阵模板）
2. ❌ 没有矩阵列配置保存
3. ❌ 缺少前端到后端的数据扁平化转换（PRD明确要求）

**PRD要求：**
> 前端矩阵录入在提交时，必须在前端或 BFF 层**扁平化**为标准的 SKU 明细行一维数组

---

### 3.4 报表功能严重不足（中等）

#### 3.4.1 只有一个销售日报

**当前实现：**
```java
@RestController
@RequestMapping("/report")
public class ReportController {
    
    @GetMapping("/sales-daily")
    public Result salesDailyReport(SalesReportQuery query) {
        // 只有28行代码
    }
}
```

**行业标准报表对比：**

| 报表类型 | 行业标准 | 当前实现 | 缺失情况 |
|---------|---------|---------|---------|
| **库存报表** | 库存明细表、库存汇总表、库存周转率、库存预警表 | ❌ 无 | 全部缺失 |
| **采购报表** | 采购统计、供应商对账、采购成本分析 | ❌ 无 | 全部缺失 |
| **销售报表** | 销售排行、客户分析、毛利分析、销售日报 | ✅ 仅销售日报 | 缺失75% |
| **财务报表** | 应收账龄、应付账龄、资金流水、经营分析 | ❌ 无 | 全部缺失 |
| **经营报表** | 利润表、资产负债表、现金流表 | ❌ 无 | 全部缺失 |

**代码位置：** `backend/src/main/java/com/duoduo/jxc/controller/report/ReportController.java`

---

### 3.5 代码质量问题（中等）

#### 3.5.1 Service层实现简陋

**统计：**
```
InventoryServiceImpl.java - 35行
FinanceTransactionServiceImpl.java - 75行
SalesOrderServiceImpl.java - 211行（但有大量CRUD，业务逻辑不足）
```

**问题：** 大量Service只有CRUD，缺少业务逻辑。

#### 3.5.2 缺少事务控制

**问题代码：**
```java
@Transactional(rollbackFor = Exception.class)
public void updateOrder(SalesOrderDTO dto) {
    updateById(order);
    
    // 先删除所有明细
    detailMapper.delete(deleteWrapper);
    
    // 再重新插入
    detailEntities.forEach(d -> {
        detailMapper.insert(d);
    });
}
```

**风险：** 如果插入过程中某个失败，原有数据已删除，无法恢复。

**改进方案：**
```java
@Transactional(rollbackFor = Exception.class)
public void updateOrder(SalesOrderDTO dto) {
    // 1. 查询原订单
    SalesOrder oldOrder = getById(dto.getOrderId());
    
    // 2. 如果已审核，先恢复库存
    if (oldOrder.getStatus() >= 30) {
        restoreInventory(oldOrder);
    }
    
    // 3. 更新订单主表
    updateById(converter.toEntity(dto));
    
    // 4. 更新明细（增量更新，不是全删全插）
    updateDetails(dto.getOrderId(), dto.getDetails());
    
    // 5. 如果已审核，重新扣减库存
    if (dto.getStatus() >= 30) {
        deductInventory(dto);
    }
}
```

#### 3.5.3 异常处理不规范

**问题：**
```java
// 直接抛出BusinessException
throw new BusinessException(BizCode.SALES_ORDER_NOT_FOUND);
```

但很多地方没有捕获和处理，导致前端收到500错误。

---

### 3.6 并发安全问题（严重）

#### 3.6.1 库存扣减没有锁

**问题场景：**
```
时刻T1: 销售单A读取库存 qty=100
时刻T2: 销售单B读取库存 qty=100
时刻T3: 销售单A扣减库存 qty=90（销售10个）
时刻T4: 销售单B扣减库存 qty=90（销售10个）
结果: 实际销售20个，但库存只扣减10个
```

**缺少机制：**
- ❌ 悲观锁（SELECT ... FOR UPDATE）
- ❌ 乐观锁（version字段）
- ❌ 分布式锁（Redis/Redisson）

**解决方案：**
```java
@Transactional
@RedisLock(key = "'inventory:' + #warehouseId + ':' + #skuId")
public void deductStock(Long warehouseId, Long skuId, Integer qty) {
    Inventory inv = inventoryMapper.selectOne(
        new LambdaQueryWrapper<Inventory>()
            .eq(Inventory::getWarehouseId, warehouseId)
            .eq(Inventory::getSkuId, skuId)
    );
    
    if (inv.getQty() < qty) {
        throw new BusinessException("库存不足");
    }
    
    inv.setQty(inv.getQty() - qty);
    inventoryMapper.updateById(inv);
}
```

#### 3.6.2 单据编号生成有重复风险

**问题代码：**
```java
// SalesOrderServiceImpl.createOrder():100
order.setDocNo(prefix + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + System.currentTimeMillis());
```

**风险：**
- 同一毫秒内创建多个订单会重复
- 单机环境下概率低，但分布式环境必现

**解决方案：**
```java
@RedisLock(key = "'order:no:' + #prefix")
public String generateOrderNo(String prefix) {
    String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    Long seq = redisTemplate.opsForValue()
        .increment("order:seq:" + prefix + ":" + date);
    return prefix + date + String.format("%04d", seq);
}
```

---

### 3.7 权限控制粒度不足（中等）

**当前实现：**
- ✅ 集成了Spring Security + OAuth2
- ✅ 实现了RBAC角色权限

**缺少功能：**
- ❌ 数据权限（门店隔离/客户池隔离）
- ❌ 字段级权限（进价对业务员隐藏）
- ❌ 操作权限（审核/反审核/作废）

**PRD要求：**
> 支持行级与列级的数据遮罩：
> - 进价/成本可见性：对业务员/收银员隐藏进货价和库存成本总额
> - 客户池隔离：业务员仅能查看和操作自己名下的客户资料及单据
> - 多仓隔离：门店员工仅可见本门店所属仓库的库存

---

## 四、功能是否有缺失

### 4.1 核心业务功能缺失（对比PRD）

#### 4.1.1 批次管理（PRD要求）

**PRD原文：**
> 批次与效期管理：支持快消品行业的生产日期、失效日期记录，实时计算"剩余有效天数"。

**当前状态：**
- ❌ 批次号生成规则
- ❌ 批次追溯
- ❌ 批次出库策略（FIFO/FEFO）
- ❌ 数据库没有批次相关表

#### 4.1.2 效期管理（PRD要求）

**PRD原文：**
> 预警中心：保质期临期预警。

**当前状态：**
- ❌ 生产日期记录
- ❌ 失效日期记录
- ❌ 临期预警
- ❌ 过期锁定
- ❌ 剩余有效天数计算

#### 4.1.3 组装拆卸单（PRD要求）

**PRD原文：**
> 形态转换（组装拆卸单）：解决礼盒打包或整箱拆零销售场景。

**当前状态：**
- ✅ 有 `TransferOrder` 实体
- ❌ 没有 Service 实现
- ❌ 缺少成本分摊逻辑
- ❌ 缺少防死锁校验（原商品与新商品同SKU）

#### 4.1.4 客户储值与积分（PRD要求）

**PRD原文：**
> 客户储值与积分：支持预付卡充值消费及积分兑换，沉淀 C 端流量。

**当前状态：**
- ❌ 储值卡管理
- ❌ 充值/消费记录
- ❌ 积分累计/兑换
- ❌ 积分规则配置
- ❌ 数据库客户表没有 `points`、`balance` 字段

**前端已有UI：**
```vue
<!-- frontend/src/views/sales/customer-recharge/index.vue -->
<!-- 但后端没有实现 -->
```

---

### 4.2 行业标准ERP功能缺失

#### 4.2.1 价格管理体系

**行业标准：**
- 多价格体系（批发价/零售价/会员价/协议价）
- 价格历史
- 价格审批流程
- 促销定价

**当前状态：**
- ✅ SKU表有 `purchase_price`、`wholesale_price`、`retail_price`
- ❌ 没有价格历史表
- ❌ 没有价格审批
- ❌ 没有促销管理

#### 4.2.2 促销管理

**行业标准：**
- 促销活动配置
- 买赠/满减/折扣
- 促销效果分析

**当前状态：**
- ❌ 完全缺失

#### 4.2.3 采购管理

**当前状态：**
- ✅ 有采购单、采购预定单
- ❌ 缺少采购申请流程
- ❌ 缺少供应商评估体系
- ❌ 缺少采购建议（智能补货）

#### 4.2.4 销售管理

**当前状态：**
- ✅ 有销售单、销售预订单、销售退货单
- ❌ 缺少销售提成计算
- ❌ 缺少销售目标管理
- ❌ 缺少客户信用控制
- ❌ 缺少超额赊销拦截

#### 4.2.5 财务管理

**当前状态：**
- ✅ 有应收应付实体
- ✅ 有资金账户、资金流水
- ❌ 缺少自动生成凭证
- ❌ 缺少期末结账
- ❌ 缺少成本核算
- ❌ 缺少利润计算

#### 4.2.6 多组织架构

**当前状态：**
- ✅ 有 `store_id` 字段
- ❌ 缺少多门店切换UI
- ❌ 缺少门店间调拨
- ❌ 缺少独立核算

---

### 4.3 移动端缺失

**PRD要求：**
> 多端实时协同：PC 端主攻后台管理，APP/小程序主攻移动开单与看盘。

**当前状态：**
- ❌ 没有配套APP/小程序
- ❌ 无法移动开单
- ❌ 无法移动盘点
- ❌ 无法移动审批

---

### 4.4 对外集成能力缺失

**行业标准：**
- 开放API文档
- 第三方系统对接（电商平台/WMS/财务软件）
- 银企直连
- 电子发票

**当前状态：**
- ❌ 没有API文档
- ❌ 没有对接能力
- ❌ 完全缺失

---

## 五、建议怎么优化

### 5.1 数据库设计优化（优先级：最高）

#### 5.1.1 完善库存表

```sql
-- 添加字段
ALTER TABLE jxc_inventory ADD COLUMN (
  batch_no VARCHAR(50) COMMENT '批次号',
  production_date DATE COMMENT '生产日期',
  expiry_date DATE COMMENT '失效日期',
  available_qty INT DEFAULT 0 COMMENT '可用库存',
  locked_qty INT DEFAULT 0 COMMENT '锁定库存',
  cost_price DECIMAL(10,4) COMMENT '成本价',
  location VARCHAR(50) COMMENT '货位'
);

-- 创建批次明细表
CREATE TABLE jxc_inventory_batch (
  batch_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
  sku_id BIGINT NOT NULL COMMENT 'SKU ID',
  batch_no VARCHAR(50) NOT NULL COMMENT '批次号',
  production_date DATE COMMENT '生产日期',
  expiry_date DATE COMMENT '失效日期',
  qty INT DEFAULT 0 COMMENT '数量',
  available_qty INT DEFAULT 0 COMMENT '可用库存',
  locked_qty INT DEFAULT 0 COMMENT '锁定库存',
  cost_price DECIMAL(10,4) COMMENT '成本价',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_batch (warehouse_id, sku_id, batch_no),
  KEY idx_expiry_date (expiry_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存批次明细表';

-- 创建库存流水表
CREATE TABLE jxc_inventory_transaction (
  transaction_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  warehouse_id BIGINT NOT NULL,
  sku_id BIGINT NOT NULL,
  batch_no VARCHAR(50),
  trans_type TINYINT NOT NULL COMMENT '类型(1入库2出库3调拨4盘点5锁定6解锁)',
  qty INT NOT NULL COMMENT '数量(正数)',
  before_qty INT COMMENT '变动前库存',
  after_qty INT COMMENT '变动后库存',
  cost_price DECIMAL(10,4) COMMENT '成本价',
  bill_type VARCHAR(20) COMMENT '源单据类型',
  bill_id BIGINT COMMENT '源单据ID',
  bill_no VARCHAR(50) COMMENT '源单据编号',
  operator_id BIGINT COMMENT '操作人ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY idx_warehouse_sku (warehouse_id, sku_id),
  KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存流水表';
```

#### 5.1.2 完善客户表

```sql
ALTER TABLE jxc_customer ADD COLUMN (
  points INT DEFAULT 0 COMMENT '积分',
  balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '储值余额',
  credit_limit DECIMAL(10,2) COMMENT '信用额度',
  credit_days INT COMMENT '账期天数',
  level_id BIGINT COMMENT '客户等级ID',
  salesperson_id BIGINT COMMENT '业务员ID',
  tax_no VARCHAR(50) COMMENT '税号',
  bank_name VARCHAR(100) COMMENT '开户行',
  bank_account VARCHAR(50) COMMENT '银行账号'
);

-- 创建客户储值记录表
CREATE TABLE jxc_customer_balance_log (
  log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  customer_id BIGINT NOT NULL,
  trans_type TINYINT NOT NULL COMMENT '类型(1充值2消费3退款)',
  amount DECIMAL(10,2) NOT NULL,
  before_balance DECIMAL(10,2),
  after_balance DECIMAL(10,2),
  bill_type VARCHAR(20),
  bill_id BIGINT,
  bill_no VARCHAR(50),
  remark VARCHAR(255),
  operator_id BIGINT,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY idx_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户储值流水表';

-- 创建客户积分记录表
CREATE TABLE jxc_customer_points_log (
  log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  customer_id BIGINT NOT NULL,
  trans_type TINYINT NOT NULL COMMENT '类型(1获得2消费3过期)',
  points INT NOT NULL,
  before_points INT,
  after_points INT,
  bill_type VARCHAR(20),
  bill_id BIGINT,
  bill_no VARCHAR(50),
  remark VARCHAR(255),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY idx_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户积分流水表';
```

#### 5.1.3 创建核销明细表

```sql
CREATE TABLE jxc_write_off_detail (
  detail_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  write_off_id BIGINT NOT NULL COMMENT '核销ID',
  receivable_id BIGINT NOT NULL COMMENT '应收单ID',
  receipt_id BIGINT COMMENT '收款单ID',
  payment_id BIGINT COMMENT '付款单ID',
  amount DECIMAL(10,2) NOT NULL COMMENT '核销金额',
  discount_amount DECIMAL(10,2) DEFAULT 0 COMMENT '优惠抹零',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY idx_receivable_id (receivable_id),
  KEY idx_receipt_id (receipt_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='核销明细表';
```

---

### 5.2 核心业务逻辑完善（优先级：最高）

#### 5.2.1 库存服务重构

**文件位置：** `backend/src/main/java/com/duoduo/jxc/service/impl/InventoryServiceImpl.java`

```java
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    
    private final InventoryMapper inventoryMapper;
    private final InventoryBatchMapper batchMapper;
    private final InventoryTransactionMapper transMapper;
    private final RedissonClient redissonClient;
    
    /**
     * 扣减库存（支持分布式锁）
     */
    @Transactional(rollbackFor = Exception.class)
    public void deductStock(Long warehouseId, Long skuId, Integer qty, 
                           String batchNo, String billType, Long billId, String billNo) {
        // 1. 获取分布式锁
        String lockKey = "inventory:" + warehouseId + ":" + skuId;
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            lock.lock(10, TimeUnit.SECONDS);
            
            // 2. 查询库存
            Inventory inv = inventoryMapper.selectOne(
                new LambdaQueryWrapper<Inventory>()
                    .eq(Inventory::getWarehouseId, warehouseId)
                    .eq(Inventory::getSkuId, skuId)
            );
            
            if (inv == null || inv.getAvailableQty() < qty) {
                throw new BusinessException("库存不足");
            }
            
            // 3. 扣减库存
            inv.setQty(inv.getQty() - qty);
            inv.setAvailableQty(inv.getAvailableQty() - qty);
            inventoryMapper.updateById(inv);
            
            // 4. 扣减批次库存（如果有批次管理）
            if (batchNo != null) {
                deductBatchStock(warehouseId, skuId, batchNo, qty);
            }
            
            // 5. 记录库存流水
            InventoryTransaction trans = new InventoryTransaction();
            trans.setWarehouseId(warehouseId);
            trans.setSkuId(skuId);
            trans.setBatchNo(batchNo);
            trans.setTransType(2); // 出库
            trans.setQty(qty);
            trans.setBeforeQty(inv.getQty() + qty);
            trans.setAfterQty(inv.getQty());
            trans.setBillType(billType);
            trans.setBillId(billId);
            trans.setBillNo(billNo);
            transMapper.insert(trans);
            
            // 6. 检查库存预警
            checkInventoryAlert(warehouseId, skuId, inv.getQty());
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 锁定库存（预订单用）
     */
    @Transactional(rollbackFor = Exception.class)
    public void lockStock(Long warehouseId, Long skuId, Integer qty, String orderId) {
        RLock lock = redissonClient.getLock("inventory:" + warehouseId + ":" + skuId);
        
        try {
            lock.lock(10, TimeUnit.SECONDS);
            
            Inventory inv = inventoryMapper.selectOne(
                new LambdaQueryWrapper<Inventory>()
                    .eq(Inventory::getWarehouseId, warehouseId)
                    .eq(Inventory::getSkuId, skuId)
            );
            
            if (inv.getAvailableQty() < qty) {
                throw new BusinessException("可用库存不足");
            }
            
            inv.setAvailableQty(inv.getAvailableQty() - qty);
            inv.setLockedQty(inv.getLockedQty() + qty);
            inventoryMapper.updateById(inv);
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 批次扣减（FEFO策略）
     */
    private void deductBatchStock(Long warehouseId, Long skuId, String batchNo, Integer qty) {
        // 如果batchNo为空，自动选择最早失效的批次
        if (batchNo == null) {
            List<InventoryBatch> batches = batchMapper.selectList(
                new LambdaQueryWrapper<InventoryBatch>()
                    .eq(InventoryBatch::getWarehouseId, warehouseId)
                    .eq(InventoryBatch::getSkuId, skuId)
                    .gt(InventoryBatch::getAvailableQty, 0)
                    .orderByAsc(InventoryBatch::getExpiryDate) // FEFO
            );
            
            int remaining = qty;
            for (InventoryBatch batch : batches) {
                if (remaining <= 0) break;
                
                int deductQty = Math.min(batch.getAvailableQty(), remaining);
                batch.setQty(batch.getQty() - deductQty);
                batch.setAvailableQty(batch.getAvailableQty() - deductQty);
                batchMapper.updateById(batch);
                
                remaining -= deductQty;
            }
        } else {
            // 指定批次扣减
            InventoryBatch batch = batchMapper.selectOne(
                new LambdaQueryWrapper<InventoryBatch>()
                    .eq(InventoryBatch::getWarehouseId, warehouseId)
                    .eq(InventoryBatch::getSkuId, skuId)
                    .eq(InventoryBatch::getBatchNo, batchNo)
            );
            
            if (batch == null || batch.getAvailableQty() < qty) {
                throw new BusinessException("批次库存不足");
            }
            
            batch.setQty(batch.getQty() - qty);
            batch.setAvailableQty(batch.getAvailableQty() - qty);
            batchMapper.updateById(batch);
        }
    }
}
```

#### 5.2.2 预订单转销售完善

**文件位置：** `backend/src/main/java/com/duoduo/jxc/service/impl/SalesOrderServiceImpl.java`

```java
/**
 * 预订单转销售（支持分批发货）
 */
@Transactional(rollbackFor = Exception.class)
public void convertBookingToSales(Long bookingOrderId, List<DeliveryDetailDTO> deliveryDetails) {
    // 1. 查询预订单
    SalesOrderDTO booking = getDetail(bookingOrderId);
    if (booking == null || booking.getOrderType() != 3) {
        throw new BusinessException(BizCode.BOOKING_ORDER_INVALID);
    }
    
    // 2. 校验发货明细合法性
    validateDeliveryDetails(booking.getDetails(), deliveryDetails);
    
    // 3. 更新预订单明细的unfulfilled_qty
    for (DeliveryDetailDTO delivery : deliveryDetails) {
        SalesOrderDetail detail = detailMapper.selectById(delivery.getDetailId());
        int newUnfulfilledQty = detail.getUnfulfilledQty() - delivery.getQty();
        
        if (newUnfulfilledQty < 0) {
            throw new BusinessException("发货数量超过预定数量");
        }
        
        detail.setUnfulfilledQty(newUnfulfilledQty);
        detailMapper.updateById(detail);
    }
    
    // 4. 判断是否全部发货
    boolean allDelivered = checkAllDelivered(bookingOrderId);
    
    // 5. 创建销售单
    SalesOrderDTO salesOrder = new SalesOrderDTO();
    salesOrder.setOrderType(1); // 批发单
    salesOrder.setCustomerId(booking.getCustomerId());
    salesOrder.setDocDate(LocalDate.now());
    salesOrder.setStatus(10); // 草稿
    
    List<SalesOrderDetailDTO> salesDetails = deliveryDetails.stream()
        .map(d -> {
            SalesOrderDetailDTO detail = new SalesOrderDetailDTO();
            detail.setSkuId(d.getSkuId());
            detail.setSpuId(d.getSpuId());
            detail.setQty(d.getQty());
            detail.setUnitPrice(d.getUnitPrice());
            detail.setWarehouseId(d.getWarehouseId());
            return detail;
        })
        .collect(Collectors.toList());
    salesOrder.setDetails(salesDetails);
    
    Long salesOrderId = createOrder(salesOrder);
    
    // 6. 扣减库存
    for (DeliveryDetailDTO delivery : deliveryDetails) {
        inventoryService.deductStock(
            delivery.getWarehouseId(),
            delivery.getSkuId(),
            delivery.getQty(),
            delivery.getBatchNo(),
            "SALES_ORDER",
            salesOrderId,
            salesOrder.getDocNo()
        );
    }
    
    // 7. 生成应收账款
    SalesOrder savedSalesOrder = getById(salesOrderId);
    createReceivable(savedSalesOrder);
    
    // 8. 更新预订单状态
    SalesOrder updateBooking = new SalesOrder();
    updateBooking.setOrderId(bookingOrderId);
    updateBooking.setStatus(allDelivered ? 40 : 30); // 已完成/部分发货
    updateById(updateBooking);
}

/**
 * 校验发货明细
 */
private void validateDeliveryDetails(List<SalesOrderDetailDTO> bookingDetails, 
                                     List<DeliveryDetailDTO> deliveryDetails) {
    Map<Long, Integer> bookedQtyMap = bookingDetails.stream()
        .collect(Collectors.toMap(SalesOrderDetailDTO::getDetailId, 
                                 SalesOrderDetailDTO::getUnfulfilledQty));
    
    for (DeliveryDetailDTO delivery : deliveryDetails) {
        Integer remaining = bookedQtyMap.get(delivery.getDetailId());
        if (remaining == null || delivery.getQty() > remaining) {
            throw new BusinessException("发货数量超过未转销售数量");
        }
    }
}

/**
 * 检查是否全部发货
 */
private boolean checkAllDelivered(Long bookingOrderId) {
    List<SalesOrderDetail> details = detailMapper.selectList(
        new LambdaQueryWrapper<SalesOrderDetail>()
            .eq(SalesOrderDetail::getOrderId, bookingOrderId)
    );
    
    return details.stream()
        .allMatch(d -> d.getUnfulfilledQty() == 0);
}
```

#### 5.2.3 财务核销实现

**新建文件：** `backend/src/main/java/com/duoduo/jxc/service/impl/WriteOffServiceImpl.java`

```java
@Service
@RequiredArgsConstructor
public class WriteOffServiceImpl implements WriteOffService {
    
    private final ReceivableMapper receivableMapper;
    private final ReceiptMapper receiptMapper;
    private final WriteOffMapper writeOffMapper;
    private final WriteOffDetailMapper writeOffDetailMapper;
    
    /**
     * 自动核销（按时间优先）
     */
    @Transactional(rollbackFor = Exception.class)
    public void autoWriteOff(Long customerId, WriteOffStrategy strategy) {
        // 1. 查询客户的应收单（按策略排序）
        List<Receivable> receivables = receivableMapper.selectList(
            new LambdaQueryWrapper<Receivable>()
                .eq(Receivable::getCustomerId, customerId)
                .gt(Receivable::getRemainingAmount, BigDecimal.ZERO)
                .orderByAsc(strategy == WriteOffStrategy.TIME ? 
                           Receivable::getBillDate : Receivable::getAmount)
        );
        
        // 2. 查询客户的收款单
        List<Receipt> receipts = receiptMapper.selectList(
            new LambdaQueryWrapper<Receipt>()
                .eq(Receipt::getCustomerId, customerId)
                .eq(Receipt::getStatus, 0) // 未核销
                .orderByAsc(Receipt::getReceiptDate)
        );
        
        // 3. 匹配核销
        for (Receipt receipt : receipts) {
            BigDecimal remainingReceipt = receipt.getAmount();
            
            for (Receivable receivable : receivables) {
                if (remainingReceipt.compareTo(BigDecimal.ZERO) <= 0) break;
                if (receivable.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) continue;
                
                // 计算本次核销金额
                BigDecimal writeOffAmount = remainingReceipt.min(receivable.getRemainingAmount());
                
                // 创建核销记录
                createWriteOffRecord(receivable, receipt, writeOffAmount);
                
                // 更新应收单
                receivable.setReceivedAmount(receivable.getReceivedAmount().add(writeOffAmount));
                receivable.setRemainingAmount(receivable.getRemainingAmount().subtract(writeOffAmount));
                receivable.setStatus(receivable.getRemainingAmount().compareTo(BigDecimal.ZERO) == 0 ? 2 : 1);
                receivableMapper.updateById(receivable);
                
                // 更新剩余收款金额
                remainingReceipt = remainingReceipt.subtract(writeOffAmount);
            }
            
            // 更新收款单状态
            receipt.setStatus(remainingReceipt.compareTo(BigDecimal.ZERO) == 0 ? 1 : 0);
            receiptMapper.updateById(receipt);
        }
    }
    
    /**
     * 手动核销
     */
    @Transactional(rollbackFor = Exception.class)
    public void manualWriteOff(List<Long> receivableIds, List<Long> receiptIds, 
                               BigDecimal discountAmount) {
        // 1. 查询应收单
        List<Receivable> receivables = receivableMapper.selectBatchIds(receivableIds);
        BigDecimal totalReceivable = receivables.stream()
            .map(Receivable::getRemainingAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 2. 查询收款单
        List<Receipt> receipts = receiptMapper.selectBatchIds(receiptIds);
        BigDecimal totalReceipt = receipts.stream()
            .map(Receipt::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 3. 校验金额匹配
        if (totalReceipt.add(discountAmount).compareTo(totalReceivable) < 0) {
            throw new BusinessException("核销金额不足");
        }
        
        // 4. 创建核销记录
        WriteOff writeOff = new WriteOff();
        writeOff.setCustomerId(receivables.get(0).getCustomerId());
        writeOff.setTotalAmount(totalReceivable);
        writeOff.setWriteOffAmount(totalReceipt);
        writeOff.setDiscountAmount(discountAmount);
        writeOffMapper.insert(writeOff);
        
        // 5. 记录核销明细
        for (Receivable receivable : receivables) {
            for (Receipt receipt : receipts) {
                WriteOffDetail detail = new WriteOffDetail();
                detail.setWriteOffId(writeOff.getWriteOffId());
                detail.setReceivableId(receivable.getReceivableId());
                detail.setReceiptId(receipt.getReceiptId());
                detail.setAmount(receivable.getRemainingAmount());
                writeOffDetailMapper.insert(detail);
            }
            
            // 更新应收单状态
            receivable.setStatus(2); // 已核销
            receivable.setRemainingAmount(BigDecimal.ZERO);
            receivableMapper.updateById(receivable);
        }
        
        // 6. 更新收款单状态
        for (Receipt receipt : receipts) {
            receipt.setStatus(1); // 已核销
            receiptMapper.updateById(receipt);
        }
    }
}
```

---

### 5.3 报表体系完善（优先级：高）

**新建文件：** `backend/src/main/java/com/duoduo/jxc/controller/report/ReportController.java`

```java
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {
    
    private final ReportService reportService;
    
    /**
     * 库存汇总表
     */
    @GetMapping("/inventory/summary")
    @PreAuthorize("@perm.has('report:inventory:view')")
    public Result<PageResult<InventorySummaryDTO>> inventorySummary(InventoryReportQuery query) {
        return Result.success(reportService.getInventorySummary(query));
    }
    
    /**
     * 库存明细表
     */
    @GetMapping("/inventory/detail")
    @PreAuthorize("@perm.has('report:inventory:view')")
    public Result<PageResult<InventoryDetailDTO>> inventoryDetail(InventoryReportQuery query) {
        return Result.success(reportService.getInventoryDetail(query));
    }
    
    /**
     * 库存周转率分析
     */
    @GetMapping("/inventory/turnover")
    @PreAuthorize("@perm.has('report:inventory:view')")
    public Result<List<InventoryTurnoverDTO>> inventoryTurnover(InventoryReportQuery query) {
        return Result.success(reportService.getInventoryTurnover(query));
    }
    
    /**
     * 销售毛利分析
     */
    @GetMapping("/sales/profit")
    @PreAuthorize("@perm.has('report:sales:view')")
    public Result<PageResult<SalesProfitDTO>> salesProfitAnalysis(SalesReportQuery query) {
        return Result.success(reportService.getSalesProfitAnalysis(query));
    }
    
    /**
     * 销售排行榜
     */
    @GetMapping("/sales/ranking")
    @PreAuthorize("@perm.has('report:sales:view')")
    public Result<SalesRankingDTO> salesRanking(SalesReportQuery query) {
        return Result.success(reportService.getSalesRanking(query));
    }
    
    /**
     * 客户分析
     */
    @GetMapping("/customer/analysis")
    @PreAuthorize("@perm.has('report:customer:view')")
    public Result<List<CustomerAnalysisDTO>> customerAnalysis(ReportQuery query) {
        return Result.success(reportService.getCustomerAnalysis(query));
    }
    
    /**
     * 应收账龄分析
     */
    @GetMapping("/finance/receivable-aging")
    @PreAuthorize("@perm.has('report:finance:view')")
    public Result<List<ReceivableAgingDTO>> receivableAging(ReportQuery query) {
        return Result.success(reportService.getReceivableAging(query));
    }
    
    /**
     * 应付账龄分析
     */
    @GetMapping("/finance/payable-aging")
    @PreAuthorize("@perm.has('report:finance:view')")
    public Result<List<PayableAgingDTO>> payableAging(ReportQuery query) {
        return Result.success(reportService.getPayableAging(query));
    }
    
    /**
     * 采购统计
     */
    @GetMapping("/purchase/statistics")
    @PreAuthorize("@perm.has('report:purchase:view')")
    public Result<PageResult<PurchaseStatisticsDTO>> purchaseStatistics(PurchaseReportQuery query) {
        return Result.success(reportService.getPurchaseStatistics(query));
    }
    
    /**
     * 供应商对账
     */
    @GetMapping("/supplier/statement")
    @PreAuthorize("@perm.has('report:purchase:view')")
    public Result<SupplierStatementDTO> supplierStatement(SupplierStatementQuery query) {
        return Result.success(reportService.getSupplierStatement(query));
    }
    
    /**
     * 经营概况（首页看板）
     */
    @GetMapping("/business/overview")
    @PreAuthorize("@perm.has('report:business:view')")
    public Result<BusinessOverviewDTO> businessOverview(ReportQuery query) {
        return Result.success(reportService.getBusinessOverview(query));
    }
}
```

---

### 5.4 并发安全优化（优先级：高）

#### 5.4.1 库存并发控制方案

**问题场景：**
```
时刻T1: 销售单A读取库存 qty=100
时刻T2: 销售单B读取库存 qty=100
时刻T3: 销售单A扣减库存 qty=90（销售10个）
时刻T4: 销售单B扣减库存 qty=90（销售10个）
结果: 实际销售20个，但库存只扣减10个 → 超卖
```

**方案选择建议：**

| 方案 | 适用场景 | 实现复杂度 | 性能 | 说明 |
|-----|---------|-----------|------|------|
| **乐观锁** | 单机部署、中小规模 | ⭐ | ⭐⭐⭐⭐ | 初期推荐 |
| **悲观锁** | 单机部署、高并发 | ⭐⭐ | ⭐⭐⭐ | 简单可靠 |
| **分布式锁** | 集群部署、大规模 | ⭐⭐⭐⭐ | ⭐⭐ | 进阶方案 |

#### 方案一：乐观锁（初期推荐）

**适用场景：** 单机部署、中小规模业务、并发冲突较少

**实现步骤：**

1. **数据库添加version字段**
```sql
ALTER TABLE jxc_inventory ADD COLUMN version INT DEFAULT 0 COMMENT '乐观锁版本号';
```

2. **实体类添加version字段**
```java
@Data
public class Inventory {
    // ... 其他字段
    @Version
    private Integer version;
}
```

3. **Service实现**
```java
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl {
    
    private final InventoryMapper inventoryMapper;
    
    /**
     * 扣减库存（乐观锁）
     * 优点：无锁等待，性能好
     * 缺点：高并发时重试多
     */
    @Transactional(rollbackFor = Exception.class)
    public void deductStock(Long warehouseId, Long skuId, Integer qty, 
                           String billType, Long billId, String billNo) {
        int retryCount = 0;
        int maxRetry = 3;
        
        while (retryCount < maxRetry) {
            // 1. 查询库存
            Inventory inv = inventoryMapper.selectOne(
                new LambdaQueryWrapper<Inventory>()
                    .eq(Inventory::getWarehouseId, warehouseId)
                    .eq(Inventory::getSkuId, skuId)
            );
            
            if (inv == null || inv.getQty() < qty) {
                throw new BusinessException("库存不足");
            }
            
            // 2. 更新库存（乐观锁自动检查version）
            Inventory update = new Inventory();
            update.setInventoryId(inv.getInventoryId());
            update.setQty(inv.getQty() - qty);
            update.setVersion(inv.getVersion()); // MyBatis-Plus自动处理
            
            int updated = inventoryMapper.updateById(update);
            
            if (updated > 0) {
                // 3. 记录库存流水
                recordTransaction(inv, qty, billType, billId, billNo);
                return;
            }
            
            // 更新失败，version不匹配，重试
            retryCount++;
            try {
                Thread.sleep(50); // 短暂等待
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        throw new BusinessException("系统繁忙，请稍后重试");
    }
}
```

**优点：**
- 实现简单，无需引入额外组件
- 性能好，无锁等待
- MyBatis-Plus原生支持

**缺点：**
- 高并发时重试多，用户体验稍差
- 只适合单机部署

#### 方案二：悲观锁（单机高并发推荐）

**适用场景：** 单机部署、高并发、要求强一致性

**实现：**
```java
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl {
    
    /**
     * 扣减库存（悲观锁）
     * 优点：强一致性，无重试
     * 缺点：性能稍低
     */
    @Transactional(rollbackFor = Exception.class)
    public void deductStockWithPessimisticLock(Long warehouseId, Long skuId, Integer qty) {
        // 1. 使用SELECT FOR UPDATE加锁
        Inventory inv = inventoryMapper.selectOneForUpdate(warehouseId, skuId);
        
        if (inv == null || inv.getQty() < qty) {
            throw new BusinessException("库存不足");
        }
        
        // 2. 更新库存
        inv.setQty(inv.getQty() - qty);
        inventoryMapper.updateById(inv);
        
        // 3. 记录库存流水
        // ...
    }
}

// Mapper接口
public interface InventoryMapper extends BaseMapper<Inventory> {
    
    @Select("SELECT * FROM jxc_inventory WHERE warehouse_id = #{warehouseId} AND sku_id = #{skuId} FOR UPDATE")
    Inventory selectOneForUpdate(@Param("warehouseId") Long warehouseId, @Param("skuId") Long skuId);
}
```

**优点：**
- 强一致性保证
- 无重试，代码简洁

**缺点：**
- 性能稍低（数据库锁）
- 只适合单机部署

#### 方案三：分布式锁（进阶方案）

**适用场景：** 集群部署、大规模业务、多实例并发

**前置条件：**
- 集群部署（多个应用实例）
- 已引入Redis
- 对一致性要求极高

**实现步骤：**

1. **引入Redisson依赖**

```xml
<!-- backend/pom.xml -->
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
    <version>3.27.0</version>
</dependency>
```

2. **配置Redisson**
```yaml
# application.yml
spring:
  redis:
    host: localhost
    port: 6379
    
# redisson配置（可选，默认即可）
# 如果需要集群，配置为：
# spring:
#   redis:
#     cluster:
#       nodes: node1:6379,node2:6379,node3:6379
```

3. **分布式锁实现**
```java
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl {
    
    private final RedissonClient redissonClient;
    
    /**
     * 扣减库存（分布式锁）
     * 优点：支持集群部署
     * 缺点：引入额外组件，复杂度高
     */
    @Transactional(rollbackFor = Exception.class)
    public void deductStockWithDistributedLock(Long warehouseId, Long skuId, Integer qty) {
        String lockKey = "inventory:" + warehouseId + ":" + skuId;
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            // 尝试获取锁，最多等待5秒，锁自动过期时间10秒
            boolean acquired = lock.tryLock(5, 10, TimeUnit.SECONDS);
            
            if (!acquired) {
                throw new BusinessException("系统繁忙，请稍后重试");
            }
            
            // 1. 查询库存
            Inventory inv = inventoryMapper.selectOne(
                new LambdaQueryWrapper<Inventory>()
                    .eq(Inventory::getWarehouseId, warehouseId)
                    .eq(Inventory::getSkuId, skuId)
            );
            
            if (inv == null || inv.getQty() < qty) {
                throw new BusinessException("库存不足");
            }
            
            // 2. 更新库存
            inv.setQty(inv.getQty() - qty);
            inventoryMapper.updateById(inv);
            
            // 3. 记录库存流水
            // ...
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("系统异常");
        } finally {
            // 释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
```

**优点：**
- 支持集群部署
- 高可用（Redis集群）
- 锁自动续期

**缺点：**
- 引入额外组件
- 复杂度高
- 需要考虑Redis故障场景

#### 方案选择建议

```
初期阶段（单机部署）：
  ├─ 并发量 < 100 QPS → 乐观锁
  └─ 并发量 > 100 QPS → 悲观锁

进阶阶段（集群部署）：
  └─ 分布式锁（Redisson）
```

**注意：** 对于中小微企业进销存系统，初期建议使用乐观锁，简单可靠。只有在业务规模增长到需要集群部署时，才考虑分布式锁。

#### 5.4.2 单据编号生成优化

**新建文件：** `backend/src/main/java/com/duoduo/jxc/annotation/RedisLock.java`

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLock {
    String key();
    long waitTime() default 0;
    long leaseTime() default 10;
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
```

**新建文件：** `backend/src/main/java/com/duoduo/jxc/aspect/RedisLockAspect.java`

```java
@Aspect
@Component
@RequiredArgsConstructor
public class RedisLockAspect {
    
    private final RedissonClient redissonClient;
    
    @Around("@annotation(redisLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
        String key = parseKey(joinPoint, redisLock.key());
        RLock lock = redissonClient.getLock(key);
        
        boolean acquired = lock.tryLock(
            redisLock.waitTime(), 
            redisLock.leaseTime(), 
            redisLock.timeUnit()
        );
        
        if (!acquired) {
            throw new BusinessException("系统繁忙，请稍后重试");
        }
        
        try {
            return joinPoint.proceed();
        } finally {
            lock.unlock();
        }
    }
    
    private String parseKey(ProceedingJoinPoint joinPoint, String keyExpression) {
        // 解析SpEL表达式
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new MethodBasedEvaluationContext(
            null, method, args, new DefaultParameterNameDiscoverer()
        );
        
        return parser.parseExpression(keyExpression).getValue(context, String.class);
    }
}
```

#### 5.4.3 单据编号生成优化

**新建文件：** `backend/src/main/java/com/duoduo/jxc/service/impl/DocNoGenerator.java`

```java
@Service
@RequiredArgsConstructor
public class DocNoGenerator {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 生成唯一单据编号
     */
    @RedisLock(key = "'doc:no:' + #prefix")
    public String generate(String prefix) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String key = "doc:no:seq:" + prefix + ":" + date;
        
        Long seq = redisTemplate.opsForValue().increment(key);
        
        // 设置过期时间（7天）
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
        
        return prefix + date + String.format("%04d", seq);
    }
}
```

**使用示例：**
```java
@Service
@RequiredArgsConstructor
public class SalesOrderServiceImpl {
    
    private final DocNoGenerator docNoGenerator;
    
    public Long createOrder(SalesOrderDTO dto) {
        SalesOrder order = converter.toEntity(dto);
        
        // 使用单据编号生成器
        String prefix = dto.getOrderType() == 3 ? "YD" : "XS";
        order.setDocNo(docNoGenerator.generate(prefix));
        
        save(order);
        return order.getOrderId();
    }
}
```

---

### 5.5 事务管理优化（优先级：中）

#### 5.5.1 订单更新优化

```java
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public void updateOrder(SalesOrderDTO dto) {
    // 1. 查询原订单
    SalesOrder oldOrder = getById(dto.getOrderId());
    if (oldOrder == null) {
        throw new BusinessException(BizCode.SALES_ORDER_NOT_FOUND);
    }
    
    // 2. 如果已审核，先恢复库存和应收
    if (oldOrder.getStatus() >= 30) {
        // 恢复库存
        restoreInventory(oldOrder);
        
        // 删除应收账款
        deleteReceivable(oldOrder.getOrderId());
    }
    
    // 3. 更新订单主表
    SalesOrder order = converter.toEntity(dto);
    updateById(order);
    
    // 4. 增量更新明细（不是全删全插）
    updateOrderDetails(dto.getOrderId(), dto.getDetails());
    
    // 5. 如果已审核，重新扣减库存和生成应收
    if (dto.getStatus() >= 30) {
        // 扣减库存
        deductInventory(dto);
        
        // 生成应收账款
        createReceivable(order);
    }
}

/**
 * 增量更新明细
 */
private void updateOrderDetails(Long orderId, List<SalesOrderDetailDTO> newDetails) {
    // 查询原明细
    List<SalesOrderDetail> oldDetails = detailMapper.selectList(
        new LambdaQueryWrapper<SalesOrderDetail>()
            .eq(SalesOrderDetail::getOrderId, orderId)
    );
    
    Set<Long> oldDetailIds = oldDetails.stream()
        .map(SalesOrderDetail::getDetailId)
        .collect(Collectors.toSet());
    
    Set<Long> newDetailIds = newDetails.stream()
        .filter(d -> d.getDetailId() != null)
        .map(SalesOrderDetailDTO::getDetailId)
        .collect(Collectors.toSet());
    
    // 删除不在新列表中的明细
    oldDetailIds.stream()
        .filter(id -> !newDetailIds.contains(id))
        .forEach(id -> detailMapper.deleteById(id));
    
    // 更新或插入明细
    for (SalesOrderDetailDTO detail : newDetails) {
        if (detail.getDetailId() != null && oldDetailIds.contains(detail.getDetailId())) {
            // 更新
            detailMapper.updateById(converter.toDetailEntity(detail));
        } else {
            // 插入
            SalesOrderDetail entity = converter.toDetailEntity(detail);
            entity.setDetailId(null);
            entity.setOrderId(orderId);
            detailMapper.insert(entity);
        }
    }
}
```

---

### 5.6 功能模块补充（优先级：中）

#### 5.6.1 批次管理模块

**新建Controller：** `backend/src/main/java/com/duoduo/jxc/controller/InventoryBatchController.java`

```java
@RestController
@RequestMapping("/inventory/batch")
@RequiredArgsConstructor
public class InventoryBatchController {
    
    private final InventoryBatchService batchService;
    
    /**
     * 批次入库
     */
    @PostMapping("/inbound")
    @PreAuthorize("@perm.has('inventory:batch:inbound')")
    public Result<Long> batchInbound(@RequestBody @Valid BatchInboundDTO dto) {
        return Result.success(batchService.batchInbound(dto));
    }
    
    /**
     * 效期预警
     */
    @GetMapping("/expiry-warning")
    @PreAuthorize("@perm.has('inventory:batch:view')")
    public Result<List<ExpiryWarningDTO>> expiryWarning() {
        return Result.success(batchService.getExpiryWarning());
    }
    
    /**
     * 批次追溯
     */
    @GetMapping("/trace/{batchNo}")
    @PreAuthorize("@perm.has('inventory:batch:view')")
    public Result<BatchTraceDTO> traceBatch(@PathVariable String batchNo) {
        return Result.success(batchService.traceBatch(batchNo));
    }
}
```

**Service实现：**
```java
@Service
@RequiredArgsConstructor
public class InventoryBatchServiceImpl implements InventoryBatchService {
    
    /**
     * 批次入库
     */
    @Transactional(rollbackFor = Exception.class)
    public Long batchInbound(BatchInboundDTO dto) {
        // 1. 生成批次号
        String batchNo = generateBatchNo(dto);
        
        // 2. 创建批次记录
        InventoryBatch batch = new InventoryBatch();
        batch.setWarehouseId(dto.getWarehouseId());
        batch.setSkuId(dto.getSkuId());
        batch.setBatchNo(batchNo);
        batch.setProductionDate(dto.getProductionDate());
        batch.setExpiryDate(dto.getExpiryDate());
        batch.setQty(dto.getQty());
        batch.setAvailableQty(dto.getQty());
        batch.setCostPrice(dto.getCostPrice());
        batchMapper.insert(batch);
        
        // 3. 更新库存汇总
        updateInventorySummary(dto.getWarehouseId(), dto.getSkuId(), dto.getQty(), dto.getCostPrice());
        
        // 4. 记录库存流水
        recordInventoryTransaction(batch, dto);
        
        return batch.getBatchId();
    }
    
    /**
     * 效期预警
     */
    public List<ExpiryWarningDTO> getExpiryWarning() {
        LocalDate warningDate = LocalDate.now().plusDays(30); // 30天内过期
        
        List<InventoryBatch> batches = batchMapper.selectList(
            new LambdaQueryWrapper<InventoryBatch>()
                .le(InventoryBatch::getExpiryDate, warningDate)
                .gt(InventoryBatch::getQty, 0)
                .orderByAsc(InventoryBatch::getExpiryDate)
        );
        
        return batches.stream()
            .map(this::toExpiryWarningDTO)
            .collect(Collectors.toList());
    }
}
```

#### 5.6.2 客户储值模块

**新建Controller：** `backend/src/main/java/com/duoduo/jxc/controller/CustomerBalanceController.java`

```java
@RestController
@RequestMapping("/customer/balance")
@RequiredArgsConstructor
public class CustomerBalanceController {
    
    private final CustomerBalanceService balanceService;
    
    /**
     * 客户充值
     */
    @PostMapping("/recharge")
    @PreAuthorize("@perm.has('customer:balance:recharge')")
    public Result recharge(@RequestBody @Valid CustomerRechargeDTO dto) {
        balanceService.recharge(dto);
        return Result.success();
    }
    
    /**
     * 储值消费
     */
    @PostMapping("/consume")
    @PreAuthorize("@perm.has('customer:balance:consume')")
    public Result consume(@RequestBody @Valid CustomerConsumeDTO dto) {
        balanceService.consume(dto);
        return Result.success();
    }
    
    /**
     * 储值流水查询
     */
    @GetMapping("/log")
    @PreAuthorize("@perm.has('customer:balance:view')")
    public Result<PageResult<CustomerBalanceLogDTO>> balanceLog(PageQuery query) {
        return Result.success(balanceService.getBalanceLog(query));
    }
}
```

**Service实现：**
```java
@Service
@RequiredArgsConstructor
public class CustomerBalanceServiceImpl implements CustomerBalanceService {
    
    /**
     * 客户充值
     */
    @Transactional(rollbackFor = Exception.class)
    public void recharge(CustomerRechargeDTO dto) {
        // 1. 查询客户
        Customer customer = customerMapper.selectById(dto.getCustomerId());
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        
        // 2. 更新余额
        BigDecimal beforeBalance = customer.getBalance();
        BigDecimal afterBalance = beforeBalance.add(dto.getAmount());
        customer.setBalance(afterBalance);
        customerMapper.updateById(customer);
        
        // 3. 记录流水
        CustomerBalanceLog log = new CustomerBalanceLog();
        log.setCustomerId(dto.getCustomerId());
        log.setTransType(1); // 充值
        log.setAmount(dto.getAmount());
        log.setBeforeBalance(beforeBalance);
        log.setAfterBalance(afterBalance);
        log.setBillType("RECHARGE");
        log.setBillNo(dto.getReceiptNo());
        log.setRemark(dto.getRemark());
        balanceLogMapper.insert(log);
        
        // 4. 记录资金流水
        createFinanceTransaction(dto);
    }
    
    /**
     * 储值消费
     */
    @Transactional(rollbackFor = Exception.class)
    public void consume(CustomerConsumeDTO dto) {
        Customer customer = customerMapper.selectById(dto.getCustomerId());
        
        // 校验余额
        if (customer.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new BusinessException("储值余额不足");
        }
        
        // 扣减余额
        BigDecimal beforeBalance = customer.getBalance();
        BigDecimal afterBalance = beforeBalance.subtract(dto.getAmount());
        customer.setBalance(afterBalance);
        customerMapper.updateById(customer);
        
        // 记录流水
        CustomerBalanceLog log = new CustomerBalanceLog();
        log.setCustomerId(dto.getCustomerId());
        log.setTransType(2); // 消费
        log.setAmount(dto.getAmount());
        log.setBeforeBalance(beforeBalance);
        log.setAfterBalance(afterBalance);
        log.setBillType(dto.getBillType());
        log.setBillId(dto.getBillId());
        log.setBillNo(dto.getBillNo());
        balanceLogMapper.insert(log);
    }
}
```

---

### 5.7 性能优化建议（优先级：中）

#### 5.7.1 数据库索引优化

```sql
-- 销售单
CREATE INDEX idx_sales_date ON jxc_sales_order(doc_date);
CREATE INDEX idx_sales_customer ON jxc_sales_order(customer_id);
CREATE INDEX idx_sales_status ON jxc_sales_order(status);
CREATE INDEX idx_sales_create_time ON jxc_sales_order(create_time);

-- 销售单明细
CREATE INDEX idx_sales_detail_sku ON jxc_sales_order_detail(sku_id);
CREATE INDEX idx_sales_detail_order ON jxc_sales_order_detail(order_id);

-- 库存
CREATE INDEX idx_inventory_sku ON jxc_inventory(sku_id);
CREATE INDEX idx_inventory_warehouse ON jxc_inventory(warehouse_id);

-- 库存流水
CREATE INDEX idx_inv_trans_warehouse_sku ON jxc_inventory_transaction(warehouse_id, sku_id);
CREATE INDEX idx_inv_trans_time ON jxc_inventory_transaction(create_time);

-- 应收应付
CREATE INDEX idx_receivable_customer ON jxc_receivable(customer_id);
CREATE INDEX idx_receivable_status ON jxc_receivable(status);

-- 客户
CREATE INDEX idx_customer_name ON jxc_customer(customer_name);
CREATE INDEX idx_customer_salesperson ON jxc_customer(salesperson_id);

-- 商品
CREATE INDEX idx_spu_category ON jxc_product_spu(category_id);
CREATE INDEX idx_spu_brand ON jxc_product_spu(brand_id);
CREATE INDEX idx_sku_code ON jxc_product_sku(sku_code);
CREATE INDEX idx_sku_spu ON jxc_product_sku(spu_id);
```

#### 5.7.2 缓存策略

**新建文件：** `backend/src/main/java/com/duoduo/jxc/config/CacheConfig.java`

```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        
        return RedisCacheManager.builder(factory)
            .cacheDefaults(config)
            .build();
    }
}
```

**使用示例：**
```java
@Service
public class ProductServiceImpl {
    
    @Cacheable(value = "product:sku", key = "#skuId")
    public ProductSkuDTO getSkuById(Long skuId) {
        // 热点商品缓存1小时
    }
    
    @CacheEvict(value = "product:sku", key = "#dto.skuId")
    public void updateSku(ProductSkuDTO dto) {
        // 更新时清除缓存
    }
}
```

---

### 5.8 代码质量提升（优先级：低）

#### 5.8.1 完善单元测试

**新建测试文件：** `backend/src/test/java/com/duoduo/jxc/service/SalesOrderServiceTest.java`

```java
@SpringBootTest
@Transactional
class SalesOrderServiceTest {
    
    @Autowired
    private SalesOrderService salesOrderService;
    
    @Autowired
    private InventoryService inventoryService;
    
    @Test
    void testCreateSalesOrder_Success() {
        SalesOrderDTO dto = new SalesOrderDTO();
        dto.setOrderType(1);
        dto.setCustomerId(1L);
        dto.setDetails(List.of(
            createDetail(1L, 1L, 10, new BigDecimal("100.00"))
        ));
        
        Long orderId = salesOrderService.createOrder(dto);
        
        assertNotNull(orderId);
        SalesOrderDTO saved = salesOrderService.getDetail(orderId);
        assertEquals(1, saved.getDetails().size());
    }
    
    @Test
    void testConvertBookingToSales_Success() {
        // 1. 创建预订单
        Long bookingId = createBookingOrder();
        
        // 2. 部分发货
        List<DeliveryDetailDTO> deliveryDetails = List.of(
            createDeliveryDetail(1L, 5) // 发货5个
        );
        
        salesOrderService.convertBookingToSales(bookingId, deliveryDetails);
        
        // 3. 验证预订单状态
        SalesOrderDTO booking = salesOrderService.getDetail(bookingId);
        assertEquals(30, booking.getStatus()); // 部分发货
        
        // 4. 验证明细的unfulfilled_qty
        assertEquals(5, booking.getDetails().get(0).getUnfulfilledQty());
    }
    
    @Test
    void testDeductInventory_Concurrent() throws InterruptedException {
        // 准备库存
        Long warehouseId = 1L;
        Long skuId = 1L;
        inventoryService.addStock(warehouseId, skuId, 100, null);
        
        // 并发扣减
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    inventoryService.deductStock(warehouseId, skuId, 10, null, "TEST", 1L, "TEST001");
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        
        // 验证最终库存
        InventoryDTO inventory = inventoryService.getInventory(warehouseId, skuId);
        assertEquals(0, inventory.getQty()); // 100 - 10*10 = 0
    }
    
    @Test
    void testDeductInventory_InsufficientStock() {
        // 库存不足时应该抛出异常
        assertThrows(BusinessException.class, () -> {
            inventoryService.deductStock(1L, 1L, 1000, null, "TEST", 1L, "TEST001");
        });
    }
}
```

#### 5.8.2 添加API文档

**添加依赖：**
```xml
<!-- backend/pom.xml -->
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
    <version>4.4.0</version>
</dependency>
```

**配置：**
```java
@Configuration
public class Knife4jConfig {
    
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("多多进销存API文档")
                .version("v1.0.0")
                .description("多多进销存系统后端API接口文档"))
            .addSecurityItem(new SecurityRequirement().addList("Bearer"))
            .components(new Components()
                .addSecuritySchemes("Bearer", 
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}
```

**Controller注解：**
```java
@RestController
@RequestMapping("/sales")
@Tag(name = "销售管理", description = "销售单、销售预订单相关接口")
public class SalesOrderController {
    
    @Operation(summary = "创建销售单", description = "创建新的销售单")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @PostMapping
    public Result<Long> createOrder(@RequestBody @Valid SalesOrderDTO dto) {
        return Result.success(salesOrderService.createOrder(dto));
    }
}
```

---

## 六、总结与建议

### 6.1 项目整体评估

| 维度 | 当前状态 | 行业标准 | 完成度 | 说明 |
|-----|---------|---------|-------|------|
| **技术架构** | 优秀 | 工作流+打印设计+JWT认证 | 85% | 架构质量高，亮点突出 |
| **数据库设计** | 基础字段缺失 | 批次、效期、成本、储值等 | 30% | 需要补充关键字段 |
| **核心业务逻辑** | 简单CRUD为主 | 库存扣减、核销、成本核算 | 30% | 业务逻辑填充不足 |
| **前端UI** | 基本完成 | 与后端功能匹配 | 70% | UI完整，但部分无后端支持 |
| **报表体系** | 仅1个 | 10+标准报表 | 10% | 严重不足 |
| **并发安全** | 无锁机制 | 乐观锁/分布式锁 | 0% | 需要立即处理 |
| **权限控制** | 完整RBAC | 数据权限、字段权限 | 60% | 框架完善，实现基础 |
| **核销功能** | 基础功能完整 | 自动核销、明细追溯 | 60% | 功能存在但不完善 |
| **整体完成度** | - | - | **50%** | 架构好，业务填充不足 |

**修正说明：**
- 完成度从40%调整为50%
- 补充技术架构维度（85%）- JWT认证 + 工作流 + 打印设计
- 核销功能从"缺失"修正为"不够完善"（60%）
- 权限控制说明：框架完善，但数据权限实现较基础

### 6.2 关键问题优先级（修正后）

#### P0（致命，必须立即修复）
1. **数据库设计缺失** - 批次、效期、储值、积分等关键字段
2. **库存扣减无并发控制** - 可能导致超卖
3. **预订单转销售逻辑错误** - 核心业务功能缺失

#### P1（严重，需要尽快修复）
1. **核销功能不够完善** - 缺少自动核销、明细追溯（从P0降级）
2. **报表体系严重不足** - 影响经营决策
3. **事务控制不完善** - 数据一致性风险
4. **单据编号生成风险** - 可能重复
5. **前后端功能不匹配** - 前端UI无后端支持

#### P2（中等，计划修复）
1. **权限粒度不足** - 数据权限、字段权限
2. **代码质量问题** - Service层逻辑简陋
3. **性能优化** - 索引、缓存
4. **移动端缺失** - 无法移动办公

#### P3（低优先级，长期优化）
1. **单元测试覆盖率** - 代码质量保障
2. **API文档** - 开发协作
3. **第三方集成** - 生态扩展

### 6.3 重构建议时间表

**第一阶段（1个月）：核心修复**
- 完善数据库设计（批次、效期、储值、积分）
- 实现库存扣减并发控制
- 完善预订单转销售逻辑
- 实现财务核销功能

**第二阶段（1个月）：功能补充**
- 完善报表体系（至少10个标准报表）
- 实现批次管理模块
- 实现客户储值模块
- 完善权限控制

**第三阶段（1个月）：性能优化**
- 数据库索引优化
- 引入缓存策略
- 事务控制优化
- 性能测试与调优

### 6.4 技术债务清单（修正后）

| 技术债务 | 影响 | 预计工作量 | 说明 |
|---------|------|-----------|------|
| 数据库设计重构 | 高 | 5人天 | 补充批次、效期、储值等字段 |
| 库存服务重构 | 高 | 5人天 | 实现并发控制、批次管理 |
| 预订单转销售完善 | 高 | 3人天 | 支持分批发货、校验逻辑 |
| 核销功能完善 | 中 | 3人天 | 添加自动核销、明细表 |
| 报表体系完善 | 中 | 10人天 | 至少10个标准报表 |
| 批次管理模块 | 中 | 5人天 | 批次入库、效期预警 |
| 客户储值模块 | 中 | 5人天 | 充值、消费、积分 |
| 并发控制集成 | 高 | 2人天 | 乐观锁或分布式锁 |
| 权限控制完善 | 中 | 5人天 | 数据权限、字段权限 |
| 单元测试补充 | 低 | 10人天 | 核心业务测试 |
| **总计** | - | **53人天** | 约1.5-2个月 |

### 6.5 最终建议

#### 项目现状评价

**架构层面（优秀）：**
- ✅ 前后端分离架构，技术栈先进（Spring Boot 3.2 + Vue 3.5）
- ✅ OAuth2 Resource Server + JWT，无状态认证（认证中心依赖外部系统或自实现）
- ✅ Flowable工作流引擎集成，在同类项目中少见
- ✅ Fabric.js打印模板设计，专业度高
- ✅ 完整的RBAC权限体系
- ✅ 375个Java文件 + 76个Vue文件，代码规模可观

**业务层面（不足）：**
- ⚠️ 数据库设计缺失关键字段（批次、效期、储值等）
- ⚠️ 核心业务逻辑填充不足（库存扣减、预订单转销售等）
- ⚠️ 报表体系严重缺失
- ⚠️ 并发安全机制缺失

**总体评价：**
项目在**架构搭建和技术选型**上质量较高，具备良好的扩展基础，主要问题在于**业务逻辑填充不够**。这是一个"框架好、业务薄"的项目，而非"很差"的项目。

#### 建议方向

**第一阶段（1-2个月）：补齐核心短板**
1. **完善数据库设计**（批次、效期、储值、积分）
2. **实现库存并发控制**（推荐初期使用乐观锁）
3. **完善预订单转销售逻辑**（支持分批发货）
4. **完善核销功能**（添加自动核销、明细追溯）

**第二阶段（1个月）：丰富功能模块**
1. **完善报表体系**（至少10个标准报表）
2. **实现批次管理模块**
3. **实现客户储值模块**
4. **补充移动端**（可选）

**第三阶段（持续）：质量提升**
1. **补充单元测试**
2. **完善API文档**
3. **性能优化**（索引、缓存）
4. **代码质量改进**

#### 重要提示

1. **不要推翻重来** - 项目架构质量较好，应在现有基础上完善
2. **分阶段推进** - 先解决核心问题，再补充外围功能
3. **考虑实际规模** - 对于中小微企业进销存，初期用乐观锁即可，不必过度工程化
4. **关注投资回报** - 优先修复影响业务闭环的功能，报表可逐步补充

#### 技术选型建议

| 功能 | 初期方案 | 进阶方案 | 说明 |
|-----|---------|---------|------|
| 库存并发控制 | 乐观锁 | 分布式锁 | 单机用乐观锁，集群用分布式锁 |
| 单据编号生成 | 数据库序列 | Redis序列 | 初期数据库即可 |
| 缓存策略 | 暂不引入 | Redis缓存 | 先优化SQL，再考虑缓存 |
| 报表导出 | 简单CSV | Excel模板 | 初期CSV够用 |

**预计时间：** 2-3个月的完善时间可达到生产级可用标准。

#### 项目亮点回顾

在指出问题的同时，也要看到项目的优点：
- 🌟 **工作流引擎** - 这是很多进销存项目没有的
- 🌟 **打印模板设计** - 可视化设计，专业度高
- 🌟 **JWT认证** - OAuth2 Resource Server + JWT，无状态设计
- 🌟 **现代化技术栈** - 技术选型先进，易于维护

这些亮点应该保留和发扬，在此基础上补充业务逻辑即可。

---

**报告编制人：** AI代码分析助手  
**分析日期：** 2026-03-27  
**分析方法：** 实际代码审查 + 行业标准对比  
**报告版本：** v1.3（第三次修正版：核心特性2/3描述失实；第6章OAuth2描述偏差）

**修正记录：**
- v1.3 (2026-03-27): 第三次修正（核心特性验证）
  - ✅ 修正核心特性描述：精细化欠货追踪、极简业财一体标记为"未实现"
  - ✅ 补充核心特性详细验证表格（1.2节）
  - ✅ 补充工作流审批回调说明：已实现回调但缺失业务逻辑
  - ✅ 补充退货模块说明：SalesReturnOrderServiceImpl 437行，功能较完整
  - ✅ 修正批零双线并行：从"已实现"改为"部分实现"
- v1.2 (2026-03-27): 第二次修正（OAuth2描述修正）
  - ✅ 修正OAuth2描述：从"自建认证中心"改为"Resource Server + JWT"
  - ✅ 补充安全架构说明：说明Token发放方式
  - ✅ 细化数据权限描述：框架完善但实现基础
  - ✅ 修正对比表格：更准确的认证方式描述
  - ✅ 共修正5处OAuth2相关描述
- v1.1 (2026-03-27): 第一次修正（核销功能修正）
  - ✅ 修正核销功能描述：从"缺失"改为"不够完善"
  - ✅ 调整项目完成度：从40%改为50%
  - ✅ 调整核销优先级：从P0降为P1
  - ✅ 补充项目亮点章节
  - ✅ 优化建议区分初期方案和进阶方案
  - ✅ 修正语气，更加客观
- v1.0 (2026-03-27): 初始版本
