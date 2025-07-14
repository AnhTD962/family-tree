import { defineStore } from 'pinia'
import { userService } from '@/services/userService'

export const useUserStore = defineStore('user', {
  state: () => ({
    profile: null,
    loading: false,
    error: null,
  }),

  actions: {
    async fetchProfile() {
      this.loading = true
      this.error = null
      try {
        const res = await userService.getUserProfile()
        this.profile = res
        return res
      } catch (err) {
        this.error = err.response?.data?.message || 'Failed to load profile'
        console.error(err)
      } finally {
        this.loading = false
      }
    },

    async updateProfile({ user, file }) {
      const formData = new FormData()
      formData.append('user', new Blob([JSON.stringify(user)], { type: 'application/json' }))
      if (file) {
        formData.append('file', file)
      }

      try {
        const updated = await userService.updateUserProfile(formData)
        this.profile = updated
        return updated
      } catch (err) {
        console.error('Update profile error:', err)
        throw err
      }
    },

    async changePassword(data) {
      return await userService.changePassword(data)
    },
  },
})
