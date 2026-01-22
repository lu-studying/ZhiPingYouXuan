/**
 * 菜单管理相关 API 接口封装
 * 
 * 说明：
 * 本模块封装了所有与菜单管理相关的 API 调用
 */

import request from './request'

/**
 * 获取商家的推荐菜单列表
 * 
 * @param {number} shopId - 商家ID
 * @param {number} limit - 返回数量限制，可选
 * @returns {Promise<Array>} 推荐菜单列表
 * 
 * @example
 * getRecommendedMenus(1, 5)
 *   .then(menus => {
 *     console.log('推荐菜单:', menus)
 *   })
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
 * 
 * @example
 * getMenusByShopId(1)
 *   .then(menus => {
 *     console.log('菜单列表:', menus)
 *   })
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

