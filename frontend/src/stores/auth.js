import { defineStore } from 'pinia'
import { authService } from '@/services/authService'
import { setTokens, removeTokens, getToken } from '@/utils/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: null,
    refreshToken: null,
    isAuthenticated: false,
    loading: false,
  }),

  getters: {
    isAdmin: (state) => state.user?.roles?.includes('ADMIN'),
    isUser: (state) => state.user?.roles?.includes('USER'),
    userRoles: (state) => state.user?.roles || [],
  },

  actions: {
    async login(credentials) {
      this.loading = true
      try {
        const response = await authService.login(credentials)
        this.setAuthData(response)
        return response
      } catch (error) {
        throw error
      } finally {
        this.loading = false
      }
    },

    async register(userData) {
      this.loading = true
      try {
        const response = await authService.register(userData)
        this.setAuthData(response)
        return response
      } catch (error) {
        throw error
      } finally {
        this.loading = false
      }
    },

    async refresh() {
      try {
        const response = await authService.refresh(this.refreshToken)
        this.setAuthData(response)
        return response
      } catch (error) {
        this.logout()
        throw error
      }
    },

    setAuthData(data) {
      this.user = {
        username: data.username,
        email: data.email,
        fullName: data.fullName,
        avatarUrl: data.avatarUrl,
        phoneNumber: data.phoneNumber,
        roles: data.roles,
        isActive: data.isActive,
      }
      this.token = data.token
      this.refreshToken = data.refreshToken
      this.isAuthenticated = true

      setTokens(data.token, data.refreshToken)
      localStorage.setItem('user', JSON.stringify(this.user))
    },

    logout() {
      this.user = null
      this.token = null
      this.refreshToken = null
      this.isAuthenticated = false
      removeTokens()
    },

    checkAuth() {
      const token = getToken()
      const user = localStorage.getItem('user')

      if (token && user) {
        const parsedUser = JSON.parse(user)
        this.token = token
        this.user = parsedUser
        this.isAuthenticated = true
      } else {
        this.logout()
      }
    },
  },
})
