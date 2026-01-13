/**
 * 常量定义
 * 
 * 功能说明：
 * 定义应用中的常量，如 API 基础 URL、配置项等
 */

// API 基础 URL
// 开发环境：直接访问后端（后端 CORS 已配置允许 localhost:5173）
// 生产环境：需要修改为实际的后端地址
export const BASE_URL = process.env.NODE_ENV === 'production' 
  ? 'https://your-api-domain.com/api'  // 生产环境 API 地址
  : 'http://localhost:8080/api'        // 开发环境：直接访问后端（CORS 已配置）

// 请求超时时间（毫秒）
export const REQUEST_TIMEOUT = 10000

// 分页默认配置
export const DEFAULT_PAGE_SIZE = 10
export const DEFAULT_PAGE = 0

// 图片上传配置
export const MAX_IMAGE_COUNT = 9
export const IMAGE_QUALITY = 0.8

// 存储 key
export const STORAGE_KEY_TOKEN = 'token'
export const STORAGE_KEY_USER_INFO = 'userInfo'

