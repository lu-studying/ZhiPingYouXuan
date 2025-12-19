# 标签 / 偏好体系接口测试用例

## 目录
1. 创建标签
2. 查询标签列表
3. 为商家绑定标签并查询
4. 为当前用户绑定标签并查询

统一说明：
- 基础 URL：`http://localhost:8080`
- 需要认证的接口：在 Header 添加 `Authorization: Bearer <token>`

---

## 1) 创建标签

### 接口信息
- 方法：`POST`
- URL：`/api/tags`
- 认证：是（JWT，建议仅管理端使用）
- Body(JSON)：
  ```json
  {
    "name": "爱吃辣",
    "type": "user"
  }
  ```

### 用例1.1 正常创建用户标签
- 场景：运营人员创建“爱吃辣”用户标签。
- 请求：
  - 方法：`POST`
  - URL：`/api/tags`
  - Header：`Authorization: Bearer <token>`
  - Body：
    ```json
    {
      "name": "爱吃辣",
      "type": "user"
    }
    ```
- 期望：
  - HTTP 200；
  - 返回 `Tag` 对象，包含生成的 `id`、`name="爱吃辣"`、`type="user"`。

### 用例1.2 缺少名称（错误）
- 场景：name 为空字符串。
- Body：
  ```json
  {
    "name": "",
    "type": "shop"
  }
  ```
- 期望：
  - HTTP 400；
  - 响应：
    ```json
    {
      "code": 400,
      "message": "标签名称不能为空"
    }
    ```

### 用例1.3 缺少类型（错误）
- Body：
  ```json
  {
    "name": "适合聚会"
  }
  ```
- 期望：
  - HTTP 400；
  - message 为“标签类型不能为空”。

---

## 2) 查询标签列表

### 接口信息
- 方法：`GET`
- URL：`/api/tags`
- 认证：是（JWT；如需完全开放可后续放开）
- 参数：
  - `type`（可选）：user / shop / review，不传则查询全部。

### 用例2.1 查询所有标签
- 请求：
  - 方法：`GET`
  - URL：`/api/tags`
  - Header：`Authorization: Bearer <token>`
- 期望：
  - HTTP 200；
  - 返回 `Tag` 数组，长度 >= 0。

### 用例2.2 查询商家标签
- 请求：
  - 方法：`GET`
  - URL：`/api/tags?type=shop`
- 期望：
  - HTTP 200；
  - 返回的每个 `Tag` 的 `type` 都为 `"shop"`。

---

## 3) 为商家绑定标签并查询

### 接口信息
- 绑定：
  - 方法：`POST`
  - URL：`/api/shops/{shopId}/tags`
  - Body(JSON)：
    ```json
    {
      "tagIds": [1, 2, 3]
    }
    ```
- 查询：
  - 方法：`GET`
  - URL：`/api/shops/{shopId}/tags`

### 用例3.1 为商家绑定多个标签
- 前置条件：
  - 已存在 `shopId=1`；
  - 已存在 `Tag`：`id=1,type=shop`（如“火锅”），`id=2,type=shop`（如“适合聚会”）。
- 绑定请求：
  - 方法：`POST`
  - URL：`/api/shops/1/tags`
  - Body：
    ```json
    {
      "tagIds": [1, 2]
    }
    ```
- 期望：
  - HTTP 200；
  - 响应：`{"message":"商家标签绑定成功"}`。

### 用例3.2 查询商家已绑定标签
- 请求：
  - 方法：`GET`
  - URL：`/api/shops/1/tags`
- 期望：
  - HTTP 200；
  - 返回标签列表，至少包含 id=1 和 id=2 对应的标签。

### 用例3.3 为商家清空标签
- 场景：传入空数组，表示清空当前商家所有标签。
- 请求：
  - 方法：`POST`
  - URL：`/api/shops/1/tags`
  - Body：
    ```json
    {
      "tagIds": []
    }
    ```
- 期望：
  - HTTP 200；
  - 再次查询 `/api/shops/1/tags`，返回空数组 `[]`。

---

## 4) 为当前用户绑定标签并查询

### 接口信息
- 绑定当前用户标签：
  - 方法：`POST`
  - URL：`/api/users/me/tags`
  - 认证：是（JWT，从 token 解析 userId）
  - Body(JSON)：
    ```json
    {
      "tagIds": [3, 4]
    }
    ```
- 查询当前用户标签：
  - 方法：`GET`
  - URL：`/api/users/me/tags`

### 用例4.1 为当前用户绑定偏好标签
- 前置条件：
  - 已存在用户并登录；
  - 已存在 `Tag`：`id=3,type=user`（如“爱吃辣”），`id=4,type=user`（如“口味清淡”）。
- 请求：
  - 方法：`POST`
  - URL：`/api/users/me/tags`
  - Header：`Authorization: Bearer <token>`
  - Body：
    ```json
    {
      "tagIds": [3]
    }
    ```
- 期望：
  - HTTP 200；
  - 响应：`{"message":"用户标签绑定成功"}`。

### 用例4.2 查询当前用户标签
- 请求：
  - 方法：`GET`
  - URL：`/api/users/me/tags`
  - Header：`Authorization: Bearer <token>`
- 期望：
  - HTTP 200；
  - 返回的标签列表中包含 id=3 对应的标签。

### 用例4.3 为用户清空标签
- 请求：
  - 方法：`POST`
  - URL：`/api/users/me/tags`
  - Body：
    ```json
    {
      "tagIds": []
    }
    ```
- 期望：
  - HTTP 200；
  - 再次调用 `GET /api/users/me/tags` 时返回空数组。


