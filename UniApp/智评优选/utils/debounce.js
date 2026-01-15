/**
 * 防抖函数工具
 * 
 * @param {Function} func - 要防抖的函数
 * @param {number} wait - 等待时间（毫秒），默认 500ms
 * @param {boolean} immediate - 是否立即执行，默认 false
 * @returns {Function} 防抖后的函数
 * 
 * @example
 * const debouncedSearch = debounce(() => {
 *   console.log('搜索')
 * }, 500)
 * 
 * // 使用
 * debouncedSearch()
 */
export function debounce(func, wait = 500, immediate = false) {
  let timeout
  
  return function executedFunction(...args) {
    const later = () => {
      timeout = null
      if (!immediate) func(...args)
    }
    
    const callNow = immediate && !timeout
    
    clearTimeout(timeout)
    timeout = setTimeout(later, wait)
    
    if (callNow) func(...args)
  }
}

