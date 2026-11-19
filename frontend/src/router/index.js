import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/login', component: () => import('../views/Login.vue') },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', component: () => import('../views/Placeholder.vue'), meta: { title: '工作台' } },
      { path: 'clue', component: () => import('../views/Placeholder.vue'), meta: { title: '线索' } },
      { path: 'customer', component: () => import('../views/Placeholder.vue'), meta: { title: '客户' } },
      { path: 'opportunity', component: () => import('../views/Placeholder.vue'), meta: { title: '商机' } },
      { path: 'order', component: () => import('../views/Placeholder.vue'), meta: { title: '订单' } },
      { path: 'product/category', component: () => import('../views/product/ProductCategory.vue'), meta: { title: '产品分类' } },
      { path: 'employee', component: () => import('../views/Placeholder.vue'), meta: { title: '员工管理' } },
      { path: 'role', component: () => import('../views/Placeholder.vue'), meta: { title: '角色管理' } },
      { path: 'department', component: () => import('../views/Department.vue'), meta: { title: '部门管理' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 没登录跳回登录页
router.beforeEach(to => {
  if (to.path !== '/login' && !localStorage.getItem('token')) return '/login'
})

export default router
