<template>
  <!-- 注册页面容器 -->
  <div class="register-container">
    <!-- 注册表单卡片 -->
    <div class="register-box">
      <!-- 注册页头部：标题和副标题 -->
      <div class="register-header">
        <h1 class="title">智评优选管理后台</h1>
        <p class="subtitle">创建新账号</p>
      </div>

      <!-- Element Plus 表单组件 -->
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        @submit.prevent="handleRegister"
      >
        <!-- 账号输入框 -->
        <el-form-item prop="mobileOrEmail">
          <el-input
            v-model="registerForm.mobileOrEmail"
            placeholder="请输入手机号或邮箱"
            size="large"
            prefix-icon="User"
            clearable
            @keyup.enter="handleRegister"
          />
        </el-form-item>

        <!-- 密码输入框 -->
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码（至少6位）"
            size="large"
            prefix-icon="Lock"
            show-password
            clearable
            @keyup.enter="handleRegister"
          />
        </el-form-item>

        <!-- 确认密码输入框 -->
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            size="large"
            prefix-icon="Lock"
            show-password
            clearable
            @keyup.enter="handleRegister"
          />
        </el-form-item>

        <!-- 注册按钮 -->
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="register-button"
            :loading="loading"
            @click="handleRegister"
          >
            {{ loading ? '注册中...' : '注册' }}
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 注册页底部：登录链接 -->
      <div class="register-footer">
        <el-link type="info" :underline="false" @click="handleLogin">
          已有账号？立即登录
        </el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 注册页面组件
 * 
 * 功能说明：
 * 1. 用户注册表单（手机号/邮箱 + 密码 + 确认密码）
 * 2. 表单验证（格式校验、必填校验、密码一致性校验）
 * 3. 注册请求处理
 * 4. 注册成功后保存 token 并跳转
 */

import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElForm } from 'element-plus'
import { register } from '@/api/auth'
import { useStore } from 'vuex'

// 使用 Vue Router 的 composables
const router = useRouter() // 用于编程式导航
const route = useRoute() // 用于获取当前路由信息（如 query 参数）

// 使用 Vuex store
const store = useStore() // 用于调用 actions 保存登录状态

// 表单引用（用于调用表单的 validate 方法）
const registerFormRef = ref(null)

// 注册按钮加载状态（防止重复提交）
const loading = ref(false)

/**
 * 注册表单数据
 * 
 * 使用 reactive 创建响应式对象
 * - mobileOrEmail: 手机号或邮箱
 * - password: 密码
 * - confirmPassword: 确认密码
 */
const registerForm = reactive({
  mobileOrEmail: '',
  password: '',
  confirmPassword: ''
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
const registerRules = {
  // 账号验证规则
  mobileOrEmail: [
    // 规则1：必填校验
    { required: true, message: '请输入手机号或邮箱', trigger: 'blur' },
    // 规则2：格式校验（自定义验证函数）
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('请输入手机号或邮箱'))
        } else if (
          !/^1[2-9]\d{9}$/.test(value) && // 手机号格式：1开头，第二位3-9，共11位数字
          !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value) // 邮箱格式：包含@和.
        ) {
          callback(new Error('请输入正确的手机号或邮箱格式'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  // 密码验证规则
  password: [
    // 规则1：必填校验
    { required: true, message: '请输入密码', trigger: 'blur' },
    // 规则2：最小长度校验
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  // 确认密码验证规则
  confirmPassword: [
    // 规则1：必填校验
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    // 规则2：密码一致性校验（自定义验证函数）
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('请再次输入密码'))
        } else if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

/**
 * 处理注册逻辑
 * 
 * 流程：
 * 1. 验证表单
 * 2. 如果验证通过，调用注册 API
 * 3. 注册成功后保存 token 到 store 和 localStorage
 * 4. 跳转到目标页面（如果有 redirect 参数则跳转到该页面，否则跳转到仪表盘）
 */
const handleRegister = async () => {
  // 如果表单引用不存在，直接返回
  if (!registerFormRef.value) return

  // 调用 Element Plus 表单的 validate 方法进行验证
  await registerFormRef.value.validate(async (valid) => {
    // 如果验证不通过，直接返回
    if (!valid) {
      return false
    }

    // 设置加载状态，禁用按钮，防止重复提交
    loading.value = true

    try {
      // 调用注册 API，传入账号和密码
      const response = await register(registerForm.mobileOrEmail, registerForm.password)
      
      // 检查响应中是否包含 token
      if (response && response.token) {
        // 注册成功，保存 token 和用户信息到 Vuex store
        store.dispatch('auth/login', {
          token: response.token, // JWT 令牌
          userInfo: {
            mobileOrEmail: registerForm.mobileOrEmail // 用户账号信息
          }
        })

        // 显示成功提示
        ElMessage.success('注册成功，已自动登录')

        // 跳转到目标页面
        // 如果 URL 中有 redirect 参数（如 /register?redirect=/shops），则跳转到该页面
        // 否则跳转到仪表盘
        const redirect = route.query.redirect || '/dashboard'
        router.push(redirect)
      } else {
        // 响应中没有 token，注册失败
        ElMessage.error('注册失败，请稍后重试')
      }
    } catch (error) {
      // 注册请求失败
      // 注意：错误信息已经在 request.js 的响应拦截器中处理并显示
      // 这里只需要记录错误日志即可
      console.error('注册错误:', error)
    } finally {
      // 无论成功还是失败，都要取消加载状态，恢复按钮可点击
      loading.value = false
    }
  })
}

/**
 * 处理登录链接点击
 * 
 * 跳转到登录页面
 */
const handleLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
/**
 * 注册页面样式
 * 
 * scoped: 样式只作用于当前组件，不会影响其他组件
 * 样式与登录页面保持一致，保持视觉统一
 */

/* 注册页面容器 */
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

/* 注册表单卡片 */
.register-box {
  width: 100%;
  max-width: 420px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  padding: 40px;
}

/* 注册页头部 */
.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.register-header .title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.register-header .subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

/* 注册表单 */
.register-form {
  margin-top: 30px;
}

.register-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.register-form :deep(.el-input__wrapper) {
  border-radius: 8px;
}

/* 注册按钮 */
.register-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border-radius: 8px;
  margin-top: 10px;
}

/* 注册页底部 */
.register-footer {
  text-align: center;
  margin-top: 24px;
}

.register-footer .el-link {
  font-size: 14px;
  color: #909399;
}

.register-footer .el-link:hover {
  color: #409eff;
}
</style>

