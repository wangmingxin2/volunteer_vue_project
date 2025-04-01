import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'
import AdminLayout from '../layouts/AdminLayout.vue'
import VolunteerLayout from '../layouts/VolunteerLayout.vue'
import OrganizationLayout from '../layouts/OrganizationLayout.vue'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import UserProfile from '../views/UserProfile.vue'

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
          component: () => import('../views/Projects.vue'),
        },
        {
          path: 'organizations',
          name: 'organizations',
          component: () => import('../views/Organizations.vue'),
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
      redirect: '/admin/dashboard',
      meta: { requiresAuth: true, role: 'admin' },
      children: [
        {
          path: 'dashboard',
          name: 'adminDashboard',
          component: () => import('../views/admin/Dashboard.vue'),
          meta: { title: '仪表盘' },
        },
        {
          path: 'profile',
          name: 'adminProfile',
          component: UserProfile,
          meta: { title: '个人信息' },
        },
        {
          path: 'users',
          name: 'adminUsers',
          component: () => import('../views/admin/Users.vue'),
          meta: { title: '用户管理' },
        },
        {
          path: 'organizations',
          name: 'adminOrganizations',
          component: () => import('../views/admin/Organizations.vue'),
          meta: { title: '组织管理' },
        },
        {
          path: 'activities',
          name: 'adminActivities',
          component: () => import('../views/admin/Activities.vue'),
          meta: { title: '活动管理' },
        },
        {
          path: 'banners',
          name: 'adminBanners',
          component: () => import('../views/admin/Banners.vue'),
          meta: { title: '轮播图管理' },
        },
        {
          path: 'announcements',
          name: 'adminAnnouncements',
          component: () => import('../views/admin/Announcements.vue'),
          meta: { title: '公告管理' },
        },
        {
          path: 'signup-management',
          name: 'adminSignupManagement',
          component: () => import('../views/admin/SignupManagement.vue'),
          meta: { title: '活动报名管理' },
        },
        {
          path: 'reflection-management',
          name: 'adminReflectionManagement',
          component: () => import('../views/admin/ReflectionManagement.vue'),
          meta: { title: '活动心得管理' },
        },
      ],
    },
    {
      path: '/volunteer',
      component: VolunteerLayout,
      redirect: '/volunteer/home',
      meta: { requiresAuth: true, role: 'volunteer' },
      children: [
        {
          path: '',
          redirect: '/volunteer/home',
        },
        {
          path: 'home',
          name: 'volunteer-home',
          component: () => import('../views/volunteer/Home.vue'),
        },
        {
          path: 'profile',
          name: 'volunteer-profile',
          component: UserProfile,
        },
        {
          path: 'projects',
          name: 'volunteer-projects',
          component: () => import('../views/volunteer/ActivitySignup.vue'),
          meta: { title: '活动报名' },
        },
        {
          path: 'activities',
          name: 'volunteer-activities',
          component: () => import('../views/volunteer/Activities.vue'),
          meta: { title: '我的活动' },
        },
        {
          path: 'activity-signup',
          name: 'activity-signup',
          component: () => import('../views/volunteer/ActivitySignup.vue'),
          meta: { title: '活动报名' },
        },
        {
          path: 'reflections',
          name: 'volunteer-reflections',
          component: () => import('../views/volunteer/Reflections.vue'),
          meta: { title: '心得体会管理' },
        },
        {
          path: 'messages',
          name: 'volunteer-messages',
          component: () => import('../views/volunteer/Messages.vue'),
          meta: { title: '消息中心' },
        },
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
          component: () => import('../views/organization/Home.vue'),
        },
        {
          path: 'profile',
          name: 'organization-profile',
          component: UserProfile,
        },
        {
          path: 'activities',
          name: 'organization-activities',
          component: () => import('../views/organization/Activities.vue'),
          meta: { title: '志愿活动管理' },
        },
        {
          path: 'organization-info',
          name: 'organization-info',
          component: () => import('../views/organization/OrganizationInfo.vue'),
          meta: { title: '组织信息' },
        },
        {
          path: 'signup-management',
          name: 'organization-signup-management',
          component: () => import('../views/organization/SignupManagement.vue'),
          meta: { title: '活动报名管理' },
        },
        {
          path: 'reflection-management',
          name: 'OrganizationReflectionManagement',
          component: () => import('../views/admin/ReflectionManagement.vue'),
          meta: {
            title: '活动心得管理',
            requiresAuth: true,
            roles: ['organization'],
          },
        },
      ],
    },
  ],
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const satoken = localStorage.getItem('satoken')
  const role = localStorage.getItem('role')

  if (to.meta.requiresAuth && !satoken) {
    next('/login')
  } else if (to.meta.role && satoken) {
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
