<template>
  <div>
    <div class="text-center mb-6">
      <div class="mx-auto h-12 w-12 bg-blue-600 rounded-lg flex items-center justify-center">
        <TreePine class="h-8 w-8 text-white" />
      </div>
      <h2 class="mt-4 text-2xl font-bold text-gray-900">Create a new account</h2>
    </div>

    <form class="space-y-4" @submit.prevent="handleRegister">
      <input v-model="form.username" type="text" required placeholder="Username"
        class="w-full px-4 py-2 border border-gray-300 rounded-lg" />
      <input v-model="form.email" type="email" required placeholder="Email"
        class="w-full px-4 py-2 border border-gray-300 rounded-lg" />
      <input v-model="form.fullName" type="text" required placeholder="Full name"
        class="w-full px-4 py-2 border border-gray-300 rounded-lg" />
      <input v-model="form.phoneNumber" type="tel" required placeholder="Phone number"
        class="w-full px-4 py-2 border border-gray-300 rounded-lg" />
      <input v-model="form.avatarUrl" type="url" placeholder="Avatar URL (optional)"
        class="w-full px-4 py-2 border border-gray-300 rounded-lg" />
      <input v-model="form.password" type="password" required placeholder="Password"
        class="w-full px-4 py-2 border border-gray-300 rounded-lg" />
      <input v-model="form.confirmPassword" type="password" required placeholder="Confirm password"
        class="w-full px-4 py-2 border border-gray-300 rounded-lg" />

      <div v-if="errorMessage" class="text-red-500 text-sm text-center">{{ errorMessage }}</div>

      <button type="submit" :disabled="loading"
        class="w-full bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 disabled:opacity-50">
        <span v-if="loading" class="flex justify-center items-center">
          <div class="animate-spin h-5 w-5 border-2 border-white border-t-transparent rounded-full mr-2"></div>
          Registering...
        </span>
        <span v-else>Register</span>
      </button>
    </form>

    <div class="mt-4 text-center">
      <button @click="$emit('toggle')" class="text-blue-600 hover:underline">
        Already have an account? Sign in
      </button>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { TreePine } from 'lucide-vue-next'

export default {
  name: 'RegisterForm',
  components: { TreePine },
  setup() {
    const form = reactive({
      username: '',
      email: '',
      fullName: '',
      phoneNumber: '',
      avatarUrl: '',
      password: '',
      confirmPassword: ''
    })

    const errorMessage = ref('')
    const loading = ref(false)
    const router = useRouter()
    const authStore = useAuthStore()

    const handleRegister = async () => {
      errorMessage.value = ''

      if (form.password !== form.confirmPassword) {
        errorMessage.value = 'Passwords do not match.'
        return
      }

      try {
        loading.value = true
        await authStore.register({
          username: form.username,
          email: form.email,
          fullName: form.fullName,
          phoneNumber: form.phoneNumber,
          avatarUrl: form.avatarUrl,
          password: form.password
        })
        router.push('/family-tree')
      } catch (err) {
        errorMessage.value = err.message || 'Registration failed.'
      } finally {
        loading.value = false
      }
    }

    return {
      form,
      errorMessage,
      loading,
      handleRegister
    }
  }
}
</script>
