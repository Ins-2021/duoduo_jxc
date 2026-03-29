# 模块差距分析报告 (Trae 负责模块)

> **分析时间**: 2026-03-29
> **负责模块**: 基础数据(M01)、销售(M02)、工序(M06)、质量(M07)
> **参考文档**: `docs/split/02-数据模型设计.md`、`docs/split/12-销售管理模块.md`

## 1. 概述
根据《02-数据模型设计》与《12-销售管理模块》的设计文档要求，对比当前代码库的实现现状，对负责的四个核心模块进行差距分析，为后续开发提供明确的优先级与任务清单。

---

## 2. 基础数据模块 (M01)
### 2.1 已实现功能
- 商品档案管理 (`ProductSpu`, `ProductSku`, `ProductController`)
- 商品分类与品牌 (`ProductCategory`, `ProductBrand`)
- 商品属性与规格 (`ProductAttribute`, `ProductAttributeValue`)
- 客户与供应商管理 (`Customer`, `Supplier`)
- 基础组织架构 (`SysUser`, `SysDept`, `SysPost`, `SysRole`)
- 仓库管理 (`Warehouse`)

### 2.2 缺失功能 (待新增)
根据服装行业特性，缺失大量垂直领域的业务基础数据：
- **款式管理**：`jxc_style` (款式档案), `jxc_style_colorway` (款式配色)
- **尺码管理**：`jxc_size_category` (尺码分类), `jxc_size_option` (尺码选项), `jxc_size_chart` (尺码规格), `jxc_size_ratio_template` (尺码配比模板)
- **颜色管理**：`jxc_color` (颜色档案)
- **条码规则**：`jxc_barcode_rule` (各类条码生成规则)
- **组织架构增强**：`jxc_factory` (工厂), `jxc_workshop` (车间), `jxc_work_group` (班组)
- **员工与技能**：`jxc_worker_profile` (员工档案扩展), `jxc_worker_skill` (技能认证), `jxc_process_capacity_config` (产能配置)

### 2.3 需修改功能
- 商品模块（SPU/SKU）需要与新增的“款式”、“颜色”、“尺码”进行关联重构，替代现有较为通用的规格逻辑，以支撑后续的 BOM 矩阵。

---

## 3. 销售管理模块 (M02)
### 3.1 已实现功能
- **销售订单** (`SalesOrder`, `SalesOrderDetail`)：支持批发、零售、预订单三种类型，包含完整的增删改查与审核逻辑。
- **预订单发货** (`BookingDelivery`)：支持预订单的分批发货与欠货追踪。
- **销售退货** (`SalesReturnOrder`, `SalesReturnDetail`)：支持销售退货流程。
- **业财库存联动**：销售单审核已打通应收账款生成与库存扣减（如 `SalesOrderServiceImpl#auditOrder`）。

### 3.2 缺失功能 (待新增)
- **销售报价单** (`jxc_quotation`)：缺失报价单的创建、发送及转订单流程。
- **通用发货单** (`jxc_sales_delivery`)：当前只有预订单的发货实现，缺少标准流程的独立发货单、物流跟踪以及确认签收闭环。

### 3.3 需修改功能
- 剥离并完善统一的“发货”业务模型，使其不仅服务于预订单，也能服务于标准批发单。
- 补充报价单、发货单的前端页面与交互流转。

---

## 4. 工序计件模块 (M06)
### 4.1 已实现功能
- 无。该模块为全新设计的 MES 核心模块，当前代码库中未发现相关实体与接口。

### 4.2 缺失功能 (待新增)
- **工序基础**：`jxc_process` (工序库), `jxc_process_dependency` (工序依赖)
- **工价配置**：`jxc_product_process_config` (货品工序单价), `jxc_price_match_rule`, `jxc_sku_price_premium` (溢价配置)
- **扎包流转**：`jxc_bundle` (扎包), `jxc_bundle_process` (扎包工序关联)
- **计件登记**：`jxc_process_task` (工序任务), `jxc_process_record` (计件记录), `jxc_process_record_share` (多人分成), `jxc_process_record_dedup` (防重机制)
- **流程门禁**：`jxc_flow_template`, `jxc_factory_flow_config`, `jxc_bundle_flow_instance` (动态工序流程)

### 4.3 需修改功能
- 纯新增模块，需从底层数据库 DDL 到 Controller、Vue 页面从零构建。

---

## 5. 质量管理模块 (M07)
### 5.1 已实现功能
- 无。该模块为全新设计模块。

### 5.2 缺失功能 (待新增)
- **质检标准**：`jxc_quality_standard` (质检标准), `jxc_aql_standard` (AQL抽检标准)
- **首件确认**：`jxc_first_article_confirmation` (首件检验与放行机制)
- **日常质检**：`jxc_quality_check` (质检记录), `jxc_patrol_check` (巡检记录)
- **缺陷返工**：`jxc_defect_record` (缺陷记录), `jxc_defect_category` (缺陷分类)

### 5.3 需修改功能
- 需与 M06(工序模块) 的扎包流转、外协加工、裁床等环节紧密集成，在工作流节点上埋点，触发质检门禁。

---

## 6. 总结与执行建议
1. **基础数据先行**：优先开发 M01 缺失的 **款式-颜色-尺码** 及 **工厂-车间-班组** 功能。这是构建服装 ERP/MES 系统的基石。
2. **销售模块补漏**：在现有销售体系基础上，平行扩展报价单模块与标准发货单模块，业务难度较低，可穿插进行。
3. **生产核心攻坚**：M06(工序) 与 M07(质量) 是本次系统的核心增量，业务高度耦合（扎包流转 -> 质检门禁 -> 计件核算）。需要优先设计扎包状态机与计件防重逻辑，再向外延伸。
