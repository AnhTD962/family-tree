<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 p-4">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-md overflow-hidden">
      <!-- Header -->
      <div class="bg-indigo-600 p-6 text-center">
        <h1 class="text-2xl font-bold text-white">Welcome Back</h1>
        <p class="text-indigo-100 mt-1">Sign in to your account</p>
      </div>
      
      <!-- Login Form -->
      <div class="p-8">
        <form @submit.prevent="handleSubmit" class="space-y-6">
          <!-- Username -->
          <div>
            <label for="username" class="block text-sm font-medium text-gray-700 mb-1">
              Username
            </label>
            <div class="relative">
              <input
                id="username"
                v-model="form.username"
                type="text"
                required
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                :class="{ 'border-red-500': errors.username }"
                placeholder="Enter your username"
                @input="clearError('username')"
              />
              <span v-if="errors.username" class="absolute right-3 top-2.5 text-red-500">
                <i class="fas fa-exclamation-circle"></i>
              </span>
            </div>
            <p v-if="errors.username" class="mt-1 text-sm text-red-600">{{ errors.username }}</p>
          </div>
          
          <!-- Password -->
          <div>
            <div class="flex justify-between items-center mb-1">
              <label for="password" class="block text-sm font-medium text-gray-700">
                Password
              </label>
              <router-link 
                to="/forgot-password" 
                class="text-sm text-indigo-600 hover:text-indigo-500"
              >
                Forgot password?
              </router-link>
            </div>
            <div class="relative">
              <input
                id="password"
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                required
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 pr-10"
                :class="{ 'border-red-500': errors.password }"
                placeholder="Enter your password"
                @input="clearError('password')"
              />
              <button
                type="button"
                class="absolute right-3 top-2.5 text-gray-500 hover:text-gray-700"
                @click="showPassword = !showPassword"
              >
                <i :class="showPassword ? 'far fa-eye-slash' : 'far fa-eye'"></i>
              </button>
            </div>
            <p v-if="errors.password" class="mt-1 text-sm text-red-600">{{ errors.password }}</p>
          </div>
          
          <!-- Remember Me -->
          <div class="flex items-center">
            <input
              id="remember"
              v-model="form.rememberMe"
              type="checkbox"
              class="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
            />
            <label for="remember" class="ml-2 block text-sm text-gray-700">
              Remember me
            </label>
          </div>
          
          <!-- Submit Button -->
          <div>
            <button
              type="submit"
              :disabled="loading"
              class="w-full flex justify-center py-2.5 px-4 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <span v-if="!loading">Sign in</span>
              <span v-else class="flex items-center">
                <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Signing in...
              </span>
            </button>
          </div>
        </form>
        
        <!-- Sign Up Link -->
        <div class="mt-6 text-center">
          <p class="text-sm text-gray-600">
            Don't have an account?
            <router-link to="/register" class="font-medium text-indigo-600 hover:text-indigo-500">
              Sign up
            </router-link>
          </p>
        </div>
      </div>
      
      <!-- Error Alert -->
      <div v-if="errorMessage" class="px-6 pb-6">
        <div class="bg-red-50 border-l-4 border-red-400 p-4">
          <div class="flex">
            <div class="flex-shrink-0">
              <i class="fas fa-exclamation-circle h-5 w-5 text-red-400"></i>
            </div>
            <div class="ml-3">
              <p class="text-sm text-red-700">{{ errorMessage }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/store/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// Form state
const form = reactive({
  username: '',
  password: '',
  rememberMe: false
})

// UI state
const loading = ref(false)
const showPassword = ref(false)
const errorMessage = ref('')
const errors = reactive({
  username: '',
  password: ''
})

// Check if user is already logged in
onMounted(() => {
  if (authStore.isAuthenticated) {
    router.push(route.query.redirect || '/')
  }
  
  // Pre-fill username if available in query params
  if (route.query.username) {
    form.username = route.query.username
  }
})

// Clear error when user starts typing
const clearError = (field) => {
  if (errors[field]) {
    errors[field] = ''
  }
  if (errorMessage.value) {
    errorMessage.value = ''
  }
}

// Form submission
const handleSubmit = async () => {
  // Reset errors
  Object.keys(errors).forEach(key => errors[key] = '')
  errorMessage.value = ''
  
  // Simple validation
  let isValid = true
  
  if (!form.username.trim()) {
    errors.username = 'Username is required'
    isValid = false
  }
  
  if (!form.password) {
    errors.password = 'Password is required'
    isValid = false
  } else if (form.password.length < 6) {
    errors.password = 'Password must be at least 6 characters'
    isValid = false
  }
  
  if (!isValid) return
  
  // Submit form
  loading.value = true
  
  try {
    await authStore.login(form.username, form.password)
    
    // Redirect to the originally requested page or home
    const redirectPath = route.query.redirect || '/'
    router.push(redirectPath)
    
  } catch (error) {
    console.error('Login failed:', error)
    errorMessage.value = error.message || 'Invalid username or password. Please try again.'
    
    // Auto-hide error message after 5 seconds
    setTimeout(() => {
      errorMessage.value = ''
    }, 5000)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* Add any custom styles here */
</style>
