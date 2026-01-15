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
        <text class="label">图片（最多9张）</text>
        <view class="image-upload-section">
          <view class="image-list">
            <view 
              v-for="(image, index) in images" 
              :key="index"
              class="image-item"
            >
              <image :src="image" mode="aspectFill" class="image-preview" @click="previewImage(index)" />
              <view class="image-delete" @click="removeImage(index)">
                <text class="delete-icon">×</text>
              </view>
            </view>
            <view 
              v-if="images.length < 9" 
              class="image-add-btn" 
              @click="chooseImage"
            >
              <text class="add-icon">+</text>
              <text class="add-text">添加图片</text>
            </view>
          </view>
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
const images = ref([])
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
    
    // 处理图片数据：将数组转换为 JSON 字符串
    const imagesData = images.value.length > 0 ? JSON.stringify(images.value) : undefined
    
    await createReview(shopId.value, {
      rating: rating.value,
      content: content.value.trim(),
      images: imagesData
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

/**
 * 选择图片
 */
const chooseImage = () => {
  const maxCount = 9 - images.value.length
  if (maxCount <= 0) {
    uni.showToast({ title: '最多只能上传9张图片', icon: 'none' })
    return
  }
  
  uni.chooseImage({
    count: maxCount,
    sizeType: ['compressed'], // 压缩图
    sourceType: ['album', 'camera'], // 相册和相机
    success: (res) => {
      // 将选择的图片添加到列表
      const tempFilePaths = res.tempFilePaths
      images.value = [...images.value, ...tempFilePaths]
      
      // 注意：这里使用的是本地临时路径
      // 如果需要上传到服务器，需要调用上传接口获取URL
      // 目前先使用本地路径，后续可以完善上传功能
    },
    fail: (error) => {
      console.error('选择图片失败:', error)
      uni.showToast({ title: '选择图片失败', icon: 'none' })
    }
  })
}

/**
 * 预览图片
 */
const previewImage = (index) => {
  uni.previewImage({
    urls: images.value,
    current: images.value[index]
  })
}

/**
 * 删除图片
 */
const removeImage = (index) => {
  images.value.splice(index, 1)
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

/* 图片上传区域 */
.image-upload-section {
  margin-top: 20rpx;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.image-item {
  position: relative;
  width: 200rpx;
  height: 200rpx;
  border-radius: 12rpx;
  overflow: hidden;
  background-color: #f5f5f5;
}

.image-preview {
  width: 100%;
  height: 100%;
}

.image-delete {
  position: absolute;
  top: 0;
  right: 0;
  width: 48rpx;
  height: 48rpx;
  background-color: rgba(0, 0, 0, 0.6);
  border-radius: 0 0 0 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.delete-icon {
  color: #fff;
  font-size: 36rpx;
  font-weight: bold;
  line-height: 1;
}

.image-add-btn {
  width: 200rpx;
  height: 200rpx;
  border: 2rpx dashed #ddd;
  border-radius: 12rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #fafafa;
  transition: border-color 0.2s;
}

.image-add-btn:active {
  border-color: #3c9cff;
  background-color: #f0f7ff;
}

.add-icon {
  font-size: 60rpx;
  color: #999;
  line-height: 1;
  margin-bottom: 10rpx;
}

.add-text {
  font-size: 24rpx;
  color: #999;
}
</style>

