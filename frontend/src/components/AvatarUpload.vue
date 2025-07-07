<template>
  <div class="mb-4">
    <label class="block text-sm font-medium">Ảnh đại diện</label>
    <div class="flex items-center space-x-4">
      <img v-if="avatarUrl" :src="avatarUrl" class="w-12 h-12 rounded-full" />
      <input type="file" @change="upload" />
    </div>
  </div>
</template>

<script setup>
import api from '@/services/api'
const props = defineProps({ memberId: String, avatarUrl: String })
const emit = defineEmits(['updated'])

const upload = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  const formData = new FormData()
  formData.append('file', file)
  await api.post(`/members/${props.memberId}/upload-avatar`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
  emit('updated')
}
</script>
