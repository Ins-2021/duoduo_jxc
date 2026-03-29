# API接口详细设计

> **模块标识**: API-DESIGN  
> **所属系统**: ERP-API  
> **AI处理提示**: 本文档需配合 08-前端页面设计.md、06-生产流程设计.md 使用

---

## 模块关联

本文档与以下模块存在数据/流程关联：

| 关联类型 | 目标文档 | 关联内容 |
|----------|----------|----------|
| 数据输出 | 08-前端页面设计.md | API响应数据供前端页面展示 |
| 业务实现 | 06-生产流程设计.md | 工序、扎包、计件等API实现业务流程 |
| 数据基础 | 01-系统架构设计.md | 基础数据接口依赖系统架构 |
| 权限控制 | 05-权限体系设计.md | 接口权限标识与权限体系对应 |

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
