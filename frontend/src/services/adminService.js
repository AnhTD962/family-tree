import api from './api'

export const adminService = {
  async getAllUsers() {
    const response = await api.get('/api/admin/users')
    return response.data
  },

  async toggleUserStatus(id) {
    await api.put(`/api/admin/users/${id}/toggle-status`)
  },

  async getFamilyTreeHistory() {
    const response = await api.get('/api/admin/history')
    return response.data
  },
}
