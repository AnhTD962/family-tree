<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 p-4">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-2xl overflow-hidden">
      <!-- Header -->
      <div class="bg-indigo-600 p-6 text-center">
        <h1 class="text-2xl font-bold text-white">Create Your Account</h1>
        <p class="text-indigo-100 mt-1">Join our family tree community</p>
      </div>
      
      <!-- Registration Form -->
      <div class="p-8">
        <form @submit.prevent="handleSubmit" class="space-y-6">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <!-- First Name -->
            <div>
              <label for="firstName" class="block text-sm font-medium text-gray-700 mb-1">
                First Name <span class="text-red-500">*</span>
              </label>
              <input
                id="firstName"
                v-model="form.firstName"
                type="text"
                required
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                :class="{ 'border-red-500': errors.firstName }"
                placeholder="John"
                @input="clearError('firstName')"
              />
              <p v-if="errors.firstName" class="mt-1 text-sm text-red-600">{{ errors.firstName }}</p>
            </div>
            
            <!-- Last Name -->
            <div>
              <label for="lastName" class="block text-sm font-medium text-gray-700 mb-1">
                Last Name <span class="text-red-500">*</span>
              </label>
              <input
                id="lastName"
                v-model="form.lastName"
                type="text"
                required
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                :class="{ 'border-red-500': errors.lastName }"
                placeholder="Doe"
                @input="clearError('lastName')"
              />
              <p v-if="errors.lastName" class="mt-1 text-sm text-red-600">{{ errors.lastName }}</p>
            </div>
          </div>
          
          <!-- Email -->
          <div>
            <label for="email" class="block text-sm font-medium text-gray-700 mb-1">
              Email Address <span class="text-red-500">*</span>
            </label>
            <input
              id="email"
              v-model="form.email"
              type="email"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
              :class="{ 'border-red-500': errors.email }"
              placeholder="john@example.com"
              @input="clearError('email')"
            />
            <p v-if="errors.email" class="mt-1 text-sm text-red-600">{{ errors.email }}</p>
          </div>
          
          <!-- Username -->
          <div>
            <label for="username" class="block text-sm font-medium text-gray-700 mb-1">
              Username <span class="text-red-500">*</span>
            </label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <span class="text-gray-500 sm:text-sm">@</span>
              </div>
              <input
                id="username"
                v-model="form.username"
                type="text"
                required
                class="pl-7 w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                :class="{ 'border-red-500': errors.username }"
                placeholder="johndoe"
                @input="clearError('username')"
              />
            </div>
            <p v-if="errors.username" class="mt-1 text-sm text-red-600">{{ errors.username }}</p>
          </div>
          
          <!-- Password -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label for="password" class="block text-sm font-medium text-gray-700 mb-1">
                Password <span class="text-red-500">*</span>
              </label>
              <div class="relative">
                <input
                  id="password"
                  v-model="form.password"
                  :type="showPassword ? 'text' : 'password'"
                  required
                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 pr-10"
                  :class="{ 'border-red-500': errors.password }"
                  placeholder="••••••••"
                  @input="clearError('password')"
                />
                <button
                  type="button"
                  class="absolute right-3 top-2.5 text-gray-500 hover:text-gray-700"
                  @click="showPassword = !showPassword"
                  :title="showPassword ? 'Hide password' : 'Show password'"
                >
                  <i :class="showPassword ? 'far fa-eye-slash' : 'far fa-eye'"></i>
                </button>
              </div>
              <p v-if="errors.password" class="mt-1 text-sm text-red-600">{{ errors.password }}</p>
              <p class="mt-1 text-xs text-gray-500">
                Must be at least 8 characters with a number and special character
              </p>
            </div>
            
            <!-- Confirm Password -->
            <div>
              <label for="confirmPassword" class="block text-sm font-medium text-gray-700 mb-1">
                Confirm Password <span class="text-red-500">*</span>
              </label>
              <input
                id="confirmPassword"
                v-model="form.confirmPassword"
                :type="showPassword ? 'text' : 'password'"
                required
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                :class="{ 'border-red-500': errors.confirmPassword }"
                placeholder="••••••••"
                @input="clearError('confirmPassword')"
              />
              <p v-if="errors.confirmPassword" class="mt-1 text-sm text-red-600">{{ errors.confirmPassword }}</p>
            </div>
          </div>
          
          <!-- Terms and Conditions -->
          <div class="flex items-start">
            <div class="flex items-center h-5">
              <input
                id="terms"
                v-model="form.termsAccepted"
                type="checkbox"
                required
                class="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
                :class="{ 'border-red-500': errors.termsAccepted }"
              />
            </div>
            <div class="ml-3 text-sm">
              <label for="terms" class="font-medium text-gray-700">
                I agree to the <a href="#" class="text-indigo-600 hover:text-indigo-500">Terms of Service</a> and 
                <a href="#" class="text-indigo-600 hover:text-indigo-500">Privacy Policy</a>
                <span class="text-red-500">*</span>
              </label>
              <p v-if="errors.termsAccepted" class="mt-1 text-sm text-red-600">{{ errors.termsAccepted }}</p>
            </div>
          </div>
          
          <!-- Submit Button -->
          <div>
            <button
              type="submit"
              :disabled="loading"
              class="w-full flex justify-center py-2.5 px-4 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <span v-if="!loading">Create Account</span>
              <span v-else class="flex items-center">
                <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Creating Account...
              </span>
            </button>
          </div>
        </form>
        
        <!-- Sign In Link -->
        <div class="mt-6 text-center">
          <p class="text-sm text-gray-600">
            Already have an account?
            <router-link to="/login" class="font-medium text-indigo-600 hover:text-indigo-500">
              Sign in
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
  firstName: '',
  lastName: '',
  email: '',
  username: '',
  password: '',
  confirmPassword: '',
  termsAccepted: false
})

// UI state
const loading = ref(false)
const showPassword = ref(false)
const errorMessage = ref('')
const errors = reactive({
  firstName: '',
  lastName: '',
  email: '',
  username: '',
  password: '',
  confirmPassword: '',
  termsAccepted: ''
})

// Check if user is already logged in
onMounted(() => {
  if (authStore.isAuthenticated) {
    router.push(route.query.redirect || '/')
  }
  
  // Pre-fill email if available in query params
  if (route.query.email) {
    form.email = route.query.email
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

// Validate form
const validateForm = () => {
  let isValid = true
  
  // Reset errors
  Object.keys(errors).forEach(key => errors[key] = '')
  
  // First Name validation
  if (!form.firstName.trim()) {
    errors.firstName = 'First name is required'
    isValid = false
  }
  
  // Last Name validation
  if (!form.lastName.trim()) {
    errors.lastName = 'Last name is required'
    isValid = false
  }
  
  // Email validation
  if (!form.email.trim()) {
    errors.email = 'Email is required'
    isValid = false
  } else if (!/\S+@\S+\.\S+/.test(form.email)) {
    errors.email = 'Please enter a valid email address'
    isValid = false
  }
  
  // Username validation
  if (!form.username.trim()) {
    errors.username = 'Username is required'
    isValid = false
  } else if (!/^[a-zA-Z0-9_]+$/.test(form.username)) {
    errors.username = 'Username can only contain letters, numbers, and underscores'
    isValid = false
  } else if (form.username.length < 3) {
    errors.username = 'Username must be at least 3 characters long'
    isValid = false
  }
  
  // Password validation
  if (!form.password) {
    errors.password = 'Password is required'
    isValid = false
  } else if (form.password.length < 8) {
    errors.password = 'Password must be at least 8 characters long'
    isValid = false
  } else if (!/(?=.*[0-9])(?=.*[!@#$%^&*])(?=.*[a-z])(?=.*[A-Z])/.test(form.password)) {
    errors.password = 'Password must contain at least one number, one special character, one lowercase and one uppercase letter'
    isValid = false
  }
  
  // Confirm Password validation
  if (form.password !== form.confirmPassword) {
    errors.confirmPassword = 'Passwords do not match'
    isValid = false
  }
  
  // Terms and Conditions validation
  if (!form.termsAccepted) {
    errors.termsAccepted = 'You must accept the terms and conditions'
    isValid = false
  }
  
  return isValid
}

// Form submission
const handleSubmit = async () => {
  // Reset error message
  errorMessage.value = ''
  
  // Validate form
  if (!validateForm()) {
    return
  }
  
  // Submit form
  loading.value = true
  
  try {
    const userData = {
      firstName: form.firstName.trim(),
      lastName: form.lastName.trim(),
      email: form.email.trim(),
      username: form.username.trim(),
      password: form.password
    }
    
    await authStore.register(userData)
    
    // Redirect to login with success message
    router.push({
      path: '/login',
      query: { 
        registered: 'true',
        email: form.email.trim()
      }
    })
    
  } catch (error) {
    console.error('Registration failed:', error)
    
    // Handle specific error messages from the API
    if (error.response && error.response.data) {
      const { data } = error.response
      
      if (data.fieldErrors) {
        // Handle field-specific errors
        data.fieldErrors.forEach(({ field, message }) => {
          if (field in errors) {
            errors[field] = message
          } else {
            errorMessage.value = message
          }
        })
      } else if (data.message) {
        errorMessage.value = data.message
      } else {
        errorMessage.value = 'Registration failed. Please try again.'
      }
    } else {
      errorMessage.value = error.message || 'An unexpected error occurred. Please try again.'
    }
    
    // Auto-hide error message after 10 seconds
    setTimeout(() => {
      errorMessage.value = ''
    }, 10000)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* Custom styles can be added here */
</style>