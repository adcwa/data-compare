import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import App from './App.vue'
import router from './router'
import { wsService } from './services/websocket'
import { setupGlobalErrorHandler } from './utils/errorHandler'

// 创建应用实例
const app = createApp(App)

// 注册全局错误处理
setupGlobalErrorHandler()

// 使用插件
app.use(createPinia())
app.use(router)
app.use(ElementPlus, {
  locale: zhCn
})

// 挂载应用
app.mount('#app')

// 初始化WebSocket连接
wsService.connect() 