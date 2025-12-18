# 点评相关接口测试用例（含 AI 草稿与推荐）

## 目录
1. 获取点评列表（公开）
2. 创建点评（需登录）
3. AI 生成点评草稿（需登录）
4. AI 点评推荐（公开）

统一说明：
- 基础 URL：`http://localhost:8080`
- 需要认证的接口：在 Header 添加 `Authorization: Bearer <token>`
- 评分范围：1-5

---

## 1) 获取点评列表
- 方法：`GET`
- URL：`/api/shops/{shopId}/reviews`
- 认证：否
- 功能：分页查询店铺点评
- 参数：
  - `page`（可选，默认0）
  - `size`（可选，默认10）

### 用例1.1 基础分页
```
GET /api/shops/1/reviews?page=0&size=10
```
期望：HTTP 200，返回 `content` 数组、`total`、`page`、`size`。

### 用例1.2 第二页
```
GET /api/shops/1/reviews?page=1&size=5
```
期望：HTTP 200，page=1，size=5。

### 用例1.3 空结果
```
GET /api/shops/99999/reviews
```
期望：HTTP 200，`content` 为空，`total` 为 0。

---

## 2) 创建点评
- 方法：`POST`
- URL：`/api/shops/{shopId}/reviews`
- 认证：是（JWT）
- Body(JSON)：
  ```json
  {
    "rating": 5,
    "content": "很好吃，服务热情",
    "images": "[\"https://xx/1.jpg\"]"
  }
  ```
  必填：`rating`，`content`

### 用例2.1 正常创建
```
POST /api/shops/1/reviews
Headers: Authorization: Bearer <token>
Body: { "rating": 5, "content": "好吃", "images": "[\"https://xx/1.jpg\"]" }
```
期望：HTTP 200，返回点评对象，`isAiGenerated=false`。

### 用例2.2 缺少内容（错误）
```
POST /api/shops/1/reviews
Headers: Authorization: Bearer <token>
Body: { "rating": 4 }
```
期望：HTTP 400 或业务校验失败。

### 用例2.3 未登录（错误）
```
POST /api/shops/1/reviews
Body: { "rating": 5, "content": "好吃" }
```
期望：HTTP 403。

---

## 3) AI 生成点评草稿
- 方法：`POST`
- URL：`/api/shops/{shopId}/reviews/ai-draft`
- 认证：是（JWT）
- Body(JSON)：
  ```json
  {
    "preference": "偏辣，想突出服务体验",
    "maxTokens": 120
  }
  ```
  `preference` 可空，`maxTokens` 可空

### 用例3.1 正常生成
```
POST /api/shops/1/reviews/ai-draft
Headers: Authorization: Bearer <token>
Body: { "preference": "偏辣，别太咸", "maxTokens": 120 }
```
期望：HTTP 200，`{"draft": "..."}` 字数 50~120 左右。

### 用例3.2 不带偏好
```
POST /api/shops/1/reviews/ai-draft
Headers: Authorization: Bearer <token>
Body: {}
```
期望：HTTP 200，返回草稿。

### 用例3.3 未登录（错误）
```
POST /api/shops/1/reviews/ai-draft
Body: { "preference": "偏辣" }
```
期望：HTTP 403。

---

## 4) AI 点评推荐
- 方法：`GET`
- URL：`/api/shops/{shopId}/reviews/recommend`
- 认证：否（可匿名）
- 参数：
  - `preference`（可选，如 "辣"、"微辣"、"不辣"、"环境"）
  - `limit`（可选，默认 3，最大 10）

### 用例4.1 基于偏好的推荐
```
GET /api/shops/1/reviews/recommend?preference=辣&limit=3
```
期望：HTTP 200，返回 1~3 条点评，命中偏好关键词优先。

### 用例4.2 兜底推荐（无偏好）
```
GET /api/shops/1/reviews/recommend
```
期望：HTTP 200，返回热门点评（按点赞/评分/时间）。

### 用例4.3 限制数量
```
GET /api/shops/1/reviews/recommend?limit=1
```
期望：HTTP 200，返回 1 条。

### 用例4.4 店铺不存在
```
GET /api/shops/99999/reviews/recommend
```
期望：HTTP 200，可能返回空列表。

### 用例4.5 关键词索引命中验证（先创建含“辣”点评，再推荐）
步骤：
1) 创建点评（需登录）：
```
POST /api/shops/1/reviews
Headers: Authorization: Bearer <token>
Body:
{
  "rating": 5,
  "content": "这家火锅很辣很过瘾，服务也不错",
  "images": "[]"
}
```
期望：HTTP 200，创建成功。
2) 调用推荐：
```
GET /api/shops/1/reviews/recommend?preference=辣&limit=3
```
期望：HTTP 200，返回列表首条应包含上一步的点评（因 review_keyword 索引命中“辣”）。

### 用例4.6 无关键词命中时的兜底
```
GET /api/shops/1/reviews/recommend?preference=不存在的口味&limit=3
```
期望：HTTP 200，回落到正文 LIKE 或热门兜底，结果非空（若店内有点评），不报错。

---

## 获取 Token（前置步骤，创建点评/AI草稿需登录）
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

## 5) 评分边界与非法值

### 用例5.1 评分最小值 1
- 场景：用户对店铺评分为 1 分。
- 请求：
  - 方法：`POST`
  - URL：`/api/shops/1/reviews`
  - Header：`Authorization: Bearer <token>`
  - Body：
    ```json
    {
      "rating": 1,
      "content": "非常差，不推荐",
      "images": "[]"
    }
    ```
- 期望：
  - HTTP 200
  - 返回的 `rating` 字段为 1。

### 用例5.2 评分最大值 5
- 场景：用户对店铺评分为 5 分。
- 请求：
  - 方法：`POST`
  - URL：`/api/shops/1/reviews`
  - Header：`Authorization: Bearer <token>`
  - Body：
    ```json
    {
      "rating": 5,
      "content": "非常好，强烈推荐",
      "images": "[]"
    }
    ```
- 期望：
  - HTTP 200
  - 返回的 `rating` 字段为 5。

### 用例5.3 评分为 0（非法值）
- 场景：前端传入 rating=0。
- 请求：
  - 方法：`POST`
  - URL：`/api/shops/1/reviews`
  - Header：`Authorization: Bearer <token>`
  - Body：
    ```json
    {
      "rating": 0,
      "content": "测试非法评分",
      "images": "[]"
    }
    ```
- 期望：
  - HTTP 400 或业务校验失败（如果后续在 Service 增加校验）。
  - 返回错误信息中提示评分范围错误。

### 用例5.4 评分为 6（非法值）
- 场景：前端传入 rating=6。
- 请求：
  - 方法：`POST`
  - URL：`/api/shops/1/reviews`
  - Header：`Authorization: Bearer <token>`
  - Body：
    ```json
    {
      "rating": 6,
      "content": "测试非法评分",
      "images": "[]"
    }
    ```
- 期望：
  - HTTP 400 或业务校验失败。
  - 返回错误信息中提示评分范围错误。

---

## 6) 图片字段格式相关

### 用例6.1 无图片（空数组字符串）
- 场景：用户仅文字点评。
- 请求：
  - 方法：`POST`
  - URL：`/api/shops/1/reviews`
  - Header：`Authorization: Bearer <token>`
  - Body：
    ```json
    {
      "rating": 4,
      "content": "味道不错，就是有点辣",
      "images": "[]"
    }
    ```
- 期望：
  - HTTP 200
  - 数据库中 `images` 字段为 `"[]"`。

### 用例6.2 多张图片
- 场景：用户上传多张图片。
- 请求：
  - 方法：`POST`
  - URL：`/api/shops/1/reviews`
  - Header：`Authorization: Bearer <token>`
  - Body：
    ```json
    {
      "rating": 5,
      "content": "环境很好，适合聚会",
      "images": "[\"https://img.example.com/a.jpg\",\"https://img.example.com/b.jpg\"]"
    }
    ```
- 期望：
  - HTTP 200
  - `images` 为合法 JSON 字符串。

### 用例6.3 非法 JSON 字符串
- 场景：`images` 传 `"not-json"`。
- 请求：
  - 方法：`POST`
  - URL：`/api/shops/1/reviews`
  - Header：`Authorization: Bearer <token>`
  - Body：
    ```json
    {
      "rating": 4,
      "content": "测试非法 images",
      "images": "not-json"
    }
    ```
- 期望：
  - 当前实现下请求可能仍然成功入库，但前端应避免此值；
  - 后续若在后端增加校验，则应返回 400，并提示 `images` 格式错误。

---

## 7) AI 草稿与推荐的异常场景

### 用例7.1 AI 草稿生成失败/超时
- 场景：后端调用外部 LLM 失败或超时（例如配置错误的 `ai.endpoint`）。
- 请求：
  - 方法：`POST`
  - URL：`/api/shops/1/reviews/ai-draft`
  - Header：`Authorization: Bearer <token>`
  - Body：
    ```json
    {
      "preference": "偏辣",
      "maxTokens": 120
    }
    ```
- 期望：
  - HTTP 200 或 500（取决于后续异常处理策略）；
  - 响应文案中给出“生成失败，请稍后重试”类似提示；
  - 表 `ai_call_log` 中新增一条记录，`type=generate`，`status=0`，`response_ref` 中包含错误摘要。

### 用例7.2 推荐接口 limit 超过上限
- 场景：`limit=100`，超过服务端安全上限 10。
- 请求：
  - 方法：`GET`
  - URL：`/api/shops/1/reviews/recommend?preference=辣&limit=100`
- 期望：
  - HTTP 200；
  - 实际返回条数不超过 10。

### 用例7.3 推荐接口 limit 为 0 或负数
- 场景：`limit=0` 或 `limit=-1`。
- 请求：
  - 方法：`GET`
  - URL：`/api/shops/1/reviews/recommend?limit=0`
- 期望：
  - HTTP 200；
  - 服务端将 limit 归一为最小值 1；
  - 如果店内有点评，返回 1 条记录。
