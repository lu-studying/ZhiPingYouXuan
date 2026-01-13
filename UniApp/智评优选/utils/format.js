/**
 * 格式化工具函数
 * 
 * 功能说明：
 * 提供日期、价格、评分等数据的格式化函数
 */

/**
 * 格式化日期
 * 
 * @param {string|Date} date - 日期字符串或 Date 对象
 * @param {string} format - 格式化模板（默认：'YYYY-MM-DD HH:mm:ss'）
 * @returns {string} 格式化后的日期字符串
 * 
 * @example
 * formatDate('2025-12-15T10:00:00') // '2025-12-15 10:00:00'
 * formatDate(new Date(), 'YYYY-MM-DD') // '2025-12-15'
 */
export function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) {
    return ''
  }
  
  const d = new Date(date)
  if (isNaN(d.getTime())) {
    return ''
  }
  
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 格式化相对时间（如：刚刚、5分钟前、2小时前）
 * 
 * @param {string|Date} date - 日期字符串或 Date 对象
 * @returns {string} 相对时间字符串
 * 
 * @example
 * formatRelativeTime('2025-12-15T10:00:00') // '刚刚' 或 '5分钟前'
 */
export function formatRelativeTime(date) {
  if (!date) {
    return ''
  }
  
  const d = new Date(date)
  if (isNaN(d.getTime())) {
    return ''
  }
  
  const now = new Date()
  const diff = now.getTime() - d.getTime()
  
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  const month = 30 * day
  const year = 365 * day
  
  if (diff < minute) {
    return '刚刚'
  } else if (diff < hour) {
    return `${Math.floor(diff / minute)}分钟前`
  } else if (diff < day) {
    return `${Math.floor(diff / hour)}小时前`
  } else if (diff < month) {
    return `${Math.floor(diff / day)}天前`
  } else if (diff < year) {
    return `${Math.floor(diff / month)}个月前`
  } else {
    return `${Math.floor(diff / year)}年前`
  }
}

/**
 * 格式化价格
 * 
 * @param {number} price - 价格（元）
 * @returns {string} 格式化后的价格字符串
 * 
 * @example
 * formatPrice(128.5) // '¥128.5'
 * formatPrice(0) // '免费'
 */
export function formatPrice(price) {
  if (price === null || price === undefined) {
    return '价格未知'
  }
  
  if (price === 0) {
    return '免费'
  }
  
  return `¥${price}`
}

/**
 * 格式化人均价格
 * 
 * @param {number} price - 人均价格（元）
 * @returns {string} 格式化后的人均价格字符串
 * 
 * @example
 * formatAvgPrice(128) // '人均¥128'
 */
export function formatAvgPrice(price) {
  if (price === null || price === undefined) {
    return '价格未知'
  }
  
  return `人均${formatPrice(price)}`
}

/**
 * 格式化评分（保留一位小数）
 * 
 * @param {number} rating - 评分（1-5）
 * @returns {string} 格式化后的评分字符串
 * 
 * @example
 * formatRating(4.5) // '4.5'
 * formatRating(5) // '5.0'
 */
export function formatRating(rating) {
  if (rating === null || rating === undefined) {
    return '0.0'
  }
  
  return rating.toFixed(1)
}

/**
 * 格式化数字（添加千分位）
 * 
 * @param {number} num - 数字
 * @returns {string} 格式化后的数字字符串
 * 
 * @example
 * formatNumber(1234) // '1,234'
 * formatNumber(1234567) // '1,234,567'
 */
export function formatNumber(num) {
  if (num === null || num === undefined) {
    return '0'
  }
  
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

