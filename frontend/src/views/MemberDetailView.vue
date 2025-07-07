<template>
  <div class="p-4 max-w-xl mx-auto">
    <h2 class="text-2xl font-bold mb-4">Chi tiết thành viên</h2>
    <div v-if="member">
      <div class="mb-4">
        <label class="block text-sm font-medium">Tên</label>
        <input v-model="member.name" class="w-full border px-3 py-2 rounded" />
      </div>
      <div class="mb-4">
        <label class="block text-sm font-medium">Giới tính</label>
        <select v-model="member.gender" class="w-full border px-3 py-2 rounded">
          <option>Nam</option>
          <option>Nữ</option>
        </select>
      </div>
      <div class="mb-4">
        <label class="block text-sm font-medium">Năm sinh</label>
        <input v-model="member.birthYear" type="number" class="w-full border px-3 py-2 rounded" />
      </div>
      <AvatarUpload :memberId="member.id" :avatarUrl="member.avatarUrl" @updated="refresh" />

      <div class="mb-4">
        <label class="block text-sm font-medium">Quan hệ</label>
        <div class="space-y-2">
          <div>
            <label>Cha: </label>
            <input v-model="newRelation.parentId" placeholder="ID cha" class="border rounded px-2 py-1 w-full" />
          </div>
          <div>
            <label>Mẹ: </label>
            <input v-model="newRelation.motherId" placeholder="ID mẹ" class="border rounded px-2 py-1 w-full" />
          </div>
          <div>
            <label>Vợ/Chồng: </label>
            <input v-model="newRelation.spouseId" placeholder="ID vợ/chồng" class="border rounded px-2 py-1 w-full" />
          </div>
          <div>
            <label>Con: </label>
            <input v-model="newRelation.childId" placeholder="ID con" class="border rounded px-2 py-1 w-full" />
          </div>
        </div>
        <button @click="addRelation" class="mt-2 bg-purple-500 text-white px-3 py-1 rounded">Thêm quan hệ</button>
      </div>

      <div class="mb-4">
        <label class="block text-sm font-medium">Lịch sử chỉnh sửa</label>
        <ul class="text-sm list-disc list-inside">
          <li v-for="edit in editHistory" :key="edit.timestamp">
            {{ new Date(edit.timestamp).toLocaleString() }} - {{ edit.action }} bởi {{ edit.modifiedBy }}
          </li>
        </ul>
      </div>

      <div class="flex space-x-2">
        <button @click="update" class="bg-blue-500 text-white px-4 py-2 rounded">Cập nhật</button>
        <button @click="undo" class="bg-yellow-500 text-white px-4 py-2 rounded">Hoàn tác</button>
        <button @click="exportTree" class="bg-green-600 text-white px-4 py-2 rounded">Xuất JSON</button>
        <button @click="exportPDF" class="bg-red-600 text-white px-4 py-2 rounded">Xuất PDF</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import api from '@/services/api'
import AvatarUpload from '@/components/AvatarUpload.vue'

const route = useRoute()
const member = ref(null)
const previousVersion = ref(null)
const newRelation = ref({ parentId: '', motherId: '', spouseId: '', childId: '' })
const editHistory = ref([])

const fetchMember = async () => {
  const res = await api.get(`/members/${route.params.id}`)
  member.value = res.data
  previousVersion.value = JSON.parse(JSON.stringify(res.data))

  const historyRes = await api.get(`/members/${route.params.id}/history`)
  editHistory.value = historyRes.data
}

const update = async () => {
  await api.put(`/members/${member.value.id}`, member.value)
  alert('Cập nhật thành công')
  fetchMember()
}

const undo = () => {
  if (previousVersion.value) {
    member.value = JSON.parse(JSON.stringify(previousVersion.value))
  }
}

const exportTree = async () => {
  const res = await api.get(`/members/${member.value.id}/export-json`)
  const blob = new Blob([JSON.stringify(res.data, null, 2)], { type: 'application/json' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `${member.value.name}-tree.json`
  link.click()
}

const exportPDF = async () => {
  const res = await api.get(`/members/${member.value.id}/export-pdf`, { responseType: 'blob' })
  const blob = new Blob([res.data], { type: 'application/pdf' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `${member.value.name}-tree.pdf`
  link.click()
}

const addRelation = async () => {
  await api.post(`/members/${member.value.id}/add-relation`, newRelation.value)
  alert('Đã thêm quan hệ')
  fetchMember()
}

const refresh = () => {
  fetchMember()
}

onMounted(fetchMember)
</script>
