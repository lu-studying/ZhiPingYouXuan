/**
 * 文件上传相关 API 接口封装
 * 
 * 说明：
 * 本模块封装了所有与文件上传相关的 API 调用
 * 使用本地文件存储，图片存储在服务器本地
 */

import { BASE_URL } from '@/utils/constants'

/**
 * 上传文件到本地存储
 * 
 * @param {string} filePath - 文件路径（uni.chooseImage 返回的 tempFilePath）
 * @param {string} pathPrefix - 存储路径前缀（可选，如 "menu/"），如果不提供则使用后端默认值
 * @returns {Promise<Object>} 返回包含 url 和 relativeUrl 字段的对象
 * 
 * @example
 * uploadFile('/tmp/xxx.jpg', 'menu/')
 *   .then(res => {
 *     console.log('上传成功，图片URL:', res.url)
 *     // res.url: http://localhost:8080/uploads/menu/2024/01/15/xxx.jpg
 *     // res.relativeUrl: /uploads/menu/2024/01/15/xxx.jpg
 *   })
 */
export function uploadFile(filePath, pathPrefix) {
  return new Promise((resolve, reject) => {
    // 使用 uni.uploadFile 上传文件（因为需要 multipart/form-data）
    const token = uni.getStorageSync('token')
    
    uni.uploadFile({
      url: `${BASE_URL}/files/upload`,
      filePath: filePath,
      name: 'file',
      formData: pathPrefix ? { pathPrefix } : {},
      header: {
        'Authorization': token ? `Bearer ${token}` : ''
      },
      success: (res) => {
        try {
          const data = JSON.parse(res.data)
          if (res.statusCode === 200) {
            // 后端返回的是相对路径（如 /uploads/menu/xxx.jpg）
            // 需要拼接 BASE_URL 得到完整 URL
            const relativeUrl = data.url || data.relativeUrl
            const fullUrl = relativeUrl.startsWith('http') 
              ? relativeUrl 
              : BASE_URL.replace('/api', '') + relativeUrl
            resolve({
              ...data,
              url: fullUrl, // 完整 URL
              relativeUrl: relativeUrl // 相对路径
            })
          } else {
            const errorMsg = data.message || data.error || '上传失败'
            uni.showToast({
              title: errorMsg,
              icon: 'none',
              duration: 2000
            })
            reject(new Error(errorMsg))
          }
        } catch (e) {
          console.error('解析响应失败:', e)
          uni.showToast({
            title: '解析响应失败',
            icon: 'none',
            duration: 2000
          })
          reject(new Error('解析响应失败'))
        }
      },
      fail: (err) => {
        console.error('上传失败:', err)
        uni.showToast({
          title: '上传失败，请稍后重试',
          icon: 'none',
          duration: 2000
        })
        reject(err)
      }
    })
  })
}

/**
 * 删除本地文件
 * 
 * @param {string} fileUrl - 文件的完整 URL 或相对路径
 * @returns {Promise<Object>} 返回删除结果
 * 
 * @example
 * deleteFile('http://localhost:8080/uploads/menu/xxx.jpg')
 *   .then(res => {
 *     console.log('删除成功')
 *   })
 */
export function deleteFile(fileUrl) {
  // 注意：这里需要使用 request 工具，但需要先导入
  // 由于 request.js 可能不支持 DELETE 请求的 params，这里直接使用 uni.request
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')
    const url = fileUrl.startsWith('http') 
      ? `${BASE_URL}/files/delete?url=${encodeURIComponent(fileUrl)}`
      : `${BASE_URL}/files/delete?url=${encodeURIComponent(fileUrl)}`
    
    uni.request({
      url: url,
      method: 'DELETE',
      header: {
        'Authorization': token ? `Bearer ${token}` : '',
        'Content-Type': 'application/json'
      },
      success: (res) => {
        if (res.statusCode === 200) {
          resolve(res.data)
        } else {
          reject(new Error(res.data?.message || '删除失败'))
        }
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}

