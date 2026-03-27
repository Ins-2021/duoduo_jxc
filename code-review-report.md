# 多多进销存系统代码审查报告

**审查日期：** 2026-03-26  
**审查范围：** 前后端完整代码  
**总体评估：** ⚠️ 存在一定风险

---

## 概述

项目整体架构清晰，采用了 Spring Security + JWT + RBAC 的标准安全框架，前后端分离设计合理。但存在 **数据库密码硬编码**、**修改密码未校验旧密码**、**CORS 完全放开**、**前端 Token 存储于 localStorage**、**登录页泄露默认凭据** 等安全和功能性问题，其中部分为严重级别。

---

## 问题清单

### 1. 🔴 数据库密码硬编码在配置文件中 [安全 - 高危]

**位置：** `backend/src/main/resources/application.yml`

**问题描述：** 数据库 root 用户的密码 `Root@1234` 直接硬编码在版本控制中的配置文件里。一旦代码仓库泄露，攻击者可直接获取数据库访问权限。

**风险评级：** CVSS 约 7.5

**当前代码：**

```yaml
url: jdbc:mysql://127.0.0.1:3306/duoduo_jxc?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai
username: root
password: Root@1234 # 请根据实际情况修改
```

**建议修复：**

```yaml
url: jdbc:mysql://127.0.0.1:3306/duoduo_jxc?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai
username: ${DB_USERNAME:root}
password: ${DB_PASSWORD}
```

> 密码通过环境变量注入，`application.yml` 只保留占位符。同时在 `.gitignore` 中排除 `application-dev.yml`、`application-prod.yml` 等环境配置文件。

---

### 2. 🔴 修改密码未验证旧密码 [安全 - 高危]

**位置：** `backend/src/main/java/com/duoduo/jxc/controller/SystemProfileController.java`

**问题描述：** 用户修改密码时，仅要求提供 `newPassword`，未校验当前密码。如果用户会话被劫持或设备未锁屏，攻击者可以直接修改密码。

**当前代码：**

```java
@PostMapping("/profile/change-password")
public Result<Void> changePassword(@RequestBody @Valid ResetPasswordRequest request) {
    Long userId = SecurityUtils.getUserId();
    if (userId == null) {
        throw new BusinessException(401, "未登录");
    }
    SysUser user = sysUserMapper.selectById(userId);
    if (user == null || user.getDeleted() != null && user.getDeleted() == 1) {
        throw new BusinessException(401, "用户不存在");
    }
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    sysUserMapper.updateById(user);
    return Result.success();
}
```

**建议修复：**

```java
@PostMapping("/profile/change-password")
public Result<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
    Long userId = SecurityUtils.getUserId();
    if (userId == null) {
        throw new BusinessException(401, "未登录");
    }
    SysUser user = sysUserMapper.selectById(userId);
    if (user == null || user.getDeleted() != null && user.getDeleted() == 1) {
        throw new BusinessException(401, "用户不存在");
    }
    if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
        throw new BusinessException(400, "原密码不正确");
    }
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    sysUserMapper.updateById(user);
    return Result.success();
}
```

> 对应 DTO 需新增 `oldPassword` 字段。

---

### 3. 🔴 创建用户时密码字段可选，允许创建无密码账户 [安全 - 高危]

**位置：** `backend/src/main/java/com/duoduo/jxc/controller/SystemUserController.java`

**问题描述：** `UserCreateRequest.password` 没有 `@NotBlank` 校验，且 Controller 中只在密码非空时才编码。攻击者可以创建一个 password 为 null 的用户。

**当前代码：**

```java
SysUser user = new SysUser();
user.setUsername(request.getUsername());
user.setRealName(request.getRealName());
user.setDeptId(request.getDeptId());
user.setStatus(request.getStatus() == null ? 1 : request.getStatus());
if (request.getPassword() != null && !request.getPassword().isBlank()) {
    user.setPassword(passwordEncoder.encode(request.getPassword()));
}
sysUserMapper.insert(user);
```

**建议修复：**

```java
SysUser user = new SysUser();
user.setUsername(request.getUsername());
user.setRealName(request.getRealName());
user.setDeptId(request.getDeptId());
user.setStatus(request.getStatus() == null ? 1 : request.getStatus());
if (request.getPassword() == null || request.getPassword().isBlank()) {
    throw new BusinessException(400, "密码不能为空");
}
user.setPassword(passwordEncoder.encode(request.getPassword()));
sysUserMapper.insert(user);
```

---

### 4. 🔴 CORS 配置完全放开，允许任意来源 [安全 - 高危]

**位置：** `backend/src/main/java/com/duoduo/jxc/config/CorsConfig.java`

**问题描述：** `allowedOriginPatterns("*")` 结合 `allowCredentials(true)` 意味着任何网站都可以携带凭据（Cookie/Token）向你的 API 发起跨域请求，可导致 CSRF 和数据窃取。

**当前代码：**

```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(true);
}
```

**建议修复：**

```java
@Value("${jxc.cors.allowed-origins:http://localhost:3000}")
private String[] allowedOrigins;

@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins(allowedOrigins)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
}
```

---

### 5. 🔴 前端 Token 存储在 localStorage 中，存在 XSS 窃取风险 [安全 - 中高危]

**位置：** `frontend/src/store/user.ts`

**问题描述：** `accessToken` 和 `refreshToken` 均存储在 `localStorage` 中。如果应用存在任何 XSS 漏洞，攻击者可通过 `localStorage.getItem()` 获取所有 Token。

**当前代码：**

```typescript
setTokens(accessToken: string, refreshToken: string) {
  this.accessToken = accessToken || ''
  this.refreshToken = refreshToken || ''
  if (this.accessToken) {
    localStorage.setItem(ACCESS_TOKEN_KEY, this.accessToken)
  } else {
    localStorage.removeItem(ACCESS_TOKEN_KEY)
  }
  if (this.refreshToken) {
    localStorage.setItem(REFRESH_TOKEN_KEY, this.refreshToken)
  } else {
    localStorage.removeItem(REFRESH_TOKEN_KEY)
  }
}
```

**建议修复：**

```typescript
setTokens(accessToken: string, refreshToken: string) {
  this.accessToken = accessToken || ''
  this.refreshToken = refreshToken || ''
  // 建议迁移方案：优先使用 HttpOnly Cookie（需后端配合）
  // 当前如继续使用 localStorage，需确保全站无 XSS 风险：
  // 1. 禁止 v-html 渲染用户输入
  // 2. 启用 CSP 策略
  // 3. 对所有用户输入进行 sanitize
  if (this.accessToken) {
    sessionStorage.setItem(ACCESS_TOKEN_KEY, this.accessToken)
  } else {
    sessionStorage.removeItem(ACCESS_TOKEN_KEY)
  }
  if (this.refreshToken) {
    sessionStorage.setItem(REFRESH_TOKEN_KEY, this.refreshToken)
  } else {
    sessionStorage.removeItem(REFRESH_TOKEN_KEY)
  }
}
```

> 最低限度建议先迁移到 `sessionStorage`（关闭标签页即清除），长期方案应改为 HttpOnly Cookie。

---

### 6. 🔴 前端权限检查可被绕过，后端权限粒度不足 [安全 - 中危]

**位置：** `frontend/src/store/user.ts`、`frontend/src/directives/perm.ts`

**问题描述：** 前端权限完全依赖 localStorage 中的 `perms` 数组，用户可在浏览器 DevTools 中手动修改 localStorage 添加 `*:*:*` 来绕过所有前端权限限制。`perm` 指令仅用 `removeChild` 隐藏元素，DOM 中仍可恢复。

**当前代码：**

```typescript
hasPerm(perm: string) {
  if (!perm) {
    return true
  }
  const perms = this.perms || []
  return perms.includes('*:*:*') || perms.includes('*') || perms.includes(perm)
}
```

**建议修复：**

```typescript
hasPerm(perm: string) {
  if (!perm) {
    return true
  }
  const perms = this.perms || []
  // 移除通配符快捷放行逻辑，确保每个权限精确匹配
  // 真正的超管判断应在后端完成，前端仅做 UI 隐藏
  return perms.includes(perm)
}
```

> 建议在关键操作按钮上增加后端二次校验。

---

### 7. 🔴 登录页泄露默认管理员凭据 [安全 - 中危]

**位置：** `frontend/src/views/login/index.vue`

**问题描述：** 登录表单默认填充了 `admin / admin123`，并在页面上显示提示文字。如果部署到公网，攻击者可直接使用此默认凭据登录系统。

**当前代码：**

```vue
<div class="tip">默认：admin / admin123</div>

const form = reactive({
  username: 'admin',
  password: 'admin123'
})
```

**建议修复：**

```vue
<!-- 生产环境移除默认凭据提示 -->

const form = reactive({
  username: '',
  password: ''
})
// 初始化时仅开发环境预填：
if (import.meta.env.DEV) {
  form.username = 'admin'
  form.password = 'admin123'
}
```

---

### 8. 🔴 Actuator 端点未鉴权完全暴露 [安全 - 中危]

**位置：** `backend/src/main/java/com/duoduo/jxc/security/SecurityConfig.java`

**问题描述：** `/actuator/**` 路径被设为 `permitAll()`，后续如果新增端点（如 `env`、`beans`），也会自动暴露，可能泄露应用内部配置。

**当前代码：**

```java
http.authorizeHttpRequests(auth -> auth
        .requestMatchers("/auth/**").permitAll()
        .requestMatchers("/actuator/**").permitAll()
        .anyRequest().authenticated());
```

**建议修复：**

```java
http.authorizeHttpRequests(auth -> auth
        .requestMatchers("/auth/**").permitAll()
        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
        .anyRequest().authenticated());
```

---

### 9. 🟡 JWT 密钥文件无权限控制，computeKid 异常吞没返回 null [安全/健壮性 - 中危]

**位置：** `backend/src/main/java/com/duoduo/jxc/security/JwtKeyConfig.java`

**问题描述：**
1. 生成的 `jwt-private.pem` 和 `jwt-public.pem` 文件未设置文件权限（默认可能为 644，其他用户可读）
2. `computeKid` 方法 catch 了所有异常后返回 `null`，导致 JWT 的 `kid` 可能为 null

**建议修复：**

```java
Files.createDirectories(dir);
KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
gen.initialize(2048);
KeyPair keyPair = gen.generateKeyPair();
writePem(privateKeyPath, "PRIVATE KEY", keyPair.getPrivate().getEncoded());
writePem(publicKeyPath, "PUBLIC KEY", keyPair.getPublic().getEncoded());
// 设置私钥文件仅所有者可读写 (600)
privateKeyPath.toFile().setReadable(false, false);
privateKeyPath.toFile().setReadable(true, true);
privateKeyPath.toFile().setWritable(false, false);
privateKeyPath.toFile().setWritable(true, true);
return keyPair;
```

---

### 10. 🟡 FileController.download 未做空指针防御 [健壮性 - 中危]

**位置：** `backend/src/main/java/com/duoduo/jxc/controller/FileController.java`

**问题描述：** 当 `fileResourceService.getById(id)` 返回 null 时，`fr.getFileName()` 会抛出 `NullPointerException`。

**当前代码：**

```java
@GetMapping("/{id}/download")
@PreAuthorize("@perm.has('common:file:download')")
public ResponseEntity<Resource> download(@PathVariable Long id) {
    FileResource fr = fileResourceService.getById(id);
    Resource resource = fileResourceService.loadAsResource(id);
    String filename = fr == null ? "file" : fr.getFileName();
```

**建议修复：**

```java
@GetMapping("/{id}/download")
@PreAuthorize("@perm.has('common:file:download')")
public ResponseEntity<Resource> download(@PathVariable Long id) {
    FileResource fr = fileResourceService.getById(id);
    if (fr == null) {
        return ResponseEntity.notFound().build();
    }
    Resource resource = fileResourceService.loadAsResource(id);
    String filename = fr.getFileName();
```

---

### 11. 🟡 SalesOrderServiceImpl.convertToSales 使用 RuntimeException 而非 BusinessException [一致性 - 低危]

**位置：** `backend/src/main/java/com/duoduo/jxc/service/impl/SalesOrderServiceImpl.java`

**问题描述：** 项目统一使用 `BusinessException` 处理业务异常，但 `convertToSales` 方法中抛出了 `RuntimeException`，这会导致异常无法被 `GlobalExceptionHandler` 正确捕获。

**当前代码：**

```java
SalesOrderDTO booking = getDetail(bookingOrderId);
if (booking == null || booking.getOrderType() != 3) {
    throw new RuntimeException("预订单不存在或类型错误");
}
```

**建议修复：**

```java
SalesOrderDTO booking = getDetail(bookingOrderId);
if (booking == null || booking.getOrderType() != 3) {
    throw new BusinessException("预订单不存在或类型错误");
}
```

---

### 12. 🟡 SysUser 实体直接暴露密码字段，JSON 序列化可能泄露密码 [安全 - 低危]

**位置：** `backend/src/main/java/com/duoduo/jxc/entity/SysUser.java`

**问题描述：** `SysUser` 实体包含 `password` 字段。在 `SystemUserController.list()` 中直接返回 `List<SysUser>`，密码字段会被序列化到 JSON 响应中。

**当前代码：**

```java
private String password;
```

**建议修复：**

```java
@com.fasterxml.jackson.annotation.JsonIgnore
private String password;
```

---

### 13. 🟡 前端 request.ts 模块级别实例化 Pinia Store 存在时序风险 [健壮性 - 低危]

**位置：** `frontend/src/utils/request.ts`、`frontend/src/utils/auth.ts`、`frontend/src/directives/perm.ts`、`frontend/src/router/index.ts`

**问题描述：** 在多个非组件文件的模块顶层调用 `useUserStore(pinia)`。Vue 3 + Pinia 官方文档明确指出 `useStore()` 应在 `setup()` 函数或组件内部调用。

**建议修复：**

将 `useUserStore(pinia)` 调用从模块顶层移至使用它的函数内部，例如：

```typescript
// 从模块顶层移除
// const userStore = useUserStore(pinia)

service.interceptors.request.use(
  config => {
    // 在使用处调用
    const userStore = useUserStore(pinia)
    const token = userStore.accessToken
    // ...
  }
)
```

---

### 14. 🟡 分页查询无上限校验，可能导致 OOM [性能 - 低危]

**位置：** `backend/src/main/java/com/duoduo/jxc/common/PageQuery.java`

**问题描述：** 未看到 `PageQuery` 对 `pageSize` 设置上限。如果攻击者传入 `pageSize=999999`，会导致一次性加载海量数据到内存，可能引发 OOM。

**建议修复：** 对 `pageSize` 限制最大值（如 500）。

---

### 15. 🟡 前端 API 函数参数全部使用 `any` 类型 [可维护性 - 低危]

**位置：** `frontend/src/api/auth.ts`、`frontend/src/api/sales.ts` 等所有 API 文件

**问题描述：** 所有 API 函数的参数和返回值都使用 `any` 类型，失去了 TypeScript 类型检查的意义。

**建议修复：** 定义统一的 `RequestDTO` 和 `ResponseVO` 类型接口，在 API 层和视图层共享。

---

## 总结

| 严重级别 | 数量 | 关键问题 |
|---------|------|---------|
| 🔴 严重 | 8 | 数据库密码硬编码、修改密码不验旧密码、无密码用户创建、CORS 全开、Token localStorage 存储、权限可绕过、默认凭据泄露、Actuator 暴露 |
| 🟡 一般 | 7 | JWT 密钥权限、NPE 风险、异常类型不一致、密码字段序列化泄露、Pinia 时序、分页无上限、any 类型泛滥 |

### 优先处理建议

**Issue 1-8 为安全高危，建议立即修复。其中：**

1. **Issue 1（密码硬编码）** 和 **Issue 4（CORS 全开）** 是最高优先级，因为它们可以在代码仓库泄露后直接被利用
2. **Issue 2（修改密码不验旧密码）** 和 **Issue 3（无密码用户）** 涉及认证逻辑，需尽快修复
3. **Issue 5（Token 存储）** 和 **Issue 6（权限绕过）** 涉及前端安全，需要前后端配合修复
4. **Issue 7（默认凭据）** 在部署到生产环境前必须移除
5. **Issue 8（Actuator 暴露）** 需要限制端点访问范围

---

## 附录：涉及文件列表

### 后端文件
- `backend/src/main/resources/application.yml`
- `backend/src/main/java/com/duoduo/jxc/config/CorsConfig.java`
- `backend/src/main/java/com/duoduo/jxc/security/SecurityConfig.java`
- `backend/src/main/java/com/duoduo/jxc/security/JwtKeyConfig.java`
- `backend/src/main/java/com/duoduo/jxc/controller/SystemProfileController.java`
- `backend/src/main/java/com/duoduo/jxc/controller/SystemUserController.java`
- `backend/src/main/java/com/duoduo/jxc/controller/FileController.java`
- `backend/src/main/java/com/duoduo/jxc/service/impl/SalesOrderServiceImpl.java`
- `backend/src/main/java/com/duoduo/jxc/entity/SysUser.java`
- `backend/src/main/java/com/duoduo/jxc/common/PageQuery.java`

### 前端文件
- `frontend/src/store/user.ts`
- `frontend/src/directives/perm.ts`
- `frontend/src/views/login/index.vue`
- `frontend/src/utils/request.ts`
- `frontend/src/utils/auth.ts`
- `frontend/src/router/index.ts`
- `frontend/src/api/auth.ts`
- `frontend/src/api/sales.ts`
- `frontend/src/api/product.ts`
