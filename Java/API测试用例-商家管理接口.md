# 商家管理接口测试用例

## 目录
1. [获取商家列表](#1-获取商家列表)
2. [获取商家详情](#2-获取商家详情)
3. [创建商家](#3-创建商家)
4. [更新商家](#4-更新商家)
5. [删除商家](#5-删除商家)

---

## 1. 获取商家列表

### 接口信息
- **请求方法**: `GET`
- **请求URL**: `http://localhost:8080/api/shops`
- **是否需要认证**: ❌ 否（匿名访问）
- **功能**: 分页查询商家列表，支持搜索和筛选

### 测试用例

#### 用例1.1：基础分页查询（默认参数）
**请求**:
```
GET http://localhost:8080/api/shops
```

**或者使用查询参数**:
```
GET http://localhost:8080/api/shops?page=0&size=10
```

**请求头**: 无需

**预期响应** (HTTP 200):
```json
{
  "content": [
    {
      "id": 1,
      "name": "海底捞火锅",
      "category": "火锅",
      "address": "北京市朝阳区xxx",
      "lng": 116.397128,
      "lat": 39.916527,
      "avgPrice": 150.0,
      "avgScore": 4.5,
      "status": 1,
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:00:00"
    }
  ],
  "total": 100,
  "page": 0,
  "size": 10,
  "totalPages": 10
}
```

---

#### 用例1.2：指定页码和每页大小
**请求**:
```
GET http://localhost:8080/api/shops?page=1&size=20
```

**请求参数**:
- `page`: 1 (第二页)
- `size`: 20 (每页20条)

**预期响应** (HTTP 200):
```json
{
  "content": [...],
  "total": 100,
  "page": 1,
  "size": 20,
  "totalPages": 5
}
```

---

#### 用例1.3：按分类筛选
**请求**:
```
GET http://localhost:8080/api/shops?category=火锅
```

**请求参数**:
- `category`: "火锅"

**预期响应** (HTTP 200):
```json
{
  "content": [
    {
      "id": 1,
      "name": "海底捞火锅",
      "category": "火锅",
      ...
    },
    {
      "id": 2,
      "name": "呷哺呷哺",
      "category": "火锅",
      ...
    }
  ],
  "total": 25,
  "page": 0,
  "size": 10,
  "totalPages": 3
}
```

**其他分类示例**:
- `category=川菜`
- `category=日料`
- `category=咖啡`
- `category=西餐`

---

#### 用例1.4：关键词搜索
**请求**:
```
GET http://localhost:8080/api/shops?keyword=海底捞
```

**请求参数**:
- `keyword`: "海底捞"

**说明**: 会在商家名称和地址中搜索包含"海底捞"的记录

**预期响应** (HTTP 200):
```json
{
  "content": [
    {
      "id": 1,
      "name": "海底捞火锅（朝阳店）",
      "address": "北京市朝阳区xxx",
      ...
    },
    {
      "id": 5,
      "name": "海底捞火锅（海淀店）",
      "address": "北京市海淀区xxx",
      ...
    }
  ],
  "total": 2,
  "page": 0,
  "size": 10,
  "totalPages": 1
}
```

---

#### 用例1.5：按最低评分筛选
**请求**:
```
GET http://localhost:8080/api/shops?minScore=4.5
```

**请求参数**:
- `minScore`: 4.5

**说明**: 只返回平均评分 >= 4.5 的商家

**预期响应** (HTTP 200):
```json
{
  "content": [
    {
      "id": 1,
      "name": "海底捞火锅",
      "avgScore": 4.8,
      ...
    }
  ],
  "total": 15,
  "page": 0,
  "size": 10,
  "totalPages": 2
}
```

---

#### 用例1.6：按最高价格筛选
**请求**:
```
GET http://localhost:8080/api/shops?maxPrice=100
```

**请求参数**:
- `maxPrice`: 100

**说明**: 只返回人均价格 <= 100 的商家

**预期响应** (HTTP 200):
```json
{
  "content": [
    {
      "id": 3,
      "name": "沙县小吃",
      "avgPrice": 25.0,
      ...
    }
  ],
  "total": 30,
  "page": 0,
  "size": 10,
  "totalPages": 3
}
```

---

#### 用例1.7：组合条件查询
**请求**:
```
GET http://localhost:8080/api/shops?category=火锅&keyword=海底捞&minScore=4.0&maxPrice=200&page=0&size=10
```

**请求参数**:
- `category`: "火锅"
- `keyword`: "海底捞"
- `minScore`: 4.0
- `maxPrice`: 200
- `page`: 0
- `size`: 10

**说明**: 查询火锅类、名称或地址包含"海底捞"、评分>=4.0、价格<=200的商家

**预期响应** (HTTP 200):
```json
{
  "content": [...],
  "total": 5,
  "page": 0,
  "size": 10,
  "totalPages": 1
}
```

---

#### 用例1.8：空结果查询
**请求**:
```
GET http://localhost:8080/api/shops?category=不存在的分类
```

**预期响应** (HTTP 200):
```json
{
  "content": [],
  "total": 0,
  "page": 0,
  "size": 10,
  "totalPages": 0
}
```

---

## 2. 获取商家详情

### 接口信息
- **请求方法**: `GET`
- **请求URL**: `http://localhost:8080/api/shops/{id}`
- **是否需要认证**: ❌ 否（匿名访问）
- **功能**: 根据商家ID获取商家详细信息

### 测试用例

#### 用例2.1：获取存在的商家详情
**请求**:
```
GET http://localhost:8080/api/shops/1
```

**路径参数**:
- `id`: 1

**请求头**: 无需

**预期响应** (HTTP 200):
```json
{
  "id": 1,
  "name": "海底捞火锅",
  "category": "火锅",
  "address": "北京市朝阳区xxx街道xxx号",
  "lng": 116.397128,
  "lat": 39.916527,
  "avgPrice": 150.0,
  "avgScore": 4.5,
  "status": 1,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

---

#### 用例2.2：获取不存在的商家（错误场景）
**请求**:
```
GET http://localhost:8080/api/shops/99999
```

**路径参数**:
- `id`: 99999 (不存在的ID)

**预期响应** (HTTP 404):
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "No message available",
  "path": "/api/shops/99999"
}
```

---

#### 用例2.3：无效的ID格式（错误场景）
**请求**:
```
GET http://localhost:8080/api/shops/abc
```

**路径参数**:
- `id`: "abc" (非数字)

**预期响应** (HTTP 400):
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'",
  "path": "/api/shops/abc"
}
```

---

## 3. 创建商家

### 接口信息
- **请求方法**: `POST`
- **请求URL**: `http://localhost:8080/api/shops`
- **是否需要认证**: ✅ 是（需要JWT Token）
- **功能**: 创建新商家

### 测试用例

#### 用例3.1：创建商家（完整信息）
**请求**:
```
POST http://localhost:8080/api/shops
```

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

**请求体** (JSON):
```json
{
  "name": "海底捞火锅",
  "category": "火锅",
  "address": "北京市朝阳区xxx街道xxx号",
  "lng": 116.397128,
  "lat": 39.916527,
  "avgPrice": 150.0
}
```

**字段说明**:
- `name`: 商家名称（必填）
- `category`: 商家分类（必填）
- `address`: 商家地址（必填）
- `lng`: 经度（可选）
- `lat`: 纬度（可选）
- `avgPrice`: 人均价格（可选）

**预期响应** (HTTP 201):
```json
{
  "id": 1,
  "name": "海底捞火锅",
  "category": "火锅",
  "address": "北京市朝阳区xxx街道xxx号",
  "lng": 116.397128,
  "lat": 39.916527,
  "avgPrice": 150.0,
  "avgScore": 0.0,
  "status": 1,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

---

#### 用例3.2：创建商家（仅必填字段）
**请求**:
```
POST http://localhost:8080/api/shops
```

**请求头**:
```
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体** (JSON):
```json
{
  "name": "星巴克咖啡",
  "category": "咖啡",
  "address": "北京市海淀区xxx"
}
```

**预期响应** (HTTP 201):
```json
{
  "id": 2,
  "name": "星巴克咖啡",
  "category": "咖啡",
  "address": "北京市海淀区xxx",
  "lng": null,
  "lat": null,
  "avgPrice": null,
  "avgScore": 0.0,
  "status": 1,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

---

#### 用例3.3：创建商家 - 缺少必填字段（错误场景）
**请求**:
```
POST http://localhost:8080/api/shops
```

**请求头**:
```
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体** (JSON):
```json
{
  "name": "测试商家",
  "category": "火锅"
}
```

**说明**: 缺少必填字段 `address`

**预期响应** (HTTP 400):
```json
{
  "code": 400,
  "message": "商家地址不能为空"
}
```

---

#### 用例3.4：创建商家 - 名称为空（错误场景）
**请求**:
```
POST http://localhost:8080/api/shops
```

**请求头**:
```
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体** (JSON):
```json
{
  "name": "",
  "category": "火锅",
  "address": "北京市xxx"
}
```

**预期响应** (HTTP 400):
```json
{
  "code": 400,
  "message": "商家名称不能为空"
}
```

---

#### 用例3.5：创建商家 - 未提供Token（错误场景）
**请求**:
```
POST http://localhost:8080/api/shops
```

**请求头**: 无Authorization头

**请求体** (JSON):
```json
{
  "name": "测试商家",
  "category": "火锅",
  "address": "北京市xxx"
}
```

**预期响应** (HTTP 403):
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/shops"
}
```

---

#### 用例3.6：创建商家 - 无效Token（错误场景）
**请求**:
```
POST http://localhost:8080/api/shops
```

**请求头**:
```
Authorization: Bearer invalid_token_12345
Content-Type: application/json
```

**预期响应** (HTTP 403):
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/shops"
}
```

---

## 4. 更新商家

### 接口信息
- **请求方法**: `PUT`
- **请求URL**: `http://localhost:8080/api/shops/{id}`
- **是否需要认证**: ✅ 是（需要JWT Token）
- **功能**: 更新商家信息（支持部分更新）

### 测试用例

#### 用例4.1：更新商家名称
**请求**:
```
PUT http://localhost:8080/api/shops/1
```

**请求头**:
```
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体** (JSON):
```json
{
  "name": "海底捞火锅（新店名）"
}
```

**说明**: 只更新name字段，其他字段保持不变

**预期响应** (HTTP 200):
```json
{
  "id": 1,
  "name": "海底捞火锅（新店名）",
  "category": "火锅",
  "address": "北京市朝阳区xxx",
  "lng": 116.397128,
  "lat": 39.916527,
  "avgPrice": 150.0,
  "avgScore": 4.5,
  "status": 1,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T11:00:00"
}
```

---

#### 用例4.2：更新多个字段
**请求**:
```
PUT http://localhost:8080/api/shops/1
```

**请求头**:
```
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体** (JSON):
```json
{
  "name": "海底捞火锅（升级版）",
  "address": "北京市朝阳区新地址xxx",
  "avgPrice": 180.0
}
```

**预期响应** (HTTP 200):
```json
{
  "id": 1,
  "name": "海底捞火锅（升级版）",
  "category": "火锅",
  "address": "北京市朝阳区新地址xxx",
  "lng": 116.397128,
  "lat": 39.916527,
  "avgPrice": 180.0,
  "avgScore": 4.5,
  "status": 1,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T11:00:00"
}
```

---

#### 用例4.3：更新地理位置
**请求**:
```
PUT http://localhost:8080/api/shops/1
```

**请求头**:
```
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体** (JSON):
```json
{
  "lng": 116.407526,
  "lat": 39.904211
}
```

**预期响应** (HTTP 200):
```json
{
  "id": 1,
  "name": "海底捞火锅",
  "category": "火锅",
  "address": "北京市朝阳区xxx",
  "lng": 116.407526,
  "lat": 39.904211,
  "avgPrice": 150.0,
  "avgScore": 4.5,
  "status": 1,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T11:00:00"
}
```

---

#### 用例4.4：更新平均评分（管理端特殊操作）
**请求**:
```
PUT http://localhost:8080/api/shops/1
```

**请求头**:
```
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体** (JSON):
```json
{
  "avgScore": 4.8
}
```

**说明**: 通常avgScore应该由系统根据用户点评自动计算，这里允许手动更新（管理端特殊需求）

**预期响应** (HTTP 200):
```json
{
  "id": 1,
  "name": "海底捞火锅",
  "category": "火锅",
  "address": "北京市朝阳区xxx",
  "lng": 116.397128,
  "lat": 39.916527,
  "avgPrice": 150.0,
  "avgScore": 4.8,
  "status": 1,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T11:00:00"
}
```

---

#### 用例4.5：更新商家状态
**请求**:
```
PUT http://localhost:8080/api/shops/1
```

**请求头**:
```
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体** (JSON):
```json
{
  "status": 0
}
```

**说明**: 将商家状态设置为0（下线/删除），等同于软删除

**预期响应** (HTTP 200):
```json
{
  "id": 1,
  "name": "海底捞火锅",
  "category": "火锅",
  "address": "北京市朝阳区xxx",
  "lng": 116.397128,
  "lat": 39.916527,
  "avgPrice": 150.0,
  "avgScore": 4.5,
  "status": 0,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T11:00:00"
}
```

---

#### 用例4.6：更新不存在的商家（错误场景）
**请求**:
```
PUT http://localhost:8080/api/shops/99999
```

**请求头**:
```
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体** (JSON):
```json
{
  "name": "测试商家"
}
```

**预期响应** (HTTP 404):
```json
{
  "code": 404,
  "message": "商家不存在"
}
```

---

#### 用例4.7：更新商家 - 未提供Token（错误场景）
**请求**:
```
PUT http://localhost:8080/api/shops/1
```

**请求头**: 无Authorization头

**请求体** (JSON):
```json
{
  "name": "新名称"
}
```

**预期响应** (HTTP 403):
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/shops/1"
}
```

---

## 5. 删除商家

### 接口信息
- **请求方法**: `DELETE`
- **请求URL**: `http://localhost:8080/api/shops/{id}`
- **是否需要认证**: ✅ 是（需要JWT Token）
- **功能**: 删除商家（软删除，设置status=0）

### 测试用例

#### 用例5.1：删除存在的商家
**请求**:
```
DELETE http://localhost:8080/api/shops/1
```

**路径参数**:
- `id`: 1

**请求头**:
```
Authorization: Bearer <token>
```

**请求体**: 无

**预期响应** (HTTP 200):
```json
{
  "message": "删除成功"
}
```

**说明**: 
- 删除后，该商家不会在查询列表中显示（因为查询时过滤status=1）
- 但数据库记录仍然存在，只是status被设置为0
- 这是软删除，可以恢复数据

---

#### 用例5.2：删除不存在的商家（错误场景）
**请求**:
```
DELETE http://localhost:8080/api/shops/99999
```

**路径参数**:
- `id`: 99999 (不存在的ID)

**请求头**:
```
Authorization: Bearer <token>
```

**预期响应** (HTTP 404):
```json
{
  "code": 404,
  "message": "商家不存在"
}
```

---

#### 用例5.3：删除商家 - 未提供Token（错误场景）
**请求**:
```
DELETE http://localhost:8080/api/shops/1
```

**请求头**: 无Authorization头

**预期响应** (HTTP 403):
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/shops/1"
}
```

---

## 获取JWT Token的方法

### 步骤1：用户登录
**请求**:
```
POST http://localhost:8080/api/users/login
```

**请求头**:
```
Content-Type: application/json
```

**请求体** (JSON):
```json
{
  "mobileOrEmail": "13800138000",
  "password": "your_password"
}
```

**或者使用邮箱登录**:
```json
{
  "mobileOrEmail": "user@example.com",
  "password": "your_password"
}
```

**预期响应** (HTTP 200):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwiaWF0IjoxNTE2MjM5MDIyfQ..."
}
```

### 步骤2：使用Token
将返回的token值复制，在需要认证的接口的请求头中添加：
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## Postman测试技巧

### 1. 环境变量设置
在Postman中设置环境变量：
- `base_url`: `http://localhost:8080`
- `token`: `你的JWT token`

然后在URL中使用：`{{base_url}}/api/shops`

### 2. 自动添加Token
在Postman的Collection或Request的"Authorization"标签中：
- Type: Bearer Token
- Token: `{{token}}`

这样所有请求都会自动添加Authorization头。

### 3. 测试脚本（Tests标签）
可以在Postman的Tests标签中添加脚本，自动保存token：
```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    if (jsonData.token) {
        pm.environment.set("token", jsonData.token);
    }
}
```

---

## 测试数据准备

### 使用SQL插入测试数据
```sql
-- 插入测试商家数据
INSERT INTO shop (name, category, address, lng, lat, avg_price, avg_score, status, created_at, updated_at)
VALUES 
('海底捞火锅', '火锅', '北京市朝阳区xxx', 116.397128, 39.916527, 150.0, 4.5, 1, NOW(), NOW()),
('星巴克咖啡', '咖啡', '北京市海淀区xxx', 116.316833, 39.959539, 45.0, 4.3, 1, NOW(), NOW()),
('呷哺呷哺', '火锅', '北京市西城区xxx', 116.366794, 39.913369, 80.0, 4.2, 1, NOW(), NOW()),
('沙县小吃', '快餐', '北京市东城区xxx', 116.418343, 39.906901, 25.0, 3.8, 1, NOW(), NOW());
```

---

## 总结

### 接口权限总结
| 接口 | 方法 | 是否需要认证 | 说明 |
|------|------|------------|------|
| 获取商家列表 | GET | ❌ | 支持分页、搜索、筛选 |
| 获取商家详情 | GET | ❌ | 根据ID查询 |
| 创建商家 | POST | ✅ | 需要JWT Token |
| 更新商家 | PUT | ✅ | 支持部分更新 |
| 删除商家 | DELETE | ✅ | 软删除 |

### 常见错误码
- **200**: 成功
- **201**: 创建成功
- **400**: 请求参数错误
- **403**: 未认证或权限不足
- **404**: 资源不存在
- **500**: 服务器内部错误

---

**注意**: 
1. 所有需要认证的接口都需要在请求头中添加 `Authorization: Bearer <token>`
2. Token可以通过登录接口获取
3. Token通常有过期时间，过期后需要重新登录获取新token
4. 测试前请确保数据库中有测试数据

