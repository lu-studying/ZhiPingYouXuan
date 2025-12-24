/**
 * Vue 应用主入口文件
 * 
 * 功能说明：
 * 1. 创建 Vue 应用实例
 * 2. 注册全局插件（Vue Router、Vuex、Element Plus）
 * 3. 注册 Element Plus 图标组件
 * 4. 挂载应用到 DOM
 */

import { createApp } from 'vue'
import App from './App.vue' // 根组件
import router from './router' // 路由配置
import store from './store' // 状态管理

// Element Plus UI 组件库
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css' // Element Plus 样式文件
import * as ElementPlusIconsVue from '@element-plus/icons-vue' // Element Plus 图标库

// 创建 Vue 应用实例
const app = createApp(App)

/**
 * 注册所有 Element Plus 图标组件
 * 
 * 说明：
 * - Element Plus 的图标需要单独注册才能使用
 * - 遍历图标库，将所有图标注册为全局组件
 * - 使用方式：<el-icon><User /></el-icon>
 */
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 注册 Vuex 状态管理（用于管理全局状态，如用户登录信息）
app.use(store)

// 注册 Vue Router（用于页面路由导航）
app.use(router)

// 注册 Element Plus UI 组件库（提供丰富的 UI 组件）
app.use(ElementPlus)

// 将应用挂载到 DOM 元素上（#app 对应 public/index.html 中的 div#app）
app.mount('#app')

