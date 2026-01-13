/**
 * HTTP 请求封装（参考 Vue 管理端）
 * 统一处理 token、错误处理
 * 
 * 功能说明：
 * 1. 封装 uni.request，统一处理请求和响应
 * 2. 自动添加 JWT token 到请求头
 * 3. 统一处理错误（401 跳转登录、其他错误提示）
 * 4. 支持 loading 状态控制
 */

import { BASE_URL, REQUEST_TIMEOUT, STORAGE_KEY_TOKEN, STORAGE_KEY_USER_INFO } from '@/utils/constants'

/**
 * 发起 HTTP 请求
 * 
 * @param {Object} options - 请求配置
 * @param {string} options.url - 请求路径（相对路径，会自动拼接 BASE_URL）
 * @param {string} options.method - 请求方法（GET、POST、PUT、DELETE 等）
 * @param {Object} options.data - 请求数据（POST、PUT 请求使用）
 * @param {Object} options.params - 查询参数（GET 请求使用，会自动转换为 URL 参数）
 * @param {Object} options.header - 自定义请求头
 * @param {boolean} options.loading - 是否显示 loading（默认 true）
 * @returns {Promise} 返回 Promise，成功时 resolve 响应数据，失败时 reject 错误信息
 * 
 * @example
 * // GET 请求
 * request({
 *   url: '/shops',
 *   method: 'get',
 *   params: { page: 0, size: 10 }
 * })
 * 
 * @example
 * // POST 请求
 * request({
 *   url: '/shops/1/reviews',
 *   method: 'post',
 *   data: { rating: 5, content: '很好吃' }
 * })
 */
function request(options) {
  return new Promise((resolve, reject) => {
    // 从本地存储获取 token
    const token = uni.getStorageSync(STORAGE_KEY_TOKEN)
    
    // 构建完整 URL
    let url = BASE_URL + options.url
    
    // 调试日志：打印实际请求 URL
    console.log('API 请求 URL:', url)
    console.log('BASE_URL:', BASE_URL)
    console.log('options.url:', options.url)
    
    // 处理 GET 请求的查询参数
    if (options.params && Object.keys(options.params).length > 0) {
      const params = new URLSearchParams()
      Object.keys(options.params).forEach(key => {
        if (options.params[key] !== null && options.params[key] !== undefined) {
          params.append(key, options.params[key])
        }
      })
      const queryString = params.toString()
      if (queryString) {
        url += (url.includes('?') ? '&' : '?') + queryString
      }
    }
    
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
      uni.showLoading({ 
        title: '加载中...', 
        mask: true 
      })
    }
    
    // 发起请求
    uni.request({
      url: url,
      method: options.method || 'GET',
      data: options.data,
      header: header,
      timeout: REQUEST_TIMEOUT,
      success: (res) => {
        // 隐藏 loading
        if (options.loading !== false) {
          uni.hideLoading()
        }
        
        // 处理响应
        if (res.statusCode === 200) {
          // 请求成功，返回数据
          resolve(res.data)
        } else if (res.statusCode === 401) {
          // 401 未授权：清除 token，跳转登录
          uni.removeStorageSync(STORAGE_KEY_TOKEN)
          uni.removeStorageSync(STORAGE_KEY_USER_INFO)
          
          // 跳转到登录页
          uni.reLaunch({ 
            url: '/pages/user/login' 
          })
          
          uni.showToast({ 
            title: '登录已过期，请重新登录', 
            icon: 'none',
            duration: 2000
          })
          
          reject(res)
        } else {
          // 其他错误状态码（400, 403, 404, 500 等）
          const message = res.data?.message || `请求失败: ${res.statusCode}`
          uni.showToast({ 
            title: message, 
            icon: 'none',
            duration: 2000
          })
          reject(res)
        }
      },
      fail: (err) => {
        // 隐藏 loading
        if (options.loading !== false) {
          uni.hideLoading()
        }
        
        // 网络错误或其他错误（如请求超时、服务器无响应等）
        uni.showToast({ 
          title: '网络错误，请稍后重试', 
          icon: 'none',
          duration: 2000
        })
        
        console.error('请求失败:', err)
        reject(err)
      }
    })
  })
}

export default request

