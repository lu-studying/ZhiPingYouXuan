/**
 * 订单/消费记录相关 API 接口封装
 * 
 * 说明：
 * 本模块封装了所有与订单管理相关的 API 调用
 */

import request from './request'

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
 * 创建待支付订单
 * 
 * @param {number} shopId - 商家ID
 * @param {Object} data - 下单信息
 * @param {string} data.visitTime - 到店时间（ISO 8601格式，可选）
 * @param {string} data.items - 下单项JSON字符串，格式：[{menuId, quantity}]
 * @returns {Promise<Object>} 创建的订单对象（payStatus=0）
 */
export function createOrder(shopId, data) {
  return request({
    url: `/shops/${shopId}/orders`,
    method: 'post',
    data
  })
}

/**
 * 模拟支付
 */
export function payOrder(orderId, payMethod = 'MOCK_WECHAT') {
  return request({
    url: `/orders/${orderId}/pay`,
    method: 'post',
    data: { payMethod }
  })
}

/**
 * 取消待支付订单
 */
export function cancelOrder(orderId) {
  return request({
    url: `/orders/${orderId}/cancel`,
    method: 'post'
  })
}

