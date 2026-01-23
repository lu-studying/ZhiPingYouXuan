<template>
  <view class="home-container">
    <view class="search-bar">
      <input 
        class="search-input" 
        placeholder="搜索商家、地址..." 
        v-model="searchKeyword"
        @input="handleSearchInput"
        @confirm="handleSearch"
      />
    </view>
    
    <view class="category-tabs">
      <view 
        v-for="category in categories" 
        :key="category"
        class="tab-item"
        :class="{ active: selectedCategory === category }"
        @click="selectCategory(category)"
      >
        {{ category }}
      </view>
    </view>
    
    <view class="shop-list">
      <shop-card 
        v-for="shop in shops" 
        :key="shop.id"
        :shop="shop"
        :recommended-menus="recommendedMenusMap[shop.id] || []"
      />
      
      <empty-state 
        v-if="shops && shops.length === 0 && !loading" 
        text="暂无商家数据"
        icon="🏪"
      />
    </view>
    
    <loading v-if="loading" text="加载中..." />
    
    <loading v-if="loadingMore" text="加载更多..." size="small" />
    
    <view v-if="!hasMore && shops.length > 0" class="no-more">
      <text>没有更多了</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app'
import { listShops } from '@/api/shops'
import { getRecommendedMenus } from '@/api/menus'
import ShopCard from '@/components/shop-card.vue'
import Loading from '@/components/loading.vue'
import EmptyState from '@/components/empty-state.vue'
import { debounce } from '@/utils/debounce'

const searchKeyword = ref('')
const selectedCategory = ref('全部')
const shops = ref([])
// shopId -> 推荐菜单数组
const recommendedMenusMap = ref({})
const loading = ref(false)
const page = ref(0)
const hasMore = ref(true)
const loadingMore = ref(false)

const categories = ['全部', '火锅', '川菜', '日料', '西餐', '咖啡', '其他']

/**
 * 加载商家列表
 */
const loadShops = async (isLoadMore = false) => {
  if (loading.value || loadingMore.value) return
  
  if (!isLoadMore) {
    page.value = 0
    hasMore.value = true
    loading.value = true
  } else {
    if (!hasMore.value) return
    loadingMore.value = true
  }
  
  try {
    const params = {
      page: page.value,
      size: 10,
      keyword: searchKeyword.value || undefined,
      category: selectedCategory.value === '全部' ? undefined : selectedCategory.value
    }
    const res = await listShops(params)
    
    const newShops = Array.isArray(res?.content) ? res.content : (Array.isArray(res) ? res : [])
    const total = res?.total || 0
    
    if (isLoadMore) {
      shops.value = [...shops.value, ...newShops]
    } else {
      shops.value = newShops
      // 重置推荐菜单缓存
      recommendedMenusMap.value = {}
    }

    // 异步加载每个商家的推荐菜单（最多 2 个）
    loadRecommendedMenusForShops(newShops)
    
    // 判断是否还有更多
    hasMore.value = shops.value.length < total
    page.value++
  } catch (error) {
    console.error('加载商家列表失败:', error)
    if (!isLoadMore) {
      shops.value = []
    }
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

/**
 * 为一批商家加载推荐菜单
 */
const loadRecommendedMenusForShops = async (shopList) => {
  if (!Array.isArray(shopList) || shopList.length === 0) return

  const tasks = shopList.map(async (shop) => {
    if (!shop?.id) return
    try {
      const menus = await getRecommendedMenus(shop.id, 2)
      if (Array.isArray(menus) && menus.length > 0) {
        recommendedMenusMap.value = {
          ...recommendedMenusMap.value,
          [shop.id]: menus
        }
      }
    } catch (e) {
      console.error('加载推荐菜单失败:', e)
    }
  })

  await Promise.all(tasks)
}

/**
 * 处理搜索输入（带防抖）
 */
const handleSearchInput = debounce(() => {
  loadShops(false)
}, 500)

/**
 * 处理搜索确认
 */
const handleSearch = () => {
  loadShops(false)
}

/**
 * 选择分类
 */
const selectCategory = (category) => {
  selectedCategory.value = category
  loadShops(false)
}

// 商家卡片组件已内置跳转功能，此函数可保留用于其他场景
const goToShopDetail = (shopId) => {
  uni.navigateTo({
    url: `/pages/shop/detail?shopId=${shopId}`
  })
}

onMounted(() => {
  loadShops()
})

// 下拉刷新
onPullDownRefresh(() => {
  loadShops(false).finally(() => {
    uni.stopPullDownRefresh()
  })
})

// 上拉加载更多
onReachBottom(() => {
  if (hasMore.value && !loadingMore.value) {
    loadShops(true)
  }
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.search-bar {
  padding: 20rpx;
  background-color: #fff;
}

.search-input {
  height: 70rpx;
  background-color: #f5f5f5;
  border-radius: 35rpx;
  padding: 0 30rpx;
  font-size: 28rpx;
}

.category-tabs {
  display: flex;
  padding: 20rpx;
  background-color: #fff;
  border-bottom: 1rpx solid #eee;
}

.tab-item {
  padding: 10rpx 20rpx;
  margin-right: 20rpx;
  font-size: 28rpx;
  color: #666;
  border-radius: 20rpx;
}

.tab-item.active {
  background-color: #3c9cff;
  color: #fff;
}

.shop-list {
  padding: 20rpx;
}

.empty-state {
  text-align: center;
  padding: 100rpx 0;
  color: #999;
}

.loading {
  text-align: center;
  padding: 40rpx 0;
  color: #999;
}

.loading-more {
  text-align: center;
  padding: 30rpx 0;
  color: #999;
  font-size: 26rpx;
}

.no-more {
  text-align: center;
  padding: 30rpx 0;
  color: #ccc;
  font-size: 24rpx;
}
</style>

