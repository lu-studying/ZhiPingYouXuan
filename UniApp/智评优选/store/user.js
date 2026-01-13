/**
 * 用户信息状态管理
 * 
 * 功能说明：
 * 管理用户详细信息、标签等
 */

import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null,
    tags: [], // 用户标签列表
    loading: false
  }),
  
  actions: {
    /**
     * 设置用户信息
     * 
     * @param {Object} userInfo - 用户信息对象
     */
    setUserInfo(userInfo) {
      this.userInfo = userInfo
    },
    
    /**
     * 设置用户标签
     * 
     * @param {Array} tags - 标签列表
     */
    setTags(tags) {
      this.tags = tags || []
    },
    
    /**
     * 设置加载状态
     * 
     * @param {boolean} loading - 是否加载中
     */
    setLoading(loading) {
      this.loading = loading
    }
  }
})

