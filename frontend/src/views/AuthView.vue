<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center px-4">
    <div class="max-w-md w-full bg-white rounded-xl shadow-lg p-8">
      <component :is="isLogin ? 'LoginForm' : 'RegisterForm'" @toggle="toggleForm" />
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import LoginForm from '@/components/auth/LoginForm.vue'
import RegisterForm from '@/components/auth/RegisterForm.vue'

export default {
  name: 'AuthView',
  components: {
    LoginForm,
    RegisterForm
  },
  setup() {
    const isLogin = ref(true)

    const toggleForm = () => {
      isLogin.value = !isLogin.value
    }

    return {
      isLogin,
      toggleForm
    }
  }
}
</script>
