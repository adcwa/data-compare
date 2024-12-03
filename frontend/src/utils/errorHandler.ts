import { ElMessage } from 'element-plus'

export function setupGlobalErrorHandler() {
  window.addEventListener('unhandledrejection', event => {
    console.error('Unhandled promise rejection:', event.reason)
    ElMessage.error('操作失败：' + (event.reason?.message || '未知错误'))
    event.preventDefault()
  })

  window.addEventListener('error', event => {
    console.error('Global error:', event.error)
    ElMessage.error('系统错误：' + (event.error?.message || '未知错误'))
    event.preventDefault()
  })
} 