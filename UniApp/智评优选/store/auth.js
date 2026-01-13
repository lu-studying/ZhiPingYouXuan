/**
 * 用户认证状态管理
 * 
 * 功能说明：
 * 管理用户登录状态、token 等信息
 */

import { defineStore } from 'pinia'
import { STORAGE_KEY_TOKEN, STORAGE_KEY_USER_INFO } from '@/utils/constants'
import { isTokenExpired } from '@/utils/jwt'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: uni.getStorageSync(STORAGE_KEY_TOKEN) || null,
    userInfo: uni.getStorageSync(STORAGE_KEY_USER_INFO) || null,
    isAuthenticated: false
  }),
  
  getters: {
    /**
     * 检查是否已登录
     */
    isLoggedIn: (state) => {
      if (!state.token) {
        return false
      }
      // 检查 token 是否过期
      if (isTokenExpired(state.token)) {
        return false
      }
      return true
    }
  },
  
  actions: {
    /**
     * 设置 token
     * 
     * @param {string} token - JWT token
     */
    setToken(token) {
      this.token = token
      this.isAuthenticated = !!token
      if (token) {
        uni.setStorageSync(STORAGE_KEY_TOKEN, token)
      } else {
        uni.removeStorageSync(STORAGE_KEY_TOKEN)
      }
    },
    
    /**
     * 设置用户信息
     * 
     * @param {Object} userInfo - 用户信息对象
     */
    setUserInfo(userInfo) {
      this.userInfo = userInfo
      if (userInfo) {
        uni.setStorageSync(STORAGE_KEY_USER_INFO, userInfo)
      } else {
        uni.removeStorageSync(STORAGE_KEY_USER_INFO)
      }
    },
    
    /**
     * 退出登录
     */
    logout() {
      this.token = null
      this.userInfo = null
      this.isAuthenticated = false
      uni.removeStorageSync(STORAGE_KEY_TOKEN)
      uni.removeStorageSync(STORAGE_KEY_USER_INFO)
    },
    
    /**
     * 初始化认证状态（从本地存储恢复）
     */
    initAuth() {
      const token = uni.getStorageSync(STORAGE_KEY_TOKEN)
      const userInfo = uni.getStorageSync(STORAGE_KEY_USER_INFO)
      
      if (token && !isTokenExpired(token)) {
        this.token = token
        this.userInfo = userInfo
        this.isAuthenticated = true
      } else {
        // Token 已过期或不存在，清除本地存储
        this.logout()
      }
    }
  }
})

