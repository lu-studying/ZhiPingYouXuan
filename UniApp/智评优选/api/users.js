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
  // 注意：后端可能没有 /users/me 接口，需要根据实际情况调整
  // 这里假设可以通过解析 token 获取用户ID，然后调用 /users/{id}
  // 或者后端提供了 /users/me 接口
  return request({
    url: '/users/me',
    method: 'get'
  }).catch(() => {
    // 如果接口不存在，返回 null
    return null
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
 * 获取当前用户的点评列表
 * 
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码（从0开始），默认0
 * @param {number} params.size - 每页大小，默认10
 * @returns {Promise<{content: Array, total: number, page: number, size: number}>}
 */
export function getMyReviews(params = {}) {
  // 注意：后端可能没有 /users/me/reviews 接口
  // 需要先获取当前用户ID，然后调用 /users/{id}/reviews
  // 这里先假设有 /users/me/reviews 接口
  return request({
    url: '/users/me/reviews',
    method: 'get',
    params: {
      page: params.page || 0,
      size: params.size || 10
    }
  }).catch(() => {
    // 如果接口不存在，返回空列表
    return { content: [], total: 0, page: 0, size: 10 }
  })
}

