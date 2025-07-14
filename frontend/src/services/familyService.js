import api from './api'

function sanitizeMember(member) {
  return {
    ...member,
    fatherId: Array.isArray(member.fatherId) ? member.fatherId[0] : member.fatherId,
    motherId: Array.isArray(member.motherId) ? member.motherId[0] : member.motherId,
    spouseId: Array.isArray(member.spouseId) ? member.spouseId[0] : member.spouseId,
    childrenIds: Array.isArray(member.childrenIds) ? member.childrenIds : [],
  }
}

export const familyService = {
  async getFamilyTree() {
    const response = await api.get('/api/family-members/tree')
    return response.data
  },

  async getFamilyMember(id) {
    const response = await api.get(`/api/family-members/${id}`)
    return response.data
  },

  async searchMembers(query) {
    const response = await api.get(`/api/family-members/search?query=${query}`)
    return response.data
  },

  async createFamilyMember(member) {
    const formData = new FormData()
    const sanitized = sanitizeMember(member)

    formData.append('member', new Blob([JSON.stringify(sanitized)], { type: 'application/json' }))
    if (sanitized.file) {
      formData.append('file', sanitized.file)
    }

    const response = await api.post('/api/family-members', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
    return response.data
  },

  async updateFamilyMember(id, member) {
    const formData = new FormData()
    const sanitized = sanitizeMember(member)

    formData.append('member', new Blob([JSON.stringify(sanitized)], { type: 'application/json' }))
    if (sanitized.file) {
      formData.append('file', sanitized.file)
    }

    const response = await api.put(`/api/family-members/${id}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
    return response.data
  },

  async deleteFamilyMember(id) {
    await api.delete(`/api/family-members/${id}`)
  },
}
