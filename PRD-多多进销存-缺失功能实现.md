# 多多进销存系统 - 产品需求文档（PRD）

**文档版本：** v1.0
**编制依据：** 项目深度分析报告 v1.3
**目标读者：** 开发工程师
**技术规范：** 遵循现有代码风格 + 阿里巴巴 Java 开发规范

---

## 一、规范性说明

### 1.1 代码风格要求

所有新增代码必须遵循以下规范：

**包结构：**
```
com.duoduo.jxc.{module}.{Controller,Service,ServiceImpl,Mapper,Entity,DTO,Enum,Constant}
```

**类命名：**
- Controller：`SalesOrderController.java`
- Service 接口：`SalesOrderService.java`
- Service 实现：`SalesOrderServiceImpl.java`
- Entity：`SalesOrder.java`
- DTO：`SalesOrderDTO.java`、`SalesOrderDetailDTO.java`
- 枚举：`XxxStatusEnum.java`、`XxxTypeEnum.java`

**注解规范（Entity）：**
```java
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_xxx")
public class Xxx extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    // 字段注释使用 Javadoc 风格
}
```

**注解规范（Service）：**
```java
@Service
@RequiredArgsConstructor
public class XxxServiceImpl extends ServiceImpl<XxxMapper, Xxx> implements XxxService {

    @Transactional(rollbackFor = Exception.class)
    public void doSomething() {
        // 事务必须指定 rollbackFor = Exception.class
    }
}
```

**枚举规范：**
```java
@Getter
public enum XxxEnum implements DictEnum {
    STATUS_A(1, "状态A"),
    STATUS_B(2, "状态B");

    private final int code;    // 状态码用 int
    private final String label;

    XxxEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }
}
```

**异常抛出：**
```java
throw new BusinessException(BizCode.XXX_ERROR);
```
必须在 `BizCode` 枚举中定义对应错误码，遵循现有分段规则：
- 销售：45xxx
- 库存：60xxx
- 财务：70xxx

---

## 二、功能性需求

---

### 需求一：精细化欠货追踪

#### 2.1.1 背景与目标

**PRD 原述：** 预定→分批发货→未转数量追踪

**当前实现问题（见分析报告 v1.3）：**
- `unfulfilledQty` 仅在创建预订单（orderType=3）时写入初始值，之后从不更新
- `convertToSales()` 全量转单，不支持部分转换
- 预订单状态从 10 直接跳到 40，绕过"执行中(30)"

**改造目标：**
1. 预订单审核后**锁定库存**
2. 支持**按发货明细部分转销售**，实时扣减 `unfulfilledQty`
3. 只有当 `unfulfilledQty = 0` 时才允许将预订单状态改为"已完成(40)"
4. 支持查看每个预订单的"欠货明细"

---

#### 2.1.2 数据库变更

**新增表：`jxc_booking_delivery`（预订单发货记录表）**

```sql
CREATE TABLE jxc_booking_delivery (
    delivery_id     BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '发货记录ID',
    booking_order_id BIGINT NOT NULL COMMENT '预订单ID',
    booking_detail_id BIGINT NOT NULL COMMENT '预订单明细ID',
    convert_qty     INT NOT NULL COMMENT '本次转化数量',
    sales_order_id  BIGINT COMMENT '关联的销售单ID（转单后填写）',
    warehouse_id    BIGINT COMMENT '出库仓库ID',
    operator_id     BIGINT COMMENT '操作人ID',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_booking_order_id (booking_order_id),
    KEY idx_booking_detail_id (booking_detail_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预订单发货记录表';
```

**变更表：`jxc_sales_order_detail`**

新增字段 `delivery_qty INT DEFAULT 0 COMMENT '已转发货数量'`，用于记录每个明细行已转换的数量。

```sql
ALTER TABLE jxc_sales_order_detail
    ADD COLUMN delivery_qty INT DEFAULT 0 COMMENT '已转发货数量（仅预订单有效）';
```

---

#### 2.1.3 接口变更

**新增 Controller 方法**

```java
// 预订单发货 Controller
@RestController
@RequestMapping("/sales/booking")
@RequiredArgsConstructor
public class SalesBookingController {

    private final SalesBookingService salesBookingService;

    /**
     * 预订单分批发货（支持部分发货）
     * POST /sales/booking/{bookingOrderId}/delivery
     */
    @PostMapping("/{bookingOrderId}/delivery")
    public Result<Long> partialDelivery(
            @PathVariable Long bookingOrderId,
            @RequestBody BookingDeliveryDTO dto) {
        return Result.success(salesBookingService.partialDelivery(bookingOrderId, dto));
    }

    /**
     * 查询预订单欠货明细
     * GET /sales/booking/{bookingOrderId}/unfulfilled
     */
    @GetMapping("/{bookingOrderId}/unfulfilled")
    public Result<List<BookingUnfulfilledDTO>> getUnfulfilled(@PathVariable Long bookingOrderId) {
        return Result.success(salesBookingService.getUnfulfilled(bookingOrderId));
    }
}
```

**新增 DTO**

```java
@Data
public class BookingDeliveryDTO {
    /** 本次发货明细列表 */
    private List<BookingDeliveryItemDTO> items;
}

@Data
public class BookingDeliveryItemDTO {
    /** 预订单明细ID */
    private Long bookingDetailId;
    /** 本次转化数量 */
    private Integer convertQty;
    /** 出库仓库ID */
    private Long warehouseId;
    /** 预设销售单价（可覆盖预订单单价） */
    private BigDecimal unitPrice;
}

@Data
public class BookingUnfulfilledDTO {
    private Long detailId;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private Integer bookedQty;      // 预定量
    private Integer deliveryQty;     // 已转发货量
    private Integer unfulfilledQty; // 未转数量
}
```

---

#### 2.1.4 核心业务逻辑（Service 层）

**`SalesBookingService.partialDelivery(bookingOrderId, dto)` 逻辑规范：**

```
1. 参数校验
   - dto.items 不能为空
   - 每个 item.convertQty > 0
   - 累加所有 convertQty 不超过对应明细的 unfulfilledQty

2. 查询预订单及明细（带行锁 SELECT FOR UPDATE）
   - 校验状态必须为 RUNNING(30)
   - 校验明细匹配

3. 扣减库存（调用 InventoryService）
   - 遍历 items，调用 deductStock(warehouseId, skuId, convertQty, ...)
   - 库存不足时抛 BusinessException(BizCode.STOCK_INSUFFICIENT)

4. 为每个 item 创建发货记录
   - 插入 jxc_booking_delivery

5. 更新 jxc_sales_order_detail
   - delivery_qty += convertQty
   - unfulfilledQty -= convertQty
   - 校验 unfulfilledQty >= 0

6. 创建新的销售单（调用 SalesOrderService.createOrder）
   - orderType = WHOLESALE(1)
   - details 从 dto.items 转换而来

7. 判断是否全部发货完成
   - 如果所有明细的 unfulfilledQty == 0：更新预订单状态为 FINISHED(40)
   - 否则保持 RUNNING(30)

8. 事务保证：整个过程在一个 @Transactional 中
```

**`InventoryService.deductStock` 规范（新增方法）：**

```
需支持：
- 悲观锁（SELECT FOR UPDATE）防止并发超卖
- 扣减前校验 available_qty >= convertQty
- 记录库存流水（INSERT jxc_inventory_transaction）
- 扣减后检查库存预警
```

---

#### 2.1.5 代码参考位置

- 现有 `SalesOrderServiceImpl.convertToSales()` → `backend/.../service/impl/SalesOrderServiceImpl.java:192-210`
- 现有 `InventoryServiceImpl` → 只有分页查询，需新增扣减逻辑
- 现有 `SalesOrderStatusEnum` → `backend/.../enums/SalesOrderStatusEnum.java`
- BizCode 销售补充段 45xxx 有预留码，可直接使用

---

### 需求二：极简业财一体

#### 2.2.1 背景与目标

**PRD 原述：** 业务单据自动生成财务流水

**当前实现问题（见分析报告 v1.3）：**
- `SalesOrderServiceImpl.auditOrder()` 只改 status + 启动 Flowable，不生成任何财务记录
- `ReceivableService` / `FinanceTransactionService` 与 `SalesOrderService` 完全独立
- `SalesOrder` 的 `settleStatus`、`paidAmount` 始终为 null

**改造目标：**
1. 销售单审核（`auditOrder`）时，**自动生成应收款记录**（Receivable）
2. 销售退货单审核时，**自动生成红冲应收**（负数金额）
3. 收款单核销后，**自动生成资金流水**（FinanceTransaction）
4. 采购单审核时，自动生成应付记录（Payable）

---

#### 2.2.2 数据库变更

**变更表：`jxc_receivable`**

```sql
ALTER TABLE jxc_receivable
    ADD COLUMN source_type VARCHAR(20) COMMENT '来源单据类型：SALES_ORDER/PURCHASE_ORDER/SALES_RETURN',
    ADD COLUMN source_id BIGINT COMMENT '来源单据ID',
    ADD COLUMN source_doc_no VARCHAR(50) COMMENT '来源单据编号',
    ADD COLUMN customer_id BIGINT COMMENT '客户ID冗余字段（便于查询）';
```

**变更表：`jxc_payable`**

```sql
-- Payable 表结构与 Receivable 类似，同步新增 source_type, source_id, source_doc_no, supplier_id
```

**新增表：`jxc_receivable_source`（应收来源明细，用于追溯一笔应收由哪些明细行组成）**

```sql
CREATE TABLE jxc_receivable_source (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    receivable_id   BIGINT NOT NULL COMMENT '应收单ID',
    order_id        BIGINT COMMENT '关联订单ID',
    detail_id       BIGINT COMMENT '关联订单明细ID',
    sku_id          BIGINT COMMENT '商品SKU ID',
    qty             INT COMMENT '数量',
    unit_price      DECIMAL(10,4) COMMENT '单价',
    line_amount     DECIMAL(12,2) COMMENT '金额',
    KEY idx_receivable_id (receivable_id),
    KEY idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应收来源明细表';
```

---

#### 2.2.3 核心业务逻辑

**销售单审核 → 生成应收款（`SalesOrderServiceImpl.auditOrder()` 改造）**

```
// 在 auditOrder() 现有逻辑之后，新增：

// 1. 仅 orderType == WHOLESALE(1) 或 BOOKING(3) 才生成应收
//    orderType == RETAIL(2) 零售单走日结模式，暂不生成应收
if (order.getOrderType() != 2) {
    Receivable receivable = new Receivable();
    receivable.setSourceType("SALES_ORDER");
    receivable.setSourceId(order.getOrderId());
    receivable.setSourceDocNo(order.getDocNo());
    receivable.setCustomerId(order.getCustomerId());
    // 从 SalesOrder 查 customerName 填充 customerName
    receivable.setAmount(order.getActualAmount());
    receivable.setReceivedAmount(BigDecimal.ZERO);
    receivable.setRemainingAmount(order.getActualAmount());
    receivable.setBillDate(order.getDocDate());
    // 设置到期日 = billDate + 客户账期天数（查 customer.credit_days）
    receivable.setStatus(ReceivableStatusEnum.UNPAID.getValue());
    receivableService.create(receivable);
}

// 2. 记录库存流水（销售出库）
for (SalesOrderDetail detail : details) {
    inventoryTransactionService.record(
        detail.getWarehouseId(),
        detail.getSkuId(),
        TransactionType.OUT,  // 出库
        detail.getQty(),
        order.getDocNo(),
        "SALES_ORDER",
        order.getOrderId()
    );
}

// 3. 更新订单的 settle_status = 0 (未核销)
```

**收款核销 → 生成资金流水（`ReceivableServiceImpl.writeOff()` 改造）**

```
// 在 writeOff() 现有逻辑之后，新增：

// 查询应收对应的资金账户（客户有默认资金账户字段）
// 记录收入资金流水
FinanceTransaction trans = new FinanceTransaction();
trans.setAccountId(accountId);
trans.setAccountName(accountName);
trans.setType(TransactionTypeEnum.INCOME.getValue()); // 1-收入
trans.setAmount(amount);
trans.setCategory("RECEIVABLE_WRITE_OFF");
trans.setBillType("RECEIVABLE");
trans.setBillNo(receivable.getBillNo());
trans.setTransactionDate(LocalDateTime.now());
trans.setCreateTime(LocalDateTime.now());
financeTransactionService.create(trans);
```

---

#### 2.2.4 BizCode 新增

在 `BizCode` 枚举的财务段（70xxx）新增：

```java
// ---- 财务补充 72xxx ----
RECEIVABLE_NOT_FOUND(72001, "应收单不存在"),
PAYABLE_NOT_FOUND(72002, "应付单不存在"),
RECEIVABLE_AMOUNT_MISMATCH(72003, "核销金额与应收金额不匹配"),
INVENTORY_TRANSACTION_FAILED(72004, "库存流水记录失败"),
CUSTOMER_NOT_FOUND(72005, "客户不存在"),
SUPPLIER_NOT_FOUND(72006, "供应商不存在"),
```

---

### 需求三：批零双线并行（零售差异化逻辑）

#### 2.3.1 背景与目标

**当前实现问题（见分析报告 v1.3）：**
- 前端有独立零售页面，但后端 `SalesOrderServiceImpl` 对 orderType=1 和 orderType=2 处理完全相同
- 零售单特有的日结、对账等业务逻辑不存在

**改造目标：**
1. 零售单（orderType=2）审核后，**不生成应收**（日结日清）
2. 零售单审核后，**生成资金流水**（直接收现）
3. 支持零售日结功能：将当日所有零售单按收款方式汇总
4. 零售单与批发单的报表维度分开

---

#### 2.3.2 数据库变更

**新增表：`jxc_retail_daily_settlement`（零售日结单）**

```sql
CREATE TABLE jxc_retail_daily_settlement (
    settlement_id   BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日结单ID',
    settlement_no   VARCHAR(50) NOT NULL COMMENT '日结单号 RS+yyyyMMdd+seq',
    store_id        BIGINT COMMENT '门店ID',
    settlement_date DATE NOT NULL COMMENT '结算日期',
    cash_amount     DECIMAL(12,2) DEFAULT 0 COMMENT '现金收款',
    wechat_amount  DECIMAL(12,2) DEFAULT 0 COMMENT '微信收款',
    alipay_amount   DECIMAL(12,2) DEFAULT 0 COMMENT '支付宝收款',
    card_amount     DECIMAL(12,2) DEFAULT 0 COMMENT '银行卡收款',
    other_amount    DECIMAL(12,2) DEFAULT 0 COMMENT '其他收款',
    total_amount    DECIMAL(12,2) NOT NULL COMMENT '合计收款',
    order_count     INT NOT NULL COMMENT '零售单数量',
    operator_id     BIGINT COMMENT '操作人ID',
    status          TINYINT DEFAULT 1 COMMENT '状态 1-正常 0-作废',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_settlement_date (settlement_date),
    KEY idx_store_id (store_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='零售日结单表';

CREATE TABLE jxc_retail_daily_settlement_order (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    settlement_id       BIGINT NOT NULL,
    sales_order_id      BIGINT NOT NULL,
    sales_order_doc_no  VARCHAR(50),
    payment_method      VARCHAR(20) COMMENT 'CASH/WECHAT/ALIPAY/CARD/OTHER',
    amount              DECIMAL(12,2) NOT NULL,
    create_time         DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_settlement_id (settlement_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日结单关联的零售单明细';
```

---

#### 2.3.3 零售单审核逻辑改造

**`SalesOrderServiceImpl.auditOrder()` 中增加 orderType 分支：**

```java
@Override
@Transactional(rollbackFor = Exception.class)
public void auditOrder(Long orderId) {
    // ... 现有校验逻辑不变 ...

    // ========== 新增：orderType 分支处理 ==========
    if (order.getOrderType() == 2) {
        // ========== 零售单：日结日清，不生成应收 ==========
        // 1. 生成资金流水（收入）
        FinanceTransaction trans = new FinanceTransaction();
        trans.setAccountId(order.getStoreId()); // 或配置的默认账户
        trans.setType(TransactionTypeEnum.INCOME.getValue()); // 收入
        trans.setAmount(order.getActualAmount());
        trans.setCategory("RETAIL_SALES");
        trans.setBillType("SALES_ORDER");
        trans.setBillNo(order.getDocNo());
        trans.setTransactionDate(LocalDateTime.now());
        trans.setCreateTime(LocalDateTime.now());
        financeTransactionService.create(trans);

        // 2. 更新订单 paid_amount = actual_amount, settle_status = 2 (已核销)
        order.setPaidAmount(order.getActualAmount());
        order.setSettleStatus(2);
    } else {
        // ========== 批发单/预订单：生成应收 ==========
        // 见"需求二"章节
    }
    // =============================================
}
```

**新增日结 Controller：**

```java
@RestController
@RequestMapping("/retail/settlement")
@RequiredArgsConstructor
public class RetailSettlementController {

    private final RetailSettlementService settlementService;

    /**
     * 日结（按日期汇总零售单）
     * POST /retail/settlement/daily
     * Body: { settlementDate: "2026-03-27", storeId: 1 }
     */
    @PostMapping("/daily")
    public Result<Long> createDailySettlement(@RequestBody RetailSettlementDTO dto) {
        return Result.success(settlementService.createDailySettlement(dto));
    }

    /**
     * 查询日结单列表
     * GET /retail/settlement/page
     */
    @GetMapping("/page")
    public Result<PageResult<RetailSettlementDTO>> pageQuery(RetailSettlementQuery query) {
        return Result.success(settlementService.pageQuery(query));
    }
}
```

---

### 需求四：库存并发安全

#### 2.4.1 背景与目标

**当前问题：** `InventoryServiceImpl` 只有分页查询，无任何库存扣减/锁定逻辑。多人同时开单时存在超卖风险。

**改造目标：**
1. 新增 `deductStock()` 悲观锁扣减库存（SELECT FOR UPDATE）
2. 新增 `lockStock()` 预订单库存锁定
3. 新增 `unlockStock()` 预订单库存解锁（取消/完成预订单时）
4. 新增 `addStock()` 入库增加库存

---

#### 2.4.2 数据库变更

**变更表：`jxc_inventory`**

```sql
ALTER TABLE jxc_inventory
    ADD COLUMN available_qty INT DEFAULT 0 COMMENT '可用库存',
    ADD COLUMN locked_qty INT DEFAULT 0 COMMENT '已锁定库存';
```

> **说明**：原有 `qty` 字段含义改为"总库存"，`available_qty = qty - locked_qty`。

**新增表：`jxc_inventory_transaction`（库存流水）**

```sql
CREATE TABLE jxc_inventory_transaction (
    transaction_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    warehouse_id   BIGINT NOT NULL,
    sku_id         BIGINT NOT NULL,
    trans_type     TINYINT NOT NULL COMMENT '类型：1-入库 2-出库 3-锁定 4-解锁 5-盘点调整',
    qty            INT NOT NULL COMMENT '变动数量（正数）',
    before_qty     INT COMMENT '变动前可用库存',
    after_qty      INT COMMENT '变动后可用库存',
    bill_type      VARCHAR(30) COMMENT '源单据类型',
    bill_id        BIGINT COMMENT '源单据ID',
    bill_no        VARCHAR(50) COMMENT '源单据编号',
    remark         VARCHAR(255),
    operator_id    BIGINT,
    create_time    DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_warehouse_sku (warehouse_id, sku_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存流水表';
```

---

#### 2.4.3 InventoryService 接口规范

```java
public interface InventoryService {

    /**
     * 扣减库存（悲观锁）
     *
     * @param warehouseId 仓库ID
     * @param skuId        SKU ID
     * @param qty          扣减数量
     * @param billType     源单据类型（如 SALES_ORDER）
     * @param billId       源单据ID
     * @param billNo       源单据编号
     */
    void deductStock(Long warehouseId, Long skuId, Integer qty,
                     String billType, Long billId, String billNo);

    /**
     * 锁定库存（预订单审核时调用）
     */
    void lockStock(Long warehouseId, Long skuId, Integer qty, Long orderId);

    /**
     * 解锁库存（预订单取消/完成时调用）
     */
    void unlockStock(Long warehouseId, Long skuId, Integer qty, Long orderId);

    /**
     * 增加库存（入库、采购退货）
     */
    void addStock(Long warehouseId, Long skuId, Integer qty,
                  String billType, Long billId, String billNo);
}
```

---

### 需求五：Flowable 审批回调

#### 2.5.1 背景与目标

**当前问题：** `auditOrder()` 启动 Flowable 实例后，审批完成时无人更新销售单状态（审批通过/驳回后状态不变）。

**改造目标：**
1. 新增 Flowable 审批完成回调接口
2. 审批通过：状态由 RUNNING(30) → FINISHED(40)
3. 驳回：保持原状态或退回草稿

---

#### 2.5.2 接口规范

**回调接口（`WorkflowCallbackController`）：**

```java
@RestController
@RequestMapping("/workflow/callback")
@RequiredArgsConstructor
public class WorkflowCallbackController {

    private final WorkflowCallbackService callbackService;

    /**
     * Flowable 流程结束回调
     * POST /workflow/callback/process/completed
     * Flowable TaskListener 或 ExecutionListener 调用此接口
     */
    @PostMapping("/process/completed")
    public Result<Void> onProcessCompleted(@RequestBody WorkflowCallbackDTO dto) {
        callbackService.onProcessCompleted(dto);
        return Result.success();
    }

    /**
     * 审批任务通过回调
     * POST /workflow/callback/task/approved
     */
    @PostMapping("/task/approved")
    public Result<Void> onTaskApproved(@RequestBody WorkflowCallbackDTO dto) {
        callbackService.onTaskApproved(dto);
        return Result.success();
    }

    /**
     * 审批任务驳回回调
     * POST /workflow/callback/task/rejected
     */
    @PostMapping("/task/rejected")
    public Result<Void> onTaskRejected(@RequestBody WorkflowCallbackDTO dto) {
        callbackService.onTaskRejected(dto);
        return Result.success();
    }
}
```

**`WorkflowCallbackDTO`：**

```java
@Data
public class WorkflowCallbackDTO {
    private String processInstanceId;
    private String bizType;       // 业务类型，如 SALES_ORDER
    private String bizId;         // 业务ID，如 123
    private String eventType;     // PROCESS_COMPLETED / TASK_APPROVED / TASK_REJECTED
    private String approverId;
    private String comment;
}
```

**`WorkflowCallbackService.onProcessCompleted()` 逻辑：**

```
1. 根据 bizType + bizId 查询业务单据
2. 如果是销售单（bizType == "SALES_ORDER"）：
   - 查询单据状态
   - 如果状态 == RUNNING(30)：改为 FINISHED(40)
   - 如果状态 == PENDING_AUDIT(20)（有审批流的情况）：改为 RUNNING(30)
3. 记录审批日志（可选）
```

---

## 三、非功能性需求

### 3.1 事务要求

- 所有涉及多表更新的操作（库存扣减 + 应收生成 + 订单状态更新）必须在同一个 `@Transactional(rollbackFor = Exception.class)` 中
- 库存扣减必须先 SELECT FOR UPDATE 再 UPDATE，不可用先 UPDATE 再校验的方式
- 库存扣减扣到 0 以下必须回滚

### 3.2 并发要求

- 库存扣减使用 `SELECT ... FOR UPDATE`（当前期单库方案）
- 分布式部署后升级为 Redis 分布式锁（Redisson）
- 单据编号生成使用数据库序列或 Redis INCR（不得使用 System.currentTimeMillis()）

### 3.3 日志要求

- 关键业务操作（审核、发货、核销）需要记录操作日志
- 使用 `@Slf4j` + `log.info()` / `log.error()`，不得用 `System.out`

### 3.4 代码组织

```
backend/src/main/java/com/duoduo/jxc/
├── service/
│   ├── sales/
│   │   ├── SalesBookingService.java       // 新增
│   │   └── impl/SalesBookingServiceImpl.java
│   ├── finance/
│   │   ├── FinanceIntegrationService.java // 新增：财务自动记账
│   │   └── impl/FinanceIntegrationServiceImpl.java
│   └── inventory/
│       ├── InventoryService.java         // 扩展现有
│       └── impl/InventoryServiceImpl.java
├── controller/
│   ├── SalesBookingController.java        // 新增
│   ├── RetailSettlementController.java    // 新增
│   └── WorkflowCallbackController.java    // 新增
└── enums/
    ├── BookingDeliveryStatusEnum.java     // 新增
    └── InventoryTransTypeEnum.java        // 新增
```

---

## 四、测试要求

### 4.1 单元测试覆盖（最低要求）

| 功能 | 测试场景 |
|------|---------|
| 预订单分批发货 | 正常部分发货；发货数量超限抛异常；并发发货超卖 |
| 库存扣减 | 库存充足；库存不足抛异常；并发扣减一致性 |
| 应收生成 | 批发单审核生成应收；零售单审核不生成应收 |
| 收款核销 | 核销生成资金流水；超额核销抛异常 |
| 日结 | 汇总当日所有零售单；日结后零售单状态更新 |

### 4.2 集成测试

- 销售单全流程：创建 → 审核 → 生成应收 → 收款核销 → 应收清零
- 预订单全流程：创建预订单 → 审核（锁定库存）→ 部分发货 → 继续发货 → 全部完成

---

## 五、优先级与排期建议

| 优先级 | 需求 | 预估人天 | 备注 |
|-------|------|---------|------|
| P0 | 库存并发安全（需求四） | 3天 | 所有后续功能的基础 |
| P0 | 精细化欠货追踪（需求一） | 5天 | PRD 核心功能 |
| P0 | 极简业财一体（需求二） | 4天 | PRD 核心功能 |
| P1 | 批零双线并行（需求三） | 3天 | 零售差异化逻辑 |
| P1 | Flowable 审批回调（需求五） | 2天 | 工作流闭环 |
| P2 | 日结功能（需求三延伸） | 2天 | 可独立上线 |

**总预估：19 人天（约 4 周）**

---

*本PRD由 AI 基于代码分析报告 v1.3 自动生成，确保每条需求可直接对应到现有代码位置和阿里巴巴 Java 规范。如有歧义，以现有代码实现和 `BizCode` 枚举分段规则为准。*
