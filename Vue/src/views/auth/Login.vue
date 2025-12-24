<template>
  <!-- 登录页面容器 -->
  <div class="login-container">
    <!-- 登录表单卡片 -->
    <div class="login-box">
      <!-- 登录页头部：标题和副标题 -->
      <div class="login-header">
        <h1 class="title">点评AI管理后台</h1>
        <p class="subtitle">请登录您的账号</p>
      </div>

      <!-- Element Plus 表单组件 -->
      <!-- 
        ref: 表单引用，用于调用表单方法（如 validate）
        :model: 表单数据绑定
        :rules: 表单验证规则
        @submit.prevent: 阻止表单默认提交行为，使用自定义处理函数
      -->
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <!-- 账号输入框 -->
        <el-form-item prop="mobileOrEmail">
          <el-input
            v-model="loginForm.mobileOrEmail"
            placeholder="请输入手机号或邮箱"
            size="large"
            prefix-icon="User"
            clearable
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <!-- 密码输入框 -->
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            prefix-icon="Lock"
            show-password
            clearable
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <!-- 登录按钮 -->
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-button"
            :loading="loading"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 登录页底部：注册链接 -->
      <div class="login-footer">
        <el-link type="info" :underline="false" @click="handleRegister">
          还没有账号？立即注册
        </el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 登录页面组件
 * 
 * 功能说明：
 * 1. 用户登录表单（手机号/邮箱 + 密码）
 * 2. 表单验证（格式校验、必填校验）
 * 3. 登录请求处理
 * 4. 登录成功后保存 token 并跳转
 */

import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElForm } from 'element-plus'
import { login } from '@/api/auth'
import { useStore } from 'vuex'

// 使用 Vue Router 的 composables
const router = useRouter() // 用于编程式导航
const route = useRoute() // 用于获取当前路由信息（如 query 参数）

// 使用 Vuex store
const store = useStore() // 用于调用 actions 保存登录状态

// 表单引用（用于调用表单的 validate 方法）
const loginFormRef = ref(null)

// 登录按钮加载状态（防止重复提交）
const loading = ref(false)

/**
 * 登录表单数据
 * 
 * 使用 reactive 创建响应式对象
 * - mobileOrEmail: 手机号或邮箱
 * - password: 密码
 */
const loginForm = reactive({
  mobileOrEmail: '',
  password: ''
})

/**
 * 表单验证规则
 * 
 * Element Plus 表单验证规则格式：
 * - required: 是否必填
 * - message: 错误提示信息
 * - trigger: 触发验证的时机（blur: 失去焦点时，change: 值改变时）
 * - validator: 自定义验证函数
 * - min: 最小长度
 */
const loginRules = {
  // 账号验证规则
  mobileOrEmail: [
    // 规则1：必填校验
    { required: true, message: '请输入手机号或邮箱', trigger: 'blur' },
    // 规则2：格式校验（自定义验证函数）
    {
      validator: (rule, value, callback) => {
        if (!value) {
          // 如果值为空，返回错误（但上面已经有必填校验了，这里主要是格式校验）
          callback(new Error('请输入手机号或邮箱'))
        } else if (
          !/^1[3-9]\d{9}$/.test(value) && // 手机号格式：1开头，第二位3-9，共11位数字
          !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value) // 邮箱格式：包含@和.
        ) {
          // 既不是手机号格式，也不是邮箱格式，返回错误
          callback(new Error('请输入正确的手机号或邮箱格式'))
        } else {
          // 格式正确，通过验证
          callback()
        }
      },
      trigger: 'blur' // 失去焦点时触发验证
    }
  ],
  // 密码验证规则
  password: [
    // 规则1：必填校验
    { required: true, message: '请输入密码', trigger: 'blur' },
    // 规则2：最小长度校验
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

/**
 * 处理登录逻辑
 * 
 * 流程：
 * 1. 验证表单
 * 2. 如果验证通过，调用登录 API
 * 3. 登录成功后保存 token 到 store 和 localStorage
 * 4. 跳转到目标页面（如果有 redirect 参数则跳转到该页面，否则跳转到仪表盘）
 */
const handleLogin = async () => {
  // 如果表单引用不存在，直接返回
  if (!loginFormRef.value) return

  // 调用 Element Plus 表单的 validate 方法进行验证
  await loginFormRef.value.validate(async (valid) => {
    // 如果验证不通过，直接返回
    if (!valid) {
      return false
    }

    // 设置加载状态，禁用按钮，防止重复提交
    loading.value = true

    try {
      // 调用登录 API，传入账号和密码
      const response = await login(loginForm.mobileOrEmail, loginForm.password)
      
      // 检查响应中是否包含 token
      if (response && response.token) {
        // 登录成功，保存 token 和用户信息到 Vuex store
        store.dispatch('auth/login', {
          token: response.token, // JWT 令牌
          userInfo: {
            mobileOrEmail: loginForm.mobileOrEmail // 用户账号信息
          }
        })

        // 显示成功提示
        ElMessage.success('登录成功')

        // 跳转到目标页面
        // 如果 URL 中有 redirect 参数（如 /login?redirect=/shops），则跳转到该页面
        // 否则跳转到仪表盘
        const redirect = route.query.redirect || '/dashboard'
        router.push(redirect)
      } else {
        // 响应中没有 token，登录失败
        ElMessage.error('登录失败，请检查账号密码')
      }
    } catch (error) {
      // 登录请求失败
      // 注意：错误信息已经在 request.js 的响应拦截器中处理并显示
      // 这里只需要记录错误日志即可
      console.error('登录错误:', error)
    } finally {
      // 无论成功还是失败，都要取消加载状态，恢复按钮可点击
      loading.value = false
    }
  })
}

/**
 * 处理注册链接点击
 * 
 * 说明：目前注册功能暂未开放，点击后显示提示信息
 * 如果后续有注册页面，可以在这里添加跳转逻辑
 */
const handleRegister = () => {
  ElMessage.info('注册功能暂未开放，请联系管理员')
  // 如果后续有注册页面，可以跳转：
  // router.push('/register')
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-box {
  width: 100%;
  max-width: 420px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  padding: 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header .title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.login-header .subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.login-form {
  margin-top: 30px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 8px;
}

.login-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border-radius: 8px;
  margin-top: 10px;
}

.login-footer {
  text-align: center;
  margin-top: 24px;
}

.login-footer .el-link {
  font-size: 14px;
  color: #909399;
}

.login-footer .el-link:hover {
  color: #409eff;
}
</style>

