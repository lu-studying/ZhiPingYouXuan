# 智评优选（Dianping+AI）UniApp 用户端开发计划

> **计划制定时间**: 2026-1-10  
> **项目名称**: 智评优选（Dianping+AI）用户端  
> **技术栈**: UniApp (Vue 3) + HBuilderX  
> **目标平台**: 微信小程序、H5、App（可选）  
> **后端 API**: Java Spring Boot（已完成 95%）

---

## 📋 一、项目概述

### 1.1 项目定位

**用户端（UniApp）**是面向 C 端用户的应用入口，与**管理端（Vue）**完全分离：
- **管理端**：仅供内部运营/管理员使用，用于数据管理和监控
- **用户端**：普通用户使用，核心功能包括浏览商家、查看点评、使用 AI 功能、管理个人数据

### 1.2 核心功能亮点

1. **AI 点评推荐**：基于用户偏好和标签，智能推荐相关点评，并展示推荐理由
2. **AI 草稿生成**：帮助用户快速生成点评草稿，提升点评质量
3. **标签系统**：用户可设置个人偏好标签，影响推荐算法
4. **商家浏览**：搜索、筛选、查看商家详情和点评

### 1.3 技术选型

- **框架**: UniApp (Vue 3 Composition API)
- **状态管理**: Pinia（推荐）或 Vuex
- **UI 组件库**: 
  - 微信小程序：uView UI 或 uni-ui
  - H5：可复用部分 Element Plus 组件（需适配）
- **HTTP 请求**: uni.request 封装（参考 Vue 管理端的 request.js）
- **路由**: uni-app 页面路由（pages.json 配置）
- **存储**: uni.setStorageSync / uni.getStorageSync（替代 localStorage）

---

## 🏗️ 二、项目结构设计

### 2.1 目录结构

```
UniApp/
├── pages/                      # 页面目录
│   ├── home/                   # 首页
│   │   └── index.vue
│   ├── shop/                   # 商家相关
│   │   ├── detail.vue          # 商家详情页（核心）
│   │   └── list.vue            # 商家列表页（可选，首页已包含）
│   ├── review/                 # 点评相关
│   │   ├── create.vue          # 创建点评页（核心）
│   │   └── list.vue            # 点评列表页（可选）
│   ├── user/                   # 用户中心
│   │   ├── index.vue           # 用户中心首页
│   │   ├── login.vue           # 登录页
│   │   ├── register.vue        # 注册页
│   │   ├── profile.vue         # 个人资料
│   │   ├── orders.vue          # 我的订单
│   │   ├── reviews.vue         # 我的点评
│   │   └── tags.vue            # 偏好设置（标签管理）
│   └── order/                  # 订单相关（可选）
│       └── create.vue          # 创建订单页
├── components/                 # 公共组件
│   ├── shop-card.vue          # 商家卡片
│   ├── review-card.vue         # 点评卡片（带推荐理由展示）
│   ├── rating-stars.vue        # 评分组件（星级显示）
│   ├── loading.vue             # 加载状态组件
│   ├── empty-state.vue        # 空状态组件
│   ├── ai-recommend-badge.vue  # AI 推荐标识组件
│   └── tag-selector.vue        # 标签选择器
├── api/                        # API 接口封装（参考 Vue 管理端）
│   ├── request.js              # HTTP 请求封装（统一处理 token）
│   ├── shops.js                # 商家相关 API
│   ├── reviews.js              # 点评相关 API
│   ├── orders.js               # 订单相关 API
│   ├── tags.js                 # 标签相关 API
│   ├── users.js                # 用户相关 API
│   └── auth.js                 # 认证相关 API
├── store/                      # 状态管理（Pinia）
│   ├── index.js                # Pinia 实例
│   ├── auth.js                 # 用户认证状态
│   └── user.js                 # 用户信息状态
├── utils/                      # 工具函数
│   ├── jwt.js                  # JWT 工具（token 解析、过期检查）
│   ├── format.js               # 格式化工具（日期、价格等）
│   └── constants.js            # 常量定义（API 基础 URL 等）
├── static/                     # 静态资源
│   ├── images/                 # 图片资源
│   └── icons/                  # 图标资源
├── styles/                     # 全局样式
│   ├── common.scss             # 公共样式
│   └── variables.scss          # 样式变量
├── manifest.json               # 应用配置（小程序 AppID、H5 配置等）
├── pages.json                  # 页面路由配置
├── App.vue                     # 应用入口
└── main.js                     # 入口文件
```

### 2.2 关键配置文件

#### `manifest.json` - 应用配置
```json
{
  "name": "智评优选",
  "appid": "your-miniprogram-appid",
  "description": "智评优选用户端",
  "versionName": "1.0.0",
  "versionCode": "100",
  "transformPx": false,
  "h5": {
    "router": {
      "mode": "hash"
    },
    "devServer": {
      "port": 8080,
      "proxy": {
        "/api": {
          "target": "http://localhost:8080",
          "changeOrigin": true
        }
      }
    }
  },
  "mp-weixin": {
    "appid": "your-miniprogram-appid",
    "setting": {
      "urlCheck": false
    }
  }
}
```

#### `pages.json` - 页面路由配置
```json
{
  "pages": [
    {
      "path": "pages/home/index",
      "style": {
        "navigationBarTitleText": "智评优选",
        "enablePullDownRefresh": true
      }
    },
    {
      "path": "pages/shop/detail",
      "style": {
        "navigationBarTitleText": "商家详情",
        "enablePullDownRefresh": true
      }
    },
    {
      "path": "pages/review/create",
      "style": {
        "navigationBarTitleText": "写点评"
      }
    },
    {
      "path": "pages/user/index",
      "style": {
        "navigationBarTitleText": "我的"
      }
    }
  ],
  "tabBar": {
    "color": "#7A7E83",
    "selectedColor": "#3c9cff",
    "borderStyle": "black",
    "backgroundColor": "#ffffff",
    "list": [
      {
        "pagePath": "pages/home/index",
        "iconPath": "static/icons/home.png",
        "selectedIconPath": "static/icons/home-active.png",
        "text": "首页"
      },
      {
        "pagePath": "pages/user/index",
        "iconPath": "static/icons/user.png",
        "selectedIconPath": "static/icons/user-active.png",
        "text": "我的"
      }
    ]
  },
  "globalStyle": {
    "navigationBarTextStyle": "black",
    "navigationBarTitleText": "智评优选",
    "navigationBarBackgroundColor": "#ffffff",
    "backgroundColor": "#f5f5f5"
  }
}
```

---

## 🎯 三、开发阶段规划

### 📌 阶段一：项目基础架构搭建（预计 2-3 天）

#### 1.1 项目初始化

**任务清单**:
- [ ] **在 HBuilderX 中创建 UniApp 项目**
  - 选择 Vue 3 模板
  - 配置项目名称：智评优选
  - 选择编译到微信小程序、H5

- [ ] **配置 manifest.json**
  - 配置小程序 AppID（如已申请）
  - 配置 H5 开发服务器代理（指向后端 API）
  - 配置应用基本信息

- [ ] **配置 pages.json**
  - 定义页面路由
  - 配置 TabBar（首页、我的）
  - 配置全局样式

- [ ] **安装依赖（如使用 npm）**
  ```bash
  npm install pinia  # 状态管理
  # 如需 UI 组件库，可安装 uView UI 或 uni-ui
  ```

**预计时间**: 0.5 天

---

#### 1.2 HTTP 请求封装

**目标**: 参考 Vue 管理端的 `request.js`，封装 uni.request，统一处理 token、错误处理、请求拦截

**任务清单**:
- [ ] **创建 `utils/constants.js`**
  - 定义 API 基础 URL（开发/生产环境）
  - 定义其他常量

- [ ] **创建 `api/request.js`**
  - 封装 uni.request
  - 请求拦截器：自动添加 JWT token（从 uni.getStorageSync 读取）
  - 响应拦截器：统一处理错误（401 跳转登录、其他错误提示）
  - 支持 loading 状态（可选）

**代码示例** (`api/request.js`):
```javascript
/**
 * HTTP 请求封装（参考 Vue 管理端）
 * 统一处理 token、错误处理
 */

const BASE_URL = 'http://localhost:8080/api' // 开发环境，生产环境需配置

function request(options) {
  return new Promise((resolve, reject) => {
    // 从本地存储获取 token
    const token = uni.getStorageSync('token')
    
    // 请求头配置
    const header = {
      'Content-Type': 'application/json',
      ...options.header
    }
    
    // 如果有 token，添加到请求头
    if (token) {
      header.Authorization = `Bearer ${token}`
    }
    
    // 显示 loading（可选）
    if (options.loading !== false) {
      uni.showLoading({ title: '加载中...', mask: true })
    }
    
    // 发起请求
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: header,
      success: (res) => {
        // 隐藏 loading
        if (options.loading !== false) {
          uni.hideLoading()
        }
        
        // 处理响应
        if (res.statusCode === 200) {
          resolve(res.data)
        } else if (res.statusCode === 401) {
          // 401 未授权：清除 token，跳转登录
          uni.removeStorageSync('token')
          uni.removeStorageSync('userInfo')
          uni.reLaunch({ url: '/pages/user/login' })
          uni.showToast({ title: '登录已过期', icon: 'none' })
          reject(res)
        } else {
          // 其他错误
          const message = res.data?.message || `请求失败: ${res.statusCode}`
          uni.showToast({ title: message, icon: 'none' })
          reject(res)
        }
      },
      fail: (err) => {
        // 隐藏 loading
        if (options.loading !== false) {
          uni.hideLoading()
        }
        // 网络错误
        uni.showToast({ title: '网络错误，请稍后重试', icon: 'none' })
        reject(err)
      }
    })
  })
}

export default request
```

**预计时间**: 0.5 天

---

#### 1.3 API 接口封装

**目标**: 参考 Vue 管理端的 API 封装，创建所有业务接口

**任务清单**:
- [ ] **创建 `api/auth.js`** - 认证相关（登录、注册）
- [ ] **创建 `api/shops.js`** - 商家相关（列表、详情）
- [ ] **创建 `api/reviews.js`** - 点评相关（列表、创建、点赞、AI 草稿、AI 推荐）
- [ ] **创建 `api/orders.js`** - 订单相关（列表、创建）
- [ ] **创建 `api/tags.js`** - 标签相关（列表、用户标签绑定/查询）
- [ ] **创建 `api/users.js`** - 用户相关（当前用户信息、我的点评、我的订单）

**代码示例** (`api/reviews.js`):
```javascript
/**
 * 点评相关 API 接口封装
 * 参考 Vue 管理端的实现
 */

import request from './request'

/**
 * 获取商家点评列表（分页）
 */
export function listReviews(shopId, params = {}) {
  return request({
    url: `/shops/${shopId}/reviews`,
    method: 'get',
    params: {
      page: params.page || 0,
      size: params.size || 10
    }
  })
}

/**
 * 创建点评
 */
export function createReview(shopId, data) {
  return request({
    url: `/shops/${shopId}/reviews`,
    method: 'post',
    data
  })
}

/**
 * 点赞点评
 */
export function likeReview(shopId, reviewId) {
  return request({
    url: `/shops/${shopId}/reviews/${reviewId}/like`,
    method: 'post'
  })
}

/**
 * AI 生成点评草稿
 */
export function generateAiDraft(shopId, data = {}) {
  return request({
    url: `/shops/${shopId}/reviews/ai-draft`,
    method: 'post',
    data
  })
}

/**
 * AI 推荐点评
 * 返回格式：Array<{review: Object, reason: string}>
 */
export function recommendReviews(shopId, params = {}) {
  return request({
    url: `/shops/${shopId}/reviews/recommend`,
    method: 'get',
    params: {
      preference: params.preference,
      limit: params.limit || 3
    }
  })
}
```

**预计时间**: 1 天

---

#### 1.4 状态管理（Pinia）

**目标**: 配置 Pinia，管理用户认证状态和用户信息

**任务清单**:
- [ ] **安装 Pinia**（如未安装）
- [ ] **创建 `store/index.js`** - Pinia 实例
- [ ] **创建 `store/auth.js`** - 认证状态（token、登录状态）
- [ ] **创建 `store/user.js`** - 用户信息状态（用户资料、标签等）

**代码示例** (`store/auth.js`):
```javascript
import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: uni.getStorageSync('token') || null,
    isAuthenticated: !!uni.getStorageSync('token')
  }),
  
  actions: {
    setToken(token) {
      this.token = token
      this.isAuthenticated = !!token
      if (token) {
        uni.setStorageSync('token', token)
      } else {
        uni.removeStorageSync('token')
      }
    },
    
    logout() {
      this.token = null
      this.isAuthenticated = false
      uni.removeStorageSync('token')
      uni.removeStorageSync('userInfo')
    }
  }
})
```

**预计时间**: 0.5 天

---

#### 1.5 工具函数

**任务清单**:
- [ ] **创建 `utils/jwt.js`** - JWT 工具（token 解析、过期检查，参考 Vue 管理端）
- [ ] **创建 `utils/format.js`** - 格式化工具（日期、价格、评分等）
- [ ] **创建 `utils/constants.js`** - 常量定义

**预计时间**: 0.5 天

---

### 📌 阶段二：公共组件开发（预计 2-3 天）

#### 2.1 基础组件

**任务清单**:
- [ ] **商家卡片组件** (`components/shop-card.vue`)
  - 商家名称、分类、地址
  - 评分显示（星级）
  - 人均价格
  - 点击跳转商家详情

- [ ] **点评卡片组件** (`components/review-card.vue`)
  - 用户头像、昵称
  - 评分、内容、图片
  - 点赞数、点赞按钮
  - **AI 推荐标识**（如有推荐理由，显示特殊样式）
  - **推荐理由展示**（浅蓝色背景、AI 图标）

- [ ] **评分组件** (`components/rating-stars.vue`)
  - 星级显示（1-5 星）
  - 支持只读和可编辑两种模式

- [ ] **加载状态组件** (`components/loading.vue`)
  - 加载动画
  - 可配置文字提示

- [ ] **空状态组件** (`components/empty-state.vue`)
  - 空数据提示
  - 可配置图标、文字、按钮

- [ ] **AI 推荐标识组件** (`components/ai-recommend-badge.vue`)
  - AI 图标 + "AI 推荐"文字
  - 用于标记推荐点评

**预计时间**: 2 天

---

#### 2.2 业务组件

**任务清单**:
- [ ] **标签选择器组件** (`components/tag-selector.vue`)
  - 标签列表展示（多选）
  - 用于用户偏好设置页面

**预计时间**: 0.5 天

---

### 📌 阶段三：核心页面开发（预计 1-2 周）

#### 3.1 首页（优先级：高 ⭐⭐⭐）

**页面路径**: `pages/home/index.vue`

**功能需求**:
- 商家列表展示（分页加载）
- 搜索框（顶部固定）
- 分类筛选（Tab 切换）
- 下拉刷新
- 上拉加载更多
- 点击商家卡片跳转商家详情

**任务清单**:
- [ ] **页面布局**
  - 顶部搜索框
  - 分类筛选 Tab
  - 商家列表（使用 shop-card 组件）

- [ ] **数据加载**
  - 调用 `listShops` API
  - 分页加载（page、size）
  - 下拉刷新（onPullDownRefresh）
  - 上拉加载更多（onReachBottom）

- [ ] **搜索功能**
  - 输入关键词，调用 API 搜索
  - 防抖处理（避免频繁请求）

- [ ] **分类筛选**
  - Tab 切换，重新加载数据

**预计时间**: 2-3 天

---

#### 3.2 商家详情页（优先级：高 ⭐⭐⭐）

**页面路径**: `pages/shop/detail.vue`

**功能需求**:
- 商家基本信息展示（名称、分类、地址、评分、人均价格）
- **AI 推荐点评区域**（核心亮点）
  - 调用 `GET /api/shops/{shopId}/reviews/recommend` 接口
  - 展示推荐理由（特殊样式，浅蓝色背景、AI 图标）
  - 推荐理由可展开/收起（可选）
- 全部点评列表（分页）
- 创建点评入口（按钮，跳转创建点评页）
- 下拉刷新

**任务清单**:
- [ ] **商家信息展示**
  - 调用 `getShop` API 获取商家详情
  - 展示基本信息、标签（如有）

- [ ] **AI 推荐点评区域** ⭐
  - 调用 `recommendReviews` API
  - 使用 review-card 组件展示
  - 推荐理由特殊样式（浅蓝色背景、AI 图标）
  - 如果没有推荐结果，隐藏该区域

- [ ] **全部点评列表**
  - 调用 `listReviews` API
  - 分页加载
  - 使用 review-card 组件展示

- [ ] **创建点评入口**
  - 底部固定按钮（或导航栏右侧按钮）
  - 点击跳转 `pages/review/create?shopId=xxx`

**预计时间**: 3-4 天

---

#### 3.3 创建点评页（优先级：高 ⭐⭐⭐）

**页面路径**: `pages/review/create.vue`

**功能需求**:
- 表单：评分（必填）、内容（必填）、图片上传（可选）
- **AI 草稿生成按钮**（核心亮点）
  - 调用 `POST /api/shops/{shopId}/reviews/ai-draft` 接口
  - 草稿预览与编辑
  - 加载动画（生成中...）
  - 错误处理（生成失败提示）
- 提交点评（调用 `createReview` API）
- 图片上传（uni.chooseImage + 上传到服务器，或使用后端接口）

**任务清单**:
- [ ] **表单布局**
  - 评分选择（使用 rating-stars 组件，可编辑模式）
  - 内容输入框（textarea，多行）
  - 图片上传（最多 9 张）
  - 提交按钮

- [ ] **AI 草稿生成功能** ⭐
  - "AI 生成草稿"按钮
  - 点击后调用 `generateAiDraft` API
  - 显示加载状态（Loading 组件）
  - 成功后填充到内容输入框
  - 用户可编辑草稿内容
  - 错误处理（生成失败提示）

- [ ] **图片上传**
  - 使用 uni.chooseImage 选择图片
  - 图片预览（可删除）
  - 提交时上传到服务器（需后端支持图片上传接口，或使用云存储）

- [ ] **提交点评**
  - 表单校验（评分、内容必填）
  - 调用 `createReview` API
  - 提交成功后跳转商家详情页或返回上一页

**预计时间**: 3-4 天

---

#### 3.4 用户中心（优先级：高 ⭐⭐⭐）

**页面路径**: `pages/user/index.vue`

**功能需求**:
- 用户信息展示（头像、昵称、登录状态）
- 功能入口：
  - 我的订单
  - 我的点评
  - 偏好设置（标签管理）
  - 个人资料
- 未登录时显示登录/注册入口

**任务清单**:
- [ ] **页面布局**
  - 用户信息区域（头像、昵称）
  - 功能入口列表（订单、点评、偏好设置、个人资料）
  - 退出登录按钮（已登录时显示）

- [ ] **登录状态判断**
  - 检查 token 是否存在
  - 未登录显示登录/注册入口
  - 已登录显示用户信息和功能入口

**预计时间**: 1 天

---

#### 3.5 登录/注册页（优先级：高 ⭐⭐⭐）

**页面路径**: `pages/user/login.vue`、`pages/user/register.vue`

**功能需求**:
- 登录表单（手机号/邮箱、密码）
- 注册表单（手机号/邮箱、密码、确认密码）
- 调用后端 API（`/api/users/login`、`/api/users/register`）
- 登录成功后保存 token，跳转首页或返回上一页

**任务清单**:
- [ ] **登录页**
  - 表单：账号（手机号/邮箱）、密码
  - 调用 `login` API
  - 成功后保存 token 到本地存储
  - 更新 Pinia 状态
  - 跳转（如有 redirect 参数，跳转指定页面；否则跳转首页）

- [ ] **注册页**
  - 表单：账号、密码、确认密码
  - 表单校验（密码长度、确认密码匹配）
  - 调用 `register` API
  - 成功后自动登录（保存 token）

**预计时间**: 1-2 天

---

#### 3.6 我的订单页（优先级：中 ⭐⭐）

**页面路径**: `pages/user/orders.vue`

**功能需求**:
- 订单列表展示（调用 `listMyOrders` API）
- 订单信息：商家名称、消费金额、到店时间、消费项明细
- 下拉刷新
- 点击订单可查看详情（可选：订单详情页）

**预计时间**: 1-2 天

---

#### 3.7 我的点评页（优先级：中 ⭐⭐）

**页面路径**: `pages/user/reviews.vue`

**功能需求**:
- 点评列表展示（调用用户点评 API，需后端支持 `/api/users/me/reviews` 或使用 `/api/users/{id}/reviews`）
- 使用 review-card 组件展示
- 下拉刷新、上拉加载更多
- 点击点评可查看详情（可选：点评详情页）

**预计时间**: 1-2 天

---

#### 3.8 偏好设置页（优先级：中 ⭐⭐）

**页面路径**: `pages/user/tags.vue`

**功能需求**:
- 标签列表展示（调用 `listTags('user')` API，获取用户类型标签）
- 已选标签高亮显示
- 多选标签（使用 tag-selector 组件）
- 保存按钮（调用 `POST /api/users/me/tags` 接口，传递 tagIds 数组）

**任务清单**:
- [ ] **标签展示**
  - 调用 `listTags('user')` 获取所有用户标签
  - 调用 `GET /api/users/me/tags` 获取当前用户已选标签
  - 已选标签高亮显示

- [ ] **标签选择**
  - 使用 tag-selector 组件
  - 支持多选

- [ ] **保存标签**
  - 调用 `POST /api/users/me/tags` 接口
  - 传递 `{ tagIds: [1, 2, 3] }`
  - 保存成功后提示

**预计时间**: 1-2 天

---

### 📌 阶段四：功能优化与体验提升（预计 1 周）

#### 4.1 用户体验优化

**任务清单**:
- [ ] **加载状态优化**
  - 列表加载骨架屏
  - 按钮加载状态（防止重复提交）
  - AI 草稿生成加载动画

- [ ] **错误处理优化**
  - 网络错误友好提示
  - API 错误统一处理
  - 空状态提示优化

- [ ] **性能优化**
  - 图片懒加载
  - 列表虚拟滚动（大数据量时）
  - API 请求防抖/节流（搜索框）

- [ ] **交互优化**
  - AI 推荐理由动画效果（可选）
  - 推荐理由可展开/收起（长文本时）
  - 下拉刷新、上拉加载更多优化

**预计时间**: 2-3 天

---

#### 4.2 多端适配

**任务清单**:
- [ ] **微信小程序适配**
  - 测试小程序端功能
  - 适配小程序 API（如登录、支付等，如需要）
  - 配置小程序权限（如位置、相机等）

- [ ] **H5 适配**
  - 测试 H5 端功能
  - 路由模式配置（hash 模式）
  - 响应式布局优化

- [ ] **App 适配**（可选）
  - 测试 App 端功能
  - 原生插件配置（如需要）

**预计时间**: 2-3 天

---

### 📌 阶段五：测试与发布（预计 1 周）

#### 5.1 功能测试

**任务清单**:
- [ ] **核心功能测试**
  - 登录/注册流程
  - 商家浏览、搜索、筛选
  - 商家详情、AI 推荐点评展示
  - 创建点评、AI 草稿生成
  - 用户中心、偏好设置

- [ ] **边界情况测试**
  - 网络错误处理
  - 空数据展示
  - 未登录状态访问受保护页面
  - Token 过期处理

- [ ] **兼容性测试**
  - 不同设备测试（iOS、Android）
  - 不同微信版本测试（小程序）
  - 不同浏览器测试（H5）

**预计时间**: 2-3 天

---

#### 5.2 发布准备

**任务清单**:
- [ ] **代码优化**
  - 代码审查
  - 删除调试代码
  - 优化打包体积

- [ ] **配置生产环境**
  - 配置生产环境 API 基础 URL
  - 配置小程序 AppID（如已申请）
  - 配置 H5 域名白名单

- [ ] **小程序发布**
  - 上传代码到微信开发者工具
  - 提交审核
  - 发布上线

- [ ] **H5 发布**
  - 构建 H5 版本
  - 部署到服务器（Nginx 配置）
  - 配置域名和 HTTPS

**预计时间**: 2-3 天

---

## 📊 四、开发优先级与时间表

### 4.1 优先级排序

**第一优先级（核心功能，必须完成）**:
1. ✅ 项目基础架构搭建（HTTP 封装、API 封装、状态管理）
2. ✅ 公共组件开发（商家卡片、点评卡片、评分组件）
3. ✅ 首页（商家列表、搜索、筛选）
4. ✅ 商家详情页（AI 推荐点评展示）
5. ✅ 创建点评页（AI 草稿生成）
6. ✅ 用户中心、登录/注册

**第二优先级（重要功能，建议完成）**:
7. ⭐ 我的订单页
8. ⭐ 我的点评页
9. ⭐ 偏好设置页（标签管理）

**第三优先级（优化功能，可选）**:
10. ⭐ 性能优化、用户体验优化
11. ⭐ 多端适配优化
12. ⭐ 测试与发布

---

### 4.2 时间估算

| 阶段 | 任务 | 预计时间 |
|------|------|----------|
| 阶段一 | 项目基础架构搭建 | 2-3 天 |
| 阶段二 | 公共组件开发 | 2-3 天 |
| 阶段三 | 核心页面开发 | 1-2 周 |
| 阶段四 | 功能优化与体验提升 | 1 周 |
| 阶段五 | 测试与发布 | 1 周 |
| **总计** | | **3-4 周** |

---

## 🔧 五、技术要点与注意事项

### 5.1 API 接口对接

**参考后端接口文档**:
- 所有接口路径以 `/api` 开头
- 认证接口：`POST /api/users/login`、`POST /api/users/register`
- 商家接口：`GET /api/shops`、`GET /api/shops/{id}`
- 点评接口：`GET /api/shops/{shopId}/reviews`、`POST /api/shops/{shopId}/reviews`
- AI 接口：`POST /api/shops/{shopId}/reviews/ai-draft`、`GET /api/shops/{shopId}/reviews/recommend`
- 标签接口：`GET /api/tags`、`GET /api/users/me/tags`、`POST /api/users/me/tags`
- 订单接口：`GET /api/users/me/orders`、`POST /api/shops/{shopId}/orders`

**注意事项**:
- 所有需要认证的接口，请求头需携带 `Authorization: Bearer <token>`
- 401 错误需自动跳转登录页
- API 返回格式统一（参考 Vue 管理端的 API 封装）

---

### 5.2 状态管理

**推荐使用 Pinia**（Vue 3 官方推荐）:
- 管理用户认证状态（token、登录状态）
- 管理用户信息（用户资料、标签等）
- 管理全局配置（如 API 基础 URL）

**注意事项**:
- Token 需持久化到本地存储（uni.setStorageSync）
- 页面刷新时从本地存储恢复状态

---

### 5.3 路由与导航

**UniApp 路由特点**:
- 使用 `pages.json` 配置页面路由
- 使用 `uni.navigateTo`、`uni.redirectTo`、`uni.reLaunch` 进行页面跳转
- 不支持 Vue Router，需使用 uni-app 原生路由

**注意事项**:
- 页面路径需在 `pages.json` 中注册
- TabBar 页面需在 `pages.json` 的 `tabBar` 中配置
- 路由参数通过 `onLoad` 生命周期接收

---

### 5.4 多端适配

**平台差异处理**:
- 使用条件编译（`#ifdef`、`#endif`）处理平台差异
- 小程序端：使用 `wx.xxx` API（如 `wx.login`、`wx.getUserInfo`）
- H5 端：可使用部分浏览器 API
- App 端：可使用 uni-app 原生插件

**注意事项**:
- 图片路径：使用相对路径或网络路径，避免使用绝对路径
- 样式单位：使用 `rpx`（响应式像素），小程序和 H5 会自动转换
- API 兼容性：部分 API 在不同平台表现不同，需测试

---

### 5.5 AI 功能集成

**AI 草稿生成**:
- 接口：`POST /api/shops/{shopId}/reviews/ai-draft`
- 请求参数：`{ preference: "可选偏好关键词" }`
- 响应：`{ draft: "生成的草稿文本" }`
- 需显示加载状态，处理错误情况

**AI 推荐点评**:
- 接口：`GET /api/shops/{shopId}/reviews/recommend?limit=3`
- 响应：`Array<{review: Object, reason: string}>`
- 需特殊样式展示推荐理由（浅蓝色背景、AI 图标）
- 推荐理由可展开/收起（长文本时）

---

## 📝 六、开发检查清单

### 6.1 基础架构检查

- [ ] 项目已在 HBuilderX 中创建并配置
- [ ] `manifest.json` 配置完成（小程序 AppID、H5 代理等）
- [ ] `pages.json` 配置完成（页面路由、TabBar）
- [ ] HTTP 请求封装完成（`api/request.js`）
- [ ] 所有 API 接口封装完成（`api/*.js`）
- [ ] 状态管理配置完成（Pinia）
- [ ] 工具函数完成（JWT、格式化等）

---

### 6.2 组件检查

- [ ] 商家卡片组件完成
- [ ] 点评卡片组件完成（含 AI 推荐标识）
- [ ] 评分组件完成
- [ ] 加载状态组件完成
- [ ] 空状态组件完成
- [ ] AI 推荐标识组件完成
- [ ] 标签选择器组件完成

---

### 6.3 页面检查

- [ ] 首页完成（商家列表、搜索、筛选）
- [ ] 商家详情页完成（AI 推荐点评展示）
- [ ] 创建点评页完成（AI 草稿生成）
- [ ] 用户中心完成
- [ ] 登录/注册页完成
- [ ] 我的订单页完成
- [ ] 我的点评页完成
- [ ] 偏好设置页完成

---

### 6.4 功能检查

- [ ] 登录/注册功能正常
- [ ] 商家浏览、搜索、筛选功能正常
- [ ] AI 推荐点评展示正常（推荐理由样式正确）
- [ ] AI 草稿生成功能正常（加载状态、错误处理）
- [ ] 创建点评功能正常（表单校验、图片上传）
- [ ] 用户中心功能正常（订单、点评、偏好设置）
- [ ] 下拉刷新、上拉加载更多正常
- [ ] 错误处理正常（网络错误、401 跳转登录等）

---

### 6.5 测试检查

- [ ] 核心功能测试通过
- [ ] 边界情况测试通过
- [ ] 兼容性测试通过（小程序、H5）
- [ ] 性能测试通过（加载速度、内存占用）
- [ ] 代码审查通过

---

## 🚀 七、下一步行动

### 立即开始（今天）

1. **在 HBuilderX 中创建 UniApp 项目**
   - 选择 Vue 3 模板
   - 配置项目名称：智评优选

2. **配置基础文件**
   - `manifest.json`（小程序 AppID、H5 代理）
   - `pages.json`（页面路由、TabBar）

3. **创建 HTTP 请求封装**
   - `api/request.js`（参考 Vue 管理端）

---

### 本周目标（第 1 周）

1. ✅ 完成阶段一：项目基础架构搭建
2. ✅ 完成阶段二：公共组件开发（核心组件）
3. ✅ 开始阶段三：核心页面开发（首页、商家详情页）

---

### 下周目标（第 2 周）

1. ✅ 完成阶段三：核心页面开发（创建点评页、用户中心、登录/注册）
2. ✅ 开始阶段四：功能优化

---

## 📚 八、参考资源

### 8.1 项目内参考

- **Vue 管理端代码**：`Vue/src/` - 参考 API 封装、组件实现、页面结构
- **后端 API 文档**：`Java/API测试用例-*.md` - 了解接口详细定义
- **项目进度报告**：`项目进度报告与下一步计划.md` - 了解项目整体情况

### 8.2 外部资源

- **UniApp 官方文档**：https://uniapp.dcloud.net.cn/
- **Vue 3 文档**：https://cn.vuejs.org/
- **Pinia 文档**：https://pinia.vuejs.org/zh/
- **uView UI 文档**：https://www.uviewui.com/（如使用）

---

## 📌 九、开发注意事项

### 9.1 代码规范

- 使用 Vue 3 Composition API（`<script setup>`）
- 组件命名使用 PascalCase
- 文件命名使用 kebab-case
- API 函数命名使用 camelCase
- 代码注释完善（参考 Vue 管理端）

### 9.2 错误处理

- 所有 API 调用需有错误处理（try-catch）
- 网络错误需友好提示
- 401 错误需自动跳转登录
- 空数据需显示空状态组件

### 9.3 性能优化

- 图片使用懒加载
- 列表使用分页加载
- API 请求使用防抖/节流
- 避免不必要的重复请求

### 9.4 用户体验

- 加载状态需明确提示
- 操作反馈需及时（成功/失败提示）
- 页面跳转需流畅
- AI 功能需有加载动画

---

**计划制定时间**: 2026-1-10  
**预计完成时间**: 3-4 周  
**下一步**: 立即在 HBuilderX 中创建项目，开始阶段一的开发

