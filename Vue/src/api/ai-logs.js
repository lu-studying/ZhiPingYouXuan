/**
 * AI 调用日志相关 API 接口封装
 * 
 * 说明：
 * 本模块封装了所有与 AI 调用日志相关的 API 调用
 */

import request from '@/utils/request'

/**
 * 获取 AI 调用日志列表（支持分页、多条件筛选）
 * 
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码（从0开始），默认0
 * @param {number} params.size - 每页大小，默认10
 * @param {string} params.type - 调用类型筛选（可选）：generate（生成草稿）、recommend（推荐）
 * @param {number} params.status - 状态筛选（可选）：1（成功）、0（失败））
 * @param {number} params.userId - 用户ID筛选（可选）
 * @returns {Promise<{content: Array, total: number, page: number, size: number}>}
 * 
 * @example
 * // 获取第一页日志列表
 * listAiLogs({ page: 0, size: 10 })
 *   .then(res => {
 *     console.log('日志总数:', res.total)
 *     console.log('日志列表:', res.content)
 *   })
 * 
 * // 筛选"生成草稿"类型的成功调用
 * listAiLogs({ page: 0, size: 10, type: 'generate', status: 1 })
 *   .then(res => {
 *     console.log('筛选结果:', res.content)
 *   })
 */
export function listAiLogs(params = {}) {
  return request({
    url: '/ai-logs',
    method: 'get',
    params: {
      page: params.page || 0,
      size: params.size || 10,
      type: params.type,
      status: params.status,
      userId: params.userId
    }
  })
}

