# 公共组件说明

## 组件列表

### 1. rating-stars.vue - 评分组件

评分星级显示组件，支持只读和可编辑两种模式。

#### Props

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| rating | Number | 0 | 评分值（0-5） |
| editable | Boolean | false | 是否可编辑 |
| showScore | Boolean | false | 是否显示分数 |

#### Events

| 事件名 | 说明 | 参数 |
|--------|------|------|
| update:rating | 评分值更新时触发（支持 v-model） | (value: number) |
| change | 评分值改变时触发 | (value: number) |

#### 使用示例

```vue
<template>
  <!-- 只读模式 -->
  <rating-stars :rating="4.5" :show-score="true" />
  
  <!-- 可编辑模式（支持 v-model） -->
  <rating-stars v-model="rating" :editable="true" />
</template>

<script setup>
import { ref } from 'vue'
import RatingStars from '@/components/rating-stars.vue'

const rating = ref(0)
</script>
```

---

### 2. shop-card.vue - 商家卡片组件

商家信息展示卡片组件，用于列表展示。

#### Props

| 属性名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| shop | Object | 是 | 商家对象 |

**shop 对象结构**:
```typescript
{
  id: number,              // 商家ID（必填）
  name: string,            // 商家名称（必填）
  category: string,        // 商家分类
  address: string,         // 商家地址
  rating: number,          // 评分（0-5）
  averagePrice: number,    // 人均价格
  tags: Array<{           // 标签数组（可选）
    id: number,
    name: string
  }>
}
```

#### Events

| 事件名 | 说明 | 参数 |
|--------|------|------|
| click | 卡片点击时触发 | (shop: Object) |

#### 功能特性

- ✅ 自动跳转到商家详情页
- ✅ 显示商家基本信息（名称、分类、地址）
- ✅ 显示评分（使用 rating-stars 组件）
- ✅ 显示人均价格
- ✅ 显示标签（最多3个）
- ✅ 卡片式设计，支持点击反馈

#### 使用示例

```vue
<template>
  <view class="shop-list">
    <shop-card 
      v-for="shop in shops" 
      :key="shop.id"
      :shop="shop"
      @click="handleShopClick"
    />
  </view>
</template>

<script setup>
import { ref } from 'vue'
import ShopCard from '@/components/shop-card.vue'
import { listShops } from '@/api/shops'

const shops = ref([])

const loadShops = async () => {
  const res = await listShops({ page: 0, size: 10 })
  shops.value = res.content || []
}

const handleShopClick = (shop) => {
  console.log('点击了商家:', shop)
}

onMounted(() => {
  loadShops()
})
</script>
```

---

### 3. review-card.vue - 点评卡片组件

点评信息展示卡片组件，用于商家详情页、我的点评页等。

#### Props

| 属性名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| review | Object | 是 | - | 点评对象 |
| showRecommendReason | Boolean | 否 | false | 是否显示推荐理由 |
| recommendReason | String | 否 | '' | 推荐理由文本（AI 推荐时使用） |
| shopId | Number/String | 否 | null | 商家ID（用于点赞功能） |

**review 对象结构**:
```typescript
{
  id: number,              // 点评ID（必填）
  userId: number,          // 用户ID
  user: {                  // 用户对象（可选）
    id: number,
    nickname: string,
    username: string
  },
  rating: number,          // 评分（1-5）
  content: string,         // 点评内容
  images: string|Array,    // 图片列表（JSON字符串或数组）
  likeCount: number,       // 点赞数
  createdAt: string        // 创建时间
}
```

#### Events

| 事件名 | 说明 | 参数 |
|--------|------|------|
| like | 点赞时触发 | (reviewId: number) |

#### 功能特性

- ✅ 用户头像、昵称展示（头像显示首字母）
- ✅ 评分显示（使用 rating-stars 组件）
- ✅ 内容展示
- ✅ 图片展示（支持预览）
- ✅ 点赞功能
- ✅ **AI 推荐标识**（右上角徽章）
- ✅ **推荐理由展示**（浅蓝色背景、特殊样式）
- ✅ 时间格式化显示（相对时间）

#### 使用示例

```vue
<template>
  <!-- 普通点评 -->
  <review-card 
    v-for="review in reviews" 
    :key="review.id"
    :review="review"
    :shop-id="shopId"
    @like="handleLike"
  />
  
  <!-- AI 推荐点评 -->
  <review-card 
    v-for="item in recommendedReviews" 
    :key="item.review.id"
    :review="item.review"
    :shop-id="shopId"
    :show-recommend-reason="true"
    :recommend-reason="item.reason"
  />
</template>

<script setup>
import { ref } from 'vue'
import ReviewCard from '@/components/review-card.vue'
import { listReviews, recommendReviews } from '@/api/reviews'

const shopId = ref(1)
const reviews = ref([])
const recommendedReviews = ref([])

const loadReviews = async () => {
  const res = await listReviews(shopId.value, { page: 0, size: 10 })
  reviews.value = res.content || []
}

const loadRecommendedReviews = async () => {
  const data = await recommendReviews(shopId.value, { limit: 3 })
  recommendedReviews.value = data || []
}

const handleLike = (reviewId) => {
  console.log('点赞了点评:', reviewId)
}

onMounted(() => {
  loadReviews()
  loadRecommendedReviews()
})
</script>
```

---

## 组件依赖

- `@/utils/format` - 格式化工具函数（formatRating, formatAvgPrice, formatRelativeTime）
- `@/api/reviews` - 点评相关 API（likeReview）

---

## 样式说明

所有组件使用 `rpx` 作为单位，支持响应式适配。

组件样式已优化，包含：
- 卡片阴影效果
- 点击反馈动画
- 响应式布局
- 统一的颜色主题
- AI 推荐特殊样式（渐变背景、徽章）

