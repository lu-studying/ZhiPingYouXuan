/**
 * JWT Token 工具函数
 * 
 * 功能说明：
 * 1. 解析 JWT token，提取 payload 信息
 * 2. 检查 token 是否过期
 * 3. 获取 token 的过期时间
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

    // 使用 atob 解码 base64 字符串，然后解析 JSON
    const decoded = JSON.parse(atob(base64))
    
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

