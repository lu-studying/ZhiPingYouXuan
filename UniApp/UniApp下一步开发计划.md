# 智评优选（Dianping+AI）UniApp 下一步开发计划

> **计划制定时间**: 2026-1-13  
> **当前项目状态**: 基础架构已完成，核心功能待完善  
> **预计完成时间**: 2-3 周

---

## 📊 一、当前项目状态评估

### ✅ 已完成部分（约 30%）

#### 1.1 基础架构 ✅
- ✅ HTTP 请求封装 (`api/request.js`) - 完整实现，包含 token 处理、错误处理
- ✅ API 接口封装 (`api/*.js`) - 所有业务接口已封装
- ✅ 状态管理 (`store/auth.js`, `store/user.js`) - Pinia 配置完成
- ✅ 工具函数 (`utils/*.js`) - JWT、格式化、常量定义完成
- ✅ 页面路由配置 (`pages.json`) - 所有页面路由已配置
- ✅ 应用配置 (`manifest.json`) - H5 开发环境配置完成

#### 1.2 页面文件 ✅
- ✅ 所有页面文件已创建（9 个页面）
- ✅ 基础页面结构已实现
- ✅ 基础功能已实现（但不完整）

#### 1.3 已修复问题 ✅
- ✅ 跨域问题已解决（通过直接访问后端 API）
- ✅ 生命周期钩子导入问题已修复
- ✅ 数据初始化问题已修复

---

### ❌ 待完善部分（约 70%）

#### 2.1 核心功能缺失 ⚠️

**商家详情页** (`pages/shop/detail.vue`):
- ❌ **缺少 AI 推荐点评展示**（核心亮点功能）
- ❌ 缺少点评列表展示
- ❌ 缺少创建点评入口按钮
- ❌ 商家信息展示不完整（缺少评分、人均价格等）
- ❌ 样式简陋，需要优化

**创建点评页** (`pages/review/create.vue`):
- ❌ **缺少 AI 草稿生成功能**（核心亮点功能）
- ❌ 缺少图片上传功能
- ❌ 评分组件样式简陋
- ❌ 表单验证不完善

**首页** (`pages/home/index.vue`):
- ❌ 缺少分页加载功能（上拉加载更多未实现）
- ❌ 缺少商家卡片组件（当前是简单文本展示）
- ❌ 搜索功能缺少防抖处理
- ❌ 样式简陋，需要优化

#### 2.2 公共组件缺失 ⚠️

**完全缺失 `components` 目录**，需要创建：
- ❌ `components/shop-card.vue` - 商家卡片组件
- ❌ `components/review-card.vue` - 点评卡片组件（需支持 AI 推荐标识）
- ❌ `components/rating-stars.vue` - 评分组件
- ❌ `components/loading.vue` - 加载状态组件
- ❌ `components/empty-state.vue` - 空状态组件
- ❌ `components/ai-recommend-badge.vue` - AI 推荐标识组件

#### 2.3 用户体验待优化 ⚠️

- ❌ 所有页面样式简陋，需要 UI 优化
- ❌ 缺少加载动画和骨架屏
- ❌ 错误提示不够友好
- ❌ 缺少空状态提示
- ❌ AI 功能缺少加载动画和错误处理

#### 2.4 功能完善 ⚠️

**用户相关页面**:
- ⚠️ 登录/注册页面：基础功能完成，但缺少表单验证增强
- ⚠️ 我的订单页：基础实现，需要优化展示
- ⚠️ 我的点评页：基础实现，需要优化展示
- ⚠️ 偏好设置页：功能完成，但样式需要优化

---

## 🎯 二、下一步开发计划（优先级排序）

### 📌 阶段一：公共组件开发（优先级：高 ⭐⭐⭐）

**预计时间**: 2-3 天

#### 1.1 创建组件目录结构

**任务清单**:
- [x] 创建 `components` 目录
- [x] 创建组件基础文件

#### 1.2 核心组件开发

**任务清单**:

1. **商家卡片组件** (`components/shop-card.vue`) ⭐⭐⭐ ✅
   - [x] 商家名称、分类、地址展示
   - [x] 评分显示（使用 rating-stars 组件）
   - [x] 人均价格展示
   - [x] 点击跳转商家详情
   - [x] 样式优化（卡片式设计）

2. **点评卡片组件** (`components/review-card.vue`) ⭐⭐⭐
   - [ ] 用户头像、昵称展示
   - [ ] 评分、内容、图片展示
   - [ ] 点赞数、点赞按钮
   - [ ] **AI 推荐标识**（如有推荐理由，显示特殊样式）
   - [ ] **推荐理由展示**（浅蓝色背景、AI 图标）
   - [ ] 时间格式化显示

3. **评分组件** (`components/rating-stars.vue`) ⭐⭐⭐ ✅
   - [x] 星级显示（1-5 星）
   - [x] 支持只读和可编辑两种模式
   - [x] 样式优化（星星图标）

4. **加载状态组件** (`components/loading.vue`) ⭐⭐
   - [ ] 加载动画
   - [ ] 可配置文字提示

5. **空状态组件** (`components/empty-state.vue`) ⭐⭐
   - [ ] 空数据提示
   - [ ] 可配置图标、文字、按钮

6. **AI 推荐标识组件** (`components/ai-recommend-badge.vue`) ⭐⭐
   - [ ] AI 图标 + "AI 推荐"文字
   - [ ] 用于标记推荐点评

**预计时间**: 2-3 天

---

### 📌 阶段二：核心页面功能完善（优先级：高 ⭐⭐⭐）

**预计时间**: 1-2 周

#### 2.1 商家详情页完善 ⭐⭐⭐

**页面路径**: `pages/shop/detail.vue`

**任务清单**:
- [ ] **商家信息完善**
  - [ ] 展示商家完整信息（名称、分类、地址、评分、人均价格）
  - [ ] 使用商家卡片组件或优化展示样式
  - [ ] 商家标签展示（如有）

- [ ] **AI 推荐点评区域** ⭐⭐⭐（核心亮点）
  - [ ] 调用 `recommendReviews` API
  - [ ] 使用 review-card 组件展示推荐点评
  - [ ] 推荐理由特殊样式（浅蓝色背景、AI 图标）
  - [ ] 推荐理由可展开/收起（长文本时）
  - [ ] 如果没有推荐结果，隐藏该区域
  - [ ] 加载状态和错误处理

- [ ] **全部点评列表**
  - [ ] 调用 `listReviews` API
  - [ ] 分页加载（下拉刷新、上拉加载更多）
  - [ ] 使用 review-card 组件展示
  - [ ] 空状态提示

- [ ] **创建点评入口**
  - [ ] 底部固定按钮（或导航栏右侧按钮）
  - [ ] 点击跳转 `pages/review/create?shopId=xxx`
  - [ ] 未登录时提示登录

- [ ] **样式优化**
  - [ ] 整体布局优化
  - [ ] 商家信息区域样式优化
  - [ ] AI 推荐区域样式优化（突出显示）

**预计时间**: 3-4 天

---

#### 2.2 创建点评页完善 ⭐⭐⭐

**页面路径**: `pages/review/create.vue`

**任务清单**:
- [ ] **表单完善**
  - [ ] 使用 rating-stars 组件（可编辑模式）
  - [ ] 内容输入框优化（多行、字数统计）
  - [ ] 图片上传功能（最多 9 张）
    - [ ] 使用 `uni.chooseImage` 选择图片
    - [ ] 图片预览（可删除）
    - [ ] 图片上传到服务器（需后端支持或使用云存储）

- [ ] **AI 草稿生成功能** ⭐⭐⭐（核心亮点）
  - [ ] "AI 生成草稿"按钮（突出显示）
  - [ ] 点击后调用 `generateAiDraft` API
  - [ ] 显示加载状态（Loading 组件，显示"AI 正在生成中..."）
  - [ ] 成功后填充到内容输入框
  - [ ] 用户可编辑草稿内容
  - [ ] 错误处理（生成失败提示）
  - [ ] 可多次生成（覆盖或追加）

- [ ] **提交功能完善**
  - [ ] 表单校验增强（评分、内容必填）
  - [ ] 提交按钮加载状态（防止重复提交）
  - [ ] 提交成功后跳转商家详情页

- [ ] **样式优化**
  - [ ] 整体布局优化
  - [ ] AI 按钮样式优化（突出显示）
  - [ ] 表单样式优化

**预计时间**: 3-4 天

---

#### 2.3 首页完善 ⭐⭐⭐

**页面路径**: `pages/home/index.vue`

**任务清单**:
- [ ] **商家列表优化**
  - [ ] 使用 shop-card 组件替换当前简单展示
  - [ ] 分页加载功能完善
    - [ ] 实现上拉加载更多（onReachBottom）
    - [ ] 分页状态管理（page、hasMore）
    - [ ] 加载更多提示

- [ ] **搜索功能优化**
  - [ ] 添加防抖处理（避免频繁请求）
  - [ ] 搜索历史记录（可选）

- [ ] **分类筛选优化**
  - [ ] Tab 样式优化
  - [ ] 切换分类时重置分页

- [ ] **样式优化**
  - [ ] 整体布局优化
  - [ ] 搜索框样式优化
  - [ ] 分类 Tab 样式优化

**预计时间**: 2-3 天

---

#### 2.4 用户相关页面优化 ⭐⭐

**任务清单**:

1. **登录/注册页优化**
   - [ ] 表单验证增强（邮箱格式、密码强度）
   - [ ] 样式优化
   - [ ] 错误提示优化

2. **我的订单页优化**
   - [ ] 使用订单卡片组件（需创建）
   - [ ] 订单信息展示优化
   - [ ] 空状态提示

3. **我的点评页优化**
   - [ ] 使用 review-card 组件
   - [ ] 分页加载
   - [ ] 空状态提示

4. **偏好设置页优化**
   - [ ] 标签选择器样式优化
   - [ ] 保存成功提示优化

**预计时间**: 2-3 天

---

### 📌 阶段三：用户体验优化（优先级：中 ⭐⭐）

**预计时间**: 3-5 天

#### 3.1 加载状态优化

**任务清单**:
- [ ] 列表加载骨架屏
- [ ] 按钮加载状态（防止重复提交）
- [ ] AI 草稿生成加载动画（特殊动画效果）
- [ ] AI 推荐加载状态

#### 3.2 错误处理优化

**任务清单**:
- [ ] 网络错误友好提示
- [ ] API 错误统一处理
- [ ] 空状态提示优化
- [ ] 401 错误自动跳转登录优化

#### 3.3 交互优化

**任务清单**:
- [ ] AI 推荐理由动画效果（可选）
- [ ] 推荐理由可展开/收起（长文本时）
- [ ] 下拉刷新、上拉加载更多优化
- [ ] 页面跳转动画优化

#### 3.4 性能优化

**任务清单**:
- [ ] 图片懒加载
- [ ] API 请求防抖/节流（搜索框）
- [ ] 避免不必要的重复请求

**预计时间**: 3-5 天

---

### 📌 阶段四：多端适配与测试（优先级：中 ⭐⭐）

**预计时间**: 3-5 天

#### 4.1 多端适配

**任务清单**:
- [ ] 微信小程序适配
  - [ ] 测试小程序端功能
  - [ ] 适配小程序 API（如登录、支付等，如需要）
  - [ ] 配置小程序权限

- [ ] H5 适配
  - [ ] 测试 H5 端功能
  - [ ] 响应式布局优化
  - [ ] 路由模式配置

#### 4.2 功能测试

**任务清单**:
- [ ] 核心功能测试
  - [ ] 登录/注册流程
  - [ ] 商家浏览、搜索、筛选
  - [ ] 商家详情、AI 推荐点评展示
  - [ ] 创建点评、AI 草稿生成
  - [ ] 用户中心、偏好设置

- [ ] 边界情况测试
  - [ ] 网络错误处理
  - [ ] 空数据展示
  - [ ] 未登录状态访问受保护页面
  - [ ] Token 过期处理

**预计时间**: 3-5 天

---

## 📋 三、详细任务清单（按优先级）

### 🔥 第一优先级（核心功能，必须完成）

#### 本周任务（第 1 周）

**Day 1-2: 公共组件开发**
- [x] 创建 `components` 目录
- [x] 开发 `components/shop-card.vue`
- [ ] 开发 `components/review-card.vue`（含 AI 推荐标识）
- [x] 开发 `components/rating-stars.vue`
- [ ] 开发 `components/loading.vue`
- [ ] 开发 `components/empty-state.vue`
- [ ] 开发 `components/ai-recommend-badge.vue`

**Day 3-4: 商家详情页完善**
- [ ] 完善商家信息展示
- [ ] 实现 AI 推荐点评展示（核心功能）
- [ ] 实现点评列表展示
- [ ] 添加创建点评入口
- [ ] 样式优化

**Day 5-7: 创建点评页完善**
- [ ] 实现 AI 草稿生成功能（核心功能）
- [ ] 实现图片上传功能
- [ ] 使用 rating-stars 组件
- [ ] 表单验证完善
- [ ] 样式优化

---

#### 下周任务（第 2 周）

**Day 8-10: 首页完善**
- [ ] 使用 shop-card 组件
- [ ] 实现分页加载功能
- [ ] 搜索防抖处理
- [ ] 样式优化

**Day 11-12: 用户页面优化**
- [ ] 登录/注册页优化
- [ ] 我的订单页优化
- [ ] 我的点评页优化
- [ ] 偏好设置页优化

**Day 13-14: 用户体验优化**
- [ ] 加载状态优化
- [ ] 错误处理优化
- [ ] 交互优化
- [ ] 性能优化

---

### ⭐ 第二优先级（重要功能，建议完成）

#### 第 3 周任务

**Day 15-17: 多端适配**
- [ ] 微信小程序适配
- [ ] H5 适配优化

**Day 18-21: 测试与优化**
- [ ] 功能测试
- [ ] 边界情况测试
- [ ] Bug 修复
- [ ] 性能优化

---

## 🎯 四、关键功能实现要点

### 4.1 AI 推荐点评展示（商家详情页）

**实现步骤**:
1. 在 `pages/shop/detail.vue` 中调用 `recommendReviews` API
2. 使用 `review-card` 组件展示推荐点评
3. 推荐理由使用特殊样式（浅蓝色背景、AI 图标）
4. 推荐理由可展开/收起（长文本时）
5. 如果没有推荐结果，隐藏该区域

**代码示例**:
```vue
<template>
  <!-- AI 推荐点评区域 -->
  <view v-if="recommendedReviews.length > 0" class="ai-recommend-section">
    <view class="section-title">
      <text>AI 为你推荐</text>
      <ai-recommend-badge />
    </view>
    <view 
      v-for="item in recommendedReviews" 
      :key="item.review.id"
      class="recommend-item"
    >
      <review-card 
        :review="item.review" 
        :show-recommend-reason="true"
        :recommend-reason="item.reason"
      />
    </view>
  </view>
  
  <!-- 全部点评列表 -->
  <view class="reviews-section">
    <view class="section-title">全部点评</view>
    <view 
      v-for="review in reviews" 
      :key="review.id"
      class="review-item"
    >
      <review-card :review="review" />
    </view>
  </view>
</template>

<script setup>
import { recommendReviews, listReviews } from '@/api/reviews'
import ReviewCard from '@/components/review-card.vue'
import AiRecommendBadge from '@/components/ai-recommend-badge.vue'

const recommendedReviews = ref([])
const reviews = ref([])

// 加载 AI 推荐点评
const loadRecommendedReviews = async () => {
  try {
    const data = await recommendReviews(shopId.value, { limit: 3 })
    recommendedReviews.value = data || []
  } catch (error) {
    console.error('加载推荐点评失败:', error)
  }
}

// 加载全部点评
const loadReviews = async () => {
  try {
    const res = await listReviews(shopId.value, { page: 0, size: 10 })
    reviews.value = res.content || []
  } catch (error) {
    console.error('加载点评列表失败:', error)
  }
}
</script>
```

---

### 4.2 AI 草稿生成功能（创建点评页）

**实现步骤**:
1. 在 `pages/review/create.vue` 中添加"AI 生成草稿"按钮
2. 点击后调用 `generateAiDraft` API
3. 显示加载状态（Loading 组件，显示"AI 正在生成中..."）
4. 成功后填充到内容输入框
5. 用户可编辑草稿内容
6. 错误处理（生成失败提示）

**代码示例**:
```vue
<template>
  <view class="review-create-container">
    <!-- AI 草稿生成按钮 -->
    <view class="ai-draft-section">
      <button 
        class="btn-ai-draft" 
        @click="handleGenerateDraft"
        :loading="aiDraftLoading"
      >
        <text>✨ AI 生成草稿</text>
      </button>
    </view>
    
    <!-- 表单 -->
    <view class="form">
      <view class="form-item">
        <text class="label">评分</text>
        <rating-stars v-model="rating" :editable="true" />
      </view>
      
      <view class="form-item">
        <text class="label">点评内容</text>
        <textarea 
          v-model="content" 
          placeholder="请输入点评内容..."
          class="textarea"
        />
      </view>
      
      <view class="form-item">
        <button class="btn-primary" @click="handleSubmit">提交</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { generateAiDraft, createReview } from '@/api/reviews'
import RatingStars from '@/components/rating-stars.vue'

const rating = ref(0)
const content = ref('')
const shopId = ref(null)
const aiDraftLoading = ref(false)

// AI 生成草稿
const handleGenerateDraft = async () => {
  if (!shopId.value) {
    uni.showToast({ title: '缺少商家ID', icon: 'none' })
    return
  }
  
  try {
    aiDraftLoading.value = true
    uni.showLoading({ 
      title: 'AI 正在生成中...', 
      mask: true 
    })
    
    const res = await generateAiDraft(shopId.value, {
      preference: '' // 可选：传递用户偏好
    })
    
    if (res.draft) {
      content.value = res.draft
      uni.showToast({ 
        title: '草稿生成成功', 
        icon: 'success' 
      })
    }
  } catch (error) {
    console.error('生成草稿失败:', error)
    uni.showToast({ 
      title: '生成失败，请稍后重试', 
      icon: 'none' 
    })
  } finally {
    aiDraftLoading.value = false
    uni.hideLoading()
  }
}
</script>
```

---

## 📊 五、开发时间表

| 阶段 | 任务 | 预计时间 | 优先级 |
|------|------|----------|--------|
| 阶段一 | 公共组件开发 | 2-3 天 | ⭐⭐⭐ |
| 阶段二 | 商家详情页完善 | 3-4 天 | ⭐⭐⭐ |
| 阶段二 | 创建点评页完善 | 3-4 天 | ⭐⭐⭐ |
| 阶段二 | 首页完善 | 2-3 天 | ⭐⭐⭐ |
| 阶段二 | 用户页面优化 | 2-3 天 | ⭐⭐ |
| 阶段三 | 用户体验优化 | 3-5 天 | ⭐⭐ |
| 阶段四 | 多端适配与测试 | 3-5 天 | ⭐⭐ |
| **总计** | | **18-27 天** | |

---

## 🚀 六、立即行动项（今天开始）

### 今天任务（Day 1）✅ 已完成

1. **创建组件目录结构** ✅
   ```bash
   mkdir -p components
   ```

2. **开发商家卡片组件** (`components/shop-card.vue`) ✅
   - ✅ 参考 Vue 管理端的商家展示
   - ✅ 使用 rating-stars 组件
   - ✅ 样式优化（卡片式设计）
   - ✅ 包含商家名称、分类、地址、评分、人均价格、标签展示
   - ✅ 点击跳转商家详情功能

3. **开发评分组件** (`components/rating-stars.vue`) ✅
   - ✅ 支持只读和可编辑两种模式
   - ✅ 样式优化（星星图标）
   - ✅ 支持显示分数
   - ✅ 支持 v-model 双向绑定

---

### 本周目标（第 1 周）

1. ✅ 完成所有公共组件开发
2. ✅ 完成商家详情页 AI 推荐功能
3. ✅ 完成创建点评页 AI 草稿功能
4. ✅ 完成首页分页加载功能

---

## 📝 七、开发注意事项

### 7.1 代码规范

- 使用 Vue 3 Composition API (`<script setup>`)
- 组件命名使用 PascalCase
- 文件命名使用 kebab-case
- API 函数命名使用 camelCase
- 代码注释完善

### 7.2 错误处理

- 所有 API 调用需有错误处理（try-catch）
- 网络错误需友好提示
- 401 错误需自动跳转登录
- 空数据需显示空状态组件

### 7.3 性能优化

- 图片使用懒加载
- 列表使用分页加载
- API 请求使用防抖/节流
- 避免不必要的重复请求

### 7.4 用户体验

- 加载状态需明确提示
- 操作反馈需及时（成功/失败提示）
- 页面跳转需流畅
- AI 功能需有加载动画

---

## 📚 八、参考资源

### 8.1 项目内参考

- **Vue 管理端代码**: `Vue/src/` - 参考 API 封装、组件实现、页面结构
- **后端 API 文档**: `Java/API测试用例-*.md` - 了解接口详细定义
- **项目进度报告**: `项目进度报告与下一步计划.md` - 了解项目整体情况

### 8.2 外部资源

- **UniApp 官方文档**: https://uniapp.dcloud.net.cn/
- **Vue 3 文档**: https://cn.vuejs.org/
- **Pinia 文档**: https://pinia.vuejs.org/zh/

---

**计划制定时间**: 2026-1-13  
**预计完成时间**: 2-3 周  
**下一步**: 立即开始公共组件开发，然后完善核心页面功能

