import axios from 'axios'
import { API_BASE_URL } from '@/utils/constants'
import { getToken, getRefreshToken, setTokens, removeTokens } from '@/utils/auth'
import { useToast } from 'vue-toastification'

// Toast instance (safe fallback for non-UI environments)
let toast
try {
  toast = useToast()
} catch (err) {
  toast = { error: console.error }
}

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request Interceptor: Attach access token
api.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Response Interceptor: Handle errors + refresh logic
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    // Handle expired token (401) and refresh
    if (error.response?.status === 401 && !originalRequest._retry && getRefreshToken()) {
      originalRequest._retry = true
      try {
        const refreshResponse = await axios.post(`${API_BASE_URL}/api/auth/refresh`, null, {
          headers: {
            Authorization: `Bearer ${getRefreshToken()}`,
          },
        })

        const newToken = refreshResponse.data.token
        const newRefreshToken = refreshResponse.data.refreshToken

        setTokens(newToken, newRefreshToken)

        originalRequest.headers.Authorization = `Bearer ${newToken}`
        return api(originalRequest)
      } catch (refreshError) {
        removeTokens()
        toast.error('Session expired. Please login again.')
        window.location.href = '/login'
        return Promise.reject(refreshError)
      }
    }

    // General error handling
    if (error.response?.status === 401) {
      removeTokens()
      toast.error('Unauthorized. Please login again.')
      window.location.href = '/login'
    } else if (error.response?.status >= 500) {
      toast.error('Server error. Please try again later.')
    } else if (error.response?.data?.message) {
      toast.error(error.response.data.message)
    }

    return Promise.reject(error)
  }
)

export default api
