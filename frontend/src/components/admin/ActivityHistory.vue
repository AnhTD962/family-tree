<!-- ActivityHistory.vue -->
<template>
  <div class="p-6 max-w-7xl mx-auto relative">
    <!-- Header -->
    <div class="mb-6">
      <h2 class="text-2xl font-bold text-gray-800 mb-4">Activity History</h2>
      <p class="text-sm text-gray-500">Track all family tree modifications and user activities</p>
    </div>

    <!-- Filters -->
    <div class="bg-white p-4 rounded-xl shadow mb-6 flex flex-wrap items-end gap-4">
      <div class="flex flex-col">
        <label class="text-sm font-medium text-gray-700">Action Type:</label>
        <select v-model="actionFilter" class="border border-gray-300 rounded px-3 py-2 text-sm min-w-[150px]">
          <option value="">All</option>
          <option value="CREATE">Create</option>
          <option value="UPDATE">Update</option>
          <option value="DELETE">Delete</option>
        </select>
      </div>

      <div class="flex flex-col">
        <label class="text-sm font-medium text-gray-700">User:</label>
        <input v-model="userFilter" type="text" placeholder="Search by username..."
          class="border border-gray-300 rounded px-3 py-2 text-sm min-w-[150px]" />
      </div>

      <div class="flex flex-col">
        <label class="text-sm font-medium text-gray-700">Date Range:</label>
        <div class="flex items-center gap-2">
          <input v-model="dateFromFilter" type="date" class="border border-gray-300 rounded px-3 py-2 text-sm" />
          <span class="text-gray-500 text-sm">to</span>
          <input v-model="dateToFilter" type="date" class="border border-gray-300 rounded px-3 py-2 text-sm" />
        </div>
      </div>

      <button @click="clearFilters" class="bg-gray-600 text-white px-4 py-2 rounded hover:bg-gray-700 text-sm">
        Clear Filters
      </button>
    </div>

    <!-- History Table -->
    <div class="bg-white rounded-xl shadow overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50 text-xs text-gray-500 uppercase tracking-wider">
          <tr>
            <th class="px-6 py-3 text-left">Action</th>
            <th class="px-6 py-3 text-left">Member</th>
            <th class="px-6 py-3 text-left">User</th>
            <th class="px-6 py-3 text-left">Details</th>
            <th class="px-6 py-3 text-left">Timestamp</th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-100 text-sm">
          <tr v-for="activity in paginatedHistory" :key="activity.id" class="hover:bg-gray-50">
            <td class="px-6 py-4 whitespace-nowrap">
              <span :class="[
                'text-xs font-semibold px-2 py-1 rounded-full',
                {
                  'bg-green-100 text-green-800': activity.action === 'CREATE',
                  'bg-yellow-100 text-yellow-800': activity.action === 'UPDATE',
                  'bg-red-100 text-red-800': activity.action === 'DELETE'
                }
              ]">
                {{ activity.action }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap font-medium text-gray-800">
              {{ activity.targetMemberName || '-' }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-gray-700">
              <strong class="text-gray-400 text-xs">{{ activity.userId }}</strong>
            </td>
            <td class="px-6 py-4 whitespace-pre-wrap text-gray-700">
              {{ activity.details }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-gray-500">
              {{ formatDateTime(activity.timestamp) }}
            </td>
          </tr>
        </tbody>
      </table>

      <!-- No history -->
      <div v-if="!paginatedHistory.length && !loading" class="text-center p-10 text-gray-500">
        <i class="fas fa-history text-4xl text-gray-300 mb-4"></i>
        <p class="text-sm">No activity history found</p>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="flex justify-center items-center gap-2 border-t px-4 py-3 bg-gray-50">
        <button @click="currentPage = 1" :disabled="currentPage === 1" class="px-3 py-1 border rounded text-sm"
          :class="buttonClass(currentPage === 1)">First</button>
        <button @click="currentPage--" :disabled="currentPage === 1" class="px-3 py-1 border rounded text-sm"
          :class="buttonClass(currentPage === 1)">Previous</button>

        <span class="text-sm text-gray-500">Page {{ currentPage }} of {{ totalPages }}</span>

        <button @click="currentPage++" :disabled="currentPage === totalPages" class="px-3 py-1 border rounded text-sm"
          :class="buttonClass(currentPage === totalPages)">Next</button>
        <button @click="currentPage = totalPages" :disabled="currentPage === totalPages"
          class="px-3 py-1 border rounded text-sm" :class="buttonClass(currentPage === totalPages)">Last</button>
      </div>
    </div>


    <!-- Loading -->
    <div v-if="loading" class="absolute inset-0 bg-white bg-opacity-70 flex items-center justify-center z-10">
      <div class="w-10 h-10 border-4 border-blue-200 border-t-blue-500 rounded-full animate-spin"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useAdminStore } from '@/stores/admin.js'

const adminStore = useAdminStore()
const history = ref([])
const loading = ref(false)
const currentPage = ref(1)
const totalElements = ref(0)
const pageSize = 10

const actionFilter = ref('')
const userFilter = ref('')
const dateFromFilter = ref('')
const dateToFilter = ref('')

const totalPages = computed(() => Math.ceil(totalElements.value / pageSize))

const filteredHistory = computed(() => {
  if (!Array.isArray(history.value)) return []

  return history.value.filter((activity) => {
    const matchesAction = !actionFilter.value || activity.action === actionFilter.value
    const matchesUser = !userFilter.value ||
      (activity.username && activity.username.toLowerCase().includes(userFilter.value.toLowerCase()))

    let matchesDate = true
    if (dateFromFilter.value || dateToFilter.value) {
      const activityDate = new Date(activity.timestamp)
      const fromDate = dateFromFilter.value ? new Date(dateFromFilter.value + 'T00:00:00') : null
      const toDate = dateToFilter.value ? new Date(dateToFilter.value + 'T23:59:59') : null

      if (fromDate && activityDate < fromDate) matchesDate = false
      if (toDate && activityDate > toDate) matchesDate = false
    }

    return matchesAction && matchesUser && matchesDate
  })
})


const paginatedHistory = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  const end = start + pageSize
  return filteredHistory.value.slice(start, end)
})

const fetchHistory = async () => {
  loading.value = true
  try {
    await adminStore.loadHistory();
    history.value = adminStore.history || []
    totalElements.value = adminStore.totalActivityHistory || 0
  } catch (error) {
    console.error('Error fetching activity history:', error)
    alert('Failed to fetch activity history')
  } finally {
    loading.value = false
  }
}

const formatDateTime = (timestamp) => {
  const date = new Date(timestamp)
  return date.toLocaleString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const clearFilters = () => {
  actionFilter.value = ''
  userFilter.value = ''
  dateFromFilter.value = ''
  dateToFilter.value = ''
  currentPage.value = 1
}

const buttonClass = (disabled) => {
  return disabled ? 'text-gray-400 border-gray-300 cursor-not-allowed' : 'hover:bg-gray-100 text-gray-700'
}

watch([actionFilter, userFilter, dateFromFilter, dateToFilter], () => {
  currentPage.value = 1
})

onMounted(fetchHistory)
</script>
