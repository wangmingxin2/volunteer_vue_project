import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import AdminDashboard from '../views/AdminDashboard.vue'
import OrganizationDashboard from '../views/OrganizationDashboard.vue'
import VolunteerDashboard from '../views/VolunteerDashboard.vue'
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
      name: 'admin',
      component: AdminDashboard,
      meta: { requiresAuth: true, role: 'admin' },
    },
    {
      path: '/organization',
      name: 'organization',
      component: OrganizationDashboard,
      meta: { requiresAuth: true, role: 'organization' },
    },
    {
      path: '/volunteer',
      name: 'volunteer',
      component: VolunteerDashboard,
      meta: { requiresAuth: true, role: 'volunteer' },
    },
  ],
})

export default router
