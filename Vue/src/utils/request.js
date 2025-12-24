/**
 * HTTP 请求封装模块
 * 
 * 功能说明：
 * 1. 统一配置 axios 实例（baseURL、超时时间等）
 * 2. 请求拦截器：自动在请求头中添加 JWT token
 * 3. 响应拦截器：统一处理响应数据和错误信息
 * 4. 自动处理 401 未授权错误，跳转到登录页
 */

import axios from 'axios'
import { ElMessage } from 'element-plus'

/**
 * 创建 axios 实例
 * 
 * 配置说明：
 * - baseURL: 后端 API 的基础路径，所有请求都会自动拼接此路径
 * - timeout: 请求超时时间（10秒），超过此时间未响应则自动取消请求
 * - headers: 默认请求头，设置为 JSON 格式
 */
/**
 * 创建 axios 实例
 * 
 * 配置说明：
 * - baseURL: 后端 API 的基础路径
 *   - 开发环境（使用 Nginx）：使用相对路径 '/api'，由 Nginx 代理到后端
 *   - 开发环境（直接访问）：使用 'http://localhost:8080/api'
 *   - 生产环境：根据实际部署环境修改
 * - timeout: 请求超时时间（10秒），超过此时间未响应则自动取消请求
 * - headers: 默认请求头，设置为 JSON 格式
 */
const request = axios.create({
  // 使用相对路径，由 Nginx 代理到后端（开发/生产环境统一）
  // 如果直接访问后端，可以改为：baseURL: 'http://localhost:8080/api'
  baseURL: '/api', // 相对路径，通过 Nginx 代理到后端
  timeout: 10000, // 请求超时时间（毫秒）
  headers: {
    'Content-Type': 'application/json' // 默认请求头为 JSON 格式
  }
})

/**
 * 请求拦截器
 * 
 * 作用：在发送请求之前统一处理请求配置
 * 主要功能：
 * 1. 从 localStorage 中读取 token
 * 2. 如果存在 token，自动添加到请求头的 Authorization 字段
 * 3. 格式：Authorization: Bearer <token>
 */
request.interceptors.request.use(
  config => {
    // 从 localStorage 获取 token（登录时保存的 JWT token）
    const token = localStorage.getItem('token')
    if (token) {
      // 将 token 添加到请求头中，后端可以通过此字段验证用户身份
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    // 请求配置错误时的处理（通常不会发生）
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 * 
 * 作用：统一处理服务器返回的响应数据
 * 主要功能：
 * 1. 成功响应：直接返回 response.data（去掉 axios 包装）
 * 2. 错误响应：根据状态码进行不同处理
 *    - 401: 未授权，清除 token 并跳转到登录页
 *    - 其他: 显示错误提示信息
 * 3. 网络错误：显示网络错误提示
 */
request.interceptors.response.use(
  response => {
    // 响应成功，直接返回数据部分（去掉 axios 的包装层）
    // 这样调用 API 时可以直接使用返回的数据，无需再访问 response.data
    return response.data
  },
  error => {
    // 处理错误响应
    if (error.response) {
      // 服务器返回了错误响应（状态码不是 2xx）
      const { status, data } = error.response
      
      // 401 未授权：token 无效或已过期
      if (status === 401) {
        // 清除本地存储的 token 和用户信息
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        
        // 如果当前不在登录页，则跳转到登录页
        // 避免在登录页重复跳转
        if (window.location.pathname !== '/login') {
          window.location.href = '/login'
        }
        
        // 显示错误提示
        ElMessage.error(data?.message || '登录已过期，请重新登录')
      } else {
        // 其他错误状态码（400, 403, 404, 500 等）
        // 显示服务器返回的错误信息，如果没有则显示状态码
        const message = data?.message || `请求失败: ${status}`
        ElMessage.error(message)
      }
    } else {
      // 网络错误或其他错误（如请求超时、服务器无响应等）
      ElMessage.error(error.message || '网络错误，请稍后重试')
    }
    
    // 将错误继续抛出，让调用方可以捕获处理
    return Promise.reject(error)
  }
)

// 导出配置好的 axios 实例，供其他模块使用
export default request

