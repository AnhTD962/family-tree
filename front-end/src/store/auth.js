// src/store/auth.js
import { defineStore } from 'pinia'
import { loginApi, refreshToken } from '@/api/authApi'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    user: JSON.parse(localStorage.getItem('user')) || null,
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
  },
  actions: {
    async login(credentials) {
      const data = await loginApi(credentials)
      this.token = data.token
      this.user = {
        username: data.username,
        roles: data.roles,
        fullName: data.fullName,
        email: data.email,
      }
      localStorage.setItem('token', this.token)
      localStorage.setItem('user', JSON.stringify(this.user))
    },
    logout() {
      this.token = null
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    },
    async refresh() {
      const res = await refreshToken()
      this.token = res.token
      localStorage.setItem('token', res.token)
    },
  },
})
