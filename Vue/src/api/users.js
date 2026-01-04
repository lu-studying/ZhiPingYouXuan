/**
 * 用户管理相关 API 接口封装
 * 
 * 说明：
 * 本模块封装了所有与用户管理相关的 API 调用
 */

import request from '@/utils/request'

/**
 * 获取用户列表（支持分页、搜索）
 * 
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码（从0开始），默认0
 * @param {number} params.size - 每页大小，默认10
 * @param {string} params.keyword - 搜索关键词（在手机号和邮箱中搜索），可选
 * @returns {Promise<{content: Array, total: number, page: number, size: number}>}
 * 
 * @example
 * // 获取第一页用户列表
 * listUsers({ page: 0, size: 10 })
 *   .then(res => {
 *     console.log('用户总数:', res.total)
 *     console.log('用户列表:', res.content)
 *   })
 */
export function listUsers(params = {}) {
  return request({
    url: '/users',
    method: 'get',
    params: {
      page: params.page || 0,
      size: params.size || 10,
      keyword: params.keyword
    }
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
 * 获取用户点评列表
 * 
 * @param {number} id - 用户ID
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码（从0开始），默认0
 * @param {number} params.size - 每页大小，默认10
 * @returns {Promise<{content: Array, total: number, page: number, size: number}>}
 */
export function getUserReviews(id, params = {}) {
  return request({
    url: `/users/${id}/reviews`,
    method: 'get',
    params: {
      page: params.page || 0,
      size: params.size || 10
    }
  })
}

