# 接口文档模板

> 每个AI完成模块开发后，需要填写此文档

## 一、模块信息

| 字段 | 内容 |
|------|------|
| 模块名称 | |
| AI_ID | |
| 完成日期 | |
| 版本号 | |

## 二、接口列表

### 2.1 接口汇总

| 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|
| POST | /api/v1/xxx | 创建xxx | ✅ |
| GET | /api/v1/xxx/{id} | 获取xxx详情 | ✅ |
| PUT | /api/v1/xxx/{id} | 更新xxx | ✅ |
| DELETE | /api/v1/xxx/{id} | 删除xxx | ✅ |

### 2.2 接口详情

#### POST /api/v1/xxx

**描述**：创建xxx

**请求头**：
```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数**：
```json
{
  "field1": "string",     // 必填，说明
  "field2": "number",     // 可选，说明
  "field3": "string"      // 可选，说明
}
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "field1": "value1",
    "createTime": "2026-03-28 10:00:00"
  }
}
```

**错误码**：
| 错误码 | 说明 |
|--------|------|
| 1001 | 参数校验失败 |
| 2001 | xxx不存在 |

---

#### GET /api/v1/xxx/{id}

**描述**：获取xxx详情

**路径参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | xxx的ID |

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "field1": "value1",
    "status": "active",
    "createTime": "2026-03-28 10:00:00"
  }
}
```

---

## 三、数据模型

### 3.1 请求DTO

```java
@Data
public class XxxCreateDTO {
    @NotBlank(message = "field1不能为空")
    private String field1;
    
    private Integer field2;
    
    private String field3;
}
```

### 3.2 响应VO

```java
@Data
public class XxxVO {
    private Long id;
    private String field1;
    private String status;
    private LocalDateTime createTime;
}
```

---

## 四、状态枚举

| 枚举值 | 数据库值 | 显示名称 | 说明 |
|--------|---------|---------|------|
| PENDING | pending | 待处理 | 初始状态 |
| ACTIVE | active | 激活 | 正常状态 |
| DISABLED | disabled | 禁用 | 禁用状态 |

**状态流转**：
```
pending → active → disabled
    ↓         ↓
  deleted  deleted
```

---

## 五、依赖接口

本模块依赖其他模块的接口：

| 来源模块 | 接口 | 用途 |
|---------|------|------|
| AI-1 | GET /api/v1/products/{id} | 获取商品信息 |
| AI-4 | POST /api/v1/inventory/reserve | 库存占用 |

---

## 六、被依赖说明

其他模块对本模块接口的调用说明：

| 调用模块 | 接口 | 调用场景 |
|---------|------|---------|
| AI-4 | POST /api/v1/xxx | xxx场景 |
| AI-8 | GET /api/v1/xxx/{id} | xxx场景 |
