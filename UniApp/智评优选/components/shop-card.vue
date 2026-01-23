<template>
  <view class="shop-card" @click="handleClick">
    <view class="shop-header">
      <view class="shop-info">
        <text class="shop-name">{{ shop.name }}</text>
        <text class="shop-category">{{ shop.category }}</text>
      </view>
      <view v-if="shop.avgScore !== null && shop.avgScore !== undefined" class="rating-wrapper">
        <rating-stars :rating="shop.avgScore" :show-score="true" />
      </view>
    </view>
    
    <view class="shop-body">
      <text class="shop-address">{{ shop.address || '地址未知' }}</text>
      <view v-if="shop.avgPrice !== null && shop.avgPrice !== undefined" class="price-info">
        <text class="price-text">{{ formatAvgPrice(shop.avgPrice) }}</text>
      </view>
    </view>

    <!-- 推荐菜单简要展示（无数据也展示占位） -->
    <view class="recommended-menus">
      <text class="recommended-label">推荐菜：</text>
      <scroll-view
        v-if="hasRecommendedMenus"
        class="recommended-scroll"
        scroll-x="true"
        show-scrollbar="false"
      >
        <view
          v-for="(item, index) in limitedRecommendedMenus"
          :key="item.id || index"
          class="menu-thumb"
        >
          <view class="menu-thumb-image-wrapper">
            <image
              v-if="item.image"
              class="menu-thumb-image"
              :src="item.image"
              mode="aspectFill"
            />
            <view v-else class="menu-thumb-placeholder">
              <text class="menu-thumb-icon">🍽️</text>
            </view>
          </view>
          <text class="menu-thumb-name">{{ item.name }}</text>
          <text class="menu-thumb-price">{{ formatPrice(item.price) }}</text>
        </view>
      </scroll-view>
      <text v-else class="recommended-placeholder">商家暂未设置推荐菜品</text>
    </view>
    
    <view v-if="shop.tags && shop.tags.length > 0" class="shop-tags">
      <view 
        v-for="tag in shop.tags.slice(0, 3)" 
        :key="tag.id"
        class="tag-item"
      >
        <text>{{ tag.name }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'
import RatingStars from './rating-stars.vue'
import { formatAvgPrice, formatPrice } from '@/utils/format'

/**
 * 商家卡片组件
 * 
 * Props:
 * - shop: 商家对象
 *   - id: 商家ID
 *   - name: 商家名称
 *   - category: 商家分类
 *   - address: 商家地址
 *   - avgScore: 平均评分（0-5）
 *   - avgPrice: 人均价格
 *   - tags: 标签数组（可选）
 */
const props = defineProps({
  shop: {
    type: Object,
    required: true,
    validator: (value) => {
      return value && value.id && value.name
    }
  },
  // 首页传入的推荐菜单简要信息
  recommendedMenus: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['click'])

// 是否有推荐菜
const hasRecommendedMenus = computed(() => {
  return Array.isArray(props.recommendedMenus) && props.recommendedMenus.length > 0
})

// 只展示前 2~3 个推荐菜名，避免一行太长
const limitedRecommendedMenus = computed(() => {
  if (!props.recommendedMenus) return []
  return props.recommendedMenus.slice(0, 3)
})

/**
 * 处理卡片点击事件
 */
const handleClick = () => {
  emit('click', props.shop)
  
  // 跳转到商家详情页
  uni.navigateTo({
    url: `/pages/shop/detail?shopId=${props.shop.id}`
  })
}
</script>

<style scoped>
.shop-card {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.08);
  transition: transform 0.2s, box-shadow 0.2s;
}

.shop-card:active {
  transform: scale(0.98);
  box-shadow: 0 1rpx 4rpx rgba(0, 0, 0, 0.12);
}

.shop-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20rpx;
}

.shop-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.shop-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  line-height: 1.4;
}

.shop-category {
  font-size: 24rpx;
  color: #999;
}

.rating-wrapper {
  display: flex;
  align-items: center;
  margin-left: 20rpx;
}

.shop-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.shop-address {
  flex: 1;
  font-size: 26rpx;
  color: #666;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price-info {
  margin-left: 20rpx;
}

.price-text {
  font-size: 26rpx;
  color: #ff6b35;
  font-weight: 500;
}

/* 推荐菜单简要区域 */
.recommended-menus {
  padding-top: 16rpx;
  border-top: 1rpx solid #f0f0f0;
  display: flex;
  align-items: flex-start;
  font-size: 24rpx;
  color: #666;
}

.recommended-label {
  color: #ff9f43;
  font-weight: 500;
  margin-right: 8rpx;
}

.recommended-scroll {
  flex: 1;
  overflow: hidden;
  white-space: nowrap;
}

.menu-thumb {
  display: inline-flex;
  flex-direction: column;
  align-items: flex-start;
  width: 180rpx;
  margin-right: 16rpx;
}

.menu-thumb-image-wrapper {
  width: 180rpx;
  height: 120rpx;
  border-radius: 12rpx;
  overflow: hidden;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.menu-thumb-image {
  width: 100%;
  height: 100%;
}

.menu-thumb-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.menu-thumb-icon {
  font-size: 40rpx;
}

.menu-thumb-name {
  margin-top: 8rpx;
  font-size: 24rpx;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.menu-thumb-price {
  margin-top: 4rpx;
  font-size: 22rpx;
  color: #ff6b35;
}

.recommended-placeholder {
  font-size: 24rpx;
  color: #aaa;
  flex: 1;
}

.shop-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.tag-item {
  padding: 6rpx 16rpx;
  background-color: #f5f5f5;
  border-radius: 20rpx;
  font-size: 22rpx;
  color: #666;
}

.tag-item text {
  font-size: 22rpx;
}
</style>

