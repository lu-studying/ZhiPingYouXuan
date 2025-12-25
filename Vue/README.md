# 智评优选管理后台

Vue 3 + Element Plus 构建的管理后台系统

## 技术栈

- Vue 3 (Composition API)
- Vue Router 4
- Vuex 4
- Element Plus
- Axios
- Vite

## 项目结构

```
Vue/
├── public/              # 静态资源
├── src/
│   ├── api/            # API 接口封装
│   ├── components/     # 公共组件
│   ├── layouts/        # 布局组件
│   ├── router/         # 路由配置
│   ├── store/          # 状态管理
│   ├── styles/         # 样式文件
│   ├── utils/          # 工具函数
│   ├── views/          # 页面组件
│   ├── App.vue         # 根组件
│   └── main.js         # 入口文件
├── package.json
└── vite.config.js      # Vite 配置
```

## 安装依赖

```bash
npm install
```

## 开发

```bash
npm run dev
```

访问 http://localhost:3000

## 构建

```bash
npm run build
```

## 环境配置

后端 API 地址配置在 `src/utils/request.js` 中，默认指向 `http://localhost:8080/api`

如需修改，请编辑 `vite.config.js` 中的 proxy 配置或 `src/utils/request.js` 中的 baseURL

