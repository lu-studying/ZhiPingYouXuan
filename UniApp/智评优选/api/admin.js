/**
 * 管理端 API 接口封装
 * 
 * 说明：
 * 本模块封装了所有管理端相关的 API 调用
 * 包括用户管理、菜单管理等
 */

import request from './request'

/**
 * 获取用户列表（分页，仅 ADMIN）
 * 
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码（从0开始），默认0
 * @param {number} params.size - 每页大小，默认10
 * @param {string} params.keyword - 搜索关键词（可选）
 * @returns {Promise<{content: Array, total: number, page: number, size: number}>}
 */
export function getUserList(params = {}) {
  return request({
    url: '/users',
    method: 'get',
    params: {
      page: params.page || 0,
      size: params.size || 10,
      keyword: params.keyword || ''
    }
  })
}

/**
 * 获取商家菜单列表
 * 
 * @param {number} shopId - 商家ID
 * @returns {Promise<Array>} 菜单列表
 */
export function getShopMenus(shopId) {
  return request({
    url: `/menus/shop/${shopId}`,
    method: 'get'
  })
}

/**
 * 创建菜单
 * 
 * @param {Object} menu - 菜单对象
 * @param {number} menu.shopId - 商家ID
 * @param {string} menu.name - 菜品名称
 * @param {string} menu.description - 菜品描述（可选）
 * @param {number} menu.price - 价格
 * @param {string} menu.image - 图片URL（可选）
 * @param {number} menu.isRecommended - 是否推荐：1是，0否
 * @param {number} menu.sortOrder - 排序顺序
 * @returns {Promise<Object>} 创建的菜单对象
 */
export function createMenu(menu) {
  return request({
    url: '/menus',
    method: 'post',
    data: menu
  })
}

/**
 * 更新菜单
 * 
 * @param {number} id - 菜单ID
 * @param {Object} menu - 菜单对象（包含要更新的字段）
 * @returns {Promise<Object>} 更新后的菜单对象
 */
export function updateMenu(id, menu) {
  return request({
    url: `/menus/${id}`,
    method: 'put',
    data: menu
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

/**
 * 获取商家列表（用于商家选择）
 * 
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码（从0开始），默认0
 * @param {number} params.size - 每页大小，默认10
 * @returns {Promise<{content: Array, total: number}>}
 */
export function getShopList(params = {}) {
  return request({
    url: '/shops',
    method: 'get',
    params: {
      page: params.page || 0,
      size: params.size || 10
    }
  })
}

/**
 * 获取当前商家拥有的店铺列表（仅商家账号）
 * 
 * @returns {Promise<Array>} 店铺列表
 */
export function getMyShops() {
  return request({
    url: '/shops/my',
    method: 'get'
  })
}

