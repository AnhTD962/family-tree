import { defineStore } from 'pinia'
import { adminService } from '@/services/adminService'

export const useAdminStore = defineStore('admin', {
  state: () => ({
    users: [],
    history: [],
    totalHistoryPages: 0,
    loading: false,
  }),

  actions: {
    async loadUsers() {
      this.loading = true
      try {
        this.users = await adminService.getAllUsers()
      } catch (error) {
        throw error
      } finally {
        this.loading = false
      }
    },

    async toggleUserStatus(id) {
      try {
        await adminService.toggleUserStatus(id)
        const user = this.users.find((u) => u.id === id)
        if (user) {
          user.isActive = !user.isActive
        }
      } catch (error) {
        throw error
      }
    },

    async loadHistory() {
      const data = await adminService.getFamilyTreeHistory()
      this.history = data
      this.totalActivityHistory = data.length
    },
  },
})
