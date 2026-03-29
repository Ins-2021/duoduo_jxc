# 服装行业全链路ERP系统设计方案

> 版本: v2.2  
> 更新日期: 2024-03-28  
> 项目: duoduo_jxc + coding 整合
> 
> 更新记录:
> - v2.2 (2024-03-28): 新增打印模板、数据导入导出、测试用例、性能优化章节
> - v2.1 (2024-03-28): 新增库存管理、报表统计、数据迁移、接口规范、前端组件库章节
> - v2.0 (2024-03-28): 新增销售管理、采购管理、财务收付款、错误码定义章节
> - v1.9 (2024-03-28): 新增数据库DDL、系统配置、消息通知、日志审计、部署架构、安全设计章节
> - v1.8 (2024-03-28): 新增小程序页面设计章节（uni-app）
> - v1.7 (2024-03-28): 新增前端页面交互设计章节
> - v1.6 (2024-03-28): 新增API接口详细设计
> - v1.5 (2024-03-28): 新增权限设计章节
> - v1.4 (2024-03-28): 新增数据字典与枚举章节
> - v1.3 (2024-03-28): 完善计件功能设计
> - v1.2 (2024-03-28): 新增灵活流程管理设计
> - v1.1 (2024-03-28): 新增销售→生产数据打通、库存一体化设计
> - v1.0 (2024-03-28): 初始版本

---

## 目录

### 第一部分：系统概述与架构

- [一、项目背景](#一项目背景)
- [二、系统架构](#二系统架构)
- [三、数据模型设计](#三数据模型设计)
- [四、API接口设计](#四api接口设计)
- [五、业务流程设计](#五业务流程设计)

### 第二部分：核心功能设计

- [六、工作流适配设计](#六工作流适配设计)
- [七、裁床后扫码计件流程](#七裁床后扫码计件流程)
- [八、打印功能设计](#八打印功能设计)
- [九、小程序功能设计](#九小程序功能设计)
- [十、前端页面设计](#十前端页面设计)

### 第三部分：数据打通与整合

- [十一、销售→生产数据打通](#十一销售生产数据打通)
- [十二、库存一体化设计](#十二库存一体化设计)
- [十三、财务成本联动](#十三财务成本联动)
- [十四、数据迁移策略](#十四数据迁移策略)
- [十五、实施计划](#十五实施计划)

### 第四部分：数据与权限设计

- [十六、数据表统计](#十六数据表统计)
- [十七、数据字典与枚举](#十七数据字典与枚举)
- [十八、权限设计](#十八权限设计)
- [十九、API接口详细设计](#十九api接口详细设计)

### 第五部分：前端与小程序设计

- [二十、前端页面交互设计](#二十前端页面交互设计)
- [二十一、小程序页面设计（uni-app）](#二十一小程序页面设计uni-app)

### 第六部分：数据库与技术架构

- [二十二、数据库DDL设计](#二十二数据库ddl设计)
- [二十三、系统配置设计](#二十三系统配置设计)
- [二十四、消息通知设计](#二十四消息通知设计)
- [二十五、日志与审计设计](#二十五日志与审计设计)
- [二十六、部署架构设计](#二十六部署架构设计)
- [二十七、安全设计](#二十七安全设计)

### 第七部分：业务模块详细设计

- [二十八、销售管理模块设计](#二十八销售管理模块设计)
- [二十九、采购管理模块设计](#二十九采购管理模块设计)
- [三十、财务收付款设计](#三十财务收付款设计)
- [三十一、错误码定义](#三十一错误码定义)
- [三十二、库存管理详细设计](#三十二库存管理详细设计)
- [三十三、报表统计详细设计](#三十三报表统计详细设计)

### 第八部分：实施与运维

- [三十四、数据迁移详细方案](#三十四数据迁移详细方案)
- [三十五、接口文档规范](#三十五接口文档规范)
- [三十六、前端组件库设计](#三十六前端组件库设计)
- [三十七、打印模板详细设计](#三十七打印模板详细设计)
- [三十八、数据导入导出设计](#三十八数据导入导出设计)
- [三十九、测试用例设计](#三十九测试用例设计)
- [四十、性能优化方案](#四十性能优化方案)

### 第九部分：成本与工资管理

- [四十一、成本核算详细设计](#四十一成本核算详细设计)
- [四十二、工资管理模块](#四十二工资管理模块)
- [四十三、消息实时推送设计](#四十三消息实时推送设计)

### 附录

- [附录：服装行业特性要点](#附录服装行业特性要点)

---

## 一、项目背景

### 1.1 现状分析

| 系统 | 技术栈 | 定位 | 完成度 |
|------|--------|------|--------|
| duoduo_jxc | Spring Boot + Vue 3 | 进销存/ERP | 85% |
| coding (衣多多) | Django + Vue 3 + 小程序 | 生产管理/MES | 90% |

### 1.2 整合目标

将两系统整合为**完整的服装行业全链路ERP系统**，覆盖：
- 销售管理（批发/零售）
- 采购管理
- 库存管理
- 财务管理
- 生产管理
- 质量管理
- 成本核算

### 1.3 技术选型

以 **Spring Boot** 为统一后端架构：
- 后端: Spring Boot 3.2 + MyBatis-Plus + Flowable
- 前端: Vue 3 + Element Plus + TypeScript
- 数据库: MySQL 8.0
- 缓存: Redis
- 小程序: uni-app 重写

---

## 二、系统架构

### 2.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────────────┐
│                              前端层                                      │
├─────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐         │
│  │   Web管理后台    │  │   小程序端       │  │   PDA/扫码枪    │         │
│  │   (Vue 3)       │  │  (uni-app)      │  │   (可选)        │         │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘         │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                           业务服务层 (Spring Boot)                       │
├─────────────────────────────────────────────────────────────────────────┤
│ 基础数据 │ 销售管理 │ 采购管理 │ 库存管理 │ 财务管理 │ 生产管理         │
│ 工序管理 │ 质量管理 │ 面料管理 │ 外协管理 │ 成本核算 │ 打印服务         │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                          数据存储层                                      │
│  MySQL (主数据)  │  Redis (缓存/会话)  │  MinIO/OSS (文件存储)          │
└─────────────────────────────────────────────────────────────────────────┘
```

### 2.2 模块划分

```
├── 基础数据模块 (已有 + 增强)
│   ├── 商品管理 (SPU/SKU) ✓
│   ├── 【新增】款式档案管理
│   ├── 【新增】尺码分类/尺码表
│   ├── 【新增】颜色档案
│   ├── 【新增】条码规则配置
│   ├── 客户/供应商/仓库 ✓
│   ├── 【新增】工厂/车间/班组管理
│   └── 【新增】辅料档案管理
│
├── 销售管理模块 (已有)
├── 采购管理模块 (已有)
├── 库存管理模块 (已有 + 增强)
├── 财务管理模块 (已有)
│
├── 【新增】生产管理模块
│   ├── 生产订单/计划/排程
│   ├── BOM物料清单
│   ├── 物料需求计划 (MRP)
│   └── 返工管理
│
├── 【新增】工序管理模块
├── 【新增】扎包管理模块
├── 【新增】裁床管理模块
├── 【新增】质量管理模块
├── 【新增】面料管理模块
├── 【新增】外协加工模块
├── 【新增】成本核算模块
├── 【新增】样衣打版模块
├── 【新增】装箱配码模块
├── 打印服务模块 (已有 + 增强)
└── 报表分析模块 (增强)
```

---

## 三、数据模型设计

### 3.1 款式管理

```sql
-- 款式档案
CREATE TABLE jxc_style (
    style_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    style_no VARCHAR(50) NOT NULL COMMENT '款号',
    style_name VARCHAR(200) NOT NULL COMMENT '款式名称',
    year INT COMMENT '年度',
    season VARCHAR(20) COMMENT '季节(春/夏/秋/冬/四季)',
    series VARCHAR(50) COMMENT '系列',
    category_id BIGINT COMMENT '品类ID',
    brand_id BIGINT COMMENT '品牌ID',
    designer_id BIGINT COMMENT '设计师ID',
    style_type VARCHAR(20) COMMENT '风格(休闲/商务/时尚/运动)',
    target_gender VARCHAR(20) COMMENT '适用性别(男/女/中性/儿童)',
    target_age_group VARCHAR(50) COMMENT '年龄段',
    design_image VARCHAR(500) COMMENT '设计图',
    sample_image VARCHAR(500) COMMENT '样衣图',
    tech_pack VARCHAR(500) COMMENT '工艺单附件',
    status TINYINT DEFAULT 1 COMMENT '状态(开发中/已定版/已投产/已停产)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_style_no (style_no)
) COMMENT '款式档案表';

-- 款式配色
CREATE TABLE jxc_style_colorway (
    colorway_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    style_id BIGINT NOT NULL,
    colorway_no VARCHAR(50) COMMENT '配色号',
    colorway_name VARCHAR(100) COMMENT '配色名称',
    main_color VARCHAR(50) COMMENT '主色',
    accent_color VARCHAR(50) COMMENT '配色',
    color_images JSON COMMENT '配色图片',
    is_active TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '款式配色表';
```

### 3.2 尺码管理

```sql
-- 尺码分类
CREATE TABLE jxc_size_category (
    category_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    code VARCHAR(30) NOT NULL COMMENT '分类编码',
    description VARCHAR(100) COMMENT '描述',
    sort_order INT DEFAULT 0,
    is_active TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_code (code)
) COMMENT '尺码分类表(男装尺码/女装尺码/童装尺码/欧码)';

-- 尺码选项
CREATE TABLE jxc_size_option (
    option_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT NOT NULL COMMENT '所属分类',
    name VARCHAR(20) NOT NULL COMMENT '尺码名称(S/M/L/XL)',
    code VARCHAR(20) NOT NULL COMMENT '尺码编码',
    sort_order INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_category_code (category_id, code)
) COMMENT '尺码选项表';

-- 尺码表(详细规格)
CREATE TABLE jxc_size_chart (
    chart_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    standard_id BIGINT COMMENT '尺码标准ID',
    category_id BIGINT COMMENT '品类ID',
    size_code VARCHAR(20) COMMENT '尺码',
    bust DECIMAL(10,2) COMMENT '胸围',
    waist DECIMAL(10,2) COMMENT '腰围',
    hip DECIMAL(10,2) COMMENT '臀围',
    shoulder_width DECIMAL(10,2) COMMENT '肩宽',
    sleeve_length DECIMAL(10,2) COMMENT '袖长',
    length DECIMAL(10,2) COMMENT '衣长',
    inseam DECIMAL(10,2) COMMENT '内长',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '尺码规格表';

-- 尺码配比模板
CREATE TABLE jxc_size_ratio_template (
    template_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '模板名称',
    category_id BIGINT COMMENT '适用品类',
    ratios JSON COMMENT '配比[{size, ratio}]',
    description VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '尺码配比模板';
```

### 3.3 颜色管理

```sql
-- 颜色档案
CREATE TABLE jxc_color (
    color_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '颜色名称',
    code VARCHAR(30) NOT NULL COMMENT '颜色编码',
    color_value VARCHAR(20) COMMENT '颜色值(#FF0000)',
    pantone_no VARCHAR(30) COMMENT '潘通色号',
    sort_order INT DEFAULT 0,
    is_active TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_code (code)
) COMMENT '颜色档案表';
```

### 3.4 条码规则

```sql
-- 条码规则
CREATE TABLE jxc_barcode_rule (
    rule_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    rule_type VARCHAR(20) COMMENT '类型(SKU/扎包/箱码)',
    prefix VARCHAR(20) COMMENT '前缀',
    date_format VARCHAR(20) COMMENT '日期格式',
    seq_length INT DEFAULT 4 COMMENT '序列号长度',
    rule_expression VARCHAR(200) COMMENT '规则表达式',
    example VARCHAR(100) COMMENT '示例',
    is_default TINYINT DEFAULT 0,
    is_active TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '条码生成规则表';
```

### 3.5 工序管理

```sql
-- 工序库
CREATE TABLE jxc_process (
    process_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL COMMENT '工序编码',
    name VARCHAR(100) NOT NULL COMMENT '工序名称',
    process_type VARCHAR(30) COMMENT '工序类型',
    process_category VARCHAR(30) COMMENT '工序大类(裁剪/缝制/整烫/质检/包装)',
    equipment_type VARCHAR(50) COMMENT '设备类型(平车/双针/锁边机/烫台)',
    difficulty_level INT DEFAULT 1 COMMENT '难度等级(1-5)',
    standard_time INT DEFAULT 0 COMMENT '标准工时(秒)',
    default_price1 DECIMAL(10,4) DEFAULT 0 COMMENT '默认单价1',
    default_price2 DECIMAL(10,4) DEFAULT 0 COMMENT '默认单价2',
    default_price3 DECIMAL(10,4) DEFAULT 0 COMMENT '默认单价3',
    skill_requirement VARCHAR(200) COMMENT '技能要求',
    quality_checkpoints JSON COMMENT '质检要点',
    sort_order INT DEFAULT 0,
    is_active TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_code (code)
) COMMENT '工序库表';

-- 工序依赖
CREATE TABLE jxc_process_dependency (
    dependency_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    process_id BIGINT NOT NULL COMMENT '工序ID',
    depends_on_id BIGINT NOT NULL COMMENT '依赖工序ID',
    dependency_type VARCHAR(20) DEFAULT 'required' COMMENT '依赖类型(必须完成/可选)',
    description VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_process_depends (process_id, depends_on_id)
) COMMENT '工序依赖关系表';

-- 货品工序配置(计件单价)
CREATE TABLE jxc_product_process_config (
    config_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL COMMENT '货品ID',
    process_id BIGINT NOT NULL COMMENT '工序ID',
    price1 DECIMAL(10,4) DEFAULT 0 COMMENT '单价1',
    price2 DECIMAL(10,4) DEFAULT 0 COMMENT '单价2',
    price3 DECIMAL(10,4) DEFAULT 0 COMMENT '单价3',
    sort_order INT DEFAULT 0 COMMENT '工序顺序',
    is_enabled TINYINT DEFAULT 1,
    remark VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_product_process (product_id, process_id)
) COMMENT '货品工序配置表';

-- 工序任务
CREATE TABLE jxc_process_task (
    task_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_no VARCHAR(50) NOT NULL COMMENT '任务编号',
    bundle_id BIGINT NOT NULL COMMENT '扎包ID',
    process_id BIGINT NOT NULL COMMENT '工序ID',
    target_quantity INT NOT NULL COMMENT '目标数量',
    completed_quantity INT DEFAULT 0 COMMENT '已完成数量',
    assigned_to BIGINT COMMENT '分配给(员工ID)',
    deadline DATETIME COMMENT '截止时间',
    wf_task_id VARCHAR(64) COMMENT '工作流任务ID',
    wf_node_key VARCHAR(100) COMMENT '工作流节点Key',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态(待接单/已接单/进行中/已完成/已取消)',
    accepted_at DATETIME COMMENT '接单时间',
    completed_at DATETIME COMMENT '完成时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_task_no (task_no)
) COMMENT '工序任务表';

-- 计件记录
CREATE TABLE jxc_process_record (
    record_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    record_no VARCHAR(50) NOT NULL COMMENT '记录编号',
    bundle_id BIGINT NOT NULL COMMENT '扎包ID',
    process_id BIGINT NOT NULL COMMENT '工序ID',
    sku_id BIGINT COMMENT 'SKU ID(用于溢价计算)',
    
    -- 工人信息
    worker_id BIGINT NOT NULL COMMENT '主工人ID',
    is_team_work TINYINT DEFAULT 0 COMMENT '是否团队协作',
    team_type VARCHAR(20) COMMENT '协作类型(main_assistant/custom)',
    team_group_id VARCHAR(50) COMMENT '团队组ID(同一批多人计件共用)',
    
    -- 计件数量
    quantity INT NOT NULL COMMENT '计件数量',
    
    -- 单价快照固化 (财务严谨性)
    price_level VARCHAR(10) NOT NULL COMMENT '价格等级(price1/price2/price3)',
    base_price DECIMAL(10,4) NOT NULL COMMENT 'SPU基准单价(快照)',
    premium_ratio DECIMAL(5,2) DEFAULT 0 COMMENT 'SKU溢价比例(%)',
    unit_price DECIMAL(10,4) NOT NULL COMMENT '最终单价(快照)=base_price*(1+premium_ratio)',
    share_ratio DECIMAL(5,2) DEFAULT 100 COMMENT '分配比例(%)',
    amount DECIMAL(12,4) NOT NULL COMMENT '计件金额=quantity*unit_price*share_ratio/100',
    
    -- 记录类型
    record_type VARCHAR(20) DEFAULT 'normal' COMMENT '类型(normal/rework/deduction/bonus)',
    related_rework_id BIGINT COMMENT '关联返工单ID',
    deduction_reason VARCHAR(200) COMMENT '扣款原因',
    
    -- 结算状态 (结算闭环)
    settlement_status VARCHAR(20) DEFAULT 'unsettled' COMMENT '结算状态(unsettled/settled)',
    salary_sheet_id BIGINT COMMENT '关联工资单ID',
    settled_at DATETIME COMMENT '结算时间',
    
    -- 基础信息
    scan_time DATETIME NOT NULL COMMENT '扫码时间',
    confirm_time DATETIME COMMENT '确认时间',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态(待确认/已确认/已驳回)',
    device_id VARCHAR(100) COMMENT '设备ID',
    location VARCHAR(100) COMMENT '位置',
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_record_no (record_no),
    INDEX idx_worker_settlement (worker_id, settlement_status),
    INDEX idx_salary_sheet (salary_sheet_id)
) COMMENT '计件记录表';

-- 多人计件分配明细表
CREATE TABLE jxc_process_record_share (
    share_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    record_id BIGINT NOT NULL COMMENT '主记录ID',
    team_group_id VARCHAR(50) COMMENT '团队组ID',
    worker_id BIGINT NOT NULL COMMENT '工人ID',
    worker_role VARCHAR(20) COMMENT '角色(main/assistant/member)',
    share_ratio DECIMAL(5,2) NOT NULL COMMENT '分配比例(%)',
    share_amount DECIMAL(12,4) NOT NULL COMMENT '分配金额',
    settlement_status VARCHAR(20) DEFAULT 'unsettled' COMMENT '结算状态',
    salary_sheet_id BIGINT COMMENT '关联工资单ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_record (record_id),
    INDEX idx_team_group (team_group_id)
) COMMENT '多人计件分配明细表';

-- 计件记录防重表
CREATE TABLE jxc_process_record_dedup (
    dedup_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id VARCHAR(50) NOT NULL COMMENT '请求唯一ID(前端生成的UUID)',
    record_id BIGINT COMMENT '关联的计件记录ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_request_id (request_id)
) COMMENT '计件记录防重表(防止网络重复提交)';

-- 工价匹配规则表
CREATE TABLE jxc_price_match_rule (
    rule_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    skill_level VARCHAR(20) NOT NULL COMMENT '技能等级(trainee/standard/expert)',
    time_period VARCHAR(20) NOT NULL COMMENT '时段(normal/overtime/night)',
    price_field VARCHAR(10) NOT NULL COMMENT '使用价格字段(price1/price2/price3)',
    priority INT DEFAULT 0 COMMENT '优先级',
    is_active TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_level_period (skill_level, time_period)
) COMMENT '工价匹配规则表';

-- SKU计件溢价配置表
CREATE TABLE jxc_sku_price_premium (
    premium_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    process_id BIGINT NOT NULL COMMENT '工序ID',
    premium_ratio DECIMAL(5,2) DEFAULT 0 COMMENT '溢价比例(%)',
    fixed_price DECIMAL(10,4) COMMENT '固定价格(优先于溢价比例)',
    premium_reason VARCHAR(100) COMMENT '溢价原因(大码/特殊面料/复杂工艺)',
    is_active TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_sku_process (sku_id, process_id)
) COMMENT 'SKU计件溢价配置表';

-- 工序异常
CREATE TABLE jxc_process_exception (
    exception_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    exception_no VARCHAR(50) NOT NULL COMMENT '异常编号',
    bundle_id BIGINT COMMENT '扎包ID',
    process_id BIGINT COMMENT '工序ID',
    exception_type VARCHAR(30) COMMENT '异常类型(缺料/设备故障/质量问题/其他)',
    description TEXT COMMENT '描述',
    images JSON COMMENT '图片',
    reported_by BIGINT COMMENT '上报人',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态(待处理/处理中/已解决/已驳回)',
    handled_by BIGINT COMMENT '处理人',
    handling_remark TEXT COMMENT '处理备注',
    resolved_at DATETIME COMMENT '解决时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_exception_no (exception_no)
) COMMENT '工序异常表';
```

### 3.6 扎包管理

```sql
-- 扎包
CREATE TABLE jxc_bundle (
    bundle_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bundle_no VARCHAR(50) NOT NULL COMMENT '扎包号',
    cutting_bundle_id BIGINT COMMENT '裁床扎包ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    size VARCHAR(20) COMMENT '尺码',
    color VARCHAR(50) COMMENT '颜色',
    quantity INT NOT NULL COMMENT '数量',
    current_process_id BIGINT COMMENT '当前工序ID',
    work_group_id BIGINT COMMENT '负责班组ID',
    wf_instance_id BIGINT COMMENT '工作流实例ID',
    wf_status VARCHAR(20) COMMENT '工作流状态',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态(待分配/已分配/生产中/已完成/异常/已退还)',
    qr_code VARCHAR(500) COMMENT '二维码图片URL',
    qr_data JSON COMMENT '二维码内容',
    location VARCHAR(100) COMMENT '当前位置',
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_bundle_no (bundle_no)
) COMMENT '扎包表';

-- 扎包工序关联
CREATE TABLE jxc_bundle_process (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bundle_id BIGINT NOT NULL,
    process_id BIGINT NOT NULL,
    sequence INT NOT NULL COMMENT '顺序',
    
    -- 计件数量追踪 (并发控制)
    target_quantity INT NOT NULL COMMENT '目标数量(扎包总件数)',
    completed_quantity INT DEFAULT 0 COMMENT '已完成计件数量',
    remaining_quantity INT DEFAULT 0 COMMENT '剩余可计件数量',
    
    -- 乐观锁
    version INT DEFAULT 0 COMMENT '版本号(乐观锁)',
    
    is_completed TINYINT DEFAULT 0,
    completed_at DATETIME,
    completed_by BIGINT,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_bundle_process (bundle_id, process_id)
) COMMENT '扎包工序关联表';
```

### 3.7 生产管理

```sql
-- 生产订单
CREATE TABLE jxc_production_order (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL COMMENT '订单号',
    style_no VARCHAR(100) COMMENT '款号',
    style_name VARCHAR(200) COMMENT '款式名称',
    style_id BIGINT COMMENT '款式ID',
    customer_id BIGINT COMMENT '客户ID',
    quantity INT NOT NULL COMMENT '订单数量',
    completed_quantity INT DEFAULT 0 COMMENT '已完成数量',
    deadline DATE COMMENT '交期',
    priority VARCHAR(20) DEFAULT 'normal' COMMENT '优先级(低/普通/高/紧急)',
    factory_id BIGINT COMMENT '所属工厂ID',
    source_order_id BIGINT COMMENT '来源销售订单ID',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态(待生产/生产中/已完成/已取消)',
    remark TEXT,
    create_by BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_order_no (order_no)
) COMMENT '生产订单表';

-- 订单尺码配比
CREATE TABLE jxc_order_size_ratio (
    ratio_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    size VARCHAR(20) NOT NULL,
    color VARCHAR(50) NOT NULL,
    ratio DECIMAL(5,2) DEFAULT 0 COMMENT '比例(%)',
    quantity INT DEFAULT 0 COMMENT '数量',
    UNIQUE KEY uk_order_size_color (order_id, size, color)
) COMMENT '订单尺码配比表';

-- 生产计划
CREATE TABLE jxc_production_plan (
    plan_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_no VARCHAR(50) NOT NULL,
    order_id BIGINT NOT NULL,
    planned_date DATE COMMENT '计划日期',
    planned_quantity INT COMMENT '计划数量',
    actual_quantity INT DEFAULT 0 COMMENT '实际完成',
    cutting_group_id BIGINT COMMENT '裁床组ID',
    sewing_group_id BIGINT COMMENT '缝制组ID',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态(草稿/已确认/进行中/已完成/已取消)',
    remark TEXT,
    create_by BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_plan_no (plan_no)
) COMMENT '生产计划表';

-- 生产排程
CREATE TABLE jxc_production_schedule (
    schedule_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    schedule_no VARCHAR(50) NOT NULL,
    order_id BIGINT NOT NULL,
    start_date DATE,
    end_date DATE,
    schedule_items JSON COMMENT '排程明细',
    status VARCHAR(20) DEFAULT 'draft',
    create_by BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_schedule_no (schedule_no)
) COMMENT '生产排程表';

-- 返工单
CREATE TABLE jxc_rework_order (
    rework_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rework_no VARCHAR(50) NOT NULL,
    quality_check_id BIGINT NOT NULL COMMENT '质检记录ID',
    bundle_id BIGINT NOT NULL COMMENT '扎包ID',
    defect_types JSON COMMENT '缺陷类型',
    rework_quantity INT COMMENT '返工数量',
    rework_process_id BIGINT COMMENT '返工工序ID',
    assigned_to BIGINT COMMENT '分配给',
    deadline DATE COMMENT '完成期限',
    completed_quantity INT DEFAULT 0 COMMENT '已完成数量',
    completed_at DATETIME,
    recheck_passed TINYINT COMMENT '复检是否通过',
    recheck_by BIGINT COMMENT '复检人',
    recheck_at DATETIME COMMENT '复检时间',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态(待分配/已分配/返工中/已完成/已复检)',
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_rework_no (rework_no)
) COMMENT '返工单表';

-- 返工计件关联表
CREATE TABLE jxc_rework_piece_work (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rework_id BIGINT NOT NULL COMMENT '返工单ID',
    bundle_id BIGINT NOT NULL COMMENT '扎包ID',
    
    -- 原工序扣款
    original_record_id BIGINT COMMENT '原计件记录ID',
    original_worker_id BIGINT COMMENT '原工人ID',
    original_amount DECIMAL(12,4) COMMENT '原计件金额',
    deduction_ratio DECIMAL(5,2) COMMENT '扣款比例(%)',
    deduction_amount DECIMAL(12,4) DEFAULT 0 COMMENT '扣款金额',
    deduction_record_id BIGINT COMMENT '扣款计件记录ID',
    
    -- 返工计件
    rework_record_id BIGINT COMMENT '返工计件记录ID',
    rework_worker_id BIGINT COMMENT '返工工人ID',
    rework_amount DECIMAL(12,4) COMMENT '返工计件金额',
    
    -- 责任判定
    responsibility VARCHAR(20) COMMENT '责任归属(worker/material/design/other)',
    responsibility_detail VARCHAR(200) COMMENT '责任说明',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_rework (rework_id),
    INDEX idx_original_worker (original_worker_id)
) COMMENT '返工计件关联表';

-- 返工扣款配置表
CREATE TABLE jxc_rework_deduction_config (
    config_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    defect_type VARCHAR(50) NOT NULL COMMENT '缺陷类型',
    severity VARCHAR(20) NOT NULL COMMENT '严重程度(critical/major/minor)',
    responsibility_type VARCHAR(20) NOT NULL COMMENT '责任类型(worker/material/other)',
    deduction_ratio DECIMAL(5,2) DEFAULT 0 COMMENT '扣款比例(%)',
    description VARCHAR(200),
    is_active TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_defect_severity (defect_type, severity, responsibility_type)
) COMMENT '返工扣款配置表';
```

### 3.8 BOM物料清单

```sql
-- BOM主表
CREATE TABLE jxc_bom (
    bom_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bom_no VARCHAR(50) NOT NULL COMMENT 'BOM编号',
    product_id BIGINT NOT NULL COMMENT '货品ID',
    style_id BIGINT COMMENT '款式ID',
    version VARCHAR(20) DEFAULT '1.0' COMMENT '版本号',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态(草稿/生效/失效)',
    effective_date DATE COMMENT '生效日期',
    expiry_date DATE COMMENT '失效日期',
    remark TEXT,
    create_by BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_bom_no (bom_no)
) COMMENT 'BOM物料清单主表';

-- BOM明细
CREATE TABLE jxc_bom_item (
    item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bom_id BIGINT NOT NULL,
    material_type VARCHAR(20) COMMENT '物料类型(面料/辅料/包装材料)',
    material_id BIGINT COMMENT '物料ID',
    material_name VARCHAR(100) COMMENT '物料名称',
    material_code VARCHAR(50) COMMENT '物料编码',
    unit VARCHAR(20) COMMENT '单位',
    usage_per_unit DECIMAL(10,4) COMMENT '单件用量',
    wastage_rate DECIMAL(5,2) DEFAULT 0 COMMENT '损耗率(%)',
    specification VARCHAR(100) COMMENT '规格',
    color VARCHAR(50) COMMENT '颜色',
    position VARCHAR(50) COMMENT '使用部位',
    sort_order INT DEFAULT 0,
    remark VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT 'BOM物料清单明细表';

-- BOM工序清单
CREATE TABLE jxc_bom_process (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bom_id BIGINT NOT NULL,
    process_id BIGINT NOT NULL,
    sequence INT COMMENT '工序顺序',
    standard_time INT COMMENT '标准工时(秒)',
    work_group_id BIGINT COMMENT '推荐班组ID',
    remark VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT 'BOM工序清单表';

-- BOM颜色映射规则表 (解决"同款不同色用料不同"问题)
CREATE TABLE jxc_bom_color_mapping (
    mapping_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bom_id BIGINT NOT NULL COMMENT 'BOM主表ID',
    product_color VARCHAR(50) NOT NULL COMMENT '成品颜色',
    material_type VARCHAR(20) COMMENT '物料类型(fabric/accessory)',
    material_id BIGINT COMMENT '物料ID',
    material_color VARCHAR(50) COMMENT '对应物料颜色',
    usage_ratio DECIMAL(5,2) DEFAULT 100 COMMENT '用量比例(%)',
    remark VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_bom (bom_id)
) COMMENT 'BOM颜色映射规则表';

-- BOM尺码映射规则表 (解决"同款不同码辅料不同"问题)
CREATE TABLE jxc_bom_size_mapping (
    mapping_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bom_id BIGINT NOT NULL COMMENT 'BOM主表ID',
    product_size VARCHAR(20) NOT NULL COMMENT '成品尺码',
    material_type VARCHAR(20) COMMENT '物料类型',
    material_id BIGINT COMMENT '物料ID',
    material_spec VARCHAR(100) COMMENT '辅料规格(如拉链长度)',
    usage_adjust DECIMAL(10,4) COMMENT '用量调整值',
    remark VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_bom (bom_id)
) COMMENT 'BOM尺码映射规则表';
```

#### 3.8.1 BOM矩阵设计说明

**业务场景**：服装行业典型特征是同款不同色码的用料不同。

**示例**：
- 红色L码衣服 → 需要"红色面料" + "L码拉链(18cm)"
- 蓝色S码衣服 → 需要"蓝色面料" + "S码拉链(15cm)"

**矩阵解析逻辑**：
```
生成生产计划时：
1. 获取订单的颜色尺码组合
2. 从 jxc_bom_item 获取基础用量
3. 从 jxc_bom_color_mapping 解析颜色映射
4. 从 jxc_bom_size_mapping 解析尺码映射
5. 计算最终物料需求
```

**SQL示例**：
```sql
-- 解析某订单的物料需求
SELECT 
    bom.material_type,
    COALESCE(cm.material_id, bom.material_id) AS actual_material_id,
    COALESCE(cm.material_color, bom.color) AS actual_color,
    COALESCE(sm.usage_adjust, bom.usage_per_unit) AS actual_usage,
    order_item.quantity AS order_qty
FROM jxc_bom_item bom
LEFT JOIN jxc_bom_color_mapping cm 
    ON bom.bom_id = cm.bom_id 
    AND order_item.color = cm.product_color
LEFT JOIN jxc_bom_size_mapping sm 
    ON bom.bom_id = sm.bom_id 
    AND order_item.size = sm.product_size
WHERE bom.bom_id = :bom_id;
```

### 3.9 裁床管理

```sql
-- 裁床计划
CREATE TABLE jxc_cutting_plan (
    plan_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_no VARCHAR(50) NOT NULL,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    fabric_id BIGINT NOT NULL COMMENT '面料ID',
    marker_id BIGINT COMMENT '唛架ID',
    planned_quantity INT NOT NULL COMMENT '计划裁剪数量',
    cut_quantity INT DEFAULT 0 COMMENT '已裁数量',
    cutting_date DATE COMMENT '裁剪日期',
    cutting_group_id BIGINT COMMENT '裁床组ID',
    cutting_machine VARCHAR(50) COMMENT '裁床机台',
    spread_method VARCHAR(20) COMMENT '铺布方式(单向/往返/对合)',
    fabric_consumption DECIMAL(10,2) DEFAULT 0 COMMENT '面料消耗(米)',
    fabric_defect_rate DECIMAL(5,2) DEFAULT 0 COMMENT '面料疵点率(%)',
    utilization_rate DECIMAL(5,2) DEFAULT 0 COMMENT '利用率(%)',
    actual_efficiency DECIMAL(5,2) COMMENT '实际利用率(%)',
    cut_pieces INT COMMENT '实际裁片数',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态(待执行/进行中/已完成/已取消)',
    remark TEXT,
    create_by BIGINT,
    completed_at DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_plan_no (plan_no)
) COMMENT '裁床计划表';

-- 裁床扎包
CREATE TABLE jxc_cutting_bundle (
    bundle_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bundle_no VARCHAR(50) NOT NULL,
    cutting_plan_id BIGINT NOT NULL,
    size VARCHAR(20) COMMENT '尺码',
    color VARCHAR(50) COMMENT '颜色',
    quantity INT NOT NULL COMMENT '数量',
    bundle_quantity INT DEFAULT 1 COMMENT '每扎件数',
    max_per_bundle INT DEFAULT 20 COMMENT '每扎最大件数',
    process_id BIGINT COMMENT '目标工序ID',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态(待分配/已分配/进行中/已完成/异常)',
    qr_code VARCHAR(500) COMMENT '二维码',
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_bundle_no (bundle_no)
) COMMENT '裁床扎包表';

-- 唛架/排料图
CREATE TABLE jxc_marker (
    marker_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    marker_no VARCHAR(50) NOT NULL,
    style_no VARCHAR(100) COMMENT '款号',
    fabric_id BIGINT COMMENT '面料ID',
    fabric_width DECIMAL(10,2) COMMENT '面料幅宽(cm)',
    marker_length DECIMAL(10,2) COMMENT '唛架长度(m)',
    marker_efficiency DECIMAL(5,2) COMMENT '排料利用率(%)',
    layers INT COMMENT '层数',
    pieces_per_layer INT COMMENT '每层件数',
    size_breakdown JSON COMMENT '尺码分解[{size, pieces}]',
    marker_image VARCHAR(500) COMMENT '唛架图',
    marker_file VARCHAR(500) COMMENT '排料文件(CAD/DXF)',
    create_by BIGINT,
    status VARCHAR(20) DEFAULT 'draft',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_marker_no (marker_no)
) COMMENT '唛架/排料图表';
```

### 3.10 质量管理

```sql
-- 首件确认
CREATE TABLE jxc_first_article_confirmation (
    confirmation_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    confirmation_no VARCHAR(50) NOT NULL,
    cutting_plan_id BIGINT NOT NULL COMMENT '裁床计划ID',
    bundle_id BIGINT COMMENT '首件扎包ID',
    size VARCHAR(20) COMMENT '尺码',
    color VARCHAR(50) COMMENT '颜色',
    check_items JSON COMMENT '检验项目',
    result VARCHAR(20) DEFAULT 'pending' COMMENT '结论(待确认/合格/不合格/返工后待确认)',
    images JSON COMMENT '图片',
    checker_id BIGINT COMMENT '确认人ID',
    check_time DATETIME COMMENT '确认时间',
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_confirmation_no (confirmation_no)
) COMMENT '首件确认表';

-- 质检标准
CREATE TABLE jxc_quality_standard (
    standard_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT COMMENT '订单ID',
    process_id BIGINT COMMENT '工序ID',
    check_items JSON COMMENT '检查项目',
    total_score INT DEFAULT 100 COMMENT '总分',
    pass_score INT DEFAULT 60 COMMENT '及格分数',
    is_active TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_order_process (order_id, process_id)
) COMMENT '质检标准表';

-- 质检记录
CREATE TABLE jxc_quality_check (
    check_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    check_no VARCHAR(50) NOT NULL,
    bundle_id BIGINT NOT NULL COMMENT '扎包ID',
    process_id BIGINT COMMENT '工序ID',
    checker_id BIGINT COMMENT '质检员ID',
    check_type VARCHAR(20) DEFAULT 'sample' COMMENT '质检类型(全检/抽检)',
    check_quantity INT COMMENT '检验数量',
    qualified_quantity INT DEFAULT 0 COMMENT '合格数量',
    result VARCHAR(20) COMMENT '结果(合格/不合格/返工)',
    score INT COMMENT '得分',
    defect_types JSON COMMENT '缺陷类型',
    images JSON COMMENT '图片',
    check_time DATETIME COMMENT '质检时间',
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_check_no (check_no)
) COMMENT '质检记录表';

-- 缺陷记录
CREATE TABLE jxc_defect_record (
    defect_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    defect_no VARCHAR(50) NOT NULL,
    quality_check_id BIGINT NOT NULL,
    defect_type VARCHAR(50) COMMENT '缺陷类型',
    defect_position VARCHAR(100) COMMENT '缺陷位置',
    quantity INT DEFAULT 1,
    suspected_process_id BIGINT COMMENT '疑似问题工序ID',
    suspected_worker_id BIGINT COMMENT '疑似问题员工ID',
    handling_method VARCHAR(20) DEFAULT 'rework' COMMENT '处理方式(返工/报废/让步接收)',
    handling_remark TEXT COMMENT '处理备注',
    handled_by BIGINT COMMENT '处理人',
    handled_at DATETIME COMMENT '处理时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_defect_no (defect_no)
) COMMENT '缺陷记录表';

-- 巡检记录
CREATE TABLE jxc_patrol_check (
    patrol_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patrol_no VARCHAR(50) NOT NULL,
    work_group_id BIGINT NOT NULL COMMENT '巡检班组ID',
    process_id BIGINT NOT NULL COMMENT '巡检工序ID',
    check_items JSON COMMENT '检查项目',
    total_checked INT DEFAULT 0 COMMENT '检查总数',
    passed_count INT DEFAULT 0 COMMENT '合格数',
    failed_count INT DEFAULT 0 COMMENT '不合格数',
    issues JSON COMMENT '问题记录',
    patrol_time DATETIME COMMENT '巡检时间',
    patrol_by BIGINT COMMENT '巡检员ID',
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_patrol_no (patrol_no)
) COMMENT '巡检记录表';

-- 缺陷分类
CREATE TABLE jxc_defect_category (
    category_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(30) NOT NULL,
    parent_id BIGINT COMMENT '父分类ID',
    severity VARCHAR(20) COMMENT '严重程度(致命/严重/轻微)',
    sort_order INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_code (code)
) COMMENT '缺陷分类表';

-- AQL标准
CREATE TABLE jxc_aql_standard (
    standard_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    sample_level VARCHAR(20) COMMENT '检验水平',
    aql_critical DECIMAL(5,2) DEFAULT 0 COMMENT '致命缺陷AQL',
    aql_major DECIMAL(5,2) DEFAULT 2.5 COMMENT '严重缺陷AQL',
    aql_minor DECIMAL(5,2) DEFAULT 4.0 COMMENT '轻微缺陷AQL',
    is_active TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT 'AQL抽检标准表';
```

### 3.11 面料管理

```sql
-- 面料档案
CREATE TABLE jxc_fabric (
    fabric_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '品名',
    fabric_type VARCHAR(30) COMMENT '面料类型(梭织/针织/毛织/皮革)',
    fabric_structure VARCHAR(30) COMMENT '织物结构(平纹/斜纹/缎纹/提花)',
    fabric_usage VARCHAR(20) COMMENT '用途(主料/里料/辅料/衬料)',
    weight VARCHAR(50) COMMENT '克重',
    width VARCHAR(50) COMMENT '幅宽',
    composition VARCHAR(100) COMMENT '成分',
    supplier_id BIGINT COMMENT '供应商ID',
    price DECIMAL(10,4) COMMENT '单价',
    unit VARCHAR(20) DEFAULT '米' COMMENT '单位',
    stock DECIMAL(12,2) DEFAULT 0 COMMENT '库存数量',
    reserved_stock DECIMAL(12,2) DEFAULT 0 COMMENT '占用库存',
    available_stock DECIMAL(12,2) DEFAULT 0 COMMENT '可用库存',
    warning_threshold DECIMAL(12,2) DEFAULT 100 COMMENT '预警阈值',
    color_fastness VARCHAR(20) COMMENT '色牢度等级',
    shrinkage_rate DECIMAL(5,2) COMMENT '缩水率(%)',
    stretch_rate DECIMAL(5,2) COMMENT '弹力率(%)',
    shelf_life INT DEFAULT 365 COMMENT '保质期(天)',
    moq DECIMAL(10,2) COMMENT '起订量',
    lead_time INT COMMENT '货期(天)',
    storage_condition VARCHAR(200) COMMENT '存储条件',
    test_report VARCHAR(500) COMMENT '检测报告附件',
    sample_image VARCHAR(500) COMMENT '面料样卡图片',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态(在用/停用/已过期)',
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '面料档案表';

-- 面料色号
CREATE TABLE jxc_fabric_color (
    color_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    fabric_id BIGINT NOT NULL,
    color_no VARCHAR(50) COMMENT '色号',
    color_name VARCHAR(50) COMMENT '颜色名',
    pantone_no VARCHAR(30) COMMENT '潘通色号',
    color_card_image VARCHAR(500) COMMENT '色卡图',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '面料色号表';

-- 面料批次
CREATE TABLE jxc_fabric_batch (
    batch_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    fabric_id BIGINT NOT NULL,
    fabric_color_id BIGINT,
    batch_no VARCHAR(100) NOT NULL COMMENT '批号/缸号',
    roll_count INT COMMENT '卷数',
    total_length DECIMAL(10,2) COMMENT '总长度',
    total_weight DECIMAL(10,2) COMMENT '总重量',
    defect_length DECIMAL(10,2) COMMENT '疵布长度',
    grade VARCHAR(10) COMMENT '等级(A/B/C)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '面料批次表';

-- 面料入库
CREATE TABLE jxc_fabric_inbound (
    inbound_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    inbound_no VARCHAR(50) NOT NULL,
    fabric_id BIGINT NOT NULL,
    batch_no VARCHAR(100) COMMENT '批次号',
    quantity DECIMAL(12,2) NOT NULL COMMENT '入库数量',
    received_quantity DECIMAL(12,2) DEFAULT 0 COMMENT '实收数量',
    quality_status VARCHAR(20) DEFAULT 'pending' COMMENT '质检状态(待质检/合格/不合格)',
    warehouse_location VARCHAR(100) COMMENT '仓库位置',
    inbound_date DATE COMMENT '入库日期',
    operator_id BIGINT COMMENT '入库人ID',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态(待入库/部分入库/已完成/已取消)',
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_inbound_no (inbound_no)
) COMMENT '面料入库表';

-- 面料检验
CREATE TABLE jxc_fabric_inspection (
    inspection_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    fabric_batch_id BIGINT NOT NULL,
    inspector_id BIGINT COMMENT '检验员ID',
    inspection_date DATE COMMENT '检验日期',
    check_items JSON COMMENT '检验项目',
    defect_details JSON COMMENT '疵点明细',
    result VARCHAR(20) COMMENT '结果(合格/让步接收/退货)',
    inspection_report VARCHAR(500) COMMENT '检验报告',
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '面料检验表';

-- 领料申请
CREATE TABLE jxc_fabric_requisition (
    requisition_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    requisition_no VARCHAR(50) NOT NULL,
    fabric_id BIGINT NOT NULL,
    quantity DECIMAL(12,2) NOT NULL,
    purpose VARCHAR(200) COMMENT '用途',
    work_group_id BIGINT COMMENT '班组ID',
    applicant_id BIGINT COMMENT '申请人ID',
    approver_id BIGINT COMMENT '审批人ID',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态(待审批/已审批/已出库/已取消)',
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_requisition_no (requisition_no)
) COMMENT '领料申请表';
```

### 3.12 辅料管理

```sql
-- 辅料分类
CREATE TABLE jxc_accessory_category (
    category_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    code VARCHAR(30) NOT NULL COMMENT '分类编码',
    parent_id BIGINT,
    sort_order INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_code (code)
) COMMENT '辅料分类表';

-- 辅料档案
CREATE TABLE jxc_accessory (
    accessory_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    accessory_no VARCHAR(50) COMMENT '辅料编号',
    name VARCHAR(100) NOT NULL COMMENT '名称',
    category_id BIGINT COMMENT '分类ID',
    supplier_id BIGINT COMMENT '供应商ID',
    specification VARCHAR(100) COMMENT '规格',
    material VARCHAR(100) COMMENT '材质',
    color VARCHAR(50) COMMENT '颜色',
    size VARCHAR(50) COMMENT '尺寸',
    unit VARCHAR(20) COMMENT '单位',
    unit_price DECIMAL(10,4) COMMENT '单价',
    moq DECIMAL(10,2) COMMENT '起订量',
    lead_time INT COMMENT '货期(天)',
    stock DECIMAL(12,2) DEFAULT 0 COMMENT '库存',
    warning_qty DECIMAL(12,2) COMMENT '预警数量',
    image VARCHAR(500) COMMENT '图片',
    remark TEXT,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_accessory_no (accessory_no)
) COMMENT '辅料档案表';
```

### 3.13 样衣打版

```sql
-- 打样申请
CREATE TABLE jxc_sample_request (
    request_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_no VARCHAR(50) NOT NULL,
    style_id BIGINT COMMENT '款式ID',
    client_id BIGINT COMMENT '客户ID',
    request_type VARCHAR(20) COMMENT '类型(开发样/销售样/产前样/船样)',
    size_requirement VARCHAR(100) COMMENT '尺码要求',
    color_requirement VARCHAR(100) COMMENT '颜色要求',
    quantity INT COMMENT '数量',
    deadline DATE COMMENT '截止日期',
    design_sketch VARCHAR(500) COMMENT '设计稿',
    reference_sample VARCHAR(500) COMMENT '参考样衣',
    special_requirements TEXT COMMENT '特殊要求',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态(待分配/打版中/待确认/已确认/已取消)',
    create_by BIGINT,
    assigned_to BIGINT,
    completed_at DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_request_no (request_no)
) COMMENT '打样申请表';

-- 版型档案
CREATE TABLE jxc_pattern (
    pattern_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pattern_no VARCHAR(50) NOT NULL,
    style_id BIGINT NOT NULL,
    pattern_maker_id BIGINT COMMENT '打版师ID',
    pattern_type VARCHAR(20) COMMENT '类型(净版/毛版/放码版)',
    size_range VARCHAR(50) COMMENT '尺码范围(S-XXL)',
    pattern_file VARCHAR(500) COMMENT '版型文件',
    grading_rule JSON COMMENT '放码规则',
    version VARCHAR(20) DEFAULT '1.0',
    is_latest TINYINT DEFAULT 1,
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_pattern_no (pattern_no)
) COMMENT '版型档案表';

-- 样衣档案
CREATE TABLE jxc_sample (
    sample_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sample_no VARCHAR(50) NOT NULL,
    sample_request_id BIGINT,
    pattern_id BIGINT,
    style_id BIGINT,
    size VARCHAR(20),
    color VARCHAR(50),
    sample_images JSON COMMENT '样衣图片',
    measurement_data JSON COMMENT '尺寸数据',
    fitting_report TEXT COMMENT '试穿报告',
    revision_no INT DEFAULT 0 COMMENT '修改次数',
    revision_notes TEXT COMMENT '修改意见',
    status VARCHAR(20) DEFAULT 'making' COMMENT '状态(制作中/待评审/已通过/需修改/已废弃)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_sample_no (sample_no)
) COMMENT '样衣档案表';

-- 尺寸测量表
CREATE TABLE jxc_measurement_chart (
    chart_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sample_id BIGINT NOT NULL,
    size VARCHAR(20),
    measurements JSON COMMENT '测量数据[{position, standard, actual, diff}]',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '尺寸测量表';
```

### 3.14 工艺单

```sql
-- 工艺单主表
CREATE TABLE jxc_tech_pack (
    pack_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pack_no VARCHAR(50) NOT NULL,
    style_id BIGINT NOT NULL,
    order_id BIGINT COMMENT '订单ID',
    version VARCHAR(20) DEFAULT '1.0',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态(草稿/已审核/生效/作废)',
    create_by BIGINT,
    approved_by BIGINT,
    effective_date DATE,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_pack_no (pack_no)
) COMMENT '工艺单主表';

-- 工艺单章节
CREATE TABLE jxc_tech_pack_section (
    section_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pack_id BIGINT NOT NULL,
    section_type VARCHAR(30) COMMENT '章节类型(基本信息/尺寸规格/面料清单/辅料清单/工序/包装/质检)',
    content JSON COMMENT '内容',
    images JSON COMMENT '图片',
    sort_order INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '工艺单章节表';
```

### 3.15 装箱管理

```sql
-- 箱码
CREATE TABLE jxc_box (
    box_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    box_no VARCHAR(50) NOT NULL COMMENT '箱号',
    box_code VARCHAR(100) COMMENT '箱码/条码',
    order_id BIGINT COMMENT '订单ID',
    style_no VARCHAR(100) COMMENT '款号',
    color VARCHAR(50) COMMENT '颜色',
    total_quantity INT COMMENT '总件数',
    size_distribution JSON COMMENT '尺码分布[{size, quantity}]',
    gross_weight DECIMAL(10,2) COMMENT '毛重(kg)',
    net_weight DECIMAL(10,2) COMMENT '净重(kg)',
    volume DECIMAL(10,4) COMMENT '体积(m³)',
    carton_spec VARCHAR(50) COMMENT '纸箱规格',
    qr_code VARCHAR(500) COMMENT '箱码二维码',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态(待装箱/已装箱/已发货)',
    packed_by BIGINT,
    packed_at DATETIME,
    shipped_at DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_box_no (box_no)
) COMMENT '箱码管理表';

-- 装箱明细
CREATE TABLE jxc_box_item (
    item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    box_id BIGINT NOT NULL,
    sku_id BIGINT COMMENT 'SKU ID',
    size VARCHAR(20),
    color VARCHAR(50),
    quantity INT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '装箱明细表';

-- 装箱规则
CREATE TABLE jxc_packing_rule (
    rule_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category_id BIGINT COMMENT '品类ID',
    pieces_per_box INT COMMENT '每箱件数',
    mix_size TINYINT DEFAULT 1 COMMENT '是否混码',
    mix_color TINYINT DEFAULT 0 COMMENT '是否混色',
    mix_style TINYINT DEFAULT 0 COMMENT '是否混款',
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '装箱规则表';
```

### 3.16 外协加工

```sql
-- 外协加工单
CREATE TABLE jxc_outsource_order (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL,
    production_order_id BIGINT NOT NULL COMMENT '生产订单ID',
    factory_id BIGINT NOT NULL COMMENT '外协工厂ID',
    process_id BIGINT COMMENT '外协工序ID',
    quantity INT NOT NULL COMMENT '加工数量',
    completed_quantity INT DEFAULT 0 COMMENT '已完成数量',
    unit_price DECIMAL(10,4) COMMENT '单价',
    total_amount DECIMAL(12,4) COMMENT '总金额',
    paid_amount DECIMAL(12,4) DEFAULT 0 COMMENT '已付金额',
    deadline DATE COMMENT '截止日期',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态(待发料/已发料/加工中/已收货/已结算)',
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    completed_at DATETIME,
    UNIQUE KEY uk_order_no (order_no)
) COMMENT '外协加工单表';

-- 外协发料
CREATE TABLE jxc_outsource_delivery (
    delivery_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    delivery_no VARCHAR(50) NOT NULL,
    outsource_order_id BIGINT NOT NULL,
    materials JSON COMMENT '发料明细[{fabric_id, quantity}]',
    bundles JSON COMMENT '扎包明细[{bundle_id, quantity}]',
    delivery_date DATE,
    receiver VARCHAR(50) COMMENT '收货人',
    status VARCHAR(20) DEFAULT 'pending',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_delivery_no (delivery_no)
) COMMENT '外协发料表';

-- 外协收货
CREATE TABLE jxc_outsource_receive (
    receive_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    receive_no VARCHAR(50) NOT NULL,
    outsource_order_id BIGINT NOT NULL,
    received_quantity INT COMMENT '收货数量',
    qualified_quantity INT COMMENT '合格数量',
    defect_quantity INT DEFAULT 0 COMMENT '缺陷数量',
    quality_check_id BIGINT COMMENT '质检记录ID',
    receive_date DATE,
    receiver_id BIGINT COMMENT '收货人ID',
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_receive_no (receive_no)
) COMMENT '外协收货表';
```

### 3.17 成本核算

```sql
-- 成本中心
CREATE TABLE jxc_cost_center (
    center_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(30) NOT NULL,
    factory_id BIGINT COMMENT '工厂ID',
    center_type VARCHAR(20) COMMENT '类型(车间/班组/生产线)',
    is_active TINYINT DEFAULT 1,
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_code (code)
) COMMENT '成本中心表';

-- 成本项目
CREATE TABLE jxc_cost_item (
    item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(20) COMMENT '分类(材料/人工/制造费用)',
    calculation_method VARCHAR(20) COMMENT '计算方法(实际/标准/分摊)',
    accounting_code VARCHAR(50) COMMENT '财务科目',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '成本项目表';

-- 生产成本单
CREATE TABLE jxc_production_cost (
    cost_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    production_order_id BIGINT NOT NULL,
    product_id BIGINT,
    quantity INT,
    material_cost DECIMAL(12,4) DEFAULT 0 COMMENT '材料成本',
    labor_cost DECIMAL(12,4) DEFAULT 0 COMMENT '人工成本',
    overhead_cost DECIMAL(12,4) DEFAULT 0 COMMENT '制造费用',
    total_cost DECIMAL(12,4) DEFAULT 0 COMMENT '总成本',
    unit_cost DECIMAL(12,4) DEFAULT 0 COMMENT '单位成本',
    cost_date DATE,
    status VARCHAR(20) DEFAULT 'calculating' COMMENT '状态(计算中/已确认)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '生产成本单表';

-- 材料成本明细
CREATE TABLE jxc_cost_material_detail (
    detail_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cost_id BIGINT NOT NULL,
    material_id BIGINT,
    material_name VARCHAR(100),
    quantity DECIMAL(10,2),
    unit_price DECIMAL(10,4),
    amount DECIMAL(12,4),
    wastage_amount DECIMAL(12,4) COMMENT '损耗金额',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '材料成本明细表';

-- 人工成本明细
CREATE TABLE jxc_cost_labor_detail (
    detail_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cost_id BIGINT NOT NULL,
    process_id BIGINT,
    process_name VARCHAR(100),
    worker_count INT COMMENT '工人数',
    total_hours DECIMAL(10,2) COMMENT '总工时',
    hourly_rate DECIMAL(10,2) COMMENT '时薪',
    piece_work_amount DECIMAL(12,4) COMMENT '计件金额',
    time_work_amount DECIMAL(12,4) COMMENT '计时金额',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '人工成本明细表';

-- 制造费用明细
CREATE TABLE jxc_cost_overhead_detail (
    detail_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cost_id BIGINT NOT NULL,
    cost_center_id BIGINT,
    overhead_item VARCHAR(50) COMMENT '费用项目(折旧/水电/租金/其他)',
    allocation_base VARCHAR(20) COMMENT '分摊基数(工时/产量)',
    allocation_rate DECIMAL(10,4) COMMENT '分摊率',
    allocated_amount DECIMAL(12,4) COMMENT '分摊金额',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '制造费用明细表';
```

### 3.18 组织架构增强

```sql
-- 工厂
CREATE TABLE jxc_factory (
    factory_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '工厂名称',
    code VARCHAR(30) NOT NULL COMMENT '工厂编码',
    factory_type VARCHAR(20) COMMENT '类型(自有/外协)',
    address VARCHAR(300) COMMENT '地址',
    contact VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '电话',
    is_active TINYINT DEFAULT 1,
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_code (code)
) COMMENT '工厂表';

-- 车间
CREATE TABLE jxc_workshop (
    workshop_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    factory_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(30) NOT NULL,
    workshop_type VARCHAR(30) COMMENT '类型(裁床车间/缝制车间/整烫车间/包装车间)',
    manager_id BIGINT COMMENT '负责人ID',
    is_active TINYINT DEFAULT 1,
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_code (code)
) COMMENT '车间表';

-- 班组
CREATE TABLE jxc_work_group (
    group_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(30) NOT NULL,
    factory_id BIGINT COMMENT '工厂ID',
    workshop_id BIGINT COMMENT '车间ID',
    leader_id BIGINT COMMENT '班组长ID',
    group_type VARCHAR(30) COMMENT '类型(裁床组/缝制组/质检组/包装组)',
    is_active TINYINT DEFAULT 1,
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_code (code)
) COMMENT '班组表';

-- 工序产能配置
CREATE TABLE jxc_process_capacity_config (
    config_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    process_id BIGINT NOT NULL,
    work_group_id BIGINT NOT NULL,
    daily_capacity INT DEFAULT 200 COMMENT '日产能',
    is_active TINYINT DEFAULT 1,
    remark VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_process_group (process_id, work_group_id)
) COMMENT '工序产能配置表';

-- 员工档案扩展 (计件相关)
CREATE TABLE jxc_worker_profile (
    profile_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    employee_no VARCHAR(50) COMMENT '工号',
    skill_level VARCHAR(20) DEFAULT 'trainee' COMMENT '技能等级(trainee/standard/expert)',
    default_price_level VARCHAR(10) DEFAULT 'price1' COMMENT '默认价格等级',
    work_group_id BIGINT COMMENT '所属班组ID',
    hire_date DATE COMMENT '入职日期',
    base_salary DECIMAL(12,2) DEFAULT 0 COMMENT '基本工资',
    is_piece_work TINYINT DEFAULT 1 COMMENT '是否计件制',
    bank_account VARCHAR(50) COMMENT '银行账号',
    is_active TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user (user_id),
    UNIQUE KEY uk_employee_no (employee_no)
) COMMENT '员工档案扩展表';

-- 员工技能认证
CREATE TABLE jxc_worker_skill (
    skill_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    worker_id BIGINT NOT NULL COMMENT '员工ID',
    process_id BIGINT NOT NULL COMMENT '工序ID',
    skill_level VARCHAR(20) COMMENT '技能等级',
    certified_date DATE COMMENT '认证日期',
    certified_by BIGINT COMMENT '认证人',
    expire_date DATE COMMENT '有效期',
    is_active TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_worker_process (worker_id, process_id)
) COMMENT '员工技能认证表';
```

### 3.19 工作流相关表

```sql
-- 工序-工作流节点映射
CREATE TABLE jxc_process_wf_mapping (
    mapping_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    process_id BIGINT NOT NULL COMMENT '工序ID',
    wf_node_key VARCHAR(100) NOT NULL COMMENT '工作流节点Key',
    wf_node_name VARCHAR(100) COMMENT '节点名称',
    is_gateway TINYINT DEFAULT 0 COMMENT '是否网关(门禁)',
    block_on_condition VARCHAR(500) COMMENT '阻断条件',
    block_reason VARCHAR(200) COMMENT '阻断原因',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_process_node (process_id, wf_node_key)
) COMMENT '工序-工作流节点映射表';

-- 工作流门禁记录
CREATE TABLE jxc_wf_gate_record (
    record_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bundle_id BIGINT NOT NULL COMMENT '扎包ID',
    gate_type VARCHAR(50) NOT NULL COMMENT '门禁类型(first_article/quality_check)',
    gate_node_key VARCHAR(100) COMMENT '门禁节点Key',
    check_result VARCHAR(20) COMMENT '检查结果',
    block_reason VARCHAR(200) COMMENT '阻断原因',
    passed_at DATETIME COMMENT '通过时间',
    passed_by BIGINT COMMENT '通过人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '工作流门禁记录表';
```

### 3.20 灵活流程管理

#### 3.20.1 设计思路

支持不同工厂、不同品类、不同季节使用不同的生产流程：

```
┌─────────────────────────────────────────────────────────────────┐
│                     灵活流程设计                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  层级1: 工序库 (全局基础)                                       │
│  ├── 裁剪、缝制、整烫、质检、包装...                            │
│  └── 所有工厂/款式共享的基础工序定义                            │
│                                                                  │
│  层级2: 流程模板 (可复用)                                       │
│  ├── T恤生产流程模板                                            │
│  ├── 羽绒服生产流程模板                                         │
│  ├── 牛仔裤生产流程模板                                         │
│  └── 每个模板定义工序顺序、门禁规则                             │
│                                                                  │
│  层级3: 工厂/款式配置 (差异化)                                  │
│  ├── 工厂A: T恤使用模板1，羽绒服使用模板2                       │
│  ├── 工厂B: T恤使用模板1（可自定义修改）                        │
│  └── 款式STYLE-001: 默认使用T恤模板，可覆盖调整                 │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

#### 3.20.2 流程选择优先级

```
创建生产订单/扎包时，按以下顺序确定使用哪个流程:

优先级1: 款式专属流程
  if (jxc_style_flow_config 存在该款式的自定义配置)
      → 使用款式自定义流程

优先级2: 工厂+品类流程
  if (jxc_factory_flow_config 存在该工厂+品类的配置)
      → 使用工厂品类流程

优先级3: 工厂默认流程
  if (jxc_factory_flow_config 存在该工厂的默认配置)
      → 使用工厂默认流程

优先级4: 系统默认模板
  → 使用流程模板中 is_default=1 的模板
```

#### 3.20.3 数据表设计

```sql
-- ============================================
-- 流程模板表
-- ============================================

-- 流程模板主表
CREATE TABLE jxc_flow_template (
    template_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_no VARCHAR(50) NOT NULL COMMENT '模板编号',
    template_name VARCHAR(100) NOT NULL COMMENT '模板名称',
    category VARCHAR(50) COMMENT '适用品类(T恤/羽绒服/牛仔裤/连衣裙...)',
    season VARCHAR(20) COMMENT '适用季节(春夏/秋冬/四季)',
    description VARCHAR(500) COMMENT '描述',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认模板',
    is_active TINYINT DEFAULT 1,
    create_by BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_template_no (template_no)
) COMMENT '流程模板表';

-- 流程模板工序明细
CREATE TABLE jxc_flow_template_process (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_id BIGINT NOT NULL COMMENT '模板ID',
    process_id BIGINT NOT NULL COMMENT '工序ID',
    sequence INT NOT NULL COMMENT '工序顺序',
    is_required TINYINT DEFAULT 1 COMMENT '是否必须工序',
    is_gateway TINYINT DEFAULT 0 COMMENT '是否门禁节点',
    gateway_type VARCHAR(30) COMMENT '门禁类型(first_article/quality_check)',
    gate_condition VARCHAR(500) COMMENT '门禁条件(JSON)',
    estimated_time INT COMMENT '预计耗时(分钟)',
    remark VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_template_process (template_id, process_id),
    INDEX idx_template_sequence (template_id, sequence)
) COMMENT '流程模板工序明细表';

-- 流程模板工序依赖
CREATE TABLE jxc_flow_template_dependency (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_id BIGINT NOT NULL,
    process_id BIGINT NOT NULL COMMENT '工序ID',
    depends_on_process_id BIGINT NOT NULL COMMENT '依赖工序ID',
    dependency_type VARCHAR(20) DEFAULT 'required' COMMENT '依赖类型(required/optional)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_template_dep (template_id, process_id, depends_on_process_id)
) COMMENT '流程模板工序依赖表';

-- ============================================
-- 工厂/款式流程配置
-- ============================================

-- 工厂流程配置
CREATE TABLE jxc_factory_flow_config (
    config_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    factory_id BIGINT NOT NULL COMMENT '工厂ID',
    category VARCHAR(50) COMMENT '品类(为空表示默认)',
    template_id BIGINT COMMENT '使用的模板ID',
    is_customized TINYINT DEFAULT 0 COMMENT '是否自定义(复制后修改)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_factory_category (factory_id, category)
) COMMENT '工厂流程配置表';

-- 款式流程配置 (优先级最高)
CREATE TABLE jxc_style_flow_config (
    config_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    style_id BIGINT NOT NULL COMMENT '款式ID',
    template_id BIGINT COMMENT '使用的模板ID',
    custom_processes JSON COMMENT '自定义工序配置(覆盖模板)',
    is_customized TINYINT DEFAULT 0 COMMENT '是否自定义',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_style (style_id)
) COMMENT '款式流程配置表';

-- 扎包实际执行流程
CREATE TABLE jxc_bundle_flow_instance (
    instance_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bundle_id BIGINT NOT NULL COMMENT '扎包ID',
    template_id BIGINT COMMENT '来源模板ID',
    flow_data JSON NOT NULL COMMENT '流程数据(工序列表+状态)',
    current_process_id BIGINT COMMENT '当前工序ID',
    current_sequence INT COMMENT '当前工序顺序',
    status VARCHAR(20) DEFAULT 'in_progress' COMMENT '状态(in_progress/completed/suspended)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_bundle (bundle_id)
) COMMENT '扎包流程实例表';
```

#### 3.20.4 应用场景示例

```
场景1: 不同工厂不同流程
─────────────────────────
工厂A (自有工厂): 
  - 工序齐全: 裁剪→缝制→整烫→质检→包装
  - 有首件确认门禁

工厂B (外协工厂):
  - 简化流程: 裁剪→缝制→质检→包装
  - 跳过整烫工序
  - 无首件确认要求

配置方式:
INSERT INTO jxc_factory_flow_config (factory_id, category, template_id)
VALUES 
  (1, NULL, 1),  -- 工厂A默认使用模板1
  (2, NULL, 2);  -- 工厂B默认使用模板2


场景2: 不同季节不同流程
─────────────────────────
T恤(夏季):
  裁剪→缝制→锁钉→整烫→质检→包装

羽绒服(冬季):
  裁剪→缝制→充绒→绗缝→锁钉→整烫→质检→包装
  (多了充绒、绗缝工序)

配置方式:
INSERT INTO jxc_flow_template (template_no, template_name, category, season)
VALUES 
  ('TPL-TSHIRT', 'T恤生产流程', 'T恤', '春夏'),
  ('TPL-DOWN', '羽绒服生产流程', '羽绒服', '秋冬');


场景3: 特殊款式自定义流程
─────────────────────────
款式STYLE-001 (特殊定制款):
  需要在缝制后增加一道"刺绣"工序
  需要在包装前增加"客户验货"门禁

配置方式:
INSERT INTO jxc_style_flow_config (style_id, template_id, is_customized, custom_processes)
VALUES (
  1, 
  1,  -- 基于T恤模板
  1,  -- 自定义
  '{
    "add_processes": [
      {"process_id": 100, "name": "刺绣", "after_process_id": 5, "sequence": 6}
    ],
    "add_gateways": [
      {"process_id": 99, "name": "客户验货", "type": "customer_check", "before_process_id": 20}
    ]
  }'
);
```

#### 3.20.5 与现有系统的兼容

```
1. 保留现有工序库 (jxc_process)
   - 继续作为全局基础工序定义

2. 迁移现有工序依赖
   jxc_process_dependency → jxc_flow_template_dependency
   创建一个默认流程模板包含现有依赖关系

3. 废弃 jxc_process_wf_mapping
   - 改用 jxc_flow_template_process 统一管理

4. 扎包创建时生成流程实例
   - 从模板复制流程数据到 jxc_bundle_flow_instance
   - 支持运行时动态调整（不影响其他扎包）
```

---

## 四、API接口设计

### 4.1 新增API模块

```
/api/production/          # 生产管理
├── /orders               # 生产订单 CRUD
├── /plans                # 生产计划 CRUD
├── /schedules            # 生产排程 CRUD
├── /reworks              # 返工单 CRUD
└── /capacity-configs     # 产能配置

/api/processes/           # 工序管理
├── /list                 # 工序库 CRUD
├── /dependencies         # 工序依赖 CRUD
├── /product-configs      # 货品工序配置 CRUD
├── /tasks                # 工序任务 CRUD
│   ├── /mine             # 我的任务
│   ├── /accept/{id}      # 接单
│   └── /complete/{id}    # 完成
├── /records              # 计件记录 CRUD
│   ├── /scan             # 扫码计件
│   ├── /confirm/{id}     # 确认
│   └── /reject/{id}      # 驳回
└── /exceptions           # 异常上报 CRUD

/api/bundles/             # 扎包管理
├── /list                 # 扎包 CRUD
├── /by-no                # 按扎包号查询
├── /{id}/flow            # 扎包流转信息
├── /{id}/generate-qr     # 生成二维码
├── /{id}/print-qr        # 打印二维码
└── /handover             # 扎包交接

/api/cutting/             # 裁床管理
├── /plans                # 裁床计划 CRUD
├── /bundles              # 裁床扎包 CRUD
└── /complete/{id}        # 完成裁床

/api/quality/             # 质量管理
├── /first-articles       # 首件确认 CRUD
│   └── /confirm/{id}     # 确认首件
├── /standards            # 质检标准 CRUD
├── /checks               # 质检记录 CRUD
├── /defects              # 缺陷记录 CRUD
├── /patrols              # 巡检记录 CRUD
└── /stats                # 质量统计

/api/fabrics/             # 面料管理
├── /list                 # 面料档案 CRUD
├── /inbounds             # 面料入库 CRUD
├── /requisitions         # 领料申请 CRUD
│   └── /approve/{id}     # 审批
├── /stock                # 面料库存查询
└── /warning              # 面料预警

/api/styles/              # 款式管理
├── /list                 # 款式档案 CRUD
└── /colorways            # 款式配色 CRUD

/api/accessories/         # 辅料管理
├── /categories           # 辅料分类 CRUD
└── /list                 # 辅料档案 CRUD

/api/samples/             # 样衣管理
├── /requests             # 打样申请 CRUD
├── /patterns             # 版型档案 CRUD
└── /list                 # 样衣档案 CRUD

/api/bom/                 # BOM管理
├── /list                 # BOM主表 CRUD
├── /{id}/items           # BOM明细
└── /{id}/processes       # BOM工序

/api/outsource/           # 外协管理
├── /orders               # 外协加工单 CRUD
├── /deliveries           # 外协发料 CRUD
└── /receives             # 外协收货 CRUD

/api/cost/                # 成本核算
├── /centers              # 成本中心 CRUD
├── /items                # 成本项目 CRUD
├── /calculate/{orderId}  # 计算成本
└── /reports              # 成本报表

/api/box/                 # 装箱管理
├── /list                 # 箱码 CRUD
├── /{id}/items           # 装箱明细
└── /rules                # 装箱规则

/api/print/               # 打印服务
├── /templates            # 打印模板 CRUD (已有)
├── /preview/{bizType}/{id}  # 预览打印
├── /execute              # 执行打印
├── /qrcode/bundle/{id}   # 扎包二维码
├── /qrcode/product/{id}  # 商品条码
└── /label                # 标签打印
    ├── /product          # 商品标签
    └── /bundle           # 扎包标签

/api/reports/             # 报表服务 (增强)
├── /sales/daily          # 销售日报 (已有)
├── /production/progress  # 生产进度报表
├── /production/efficiency # 生产效率分析
├── /piece-work/wage      # 计件工资报表
├── /quality/analysis     # 质量分析报表
├── /inventory/turnover   # 库存周转分析
└── /cost/production      # 生产成本分析

/api/flow-templates/      # 流程模板管理 (新增)
├── /list                 # 模板列表
├── /{id}                 # 模板详情
├── /create               # 创建模板
├── /{id}/update          # 更新模板
├── /{id}/processes       # 模板工序配置
├── /{id}/preview         # 预览流程图
└── /{id}/copy            # 复制模板

/api/flow-configs/        # 流程配置 (新增)
├── /factory              # 工厂流程配置 CRUD
├── /style                # 款式流程配置 CRUD
└── /resolve              # 解析最终流程(给定款式+工厂)
```

### 4.2 关键接口设计

#### 扫码计件接口

```
POST /api/processes/records/scan

Request:
{
  "bundle_no": "ZB20240101001",
  "process_id": 5,
  "quantity": 10,
  "device_id": "PDA-001",
  "location": "缝制车间-A线"
}

Response (成功):
{
  "code": 200,
  "message": "计件成功",
  "data": {
    "record_id": 1001,
    "record_no": "JJ20240101001",
    "bundle_no": "ZB20240101001",
    "process_name": "缝制",
    "quantity": 10,
    "status": "pending",
    "workflow_progressed": true,
    "next_process": "锁钉"
  }
}

Response (失败-工序顺序错误):
{
  "code": 400,
  "message": "工序顺序错误",
  "error_code": "PROCESS_SEQUENCE_INVALID",
  "data": {
    "current_process": "缝制",
    "expected_process": "裁剪"
  }
}
```

#### 扎包二维码生成接口

```
GET /api/bundles/{id}/generate-qr

Response:
{
  "code": 200,
  "data": {
    "bundle_no": "ZB20240101001",
    "qr_code_url": "/media/qr/bundles/ZB20240101001.png",
    "qr_data": {
      "type": "bundle",
      "bundle_no": "ZB20240101001",
      "order_no": "DD20240101001",
      "size": "L",
      "color": "黑色",
      "quantity": 20
    }
  }
}
```

---

## 五、业务流程设计

### 5.1 生产主流程

```
销售订单 → 生产订单 → 生产计划 → 生产排程
    ↓
裁床计划 → 首件确认 → 扎包生成 → 任务派发
    ↓
工序执行(扫码计件) → 质检 → 返工(如有) → 成品入库
    ↓
成品发货
```

### 5.2 销售→生产数据打通

```
┌───────────────────────────────────────────────────────────────┐
│ 方式一: 手动创建                                              │
│   销售订单(批发单) → 选择 → 创建生产订单                       │
├───────────────────────────────────────────────────────────────┤
│ 方式二: 自动生成                                              │
│   销售订单审核通过 → 自动生成生产订单(需配置)                   │
├───────────────────────────────────────────────────────────────┤
│ 数据映射:                                                     │
│   销售订单.order_id → 生产订单.source_order_id               │
│   销售订单.customer_id → 生产订单.customer_id                 │
│   销售订单商品明细 → 生产订单.style_no/style_name             │
│   销售订单尺码颜色 → 生产订单.order_size_ratio                │
│   销售订单.delivery_date → 生产订单.deadline                  │
└───────────────────────────────────────────────────────────────┘
```

---

## 六、工作流适配设计

### 6.1 工作流架构调整

> **重要设计变更**：根据性能评估，扎包级工序流转不适合使用Flowable引擎。

#### 6.1.1 职责划分

| 工作流引擎 | 适用场景 | 原因 |
|-----------|---------|------|
| **Flowable BPMN** | 业务审批流程 | 订单审批、首件确认、领料审批、外协审批等低频审批场景，适合复杂审批链 |
| **轻量级状态机** | 扎包工序流转 | 车间高频扫码计件场景（日均数千次），避免Flowable的性能瓶颈 |

#### 6.1.2 Flowable适用场景

| 业务类型 | 流程定义 | 说明 |
|---------|---------|------|
| production_order | production_order_approval | 生产订单审批 |
| cutting_plan | cutting_plan_approval | 裁床计划审批 |
| first_article | first_article_confirmation | 首件确认流程 |
| fabric_requisition | fabric_requisition_approval | 领料审批 |
| outsource_order | outsource_order_approval | 外协审批 |

#### 6.1.3 扎包工序流转（状态机方案）

**不使用Flowable，改用轻量级状态机控制**

**原因**：
- 中型服装厂日均产生几千到上万个扎包
- Flowable底层涉及数十张`ACT_RU_*`运行时表和历史表
- 高并发扫码场景会导致严重的数据库锁竞争和性能崩溃

### 6.2 扎包工序状态机设计

#### 6.2.1 状态定义

```java
public enum BundleProcessStatus {
    PENDING,        // 待开始
    IN_PROGRESS,    // 进行中
    COMPLETED,      // 已完成
    SUSPENDED,      // 暂停（异常）
    SKIPPED         // 跳过（该扎包无需此工序）
}
```

#### 6.2.2 状态转移规则

| 当前状态 | 可转移状态 | 触发条件 |
|---------|-----------|---------|
| PENDING | IN_PROGRESS | 首件确认通过 + 前置工序完成 |
| IN_PROGRESS | COMPLETED | 计件数量达标 |
| IN_PROGRESS | SUSPENDED | 异常上报 |
| SUSPENDED | IN_PROGRESS | 异常解决 |

#### 6.2.3 流程模板驱动

```
┌─────────────────────────────────────────────────────────────────┐
│                    扎包流程启动流程                              │
├─────────────────────────────────────────────────────────────────┤
│  创建扎包                                                        │
│      │                                                           │
│      ▼                                                           │
│  解析流程模板                                                     │
│  1. 查询款式流程配置 (jxc_style_flow_config)                     │
│  2. 查询工厂+品类流程配置 (jxc_factory_flow_config)               │
│  3. 使用系统默认模板                                              │
│      │                                                           │
│      ▼                                                           │
│  生成流程实例                                                     │
│  - 从模板复制工序列表到 jxc_bundle_flow_instance                  │
│  - 初始化所有工序状态为 pending                                   │
│      │                                                           │
│      ▼                                                           │
│  扎包开始流转                                                     │
└─────────────────────────────────────────────────────────────────┘
```

#### 6.2.4 示例流程图

**T恤生产流程模板：**
```
开始 → [首件确认门禁] → 裁剪 → 缝制 → 锁钉 → 整烫 → 质检 → 包装 → 入库 → 结束
                                  │                                      │
                                  └──────── 返工流程 ←──────────────────┘
```

**羽绒服生产流程模板：**
```
开始 → [首件确认门禁] → 裁剪 → 缝制 → 充绒 → 绗缝 → 锁钉 → 整烫 → 质检 → 包装 → 入库 → 结束
                                  │                                                    │
                                  └───────────── 返工流程 ←───────────────────────────┘
```

### 6.3 门禁控制逻辑（状态机实现）

```java
public class ProcessGateService {
    
    // 首件门禁检查
    public boolean checkFirstArticleGate(Long cuttingPlanId) {
        FirstArticleConfirmation fac = facMapper.selectByCuttingPlanId(cuttingPlanId);
        return fac != null && "passed".equals(fac.getResult());
    }
    
    // 工序顺序门禁检查
    public boolean checkProcessSequenceGate(Long bundleId, Long processId) {
        BundleProcess current = bpMapper.selectByBundleAndProcess(bundleId, processId);
        if (current.getSequence() == 1) return true; // 第一道工序
        
        BundleProcess prev = bpMapper.selectBySequence(bundleId, current.getSequence() - 1);
        return prev != null && "completed".equals(prev.getStatus());
    }
    
    // 数量溢出门禁检查
    public boolean checkQuantityGate(Long bundleId, Integer quantity) {
        Bundle bundle = bundleMapper.selectById(bundleId);
        int completed = recordMapper.sumQuantity(bundleId);
        return completed + quantity <= bundle.getQuantity();
    }
}
```

| 门禁类型 | 错误码 | 触发条件 | 处理方式 |
|---------|--------|---------|---------|
| 首件门禁 | FIRST_ARTICLE_NOT_PASSED | 首件未通过 | 禁止生成扎包和计件 |
| 工序顺序门禁 | PROCESS_SEQUENCE_INVALID | 前置工序未完成 | 禁止当前工序计件 |
| 数量溢出门禁 | QUANTITY_OVERFLOW | 计件数超过扎包总数 | 禁止超额计件 |

### 6.4 流程实例状态管理

`jxc_bundle_flow_instance.flow_data` 结构：

```json
{
  "template_id": 1,
  "template_name": "T恤生产流程",
  "processes": [
    {
      "process_id": 1,
      "process_name": "裁剪",
      "sequence": 1,
      "status": "completed",
      "completed_at": "2024-01-01 10:00:00",
      "completed_by": 100
    },
    {
      "process_id": 2,
      "process_name": "缝制",
      "sequence": 2,
      "status": "in_progress",
      "started_at": "2024-01-01 11:00:00"
    },
    {
      "process_id": 3,
      "process_name": "整烫",
      "sequence": 3,
      "status": "pending"
    }
  ],
  "gateways": [
    {
      "gateway_type": "first_article",
      "passed": true,
      "passed_at": "2024-01-01 09:00:00"
    }
  ]
}
```

### 6.5 扎包流转服务接口

```java
public interface BundleFlowService {
    // 初始化扎包流程实例（从模板解析）
    void initBundleFlowInstance(Bundle bundle);
    
    // 获取当前可执行的工序
    List<Process> getExecutableProcesses(Long bundleId);
    
    // 获取扎包流程进度
    BundleFlowProgress getBundleFlowProgress(Long bundleId);
    
    // 推进工序状态
    boolean advanceProcess(Long bundleId, Long processId, String status);
    
    // 暂停/恢复流程
    boolean suspendBundle(Long bundleId, String reason);
    boolean resumeBundle(Long bundleId);
    
    // 完成扎包流程
    boolean completeBundle(Long bundleId);
}
```

---

## 七、裁床后扫码计件流程

### 7.1 流程概览

```
裁床计划 → 首件确认 → 裁床执行 → 生成扎包 → 打印二维码 → 扫码计件
```

### 7.2 详细流程步骤

#### 步骤一：创建裁床计划
- 选择订单、面料
- 设置裁剪数量、尺码配比
- 分配裁床组
- 状态: 待执行

#### 步骤二：首件确认（门禁）
- 裁床裁出第一件样衣
- 质检员检验并填写检验项目
- 拍照记录
- 结果: 合格/不合格
- ⚠️ 首件未通过 → 禁止生成扎包，禁止后续计件

#### 步骤三：裁床执行
- 裁床工人按计划裁剪
- 铺布、排料、裁剪
- 分扎（每扎N件）
- 登记每扎的尺码、颜色、数量

#### 步骤四：生成扎包 + 打印二维码
- 系统自动/手动生成生产扎包
- 生成扎包号: ZB20240101001
- 关联订单、裁床计划
- 生成二维码图片
- 批量打印扎包标签

#### 步骤五：扎包绑定到实物
- 裁床工人将打印好的标签挂/贴到对应的实物扎包上
- 扎包随工序流转

#### 步骤六：工人扫码计件
1. 打开小程序 → 扫码计件页面
2. 扫描扎包上的二维码
3. 系统显示扎包信息
4. 选择当前工序（如: 缝制-前幅）
5. 输入完成数量
6. 提交计件
7. 系统记录 + 推进流程

### 7.3 扎包二维码设计

**二维码内容：**
```json
{
  "type": "bundle",
  "bundle_no": "ZB20240101001",
  "order_no": "DD20240101001",
  "style_no": "STYLE-001",
  "style_name": "春季休闲裤",
  "size": "L",
  "color": "黑色",
  "quantity": 20,
  "current_process": "缝制-前幅",
  "status": "in_production"
}
```

**扎包标签样式：**
```
┌─────────────────────┐
│    [二维码图片]      │
│      衣多多          │
├─────────────────────┤
│ 扎包号: ZB20240101  │
│ 款  号: STYLE-001   │
│ 尺  码: L           │
│ 颜  色: 黑色        │
│ 数  量: 20件        │
└─────────────────────┘
```

### 7.4 扎包实物流转

```
裁床车间          缝制车间          整烫车间          质检车间
┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐
│ 裁床扎包 │────►│ 缝制扎包 │────►│ 整烫扎包 │────►│ 质检扎包 │
│ ZB001   │     │ ZB001   │     │ ZB001   │     │ ZB001   │
│ L/黑色  │     │ L/黑色  │     │ L/黑色  │     │ L/黑色  │
│ 20件   │     │ 20件   │     │ 20件   │     │ 20件   │
└─────────┘     └─────────┘     └─────────┘     └─────────┘
     │               │               │               │
     ▼               ▼               ▼               ▼
 扫码计件         扫码计件         扫码计件         扫码计件
 (裁剪工序)       (缝制工序)       (整烫工序)       (质检工序)
```

### 7.5 扫码计件校验流程

```
扫描扎包二维码
       │
       ▼
┌──────────────────┐     不存在
│ 扎包是否存在?    │─────────► 提示: 扎包不存在
└────────┬─────────┘
         │ 存在
         ▼
┌──────────────────┐     未通过
│ 首件确认通过?    │─────────► 提示: 首件未确认
│ (门禁检查)       │           错误码: FIRST_ARTICLE_NOT_PASSED
└────────┬─────────┘
         │ 通过
         ▼
┌──────────────────┐     是
│ 扎包状态异常?    │─────────► 提示: 扎包状态异常
└────────┬─────────┘
         │ 否
         ▼
┌──────────────────┐
│ 显示扎包信息     │
│ 显示可执行工序   │
└────────┬─────────┘
         │
         ▼
    选择工序
         │
         ▼
┌──────────────────┐     否
│ 工序是否可执行?  │─────────► 提示: 前置工序未完成
│ (工序依赖检查)   │           错误码: PROCESS_SEQUENCE_INVALID
└────────┬─────────┘
         │ 是
         ▼
┌──────────────────┐     否
│ 工作流节点就绪?  │─────────► 提示: 工作流节点未就绪
└────────┬─────────┘           错误码: WORKFLOW_NODE_NOT_READY
         │ 是
         ▼
    输入数量
         │
         ▼
┌──────────────────┐     是
│ 数量是否溢出?    │─────────► 提示: 数量超出扎包总数
└────────┬─────────┘           错误码: QUANTITY_OVERFLOW
         │ 否
         ▼
┌──────────────────┐
│ 提交计件记录     │
│ 更新工序任务状态 │
│ 推进工作流节点   │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ 计件成功         │
└──────────────────┘
```

### 7.6 打印时机选择

**推荐方案：裁床完成后批量打印**

| 时机 | 操作 | 优点 |
|------|------|------|
| 裁床完成确认后 | 系统自动生成所有扎包 | 效率高 |
| 首件确认通过后 | 批量打印所有扎包标签 | 不易遗漏 |
| 裁床工人操作 | 将标签挂到实物扎包 | 流程清晰 |

**支持功能：**
- 单张打印
- 批量打印
- 补打（标签丢失时）

### 7.7 异常处理

| 场景 | 处理方式 |
|------|---------|
| 二维码损坏无法扫描 | 小程序支持手动输入扎包号 |
| 首件未通过 | 禁止计件，提示等待首件确认 |
| 工序顺序错误 | 提示"请先完成XX工序" |
| 数量超出 | 提示最大可计件数量 |
| 扎包状态异常 | 显示异常原因，引导处理 |
| 网络断开 | 本地缓存，网络恢复后同步 |

---

## 八、打印功能设计

### 8.1 打印模板类型

| 模板类型 | 业务场景 | 关键字段 |
|---------|---------|---------|
| 销售单据 | 销售单、退货单 | 单号、日期、客户、商品明细、金额 |
| 采购单据 | 进货单、采购退货 | 单号、日期、供应商、商品明细 |
| 库存单据 | 入库单、出库单、调拨单 | 单号、日期、仓库、商品明细 |
| 条码标签 | 商品条码标签 | 条码、商品名称、规格、价格 |
| 扎包二维码 | 扎包流转卡 | 扎包号、订单、尺码、颜色、数量、二维码 |
| 装箱单 | 装箱清单 | 箱号、商品明细、件数 |
| 生产流转卡 | 工序流转 | 扎包号、当前工序、流转记录 |
| 质检报告 | 质检记录 | 单号、检验结果、缺陷明细 |

### 8.2 二维码内容设计

```json
// 扎包二维码
{
  "type": "bundle",
  "bundle_no": "ZB20240101001",
  "order_no": "DD20240101001",
  "style_no": "STYLE-001",
  "size": "L",
  "color": "黑色",
  "quantity": 20,
  "status": "in_production"
}

// 箱码二维码
{
  "type": "box",
  "box_no": "BOX20240101001",
  "order_no": "DD20240101001",
  "style_no": "STYLE-001",
  "color": "黑色",
  "total_quantity": 45,
  "size_distribution": {"S":5, "M":10, "L":15, "XL":10, "XXL":5}
}
```

---

## 九、小程序功能设计

### 9.1 功能模块

| 模块 | 功能 | 离线支持 |
|------|------|---------|
| 扫码计件 | 扫描扎包二维码、选择工序、提交计件 | ✅ |
| 任务列表 | 查看待接单/进行中/已完成任务 | ✅ 缓存 |
| 生产进度 | 查看订单进度、扎包状态 | ❌ |
| 异常上报 | 拍照上传、填写描述、提交异常 | ✅ |
| 质检录入 | 扫扎包、录入质检结果 | ✅ |
| 个人中心 | 个人计件统计、工资查询 | ❌ |

### 9.2 离线处理机制

- 网络断开时自动存入本地Storage
- 网络恢复时自动同步到服务器
- 同步失败保留记录下次重试
- TabBar显示离线记录数量徽标

### 9.3 小程序界面设计

```
┌─────────────────────────────────────────┐
│            扫码计件                      │
├─────────────────────────────────────────┤
│  ┌─────────────────────────────────┐    │
│  │          [扫码框]                │    │
│  │       点击扫描扎包二维码          │    │
│  └─────────────────────────────────┘    │
│                                          │
│  ──────── 或手动输入 ────────           │
│  扎包号: [ZB20240101001    ]            │
│                                          │
├─────────────────────────────────────────┤
│  扎包信息:                               │
│  ┌─────────────────────────────────┐    │
│  │ 扎包号: ZB20240101001           │    │
│  │ 款  号: STYLE-001 春季休闲裤     │    │
│  │ 尺  码: L                       │    │
│  │ 颜  色: 黑色                    │    │
│  │ 数  量: 20件                    │    │
│  │ 当前进度: 缝制中 (3/8工序)       │    │
│  │ ⚠️ 流程状态: 正常               │    │
│  └─────────────────────────────────┘    │
│                                          │
├─────────────────────────────────────────┤
│  选择工序:                               │
│  ┌─────────────────────────────────┐    │
│  │ ○ 缝制-前幅 (当前工序) ✓        │    │
│  │ ○ 缝制-后幅 (待前工序完成)       │    │
│  │ ○ 锁钉 (待前工序完成)           │    │
│  └─────────────────────────────────┘    │
│                                          │
│  完成数量:  [ - ] 20 [ + ]              │
│                                          │
│  ┌─────────────────────────────────┐    │
│  │         提交计件                 │    │
│  └─────────────────────────────────┘    │
└─────────────────────────────────────────┘
```

---

## 十、前端页面设计

### 10.1 新增页面清单

```
frontend/src/views/
├── style/                        # 款式管理
│   ├── index.vue                 # 款式列表
│   ├── form.vue                  # 款式表单
│   └── colorway.vue              # 配色管理
├── production/                   # 生产管理
│   ├── order/                    # 生产订单
│   ├── plan/                     # 生产计划
│   ├── schedule/                 # 生产排程
│   └── rework/                   # 返工管理
├── process/                      # 工序管理
│   ├── process/                  # 工序库
│   ├── dependency/               # 工序依赖
│   ├── product-config/           # 货品工序配置
│   ├── task/                     # 任务池
│   │   ├── index.vue             # 任务列表
│   │   └── mine.vue              # 我的任务
│   ├── record/                   # 计件记录
│   └── exception/                # 异常上报
├── bundle/                       # 扎包管理
│   ├── index.vue                 # 扎包列表
│   ├── detail.vue                # 扎包详情
│   └── handover.vue              # 扎包交接
├── cutting/                      # 裁床管理
│   ├── plan/                     # 裁床计划
│   └── bundle/                   # 裁床扎包
├── quality/                      # 质量管理
│   ├── first-article/            # 首件确认
│   ├── standard/                 # 质检标准
│   ├── check/                    # 质检记录
│   ├── defect/                   # 缺陷记录
│   └── patrol/                   # 巡检记录
├── fabric/                       # 面料管理
│   ├── index.vue                 # 面料档案
│   ├── inbound/                  # 面料入库
│   └── requisition/              # 领料申请
├── accessory/                    # 辅料管理
├── sample/                       # 样衣打版
├── tech-pack/                    # 工艺单
├── outsource/                    # 外协加工
├── cost/                         # 成本核算
├── box/                          # 装箱管理
├── data/ (扩展)
│   ├── size-category/            # 尺码分类
│   ├── color/                    # 颜色管理
│   └── barcode-rule/             # 条码规则
└── report/ (增强)
    ├── production-progress.vue   # 生产进度
    ├── piece-work-wage.vue       # 计件工资
    ├── quality-analysis.vue      # 质量分析
    ├── cost-analysis.vue         # 成本分析
    └── inventory-matrix.vue      # 尺码库存矩阵
```

### 10.2 关键页面设计

#### 尺码库存矩阵视图

```
┌─────────────────────────────────────────────────────────────────────────┐
│  商品: 春季休闲裤 (STYLE-001)                    总库存: 285件          │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│         │   S   │   M   │   L   │   XL  │  XXL  │  合计  │              │
│  ───────┼───────┼───────┼───────┼───────┼───────┼───────│              │
│   黑色   │  10   │  20   │  30   │  20   │  10   │  90   │              │
│  ───────┼───────┼───────┼───────┼───────┼───────┼───────│              │
│   白色   │   5   │  15   │  25   │  15   │   5   │  65   │              │
│  ───────┼───────┼───────┼───────┼───────┼───────┼───────│              │
│   蓝色   │   8   │  18   │  28   │  18   │   8   │  80   │              │
│  ───────┼───────┼───────┼───────┼───────┼───────┼───────│              │
│   合计   │  23   │  53   │  83   │  53   │  23   │ 235   │              │
│                                                                          │
│  [打印条码] [导出Excel] [批量调整库存]                                   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

#### 货品工序配置 (二维矩阵)

```
┌─────────────────────────────────────────────────────────────────────────┐
│  货品工序配置 - STYLE-001 春季休闲裤                                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  工序        │ 单价1   │ 单价2   │ 单价3   │ 排序 │ 启用 │ 操作        │
│  ───────────┼────────┼────────┼────────┼─────┼─────┼─────────           │
│  裁剪准备    │  0.50  │  0.60  │  0.70  │  1  │ ✓   │ [编辑]           │
│  裁剪       │  1.00  │  1.20  │  1.50  │  2  │ ✓   │ [编辑]           │
│  缝制-前幅   │  2.00  │  2.50  │  3.00  │  3  │ ✓   │ [编辑]           │
│  缝制-后幅   │  2.00  │  2.50  │  3.00  │  4  │ ✓   │ [编辑]           │
│  锁钉       │  1.50  │  1.80  │  2.00  │  5  │ ✓   │ [编辑]           │
│  中烫       │  0.80  │  1.00  │  1.20  │  6  │ ✓   │ [编辑]           │
│  整烫       │  1.20  │  1.50  │  1.80  │  7  │ ✓   │ [编辑]           │
│  质检       │  0.50  │  0.60  │  0.80  │  8  │ ✓   │ [编辑]           │
│  包装       │  0.80  │  1.00  │  1.20  │  9  │ ✓   │ [编辑]           │
│  ───────────┴────────┴────────┴────────┴─────┴─────┴─────────           │
│                                                                          │
│  工序单价合计:  10.30 │ 12.70 │ 15.20                                   │
│                                                                          │
│  [添加工序] [批量导入] [从其他货品复制]                                   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 十一、销售→生产数据打通

### 11.1 完整数据流转图

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           销售到生产完整链路                             │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌──────────────┐                                                       │
│  │  销售订单    │  jxc_sales_order                                      │
│  │  (批发单)    │  - order_no, customer_id                              │
│  │              │  - status: pending/confirmed/completed                │
│  └──────┬───────┘                                                       │
│         │                                                                │
│         │ ① 审批通过                                                     │
│         ▼                                                                │
│  ┌──────────────┐                                                       │
│  │  库存检查    │  检查成品库存是否充足                                 │
│  └──────┬───────┘                                                       │
│         │                                                                │
│    ┌────┴────┐                                                          │
│    │         │                                                          │
│    ▼         ▼                                                          │
│ 库存充足   库存不足                                                      │
│    │         │                                                          │
│    ▼         ▼                                                          │
│ 直接发货   触发生产                                                      │
│    │         │                                                          │
│    │         ▼                                                          │
│    │    ┌──────────────┐                                                │
│    │    │  生产订单    │  jxc_production_order                          │
│    │    │              │  - source_order_id → sales_order.order_id      │
│    │    │              │  - quantity = 缺口数量                          │
│    │    └──────┬───────┘                                                │
│    │         │                                                           │
│    │         │ ② 生产完成                                               │
│    │         ▼                                                           │
│    │    ┌──────────────┐                                                │
│    │    │  成品入库    │  增加成品库存                                   │
│    │    └──────┬───────┘                                                │
│    │         │                                                           │
│    └────┬────┘                                                           │
│         │                                                                │
│         ▼                                                                │
│    ┌──────────────┐                                                     │
│    │  销售发货    │  减少成品库存，更新销售订单状态                      │
│    └──────────────┘                                                     │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### 11.2 触发模式

**推荐：混合模式**

```
触发规则：

if (商品是常规款 AND 库存 < 安全库存) {
    销售订单审批通过 → 自动创建生产订单 (补货)
} else if (商品是定制款 OR 订单数量 > 大单阈值) {
    销售订单审批通过 → 提示"需手动创建生产订单"
} else {
    从现有库存发货，不触发生产
}
```

| 模式 | 适用场景 | 操作方式 |
|------|---------|---------|
| 自动触发 | 常规款、库存低于安全库存 | 销售审批通过后自动创建生产订单 |
| 手动触发 | 定制款、大额订单 | 销售订单列表选择订单，点击"生成生产订单" |
| 不触发 | 库存充足 | 直接从库存发货 |

### 11.3 字段映射详细对照表

| 销售订单 (jxc_sales_order) | 生产订单 (jxc_production_order) | 转换规则 |
|---------------------------|-------------------------------|---------|
| order_id | source_order_id | 直接映射 |
| order_no | - | 不迁移，生产订单有独立编号 |
| customer_id | customer_id | 直接映射 |
| - | order_no | 按生产订单编号规则生成 (PO+年月日+序号) |
| - | style_no | 从销售订单商品明细提取款号 |
| - | style_name | 从销售订单商品明细提取款式名称 |
| - | style_id | 通过款号匹配款式档案ID |
| delivery_date | deadline | 直接映射 |
| 销售订单商品明细 | quantity | 汇总所有商品的缺口数量 |
| 销售订单商品明细 | order_size_ratio | 转换为尺码配比表记录 |

### 11.4 销售订单商品明细 → 生产订单尺码配比

```sql
-- 销售订单商品明细表 jxc_sales_order_item:
-- - sku_id (SKU ID)
-- - quantity (数量)

-- SKU表 jxc_product_sku:
-- - spu_id
-- - color
-- - size

-- 转换逻辑:
INSERT INTO jxc_order_size_ratio (order_id, size, color, quantity)
SELECT 
    :production_order_id,
    sku.size,
    sku.color,
    SUM(item.quantity)
FROM jxc_sales_order_item item
JOIN jxc_product_sku sku ON item.sku_id = sku.id
WHERE item.order_id = :sales_order_id
GROUP BY sku.size, sku.color;
```

### 11.5 状态同步机制

```
┌─────────────────────────────────────────────────────────────────┐
│                     状态同步规则                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  销售订单状态            生产订单状态          同步动作          │
│  ───────────────────────────────────────────────────────────    │
│  confirmed (已确认)  →  pending (待生产)      创建生产订单       │
│  -                    →  in_production        无动作            │
│  -                    →  completed            更新可发货数量    │
│  partial_shipped     ←  completed            部分发货          │
│  shipped             ←  completed            全部发货          │
│  completed           ←  -                    订单完成          │
│                                                                  │
│  逆向同步（生产异常影响销售）:                                   │
│  生产延期 → 更新销售订单预计交期                                 │
│  生产取消 → 通知销售重新安排                                    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 11.6 边界场景处理

| 场景 | 处理方式 |
|------|---------|
| 一笔销售订单多个款式 | 按款式拆分为多个生产订单 |
| 销售订单修改数量 | 已生产的按原数量，差额新建生产订单 |
| 销售订单取消 | 未开始生产可取消，已开始需协商处理 |
| 部分发货部分生产 | 分别跟踪，发货扣减库存 |
| 紧急插单 | 提高生产订单优先级，调整排程 |

---

## 十二、库存一体化设计

> **重要设计变更**：在制品(WIP)不写入统一库存表，改为动态视图统计。

### 12.1 统一库存表结构

```sql
-- 统一库存表 (替代原有的 jxc_inventory)
-- 注意：item_type 仅支持 fabric/accessory/product，不含 wip
CREATE TABLE jxc_unified_inventory (
    inventory_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    item_type VARCHAR(20) NOT NULL COMMENT '物料类型(fabric/accessory/product)',
    item_id BIGINT NOT NULL COMMENT '物料ID(对应各类型主表ID)',
    
    -- 批次/色号/尺码 (根据类型使用)
    batch_no VARCHAR(100) COMMENT '批次号',
    color VARCHAR(50) COMMENT '颜色/色号',
    size VARCHAR(20) COMMENT '尺码',
    
    -- 数量
    quantity DECIMAL(12,4) DEFAULT 0 COMMENT '库存数量',
    available_qty DECIMAL(12,4) DEFAULT 0 COMMENT '可用数量',
    reserved_qty DECIMAL(12,4) DEFAULT 0 COMMENT '占用数量',
    
    -- 成本
    unit_cost DECIMAL(12,4) COMMENT '单位成本',
    total_cost DECIMAL(12,4) COMMENT '总成本',
    
    -- 预警
    warning_qty DECIMAL(12,4) COMMENT '预警数量',
    
    -- 位置
    location VARCHAR(100) COMMENT '库位',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_warehouse_item (warehouse_id, item_type, item_id, batch_no, color, size),
    INDEX idx_item_type_id (item_type, item_id)
) COMMENT '统一库存表(不含WIP)';

-- 库存流水表
CREATE TABLE jxc_inventory_transaction (
    transaction_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    transaction_no VARCHAR(50) NOT NULL COMMENT '流水号',
    warehouse_id BIGINT NOT NULL,
    item_type VARCHAR(20) NOT NULL COMMENT '物料类型(fabric/accessory/product)',
    item_id BIGINT NOT NULL,
    
    -- 变动信息
    transaction_type VARCHAR(30) NOT NULL COMMENT '类型(inbound/outbound/transfer/reserve/release)',
    quantity DECIMAL(12,4) NOT NULL COMMENT '变动数量(正数入库,负数出库)',
    before_qty DECIMAL(12,4) COMMENT '变动前数量',
    after_qty DECIMAL(12,4) COMMENT '变动后数量',
    
    -- 关联业务
    biz_type VARCHAR(30) COMMENT '业务类型(fabric_inbound/production/quality/sales)',
    biz_id BIGINT COMMENT '业务ID',
    biz_no VARCHAR(50) COMMENT '业务单号',
    
    remark TEXT,
    operator_id BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_transaction_no (transaction_no),
    INDEX idx_biz (biz_type, biz_id)
) COMMENT '库存流水表';
```

### 12.2 库存类型对应关系

| item_type | 说明 | item_id对应 | 批次/色号/尺码 | 写入物理库存 |
|-----------|------|------------|---------------|-------------|
| fabric | 面料库存 | jxc_fabric.fabric_id | 使用batch_no, color | ✅ 是 |
| accessory | 辅料库存 | jxc_accessory.accessory_id | 使用batch_no | ✅ 是 |
| product | 成品库存 | jxc_product_sku.id | 使用color, size | ✅ 是 |

> **WIP说明**：在制品不写入统一库存表，原因如下：
> - 扎包是离散个体，具有唯一bundle_id和进度状态
> - 汇总成quantity存入库存表毫无意义
> - 扎包跨工序移动频繁，会产生海量垃圾流水

### 12.3 在制品(WIP)动态统计视图

```sql
-- WIP动态统计视图（不存储物理数据，实时查询）
CREATE VIEW v_wip_statistics AS
SELECT 
    bundle.order_id,
    bundle.style_no,
    bundle.style_name,
    bundle.size,
    bundle.color,
    bundle.current_process_id,
    proc.name AS current_process_name,
    COUNT(bundle.bundle_id) AS bundle_count,
    SUM(bundle.quantity) AS total_quantity
FROM jxc_bundle bundle
LEFT JOIN jxc_process proc ON bundle.current_process_id = proc.process_id
WHERE bundle.status IN ('assigned', 'in_production', 'quality_check')
GROUP BY bundle.order_id, bundle.style_no, bundle.style_name, 
         bundle.size, bundle.color, bundle.current_process_id, proc.name;

-- 按订单统计WIP
CREATE VIEW v_wip_by_order AS
SELECT 
    order_id,
    SUM(total_quantity) AS wip_quantity
FROM v_wip_statistics
GROUP BY order_id;

-- 按工序统计WIP
CREATE VIEW v_wip_by_process AS
SELECT 
    current_process_id,
    current_process_name,
    SUM(total_quantity) AS wip_quantity
FROM v_wip_statistics
GROUP BY current_process_id, current_process_name;
```

### 12.4 库存流转完整链路（修正版）

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          库存流转链路（修正版）                           │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ① 面料入库 → jxc_unified_inventory (item_type=fabric) +               │
│                                                                          │
│  ② 辅料入库 → jxc_unified_inventory (item_type=accessory) +            │
│                                                                          │
│  ③ 面料领用出库 → jxc_unified_inventory (item_type=fabric) -           │
│                                                                          │
│  ④ 裁床生成扎包 → jxc_bundle 表记录（不写库存表）                        │
│     WIP数量通过 v_wip_statistics 视图实时统计                            │
│                                                                          │
│  ⑤ 扎包流转 → 仅更新 jxc_bundle.current_process_id                      │
│     不产生库存流水                                                        │
│                                                                          │
│  ⑥ 质检通过入库 → jxc_unified_inventory (item_type=product) +          │
│                                                                          │
│  ⑦ 销售发货 → jxc_unified_inventory (item_type=product) -              │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### 12.5 库存占用与释放机制

```
┌─────────────────────────────────────────────────────────────────┐
│                     库存占用/释放场景                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  占用场景 (reserved_qty +, available_qty -):                    │
│  ─────────────────────────────────────                          │
│  - 销售订单确认 → 占用成品库存                                   │
│  - 生产计划确认 → 占用面料库存                                   │
│  - 领料申请审批通过 → 占用面料库存                               │
│                                                                  │
│  释放场景 (reserved_qty -, available_qty +):                    │
│  ─────────────────────────────────────                          │
│  - 销售订单取消 → 释放成品库存占用                               │
│  - 领料出库完成 → 释放面料库存占用，扣减实际库存                 │
│  - 生产计划取消 → 释放面料库存占用                               │
│                                                                  │
│  扣减场景 (quantity -, reserved_qty -):                         │
│  ─────────────────────────────────────                          │
│  - 实际出库 → 扣减占用数量和实际数量                             │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 12.5 与现有库存表的关系

| 现有表 | 处理方式 |
|-------|---------|
| jxc_inventory (商品库存) | 迁移到 jxc_unified_inventory (item_type=product) |
| jxc_inventory_batch (批次库存) | 合并到 jxc_unified_inventory |
| jxc_inventory_log (库存日志) | 迁移到 jxc_inventory_transaction |

**建议**：保留原表作为历史数据，新建统一库存表

---

## 十三、财务成本联动

### 13.1 成本计算详细逻辑

```
┌─────────────────────────────────────────────────────────────────┐
│                     生产成本计算公式                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  总成本 = 材料成本 + 人工成本 + 制造费用                         │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │  1. 材料成本                                            │    │
│  │  ────────────                                           │    │
│  │  材料成本 = Σ(领料数量 × 单价 × (1 + 损耗率))           │    │
│  │                                                        │    │
│  │  数据来源:                                              │    │
│  │  - 面料领料单 jxc_fabric_requisition                   │    │
│  │  - 辅料领料单                                          │    │
│  │  - 单价: 最新入库成本 / 标准成本                       │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │  2. 人工成本                                            │    │
│  │  ────────────                                           │    │
│  │  人工成本 = Σ(计件数量 × 工序单价)                      │    │
│  │                                                        │    │
│  │  数据来源:                                              │    │
│  │  - 计件记录 jxc_process_record                         │    │
│  │  - 工序单价 jxc_product_process_config                 │    │
│  │                                                        │    │
│  │  多单价处理:                                            │    │
│  │  - price1: 普通工价                                    │    │
│  │  - price2: 熟练工价                                    │    │
│  │  - price3: 加班/加急工价                               │    │
│  │  根据员工等级/时间段自动选择                           │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │  3. 制造费用                                            │    │
│  │  ────────────                                           │    │
│  │  制造费用 = 水电 + 折旧 + 租金 + 其他                   │    │
│  │                                                        │    │
│  │  分摊方式:                                              │    │
│  │  - 按产量分摊: 费用 / 总产量 × 本订单产量              │    │
│  │  - 按工时分摊: 费用 / 总工时 × 本订单工时              │    │
│  │  - 按机器时分摊: 费用 / 总机器时 × 本订单机器时        │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
│  单位成本 = 总成本 / 入库数量                                    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 13.2 成本计算数据来源

```sql
-- 材料成本明细查询
SELECT 
    fabric.fabric_id,
    fabric.name AS fabric_name,
    req.quantity,
    fabric.price AS unit_price,
    req.quantity * fabric.price AS amount
FROM jxc_fabric_requisition req
JOIN jxc_fabric fabric ON req.fabric_id = fabric.fabric_id
WHERE req.production_order_id = :order_id
  AND req.status = 'completed';

-- 人工成本明细查询
SELECT 
    proc.process_id,
    proc.name AS process_name,
    SUM(rec.quantity) AS total_quantity,
    config.price1 AS unit_price,
    SUM(rec.quantity) * config.price1 AS amount
FROM jxc_process_record rec
JOIN jxc_process proc ON rec.process_id = proc.process_id
JOIN jxc_bundle bundle ON rec.bundle_id = bundle.bundle_id
JOIN jxc_product_process_config config 
    ON config.product_id = bundle.product_id 
    AND config.process_id = proc.process_id
WHERE bundle.order_id = :order_id
  AND rec.status = 'confirmed'
GROUP BY proc.process_id, proc.name, config.price1;
```

### 13.3 财务凭证生成规则

```
┌─────────────────────────────────────────────────────────────────┐
│                     凭证生成规则                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  业务场景一: 材料领用                                           │
│  ─────────────────────                                          │
│  借: 生产成本-直接材料    XXX                                   │
│      贷: 原材料-面料      XXX                                   │
│      贷: 原材料-辅料      XXX                                   │
│                                                                  │
│  业务场景二: 计件工资确认                                       │
│  ─────────────────────                                          │
│  借: 生产成本-直接人工    XXX                                   │
│      贷: 应付职工薪酬-计件工资  XXX                             │
│                                                                  │
│  业务场景三: 制造费用分摊                                       │
│  ─────────────────────                                          │
│  借: 生产成本-制造费用    XXX                                   │
│      贷: 制造费用-水电费  XXX                                   │
│      贷: 制造费用-折旧费  XXX                                   │
│      贷: 制造费用-租金    XXX                                   │
│                                                                  │
│  业务场景四: 成品入库 (成本结转)                                │
│  ─────────────────────                                          │
│  借: 库存商品            XXX (总成本)                           │
│      贷: 生产成本-直接材料  XXX                                 │
│      贷: 生产成本-直接人工  XXX                                 │
│      贷: 生产成本-制造费用  XXX                                 │
│                                                                  │
│  业务场景五: 销售出库 (结转销售成本)                            │
│  ─────────────────────                                          │
│  借: 主营业务成本        XXX                                    │
│      贷: 库存商品        XXX                                    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 13.4 计件工资与财务联动详细流程

```
┌─────────────────────────────────────────────────────────────────┐
│                  计件工资→财务完整流程                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ① 工人扫码计件                                                 │
│     小程序 → jxc_process_record (status=pending)               │
│                                                                  │
│  ② 班组长/质检确认                                              │
│     确认 → jxc_process_record (status=confirmed)               │
│                                                                  │
│  ③ 日/周/月汇总                                                 │
│     定时任务 → jxc_piece_work_summary (员工+时间段汇总)         │
│                                                                  │
│  ④ 生成工资单                                                   │
│     月底 → jxc_salary_sheet (工资单)                           │
│                                                                  │
│  ⑤ 财务审核                                                     │
│     财务审核通过 → status=approved                              │
│                                                                  │
│  ⑥ 生成凭证                                                     │
│     自动生成 → jxc_finance_voucher                              │
│     借: 生产成本-直接人工                                       │
│         贷: 应付职工薪酬                                        │
│                                                                  │
│  ⑦ 工资发放                                                     │
│     银行转账 → status=paid                                      │
│     生成付款凭证                                                │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 13.5 新增数据表

```sql
-- 计件工资汇总表
CREATE TABLE jxc_piece_work_summary (
    summary_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    worker_id BIGINT NOT NULL COMMENT '员工ID',
    period_type VARCHAR(20) NOT NULL COMMENT '周期类型(daily/weekly/monthly)',
    period_value VARCHAR(20) NOT NULL COMMENT '周期值(2024-01-01/2024-W01/2024-01)',
    total_quantity INT DEFAULT 0 COMMENT '总件数',
    total_amount DECIMAL(12,4) DEFAULT 0 COMMENT '总金额',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态(pending/confirmed/incorporated)',
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_worker_period (worker_id, period_type, period_value)
) COMMENT '计件工资汇总表';

-- 工资单表
CREATE TABLE jxc_salary_sheet (
    sheet_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sheet_no VARCHAR(50) NOT NULL COMMENT '工资单号',
    worker_id BIGINT NOT NULL COMMENT '员工ID',
    period_value VARCHAR(20) NOT NULL COMMENT '期间(2024-01)',
    base_salary DECIMAL(12,2) DEFAULT 0 COMMENT '基本工资',
    piece_work_amount DECIMAL(12,4) DEFAULT 0 COMMENT '计件工资',
    overtime_amount DECIMAL(12,4) DEFAULT 0 COMMENT '加班工资',
    bonus DECIMAL(12,2) DEFAULT 0 COMMENT '奖金',
    subsidy DECIMAL(12,2) DEFAULT 0 COMMENT '补贴',
    deduction DECIMAL(12,2) DEFAULT 0 COMMENT '扣款',
    actual_amount DECIMAL(12,4) DEFAULT 0 COMMENT '实发金额',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态(draft/approved/paid)',
    approved_by BIGINT COMMENT '审核人',
    approved_at DATETIME COMMENT '审核时间',
    paid_at DATETIME COMMENT '发放时间',
    voucher_id BIGINT COMMENT '关联凭证ID',
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_sheet_no (sheet_no),
    UNIQUE KEY uk_worker_period (worker_id, period_value)
) COMMENT '工资单表';
```

### 13.6 计件功能业务流程设计

#### 13.6.1 单价固化流程

```
┌─────────────────────────────────────────────────────────────────┐
│                    单价计算与固化流程                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  扫码计件时:                                                     │
│  ────────────                                                    │
│  1. 获取员工技能等级 (jxc_worker_profile.skill_level)           │
│  2. 判断扫码时间段 (normal/overtime/night)                      │
│  3. 匹配工价规则 (jxc_price_match_rule)                         │
│  4. 获取SPU基准单价 (jxc_product_process_config)               │
│  5. 查询SKU溢价配置 (jxc_sku_price_premium)                     │
│  6. 计算最终单价:                                                │
│     unit_price = base_price × (1 + premium_ratio/100)          │
│  7. 固化单价到计件记录                                           │
│                                                                  │
│  示例:                                                           │
│  ──────                                                          │
│  员工: 张三 (熟练工, skill_level=standard)                      │
│  时间: 2024-01-15 14:30 (正常班)                                │
│  工序: 缝制-前幅                                                 │
│  SPU基准单价: 2.00元 (price2)                                   │
│  SKU溢价: XXL码 +20%                                            │
│  最终单价: 2.00 × 1.20 = 2.40元                                 │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

#### 13.6.2 结算闭环状态流转

```
┌─────────────────────────────────────────────────────────────────┐
│                    计件记录状态流转                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  status (确认状态):                                              │
│  ──────────────────                                              │
│  pending (待确认) → confirmed (已确认) → rejected (已驳回)      │
│                                                                  │
│  settlement_status (结算状态):                                   │
│  ─────────────────────────                                       │
│  unsettled (未结算) → settled (已结算)                           │
│                                                                  │
│  状态组合:                                                       │
│  ──────────                                                      │
│  pending + unsettled  → 待确认，未结算                           │
│  confirmed + unsettled → 已确认，待结算                          │
│  confirmed + settled   → 已结算（已计入工资单）                  │
│  rejected + -          → 已驳回（不参与结算）                    │
│                                                                  │
│  结算规则:                                                       │
│  ──────────                                                      │
│  1. 只有 status=confirmed 的记录才能参与结算                    │
│  2. 结算后 settlement_status=settled                            │
│  3. 已结算记录不可修改/删除                                      │
│  4. 工资单取消时，关联记录回滚为 unsettled                       │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

#### 13.6.3 多人计件分配流程

```
┌─────────────────────────────────────────────────────────────────┐
│                    多人计件业务流程                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  场景一: 主副手模式                                              │
│  ─────────────────────                                          │
│  小程序选择: [主副手计件]                                        │
│  输入: 主工人 + 副工人 + 总数量                                  │
│  系统自动按配置比例分配:                                         │
│    - 主工人: 70%                                                 │
│    - 副工人: 30%                                                 │
│                                                                  │
│  场景二: 自定义分配                                              │
│  ─────────────────────                                          │
│  小程序选择: [团队计件]                                          │
│  输入: 工人列表 + 各自分配比例                                   │
│    - 工人A: 40%                                                  │
│    - 工人B: 35%                                                  │
│    - 工人C: 25%                                                  │
│                                                                  │
│  数据生成:                                                       │
│  ──────────                                                      │
│  1. 创建主计件记录 (jxc_process_record)                         │
│     - is_team_work = 1                                          │
│     - team_type = 'main_assistant' 或 'custom'                  │
│     - team_group_id = 'TG20240115001'                           │
│     - share_ratio = 70 (主工人分配比例)                         │
│     - amount = 总金额 × 70%                                      │
│                                                                  │
│  2. 创建分配明细 (jxc_process_record_share)                     │
│     - 主工人: share_ratio=70, share_amount=...                  │
│     - 副工人: share_ratio=30, share_amount=...                  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

#### 13.6.4 返工扣款业务流程

```
┌─────────────────────────────────────────────────────────────────┐
│                    返工扣款业务流程                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  质检发现缺陷 → 创建返工单                                      │
│       │                                                          │
│       ▼                                                          │
│  ┌─────────────────────────────────────────┐                    │
│  │  责任判定                               │                    │
│  │  - 工人责任 (worker)                    │                    │
│  │  - 面料问题 (material)                  │                    │
│  │  - 工艺设计 (design)                    │                    │
│  │  - 其他原因 (other)                     │                    │
│  └─────────────────────────────────────────┘                    │
│       │                                                          │
│       ├──── 工人责任 ────────────────────────────┐              │
│       │                                          │              │
│       ▼                                          ▼              │
│  ┌─────────────────────┐    ┌─────────────────────────────┐    │
│  │  查询扣款配置       │    │  创建扣款计件记录           │    │
│  │  defect_type +      │    │  record_type = 'deduction'  │    │
│  │  severity →         │    │  amount = -扣款金额          │    │
│  │  deduction_ratio    │    │  (负数表示扣款)              │    │
│  └─────────────────────┘    └─────────────────────────────┘    │
│                                                                  │
│  返工完成 → 返工工人计件                                        │
│       │                                                          │
│       ▼                                                          │
│  ┌─────────────────────────────────────────┐                    │
│  │  创建返工计件记录                       │                    │
│  │  record_type = 'rework'                 │                    │
│  │  related_rework_id = 返工单ID           │                    │
│  │  amount = 正常计件金额                   │                    │
│  └─────────────────────────────────────────┘                    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

#### 13.6.5 并发控制机制

```
┌─────────────────────────────────────────────────────────────────┐
│                    并发防超额控制                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  场景: 网络卡顿导致重复提交 / 两名工人同时扫同一扎包            │
│                                                                  │
│  控制机制:                                                       │
│  ──────────                                                      │
│                                                                  │
│  1. 防重复提交 (jxc_process_record_dedup)                       │
│     ──────────────────────────────────────                       │
│     前端生成 request_id = UUID.randomUUID()                     │
│     提交时携带 request_id，后端检查:                             │
│     SELECT COUNT(*) FROM jxc_process_record_dedup               │
│     WHERE request_id = ?                                         │
│     如果存在 → 拒绝重复提交                                      │
│                                                                  │
│     说明: 使用 UUID 而非业务字段组合，避免拦截合规的分批计件    │
│     (同一工人同一天可以对同一扎包分多次计件)                     │
│                                                                  │
│  2. 乐观锁控制 (jxc_bundle_process.version)                     │
│     ──────────────────────────────────────                       │
│     UPDATE jxc_bundle_process                                    │
│     SET remaining_quantity = remaining_quantity - ?,            │
│         version = version + 1                                    │
│     WHERE id = ? AND version = ? AND remaining_quantity >= ?    │
│                                                                  │
│     affected_rows = 0 → 并发冲突或超额，提示重试                │
│                                                                  │
│  3. 数据库事务保证                                               │
│     ─────────────────────                                        │
│     @Transactional                                               │
│     - 检查防重表 (request_id)                                    │
│     - 乐观锁扣减剩余数量                                         │
│     - 创建计件记录                                               │
│     - 写入防重表 (request_id)                                    │
│     全部成功或全部回滚                                           │
│                                                                  │
│  4. 业务层校验                                                   │
│     ────────────                                                 │
│     if (remaining_quantity < request.quantity) {                │
│         throw new BusinessException("超出可计件数量，剩余" +    │
│             remaining_quantity + "件");                         │
│     }                                                            │
│                                                                  │
│  5. 防重表清理 (定时任务)                                        │
│     ───────────────────────                                      │
│     DELETE FROM jxc_process_record_dedup                        │
│     WHERE created_at < DATE_SUB(NOW(), INTERVAL 7 DAY)          │
│     (保留7天的防重记录，过期自动清理)                            │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

#### 13.6.6 多级工价匹配规则

```
┌─────────────────────────────────────────────────────────────────┐
│                    工价匹配规则示例                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  默认规则配置:                                                   │
│  ──────────────                                                  │
│  | 技能等级 | 时段      | 价格字段 | 说明                  |    │
│  |----------|-----------|----------|-----------------------|    │
│  | trainee  | normal    | price1   | 学徒正常班→基础单价  |    │
│  | trainee  | overtime  | price2   | 学徒加班→熟练工单价  |    │
│  | standard | normal    | price2   | 熟练工正常班         |    │
│  | standard | overtime  | price3   | 熟练工加班→加急单价  |    │
│  | expert   | normal    | price3   | 专家正常班→加急单价  |    │
│  | expert   | overtime  | price3   | 专家加班             |    │
│  | *        | night     | price3   | 夜班统一加急单价     |    │
│                                                                  │
│  时段判断规则:                                                   │
│  ──────────────                                                  │
│  normal:   08:00 - 18:00 (正常班)                               │
│  overtime: 18:00 - 22:00 (加班)                                 │
│  night:    22:00 - 08:00 (夜班)                                 │
│                                                                  │
│  优先级: 夜班 > 加班 > 正常班                                    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

#### 13.6.7 SKU溢价计算示例

```
┌─────────────────────────────────────────────────────────────────┐
│                    SKU溢价计算逻辑                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  计算公式:                                                       │
│  ──────────                                                      │
│  最终单价 = SPU基准单价 × (1 + 溢价比例/100)                    │
│           或 固定价格 (如果配置了fixed_price)                   │
│                                                                  │
│  示例场景:                                                       │
│  ──────────                                                      │
│  SPU: 春季休闲裤 (STYLE-001)                                    │
│  工序: 缝制-前幅                                                 │
│  SPU基准单价: 2.00元                                            │
│                                                                  │
│  SKU溢价配置:                                                    │
│  ┌────────────────┬────────────┬─────────┬──────────────┐      │
│  │ SKU            │ 工序       │ 溢价    │ 最终单价     │      │
│  ├────────────────┼────────────┼─────────┼──────────────┤      │
│  │ S码/黑色       │ 缝制-前幅  │ 0%      │ 2.00元       │      │
│  │ M码/黑色       │ 缝制-前幅  │ 0%      │ 2.00元       │      │
│  │ L码/黑色       │ 缝制-前幅  │ 0%      │ 2.00元       │      │
│  │ XL码/黑色      │ 缝制-前幅  │ 10%     │ 2.20元       │      │
│  │ XXL码/黑色     │ 缝制-前幅  │ 20%     │ 2.40元       │      │
│  │ L码/荧光绿     │ 缝制-前幅  │ 15%     │ 2.30元       │      │
│  │ M码/亮片款     │ 缝制-前幅  │ 固定3.00│ 3.00元       │      │
│  └────────────────┴────────────┴─────────┴──────────────┘      │
│                                                                  │
│  查询逻辑:                                                       │
│  ──────────                                                      │
│  1. 先查 jxc_sku_price_premium (sku_id, process_id)            │
│  2. 如果存在且有 fixed_price → 使用固定价格                     │
│  3. 如果存在且有 premium_ratio → 计算: base × (1+ratio)        │
│  4. 如果不存在 → 使用SPU基准价格                                │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 十四、数据迁移策略

### 14.1 两系统数据资产清单

```
┌─────────────────────────────────────────────────────────────────┐
│                    duoduo_jxc 数据资产                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  基础数据:                                                       │
│  - jxc_user (用户/员工)                                         │
│  - jxc_customer (客户)                                          │
│  - jxc_supplier (供应商)                                        │
│  - jxc_warehouse (仓库)                                         │
│  - jxc_product_spu (商品SPU)                                    │
│  - jxc_product_sku (商品SKU)                                    │
│  - jxc_product_category (商品分类)                              │
│                                                                  │
│  业务数据:                                                       │
│  - jxc_sales_order (销售订单)                                   │
│  - jxc_sales_order_item (销售订单明细)                          │
│  - jxc_purchase_order (采购订单)                                │
│  - jxc_inventory (库存)                                         │
│  - jxc_finance_* (财务相关表)                                   │
│                                                                  │
│  工作流数据:                                                     │
│  - act_* (Flowable工作流表)                                     │
│  - jxc_workflow_* (工作流配置)                                  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    coding 数据资产                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  基础数据:                                                       │
│  - processes_process (工序库)                                   │
│  - factories_factory (工厂)                                     │
│  - workshops_workshop (车间)                                    │
│  - work_groups_workgroup (班组)                                 │
│                                                                  │
│  业务数据:                                                       │
│  - production_order (生产订单)                                  │
│  - bundles_bundle (扎包)                                        │
│  - bundles_bundleprocess (扎包工序)                             │
│  - processes_processrecord (计件记录)                           │
│  - processes_processtask (工序任务)                             │
│  - cutting_cuttingplan (裁床计划)                               │
│  - cutting_cuttingbundle (裁床扎包)                             │
│  - quality_* (质检相关表)                                       │
│  - fabrics_* (面料相关表)                                       │
│                                                                  │
│  小程序数据:                                                     │
│  - 用户扫码记录、异常上报等                                      │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 14.2 数据映射规则

```
┌─────────────────────────────────────────────────────────────────┐
│                    基础数据映射                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ① 员工数据合并                                                 │
│  ─────────────────                                              │
│  jxc_user (jxc)  +  User (coding)  →  jxc_user (新)            │
│                                                                  │
│  合并规则:                                                       │
│  - 以手机号为唯一标识去重                                       │
│  - jxc已有员工保留，coding新增员工插入                          │
│  - 记录ID映射表，用于关联数据迁移                               │
│                                                                  │
│  ② 客户/供应商                                                  │
│  ─────────────────                                              │
│  以jxc为准，coding中的客户数据不迁移（通过order_no关联）        │
│                                                                  │
│  ③ 商品与款式关联                                               │
│  ─────────────────                                              │
│  jxc_product_spu.style_no  ←→  coding订单中的style_no          │
│  新建 jxc_style 表，将款号信息提取为款式档案                    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 14.3 ID映射表设计

```sql
-- 数据迁移ID映射表
CREATE TABLE jxc_migration_id_mapping (
    mapping_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    source_system VARCHAR(30) NOT NULL COMMENT '来源系统(jxc/coding)',
    source_table VARCHAR(100) NOT NULL COMMENT '来源表名',
    source_id BIGINT NOT NULL COMMENT '来源ID',
    target_table VARCHAR(100) NOT NULL COMMENT '目标表名',
    target_id BIGINT NOT NULL COMMENT '目标ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_source (source_system, source_table, source_id),
    INDEX idx_target (target_table, target_id)
) COMMENT '数据迁移ID映射表';
```

### 14.4 迁移脚本示例

```sql
-- ============================================
-- 迁移脚本示例: 工序库迁移
-- ============================================

-- 1. 迁移工序主表
INSERT INTO jxc_process (code, name, process_type, process_category, 
    equipment_type, difficulty_level, standard_time, default_price1, 
    default_price2, default_price3, skill_requirement, sort_order, is_active)
SELECT 
    code,
    name,
    process_type,
    category AS process_category,
    equipment_type,
    difficulty_level,
    standard_time,
    price1 AS default_price1,
    price2 AS default_price2,
    price3 AS default_price3,
    skill_requirement,
    sort_order,
    is_active
FROM coding_processes_process;

-- 2. 记录ID映射
INSERT INTO jxc_migration_id_mapping (source_system, source_table, source_id, target_table, target_id)
SELECT 'coding', 'processes_process', p.id, 'jxc_process', jp.process_id
FROM coding_processes_process p
JOIN jxc_process jp ON jp.code = p.code;

-- 3. 迁移工序依赖关系
INSERT INTO jxc_process_dependency (process_id, depends_on_id, dependency_type, description)
SELECT 
    jp.target_id AS process_id,
    jp2.target_id AS depends_on_id,
    pd.dependency_type,
    pd.description
FROM coding_processes_processdependency pd
JOIN jxc_migration_id_mapping jp ON jp.source_id = pd.process_id AND jp.source_table = 'processes_process'
JOIN jxc_migration_id_mapping jp2 ON jp2.source_id = pd.depends_on_id AND jp2.source_table = 'processes_process'
WHERE jp.source_system = 'coding' AND jp2.source_system = 'coding';
```

### 14.5 迁移验证规则

```
┌─────────────────────────────────────────────────────────────────┐
│                    迁移验证检查清单                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  数据完整性检查:                                                 │
│  □ 源系统记录数 = 目标系统记录数                                │
│  □ 外键关联完整性 (所有引用ID都存在)                            │
│  □ 必填字段非空检查                                             │
│                                                                  │
│  数据一致性检查:                                                 │
│  □ 金额汇总: 源系统总金额 = 目标系统总金额                      │
│  □ 数量汇总: 源系统总数量 = 目标系统总数量                      │
│  □ 枚举值映射正确                                               │
│                                                                  │
│  业务逻辑检查:                                                   │
│  □ 订单状态流转正确                                             │
│  □ 库存数量计算正确                                             │
│  □ 财务金额平衡                                                 │
│                                                                  │
│  功能验证:                                                       │
│  □ 新系统能正常查询迁移数据                                     │
│  □ 新系统能正常创建新数据                                       │
│  □ 历史数据与新建数据关联正确                                   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 14.6 迁移执行计划

```
┌─────────────────────────────────────────────────────────────────┐
│                    迁移执行时间表                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  T-7天: 准备阶段                                                │
│  ├── 导出两系统完整数据快照                                     │
│  ├── 在测试环境执行迁移测试                                     │
│  ├── 验证迁移数据正确性                                         │
│  └── 制定回滚方案                                               │
│                                                                  │
│  T-3天: 冻结阶段                                                │
│  ├── 公告用户系统即将迁移                                       │
│  ├── 停止coding新数据录入                                       │
│  └── jxc仅允许查询，禁止修改                                    │
│                                                                  │
│  T-0天: 迁移日 (建议周末)                                       │
│  ├── 00:00 - 停止所有系统访问                                   │
│  ├── 00:30 - 执行数据库备份                                     │
│  ├── 01:00 - 开始数据迁移                                       │
│  │   ├── 01:00-02:00 基础数据迁移                              │
│  │   ├── 02:00-04:00 业务数据迁移                              │
│  │   └── 04:00-06:00 数据验证                                  │
│  ├── 06:00 - 启动新系统                                         │
│  ├── 06:30 - 功能冒烟测试                                       │
│  └── 08:00 - 开放用户访问                                       │
│                                                                  │
│  T+1~T+7天: 监控阶段                                            │
│  ├── 监控系统运行状态                                           │
│  ├── 收集用户反馈                                               │
│  ├── 修复发现的问题                                             │
│  └── 保留旧系统只读访问1个月                                    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 14.7 迁移优先级

| 优先级 | 数据类型 | 来源 | 处理方式 |
|-------|---------|------|---------|
| P0 | 客户/供应商/员工 | jxc | 全量迁移 |
| P0 | 商品基础信息 | jxc | 全量迁移 |
| P1 | 工序库 | coding | 全量迁移 |
| P1 | 班组/车间 | coding | 全量迁移 |
| P2 | 在产扎包 | coding | 迁移活跃数据 |
| P2 | 计件记录 | coding | 迁移近3个月 |
| P3 | 历史订单 | jxc+coding | 按需迁移 |
| P3 | 历史质检 | coding | 按需迁移 |

---

## 十五、实施计划

### 15.1 阶段划分

| 阶段 | 内容 | 工作量 | 依赖 |
|------|------|--------|------|
| 阶段一 | 基础数据 + 多工厂/车间/班组 | 1-2月 | 无 |
| 阶段二 | 工序管理 + 计件计费 + 扎包 | 2-4月 | 阶段一 |
| 阶段三 | BOM + 生产订单/排程 + 裁床 | 4-6月 | 阶段二 |
| 阶段四 | 质量管理 + 外协加工 | 6-8月 | 阶段三 |
| 阶段五 | 面料管理 + 成本核算 | 8-10月 | 无 |
| 阶段六 | 数据打通 + 报表完善 | 10-11月 | 全部 |
| 阶段七 | 小程序重写 | 11-12月 | 阶段二 |

### 15.2 里程碑

- **M1 (第2月末)**: 尺码/颜色/条码规则/工厂/班组管理完成
- **M2 (第4月末)**: 工序管理/计件计费/扎包管理完成
- **M3 (第6月末)**: 裁床/生产订单/排程/返工/BOM完成
- **M4 (第8月末)**: 首件确认/质检/巡检/外协完成
- **M5 (第10月末)**: 面料/辅料/样衣/成本核算完成
- **M6 (第11月末)**: 销售→生产打通、报表完成
- **M7 (第12月末)**: 小程序重写完成、系统验收

---

## 十六、数据表统计

### 16.1 数据表统计

| 模块 | 新增表数量 |
|------|-----------|
| 款式管理 | 2 |
| 尺码颜色管理 | 5 |
| 条码规则 | 1 |
| 工序管理 | 12 |
| 扎包管理 | 2 |
| 生产管理 | 7 |
| BOM管理 | 3 |
| 裁床管理 | 3 |
| 质量管理 | 7 |
| 面料管理 | 6 |
| 辅料管理 | 2 |
| 样衣打版 | 4 |
| 工艺单 | 2 |
| 装箱管理 | 3 |
| 外协加工 | 3 |
| 成本核算 | 6 |
| 组织架构 | 6 |
| 工作流扩展 | 2 |
| 灵活流程管理 | 7 |
| 库存一体化 | 2 |
| 财务联动 | 2 |
| 数据迁移 | 1 |
| 权限管理 | 5 |
| **销售管理** | **10** |
| **采购管理** | **10** |
| **财务管理** | **8** |
| **库存管理** | **6** |
| 系统配置 | 3 |
| 消息通知 | 3 |
| 日志审计 | 4 |
| 打印模板 | 1 |
| **合计** | **约130张** |

### 16.2 融合设计新增表

| 表名 | 用途 | 所属章节 |
|------|------|---------|
| jxc_unified_inventory | 统一库存表 | 库存一体化 |
| jxc_inventory_transaction | 库存流水表 | 库存一体化 |
| jxc_piece_work_summary | 计件工资汇总表 | 财务成本联动 |
| jxc_salary_sheet | 工资单表 | 财务成本联动 |
| jxc_migration_id_mapping | 数据迁移ID映射表 | 数据迁移策略 |

### 16.3 灵活流程管理新增表

| 表名 | 用途 |
|------|------|
| jxc_flow_template | 流程模板主表 |
| jxc_flow_template_process | 流程模板工序明细 |
| jxc_flow_template_dependency | 流程模板工序依赖 |
| jxc_factory_flow_config | 工厂流程配置 |
| jxc_style_flow_config | 款式流程配置 |
| jxc_bundle_flow_instance | 扎包流程实例 |

### 16.4 计件功能改进新增表

| 表名 | 用途 |
|------|------|
| jxc_process_record_share | 多人计件分配明细表 |
| jxc_process_record_dedup | 计件记录防重表 |
| jxc_price_match_rule | 工价匹配规则表 |
| jxc_sku_price_premium | SKU计件溢价配置表 |
| jxc_rework_piece_work | 返工计件关联表 |
| jxc_rework_deduction_config | 返工扣款配置表 |
| jxc_worker_profile | 员工档案扩展表 |
| jxc_worker_skill | 员工技能认证表 |

### 16.5 权限管理新增表

| 表名 | 用途 |
|------|------|
| jxc_role | 角色表 |
| jxc_menu | 菜单表 |
| jxc_role_menu | 角色菜单关联表 |
| jxc_user_role | 用户角色关联表 |
| jxc_data_permission | 数据权限规则表 |

---

## 十七、数据字典与枚举

### 17.1 枚举命名规范

```
命名规范:
- 枚举常量: 大写下划线格式 (PENDING, IN_PROGRESS, COMPLETED)
- 数据库值: 小写下划线格式 (pending, in_progress, completed)
- 显示名称: 中文名称，通过前端映射

枚举定义格式:
| 枚举值 | 数据库值 | 显示名称 | 说明 |
```

### 17.2 基础数据枚举

#### 17.2.1 款式状态 (StyleStatus)

| 枚举值 | 数据库值 | 显示名称 | 说明 |
|--------|---------|---------|------|
| DEVELOPING | developing | 开发中 | 新款式开发阶段 |
| CONFIRMED | confirmed | 已定版 | 开发完成，可投产 |
| PRODUCING | producing | 已投产 | 正在生产中 |
| DISCONTINUED | discontinued | 已停产 | 停止生产 |

**状态流转：**
```
developing → confirmed → producing → discontinued
     ↓            ↓          ↓
discontinued  discontinued  discontinued
```

#### 17.2.2 季节 (Season)

| 枚举值 | 数据库值 | 显示名称 |
|--------|---------|---------|
| SPRING | spring | 春季 |
| SUMMER | summer | 夏季 |
| AUTUMN | autumn | 秋季 |
| WINTER | winter | 冬季 |
| ALL_SEASON | all_season | 四季通用 |

#### 17.2.3 目标性别 (TargetGender)

| 枚举值 | 数据库值 | 显示名称 |
|--------|---------|---------|
| MALE | male | 男装 |
| FEMALE | female | 女装 |
| UNISEX | unisex | 中性 |
| CHILDREN | children | 童装 |

### 17.3 生产管理枚举

#### 17.3.1 生产订单状态 (ProductionOrderStatus)

| 枚举值 | 数据库值 | 显示名称 | 说明 |
|--------|---------|---------|------|
| PENDING | pending | 待生产 | 订单已创建，等待开始 |
| IN_PRODUCTION | in_production | 生产中 | 正在生产 |
| COMPLETED | completed | 已完成 | 生产完成 |
| CANCELLED | cancelled | 已取消 | 订单取消 |

**状态流转：**
```
pending → in_production → completed
    ↓              ↓
cancelled    cancelled
```

#### 17.3.2 订单优先级 (OrderPriority)

| 枚举值 | 数据库值 | 显示名称 | 数值 |
|--------|---------|---------|------|
| LOW | low | 低 | 1 |
| NORMAL | normal | 普通 | 2 |
| HIGH | high | 高 | 3 |
| URGENT | urgent | 紧急 | 4 |

#### 17.3.3 生产计划状态 (ProductionPlanStatus)

| 枚举值 | 数据库值 | 显示名称 |
|--------|---------|---------|
| DRAFT | draft | 草稿 |
| CONFIRMED | confirmed | 已确认 |
| IN_PROGRESS | in_progress | 进行中 |
| COMPLETED | completed | 已完成 |
| CANCELLED | cancelled | 已取消 |

### 17.4 工序管理枚举

#### 17.4.1 工序大类 (ProcessCategory)

| 枚举值 | 数据库值 | 显示名称 | 说明 |
|--------|---------|---------|------|
| CUTTING_PREP | cutting_prep | 裁剪准备 | 验布、铺布、划样 |
| CUTTING | cutting | 裁剪 | 裁床裁剪 |
| SEWING_FRONT | sewing_front | 缝制前道 | 粘衬、打省、收褶 |
| SEWING_MAIN | sewing_main | 缝制主道 | 前后幅、袖子、领子 |
| SEWING_BACK | sewing_back | 缝制后道 | 锁眼、钉扣、套结 |
| IRONING | ironing | 整烫 | 中烫、整烫 |
| QUALITY | quality | 质检 | 各类检验 |
| PACKING | packing | 包装 | 折叠、包装 |
| SPECIAL | special | 特殊工序 | 刺绣、印花、洗水 |

#### 17.4.2 工序任务状态 (ProcessTaskStatus)

| 枚举值 | 数据库值 | 显示名称 |
|--------|---------|---------|
| PENDING | pending | 待接单 |
| ACCEPTED | accepted | 已接单 |
| IN_PROGRESS | in_progress | 进行中 |
| COMPLETED | completed | 已完成 |
| CANCELLED | cancelled | 已取消 |

**状态流转：**
```
pending → accepted → in_progress → completed
    ↓         ↓           ↓
cancelled  cancelled  cancelled
```

### 17.5 计件管理枚举

#### 17.5.1 计件记录状态 (ProcessRecordStatus)

| 枚举值 | 数据库值 | 显示名称 | 说明 |
|--------|---------|---------|------|
| PENDING | pending | 待确认 | 等待班组长确认 |
| CONFIRMED | confirmed | 已确认 | 已确认，可结算 |
| REJECTED | rejected | 已驳回 | 驳回，需重新提交 |

#### 17.5.2 计件类型 (RecordType)

| 枚举值 | 数据库值 | 显示名称 | 金额方向 |
|--------|---------|---------|---------|
| NORMAL | normal | 正常计件 | 正数 |
| REWORK | rework | 返工计件 | 正数 |
| DEDUCTION | deduction | 质量扣款 | 负数 |
| BONUS | bonus | 奖金 | 正数 |

#### 17.5.3 结算状态 (SettlementStatus)

| 枚举值 | 数据库值 | 显示名称 |
|--------|---------|---------|
| UNSETTLED | unsettled | 未结算 |
| SETTLED | settled | 已结算 |

**完整状态流转：**
```
pending → confirmed → settled
    ↓         ↓
rejected  unsettled (工资单取消时回滚)
```

#### 17.5.4 技能等级 (SkillLevel)

| 枚举值 | 数据库值 | 显示名称 | 默认价格字段 |
|--------|---------|---------|-------------|
| TRAINEE | trainee | 学徒 | price1 |
| STANDARD | standard | 熟练工 | price2 |
| EXPERT | expert | 专家 | price3 |

#### 17.5.5 时段类型 (TimePeriod)

| 枚举值 | 数据库值 | 显示名称 | 时间范围 |
|--------|---------|---------|---------|
| NORMAL | normal | 正常班 | 08:00-18:00 |
| OVERTIME | overtime | 加班 | 18:00-22:00 |
| NIGHT | night | 夜班 | 22:00-08:00 |

#### 17.5.6 团队协作类型 (TeamType)

| 枚举值 | 数据库值 | 显示名称 |
|--------|---------|---------|
| MAIN_ASSISTANT | main_assistant | 主副手 |
| CUSTOM | custom | 自定义分配 |

### 17.6 扎包管理枚举

#### 17.6.1 扎包状态 (BundleStatus)

| 枚举值 | 数据库值 | 显示名称 | 说明 |
|--------|---------|---------|------|
| PENDING | pending | 待分配 | 扎包已生成，待分配工序 |
| ASSIGNED | assigned | 已分配 | 已分配给班组 |
| IN_PRODUCTION | in_production | 生产中 | 正在流转 |
| QUALITY_CHECK | quality_check | 质检中 | 质检环节 |
| COMPLETED | completed | 已完成 | 全部工序完成 |
| ABNORMAL | abnormal | 异常 | 有异常需处理 |
| RETURNED | returned | 已退还 | 已退回上工序 |

**状态流转：**
```
pending → assigned → in_production → quality_check → completed
                         ↓                 ↓
                     abnormal          abnormal
                         ↓                 ↓
                     returned          returned
```

### 17.7 质量管理枚举

#### 17.7.1 首件确认结果 (FirstArticleResult)

| 枚举值 | 数据库值 | 显示名称 |
|--------|---------|---------|
| PENDING | pending | 待确认 |
| PASSED | passed | 合格 |
| FAILED | failed | 不合格 |
| REWORK_PENDING | rework_pending | 返工后待确认 |

#### 17.7.2 质检结果 (QualityCheckResult)

| 枚举值 | 数据库值 | 显示名称 |
|--------|---------|---------|
| PASSED | passed | 合格 |
| FAILED | failed | 不合格 |
| REWORK | rework | 返工 |
| SCRAPPED | scrapped | 报废 |

#### 17.7.3 缺陷处理方式 (HandlingMethod)

| 枚举值 | 数据库值 | 显示名称 |
|--------|---------|---------|
| REWORK | rework | 返工 |
| SCRAPPED | scrapped | 报废 |
| CONCESSION | concession | 让步接收 |

#### 17.7.4 缺陷严重程度 (DefectSeverity)

| 枚举值 | 数据库值 | 显示名称 | AQL标准 |
|--------|---------|---------|---------|
| CRITICAL | critical | 致命缺陷 | 0 |
| MAJOR | major | 严重缺陷 | 2.5 |
| MINOR | minor | 轻微缺陷 | 4.0 |

#### 17.7.5 责任归属 (ResponsibilityType)

| 枚举值 | 数据库值 | 显示名称 |
|--------|---------|---------|
| WORKER | worker | 工人责任 |
| MATERIAL | material | 面料问题 |
| DESIGN | design | 工艺设计 |
| EQUIPMENT | equipment | 设备故障 |
| OTHER | other | 其他原因 |

### 17.8 返工管理枚举

#### 17.8.1 返工单状态 (ReworkOrderStatus)

| 枚举值 | 数据库值 | 显示名称 |
|--------|---------|---------|
| PENDING | pending | 待分配 |
| ASSIGNED | assigned | 已分配 |
| IN_PROGRESS | in_progress | 返工中 |
| COMPLETED | completed | 已完成 |
| RECHECKED | rechecked | 已复检 |

**状态流转：**
```
pending → assigned → in_progress → completed → rechecked
```

### 17.9 面料管理枚举

#### 17.9.1 面料类型 (FabricType)

| 枚举值 | 数据库值 | 显示名称 |
|--------|---------|---------|
| WOVEN | woven | 梭织 |
| KNITTED | knitted | 针织 |
| WOOLEN | woolen | 毛织 |
| LEATHER | leather | 皮革 |

#### 17.9.2 面料用途 (FabricUsage)

| 枚举值 | 数据库值 | 显示名称 |
|--------|---------|---------|
| MAIN | main | 主料 |
| LINING | lining | 里料 |
| ACCESSORY | accessory | 辅料 |
| INTERLINING | interlining | 衬料 |

#### 17.9.3 面料入库状态 (FabricInboundStatus)

| 枚举值 | 数据库值 | 显示名称 |
|--------|---------|---------|
| PENDING | pending | 待入库 |
| PARTIAL | partial | 部分入库 |
| COMPLETED | completed | 已完成 |
| CANCELLED | cancelled | 已取消 |

### 17.10 外协管理枚举

#### 17.10.1 外协订单状态 (OutsourceOrderStatus)

| 枚举值 | 数据库值 | 显示名称 |
|--------|---------|---------|
| PENDING | pending | 待发料 |
| DELIVERED | delivered | 已发料 |
| IN_PROGRESS | in_progress | 加工中 |
| RECEIVED | received | 已收货 |
| SETTLED | settled | 已结算 |

**状态流转：**
```
pending → delivered → in_progress → received → settled
```

### 17.11 工资管理枚举

#### 17.11.1 工资单状态 (SalarySheetStatus)

| 枚举值 | 数据库值 | 显示名称 |
|--------|---------|---------|
| DRAFT | draft | 草稿 |
| APPROVED | approved | 已审核 |
| PAID | paid | 已发放 |

**状态流转：**
```
draft → approved → paid
```

#### 17.11.2 计件汇总状态 (PieceWorkSummaryStatus)

| 枚举值 | 数据库值 | 显示名称 |
|--------|---------|---------|
| PENDING | pending | 待确认 |
| CONFIRMED | confirmed | 已确认 |
| INCORPORATED | incorporated | 已并入工资单 |

### 17.12 库存管理枚举

> **v2.3修正**：在制品(WIP)不再写入统一库存表，改为动态视图统计。

#### 17.12.1 库存类型 (ItemType)

| 枚举值 | 数据库值 | 显示名称 | 写入物理库存 |
|--------|---------|---------|-------------|
| FABRIC | fabric | 面料库存 | ✅ 是 |
| ACCESSORY | accessory | 辅料库存 | ✅ 是 |
| PRODUCT | product | 成品库存 | ✅ 是 |

> **说明**：WIP(在制品)通过 `v_wip_statistics` 视图动态统计，不写入 `jxc_unified_inventory` 表。

#### 17.12.2 库存变动类型 (TransactionType)

| 枚举值 | 数据库值 | 显示名称 | 数量方向 |
|--------|---------|---------|---------|
| INBOUND | inbound | 入库 | + |
| OUTBOUND | outbound | 出库 | - |
| TRANSFER | transfer | 调拨 | +/- |
| RESERVE | reserve | 占用 | -available |
| RELEASE | release | 释放 | +available |
| ADJUST | adjust | 盘点调整 | +/- |

#### 17.12.3 库存业务类型 (BizType)

| 枚举值 | 数据库值 | 显示名称 |
|--------|---------|---------|
| FABRIC_INBOUND | fabric_inbound | 面料入库 |
| FABRIC_REQUISITION | fabric_requisition | 面料领用 |
| PRODUCTION | production | 生产入库(成品+) |
| QUALITY_PASS | quality_pass | 质检合格入库 |
| SALES | sales | 销售出库 |
| PURCHASE_RETURN | purchase_return | 采购退货 |
| SALES_RETURN | sales_return | 销售退货 |

> **v2.3修正**：移除CUTTING类型，裁床生成的扎包不再写入库存表。

### 17.13 流程模板枚举

#### 17.13.1 门禁类型 (GateType)

| 枚举值 | 数据库值 | 显示名称 |
|--------|---------|---------|
| FIRST_ARTICLE | first_article | 首件确认门禁 |
| QUALITY_CHECK | quality_check | 质检门禁 |
| PROCESS_SEQUENCE | process_sequence | 工序顺序门禁 |
| QUANTITY | quantity | 数量溢出门禁 |

### 17.14 错误码定义

#### 17.14.1 通用错误码 (1xxx)

| 错误码 | 枚举值 | 显示名称 |
|--------|--------|---------|
| 1001 | UNAUTHORIZED | 未授权 |
| 1002 | FORBIDDEN | 无权限 |
| 1003 | NOT_FOUND | 资源不存在 |
| 1004 | VALIDATION_ERROR | 参数校验失败 |
| 1005 | DUPLICATE_REQUEST | 重复请求 |

#### 17.14.2 业务错误码 (2xxx)

| 错误码 | 枚举值 | 显示名称 |
|--------|--------|---------|
| 2001 | BUNDLE_NOT_FOUND | 扎包不存在 |
| 2002 | FIRST_ARTICLE_NOT_PASSED | 首件未确认 |
| 2003 | PROCESS_SEQUENCE_INVALID | 工序顺序错误 |
| 2004 | QUANTITY_OVERFLOW | 数量超出 |
| 2005 | WORKFLOW_NODE_NOT_READY | 工作流节点未就绪 |
| 2006 | ALREADY_SETTLED | 已结算，不可修改 |
| 2007 | INSUFFICIENT_STOCK | 库存不足 |
| 2008 | PRICE_NOT_FOUND | 未找到单价配置 |
| 2009 | RECORD_ALREADY_CONFIRMED | 记录已确认 |
| 2010 | SALARY_SHEET_APPROVED | 工资单已审核 |

---

## 十八、权限设计

### 18.1 权限体系架构

```
┌─────────────────────────────────────────────────────────────────┐
│                       权限体系架构                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  第一层: 功能权限 (菜单+按钮)                                   │
│  ────────────────────────────                                   │
│  - 菜单权限: 控制用户能看到哪些菜单                             │
│  - 按钮权限: 控制用户能看到/点击哪些按钮                        │
│                                                                  │
│  第二层: 数据权限 (按组织隔离)                                  │
│  ────────────────────────────                                   │
│  - 全部数据: 超级管理员可查看所有数据                           │
│  - 本工厂数据: 工厂管理员只能查看本工厂数据                     │
│  - 本车间数据: 车间主任只能查看本车间数据                       │
│  - 本班组数据: 班组长只能查看本班组数据                         │
│  - 个人数据: 普通工人只能查看自己的计件记录                     │
│                                                                  │
│  第三层: 字段权限 (敏感数据脱敏)                                │
│  ────────────────────────────                                   │
│  - 成本价: 仅财务可见                                           │
│  - 工资明细: 仅本人+财务可见                                    │
│  - 客户信息: 销售可见完整，其他角色脱敏                         │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 18.2 数据表设计

```sql
-- 角色表
CREATE TABLE jxc_role (
    role_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    data_scope VARCHAR(20) DEFAULT 'self' COMMENT '数据权限范围(all/factory/workshop/group/self)',
    description VARCHAR(200),
    is_system TINYINT DEFAULT 0 COMMENT '是否系统内置角色',
    is_active TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_role_code (role_code)
) COMMENT '角色表';

-- 菜单表
CREATE TABLE jxc_menu (
    menu_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
    menu_code VARCHAR(50) NOT NULL COMMENT '菜单编码',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    menu_type VARCHAR(20) NOT NULL COMMENT '类型(menu/button)',
    path VARCHAR(100) COMMENT '路由路径',
    component VARCHAR(100) COMMENT '组件路径',
    permission VARCHAR(100) COMMENT '权限标识',
    icon VARCHAR(50) COMMENT '图标',
    sort_order INT DEFAULT 0,
    is_visible TINYINT DEFAULT 1 COMMENT '是否显示',
    is_active TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_menu_code (menu_code)
) COMMENT '菜单表';

-- 角色菜单关联表
CREATE TABLE jxc_role_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_role_menu (role_id, menu_id)
) COMMENT '角色菜单关联表';

-- 用户角色关联表
CREATE TABLE jxc_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_role (user_id, role_id)
) COMMENT '用户角色关联表';

-- 数据权限规则表
CREATE TABLE jxc_data_permission (
    permission_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL COMMENT '角色ID',
    table_name VARCHAR(50) NOT NULL COMMENT '表名',
    scope_field VARCHAR(50) COMMENT '数据范围字段(factory_id/workshop_id/work_group_id/worker_id)',
    custom_rule VARCHAR(500) COMMENT '自定义规则(SQL片段)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_role_table (role_id, table_name)
) COMMENT '数据权限规则表';
```

### 18.3 预设角色定义

| 角色编码 | 角色名称 | 数据权限范围 | 说明 |
|---------|---------|-------------|------|
| SUPER_ADMIN | 超级管理员 | all | 系统最高权限 |
| FACTORY_ADMIN | 工厂管理员 | factory | 管理本工厂所有业务 |
| WORKSHOP_MANAGER | 车间主任 | workshop | 管理本车间生产 |
| GROUP_LEADER | 班组长 | group | 管理本班组工人和任务 |
| QC_INSPECTOR | 质检员 | factory_qc | 本工厂质检数据 |
| WAREHOUSE_KEEPER | 仓库管理员 | warehouse | 本仓库数据 |
| FINANCE_STAFF | 财务人员 | finance | 全部财务数据 |
| SALES_STAFF | 销售人员 | self_customer | 本人客户+订单 |
| WORKER | 普通工人 | self | 本人数据 |

### 18.4 菜单权限分配

#### 18.4.1 菜单结构

```
系统管理
├── 用户管理 (system:user)
│   ├── 新增 (system:user:add)
│   ├── 编辑 (system:user:edit)
│   ├── 删除 (system:user:delete)
│   └── 分配角色 (system:user:assign)
├── 角色管理 (system:role)
├── 菜单管理 (system:menu)
└── 数据权限 (system:permission)

基础数据
├── 款式管理 (data:style)
├── 尺码管理 (data:size)
├── 颜色管理 (data:color)
├── 条码规则 (data:barcode)
├── 工厂管理 (data:factory)
├── 车间管理 (data:workshop)
├── 班组管理 (data:group)
└── 员工管理 (data:worker)

生产管理
├── 生产订单 (production:order)
│   ├── 新增 (production:order:add)
│   ├── 编辑 (production:order:edit)
│   ├── 删除 (production:order:delete)
│   └── 审批 (production:order:approve)
├── 生产计划 (production:plan)
├── 生产排程 (production:schedule)
└── 返工管理 (production:rework)

工序管理
├── 工序库 (process:library)
├── 工序配置 (process:config)
├── 任务管理 (process:task)
├── 计件记录 (process:record)
│   ├── 确认 (process:record:confirm)
│   └── 驳回 (process:record:reject)
└── 异常上报 (process:exception)

扎包管理
├── 扎包列表 (bundle:list)
├── 扎包流转 (bundle:flow)
└── 打印标签 (bundle:print)

裁床管理
├── 裁床计划 (cutting:plan)
└── 裁床扎包 (cutting:bundle)

质量管理
├── 首件确认 (quality:first-article)
├── 质检记录 (quality:check)
├── 缺陷记录 (quality:defect)
└── 巡检记录 (quality:patrol)

面料管理
├── 面料档案 (fabric:list)
├── 面料入库 (fabric:inbound)
└── 领料申请 (fabric:requisition)

库存管理
├── 库存查询 (inventory:query)
├── 入库管理 (inventory:inbound)
├── 出库管理 (inventory:outbound)
└── 库存盘点 (inventory:check)

流程管理
├── 流程模板 (flow:template)
└── 流程配置 (flow:config)

工资管理
├── 计件汇总 (salary:summary)
├── 工资单 (salary:sheet)
│   ├── 审核 (salary:sheet:approve)
│   └── 发放 (salary:sheet:pay)
└── 工资查询 (salary:query)

成本管理
├── 成本核算 (cost:calculate)
└── 成本报表 (cost:report)

报表中心
├── 生产进度报表 (report:production)
├── 计件工资报表 (report:piece-work)
├── 质量分析报表 (report:quality)
└── 成本分析报表 (report:cost)
```

### 18.5 角色权限矩阵

| 模块 | 超级管理员 | 工厂管理员 | 车间主任 | 班组长 | 质检员 | 仓库管理员 | 财务 | 工人 |
|------|-----------|-----------|---------|--------|--------|-----------|------|------|
| 系统管理 | ✓ | - | - | - | - | - | - | - |
| 款式管理 | ✓ | ✓ | ✓ | 读 | - | - | 读 | - |
| 尺码颜色 | ✓ | ✓ | ✓ | 读 | - | - | 读 | - |
| 工厂管理 | ✓ | ✓ | 读 | - | - | - | - | - |
| 车间管理 | ✓ | ✓ | ✓ | 读 | - | - | - | - |
| 班组管理 | ✓ | ✓ | ✓ | ✓ | - | - | - | - |
| 员工管理 | ✓ | ✓ | ✓ | ✓ | 读 | 读 | 读 | 读 |
| 生产订单 | ✓ | ✓ | ✓ | 读 | 读 | - | 读 | - |
| 生产计划 | ✓ | ✓ | ✓ | 读 | - | - | 读 | - |
| 工序管理 | ✓ | ✓ | ✓ | ✓ | 读 | - | 读 | - |
| 计件记录 | ✓ | ✓ | ✓ | 确认 | - | - | ✓ | 本人 |
| 扎包管理 | ✓ | ✓ | ✓ | ✓ | ✓ | - | - | 读 |
| 裁床管理 | ✓ | ✓ | ✓ | 读 | - | - | - | - |
| 质量管理 | ✓ | ✓ | ✓ | 读 | ✓ | - | 读 | - |
| 面料管理 | ✓ | ✓ | 读 | - | - | ✓ | 读 | - |
| 库存管理 | ✓ | ✓ | 读 | - | - | ✓ | 读 | - |
| 流程管理 | ✓ | ✓ | 读 | - | - | - | - | - |
| 工资管理 | ✓ | - | - | - | - | - | ✓ | 本人 |
| 成本管理 | ✓ | - | - | - | - | - | ✓ | - |
| 报表中心 | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | - |

### 18.6 数据权限实现

#### 18.6.1 数据权限范围

| 范围编码 | 说明 | SQL条件示例 |
|---------|------|------------|
| all | 全部数据 | 1=1 |
| factory | 本工厂数据 | factory_id = #{currentUser.factoryId} |
| workshop | 本车间数据 | workshop_id = #{currentUser.workshopId} |
| group | 本班组数据 | work_group_id = #{currentUser.groupId} |
| self | 本人数据 | worker_id = #{currentUser.userId} |
| self_customer | 本人客户 | customer_id IN (SELECT id FROM customer WHERE sales_id = #{currentUser.userId}) |

#### 18.6.2 MyBatis拦截器实现

```java
@Intercepts({
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class DataPermissionInterceptor implements Interceptor {
    
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 1. 获取当前用户
        User currentUser = SecurityUtils.getCurrentUser();
        
        // 2. 获取用户角色的数据权限范围
        String dataScope = currentUser.getRole().getDataScope();
        
        // 3. 获取SQL并添加数据权限条件
        String sql = getSql(invocation);
        String newSql = addDataPermissionCondition(sql, dataScope, currentUser);
        
        // 4. 替换SQL执行
        return invocation.proceed();
    }
}
```

### 18.7 字段权限控制

| 字段 | 可见角色 | 脱敏规则 |
|------|---------|---------|
| 成本价 | 财务、超级管理员 | 其他角色返回 null |
| 工资明细 | 本人、财务、超级管理员 | 其他角色返回 null |
| 客户电话 | 销售、超级管理员 | 其他角色显示 138****1234 |
| 客户地址 | 销售、超级管理员 | 其他角色返回 null |
| 供应商价格 | 采购、财务、超级管理员 | 其他角色返回 null |
| 员工银行账号 | 本人、财务、超级管理员 | 其他角色返回 null |

---

## 十九、API接口详细设计

### 19.1 接口规范

#### 19.1.1 基础规范

```
基础路径: /api/v1/
认证方式: JWT Token (Header: Authorization: Bearer {token})
请求格式: JSON
响应格式: JSON
```

#### 19.1.2 统一响应格式

**成功响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1711600000000
}
```

**分页响应：**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "list": [],
    "total": 100,
    "page": 1,
    "pageSize": 20
  },
  "timestamp": 1711600000000
}
```

**失败响应：**
```json
{
  "code": 2003,
  "message": "工序顺序错误",
  "data": {
    "errorCode": "PROCESS_SEQUENCE_INVALID"
  },
  "timestamp": 1711600000000
}
```

---

### 19.2 基础数据接口

#### 19.2.1 款式管理

**款式列表**

```
GET /api/v1/styles
权限: data:style

Query Parameters:
| 参数名     | 类型   | 必填 | 说明               |
|-----------|--------|------|-------------------|
| page      | int    | 否   | 页码，默认1        |
| pageSize  | int    | 否   | 每页条数，默认20   |
| styleNo   | string | 否   | 款号（模糊搜索）   |
| styleName | string | 否   | 款式名称（模糊搜索）|
| season    | string | 否   | 季节              |
| status    | string | 否   | 状态              |
| categoryId| long   | 否   | 品类ID            |
```

**响应示例：**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "list": [
      {
        "styleId": 1,
        "styleNo": "STYLE-001",
        "styleName": "春季休闲裤",
        "year": 2024,
        "season": "spring",
        "seasonName": "春季",
        "categoryId": 10,
        "categoryName": "休闲裤",
        "designerId": 5,
        "designerName": "李设计师",
        "status": "producing",
        "statusName": "已投产",
        "designImage": "/media/styles/STYLE-001/design.jpg",
        "createdAt": "2024-01-15 10:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "pageSize": 20
  }
}
```

**款式详情**

```
GET /api/v1/styles/{id}
权限: data:style
```

**响应示例：**
```json
{
  "code": 200,
  "data": {
    "styleId": 1,
    "styleNo": "STYLE-001",
    "styleName": "春季休闲裤",
    "year": 2024,
    "season": "spring",
    "series": "都市系列",
    "categoryId": 10,
    "categoryName": "休闲裤",
    "brandId": 1,
    "brandName": "衣多多",
    "designerId": 5,
    "designerName": "李设计师",
    "styleType": "casual",
    "targetGender": "male",
    "targetAgeGroup": "25-35",
    "designImage": "/media/styles/STYLE-001/design.jpg",
    "sampleImage": "/media/styles/STYLE-001/sample.jpg",
    "techPack": "/media/styles/STYLE-001/tech.pdf",
    "status": "producing",
    "colorways": [
      { "colorwayId": 1, "colorwayNo": "C01", "colorwayName": "黑色", "mainColor": "黑色" }
    ],
    "createdAt": "2024-01-15 10:00:00",
    "updatedAt": "2024-02-01 14:30:00"
  }
}
```

**新增款式**

```
POST /api/v1/styles
权限: data:style:add

Request:
{
  "styleNo": "STYLE-002",
  "styleName": "夏季T恤",
  "year": 2024,
  "season": "summer",
  "series": "都市系列",
  "categoryId": 5,
  "brandId": 1,
  "designerId": 5,
  "styleType": "casual",
  "targetGender": "unisex",
  "targetAgeGroup": "20-40",
  "designImage": "/media/styles/upload/xxx.jpg"
}
```

**编辑款式**

```
PUT /api/v1/styles/{id}
权限: data:style:edit
```

**删除款式**

```
DELETE /api/v1/styles/{id}
权限: data:style:delete
```

**款式配色列表**

```
GET /api/v1/styles/{id}/colorways
权限: data:style
```

#### 19.2.2 尺码管理

**尺码分类列表**

```
GET /api/v1/size-categories
权限: data:size
```

**响应示例：**
```json
{
  "code": 200,
  "data": [
    {
      "categoryId": 1,
      "name": "男装尺码",
      "code": "MALE",
      "description": "男装通用尺码",
      "sortOrder": 1,
      "options": [
        { "optionId": 1, "name": "S", "code": "S", "sortOrder": 1 },
        { "optionId": 2, "name": "M", "code": "M", "sortOrder": 2 },
        { "optionId": 3, "name": "L", "code": "L", "sortOrder": 3 },
        { "optionId": 4, "name": "XL", "code": "XL", "sortOrder": 4 },
        { "optionId": 5, "name": "XXL", "code": "XXL", "sortOrder": 5 }
      ]
    }
  ]
}
```

**新增尺码分类**

```
POST /api/v1/size-categories
权限: data:size:add

Request:
{
  "name": "童装尺码",
  "code": "KIDS",
  "description": "童装通用尺码",
  "options": [
    { "name": "100", "code": "100", "sortOrder": 1 },
    { "name": "110", "code": "110", "sortOrder": 2 },
    { "name": "120", "code": "120", "sortOrder": 3 }
  ]
}
```

#### 19.2.3 工厂/车间/班组

**工厂列表**

```
GET /api/v1/factories
权限: data:factory

Query Parameters:
| 参数名      | 类型   | 必填 | 说明                |
|------------|--------|------|--------------------|
| factoryType| string | 否   | 类型(own/outsource) |
| isActive   | bool   | 否   | 是否启用            |
```

**响应示例：**
```json
{
  "code": 200,
  "data": [
    {
      "factoryId": 1,
      "name": "总厂",
      "code": "F001",
      "factoryType": "own",
      "factoryTypeName": "自有工厂",
      "address": "广东省广州市xx区xx路",
      "contact": "张经理",
      "phone": "138****1234",
      "workshopCount": 5,
      "workerCount": 200,
      "isActive": true
    }
  ]
}
```

**车间列表**

```
GET /api/v1/workshops
权限: data:workshop

Query Parameters:
| 参数名       | 类型   | 必填 | 说明     |
|-------------|--------|------|---------|
| factoryId   | long   | 否   | 工厂ID  |
| workshopType| string | 否   | 车间类型 |
```

**班组列表**

```
GET /api/v1/work-groups
权限: data:group

Query Parameters:
| 参数名     | 类型   | 必填 | 说明     |
|-----------|--------|------|---------|
| factoryId | long   | 否   | 工厂ID  |
| workshopId| long   | 否   | 车间ID  |
| groupType | string | 否   | 班组类型 |
```

---

### 19.3 工序管理接口

#### 19.3.1 工序库

**工序列表**

```
GET /api/v1/processes
权限: process:library

Query Parameters:
| 参数名          | 类型   | 必填 | 说明               |
|----------------|--------|------|-------------------|
| page           | int    | 否   | 页码              |
| pageSize       | int    | 否   | 每页条数          |
| keyword        | string | 否   | 关键词（名称/编码）|
| processCategory| string | 否   | 工序大类          |
| isActive       | bool   | 否   | 是否启用          |
```

**响应示例：**
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "processId": 1,
        "code": "PROC001",
        "name": "裁剪",
        "processType": "main",
        "processCategory": "cutting",
        "processCategoryName": "裁剪",
        "equipmentType": "裁床",
        "difficultyLevel": 2,
        "standardTime": 60,
        "defaultPrice1": 1.00,
        "defaultPrice2": 1.20,
        "defaultPrice3": 1.50,
        "isActive": true
      }
    ],
    "total": 50,
    "page": 1,
    "pageSize": 20
  }
}
```

**工序详情**

```
GET /api/v1/processes/{id}
权限: process:library
```

**新增工序**

```
POST /api/v1/processes
权限: process:library:add

Request:
{
  "code": "PROC020",
  "name": "刺绣",
  "processType": "special",
  "processCategory": "special",
  "equipmentType": "刺绣机",
  "difficultyLevel": 3,
  "standardTime": 120,
  "defaultPrice1": 3.00,
  "defaultPrice2": 4.00,
  "defaultPrice3": 5.00,
  "skillRequirement": "熟练操作刺绣设备，能看懂图案",
  "qualityCheckpoints": ["图案清晰", "位置准确", "无线头"]
}
```

**编辑工序**

```
PUT /api/v1/processes/{id}
权限: process:library:edit
```

**删除工序**

```
DELETE /api/v1/processes/{id}
权限: process:library:delete
```

#### 19.3.2 货品工序配置

**配置列表**

```
GET /api/v1/product-process-configs
权限: process:config

Query Parameters:
| 参数名    | 类型 | 必填 | 说明   |
|----------|------|------|-------|
| productId| long | 是   | 货品ID|
```

**响应示例：**
```json
{
  "code": 200,
  "data": {
    "productId": 1,
    "productName": "春季休闲裤",
    "productNo": "STYLE-001",
    "configs": [
      {
        "configId": 1,
        "processId": 1,
        "processCode": "PROC001",
        "processName": "裁剪",
        "price1": 1.00,
        "price2": 1.20,
        "price3": 1.50,
        "sortOrder": 1,
        "isEnabled": true
      }
    ],
    "totalPrice1": 10.00,
    "totalPrice2": 12.50,
    "totalPrice3": 15.00
  }
}
```

**批量保存配置**

```
POST /api/v1/product-process-configs/batch
权限: process:config:save

Request:
{
  "productId": 1,
  "configs": [
    { "processId": 1, "price1": 1.00, "price2": 1.20, "price3": 1.50, "sortOrder": 1, "isEnabled": true },
    { "processId": 5, "price1": 2.00, "price2": 2.50, "price3": 3.00, "sortOrder": 2, "isEnabled": true }
  ]
}
```

#### 19.3.3 计件记录

**计件记录列表**

```
GET /api/v1/process-records
权限: process:record

Query Parameters:
| 参数名           | 类型   | 必填 | 说明                              |
|-----------------|--------|------|----------------------------------|
| page            | int    | 否   | 页码                              |
| pageSize        | int    | 否   | 每页条数                          |
| workerId        | long   | 否   | 员工ID                            |
| bundleNo        | string | 否   | 扎包号（模糊搜索）                 |
| processId       | long   | 否   | 工序ID                            |
| status          | string | 否   | 状态(pending/confirmed/rejected)  |
| settlementStatus| string | 否   | 结算状态(unsettled/settled)       |
| recordType      | string | 否   | 记录类型(normal/rework/deduction) |
| startDate       | date   | 否   | 开始日期                          |
| endDate         | date   | 否   | 结束日期                          |
```

**响应示例：**
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "recordId": 1001,
        "recordNo": "JJ202403280001",
        "bundleId": 201,
        "bundleNo": "ZB20240328001",
        "processId": 5,
        "processName": "缝制-前幅",
        "workerId": 101,
        "workerName": "张三",
        "quantity": 20,
        "unitPrice": 2.40,
        "amount": 48.00,
        "recordType": "normal",
        "status": "confirmed",
        "settlementStatus": "unsettled",
        "scanTime": "2024-03-28 10:30:00",
        "confirmTime": "2024-03-28 11:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "pageSize": 20
  }
}
```

**扫码计件**

```
POST /api/v1/process-records/scan
权限: process:record:add

Request (单人计件):
{
  "requestId": "550e8400-e29b-41d4-a716-446655440000",
  "bundleNo": "ZB20240328001",
  "processId": 5,
  "skuId": 101,
  "quantity": 20,
  "deviceId": "PDA-001",
  "location": "缝制车间-A线",
  "scanTime": "2024-03-28T10:30:00",
  "isTeamWork": false
}

Request (团队计件):
{
  "requestId": "550e8400-e29b-41d4-a716-446655440001",
  "bundleNo": "ZB20240328001",
  "processId": 3,
  "skuId": 101,
  "quantity": 50,
  "deviceId": "PDA-001",
  "scanTime": "2024-03-28T14:00:00",
  "isTeamWork": true,
  "teamType": "main_assistant",
  "teamMembers": [
    { "workerId": 101, "shareRatio": 70 },
    { "workerId": 102, "shareRatio": 30 }
  ]
}
```

**响应示例（成功）：**
```json
{
  "code": 200,
  "message": "计件成功",
  "data": {
    "recordId": 1001,
    "recordNo": "JJ202403280001",
    "bundleNo": "ZB20240328001",
    "processId": 5,
    "processName": "缝制-前幅",
    "workerId": 101,
    "workerName": "张三",
    "quantity": 20,
    "basePrice": 2.00,
    "premiumRatio": 20.00,
    "unitPrice": 2.40,
    "amount": 48.00,
    "priceLevel": "price2",
    "skillLevel": "standard",
    "timePeriod": "normal",
    "status": "pending",
    "remainingQuantity": 80,
    "workflowProgressed": true,
    "nextProcess": "锁钉"
  }
}
```

**响应示例（失败 - 工序顺序错误）：**
```json
{
  "code": 2003,
  "message": "工序顺序错误，请先完成【裁剪】工序",
  "data": {
    "errorCode": "PROCESS_SEQUENCE_INVALID",
    "currentProcess": "缝制-前幅",
    "expectedProcess": "裁剪",
    "completedProcesses": [],
    "pendingProcesses": ["裁剪", "缝制-前幅", "锁钉", "整烫"]
  }
}
```

**响应示例（失败 - 数量超出）：**
```json
{
  "code": 2004,
  "message": "数量超出可计件数量",
  "data": {
    "errorCode": "QUANTITY_OVERFLOW",
    "requestedQuantity": 30,
    "remainingQuantity": 20,
    "maxAllowed": 20
  }
}
```

**确认计件**

```
POST /api/v1/process-records/{id}/confirm
权限: process:record:confirm

Request:
{
  "remark": "确认无误"
}

Response:
{
  "code": 200,
  "message": "确认成功",
  "data": {
    "recordId": 1001,
    "status": "confirmed",
    "confirmTime": "2024-03-28T11:00:00",
    "confirmedBy": 201
  }
}
```

**驳回计件**

```
POST /api/v1/process-records/{id}/reject
权限: process:record:reject

Request:
{
  "reason": "数量有误，请核实后重新提交"
}
```

**我的计件记录**

```
GET /api/v1/process-records/mine
权限: 无需权限（仅查询本人数据）

Query Parameters:
| 参数名    | 类型   | 必填 | 说明     |
|----------|--------|------|---------|
| page     | int    | 否   | 页码    |
| pageSize | int    | 否   | 每页条数|
| startDate| date   | 否   | 开始日期|
| endDate  | date   | 否   | 结束日期|
| status   | string | 否   | 状态    |

Response:
{
  "code": 200,
  "data": {
    "list": [...],
    "total": 50,
    "page": 1,
    "pageSize": 20,
    "summary": {
      "totalQuantity": 500,
      "totalAmount": 1200.00,
      "pendingCount": 5,
      "confirmedCount": 45
    }
  }
}
```

---

### 19.4 扎包管理接口

#### 19.4.1 扎包列表

```
GET /api/v1/bundles
权限: bundle:list

Query Parameters:
| 参数名      | 类型   | 必填 | 说明     |
|------------|--------|------|---------|
| page       | int    | 否   | 页码    |
| pageSize   | int    | 否   | 每页条数|
| bundleNo   | string | 否   | 扎包号  |
| orderId    | long   | 否   | 订单ID  |
| status     | string | 否   | 状态    |
| workGroupId| long   | 否   | 班组ID  |
```

**响应示例：**
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "bundleId": 201,
        "bundleNo": "ZB20240328001",
        "orderId": 101,
        "orderNo": "PO20240328001",
        "styleNo": "STYLE-001",
        "styleName": "春季休闲裤",
        "size": "L",
        "color": "黑色",
        "quantity": 100,
        "completedQuantity": 20,
        "currentProcessId": 5,
        "currentProcessName": "缝制-前幅",
        "workGroupId": 1,
        "workGroupName": "缝制A组",
        "status": "in_production",
        "statusName": "生产中",
        "createdAt": "2024-03-28 08:00:00"
      }
    ],
    "total": 50,
    "page": 1,
    "pageSize": 20
  }
}
```

#### 19.4.2 扎包详情

```
GET /api/v1/bundles/{id}
权限: bundle:list
```

#### 19.4.3 按扎包号查询

```
GET /api/v1/bundles/by-no/{bundleNo}
权限: bundle:list
说明: 用于小程序扫码查询

Response:
{
  "code": 200,
  "data": {
    "bundleId": 201,
    "bundleNo": "ZB20240328001",
    "orderNo": "PO20240328001",
    "styleNo": "STYLE-001",
    "styleName": "春季休闲裤",
    "size": "L",
    "color": "黑色",
    "quantity": 100,
    "status": "in_production",
    "currentProcess": {
      "processId": 5,
      "processName": "缝制-前幅",
      "sequence": 2,
      "completedQuantity": 20,
      "remainingQuantity": 80
    },
    "firstArticlePassed": true,
    "executableProcesses": [
      { "processId": 5, "processName": "缝制-前幅", "isCurrent": true, "canExecute": true },
      { "processId": 6, "processName": "缝制-后幅", "isCurrent": false, "canExecute": false }
    ],
    "processHistory": [
      { "processId": 1, "processName": "裁剪", "completedAt": "2024-03-28 09:00:00", "completedBy": "张裁床" }
    ]
  }
}
```

#### 19.4.4 扎包流转信息

```
GET /api/v1/bundles/{id}/flow
权限: bundle:flow

Response:
{
  "code": 200,
  "data": {
    "bundleId": 201,
    "bundleNo": "ZB20240328001",
    "templateId": 1,
    "templateName": "T恤生产流程",
    "processes": [
      {
        "processId": 1,
        "processName": "裁剪",
        "sequence": 1,
        "status": "completed",
        "targetQuantity": 100,
        "completedQuantity": 100,
        "completedAt": "2024-03-28 09:00:00",
        "completedBy": "张裁床"
      },
      {
        "processId": 5,
        "processName": "缝制-前幅",
        "sequence": 2,
        "status": "in_progress",
        "targetQuantity": 100,
        "completedQuantity": 20,
        "remainingQuantity": 80
      }
    ],
    "gateways": [
      {
        "gatewayType": "first_article",
        "passed": true,
        "passedAt": "2024-03-28 08:30:00",
        "passedBy": "王质检"
      }
    ]
  }
}
```

#### 19.4.5 生成二维码

```
GET /api/v1/bundles/{id}/qrcode
权限: bundle:print

Response:
{
  "code": 200,
  "data": {
    "bundleId": 201,
    "bundleNo": "ZB20240328001",
    "qrCodeUrl": "/media/qr/bundles/ZB20240328001.png",
    "qrData": {
      "type": "bundle",
      "bundleNo": "ZB20240328001",
      "orderNo": "PO20240328001",
      "styleNo": "STYLE-001",
      "size": "L",
      "color": "黑色",
      "quantity": 100
    }
  }
}
```

#### 19.4.6 打印标签

```
GET /api/v1/bundles/{id}/print
权限: bundle:print

Query Parameters:
| 参数名     | 类型 | 必填 | 说明         |
|-----------|------|------|-------------|
| templateId| long | 否   | 打印模板ID   |
| copies    | int  | 否   | 打印份数，默认1|
```

#### 19.4.7 扎包交接

```
POST /api/v1/bundles/handover
权限: bundle:flow

Request:
{
  "bundleIds": [201, 202, 203],
  "fromWorkGroupId": 1,
  "toWorkGroupId": 2,
  "handoverType": "normal",
  "remark": "正常流转"
}

Response:
{
  "code": 200,
  "message": "交接成功",
  "data": {
    "successCount": 3,
    "failedCount": 0,
    "details": [
      { "bundleId": 201, "bundleNo": "ZB20240328001", "success": true }
    ]
  }
}
```

#### 19.4.8 扎包可执行工序

```
GET /api/v1/bundles/{id}/executable-processes
权限: bundle:flow

Response:
{
  "code": 200,
  "data": {
    "bundleId": 201,
    "bundleNo": "ZB20240328001",
    "processes": [
      {
        "processId": 5,
        "processName": "缝制-前幅",
        "sequence": 2,
        "isCurrent": true,
        "canExecute": true,
        "targetQuantity": 100,
        "completedQuantity": 20,
        "remainingQuantity": 80,
        "prices": {
          "price1": 2.00,
          "price2": 2.50,
          "price3": 3.00
        }
      }
    ],
    "blockReason": null
  }
}
```

---

### 19.5 生产管理接口

#### 19.5.1 生产订单

**订单列表**

```
GET /api/v1/production-orders
权限: production:order

Query Parameters:
| 参数名     | 类型   | 必填 | 说明     |
|-----------|--------|------|---------|
| page      | int    | 否   | 页码    |
| pageSize  | int    | 否   | 每页条数|
| orderNo   | string | 否   | 订单号  |
| styleNo   | string | 否   | 款号    |
| status    | string | 否   | 状态    |
| factoryId | long   | 否   | 工厂ID  |
| customerId| long   | 否   | 客户ID  |
| startDate | date   | 否   | 开始日期|
| endDate   | date   | 否   | 结束日期|
```

**响应示例：**
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "orderId": 101,
        "orderNo": "PO20240328001",
        "styleId": 1,
        "styleNo": "STYLE-001",
        "styleName": "春季休闲裤",
        "customerId": 10,
        "customerName": "XX服装公司",
        "quantity": 1000,
        "completedQuantity": 200,
        "deadline": "2024-04-15",
        "priority": "high",
        "priorityName": "高",
        "factoryId": 1,
        "factoryName": "总厂",
        "status": "in_production",
        "statusName": "生产中",
        "progress": 20.0,
        "sourceOrderId": 501,
        "sourceOrderNo": "SO20240320001",
        "createdAt": "2024-03-20 10:00:00"
      }
    ],
    "total": 50,
    "page": 1,
    "pageSize": 20
  }
}
```

**订单详情**

```
GET /api/v1/production-orders/{id}
权限: production:order

Response:
{
  "code": 200,
  "data": {
    "orderId": 101,
    "orderNo": "PO20240328001",
    "styleId": 1,
    "styleNo": "STYLE-001",
    "styleName": "春季休闲裤",
    "customerId": 10,
    "customerName": "XX服装公司",
    "quantity": 1000,
    "completedQuantity": 200,
    "deadline": "2024-04-15",
    "priority": "high",
    "factoryId": 1,
    "factoryName": "总厂",
    "sourceOrderId": 501,
    "sourceOrderNo": "SO20240320001",
    "status": "in_production",
    "remark": "加急订单",
    "sizeRatios": [
      { "size": "S", "color": "黑色", "ratio": 20.0, "quantity": 200 },
      { "size": "M", "color": "黑色", "ratio": 30.0, "quantity": 300 },
      { "size": "L", "color": "黑色", "ratio": 30.0, "quantity": 300 },
      { "size": "XL", "color": "黑色", "ratio": 20.0, "quantity": 200 }
    ],
    "bomId": 1,
    "bomNo": "BOM20240328001",
    "templateId": 1,
    "templateName": "休闲裤生产流程",
    "createdAt": "2024-03-20 10:00:00",
    "createdBy": 1,
    "createdByName": "管理员"
  }
}
```

**新增订单**

```
POST /api/v1/production-orders
权限: production:order:add

Request:
{
  "styleId": 1,
  "customerId": 10,
  "quantity": 1000,
  "deadline": "2024-04-15",
  "priority": "high",
  "factoryId": 1,
  "sourceOrderId": 501,
  "bomId": 1,
  "templateId": 1,
  "sizeRatios": [
    { "size": "S", "color": "黑色", "ratio": 20.0, "quantity": 200 },
    { "size": "M", "color": "黑色", "ratio": 30.0, "quantity": 300 },
    { "size": "L", "color": "黑色", "ratio": 30.0, "quantity": 300 },
    { "size": "XL", "color": "黑色", "ratio": 20.0, "quantity": 200 }
  ],
  "remark": "加急订单"
}
```

**编辑订单**

```
PUT /api/v1/production-orders/{id}
权限: production:order:edit
```

**删除订单**

```
DELETE /api/v1/production-orders/{id}
权限: production:order:delete
```

**订单进度**

```
GET /api/v1/production-orders/{id}/progress
权限: production:order

Response:
{
  "code": 200,
  "data": {
    "orderId": 101,
    "orderNo": "PO20240328001",
    "totalQuantity": 1000,
    "completedQuantity": 200,
    "progress": 20.0,
    "processProgress": [
      {
        "processId": 1,
        "processName": "裁剪",
        "targetQuantity": 1000,
        "completedQuantity": 1000,
        "progress": 100.0
      },
      {
        "processId": 5,
        "processName": "缝制-前幅",
        "targetQuantity": 1000,
        "completedQuantity": 200,
        "progress": 20.0
      }
    ],
    "bundleCount": 50,
    "completedBundleCount": 10,
    "abnormalBundleCount": 2,
    "estimatedCompletionDate": "2024-04-10"
  }
}
```

#### 19.5.2 返工管理

**返工单列表**

```
GET /api/v1/rework-orders
权限: production:rework

Query Parameters:
| 参数名   | 类型   | 必填 | 说明     |
|---------|--------|------|---------|
| page    | int    | 否   | 页码    |
| pageSize| int    | 否   | 每页条数|
| bundleId| long   | 否   | 扎包ID  |
| status  | string | 否   | 状态    |
```

**响应示例：**
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "reworkId": 1,
        "reworkNo": "RW20240328001",
        "bundleId": 201,
        "bundleNo": "ZB20240328001",
        "qualityCheckId": 10,
        "defectTypes": ["跳线", "线头"],
        "reworkQuantity": 5,
        "reworkProcessId": 5,
        "reworkProcessName": "缝制-前幅",
        "assignedTo": 101,
        "assignedToName": "张三",
        "deadline": "2024-03-29",
        "completedQuantity": 0,
        "status": "assigned",
        "statusName": "已分配",
        "createdAt": "2024-03-28 14:00:00"
      }
    ],
    "total": 10,
    "page": 1,
    "pageSize": 20
  }
}
```

**新增返工单**

```
POST /api/v1/rework-orders
权限: production:rework:add

Request:
{
  "qualityCheckId": 10,
  "bundleId": 201,
  "defectTypes": ["跳线", "线头"],
  "reworkQuantity": 5,
  "reworkProcessId": 5,
  "assignedTo": 101,
  "deadline": "2024-03-29",
  "remark": "需重新缝制"
}
```

**返工单详情**

```
GET /api/v1/rework-orders/{id}
权限: production:rework

Response:
{
  "code": 200,
  "data": {
    "reworkId": 1,
    "reworkNo": "RW20240328001",
    "bundleId": 201,
    "bundleNo": "ZB20240328001",
    "qualityCheckId": 10,
    "defectTypes": ["跳线", "线头"],
    "reworkQuantity": 5,
    "reworkProcessId": 5,
    "reworkProcessName": "缝制-前幅",
    "assignedTo": 101,
    "assignedToName": "张三",
    "deadline": "2024-03-29",
    "completedQuantity": 0,
    "status": "assigned",
    "remark": "需重新缝制",
    "pieceWork": {
      "originalWorkerId": 102,
      "originalWorkerName": "李四",
      "originalAmount": 10.00,
      "deductionRatio": 50.0,
      "deductionAmount": 5.00,
      "deductionRecordId": 5001,
      "reworkWorkerId": 101,
      "reworkWorkerName": "张三",
      "reworkAmount": 12.00,
      "reworkRecordId": 5002
    },
    "createdAt": "2024-03-28 14:00:00"
  }
}
```

---

### 19.6 接口统计

#### 19.6.1 生产模块接口（本章）

| 模块 | 接口数量 |
|------|---------|
| 基础数据 | 11 |
| 工序管理 | 12 |
| 扎包管理 | 8 |
| 生产管理 | 12 |
| **小计** | **43** |

#### 19.6.2 全系统接口总览

| 模块 | 接口数量 | 所在章节 |
|------|---------|----------|
| 基础数据 | 11 | 第十九章 |
| 工序管理 | 12 | 第十九章 |
| 扎包管理 | 8 | 第十九章 |
| 生产管理 | 12 | 第十九章 |
| 客户管理 | 6 | 第二十八章 |
| 销售报价 | 7 | 第二十八章 |
| 销售订单 | 9 | 第二十八章 |
| 销售发货 | 5 | 第二十八章 |
| 销售退货 | 5 | 第二十八章 |
| 供应商管理 | 7 | 第二十九章 |
| 采购申请 | 7 | 第二十九章 |
| 采购订单 | 8 | 第二十九章 |
| 采购入库 | 4 | 第二十九章 |
| 采购退货 | 5 | 第二十九章 |
| 银行账户 | 5 | 第三十章 |
| 收款管理 | 6 | 第三十章 |
| 付款管理 | 7 | 第三十章 |
| 对账管理 | 6 | 第三十章 |
| 库存盘点 | 7 | 第三十二章 |
| 库存调拨 | 6 | 第三十二章 |
| 打印服务 | 6 | 第三十七章 |
| 导入导出 | 6 | 第三十八章 |
| **合计** | **约150个** | - |

---

## 20. 前端页面交互设计

### 20.1 设计规范

#### 20.1.1 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.x | 前端框架 |
| Element Plus | 2.x | UI组件库 |
| Vue Router | 4.x | 路由管理 |
| Pinia | 2.x | 状态管理 |
| Axios | 1.x | HTTP请求 |
| Vite | 5.x | 构建工具 |

#### 20.1.2 页面结构规范

**列表页结构：**
```
el-card (shadow="never")
├── toolbar (工具栏)
│   ├── left-actions (左侧操作按钮)
│   └── right-search (右侧搜索区)
├── el-table (表格)
└── el-pagination (分页)
```

**新增/编辑页结构：**
```
div.app-container
├── div.sticky-header (固定头部)
└── el-card.box-card
    └── el-form (label-position="top")
```

#### 20.1.3 样式类规范

| 类名 | 用途 |
|------|------|
| app-container | 页面容器 |
| box-card | 卡片容器 |
| sticky-header | 固定头部 |
| toolbar | 工具栏 |
| search-form-row | 搜索行 |
| form-section | 表单分区 |

#### 20.1.4 权限指令

使用 `v-perm` 指令控制按钮权限：
```html
<el-button v-perm="'process:library:add'">新增</el-button>
<el-button v-perm="'process:library:edit'">编辑</el-button>
<el-button v-perm="'process:library:delete'">删除</el-button>
```

### 20.2 页面清单

#### 20.2.1 工序管理模块

| 页面名称 | 文件路径 | 权限标识 |
|----------|----------|----------|
| 工序库列表 | frontend/src/views/process/library/index.vue | process:library:list |
| 工序新增/编辑 | frontend/src/views/process/library/add.vue | process:library:add/edit |
| 工序配置列表 | frontend/src/views/process/config/index.vue | process:config:list |
| 工序配置详情 | frontend/src/views/process/config/detail.vue | process:config:detail |
| 计件记录列表 | frontend/src/views/process/record/index.vue | process:record:list |
| 计件记录详情 | frontend/src/views/process/record/detail.vue | process:record:detail |

#### 20.2.2 扎包管理模块

| 页面名称 | 文件路径 | 权限标识 |
|----------|----------|----------|
| 扎包列表 | frontend/src/views/bundle/index.vue | bundle:list |
| 扎包详情 | frontend/src/views/bundle/detail.vue | bundle:detail |
| 扎包流转记录 | frontend/src/views/bundle/flow.vue | bundle:flow |

#### 20.2.3 生产管理模块

| 页面名称 | 文件路径 | 权限标识 |
|----------|----------|----------|
| 生产订单列表 | frontend/src/views/production/order/index.vue | production:order:list |
| 生产订单新增 | frontend/src/views/production/order/add.vue | production:order:add |
| 生产订单详情 | frontend/src/views/production/order/detail.vue | production:order:detail |
| 生产进度看板 | frontend/src/views/production/dashboard/index.vue | production:dashboard |
| 质检记录列表 | frontend/src/views/production/quality/index.vue | production:quality:list |
| 返工记录列表 | frontend/src/views/production/rework/index.vue | production:rework:list |

#### 20.2.4 计件工资模块

| 页面名称 | 文件路径 | 权限标识 |
|----------|----------|----------|
| 计件工资汇总 | frontend/src/views/wage/summary/index.vue | wage:summary |
| 计件工资明细 | frontend/src/views/wage/detail/index.vue | wage:detail |
| 工资结算单 | frontend/src/views/wage/settlement/index.vue | wage:settlement |

#### 20.2.5 报表分析模块

| 页面名称 | 文件路径 | 权限标识 |
|----------|----------|----------|
| 生产效率报表 | frontend/src/views/report/efficiency/index.vue | report:efficiency |
| 工序产量报表 | frontend/src/views/report/output/index.vue | report:output |
| 质量分析报表 | frontend/src/views/report/quality/index.vue | report:quality |

### 20.3 页面详细设计

#### 20.3.1 工序库列表页

**文件路径：** `frontend/src/views/process/library/index.vue`

**页面结构：**
```vue
<template>
  <el-card shadow="never">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="left-actions">
        <el-button type="primary" v-perm="'process:library:add'" @click="handleAdd">
          新增工序
        </el-button>
        <el-button v-perm="'process:library:import'" @click="handleImport">
          导入
        </el-button>
        <el-button v-perm="'process:library:export'" @click="handleExport">
          导出
        </el-button>
      </div>
      <div class="right-search">
        <el-select v-model="queryParams.category" placeholder="工序分类" clearable>
          <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value"/>
        </el-select>
        <el-input v-model="queryParams.keyword" placeholder="工序名称/编码" clearable @keyup.enter="handleQuery"/>
        <el-button type="primary" @click="handleQuery">查询</el-button>
      </div>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading">
      <el-table-column prop="code" label="工序编码" width="120"/>
      <el-table-column prop="name" label="工序名称" width="150"/>
      <el-table-column prop="category" label="工序分类" width="100">
        <template #default="{row}">
          <el-tag>{{ getCategoryName(row.category) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="unit" label="计件单位" width="80"/>
      <el-table-column prop="standardPrice" label="标准单价" width="100"/>
      <el-table-column prop="estimatedTime" label="标准工时(分钟)" width="120"/>
      <el-table-column prop="description" label="工序说明" show-overflow-tooltip/>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{row}">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{row}">
          <el-button link type="primary" v-perm="'process:library:edit'" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="primary" v-perm="'process:library:price'" @click="handlePriceHistory(row)">单价历史</el-button>
          <el-button link type="danger" v-perm="'process:library:delete'" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="queryParams.page"
      v-model:page-size="queryParams.size"
      :total="total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleQuery"
      @current-change="handleQuery"
    />
  </el-card>
</template>
```

**交互说明：**

| 操作 | 触发条件 | 行为 |
|------|----------|------|
| 新增 | 点击新增按钮 | 跳转到 `/process/library/add` |
| 编辑 | 点击编辑按钮 | 跳转到 `/process/library/add?id={id}` |
| 删除 | 点击删除按钮 | 弹出确认框，确认后删除 |
| 单价历史 | 点击单价历史按钮 | 弹出抽屉显示单价变更历史 |
| 导入 | 点击导入按钮 | 弹出导入对话框 |
| 导出 | 点击导出按钮 | 下载Excel文件 |
| 查询 | 点击查询/回车 | 刷新列表 |
| 筛选 | 选择工序分类 | 自动刷新列表 |

#### 20.3.2 工序新增/编辑页

**文件路径：** `frontend/src/views/process/library/add.vue`

**页面结构：**
```vue
<template>
  <div class="app-container">
    <!-- 固定头部 -->
    <div class="sticky-header">
      <el-page-header @back="goBack">
        <template #content>
          <span class="text-large font-600 mr-3">{{ isEdit ? '编辑工序' : '新增工序' }}</span>
        </template>
        <template #extra>
          <div class="flex items-center">
            <el-button @click="goBack">取消</el-button>
            <el-button type="primary" @click="handleSubmit" :loading="submitting">保存</el-button>
          </div>
        </template>
      </el-page-header>
    </div>

    <!-- 表单卡片 -->
    <el-card class="box-card">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <!-- 基本信息 -->
        <div class="form-section">
          <h4>基本信息</h4>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="工序编码" prop="code">
                <el-input v-model="form.code" placeholder="自动生成" :disabled="true"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="工序名称" prop="name">
                <el-input v-model="form.name" placeholder="请输入工序名称"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="工序分类" prop="category">
                <el-select v-model="form.category" placeholder="请选择工序分类">
                  <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value"/>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="计件单位" prop="unit">
                <el-select v-model="form.unit" placeholder="请选择计件单位">
                  <el-option label="件" value="piece"/>
                  <el-option label="包" value="bundle"/>
                  <el-option label="打" value="dozen"/>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="标准单价(元)" prop="standardPrice">
                <el-input-number v-model="form.standardPrice" :min="0" :precision="2" placeholder="请输入标准单价"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="标准工时(分钟)" prop="estimatedTime">
                <el-input-number v-model="form.estimatedTime" :min="0" placeholder="请输入标准工时"/>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 工序说明 -->
        <div class="form-section">
          <h4>工序说明</h4>
          <el-form-item prop="description">
            <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入工序说明"/>
          </el-form-item>
        </div>

        <!-- 质量要求 -->
        <div class="form-section">
          <h4>质量要求</h4>
          <el-form-item prop="qualityRequirements">
            <el-input v-model="form.qualityRequirements" type="textarea" :rows="3" placeholder="请输入质量要求"/>
          </el-form-item>
        </div>

        <!-- 附件上传 -->
        <div class="form-section">
          <h4>工序图片/视频</h4>
          <el-upload
            v-model:file-list="form.attachments"
            action="/api/upload"
            list-type="picture-card"
            :limit="5"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </div>
      </el-form>
    </el-card>
  </div>
</template>
```

#### 20.3.3 工序配置列表页

**文件路径：** `frontend/src/views/process/config/index.vue`

**页面结构：**
```vue
<template>
  <el-card shadow="never">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="left-actions">
        <el-button type="primary" v-perm="'process:config:add'" @click="handleAdd">新增配置</el-button>
      </div>
      <div class="right-search">
        <el-select v-model="queryParams.factoryId" placeholder="选择工厂" clearable>
          <el-option v-for="item in factoryOptions" :key="item.id" :label="item.name" :value="item.id"/>
        </el-select>
        <el-input v-model="queryParams.keyword" placeholder="款号/品名" clearable @keyup.enter="handleQuery"/>
        <el-button type="primary" @click="handleQuery">查询</el-button>
      </div>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading">
      <el-table-column prop="styleCode" label="款号" width="120"/>
      <el-table-column prop="styleName" label="品名" width="150"/>
      <el-table-column prop="factoryName" label="工厂" width="120"/>
      <el-table-column prop="processCount" label="工序数" width="80"/>
      <el-table-column prop="totalPrice" label="工序总价" width="100">
        <template #default="{row}">
          ¥{{ row.totalPrice.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{row}">
          <el-tag :type="getStatusType(row.status)">{{ getStatusName(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="updatedAt" label="更新时间" width="160"/>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{row}">
          <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
          <el-button link type="primary" v-perm="'process:config:edit'" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="primary" v-perm="'process:config:copy'" @click="handleCopy(row)">复制</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="queryParams.page"
      v-model:page-size="queryParams.size"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
    />
  </el-card>
</template>
```

#### 20.3.4 工序配置详情页

**文件路径：** `frontend/src/views/process/config/detail.vue`

**页面结构：**
```vue
<template>
  <div class="app-container">
    <!-- 固定头部 -->
    <div class="sticky-header">
      <el-page-header @back="goBack">
        <template #content>
          <span class="text-large font-600 mr-3">工序配置详情</span>
        </template>
        <template #extra>
          <el-button type="primary" v-perm="'process:config:edit'" @click="handleEdit">编辑</el-button>
        </template>
      </el-page-header>
    </div>

    <!-- 基本信息 -->
    <el-card class="box-card" style="margin-bottom: 16px;">
      <template #header>
        <span>基本信息</span>
      </template>
      <el-descriptions :column="4" border>
        <el-descriptions-item label="款号">{{ config.styleCode }}</el-descriptions-item>
        <el-descriptions-item label="品名">{{ config.styleName }}</el-descriptions-item>
        <el-descriptions-item label="工厂">{{ config.factoryName }}</el-descriptions-item>
        <el-descriptions-item label="工序数">{{ config.processCount }}</el-descriptions-item>
        <el-descriptions-item label="工序总价">¥{{ config.totalPrice.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(config.status)">{{ getStatusName(config.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建人">{{ config.createdBy }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ config.createdAt }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 工序列表 -->
    <el-card class="box-card">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>工序列表</span>
          <el-button type="primary" size="small" v-perm="'process:config:edit'" @click="handleAddProcess">
            添加工序
          </el-button>
        </div>
      </template>
      <el-table :data="config.processes" border>
        <el-table-column type="index" label="序号" width="60"/>
        <el-table-column prop="processCode" label="工序编码" width="100"/>
        <el-table-column prop="processName" label="工序名称" width="120"/>
        <el-table-column prop="category" label="分类" width="80">
          <template #default="{row}">
            <el-tag size="small">{{ getCategoryName(row.category) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="60"/>
        <el-table-column prop="price" label="单价(元)" width="100"/>
        <el-table-column prop="estimatedTime" label="工时(分钟)" width="100"/>
        <el-table-column prop="remark" label="备注"/>
        <el-table-column label="操作" width="100" v-if="hasEditPerm">
          <template #default="{row, $index}">
            <el-button link type="primary" @click="handleEditProcess(row, $index)">编辑</el-button>
            <el-button link type="danger" @click="handleDeleteProcess($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>
```

#### 20.3.5 计件记录列表页

**文件路径：** `frontend/src/views/process/record/index.vue`

**页面结构：**
```vue
<template>
  <el-card shadow="never">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="left-actions">
        <el-button type="primary" v-perm="'process:record:add'" @click="handleAdd">新增计件</el-button>
        <el-button v-perm="'process:record:batch'" @click="handleBatchAdd">批量录入</el-button>
        <el-button v-perm="'process:record:export'" @click="handleExport">导出</el-button>
      </div>
      <div class="right-search">
        <el-date-picker v-model="queryParams.dateRange" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期"/>
        <el-select v-model="queryParams.workerId" placeholder="选择员工" clearable filterable>
          <el-option v-for="item in workerOptions" :key="item.id" :label="item.name" :value="item.id"/>
        </el-select>
        <el-input v-model="queryParams.keyword" placeholder="扎包号/款号" clearable/>
        <el-button type="primary" @click="handleQuery">查询</el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" style="margin-bottom: 16px;">
      <el-col :span="6">
        <el-statistic title="今日计件数" :value="statistics.todayCount"/>
      </el-col>
      <el-col :span="6">
        <el-statistic title="今日计件金额" :value="statistics.todayAmount" :precision="2" prefix="¥"/>
      </el-col>
      <el-col :span="6">
        <el-statistic title="本月计件数" :value="statistics.monthCount"/>
      </el-col>
      <el-col :span="6">
        <el-statistic title="本月计件金额" :value="statistics.monthAmount" :precision="2" prefix="¥"/>
      </el-col>
    </el-row>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading">
      <el-table-column prop="recordNo" label="记录编号" width="120"/>
      <el-table-column prop="bundleNo" label="扎包号" width="120"/>
      <el-table-column prop="styleCode" label="款号" width="100"/>
      <el-table-column prop="processName" label="工序" width="100"/>
      <el-table-column prop="workerName" label="员工" width="80"/>
      <el-table-column prop="quantity" label="数量" width="80"/>
      <el-table-column prop="unitPrice" label="单价" width="80"/>
      <el-table-column prop="amount" label="金额" width="100">
        <template #default="{row}">
          ¥{{ row.amount.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{row}">
          <el-tag :type="row.status === 'settled' ? 'success' : 'warning'">
            {{ row.status === 'settled' ? '已结算' : '待结算' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="recordDate" label="计件日期" width="100"/>
      <el-table-column prop="createdAt" label="录入时间" width="160"/>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{row}">
          <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
          <el-button link type="danger" v-perm="'process:record:delete'" @click="handleDelete(row)" v-if="row.status !== 'settled'">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="queryParams.page"
      v-model:page-size="queryParams.size"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
    />
  </el-card>
</template>
```

#### 20.3.6 扎包列表页

**文件路径：** `frontend/src/views/bundle/index.vue`

**页面结构：**
```vue
<template>
  <el-card shadow="never">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="left-actions">
        <el-button type="primary" v-perm="'bundle:add'" @click="handleAdd">新建扎包</el-button>
        <el-button v-perm="'bundle:print'" @click="handleBatchPrint">批量打印</el-button>
      </div>
      <div class="right-search">
        <el-select v-model="queryParams.status" placeholder="扎包状态" clearable>
          <el-option label="待开工" value="pending"/>
          <el-option label="生产中" value="in_progress"/>
          <el-option label="已完成" value="completed"/>
        </el-select>
        <el-input v-model="queryParams.keyword" placeholder="扎包号/款号" clearable/>
        <el-button type="primary" @click="handleQuery">查询</el-button>
      </div>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50"/>
      <el-table-column prop="bundleNo" label="扎包号" width="140">
        <template #default="{row}">
          <el-link type="primary" @click="handleDetail(row)">{{ row.bundleNo }}</el-link>
        </template>
      </el-table-column>
      <el-table-column prop="styleCode" label="款号" width="100"/>
      <el-table-column prop="color" label="颜色" width="80"/>
      <el-table-column prop="size" label="尺码" width="60"/>
      <el-table-column prop="quantity" label="数量" width="80"/>
      <el-table-column prop="completedQuantity" label="完成数" width="80"/>
      <el-table-column prop="progress" label="进度" width="120">
        <template #default="{row}">
          <el-progress :percentage="row.progress" :stroke-width="10"/>
        </template>
      </el-table-column>
      <el-table-column prop="currentProcess" label="当前工序" width="100"/>
      <el-table-column prop="currentHolder" label="当前持有者" width="100"/>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{row}">
          <el-tag :type="getStatusType(row.status)">{{ getStatusName(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="160"/>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{row}">
          <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
          <el-button link type="primary" @click="handleFlow(row)">流转</el-button>
          <el-button link type="primary" @click="handlePrint(row)">打印</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="queryParams.page"
      v-model:page-size="queryParams.size"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
    />
  </el-card>
</template>
```

#### 20.3.7 扎包详情页

**文件路径：** `frontend/src/views/bundle/detail.vue`

**页面结构：**
```vue
<template>
  <div class="app-container">
    <!-- 固定头部 -->
    <div class="sticky-header">
      <el-page-header @back="goBack">
        <template #content>
          <span class="text-large font-600 mr-3">扎包详情</span>
        </template>
      </el-page-header>
    </div>

    <!-- 扎包信息卡片 -->
    <el-card class="box-card" style="margin-bottom: 16px;">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>扎包信息</span>
          <div>
            <el-button type="primary" @click="handleFlow">流转</el-button>
            <el-button @click="handlePrint">打印标签</el-button>
          </div>
        </div>
      </template>
      
      <!-- 扎包标签预览 -->
      <div style="display: flex; gap: 24px;">
        <div style="width: 300px; border: 1px solid #dcdfe6; padding: 16px; border-radius: 4px;">
          <div style="text-align: center; font-size: 24px; font-weight: bold; margin-bottom: 8px;">
            {{ bundle.bundleNo }}
          </div>
          <div style="display: flex; justify-content: space-between; margin-bottom: 4px;">
            <span>款号：{{ bundle.styleCode }}</span>
            <span>数量：{{ bundle.quantity }}</span>
          </div>
          <div style="display: flex; justify-content: space-between; margin-bottom: 4px;">
            <span>颜色：{{ bundle.color }}</span>
            <span>尺码：{{ bundle.size }}</span>
          </div>
          <div style="text-align: center; margin-top: 8px;">
            <qr-code :value="bundle.bundleNo" :size="100"/>
          </div>
        </div>
        
        <div style="flex: 1;">
          <el-descriptions :column="3" border>
            <el-descriptions-item label="扎包号">{{ bundle.bundleNo }}</el-descriptions-item>
            <el-descriptions-item label="款号">{{ bundle.styleCode }}</el-descriptions-item>
            <el-descriptions-item label="品名">{{ bundle.styleName }}</el-descriptions-item>
            <el-descriptions-item label="颜色">{{ bundle.color }}</el-descriptions-item>
            <el-descriptions-item label="尺码">{{ bundle.size }}</el-descriptions-item>
            <el-descriptions-item label="数量">{{ bundle.quantity }}</el-descriptions-item>
            <el-descriptions-item label="完成数">{{ bundle.completedQuantity }}</el-descriptions-item>
            <el-descriptions-item label="当前工序">{{ bundle.currentProcess }}</el-descriptions-item>
            <el-descriptions-item label="当前持有者">{{ bundle.currentHolder }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(bundle.status)">{{ getStatusName(bundle.status) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ bundle.createdAt }}</el-descriptions-item>
            <el-descriptions-item label="生产订单">{{ bundle.productionOrderNo }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-card>

    <!-- 工序进度 -->
    <el-card class="box-card" style="margin-bottom: 16px;">
      <template #header>工序进度</template>
      <el-table :data="bundle.processes" border>
        <el-table-column type="index" label="序号" width="60"/>
        <el-table-column prop="processName" label="工序名称" width="120"/>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{row}">
            <el-tag :type="getProcessStatusType(row.status)">{{ getProcessStatusName(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="completedQuantity" label="完成数量" width="100"/>
        <el-table-column prop="workerName" label="操作员" width="100"/>
        <el-table-column prop="startTime" label="开始时间" width="160"/>
        <el-table-column prop="endTime" label="完成时间" width="160"/>
        <el-table-column prop="remark" label="备注"/>
      </el-table>
    </el-card>

    <!-- 流转记录 -->
    <el-card class="box-card">
      <template #header>流转记录</template>
      <el-timeline>
        <el-timeline-item v-for="(item, index) in bundle.flowRecords" :key="index" :timestamp="item.createdAt" placement="top">
          <el-card>
            <div style="display: flex; justify-content: space-between;">
              <span>{{ item.processName }} - {{ item.action }}</span>
              <span>操作员：{{ item.operatorName }}</span>
            </div>
            <div v-if="item.remark" style="margin-top: 8px; color: #909399;">备注：{{ item.remark }}</div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>
```

#### 20.3.8 生产订单列表页

**文件路径：** `frontend/src/views/production/order/index.vue`

**页面结构：**
```vue
<template>
  <el-card shadow="never">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="left-actions">
        <el-button type="primary" v-perm="'production:order:add'" @click="handleAdd">新建生产单</el-button>
        <el-button v-perm="'production:order:import'" @click="handleImport">从销售单导入</el-button>
      </div>
      <div class="right-search">
        <el-select v-model="queryParams.status" placeholder="订单状态" clearable>
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value"/>
        </el-select>
        <el-date-picker v-model="queryParams.dateRange" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期"/>
        <el-input v-model="queryParams.keyword" placeholder="生产单号/款号" clearable/>
        <el-button type="primary" @click="handleQuery">查询</el-button>
      </div>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading">
      <el-table-column prop="orderNo" label="生产单号" width="140">
        <template #default="{row}">
          <el-link type="primary" @click="handleDetail(row)">{{ row.orderNo }}</el-link>
        </template>
      </el-table-column>
      <el-table-column prop="styleCode" label="款号" width="100"/>
      <el-table-column prop="styleName" label="品名" width="150"/>
      <el-table-column prop="totalQuantity" label="总数量" width="80"/>
      <el-table-column prop="completedQuantity" label="完成数" width="80"/>
      <el-table-column prop="progress" label="进度" width="150">
        <template #default="{row}">
          <el-progress :percentage="row.progress" :stroke-width="10"/>
        </template>
      </el-table-column>
      <el-table-column prop="factoryName" label="生产工厂" width="120"/>
      <el-table-column prop="planStartDate" label="计划开始" width="100"/>
      <el-table-column prop="planEndDate" label="计划完成" width="100"/>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{row}">
          <el-tag :type="getStatusType(row.status)">{{ getStatusName(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="priority" label="优先级" width="80">
        <template #default="{row}">
          <el-tag :type="getPriorityType(row.priority)" size="small">{{ getPriorityName(row.priority) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{row}">
          <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
          <el-button link type="primary" v-perm="'production:order:edit'" @click="handleEdit(row)" v-if="row.status === 'draft'">编辑</el-button>
          <el-button link type="primary" v-perm="'production:order:issue'" @click="handleIssue(row)" v-if="row.status === 'draft'">下发</el-button>
          <el-button link type="primary" v-perm="'production:order:complete'" @click="handleComplete(row)" v-if="row.status === 'in_progress'">完工</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="queryParams.page"
      v-model:page-size="queryParams.size"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
    />
  </el-card>
</template>
```

#### 20.3.9 生产订单新增页

**文件路径：** `frontend/src/views/production/order/add.vue`

**页面结构：**
```vue
<template>
  <div class="app-container">
    <!-- 固定头部 -->
    <div class="sticky-header">
      <el-page-header @back="goBack">
        <template #content>
          <span class="text-large font-600 mr-3">{{ isEdit ? '编辑生产单' : '新建生产单' }}</span>
        </template>
        <template #extra>
          <div class="flex items-center">
            <el-button @click="goBack">取消</el-button>
            <el-button @click="handleSaveDraft" :loading="saving">保存草稿</el-button>
            <el-button type="primary" @click="handleSubmit" :loading="submitting">提交</el-button>
          </div>
        </template>
      </el-page-header>
    </div>

    <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
      <!-- 基本信息 -->
      <el-card class="box-card" style="margin-bottom: 16px;">
        <template #header>基本信息</template>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="生产单号" prop="orderNo">
              <el-input v-model="form.orderNo" placeholder="自动生成" :disabled="true"/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="款号" prop="styleCode">
              <el-select v-model="form.styleCode" placeholder="选择款号" filterable @change="handleStyleChange">
                <el-option v-for="item in styleOptions" :key="item.code" :label="item.code" :value="item.code"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="品名" prop="styleName">
              <el-input v-model="form.styleName" :disabled="true"/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="关联销售单" prop="salesOrderId">
              <el-select v-model="form.salesOrderId" placeholder="选择销售单" clearable filterable>
                <el-option v-for="item in salesOrderOptions" :key="item.id" :label="item.orderNo" :value="item.id"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="生产工厂" prop="factoryId">
              <el-select v-model="form.factoryId" placeholder="选择工厂">
                <el-option v-for="item in factoryOptions" :key="item.id" :label="item.name" :value="item.id"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="优先级" prop="priority">
              <el-select v-model="form.priority" placeholder="选择优先级">
                <el-option label="低" value="low"/>
                <el-option label="中" value="medium"/>
                <el-option label="高" value="high"/>
                <el-option label="紧急" value="urgent"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="计划开始日期" prop="planStartDate">
              <el-date-picker v-model="form.planStartDate" type="date" placeholder="选择日期"/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="计划完成日期" prop="planEndDate">
              <el-date-picker v-model="form.planEndDate" type="date" placeholder="选择日期"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 规格数量 -->
      <el-card class="box-card" style="margin-bottom: 16px;">
        <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <span>规格数量</span>
            <el-button type="primary" size="small" @click="handleAddSku">添加规格</el-button>
          </div>
        </template>
        <el-table :data="form.skus" border>
          <el-table-column prop="color" label="颜色" width="120">
            <template #default="{row}">
              <el-select v-model="row.color" placeholder="选择颜色">
                <el-option v-for="item in colorOptions" :key="item" :label="item" :value="item"/>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="size" label="尺码" width="100">
            <template #default="{row}">
              <el-select v-model="row.size" placeholder="选择尺码">
                <el-option v-for="item in sizeOptions" :key="item" :label="item" :value="item"/>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="120">
            <template #default="{row}">
              <el-input-number v-model="row.quantity" :min="1"/>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注">
            <template #default="{row}">
              <el-input v-model="row.remark"/>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="{$index}">
              <el-button link type="danger" @click="handleRemoveSku($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div style="margin-top: 8px; font-weight: bold;">
          合计数量：{{ totalQuantity }}
        </div>
      </el-card>

      <!-- 工序配置 -->
      <el-card class="box-card" style="margin-bottom: 16px;">
        <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <span>工序配置</span>
            <el-button type="primary" size="small" @click="handleLoadProcessConfig">加载模板</el-button>
          </div>
        </template>
        <el-table :data="form.processes" border>
          <el-table-column type="index" label="序号" width="60"/>
          <el-table-column prop="processName" label="工序名称" width="150">
            <template #default="{row}">
              <el-select v-model="row.processId" placeholder="选择工序" filterable @change="handleProcessChange(row)">
                <el-option v-for="item in processOptions" :key="item.id" :label="item.name" :value="item.id"/>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="unit" label="单位" width="80"/>
          <el-table-column prop="price" label="单价" width="100"/>
          <el-table-column prop="estimatedTime" label="工时(分钟)" width="100"/>
          <el-table-column prop="remark" label="备注"/>
          <el-table-column label="操作" width="80">
            <template #default="{$index}">
              <el-button link type="danger" @click="handleRemoveProcess($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div style="margin-top: 8px;">
          <el-button type="primary" link @click="handleAddProcess">添加工序</el-button>
        </div>
      </el-card>

      <!-- 备注 -->
      <el-card class="box-card">
        <template #header>备注信息</template>
        <el-form-item prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注信息"/>
        </el-form-item>
      </el-card>
    </el-form>
  </div>
</template>
```

#### 20.3.10 生产进度看板页

**文件路径：** `frontend/src/views/production/dashboard/index.vue`

**页面结构：**
```vue
<template>
  <div class="app-container">
    <!-- 筛选栏 -->
    <el-card shadow="never" style="margin-bottom: 16px;">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-select v-model="queryParams.factoryId" placeholder="选择工厂" clearable>
            <el-option v-for="item in factoryOptions" :key="item.id" :label="item.name" :value="item.id"/>
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="queryParams.status" placeholder="订单状态" clearable>
            <el-option label="待开工" value="pending"/>
            <el-option label="生产中" value="in_progress"/>
            <el-option label="已完成" value="completed"/>
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleRefresh">刷新</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="16" style="margin-bottom: 16px;">
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="生产中订单" :value="statistics.inProgressCount">
            <template #suffix>
              <span style="font-size: 14px;">单</span>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="今日产量" :value="statistics.todayOutput">
            <template #suffix>
              <span style="font-size: 14px;">件</span>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="本周产量" :value="statistics.weekOutput">
            <template #suffix>
              <span style="font-size: 14px;">件</span>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="本月产量" :value="statistics.monthOutput">
            <template #suffix>
              <span style="font-size: 14px;">件</span>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <!-- 订单看板 -->
    <el-card shadow="never">
      <template #header>生产看板</template>
      <div style="display: flex; gap: 16px; overflow-x: auto;">
        <!-- 待开工列 -->
        <div style="min-width: 300px; background: #f5f7fa; border-radius: 4px; padding: 12px;">
          <div style="font-weight: bold; margin-bottom: 12px; display: flex; justify-content: space-between;">
            <span>待开工</span>
            <el-badge :value="pendingOrders.length" type="info"/>
          </div>
          <div v-for="order in pendingOrders" :key="order.id" style="background: white; padding: 12px; border-radius: 4px; margin-bottom: 8px; cursor: pointer;" @click="handleOrderDetail(order)">
            <div style="font-weight: bold;">{{ order.orderNo }}</div>
            <div style="color: #909399; font-size: 12px; margin-top: 4px;">{{ order.styleCode }} - {{ order.styleName }}</div>
            <div style="display: flex; justify-content: space-between; margin-top: 8px;">
              <span>数量：{{ order.totalQuantity }}</span>
              <el-tag size="small" :type="getPriorityType(order.priority)">{{ getPriorityName(order.priority) }}</el-tag>
            </div>
          </div>
        </div>

        <!-- 生产中列 -->
        <div style="min-width: 300px; background: #fdf6ec; border-radius: 4px; padding: 12px;">
          <div style="font-weight: bold; margin-bottom: 12px; display: flex; justify-content: space-between;">
            <span>生产中</span>
            <el-badge :value="inProgressOrders.length" type="warning"/>
          </div>
          <div v-for="order in inProgressOrders" :key="order.id" style="background: white; padding: 12px; border-radius: 4px; margin-bottom: 8px; cursor: pointer;" @click="handleOrderDetail(order)">
            <div style="font-weight: bold;">{{ order.orderNo }}</div>
            <div style="color: #909399; font-size: 12px; margin-top: 4px;">{{ order.styleCode }} - {{ order.styleName }}</div>
            <div style="margin-top: 8px;">
              <el-progress :percentage="order.progress" :stroke-width="6"/>
            </div>
            <div style="display: flex; justify-content: space-between; margin-top: 8px; font-size: 12px;">
              <span>完成：{{ order.completedQuantity }}/{{ order.totalQuantity }}</span>
              <span>{{ order.currentProcess }}</span>
            </div>
          </div>
        </div>

        <!-- 已完成列 -->
        <div style="min-width: 300px; background: #f0f9eb; border-radius: 4px; padding: 12px;">
          <div style="font-weight: bold; margin-bottom: 12px; display: flex; justify-content: space-between;">
            <span>已完成</span>
            <el-badge :value="completedOrders.length" type="success"/>
          </div>
          <div v-for="order in completedOrders" :key="order.id" style="background: white; padding: 12px; border-radius: 4px; margin-bottom: 8px; cursor: pointer;" @click="handleOrderDetail(order)">
            <div style="font-weight: bold;">{{ order.orderNo }}</div>
            <div style="color: #909399; font-size: 12px; margin-top: 4px;">{{ order.styleCode }} - {{ order.styleName }}</div>
            <div style="display: flex; justify-content: space-between; margin-top: 8px;">
              <span>数量：{{ order.totalQuantity }}</span>
              <el-tag size="small" type="success">已完成</el-tag>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>
```

#### 20.3.11 计件工资汇总页

**文件路径：** `frontend/src/views/wage/summary/index.vue`

**页面结构：**
```vue
<template>
  <el-card shadow="never">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="left-actions">
        <el-button type="primary" v-perm="'wage:settlement'" @click="handleSettlement">生成结算单</el-button>
        <el-button v-perm="'wage:export'" @click="handleExport">导出</el-button>
      </div>
      <div class="right-search">
        <el-date-picker v-model="queryParams.month" type="month" placeholder="选择月份"/>
        <el-select v-model="queryParams.departmentId" placeholder="选择车间" clearable>
          <el-option v-for="item in departmentOptions" :key="item.id" :label="item.name" :value="item.id"/>
        </el-select>
        <el-input v-model="queryParams.keyword" placeholder="员工姓名/工号" clearable/>
        <el-button type="primary" @click="handleQuery">查询</el-button>
      </div>
    </div>

    <!-- 汇总统计 -->
    <el-row :gutter="16" style="margin-bottom: 16px;">
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="计件人数" :value="summary.workerCount"/>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="计件总数量" :value="summary.totalQuantity"/>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="计件总金额" :value="summary.totalAmount" :precision="2" prefix="¥"/>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="人均金额" :value="summary.avgAmount" :precision="2" prefix="¥"/>
        </el-card>
      </el-col>
    </el-row>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading" show-summary :summary-method="getSummaryRow">
      <el-table-column prop="workerCode" label="工号" width="100"/>
      <el-table-column prop="workerName" label="姓名" width="100"/>
      <el-table-column prop="departmentName" label="车间" width="120"/>
      <el-table-column prop="recordCount" label="计件次数" width="100"/>
      <el-table-column prop="totalQuantity" label="总数量" width="100"/>
      <el-table-column prop="totalAmount" label="计件金额" width="120">
        <template #default="{row}">
          ¥{{ row.totalAmount.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="deductionAmount" label="扣款金额" width="100">
        <template #default="{row}">
          <span v-if="row.deductionAmount > 0" style="color: #f56c6c;">-¥{{ row.deductionAmount.toFixed(2) }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="bonusAmount" label="奖金" width="100">
        <template #default="{row}">
          <span v-if="row.bonusAmount > 0" style="color: #67c23a;">+¥{{ row.bonusAmount.toFixed(2) }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="finalAmount" label="应发金额" width="120">
        <template #default="{row}">
          <span style="font-weight: bold;">¥{{ row.finalAmount.toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="settledAmount" label="已结算" width="100">
        <template #default="{row}">
          ¥{{ row.settledAmount.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="unsettledAmount" label="待结算" width="100">
        <template #default="{row}">
          <span v-if="row.unsettledAmount > 0" style="color: #e6a23c;">¥{{ row.unsettledAmount.toFixed(2) }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{row}">
          <el-button link type="primary" @click="handleDetail(row)">明细</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="queryParams.page"
      v-model:page-size="queryParams.size"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
    />
  </el-card>
</template>
```

#### 20.3.12 计件工资明细页

**文件路径：** `frontend/src/views/wage/detail/index.vue`

**页面结构：**
```vue
<template>
  <el-card shadow="never">
    <!-- 员工信息 -->
    <div style="margin-bottom: 16px;">
      <el-descriptions :column="6" border size="small">
        <el-descriptions-item label="工号">{{ worker.code }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ worker.name }}</el-descriptions-item>
        <el-descriptions-item label="车间">{{ worker.departmentName }}</el-descriptions-item>
        <el-descriptions-item label="统计月份">{{ queryParams.month }}</el-descriptions-item>
        <el-descriptions-item label="计件总数">{{ summary.totalQuantity }}</el-descriptions-item>
        <el-descriptions-item label="计件总额">¥{{ summary.totalAmount.toFixed(2) }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="left-actions">
        <el-button v-perm="'wage:export'" @click="handleExport">导出</el-button>
      </div>
      <div class="right-search">
        <el-date-picker v-model="queryParams.dateRange" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期"/>
        <el-select v-model="queryParams.processId" placeholder="选择工序" clearable>
          <el-option v-for="item in processOptions" :key="item.id" :label="item.name" :value="item.id"/>
        </el-select>
        <el-button type="primary" @click="handleQuery">查询</el-button>
      </div>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading" show-summary :summary-method="getSummaryRow">
      <el-table-column prop="recordDate" label="日期" width="100"/>
      <el-table-column prop="bundleNo" label="扎包号" width="120"/>
      <el-table-column prop="styleCode" label="款号" width="100"/>
      <el-table-column prop="processName" label="工序" width="100"/>
      <el-table-column prop="quantity" label="数量" width="80"/>
      <el-table-column prop="unitPrice" label="单价" width="80"/>
      <el-table-column prop="amount" label="金额" width="100"/>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{row}">
          <el-tag :type="row.status === 'settled' ? 'success' : 'warning'" size="small">
            {{ row.status === 'settled' ? '已结算' : '待结算' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注"/>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="queryParams.page"
      v-model:page-size="queryParams.size"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
    />
  </el-card>
</template>
```

#### 20.3.13 质检记录列表页

**文件路径：** `frontend/src/views/production/quality/index.vue`

**页面结构：**
```vue
<template>
  <el-card shadow="never">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="left-actions">
        <el-button type="primary" v-perm="'production:quality:add'" @click="handleAdd">新增质检</el-button>
      </div>
      <div class="right-search">
        <el-select v-model="queryParams.type" placeholder="质检类型" clearable>
          <el-option label="首件检验(FAI)" value="FAI"/>
          <el-option label="过程检验(IPQC)" value="IPQC"/>
          <el-option label="最终检验(FQC)" value="FQC"/>
        </el-select>
        <el-select v-model="queryParams.result" placeholder="检验结果" clearable>
          <el-option label="合格" value="pass"/>
          <el-option label="不合格" value="fail"/>
          <el-option label="返工" value="rework"/>
        </el-select>
        <el-input v-model="queryParams.keyword" placeholder="生产单号/扎包号" clearable/>
        <el-button type="primary" @click="handleQuery">查询</el-button>
      </div>
    </div>

    <!-- 质量统计 -->
    <el-row :gutter="16" style="margin-bottom: 16px;">
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="今日检验数" :value="statistics.todayCount"/>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="合格率" :value="statistics.passRate" suffix="%"/>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="返工率" :value="statistics.reworkRate" suffix="%"/>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="报废率" :value="statistics.scrapRate" suffix="%"/>
        </el-card>
      </el-col>
    </el-row>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading">
      <el-table-column prop="qcNo" label="质检单号" width="140"/>
      <el-table-column prop="type" label="质检类型" width="100">
        <template #default="{row}">
          <el-tag size="small">{{ getQCTypeName(row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="orderNo" label="生产单号" width="140"/>
      <el-table-column prop="bundleNo" label="扎包号" width="120"/>
      <el-table-column prop="processName" label="工序" width="100"/>
      <el-table-column prop="inspectQuantity" label="检验数量" width="80"/>
      <el-table-column prop="passQuantity" label="合格数量" width="80"/>
      <el-table-column prop="failQuantity" label="不合格数量" width="100">
        <template #default="{row}">
          <span v-if="row.failQuantity > 0" style="color: #f56c6c;">{{ row.failQuantity }}</span>
          <span v-else>0</span>
        </template>
      </el-table-column>
      <el-table-column prop="result" label="检验结果" width="80">
        <template #default="{row}">
          <el-tag :type="getResultType(row.result)" size="small">{{ getResultName(row.result) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="inspectorName" label="检验员" width="80"/>
      <el-table-column prop="createdAt" label="检验时间" width="160"/>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{row}">
          <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
          <el-button link type="primary" v-perm="'production:rework:add'" @click="handleRework(row)" v-if="row.result === 'fail'">返工</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="queryParams.page"
      v-model:page-size="queryParams.size"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
    />
  </el-card>
</template>
```

#### 20.3.14 返工记录列表页

**文件路径：** `frontend/src/views/production/rework/index.vue`

**页面结构：**
```vue
<template>
  <el-card shadow="never">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="left-actions">
        <el-button type="primary" v-perm="'production:rework:add'" @click="handleAdd">新增返工</el-button>
      </div>
      <div class="right-search">
        <el-select v-model="queryParams.status" placeholder="返工状态" clearable>
          <el-option label="待处理" value="pending"/>
          <el-option label="处理中" value="in_progress"/>
          <el-option label="已完成" value="completed"/>
        </el-select>
        <el-input v-model="queryParams.keyword" placeholder="返工单号/扎包号" clearable/>
        <el-button type="primary" @click="handleQuery">查询</el-button>
      </div>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading">
      <el-table-column prop="reworkNo" label="返工单号" width="140"/>
      <el-table-column prop="bundleNo" label="扎包号" width="120"/>
      <el-table-column prop="processName" label="返工工序" width="100"/>
      <el-table-column prop="defectType" label="缺陷类型" width="100"/>
      <el-table-column prop="quantity" label="返工数量" width="80"/>
      <el-table-column prop="originalWorkerName" label="原操作员" width="100"/>
      <el-table-column prop="reworkWorkerName" label="返工操作员" width="100"/>
      <el-table-column prop="deductionAmount" label="扣款金额" width="100">
        <template #default="{row}">
          <span v-if="row.deductionAmount > 0" style="color: #f56c6c;">-¥{{ row.deductionAmount.toFixed(2) }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{row}">
          <el-tag :type="getStatusType(row.status)">{{ getStatusName(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="160"/>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{row}">
          <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
          <el-button link type="primary" v-perm="'production:rework:complete'" @click="handleComplete(row)" v-if="row.status === 'in_progress'">完成</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="queryParams.page"
      v-model:page-size="queryParams.size"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
    />
  </el-card>
</template>
```

### 20.4 公共组件设计

#### 20.4.1 工序选择器组件

**文件路径：** `frontend/src/components/ProcessSelector.vue`

**功能说明：**
- 支持工序分类筛选
- 支持工序名称搜索
- 支持多选/单选模式
- 显示工序单价、工时信息

#### 20.4.2 扎包标签打印组件

**文件路径：** `frontend/src/components/BundleLabelPrint.vue`

**功能说明：**
- 支持单个/批量打印
- 生成二维码
- 打印预览
- 自定义模板

#### 20.4.3 计件录入组件

**文件路径：** `frontend/src/components/PieceWorkInput.vue`

**功能说明：**
- 扫码录入扎包号
- 自动加载工序配置
- 支持快速录入
- 实时计算金额

### 20.5 路由配置

```typescript
const routes = [
  {
    path: '/process',
    component: Layout,
    meta: { title: '工序管理', icon: 'operation' },
    children: [
      { path: 'library', name: 'ProcessLibrary', component: () => import('@/views/process/library/index.vue'), meta: { title: '工序库' } },
      { path: 'library/add', name: 'ProcessLibraryAdd', component: () => import('@/views/process/library/add.vue'), meta: { title: '新增工序' }, hidden: true },
      { path: 'config', name: 'ProcessConfig', component: () => import('@/views/process/config/index.vue'), meta: { title: '工序配置' } },
      { path: 'config/detail/:id', name: 'ProcessConfigDetail', component: () => import('@/views/process/config/detail.vue'), meta: { title: '配置详情' }, hidden: true },
      { path: 'record', name: 'ProcessRecord', component: () => import('@/views/process/record/index.vue'), meta: { title: '计件记录' } },
    ]
  },
  {
    path: '/bundle',
    component: Layout,
    meta: { title: '扎包管理', icon: 'box' },
    children: [
      { path: 'index', name: 'BundleList', component: () => import('@/views/bundle/index.vue'), meta: { title: '扎包列表' } },
      { path: 'detail/:id', name: 'BundleDetail', component: () => import('@/views/bundle/detail.vue'), meta: { title: '扎包详情' }, hidden: true },
      { path: 'flow/:id', name: 'BundleFlow', component: () => import('@/views/bundle/flow.vue'), meta: { title: '流转记录' }, hidden: true },
    ]
  },
  {
    path: '/production',
    component: Layout,
    meta: { title: '生产管理', icon: 'industry' },
    children: [
      { path: 'order', name: 'ProductionOrder', component: () => import('@/views/production/order/index.vue'), meta: { title: '生产订单' } },
      { path: 'order/add', name: 'ProductionOrderAdd', component: () => import('@/views/production/order/add.vue'), meta: { title: '新建生产单' }, hidden: true },
      { path: 'order/detail/:id', name: 'ProductionOrderDetail', component: () => import('@/views/production/order/detail.vue'), meta: { title: '订单详情' }, hidden: true },
      { path: 'dashboard', name: 'ProductionDashboard', component: () => import('@/views/production/dashboard/index.vue'), meta: { title: '进度看板' } },
      { path: 'quality', name: 'ProductionQuality', component: () => import('@/views/production/quality/index.vue'), meta: { title: '质检记录' } },
      { path: 'rework', name: 'ProductionRework', component: () => import('@/views/production/rework/index.vue'), meta: { title: '返工记录' } },
    ]
  },
  {
    path: '/wage',
    component: Layout,
    meta: { title: '计件工资', icon: 'money' },
    children: [
      { path: 'summary', name: 'WageSummary', component: () => import('@/views/wage/summary/index.vue'), meta: { title: '工资汇总' } },
      { path: 'detail', name: 'WageDetail', component: () => import('@/views/wage/detail/index.vue'), meta: { title: '工资明细' }, hidden: true },
      { path: 'settlement', name: 'WageSettlement', component: () => import('@/views/wage/settlement/index.vue'), meta: { title: '结算单' } },
    ]
  },
  {
    path: '/report',
    component: Layout,
    meta: { title: '报表分析', icon: 'chart' },
    children: [
      { path: 'efficiency', name: 'ReportEfficiency', component: () => import('@/views/report/efficiency/index.vue'), meta: { title: '效率报表' } },
      { path: 'output', name: 'ReportOutput', component: () => import('@/views/report/output/index.vue'), meta: { title: '产量报表' } },
      { path: 'quality', name: 'ReportQuality', component: () => import('@/views/report/quality/index.vue'), meta: { title: '质量报表' } },
    ]
  },
]
```

### 20.6 页面统计

#### 20.6.1 生产模块页面（本章）

| 模块 | 页面数量 |
|------|---------|
| 工序管理 | 6 |
| 扎包管理 | 3 |
| 生产管理 | 6 |
| 计件工资 | 3 |
| 报表分析 | 3 |
| 公共组件 | 3 |
| **小计** | **24** |

#### 20.6.2 全系统页面总览

| 模块 | 页面数量 | 所在章节 |
|------|---------|----------|
| 生产模块 | 24 | 第二十章 |
| 销售管理 | 6 | 第二十八章 |
| 采购管理 | 5 | 第二十九章 |
| 财务管理 | 4 | 第三十章 |
| 库存管理 | 3 | 第三十二章 |
| 报表统计 | 3 | 第三十三章 |
| 小程序 | 39 | 第二十一章 |
| **合计** | **约84个** | - |

---

## 21. 小程序页面设计（uni-app）

### 21.1 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| uni-app | 3.x | 跨平台框架 |
| Vue 3 | 3.x | 前端框架 |
| Pinia | 2.x | 状态管理 |
| uView UI | 2.x | UI组件库 |
| luch-request | 3.x | HTTP请求 |

### 21.2 角色与权限设计

| 角色 | 权限标识 | 核心功能 | TabBar 配置 |
|------|----------|----------|-------------|
| 缝制工 | worker | 计件、任务、工资 | 首页/计件/任务/工资/我的 |
| 裁床工 | cutter | 裁床、分包、入库 | 首页/裁床/分包/我的 |
| 质检员 | inspector | 质检、返工、首件 | 首页/质检/返工/我的 |
| 仓库员 | warehouse | 入库、出库、盘点 | 首页/入库/出库/我的 |
| 班组长 | leader | 分配、进度、审批 | 首页/分配/进度/审批/我的 |
| 车间主任 | manager | 看板、订单、报表 | 首页/看板/订单/报表/我的 |
| 老板 | boss | 大屏、经营、预警 | 首页/大屏/报表/我的 |

### 21.3 项目结构

```
miniprogram-uni/
├── pages/
│   ├── common/              # 公共页面
│   │   ├── login/           # 登录
│   │   ├── profile/         # 个人中心
│   │   ├── message/         # 消息中心
│   │   ├── settings/        # 设置
│   │   └── scan/            # 通用扫码
│   ├── worker/              # 缝制工
│   │   ├── index/           # 首页
│   │   ├── piecework/       # 扫码计件
│   │   ├── task/            # 我的任务
│   │   ├── wage/            # 工资查询
│   │   └── wage-detail/     # 工资明细
│   ├── cutter/              # 裁床工
│   │   ├── index/           # 首页
│   │   ├── task/            # 裁床任务
│   │   ├── input/           # 裁床录入
│   │   ├── bundle/          # 分包管理
│   │   └── bundle-detail/   # 扎包详情
│   ├── inspector/           # 质检员
│   │   ├── index/           # 首页
│   │   ├── check/           # 扫码质检
│   │   ├── first-article/   # 首件确认
│   │   ├── rework/          # 返工处理
│   │   └── history/         # 质检记录
│   ├── warehouse/           # 仓库员
│   │   ├── index/           # 首页
│   │   ├── in/              # 成品入库
│   │   ├── out/             # 物料出库
│   │   ├── stock/           # 库存查询
│   │   └── inventory/       # 盘点任务
│   ├── leader/              # 班组长
│   │   ├── index/           # 首页
│   │   ├── assign/          # 任务分配
│   │   ├── progress/        # 生产进度
│   │   ├── exception/       # 异常处理
│   │   └── approval/        # 审批待办
│   ├── manager/             # 车间主任
│   │   ├── index/           # 首页
│   │   ├── dashboard/       # 生产看板
│   │   ├── order/           # 订单管理
│   │   ├── staff/           # 人员管理
│   │   └── report/          # 报表中心
│   └── boss/                # 老板/经营者
│       ├── index/           # 首页
│       ├── dashboard/       # 经营大屏
│       ├── report/          # 报表中心
│       └── alert/           # 预警中心
├── components/              # 公共组件
│   ├── bundle-card/         # 扎包卡片
│   ├── process-picker/      # 工序选择器
│   ├── stats-card/          # 统计卡片
│   └── scan-input/          # 扫码输入框
├── store/                   # Pinia 状态
│   ├── user.js              # 用户状态
│   ├── role.js              # 角色状态
│   └── offline.js           # 离线缓存
├── api/                     # 接口定义
│   ├── worker.js            # 缝制工接口
│   ├── cutter.js            # 裁床工接口
│   ├── inspector.js         # 质检员接口
│   ├── warehouse.js         # 仓库员接口
│   ├── leader.js            # 班组长接口
│   ├── manager.js           # 车间主任接口
│   └── boss.js              # 老板接口
├── utils/                   # 工具函数
│   ├── request.js           # 请求封装
│   ├── offline.js           # 离线处理
│   ├── printer.js           # 蓝牙打印
│   └── permission.js        # 权限控制
└── static/                  # 静态资源
```

### 21.4 TabBar 动态配置

根据登录用户的角色，动态显示不同的 TabBar：

```javascript
// store/role.js
import { defineStore } from 'pinia'

export const useRoleStore = defineStore('role', {
  state: () => ({
    currentRole: null,
    tabbarConfig: {
      worker: [
        { pagePath: 'pages/worker/index', text: '首页', icon: 'home' },
        { pagePath: 'pages/worker/piecework', text: '计件', icon: 'scan' },
        { pagePath: 'pages/worker/task', text: '任务', icon: 'list' },
        { pagePath: 'pages/worker/wage', text: '工资', icon: 'money-circle' },
        { pagePath: 'pages/common/profile', text: '我的', icon: 'account' }
      ],
      cutter: [
        { pagePath: 'pages/cutter/index', text: '首页', icon: 'home' },
        { pagePath: 'pages/cutter/task', text: '裁床', icon: 'cut' },
        { pagePath: 'pages/cutter/bundle', text: '分包', icon: 'box' },
        { pagePath: 'pages/common/profile', text: '我的', icon: 'account' }
      ],
      inspector: [
        { pagePath: 'pages/inspector/index', text: '首页', icon: 'home' },
        { pagePath: 'pages/inspector/check', text: '质检', icon: 'checkmark-circle' },
        { pagePath: 'pages/inspector/rework', text: '返工', icon: 'reload' },
        { pagePath: 'pages/common/profile', text: '我的', icon: 'account' }
      ],
      warehouse: [
        { pagePath: 'pages/warehouse/index', text: '首页', icon: 'home' },
        { pagePath: 'pages/warehouse/in', text: '入库', icon: 'arrow-down' },
        { pagePath: 'pages/warehouse/out', text: '出库', icon: 'arrow-up' },
        { pagePath: 'pages/common/profile', text: '我的', icon: 'account' }
      ],
      leader: [
        { pagePath: 'pages/leader/index', text: '首页', icon: 'home' },
        { pagePath: 'pages/leader/assign', text: '分配', icon: 'share' },
        { pagePath: 'pages/leader/progress', text: '进度', icon: 'chart' },
        { pagePath: 'pages/leader/approval', text: '审批', icon: 'checkmark' },
        { pagePath: 'pages/common/profile', text: '我的', icon: 'account' }
      ],
      manager: [
        { pagePath: 'pages/manager/index', text: '首页', icon: 'home' },
        { pagePath: 'pages/manager/dashboard', text: '看板', icon: 'grid' },
        { pagePath: 'pages/manager/order', text: '订单', icon: 'order' },
        { pagePath: 'pages/manager/report', text: '报表', icon: 'chart' },
        { pagePath: 'pages/common/profile', text: '我的', icon: 'account' }
      ],
      boss: [
        { pagePath: 'pages/boss/index', text: '首页', icon: 'home' },
        { pagePath: 'pages/boss/dashboard', text: '大屏', icon: 'grid' },
        { pagePath: 'pages/boss/report', text: '报表', icon: 'chart' },
        { pagePath: 'pages/common/profile', text: '我的', icon: 'account' }
      ]
    }
  }),
  
  getters: {
    currentTabbar: (state) => {
      return state.tabbarConfig[state.currentRole] || []
    }
  },
  
  actions: {
    setRole(role) {
      this.currentRole = role
    }
  }
})
```

### 21.5 页面详细设计

#### 21.5.1 公共页面

##### 登录页

**文件路径：** `pages/common/login/index.vue`

```vue
<template>
  <view class="login-container">
    <view class="logo-section">
      <image class="logo" src="/static/logo.png" mode="aspectFit" />
      <text class="app-name">衣多多生产管理</text>
    </view>
    
    <view class="login-section">
      <u-button 
        type="primary" 
        text="微信授权登录" 
        open-type="getPhoneNumber"
        @getphonenumber="handlePhoneLogin"
      />
      
      <view class="tips">
        <text>登录即表示同意《用户协议》和《隐私政策》</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const handlePhoneLogin = async (e) => {
  if (e.detail.code) {
    const res = await userStore.loginByWechat(e.detail.code)
    if (res.success) {
      if (res.data.roles.length > 1) {
        uni.navigateTo({ url: '/pages/common/role/select' })
      } else {
        uni.switchTab({ url: '/pages/worker/index' })
      }
    }
  }
}
</script>
```

##### 角色选择页

**文件路径：** `pages/common/role/select.vue`

```vue
<template>
  <view class="role-container">
    <view class="title">请选择角色</view>
    <view class="role-list">
      <view 
        class="role-item" 
        v-for="role in roles" 
        :key="role.code"
        @click="selectRole(role.code)"
      >
        <view class="role-icon">
          <u-icon :name="role.icon" size="60" />
        </view>
        <text class="role-name">{{ role.name }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { useRoleStore } from '@/store/role'

const roleStore = useRoleStore()

const roles = ref([
  { code: 'worker', name: '缝制工', icon: 'man' },
  { code: 'cutter', name: '裁床工', icon: 'cut' },
  { code: 'inspector', name: '质检员', icon: 'checkmark-circle' },
  { code: 'warehouse', name: '仓库员', icon: 'home' },
  { code: 'leader', name: '班组长', icon: 'list' },
  { code: 'manager', name: '车间主任', icon: 'grid' },
  { code: 'boss', name: '经营者', icon: 'chart' }
])

const selectRole = (roleCode) => {
  roleStore.setRole(roleCode)
  uni.switchTab({ url: `/pages/${roleCode}/index` })
}
</script>
```

#### 21.5.2 缝制工页面

##### 首页

**文件路径：** `pages/worker/index.vue`

```vue
<template>
  <view class="container">
    <!-- 用户卡片 -->
    <view class="user-card">
      <view class="avatar">
        <image :src="userInfo.avatar || '/static/default-avatar.png'" mode="aspectFill" />
      </view>
      <view class="info">
        <text class="name">{{ userInfo.name }}</text>
        <text class="dept">{{ userInfo.departmentName }}</text>
      </view>
    </view>
    
    <!-- 今日统计 -->
    <view class="stats-card">
      <view class="stats-title">今日统计</view>
      <view class="stats-row">
        <view class="stat-item">
          <text class="value">{{ todayStats.count }}</text>
          <text class="label">计件数</text>
        </view>
        <view class="stat-item">
          <text class="value">{{ todayStats.quantity }}</text>
          <text class="label">总数量</text>
        </view>
        <view class="stat-item">
          <text class="value">¥{{ todayStats.amount }}</text>
          <text class="label">计件金额</text>
        </view>
      </view>
    </view>
    
    <!-- 快捷操作 -->
    <view class="quick-actions">
      <view class="action-item" @click="goToPiecework">
        <u-icon name="scan" size="40" color="#1890ff" />
        <text>扫码计件</text>
      </view>
      <view class="action-item" @click="goToTask">
        <u-icon name="list" size="40" color="#fa8c16" />
        <text>我的任务</text>
      </view>
      <view class="action-item" @click="goToWage">
        <u-icon name="money-circle" size="40" color="#52c41a" />
        <text>工资查询</text>
      </view>
    </view>
    
    <!-- 本月工资概览 -->
    <view class="wage-card">
      <view class="card-header">
        <text class="title">本月工资</text>
        <text class="month">{{ currentMonth }}</text>
      </view>
      <view class="wage-amount">¥{{ monthWage.amount }}</view>
      <view class="wage-detail">
        <text>计件 {{ monthWage.count }} 次</text>
        <text>数量 {{ monthWage.quantity }}</text>
      </view>
    </view>
  </view>
</template>
```

##### 扫码计件页

**文件路径：** `pages/worker/piecework.vue`

```vue
<template>
  <view class="container">
    <!-- 离线提示 -->
    <view class="offline-tip" v-if="isOffline">
      <u-icon name="info-circle" />
      <text>离线模式，数据将缓存后自动同步</text>
    </view>
    
    <!-- 扫码区域 -->
    <view class="scan-section">
      <view class="scan-input">
        <u-input 
          v-model="bundleNo" 
          placeholder="请输入或扫描扎包号" 
          @confirm="handleScan"
        >
          <template #suffix>
            <u-icon name="scan" size="40" @click="scanQRCode" />
          </template>
        </u-input>
      </view>
    </view>
    
    <!-- 扎包信息 -->
    <view class="bundle-info" v-if="bundleInfo">
      <view class="info-card">
        <view class="info-row">
          <text class="label">扎包号</text>
          <text class="value">{{ bundleInfo.bundleNo }}</text>
        </view>
        <view class="info-row">
          <text class="label">款号</text>
          <text class="value">{{ bundleInfo.styleCode }}</text>
        </view>
        <view class="info-row">
          <text class="label">颜色/尺码</text>
          <text class="value">{{ bundleInfo.color }} / {{ bundleInfo.size }}</text>
        </view>
        <view class="info-row">
          <text class="label">数量</text>
          <text class="value highlight">{{ bundleInfo.quantity }}</text>
        </view>
      </view>
      
      <!-- 工序选择 -->
      <view class="process-section">
        <view class="section-title">选择工序</view>
        <view class="process-list">
          <view 
            class="process-item" 
            :class="{ active: selectedProcess === item.id }"
            v-for="item in processList" 
            :key="item.id"
            @click="selectProcess(item)"
          >
            <view class="process-name">{{ item.name }}</view>
            <view class="process-price">¥{{ item.price }}/件</view>
          </view>
        </view>
      </view>
      
      <!-- 数量输入 -->
      <view class="quantity-section">
        <view class="section-title">计件数量</view>
        <u-number-box v-model="quantity" :max="bundleInfo.quantity" />
      </view>
      
      <!-- 提交按钮 -->
      <view class="submit-section">
        <u-button type="primary" text="提交计件" @click="handleSubmit" :loading="submitting" />
      </view>
    </view>
    
    <!-- 今日记录 -->
    <view class="records-section">
      <view class="section-title">今日计件记录</view>
      <scroll-view scroll-y class="record-list">
        <view class="record-item" v-for="item in todayRecords" :key="item.id">
          <view class="record-left">
            <text class="bundle-no">{{ item.bundleNo }}</text>
            <text class="process-name">{{ item.processName }}</text>
          </view>
          <view class="record-right">
            <text class="quantity">×{{ item.quantity }}</text>
            <text class="amount">¥{{ item.amount }}</text>
          </view>
        </view>
        <u-empty v-if="todayRecords.length === 0" text="暂无计件记录" />
      </scroll-view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { useOfflineStore } from '@/store/offline'
import { getBundleInfo, getProcessList, submitPiecework, getTodayRecords } from '@/api/worker'

const offlineStore = useOfflineStore()

const isOffline = computed(() => offlineStore.isOffline)
const bundleNo = ref('')
const bundleInfo = ref(null)
const processList = ref([])
const selectedProcess = ref(null)
const quantity = ref(1)
const submitting = ref(false)
const todayRecords = ref([])

onLoad(() => {
  loadTodayRecords()
})

const scanQRCode = () => {
  uni.scanCode({
    success: (res) => {
      bundleNo.value = res.result
      handleScan()
    }
  })
}

const handleScan = async () => {
  if (!bundleNo.value) return
  
  try {
    const res = await getBundleInfo(bundleNo.value)
    if (res.success) {
      bundleInfo.value = res.data
      processList.value = res.data.processes || []
      if (processList.value.length > 0) {
        selectedProcess.value = processList.value[0].id
      }
    }
  } catch (e) {
    uni.showToast({ title: '获取扎包信息失败', icon: 'none' })
  }
}

const selectProcess = (process) => {
  selectedProcess.value = process.id
}

const handleSubmit = async () => {
  if (!bundleInfo.value || !selectedProcess.value) {
    uni.showToast({ title: '请先扫描扎包并选择工序', icon: 'none' })
    return
  }
  
  submitting.value = true
  try {
    const data = {
      bundleNo: bundleInfo.value.bundleNo,
      processId: selectedProcess.value,
      quantity: quantity.value
    }
    
    if (isOffline.value) {
      await offlineStore.addQueue('piecework', data)
      uni.showToast({ title: '已保存到本地，联网后自动同步', icon: 'none' })
    } else {
      await submitPiecework(data)
      uni.showToast({ title: '提交成功', icon: 'success' })
    }
    
    resetForm()
    loadTodayRecords()
  } catch (e) {
    uni.showToast({ title: '提交失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  bundleNo.value = ''
  bundleInfo.value = null
  selectedProcess.value = null
  quantity.value = 1
}

const loadTodayRecords = async () => {
  const res = await getTodayRecords()
  if (res.success) {
    todayRecords.value = res.data
  }
}
</script>
```

##### 工资查询页

**文件路径：** `pages/worker/wage.vue`

```vue
<template>
  <view class="container">
    <!-- 月份选择 -->
    <view class="month-selector">
      <u-icon name="arrow-left" @click="prevMonth" />
      <text class="current-month">{{ currentMonth }}</text>
      <u-icon name="arrow-right" @click="nextMonth" />
    </view>
    
    <!-- 工资汇总 -->
    <view class="wage-summary">
      <view class="amount-card">
        <text class="label">应发工资</text>
        <text class="amount">¥{{ summary.finalAmount }}</text>
      </view>
      <view class="detail-row">
        <view class="detail-item">
          <text class="value">{{ summary.totalCount }}</text>
          <text class="label">计件次数</text>
        </view>
        <view class="detail-item">
          <text class="value">{{ summary.totalQuantity }}</text>
          <text class="label">总数量</text>
        </view>
        <view class="detail-item">
          <text class="value">¥{{ summary.totalAmount }}</text>
          <text class="label">计件金额</text>
        </view>
      </view>
      <view class="deduction-row" v-if="summary.deductionAmount > 0">
        <text>扣款：-¥{{ summary.deductionAmount }}</text>
      </view>
    </view>
    
    <!-- 明细列表 -->
    <view class="detail-list">
      <view class="list-header">
        <text class="title">明细列表</text>
        <text class="more" @click="goToDetail">查看全部 ></text>
      </view>
      <view class="list-content">
        <view class="detail-item" v-for="item in detailList" :key="item.id">
          <view class="item-left">
            <text class="date">{{ item.recordDate }}</text>
            <text class="process">{{ item.processName }}</text>
          </view>
          <view class="item-right">
            <text class="quantity">×{{ item.quantity }}</text>
            <text class="amount">¥{{ item.amount }}</text>
          </view>
        </view>
        <u-empty v-if="detailList.length === 0" text="暂无记录" />
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getWageSummary, getWageDetail } from '@/api/worker'

const currentMonth = ref('2024-03')
const summary = ref({
  finalAmount: 0,
  totalCount: 0,
  totalQuantity: 0,
  totalAmount: 0,
  deductionAmount: 0
})
const detailList = ref([])

onLoad(() => {
  loadData()
})

const loadData = async () => {
  const [summaryRes, detailRes] = await Promise.all([
    getWageSummary(currentMonth.value),
    getWageDetail(currentMonth.value, { page: 1, size: 10 })
  ])
  
  if (summaryRes.success) {
    summary.value = summaryRes.data
  }
  if (detailRes.success) {
    detailList.value = detailRes.data.list
  }
}

const prevMonth = () => {
  const [year, month] = currentMonth.value.split('-').map(Number)
  const prev = new Date(year, month - 2)
  currentMonth.value = `${prev.getFullYear()}-${String(prev.getMonth() + 1).padStart(2, '0')}`
  loadData()
}

const nextMonth = () => {
  const [year, month] = currentMonth.value.split('-').map(Number)
  const next = new Date(year, month)
  currentMonth.value = `${next.getFullYear()}-${String(next.getMonth() + 1).padStart(2, '0')}`
  loadData()
}

const goToDetail = () => {
  uni.navigateTo({ url: `/pages/worker/wage-detail?month=${currentMonth.value}` })
}
</script>
```

#### 21.5.3 裁床工页面

##### 首页

**文件路径：** `pages/cutter/index.vue`

```vue
<template>
  <view class="container">
    <!-- 用户卡片 -->
    <view class="user-card">
      <view class="avatar">
        <image :src="userInfo.avatar || '/static/default-avatar.png'" mode="aspectFill" />
      </view>
      <view class="info">
        <text class="name">{{ userInfo.name }}</text>
        <text class="dept">{{ userInfo.departmentName }}</text>
      </view>
    </view>
    
    <!-- 今日统计 -->
    <view class="stats-card">
      <view class="stats-title">今日统计</view>
      <view class="stats-row">
        <view class="stat-item">
          <text class="value">{{ todayStats.cutQuantity }}</text>
          <text class="label">裁床数量</text>
        </view>
        <view class="stat-item">
          <text class="value">{{ todayStats.bundleCount }}</text>
          <text class="label">分包数</text>
        </view>
        <view class="stat-item">
          <text class="value">{{ todayStats.pendingOrders }}</text>
          <text class="label">待处理订单</text>
        </view>
      </view>
    </view>
    
    <!-- 快捷操作 -->
    <view class="quick-actions">
      <view class="action-item" @click="goToTask">
        <u-icon name="list" size="40" color="#1890ff" />
        <text>裁床任务</text>
      </view>
      <view class="action-item" @click="goToBundle">
        <u-icon name="box" size="40" color="#fa8c16" />
        <text>分包管理</text>
      </view>
    </view>
    
    <!-- 待处理任务 -->
    <view class="pending-section">
      <view class="section-title">待处理任务</view>
      <view class="task-list">
        <view class="task-item" v-for="item in pendingTasks" :key="item.id">
          <view class="task-header">
            <text class="order-no">{{ item.orderNo }}</text>
            <text class="priority" :class="item.priority">{{ getPriorityText(item.priority) }}</text>
          </view>
          <view class="task-body">
            <text>{{ item.styleCode }} - {{ item.styleName }}</text>
            <text>数量：{{ item.quantity }}</text>
          </view>
        </view>
        <u-empty v-if="pendingTasks.length === 0" text="暂无待处理任务" />
      </view>
    </view>
  </view>
</template>
```

##### 分包管理页

**文件路径：** `pages/cutter/bundle.vue`

```vue
<template>
  <view class="container">
    <!-- 待分包订单 -->
    <view class="order-section">
      <view class="section-title">待分包订单</view>
      <scroll-view scroll-y class="order-list">
        <view 
          class="order-item" 
          :class="{ active: selectedOrder?.id === item.id }"
          v-for="item in pendingOrders" 
          :key="item.id" 
          @click="selectOrder(item)"
        >
          <view class="order-header">
            <text class="order-no">{{ item.orderNo }}</text>
            <text class="style-code">{{ item.styleCode }}</text>
          </view>
          <view class="order-body">
            <text>{{ item.styleName }}</text>
            <text>总数：{{ item.quantity }}</text>
          </view>
        </view>
        <u-empty v-if="pendingOrders.length === 0" text="暂无待分包订单" />
      </scroll-view>
    </view>
    
    <!-- 分包表单 -->
    <view class="bundle-form" v-if="selectedOrder">
      <view class="form-section">
        <view class="section-title">创建扎包</view>
        
        <!-- 规格列表 -->
        <view class="sku-list">
          <view class="sku-item" v-for="sku in selectedOrder.skus" :key="sku.id">
            <view class="sku-info">
              <text class="sku-name">{{ sku.color }} / {{ sku.size }}</text>
              <text class="sku-qty">可分包：{{ sku.quantity }}</text>
            </view>
            <u-number-box v-model="sku.bundleQty" :max="sku.quantity" :min="0" />
          </view>
        </view>
        
        <!-- 每包数量 -->
        <view class="form-item">
          <text class="label">每包数量</text>
          <u-input v-model="bundleSize" type="number" placeholder="输入每包数量" />
        </view>
        
        <!-- 操作按钮 -->
        <view class="form-actions">
          <u-button type="primary" text="生成扎包" @click="generateBundles" :loading="generating" />
        </view>
      </view>
    </view>
    
    <!-- 已生成扎包 -->
    <view class="bundle-list" v-if="generatedBundles.length > 0">
      <view class="section-title">已生成扎包 ({{ generatedBundles.length }})</view>
      <scroll-view scroll-y class="bundle-items">
        <view class="bundle-item" v-for="item in generatedBundles" :key="item.id">
          <view class="bundle-no">{{ item.bundleNo }}</view>
          <view class="bundle-info">{{ item.color }} / {{ item.size }} × {{ item.quantity }}</view>
          <view class="bundle-actions">
            <u-button size="small" text="打印" @click="printBundle(item)" />
          </view>
        </view>
      </scroll-view>
      <view class="batch-actions">
        <u-button type="primary" text="批量打印" @click="batchPrint" />
        <u-button text="确认入库" @click="confirmInbound" />
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getPendingOrders, generateBundlesApi, printBundleLabel } from '@/api/cutter'

const pendingOrders = ref([])
const selectedOrder = ref(null)
const bundleSize = ref(50)
const generating = ref(false)
const generatedBundles = ref([])

onLoad(() => {
  loadPendingOrders()
})

const loadPendingOrders = async () => {
  const res = await getPendingOrders()
  if (res.success) {
    pendingOrders.value = res.data
  }
}

const selectOrder = (order) => {
  selectedOrder.value = order
  generatedBundles.value = []
}

const generateBundles = async () => {
  if (!selectedOrder.value) return
  
  generating.value = true
  try {
    const skus = selectedOrder.value.skus.filter(s => s.bundleQty > 0)
    if (skus.length === 0) {
      uni.showToast({ title: '请选择要分包的规格', icon: 'none' })
      return
    }
    
    const res = await generateBundlesApi({
      orderId: selectedOrder.value.id,
      bundleSize: bundleSize.value,
      skus: skus.map(s => ({ skuId: s.id, quantity: s.bundleQty }))
    })
    
    if (res.success) {
      generatedBundles.value = res.data
      uni.showToast({ title: `成功生成 ${res.data.length} 个扎包`, icon: 'success' })
    }
  } finally {
    generating.value = false
  }
}

const printBundle = async (bundle) => {
  await printBundleLabel(bundle.id)
  uni.showToast({ title: '打印成功', icon: 'success' })
}

const batchPrint = async () => {
  for (const bundle of generatedBundles.value) {
    await printBundleLabel(bundle.id)
  }
  uni.showToast({ title: '批量打印完成', icon: 'success' })
}

const confirmInbound = () => {
  uni.showModal({
    title: '确认入库',
    content: `确认将 ${generatedBundles.value.length} 个扎包入库？`,
    success: async (res) => {
      if (res.confirm) {
        // 调用入库接口
        uni.showToast({ title: '入库成功', icon: 'success' })
        generatedBundles.value = []
        selectedOrder.value = null
        loadPendingOrders()
      }
    }
  })
}
</script>
```

#### 21.5.4 质检员页面

##### 首页

**文件路径：** `pages/inspector/index.vue`

```vue
<template>
  <view class="container">
    <!-- 质检统计 -->
    <view class="stats-card">
      <view class="stats-row">
        <view class="stat-item">
          <text class="value">{{ stats.total }}</text>
          <text class="label">今日质检</text>
        </view>
        <view class="stat-item">
          <text class="value success">{{ stats.passed }}</text>
          <text class="label">合格</text>
        </view>
        <view class="stat-item">
          <text class="value danger">{{ stats.failed }}</text>
          <text class="label">不合格</text>
        </view>
        <view class="stat-item">
          <text class="value warning">{{ stats.passRate }}%</text>
          <text class="label">合格率</text>
        </view>
      </view>
    </view>
    
    <!-- 快捷操作 -->
    <view class="quick-actions">
      <view class="action-item" @click="goToCheck">
        <u-icon name="scan" size="40" color="#1890ff" />
        <text>扫码质检</text>
      </view>
      <view class="action-item" @click="goToFirstArticle">
        <u-icon name="checkmark-circle" size="40" color="#52c41a" />
        <text>首件确认</text>
      </view>
      <view class="action-item" @click="goToRework">
        <u-icon name="reload" size="40" color="#fa8c16" />
        <text>返工处理</text>
      </view>
      <view class="action-item" @click="goToHistory">
        <u-icon name="list" size="40" color="#722ed1" />
        <text>质检记录</text>
      </view>
    </view>
    
    <!-- 待办事项 -->
    <view class="pending-section">
      <view class="section-title">待处理事项</view>
      
      <!-- 待确认首件 -->
      <view class="pending-group" v-if="pendingFirstArticles.length > 0">
        <view class="group-title">
          <text>待确认首件</text>
          <text class="badge">{{ pendingFirstArticles.length }}</text>
        </view>
        <view class="pending-list">
          <view class="pending-item" v-for="item in pendingFirstArticles" :key="item.id" @click="confirmFirstArticle(item)">
            <view class="item-info">
              <text class="item-no">{{ item.confirmationNo }}</text>
              <text class="item-desc">{{ item.styleCode }} - {{ item.color }}/{{ item.size }}</text>
            </view>
            <view class="item-status waiting">待确认</view>
          </view>
        </view>
      </view>
      
      <!-- 待处理缺陷 -->
      <view class="pending-group" v-if="pendingDefects.length > 0">
        <view class="group-title">
          <text>待处理缺陷</text>
          <text class="badge danger">{{ pendingDefects.length }}</text>
        </view>
        <view class="pending-list">
          <view class="pending-item" v-for="item in pendingDefects" :key="item.id" @click="handleDefect(item)">
            <view class="item-info">
              <text class="item-no">{{ item.defectNo }}</text>
              <text class="item-desc">{{ item.defectType }} - {{ item.position }}</text>
            </view>
            <view class="item-status danger">待处理</view>
          </view>
        </view>
      </view>
      
      <u-empty v-if="pendingFirstArticles.length === 0 && pendingDefects.length === 0" text="暂无待处理事项" />
    </view>
  </view>
</template>
```

##### 扫码质检页

**文件路径：** `pages/inspector/check.vue`

```vue
<template>
  <view class="container">
    <!-- 扫码区域 -->
    <view class="scan-section">
      <u-input v-model="bundleNo" placeholder="请输入或扫描扎包号" @confirm="handleScan">
        <template #suffix>
          <u-icon name="scan" size="40" @click="scanQRCode" />
        </template>
      </u-input>
    </view>
    
    <!-- 扎包信息 -->
    <view class="bundle-info" v-if="bundleInfo">
      <view class="info-card">
        <view class="info-row">
          <text class="label">扎包号</text>
          <text class="value">{{ bundleInfo.bundleNo }}</text>
        </view>
        <view class="info-row">
          <text class="label">款号</text>
          <text class="value">{{ bundleInfo.styleCode }}</text>
        </view>
        <view class="info-row">
          <text class="label">数量</text>
          <text class="value">{{ bundleInfo.quantity }}</text>
        </view>
        <view class="info-row">
          <text class="label">当前工序</text>
          <text class="value">{{ bundleInfo.currentProcess }}</text>
        </view>
      </view>
      
      <!-- 质检表单 -->
      <view class="check-form">
        <view class="form-row">
          <text class="label">检验类型</text>
          <u-radio-group v-model="checkType">
            <u-radio label="抽检" name="sample" />
            <u-radio label="全检" name="full" />
          </u-radio-group>
        </view>
        
        <view class="form-row">
          <text class="label">检验数量</text>
          <u-number-box v-model="checkQuantity" :max="bundleInfo.quantity" />
        </view>
        
        <view class="form-row">
          <text class="label">合格数量</text>
          <u-number-box v-model="qualifiedQuantity" :max="checkQuantity" />
        </view>
        
        <view class="form-row">
          <text class="label">质检结果</text>
          <u-radio-group v-model="checkResult">
            <u-radio label="合格" name="passed" />
            <u-radio label="返工" name="rework" />
            <u-radio label="报废" name="scrap" />
          </u-radio-group>
        </view>
        
        <!-- 缺陷类型（返工/报废时显示） -->
        <view class="form-row" v-if="checkResult !== 'passed'">
          <text class="label">缺陷类型</text>
          <u-checkbox-group v-model="defectTypes">
            <u-checkbox label="尺寸不符" name="尺寸不符" />
            <u-checkbox label="色差" name="色差" />
            <u-checkbox label="线头" name="线头" />
            <u-checkbox label="污渍" name="污渍" />
            <u-checkbox label="破损" name="破损" />
            <u-checkbox label="工艺问题" name="工艺问题" />
          </u-checkbox-group>
        </view>
        
        <view class="form-row">
          <text class="label">备注</text>
          <u-textarea v-model="remark" placeholder="请输入备注信息" />
        </view>
        
        <view class="form-row">
          <text class="label">拍照上传</text>
          <u-upload :fileList="imageList" @afterRead="afterRead" @delete="deleteImage" maxCount="5" />
        </view>
      </view>
      
      <!-- 提交按钮 -->
      <view class="submit-section">
        <u-button type="primary" text="提交质检" @click="handleSubmit" :loading="submitting" />
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { getBundleInfo, submitQualityCheck } from '@/api/inspector'

const bundleNo = ref('')
const bundleInfo = ref(null)
const checkType = ref('sample')
const checkQuantity = ref(1)
const qualifiedQuantity = ref(1)
const checkResult = ref('passed')
const defectTypes = ref([])
const remark = ref('')
const imageList = ref([])
const submitting = ref(false)

const scanQRCode = () => {
  uni.scanCode({
    success: (res) => {
      bundleNo.value = res.result
      handleScan()
    }
  })
}

const handleScan = async () => {
  if (!bundleNo.value) return
  
  const res = await getBundleInfo(bundleNo.value)
  if (res.success) {
    bundleInfo.value = res.data
    checkQuantity.value = Math.min(5, res.data.quantity)
    qualifiedQuantity.value = checkQuantity.value
  }
}

const handleSubmit = async () => {
  if (!bundleInfo.value) return
  
  submitting.value = true
  try {
    await submitQualityCheck({
      bundleNo: bundleInfo.value.bundleNo,
      checkType: checkType.value,
      checkQuantity: checkQuantity.value,
      qualifiedQuantity: qualifiedQuantity.value,
      result: checkResult.value,
      defectTypes: defectTypes.value,
      remark: remark.value,
      images: imageList.value.map(i => i.url)
    })
    
    uni.showToast({ title: '提交成功', icon: 'success' })
    resetForm()
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  bundleNo.value = ''
  bundleInfo.value = null
  checkType.value = 'sample'
  checkQuantity.value = 1
  qualifiedQuantity.value = 1
  checkResult.value = 'passed'
  defectTypes.value = []
  remark.value = ''
  imageList.value = []
}
</script>
```

#### 21.5.5 仓库员页面

##### 首页

**文件路径：** `pages/warehouse/index.vue`

```vue
<template>
  <view class="container">
    <!-- 库存概览 -->
    <view class="overview-card">
      <view class="overview-item">
        <text class="value">{{ overview.totalSku }}</text>
        <text class="label">SKU数</text>
      </view>
      <view class="overview-item">
        <text class="value">{{ overview.totalQuantity }}</text>
        <text class="label">总库存</text>
      </view>
      <view class="overview-item">
        <text class="value warning">{{ overview.warningCount }}</text>
        <text class="label">库存预警</text>
      </view>
    </view>
    
    <!-- 快捷操作 -->
    <view class="quick-actions">
      <view class="action-item" @click="goToIn">
        <u-icon name="arrow-down" size="40" color="#52c41a" />
        <text>成品入库</text>
      </view>
      <view class="action-item" @click="goToOut">
        <u-icon name="arrow-up" size="40" color="#1890ff" />
        <text>物料出库</text>
      </view>
      <view class="action-item" @click="goToStock">
        <u-icon name="search" size="40" color="#722ed1" />
        <text>库存查询</text>
      </view>
      <view class="action-item" @click="goToInventory">
        <u-icon name="list" size="40" color="#fa8c16" />
        <text>盘点任务</text>
      </view>
    </view>
    
    <!-- 待处理事项 -->
    <view class="pending-section">
      <view class="section-title">待处理事项</view>
      <view class="pending-list">
        <view class="pending-item" v-for="item in pendingList" :key="item.id" @click="handlePending(item)">
          <view class="item-left">
            <text class="item-type">{{ item.type }}</text>
            <text class="item-no">{{ item.no }}</text>
          </view>
          <view class="item-right">
            <text class="item-time">{{ item.createdAt }}</text>
          </view>
        </view>
        <u-empty v-if="pendingList.length === 0" text="暂无待处理事项" />
      </view>
    </view>
  </view>
</template>
```

##### 成品入库页

**文件路径：** `pages/warehouse/in.vue`

```vue
<template>
  <view class="container">
    <!-- 扫码区域 -->
    <view class="scan-section">
      <u-input v-model="bundleNo" placeholder="扫描扎包号入库" @confirm="handleScan">
        <template #suffix>
          <u-icon name="scan" size="40" @click="scanQRCode" />
        </template>
      </u-input>
    </view>
    
    <!-- 扎包信息 -->
    <view class="bundle-info" v-if="bundleInfo">
      <view class="info-card">
        <view class="info-row">
          <text class="label">扎包号</text>
          <text class="value">{{ bundleInfo.bundleNo }}</text>
        </view>
        <view class="info-row">
          <text class="label">款号</text>
          <text class="value">{{ bundleInfo.styleCode }}</text>
        </view>
        <view class="info-row">
          <text class="label">颜色/尺码</text>
          <text class="value">{{ bundleInfo.color }} / {{ bundleInfo.size }}</text>
        </view>
        <view class="info-row">
          <text class="label">数量</text>
          <text class="value highlight">{{ bundleInfo.quantity }}</text>
        </view>
        <view class="info-row">
          <text class="label">质检状态</text>
          <text class="value" :class="bundleInfo.qcStatus">{{ getQcStatusText(bundleInfo.qcStatus) }}</text>
        </view>
      </view>
      
      <!-- 入库信息 -->
      <view class="inbound-form">
        <view class="form-row">
          <text class="label">入库仓库</text>
          <u-picker :columns="warehouseList" @confirm="selectWarehouse">
            <u-input v-model="selectedWarehouse.name" placeholder="选择仓库" disabled />
          </u-picker>
        </view>
        
        <view class="form-row">
          <text class="label">库位</text>
          <u-input v-model="location" placeholder="输入库位" />
        </view>
        
        <view class="form-row">
          <text class="label">备注</text>
          <u-textarea v-model="remark" placeholder="入库备注" />
        </view>
      </view>
      
      <!-- 提交按钮 -->
      <view class="submit-section">
        <u-button type="primary" text="确认入库" @click="handleInbound" :loading="submitting" />
      </view>
    </view>
    
    <!-- 今日入库记录 -->
    <view class="records-section">
      <view class="section-title">今日入库记录</view>
      <view class="record-list">
        <view class="record-item" v-for="item in todayRecords" :key="item.id">
          <view class="record-left">
            <text class="bundle-no">{{ item.bundleNo }}</text>
            <text class="style-code">{{ item.styleCode }}</text>
          </view>
          <view class="record-right">
            <text class="quantity">×{{ item.quantity }}</text>
            <text class="time">{{ item.createdAt }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>
```

#### 21.5.6 班组长页面

##### 首页

**文件路径：** `pages/leader/index.vue`

```vue
<template>
  <view class="container">
    <!-- 任务概览 -->
    <view class="overview-card">
      <view class="overview-item">
        <text class="value">{{ overview.totalTasks }}</text>
        <text class="label">总任务</text>
      </view>
      <view class="overview-item">
        <text class="value warning">{{ overview.pendingTasks }}</text>
        <text class="label">待分配</text>
      </view>
      <view class="overview-item">
        <text class="value success">{{ overview.completedTasks }}</text>
        <text class="label">已完成</text>
      </view>
    </view>
    
    <!-- 快捷操作 -->
    <view class="quick-actions">
      <view class="action-item" @click="goToAssign">
        <u-icon name="share" size="40" color="#1890ff" />
        <text>任务分配</text>
      </view>
      <view class="action-item" @click="goToProgress">
        <u-icon name="chart" size="40" color="#52c41a" />
        <text>生产进度</text>
      </view>
      <view class="action-item" @click="goToException">
        <u-icon name="warning" size="40" color="#fa8c16" />
        <text>异常处理</text>
      </view>
      <view class="action-item" @click="goToApproval">
        <u-icon name="checkmark" size="40" color="#722ed1" />
        <text>审批待办</text>
      </view>
    </view>
    
    <!-- 今日生产进度 -->
    <view class="progress-section">
      <view class="section-title">今日生产进度</view>
      <view class="order-list">
        <view class="order-item" v-for="item in orderProgress" :key="item.id">
          <view class="order-header">
            <text class="order-no">{{ item.orderNo }}</text>
            <text class="priority" :class="item.priority">{{ getPriorityText(item.priority) }}</text>
          </view>
          <view class="order-progress">
            <u-line-progress :percentage="item.progress" />
            <text class="progress-text">{{ item.completedQuantity }}/{{ item.totalQuantity }}</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 待审批事项 -->
    <view class="approval-section" v-if="pendingApprovals.length > 0">
      <view class="section-title">
        <text>待审批</text>
        <text class="badge">{{ pendingApprovals.length }}</text>
      </view>
      <view class="approval-list">
        <view class="approval-item" v-for="item in pendingApprovals" :key="item.id" @click="handleApproval(item)">
          <view class="approval-info">
            <text class="approval-type">{{ item.type }}</text>
            <text class="approval-no">{{ item.no }}</text>
          </view>
          <text class="approval-time">{{ item.createdAt }}</text>
        </view>
      </view>
    </view>
  </view>
</template>
```

##### 任务分配页

**文件路径：** `pages/leader/assign.vue`

```vue
<template>
  <view class="container">
    <!-- 待分配扎包 -->
    <view class="bundle-section">
      <view class="section-title">待分配扎包</view>
      <view class="bundle-list">
        <view 
          class="bundle-item"
          :class="{ selected: selectedBundles.includes(item.id) }"
          v-for="item in pendingBundles"
          :key="item.id"
          @click="toggleBundle(item.id)"
        >
          <view class="bundle-header">
            <u-checkbox :checked="selectedBundles.includes(item.id)" />
            <text class="bundle-no">{{ item.bundleNo }}</text>
          </view>
          <view class="bundle-info">
            <text>{{ item.styleCode }} - {{ item.processName }}</text>
            <text>数量：{{ item.quantity }}</text>
          </view>
        </view>
        <u-empty v-if="pendingBundles.length === 0" text="暂无待分配扎包" />
      </view>
    </view>
    
    <!-- 分配表单 -->
    <view class="assign-form" v-if="selectedBundles.length > 0">
      <view class="form-row">
        <text class="label">选择员工</text>
        <u-picker :columns="workerList" @confirm="selectWorker">
          <u-input v-model="selectedWorker.name" placeholder="选择员工" disabled />
        </u-picker>
      </view>
      
      <view class="form-row">
        <text class="label">要求完成时间</text>
        <u-datetime-picker v-model="deadline" mode="datetime">
          <u-input :value="formatDeadline" placeholder="选择时间" disabled />
        </u-datetime-picker>
      </view>
      
      <view class="form-row">
        <text class="label">备注</text>
        <u-textarea v-model="remark" placeholder="分配备注" />
      </view>
      
      <view class="form-actions">
        <u-button type="primary" text="确认分配" @click="handleAssign" :loading="assigning" />
      </view>
    </view>
  </view>
</template>
```

#### 21.5.7 车间主任页面

##### 生产看板页

**文件路径：** `pages/manager/dashboard.vue`

```vue
<template>
  <view class="container">
    <!-- 统计卡片 -->
    <view class="stats-grid">
      <view class="stats-card">
        <text class="value">{{ stats.producingOrders }}</text>
        <text class="label">生产中订单</text>
      </view>
      <view class="stats-card">
        <text class="value">{{ stats.todayOutput }}</text>
        <text class="label">今日产量</text>
      </view>
      <view class="stats-card">
        <text class="value">{{ stats.weekOutput }}</text>
        <text class="label">本周产量</text>
      </view>
      <view class="stats-card">
        <text class="value">{{ stats.passRate }}%</text>
        <text class="label">合格率</text>
      </view>
    </view>
    
    <!-- 生产看板 -->
    <view class="kanban-section">
      <view class="kanban-header">
        <text class="title">生产看板</text>
        <view class="filter">
          <u-tag text="全部" type="primary" />
          <u-tag text="待开工" />
          <u-tag text="生产中" />
          <u-tag text="已完成" />
        </view>
      </view>
      
      <scroll-view scroll-x class="kanban-columns">
        <!-- 待开工列 -->
        <view class="kanban-column pending">
          <view class="column-header">
            <text>待开工</text>
            <text class="count">{{ pendingOrders.length }}</text>
          </view>
          <view class="column-content">
            <view class="order-card" v-for="item in pendingOrders" :key="item.id" @click="viewOrder(item)">
              <view class="order-no">{{ item.orderNo }}</view>
              <view class="order-style">{{ item.styleCode }}</view>
              <view class="order-qty">数量：{{ item.quantity }}</view>
              <view class="order-priority" :class="item.priority">{{ getPriorityText(item.priority) }}</view>
            </view>
          </view>
        </view>
        
        <!-- 生产中列 -->
        <view class="kanban-column producing">
          <view class="column-header">
            <text>生产中</text>
            <text class="count">{{ producingOrders.length }}</text>
          </view>
          <view class="column-content">
            <view class="order-card" v-for="item in producingOrders" :key="item.id" @click="viewOrder(item)">
              <view class="order-no">{{ item.orderNo }}</view>
              <view class="order-style">{{ item.styleCode }}</view>
              <view class="order-progress">
                <u-line-progress :percentage="item.progress" />
              </view>
              <view class="order-process">当前：{{ item.currentProcess }}</view>
            </view>
          </view>
        </view>
        
        <!-- 已完成列 -->
        <view class="kanban-column completed">
          <view class="column-header">
            <text>已完成</text>
            <text class="count">{{ completedOrders.length }}</text>
          </view>
          <view class="column-content">
            <view class="order-card" v-for="item in completedOrders" :key="item.id" @click="viewOrder(item)">
              <view class="order-no">{{ item.orderNo }}</view>
              <view class="order-style">{{ item.styleCode }}</view>
              <view class="order-qty">完成：{{ item.quantity }}</view>
              <view class="order-status success">已完成</view>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>
    
    <!-- 产量趋势 -->
    <view class="trend-section">
      <view class="section-title">产量趋势（近7天）</view>
      <view class="chart-container">
        <qiun-ucharts type="line" :opts="chartOpts" :chartData="chartData" />
      </view>
    </view>
  </view>
</template>
```

#### 21.5.8 老板/经营者页面

##### 经营大屏页

**文件路径：** `pages/boss/dashboard.vue`

```vue
<template>
  <view class="container dashboard">
    <!-- 核心指标 -->
    <view class="kpi-grid">
      <view class="kpi-card">
        <text class="kpi-value">{{ kpi.monthOutput }}</text>
        <text class="kpi-label">本月产量</text>
        <text class="kpi-trend up">↑ {{ kpi.monthOutputGrowth }}%</text>
      </view>
      <view class="kpi-card">
        <text class="kpi-value">¥{{ kpi.monthRevenue }}</text>
        <text class="kpi-label">本月产值</text>
        <text class="kpi-trend up">↑ {{ kpi.monthRevenueGrowth }}%</text>
      </view>
      <view class="kpi-card">
        <text class="kpi-value">{{ kpi.passRate }}%</text>
        <text class="kpi-label">合格率</text>
      </view>
      <view class="kpi-card">
        <text class="kpi-value">{{ kpi.onTimeRate }}%</text>
        <text class="kpi-label">准时交付率</text>
      </view>
    </view>
    
    <!-- 生产进度概览 -->
    <view class="production-overview">
      <view class="section-title">生产进度概览</view>
      <view class="progress-list">
        <view class="progress-item" v-for="item in productionProgress" :key="item.id">
          <view class="progress-header">
            <text class="order-no">{{ item.orderNo }}</text>
            <text class="customer">{{ item.customerName }}</text>
          </view>
          <view class="progress-bar">
            <u-line-progress :percentage="item.progress" :showText="false" />
            <text class="progress-text">{{ item.progress }}%</text>
          </view>
          <view class="progress-footer">
            <text>交付日期：{{ item.deliveryDate }}</text>
            <text :class="item.isDelayed ? 'delayed' : ''">
              {{ item.isDelayed ? '已延期' : '正常' }}
            </text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 预警信息 -->
    <view class="alert-section" v-if="alerts.length > 0">
      <view class="section-title">
        <text>预警信息</text>
        <text class="alert-count">{{ alerts.length }}</text>
      </view>
      <view class="alert-list">
        <view class="alert-item" v-for="item in alerts" :key="item.id" :class="item.level">
          <view class="alert-icon">
            <u-icon :name="getAlertIcon(item.level)" size="20" />
          </view>
          <view class="alert-content">
            <text class="alert-title">{{ item.title }}</text>
            <text class="alert-desc">{{ item.description }}</text>
          </view>
          <text class="alert-time">{{ item.createdAt }}</text>
        </view>
      </view>
    </view>
  </view>
</template>
```

### 21.6 公共组件设计

#### 21.6.1 扎包卡片组件

**文件路径：** `components/bundle-card/bundle-card.vue`

```vue
<template>
  <view class="bundle-card" @click="handleClick">
    <view class="card-header">
      <text class="bundle-no">{{ bundle.bundleNo }}</text>
      <view class="bundle-status" :class="bundle.status">{{ getStatusText(bundle.status) }}</view>
    </view>
    <view class="card-body">
      <view class="info-row">
        <text class="label">款号：</text>
        <text class="value">{{ bundle.styleCode }}</text>
      </view>
      <view class="info-row">
        <text class="label">颜色/尺码：</text>
        <text class="value">{{ bundle.color }} / {{ bundle.size }}</text>
      </view>
      <view class="info-row">
        <text class="label">数量：</text>
        <text class="value highlight">{{ bundle.quantity }}</text>
      </view>
    </view>
    <view class="card-footer" v-if="showActions">
      <u-button size="small" text="详情" @click.stop="handleDetail" />
      <u-button size="small" type="primary" text="操作" @click.stop="handleAction" />
    </view>
  </view>
</template>

<script setup>
const props = defineProps({
  bundle: {
    type: Object,
    required: true
  },
  showActions: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['click', 'detail', 'action'])

const handleClick = () => emit('click', props.bundle)
const handleDetail = () => emit('detail', props.bundle)
const handleAction = () => emit('action', props.bundle)

const getStatusText = (status) => {
  const map = {
    pending: '待开工',
    producing: '生产中',
    completed: '已完成'
  }
  return map[status] || status
}
</script>
```

#### 21.6.2 工序选择器组件

**文件路径：** `components/process-picker/process-picker.vue`

```vue
<template>
  <u-popup v-model:show="visible" mode="bottom" :round="10">
    <view class="process-picker">
      <view class="picker-header">
        <text class="title">选择工序</text>
        <u-icon name="close" @click="close" />
      </view>
      
      <!-- 搜索框 -->
      <view class="search-box">
        <u-input v-model="keyword" placeholder="搜索工序" clearable>
          <template #prefix>
            <u-icon name="search" />
          </template>
        </u-input>
      </view>
      
      <!-- 工序列表 -->
      <scroll-view scroll-y class="process-list">
        <view 
          class="process-item"
          :class="{ selected: selected?.id === item.id }"
          v-for="item in filteredProcesses"
          :key="item.id"
          @click="selectProcess(item)"
        >
          <view class="process-info">
            <text class="process-name">{{ item.name }}</text>
            <text class="process-category">{{ item.categoryName }}</text>
          </view>
          <view class="process-price">¥{{ item.price }}/件</view>
          <u-icon v-if="selected?.id === item.id" name="checkmark" color="#1890ff" />
        </view>
        <u-empty v-if="filteredProcesses.length === 0" text="暂无数据" />
      </scroll-view>
      
      <!-- 确认按钮 -->
      <view class="picker-footer">
        <u-button type="primary" text="确认" @click="confirm" />
      </view>
    </view>
  </u-popup>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  show: Boolean,
  processes: {
    type: Array,
    default: () => []
  },
  modelValue: Object
})

const emit = defineEmits(['update:show', 'update:modelValue', 'confirm'])

const visible = computed({
  get: () => props.show,
  set: (val) => emit('update:show', val)
})

const keyword = ref('')
const selected = ref(props.modelValue)

const filteredProcesses = computed(() => {
  if (!keyword.value) return props.processes
  return props.processes.filter(p => 
    p.name.includes(keyword.value) || p.code?.includes(keyword.value)
  )
})

const selectProcess = (process) => {
  selected.value = process
}

const confirm = () => {
  emit('update:modelValue', selected.value)
  emit('confirm', selected.value)
  close()
}

const close = () => {
  visible.value = false
}
</script>
```

#### 21.6.3 扫码输入框组件

**文件路径：** `components/scan-input/scan-input.vue`

```vue
<template>
  <view class="scan-input">
    <u-input 
      v-model="inputValue"
      :placeholder="placeholder"
      :focus="focus"
      @confirm="handleConfirm"
      @blur="handleBlur"
    >
      <template #prefix>
        <u-icon name="scan" size="20" @click="handleScan" />
      </template>
      <template #suffix>
        <u-button size="small" type="primary" text="扫码" @click="handleScan" />
      </template>
    </u-input>
  </view>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: String,
  placeholder: {
    type: String,
    default: '请输入或扫描'
  },
  autoScan: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['update:modelValue', 'scan', 'confirm'])

const inputValue = ref(props.modelValue)
const focus = ref(false)

watch(() => props.modelValue, (val) => {
  inputValue.value = val
})

watch(inputValue, (val) => {
  emit('update:modelValue', val)
})

const handleScan = () => {
  uni.scanCode({
    success: (res) => {
      inputValue.value = res.result
      emit('scan', res.result)
      if (props.autoScan) {
        emit('confirm', res.result)
      }
    },
    fail: () => {
      uni.showToast({ title: '扫码失败', icon: 'none' })
    }
  })
}

const handleConfirm = () => {
  emit('confirm', inputValue.value)
}

const handleBlur = () => {
  focus.value = false
}

const setFocus = () => {
  focus.value = true
}

defineExpose({ setFocus })
</script>
```

### 21.7 离线支持设计

```javascript
// utils/offline.js
import { getStorageSync, setStorageSync } from '@dcloudio/uni-app'

class OfflineManager {
  constructor() {
    this.queueKey = 'offline_queue'
    this.queue = this.loadQueue()
    this.syncing = false
  }
  
  loadQueue() {
    return getStorageSync(this.queueKey) || []
  }
  
  saveQueue() {
    setStorageSync(this.queueKey, this.queue)
  }
  
  async addQueue(type, data) {
    const record = {
      id: this.generateId(),
      type,
      data,
      createdAt: Date.now(),
      status: 'pending',
      retryCount: 0
    }
    this.queue.push(record)
    this.saveQueue()
    return record.id
  }
  
  async sync() {
    if (this.syncing) return
    this.syncing = true
    
    const pending = this.queue.filter(r => r.status === 'pending')
    
    for (const record of pending) {
      try {
        await this.syncRecord(record)
        record.status = 'synced'
        record.syncedAt = Date.now()
      } catch (e) {
        record.retryCount++
        if (record.retryCount >= 3) {
          record.status = 'failed'
          record.error = e.message
        }
      }
    }
    
    this.saveQueue()
    this.syncing = false
  }
  
  async syncRecord(record) {
    const apiMap = {
      piecework: '/api/worker/piecework',
      quality: '/api/inspector/check',
      inbound: '/api/warehouse/in',
      outbound: '/api/warehouse/out'
    }
    
    const url = apiMap[record.type]
    if (!url) throw new Error('Unknown record type')
    
    return await this.request(url, 'POST', record.data)
  }
  
  generateId() {
    return 'offline_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
  }
  
  getPendingCount() {
    return this.queue.filter(r => r.status === 'pending').length
  }
  
  clearSynced() {
    this.queue = this.queue.filter(r => r.status !== 'synced')
    this.saveQueue()
  }
}

export const offlineManager = new OfflineManager()
```

### 21.8 蓝牙打印支持

```javascript
// utils/printer.js
class BluetoothPrinter {
  constructor() {
    this.deviceId = null
    this.serviceId = null
    this.characteristicId = null
    this.connected = false
  }
  
  async init() {
    try {
      await uni.openBluetoothAdapter()
      console.log('蓝牙初始化成功')
    } catch (e) {
      throw new Error('请开启蓝牙')
    }
  }
  
  async searchDevices() {
    return new Promise((resolve, reject) => {
      uni.startBluetoothDevicesDiscovery({
        success: () => {
          uni.onBluetoothDeviceFound((res) => {
            const devices = res.devices.filter(d => d.name && d.name.includes('Printer'))
            resolve(devices)
          })
        },
        fail: reject
      })
    })
  }
  
  async connect(deviceId) {
    try {
      await uni.createBLEConnection({ deviceId })
      this.deviceId = deviceId
      
      const services = await uni.getBLEDeviceServices({ deviceId })
      this.serviceId = services.services[0].uuid
      
      const characteristics = await uni.getBLEDeviceCharacteristics({
        deviceId,
        serviceId: this.serviceId
      })
      this.characteristicId = characteristics.characteristics[0].uuid
      
      this.connected = true
      return true
    } catch (e) {
      console.error('连接失败:', e)
      return false
    }
  }
  
  async printBundleLabel(bundle) {
    if (!this.connected) {
      throw new Error('打印机未连接')
    }
    
    const commands = this.generateLabelCommands(bundle)
    
    for (const cmd of commands) {
      await uni.writeBLECharacteristicValue({
        deviceId: this.deviceId,
        serviceId: this.serviceId,
        characteristicId: this.characteristicId,
        value: cmd
      })
    }
  }
  
  generateLabelCommands(bundle) {
    // TSPL 打印指令
    const cmd = `
      SIZE 50 mm, 30 mm
      GAP 2 mm, 0 mm
      DIRECTION 1
      CLS
      TEXT 10,10,"2",0,1,1,"${bundle.bundleNo}"
      TEXT 10,40,"1",0,1,1,"款号:${bundle.styleCode}"
      TEXT 10,65,"1",0,1,1,"${bundle.color}/${bundle.size}"
      TEXT 10,90,"1",0,1,1,"数量:${bundle.quantity}"
      QRCODE 150,50,H,3,A,0,"${bundle.bundleNo}"
      PRINT 1
    `
    return [this.stringToBuffer(cmd)]
  }
  
  stringToBuffer(str) {
    const buffer = new ArrayBuffer(str.length)
    const dataView = new DataView(buffer)
    for (let i = 0; i < str.length; i++) {
      dataView.setUint8(i, str.charCodeAt(i))
    }
    return buffer
  }
  
  disconnect() {
    if (this.deviceId) {
      uni.closeBLEConnection({ deviceId: this.deviceId })
      this.connected = false
    }
  }
}

export const bluetoothPrinter = new BluetoothPrinter()
```

### 21.9 页面统计

| 类别 | 数量 |
|------|------|
| 角色 | 7 |
| 公共页面 | 5 |
| 缝制工页面 | 5 |
| 裁床工页面 | 5 |
| 质检员页面 | 5 |
| 仓库员页面 | 5 |
| 班组长页面 | 5 |
| 车间主任页面 | 5 |
| 老板页面 | 4 |
| 公共组件 | 4 |
| **总页面数** | **39** |

---

## 22. 数据库DDL设计

### 22.1 数据库选型

| 组件 | 技术 | 版本 | 用途 |
|------|------|------|------|
| 主数据库 | MySQL | 8.0+ | 业务数据存储 |
| 缓存 | Redis | 7.0+ | 缓存、会话、分布式锁 |
| 时序数据 | TimescaleDB | 2.x | 设备数据、传感器数据（可选） |
| 文档存储 | MongoDB | 6.0+ | 日志、附件元数据（可选） |

### 22.2 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 表名 | 小写下划线，模块前缀 | `prod_process_library` |
| 主键 | `id`，BIGINT | `id BIGINT PRIMARY KEY` |
| 外键 | `{表名}_id` | `order_id`, `worker_id` |
| 索引 | `idx_{表名}_{字段}` | `idx_prod_order_status` |
| 唯一索引 | `uk_{表名}_{字段}` | `uk_sys_user_username` |
| 时间字段 | `_at` 后缀 | `created_at`, `updated_at` |
| 状态字段 | `_status` 后缀 | `order_status`, `pay_status` |

### 22.3 核心表DDL

#### 22.3.1 基础数据模块

```sql
-- =============================================
-- 组织架构
-- =============================================

CREATE TABLE sys_organization (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '组织ID',
    org_code VARCHAR(50) NOT NULL COMMENT '组织编码',
    org_name VARCHAR(100) NOT NULL COMMENT '组织名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父组织ID',
    org_type TINYINT NOT NULL COMMENT '组织类型: 1-公司 2-工厂 3-车间 4-班组',
    leader_id BIGINT COMMENT '负责人ID',
    phone VARCHAR(20) COMMENT '联系电话',
    address VARCHAR(200) COMMENT '地址',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_org_code (org_code),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织架构表';

-- =============================================
-- 用户与权限
-- =============================================

CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(加密)',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    gender TINYINT DEFAULT 0 COMMENT '性别: 0-未知 1-男 2-女',
    avatar VARCHAR(200) COMMENT '头像URL',
    org_id BIGINT COMMENT '所属组织ID',
    org_name VARCHAR(100) COMMENT '所属组织名称',
    role_codes VARCHAR(500) COMMENT '角色编码列表(JSON)',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    last_login_at DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    password_updated_at DATETIME COMMENT '密码修改时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_phone (phone),
    KEY idx_org_id (org_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_type VARCHAR(20) NOT NULL COMMENT '角色类型: worker/cutter/inspector/warehouse/leader/manager/boss/admin',
    description VARCHAR(200) COMMENT '角色描述',
    data_scope TINYINT DEFAULT 1 COMMENT '数据权限: 1-全部 2-本部门 3-本部门及下级 4-仅本人',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

CREATE TABLE sys_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜单ID',
    menu_code VARCHAR(50) NOT NULL COMMENT '菜单编码',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
    menu_type TINYINT NOT NULL COMMENT '类型: 1-目录 2-菜单 3-按钮',
    path VARCHAR(200) COMMENT '路由路径',
    component VARCHAR(200) COMMENT '组件路径',
    permission VARCHAR(100) COMMENT '权限标识',
    icon VARCHAR(50) COMMENT '图标',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    visible TINYINT DEFAULT 1 COMMENT '是否可见: 0-否 1-是',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_menu_code (menu_code),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

CREATE TABLE sys_role_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_role_menu (role_id, menu_id),
    KEY idx_menu_id (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- =============================================
-- 商品数据
-- =============================================

CREATE TABLE prod_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    product_code VARCHAR(50) NOT NULL COMMENT '商品编码',
    product_name VARCHAR(200) NOT NULL COMMENT '商品名称',
    product_type VARCHAR(20) NOT NULL COMMENT '商品类型: fabric-面料 accessory-辅料 finished-成品',
    category_id BIGINT COMMENT '分类ID',
    category_name VARCHAR(50) COMMENT '分类名称',
    brand VARCHAR(50) COMMENT '品牌',
    unit VARCHAR(20) DEFAULT '件' COMMENT '单位',
    specification VARCHAR(200) COMMENT '规格',
    color VARCHAR(50) COMMENT '颜色',
    size VARCHAR(20) COMMENT '尺码',
    cost_price DECIMAL(12,4) COMMENT '成本价',
    sale_price DECIMAL(12,2) COMMENT '销售价',
    barcode VARCHAR(50) COMMENT '条码',
    images TEXT COMMENT '图片(JSON)',
    description TEXT COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_product_code (product_code),
    KEY idx_category_id (category_id),
    KEY idx_product_type (product_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

CREATE TABLE prod_style (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '款号ID',
    style_code VARCHAR(50) NOT NULL COMMENT '款号',
    style_name VARCHAR(200) NOT NULL COMMENT '品名',
    season VARCHAR(20) COMMENT '季节: spring/summer/autumn/winter',
    year INT COMMENT '年份',
    category_id BIGINT COMMENT '品类ID',
    designer_id BIGINT COMMENT '设计师ID',
    fabric_id BIGINT COMMENT '主面料ID',
    sample_status TINYINT DEFAULT 0 COMMENT '样板状态: 0-未打样 1-打样中 2-已确认',
    tech_sheet_url VARCHAR(200) COMMENT '工艺单URL',
    bom_status TINYINT DEFAULT 0 COMMENT 'BOM状态: 0-未配置 1-已配置',
    process_config_status TINYINT DEFAULT 0 COMMENT '工序配置状态: 0-未配置 1-已配置',
    images TEXT COMMENT '图片(JSON)',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_style_code (style_code),
    KEY idx_season_year (season, year),
    KEY idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='款号表';

CREATE TABLE prod_style_sku (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'SKU ID',
    style_id BIGINT NOT NULL COMMENT '款号ID',
    style_code VARCHAR(50) NOT NULL COMMENT '款号',
    color VARCHAR(50) NOT NULL COMMENT '颜色',
    size VARCHAR(20) NOT NULL COMMENT '尺码',
    sku_code VARCHAR(50) NOT NULL COMMENT 'SKU编码',
    barcode VARCHAR(50) COMMENT '条码',
    cost_price DECIMAL(12,4) COMMENT '成本价',
    sale_price DECIMAL(12,2) COMMENT '销售价',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_sku_code (sku_code),
    KEY idx_style_id (style_id),
    KEY idx_style_code (style_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='款号SKU表';
```

#### 22.3.2 工序管理模块

```sql
-- =============================================
-- 工序管理
-- =============================================

CREATE TABLE prod_process_library (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '工序ID',
    process_code VARCHAR(50) NOT NULL COMMENT '工序编码',
    process_name VARCHAR(100) NOT NULL COMMENT '工序名称',
    category VARCHAR(20) NOT NULL COMMENT '工序分类: cutting/prep/main/post/finish/special',
    unit VARCHAR(20) DEFAULT 'piece' COMMENT '计件单位',
    standard_price DECIMAL(10,4) NOT NULL COMMENT '标准单价',
    estimated_time INT COMMENT '标准工时(分钟)',
    equipment_type VARCHAR(50) COMMENT '所需设备类型',
    quality_requirements TEXT COMMENT '质量要求',
    description TEXT COMMENT '工序说明',
    images TEXT COMMENT '图片/视频(JSON)',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_process_code (process_code),
    KEY idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工序库表';

CREATE TABLE prod_process_price_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    process_id BIGINT NOT NULL COMMENT '工序ID',
    old_price DECIMAL(10,4) COMMENT '原单价',
    new_price DECIMAL(10,4) NOT NULL COMMENT '新单价',
    change_reason VARCHAR(200) COMMENT '变更原因',
    effective_date DATE NOT NULL COMMENT '生效日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    KEY idx_process_id (process_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工序单价历史表';

CREATE TABLE prod_process_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    config_code VARCHAR(50) NOT NULL COMMENT '配置编码',
    style_id BIGINT NOT NULL COMMENT '款号ID',
    style_code VARCHAR(50) NOT NULL COMMENT '款号',
    factory_id BIGINT NOT NULL COMMENT '工厂ID',
    factory_name VARCHAR(100) COMMENT '工厂名称',
    total_price DECIMAL(12,2) DEFAULT 0 COMMENT '工序总价',
    process_count INT DEFAULT 0 COMMENT '工序数量',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-草稿 1-已发布',
    version INT DEFAULT 1 COMMENT '版本号',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_config_code (config_code),
    KEY idx_style_id (style_id),
    KEY idx_factory_id (factory_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工序配置表';

CREATE TABLE prod_process_config_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_id BIGINT NOT NULL COMMENT '配置ID',
    process_id BIGINT NOT NULL COMMENT '工序ID',
    process_code VARCHAR(50) COMMENT '工序编码',
    process_name VARCHAR(100) COMMENT '工序名称',
    category VARCHAR(20) COMMENT '工序分类',
    unit VARCHAR(20) COMMENT '单位',
    price DECIMAL(10,4) NOT NULL COMMENT '单价',
    estimated_time INT COMMENT '工时(分钟)',
    sort_order INT DEFAULT 0 COMMENT '排序',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_config_id (config_id),
    KEY idx_process_id (process_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工序配置明细表';
```

#### 22.3.3 生产管理模块

```sql
-- =============================================
-- 生产订单
-- =============================================

CREATE TABLE prod_production_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '生产订单ID',
    order_no VARCHAR(50) NOT NULL COMMENT '生产单号',
    sales_order_id BIGINT COMMENT '关联销售单ID',
    sales_order_no VARCHAR(50) COMMENT '销售单号',
    style_id BIGINT NOT NULL COMMENT '款号ID',
    style_code VARCHAR(50) NOT NULL COMMENT '款号',
    style_name VARCHAR(200) COMMENT '品名',
    factory_id BIGINT NOT NULL COMMENT '工厂ID',
    factory_name VARCHAR(100) COMMENT '工厂名称',
    workshop_id BIGINT COMMENT '车间ID',
    total_quantity INT NOT NULL COMMENT '总数量',
    completed_quantity INT DEFAULT 0 COMMENT '完成数量',
    defect_quantity INT DEFAULT 0 COMMENT '次品数量',
    progress DECIMAL(5,2) DEFAULT 0 COMMENT '进度(%)',
    plan_start_date DATE COMMENT '计划开始日期',
    plan_end_date DATE COMMENT '计划完成日期',
    actual_start_date DATE COMMENT '实际开始日期',
    actual_end_date DATE COMMENT '实际完成日期',
    order_status VARCHAR(20) DEFAULT 'draft' COMMENT '订单状态: draft/pending/producing/completed/cancelled',
    priority VARCHAR(20) DEFAULT 'medium' COMMENT '优先级: low/medium/high/urgent',
    process_config_id BIGINT COMMENT '工序配置ID',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_style_id (style_id),
    KEY idx_factory_id (factory_id),
    KEY idx_order_status (order_status),
    KEY idx_plan_end_date (plan_end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产订单表';

CREATE TABLE prod_production_order_sku (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    color VARCHAR(50) NOT NULL COMMENT '颜色',
    size VARCHAR(20) NOT NULL COMMENT '尺码',
    plan_quantity INT NOT NULL COMMENT '计划数量',
    completed_quantity INT DEFAULT 0 COMMENT '完成数量',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_order_id (order_id),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产订单SKU表';

-- =============================================
-- 扎包管理
-- =============================================

CREATE TABLE prod_bundle (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '扎包ID',
    bundle_no VARCHAR(50) NOT NULL COMMENT '扎包号',
    qr_code VARCHAR(100) COMMENT '二维码',
    order_id BIGINT NOT NULL COMMENT '生产订单ID',
    order_no VARCHAR(50) COMMENT '生产单号',
    style_id BIGINT NOT NULL COMMENT '款号ID',
    style_code VARCHAR(50) COMMENT '款号',
    style_name VARCHAR(200) COMMENT '品名',
    sku_id BIGINT COMMENT 'SKU ID',
    color VARCHAR(50) COMMENT '颜色',
    size VARCHAR(20) COMMENT '尺码',
    quantity INT NOT NULL COMMENT '数量',
    completed_quantity INT DEFAULT 0 COMMENT '完成数量',
    current_process_id BIGINT COMMENT '当前工序ID',
    current_process_name VARCHAR(100) COMMENT '当前工序名称',
    current_holder_id BIGINT COMMENT '当前持有者ID',
    current_holder_name VARCHAR(50) COMMENT '当前持有者姓名',
    bundle_status VARCHAR(20) DEFAULT 'pending' COMMENT '扎包状态: pending/producing/completed',
    cutting_id BIGINT COMMENT '裁床单ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_bundle_no (bundle_no),
    KEY idx_order_id (order_id),
    KEY idx_style_id (style_id),
    KEY idx_bundle_status (bundle_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='扎包表';

CREATE TABLE prod_bundle_process (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bundle_id BIGINT NOT NULL COMMENT '扎包ID',
    process_id BIGINT NOT NULL COMMENT '工序ID',
    process_name VARCHAR(100) COMMENT '工序名称',
    sort_order INT DEFAULT 0 COMMENT '排序',
    plan_quantity INT COMMENT '计划数量',
    completed_quantity INT DEFAULT 0 COMMENT '完成数量',
    process_status VARCHAR(20) DEFAULT 'pending' COMMENT '工序状态: pending/producing/completed',
    worker_id BIGINT COMMENT '操作员ID',
    worker_name VARCHAR(50) COMMENT '操作员姓名',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '完成时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_bundle_id (bundle_id),
    KEY idx_process_id (process_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='扎包工序表';

CREATE TABLE prod_bundle_flow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bundle_id BIGINT NOT NULL COMMENT '扎包ID',
    bundle_no VARCHAR(50) COMMENT '扎包号',
    process_id BIGINT COMMENT '工序ID',
    process_name VARCHAR(100) COMMENT '工序名称',
    action_type VARCHAR(20) NOT NULL COMMENT '操作类型: receive/transfer/complete/rework',
    from_worker_id BIGINT COMMENT '原操作员ID',
    from_worker_name VARCHAR(50) COMMENT '原操作员姓名',
    to_worker_id BIGINT COMMENT '目标操作员ID',
    to_worker_name VARCHAR(50) COMMENT '目标操作员姓名',
    quantity INT COMMENT '数量',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    KEY idx_bundle_id (bundle_id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='扎包流转记录表';
```

#### 22.3.4 计件工资模块

```sql
-- =============================================
-- 计件记录
-- =============================================

CREATE TABLE prod_piecework_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '计件记录ID',
    record_no VARCHAR(50) NOT NULL COMMENT '记录编号',
    request_id VARCHAR(50) NOT NULL COMMENT '请求ID(UUID防重)',
    bundle_id BIGINT NOT NULL COMMENT '扎包ID',
    bundle_no VARCHAR(50) COMMENT '扎包号',
    order_id BIGINT COMMENT '订单ID',
    order_no VARCHAR(50) COMMENT '生产单号',
    style_id BIGINT COMMENT '款号ID',
    style_code VARCHAR(50) COMMENT '款号',
    process_id BIGINT NOT NULL COMMENT '工序ID',
    process_name VARCHAR(100) COMMENT '工序名称',
    worker_id BIGINT NOT NULL COMMENT '工人ID',
    worker_name VARCHAR(50) COMMENT '工人姓名',
    quantity INT NOT NULL COMMENT '数量',
    unit_price DECIMAL(10,4) NOT NULL COMMENT '单价',
    amount DECIMAL(12,4) NOT NULL COMMENT '金额',
    settlement_status VARCHAR(20) DEFAULT 'pending' COMMENT '结算状态: pending/settled',
    settlement_id BIGINT COMMENT '结算单ID',
    settlement_date DATE COMMENT '结算日期',
    record_date DATE NOT NULL COMMENT '计件日期',
    is_rework TINYINT DEFAULT 0 COMMENT '是否返工',
    original_record_id BIGINT COMMENT '原始计件记录ID',
    remark VARCHAR(200) COMMENT '备注',
    offline_flag TINYINT DEFAULT 0 COMMENT '离线标识',
    sync_status VARCHAR(20) DEFAULT 'synced' COMMENT '同步状态: pending/synced/failed',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_request_id (request_id),
    KEY idx_bundle_id (bundle_id),
    KEY idx_worker_id (worker_id),
    KEY idx_process_id (process_id),
    KEY idx_record_date (record_date),
    KEY idx_settlement_status (settlement_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计件记录表';

-- =============================================
-- 工资结算
-- =============================================

CREATE TABLE wage_settlement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '结算单ID',
    settlement_no VARCHAR(50) NOT NULL COMMENT '结算单号',
    settlement_month VARCHAR(7) NOT NULL COMMENT '结算月份(YYYY-MM)',
    department_id BIGINT COMMENT '部门ID',
    worker_count INT DEFAULT 0 COMMENT '结算人数',
    total_quantity INT DEFAULT 0 COMMENT '总数量',
    total_amount DECIMAL(14,2) DEFAULT 0 COMMENT '计件总额',
    deduction_amount DECIMAL(12,4) DEFAULT 0 COMMENT '扣款总额',
    bonus_amount DECIMAL(12,4) DEFAULT 0 COMMENT '奖金总额',
    final_amount DECIMAL(14,2) DEFAULT 0 COMMENT '应发总额',
    settlement_status VARCHAR(20) DEFAULT 'draft' COMMENT '结算状态: draft/confirmed/paid',
    confirm_time DATETIME COMMENT '确认时间',
    confirm_by BIGINT COMMENT '确认人',
    pay_time DATETIME COMMENT '发放时间',
    pay_by BIGINT COMMENT '发放人',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_settlement_no (settlement_no),
    KEY idx_settlement_month (settlement_month),
    KEY idx_department_id (department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工资结算单表';

CREATE TABLE wage_settlement_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    settlement_id BIGINT NOT NULL COMMENT '结算单ID',
    worker_id BIGINT NOT NULL COMMENT '工人ID',
    worker_code VARCHAR(50) COMMENT '工号',
    worker_name VARCHAR(50) COMMENT '姓名',
    department_name VARCHAR(100) COMMENT '部门',
    record_count INT DEFAULT 0 COMMENT '计件次数',
    total_quantity INT DEFAULT 0 COMMENT '总数量',
    piecework_amount DECIMAL(12,4) DEFAULT 0 COMMENT '计件金额',
    deduction_amount DECIMAL(10,2) DEFAULT 0 COMMENT '扣款金额',
    bonus_amount DECIMAL(10,2) DEFAULT 0 COMMENT '奖金',
    final_amount DECIMAL(12,4) DEFAULT 0 COMMENT '应发金额',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_settlement_id (settlement_id),
    KEY idx_worker_id (worker_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工资结算明细表';
```

#### 22.3.5 质量管理模块

```sql
-- =============================================
-- 质检管理
-- =============================================

CREATE TABLE qc_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '质检记录ID',
    qc_no VARCHAR(50) NOT NULL COMMENT '质检单号',
    qc_type VARCHAR(20) NOT NULL COMMENT '质检类型: FAI/IPQC/FQC/OQC',
    order_id BIGINT COMMENT '订单ID',
    order_no VARCHAR(50) COMMENT '生产单号',
    bundle_id BIGINT COMMENT '扎包ID',
    bundle_no VARCHAR(50) COMMENT '扎包号',
    process_id BIGINT COMMENT '工序ID',
    process_name VARCHAR(100) COMMENT '工序名称',
    check_quantity INT NOT NULL COMMENT '检验数量',
    pass_quantity INT DEFAULT 0 COMMENT '合格数量',
    fail_quantity INT DEFAULT 0 COMMENT '不合格数量',
    qc_result VARCHAR(20) NOT NULL COMMENT '检验结果: pass/fail/rework/scrap',
    pass_rate DECIMAL(5,2) COMMENT '合格率(%)',
    defect_types VARCHAR(500) COMMENT '缺陷类型(JSON)',
    remark TEXT COMMENT '备注',
    images TEXT COMMENT '图片(JSON)',
    inspector_id BIGINT NOT NULL COMMENT '检验员ID',
    inspector_name VARCHAR(50) COMMENT '检验员姓名',
    qc_time DATETIME NOT NULL COMMENT '检验时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_qc_no (qc_no),
    KEY idx_order_id (order_id),
    KEY idx_bundle_id (bundle_id),
    KEY idx_qc_type (qc_type),
    KEY idx_qc_time (qc_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质检记录表';

CREATE TABLE qc_rework (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '返工记录ID',
    rework_no VARCHAR(50) NOT NULL COMMENT '返工单号',
    qc_id BIGINT COMMENT '质检记录ID',
    bundle_id BIGINT NOT NULL COMMENT '扎包ID',
    bundle_no VARCHAR(50) COMMENT '扎包号',
    process_id BIGINT NOT NULL COMMENT '返工工序ID',
    process_name VARCHAR(100) COMMENT '返工工序名称',
    defect_type VARCHAR(50) COMMENT '缺陷类型',
    quantity INT NOT NULL COMMENT '返工数量',
    original_worker_id BIGINT COMMENT '原操作员ID',
    original_worker_name VARCHAR(50) COMMENT '原操作员姓名',
    original_amount DECIMAL(10,2) COMMENT '原计件金额',
    deduction_ratio DECIMAL(5,2) COMMENT '扣款比例(%)',
    deduction_amount DECIMAL(10,2) COMMENT '扣款金额',
    rework_worker_id BIGINT COMMENT '返工操作员ID',
    rework_worker_name VARCHAR(50) COMMENT '返工操作员姓名',
    rework_amount DECIMAL(10,2) COMMENT '返工计件金额',
    rework_status VARCHAR(20) DEFAULT 'pending' COMMENT '返工状态: pending/in_progress/completed',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '完成时间',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_rework_no (rework_no),
    KEY idx_bundle_id (bundle_id),
    KEY idx_rework_status (rework_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='返工记录表';
```

### 22.4 索引设计原则

| 索引类型 | 使用场景 | 示例 |
|----------|----------|------|
| 主键索引 | 表主键 | `id BIGINT PRIMARY KEY` |
| 唯一索引 | 业务唯一字段 | `uk_order_no`, `uk_bundle_no` |
| 普通索引 | 频繁查询字段 | `idx_order_status`, `idx_worker_id` |
| 组合索引 | 多条件查询 | `idx_style_color_size` |
| 全文索引 | 文本搜索 | `FULLTEXT(description)` |

### 22.5 分库分表策略

#### 22.5.1 垂直分库

| 库名 | 包含表 | 说明 |
|------|--------|------|
| erp_base | sys_*, org_* | 基础数据 |
| erp_product | prod_product, prod_style, prod_style_sku | 商品数据 |
| erp_production | prod_order, prod_bundle, prod_process_* | 生产数据 |
| erp_quality | qc_* | 质量数据 |
| erp_wage | wage_*, prod_piecework_* | 工资数据 |
| erp_sales | sales_* | 销售数据 |
| erp_inventory | inv_* | 库存数据 |

#### 22.5.2 水平分表

| 表名 | 分表键 | 策略 |
|------|--------|------|
| prod_piecework_record | worker_id + record_date | 按月分表 |
| prod_bundle_flow | bundle_id + created_at | 按月分表 |
| qc_record | qc_time | 按月分表 |

### 22.6 销售管理模块DDL

#### 22.6.1 客户管理

```sql
CREATE TABLE jxc_customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '客户ID',
    customer_code VARCHAR(50) NOT NULL COMMENT '客户编码',
    customer_name VARCHAR(100) NOT NULL COMMENT '客户名称',
    customer_type VARCHAR(20) DEFAULT 'wholesale' COMMENT '客户类型: wholesale-批发 retail-零售 agent-代理',
    contact_name VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '电话',
    mobile VARCHAR(20) COMMENT '手机',
    email VARCHAR(100) COMMENT '邮箱',
    province VARCHAR(50) COMMENT '省',
    city VARCHAR(50) COMMENT '市',
    district VARCHAR(50) COMMENT '区',
    address VARCHAR(200) COMMENT '详细地址',
    sales_id BIGINT COMMENT '销售员ID',
    sales_name VARCHAR(50) COMMENT '销售员姓名',
    credit_amount DECIMAL(12,4) DEFAULT 0 COMMENT '信用额度',
    used_credit DECIMAL(12,2) DEFAULT 0 COMMENT '已用额度',
    payment_days INT DEFAULT 30 COMMENT '账期(天)',
    level VARCHAR(20) DEFAULT 'normal' COMMENT '客户等级: vip/normal/potential',
    source VARCHAR(50) COMMENT '客户来源',
    remark TEXT COMMENT '备注',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_customer_code (customer_code),
    KEY idx_sales_id (sales_id),
    KEY idx_customer_name (customer_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

CREATE TABLE jxc_customer_address (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    address_name VARCHAR(50) COMMENT '地址名称',
    contact_name VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '电话',
    province VARCHAR(50) COMMENT '省',
    city VARCHAR(50) COMMENT '市',
    district VARCHAR(50) COMMENT '区',
    address VARCHAR(200) COMMENT '详细地址',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认: 0-否 1-是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户地址表';
```

#### 22.6.2 销售报价单

```sql
CREATE TABLE jxc_quotation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '报价单ID',
    quotation_no VARCHAR(50) NOT NULL COMMENT '报价单号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    customer_name VARCHAR(100) COMMENT '客户名称',
    contact_name VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '电话',
    sales_id BIGINT COMMENT '销售员ID',
    sales_name VARCHAR(50) COMMENT '销售员姓名',
    quotation_date DATE NOT NULL COMMENT '报价日期',
    valid_until DATE COMMENT '有效期至',
    currency VARCHAR(20) DEFAULT 'CNY' COMMENT '币种',
    total_amount DECIMAL(14,2) DEFAULT 0 COMMENT '总金额',
    discount_rate DECIMAL(5,2) DEFAULT 0 COMMENT '折扣率(%)',
    discount_amount DECIMAL(12,4) DEFAULT 0 COMMENT '折扣金额',
    final_amount DECIMAL(14,2) DEFAULT 0 COMMENT '折后金额',
    quotation_status VARCHAR(20) DEFAULT 'draft' COMMENT '状态: draft-草稿 sent-已发送 accepted-已接受 rejected-已拒绝 expired-已过期',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_quotation_no (quotation_no),
    KEY idx_customer_id (customer_id),
    KEY idx_quotation_date (quotation_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售报价单表';

CREATE TABLE jxc_quotation_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    quotation_id BIGINT NOT NULL COMMENT '报价单ID',
    style_id BIGINT COMMENT '款号ID',
    style_code VARCHAR(50) COMMENT '款号',
    style_name VARCHAR(200) COMMENT '品名',
    sku_id BIGINT COMMENT 'SKU ID',
    color VARCHAR(50) COMMENT '颜色',
    size VARCHAR(20) COMMENT '尺码',
    unit VARCHAR(20) COMMENT '单位',
    quantity INT NOT NULL COMMENT '数量',
    unit_price DECIMAL(12,2) NOT NULL COMMENT '单价',
    discount_rate DECIMAL(5,2) DEFAULT 0 COMMENT '折扣率(%)',
    amount DECIMAL(14,2) NOT NULL COMMENT '金额',
    remark VARCHAR(200) COMMENT '备注',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_quotation_id (quotation_id),
    KEY idx_style_id (style_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售报价单明细表';
```

#### 22.6.3 销售订单

```sql
CREATE TABLE jxc_sales_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL COMMENT '订单号',
    quotation_id BIGINT COMMENT '报价单ID',
    quotation_no VARCHAR(50) COMMENT '报价单号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    customer_name VARCHAR(100) COMMENT '客户名称',
    contact_name VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '电话',
    address_id BIGINT COMMENT '收货地址ID',
    province VARCHAR(50) COMMENT '省',
    city VARCHAR(50) COMMENT '市',
    district VARCHAR(50) COMMENT '区',
    address VARCHAR(200) COMMENT '详细地址',
    sales_id BIGINT COMMENT '销售员ID',
    sales_name VARCHAR(50) COMMENT '销售员姓名',
    order_date DATE NOT NULL COMMENT '订单日期',
    delivery_date DATE COMMENT '交货日期',
    currency VARCHAR(20) DEFAULT 'CNY' COMMENT '币种',
    total_quantity INT DEFAULT 0 COMMENT '总数量',
    total_amount DECIMAL(14,2) DEFAULT 0 COMMENT '总金额',
    discount_rate DECIMAL(5,2) DEFAULT 0 COMMENT '折扣率(%)',
    discount_amount DECIMAL(12,4) DEFAULT 0 COMMENT '折扣金额',
    tax_rate DECIMAL(5,2) DEFAULT 0 COMMENT '税率(%)',
    tax_amount DECIMAL(12,4) DEFAULT 0 COMMENT '税额',
    final_amount DECIMAL(14,2) DEFAULT 0 COMMENT '价税合计',
    paid_amount DECIMAL(14,2) DEFAULT 0 COMMENT '已收金额',
    shipped_quantity INT DEFAULT 0 COMMENT '已发货数量',
    received_quantity INT DEFAULT 0 COMMENT '已收货数量',
    order_status VARCHAR(20) DEFAULT 'draft' COMMENT '订单状态: draft-草稿 pending-待审核 confirmed-已确认 producing-生产中 shipping-发货中 completed-已完成 cancelled-已取消',
    order_source VARCHAR(20) DEFAULT 'manual' COMMENT '订单来源: manual-手工录入 import-导入 wechat-微信小程序 erp-系统生成',
    payment_status VARCHAR(20) DEFAULT 'unpaid' COMMENT '付款状态: unpaid-未付款 partial-部分付款 paid-已付款',
    delivery_status VARCHAR(20) DEFAULT 'undelivered' COMMENT '发货状态: undelivered-未发货 partial-部分发货 delivered-已发货',
    production_order_id BIGINT COMMENT '关联生产订单ID',
    production_order_no VARCHAR(50) COMMENT '生产单号',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_customer_id (customer_id),
    KEY idx_sales_id (sales_id),
    KEY idx_order_date (order_date),
    KEY idx_delivery_date (delivery_date),
    KEY idx_order_status (order_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售订单表';

CREATE TABLE jxc_sales_order_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    style_id BIGINT COMMENT '款号ID',
    style_code VARCHAR(50) COMMENT '款号',
    style_name VARCHAR(200) COMMENT '品名',
    sku_id BIGINT COMMENT 'SKU ID',
    sku_code VARCHAR(50) COMMENT 'SKU编码',
    color VARCHAR(50) COMMENT '颜色',
    size VARCHAR(20) COMMENT '尺码',
    unit VARCHAR(20) COMMENT '单位',
    quantity INT NOT NULL COMMENT '数量',
    unit_price DECIMAL(12,2) NOT NULL COMMENT '单价',
    discount_rate DECIMAL(5,2) DEFAULT 0 COMMENT '折扣率(%)',
    amount DECIMAL(14,2) NOT NULL COMMENT '金额',
    tax_rate DECIMAL(5,2) DEFAULT 0 COMMENT '税率(%)',
    tax_amount DECIMAL(12,4) DEFAULT 0 COMMENT '税额',
    shipped_quantity INT DEFAULT 0 COMMENT '已发货数量',
    received_quantity INT DEFAULT 0 COMMENT '已收货数量',
    production_quantity INT DEFAULT 0 COMMENT '生产数量',
    remark VARCHAR(200) COMMENT '备注',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_order_id (order_id),
    KEY idx_style_id (style_id),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售订单明细表';
```

#### 22.6.4 销售发货单

```sql
CREATE TABLE jxc_delivery_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '发货单ID',
    delivery_no VARCHAR(50) NOT NULL COMMENT '发货单号',
    sales_order_id BIGINT NOT NULL COMMENT '销售订单ID',
    sales_order_no VARCHAR(50) COMMENT '订单号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    customer_name VARCHAR(100) COMMENT '客户名称',
    contact_name VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '电话',
    province VARCHAR(50) COMMENT '省',
    city VARCHAR(50) COMMENT '市',
    district VARCHAR(50) COMMENT '区',
    address VARCHAR(200) COMMENT '详细地址',
    sales_id BIGINT COMMENT '销售员ID',
    sales_name VARCHAR(50) COMMENT '销售员姓名',
    delivery_date DATE NOT NULL COMMENT '发货日期',
    logistics_company VARCHAR(50) COMMENT '物流公司',
    logistics_no VARCHAR(50) COMMENT '物流单号',
    total_quantity INT DEFAULT 0 COMMENT '总数量',
    total_amount DECIMAL(14,2) DEFAULT 0 COMMENT '总金额',
    delivery_status VARCHAR(20) DEFAULT 'pending' COMMENT '发货状态: pending-待发货 shipped-已发货 received-已签收 returned-已退货',
    warehouse_id BIGINT COMMENT '仓库ID',
    warehouse_name VARCHAR(100) COMMENT '仓库名称',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_delivery_no (delivery_no),
    KEY idx_sales_order_id (sales_order_id),
    KEY idx_customer_id (customer_id),
    KEY idx_delivery_date (delivery_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售发货单表';

CREATE TABLE jxc_delivery_order_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    delivery_id BIGINT NOT NULL COMMENT '发货单ID',
    sales_order_detail_id BIGINT COMMENT '订单明细ID',
    style_id BIGINT COMMENT '款号ID',
    style_code VARCHAR(50) COMMENT '款号',
    style_name VARCHAR(200) COMMENT '品名',
    sku_id BIGINT COMMENT 'SKU ID',
    sku_code VARCHAR(50) COMMENT 'SKU编码',
    color VARCHAR(50) COMMENT '颜色',
    size VARCHAR(20) COMMENT '尺码',
    unit VARCHAR(20) COMMENT '单位',
    quantity INT NOT NULL COMMENT '发货数量',
    unit_price DECIMAL(12,2) COMMENT '单价',
    amount DECIMAL(14,2) COMMENT '金额',
    batch_no VARCHAR(50) COMMENT '批次号',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_delivery_id (delivery_id),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售发货单明细表';
```

#### 22.6.5 销售退货单

```sql
CREATE TABLE jxc_sales_return (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '退货单ID',
    return_no VARCHAR(50) NOT NULL COMMENT '退货单号',
    sales_order_id BIGINT COMMENT '销售订单ID',
    sales_order_no VARCHAR(50) COMMENT '订单号',
    delivery_id BIGINT COMMENT '发货单ID',
    delivery_no VARCHAR(50) COMMENT '发货单号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    customer_name VARCHAR(100) COMMENT '客户名称',
    return_date DATE NOT NULL COMMENT '退货日期',
    return_type VARCHAR(20) NOT NULL COMMENT '退货类型: quality-质量问题 wrong-发错货 cancel-取消订单 other-其他',
    total_quantity INT DEFAULT 0 COMMENT '总数量',
    total_amount DECIMAL(14,2) DEFAULT 0 COMMENT '总金额',
    refund_amount DECIMAL(14,2) DEFAULT 0 COMMENT '退款金额',
    return_status VARCHAR(20) DEFAULT 'pending' COMMENT '退货状态: pending-待审核 approved-已审核 received-已收货 completed-已完成 rejected-已拒绝',
    warehouse_id BIGINT COMMENT '入库仓库ID',
    warehouse_name VARCHAR(100) COMMENT '仓库名称',
    remark TEXT COMMENT '退货原因',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_return_no (return_no),
    KEY idx_sales_order_id (sales_order_id),
    KEY idx_customer_id (customer_id),
    KEY idx_return_date (return_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售退货单表';

CREATE TABLE jxc_sales_return_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    return_id BIGINT NOT NULL COMMENT '退货单ID',
    delivery_detail_id BIGINT COMMENT '发货明细ID',
    style_id BIGINT COMMENT '款号ID',
    style_code VARCHAR(50) COMMENT '款号',
    style_name VARCHAR(200) COMMENT '品名',
    sku_id BIGINT COMMENT 'SKU ID',
    sku_code VARCHAR(50) COMMENT 'SKU编码',
    color VARCHAR(50) COMMENT '颜色',
    size VARCHAR(20) COMMENT '尺码',
    unit VARCHAR(20) COMMENT '单位',
    quantity INT NOT NULL COMMENT '退货数量',
    unit_price DECIMAL(12,2) COMMENT '单价',
    amount DECIMAL(14,2) COMMENT '金额',
    quality_status VARCHAR(20) COMMENT '质量状态: good-良品 defect-次品',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_return_id (return_id),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售退货单明细表';
```

### 22.7 采购管理模块DDL

#### 22.7.1 供应商管理

```sql
CREATE TABLE jxc_supplier (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '供应商ID',
    supplier_code VARCHAR(50) NOT NULL COMMENT '供应商编码',
    supplier_name VARCHAR(100) NOT NULL COMMENT '供应商名称',
    supplier_type VARCHAR(20) DEFAULT 'material' COMMENT '供应商类型: material-面料辅料 factory-加工厂 service-服务商',
    contact_name VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '电话',
    mobile VARCHAR(20) COMMENT '手机',
    email VARCHAR(100) COMMENT '邮箱',
    province VARCHAR(50) COMMENT '省',
    city VARCHAR(50) COMMENT '市',
    district VARCHAR(50) COMMENT '区',
    address VARCHAR(200) COMMENT '详细地址',
    bank_name VARCHAR(100) COMMENT '开户银行',
    bank_account VARCHAR(50) COMMENT '银行账号',
    tax_no VARCHAR(50) COMMENT '税号',
    payment_days INT DEFAULT 30 COMMENT '账期(天)',
    level VARCHAR(20) DEFAULT 'normal' COMMENT '供应商等级: a/b/c',
    evaluate_score DECIMAL(5,2) COMMENT '评估分数',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_supplier_code (supplier_code),
    KEY idx_supplier_name (supplier_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

CREATE TABLE jxc_supplier_evaluate (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    supplier_id BIGINT NOT NULL COMMENT '供应商ID',
    evaluate_date DATE NOT NULL COMMENT '评估日期',
    quality_score DECIMAL(5,2) COMMENT '质量评分',
    delivery_score DECIMAL(5,2) COMMENT '交付评分',
    price_score DECIMAL(5,2) COMMENT '价格评分',
    service_score DECIMAL(5,2) COMMENT '服务评分',
    total_score DECIMAL(5,2) COMMENT '总评分',
    evaluator_id BIGINT COMMENT '评估人ID',
    evaluator_name VARCHAR(50) COMMENT '评估人姓名',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_supplier_id (supplier_id),
    KEY idx_evaluate_date (evaluate_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商评估表';
```

#### 22.7.2 采购申请单

```sql
CREATE TABLE jxc_purchase_request (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '采购申请ID',
    request_no VARCHAR(50) NOT NULL COMMENT '申请单号',
    request_type VARCHAR(20) NOT NULL COMMENT '申请类型: material-面料辅料 product-成品',
    department_id BIGINT COMMENT '申请部门ID',
    department_name VARCHAR(100) COMMENT '申请部门',
    requester_id BIGINT NOT NULL COMMENT '申请人ID',
    requester_name VARCHAR(50) COMMENT '申请人姓名',
    request_date DATE NOT NULL COMMENT '申请日期',
    expect_date DATE COMMENT '期望到货日期',
    total_quantity INT DEFAULT 0 COMMENT '总数量',
    total_amount DECIMAL(14,2) DEFAULT 0 COMMENT '预估金额',
    request_status VARCHAR(20) DEFAULT 'draft' COMMENT '申请状态: draft-草稿 pending-待审核 approved-已审核 rejected-已拒绝 ordered-已下单',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_request_no (request_no),
    KEY idx_requester_id (requester_id),
    KEY idx_request_date (request_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购申请单表';

CREATE TABLE jxc_purchase_request_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id BIGINT NOT NULL COMMENT '申请单ID',
    product_id BIGINT COMMENT '商品ID',
    product_code VARCHAR(50) COMMENT '商品编码',
    product_name VARCHAR(200) COMMENT '商品名称',
    sku_id BIGINT COMMENT 'SKU ID',
    sku_code VARCHAR(50) COMMENT 'SKU编码',
    color VARCHAR(50) COMMENT '颜色',
    size VARCHAR(20) COMMENT '尺码',
    unit VARCHAR(20) COMMENT '单位',
    quantity INT NOT NULL COMMENT '申请数量',
    purchased_quantity INT DEFAULT 0 COMMENT '已采购数量',
    reference_price DECIMAL(12,2) COMMENT '参考单价',
    reference_amount DECIMAL(14,2) COMMENT '参考金额',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_request_id (request_id),
    KEY idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购申请单明细表';
```

#### 22.7.3 采购订单

```sql
CREATE TABLE jxc_purchase_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '采购订单ID',
    order_no VARCHAR(50) NOT NULL COMMENT '订单号',
    request_id BIGINT COMMENT '采购申请ID',
    request_no VARCHAR(50) COMMENT '申请单号',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID',
    supplier_name VARCHAR(100) COMMENT '供应商名称',
    contact_name VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '电话',
    buyer_id BIGINT COMMENT '采购员ID',
    buyer_name VARCHAR(50) COMMENT '采购员姓名',
    order_date DATE NOT NULL COMMENT '订单日期',
    expect_date DATE COMMENT '预计到货日期',
    currency VARCHAR(20) DEFAULT 'CNY' COMMENT '币种',
    total_quantity INT DEFAULT 0 COMMENT '总数量',
    total_amount DECIMAL(14,2) DEFAULT 0 COMMENT '总金额',
    tax_rate DECIMAL(5,2) DEFAULT 0 COMMENT '税率(%)',
    tax_amount DECIMAL(12,4) DEFAULT 0 COMMENT '税额',
    final_amount DECIMAL(14,2) DEFAULT 0 COMMENT '价税合计',
    paid_amount DECIMAL(14,2) DEFAULT 0 COMMENT '已付金额',
    received_quantity INT DEFAULT 0 COMMENT '已收货数量',
    order_status VARCHAR(20) DEFAULT 'draft' COMMENT '订单状态: draft-草稿 pending-待审核 confirmed-已确认 receiving-收货中 completed-已完成 cancelled-已取消',
    payment_status VARCHAR(20) DEFAULT 'unpaid' COMMENT '付款状态: unpaid-未付款 partial-部分付款 paid-已付款',
    warehouse_id BIGINT COMMENT '入库仓库ID',
    warehouse_name VARCHAR(100) COMMENT '仓库名称',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_supplier_id (supplier_id),
    KEY idx_buyer_id (buyer_id),
    KEY idx_order_date (order_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单表';

CREATE TABLE jxc_purchase_order_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    request_detail_id BIGINT COMMENT '申请明细ID',
    product_id BIGINT COMMENT '商品ID',
    product_code VARCHAR(50) COMMENT '商品编码',
    product_name VARCHAR(200) COMMENT '商品名称',
    sku_id BIGINT COMMENT 'SKU ID',
    sku_code VARCHAR(50) COMMENT 'SKU编码',
    color VARCHAR(50) COMMENT '颜色',
    size VARCHAR(20) COMMENT '尺码',
    unit VARCHAR(20) COMMENT '单位',
    quantity INT NOT NULL COMMENT '采购数量',
    unit_price DECIMAL(12,2) NOT NULL COMMENT '单价',
    amount DECIMAL(14,2) NOT NULL COMMENT '金额',
    tax_rate DECIMAL(5,2) DEFAULT 0 COMMENT '税率(%)',
    tax_amount DECIMAL(12,4) DEFAULT 0 COMMENT '税额',
    received_quantity INT DEFAULT 0 COMMENT '已收货数量',
    remark VARCHAR(200) COMMENT '备注',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_order_id (order_id),
    KEY idx_product_id (product_id),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单明细表';
```

#### 22.7.4 采购入库单

```sql
CREATE TABLE jxc_purchase_inbound (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '入库单ID',
    inbound_no VARCHAR(50) NOT NULL COMMENT '入库单号',
    purchase_order_id BIGINT NOT NULL COMMENT '采购订单ID',
    purchase_order_no VARCHAR(50) COMMENT '采购订单号',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID',
    supplier_name VARCHAR(100) COMMENT '供应商名称',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    warehouse_name VARCHAR(100) COMMENT '仓库名称',
    inbound_date DATE NOT NULL COMMENT '入库日期',
    total_quantity INT DEFAULT 0 COMMENT '总数量',
    total_amount DECIMAL(14,2) DEFAULT 0 COMMENT '总金额',
    inbound_status VARCHAR(20) DEFAULT 'pending' COMMENT '入库状态: pending-待入库 partial-部分入库 completed-已完成',
    qc_status VARCHAR(20) COMMENT '质检状态: pending-待检 passed-合格 failed-不合格',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_inbound_no (inbound_no),
    KEY idx_purchase_order_id (purchase_order_id),
    KEY idx_supplier_id (supplier_id),
    KEY idx_inbound_date (inbound_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购入库单表';

CREATE TABLE jxc_purchase_inbound_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    inbound_id BIGINT NOT NULL COMMENT '入库单ID',
    order_detail_id BIGINT COMMENT '订单明细ID',
    product_id BIGINT COMMENT '商品ID',
    product_code VARCHAR(50) COMMENT '商品编码',
    product_name VARCHAR(200) COMMENT '商品名称',
    sku_id BIGINT COMMENT 'SKU ID',
    sku_code VARCHAR(50) COMMENT 'SKU编码',
    color VARCHAR(50) COMMENT '颜色',
    size VARCHAR(20) COMMENT '尺码',
    unit VARCHAR(20) COMMENT '单位',
    quantity INT NOT NULL COMMENT '入库数量',
    qualified_quantity INT COMMENT '合格数量',
    unqualified_quantity INT COMMENT '不合格数量',
    unit_price DECIMAL(12,2) COMMENT '单价',
    amount DECIMAL(14,2) COMMENT '金额',
    batch_no VARCHAR(50) COMMENT '批次号',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_inbound_id (inbound_id),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购入库单明细表';
```

#### 22.7.5 采购退货单

```sql
CREATE TABLE jxc_purchase_return (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '退货单ID',
    return_no VARCHAR(50) NOT NULL COMMENT '退货单号',
    purchase_order_id BIGINT COMMENT '采购订单ID',
    purchase_order_no VARCHAR(50) COMMENT '采购订单号',
    inbound_id BIGINT COMMENT '入库单ID',
    inbound_no VARCHAR(50) COMMENT '入库单号',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID',
    supplier_name VARCHAR(100) COMMENT '供应商名称',
    return_date DATE NOT NULL COMMENT '退货日期',
    return_type VARCHAR(20) NOT NULL COMMENT '退货类型: quality-质量问题 wrong-发错货 cancel-取消订单 other-其他',
    total_quantity INT DEFAULT 0 COMMENT '总数量',
    total_amount DECIMAL(14,2) DEFAULT 0 COMMENT '总金额',
    refund_amount DECIMAL(14,2) DEFAULT 0 COMMENT '退款金额',
    return_status VARCHAR(20) DEFAULT 'pending' COMMENT '退货状态: pending-待审核 approved-已审核 shipped-已发货 completed-已完成 rejected-已拒绝',
    remark TEXT COMMENT '退货原因',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_return_no (return_no),
    KEY idx_purchase_order_id (purchase_order_id),
    KEY idx_supplier_id (supplier_id),
    KEY idx_return_date (return_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购退货单表';

CREATE TABLE jxc_purchase_return_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    return_id BIGINT NOT NULL COMMENT '退货单ID',
    inbound_detail_id BIGINT COMMENT '入库明细ID',
    product_id BIGINT COMMENT '商品ID',
    product_code VARCHAR(50) COMMENT '商品编码',
    product_name VARCHAR(200) COMMENT '商品名称',
    sku_id BIGINT COMMENT 'SKU ID',
    sku_code VARCHAR(50) COMMENT 'SKU编码',
    color VARCHAR(50) COMMENT '颜色',
    size VARCHAR(20) COMMENT '尺码',
    unit VARCHAR(20) COMMENT '单位',
    quantity INT NOT NULL COMMENT '退货数量',
    unit_price DECIMAL(12,2) COMMENT '单价',
    amount DECIMAL(14,2) COMMENT '金额',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_return_id (return_id),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购退货单明细表';
```

### 22.8 财务管理模块DDL

#### 22.8.1 银行账户

```sql
CREATE TABLE jxc_bank_account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '账户ID',
    account_code VARCHAR(50) NOT NULL COMMENT '账户编码',
    account_name VARCHAR(100) NOT NULL COMMENT '账户名称',
    account_type VARCHAR(20) NOT NULL COMMENT '账户类型: bank-银行账户 alipay-支付宝 wechat-微信 cash-现金',
    bank_name VARCHAR(100) COMMENT '开户银行',
    bank_account VARCHAR(50) COMMENT '银行账号',
    currency VARCHAR(20) DEFAULT 'CNY' COMMENT '币种',
    balance DECIMAL(14,2) DEFAULT 0 COMMENT '账户余额',
    frozen_amount DECIMAL(14,2) DEFAULT 0 COMMENT '冻结金额',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认账户',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_account_code (account_code),
    KEY idx_bank_account (bank_account)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='银行账户表';
```

#### 22.8.2 收款单

```sql
CREATE TABLE jxc_receipt_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收款记录ID',
    receipt_no VARCHAR(50) NOT NULL COMMENT '收款单号',
    receipt_type VARCHAR(20) NOT NULL COMMENT '收款类型: sales-销售收款 advance-预收款 refund-退款收回 other-其他',
    customer_id BIGINT COMMENT '客户ID',
    customer_name VARCHAR(100) COMMENT '客户名称',
    sales_order_id BIGINT COMMENT '销售订单ID',
    sales_order_no VARCHAR(50) COMMENT '销售订单号',
    receipt_date DATE NOT NULL COMMENT '收款日期',
    receipt_amount DECIMAL(14,2) NOT NULL COMMENT '收款金额',
    bank_account_id BIGINT COMMENT '收款账户ID',
    bank_account_name VARCHAR(100) COMMENT '收款账户名称',
    payment_method VARCHAR(20) NOT NULL COMMENT '付款方式: bank-银行转账 alipay-支付宝 wechat-微信 cash-现金 check-支票',
    payer_name VARCHAR(50) COMMENT '付款人',
    payer_account VARCHAR(50) COMMENT '付款账号',
    receipt_status VARCHAR(20) DEFAULT 'pending' COMMENT '收款状态: pending-待确认 confirmed-已确认 cancelled-已取消',
    voucher_no VARCHAR(50) COMMENT '凭证号',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_receipt_no (receipt_no),
    KEY idx_customer_id (customer_id),
    KEY idx_sales_order_id (sales_order_id),
    KEY idx_receipt_date (receipt_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收款记录表';
```

#### 22.8.3 付款单

```sql
CREATE TABLE jxc_payment_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '付款记录ID',
    payment_no VARCHAR(50) NOT NULL COMMENT '付款单号',
    payment_type VARCHAR(20) NOT NULL COMMENT '付款类型: purchase-采购付款 advance-预付款 wage-工资 refund-退款 other-其他',
    supplier_id BIGINT COMMENT '供应商ID',
    supplier_name VARCHAR(100) COMMENT '供应商名称',
    purchase_order_id BIGINT COMMENT '采购订单ID',
    purchase_order_no VARCHAR(50) COMMENT '采购订单号',
    payment_date DATE NOT NULL COMMENT '付款日期',
    payment_amount DECIMAL(14,2) NOT NULL COMMENT '付款金额',
    bank_account_id BIGINT COMMENT '付款账户ID',
    bank_account_name VARCHAR(100) COMMENT '付款账户名称',
    payment_method VARCHAR(20) NOT NULL COMMENT '付款方式: bank-银行转账 alipay-支付宝 wechat-微信 cash-现金 check-支票',
    payee_name VARCHAR(50) COMMENT '收款人',
    payee_account VARCHAR(50) COMMENT '收款账号',
    payment_status VARCHAR(20) DEFAULT 'pending' COMMENT '付款状态: pending-待审核 approved-已审核 paid-已付款 cancelled-已取消',
    voucher_no VARCHAR(50) COMMENT '凭证号',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_payment_no (payment_no),
    KEY idx_supplier_id (supplier_id),
    KEY idx_purchase_order_id (purchase_order_id),
    KEY idx_payment_date (payment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='付款记录表';
```

#### 22.8.4 发票管理

```sql
CREATE TABLE jxc_invoice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '发票ID',
    invoice_no VARCHAR(50) NOT NULL COMMENT '发票号码',
    invoice_type VARCHAR(20) NOT NULL COMMENT '发票类型: special-增值税专用发票 normal-增值税普通发票 electronic-电子发票',
    invoice_direction VARCHAR(20) NOT NULL COMMENT '发票方向: input-进项 output-销项',
    biz_type VARCHAR(20) NOT NULL COMMENT '业务类型: sales-销售 purchase-采购',
    biz_id BIGINT COMMENT '业务单据ID',
    biz_no VARCHAR(50) COMMENT '业务单号',
    customer_id BIGINT COMMENT '客户ID',
    customer_name VARCHAR(100) COMMENT '客户名称',
    supplier_id BIGINT COMMENT '供应商ID',
    supplier_name VARCHAR(100) COMMENT '供应商名称',
    invoice_date DATE NOT NULL COMMENT '开票日期',
    tax_rate DECIMAL(5,2) NOT NULL COMMENT '税率(%)',
    amount_without_tax DECIMAL(14,2) NOT NULL COMMENT '不含税金额',
    tax_amount DECIMAL(12,4) NOT NULL COMMENT '税额',
    amount_with_tax DECIMAL(14,2) NOT NULL COMMENT '价税合计',
    invoice_status VARCHAR(20) DEFAULT 'pending' COMMENT '发票状态: pending-待开票 issued-已开票 received-已收票 verified-已认证 voided-已作废',
    verify_date DATE COMMENT '认证日期',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_invoice_no (invoice_no),
    KEY idx_biz_id (biz_type, biz_id),
    KEY idx_customer_id (customer_id),
    KEY idx_supplier_id (supplier_id),
    KEY idx_invoice_date (invoice_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发票管理表';
```

#### 22.8.5 对账单

```sql
CREATE TABLE jxc_customer_statement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '对账单ID',
    statement_no VARCHAR(50) NOT NULL COMMENT '对账单号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    customer_name VARCHAR(100) COMMENT '客户名称',
    statement_month VARCHAR(7) NOT NULL COMMENT '对账月份(YYYY-MM)',
    start_date DATE NOT NULL COMMENT '起始日期',
    end_date DATE NOT NULL COMMENT '截止日期',
    begin_amount DECIMAL(14,2) DEFAULT 0 COMMENT '期初金额',
    order_amount DECIMAL(14,2) DEFAULT 0 COMMENT '本期订货金额',
    receipt_amount DECIMAL(14,2) DEFAULT 0 COMMENT '本期收款金额',
    refund_amount DECIMAL(14,2) DEFAULT 0 COMMENT '本期退款金额',
    end_amount DECIMAL(14,2) DEFAULT 0 COMMENT '期末金额',
    confirm_status VARCHAR(20) DEFAULT 'pending' COMMENT '确认状态: pending-待确认 confirmed-已确认 disputed-有异议',
    confirm_time DATETIME COMMENT '确认时间',
    confirm_by BIGINT COMMENT '确认人',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_statement_no (statement_no),
    KEY idx_customer_id (customer_id),
    KEY idx_statement_month (statement_month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户对账单表';

CREATE TABLE jxc_supplier_statement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '对账单ID',
    statement_no VARCHAR(50) NOT NULL COMMENT '对账单号',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID',
    supplier_name VARCHAR(100) COMMENT '供应商名称',
    statement_month VARCHAR(7) NOT NULL COMMENT '对账月份(YYYY-MM)',
    start_date DATE NOT NULL COMMENT '起始日期',
    end_date DATE NOT NULL COMMENT '截止日期',
    begin_amount DECIMAL(14,2) DEFAULT 0 COMMENT '期初金额',
    order_amount DECIMAL(14,2) DEFAULT 0 COMMENT '本期订货金额',
    payment_amount DECIMAL(14,2) DEFAULT 0 COMMENT '本期付款金额',
    refund_amount DECIMAL(14,2) DEFAULT 0 COMMENT '本期退款金额',
    end_amount DECIMAL(14,2) DEFAULT 0 COMMENT '期末金额',
    confirm_status VARCHAR(20) DEFAULT 'pending' COMMENT '确认状态: pending-待确认 confirmed-已确认 disputed-有异议',
    confirm_time DATETIME COMMENT '确认时间',
    confirm_by BIGINT COMMENT '确认人',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_statement_no (statement_no),
    KEY idx_supplier_id (supplier_id),
    KEY idx_statement_month (statement_month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商对账单表';
```

### 22.9 库存管理DDL补充

#### 22.9.1 库存盘点单

```sql
CREATE TABLE jxc_inventory_check (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '盘点单ID',
    check_no VARCHAR(50) NOT NULL COMMENT '盘点单号',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    warehouse_name VARCHAR(100) COMMENT '仓库名称',
    check_type VARCHAR(20) NOT NULL COMMENT '盘点类型: full-全盘 partial-部分盘点 cycle-循环盘点',
    check_date DATE NOT NULL COMMENT '盘点日期',
    total_sku INT DEFAULT 0 COMMENT 'SKU总数',
    check_sku INT DEFAULT 0 COMMENT '已盘SKU数',
    diff_sku INT DEFAULT 0 COMMENT '差异SKU数',
    total_quantity INT DEFAULT 0 COMMENT '系统数量',
    check_quantity INT DEFAULT 0 COMMENT '盘点数量',
    diff_quantity INT DEFAULT 0 COMMENT '差异数量',
    diff_amount DECIMAL(14,2) DEFAULT 0 COMMENT '差异金额',
    check_status VARCHAR(20) DEFAULT 'draft' COMMENT '盘点状态: draft-草稿 checking-盘点中 completed-已完成 posted-已过账',
    post_time DATETIME COMMENT '过账时间',
    post_by BIGINT COMMENT '过账人',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_check_no (check_no),
    KEY idx_warehouse_id (warehouse_id),
    KEY idx_check_date (check_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存盘点单表';

CREATE TABLE jxc_inventory_check_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    check_id BIGINT NOT NULL COMMENT '盘点单ID',
    product_id BIGINT COMMENT '商品ID',
    product_code VARCHAR(50) COMMENT '商品编码',
    product_name VARCHAR(200) COMMENT '商品名称',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    sku_code VARCHAR(50) COMMENT 'SKU编码',
    color VARCHAR(50) COMMENT '颜色',
    size VARCHAR(20) COMMENT '尺码',
    batch_no VARCHAR(50) COMMENT '批次号',
    location VARCHAR(50) COMMENT '库位',
    system_quantity INT DEFAULT 0 COMMENT '系统数量',
    check_quantity INT NOT NULL COMMENT '盘点数量',
    diff_quantity INT DEFAULT 0 COMMENT '差异数量',
    unit_cost DECIMAL(12,4) COMMENT '单位成本',
    diff_amount DECIMAL(14,2) DEFAULT 0 COMMENT '差异金额',
    check_status VARCHAR(20) DEFAULT 'pending' COMMENT '盘点状态: pending-待盘 checked-已盘',
    checker_id BIGINT COMMENT '盘点人ID',
    checker_name VARCHAR(50) COMMENT '盘点人姓名',
    check_time DATETIME COMMENT '盘点时间',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_check_id (check_id),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存盘点单明细表';
```

#### 22.9.2 库存调拨单

```sql
CREATE TABLE jxc_transfer_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '调拨单ID',
    transfer_no VARCHAR(50) NOT NULL COMMENT '调拨单号',
    transfer_type VARCHAR(20) NOT NULL COMMENT '调拨类型: normal-普通调拨 project-项目调拨',
    from_warehouse_id BIGINT NOT NULL COMMENT '调出仓库ID',
    from_warehouse_name VARCHAR(100) COMMENT '调出仓库名称',
    to_warehouse_id BIGINT NOT NULL COMMENT '调入仓库ID',
    to_warehouse_name VARCHAR(100) COMMENT '调入仓库名称',
    transfer_date DATE NOT NULL COMMENT '调拨日期',
    total_quantity INT DEFAULT 0 COMMENT '总数量',
    total_amount DECIMAL(14,2) DEFAULT 0 COMMENT '总金额',
    transfer_status VARCHAR(20) DEFAULT 'draft' COMMENT '调拨状态: draft-草稿 pending-待审核 approved-已审核 shipping-发货中 received-已收货 cancelled-已取消',
    ship_time DATETIME COMMENT '发货时间',
    ship_by BIGINT COMMENT '发货人',
    receive_time DATETIME COMMENT '收货时间',
    receive_by BIGINT COMMENT '收货人',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_transfer_no (transfer_no),
    KEY idx_from_warehouse_id (from_warehouse_id),
    KEY idx_to_warehouse_id (to_warehouse_id),
    KEY idx_transfer_date (transfer_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存调拨单表';

CREATE TABLE jxc_transfer_order_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    transfer_id BIGINT NOT NULL COMMENT '调拨单ID',
    product_id BIGINT COMMENT '商品ID',
    product_code VARCHAR(50) COMMENT '商品编码',
    product_name VARCHAR(200) COMMENT '商品名称',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    sku_code VARCHAR(50) COMMENT 'SKU编码',
    color VARCHAR(50) COMMENT '颜色',
    size VARCHAR(20) COMMENT '尺码',
    unit VARCHAR(20) COMMENT '单位',
    quantity INT NOT NULL COMMENT '调拨数量',
    received_quantity INT DEFAULT 0 COMMENT '已收货数量',
    unit_cost DECIMAL(12,4) COMMENT '单位成本',
    amount DECIMAL(14,2) COMMENT '金额',
    batch_no VARCHAR(50) COMMENT '批次号',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_transfer_id (transfer_id),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存调拨单明细表';
```

#### 22.9.3 价格表

```sql
CREATE TABLE jxc_price_list (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '价格表ID',
    price_type VARCHAR(20) NOT NULL COMMENT '价格类型: purchase-采购价 sales-销售价 wholesale-批发价 retail-零售价',
    price_name VARCHAR(100) NOT NULL COMMENT '价格表名称',
    effective_date DATE NOT NULL COMMENT '生效日期',
    expiry_date DATE COMMENT '失效日期',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认价格表',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    KEY idx_price_type (price_type),
    KEY idx_effective_date (effective_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='价格表';

CREATE TABLE jxc_price_list_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    price_list_id BIGINT NOT NULL COMMENT '价格表ID',
    product_id BIGINT COMMENT '商品ID',
    product_code VARCHAR(50) COMMENT '商品编码',
    sku_id BIGINT COMMENT 'SKU ID',
    sku_code VARCHAR(50) COMMENT 'SKU编码',
    min_quantity INT DEFAULT 1 COMMENT '最小数量',
    max_quantity INT COMMENT '最大数量',
    price DECIMAL(12,2) NOT NULL COMMENT '价格',
    discount_rate DECIMAL(5,2) COMMENT '折扣率(%)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_price_list_id (price_list_id),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='价格表明细';

CREATE TABLE jxc_customer_price (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    price DECIMAL(12,2) NOT NULL COMMENT '价格',
    min_quantity INT DEFAULT 1 COMMENT '最小数量',
    effective_date DATE COMMENT '生效日期',
    expiry_date DATE COMMENT '失效日期',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    UNIQUE KEY uk_customer_sku (customer_id, sku_id),
    KEY idx_customer_id (customer_id),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户价格表';
```

---

## 23. 系统配置设计

### 23.1 系统参数表

```sql
CREATE TABLE sys_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(100) NOT NULL COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(20) DEFAULT 'string' COMMENT '配置类型: string/number/boolean/json',
    group_code VARCHAR(50) COMMENT '分组编码',
    group_name VARCHAR(100) COMMENT '分组名称',
    description VARCHAR(200) COMMENT '配置描述',
    is_system TINYINT DEFAULT 0 COMMENT '是否系统配置: 0-否 1-是',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by BIGINT,
    UNIQUE KEY uk_config_key (config_key),
    KEY idx_group_code (group_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';
```

### 23.2 配置分类

#### 23.2.1 基础配置

| 配置键 | 配置名称 | 默认值 | 说明 |
|--------|----------|--------|------|
| system.name | 系统名称 | 衣多多生产管理系统 | 系统显示名称 |
| system.logo | 系统Logo | /static/logo.png | Logo路径 |
| system.copyright | 版权信息 | © 2024 衣多多 | 页脚版权 |
| system.max_upload_size | 最大上传大小 | 10 | 文件上传限制(MB) |
| system.session_timeout | 会话超时 | 7200 | 会话超时时间(秒) |

#### 23.2.2 生产配置

| 配置键 | 配置名称 | 默认值 | 说明 |
|--------|----------|--------|------|
| production.bundle_size_default | 默认每包数量 | 50 | 创建扎包默认数量 |
| production.bundle_no_prefix | 扎包号前缀 | ZB | 扎包号前缀 |
| production.order_no_prefix | 生产单号前缀 | PO | 生产单号前缀 |
| production.process_flow_required | 流程必填 | true | 是否强制流程控制 |
| production.rework_deduction_ratio | 返工扣款比例 | 50 | 默认返工扣款比例(%) |

#### 23.2.3 质检配置

| 配置键 | 配置名称 | 默认值 | 说明 |
|--------|----------|--------|------|
| quality.sample_rate_default | 默认抽检比例 | 10 | 默认抽检比例(%) |
| quality.pass_rate_threshold | 合格率阈值 | 95 | 合格率预警阈值(%) |
| quality.first_article_required | 首件必检 | true | 是否强制首件检验 |
| quality.defect_types | 缺陷类型列表 | [...] | 缺陷类型JSON |

#### 23.2.4 工资配置

| 配置键 | 配置名称 | 默认值 | 说明 |
|--------|----------|--------|------|
| wage.settlement_day | 结算日 | 25 | 每月结算日 |
| wage.bonus_rules | 奖金规则 | {...} | 奖金计算规则JSON |
| wage.deduction_rules | 扣款规则 | {...} | 扣款规则JSON |
| wage.tax_threshold | 个税起征点 | 5000 | 个税起征点 |

#### 23.2.5 库存配置

| 配置键 | 配置名称 | 默认值 | 说明 |
|--------|----------|--------|------|
| inventory.warning_days | 库存预警天数 | 7 | 库存预警天数 |
| inventory.safety_stock_ratio | 安全库存比例 | 1.2 | 安全库存倍数 |
| inventory.expire_warning_days | 过期预警天数 | 30 | 临期预警天数 |

#### 23.2.6 通知配置

| 配置键 | 配置名称 | 默认值 | 说明 |
|--------|----------|--------|------|
| notification.sms_enabled | 短信开关 | false | 是否启用短信通知 |
| notification.wechat_enabled | 微信开关 | true | 是否启用微信通知 |
| notification.email_enabled | 邮件开关 | false | 是否启用邮件通知 |

### 23.3 业务规则配置

```sql
CREATE TABLE sys_business_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rule_code VARCHAR(50) NOT NULL COMMENT '规则编码',
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    rule_type VARCHAR(20) NOT NULL COMMENT '规则类型: workflow/price/approval/notification',
    rule_content TEXT COMMENT '规则内容(JSON)',
    description VARCHAR(500) COMMENT '规则描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    priority INT DEFAULT 0 COMMENT '优先级',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by BIGINT,
    UNIQUE KEY uk_rule_code (rule_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务规则表';
```

### 23.4 序号生成规则

```sql
CREATE TABLE sys_sequence (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    seq_type VARCHAR(50) NOT NULL COMMENT '序号类型',
    prefix VARCHAR(20) COMMENT '前缀',
    current_value BIGINT DEFAULT 0 COMMENT '当前值',
    step_size INT DEFAULT 1 COMMENT '步长',
    max_value BIGINT DEFAULT 999999999 COMMENT '最大值',
    cycle_flag TINYINT DEFAULT 0 COMMENT '是否循环',
    date_format VARCHAR(20) COMMENT '日期格式',
    padding_length INT DEFAULT 4 COMMENT '补位长度',
    description VARCHAR(200) COMMENT '描述',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_seq_type (seq_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='序号生成规则表';
```

**序号规则示例：**

| 序号类型 | 前缀 | 日期格式 | 补位长度 | 生成示例 |
|----------|------|----------|----------|----------|
| production_order | PO | yyyyMMdd | 4 | PO202403280001 |
| bundle | ZB | yyyyMMdd | 4 | ZB202403280001 |
| piecework | PW | yyyyMMddHHmmss | 6 | PW20240328143000001 |
| quality_check | QC | yyyyMMdd | 4 | QC202403280001 |
| settlement | JS | yyyyMM | 4 | JS2024030001 |

---

## 24. 消息通知设计

### 24.1 消息类型定义

| 消息类型 | 编码 | 优先级 | 通知渠道 |
|----------|------|--------|----------|
| 任务分配 | task_assign | 普通 | 小程序 |
| 任务到期提醒 | task_expire | 紧急 | 小程序/短信 |
| 订单下发 | order_issue | 普通 | 小程序 |
| 订单完成 | order_complete | 普通 | 小程序 |
| 质检不合格 | qc_fail | 紧急 | 小程序/短信 |
| 返工通知 | rework_notify | 紧急 | 小程序 |
| 工资结算 | wage_settle | 普通 | 小程序 |
| 审批待办 | approval_pending | 紧急 | 小程序 |
| 库存预警 | inventory_warning | 紧急 | 小程序/短信 |
| 系统公告 | system_notice | 普通 | 小程序/短信 |

### 24.2 消息模板设计

```sql
CREATE TABLE sys_message_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_code VARCHAR(50) NOT NULL COMMENT '模板编码',
    template_name VARCHAR(100) NOT NULL COMMENT '模板名称',
    message_type VARCHAR(20) NOT NULL COMMENT '消息类型',
    channel VARCHAR(20) NOT NULL COMMENT '通知渠道: app/sms/wechat/email',
    title VARCHAR(200) COMMENT '标题模板',
    content TEXT COMMENT '内容模板',
    params VARCHAR(500) COMMENT '参数列表(JSON)',
    jump_url VARCHAR(200) COMMENT '跳转链接模板',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_template_code (template_code),
    KEY idx_message_type (message_type),
    KEY idx_channel (channel)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息模板表';
```

**消息模板示例：**

| 模板编码 | 标题模板 | 内容模板 |
|----------|----------|----------|
| task_assign | 任务分配通知 | 您有新的任务待处理：${bundleNo}，工序：${processName}，数量：${quantity}，请及时处理。 |
| qc_fail | 质检不合格通知 | 扎包${bundleNo}质检不合格，不合格数量：${failQuantity}，请及时处理返工。 |
| wage_settle | 工资结算通知 | ${month}月工资已结算，应发金额：¥${amount}，请查看详情。 |
| inventory_warning | 库存预警通知 | 商品${productName}库存不足，当前库存：${quantity}，请及时补货。 |

### 24.3 消息记录表

```sql
CREATE TABLE sys_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    message_no VARCHAR(50) NOT NULL COMMENT '消息编号',
    template_code VARCHAR(50) COMMENT '模板编码',
    message_type VARCHAR(20) NOT NULL COMMENT '消息类型',
    channel VARCHAR(20) NOT NULL COMMENT '发送渠道',
    title VARCHAR(200) NOT NULL COMMENT '消息标题',
    content TEXT COMMENT '消息内容',
    sender_id BIGINT COMMENT '发送人ID',
    sender_name VARCHAR(50) COMMENT '发送人姓名',
    receiver_id BIGINT NOT NULL COMMENT '接收人ID',
    receiver_name VARCHAR(50) COMMENT '接收人姓名',
    biz_type VARCHAR(50) COMMENT '业务类型',
    biz_id BIGINT COMMENT '业务ID',
    jump_url VARCHAR(200) COMMENT '跳转链接',
    send_status VARCHAR(20) DEFAULT 'pending' COMMENT '发送状态: pending/sent/failed',
    send_time DATETIME COMMENT '发送时间',
    read_status TINYINT DEFAULT 0 COMMENT '阅读状态: 0-未读 1-已读',
    read_time DATETIME COMMENT '阅读时间',
    error_msg VARCHAR(500) COMMENT '错误信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_message_no (message_no),
    KEY idx_receiver_id (receiver_id),
    KEY idx_message_type (message_type),
    KEY idx_send_status (send_status),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息记录表';
```

### 24.4 消息订阅配置

```sql
CREATE TABLE sys_message_subscription (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    message_type VARCHAR(20) NOT NULL COMMENT '消息类型',
    channel VARCHAR(20) NOT NULL COMMENT '通知渠道',
    subscribe_status TINYINT DEFAULT 1 COMMENT '订阅状态: 0-未订阅 1-已订阅',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_type_channel (user_id, message_type, channel)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息订阅配置表';
```

### 24.5 微信小程序订阅消息

**订阅消息模板ID配置：**

```json
{
  "task_assign": "模板ID_xxx",
  "qc_fail": "模板ID_xxx",
  "wage_settle": "模板ID_xxx",
  "inventory_warning": "模板ID_xxx"
}
```

**订阅消息参数映射：**

```javascript
const templateParams = {
  task_assign: {
    thing1: '任务名称',      // {{thing1.DATA}}
    thing2: '工序名称',      // {{thing2.DATA}}
    number3: '数量',         // {{number3.DATA}}
    time4: '截止时间'        // {{time4.DATA}}
  },
  qc_fail: {
    thing1: '扎包号',
    thing2: '工序名称',
    number3: '不合格数量',
    time4: '检验时间'
  }
}
```

---

## 25. 日志与审计设计

### 25.1 操作日志表

```sql
CREATE TABLE sys_operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    trace_id VARCHAR(50) COMMENT '追踪ID',
    user_id BIGINT COMMENT '操作用户ID',
    user_name VARCHAR(50) COMMENT '操作用户名',
    org_id BIGINT COMMENT '组织ID',
    org_name VARCHAR(100) COMMENT '组织名称',
    module VARCHAR(50) COMMENT '模块名称',
    operation_type VARCHAR(20) COMMENT '操作类型: CREATE/UPDATE/DELETE/QUERY/EXPORT/IMPORT',
    operation_desc VARCHAR(200) COMMENT '操作描述',
    request_method VARCHAR(10) COMMENT '请求方法',
    request_url VARCHAR(500) COMMENT '请求URL',
    request_params TEXT COMMENT '请求参数',
    response_code INT COMMENT '响应码',
    response_msg VARCHAR(500) COMMENT '响应消息',
    response_data TEXT COMMENT '响应数据',
    ip VARCHAR(50) COMMENT 'IP地址',
    location VARCHAR(100) COMMENT 'IP归属地',
    user_agent VARCHAR(500) COMMENT '用户代理',
    device_type VARCHAR(20) COMMENT '设备类型: PC/APP/MINI',
    exec_time INT COMMENT '执行时间(ms)',
    status TINYINT COMMENT '操作状态: 0-失败 1-成功',
    error_msg TEXT COMMENT '错误信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_trace_id (trace_id),
    KEY idx_user_id (user_id),
    KEY idx_module (module),
    KEY idx_operation_type (operation_type),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
```

### 25.2 登录日志表

```sql
CREATE TABLE sys_login_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT '用户ID',
    user_name VARCHAR(50) COMMENT '用户名',
    login_type VARCHAR(20) COMMENT '登录类型: password/wechat/sms',
    login_status TINYINT COMMENT '登录状态: 0-失败 1-成功',
    login_time DATETIME COMMENT '登录时间',
    logout_time DATETIME COMMENT '登出时间',
    ip VARCHAR(50) COMMENT 'IP地址',
    location VARCHAR(100) COMMENT 'IP归属地',
    device_type VARCHAR(20) COMMENT '设备类型',
    device_info VARCHAR(200) COMMENT '设备信息',
    browser VARCHAR(50) COMMENT '浏览器',
    os VARCHAR(50) COMMENT '操作系统',
    fail_reason VARCHAR(200) COMMENT '失败原因',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_user_id (user_id),
    KEY idx_login_time (login_time),
    KEY idx_login_status (login_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';
```

### 25.3 数据变更日志表

```sql
CREATE TABLE sys_data_change_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    table_name VARCHAR(50) NOT NULL COMMENT '表名',
    record_id BIGINT NOT NULL COMMENT '记录ID',
    operation_type VARCHAR(20) NOT NULL COMMENT '操作类型: INSERT/UPDATE/DELETE',
    old_data TEXT COMMENT '变更前数据(JSON)',
    new_data TEXT COMMENT '变更后数据(JSON)',
    changed_fields TEXT COMMENT '变更字段(JSON)',
    user_id BIGINT COMMENT '操作用户ID',
    user_name VARCHAR(50) COMMENT '操作用户名',
    ip VARCHAR(50) COMMENT 'IP地址',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_table_record (table_name, record_id),
    KEY idx_user_id (user_id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据变更日志表';
```

### 25.4 审批日志表

```sql
CREATE TABLE sys_approval_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    approval_type VARCHAR(50) NOT NULL COMMENT '审批类型',
    approval_id BIGINT NOT NULL COMMENT '审批单ID',
    approval_no VARCHAR(50) COMMENT '审批单号',
    node_name VARCHAR(50) COMMENT '审批节点',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    action VARCHAR(20) COMMENT '操作: submit/approve/reject/withdraw',
    action_name VARCHAR(50) COMMENT '操作名称',
    opinion VARCHAR(500) COMMENT '审批意见',
    attachments TEXT COMMENT '附件(JSON)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_approval (approval_type, approval_id),
    KEY idx_operator_id (operator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批日志表';
```

### 25.5 日志记录策略

| 日志类型 | 记录方式 | 保留期限 | 存储位置 |
|----------|----------|----------|----------|
| 操作日志 | AOP切面 | 180天 | MySQL |
| 登录日志 | 拦截器 | 365天 | MySQL |
| 数据变更日志 | 触发器/MyBatis插件 | 365天 | MySQL |
| 系统日志 | Logback | 30天 | 文件/ELK |
| 错误日志 | 全局异常处理 | 90天 | 文件/ELK |
| 接口日志 | 网关过滤器 | 30天 | 文件/ELK |

### 25.6 敏感操作审计

**需要审计的敏感操作：**

| 模块 | 操作 | 审计级别 |
|------|------|----------|
| 用户管理 | 新增/修改/删除用户 | 高 |
| 角色管理 | 分配角色/修改权限 | 高 |
| 工序配置 | 修改工序单价 | 高 |
| 工资结算 | 确认/发放工资 | 高 |
| 数据导出 | 导出敏感数据 | 中 |
| 系统配置 | 修改系统参数 | 中 |
| 扎包删除 | 删除扎包记录 | 中 |

---

## 26. 部署架构设计

### 26.1 部署架构图

```
                                    ┌─────────────────────────────────────┐
                                    │             CDN / OSS               │
                                    │         (静态资源存储)              │
                                    └─────────────────────────────────────┘
                                                      │
                                    ┌─────────────────┴─────────────────┐
                                    │           Nginx (负载均衡)         │
                                    │         SSL / HTTPS 终结          │
                                    └─────────────────┬─────────────────┘
                                                      │
                    ┌─────────────────────────────────┼─────────────────────────────────┐
                    │                                 │                                 │
            ┌───────┴───────┐               ┌───────┴───────┐               ┌───────┴───────┐
            │   Backend 1   │               │   Backend 2   │               │   Backend N   │
            │  Spring Boot  │               │  Spring Boot  │               │  Spring Boot  │
            └───────┬───────┘               └───────┬───────┘               └───────┬───────┘
                    │                               │                               │
                    └───────────────────────────────┼───────────────────────────────┘
                                                    │
                                    ┌───────────────┴───────────────┐
                                    │         API Gateway           │
                                    │    (鉴权/限流/路由/熔断)       │
                                    └───────────────┬───────────────┘
                                                    │
                    ┌───────────────────────────────┼───────────────────────────────┐
                    │                               │                               │
            ┌───────┴───────┐               ┌───────┴───────┐               ┌───────┴───────┐
            │    Redis      │               │    MySQL      │               │   MongoDB     │
            │  (缓存/会话)   │               │  (主从复制)   │               │  (日志存储)   │
            └───────────────┘               └───────────────┘               └───────────────┘
```

### 26.2 服务器规划

#### 26.2.1 开发环境

| 服务器 | 配置 | 用途 |
|--------|------|------|
| 应用服务器 | 2核4G | 部署后端服务 |
| 数据库服务器 | 2核4G | MySQL + Redis |
| 文件服务器 | 1核2G | MinIO/OSS |

#### 26.2.2 测试环境

| 服务器 | 配置 | 用途 |
|--------|------|------|
| 应用服务器 | 4核8G × 2 | 部署后端服务(双实例) |
| 数据库服务器 | 4核8G | MySQL主从 |
| 缓存服务器 | 2核4G | Redis哨兵 |
| 文件服务器 | 2核4G | MinIO |

#### 26.2.3 生产环境

| 服务器 | 配置 | 数量 | 用途 |
|--------|------|------|------|
| Nginx | 4核8G | 2 | 负载均衡(主备) |
| 应用服务器 | 8核16G | 3+ | 后端服务集群 |
| MySQL主库 | 8核32G | 1 | 主库 |
| MySQL从库 | 8核16G | 2 | 从库(读写分离) |
| Redis集群 | 4核8G | 3 | Redis Cluster |
| MongoDB | 4核8G | 3 | 日志存储副本集 |
| MinIO | 4核8G | 4 | 对象存储集群 |
| ELK | 8核16G | 3 | 日志分析集群 |

### 26.3 容器化部署

#### 26.3.1 Docker Compose (开发环境)

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: erp-mysql
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: erp
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    command: --default-authentication-plugin=mysql_native_password

  redis:
    image: redis:7.0
    container_name: erp-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    command: redis-server --appendonly yes

  backend:
    build: ./backend
    container_name: erp-backend
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: dev
      MYSQL_HOST: mysql
      REDIS_HOST: redis
    depends_on:
      - mysql
      - redis

  frontend:
    build: ./frontend
    container_name: erp-frontend
    ports:
      - "80:80"
    depends_on:
      - backend

  minio:
    image: minio/minio
    container_name: erp-minio
    ports:
      - "9000:9000"
      - "9001:9001"
    environment:
      MINIO_ROOT_USER: admin
      MINIO_ROOT_PASSWORD: admin123
    volumes:
      - minio_data:/data
    command: server /data --console-address ":9001"

volumes:
  mysql_data:
  redis_data:
  minio_data:
```

#### 26.3.2 Kubernetes (生产环境)

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: erp-backend
  namespace: erp
spec:
  replicas: 3
  selector:
    matchLabels:
      app: erp-backend
  template:
    metadata:
      labels:
        app: erp-backend
    spec:
      containers:
      - name: backend
        image: registry.example.com/erp-backend:latest
        ports:
        - containerPort: 8080
        resources:
          requests:
            memory: "1Gi"
            cpu: "500m"
          limits:
            memory: "2Gi"
            cpu: "1000m"
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: MYSQL_HOST
          valueFrom:
            configMapKeyRef:
              name: erp-config
              key: mysql.host
        - name: REDIS_HOST
          valueFrom:
            configMapKeyRef:
              name: erp-config
              key: redis.host
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: erp-backend
  namespace: erp
spec:
  selector:
    app: erp-backend
  ports:
  - port: 8080
    targetPort: 8080
  type: ClusterIP
---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: erp-backend-hpa
  namespace: erp
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: erp-backend
  minReplicas: 3
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

### 26.4 CI/CD 流水线

```yaml
# .gitlab-ci.yml
stages:
  - build
  - test
  - package
  - deploy

variables:
  MAVEN_OPTS: "-Dmaven.repo.local=.m2/repository"
  DOCKER_REGISTRY: "registry.example.com"

build:
  stage: build
  image: maven:3.8-openjdk-17
  script:
    - mvn clean compile -DskipTests
  cache:
    paths:
      - .m2/repository/

test:
  stage: test
  image: maven:3.8-openjdk-17
  script:
    - mvn test
  artifacts:
    reports:
      junit: target/surefire-reports/TEST-*.xml

package:
  stage: package
  image: maven:3.8-openjdk-17
  script:
    - mvn package -DskipTests
    - docker build -t $DOCKER_REGISTRY/erp-backend:$CI_COMMIT_SHA .
    - docker push $DOCKER_REGISTRY/erp-backend:$CI_COMMIT_SHA
  only:
    - main
    - develop

deploy-dev:
  stage: deploy
  script:
    - kubectl set image deployment/erp-backend backend=$DOCKER_REGISTRY/erp-backend:$CI_COMMIT_SHA -n erp-dev
  environment:
    name: development
  only:
    - develop

deploy-prod:
  stage: deploy
  script:
    - kubectl set image deployment/erp-backend backend=$DOCKER_REGISTRY/erp-backend:$CI_COMMIT_SHA -n erp-prod
  environment:
    name: production
  only:
    - main
  when: manual
```

### 26.5 监控告警

#### 26.5.1 监控指标

| 指标类型 | 监控项 | 告警阈值 |
|----------|--------|----------|
| 应用监控 | JVM内存使用率 | > 85% |
| 应用监控 | GC频率 | > 10次/分钟 |
| 应用监控 | 接口响应时间 | > 3秒 |
| 应用监控 | 错误率 | > 1% |
| 数据库监控 | 连接数使用率 | > 80% |
| 数据库监控 | 慢查询 | > 1秒 |
| 数据库监控 | 主从延迟 | > 10秒 |
| 缓存监控 | 内存使用率 | > 80% |
| 缓存监控 | 命中率 | < 90% |
| 服务器监控 | CPU使用率 | > 80% |
| 服务器监控 | 内存使用率 | > 85% |
| 服务器监控 | 磁盘使用率 | > 85% |

#### 26.5.2 Prometheus 配置

```yaml
# prometheus.yml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

alerting:
  alertmanagers:
    - static_configs:
        - targets:
          - alertmanager:9093

rule_files:
  - /etc/prometheus/rules/*.yml

scrape_configs:
  - job_name: 'erp-backend'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['erp-backend:8080']
  
  - job_name: 'mysql'
    static_configs:
      - targets: ['mysql-exporter:9104']
  
  - job_name: 'redis'
    static_configs:
      - targets: ['redis-exporter:9121']
  
  - job_name: 'node'
    static_configs:
      - targets: ['node-exporter:9100']
```

---

## 27. 安全设计

### 27.1 认证授权

#### 27.1.1 认证方式

| 认证方式 | 适用场景 | 实现方案 |
|----------|----------|----------|
| 用户名密码 | Web后台 | JWT + Spring Security |
| 微信授权 | 小程序 | 微信登录 + JWT |
| 短信验证码 | 移动端 | 短信验证 + JWT |
| 扫码登录 | Web后台 | 二维码 + WebSocket |

#### 27.1.2 JWT Token 设计

```json
{
  "header": {
    "alg": "RS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "用户ID",
    "username": "用户名",
    "roles": ["role_code1", "role_code2"],
    "org_id": "组织ID",
    "exp": "过期时间",
    "iat": "签发时间",
    "jti": "Token唯一标识"
  }
}
```

#### 27.1.3 Token 安全策略

| 策略 | 说明 |
|------|------|
| Token 过期时间 | Access Token: 2小时, Refresh Token: 7天 |
| Token 刷新 | 使用 Refresh Token 刷新，刷新后旧 Token 失效 |
| Token 黑名单 | 用户登出/修改密码后，Token 加入黑名单 |
| 双 Token 机制 | Access Token (短期) + Refresh Token (长期) |
| 设备绑定 | 记录 Token 与设备的绑定关系 |

### 27.2 密码安全

#### 27.2.1 密码规则

| 规则 | 要求 |
|------|------|
| 最小长度 | 8位 |
| 复杂度 | 必须包含大小写字母、数字、特殊字符中的3种 |
| 历史检查 | 不能与最近5次密码相同 |
| 有效期 | 90天（可配置） |
| 试错限制 | 5次错误后锁定账户30分钟 |

#### 27.2.2 密码加密

```java
// 使用 BCrypt 加密
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // cost factor = 12
}
```

### 27.3 接口安全

#### 27.3.1 接口鉴权

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint);
        
        return http.build();
    }
}
```

#### 27.3.2 接口签名

```javascript
// 签名算法
const sign = (params, timestamp, nonce, secret) => {
    // 1. 参数按 key 排序
    const sortedKeys = Object.keys(params).sort()
    
    // 2. 拼接参数字符串
    const paramStr = sortedKeys.map(k => `${k}=${params[k]}`).join('&')
    
    // 3. 拼接签名字符串
    const signStr = `${paramStr}&timestamp=${timestamp}&nonce=${nonce}&secret=${secret}`
    
    // 4. SHA256 加密
    return sha256(signStr)
}

// 请求头
headers: {
    'X-Timestamp': timestamp,
    'X-Nonce': nonce,
    'X-Sign': sign
}
```

#### 27.3.3 接口限流

```java
@Configuration
public class RateLimitConfig {
    
    @Bean
    public RateLimiter rateLimiter(RedisTemplate<String, Object> redisTemplate) {
        return RateLimiter.create(redisTemplate, RateLimitRule.builder()
            .rule("/api/auth/login", 10, 60)      // 登录接口：60秒内最多10次
            .rule("/api/auth/sms", 5, 300)        // 短信接口：5分钟内最多5次
            .rule("/api/**", 100, 1)              // 默认：每秒100次
            .build());
    }
}
```

### 27.4 数据安全

#### 27.4.1 敏感数据脱敏

| 数据类型 | 脱敏规则 | 示例 |
|----------|----------|------|
| 手机号 | 中间4位脱敏 | 138****8888 |
| 身份证 | 中间10位脱敏 | 310***********1234 |
| 银行卡 | 中间10位脱敏 | 6222************8888 |
| 密码 | 全脱敏 | ****** |
| 地址 | 部分脱敏 | 上海市浦东新区*** |

#### 27.4.2 数据加密存储

```java
// 敏感字段加密工具
@Component
public class SensitiveDataEncryptor {
    
    @Value("${sensitive.encrypt.key}")
    private String encryptKey;
    
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    
    public String encrypt(String plainText) {
        // AES 加密实现
    }
    
    public String decrypt(String cipherText) {
        // AES 解密实现
    }
}

// MyBatis 类型处理器
@MappedTypes(String.class)
public class EncryptedStringTypeHandler extends BaseTypeHandler<String> {
    
    @Autowired
    private SensitiveDataEncryptor encryptor;
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, encryptor.encrypt(parameter));
    }
    
    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value != null ? encryptor.decrypt(value) : null;
    }
}
```

### 27.5 SQL 注入防护

```java
// 使用 MyBatis-Plus 防止 SQL 注入
@Configuration
public class MybatisPlusConfig {
    
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // SQL 性能规范
        interceptor.addInnerInterceptor(new IllegalSQLInnerInterceptor());
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }
}

// 参数校验
@Data
public class OrderQueryDTO {
    
    @NotBlank(message = "订单号不能为空")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "订单号格式不正确")
    private String orderNo;
    
    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;
    
    @Max(value = 100, message = "每页最多100条")
    private Integer pageSize = 20;
}
```

### 27.6 XSS 防护

```java
// XSS 过滤器
@Component
public class XssFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        XssHttpServletRequestWrapper wrappedRequest = new XssHttpServletRequestWrapper(httpRequest);
        chain.doFilter(wrappedRequest, response);
    }
}

// XSS 工具类
public class XssUtil {
    
    private static final Pattern[] PATTERNS = {
        Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE),
        Pattern.compile("onerror(.*?)=", Pattern.CASE_INSENSITIVE)
    };
    
    public static String stripXSS(String value) {
        if (value == null) return null;
        String cleanValue = value;
        for (Pattern pattern : PATTERNS) {
            cleanValue = pattern.matcher(cleanValue).replaceAll("");
        }
        return cleanValue;
    }
}
```

### 27.7 CSRF 防护

```java
@Configuration
public class CsrfConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers("/api/auth/**", "/api/webhook/**")
            );
        return http.build();
    }
}
```

### 27.8 安全配置清单

| 安全项 | 配置要求 | 状态 |
|--------|----------|------|
| HTTPS | 强制 HTTPS 访问 | 必须 |
| Cookie | HttpOnly, Secure, SameSite | 必须 |
| CORS | 白名单配置 | 必须 |
| CSP | Content-Security-Policy | 推荐 |
| HSTS | Strict-Transport-Security | 推荐 |
| X-Frame-Options | DENY 或 SAMEORIGIN | 必须 |
| X-Content-Type-Options | nosniff | 必须 |
| X-XSS-Protection | 1; mode=block | 必须 |

### 27.9 安全响应头配置

```java
@Configuration
public class SecurityHeadersConfig {
    
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web
            .responseHeaders(headers -> headers
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'"))
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000))
                .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .contentTypeOptions(Customizer.withDefaults())
            );
    }
}
```

---

## 28. 销售管理模块设计

### 28.1 业务流程设计

#### 28.1.1 销售整体流程

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           销售业务完整流程                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐         │
│  │ 销售报价 │───>│ 销售订单 │───>│ 销售发货 │───>│ 销售退货 │         │
│  │          │    │          │    │          │    │          │         │
│  └──────────┘    └────┬─────┘    └────┬─────┘    └──────────┘         │
│                       │               │                                 │
│                       │               │                                 │
│                       ▼               ▼                                 │
│                 ┌──────────┐    ┌──────────┐                           │
│                 │ 生产订单 │    │ 库存出库 │                           │
│                 │ (库存不足)│    │          │                           │
│                 └──────────┘    └──────────┘                           │
│                                                                          │
│  状态流转：                                                               │
│  报价: draft → sent → accepted/rejected/expired                        │
│  订单: draft → pending → confirmed → producing → shipping → completed  │
│  发货: pending → shipped → received                                     │
│  退货: pending → approved → received → completed                        │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

#### 28.1.2 销售订单流程

```
┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐
│  草稿   │──>│ 待审核  │──>│ 已确认  │──>│ 生产中  │──>│ 发货中  │
│  draft  │   │ pending │   │confirmed│   │producing│   │shipping │
└─────────┘   └─────────┘   └─────────┘   └─────────┘   └─────────┘
                                │                             │
                                │ 取消                        │
                                ▼                             ▼
                           ┌─────────┐                  ┌─────────┐
                           │ 已取消  │                  │ 已完成  │
                           │cancelled│                  │completed│
                           └─────────┘                  └─────────┘
```

### 28.2 前端页面设计

#### 28.2.1 销售订单列表页

**文件路径：** `frontend/src/views/sales/order/index.vue`

```vue
<template>
  <el-card shadow="never">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="left-actions">
        <el-button type="primary" v-perm="'sales:order:add'" @click="handleAdd">新建订单</el-button>
        <el-button v-perm="'sales:order:import'" @click="handleImport">导入</el-button>
        <el-button v-perm="'sales:order:export'" @click="handleExport">导出</el-button>
      </div>
      <div class="right-search">
        <el-select v-model="queryParams.orderStatus" placeholder="订单状态" clearable>
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value"/>
        </el-select>
        <el-date-picker v-model="queryParams.dateRange" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期"/>
        <el-input v-model="queryParams.keyword" placeholder="订单号/客户名称" clearable @keyup.enter="handleQuery"/>
        <el-button type="primary" @click="handleQuery">查询</el-button>
      </div>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading">
      <el-table-column prop="orderNo" label="订单号" width="140">
        <template #default="{row}">
          <el-link type="primary" @click="handleDetail(row)">{{ row.orderNo }}</el-link>
        </template>
      </el-table-column>
      <el-table-column prop="customerName" label="客户名称" width="150"/>
      <el-table-column prop="orderDate" label="订单日期" width="100"/>
      <el-table-column prop="deliveryDate" label="交货日期" width="100"/>
      <el-table-column prop="totalQuantity" label="数量" width="80"/>
      <el-table-column prop="finalAmount" label="金额" width="100">
        <template #default="{row}">¥{{ row.finalAmount.toFixed(2) }}</template>
      </el-table-column>
      <el-table-column prop="orderStatus" label="订单状态" width="100">
        <template #default="{row}">
          <el-tag :type="getStatusType(row.orderStatus)">{{ getStatusName(row.orderStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="paymentStatus" label="付款状态" width="80">
        <template #default="{row}">
          <el-tag :type="getPaymentStatusType(row.paymentStatus)" size="small">
            {{ getPaymentStatusName(row.paymentStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="salesName" label="销售员" width="80"/>
      <el-table-column prop="productionOrderNo" label="生产单号" width="120">
        <template #default="{row}">
          <el-link v-if="row.productionOrderNo" type="primary" @click="goToProduction(row.productionOrderId)">
            {{ row.productionOrderNo }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{row}">
          <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
          <el-button link type="primary" v-perm="'sales:order:edit'" @click="handleEdit(row)" v-if="row.orderStatus === 'draft'">编辑</el-button>
          <el-button link type="primary" v-perm="'sales:order:submit'" @click="handleSubmit(row)" v-if="row.orderStatus === 'draft'">提交</el-button>
          <el-button link type="primary" v-perm="'sales:order:createProduction'" @click="handleCreateProduction(row)" v-if="row.orderStatus === 'confirmed' && !row.productionOrderId">生成生产单</el-button>
          <el-button link type="primary" v-perm="'sales:order:delivery'" @click="handleDelivery(row)" v-if="['confirmed', 'producing'].includes(row.orderStatus)">发货</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="queryParams.page"
      v-model:page-size="queryParams.size"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
    />
  </el-card>
</template>
```

#### 28.2.2 销售订单新增/编辑页

**文件路径：** `frontend/src/views/sales/order/add.vue`

```vue
<template>
  <div class="app-container">
    <!-- 固定头部 -->
    <div class="sticky-header">
      <el-page-header @back="goBack">
        <template #content>
          <span class="text-large font-600 mr-3">{{ isEdit ? '编辑订单' : '新建订单' }}</span>
        </template>
        <template #extra>
          <div class="flex items-center">
            <el-button @click="goBack">取消</el-button>
            <el-button @click="handleSaveDraft" :loading="saving">保存草稿</el-button>
            <el-button type="primary" @click="handleSubmit" :loading="submitting">提交订单</el-button>
          </div>
        </template>
      </el-page-header>
    </div>

    <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
      <!-- 基本信息 -->
      <el-card class="box-card" style="margin-bottom: 16px;">
        <template #header>基本信息</template>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="订单号" prop="orderNo">
              <el-input v-model="form.orderNo" placeholder="自动生成" :disabled="true"/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="客户" prop="customerId">
              <el-select v-model="form.customerId" placeholder="选择客户" filterable @change="handleCustomerChange">
                <el-option v-for="item in customerOptions" :key="item.id" :label="item.customerName" :value="item.id"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="订单日期" prop="orderDate">
              <el-date-picker v-model="form.orderDate" type="date" placeholder="选择日期"/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="交货日期" prop="deliveryDate">
              <el-date-picker v-model="form.deliveryDate" type="date" placeholder="选择日期"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="联系人">
              <el-input v-model="form.contactName" placeholder="联系人"/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="联系电话">
              <el-input v-model="form.phone" placeholder="联系电话"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收货地址">
              <el-input v-model="form.address" placeholder="收货地址"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 商品明细 -->
      <el-card class="box-card" style="margin-bottom: 16px;">
        <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <span>商品明细</span>
            <el-button type="primary" size="small" @click="handleAddProduct">添加商品</el-button>
          </div>
        </template>
        <el-table :data="form.details" border>
          <el-table-column prop="styleCode" label="款号" width="120">
            <template #default="{row}">
              <el-select v-model="row.styleId" placeholder="选择款号" filterable @change="handleStyleChange(row)">
                <el-option v-for="item in styleOptions" :key="item.id" :label="item.styleCode" :value="item.id"/>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="styleName" label="品名" width="150"/>
          <el-table-column prop="color" label="颜色" width="100">
            <template #default="{row}">
              <el-select v-model="row.skuId" placeholder="选择颜色尺码" @change="handleSkuChange(row)">
                <el-option v-for="sku in row.skuList" :key="sku.id" :label="`${sku.color}/${sku.size}`" :value="sku.id"/>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="size" label="尺码" width="80"/>
          <el-table-column prop="unit" label="单位" width="60"/>
          <el-table-column prop="quantity" label="数量" width="100">
            <template #default="{row}">
              <el-input-number v-model="row.quantity" :min="1" @change="calculateAmount(row)"/>
            </template>
          </el-table-column>
          <el-table-column prop="unitPrice" label="单价" width="100">
            <template #default="{row}">
              <el-input-number v-model="row.unitPrice" :precision="2" :min="0" @change="calculateAmount(row)"/>
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额" width="100">
            <template #default="{row}">¥{{ row.amount.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="remark" label="备注">
            <template #default="{row}">
              <el-input v-model="row.remark"/>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="60">
            <template #default="{$index}">
              <el-button link type="danger" @click="handleRemoveProduct($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 金额汇总 -->
        <div style="margin-top: 16px; text-align: right;">
          <div style="margin-bottom: 8px;">
            <span>数量合计：{{ totalQuantity }}</span>
            <span style="margin-left: 20px;">金额合计：¥{{ totalAmount.toFixed(2) }}</span>
          </div>
          <el-row :gutter="10" style="width: 400px; float: right;">
            <el-col :span="12">
              <el-form-item label="折扣率(%)">
                <el-input-number v-model="form.discountRate" :min="0" :max="100" @change="calculateTotal"/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="税率(%)">
                <el-input-number v-model="form.taxRate" :min="0" :max="100" @change="calculateTotal"/>
              </el-form-item>
            </el-col>
          </el-row>
          <div style="font-size: 18px; font-weight: bold; color: #f56c6c;">
            价税合计：¥{{ form.finalAmount.toFixed(2) }}
          </div>
        </div>
      </el-card>

      <!-- 备注 -->
      <el-card class="box-card">
        <template #header>备注</template>
        <el-form-item prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注信息"/>
        </el-form-item>
      </el-card>
    </el-form>
  </div>
</template>
```

#### 28.2.3 销售发货单页面

**文件路径：** `frontend/src/views/sales/delivery/index.vue`

```vue
<template>
  <el-card shadow="never">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="left-actions">
        <el-button type="primary" v-perm="'sales:delivery:add'" @click="handleAdd">新建发货单</el-button>
      </div>
      <div class="right-search">
        <el-select v-model="queryParams.deliveryStatus" placeholder="发货状态" clearable>
          <el-option label="待发货" value="pending"/>
          <el-option label="已发货" value="shipped"/>
          <el-option label="已签收" value="received"/>
        </el-select>
        <el-date-picker v-model="queryParams.dateRange" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期"/>
        <el-input v-model="queryParams.keyword" placeholder="发货单号/订单号/客户" clearable/>
        <el-button type="primary" @click="handleQuery">查询</el-button>
      </div>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading">
      <el-table-column prop="deliveryNo" label="发货单号" width="140"/>
      <el-table-column prop="salesOrderNo" label="订单号" width="140"/>
      <el-table-column prop="customerName" label="客户名称" width="150"/>
      <el-table-column prop="deliveryDate" label="发货日期" width="100"/>
      <el-table-column prop="totalQuantity" label="发货数量" width="80"/>
      <el-table-column prop="logisticsCompany" label="物流公司" width="100"/>
      <el-table-column prop="logisticsNo" label="物流单号" width="120"/>
      <el-table-column prop="deliveryStatus" label="发货状态" width="80">
        <template #default="{row}">
          <el-tag :type="getStatusType(row.deliveryStatus)">{{ getStatusName(row.deliveryStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="salesName" label="销售员" width="80"/>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{row}">
          <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
          <el-button link type="primary" v-perm="'sales:delivery:ship'" @click="handleShip(row)" v-if="row.deliveryStatus === 'pending'">发货</el-button>
          <el-button link type="primary" @click="handlePrint(row)">打印</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="queryParams.page"
      v-model:page-size="queryParams.size"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
    />
  </el-card>
</template>
```

### 28.3 API接口设计

#### 28.3.1 客户管理接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 客户列表 | GET | /api/customer/list | 分页查询客户列表 |
| 客户详情 | GET | /api/customer/{id} | 获取客户详情 |
| 新增客户 | POST | /api/customer | 新增客户 |
| 修改客户 | PUT | /api/customer/{id} | 修改客户信息 |
| 删除客户 | DELETE | /api/customer/{id} | 删除客户 |
| 客户选择器 | GET | /api/customer/select | 获取客户下拉列表 |

#### 28.3.2 销售报价接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 报价单列表 | GET | /api/quotation/list | 分页查询报价单 |
| 报价单详情 | GET | /api/quotation/{id} | 获取报价单详情 |
| 新增报价单 | POST | /api/quotation | 新增报价单 |
| 修改报价单 | PUT | /api/quotation/{id} | 修改报价单 |
| 删除报价单 | DELETE | /api/quotation/{id} | 删除报价单 |
| 发送报价 | POST | /api/quotation/{id}/send | 发送报价给客户 |
| 报价转订单 | POST | /api/quotation/{id}/convert | 报价单转销售订单 |

#### 28.3.3 销售订单接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 订单列表 | GET | /api/sales/order/list | 分页查询订单 |
| 订单详情 | GET | /api/sales/order/{id} | 获取订单详情 |
| 新增订单 | POST | /api/sales/order | 新增订单 |
| 修改订单 | PUT | /api/sales/order/{id} | 修改订单 |
| 删除订单 | DELETE | /api/sales/order/{id} | 删除订单（仅草稿） |
| 提交审核 | POST | /api/sales/order/{id}/submit | 提交订单审核 |
| 审核订单 | POST | /api/sales/order/{id}/audit | 审核订单 |
| 取消订单 | POST | /api/sales/order/{id}/cancel | 取消订单 |
| 生成生产单 | POST | /api/sales/order/{id}/createProduction | 生成生产订单 |

#### 28.3.4 销售发货接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 发货单列表 | GET | /api/sales/delivery/list | 分页查询发货单 |
| 发货单详情 | GET | /api/sales/delivery/{id} | 获取发货单详情 |
| 新增发货单 | POST | /api/sales/delivery | 新增发货单 |
| 确认发货 | POST | /api/sales/delivery/{id}/ship | 确认发货 |
| 确认签收 | POST | /api/sales/delivery/{id}/receive | 确认签收 |

#### 28.3.5 销售退货接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 退货单列表 | GET | /api/sales/return/list | 分页查询退货单 |
| 退货单详情 | GET | /api/sales/return/{id} | 获取退货单详情 |
| 新增退货单 | POST | /api/sales/return | 新增退货单 |
| 审核退货 | POST | /api/sales/return/{id}/audit | 审核退货单 |
| 确认收货 | POST | /api/sales/return/{id}/receive | 确认退货入库 |

### 28.4 接口统计

| 模块 | 接口数量 |
|------|---------|
| 客户管理 | 6 |
| 销售报价 | 7 |
| 销售订单 | 9 |
| 销售发货 | 5 |
| 销售退货 | 5 |
| **合计** | **32** |

---

## 29. 采购管理模块设计

### 29.1 业务流程设计

#### 29.1.1 采购整体流程

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           采购业务完整流程                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐         │
│  │ 采购申请 │───>│ 采购订单 │───>│ 采购入库 │───>│ 采购退货 │         │
│  │          │    │          │    │          │    │          │         │
│  └──────────┘    └────┬─────┘    └────┬─────┘    └──────────┘         │
│                       │               │                                 │
│                       │               │                                 │
│                       ▼               ▼                                 │
│                 ┌──────────┐    ┌──────────┐                           │
│                 │ 供应商   │    │ 库存入库 │                           │
│                 │ 选择     │    │          │                           │
│                 └──────────┘    └──────────┘                           │
│                                                                          │
│  状态流转：                                                               │
│  申请: draft → pending → approved/rejected → ordered                   │
│  订单: draft → pending → confirmed → receiving → completed             │
│  入库: pending → partial → completed                                    │
│  退货: pending → approved → shipped → completed                         │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

#### 29.1.2 采购订单流程

```
┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐
│  草稿   │──>│ 待审核  │──>│ 已确认  │──>│ 收货中  │──>│ 已完成  │
│  draft  │   │ pending │   │confirmed│   │receiving│   │completed│
└─────────┘   └─────────┘   └─────────┘   └─────────┘   └─────────┘
                                │
                                │ 取消
                                ▼
                           ┌─────────┐
                           │ 已取消  │
                           │cancelled│
                           └─────────┘
```

### 29.2 前端页面设计

#### 29.2.1 采购订单列表页

**文件路径：** `frontend/src/views/purchase/order/index.vue`

```vue
<template>
  <el-card shadow="never">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="left-actions">
        <el-button type="primary" v-perm="'purchase:order:add'" @click="handleAdd">新建采购单</el-button>
        <el-button v-perm="'purchase:order:export'" @click="handleExport">导出</el-button>
      </div>
      <div class="right-search">
        <el-select v-model="queryParams.orderStatus" placeholder="订单状态" clearable>
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value"/>
        </el-select>
        <el-select v-model="queryParams.supplierId" placeholder="供应商" clearable filterable>
          <el-option v-for="item in supplierOptions" :key="item.id" :label="item.supplierName" :value="item.id"/>
        </el-select>
        <el-date-picker v-model="queryParams.dateRange" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期"/>
        <el-input v-model="queryParams.keyword" placeholder="订单号" clearable @keyup.enter="handleQuery"/>
        <el-button type="primary" @click="handleQuery">查询</el-button>
      </div>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading">
      <el-table-column prop="orderNo" label="订单号" width="140">
        <template #default="{row}">
          <el-link type="primary" @click="handleDetail(row)">{{ row.orderNo }}</el-link>
        </template>
      </el-table-column>
      <el-table-column prop="supplierName" label="供应商" width="150"/>
      <el-table-column prop="orderDate" label="订单日期" width="100"/>
      <el-table-column prop="expectDate" label="预计到货" width="100"/>
      <el-table-column prop="totalQuantity" label="数量" width="80"/>
      <el-table-column prop="finalAmount" label="金额" width="100">
        <template #default="{row}">¥{{ row.finalAmount.toFixed(2) }}</template>
      </el-table-column>
      <el-table-column prop="orderStatus" label="订单状态" width="100">
        <template #default="{row}">
          <el-tag :type="getStatusType(row.orderStatus)">{{ getStatusName(row.orderStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="paymentStatus" label="付款状态" width="80">
        <template #default="{row}">
          <el-tag :type="getPaymentStatusType(row.paymentStatus)" size="small">
            {{ getPaymentStatusName(row.paymentStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="buyerName" label="采购员" width="80"/>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{row}">
          <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
          <el-button link type="primary" v-perm="'purchase:order:edit'" @click="handleEdit(row)" v-if="row.orderStatus === 'draft'">编辑</el-button>
          <el-button link type="primary" v-perm="'purchase:order:submit'" @click="handleSubmit(row)" v-if="row.orderStatus === 'draft'">提交</el-button>
          <el-button link type="primary" v-perm="'purchase:order:inbound'" @click="handleInbound(row)" v-if="['confirmed', 'receiving'].includes(row.orderStatus)">入库</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="queryParams.page"
      v-model:page-size="queryParams.size"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
    />
  </el-card>
</template>
```

#### 29.2.2 采购入库单页面

**文件路径：** `frontend/src/views/purchase/inbound/index.vue`

```vue
<template>
  <el-card shadow="never">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="left-actions">
        <el-button type="primary" v-perm="'purchase:inbound:add'" @click="handleAdd">新建入库单</el-button>
      </div>
      <div class="right-search">
        <el-select v-model="queryParams.inboundStatus" placeholder="入库状态" clearable>
          <el-option label="待入库" value="pending"/>
          <el-option label="部分入库" value="partial"/>
          <el-option label="已完成" value="completed"/>
        </el-select>
        <el-date-picker v-model="queryParams.dateRange" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期"/>
        <el-input v-model="queryParams.keyword" placeholder="入库单号/采购单号" clearable/>
        <el-button type="primary" @click="handleQuery">查询</el-button>
      </div>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading">
      <el-table-column prop="inboundNo" label="入库单号" width="140"/>
      <el-table-column prop="purchaseOrderNo" label="采购单号" width="140"/>
      <el-table-column prop="supplierName" label="供应商" width="150"/>
      <el-table-column prop="inboundDate" label="入库日期" width="100"/>
      <el-table-column prop="warehouseName" label="仓库" width="100"/>
      <el-table-column prop="totalQuantity" label="入库数量" width="80"/>
      <el-table-column prop="totalAmount" label="入库金额" width="100">
        <template #default="{row}">¥{{ row.totalAmount.toFixed(2) }}</template>
      </el-table-column>
      <el-table-column prop="inboundStatus" label="入库状态" width="80">
        <template #default="{row}">
          <el-tag :type="getStatusType(row.inboundStatus)">{{ getStatusName(row.inboundStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="qcStatus" label="质检状态" width="80">
        <template #default="{row}">
          <el-tag v-if="row.qcStatus" :type="getQcStatusType(row.qcStatus)" size="small">
            {{ getQcStatusName(row.qcStatus) }}
          </el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{row}">
          <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
          <el-button link type="primary" v-perm="'purchase:inbound:confirm'" @click="handleConfirm(row)" v-if="row.inboundStatus === 'pending'">确认入库</el-button>
          <el-button link type="primary" @click="handlePrint(row)">打印</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="queryParams.page"
      v-model:page-size="queryParams.size"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
    />
  </el-card>
</template>
```

### 29.3 API接口设计

#### 29.3.1 供应商管理接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 供应商列表 | GET | /api/supplier/list | 分页查询供应商 |
| 供应商详情 | GET | /api/supplier/{id} | 获取供应商详情 |
| 新增供应商 | POST | /api/supplier | 新增供应商 |
| 修改供应商 | PUT | /api/supplier/{id} | 修改供应商 |
| 删除供应商 | DELETE | /api/supplier/{id} | 删除供应商 |
| 供应商选择器 | GET | /api/supplier/select | 获取供应商下拉列表 |
| 供应商评估 | POST | /api/supplier/{id}/evaluate | 提交供应商评估 |

#### 29.3.2 采购申请接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 申请单列表 | GET | /api/purchase/request/list | 分页查询申请单 |
| 申请单详情 | GET | /api/purchase/request/{id} | 获取申请单详情 |
| 新增申请单 | POST | /api/purchase/request | 新增申请单 |
| 修改申请单 | PUT | /api/purchase/request/{id} | 修改申请单 |
| 删除申请单 | DELETE | /api/purchase/request/{id} | 删除申请单 |
| 提交审核 | POST | /api/purchase/request/{id}/submit | 提交申请审核 |
| 审核申请 | POST | /api/purchase/request/{id}/audit | 审核申请单 |

#### 29.3.3 采购订单接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 订单列表 | GET | /api/purchase/order/list | 分页查询订单 |
| 订单详情 | GET | /api/purchase/order/{id} | 获取订单详情 |
| 新增订单 | POST | /api/purchase/order | 新增订单 |
| 修改订单 | PUT | /api/purchase/order/{id} | 修改订单 |
| 删除订单 | DELETE | /api/purchase/order/{id} | 删除订单 |
| 提交审核 | POST | /api/purchase/order/{id}/submit | 提交订单审核 |
| 审核订单 | POST | /api/purchase/order/{id}/audit | 审核订单 |
| 取消订单 | POST | /api/purchase/order/{id}/cancel | 取消订单 |

#### 29.3.4 采购入库接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 入库单列表 | GET | /api/purchase/inbound/list | 分页查询入库单 |
| 入库单详情 | GET | /api/purchase/inbound/{id} | 获取入库单详情 |
| 新增入库单 | POST | /api/purchase/inbound | 新增入库单 |
| 确认入库 | POST | /api/purchase/inbound/{id}/confirm | 确认入库 |

### 29.4 接口统计

| 模块 | 接口数量 |
|------|---------|
| 供应商管理 | 7 |
| 采购申请 | 7 |
| 采购订单 | 8 |
| 采购入库 | 4 |
| 采购退货 | 5 |
| **合计** | **31** |

---

## 30. 财务收付款设计

### 30.1 业务流程设计

#### 30.1.1 收款流程

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           收款业务流程                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐                          │
│  │ 销售订单 │───>│ 生成收款 │───>│ 确认收款 │                          │
│  │          │    │ 单       │    │          │                          │
│  └──────────┘    └──────────┘    └──────────┘                          │
│                       │               │                                 │
│                       │               │                                 │
│                       ▼               ▼                                 │
│                 ┌──────────┐    ┌──────────┐                           │
│                 │ 选择账户 │    │ 更新订单 │                           │
│                 │          │    │ 付款状态 │                           │
│                 └──────────┘    └──────────┘                           │
│                                                                          │
│  收款类型：                                                               │
│  - 销售收款：关联销售订单，核销应收账款                                  │
│  - 预收款：客户预付款，可后续核销订单                                    │
│  - 退款收回：销售退货后的退款收回                                        │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

#### 30.1.2 付款流程

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           付款业务流程                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐         │
│  │ 采购订单 │───>│ 付款申请 │───>│ 付款审批 │───>│ 付款执行 │         │
│  │          │    │          │    │          │    │          │         │
│  └──────────┘    └──────────┘    └──────────┘    └──────────┘         │
│                                                       │                 │
│                                                       │                 │
│                                                       ▼                 │
│                                                 ┌──────────┐           │
│                                                 │ 更新订单 │           │
│                                                 │ 付款状态 │           │
│                                                 └──────────┘           │
│                                                                          │
│  付款类型：                                                               │
│  - 采购付款：关联采购订单，核销应付账款                                  │
│  - 预付款：供应商预付款                                                  │
│  - 工资发放：关联工资结算单                                              │
│  - 退款：采购退货后的退款                                                │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### 30.2 前端页面设计

#### 30.2.1 收款单页面

**文件路径：** `frontend/src/views/finance/receipt/index.vue`

```vue
<template>
  <el-card shadow="never">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="left-actions">
        <el-button type="primary" v-perm="'finance:receipt:add'" @click="handleAdd">新建收款单</el-button>
      </div>
      <div class="right-search">
        <el-select v-model="queryParams.receiptType" placeholder="收款类型" clearable>
          <el-option label="销售收款" value="sales"/>
          <el-option label="预收款" value="advance"/>
          <el-option label="退款收回" value="refund"/>
        </el-select>
        <el-date-picker v-model="queryParams.dateRange" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期"/>
        <el-input v-model="queryParams.keyword" placeholder="收款单号/客户名称" clearable/>
        <el-button type="primary" @click="handleQuery">查询</el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" style="margin-bottom: 16px;">
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="今日收款" :value="statistics.todayAmount" :precision="2" prefix="¥"/>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="本月收款" :value="statistics.monthAmount" :precision="2" prefix="¥"/>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="待确认收款" :value="statistics.pendingCount"/>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="待确认金额" :value="statistics.pendingAmount" :precision="2" prefix="¥"/>
        </el-card>
      </el-col>
    </el-row>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading">
      <el-table-column prop="receiptNo" label="收款单号" width="140"/>
      <el-table-column prop="receiptType" label="收款类型" width="80">
        <template #default="{row}">
          <el-tag size="small">{{ getTypeName(row.receiptType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="customerName" label="客户名称" width="150"/>
      <el-table-column prop="salesOrderNo" label="关联订单" width="140"/>
      <el-table-column prop="receiptDate" label="收款日期" width="100"/>
      <el-table-column prop="receiptAmount" label="收款金额" width="100">
        <template #default="{row}">
          <span style="color: #67c23a; font-weight: bold;">¥{{ row.receiptAmount.toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="paymentMethod" label="付款方式" width="80"/>
      <el-table-column prop="bankAccountName" label="收款账户" width="120"/>
      <el-table-column prop="receiptStatus" label="状态" width="80">
        <template #default="{row}">
          <el-tag :type="getStatusType(row.receiptStatus)">{{ getStatusName(row.receiptStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{row}">
          <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
          <el-button link type="primary" v-perm="'finance:receipt:confirm'" @click="handleConfirm(row)" v-if="row.receiptStatus === 'pending'">确认</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="queryParams.page"
      v-model:page-size="queryParams.size"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
    />
  </el-card>
</template>
```

#### 30.2.2 付款单页面

**文件路径：** `frontend/src/views/finance/payment/index.vue`

```vue
<template>
  <el-card shadow="never">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="left-actions">
        <el-button type="primary" v-perm="'finance:payment:add'" @click="handleAdd">新建付款单</el-button>
      </div>
      <div class="right-search">
        <el-select v-model="queryParams.paymentType" placeholder="付款类型" clearable>
          <el-option label="采购付款" value="purchase"/>
          <el-option label="预付款" value="advance"/>
          <el-option label="工资发放" value="wage"/>
          <el-option label="退款" value="refund"/>
        </el-select>
        <el-date-picker v-model="queryParams.dateRange" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期"/>
        <el-input v-model="queryParams.keyword" placeholder="付款单号/供应商名称" clearable/>
        <el-button type="primary" @click="handleQuery">查询</el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" style="margin-bottom: 16px;">
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="今日付款" :value="statistics.todayAmount" :precision="2" prefix="¥"/>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="本月付款" :value="statistics.monthAmount" :precision="2" prefix="¥"/>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="待审核付款" :value="statistics.pendingCount"/>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="待审核金额" :value="statistics.pendingAmount" :precision="2" prefix="¥"/>
        </el-card>
      </el-col>
    </el-row>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading">
      <el-table-column prop="paymentNo" label="付款单号" width="140"/>
      <el-table-column prop="paymentType" label="付款类型" width="80">
        <template #default="{row}">
          <el-tag size="small">{{ getTypeName(row.paymentType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="supplierName" label="供应商" width="150"/>
      <el-table-column prop="purchaseOrderNo" label="关联订单" width="140"/>
      <el-table-column prop="paymentDate" label="付款日期" width="100"/>
      <el-table-column prop="paymentAmount" label="付款金额" width="100">
        <template #default="{row}">
          <span style="color: #f56c6c; font-weight: bold;">¥{{ row.paymentAmount.toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="paymentMethod" label="付款方式" width="80"/>
      <el-table-column prop="bankAccountName" label="付款账户" width="120"/>
      <el-table-column prop="paymentStatus" label="状态" width="80">
        <template #default="{row}">
          <el-tag :type="getStatusType(row.paymentStatus)">{{ getStatusName(row.paymentStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{row}">
          <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
          <el-button link type="primary" v-perm="'finance:payment:approve'" @click="handleApprove(row)" v-if="row.paymentStatus === 'pending'">审核</el-button>
          <el-button link type="primary" v-perm="'finance:payment:pay'" @click="handlePay(row)" v-if="row.paymentStatus === 'approved'">付款</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="queryParams.page"
      v-model:page-size="queryParams.size"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
    />
  </el-card>
</template>
```

### 30.3 API接口设计

#### 30.3.1 银行账户接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 账户列表 | GET | /api/finance/account/list | 账户列表 |
| 账户详情 | GET | /api/finance/account/{id} | 账户详情 |
| 新增账户 | POST | /api/finance/account | 新增账户 |
| 修改账户 | PUT | /api/finance/account/{id} | 修改账户 |
| 账户余额 | GET | /api/finance/account/balance | 账户余额查询 |

#### 30.3.2 收款接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 收款单列表 | GET | /api/finance/receipt/list | 分页查询收款单 |
| 收款单详情 | GET | /api/finance/receipt/{id} | 获取收款单详情 |
| 新增收款单 | POST | /api/finance/receipt | 新增收款单 |
| 确认收款 | POST | /api/finance/receipt/{id}/confirm | 确认收款 |
| 取消收款 | POST | /api/finance/receipt/{id}/cancel | 取消收款单 |
| 收款统计 | GET | /api/finance/receipt/statistics | 收款统计数据 |

#### 30.3.3 付款接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 付款单列表 | GET | /api/finance/payment/list | 分页查询付款单 |
| 付款单详情 | GET | /api/finance/payment/{id} | 获取付款单详情 |
| 新增付款单 | POST | /api/finance/payment | 新增付款单 |
| 审核付款 | POST | /api/finance/payment/{id}/approve | 审核付款单 |
| 执行付款 | POST | /api/finance/payment/{id}/pay | 执行付款 |
| 取消付款 | POST | /api/finance/payment/{id}/cancel | 取消付款单 |
| 付款统计 | GET | /api/finance/payment/statistics | 付款统计数据 |

#### 30.3.4 对账接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 客户对账单列表 | GET | /api/finance/statement/customer/list | 客户对账单列表 |
| 生成客户对账单 | POST | /api/finance/statement/customer/generate | 生成客户对账单 |
| 客户对账确认 | POST | /api/finance/statement/customer/{id}/confirm | 确认客户对账单 |
| 供应商对账单列表 | GET | /api/finance/statement/supplier/list | 供应商对账单列表 |
| 生成供应商对账单 | POST | /api/finance/statement/supplier/generate | 生成供应商对账单 |
| 供应商对账确认 | POST | /api/finance/statement/supplier/{id}/confirm | 确认供应商对账单 |

### 30.4 接口统计

| 模块 | 接口数量 |
|------|---------|
| 银行账户 | 5 |
| 收款管理 | 6 |
| 付款管理 | 7 |
| 对账管理 | 6 |
| **合计** | **24** |

---

## 31. 错误码定义

### 31.1 错误码规范

错误码采用 **5位数字编码**，格式为：`XXYYY`

- **XX**：模块编码（2位）
- **YYY**：错误序号（3位）

| 模块编码 | 模块名称 |
|----------|----------|
| 10 | 系统级错误 |
| 11 | 认证授权错误 |
| 12 | 参数校验错误 |
| 20 | 用户管理 |
| 21 | 组织管理 |
| 30 | 销售管理 |
| 31 | 采购管理 |
| 32 | 库存管理 |
| 33 | 财务管理 |
| 40 | 生产管理 |
| 41 | 工序管理 |
| 42 | 扎包管理 |
| 43 | 质量管理 |
| 44 | 工资管理 |
| 50 | 基础数据 |

### 31.2 系统级错误码（10000-19999）

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 10000 | 系统异常 | 未知系统异常 |
| 10001 | 服务暂不可用 | 系统维护中 |
| 10002 | 请求超时 | 请求处理超时 |
| 10003 | 请求频率超限 | 触发限流 |
| 10004 | 服务降级 | 服务熔断 |

### 31.3 认证授权错误码（11000-11999）

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 11001 | 未登录或登录已过期 | 需要重新登录 |
| 11002 | 用户名或密码错误 | 登录失败 |
| 11003 | 账号已被禁用 | 账号状态异常 |
| 11004 | 账号已被锁定 | 密码错误次数过多 |
| 11005 | Token无效 | JWT Token无效 |
| 11006 | Token已过期 | JWT Token过期 |
| 11007 | 无权限访问 | 权限不足 |
| 11008 | 验证码错误 | 图形验证码错误 |
| 11009 | 短信验证码错误 | 短信验证码错误 |
| 11010 | 短信验证码已过期 | 验证码过期 |
| 11011 | 微信授权失败 | 微信登录失败 |

### 31.4 参数校验错误码（12000-12999）

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 12001 | 参数不能为空 | 必填参数缺失 |
| 12002 | 参数格式错误 | 参数类型不匹配 |
| 12003 | 参数超出范围 | 数值参数超限 |
| 12004 | 参数长度超限 | 字符串参数过长 |
| 12005 | 日期格式错误 | 日期参数格式错误 |
| 12006 | JSON格式错误 | JSON参数解析失败 |

### 31.5 销售管理错误码（30000-30999）

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 30001 | 客户不存在 | 客户ID无效 |
| 30002 | 客户已被禁用 | 客户状态不可用 |
| 30003 | 客户信用额度不足 | 超出信用额度 |
| 30004 | 订单不存在 | 订单ID无效 |
| 30005 | 订单状态不允许此操作 | 订单状态校验失败 |
| 30006 | 订单已存在生产单 | 重复生成生产单 |
| 30007 | 订单商品库存不足 | 库存校验失败 |
| 30008 | 报价单不存在 | 报价单ID无效 |
| 30009 | 报价单已过期 | 报价单有效期已过 |
| 30010 | 发货数量超出订单数量 | 发货校验失败 |
| 30011 | 退货数量超出已发货数量 | 退货校验失败 |

### 31.6 采购管理错误码（31000-31999）

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 31001 | 供应商不存在 | 供应商ID无效 |
| 31002 | 供应商已被禁用 | 供应商状态不可用 |
| 31003 | 采购订单不存在 | 订单ID无效 |
| 31004 | 采购订单状态不允许此操作 | 订单状态校验失败 |
| 31005 | 入库数量超出订单数量 | 入库校验失败 |
| 31006 | 采购申请不存在 | 申请单ID无效 |
| 31007 | 采购申请已下单 | 重复下单 |

### 31.7 库存管理错误码（32000-32999）

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 32001 | 仓库不存在 | 仓库ID无效 |
| 32002 | 库存不足 | 库存扣减失败 |
| 32003 | 库存被占用 | 库存已被订单占用 |
| 32004 | 批次不存在 | 批次号无效 |
| 32005 | 盘点单不存在 | 盘点单ID无效 |
| 32006 | 调拨单不存在 | 调拨单ID无效 |

### 31.8 财务管理错误码（33000-33999）

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 33001 | 账户不存在 | 银行账户ID无效 |
| 33002 | 账户余额不足 | 付款余额不足 |
| 33003 | 收款单不存在 | 收款单ID无效 |
| 33004 | 付款单不存在 | 付款单ID无效 |
| 33005 | 收款单已确认 | 重复操作 |
| 33006 | 付款单已审核 | 重复操作 |
| 33007 | 发票不存在 | 发票ID无效 |
| 33008 | 发票已作废 | 发票状态异常 |

### 31.9 生产管理错误码（40000-40999）

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 40001 | 生产订单不存在 | 订单ID无效 |
| 40002 | 生产订单状态不允许此操作 | 订单状态校验失败 |
| 40003 | 款号不存在 | 款号ID无效 |
| 40004 | 工序配置不存在 | 配置ID无效 |
| 40005 | 扎包不存在 | 扎包ID无效 |
| 40006 | 扎包已完成 | 扎包状态异常 |
| 40007 | 工序流程未完成 | 流程校验失败 |

### 31.10 质量管理错误码（43000-43999）

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 43001 | 质检记录不存在 | 质检ID无效 |
| 43002 | 首件未确认 | 首件检验未通过 |
| 43003 | 返工单不存在 | 返工ID无效 |
| 43004 | 返工已完成 | 返工状态异常 |

### 31.11 工资管理错误码（44000-44999）

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 44001 | 计件记录不存在 | 记录ID无效 |
| 44002 | 计件记录已结算 | 重复结算 |
| 44003 | 结算单不存在 | 结算单ID无效 |
| 44004 | 结算单已确认 | 结算状态异常 |
| 44005 | 结算单已发放 | 重复发放 |

### 31.12 统一响应格式

```json
{
  "code": 0,
  "message": "success",
  "data": {},
  "timestamp": 1711612800000,
  "traceId": "abc123def456"
}
```

**失败响应示例：**

```json
{
  "code": 30005,
  "message": "订单状态不允许此操作",
  "data": null,
  "timestamp": 1711612800000,
  "traceId": "abc123def456"
}
```

---

## 32. 库存管理详细设计

### 32.1 业务流程设计

#### 32.1.1 库存盘点流程

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           库存盘点流程                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐         │
│  │ 创建盘点 │───>│ 盘点作业 │───>│ 审核差异 │───>│ 过账处理 │         │
│  │ 单       │    │          │    │          │    │          │         │
│  └──────────┘    └──────────┘    └──────────┘    └──────────┘         │
│                       │               │                                 │
│                       │               │                                 │
│                       ▼               ▼                                 │
│                 ┌──────────┐    ┌──────────┐                           │
│                 │ 实盘录入 │    │ 生成调整 │                           │
│                 │ (PDA扫码)│    │ 单       │                           │
│                 └──────────┘    └──────────┘                           │
│                                                                          │
│  盘点类型：                                                               │
│  - 全盘：对所有库存进行全面盘点                                           │
│  - 部分盘点：按品类/库位等条件部分盘点                                    │
│  - 循环盘点：按计划定期盘点部分SKU                                        │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

#### 32.1.2 库存调拨流程

```
┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐
│  创建   │──>│  审核   │──>│  发货   │──>│  收货   │
│  草稿   │   │         │   │         │   │         │
└─────────┘   └─────────┘   └─────────┘   └─────────┘
                               │
                               │
                               ▼
                         ┌─────────┐
                         │ 库存更新│
                         │ (双方)  │
                         └─────────┘
```

### 32.2 库存预警规则

| 预警类型 | 触发条件 | 预警级别 | 通知对象 |
|----------|----------|----------|----------|
| 库存下限 | 库存 < 安全库存 | 紧急 | 采购、仓库 |
| 库存上限 | 库存 > 最大库存 | 普通 | 仓库、销售 |
| 临期预警 | 距过期 < N天 | 紧急 | 销售、仓库 |
| 滞销预警 | 无出库 > N天 | 普通 | 销售、采购 |
| 库存异常 | 负库存/超储 | 紧急 | 仓库、财务 |

### 32.3 前端页面设计

#### 32.3.1 库存盘点单页面

**文件路径：** `frontend/src/views/inventory/check/index.vue`

```vue
<template>
  <el-card shadow="never">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="left-actions">
        <el-button type="primary" v-perm="'inventory:check:add'" @click="handleAdd">新建盘点单</el-button>
      </div>
      <div class="right-search">
        <el-select v-model="queryParams.warehouseId" placeholder="仓库" clearable>
          <el-option v-for="item in warehouseOptions" :key="item.id" :label="item.warehouseName" :value="item.id"/>
        </el-select>
        <el-select v-model="queryParams.checkStatus" placeholder="盘点状态" clearable>
          <el-option label="草稿" value="draft"/>
          <el-option label="盘点中" value="checking"/>
          <el-option label="已完成" value="completed"/>
          <el-option label="已过账" value="posted"/>
        </el-select>
        <el-date-picker v-model="queryParams.dateRange" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期"/>
        <el-button type="primary" @click="handleQuery">查询</el-button>
      </div>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading">
      <el-table-column prop="checkNo" label="盘点单号" width="140"/>
      <el-table-column prop="warehouseName" label="仓库" width="120"/>
      <el-table-column prop="checkType" label="盘点类型" width="80">
        <template #default="{row}">
          <el-tag size="small">{{ getTypeName(row.checkType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="checkDate" label="盘点日期" width="100"/>
      <el-table-column prop="totalSku" label="SKU总数" width="80"/>
      <el-table-column prop="checkSku" label="已盘SKU" width="80"/>
      <el-table-column prop="diffSku" label="差异SKU" width="80">
        <template #default="{row}">
          <span :style="{color: row.diffSku > 0 ? '#f56c6c' : '#67c23a'}">{{ row.diffSku }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="diffAmount" label="差异金额" width="100">
        <template #default="{row}">
          <span :style="{color: row.diffAmount > 0 ? '#f56c6c' : '#67c23a'}">¥{{ row.diffAmount.toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="checkStatus" label="状态" width="80">
        <template #default="{row}">
          <el-tag :type="getStatusType(row.checkStatus)">{{ getStatusName(row.checkStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{row}">
          <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
          <el-button link type="primary" v-perm="'inventory:check:edit'" @click="handleEdit(row)" v-if="row.checkStatus === 'draft'">编辑</el-button>
          <el-button link type="primary" v-perm="'inventory:check:start'" @click="handleStart(row)" v-if="row.checkStatus === 'draft'">开始盘点</el-button>
          <el-button link type="primary" v-perm="'inventory:check:post'" @click="handlePost(row)" v-if="row.checkStatus === 'completed'">过账</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="queryParams.page"
      v-model:page-size="queryParams.size"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
    />
  </el-card>
</template>
```

### 32.4 API接口设计

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 盘点单列表 | GET | /api/inventory/check/list | 分页查询盘点单 |
| 盘点单详情 | GET | /api/inventory/check/{id} | 获取盘点单详情 |
| 新增盘点单 | POST | /api/inventory/check | 新增盘点单 |
| 开始盘点 | POST | /api/inventory/check/{id}/start | 开始盘点 |
| 盘点录入 | POST | /api/inventory/check/{id}/scan | 盘点数据录入 |
| 完成盘点 | POST | /api/inventory/check/{id}/complete | 完成盘点 |
| 过账处理 | POST | /api/inventory/check/{id}/post | 盘点过账 |
| 调拨单列表 | GET | /api/inventory/transfer/list | 分页查询调拨单 |
| 调拨单详情 | GET | /api/inventory/transfer/{id} | 获取调拨单详情 |
| 新增调拨单 | POST | /api/inventory/transfer | 新增调拨单 |
| 调拨发货 | POST | /api/inventory/transfer/{id}/ship | 调拨发货 |
| 调拨收货 | POST | /api/inventory/transfer/{id}/receive | 调拨收货 |
| 库存预警列表 | GET | /api/inventory/warning/list | 库存预警列表 |

---

## 33. 报表统计详细设计

### 33.1 报表分类

| 报表类别 | 报表名称 | 数据来源 | 更新频率 |
|----------|----------|----------|----------|
| 销售报表 | 销售日报/月报/排行 | jxc_sales_order | 实时/每日 |
| 采购报表 | 采购日报/月报/供应商分析 | jxc_purchase_order | 实时/每日 |
| 库存报表 | 库存周转/库龄分析/呆滞库存 | jxc_unified_inventory | 每日 |
| 生产报表 | 生产效率/产量统计/质量分析 | prod_* | 实时 |
| 财务报表 | 应收应付/收付款统计 | jxc_* | 每日 |
| 工资报表 | 计件工资汇总/明细 | prod_piecework_record | 每月 |

### 33.2 销售报表设计

#### 33.2.1 销售日报

| 指标 | 计算公式 | 说明 |
|------|----------|------|
| 订单数 | COUNT(DISTINCT order_id) | 当日订单数 |
| 订单金额 | SUM(final_amount) | 当日订单金额 |
| 发货金额 | SUM(delivery.amount) | 当日发货金额 |
| 收款金额 | SUM(receipt_amount) | 当日收款金额 |
| 客户数 | COUNT(DISTINCT customer_id) | 当日下单客户数 |
|客单价 | 订单金额 / 订单数 | 平均客单价 |

**SQL示例：**

```sql
-- 销售日报
SELECT 
    order_date,
    COUNT(DISTINCT id) AS order_count,
    SUM(final_amount) AS order_amount,
    COUNT(DISTINCT customer_id) AS customer_count,
    SUM(final_amount) / COUNT(DISTINCT id) AS avg_order_amount
FROM jxc_sales_order
WHERE order_date = CURDATE()
    AND deleted = 0
    AND order_status NOT IN ('draft', 'cancelled')
GROUP BY order_date;
```

#### 33.2.2 销售月报

| 指标 | 本月 | 环比上月 | 同比去年 |
|------|------|----------|----------|
| 订单数 | - | - | - |
| 订单金额 | - | - | - |
| 收款金额 | - | - | - |
| 新客户数 | - | - | - |
| 回头客占比 | - | - | - |

### 33.3 库存报表设计

#### 33.3.1 库存周转率

```
库存周转率 = 销售成本 / 平均库存金额
库存周转天数 = 365 / 库存周转率

计算公式：
- 平均库存金额 = (期初库存 + 期末库存) / 2
- 销售成本 = 销售出库商品成本
```

#### 33.3.2 库龄分析

| 库龄区间 | 定义 | 风险等级 |
|----------|------|----------|
| 0-30天 | 正常库存 | 低 |
| 31-60天 | 关注库存 | 中 |
| 61-90天 | 预警库存 | 高 |
| 90天以上 | 滞销库存 | 极高 |

### 33.4 生产报表设计

#### 33.4.1 生产效率报表

| 指标 | 计算公式 | 说明 |
|------|----------|------|
| 计划达成率 | 实际产量 / 计划产量 × 100% | 生产计划完成情况 |
| 设备利用率 | 实际运行时间 / 计划运行时间 × 100% | 设备使用效率 |
| 人均产量 | 总产量 / 生产人数 | 人均效率 |
| 良品率 | 合格数量 / 总数量 × 100% | 质量指标 |

#### 33.4.2 计件工资汇总

```sql
-- 月度计件工资汇总
SELECT 
    worker_id,
    worker_name,
    department_name,
    COUNT(*) AS record_count,
    SUM(quantity) AS total_quantity,
    SUM(amount) AS total_amount,
    SUM(CASE WHEN is_rework = 1 THEN amount ELSE 0 END) AS rework_amount
FROM prod_piecework_record
WHERE record_date BETWEEN '2024-03-01' AND '2024-03-31'
    AND deleted = 0
GROUP BY worker_id, worker_name, department_name
ORDER BY total_amount DESC;
```

### 33.5 前端页面设计

#### 33.5.1 销售报表页面

**文件路径：** `frontend/src/views/report/sales/index.vue`

```vue
<template>
  <div class="app-container">
    <!-- 筛选条件 -->
    <el-card shadow="never" style="margin-bottom: 16px;">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="日期范围">
          <el-date-picker v-model="queryParams.dateRange" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期"/>
        </el-form-item>
        <el-form-item label="客户">
          <el-select v-model="queryParams.customerId" placeholder="全部客户" clearable filterable>
            <el-option v-for="item in customerOptions" :key="item.id" :label="item.customerName" :value="item.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="销售员">
          <el-select v-model="queryParams.salesId" placeholder="全部销售员" clearable>
            <el-option v-for="item in salesOptions" :key="item.id" :label="item.realName" :value="item.id"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleExport">导出</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="16" style="margin-bottom: 16px;">
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="订单数量" :value="summary.orderCount"/>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="订单金额" :value="summary.orderAmount" :precision="2" prefix="¥"/>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="收款金额" :value="summary.receiptAmount" :precision="2" prefix="¥"/>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="客单价" :value="summary.avgOrderAmount" :precision="2" prefix="¥"/>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="16" style="margin-bottom: 16px;">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>销售趋势</template>
          <div ref="trendChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>销售排行</template>
          <div ref="rankChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 明细表格 -->
    <el-card shadow="never">
      <template #header>销售明细</template>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="orderDate" label="日期" width="100"/>
        <el-table-column prop="orderNo" label="订单号" width="140"/>
        <el-table-column prop="customerName" label="客户" width="150"/>
        <el-table-column prop="totalQuantity" label="数量" width="80"/>
        <el-table-column prop="finalAmount" label="金额" width="100"/>
        <el-table-column prop="salesName" label="销售员" width="80"/>
        <el-table-column prop="orderStatus" label="状态" width="80"/>
      </el-table>
    </el-card>
  </div>
</template>
```

---

## 34. 数据迁移详细方案

### 34.1 迁移概述

| 项目 | 说明 |
|------|------|
| 源系统 | duoduo_jxc（进销存） + coding（生产管理） |
| 目标系统 | 统一ERP系统 |
| 迁移范围 | 基础数据、业务数据、历史数据 |
| 预计时间 | 2-3天（不停机迁移） |

### 34.2 字段映射表

#### 34.2.1 商品数据映射

| 源表(duoduo_jxc) | 目标表 | 字段映射 | 转换规则 |
|------------------|--------|----------|----------|
| product.id | prod_product.id | 直接映射 | - |
| product.code | prod_product.product_code | 直接映射 | - |
| product.name | prod_product.product_name | 直接映射 | - |
| product.category_id | prod_product.category_id | 直接映射 | - |
| product.price | prod_product.sale_price | 直接映射 | - |
| product_sku.id | prod_style_sku.id | 直接映射 | - |
| product_sku.code | prod_style_sku.sku_code | 直接映射 | - |

#### 34.2.2 客户数据映射

| 源字段 | 目标字段 | 转换规则 |
|--------|----------|----------|
| customer.id | jxc_customer.id | 直接映射 |
| customer.name | jxc_customer.customer_name | 直接映射 |
| customer.phone | jxc_customer.phone | 直接映射 |
| customer.address | jxc_customer.address | 直接映射 |
| customer.sales_id | jxc_customer.sales_id | 需要映射用户ID |

#### 34.2.3 订单数据映射

| 源表 | 目标表 | 字段映射 | 转换规则 |
|------|--------|----------|----------|
| sales_order.id | jxc_sales_order.id | 直接映射 | - |
| sales_order.order_no | jxc_sales_order.order_no | 直接映射 | - |
| sales_order.status | jxc_sales_order.order_status | 状态转换 | 见状态映射表 |
| sales_order.total_price | jxc_sales_order.final_amount | 直接映射 | - |

### 34.3 状态映射表

#### 34.3.1 销售订单状态映射

| 源状态 | 源含义 | 目标状态 | 目标含义 |
|--------|--------|----------|----------|
| 0 | 草稿 | draft | 草稿 |
| 1 | 待审核 | pending | 待审核 |
| 2 | 已审核 | confirmed | 已确认 |
| 3 | 生产中 | producing | 生产中 |
| 4 | 已发货 | shipping | 发货中 |
| 5 | 已完成 | completed | 已完成 |
| -1 | 已取消 | cancelled | 已取消 |

### 34.4 数据清洗规则

| 数据类型 | 清洗规则 | 处理方式 |
|----------|----------|----------|
| 手机号 | 去除空格、横线 | 正则替换 |
| 金额 | 保留2位小数 | ROUND函数 |
| 日期 | 统一格式 | DATE_FORMAT |
| 空字符串 | 转为NULL | NULLIF函数 |
| 重复数据 | 按业务主键去重 | GROUP BY |

### 34.5 迁移脚本示例

```sql
-- =============================================
-- 商品数据迁移
-- =============================================

-- 1. 迁移商品主表
INSERT INTO erp_new.prod_product (
    id, product_code, product_name, product_type, category_id, 
    unit, sale_price, status, created_at, updated_at, deleted
)
SELECT 
    p.id,
    p.code AS product_code,
    p.name AS product_name,
    CASE p.type 
        WHEN 'fabric' THEN 'fabric'
        WHEN 'accessory' THEN 'accessory'
        ELSE 'finished'
    END AS product_type,
    p.category_id,
    COALESCE(p.unit, '件') AS unit,
    p.price AS sale_price,
    CASE p.status WHEN 1 THEN 1 ELSE 0 END AS status,
    p.created_at,
    p.updated_at,
    0 AS deleted
FROM duoduo_jxc.product p
WHERE p.deleted = 0;

-- 2. 迁移SKU表
INSERT INTO erp_new.prod_style_sku (
    id, style_id, style_code, color, size, sku_code, 
    barcode, sale_price, status, created_at, deleted
)
SELECT 
    s.id,
    s.product_id AS style_id,
    p.code AS style_code,
    s.color,
    s.size,
    s.code AS sku_code,
    s.barcode,
    s.price AS sale_price,
    CASE s.status WHEN 1 THEN 1 ELSE 0 END AS status,
    s.created_at,
    0 AS deleted
FROM duoduo_jxc.product_sku s
JOIN duoduo_jxc.product p ON s.product_id = p.id
WHERE s.deleted = 0;

-- =============================================
-- 客户数据迁移
-- =============================================

INSERT INTO erp_new.jxc_customer (
    id, customer_code, customer_name, customer_type,
    contact_name, phone, mobile, address,
    sales_id, sales_name, level, status,
    created_at, updated_at, deleted
)
SELECT 
    c.id,
    CONCAT('C', LPAD(c.id, 6, '0')) AS customer_code,
    c.name AS customer_name,
    CASE c.type 
        WHEN 'wholesale' THEN 'wholesale'
        WHEN 'retail' THEN 'retail'
        ELSE 'wholesale'
    END AS customer_type,
    c.contact AS contact_name,
    c.phone,
    c.mobile,
    c.address,
    c.sales_id,
    u.real_name AS sales_name,
    'normal' AS level,
    CASE c.status WHEN 1 THEN 1 ELSE 0 END AS status,
    c.created_at,
    c.updated_at,
    0 AS deleted
FROM duoduo_jxc.customer c
LEFT JOIN duoduo_jxc.sys_user u ON c.sales_id = u.id
WHERE c.deleted = 0;
```

### 34.6 迁移验证

| 验证项 | 验证方法 | 通过标准 |
|--------|----------|----------|
| 数据量 | COUNT对比 | 源表 = 目标表 |
| 金额汇总 | SUM对比 | 差异 < 0.01 |
| 外键完整性 | 关联检查 | 无孤立记录 |
| 状态正确性 | 抽样检查 | 状态映射正确 |
| 时间连续性 | MIN/MAX检查 | 无异常值 |

---

## 35. 接口文档规范

### 35.1 OpenAPI 3.0 规范

```yaml
openapi: 3.0.0
info:
  title: 服装行业全链路ERP系统API
  description: 统一ERP系统接口文档
  version: 1.0.0
  contact:
    name: 技术支持
    email: support@example.com

servers:
  - url: https://api.example.com/v1
    description: 生产环境
  - url: https://api-test.example.com/v1
    description: 测试环境

tags:
  - name: auth
    description: 认证授权
  - name: sales
    description: 销售管理
  - name: purchase
    description: 采购管理
  - name: production
    description: 生产管理
  - name: inventory
    description: 库存管理
  - name: finance
    description: 财务管理

paths:
  /auth/login:
    post:
      tags:
        - auth
      summary: 用户登录
      description: 通过用户名密码登录系统
      operationId: login
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - username
                - password
              properties:
                username:
                  type: string
                  description: 用户名
                  example: admin
                password:
                  type: string
                  description: 密码
                  example: "******"
      responses:
        '200':
          description: 登录成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/LoginResponse'
        '400':
          description: 参数错误
        '401':
          description: 用户名或密码错误

components:
  schemas:
    LoginResponse:
      type: object
      properties:
        code:
          type: integer
          example: 0
        message:
          type: string
          example: success
        data:
          type: object
          properties:
            token:
              type: string
              description: JWT Token
            refreshToken:
              type: string
              description: 刷新Token
            expiresIn:
              type: integer
              description: 过期时间(秒)
              example: 7200
            user:
              $ref: '#/components/schemas/UserInfo'
    
    UserInfo:
      type: object
      properties:
        id:
          type: integer
          format: int64
          example: 1
        username:
          type: string
          example: admin
        realName:
          type: string
          example: 管理员
        roles:
          type: array
          items:
            type: string
          example: ["admin"]

    PageResponse:
      type: object
      properties:
        code:
          type: integer
        message:
          type: string
        data:
          type: object
          properties:
            list:
              type: array
              items:
                type: object
            total:
              type: integer
            pageNum:
              type: integer
            pageSize:
              type: integer

    ErrorResponse:
      type: object
      properties:
        code:
          type: integer
          example: 10001
        message:
          type: string
          example: 参数不能为空
        traceId:
          type: string
          example: abc123def456
        timestamp:
          type: integer
          format: int64

  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT

security:
  - BearerAuth: []
```

### 35.2 接口版本管理

| 版本规则 | 说明 |
|----------|------|
| URL版本 | /api/v1/resource, /api/v2/resource |
| 向后兼容 | 新增字段不影响旧版本客户端 |
| 废弃流程 | 标记deprecated → 保留3个月 → 下线 |
| 版本日志 | CHANGELOG.md 记录变更 |

### 35.3 接口响应规范

**统一响应格式：**

```json
{
  "code": 0,
  "message": "success",
  "data": {},
  "timestamp": 1711612800000,
  "traceId": "abc123def456"
}
```

**分页响应格式：**

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [],
    "total": 100,
    "pageNum": 1,
    "pageSize": 20,
    "pages": 5
  },
  "timestamp": 1711612800000,
  "traceId": "abc123def456"
}
```

---

## 36. 前端组件库设计

### 36.1 组件分类

| 类别 | 组件名 | 用途 |
|------|--------|------|
| 基础组件 | Button, Icon, Link | 基础交互 |
| 表单组件 | Input, Select, DatePicker, Upload | 数据录入 |
| 数据展示 | Table, Card, Descriptions, Statistic | 数据展示 |
| 导航组件 | Menu, Tabs, Breadcrumb, Pagination | 页面导航 |
| 反馈组件 | Message, Modal, Drawer, Loading | 操作反馈 |
| 业务组件 | ProductSelector, CustomerSelector, ProcessPicker | 业务选择 |

### 36.2 业务组件设计

#### 36.2.1 商品选择器

**文件路径：** `frontend/src/components/ProductSelector/index.vue`

**Props定义：**

```typescript
interface Props {
  modelValue: number | number[]  // 选中的商品ID
  multiple: boolean              // 是否多选
  productType: string            // 商品类型过滤
  placeholder: string            // 占位文本
  disabled: boolean              // 是否禁用
}
```

**使用示例：**

```vue
<ProductSelector 
  v-model="form.productId" 
  :product-type="'finished'"
  placeholder="请选择商品"
/>
```

#### 36.2.2 客户选择器

**文件路径：** `frontend/src/components/CustomerSelector/index.vue`

**Props定义：**

```typescript
interface Props {
  modelValue: number
  placeholder: string
  filterable: boolean    // 是否可搜索
  showCredit: boolean    // 是否显示信用额度
}
```

#### 36.2.3 工序选择器

**文件路径：** `frontend/src/components/ProcessPicker/index.vue`

**Props定义：**

```typescript
interface Props {
  modelValue: Process | null
  styleId: number        // 款号ID，用于加载配置
  showPrice: boolean     // 是否显示单价
}
```

### 36.3 组件规范

#### 36.3.1 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 组件名 | PascalCase | ProductSelector |
| 组件文件 | PascalCase | ProductSelector.vue |
| Props | camelCase | showPrice |
| Events | kebab-case | @change-value |
| Slots | kebab-case | #header |

#### 36.3.2 组件目录结构

```
frontend/src/components/
├── ProductSelector/
│   ├── index.vue          # 主组件
│   ├── ProductList.vue    # 商品列表子组件
│   ├── types.ts           # 类型定义
│   └── hooks.ts           # 组合式函数
├── CustomerSelector/
│   ├── index.vue
│   └── types.ts
├── ProcessPicker/
│   ├── index.vue
│   └── types.ts
└── common/
    ├── PageHeader.vue     # 页面头部
    ├── SearchForm.vue     # 搜索表单
    └── ActionBar.vue      # 操作栏
```

### 36.4 公共Hooks

#### 36.4.1 useTable

```typescript
// hooks/useTable.ts
export function useTable<T>(api: Function, defaultParams = {}) {
  const loading = ref(false)
  const tableData = ref<T[]>([])
  const total = ref(0)
  const queryParams = ref({ page: 1, size: 20, ...defaultParams })

  const loadData = async () => {
    loading.value = true
    try {
      const res = await api(queryParams.value)
      if (res.success) {
        tableData.value = res.data.list
        total.value = res.data.total
      }
    } finally {
      loading.value = false
    }
  }

  const handleQuery = () => {
    queryParams.value.page = 1
    loadData()
  }

  const handlePageChange = (page: number) => {
    queryParams.value.page = page
    loadData()
  }

  return {
    loading,
    tableData,
    total,
    queryParams,
    loadData,
    handleQuery,
    handlePageChange
  }
}
```

#### 36.4.2 useForm

```typescript
// hooks/useForm.ts
export function useForm<T>(defaultForm: T) {
  const form = ref<T>({ ...defaultForm } as T)
  const formRef = ref()
  const rules = ref({})
  const submitting = ref(false)

  const validate = async () => {
    return formRef.value?.validate()
  }

  const resetForm = () => {
    form.value = { ...defaultForm } as T
    formRef.value?.resetFields()
  }

  const setFormData = (data: Partial<T>) => {
    form.value = { ...form.value, ...data }
  }

  return {
    form,
    formRef,
    rules,
    submitting,
    validate,
    resetForm,
    setFormData
  }
}
```

---

## 37. 打印模板详细设计

### 37.1 打印模板类型

| 模板类型 | 编码 | 用途 | 纸张规格 |
|----------|------|------|----------|
| 扎包标签 | bundle_label | 扎包标识 | 50mm×30mm |
| 销售订单 | sales_order | 订单打印 | A4 |
| 采购订单 | purchase_order | 订单打印 | A4 |
| 发货单 | delivery_order | 发货凭证 | A4 |
| 入库单 | inbound_order | 入库凭证 | A4 |
| 收款单 | receipt | 收款凭证 | A4 |
| 付款单 | payment | 付款凭证 | A4 |
| 工资条 | wage_slip | 工资发放 | A5 |
| 生产工单 | production_order | 生产指令 | A4 |

### 37.2 模板变量定义

#### 37.2.1 扎包标签变量

| 变量名 | 类型 | 说明 | 示例值 |
|--------|------|------|--------|
| ${bundleNo} | String | 扎包号 | ZB20240328001 |
| ${styleCode} | String | 款号 | ST2024001 |
| ${styleName} | String | 品名 | 短袖T恤 |
| ${color} | String | 颜色 | 白色 |
| ${size} | String | 尺码 | M |
| ${quantity} | Number | 数量 | 50 |
| ${qrCode} | Image | 二维码 | Base64 |
| ${orderNo} | String | 生产单号 | PO20240328001 |
| ${createTime} | Date | 创建时间 | 2024-03-28 |

#### 37.2.2 销售订单变量

| 变量名 | 类型 | 说明 |
|--------|------|------|
| ${orderNo} | String | 订单号 |
| ${orderDate} | Date | 订单日期 |
| ${customerName} | String | 客户名称 |
| ${customerPhone} | String | 客户电话 |
| ${customerAddress} | String | 收货地址 |
| ${salesName} | String | 销售员 |
| ${details} | Array | 商品明细列表 |
| ${details[].styleCode} | String | 款号 |
| ${details[].styleName} | String | 品名 |
| ${details[].color} | String | 颜色 |
| ${details[].size} | String | 尺码 |
| ${details[].quantity} | Number | 数量 |
| ${details[].unitPrice} | Number | 单价 |
| ${details[].amount} | Number | 金额 |
| ${totalQuantity} | Number | 总数量 |
| ${totalAmount} | Number | 总金额 |
| ${discountAmount} | Number | 折扣金额 |
| ${finalAmount} | Number | 价税合计 |
| ${remark} | String | 备注 |

### 37.3 模板存储设计

```sql
CREATE TABLE sys_print_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_code VARCHAR(50) NOT NULL COMMENT '模板编码',
    template_name VARCHAR(100) NOT NULL COMMENT '模板名称',
    template_type VARCHAR(30) NOT NULL COMMENT '模板类型',
    paper_size VARCHAR(20) DEFAULT 'A4' COMMENT '纸张规格',
    paper_width INT COMMENT '纸张宽度(mm)',
    paper_height INT COMMENT '纸张高度(mm)',
    orientation VARCHAR(20) DEFAULT 'portrait' COMMENT '方向: portrait-纵向 landscape-横向',
    template_content TEXT COMMENT '模板内容(HTML/TSPL)',
    variables TEXT COMMENT '变量定义(JSON)',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认模板',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    UNIQUE KEY uk_template_code (template_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打印模板表';
```

### 37.4 扎包标签模板示例

**TSPL格式（热敏打印机）：**

```
SIZE 50 mm, 30 mm
GAP 2 mm, 0 mm
DIRECTION 1
CLS
TEXT 10,5,"2",0,1,1,"${bundleNo}"
TEXT 10,30,"1",0,1,1,"款号:${styleCode}"
TEXT 10,50,"1",0,1,1,"${color}/${size}"
TEXT 10,70,"1",0,1,1,"数量:${quantity}"
QRCODE 120,30,H,3,A,0,"${bundleNo}"
BARCODE 10,85,"128",30,1,0,2,2,"${bundleNo}"
PRINT 1
```

### 37.5 销售订单模板示例

**HTML格式：**

```html
<!DOCTYPE html>
<html>
<head>
  <style>
    body { font-family: SimSun; font-size: 12px; }
    .header { text-align: center; font-size: 18px; font-weight: bold; }
    .info-row { margin: 5px 0; }
    .info-label { display: inline-block; width: 80px; }
    table { width: 100%; border-collapse: collapse; margin-top: 10px; }
    th, td { border: 1px solid #000; padding: 5px; text-align: center; }
    .amount-row { text-align: right; margin: 5px 0; }
  </style>
</head>
<body>
  <div class="header">销售订单</div>
  <div class="info-row"><span class="info-label">订单号：</span>${orderNo}</div>
  <div class="info-row"><span class="info-label">订单日期：</span>${orderDate}</div>
  <div class="info-row"><span class="info-label">客户：</span>${customerName}</div>
  <div class="info-row"><span class="info-label">电话：</span>${customerPhone}</div>
  <div class="info-row"><span class="info-label">地址：</span>${customerAddress}</div>
  
  <table>
    <tr>
      <th>款号</th>
      <th>品名</th>
      <th>颜色</th>
      <th>尺码</th>
      <th>数量</th>
      <th>单价</th>
      <th>金额</th>
    </tr>
    <!--{{#each details}}-->
    <tr>
      <td>${styleCode}</td>
      <td>${styleName}</td>
      <td>${color}</td>
      <td>${size}</td>
      <td>${quantity}</td>
      <td>${unitPrice}</td>
      <td>${amount}</td>
    </tr>
    <!--{{/each}}-->
  </table>
  
  <div class="amount-row">总数量：${totalQuantity}</div>
  <div class="amount-row">总金额：￥${totalAmount}</div>
  <div class="amount-row">折扣金额：￥${discountAmount}</div>
  <div class="amount-row" style="font-size: 14px; font-weight: bold;">价税合计：￥${finalAmount}</div>
  
  <div class="info-row" style="margin-top: 20px;">备注：${remark}</div>
</body>
</html>
```

### 37.6 打印服务接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 模板列表 | GET | /api/print/template/list | 获取模板列表 |
| 模板详情 | GET | /api/print/template/{id} | 获取模板详情 |
| 保存模板 | POST | /api/print/template | 保存模板 |
| 预览打印 | POST | /api/print/preview | 预览打印内容 |
| 执行打印 | POST | /api/print/execute | 执行打印 |
| 获取打印机列表 | GET | /api/print/printer/list | 获取可用打印机 |

---

## 38. 数据导入导出设计

### 38.1 导入模板定义

#### 38.1.1 商品导入模板

| 列名 | 字段名 | 必填 | 类型 | 说明 |
|------|--------|------|------|------|
| 商品编码 | productCode | 是 | 文本 | 唯一标识 |
| 商品名称 | productName | 是 | 文本 | - |
| 商品类型 | productType | 是 | 文本 | 面料/辅料/成品 |
| 分类名称 | categoryName | 否 | 文本 | - |
| 单位 | unit | 否 | 文本 | 默认：件 |
| 成本价 | costPrice | 否 | 数值 | - |
| 销售价 | salePrice | 否 | 数值 | - |
| 条码 | barcode | 否 | 文本 | - |
| 状态 | status | 否 | 文本 | 启用/禁用 |

#### 38.1.2 客户导入模板

| 列名 | 字段名 | 必填 | 类型 | 说明 |
|------|--------|------|------|------|
| 客户编码 | customerCode | 是 | 文本 | - |
| 客户名称 | customerName | 是 | 文本 | - |
| 客户类型 | customerType | 否 | 文本 | 批发/零售 |
| 联系人 | contactName | 否 | 文本 | - |
| 电话 | phone | 否 | 文本 | - |
| 地址 | address | 否 | 文本 | - |
| 销售员 | salesName | 否 | 文本 | 按姓名匹配 |
| 状态 | status | 否 | 文本 | 启用/禁用 |

### 38.2 导入校验规则

| 校验类型 | 规则 | 错误处理 |
|----------|------|----------|
| 必填校验 | 字段不能为空 | 记录错误行，跳过 |
| 格式校验 | 手机号/邮箱格式 | 记录错误行，跳过 |
| 唯一性校验 | 编码不能重复 | 记录错误行，跳过 |
| 引用校验 | 外键引用存在 | 记录错误行，跳过 |
| 范围校验 | 数值在允许范围内 | 自动修正或报错 |
| 枚举校验 | 值在枚举列表内 | 报错提示正确值 |

### 38.3 导入流程设计

```
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│ 上传文件 │───>│ 解析数据 │───>│ 数据校验 │───>│ 入库保存 │
│          │    │          │    │          │    │          │
└──────────┘    └──────────┘    └──────────┘    └──────────┘
                                     │
                                     │ 有错误
                                     ▼
                               ┌──────────┐
                               │ 生成错误 │
                               │ 报告下载 │
                               └──────────┘
```

### 38.4 导出功能设计

#### 38.4.1 导出格式

| 格式 | 说明 | 适用场景 |
|------|------|----------|
| Excel (.xlsx) | 主要导出格式 | 数据导出、报表 |
| CSV (.csv) | 纯文本格式 | 数据迁移 |
| PDF (.pdf) | 只读格式 | 报表打印 |

#### 38.4.2 大数据量导出

| 数据量 | 导出方式 | 说明 |
|--------|----------|------|
| < 1万条 | 直接导出 | 内存生成，直接下载 |
| 1-10万条 | 分页导出 | 分批查询，流式写入 |
| > 10万条 | 异步导出 | 后台任务，完成后通知下载 |

### 38.5 导入导出接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 下载导入模板 | GET | /api/import/template/{type} | 下载Excel模板 |
| 导入数据 | POST | /api/import/{type} | 上传Excel导入 |
| 下载错误报告 | GET | /api/import/error/{taskId} | 下载导入错误报告 |
| 导出数据 | POST | /api/export/{type} | 导出数据 |
| 查询导出任务 | GET | /api/export/task/{taskId} | 查询异步导出任务状态 |
| 下载导出文件 | GET | /api/export/download/{taskId} | 下载导出文件 |

---

## 39. 测试用例设计

### 39.1 测试分类

| 测试类型 | 覆盖范围 | 执行频率 |
|----------|----------|----------|
| 单元测试 | Service层方法 | 每次提交 |
| 集成测试 | API接口 | 每日构建 |
| E2E测试 | 关键业务流程 | 每周/发版前 |
| 性能测试 | 核心接口 | 每月 |

### 39.2 单元测试示例

```java
@SpringBootTest
class SalesOrderServiceTest {

    @Autowired
    private SalesOrderService salesOrderService;
    
    @Autowired
    private SalesOrderMapper salesOrderMapper;

    @Test
    @DisplayName("创建销售订单-正常流程")
    void testCreateOrder_success() {
        // Given
        SalesOrderDTO dto = new SalesOrderDTO();
        dto.setCustomerId(1L);
        dto.setOrderDate(LocalDate.now());
        dto.setDetails(List.of(
            createOrderDetail(1L, 10, 100.00)
        ));
        
        // When
        Long orderId = salesOrderService.createOrder(dto);
        
        // Then
        assertNotNull(orderId);
        SalesOrder order = salesOrderMapper.selectById(orderId);
        assertEquals("draft", order.getOrderStatus());
        assertEquals(new BigDecimal("1000.00"), order.getTotalAmount());
    }

    @Test
    @DisplayName("创建销售订单-客户不存在")
    void testCreateOrder_customerNotFound() {
        // Given
        SalesOrderDTO dto = new SalesOrderDTO();
        dto.setCustomerId(99999L);
        
        // When & Then
        assertThrows(BusinessException.class, () -> {
            salesOrderService.createOrder(dto);
        });
    }

    @Test
    @DisplayName("提交订单-状态校验")
    void testSubmitOrder_invalidStatus() {
        // Given
        Long orderId = createTestOrder();
        salesOrderService.submitOrder(orderId); // 先提交
        
        // When & Then
        assertThrows(BusinessException.class, () -> {
            salesOrderService.submitOrder(orderId); // 再次提交应报错
        });
    }
}
```

### 39.3 集成测试示例

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SalesOrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    
    @Autowired
    private TestTokenProvider tokenProvider;

    @Test
    @DisplayName("获取订单列表-正常")
    void testGetOrderList() {
        webTestClient.get()
            .uri("/api/sales/order/list?page=1&size=10")
            .header("Authorization", "Bearer " + tokenProvider.getToken())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.code").isEqualTo(0)
            .jsonPath("$.data.list").isArray()
            .jsonPath("$.data.total").isNumber();
    }

    @Test
    @DisplayName("创建订单-无权限")
    void testCreateOrder_unauthorized() {
        SalesOrderDTO dto = new SalesOrderDTO();
        
        webTestClient.post()
            .uri("/api/sales/order")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isUnauthorized();
    }
}
```

### 39.4 E2E测试用例

| 用例编号 | 测试场景 | 前置条件 | 测试步骤 | 预期结果 |
|----------|----------|----------|----------|----------|
| E2E-001 | 销售订单完整流程 | 已登录、有客户数据 | 1.创建订单→2.提交→3.审核→4.发货→5.收货 | 订单状态正确流转 |
| E2E-002 | 采购订单完整流程 | 已登录、有供应商数据 | 1.创建订单→2.提交→3.审核→4.入库→5.付款 | 库存增加，应付减少 |
| E2E-003 | 计件工资结算 | 有计件数据 | 1.查看工资汇总→2.生成结算单→3.确认→4.发放 | 工资单状态正确 |
| E2E-004 | 库存盘点过账 | 有库存数据 | 1.创建盘点单→2.盘点录入→3.完成→4.过账 | 库存调整正确 |

### 39.5 测试覆盖率目标

| 模块 | 行覆盖率 | 分支覆盖率 |
|------|----------|------------|
| 核心业务(Service) | ≥ 80% | ≥ 70% |
| 工具类(Util) | ≥ 90% | ≥ 80% |
| Controller | ≥ 60% | ≥ 50% |
| Entity/DTO | 不强制 | 不强制 |

---

## 40. 性能优化方案

### 40.1 数据库优化

#### 40.1.1 索引优化

| 优化项 | 说明 |
|--------|------|
| 联合索引 | 高频组合查询字段建立联合索引 |
| 覆盖索引 | 查询字段全部包含在索引中 |
| 索引选择性 | 选择区分度高的字段建索引 |
| 避免索引失效 | 避免对索引字段做函数运算 |

**示例：**

```sql
-- 联合索引
CREATE INDEX idx_order_customer_status_date 
ON jxc_sales_order(customer_id, order_status, order_date);

-- 覆盖索引
CREATE INDEX idx_bundle_list 
ON prod_bundle(bundle_no, bundle_status, created_at) 
INCLUDE (style_code, color, size, quantity);
```

#### 40.1.2 SQL优化

| 优化项 | 说明 |
|--------|------|
| 避免SELECT * | 只查询需要的字段 |
| 分页优化 | 使用游标分页代替LIMIT OFFSET |
| 批量操作 | 使用批量INSERT/UPDATE |
| 连接优化 | 小表驱动大表，避免子查询 |

### 40.2 缓存优化

#### 40.2.1 缓存策略

| 数据类型 | 缓存策略 | 过期时间 |
|----------|----------|----------|
| 系统配置 | 本地缓存+Redis | 30分钟 |
| 用户权限 | Redis | 会话有效期内 |
| 商品信息 | Redis | 10分钟 |
| 库存数量 | Redis | 1分钟 |
| 热点数据 | 本地缓存 | 5分钟 |

#### 40.2.2 缓存架构

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   本地缓存   │────>│   Redis     │────>│   数据库    │
│  (Caffeine) │     │  (分布式)    │     │   (MySQL)   │
└─────────────┘     └─────────────┘     └─────────────┘
       │                   │                   │
       └───────────────────┴───────────────────┘
                    多级缓存架构
```

### 40.3 接口性能优化

#### 40.3.1 接口性能指标

| 接口类型 | 响应时间要求 | 并发要求 |
|----------|--------------|----------|
| 查询类 | < 200ms | 100 QPS |
| 列表类 | < 500ms | 50 QPS |
| 写入类 | < 300ms | 30 QPS |
| 报表类 | < 3s | 10 QPS |
| 导入导出 | 异步处理 | 5 QPS |

#### 40.3.2 优化手段

| 优化项 | 适用场景 | 说明 |
|--------|----------|------|
| 异步处理 | 耗时操作 | 使用消息队列异步处理 |
| 批量接口 | 批量操作 | 合并请求减少网络开销 |
| 数据预加载 | 热点数据 | 提前加载到缓存 |
| 分页查询 | 大数据量 | 避免一次查询过多数据 |
| 字段裁剪 | 列表接口 | 只返回必要字段 |

### 40.4 前端性能优化

| 优化项 | 说明 |
|--------|------|
| 路由懒加载 | 按需加载页面组件 |
| 组件懒加载 | 大组件延迟加载 |
| 图片懒加载 | 图片进入视口再加载 |
| 虚拟滚动 | 长列表使用虚拟滚动 |
| 防抖节流 | 频繁操作添加防抖/节流 |
| Gzip压缩 | 开启静态资源压缩 |

### 40.5 性能监控

#### 40.5.1 监控指标

| 指标类型 | 监控项 | 告警阈值 |
|----------|--------|----------|
| 应用指标 | 接口响应时间P99 | > 1秒 |
| 应用指标 | 接口错误率 | > 1% |
| 应用指标 | QPS | 根据容量规划 |
| 数据库指标 | 慢查询数量 | > 10/分钟 |
| 数据库指标 | 连接池使用率 | > 80% |
| 缓存指标 | 命中率 | < 90% |
| 系统指标 | CPU使用率 | > 80% |
| 系统指标 | 内存使用率 | > 85% |

#### 40.5.2 监控工具

| 工具 | 用途 |
|------|------|
| Prometheus | 指标采集 |
| Grafana | 可视化展示 |
| AlertManager | 告警通知 |
| SkyWalking | 链路追踪 |
| ELK | 日志分析 |

---

## 四十一、成本核算详细设计

> 详细设计见 [26-成本核算详细设计.md](./split/26-成本核算详细设计.md)

### 41.1 核心内容概览

| 内容 | 说明 |
|------|------|
| 材料成本归集 | BOM展开→领料汇总→成本归集 |
| 人工成本归集 | 计件汇总→工时分摊 |
| 制造费用分摊 | 产量法/工时法/机器时法 |
| 在制品核算 | 约当产量法 |
| 废品损失处理 | 正常废品/异常废品的成本结转 |

### 41.2 成本计算公式

```
总成本 = 材料成本 + 人工成本 + 制造费用 + 废品损失

材料成本 = Σ(领料数量 × 单价 × (1 + 损耗率))
人工成本 = Σ(计件数量 × 工序单价)
制造费用 = 分摊基数 × 分摊率
```

---

## 四十二、工资管理模块

> 详细设计见 [27-工资管理模块.md](./split/27-工资管理模块.md)

### 42.1 核心流程

```
计件记录确认 → 日/周/月汇总 → 生成工资单 → 财务审核 → 工资发放
```

### 42.2 工资单构成

| 项目 | 说明 |
|------|------|
| 基本工资 | 固定底薪 |
| 计件工资 | 计件汇总金额 |
| 加班工资 | 加班时段额外补贴 |
| 奖金/补贴 | 各类奖金补贴 |
| 扣款 | 返工扣款、其他扣款 |
| 实发金额 | 应发合计 - 扣款 |

---

## 四十三、消息实时推送设计

### 43.1 推送场景

| 场景 | 推送对象 | 推送方式 |
|------|---------|---------|
| 计件成功 | 当前工人 | 小程序Toast |
| 库存预警 | 仓库管理员 | 小程序订阅消息 |
| 生产异常 | 班组长/车间主任 | 小程序订阅消息 |
| 审批待办 | 审批人 | 小程序订阅消息 |
| 工资发放 | 全体员工 | 小程序订阅消息 |

### 43.2 技术方案

- 微信小程序订阅消息
- WebSocket实时连接（可选）
- 离线消息队列（Redis）

### 43.3 消息模板

```json
{
  "template_id": "xxx",
  "page": "pages/index/index",
  "data": {
    "thing1": {"value": "计件成功"},
    "number2": {"value": "20件"},
    "time3": {"value": "2024-01-01 10:00:00"}
  }
}
```

---

## 附录：服装行业特性要点

### A. 季节性管理

| 季节 | 上货时间 | 开发时间 |
|------|---------|---------|
| 春季(3-5月) | 1-2月 | 上年9月 |
| 夏季(6-8月) | 4-5月 | 上年12月 |
| 秋季(9-11月) | 7-8月 | 当年3月 |
| 冬季(12-2月) | 10-11月 | 当年6月 |

### B. 工序分类

1. **裁剪准备**: 验布→铺布→划样→裁剪→验片→分包
2. **缝制前道**: 粘衬→打省→收褶→拼合
3. **缝制主道**: 前幅→后幅→袖子→领子→组装
4. **缝制后道**: 锁眼→钉扣→套结→装饰线
5. **整理工序**: 剪线头→整烫→质检→包装
6. **特殊工序**: 刺绣→印花→洗水→成衣染色

### C. 质检分类

- **IQC (进料检验)**: 面料、辅料入库检验
- **FAI (首件检验)**: 生产前首件确认
- **IPQC (过程检验)**: 生产过程巡检
- **FQC (最终检验)**: 成品终检
- **OQC (出货检验)**: 出货前抽检

### D. 缺陷分类

| 大类 | 细分缺陷 |
|------|---------|
| 尺寸问题 | 尺寸偏大/偏小/不对称/公差超标 |
| 缝制问题 | 跳线/断线/浮线/线头/针洞/错位/漏缝 |
| 外观问题 | 污渍/色差/色花/起球/勾丝/破洞/抽纱 |
| 工艺问题 | 粘衬起泡/烫焦/烫黄/褶皱不平 |
| 辅料问题 | 拉链卡顿/扣子脱落/标牌错误 |
| 包装问题 | 折叠不规范/包装破损/标签错误 |

---

> 文档版本: v2.2  
> 最后更新: 2024-03-28
> 
> 更新记录:
> - v2.2: 新增打印模板详细设计、数据导入导出设计、测试用例设计、性能优化方案章节
> - v2.1: 新增库存管理详细设计、报表统计详细设计、数据迁移详细方案、接口文档规范、前端组件库设计章节，补充库存DDL
> - v2.0: 新增销售管理、采购管理、财务收付款模块设计，补充销售/采购/财务DDL，新增错误码定义
> - v1.9: 新增数据库DDL设计、系统配置设计、消息通知设计、日志与审计设计、部署架构设计、安全设计章节
> - v1.8: 新增小程序页面设计章节（uni-app），包含7个角色、39个页面、动态TabBar配置、离线支持、蓝牙打印
> - v1.7: 新增前端页面交互设计章节，包含24个页面/组件的完整设计
> - v1.6: 新增API接口详细设计，包含43个接口的完整定义
> - v1.5: 新增权限设计章节，包含角色定义、菜单权限、数据权限
> - v1.4: 新增数据字典与枚举章节，包含13大类约80个枚举定义
> - v1.3.1: 修正防重键设计，使用 request_id(UUID) 替代业务字段组合
> - v1.3: 完善计件功能设计，新增单价固化、结算闭环、多人计件、返工扣款、SKU溢价、并发控制
> - v1.2: 新增灵活流程管理设计，支持不同工厂/品类/季节使用不同流程
> - v1.1: 新增销售→生产数据打通、库存一体化设计、财务成本联动、数据迁移策略章节
> - v1.0: 初始版本
