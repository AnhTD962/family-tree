// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/store/auth';

import LoginView from '@/views/LoginView.vue';
import RegisterView from '@/views/RegisterView.vue';
import FamilyTreeView from '@/views/FamilyTreeView.vue';
import MemberEditView from '@/views/MemberEditView.vue';
import AdminUsersView from '@/views/AdminUsersView.vue';
import AdminHistoryView from '@/views/AdminHistoryView.vue';

const routes = [
  { path: '/', component: FamilyTreeView },
  { path: '/login', component: LoginView },
  { path: '/register', component: RegisterView },
  { path: '/tree', component: FamilyTreeView },
  { path: '/member/:id', component: MemberEditView },
  { path: '/member', component: MemberEditView },

  // Admin
  { path: '/admin/users', component: AdminUsersView, meta: { requiresAuth: true, adminOnly: true } },
  { path: '/admin/history', component: AdminHistoryView, meta: { requiresAuth: true, adminOnly: true } },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// Auth guards
router.beforeEach((to, from, next) => {
  const auth = useAuthStore();

  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return next('/login');
  }

  if (to.meta.adminOnly && !auth.user?.roles?.includes('ADMIN')) {
    return next('/');
  }

  next();
});

export default router;
