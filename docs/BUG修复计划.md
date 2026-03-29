# Bug修复计划

> **创建时间**: 2026-03-29 22:00  
> **Manager-AI**: opencode  
> **严重级别**: 严重Bug必须立即修复，中等Bug可延后

---

## 严重Bug 🔴

### B001: PurchaseOrderServiceImpl 状态码错误
**分配给**: codebuddy  
**文件**: `backend/src/main/java/com/duoduo/jxc/service/impl/PurchaseOrderServiceImpl.java:238`

**问题**: 
```java
stockInOut.setStatus(1); // 直接已审核 - 硬编码
```

**正确做法**:
```java
stockInOut.setStatus(StockInOutStatusEnum.APPROVED.getCode()); // 使用枚举
```

**修复要求**:
1. 将行238的硬编码 `status(1)` 改为使用 `StockInOutStatusEnum.APPROVED.getCode()`
2. 检查整文件中所有硬编码状态码
3. 检查是否还有其他Service存在类似问题

---

### B002: 库存Service状态保护缺失
**分配给**: codebuddy  
**文件**: 
- `InventoryCheckServiceImpl.java`
- `StockInOutServiceImpl.java`
- `TransferOrderServiceImpl.java`
- `AssemblyOrderServiceImpl.java`

**问题**: 
- `update/delete` 方法未限制单据状态
- 已审核/已完成单据仍可被修改或删除
- 使用魔法数字 `0/1/2` 而非枚举

**修复要求**:
1. 在 update/delete 方法中添加状态校验：
   - 仅允许草稿状态修改/删除
   - 已审核状态禁止修改/删除
2. 将所有 `0/1/2` 魔法数字改为枚举常量
3. 添加事务注解 `@Transactional(rollbackFor = Exception.class)`

**示例代码**:
```java
// 修改前
public void updateOrder(InventoryCheckDTO dto) {
    InventoryCheck exist = getById(dto.getId());
    // 缺少状态校验
    BeanUtils.copyProperties(dto, exist);
    updateById(exist);
}

// 修改后
@Transactional(rollbackFor = Exception.class)
public void updateOrder(InventoryCheckDTO dto) {
    InventoryCheck exist = getById(dto.getId());
    if (exist == null) {
        throw new BusinessException(BizCode.DATA_NOT_FOUND);
    }
    // 状态校验
    if (exist.getStatus() != InventoryCheckStatusEnum.DRAFT.getCode()) {
        throw new BusinessException(BizCode.BUSINESS_ERROR, "非草稿状态不允许修改");
    }
    BeanUtils.copyProperties(dto, exist);
    updateById(exist);
}
```

---

### B003: 权限控制缺失
**分配给**: trae  
**文件**:
- `ProcessController.java`
- `BundleController.java`
- `ProcessRecordController.java`
- `QualityStandardController.java`
- `QualityCheckController.java`
- `DefectRecordController.java`
- `FirstArticleConfirmationController.java`
- `QuotationController.java`

**问题**:
- 控制器已开放CRUD/转换接口
- 未添加 `@PreAuthorize`、`@Log`、`@Valid/@Validated` 等控制
- 接口可在已登录前提下被越权访问
- 缺乏审计记录

**修复要求**:
1. 参照 `BatchController`、`BarcodeController` 添加：
   - `@PreAuthorize("hasAuthority('mes:process:view')")` 等权限注解
   - `@Log` 操作日志注解
   - `@Valid` 参数校验注解
2. 每个接口都要添加对应的权限控制

**示例代码**:
```java
@GetMapping("/page")
@PreAuthorize("hasAuthority('mes:process:view')")
@Log(module = "工序管理", type = LogType.QUERY)
public Result<PageResult<ProcessVO>> page(@Valid ProcessQuery query) {
    return Result.success(processService.pageQuery(query));
}
```

---

## 中等Bug 🟡

### B004: 硬编码问题
**分配给**: codebuddy  
**文件**:
- `PurchaseOrderServiceImpl.java` (单号前缀硬编码 "RK")
- `QuotationServiceImpl.java` (单号前缀 "BJ", 状态 "draft/accepted")

**问题**:
- 单号前缀硬编码
- 状态值硬编码
- 未接入统一单号规则模块

**修复要求**:
1. 单号生成使用统一规则模块
2. 状态值使用枚举常量
3. 避免硬编码字符串

---

### B005: Service实现不完整
**分配给**: trae  
**文件**:
- `StyleServiceImpl.java`
- `StyleColorwayServiceImpl.java`
- `ColorServiceImpl.java`
- `SizeCategoryServiceImpl.java`
- `SizeOptionServiceImpl.java`
- `SizeChartServiceImpl.java`
- `SizeRatioTemplateServiceImpl.java`
- `FactoryServiceImpl.java`
- `WorkshopServiceImpl.java`
- `WorkGroupServiceImpl.java`

**问题**:
- 仅继承 `ServiceImpl`，没有任何业务方法
- 缺少参数校验、状态控制或事务边界
- 和同项目其他业务Service风格不一致

**修复要求**:
1. 补齐标准CRUD方法
2. 添加参数校验
3. 写操作添加事务注解
4. 参照现有完整Service风格

---

## 修复优先级

1. **立即修复** (阻塞上线): B001, B002, B003
2. **稍后修复** (不影响核心功能): B004, B005

## 验证标准

每个Bug修复后必须:
1. 运行 `mvn compile` 编译通过
2. 运行 `mvn test` 单元测试通过
3. 代码审查确认逻辑正确
