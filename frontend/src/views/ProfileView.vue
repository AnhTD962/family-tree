<template>
  <div class="max-w-2xl mx-auto py-10 px-4">
    <h1 class="text-2xl font-bold text-gray-900 mb-6">Your Profile</h1>

    <div v-if="loading" class="text-center text-gray-500">Loading...</div>

    <form v-else @submit.prevent="submitForm" class="space-y-6 bg-white p-6 rounded-lg shadow-md">
      <!-- Avatar -->
      <div class="flex items-center space-x-4">
        <img :src="avatarPreview || (form.avatarUrl ? `http://localhost:8080${form.avatarUrl}` : '/default-avatar.png')"
          :alt="form.fullName || 'User Avatar'" class="w-20 h-20 object-cover rounded-full border" />
        <input type="file" @change="handleAvatarChange" accept="image/*" />
      </div>

      <!-- Full Name -->
      <div>
        <label class="block text-sm font-medium text-gray-700">Full Name</label>
        <input v-model="form.fullName" type="text"
          class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring focus:ring-indigo-200" />
      </div>

      <!-- Phone -->
      <div>
        <label class="block text-sm font-medium text-gray-700">Phone Number</label>
        <input v-model="form.phoneNumber" type="text"
          class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring focus:ring-indigo-200" />
      </div>

      <!-- Email -->
      <div>
        <label class="block text-sm font-medium text-gray-700">Email</label>
        <input v-model="form.email" type="text" readonly
          class="mt-1 block w-full border-gray-200 bg-gray-100 rounded-md shadow-sm" />
      </div>

      <!-- Username -->
      <div>
        <label class="block text-sm font-medium text-gray-700">Username</label>
        <input v-model="form.username" type="text" readonly
          class="mt-1 block w-full border-gray-200 bg-gray-100 rounded-md shadow-sm" />
      </div>

      <!-- Submit -->
      <div class="flex justify-end">
        <button type="submit" class="bg-indigo-600 text-white px-4 py-2 rounded hover:bg-indigo-700 transition">
          Save Changes
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const form = ref({
  fullName: '',
  phoneNumber: '',
  email: '',
  username: '',
  avatarUrl: '',
})

const loading = ref(true)
const avatarFile = ref(null)
const avatarPreview = ref(null)

onMounted(async () => {
  loading.value = true
  const data = await userStore.fetchProfile()
  Object.assign(form.value, data)
  loading.value = false
})

const handleAvatarChange = (e) => {
  const file = e.target.files[0]
  if (file) {
    avatarFile.value = file
    avatarPreview.value = URL.createObjectURL(file)
  }
}

const submitForm = async () => {
  await userStore.updateProfile({
    user: form.value,
    file: avatarFile.value,
  })
  alert('Profile updated successfully')
}
</script>
