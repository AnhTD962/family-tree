import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user.js'

const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('@/views/HomeView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/LoginView.vue'),
    meta: { guestOnly: true }
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/RegisterView.vue'),
    meta: { guestOnly: true }
  },
  {
    path: '/family',
    name: 'family',
    component: () => import('@/views/FamilyTreeView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'profile',
    component: () => import('@/views/ProfileView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/members',
    name: 'members',
    component: () => import('@/views/MemberListView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/members/:id',
    name: 'member-detail',
    component: () => import('@/views/MemberDetailView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('@/views/NotFoundView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    return savedPosition || { top: 0 }
  }
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const isAuthenticated = userStore.isLoggedIn
  
  // Redirect to login if route requires authentication and user is not authenticated
  if (to.meta.requiresAuth && !isAuthenticated) {
    next({ name: 'login', query: { redirect: to.fullPath } })
  } 
  // Redirect to home if user is authenticated and tries to access guest-only routes
  else if (to.meta.guestOnly && isAuthenticated) {
    next({ name: 'home' })
  } 
  // Proceed with navigation
  else {
    next()
  }
  } else {
    next()
  }
})

export default router
