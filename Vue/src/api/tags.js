/**
 * 标签管理相关 API 接口封装
 * 
 * 说明：
 * 本模块封装了所有与标签管理相关的 API 调用
 */

import request from '@/utils/request'

/**
 * 获取标签列表（支持按类型筛选）
 * 
 * @param {string} type - 标签类型（user/shop/review），可选，不传则返回所有标签
 * @returns {Promise<Array>} 标签列表
 * 
 * @example
 * // 获取所有标签
 * listTags()
 *   .then(tags => {
 *     console.log('标签列表:', tags)
 *   })
 * 
 * // 获取用户类型标签
 * listTags('user')
 *   .then(tags => {
 *     console.log('用户标签:', tags)
 *   })
 */
export function listTags(type = null) {
  return request({
    url: '/tags',
    method: 'get',
    params: type ? { type } : {}
  })
}

/**
 * 获取商家已绑定的标签列表
 * 
 * @param {number} shopId - 商家ID
 * @returns {Promise<Array>} 标签列表
 * 
 * @example
 * getShopTags(1)
 *   .then(tags => {
 *     console.log('商家标签:', tags)
 *   })
 */
export function getShopTags(shopId) {
  return request({
    url: `/shops/${shopId}/tags`,
    method: 'get'
  })
}

/**
 * 获取用户已绑定的标签列表
 * 
 * @param {number} userId - 用户ID
 * @returns {Promise<Array>} 标签列表
 * 
 * @example
 * getUserTags(1)
 *   .then(tags => {
 *     console.log('用户标签:', tags)
 *   })
 */
export function getUserTags(userId) {
  return request({
    url: `/users/${userId}/tags`,
    method: 'get'
  })
}

