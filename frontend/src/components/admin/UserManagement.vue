<template>
  <div class="p-6 max-w-7xl mx-auto relative">
    <!-- Header -->
    <div class="mb-6">
      <h2 class="text-2xl font-bold text-gray-800 mb-4">User Management</h2>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div class="bg-white p-4 rounded-lg shadow text-center">
          <h3 class="text-3xl font-bold text-blue-600">{{ totalUsers }}</h3>
          <p class="text-sm text-gray-500">Total Users</p>
        </div>
        <div class="bg-white p-4 rounded-lg shadow text-center">
          <h3 class="text-3xl font-bold text-green-600">{{ activeUsers }}</h3>
          <p class="text-sm text-gray-500">Active Users</p>
        </div>
        <div class="bg-white p-4 rounded-lg shadow text-center">
          <h3 class="text-3xl font-bold text-red-600">{{ inactiveUsers }}</h3>
          <p class="text-sm text-gray-500">Inactive Users</p>
        </div>
      </div>
    </div>

    <!-- Filters -->
    <div class="bg-white p-4 rounded-lg shadow mb-6">
      <div class="flex flex-wrap justify-between items-center gap-4">
        <input v-model="searchQuery" type="text" placeholder="Search users..."
          class="w-full md:w-auto flex-1 border border-gray-300 rounded px-4 py-2 text-sm" />

        <div class="flex gap-2">
          <select v-model="statusFilter" class="border border-gray-300 rounded px-4 py-2 text-sm">
            <option value="">All Status</option>
            <option value="active">Active</option>
            <option value="inactive">Inactive</option>
          </select>
          <select v-model="roleFilter" class="border border-gray-300 rounded px-4 py-2 text-sm">
            <option value="">All Roles</option>
            <option value="ADMIN">Admin</option>
            <option value="USER">User</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Table -->
    <div class="bg-white rounded-lg shadow overflow-x-auto">
      <table class="min-w-full text-sm text-left">
        <thead class="bg-gray-100 text-xs font-semibold text-gray-600 uppercase">
          <tr>
            <th class="px-6 py-3">Avatar</th>
            <th class="px-6 py-3">Username</th>
            <th class="px-6 py-3">Full Name</th>
            <th class="px-6 py-3">Email</th>
            <th class="px-6 py-3">Phone</th>
            <th class="px-6 py-3">Roles</th>
            <th class="px-6 py-3">Status</th>
            <th class="px-6 py-3">Created</th>
            <th class="px-6 py-3">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in paginatedUsers" :key="user.id" class="border-b">
            <td class="px-6 py-3">
              <div
                class="w-10 h-10 rounded-full bg-blue-600 text-white flex items-center justify-center overflow-hidden">
                <img v-if="user.avatarUrl" :src="`http://localhost:8080${user.avatarUrl}`" alt="avatar"
                  class="w-full h-full object-cover" />
                <span v-else class="font-bold">{{ user.username.charAt(0).toUpperCase() }}</span>
              </div>
            </td>
            <td class="px-6 py-3">{{ user.username }}</td>
            <td class="px-6 py-3">{{ user.fullName }}</td>
            <td class="px-6 py-3">{{ user.email }}</td>
            <td class="px-6 py-3">{{ user.phoneNumber || 'N/A' }}</td>
            <td class="px-6 py-3 space-x-1">
              <span v-for="role in user.roles" :key="role"
                class="inline-block px-2 py-1 rounded-full text-xs font-medium text-white" :class="{
                  'bg-red-600': role === 'ADMIN',
                  'bg-blue-600': role === 'USER'
                }">
                {{ role }}
              </span>
            </td>
            <td class="px-6 py-3">
              <span :class="user.active ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'"
                class="px-2 py-1 rounded-full text-xs font-semibold">
                {{ user.active ? 'Active' : 'Inactive' }}
              </span>
            </td>
            <td class="px-6 py-3">{{ formatDate(user.createdAt) }}</td>
            <td class="px-6 py-3">
              <button @click="toggleUserStatus(user)"
                :class="user.active ? 'bg-red-500 hover:bg-red-600' : 'bg-green-500 hover:bg-green-600'"
                class="text-white px-3 py-1 rounded text-xs font-semibold disabled:opacity-50" :disabled="loading">
                {{ user.active ? 'Deactivate' : 'Activate' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="paginatedUsers.length === 0" class="p-8 text-center text-gray-500">
        No users found matching your criteria.
      </div>
    </div>

    <!-- Pagination Controls -->
    <div v-if="totalPages > 1" class="flex justify-center gap-2 mt-6">
      <button @click="currentPage = 1" :disabled="currentPage === 1" class="px-3 py-1 text-sm border rounded"
        :class="buttonClass(currentPage === 1)">First</button>
      <button @click="currentPage--" :disabled="currentPage === 1" class="px-3 py-1 text-sm border rounded"
        :class="buttonClass(currentPage === 1)">Previous</button>
      <span class="px-2 text-sm text-gray-600">Page {{ currentPage }} of {{ totalPages }}</span>
      <button @click="currentPage++" :disabled="currentPage === totalPages" class="px-3 py-1 text-sm border rounded"
        :class="buttonClass(currentPage === totalPages)">Next</button>
      <button @click="currentPage = totalPages" :disabled="currentPage === totalPages"
        class="px-3 py-1 text-sm border rounded" :class="buttonClass(currentPage === totalPages)">Last</button>
    </div>

    <!-- Loading Overlay -->
    <div v-if="loading" class="absolute inset-0 bg-white/70 flex items-center justify-center z-10">
      <div class="w-10 h-10 border-4 border-blue-200 border-t-blue-600 rounded-full animate-spin"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useAdminStore } from '@/stores/admin.js'

const adminStore = useAdminStore()

const searchQuery = ref('')
const statusFilter = ref('')
const roleFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

const totalUsers = computed(() => adminStore.users.length)
const activeUsers = computed(() => adminStore.users.filter(u => u.active).length)
const inactiveUsers = computed(() => adminStore.users.filter(u => !u.active).length)

const filteredUsers = computed(() => {
  return adminStore.users.filter(user => {
    const matchesSearch = user.username.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      user.fullName.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      user.email.toLowerCase().includes(searchQuery.value.toLowerCase())

    const matchesStatus = statusFilter.value === '' ||
      (statusFilter.value === 'active' && user.active) ||
      (statusFilter.value === 'inactive' && !user.active)

    const matchesRole = roleFilter.value === '' || user.roles.includes(roleFilter.value)

    return matchesSearch && matchesStatus && matchesRole
  })
})

const totalPages = computed(() => Math.ceil(filteredUsers.value.length / pageSize.value))

const paginatedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredUsers.value.slice(start, start + pageSize.value)
})

watch([searchQuery, statusFilter, roleFilter], () => {
  currentPage.value = 1
})

const toggleUserStatus = async (user) => {
  adminStore.loading = true
  try {
    await adminStore.toggleUserStatus(user.id)
    user.active = !user.active
  } catch {
    alert('Failed to update user status')
  } finally {
    adminStore.loading = false
  }
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

const buttonClass = (disabled) => {
  return disabled
    ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
    : 'hover:bg-gray-100 text-gray-700'
}

onMounted(() => {
  adminStore.loadUsers()
})
</script>
