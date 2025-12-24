/**
 * 认证状态管理模块（Vuex）
 * 
 * 功能说明：
 * 1. 管理用户的登录状态（token、用户信息）
 * 2. 提供登录、退出登录的 actions
 * 3. 提供计算属性（getters）用于判断登录状态
 * 4. 自动同步 localStorage，实现数据持久化
 * 
 * 注意：如果后续改用 Pinia，可以相应调整代码结构
 */

/**
 * 状态定义
 * 
 * state 中存储的数据：
 * - token: JWT 令牌，用于身份验证
 * - userInfo: 用户基本信息对象
 */
const state = {
  // 从 localStorage 读取 token，如果没有则默认为空字符串
  token: localStorage.getItem('token') || '',
  // 从 localStorage 读取用户信息，如果没有则默认为 null
  // 使用 JSON.parse 解析字符串，如果解析失败则返回 null
  userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null')
}

/**
 * 同步修改状态的方法（mutations）
 * 
 * 说明：
 * - mutations 必须是同步函数
 * - 只能通过 commit 调用
 * - 每次调用都会同步更新 state 和 localStorage
 */
const mutations = {
  /**
   * 设置 token
   * 
   * @param {Object} state - Vuex state 对象
   * @param {string} token - JWT 令牌
   */
  SET_TOKEN(state, token) {
    state.token = token
    // 同时保存到 localStorage，实现持久化
    localStorage.setItem('token', token)
  },
  
  /**
   * 设置用户信息
   * 
   * @param {Object} state - Vuex state 对象
   * @param {Object} userInfo - 用户信息对象
   */
  SET_USER_INFO(state, userInfo) {
    state.userInfo = userInfo
    // 将对象转为 JSON 字符串后保存到 localStorage
    localStorage.setItem('userInfo', JSON.stringify(userInfo))
  },
  
  /**
   * 清除认证信息（退出登录时调用）
   * 
   * @param {Object} state - Vuex state 对象
   */
  CLEAR_AUTH(state) {
    // 清空 state 中的数据
    state.token = ''
    state.userInfo = null
    // 同时清除 localStorage 中的数据
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }
}

/**
 * 异步操作方法（actions）
 * 
 * 说明：
 * - actions 可以包含异步操作
 * - 通过 dispatch 调用
 * - 内部通过 commit 调用 mutations 来修改 state
 */
const actions = {
  /**
   * 登录 action
   * 
   * @param {Object} context - Vuex context 对象，包含 commit、state 等
   * @param {Object} payload - 登录数据
   * @param {string} payload.token - JWT 令牌
   * @param {Object} payload.userInfo - 用户信息（可选）
   */
  login({ commit }, { token, userInfo }) {
    // 保存 token
    commit('SET_TOKEN', token)
    // 如果提供了用户信息，则保存用户信息
    if (userInfo) {
      commit('SET_USER_INFO', userInfo)
    }
  },
  
  /**
   * 退出登录 action
   * 
   * @param {Object} context - Vuex context 对象
   */
  logout({ commit }) {
    // 清除所有认证信息
    commit('CLEAR_AUTH')
  }
}

/**
 * 计算属性（getters）
 * 
 * 说明：
 * - getters 类似于组件的 computed
 * - 用于从 state 中派生新的状态
 * - 可以通过 this.$store.getters['auth/isAuthenticated'] 访问
 */
const getters = {
  /**
   * 判断用户是否已登录
   * 
   * @param {Object} state - Vuex state 对象
   * @returns {boolean} 如果 token 存在则返回 true，否则返回 false
   */
  isAuthenticated: state => !!state.token,
  
  /**
   * 获取当前 token
   * 
   * @param {Object} state - Vuex state 对象
   * @returns {string} JWT 令牌
   */
  token: state => state.token,
  
  /**
   * 获取当前用户信息
   * 
   * @param {Object} state - Vuex state 对象
   * @returns {Object|null} 用户信息对象，如果未登录则返回 null
   */
  userInfo: state => state.userInfo
}

/**
 * 导出 Vuex 模块
 * 
 * namespaced: true 表示这是一个命名空间模块
 * 使用时需要通过命名空间访问：this.$store.dispatch('auth/login')
 */
export default {
  namespaced: true, // 启用命名空间
  state,
  mutations,
  actions,
  getters
}

