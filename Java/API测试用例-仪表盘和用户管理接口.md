# 仪表盘和用户管理接口测试用例

## 目录
1. 仪表盘数据统计接口
2. 用户管理接口
3. AI 调用日志查询接口

统一说明：
- 基础 URL：`http://localhost:8080`
- 需要认证的接口：在 Header 添加 `Authorization: Bearer <token>`

---

## 1) 仪表盘数据统计接口

### 接口信息
- 方法：`GET`
- URL：`/api/dashboard/stats`
- 认证：是（JWT）
- 功能：获取仪表盘页面的统计数据，包括商家数、点评数、订单数、AI调用统计、今日新增数据等

### 用例1.1 正常查询统计数据
- 场景：管理员登录后查看仪表盘统计数据。
- 前置条件：
  - 已登录，获取到有效的 JWT token。
- 请求：
  - 方法：`GET`
  - URL：`/api/dashboard/stats`
  - Header：`Authorization: Bearer <token>`
- 期望：
  - HTTP 200；
  - 响应包含以下字段：
    ```json
    {
      "shopCount": 100,
      "reviewCount": 5000,
      "orderCount": 2000,
      "aiCallCount": 1500,
      "aiCallSuccessRate": 0.95,
      "todayNewShops": 5,
      "todayNewReviews": 50,
      "todayNewOrders": 20
    }
    ```
  - 所有数值字段均为非负数；
  - `aiCallSuccessRate` 在 0.0 - 1.0 之间。

### 用例1.2 未登录查询（错误）
- 场景：未登录用户尝试查询统计数据。
- 请求：
  - 方法：`GET`
  - URL：`/api/dashboard/stats`
  - Header：无 `Authorization`
- 期望：
  - HTTP 403；
  - 响应提示未认证或没有权限（`Access Denied`）。

### 用例1.3 Token 过期（错误）
- 场景：使用已过期的 token 查询统计数据。
- 请求：
  - 方法：`GET`
  - URL：`/api/dashboard/stats`
  - Header：`Authorization: Bearer <expired_token>`
- 期望：
  - HTTP 403；
  - 响应提示 token 无效或已过期。

---

## 2) 用户管理接口

### 2.1 用户列表查询

#### 接口信息
- 方法：`GET`
- URL：`/api/users`
- 认证：是（JWT）
- 功能：分页查询用户列表，支持关键词搜索

#### 用例2.1.1 基础分页查询
- 场景：管理员查看用户列表第一页。
- 前置条件：
  - 已登录，获取到有效的 JWT token。
- 请求：
  - 方法：`GET`
  - URL：`/api/users?page=0&size=10`
  - Header：`Authorization: Bearer <token>`
- 期望：
  - HTTP 200；
  - 响应包含：
    ```json
    {
      "content": [
        {
          "id": 1,
          "mobile": "13800138000",
          "email": null,
          "nickname": "用户1",
          "avatar": null,
          "status": 1,
          "createdAt": "2025-12-15T10:00:00",
          "updatedAt": "2025-12-15T10:00:00"
        }
      ],
      "total": 100,
      "page": 0,
      "size": 10
    }
    ```
  - `content` 数组包含用户对象列表；
  - `total` 为符合条件的用户总数。

#### 用例2.1.2 关键词搜索
- 场景：管理员搜索包含特定关键词的用户。
- 请求：
  - 方法：`GET`
  - URL：`/api/users?page=0&size=10&keyword=138`
  - Header：`Authorization: Bearer <token>`
- 期望：
  - HTTP 200；
  - 返回的用户列表中，手机号或邮箱包含 "138" 的用户；
  - `total` 为符合条件的用户总数。

#### 用例2.1.3 第二页查询
- 场景：管理员查看用户列表第二页。
- 请求：
  - 方法：`GET`
  - URL：`/api/users?page=1&size=10`
  - Header：`Authorization: Bearer <token>`
- 期望：
  - HTTP 200；
  - `page` 为 1；
  - `content` 数组包含第 11-20 条用户记录。

#### 用例2.1.4 空结果
- 场景：搜索不存在的关键词。
- 请求：
  - 方法：`GET`
  - URL：`/api/users?page=0&size=10&keyword=不存在的用户`
  - Header：`Authorization: Bearer <token>`
- 期望：
  - HTTP 200；
  - `content` 为空数组；
  - `total` 为 0。

### 2.2 用户详情查询

#### 接口信息
- 方法：`GET`
- URL：`/api/users/{id}`
- 认证：是（JWT）
- 功能：根据用户ID查询用户详细信息

#### 用例2.2.1 正常查询用户详情
- 场景：管理员查看指定用户的详细信息。
- 前置条件：
  - 用户ID存在（例如：`userId=1`）。
- 请求：
  - 方法：`GET`
  - URL：`/api/users/1`
  - Header：`Authorization: Bearer <token>`
- 期望：
  - HTTP 200；
  - 响应包含完整的用户信息：
    ```json
    {
      "id": 1,
      "mobile": "13800138000",
      "email": null,
      "nickname": "用户1",
      "avatar": null,
      "status": 1,
      "createdAt": "2025-12-15T10:00:00",
      "updatedAt": "2025-12-15T10:00:00"
    }
    ```

#### 用例2.2.2 用户不存在（错误）
- 场景：查询不存在的用户ID。
- 请求：
  - 方法：`GET`
  - URL：`/api/users/99999`
  - Header：`Authorization: Bearer <token>`
- 期望：
  - HTTP 404；
  - 响应提示用户不存在。

### 2.3 用户点评列表查询

#### 接口信息
- 方法：`GET`
- URL：`/api/users/{id}/reviews`
- 认证：是（JWT）
- 功能：查询指定用户的所有点评列表（分页）

#### 用例2.3.1 查询用户点评列表
- 场景：管理员查看指定用户的所有点评。
- 前置条件：
  - 用户ID存在（例如：`userId=1`）。
- 请求：
  - 方法：`GET`
  - URL：`/api/users/1/reviews?page=0&size=10`
  - Header：`Authorization: Bearer <token>`
- 期望：
  - HTTP 200；
  - 响应包含：
    ```json
    {
      "content": [],
      "total": 0,
      "page": 0,
      "size": 10
    }
    ```
  - 注意：当前实现返回空列表（功能待实现），后续需要扩展 ReviewMapper 支持按用户ID查询。

---

## 3) AI 调用日志查询接口

### 接口信息
- 方法：`GET`
- URL：`/api/ai-logs`
- 认证：是（JWT）
- 功能：分页查询 AI 调用日志列表，支持多条件筛选（调用类型、状态、用户ID）

### 用例3.1 基础分页查询
- 场景：管理员查看 AI 调用日志列表第一页。
- 前置条件：
  - 已登录，获取到有效的 JWT token。
- 请求：
  - 方法：`GET`
  - URL：`/api/ai-logs?page=0&size=10`
  - Header：`Authorization: Bearer <token>`
- 期望：
  - HTTP 200；
  - 响应包含：
    ```json
    {
      "content": [
        {
          "id": 1,
          "userId": 3,
          "type": "generate",
          "prompt": "生成点评草稿...",
          "responseRef": "生成的草稿内容...",
          "latencyMs": 1200,
          "status": 1,
          "createdAt": "2025-12-15T10:00:00"
        }
      ],
      "total": 1500,
      "page": 0,
      "size": 10
    }
    ```
  - `content` 数组包含 AI 调用日志对象列表；
  - `total` 为符合条件的日志总数。

### 用例3.2 按调用类型筛选
- 场景：管理员只查看"生成草稿"类型的调用日志。
- 请求：
  - 方法：`GET`
  - URL：`/api/ai-logs?page=0&size=10&type=generate`
  - Header：`Authorization: Bearer <token>`
- 期望：
  - HTTP 200；
  - 返回的日志列表中，所有记录的 `type` 字段均为 "generate"。

### 用例3.3 按状态筛选
- 场景：管理员只查看失败的调用日志。
- 请求：
  - 方法：`GET`
  - URL：`/api/ai-logs?page=0&size=10&status=0`
  - Header：`Authorization: Bearer <token>`
- 期望：
  - HTTP 200；
  - 返回的日志列表中，所有记录的 `status` 字段均为 0（失败）。

### 用例3.4 按用户ID筛选
- 场景：管理员查看指定用户的 AI 调用日志。
- 前置条件：
  - 用户ID存在（例如：`userId=3`）。
- 请求：
  - 方法：`GET`
  - URL：`/api/ai-logs?page=0&size=10&userId=3`
  - Header：`Authorization: Bearer <token>`
- 期望：
  - HTTP 200；
  - 返回的日志列表中，所有记录的 `userId` 字段均为 3。

### 用例3.5 组合筛选
- 场景：管理员查看指定用户的"生成草稿"类型的成功调用日志。
- 请求：
  - 方法：`GET /api/ai-logs?page=0&size=10&type=generate&status=1&userId=3`
  - Header：`Authorization: Bearer <token>`
- 期望：
  - HTTP 200；
  - 返回的日志列表同时满足：
    - `type` = "generate"
    - `status` = 1（成功）
    - `userId` = 3

### 用例3.6 空结果
- 场景：筛选条件过于严格，没有符合条件的记录。
- 请求：
  - 方法：`GET`
  - URL：`/api/ai-logs?page=0&size=10&type=generate&status=0&userId=99999`
  - Header：`Authorization: Bearer <token>`
- 期望：
  - HTTP 200；
  - `content` 为空数组；
  - `total` 为 0。

### 用例3.7 未登录查询（错误）
- 场景：未登录用户尝试查询 AI 调用日志。
- 请求：
  - 方法：`GET`
  - URL：`/api/ai-logs?page=0&size=10`
  - Header：无 `Authorization`
- 期望：
  - HTTP 403；
  - 响应提示未认证或没有权限（`Access Denied`）。

---

## 获取 Token（前置步骤）

```
POST /api/users/login
Body:
{
  "mobileOrEmail": "13800138000",
  "password": "your_password"
}
```

响应示例：
```
{ "token": "..." }
```

随后在需要认证的请求头加入：
```
Authorization: Bearer <token>
```

---

## 注意事项

1. **分页参数**：
   - `page` 从 0 开始（0 表示第一页，1 表示第二页）
   - `size` 建议值：10、20、50、100

2. **关键词搜索**：
   - 用户列表搜索会在手机号和邮箱字段中同时匹配
   - 支持模糊匹配（LIKE 查询）

3. **AI 调用日志筛选**：
   - `type` 可选值：`generate`（生成草稿）、`recommend`（推荐）
   - `status` 可选值：`1`（成功）、`0`（失败）
   - `userId` 为数字类型，传入具体的用户ID

4. **用户点评列表**：
   - 当前实现返回空列表（功能待实现）
   - 后续需要扩展 ReviewMapper 添加 `findByUserId` 方法

