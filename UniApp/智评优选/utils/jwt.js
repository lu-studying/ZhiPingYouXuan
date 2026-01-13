/**
 * JWT Token 工具函数
 * 
 * 功能说明：
 * 1. 解析 JWT token，提取 payload 信息
 * 2. 检查 token 是否过期
 * 3. 获取 token 的过期时间
 * 
 * 注意：UniApp 环境使用 uni.getStorageSync，而不是 localStorage
 */

/**
 * 解析 JWT token
 * 
 * JWT token 格式：header.payload.signature
 * 每个部分都是 base64url 编码的 JSON 字符串
 * 
 * @param {string} token - JWT token 字符串
 * @returns {Object|null} 解析后的 payload 对象，如果解析失败返回 null
 * 
 * @example
 * const payload = parseToken('eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...')
 * console.log(payload.exp) // 过期时间戳
 */
export function parseToken(token) {
  if (!token) {
    return null
  }

  try {
    // JWT token 由三部分组成，用 . 分隔：header.payload.signature
    const parts = token.split('.')
    
    // 如果格式不正确（不是三部分），返回 null
    if (parts.length !== 3) {
      return null
    }

    // 解析 payload（第二部分）
    // base64url 解码：需要处理 - 和 _ 字符，并补齐 padding
    let base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/')
    
    // 补齐 base64 padding（= 字符）
    while (base64.length % 4) {
      base64 += '='
    }

    // 使用 base64 解码
    // H5 环境使用 atob，小程序和 App 环境需要手动实现或使用 polyfill
    let decodedStr
    // #ifdef H5
    decodedStr = atob(base64)
    // #endif
    
    // #ifndef H5
    // 小程序和 App 环境：手动实现 base64 解码
    // 这里使用简单的实现方式
    try {
      // 尝试使用全局 atob（如果存在，可能是 polyfill）
      if (typeof atob !== 'undefined') {
        decodedStr = atob(base64)
      } else {
        // 手动实现 base64 解码（简化版）
        const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/='
        let output = ''
        let i = 0
        base64 = base64.replace(/[^A-Za-z0-9\+\/\=]/g, '')
        while (i < base64.length) {
          const enc1 = chars.indexOf(base64.charAt(i++))
          const enc2 = chars.indexOf(base64.charAt(i++))
          const enc3 = chars.indexOf(base64.charAt(i++))
          const enc4 = chars.indexOf(base64.charAt(i++))
          const chr1 = (enc1 << 2) | (enc2 >> 4)
          const chr2 = ((enc2 & 15) << 4) | (enc3 >> 2)
          const chr3 = ((enc3 & 3) << 6) | enc4
          output += String.fromCharCode(chr1)
          if (enc3 !== 64) output += String.fromCharCode(chr2)
          if (enc4 !== 64) output += String.fromCharCode(chr3)
        }
        decodedStr = output
      }
    } catch (e) {
      console.error('Base64 解码失败:', e)
      return null
    }
    // #endif
    
    const decoded = JSON.parse(decodedStr)
    
    return decoded
  } catch (error) {
    // 解析失败（格式错误、base64 解码失败、JSON 解析失败等）
    console.error('解析 JWT token 失败:', error)
    return null
  }
}

/**
 * 检查 token 是否过期
 * 
 * @param {string} token - JWT token 字符串
 * @returns {boolean} true 表示已过期，false 表示未过期或无法判断
 * 
 * @example
 * if (isTokenExpired(token)) {
 *   console.log('Token 已过期')
 * }
 */
export function isTokenExpired(token) {
  const payload = parseToken(token)
  
  // 如果解析失败，认为已过期（安全起见）
  if (!payload) {
    return true
  }

  // 检查是否有 exp（过期时间戳）字段
  if (!payload.exp) {
    // 如果没有 exp 字段，无法判断是否过期
    // 为了安全，认为已过期
    return true
  }

  // exp 是 Unix 时间戳（秒），需要转换为毫秒与当前时间比较
  const expirationTime = payload.exp * 1000
  const currentTime = Date.now()

  // 如果当前时间大于过期时间，说明已过期
  return currentTime >= expirationTime
}

/**
 * 获取 token 的过期时间
 * 
 * @param {string} token - JWT token 字符串
 * @returns {Date|null} 过期时间 Date 对象，如果无法解析返回 null
 * 
 * @example
 * const expDate = getTokenExpiration(token)
 * if (expDate) {
 *   console.log('Token 将在', expDate.toLocaleString(), '过期')
 * }
 */
export function getTokenExpiration(token) {
  const payload = parseToken(token)
  
  if (!payload || !payload.exp) {
    return null
  }

  // exp 是 Unix 时间戳（秒），转换为 Date 对象
  return new Date(payload.exp * 1000)
}

/**
 * 获取 token 的剩余有效时间（毫秒）
 * 
 * @param {string} token - JWT token 字符串
 * @returns {number|null} 剩余有效时间（毫秒），如果已过期或无法解析返回 null
 * 
 * @example
 * const remaining = getTokenRemainingTime(token)
 * if (remaining) {
 *   console.log('Token 还有', remaining, '毫秒过期')
 * }
 */
export function getTokenRemainingTime(token) {
  const payload = parseToken(token)
  
  if (!payload || !payload.exp) {
    return null
  }

  const expirationTime = payload.exp * 1000
  const currentTime = Date.now()
  const remaining = expirationTime - currentTime

  // 如果已过期，返回 0
  return remaining > 0 ? remaining : 0
}

