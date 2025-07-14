import { defineStore } from 'pinia'
import { familyService } from '@/services/familyService'

export const useFamilyStore = defineStore('family', {
  state: () => ({
    members: [],
    selectedMember: null,
    searchResults: [],
    loading: false,
  }),

  getters: {
    getMemberById: (state) => (id) => {
      return state.members.find((member) => member.id === id)
    },

    getRootMembers: (state) => {
      return state.members.filter((member) => !member.fatherId && !member.motherId)
    },
  },

  actions: {
    async loadFamilyTree() {
      this.loading = true
      try {
        const response = await familyService.getFamilyTree()
        this.members = response.members
      } catch (error) {
        throw error
      } finally {
        this.loading = false
      }
    },

    async loadFamilyMember(id) {
      try {
        const member = await familyService.getFamilyMember(id)
        this.selectedMember = member
        return member
      } catch (error) {
        throw error
      }
    },

    async searchMembers(query) {
      if (!query) {
        this.searchResults = []
        return
      }

      try {
        this.searchResults = await familyService.searchMembers(query)
      } catch (error) {
        throw error
      }
    },

    async createMember(member) {
      try {
        const newMember = await familyService.createFamilyMember(member)
        this.members.push(newMember)
        return newMember
      } catch (error) {
        throw error
      }
    },

    async updateMember(id, member) {
      try {
        const updatedMember = await familyService.updateFamilyMember(id, member)
        const index = this.members.findIndex((m) => m.id === id)
        if (index !== -1) {
          this.members[index] = updatedMember
        }
        return updatedMember
      } catch (error) {
        throw error
      }
    },

    async deleteMember(id) {
      try {
        await familyService.deleteFamilyMember(id)
        this.members = this.members.filter((m) => m.id !== id)
      } catch (error) {
        throw error
      }
    },
  },
})
