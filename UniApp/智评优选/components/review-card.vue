<template>
  <view class="review-card">
    <!-- AI 推荐标识 -->
    <view v-if="showRecommendReason && recommendReason" class="ai-recommend-badge">
      <text class="ai-icon">✨</text>
      <text class="ai-text">精确推荐点评</text>
    </view>
    
    <!-- 用户信息 -->
    <view class="review-header">
      <view class="user-info">
        <view class="avatar">
          <text class="avatar-text">{{ getUserInitial(getDisplayName()) }}</text>
        </view>
        <view class="user-details">
          <text class="username">{{ getDisplayName() }}</text>
          <text class="time">{{ formatTime(review.createdAt) }}</text>
        </view>
      </view>
      <view class="rating-wrapper">
        <rating-stars :rating="review.rating" />
      </view>
    </view>
    
    <!-- 点评内容 -->
    <view class="review-content">
      <text class="content-text">{{ review.content }}</text>
    </view>
    
    <!-- 推荐理由（AI 推荐时显示） -->
    <view v-if="showRecommendReason && recommendReason" class="recommend-reason">
      <view class="reason-header">
        <text class="reason-icon">💡</text>
        <text class="reason-title">推荐理由</text>
      </view>
      <view class="reason-content">
        <text 
          class="reason-text" 
          :class="{ 'reason-text-collapsed': !isExpanded && shouldShowExpand }"
        >
          {{ recommendReason }}
        </text>
        <view 
          v-if="shouldShowExpand" 
          class="expand-toggle"
          @click="toggleExpand"
        >
          <text class="expand-text">{{ isExpanded ? '收起' : '展开' }}</text>
          <text class="expand-icon" :class="{ 'expanded': isExpanded }">▼</text>
        </view>
      </view>
    </view>
    
    <!-- 图片展示 -->
    <view v-if="reviewImages && reviewImages.length > 0" class="review-images">
      <view 
        v-for="(image, index) in reviewImages" 
        :key="index"
        class="image-item"
        @click="previewImage(image, index)"
      >
        <image :src="getImageUrl(image)" mode="aspectFill" class="image" />
      </view>
    </view>
    
    <!-- 点赞区域 -->
    <view class="review-footer">
      <view class="like-section" @click="handleLike">
        <text class="like-icon" :class="{ liked: isLiked }">❤️</text>
        <text class="like-count">{{ review.likeCount || 0 }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import RatingStars from './rating-stars.vue'
import { formatRelativeTime, getImageUrl } from '@/utils/format'
import { likeReview } from '@/api/reviews'

/**
 * 点评卡片组件
 * 
 * Props:
 * - review: 点评对象
 *   - id: 点评ID
 *   - userId: 用户ID
 *   - user: 用户对象（可选，包含 nickname、username）
 *   - rating: 评分（1-5）
 *   - content: 点评内容
 *   - images: 图片列表（JSON字符串或数组）
 *   - likeCount: 点赞数
 *   - createdAt: 创建时间
 * - showRecommendReason: 是否显示推荐理由（默认 false）
 * - recommendReason: 推荐理由文本（可选）
 * - shopId: 商家ID（用于点赞功能）
 */
const props = defineProps({
  review: {
    type: Object,
    required: true,
    validator: (value) => {
      return value && value.id
    }
  },
  showRecommendReason: {
    type: Boolean,
    default: false
  },
  recommendReason: {
    type: String,
    default: ''
  },
  shopId: {
    type: [Number, String],
    default: null
  }
})

const emit = defineEmits(['like'])

const isLiked = ref(false)
const isExpanded = ref(false) // 推荐理由是否展开

/**
 * 解析图片列表
 */
const reviewImages = computed(() => {
  if (!props.review.images) {
    return []
  }
  
  // 如果是字符串，尝试解析 JSON
  if (typeof props.review.images === 'string') {
    try {
      const parsed = JSON.parse(props.review.images)
      return Array.isArray(parsed) ? parsed : []
    } catch {
      return []
    }
  }
  
  // 如果是数组，直接返回
  if (Array.isArray(props.review.images)) {
    return props.review.images
  }
  
  return []
})

/**
 * 格式化时间
 */
const formatTime = (date) => {
  if (!date) {
    return ''
  }
  return formatRelativeTime(date)
}

/**
 * 获取显示名称
 * 优先级：userNickname > user.nickname > user.username > userId > '匿名用户'
 */
const getDisplayName = () => {
  // 优先使用后端返回的 userNickname（从 JOIN 查询获取）
  if (props.review.userNickname) {
    return props.review.userNickname
  }
  // 兼容旧数据格式（如果有嵌套的 user 对象）
  if (props.review.user?.nickname) {
    return props.review.user.nickname
  }
  if (props.review.user?.username) {
    return props.review.user.username
  }
  // 如果都没有，显示用户ID
  if (props.review.userId) {
    return `用户${props.review.userId}`
  }
  return '匿名用户'
}

/**
 * 获取用户昵称首字母
 */
const getUserInitial = (name) => {
  if (!name) {
    return '用'
  }
  // 如果是"用户XXX"格式，提取数字
  const match = name.match(/用户(\d+)/)
  if (match) {
    return match[1].charAt(0)
  }
  return name.charAt(0).toUpperCase()
}

/**
 * 预览图片
 */
const previewImage = (current, index) => {
  // 预览时需要转换为完整URL
  const fullUrls = reviewImages.value.map(img => getImageUrl(img))
  uni.previewImage({
    urls: fullUrls,
    current: getImageUrl(current)
  })
}

/**
 * 判断是否需要显示展开/收起功能
 * 当推荐理由文本超过约3行（约90个字符）时显示
 */
const shouldShowExpand = computed(() => {
  if (!props.recommendReason) {
    return false
  }
  // 简单判断：如果文本长度超过90个字符，认为需要展开/收起
  // 实际可以根据实际显示效果调整
  return props.recommendReason.length > 90
})

/**
 * 切换展开/收起状态
 */
const toggleExpand = () => {
  isExpanded.value = !isExpanded.value
}

/**
 * 处理点赞
 */
const handleLike = async () => {
  if (!props.shopId) {
    uni.showToast({ title: '缺少商家ID', icon: 'none' })
    return
  }
  
  if (isLiked.value) {
    return
  }
  
  try {
    await likeReview(props.shopId, props.review.id)
    isLiked.value = true
    props.review.likeCount = (props.review.likeCount || 0) + 1
    emit('like', props.review.id)
  } catch (error) {
    console.error('点赞失败:', error)
    uni.showToast({ title: '点赞失败', icon: 'none' })
  }
}
</script>

<style scoped>
.review-card {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.08);
  position: relative;
}

/* AI 推荐标识 */
.ai-recommend-badge {
  position: absolute;
  top: 20rpx;
  right: 20rpx;
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 8rpx 16rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20rpx;
  z-index: 10;
}

.ai-icon {
  font-size: 24rpx;
}

.ai-text {
  font-size: 22rpx;
  color: #fff;
  font-weight: 500;
}

/* 用户信息 */
.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20rpx;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20rpx;
  flex: 1;
}

.avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-text {
  font-size: 32rpx;
  color: #fff;
  font-weight: bold;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  flex: 1;
}

.username {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
}

.time {
  font-size: 24rpx;
  color: #999;
}

.rating-wrapper {
  margin-left: 20rpx;
}

/* 点评内容 */
.review-content {
  margin-bottom: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.content-text {
  font-size: 28rpx;
  color: #333;
  line-height: 1.8;
  word-break: break-word;
}

/* 推荐理由 */
.recommend-reason {
  margin: 20rpx 0;
  padding: 20rpx;
  background: linear-gradient(135deg, #e6f3ff 0%, #d6e9ff 100%);
  border-radius: 12rpx;
  border-left: 4rpx solid #3c9cff;
}

.reason-header {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-bottom: 12rpx;
}

.reason-icon {
  font-size: 28rpx;
}

.reason-title {
  font-size: 26rpx;
  color: #3c9cff;
  font-weight: 500;
}

.reason-content {
  position: relative;
}

.reason-text {
  font-size: 26rpx;
  color: #666;
  line-height: 1.6;
  word-break: break-word;
  transition: max-height 0.3s ease;
}

/* 收起状态：限制显示3行 */
.reason-text-collapsed {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 展开/收起按钮 */
.expand-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6rpx;
  margin-top: 12rpx;
  padding: 8rpx 0;
  cursor: pointer;
  user-select: none;
  transition: opacity 0.2s;
}

.expand-toggle:active {
  opacity: 0.7;
}

.expand-text {
  font-size: 24rpx;
  color: #3c9cff;
  font-weight: 500;
}

.expand-icon {
  font-size: 20rpx;
  color: #3c9cff;
  transition: transform 0.3s ease;
}

.expand-icon.expanded {
  transform: rotate(180deg);
}

/* 图片展示 */
.review-images {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-bottom: 20rpx;
}

.image-item {
  width: 200rpx;
  height: 200rpx;
  border-radius: 12rpx;
  overflow: hidden;
}

.image {
  width: 100%;
  height: 100%;
}

/* 点赞区域 */
.review-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.like-section {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 8rpx 16rpx;
  border-radius: 20rpx;
  background-color: #f5f5f5;
  transition: background-color 0.2s;
}

.like-section:active {
  background-color: #e0e0e0;
}

.like-icon {
  font-size: 32rpx;
  transition: transform 0.2s;
}

.like-icon.liked {
  transform: scale(1.2);
}

.like-count {
  font-size: 24rpx;
  color: #666;
}
</style>

