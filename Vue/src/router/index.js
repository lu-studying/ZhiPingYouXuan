/**
 * 路由配置模块
 * 
 * 功能说明：
 * 1. 定义所有路由规则
 * 2. 配置路由守卫，实现登录状态检查
 * 3. 未登录用户访问受保护路由时自动跳转到登录页
 * 4. 已登录用户访问登录页时自动跳转到仪表盘
 */

import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store'
import { isTokenExpired } from '@/utils/jwt'

/**
 * 路由配置数组
 * 
 * 每个路由对象包含：
 * - path: 路由路径
 * - name: 路由名称（用于编程式导航）
 * - component: 组件（使用懒加载）
 * - meta: 路由元信息（如标题、是否需要认证等）
 */
const routes = [
  // 登录页路由
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'), // 懒加载，减少首屏加载时间
    meta: {
      title: '登录', // 页面标题
      requiresAuth: false // 登录页不需要认证，设置为 false
    }
  },
  // 注册页路由
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'), // 懒加载
    meta: {
      title: '注册', // 页面标题
      requiresAuth: false // 注册页不需要认证，设置为 false
    }
  },
  // 主布局路由（包含导航菜单）
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    meta: {
      requiresAuth: true
    },
    children: [
      // 仪表盘路由
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Index.vue'),
        meta: {
          title: '仪表盘',
          requiresAuth: true
        }
      },
      // 商家管理路由
      {
        path: '/shops',
        name: 'Shops',
        component: () => import('@/views/shops/List.vue'),
        meta: {
          title: '商家管理',
          requiresAuth: true
        }
      },
      // 新增商家路由
      {
        path: '/shops/create',
        name: 'ShopCreate',
        component: () => import('@/views/shops/Create.vue'),
        meta: {
          title: '新增商家',
          requiresAuth: true
        }
      },
      // 商家详情路由
      {
        path: '/shops/:id',
        name: 'ShopDetail',
        component: () => import('@/views/shops/Detail.vue'),
        meta: {
          title: '商家详情',
          requiresAuth: true
        }
      },
      // 编辑商家路由
      {
        path: '/shops/:id/edit',
        name: 'ShopEdit',
        component: () => import('@/views/shops/Edit.vue'),
        meta: {
          title: '编辑商家',
          requiresAuth: true
        }
      },
      // 点评管理路由
      {
        path: '/reviews',
        name: 'Reviews',
        component: () => import('@/views/reviews/List.vue'),
        meta: {
          title: '点评管理',
          requiresAuth: true
        }
      },
      // 订单管理路由
      {
        path: '/orders',
        name: 'Orders',
        component: () => import('@/views/orders/List.vue'),
        meta: {
          title: '订单管理',
          requiresAuth: true
        }
      },
      // 用户管理路由
      {
        path: '/users',
        name: 'Users',
        component: () => import('@/views/users/List.vue'),
        meta: {
          title: '用户管理',
          requiresAuth: true
        }
      },
      // 用户详情路由
      {
        path: '/users/:id',
        name: 'UserDetail',
        component: () => import('@/views/users/Detail.vue'),
        meta: {
          title: '用户详情',
          requiresAuth: true
        }
      },
      // AI 调用日志路由
      {
        path: '/ai-logs',
        name: 'AiLogs',
        component: () => import('@/views/ai-logs/List.vue'),
        meta: {
          title: 'AI 调用日志',
          requiresAuth: true
        }
      }
    ]
  },
  
  // 404 页面：匹配所有未定义的路由，重定向到仪表盘
  {
    path: '/:pathMatch(.*)*', // 匹配所有路径
    redirect: '/dashboard' // 重定向到仪表盘
  }
]

/**
 * 创建路由实例
 * 
 * createWebHistory: 使用 HTML5 History 模式（需要服务器配置支持）
 * 优点：URL 更美观，如 /dashboard 而不是 /#/dashboard
 * 缺点：需要服务器配置，否则刷新页面会 404
 */
const router = createRouter({
  history: createWebHistory(), // 使用 History 模式
  routes // 路由配置
})

/**
 * 全局前置路由守卫
 * 
 * 功能说明：
 * 1. 在每次路由跳转前执行
 * 2. 检查目标路由是否需要认证
 * 3. 如果需要认证但用户未登录，跳转到登录页
 * 4. 如果已登录但访问登录页，跳转到仪表盘
 * 
 * @param {Object} to - 目标路由对象
 * @param {Object} from - 来源路由对象
 * @param {Function} next - 路由跳转函数，必须调用才能继续导航
 */
router.beforeEach((to, from, next) => {
  // 从 localStorage 获取 token，判断用户是否已登录
  const token = localStorage.getItem('token')
  const isAuthenticated = !!token // 转换为布尔值

  // 如果存在 token，检查是否过期
  if (token && isTokenExpired(token)) {
    // Token 已过期，清除本地存储
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    
    // 如果目标路由需要认证，跳转到登录页
    if (to.meta.requiresAuth) {
      next({
        path: '/login',
        query: { redirect: to.fullPath } // 保存原始路径，登录后可以跳转回去
      })
      return
    }
  }

  // 情况1：目标路由需要认证，但用户未登录（或 token 已过期）
  if (to.meta.requiresAuth && !isAuthenticated) {
    // 跳转到登录页，并保存原始路径（登录后可以跳转回去）
    next({
      path: '/login',
      query: { redirect: to.fullPath } // 保存完整路径，如 /dashboard?page=1
    })
  } 
  // 情况2：已登录用户访问登录页或注册页（避免重复登录）
  else if ((to.path === '/login' || to.path === '/register') && isAuthenticated && !isTokenExpired(token)) {
    // 直接跳转到仪表盘（只有在 token 有效时才跳转）
    next('/dashboard')
  } 
  // 情况3：其他情况，正常跳转
  else {
    next() // 允许导航继续
  }
})

// 导出路由实例，供 main.js 使用
export default router

