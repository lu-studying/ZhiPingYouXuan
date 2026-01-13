<script setup>
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'
import { useAuthStore } from '@/store/auth'

// 初始化认证状态
onLaunch(() => {
  console.log('App Launch')
  // 从本地存储恢复认证状态
  const authStore = useAuthStore()
  authStore.initAuth()
})

onShow(() => {
  console.log('App Show')
})

onHide(() => {
  console.log('App Hide')
})
</script>

<style>
/* 每个页面公共css */
page {
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif;
}
</style>
