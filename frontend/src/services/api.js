import axios from 'axios'
import { useAuthStore } from '@/store/auth'
import router from '@/router'

// Create axios instance with base URL and headers
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  timeout: 10000 // 10 seconds
})

// Request interceptor
api.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    const token = authStore.token || localStorage.getItem('token')
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor
api.interceptors.response.use(
  (response) => {
    // Return JSON data if available, otherwise return the full response
    return response.data || response
  },
  async (error) => {
    const originalRequest = error.config
    
    // Handle 401 Unauthorized errors
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true
      
      // If we have a refresh token, try to refresh the access token
      const refreshToken = localStorage.getItem('refreshToken')
      
      if (refreshToken) {
        try {
          // Call your refresh token endpoint
          const response = await axios.post(`${import.meta.env.VITE_API_URL || 'http://localhost:8080/api'}/auth/refresh-token`, {
            refreshToken
          })
          
          const { accessToken } = response.data
          
          // Update the token in the store and localStorage
          const authStore = useAuthStore()
          authStore.token = accessToken
          localStorage.setItem('token', accessToken)
          
          // Update the Authorization header
          originalRequest.headers.Authorization = `Bearer ${accessToken}`
          
          // Retry the original request
          return api(originalRequest)
        } catch (refreshError) {
          // If refresh token fails, log the user out
          console.error('Refresh token failed:', refreshError)
          const authStore = useAuthStore()
          authStore.logout()
          router.push('/login')
          return Promise.reject(refreshError)
        }
      } else {
        // No refresh token available, log the user out
        const authStore = useAuthStore()
        authStore.logout()
        router.push('/login')
      }
    }
    
    // Handle other errors
    const errorMessage = error.response?.data?.message || 
                        error.message || 
                        'An error occurred. Please try again.'
    
    // You can add additional error handling here (e.g., show toast notification)
    console.error('API Error:', {
      message: errorMessage,
      status: error.response?.status,
      url: error.config?.url,
      method: error.config?.method
    })
    
    return Promise.reject({
      message: errorMessage,
      status: error.response?.status,
      data: error.response?.data
    })
  }
)

export default api