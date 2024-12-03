import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('@/views/HomeView.vue'),
          meta: { title: '首页' }
        },
        {
          path: 'datasources',
          name: 'datasources',
          component: () => import('@/views/DataSourceList.vue'),
          meta: { title: '数据源管理' }
        },
        {
          path: 'tasks',
          name: 'tasks',
          component: () => import('@/views/TaskList.vue'),
          meta: { title: '比较任务' }
        }
      ]
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { title: '登录' }
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFound.vue'),
      meta: { title: '页面不存在' }
    }
  ]
})

// 导航守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = `${to.meta.title} - 数据比较系统`

  // 检查用户登录状态
  const userStore = useUserStore()
  const isAuthenticated = userStore.isAuthenticated

  if (to.name !== 'login' && !isAuthenticated) {
    // 未登录且访问非登录页面，重定向到登录页
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else if (to.name === 'login' && isAuthenticated) {
    // 已登录但访问登录页，重定向到首页
    next({ name: 'home' })
  } else {
    next()
  }
})

export default router 