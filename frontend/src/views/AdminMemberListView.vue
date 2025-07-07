<template>
  <div class="p-6">
    <h2 class="text-2xl font-bold mb-4">Danh sách thành viên</h2>

    <div class="mb-4 flex items-center gap-4">
      <input v-model="search" @input="fetchMembers" placeholder="Tìm theo tên..." class="border px-3 py-2 rounded w-1/2" />
      <button @click="goToCreate" class="bg-green-600 text-white px-4 py-2 rounded">➕ Thêm thành viên</button>
    </div>

    <table class="min-w-full border">
      <thead>
        <tr class="bg-gray-100 text-left">
          <th @click="sortBy('name')" class="p-2 border cursor-pointer">Tên</th>
          <th @click="sortBy('gender')" class="p-2 border cursor-pointer">Giới tính</th>
          <th @click="sortBy('birthYear')" class="p-2 border cursor-pointer">Năm sinh</th>
          <th class="p-2 border">Hành động</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="m in paginated" :key="m.id" class="hover:bg-gray-50">
          <td class="p-2 border">{{ m.name }}</td>
          <td class="p-2 border">{{ m.gender }}</td>
          <td class="p-2 border">{{ m.birthYear }}</td>
          <td class="p-2 border">
            <router-link :to="`/members/${m.id}`" class="text-blue-500 mr-2">Sửa</router-link>
            <button @click="deleteMember(m.id)" class="text-red-600 hover:underline">Xoá</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div class="mt-4 flex justify-between items-center">
      <button @click="prevPage" :disabled="page === 1" class="px-3 py-1 bg-gray-200 rounded">← Trước</button>
      <span>Trang {{ page }} / {{ totalPages }}</span>
      <button @click="nextPage" :disabled="page === totalPages" class="px-3 py-1 bg-gray-200 rounded">Sau →</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

if (!auth.isAdmin) {
  router.push('/')
}

const members = ref([])
const search = ref('')
const sortField = ref('name')
const sortAsc = ref(true)
const page = ref(1)
const pageSize = 10

const filtered = computed(() => {
  return members.value
    .filter(m => m.name.toLowerCase().includes(search.value.toLowerCase()))
    .sort((a, b) => {
      const valA = a[sortField.value]
      const valB = b[sortField.value]
      return (valA > valB ? 1 : -1) * (sortAsc.value ? 1 : -1)
    })
})

const totalPages = computed(() => Math.ceil(filtered.value.length / pageSize))
const paginated = computed(() => {
  const start = (page.value - 1) * pageSize
  return filtered.value.slice(start, start + pageSize)
})

const fetchMembers = async () => {
  const res = await api.get('/members')
  members.value = res.data
}

const deleteMember = async (id) => {
  if (!confirm('Bạn có chắc muốn xoá?')) return
  await api.delete(`/members/${id}`)
  members.value = members.value.filter(m => m.id !== id)
}

const sortBy = (field) => {
  if (sortField.value === field) {
    sortAsc.value = !sortAsc.value
  } else {
    sortField.value = field
    sortAsc.value = true
  }
}

const prevPage = () => { if (page.value > 1) page.value-- }
const nextPage = () => { if (page.value < totalPages.value) page.value++ }
const goToCreate = () => router.push('/members/create')

onMounted(fetchMembers)
</script>

<style scoped>
table {
  border-collapse: collapse;
}
th, td {
  text-align: left;
}
th:hover {
  background: #eee;
}
</style>
