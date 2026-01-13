# 智评优选 UniApp 用户端

## 项目简介

智评优选用户端是基于 UniApp 开发的跨平台应用，支持微信小程序、H5、App 等多端发布。

## 技术栈

- **框架**: UniApp (Vue 3 Composition API)
- **状态管理**: Pinia
- **HTTP 请求**: uni.request 封装
- **后端 API**: Java Spring Boot

## 项目结构

```
智评优选/
├── api/              # API 接口封装
├── components/       # 公共组件
├── pages/            # 页面
├── store/            # Pinia 状态管理
├── utils/            # 工具函数
├── static/           # 静态资源
├── manifest.json     # 应用配置
├── pages.json        # 页面路由配置
└── main.js           # 入口文件
```

## 开发环境配置

### 1. 安装依赖

```bash
npm install
```

### 2. 配置 API 地址

编辑 `utils/constants.js`，修改 `BASE_URL` 为实际的后端 API 地址：

```javascript
export const BASE_URL = 'http://localhost:8080/api'  // 开发环境
```

### 3. 配置小程序 AppID

编辑 `manifest.json`，在 `mp-weixin` 中配置小程序 AppID：

```json
{
  "mp-weixin": {
    "appid": "your-miniprogram-appid"
  }
}
```

## 运行项目

### H5 开发

在 HBuilderX 中：
1. 选择项目
2. 运行 -> 运行到浏览器 -> Chrome

### 微信小程序开发

1. 在 HBuilderX 中：运行 -> 运行到小程序模拟器 -> 微信开发者工具
2. 或使用微信开发者工具打开 `unpackage/dist/dev/mp-weixin` 目录

## 功能模块

### 已完成

- ✅ 项目基础架构（HTTP 封装、API 封装、状态管理）
- ✅ 工具函数（JWT、格式化等）
- ✅ 页面路由配置
- ✅ 基础页面结构

### 待开发

- ⏳ 公共组件（商家卡片、点评卡片、评分组件等）
- ⏳ 首页（商家列表、搜索、筛选）
- ⏳ 商家详情页（AI 推荐点评展示）
- ⏳ 创建点评页（AI 草稿生成）
- ⏳ 用户中心、登录/注册
- ⏳ 我的订单、我的点评、偏好设置

## 开发计划

详细开发计划请参考：`UniApp开发计划.md`

## 注意事项

1. **API 接口**: 所有接口路径以 `/api` 开头
2. **Token 管理**: Token 存储在本地，通过 `uni.getStorageSync` 和 `uni.setStorageSync` 管理
3. **多端适配**: 使用条件编译（`#ifdef`、`#endif`）处理平台差异
4. **路由**: 使用 `pages.json` 配置路由，使用 `uni.navigateTo` 进行页面跳转

## 参考文档

- [UniApp 官方文档](https://uniapp.dcloud.net.cn/)
- [Vue 3 文档](https://cn.vuejs.org/)
- [Pinia 文档](https://pinia.vuejs.org/zh/)

