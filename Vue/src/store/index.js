/**
 * Vuex Store 主入口文件
 * 
 * 功能说明：
 * 1. 创建 Vuex store 实例
 * 2. 注册所有模块（modules）
 * 3. 统一管理应用的状态
 */

import { createStore } from 'vuex'
import auth from './auth' // 导入认证模块

/**
 * 创建并导出 Vuex store 实例
 * 
 * modules: 注册所有状态管理模块
 * - auth: 认证相关状态（token、用户信息等）
 * 
 * 后续可以继续添加其他模块：
 * - shops: 商家管理状态
 * - reviews: 点评管理状态
 * - orders: 订单管理状态
 * 等等
 */
export default createStore({
  modules: {
    auth // 注册认证模块，使用时通过 this.$store.dispatch('auth/login') 访问
  }
})

