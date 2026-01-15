<template>
  <view class="review-create-container">
    <!-- AI 草稿生成按钮 -->
    <view class="ai-draft-section">
      <button 
        class="btn-ai-draft" 
        @click="handleGenerateDraft"
        :disabled="aiDraftLoading"
      >
        <text v-if="!aiDraftLoading">✨ AI 生成草稿</text>
        <text v-else>AI 正在生成中...</text>
      </button>
    </view>
    
    <view class="form">
      <view class="form-item">
        <text class="label">评分</text>
        <rating-stars 
          :rating="rating" 
          :editable="true" 
          @update:rating="(value) => { rating = value; console.log('评分更新为:', value) }" 
        />
      </view>
      
      <view class="form-item">
        <text class="label">点评内容</text>
        <textarea 
          v-model="content" 
          placeholder="请输入点评内容..."
          class="textarea"
          :maxlength="500"
        />
        <view class="char-count">
          <text>{{ content.length }}/500</text>
        </view>
      </view>
      
      <view class="form-item">
        <button 
          class="btn-primary" 
          @click="handleSubmit"
          :disabled="submitting"
        >
          <text v-if="!submitting">提交</text>
          <text v-else>提交中...</text>
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { createReview, generateAiDraft } from '@/api/reviews'
import RatingStars from '@/components/rating-stars.vue'

const rating = ref(0)
const content = ref('')
const shopId = ref(null)
const aiDraftLoading = ref(false)
const submitting = ref(false)

onMounted(() => {
  // 从路由参数获取 shopId
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  shopId.value = currentPage.options?.shopId
})

/**
 * AI 生成草稿
 */
const handleGenerateDraft = async () => {
  if (!shopId.value) {
    uni.showToast({ title: '缺少商家ID', icon: 'none' })
    return
  }
  
  if (aiDraftLoading.value) {
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
        icon: 'success',
        duration: 2000
      })
    } else {
      uni.showToast({ 
        title: '生成失败，请重试', 
        icon: 'none' 
      })
    }
  } catch (error) {
    console.error('生成草稿失败:', error)
    uni.showToast({ 
      title: '生成失败，请稍后重试', 
      icon: 'none',
      duration: 2000
    })
  } finally {
    aiDraftLoading.value = false
    uni.hideLoading()
  }
}

/**
 * 提交点评
 */
const handleSubmit = async () => {
  if (!shopId.value) {
    uni.showToast({ title: '缺少商家ID', icon: 'none' })
    return
  }
  
  if (rating.value === 0) {
    uni.showToast({ title: '请选择评分', icon: 'none' })
    return
  }
  
  if (!content.value.trim()) {
    uni.showToast({ title: '请输入点评内容', icon: 'none' })
    return
  }
  
  if (submitting.value) {
    return
  }
  
  try {
    submitting.value = true
    await createReview(shopId.value, {
      rating: rating.value,
      content: content.value.trim()
    })
    
    uni.showToast({ title: '提交成功', icon: 'success' })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error) {
    console.error('提交点评失败:', error)
    uni.showToast({ 
      title: error.message || '提交失败，请稍后重试', 
      icon: 'none' 
    })
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.review-create-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx;
}

/* AI 草稿生成按钮区域 */
.ai-draft-section {
  margin-bottom: 20rpx;
}

.btn-ai-draft {
  width: 100%;
  height: 88rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 16rpx;
  line-height: 88rpx;
  text-align: center;
  border: none;
  font-size: 30rpx;
  font-weight: 500;
  box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
  transition: transform 0.2s;
}

.btn-ai-draft:active {
  transform: scale(0.98);
}

.btn-ai-draft[disabled] {
  opacity: 0.6;
}

.form {
  background-color: #fff;
  padding: 30rpx;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.08);
}

.form-item {
  margin-bottom: 40rpx;
}

.label {
  display: block;
  font-size: 28rpx;
  color: #333;
  margin-bottom: 20rpx;
  font-weight: 500;
}

.textarea {
  width: 100%;
  min-height: 300rpx;
  padding: 20rpx;
  border: 1rpx solid #ddd;
  border-radius: 12rpx;
  font-size: 28rpx;
  line-height: 1.6;
  box-sizing: border-box;
}

.textarea:focus {
  border-color: #3c9cff;
}

.char-count {
  text-align: right;
  margin-top: 10rpx;
  font-size: 24rpx;
  color: #999;
}

.btn-primary {
  width: 100%;
  height: 88rpx;
  background-color: #3c9cff;
  color: #fff;
  border-radius: 12rpx;
  line-height: 88rpx;
  text-align: center;
  border: none;
  font-size: 30rpx;
  font-weight: 500;
  transition: background-color 0.2s;
}

.btn-primary:active {
  background-color: #2d7ce6;
}

.btn-primary[disabled] {
  opacity: 0.6;
  background-color: #ccc;
}
</style>

