/**
 * 商家管理相关 API 接口封装
 * 
 * 说明：
 * 本模块封装了所有与商家管理相关的 API 调用
 */

import request from './request'

/**
 * 获取商家列表（支持分页、搜索、筛选）
 * 
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码（从0开始），默认0
 * @param {number} params.size - 每页大小，默认10
 * @param {string} params.category - 分类筛选（可选）
 * @param {string} params.keyword - 关键词搜索（可选）
 * @param {number} params.minScore - 最低评分（可选）
 * @param {number} params.maxPrice - 最高价格（可选）
 * @returns {Promise<{content: Array, total: number, page: number, size: number, totalPages: number}>}
 * 
 * @example
 * // 获取第一页商家列表
 * listShops({ page: 0, size: 10 })
 *   .then(res => {
 *     console.log('商家总数:', res.total)
 *     console.log('商家列表:', res.content)
 *   })
 */
export function listShops(params = {}) {
  return request({
    url: '/shops',
    method: 'get',
    params: {
      page: params.page || 0,
      size: params.size || 10,
      category: params.category,
      keyword: params.keyword,
      minScore: params.minScore,
      maxPrice: params.maxPrice
    }
  })
}

/**
 * 获取商家详情
 * 
 * @param {number} id - 商家ID
 * @returns {Promise<Object>} 商家详情对象
 */
export function getShop(id) {
  return request({
    url: `/shops/${id}`,
    method: 'get'
  })
}

