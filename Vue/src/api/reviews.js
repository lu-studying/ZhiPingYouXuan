/**
 * 点评管理相关 API 接口封装
 * 
 * 说明：
 * 本模块封装了所有与点评管理相关的 API 调用
 */

import request from '@/utils/request'

/**
 * 获取商家点评列表（分页）
 * 
 * @param {number} shopId - 商家ID
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码（从0开始），默认0
 * @param {number} params.size - 每页大小，默认10
 * @returns {Promise<{content: Array, total: number, page: number, size: number}>}
 */
export function listReviews(shopId, params = {}) {
  return request({
    url: `/shops/${shopId}/reviews`,
    method: 'get',
    params: {
      page: params.page || 0,
      size: params.size || 10
    }
  })
}

/**
 * 创建点评
 * 
 * @param {number} shopId - 商家ID
 * @param {Object} data - 点评信息
 * @param {number} data.rating - 评分（1-5）
 * @param {string} data.content - 点评内容
 * @param {string} data.images - 图片列表（JSON字符串，可选）
 * @returns {Promise<Object>} 创建的点评对象
 */
export function createReview(shopId, data) {
  return request({
    url: `/shops/${shopId}/reviews`,
    method: 'post',
    data
  })
}

/**
 * 点赞点评
 * 
 * @param {number} shopId - 商家ID
 * @param {number} reviewId - 点评ID
 * @returns {Promise<{message: string}>}
 */
export function likeReview(shopId, reviewId) {
  return request({
    url: `/shops/${shopId}/reviews/${reviewId}/like`,
    method: 'post'
  })
}

/**
 * AI 生成点评草稿
 * 
 * @param {number} shopId - 商家ID
 * @param {Object} data - 请求参数
 * @param {string} data.preference - 偏好（可选）
 * @returns {Promise<{draft: string}>}
 */
export function generateAiDraft(shopId, data = {}) {
  return request({
    url: `/shops/${shopId}/reviews/ai-draft`,
    method: 'post',
    data
  })
}

/**
 * AI 推荐点评
 * 
 * @param {number} shopId - 商家ID
 * @param {Object} params - 查询参数
 * @param {string} params.preference - 偏好关键词（可选）
 * @param {number} params.limit - 数量（可选，默认3）
 * @returns {Promise<Array>} 推荐点评列表
 */
export function recommendReviews(shopId, params = {}) {
  return request({
    url: `/shops/${shopId}/reviews/recommend`,
    method: 'get',
    params: {
      preference: params.preference,
      limit: params.limit || 3
    }
  })
}

