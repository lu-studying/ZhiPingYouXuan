# 订单 / 消费记录接口测试用例

## 目录
1. 创建消费记录（需登录）
2. 查询当前用户消费记录（需登录）
3. 按商家查询消费记录（管理端）

统一说明：
- 基础 URL：`http://localhost:8080`
- 创建和按用户查询接口需要 JWT：`Authorization: Bearer <token>`

---

## 1) 创建消费记录

### 接口信息
- 方法：`POST`
- URL：`/api/shops/{shopId}/orders`
- 认证：是（JWT）
- 功能：为当前登录用户在指定商家创建一条消费记录
- Body(JSON)：
  ```json
  {
    "amount": 188.50,
    "visitTime": "2025-12-15T19:30:00",
    "items": "[{\"name\":\"毛肚\",\"price\":58,\"count\":1}]"
  }
  ```

### 用例1.1 正常创建消费记录
- 场景：用户在商家 1 消费 188.5 元，录入一条记录。
- 请求：
  - 方法：`POST`
  - URL：`/api/shops/1/orders`
  - Header：
    - `Authorization: Bearer <token>`
    - `Content-Type: application/json`
  - Body：
    ```json
    {
      "amount": 188.50,
      "visitTime": "2025-12-15T19:30:00",
      "items": "[{\"name\":\"毛肚\",\"price\":58,\"count\":1}]"
    }
    ```
- 期望：
  - HTTP 200；
  - 返回 `OrderRecord`，包含生成的 `id`、`shopId=1`、`userId=当前用户`、`amount=188.50`。

### 用例1.2 不填 amount（允许为空）
- 场景：用户只想记录来过，但不写金额。
- Body：
  ```json
  {
    "visitTime": "2025-12-15T19:30:00",
    "items": "[]"
  }
  ```
- 期望：
  - HTTP 200；
  - 返回的 `amount` 为 `null`。

### 用例1.3 未登录创建（错误）
- 场景：未携带 Token。
- 请求：
  - 方法：`POST`
  - URL：`/api/shops/1/orders`
  - Header：无 `Authorization`
- 期望：
  - HTTP 403；
  - 提示未认证 / 访问被拒绝。

---

## 2) 查询当前用户消费记录（我的订单）

### 接口信息
- 方法：`GET`
- URL：`/api/users/me/orders`
- 认证：是（JWT）
- 功能：按当前登录用户查询所有消费记录（按时间倒序）

### 用例2.1 查询有记录的用户
- 前置条件：当前用户已通过用例1.1、1.2 创建过多条订单。
- 请求：
  - 方法：`GET`
  - URL：`/api/users/me/orders`
  - Header：`Authorization: Bearer <token>`
- 期望：
  - HTTP 200；
  - 返回 `List<OrderRecord>`；
  - 列表长度 >= 1，最近创建的记录排在最前。

### 用例2.2 查询没有记录的用户
- 场景：新注册用户，未创建过任何订单。
- 期望：
  - HTTP 200；
  - 返回空数组 `[]`。

### 用例2.3 未登录查询（错误）
- 场景：未携带 Token。
- 请求：
  - 方法：`GET`
  - URL：`/api/users/me/orders`
- 期望：
  - HTTP 403。

---

## 3) 按商家查询消费记录（管理端）

### 接口信息
- 方法：`GET`
- URL：`/api/shops/{shopId}/orders`
- 认证：是（JWT）（目前未区分角色，后续可加管理端角色）
- 功能：查询某商家的所有消费记录

### 用例3.1 查询有消费记录的商家
- 前置条件：商家 `shopId=1` 已产生多条消费记录。
- 请求：
  - 方法：`GET`
  - URL：`/api/shops/1/orders`
  - Header：`Authorization: Bearer <token>`
- 期望：
  - HTTP 200；
  - 响应格式：
    ```json
    {
      "content": [ /* OrderRecord 列表 */ ],
      "total": 3
    }
    ```
  - `total` 等于 `content` 的长度。

### 用例3.2 查询没有记录的商家
- 场景：商家 `shopId=99999` 或新建商家尚无消费记录。
- 请求：
  - 方法：`GET`
  - URL：`/api/shops/99999/orders`
- 期望：
  - HTTP 200；
  - `content` 为空数组，`total` 为 0。


