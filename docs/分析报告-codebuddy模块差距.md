# codebuddy 模块差距分析报告

> **负责模块**: 采购(M04)、库存(M05)
> **对照文档**: `docs/split/13-采购管理模块.md`、`docs/split/14-库存管理详细设计.md`
> **分析日期**: 2026-03-29

---

## 一、采购管理模块（M04）

### 1.1 已实现的功能

| 功能点 | 实现文件 | 说明 |
|--------|----------|------|
| 采购订单 CRUD | `PurchaseOrderController` / `PurchaseOrderServiceImpl` | 分页查询、详情、新增、修改、删除 |
| 采购订单明细管理 | `PurchaseOrderDetail` Entity + Mapper | 按SKU维度采购，关联warehouseId |
| 采购订单审核 | `PurchaseOrderServiceImpl.auditOrder()` | 草稿→已完成状态流转 |
| 采购单据类型 | `PurchaseOrderTypeEnum` | 进货单(1)、进货预订单(2)、进货退货单(3) |
| 供应商基础管理 | `SupplierController` / `SupplierServiceImpl` | CRUD + 状态管理 |
| 单号自动生成 | `PurchaseOrderServiceImpl.createOrder()` | JH/JY 前缀 + 日期时间戳 |
| 金额计算 | `PurchaseOrderServiceImpl` | totalAmount、discountAmount、otherFee、actualAmount |
| 采购明细分页查询 | `PurchaseOrderController.detailPagePageQuery()` | 支持按SKU/SPU/单号/供应商筛选 |
| 前端页面 | `views/purchase/` | index.vue + add.vue + booking/ + return/ |
| 前端API | `api/purchase.ts` | 完整的7个接口封装 |

### 1.2 缺失的功能

#### 1.2.1 核心流程缺失

| 功能点 | 设计文档要求 | 影响 |
|--------|-------------|------|
| **采购申请** | 设计文档定义了独立的采购申请流程（申请→审批→转采购订单） | 当前只有采购订单，缺少申请环节，无法实现多级审批 |
| **采购到货入库** | 设计文档定义了到货入库流程（到货通知→质检→入库），与库存联动 | 采购订单审核后不产生入库操作，库存不会增加 |
| **采购退货出库** | 设计文档定义了采购退货流程，需与库存联动 | 退货单类型已定义(3)，但审核后不扣减库存 |
| **供应商评级** | 设计文档要求供应商评级（A/B/C/D级），影响采购策略 | 完全缺失，无法按供应商质量分层管理 |
| **供应商对账** | 设计文档要求供应商对账单（应付账款核对） | 完全缺失，财务无法与供应商对账 |

#### 1.2.2 服装行业特性缺失

| 功能点 | 设计文档要求 | 影响 |
|--------|-------------|------|
| **SKU颜色×尺码矩阵采购** | 设计文档要求支持按颜色×尺码矩阵批量录入采购明细 | 当前只能逐条添加SKU，服装行业采购效率极低 |
| **按色系/尺码分组查看** | 设计文档要求采购明细支持按色系、尺码维度聚合查看 | 缺失，无法快速统计某款式的采购总量 |
| **季节性采购分类** | 设计文档要求采购订单支持季节标签（春/夏/秋/冬） | 缺失，无法按季节管理采购计划 |
| **采购跟单管理** | 设计文档要求采购订单支持跟单进度跟踪（下单→生产→发货→到货） | 缺失，服装行业采购周期长，需要跟单功能 |

### 1.3 需要修改的功能

| 功能点 | 现状 | 需修改为 |
|--------|------|----------|
| **订单状态流转** | `PurchaseOrderStatusEnum` 只有3个状态：草稿(10)、执行中(20)、已完成(40) | 设计文档要求：草稿→待审核→已审核→部分到货→全部到货→已完成，以及退货相关状态 |
| **审核逻辑** | `auditOrder()` 直接从草稿(10)跳到已完成(30) | 需改为草稿→待审核→已审核，审核后自动生成到货入库单 |
| **Supplier实体** | 只有 supplierName、contactName、contactPhone、address、initialArrears、status | 需增加：评级(rating)、银行账户(bankAccount)、开户行(bankName)、结算方式(settlementType)、信用额度(creditLimit) |
| **SupplierService.create()** | 只保存 supplierName 和 status | 需保存完整供应商信息 |
| **SupplierService.update()** | 只更新 supplierName 和 status | 需更新完整供应商信息 |
| **单号生成规则** | 使用 `System.currentTimeMillis()` 生成，无序且可能重复 | 需改为日期+流水号格式，如 `JH20260329001` |
| **删除逻辑** | `deleteOrder()` 物理删除订单和明细 | 需增加状态校验，已审核/已完成的订单不允许删除 |

---

## 二、库存管理模块（M05）

### 2.1 已实现的功能

| 功能点 | 实现文件 | 说明 |
|--------|----------|------|
| 库存基础管理 | `Inventory` Entity + `InventoryController` + `InventoryServiceImpl` | 总库存/可用库存/锁定库存三级管理 |
| 库存锁定/解锁 | `InventoryServiceImpl.lockStock()` / `unlockStock()` | 悲观锁(FOR UPDATE)保证并发安全 |
| 库存扣减/增加 | `InventoryServiceImpl.deductStock()` / `addStock()` | 支持从可用库存或锁定库存扣减 |
| 库存事务流水 | `InventoryTransaction` Entity + `InventoryTransactionServiceImpl.record()` | 记录每次变动的类型、数量、前后值、源单据 |
| 盘点单 CRUD | `InventoryCheckController` / `InventoryCheckServiceImpl` | 分页、详情、新增、修改、删除、完成 |
| 出入库单管理 | `StockInOutController` / `StockInOutServiceImpl` | 其他入库(1)/其他出库(2)，支持审核 |
| 调拨单管理 | `TransferOrderController` / `TransferOrderServiceImpl` | CRUD + 审核 + 驳回 |
| 组装拆卸单 | `AssemblyOrderController` / `AssemblyOrderServiceImpl` | 组装(1)/拆卸(2)，支持审核 |
| 库存预警框架 | `InventoryAlertController` / `InventoryAlertServiceImpl` | 分页查询、标记已处理、检查预警(空实现) |
| 仓库基础管理 | `WarehouseController` / `WarehouseServiceImpl` | CRUD + 状态管理 |
| 前端页面 | `views/inventory/` | index.vue + alert/ + assembly/ + check/ + stock-in/ + stock-out/ + transfer/ |
| 前端API | `api/inventory.ts` | 完整封装（含调拨、盘点、组装拆卸、出入库、预警） |
| 流水类型枚举 | `InventoryTransTypeEnum` | 入库(1)、出库(2)、锁定(3)、解锁(4)、盘点调整(5) |

### 2.2 缺失的功能

#### 2.2.1 核心功能缺失

| 功能点 | 设计文档要求 | 影响 |
|--------|-------------|------|
| **批次管理** | 设计文档要求每个SKU按批次管理（批次号、生产日期、保质期、入库日期） | **完全缺失**。服装行业需要按批次追溯（面料批次、生产批次），无法实现先进先出 |
| **条码管理** | 设计文档要求条码生成与扫描（商品条码、箱码、批次码） | **完全缺失**。服装行业扫码出入库是核心操作 |
| **库存流水查询** | 设计文档要求提供库存变动历史查询界面 | 有记录逻辑但无查询接口和前端页面 |
| **盘点类型** | 设计文档要求支持全盘、抽盘、动盘三种盘点方式 | 当前只有统一的盘点单，无法区分盘点类型 |

#### 2.2.2 业务联动缺失（关键问题）

| 功能点 | 设计文档要求 | 现状 | 影响 |
|--------|-------------|------|------|
| **盘点完成→库存调整** | 盘点完成后自动计算差异并调整库存，记录流水 | `complete()` 只改状态为2，**不调整库存** | 盘点形同虚设 |
| **出入库审核→库存变动** | 出入库单审核后自动增减库存并记录流水 | `approve()` 只改状态为1，**不动库存** | 出入库操作不会实际影响库存 |
| **调拨审核→库存变动** | 调拨审核后调出仓出库、调入仓入库，记录流水 | `approve()` 只改状态为1，**不动库存** | 调拨操作不会实际影响库存 |
| **组装拆卸审核→库存变动** | 组装/拆卸审核后扣减原料库存、增加成品库存 | `approve()` 只改状态为1，**不动库存** | 组装拆卸操作不会实际影响库存 |
| **库存预警检查** | 定时/手动检查库存上下限，自动生成预警记录 | `checkAlerts()` 只有日志输出，**无实际逻辑** | 预警功能不可用 |
| **采购到货→库存增加** | 采购订单到货入库后自动增加库存 | 采购模块审核后无入库操作 | 采购→入库流程断裂 |

#### 2.2.3 服装行业特性缺失

| 功能点 | 设计文档要求 | 影响 |
|--------|-------------|------|
| **季节性库存管理** | 设计文档要求支持按季节筛选库存（春/夏/秋/冬装分类统计） | 缺失，无法按季节分析库存结构 |
| **SKU颜色/尺码矩阵查看** | 设计文档要求库存查询支持颜色×尺码矩阵展示 | 缺失，无法直观查看某款式的各色各码库存分布 |
| **库龄分析** | 设计文档要求库龄分析（按入库时间统计库存滞留情况） | 缺失，服装行业季节性强，滞销品需及时处理 |

### 2.3 需要修改的功能

| 功能点 | 现状 | 需修改为 |
|--------|------|----------|
| **InventoryCheckServiceImpl.complete()** | 只改状态为2(已完成) | 需遍历明细，计算差异数量，调用 `addStock()` / `deductStock()` 调整库存，并记录流水 |
| **StockInOutServiceImpl.approve()** | 只改状态为1 | 需遍历明细，根据 type 调用 `addStock()`(入库) 或 `deductStock()`(出库)，记录流水 |
| **TransferOrderServiceImpl.approve()** | 只改状态为1 | 需遍历明细，调出仓调 `deductStock()`，调入仓调 `addStock()`，记录双向流水 |
| **AssemblyOrderServiceImpl.approve()** | 只改状态为1 | 需根据 type(组装/拆卸) 遍历明细，扣减原料+增加成品 或 反向操作，记录流水 |
| **InventoryAlertServiceImpl.checkAlerts()** | 只输出日志 | 需查询所有SKU库存，对比预警上下限，超限则生成预警记录 |
| **Inventory 实体** | 只有 warehouseId、skuId、qty、availableQty、lockedQty | 需考虑增加 `batchNo`(批次号)、`costPrice`(成本价) 字段 |
| **单号生成规则** | TransferOrder(DB)、InventoryCheck(PD)、StockInOut(QT)、AssemblyOrder(ZZ) 使用 UUID 随机串 | 需改为日期+流水号，确保可读性和有序性 |
| **Warehouse 实体** | 只有 warehouseName、status | 设计文档要求增加仓库类型(总仓/门店仓)、地址、负责人、联系电话等 |

---

## 三、优先级建议

### P0 - 紧急（业务联动断裂）

1. **出入库审核→库存变动**：所有审核操作(approve/complete)需实际调用库存变动方法
2. **库存预警检查实现**：`checkAlerts()` 需实现实际预警逻辑

### P1 - 重要（核心流程缺失）

3. **采购到货入库联动**：采购审核后自动生成入库单并增加库存
4. **采购退货出库联动**：退货单审核后扣减库存
5. **盘点完成→库存调整**：盘点差异自动调整库存

### P2 - 需要（服装行业特性）

6. **批次管理**：服装行业必备的追溯能力
7. **条码管理**：扫码出入库核心操作
8. **SKU颜色×尺码矩阵**：服装采购和库存展示效率提升

### P3 - 优化

9. 供应商评级和对账
10. 采购申请流程
11. 库龄分析
12. 季节性库存统计

---

## 四、代码文件清单

### 采购模块现有文件

| 类型 | 文件 |
|------|------|
| Entity | `PurchaseOrder.java`, `PurchaseOrderDetail.java`, `Supplier.java` |
| Controller | `PurchaseOrderController.java`, `SupplierController.java` |
| Service | `PurchaseOrderService.java`, `PurchaseOrderServiceImpl.java`, `SupplierService.java`, `SupplierServiceImpl.java` |
| Mapper | `PurchaseOrderMapper.java`, `PurchaseOrderDetailMapper.java`, `SupplierMapper.java` |
| DTO | `PurchaseOrderDTO.java`, `PurchaseOrderDetailDTO.java`, `PurchaseOrderQuery.java`, `PurchaseOrderDetailQuery.java`, `SupplierDTO.java`, `SupplierQuery.java` |
| Enum | `PurchaseOrderStatusEnum.java`, `PurchaseOrderTypeEnum.java` |
| Converter | `PurchaseOrderConverter.java` |
| 前端 | `api/purchase.ts`, `views/purchase/` |

### 库存模块现有文件

| 类型 | 文件 |
|------|------|
| Entity | `Inventory.java`, `InventoryTransaction.java`, `InventoryCheck.java`, `InventoryCheckDetail.java`, `InventoryAlert.java`, `StockInOut.java`, `StockInOutDetail.java`, `TransferOrder.java`, `TransferOrderDetail.java`, `AssemblyOrder.java`, `AssemblyOrderDetail.java`, `Warehouse.java` |
| Controller | `InventoryController.java`, `InventoryCheckController.java`, `InventoryAlertController.java`, `StockInOutController.java`, `TransferOrderController.java`, `AssemblyOrderController.java`, `WarehouseController.java` |
| Service | `InventoryService.java`, `InventoryTransactionService.java`, `InventoryCheckService.java`, `InventoryAlertService.java`, `StockInOutService.java`, `TransferOrderService.java`, `AssemblyOrderService.java`, `WarehouseService.java` |
| ServiceImpl | `InventoryServiceImpl.java`, `InventoryTransactionServiceImpl.java`, `InventoryCheckServiceImpl.java`, `InventoryAlertServiceImpl.java`, `StockInOutServiceImpl.java`, `TransferOrderServiceImpl.java`, `AssemblyOrderServiceImpl.java`, `WarehouseServiceImpl.java` |
| Mapper | `InventoryMapper.java`, `InventoryTransactionMapper.java`, `InventoryCheckMapper.java`, `InventoryCheckDetailMapper.java`, `InventoryAlertMapper.java`, `StockInOutMapper.java`, `StockInOutDetailMapper.java`, `TransferOrderMapper.java`, `TransferOrderDetailMapper.java`, `AssemblyOrderMapper.java`, `AssemblyOrderDetailMapper.java`, `WarehouseMapper.java` |
| DTO | `InventoryDTO.java`, `InventoryQuery.java`, `InventoryCheckDTO.java`, `InventoryCheckDetailDTO.java`, `InventoryAlertDTO.java`, `StockInOutDTO.java`, `StockInOutDetailDTO.java`, `TransferOrderDTO.java`, `TransferOrderDetailDTO.java`, `AssemblyOrderDTO.java`, `AssemblyOrderDetailDTO.java`, `WarehouseDTO.java`, `WarehouseQuery.java`, `SupplierDTO.java`, `SupplierQuery.java` |
| Enum | `InventoryTransTypeEnum.java`, `InventoryCheckStatusEnum.java`, `StockInOutTypeEnum.java`, `OutStockStatusEnum.java`, `TransferOrderStatusEnum.java`, `AssemblyTypeEnum.java` |
| 前端 | `api/inventory.ts`, `types/inventory.ts`, `views/inventory/` |

### 完全缺失（需新建）

| 模块 | 缺失内容 |
|------|----------|
| 采购 | PurchaseRequest (申请单) 相关全套代码 |
| 采购 | 供应商评级、对账相关代码 |
| 库存 | Batch (批次管理) 相关全套代码 |
| 库存 | Barcode (条码管理) 相关全套代码 |
| 库存 | 库存流水查询接口和页面 |
