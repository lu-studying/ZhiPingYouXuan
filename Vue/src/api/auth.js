/**
 * 认证相关 API 接口封装
 * 
 * 说明：
 * 本模块封装了所有与用户认证相关的 API 调用
 * 包括登录、注册、退出登录等功能
 */

import request from '@/utils/request'

/**
 * 用户登录接口
 * 
 * @param {string} mobileOrEmail - 手机号或邮箱（后端会自动识别格式）
 * @param {string} password - 用户密码（明文，后端会进行哈希校验）
 * @returns {Promise<{token: string}>} 返回 Promise，成功时包含 token 字段
 * 
 * @example
 * // 使用示例
 * login('13800138000', '123456')
 *   .then(res => {
 *     console.log('登录成功，token:', res.token)
 *   })
 *   .catch(err => {
 *     console.error('登录失败:', err)
 *   })
 */
export function login(mobileOrEmail, password) {
  return request({
    url: '/users/login', // 后端登录接口路径
    method: 'post', // POST 请求
    data: {
      mobileOrEmail, // 账号（手机号或邮箱）
      password // 密码
    }
  })
}

/**
 * 用户注册接口
 * 
 * @param {string} mobileOrEmail - 手机号或邮箱
 * @param {string} password - 用户密码（至少6位）
 * @returns {Promise<{token: string}>} 返回 Promise，成功时包含 token 字段
 * 
 * @example
 * // 使用示例
 * register('13800138000', '123456')
 *   .then(res => {
 *     console.log('注册成功，token:', res.token)
 *   })
 */
export function register(mobileOrEmail, password) {
  return request({
    url: '/users/register', // 后端注册接口路径
    method: 'post', // POST 请求
    data: {
      mobileOrEmail, // 账号（手机号或邮箱）
      password // 密码
    }
  })
}

/**
 * 退出登录
 * 
 * 功能说明：
 * 清除本地存储的 token 和用户信息
 * 注意：此函数只清除本地数据，不调用后端接口
 * 
 * @example
 * // 使用示例
 * logout() // 清除本地登录信息
 */
export function logout() {
  // 清除本地存储的 token（JWT 令牌）
  localStorage.removeItem('token')
  // 清除本地存储的用户信息
  localStorage.removeItem('userInfo')
}

