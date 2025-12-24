<template>
  <!-- 
    根组件模板
    说明：router-view 是 Vue Router 的占位符，会根据当前路由渲染对应的页面组件
    例如：访问 /login 时渲染 Login.vue，访问 /dashboard 时渲染 Dashboard.vue
  -->
  <router-view />
</template>

<script setup>
/**
 * 根组件脚本
 * 
 * 说明：
 * - 使用 Composition API 的 <script setup> 语法
 * - 这是 Vue 3 推荐的写法，代码更简洁
 * - 如果需要添加全局逻辑，可以在这里添加
 */
</script>

<style>
/**
 * 全局样式
 * 
 * 说明：
 * - 这些样式会应用到整个应用
 * - 包括重置样式、字体设置等
 */

/* 全局样式重置：清除所有元素的默认 margin 和 padding */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box; /* 使用 border-box 盒模型，更符合直觉 */
}

/* body 标签样式：设置全局字体 */
body {
  /* 使用系统字体栈，确保在不同操作系统上都有良好的显示效果 */
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial,
    'Noto Sans', sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol',
    'Noto Color Emoji';
  /* 字体平滑渲染，提升显示效果 */
  -webkit-font-smoothing: antialiased; /* WebKit 浏览器（Chrome、Safari） */
  -moz-osx-font-smoothing: grayscale; /* Firefox on macOS */
}

/* 应用根容器样式 */
#app {
  min-height: 100vh; /* 最小高度为视口高度，确保页面至少占满整个屏幕 */
}
</style>

