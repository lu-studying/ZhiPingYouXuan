<template>
  <view class="review-create-container">
    <view class="form">
      <view class="form-item">
        <text class="label">评分</text>
        <view class="rating">
          <text 
            v-for="i in 5" 
            :key="i"
            class="star"
            :class="{ active: rating >= i }"
            @click="rating = i"
          >★</text>
        </view>
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
import { ref, onMounted } from 'vue'
import { createReview } from '@/api/reviews'

const rating = ref(0)
const content = ref('')
const shopId = ref(null)

onMounted(() => {
  // 从路由参数获取 shopId
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  shopId.value = currentPage.options?.shopId
})

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
  
  try {
    await createReview(shopId.value, {
      rating: rating.value,
      content: content.value
    })
    
    uni.showToast({ title: '提交成功', icon: 'success' })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error) {
    console.error('提交点评失败:', error)
  }
}
</script>

<style scoped>
.review-create-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx;
}

.form {
  background-color: #fff;
  padding: 30rpx;
  border-radius: 10rpx;
}

.form-item {
  margin-bottom: 40rpx;
}

.label {
  display: block;
  font-size: 28rpx;
  color: #333;
  margin-bottom: 20rpx;
}

.rating {
  display: flex;
  gap: 10rpx;
}

.star {
  font-size: 50rpx;
  color: #ddd;
  cursor: pointer;
}

.star.active {
  color: #ffd700;
}

.textarea {
  width: 100%;
  min-height: 200rpx;
  padding: 20rpx;
  border: 1rpx solid #ddd;
  border-radius: 10rpx;
  font-size: 28rpx;
}

.btn-primary {
  width: 100%;
  height: 80rpx;
  background-color: #3c9cff;
  color: #fff;
  border-radius: 10rpx;
  line-height: 80rpx;
  text-align: center;
  border: none;
}
</style>

