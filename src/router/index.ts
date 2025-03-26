import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'
import AdminLayout from '../layouts/AdminLayout.vue'
import VolunteerLayout from '../layouts/VolunteerLayout.vue'
import OrganizationLayout from '../layouts/OrganizationLayout.vue'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import AdminDashboard from '../views/AdminDashboard.vue'
import VolunteerHome from '../views/VolunteerHome.vue'
import OrganizationHome from '../views/OrganizationHome.vue'
import Projects from '../views/Projects.vue'
import Organizations from '../views/Organizations.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: MainLayout,
      children: [
        {
          path: '',
          name: 'home',
          component: Home,
        },
        {
          path: 'projects',
          name: 'projects',
          component: Projects,
        },
        {
          path: 'organizations',
          name: 'organizations',
          component: Organizations,
        },
      ],
    },
    {
      path: '/login',
      name: 'login',
      component: Login,
    },
    {
      path: '/register',
      name: 'register',
      component: Register,
    },
    {
      path: '/admin',
      component: AdminLayout,
      meta: { requiresAuth: true, role: 'admin' },
      children: [
        {
          path: '',
          redirect: '/admin/dashboard',
        },
        {
          path: 'dashboard',
          name: 'admin-dashboard',
          component: AdminDashboard,
        },
        // 可以添加更多管理页面路由
      ],
    },
    {
      path: '/volunteer',
      component: VolunteerLayout,
      meta: { requiresAuth: true, role: 'volunteer' },
      children: [
        {
          path: '',
          redirect: '/volunteer/home',
        },
        {
          path: 'home',
          name: 'volunteer-home',
          component: VolunteerHome,
        },
        // 可以添加更多志愿者页面路由
      ],
    },
    {
      path: '/organization',
      component: OrganizationLayout,
      meta: { requiresAuth: true, role: 'organization' },
      children: [
        {
          path: '',
          redirect: '/organization/home',
        },
        {
          path: 'home',
          name: 'organization-home',
          component: OrganizationHome,
        },
        // 可以添加更多组织页面路由
      ],
    },
  ],
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.meta.role && token) {
    // 根据角色进行路由控制
    if (to.meta.role !== role) {
      // 如果用户角色与路由要求的角色不匹配，重定向到对应的首页
      next(`/${role}`)
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
