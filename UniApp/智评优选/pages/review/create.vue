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
              <image :src="getImageUrl(image)" mode="aspectFill" class="image-preview" @click="previewImage(index)" />
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
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { createReview, startAiDraftTask, getAiDraftTask } from '@/api/reviews'
import { uploadFile } from '@/api/files'
import RatingStars from '@/components/rating-stars.vue'
import { getImageUrl } from '@/utils/format'

const rating = ref(0)
const content = ref('')
const images = ref([])
const shopId = ref(null)
const aiDraftLoading = ref(false)
const submitting = ref(false)
const aiDraftTaskId = ref('')
let aiDraftPollTimer = null

onMounted(() => {
  // 从路由参数获取 shopId
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  shopId.value = currentPage.options?.shopId
})

onBeforeUnmount(() => {
  if (aiDraftPollTimer) {
    clearInterval(aiDraftPollTimer)
    aiDraftPollTimer = null
  }
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
    // 1) 启动异步任务：立刻返回，不阻塞用户继续上传图片/编辑
    const startRes = await startAiDraftTask(shopId.value, {
      preference: '' // 可选：传递用户偏好
    })
    aiDraftTaskId.value = startRes?.taskId || ''
    if (!aiDraftTaskId.value) {
      throw new Error('启动任务失败')
    }

    uni.showToast({
      title: '已开始生成，可继续上传图片',
      icon: 'none',
      duration: 2000
    })

    // 2) 轮询任务状态，完成后自动回填
    if (aiDraftPollTimer) {
      clearInterval(aiDraftPollTimer)
      aiDraftPollTimer = null
    }
    let attempts = 0
    const maxAttempts = 120 // 约 2 分钟（1s * 120）
    aiDraftPollTimer = setInterval(async () => {
      attempts += 1
      if (attempts > maxAttempts) {
        clearInterval(aiDraftPollTimer)
        aiDraftPollTimer = null
        aiDraftLoading.value = false
        uni.showToast({ title: '生成超时，可稍后再试', icon: 'none' })
        return
      }
      try {
        const statusRes = await getAiDraftTask(shopId.value, aiDraftTaskId.value)
        const status = statusRes?.status
        if (status === 'succeeded') {
          clearInterval(aiDraftPollTimer)
          aiDraftPollTimer = null
          aiDraftLoading.value = false
          if (statusRes?.draft) {
            content.value = statusRes.draft
            uni.showToast({ title: '草稿已生成', icon: 'success', duration: 1500 })
          } else {
            uni.showToast({ title: '生成完成但无内容', icon: 'none' })
          }
        } else if (status === 'failed') {
          clearInterval(aiDraftPollTimer)
          aiDraftPollTimer = null
          aiDraftLoading.value = false
          uni.showToast({ title: statusRes?.error || '生成失败', icon: 'none' })
        } else if (status === 'not_found') {
          clearInterval(aiDraftPollTimer)
          aiDraftPollTimer = null
          aiDraftLoading.value = false
          uni.showToast({ title: '任务已过期，请重试', icon: 'none' })
        }
        // queued/running: 继续等
      } catch (e) {
        // 轮询过程中允许网络偶发失败，不打断用户操作
        if (attempts % 10 === 0) {
          console.warn('轮询任务状态失败:', e)
        }
      }
    }, 1000)
  } catch (error) {
    console.error('生成草稿失败:', error)
    uni.showToast({ 
      title: '生成失败，请稍后重试', 
      icon: 'none',
      duration: 2000
    })
  } finally {
    // 注意：异步模式下 loading 的关闭交给轮询完成/失败时处理
    // 这里不要强制置 false，否则按钮文案会立刻恢复，看起来像没在生成
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
 * 选择图片并上传
 */
const chooseImage = async () => {
  const maxCount = 9 - images.value.length
  if (maxCount <= 0) {
    uni.showToast({ title: '最多只能上传9张图片', icon: 'none' })
    return
  }
  
  uni.chooseImage({
    count: maxCount,
    sizeType: ['compressed'], // 压缩图
    sourceType: ['album', 'camera'], // 相册和相机
    success: async (res) => {
      // 显示上传进度
      uni.showLoading({ title: '上传中...', mask: true })
      
      try {
        // 上传所有选择的图片
        const uploadPromises = res.tempFilePaths.map(filePath => 
          uploadFile(filePath, 'review/')
        )
        
        const uploadResults = await Promise.all(uploadPromises)
        
        // 获取上传后的相对路径（后端返回的路径，如 /uploads/review/xxx.jpg）
        const uploadedUrls = uploadResults.map(result => result.relativeUrl || result.url)
        
        // 添加到图片列表（保存相对路径，提交时使用）
        images.value = [...images.value, ...uploadedUrls]
        
        uni.hideLoading()
        uni.showToast({ title: '上传成功', icon: 'success', duration: 1500 })
      } catch (error) {
        console.error('上传图片失败:', error)
        uni.hideLoading()
        uni.showToast({ title: '上传失败，请重试', icon: 'none' })
      }
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
  // 预览时需要转换为完整URL
  const fullUrls = images.value.map(img => getImageUrl(img))
  uni.previewImage({
    urls: fullUrls,
    current: fullUrls[index]
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

