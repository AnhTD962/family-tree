<template>
  <div class="member-detail-view">
    <div class="container mx-auto px-4 py-6">
      <!-- Loading State -->
      <LoadingSpinner v-if="loading" />

      <!-- Error State -->
      <div v-else-if="error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
        <strong class="font-bold">Error!</strong>
        <span class="block sm:inline">{{ error }}</span>
      </div>

      <!-- Member Detail Content -->
      <div v-else-if="member" class="bg-white rounded-lg shadow-lg overflow-hidden">
        <!-- Header Section -->
        <div class="relative bg-gradient-to-r from-blue-500 to-purple-600 text-white p-6">
          <div class="flex items-center justify-between">
            <button @click="goBack" class="flex items-center text-white hover:text-gray-200 transition-colors">
              <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
              </svg>
              Back to Family Tree
            </button>

            <div class="flex space-x-2" v-if="canEdit">
              <button @click="toggleEdit"
                class="bg-white text-blue-600 px-4 py-2 rounded-lg hover:bg-gray-100 transition-colors">
                {{ isEditing ? 'Cancel' : 'Edit' }}
              </button>
              <button v-if="canDelete" @click="confirmDelete"
                class="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition-colors">
                Delete
              </button>
            </div>
          </div>
        </div>

        <!-- Member Information -->
        <div class="p-6">
          <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <!-- Avatar and Basic Info -->
            <div class="lg:col-span-1">
              <div class="text-center">
                <div class="relative inline-block">
                  <img :src="member.avatarUrl || defaultAvatar" :alt="member.fullName"
                    class="w-32 h-32 rounded-full mx-auto object-cover border-4 border-gray-200" />
                  <div class="absolute bottom-0 right-0 w-8 h-8 rounded-full border-2 border-white"
                    :class="member.gender === 'MALE' ? 'bg-blue-500' : 'bg-pink-500'">
                  </div>
                </div>

                <h1 class="mt-4 text-2xl font-bold text-gray-900">{{ member.fullName }}</h1>
                <p v-if="member.nickname" class="text-gray-600 italic">"{{ member.nickname }}"</p>
                <p class="text-sm text-gray-500 mt-2">Generation {{ member.generation }}</p>
              </div>
            </div>

            <!-- Detailed Information -->
            <div class="lg:col-span-2">
              <div v-if="!isEditing" class="space-y-4">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div class="bg-gray-50 p-4 rounded-lg">
                    <h3 class="font-semibold text-gray-700 mb-2">Personal Information</h3>
                    <div class="space-y-2 text-sm">
                      <p><span class="font-medium">Gender:</span> {{ formatGender(member.gender) }}</p>
                      <p v-if="member.birthDate"><span class="font-medium">Birth Date:</span> {{
                        formatDate(member.birthDate) }}</p>
                      <p v-if="member.deathDate"><span class="font-medium">Death Date:</span> {{
                        formatDate(member.deathDate) }}</p>
                      <p v-if="member.birthPlace"><span class="font-medium">Birth Place:</span> {{ member.birthPlace }}
                      </p>
                      <p v-if="member.occupation"><span class="font-medium">Occupation:</span> {{ member.occupation }}
                      </p>
                    </div>
                  </div>

                  <div class="bg-gray-50 p-4 rounded-lg">
                    <h3 class="font-semibold text-gray-700 mb-2">Family Relations</h3>
                    <div class="space-y-2 text-sm">
                      <p v-if="member.father">
                        <span class="font-medium">Father:</span>
                        <router-link :to="`/member/${member.father.id}`" class="text-blue-600 hover:underline">
                          {{ member.father.fullName }}
                        </router-link>
                      </p>
                      <p v-if="member.mother">
                        <span class="font-medium">Mother:</span>
                        <router-link :to="`/member/${member.mother.id}`" class="text-blue-600 hover:underline">
                          {{ member.mother.fullName }}
                        </router-link>
                      </p>
                      <p v-if="member.spouse">
                        <span class="font-medium">Spouse:</span>
                        <router-link :to="`/member/${member.spouse.id}`" class="text-blue-600 hover:underline">
                          {{ member.spouse.fullName }}
                        </router-link>
                      </p>
                      <p v-if="member.children && member.children.length > 0">
                        <span class="font-medium">Children:</span> {{ member.children.length }}
                        <router-link v-if="member.children.length === 1" :to="`/member/${member.children[0].id}`"
                          class="text-blue-600 hover:underline">
                          {{ member.children[0].fullName }}
                        </router-link>
                        <span v-else-if="member.children.length > 1"> (see below)</span>
                      </p>
                    </div>
                  </div>
                </div>

                <div v-if="member.description" class="bg-gray-50 p-4 rounded-lg">
                  <h3 class="font-semibold text-gray-700 mb-2">Description</h3>
                  <p class="text-gray-600 whitespace-pre-wrap">{{ member.description }}</p>
                </div>
              </div>

              <!-- Edit Form -->
              <FamilyMemberForm v-else :member="member" :is-editing="true" @save="handleSave" @cancel="toggleEdit" />
            </div>
          </div>

          <!-- Family Section -->
          <div v-if="member.children && member.children.length > 0" class="mt-8">
            <h3 class="text-xl font-semibold text-gray-900 mb-4">Children</h3>
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              <FamilyMemberCard v-for="child in member.children" :key="child.id" :member="child" :show-actions="false"
                @click="navigateToMember(child.id)" />
            </div>
          </div>

          <!-- Spouse Section -->
          <div v-if="member.spouse" class="mt-8">
            <h3 class="text-xl font-semibold text-gray-900 mb-4">Spouse</h3>
            <FamilyMemberCard :member="member.spouse" :show-actions="false"
              @click="navigateToMember(member.spouse.id)" />
          </div>

          <!-- Parents Section -->
          <div v-if="member.father || member.mother" class="mt-8">
            <h3 class="text-xl font-semibold text-gray-900 mb-4">Parents</h3>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <FamilyMemberCard v-if="member.father" :member="member.father" :show-actions="false"
                @click="navigateToMember(member.father.id)" />
              <FamilyMemberCard v-if="member.mother" :member="member.mother" :show-actions="false"
                @click="navigateToMember(member.mother.id)" />
            </div>
          </div>

          <!-- Siblings Section -->
          <div v-if="member.siblings && member.siblings.length > 0" class="mt-8">
            <h3 class="text-xl font-semibold text-gray-900 mb-4">Siblings</h3>
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              <FamilyMemberCard v-for="sibling in member.siblings" :key="sibling.id" :member="sibling"
                :show-actions="false" @click="navigateToMember(sibling.id)" />
            </div>
          </div>

          <!-- Timeline -->
          <div class="mt-8">
            <h3 class="text-xl font-semibold text-gray-900 mb-4">Timeline</h3>
            <div class="bg-gray-50 rounded-lg p-4">
              <div class="space-y-2 text-sm text-gray-600">
                <p><span class="font-medium">Created:</span> {{ formatDateTime(member.createdAt) }}</p>
                <p><span class="font-medium">Last Updated:</span> {{ formatDateTime(member.updatedAt) }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div v-if="showDeleteModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 max-w-md w-full mx-4">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">Confirm Delete</h3>
        <p class="text-gray-600 mb-6">
          Are you sure you want to delete {{ member.fullName }}? This action cannot be undone and will also delete all
          descendants.
        </p>
        <div class="flex justify-end space-x-3">
          <button @click="showDeleteModal = false"
            class="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50">
            Cancel
          </button>
          <button @click="handleDelete" class="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600">
            Delete
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useFamilyStore } from '@/stores/family'
import { useAuthStore } from '@/stores/auth'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import FamilyMemberCard from '@/components/family/FamilyMemberCard.vue'
import FamilyMemberForm from '@/components/family/FamilyMemberForm.vue'

export default {
  name: 'MemberDetailView',
  components: {
    LoadingSpinner,
    FamilyMemberCard,
    FamilyMemberForm
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const familyStore = useFamilyStore()
    const authStore = useAuthStore()

    const member = ref(null)
    const loading = ref(true)
    const error = ref(null)
    const isEditing = ref(false)
    const showDeleteModal = ref(false)

    const defaultAvatar = '/default-avatar.png'

    const canEdit = computed(() => {
      return authStore.isUser || authStore.isAdmin
    })

    const canDelete = computed(() => {
      return authStore.isAdmin
    })

    const loadMember = async () => {
      try {
        loading.value = true
        error.value = null
        const memberId = route.params.id
        member.value = await familyStore.loadFamilyMember(memberId)
      } catch (err) {
        error.value = err.message || 'Failed to load member details'
      } finally {
        loading.value = false
      }
    }

    const goBack = () => {
      router.push('/')
    }

    const toggleEdit = () => {
      isEditing.value = !isEditing.value
    }

    const handleSave = async (updatedMember) => {
      try {
        await familyStore.updateMember(member.value.id, updatedMember)
        member.value = { ...member.value, ...updatedMember }
        isEditing.value = false
        // Show success message
        // You can add a toast notification here
      } catch (err) {
        error.value = err.message || 'Failed to update member'
      }
    }

    const confirmDelete = () => {
      showDeleteModal.value = true
    }

    const handleDelete = async () => {
      try {
        await familyStore.deleteMember(member.value.id)
        showDeleteModal.value = false
        router.push('/-')
        // Show success message
        // You can add a toast notification here
      } catch (err) {
        error.value = err.message || 'Failed to delete member'
        showDeleteModal.value = false
      }
    }

    const navigateToMember = (memberId) => {
      router.push(`/member/${memberId}`)
    }

    const formatGender = (gender) => {
      return gender === 'MALE' ? 'Male' : 'Female'
    }

    const formatDate = (dateString) => {
      if (!dateString) return ''
      return new Date(dateString).toLocaleDateString()
    }

    const formatDateTime = (dateTimeString) => {
      if (!dateTimeString) return ''
      return new Date(dateTimeString).toLocaleString()
    }

    onMounted(() => {
      loadMember()
    })

    return {
      member,
      loading,
      error,
      isEditing,
      showDeleteModal,
      defaultAvatar,
      canEdit,
      canDelete,
      goBack,
      toggleEdit,
      handleSave,
      confirmDelete,
      handleDelete,
      navigateToMember,
      formatGender,
      formatDate,
      formatDateTime
    }
  }
}
</script>

<style scoped>
.member-detail-view {
  min-height: 100vh;
  background-color: #f8fafc;
}

.container {
  max-width: 1200px;
}

/* Custom scrollbar for description */
.whitespace-pre-wrap {
  white-space: pre-wrap;
  word-wrap: break-word;
}

/* Hover effects */
.transition-colors {
  transition: color 0.2s ease-in-out, background-color 0.2s ease-in-out;
}

/* Gender indicator */
.gender-indicator {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 2rem;
  height: 2rem;
  border-radius: 50%;
  border: 2px solid white;
}

/* Modal backdrop */
.fixed.inset-0 {
  backdrop-filter: blur(2px);
}
</style>
