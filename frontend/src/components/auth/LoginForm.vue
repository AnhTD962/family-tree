<template>
  <div>
    <div class="text-center mb-6">
      <div class="mx-auto h-12 w-12 bg-blue-600 rounded-lg flex items-center justify-center">
        <TreePine class="h-8 w-8 text-white" />
      </div>
      <h2 class="mt-4 text-2xl font-bold text-gray-900">Sign in to your account</h2>
    </div>

    <form class="space-y-6" @submit.prevent="handleLogin">
      <div>
        <input id="username" v-model="form.username" type="text" required
          class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
          placeholder="Username" />
      </div>
      <div>
        <input id="password" v-model="form.password" type="password" required
          class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
          placeholder="Password" />
      </div>

      <div>
        <button type="submit" :disabled="loading"
          class="w-full bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 disabled:opacity-50">
          <span v-if="loading" class="flex justify-center items-center">
            <div class="animate-spin h-5 w-5 border-2 border-white border-t-transparent rounded-full mr-2"></div>
            Signing in...
          </span>
          <span v-else>Sign in</span>
        </button>
      </div>
    </form>

    <div class="mt-4 text-center">
      <button @click="$emit('toggle')" class="text-blue-600 hover:underline">
        Don't have an account? Sign up
      </button>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useToast } from 'vue-toastification'
import { TreePine } from 'lucide-vue-next'

export default {
  name: 'LoginForm',
  components: { TreePine },
  setup() {
    const form = ref({
      username: '',
      password: ''
    })
    const loading = ref(false)
    const router = useRouter()
    const authStore = useAuthStore()
    const toast = useToast()

    const handleLogin = async () => {
      if (!form.value.username || !form.value.password) {
        toast.error('Please fill in all fields')
        return
      }

      loading.value = true
      try {
        await authStore.login(form.value)
        toast.success('Login successful!')
        router.push('/')
      } catch (error) {
        toast.error(error.message || 'Login failed.')
      } finally {
        loading.value = false
      }
    }

    return {
      form,
      loading,
      handleLogin
    }
  }
}
</script>
