/**
 * 用户管理相关 API 接口封装
 * 
 * 说明：
 * 本模块封装了所有与用户管理相关的 API 调用
 */

import request from './request'

/**
 * 获取当前用户信息
 * 
 * @returns {Promise<Object>} 用户信息对象
 */
export function getMyInfo() {
  return request({
    url: '/users/me',
    method: 'get',
    loading: false
  })
}

/**
 * 获取用户详情
 * 
 * @param {number} id - 用户ID
 * @returns {Promise<Object>} 用户详情对象
 */
export function getUser(id) {
  return request({
    url: `/users/${id}`,
    method: 'get'
  })
}

/**
 * 获取当前用户的点评列表（后端从 JWT token 中解析用户ID）
 * 
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码（从0开始），默认0
 * @param {number} params.size - 每页大小，默认10
 * @returns {Promise<{content: Array, total: number, page: number, size: number}>}
 */
export function getMyReviews(params = {}) {
  return request({
    url: '/users/me/reviews',
    method: 'get',
    params: {
      page: params.page || 0,
      size: params.size || 10
    },
    loading: false // 禁用默认 loading，使用页面内的 Loading 组件
  })
}

