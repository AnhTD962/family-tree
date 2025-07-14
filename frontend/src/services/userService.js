import api from './api'

export const userService = {
  async getUserProfile() {
    const res = await api.get('/api/user/profile')
    return res.data
  },

  async updateUserProfile(formData) {
    const res = await api.put('/api/user/profile', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
    return res.data
  },

  async changePassword(data) {
    const res = await api.put('/api/user/change-password', data)
    return res.data
  },
}
