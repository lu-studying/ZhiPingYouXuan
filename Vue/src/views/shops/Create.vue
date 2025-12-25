<template>
  <!-- 新增商家页面容器 -->
  <div class="shop-create-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">新增商家</h1>
        <p class="page-subtitle">填写商家基本信息</p>
      </div>
      <div class="header-right">
        <el-button @click="handleCancel">取消</el-button>
      </div>
    </div>

    <!-- 表单卡片 -->
    <el-card class="form-card" shadow="never">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        label-position="right"
      >
        <!-- 商家名称 -->
        <el-form-item label="商家名称" prop="name">
          <el-input
            v-model="form.name"
            placeholder="请输入商家名称"
            clearable
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <!-- 商家分类 -->
        <el-form-item label="商家分类" prop="category">
          <el-select
            v-model="form.category"
            placeholder="请选择商家分类"
            clearable
            style="width: 100%"
          >
            <el-option label="火锅" value="火锅" />
            <el-option label="川菜" value="川菜" />
            <el-option label="日料" value="日料" />
            <el-option label="西餐" value="西餐" />
            <el-option label="咖啡" value="咖啡" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <!-- 商家地址 -->
        <el-form-item label="商家地址" prop="address">
          <el-input
            v-model="form.address"
            type="textarea"
            :rows="3"
            placeholder="请输入商家详细地址"
            clearable
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <!-- 地理位置（经度、纬度） -->
        <el-form-item label="地理位置">
          <div class="location-group">
            <el-form-item prop="lng" style="margin-bottom: 0; flex: 1;">
              <template #label>
                <span>经度</span>
              </template>
              <el-input-number
                v-model="form.lng"
                :min="-180"
                :max="180"
                :precision="6"
                placeholder="经度（可选）"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item prop="lat" style="margin-bottom: 0; flex: 1; margin-left: 20px;">
              <template #label>
                <span>纬度</span>
              </template>
              <el-input-number
                v-model="form.lat"
                :min="-90"
                :max="90"
                :precision="6"
                placeholder="纬度（可选）"
                style="width: 100%"
              />
            </el-form-item>
          </div>
          <div class="form-tip">
            <el-icon><InfoFilled /></el-icon>
            <span>经纬度用于地图定位，如不填写可后续编辑补充</span>
          </div>
        </el-form-item>

        <!-- 人均价格 -->
        <el-form-item label="人均价格" prop="avgPrice">
          <el-input-number
            v-model="form.avgPrice"
            :min="0"
            :precision="2"
            :step="10"
            placeholder="人均价格（可选）"
            style="width: 100%"
          >
            <template #prefix>¥</template>
          </el-input-number>
          <div class="form-tip">
            <el-icon><InfoFilled /></el-icon>
            <span>单位：元（人民币）</span>
          </div>
        </el-form-item>

        <!-- 表单操作按钮 -->
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">
            <el-icon v-if="!loading"><Check /></el-icon>
            {{ loading ? '提交中...' : '提交' }}
          </el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
/**
 * 新增商家页面组件
 * 
 * 功能说明：
 * 1. 商家信息表单（名称、分类、地址、经纬度、价格）
 * 2. 表单验证（必填字段校验、格式校验）
 * 3. 提交创建商家
 * 4. 成功后跳转到商家列表或详情页
 */

import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createShop } from '@/api/shops'

// 使用 Vue Router
const router = useRouter()

// 表单引用（用于调用表单的 validate 方法）
const formRef = ref(null)

// 提交按钮加载状态（防止重复提交）
const loading = ref(false)

/**
 * 表单数据
 * 
 * 使用 reactive 创建响应式对象
 * - name: 商家名称（必填）
 * - category: 商家分类（必填）
 * - address: 商家地址（必填）
 * - lng: 经度（可选）
 * - lat: 纬度（可选）
 * - avgPrice: 人均价格（可选）
 */
const form = reactive({
  name: '',
  category: '',
  address: '',
  lng: null,
  lat: null,
  avgPrice: null
})

/**
 * 表单验证规则
 * 
 * Element Plus 表单验证规则格式：
 * - required: 是否必填
 * - message: 错误提示信息
 * - trigger: 触发验证的时机（blur: 失去焦点时，change: 值改变时）
 */
const rules = {
  // 商家名称验证规则
  name: [
    { required: true, message: '请输入商家名称', trigger: 'blur' },
    { min: 1, max: 100, message: '商家名称长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  // 商家分类验证规则
  category: [
    { required: true, message: '请选择商家分类', trigger: 'change' }
  ],
  // 商家地址验证规则
  address: [
    { required: true, message: '请输入商家地址', trigger: 'blur' },
    { min: 1, max: 200, message: '商家地址长度在 1 到 200 个字符', trigger: 'blur' }
  ],
  // 经度验证规则（可选，但如果填写了需要验证范围）
  lng: [
    {
      validator: (rule, value, callback) => {
        if (value === null || value === undefined || value === '') {
          callback() // 可选字段，可以为空
        } else if (value < -180 || value > 180) {
          callback(new Error('经度范围应在 -180 到 180 之间'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  // 纬度验证规则（可选，但如果填写了需要验证范围）
  lat: [
    {
      validator: (rule, value, callback) => {
        if (value === null || value === undefined || value === '') {
          callback() // 可选字段，可以为空
        } else if (value < -90 || value > 90) {
          callback(new Error('纬度范围应在 -90 到 90 之间'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  // 人均价格验证规则（可选，但如果填写了需要验证范围）
  avgPrice: [
    {
      validator: (rule, value, callback) => {
        if (value === null || value === undefined || value === '') {
          callback() // 可选字段，可以为空
        } else if (value < 0) {
          callback(new Error('人均价格不能为负数'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

/**
 * 处理表单提交
 * 
 * 流程：
 * 1. 验证表单
 * 2. 如果验证通过，调用创建商家 API
 * 3. 创建成功后显示提示并跳转
 */
const handleSubmit = async () => {
  // 如果表单引用不存在，直接返回
  if (!formRef.value) return

  // 调用 Element Plus 表单的 validate 方法进行验证
  await formRef.value.validate(async (valid) => {
    // 如果验证不通过，直接返回
    if (!valid) {
      return false
    }

    // 设置加载状态，禁用按钮，防止重复提交
    loading.value = true

    try {
      // 构建提交数据（只提交有值的字段）
      const submitData = {
        name: form.name.trim(),
        category: form.category,
        address: form.address.trim(),
        lng: form.lng || undefined,
        lat: form.lat || undefined,
        avgPrice: form.avgPrice || undefined
      }

      // 调用创建商家 API
      const response = await createShop(submitData)

      // 显示成功提示
      ElMessage.success('商家创建成功')

      // 跳转到商家列表页（可以传递参数显示成功提示）
      router.push({
        path: '/shops',
        query: { created: 'success' }
      })
    } catch (error) {
      // 创建请求失败
      // 注意：错误信息已经在 request.js 的响应拦截器中处理并显示
      // 这里只需要记录错误日志即可
      console.error('创建商家失败:', error)
    } finally {
      // 无论成功还是失败，都要取消加载状态，恢复按钮可点击
      loading.value = false
    }
  })
}

/**
 * 处理重置表单
 * 
 * 清空所有表单字段
 */
const handleReset = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

/**
 * 处理取消操作
 * 
 * 返回商家列表页
 */
const handleCancel = () => {
  router.push('/shops')
}
</script>

<style scoped>
/**
 * 新增商家页面样式
 * 
 * scoped: 样式只作用于当前组件，不会影响其他组件
 */

/* 页面容器 */
.shop-create-container {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.header-left {
  flex: 1;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
}

/* 表单卡片 */
.form-card {
  max-width: 800px;
  margin: 0 auto;
}

/* 地理位置输入组 */
.location-group {
  display: flex;
  align-items: flex-start;
  width: 100%;
}

/* 表单提示信息 */
.form-tip {
  display: flex;
  align-items: center;
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.form-tip .el-icon {
  margin-right: 4px;
  font-size: 14px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .shop-create-container {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    gap: 16px;
  }

  .location-group {
    flex-direction: column;
  }

  .location-group .el-form-item {
    margin-left: 0 !important;
    width: 100%;
  }
}
</style>

