# 多AI协作开发规范

> **版本**: v1.0  
> **适用项目**: duoduo_jxc (服装行业全链路ERP系统)

---

## 一、项目模块划分

### 1.1 模块分配表

| 模块编号 | 模块名称 | 负责AI | 核心职责 | 技术栈 |
|---------|---------|--------|---------|--------|
| M01 | 基础数据模块 | AI-1 | 商品/款式/尺码/颜色/条码/工厂/班组 | Spring Boot |
| M02 | 销售管理模块 | AI-2 | 报价/订单/发货/退货/客户管理 | Spring Boot |
| M03 | 采购管理模块 | AI-3 | 采购订单/入库/退货/供应商管理 | Spring Boot |
| M04 | 库存管理模块 | AI-4 | 库存/盘点/调拨/统一库存表 | Spring Boot |
| M05 | 生产管理模块 | AI-5 | 生产订单/计划/排程/裁床/扎包 | Spring Boot |
| M06 | 工序计件模块 | AI-6 | 工序管理/计件记录/扫码/门禁 | Spring Boot |
| M07 | 质量管理模块 | AI-7 | 首件确认/质检/返工/缺陷管理 | Spring Boot |
| M08 | 财务成本模块 | AI-8 | 收付款/成本核算/工资/凭证 | Spring Boot |
| M09 | 前端Web模块 | AI-9 | Vue3页面/组件/路由/状态管理 | Vue3 + TS |
| M10 | 小程序模块 | AI-10 | uni-app页面/离线/扫码/蓝牙打印 | uni-app |

### 1.2 模块依赖关系

```
M01 基础数据 (无依赖，最先开发)
    ↓
M02 销售 ←→ M03 采购 ←→ M04 库存
    ↓           ↓           ↓
M05 生产 ←→ M06 工序 ←→ M07 质量
    ↓           ↓
M08 财务 ←←←←←←←
    ↓
M09 前端 + M10 小程序 (最后开发)
```

---

## 二、代码规范

### 2.1 Java开发规范（阿里巴巴）

#### 2.1.1 命名规范

```java
// 类名：大驼峰，名词
public class SalesOrderService {}
public class InventoryController {}

// 方法名：小驼峰，动词开头
public void createOrder() {}
public List<Order> queryOrderList() {}
public boolean checkInventory() {}

// 常量：全大写下划线
public static final String DEFAULT_STATUS = "pending";
public static final int MAX_PAGE_SIZE = 100;

// 包名：全小写
package com.duoduo.jxc.service;
package com.duoduo.jxc.controller;

// 枚举：大驼峰，枚举成员全大写
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    COMPLETED,
    CANCELLED
}
```

#### 2.1.2 实体类规范

```java
@Data
@TableName("jxc_sales_order")
public class SalesOrder extends BaseEntity {
    
    @TableId(type = IdType.AUTO)
    private Long orderId;
    
    @TableField("order_no")
    private String orderNo;
    
    @TableField("customer_id")
    private Long customerId;
    
    @TableField("status")
    private String status;
    
    @TableField(exist = false)
    private List<SalesOrderItem> items;
}

// BaseEntity 必须包含
@Data
public class BaseEntity {
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
    
    @TableField("create_by")
    private Long createBy;
    
    @TableField("update_by")
    private Long updateBy;
    
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
```

#### 2.1.3 Service层规范

```java
@Service
@RequiredArgsConstructor
public class SalesOrderServiceImpl implements SalesOrderService {
    
    private final SalesOrderMapper orderMapper;
    private final SalesOrderItemMapper itemMapper;
    private final InventoryService inventoryService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(SalesOrderDTO dto) {
        // 1. 参数校验
        validateOrder(dto);
        
        // 2. 构建实体
        SalesOrder order = convertToEntity(dto);
        
        // 3. 保存主表
        orderMapper.insert(order);
        
        // 4. 保存明细
        saveOrderItems(order.getOrderId(), dto.getItems());
        
        // 5. 业务处理
        processBusiness(order);
        
        return order.getOrderId();
    }
    
    @Override
    public PageResult<SalesOrderVO> queryOrderList(SalesOrderQuery query) {
        Page<SalesOrder> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SalesOrder> wrapper = buildQueryWrapper(query);
        
        IPage<SalesOrder> result = orderMapper.selectPage(page, wrapper);
        
        return PageResult.of(result, this::convertToVO);
    }
}
```

#### 2.1.4 Controller层规范

```java
@RestController
@RequestMapping("/api/v1/sales/orders")
@RequiredArgsConstructor
public class SalesOrderController {
    
    private final SalesOrderService orderService;
    
    @PostMapping
    @Operation(summary = "创建销售订单")
    public Result<Long> create(@RequestBody @Valid SalesOrderDTO dto) {
        Long orderId = orderService.createOrder(dto);
        return Result.success(orderId);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情")
    public Result<SalesOrderVO> getById(@PathVariable Long id) {
        SalesOrderVO vo = orderService.getOrderDetail(id);
        return Result.success(vo);
    }
    
    @GetMapping
    @Operation(summary = "分页查询订单")
    public Result<PageResult<SalesOrderVO>> list(SalesOrderQuery query) {
        PageResult<SalesOrderVO> result = orderService.queryOrderList(query);
        return Result.success(result);
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "更新订单状态")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody StatusUpdateDTO dto) {
        orderService.updateStatus(id, dto.getStatus());
        return Result.success();
    }
}
```

#### 2.1.5 DTO/VO规范

```java
// DTO：数据传输对象（入参）
@Data
public class SalesOrderDTO {
    
    @NotNull(message = "客户ID不能为空")
    private Long customerId;
    
    @NotBlank(message = "订单类型不能为空")
    private String orderType;
    
    @NotEmpty(message = "订单明细不能为空")
    @Valid
    private List<SalesOrderItemDTO> items;
    
    private LocalDateTime deliveryDate;
    
    private String remark;
}

// VO：视图对象（出参）
@Data
public class SalesOrderVO {
    
    private Long orderId;
    private String orderNo;
    private String status;
    private String statusName;
    private Long customerId;
    private String customerName;
    private BigDecimal totalAmount;
    private LocalDateTime createTime;
    private List<SalesOrderItemVO> items;
}

// Query：查询对象
@Data
public class SalesOrderQuery extends PageQuery {
    
    private String orderNo;
    private Long customerId;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
```

#### 2.1.6 异常处理规范

```java
// 业务异常
public class BusinessException extends RuntimeException {
    private final String code;
    
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
    
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }
}

// 错误码枚举
public enum ErrorCode {
    ORDER_NOT_FOUND("2001", "订单不存在"),
    INVENTORY_INSUFFICIENT("2002", "库存不足"),
    STATUS_INVALID("2003", "状态不合法");
    
    private final String code;
    private final String message;
}

// 全局异常处理
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        return Result.fail("1004", message);
    }
}
```

### 2.2 数据库规范

#### 2.2.1 表命名规范

```sql
-- 表名：jxc_模块_业务，全小写下划线
jxc_sales_order        -- 销售订单
jxc_sales_order_item   -- 销售订单明细
jxc_inventory          -- 库存表
jxc_production_order   -- 生产订单

-- 主键：表名单数_id
order_id, item_id, inventory_id

-- 外键：关联表名_id
customer_id, product_id, warehouse_id

-- 状态字段：status，使用小写下划线值
status VARCHAR(20) DEFAULT 'pending'  -- pending, confirmed, completed
```

#### 2.2.2 必备字段

```sql
-- 每张表必须包含以下审计字段
create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
create_by BIGINT COMMENT '创建人',
update_by BIGINT COMMENT '更新人',
deleted TINYINT DEFAULT 0 COMMENT '删除标记(0正常 1删除)'
```

#### 2.2.3 索引规范

```sql
-- 主键索引
PRIMARY KEY (order_id)

-- 唯一索引：uk_字段名
UNIQUE KEY uk_order_no (order_no)

-- 普通索引：idx_字段名
INDEX idx_customer_id (customer_id)
INDEX idx_status (status)
INDEX idx_create_time (create_time)

-- 组合索引：idx_字段1_字段2
INDEX idx_customer_status (customer_id, status)
```

### 2.3 前端规范

#### 2.3.1 目录结构

```
frontend/src/
├── api/                    # API接口
│   ├── sales/             # 销售模块API
│   │   ├── order.ts
│   │   └── customer.ts
│   └── index.ts
├── components/            # 公共组件
│   ├── Table/
│   ├── Form/
│   └── Dialog/
├── composables/           # 组合式函数
│   ├── useTable.ts
│   ├── useForm.ts
│   └── usePermission.ts
├── router/               # 路由配置
├── stores/               # Pinia状态管理
├── styles/               # 样式文件
├── types/                # TypeScript类型定义
├── utils/                # 工具函数
└── views/                # 页面组件
    ├── sales/
    │   ├── order/
    │   │   ├── index.vue        # 列表页
    │   │   ├── Form.vue         # 表单组件
    │   │   └── Detail.vue       # 详情页
    │   └── customer/
    └── ...
```

#### 2.3.2 Vue组件规范

```vue
<template>
  <div class="order-list">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="订单号">
          <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card">
      <template #header>
        <el-button type="primary" @click="handleAdd">新增</el-button>
      </template>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" />
        <el-table-column prop="customerName" label="客户" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link @click="handleView(row)">查看</el-button>
            <el-button link @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        @change="handleQuery"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getSalesOrderList, deleteSalesOrder } from '@/api/sales/order'
import type { SalesOrderQuery, SalesOrderVO } from '@/types/sales'

defineOptions({
  name: 'SalesOrderList'
})

const loading = ref(false)
const tableData = ref<SalesOrderVO[]>([])
const total = ref(0)

const queryParams = reactive<SalesOrderQuery>({
  pageNum: 1,
  pageSize: 20,
  orderNo: '',
  customerId: undefined,
  status: ''
})

const handleQuery = async () => {
  loading.value = true
  try {
    const { data } = await getSalesOrderList(queryParams)
    tableData.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  queryParams.orderNo = ''
  queryParams.customerId = undefined
  queryParams.status = ''
  handleQuery()
}

const handleAdd = () => {
  // 新增逻辑
}

const handleView = (row: SalesOrderVO) => {
  // 查看逻辑
}

const handleEdit = (row: SalesOrderVO) => {
  // 编辑逻辑
}

const handleDelete = async (row: SalesOrderVO) => {
  await deleteSalesOrder(row.orderId)
  handleQuery()
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped lang="scss">
.order-list {
  .search-card {
    margin-bottom: 16px;
  }
}
</style>
```

---

## 三、Git协作规范

### 3.1 分支策略

```
main                    # 主分支，生产环境代码
  └── develop          # 开发分支
        ├── feature/M01-base-data      # AI-1 功能分支
        ├── feature/M02-sales          # AI-2 功能分支
        ├── feature/M03-purchase       # AI-3 功能分支
        └── ...
```

### 3.2 Commit规范

```bash
# 格式：<type>(<scope>): <subject>

# 类型
feat:     新功能
fix:      修复bug
docs:     文档更新
style:    代码格式（不影响逻辑）
refactor: 重构
test:     测试
chore:    构建/工具

# 示例
feat(sales): 新增销售订单创建功能
fix(inventory): 修复库存扣减精度问题
docs(api): 更新销售模块API文档
refactor(production): 重构生产排程算法
```

### 3.3 PR规范

```markdown
## 功能描述
简要描述本次PR实现的功能

## 变更内容
- 新增 SalesOrderController
- 新增 SalesOrderService 及实现
- 新增销售订单相关DTO/VO

## 测试情况
- [x] 单元测试通过
- [x] 接口测试通过
- [ ] 集成测试通过

## 相关Issue
Closes #123
```

---

## 四、AI协作流程

### 4.1 开发流程

```
1. 认领任务
   └── 在项目管理工具中认领对应模块任务

2. 拉取代码
   └── git checkout develop
   └── git pull origin develop
   └── git checkout -b feature/M0X-模块名

3. 阅读设计文档
   └── 阅读 docs/split/ 对应模块文档
   └── 参考 10-数据库DDL设计.md

4. 编写代码
   └── 严格遵循本文档规范
   └── 保持与现有代码风格一致

5. 本地测试
   └── 单元测试
   └── 接口测试

6. 提交代码
   └── git add .
   └── git commit -m "feat(module): 描述"
   └── git push origin feature/M0X-模块名

7. 创建PR
   └── 目标分支：develop
   └── 填写PR模板

8. 代码审查
   └── 等待其他AI审查
   └── 修改审查意见

9. 合并代码
   └── 审查通过后合并
```

### 4.2 接口对接规范

```yaml
# 模块间接口调用约定

# AI-1 (基础数据) 提供的接口：
- GET  /api/v1/products/{id}      # 获取商品信息
- GET  /api/v1/styles/{id}        # 获取款式信息
- GET  /api/v1/customers/{id}     # 获取客户信息
- GET  /api/v1/warehouses/{id}    # 获取仓库信息

# AI-2 (销售) 需要调用的接口：
- GET  /api/v1/products/{id}      # 来自AI-1
- GET  /api/v1/customers/{id}     # 来自AI-1
- POST /api/v1/inventory/reserve  # 来自AI-4（库存占用）
- POST /api/v1/receivables        # 来自AI-8（应收账款）

# AI-4 (库存) 提供的接口：
- POST /api/v1/inventory/reserve  # 库存占用
- POST /api/v1/inventory/release  # 库存释放
- POST /api/v1/inventory/deduct   # 库存扣减
- GET  /api/v1/inventory/available # 可用库存查询
```

### 4.3 冲突解决

```
1. 代码冲突
   - 优先保留最新提交的代码
   - 如无法判断，联系相关模块负责人

2. 接口变更
   - 变更前需通知所有依赖模块
   - 使用版本号管理（/api/v1/ → /api/v2/）

3. 数据库变更
   - 新增表/字段：直接添加
   - 修改字段：需通知所有使用方
   - 删除字段：需确认无引用后删除
```

---

## 五、代码审查清单

### 5.1 Java代码审查

```markdown
- [ ] 类/方法命名符合规范
- [ ] 有适当的注释
- [ ] 无硬编码（状态值、魔法数字）
- [ ] 异常处理完善
- [ ] 事务注解正确使用
- [ ] SQL注入防护
- [ ] 分页查询使用Page对象
- [ ] DTO/VO分离
- [ ] 参数校验使用@Valid
```

### 5.2 数据库审查

```markdown
- [ ] 表名/字段名符合规范
- [ ] 包含必备审计字段
- [ ] 主键/索引设计合理
- [ ] 外键关联正确
- [ ] 字段类型/长度合理
- [ ] 金额字段使用DECIMAL(10,4)
```

### 5.3 前端审查

```markdown
- [ ] 组件命名符合规范
- [ ] TypeScript类型定义完整
- [ ] API调用有loading状态
- [ ] 错误处理完善
- [ ] 无console.log残留
- [ ] 样式使用scoped
```

---

## 六、开发工具配置

### 6.1 后端工具

**IDE配置**（任选其一）：
- IntelliJ IDEA（推荐）
- VSCode + Extension Pack for Java
- Eclipse

**代码格式化配置**：
```xml
<!-- .idea/codeStyles/Project.xml 或使用阿里Java规范插件 -->
<code_scheme name="Project" version="173">
  <JavaCodeStyleSettings>
    <option name="CLASS_COUNT_TO_USE_IMPORT_ON_DEMAND" value="99" />
    <option name="NAMES_COUNT_TO_USE_IMPORT_ON_DEMAND" value="99" />
  </JavaCodeStyleSettings>
</code_scheme>
```

**推荐插件**：
- Alibaba Java Coding Guidelines（代码规范检查）
- Lombok Plugin
- MyBatisX
- SonarLint（代码质量检查）

### 6.2 前端工具

**IDE配置**（任选其一）：
- VSCode（推荐）
- WebStorm
- Cursor

**推荐插件**：
- ESLint
- Prettier
- Volar（Vue3）
- TypeScript Vue Plugin

### 6.3 Maven配置

```xml
<!-- pom.xml 必备依赖 -->
<dependencies>
    <!-- Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- MyBatis-Plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
        <version>3.5.5</version>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <scope>provided</scope>
    </dependency>
    
    <!-- 参数校验 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
</dependencies>
```

### 6.4 ESLint配置

```javascript
// .eslintrc.cjs
module.exports = {
  extends: [
    'eslint:recommended',
    'plugin:vue/vue3-recommended',
    '@vue/eslint-config-typescript',
    '@vue/eslint-config-prettier'
  ],
  rules: {
    'vue/multi-word-component-names': 'off',
    'vue/no-v-html': 'off',
    '@typescript-eslint/no-unused-vars': ['error', { argsIgnorePattern: '^_' }]
  }
}
```

---

## 七、AI协作机制

> **注意**：AI之间不需要像人类团队那样每天站会，而是采用即时协作模式。

### 7.1 进度同步机制

**触发时机**：
- AI完成阶段性任务后，立即更新 `AI-IDENTITY.md`
- 遇到阻塞问题时，立即在文件中标记

**同步方式**：
```
AI完成代码提交
      │
      ▼
更新 AI-IDENTITY.md
      │
      ▼
PM-AI 或 用户读取进度
      │
      ▼
发现问题立即协调
```

**无需等待**：AI不需要等待每日会议，可随时查看其他AI的 `AI-IDENTITY.md` 了解进度。

### 7.2 即时代码审查

**触发机制**：每次代码提交后**立即**进行审查，无需等待

```
AI提交代码/PR
      │
      ▼
Arch-AI/QA-AI 立即审查（秒级响应）
      │
      ├── 通过 → 合并代码
      │
      └── 有问题 → 立即反馈，AI立即修改
```

**审查流程**：
```
1. 开发AI提交代码
2. Arch-AI 即时检查代码规范、架构一致性
3. QA-AI 即时检查测试覆盖、代码质量
4. 审查结果立即反馈给开发AI
5. 如有问题，开发AI立即修改并重新提交
```

**审查优先级**：
| 优先级 | 审查类型 | 响应时间 |
|--------|---------|---------|
| P0 | 跨模块接口变更 | 立即（秒级） |
| P1 | 核心功能代码 | 立即（秒级） |
| P2 | 一般功能代码 | 立即（秒级） |
| P3 | 文档、配置 | 立即（秒级） |

### 7.3 问题升级

```
1. 模块内问题 → AI自行解决（立即）
2. 跨模块问题 → 相关AI协商（立即）
3. 协商不成 → PM-AI 协调（立即）
4. 架构问题 → Arch-AI 决策（立即）
5. 重大问题 → 上报用户（立即）
```

**AI协作特点**：
- ✅ 无需等待：AI可以24/7工作，无需等待会议
- ✅ 即时响应：代码审查秒级完成
- ✅ 并行协作：多个AI可同时开发不同模块
- ✅ 文件同步：通过 `AI-IDENTITY.md` 文件实时同步状态

---

> **文档维护**：本文档由项目负责人维护，如有变更需通知所有AI。
