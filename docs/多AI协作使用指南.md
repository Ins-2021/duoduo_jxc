# 多AI协作开发 - 使用指南

## 一、快速开始

### 步骤1：创建身份文件

**每个AI在开始工作前，必须先创建身份文件。**

复制以下模板，创建文件 `AI-IDENTITY.md`：

```yaml
# ============================================
# AI 开发者身份文件
# ============================================
# 说明：请填写你的身份信息，然后保存此文件
# 其他AI会读取此文件了解你的进度

AI_ID: ""              # 必填：AI-1 到 AI-10
AI_NAME: ""            # 必填：你的名称，如 Claude、GPT-4、Qwen
MODULE: ""             # 必填：负责的模块名称
STATUS: "pending"      # pending/active/completed
START_TIME: ""         # 开始时间
END_TIME: ""           # 完成时间（可选）

# ============================================
# 进度信息（每日更新）
# ============================================
TODAY_TASKS: |
  - 任务1
  - 任务2

COMPLETED_TASKS: |
  - 已完成任务

BLOCKERS: |
  - 遇到的问题（如有）

# ============================================
# 接口信息
# ============================================
# 你提供的接口（其他AI可以调用）
PROVIDED_APIS: |
  - POST /api/v1/xxx
  - GET /api/v1/xxx

# 你依赖的接口（需要其他AI提供）
REQUIRED_APIS: |
  - from: AI-X
    api: GET /api/v1/xxx
```

### 步骤2：领取模块

**方式A：用户直接分配**

用户在启动AI时发送：

```
请创建 AI-IDENTITY.md 文件，内容如下：
AI_ID: "AI-2"
AI_NAME: "Claude"
MODULE: "销售管理模块"
```

**方式B：AI主动选择**

用户发送：

```
请阅读 AI-CONFIG.md，选择一个未领取的模块，
然后创建 AI-IDENTITY.md 文件领取该模块。
```

AI回复：

```
我选择领取 AI-2 销售管理模块。
已创建 AI-IDENTITY.md 文件，内容如下：
[文件内容]

我现在开始开发销售管理模块。
```

### 步骤3：阅读文档

每个AI根据分配的模块，阅读对应文档：

| AI_ID | 主要文档 |
|-------|---------|
| AI-1 | `docs/split/02-数据模型设计.md` |
| AI-2 | `docs/split/12-销售管理模块.md` |
| AI-3 | `docs/split/13-采购管理模块.md` |
| AI-4 | `docs/split/14-库存管理详细设计.md` |
| AI-5 | `docs/split/02-数据模型设计.md` + `03-核心功能设计.md` |
| AI-6 | `docs/split/02-数据模型设计.md` + `03-核心功能设计.md` |
| AI-7 | `docs/split/02-数据模型设计.md` |
| AI-8 | `docs/split/26-成本核算详细设计.md` + `27-工资管理模块.md` |
| AI-9 | `docs/split/08-前端页面设计.md` |
| AI-10 | `docs/split/09-小程序设计.md` |

所有AI必须阅读：`docs/多AI协作开发规范.md`

### 步骤4：更新进度

每日更新 `AI-IDENTITY.md` 中的进度信息：

```yaml
TODAY_TASKS: |
  - 完成销售订单Service层
  - 完成销售订单Controller层

COMPLETED_TASKS: |
  - 完成销售订单DTO/VO设计
  - 完成数据库表设计

BLOCKERS: |
  - 等待 AI-4 提供库存占用接口
```

---

## 二、实际操作示例

### 示例1：AI-2 开发销售模块

**用户发送：**

```
请执行以下操作：

1. 创建文件 AI-IDENTITY.md，内容：
```yaml
AI_ID: "AI-2"
AI_NAME: "Claude"
MODULE: "销售管理模块"
STATUS: "active"
START_TIME: "2026-03-28 10:00"
```

2. 阅读以下文档：
   - docs/split/12-销售管理模块.md
   - docs/split/10-数据库DDL设计.md（销售表部分）
   - docs/多AI协作开发规范.md

3. 阅读完成后，告诉我你的开发计划。
```

**AI回复：**

```
已创建 AI-IDENTITY.md 文件。

我已阅读相关文档，我的开发计划如下：

【第一周】
1. 创建销售订单实体类、DTO、VO
2. 实现销售订单Service层
3. 实现销售订单Controller层

【依赖接口】
- AI-1: GET /api/v1/customers/{id}
- AI-4: POST /api/v1/inventory/reserve
- AI-8: POST /api/v1/receivables

【对外接口】
- POST /api/v1/sales/orders
- GET /api/v1/sales/orders/{id}
- PUT /api/v1/sales/orders/{id}/status

现在开始开发...
```

### 示例2：检查其他AI进度

**用户发送：**

```
请读取所有 AI-IDENTITY.md 文件，汇总各AI的进度。
```

**AI回复：**

```
【AI协作进度汇总】

| AI_ID | 模块 | 状态 | 进度 |
|-------|------|------|------|
| AI-1 | 基础数据 | completed | ✅ 已完成 |
| AI-2 | 销售管理 | active | 🔄 开发中（完成60%） |
| AI-3 | 采购管理 | active | 🔄 开发中（完成40%） |
| AI-4 | 库存管理 | pending | ⬜ 未开始 |
| AI-5 | 生产管理 | pending | ⬜ 未开始 |
| ... | ... | ... | ... |

【依赖关系】
- AI-2 等待 AI-4 的库存接口
- AI-5 等待 AI-1 的基础数据

【建议】
AI-4 应尽快开始，因为 AI-2 和 AI-3 都依赖库存接口。
```

### 示例3：接口协调

**AI-2 发现需要接口：**

```yaml
# AI-2 在 AI-IDENTITY.md 中添加：
REQUIRED_APIS: |
  - from: AI-4
    api: POST /api/v1/inventory/reserve
    status: waiting
    desc: 销售确认时需要占用库存
```

**AI-4 完成接口后：**

```yaml
# AI-4 在 AI-IDENTITY.md 中添加：
PROVIDED_APIS: |
  - POST /api/v1/inventory/reserve
  - POST /api/v1/inventory/release
  - GET /api/v1/inventory/available
```

**用户通知 AI-2：**

```
AI-4 已完成库存占用接口：POST /api/v1/inventory/reserve
你可以继续开发销售确认功能了。
```

---

## 三、文件结构

```
duoduo_jxc/
├── AI-CONFIG.md              # 模块分配配置
├── AI-PROMPTS.md             # 启动提示词
├── AI-IDENTITY.md            # 【每个AI创建】身份文件
│
├── docs/
│   ├── 多AI协作开发规范.md    # 代码规范
│   ├── split/                # 拆分后的设计文档
│   │   ├── 00-索引与总览.md
│   │   ├── 12-销售管理模块.md
│   │   └── ...
│   └── ...
│
├── backend/                  # 后端代码
│   └── src/main/java/com/duoduo/jxc/
│       ├── controller/
│       ├── service/
│       ├── entity/
│       ├── dto/
│       └── vo/
│
└── frontend/                 # 前端代码
    └── src/
        ├── views/
        ├── api/
        └── components/
```

---

## 四、Git工作流

### 分支命名

```
feature/M01-base-data    # AI-1 的功能分支
feature/M02-sales        # AI-2 的功能分支
feature/M03-purchase     # AI-3 的功能分支
...
```

### 提交信息

```
feat(M02): 新增销售订单创建功能
fix(M02): 修复订单金额计算精度问题
docs(M02): 更新销售模块API文档
```

### PR模板

```markdown
## AI开发者信息
- AI_ID: AI-2
- 模块: 销售管理模块

## 变更内容
- 新增 SalesOrderController
- 新增 SalesOrderService 及实现
- 新增销售订单相关DTO/VO

## 测试情况
- [x] 单元测试通过
- [x] 接口测试通过

## 接口变更
- 新增: POST /api/v1/sales/orders
- 新增: GET /api/v1/sales/orders/{id}

## 依赖
- 需要 AI-4 的 POST /api/v1/inventory/reserve 接口
```

---

## 五、常见问题

### Q1：如何知道其他AI的进度？

```
用户发送：
请读取 AI-IDENTITY.md 文件，告诉我 AI-2 的进度。
```

### Q2：接口冲突怎么办？

```
1. 在 AI-IDENTITY.md 的 BLOCKERS 中记录问题
2. 等待用户协调
3. 或主动联系相关AI协商
```

### Q3：开发顺序是什么？

```
第一批：AI-1（基础数据）
第二批：AI-2, AI-3, AI-4（可并行）
第三批：AI-5, AI-6, AI-7（可并行）
第四批：AI-8（财务）
第五批：AI-9, AI-10（前端）
```

### Q4：代码风格如何保持一致？

```
所有AI必须：
1. 阅读 docs/多AI协作开发规范.md
2. 遵循阿里巴巴Java开发规范
3. 参考现有代码风格
4. 提交前进行代码审查
```

---

## 六、检查清单

### 开始开发前

- [ ] 已创建 AI-IDENTITY.md 文件
- [ ] 已阅读对应模块设计文档
- [ ] 已阅读多AI协作开发规范
- [ ] 已了解依赖关系

### 开发过程中

- [ ] 代码风格符合规范
- [ ] 已更新 AI-IDENTITY.md 进度
- [ ] 已记录对外提供的接口
- [ ] 已记录依赖的接口

### 完成开发后

- [ ] 单元测试通过
- [ ] 接口测试通过
- [ ] 已更新 AI-IDENTITY.md 状态为 completed
- [ ] 已创建 PR

---

## 八、任命管理者

### 8.1 管理者角色定义

项目设置 **1 个管理者AI**，不参与开发，专职监督和派工。

| 角色 | 职责 | 权限 |
|------|------|------|
| **Manager-AI** | 监督进度、派工、协调、汇报 | ✅ 查看所有文件<br>✅ 分配任务<br>✅ 协调跨模块问题<br>❌ 不能修改代码<br>❌ 不负责开发 |

### 8.2 任命管理者

**用户发送**：

```
你被任命为项目的 Manager-AI（管理者AI）。

职责：
1. 监督所有开发AI的进度
2. 给开发AI分配任务
3. 协调跨模块问题
4. 向用户汇报项目状态

权限：
- ✅ 可以查看所有文件
- ✅ 可以给开发AI派工
- ❌ 不能修改代码文件
- ❌ 不参与具体开发

请创建 AI-MANAGER.md 文件，开始工作。
```

**Manager-AI 回复**：

```
已创建 AI-MANAGER.md 文件。

我已读取所有 AI-IDENTITY.md，当前项目状态：

【进度汇总】
| AI_ID | 模块 | 状态 | 进度 |
|-------|------|------|------|
| AI-1 | 基础数据 | completed | 100% |
| AI-2 | 销售管理 | active | 60% |
| AI-3 | 采购管理 | pending | 0% |
| ... | ... | ... | ... |

【待派工】
- AI-4 库存管理模块需要启动
- AI-3 采购管理模块需要启动

【阻塞问题】
- AI-2 等待 AI-4 的库存接口

建议：先派工 AI-4 开发库存模块，解除 AI-2 的阻塞。
```

### 8.3 派工流程

**用户让管理者派工**：

```
作为 Manager-AI：
1. 读取所有 AI-IDENTITY.md 查看当前进度
2. 给空闲AI分配任务
3. 更新 AI-MANAGER.md 中的派工记录
```

**Manager-AI 执行**：

```
已分析项目状态，派工如下：

【今日派工】
1. AI-4：启动库存管理模块开发
   - 任务：完成库存实体类和Service层
   - 截止：2026-03-29 18:00

2. AI-3：启动采购管理模块开发
   - 任务：完成采购订单DTO/VO设计
   - 截止：2026-03-29 12:00

已更新 AI-MANAGER.md
```

### 8.4 查看项目状态

**用户发送**：

```
作为 Manager-AI，请汇报当前项目状态。
```

**Manager-AI 回复**：

```
【项目进度报告】

一、整体进度
- 已完成：1/10 模块（10%）
- 进行中：2/10 模块（20%）
- 未开始：7/10 模块（70%）

二、各AI状态
| AI_ID | 状态 | 进度 | 备注 |
|-------|------|------|------|
| AI-1 | ✅ 完成 | 100% | 基础数据模块 |
| AI-2 | 🔄 进行中 | 60% | 阻塞：等待AI-4接口 |
| AI-3 | 🔄 进行中 | 20% | 正常 |
| AI-4 | 🔄 进行中 | 10% | 刚启动 |
| AI-5~10 | ⬜ 未开始 | 0% | 等待派工 |

三、风险预警
- 🟡 AI-2 被 AI-4 阻塞，需关注
- 🟡 开发AI数量不足，建议增加

四、建议行动
1. 加速 AI-4 开发进度
2. 考虑并行启动 AI-5
```

### 8.5 管理者文件

**AI-MANAGER.md**（管理者身份文件）：

```yaml
# ============================================
# AI 管理者身份文件
# ============================================
ROLE: "Manager-AI"
AI_NAME: ""
APPOINT_TIME: ""
APPOINTED_BY: "用户"

# ============================================
# 管理范围（所有开发AI）
# ============================================
MANAGED_AI:
  - AI-1: 基础数据模块
  - AI-2: 销售管理模块
  - AI-3: 采购管理模块
  - AI-4: 库存管理模块
  - AI-5: 生产管理模块
  - AI-6: 工序计件模块
  - AI-7: 质量管理模块
  - AI-8: 财务成本模块
  - AI-9: 前端Web模块
  - AI-10: 小程序模块

# ============================================
# 今日派工记录
# ============================================
TODAY_ASSIGNMENTS: |
  - [ ] AI-X: 任务描述

# ============================================
# 进度监督记录
# ============================================
PROGRESS_STATUS: |
  AI-1: ✅ 已完成
  AI-2: 🔄 开发中
  ...

# ============================================
# 权限说明
# ============================================
PERMISSIONS:
  CAN_READ: "所有文件"
  CAN_ASSIGN: "分配任务"
  CANNOT_MODIFY: "不能修改代码"
  CANNOT_DEVELOP: "不参与开发"
```

### 8.6 管理者与开发AI的交互

```
Manager-AI                    开发AI
    │                           │
    │── 分配任务 ──────────────▶│
    │   (写入AI-MANAGER.md)     │
    │                           │
    │                           │── 执行开发
    │                           │
    │◀── 更新进度 ─────────────│
    │   (AI-IDENTITY.md)        │
    │                           │
    │── 检查监督 ──────────────▶│
    │                           │
    │── 发现问题，协调 ────────▶│
    │                           │
```

### 8.7 文件结构

```
duoduo_jxc/
├── AI-MANAGER.md              # 【管理者AI创建】管理者身份文件
├── AI-IDENTITY.md             # 【各开发AI创建】身份文件
├── AI-CONFIG.md               # 模块分配配置
├── AI-PROMPTS.md              # 启动提示词
│
├── docs/
│   └── ...
│
└── backend/                   # 代码（管理者只读，不能修改）
```
