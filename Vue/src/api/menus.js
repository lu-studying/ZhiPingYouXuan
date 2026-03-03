/**
 * 菜单管理相关 API 接口封装
 * 
 * 说明：
 * 本模块封装了所有与菜单管理相关的 API 调用
 */

import request from '@/utils/request'

/**
 * 获取商家的推荐菜单列表
 * 
 * @param {number} shopId - 商家ID
 * @param {number} limit - 返回数量限制（可选）
 * @returns {Promise<Array>} 推荐菜单列表
 */
export function getRecommendedMenus(shopId, limit) {
  return request({
    url: `/menus/shop/${shopId}/recommended`,
    method: 'get',
    params: limit ? { limit } : {}
  })
}

/**
 * 获取商家的所有菜单列表
 * 
 * @param {number} shopId - 商家ID
 * @returns {Promise<Array>} 菜单列表
 */
export function getMenusByShopId(shopId) {
  return request({
    url: `/menus/shop/${shopId}`,
    method: 'get'
  })
}

/**
 * 获取菜单详情
 * 
 * @param {number} id - 菜单ID
 * @returns {Promise<Object>} 菜单详情对象
 */
export function getMenu(id) {
  return request({
    url: `/menus/${id}`,
    method: 'get'
  })
}

/**
 * 创建菜单
 * 
 * @param {Object} data - 菜单对象
 * @param {number} data.shopId - 商家ID
 * @param {string} data.name - 菜品名称
 * @param {string} data.description - 菜品描述（可选）
 * @param {number} data.price - 价格
 * @param {string} data.image - 图片URL（可选）
 * @param {number} data.isRecommended - 是否推荐：1是，0否
 * @param {number} data.sortOrder - 排序顺序
 * @returns {Promise<Object>} 创建的菜单对象
 */
export function createMenu(data) {
  return request({
    url: '/menus',
    method: 'post',
    data
  })
}

/**
 * 更新菜单
 * 
 * @param {number} id - 菜单ID
 * @param {Object} data - 菜单对象（包含要更新的字段）
 * @returns {Promise<Object>} 更新后的菜单对象
 */
export function updateMenu(id, data) {
  return request({
    url: `/menus/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除菜单
 * 
 * @param {number} id - 菜单ID
 * @returns {Promise<{message: string}>}
 */
export function deleteMenu(id) {
  return request({
    url: `/menus/${id}`,
    method: 'delete'
  })
}

