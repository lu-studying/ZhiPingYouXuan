/**
 * 仪表盘数据统计相关 API 接口封装
 * 
 * 说明：
 * 本模块封装了所有与仪表盘数据统计相关的 API 调用
 */

import request from '@/utils/request'

/**
 * 获取仪表盘统计数据
 * 
 * @returns {Promise<Object>} 统计数据对象，包含：
 *   - shopCount: 商家总数
 *   - reviewCount: 点评总数
 *   - orderCount: 订单总数
 *   - aiCallCount: AI 调用总次数
 *   - aiCallSuccessRate: AI 调用成功率（0.0 - 1.0）
 *   - todayNewShops: 今日新增商家数
 *   - todayNewReviews: 今日新增点评数
 *   - todayNewOrders: 今日新增订单数
 * 
 * @example
 * getDashboardStats()
 *   .then(res => {
 *     console.log('商家总数:', res.shopCount)
 *     console.log('AI调用成功率:', res.aiCallSuccessRate)
 *   })
 */
export function getDashboardStats() {
  return request({
    url: '/dashboard/stats',
    method: 'get'
  })
}

