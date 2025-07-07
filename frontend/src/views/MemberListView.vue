<template>
  <div class="p-6 max-w-5xl mx-auto">
    <div class="flex justify-between items-center mb-4">
      <h2 class="text-2xl font-bold">Danh sách thành viên</h2>
      <RouterLink to="/members/create" class="bg-blue-600 text-white px-4 py-2 rounded">+ Thêm mới</RouterLink>
    </div>

    <div class="mb-4">
      <input
        v-model="search"
        placeholder="Tìm theo tên..."
        class="w-full border px-3 py-2 rounded"
      />
    </div>

    <table class="w-full border">
      <thead>
        <tr class="bg-gray-100">
          <th class="text-left p-2 border">Tên</th>
          <th class="text-left p-2 border">Giới tính</th>
          <th class="text-left p-2 border">Năm sinh</th>
          <th class="text-left p-2 border">Hành động</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="member in filteredMembers" :key="member.id">
          <td class="p-2 border">{{ member.name }}</td>
          <td class="p-2 border">{{ member.gender }}</td>
          <td class="p-2 border">{{ member.birthYear }}</td>
          <td class="p-2 border space-x-2">
            <RouterLink :to="`/members/${member.id}`" class="text-blue-600">Xem</RouterLink>
            <button @click="remove(member.id)" class="text-red-600">Xoá</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'

const members = ref([])
const search = ref('')
const router = useRouter()

const fetchMembers = async () => {
  const res = await api.get('/members')
  members.value = res.data
}

const remove = async (id) => {
  if (confirm('Xác nhận xoá thành viên này?')) {
    await api.delete(`/members/${id}`)
    await fetchMembers()
  }
}

const filteredMembers = computed(() => {
  return members.value.filter(m =>
    m.name.toLowerCase().includes(search.value.toLowerCase())
  )
})

onMounted(fetchMembers)
</script>
