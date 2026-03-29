# 数据库DDL设计

> **模块标识**: DATABASE  
> **所属系统**: ERP-数据层  
> **AI处理提示**: 本文档需配合 [09-小程序设计.md](09-小程序设计.md)、[01-系统架构设计.md](01-系统架构设计.md) 使用

---

## 模块关联

本文档与以下模块存在数据/流程关联：

| 关联类型 | 目标文档 | 关联内容 |
|----------|----------|----------|
| 数据使用 | 09-小程序设计.md | 用户表、角色表、扎包表、计件记录表等小程序数据源 |
| 数据支撑 | 03-生产管理设计.md | 生产订单表、扎包表、工序配置表等 |
| 数据支撑 | 04-质量管理设计.md | 质检记录表、返工记录表等 |
| 数据支撑 | 05-计件工资设计.md | 计件记录表、工资结算表等 |
| 数据支撑 | 06-销售管理设计.md | 客户表、销售订单表、发货单表等 |
| 数据支撑 | 07-采购管理设计.md | 供应商表、采购订单表、入库单表等 |
| 数据支撑 | 08-库存管理设计.md | 库存表、盘点单表、调拨单表等 |

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
| 时间字段 | `_time` 后缀 | `create_time`, `update_time` |
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
    created_by BIGINT,
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
    password_update_time DATETIME COMMENT '密码修改时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
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
    sale_price DECIMAL(12,4) COMMENT '销售价',
    barcode VARCHAR(50) COMMENT '条码',
    images TEXT COMMENT '图片(JSON)',
    description TEXT COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
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
    created_by BIGINT,
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
    sale_price DECIMAL(12,4) COMMENT '销售价',
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
    created_by BIGINT,
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
    created_by BIGINT,
    KEY idx_process_id (process_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工序单价历史表';

CREATE TABLE prod_process_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    config_code VARCHAR(50) NOT NULL COMMENT '配置编码',
    style_id BIGINT NOT NULL COMMENT '款号ID',
    style_code VARCHAR(50) NOT NULL COMMENT '款号',
    factory_id BIGINT NOT NULL COMMENT '工厂ID',
    factory_name VARCHAR(100) COMMENT '工厂名称',
    total_price DECIMAL(12,4) DEFAULT 0 COMMENT '工序总价',
    process_count INT DEFAULT 0 COMMENT '工序数量',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-草稿 1-已发布',
    version INT DEFAULT 1 COMMENT '版本号',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
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
    created_by BIGINT,
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
    created_by BIGINT,
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
    created_by BIGINT,
    KEY idx_bundle_id (bundle_id),
    KEY idx_create_time (create_time)
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
    created_by BIGINT,
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
    total_amount DECIMAL(14,4) DEFAULT 0 COMMENT '计件总额',
    deduction_amount DECIMAL(12,4) DEFAULT 0 COMMENT '扣款总额',
    bonus_amount DECIMAL(12,4) DEFAULT 0 COMMENT '奖金总额',
    final_amount DECIMAL(14,4) DEFAULT 0 COMMENT '应发总额',
    settlement_status VARCHAR(20) DEFAULT 'draft' COMMENT '结算状态: draft/confirmed/paid',
    confirm_time DATETIME COMMENT '确认时间',
    confirm_by BIGINT COMMENT '确认人',
    pay_time DATETIME COMMENT '发放时间',
    pay_by BIGINT COMMENT '发放人',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
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
    deduction_amount DECIMAL(10,4) DEFAULT 0 COMMENT '扣款金额',
    bonus_amount DECIMAL(10,4) DEFAULT 0 COMMENT '奖金',
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
    original_amount DECIMAL(10,4) COMMENT '原计件金额',
    deduction_ratio DECIMAL(5,2) COMMENT '扣款比例(%)',
    deduction_amount DECIMAL(10,4) COMMENT '扣款金额',
    rework_worker_id BIGINT COMMENT '返工操作员ID',
    rework_worker_name VARCHAR(50) COMMENT '返工操作员姓名',
    rework_amount DECIMAL(10,4) COMMENT '返工计件金额',
    rework_status VARCHAR(20) DEFAULT 'pending' COMMENT '返工状态: pending/in_progress/completed',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '完成时间',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
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
| prod_bundle_flow | bundle_id + create_time | 按月分表 |
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
    used_credit DECIMAL(12,4) DEFAULT 0 COMMENT '已用额度',
    payment_days INT DEFAULT 30 COMMENT '账期(天)',
    level VARCHAR(20) DEFAULT 'normal' COMMENT '客户等级: vip/normal/potential',
    source VARCHAR(50) COMMENT '客户来源',
    remark TEXT COMMENT '备注',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
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
    total_amount DECIMAL(14,4) DEFAULT 0 COMMENT '总金额',
    discount_rate DECIMAL(5,2) DEFAULT 0 COMMENT '折扣率(%)',
    discount_amount DECIMAL(12,4) DEFAULT 0 COMMENT '折扣金额',
    final_amount DECIMAL(14,4) DEFAULT 0 COMMENT '折后金额',
    quotation_status VARCHAR(20) DEFAULT 'draft' COMMENT '状态: draft-草稿 sent-已发送 accepted-已接受 rejected-已拒绝 expired-已过期',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
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
    unit_price DECIMAL(12,4) NOT NULL COMMENT '单价',
    discount_rate DECIMAL(5,2) DEFAULT 0 COMMENT '折扣率(%)',
    amount DECIMAL(14,4) NOT NULL COMMENT '金额',
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
    total_amount DECIMAL(14,4) DEFAULT 0 COMMENT '总金额',
    discount_rate DECIMAL(5,2) DEFAULT 0 COMMENT '折扣率(%)',
    discount_amount DECIMAL(12,4) DEFAULT 0 COMMENT '折扣金额',
    tax_rate DECIMAL(5,2) DEFAULT 0 COMMENT '税率(%)',
    tax_amount DECIMAL(12,4) DEFAULT 0 COMMENT '税额',
    final_amount DECIMAL(14,4) DEFAULT 0 COMMENT '价税合计',
    paid_amount DECIMAL(14,4) DEFAULT 0 COMMENT '已收金额',
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
    created_by BIGINT,
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
    unit_price DECIMAL(12,4) NOT NULL COMMENT '单价',
    discount_rate DECIMAL(5,2) DEFAULT 0 COMMENT '折扣率(%)',
    amount DECIMAL(14,4) NOT NULL COMMENT '金额',
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
    total_amount DECIMAL(14,4) DEFAULT 0 COMMENT '总金额',
    delivery_status VARCHAR(20) DEFAULT 'pending' COMMENT '发货状态: pending-待发货 shipped-已发货 received-已签收 returned-已退货',
    warehouse_id BIGINT COMMENT '仓库ID',
    warehouse_name VARCHAR(100) COMMENT '仓库名称',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
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
    unit_price DECIMAL(12,4) COMMENT '单价',
    amount DECIMAL(14,4) COMMENT '金额',
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
    total_amount DECIMAL(14,4) DEFAULT 0 COMMENT '总金额',
    refund_amount DECIMAL(14,4) DEFAULT 0 COMMENT '退款金额',
    return_status VARCHAR(20) DEFAULT 'pending' COMMENT '退货状态: pending-待审核 approved-已审核 received-已收货 completed-已完成 rejected-已拒绝',
    warehouse_id BIGINT COMMENT '入库仓库ID',
    warehouse_name VARCHAR(100) COMMENT '仓库名称',
    remark TEXT COMMENT '退货原因',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
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
    unit_price DECIMAL(12,4) COMMENT '单价',
    amount DECIMAL(14,4) COMMENT '金额',
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
    created_by BIGINT,
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
    total_amount DECIMAL(14,4) DEFAULT 0 COMMENT '预估金额',
    request_status VARCHAR(20) DEFAULT 'draft' COMMENT '申请状态: draft-草稿 pending-待审核 approved-已审核 rejected-已拒绝 ordered-已下单',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
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
    reference_price DECIMAL(12,4) COMMENT '参考单价',
    reference_amount DECIMAL(14,4) COMMENT '参考金额',
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
    created_by BIGINT,
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
    unit_price DECIMAL(12,4) NOT NULL COMMENT '单价',
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
    created_by BIGINT,
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
    unit_price DECIMAL(12,4) COMMENT '单价',
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
    created_by BIGINT,
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
    unit_price DECIMAL(12,4) COMMENT '单价',
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
    created_by BIGINT,
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
    created_by BIGINT,
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
    created_by BIGINT,
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
    created_by BIGINT,
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
    created_by BIGINT,
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
    created_by BIGINT,
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
    created_by BIGINT,
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
    created_by BIGINT,
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
    created_by BIGINT,
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
    price DECIMAL(12,4) NOT NULL COMMENT '价格',
    discount_rate DECIMAL(5,2) COMMENT '折扣率(%)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_price_list_id (price_list_id),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='价格表明细';

CREATE TABLE jxc_customer_price (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    price DECIMAL(12,4) NOT NULL COMMENT '价格',
    min_quantity INT DEFAULT 1 COMMENT '最小数量',
    effective_date DATE COMMENT '生效日期',
    expiry_date DATE COMMENT '失效日期',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    UNIQUE KEY uk_customer_sku (customer_id, sku_id),
    KEY idx_customer_id (customer_id),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户价格表';
```

---
