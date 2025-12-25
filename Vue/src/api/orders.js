/**
 * 订单/消费记录相关 API 接口封装
 * 
 * 说明：
 * 本模块封装了所有与订单管理相关的 API 调用
 */

import request from '@/utils/request'

/**
 * 获取当前用户的订单列表
 * 
 * @returns {Promise<Array>} 订单列表
 */
export function listMyOrders() {
  return request({
    url: '/users/me/orders',
    method: 'get'
  })
}

/**
 * 获取商家的订单列表
 * 
 * @param {number} shopId - 商家ID
 * @returns {Promise<{content: Array, total: number}>}
 */
export function listOrdersByShop(shopId) {
  return request({
    url: `/shops/${shopId}/orders`,
    method: 'get'
  })
}

/**
 * 创建订单
 * 
 * @param {number} shopId - 商家ID
 * @param {Object} data - 订单信息
 * @param {number} data.amount - 消费金额
 * @param {string} data.visitTime - 到店时间（ISO 8601格式）
 * @param {string} data.items - 消费项明细（JSON字符串，可选）
 * @returns {Promise<Object>} 创建的订单对象
 */
export function createOrder(shopId, data) {
  return request({
    url: `/shops/${shopId}/orders`,
    method: 'post',
    data
  })
}

