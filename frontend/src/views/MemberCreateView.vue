<template>
  <div class="p-6 max-w-xl mx-auto">
    <h2 class="text-2xl font-bold mb-4">Thêm thành viên mới</h2>

    <form @submit.prevent="create">
      <div class="mb-4">
        <label class="block text-sm font-medium">Tên</label>
        <input v-model="form.name" class="w-full border px-3 py-2 rounded" required />
      </div>

      <div class="mb-4">
        <label class="block text-sm font-medium">Giới tính</label>
        <select v-model="form.gender" class="w-full border px-3 py-2 rounded" required>
          <option disabled value="">Chọn giới tính</option>
          <option>Nam</option>
          <option>Nữ</option>
        </select>
      </div>

      <div class="mb-4">
        <label class="block text-sm font-medium">Năm sinh</label>
        <input type="number" v-model="form.birthYear" class="w-full border px-3 py-2 rounded" required />
      </div>

      <div class="flex gap-3">
        <button type="submit" class="bg-blue-600 text-white px-4 py-2 rounded">Tạo</button>
        <button @click="router.back()" type="button" class="px-4 py-2 border rounded">Huỷ</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'

const router = useRouter()
const form = ref({ name: '', gender: '', birthYear: '' })

const create = async () => {
  await api.post('/members', form.value)
  alert('Tạo thành viên thành công')
  router.push('/admin/members')
}
</script>

<style scoped>
form label {
  font-weight: 500;
}
</style>