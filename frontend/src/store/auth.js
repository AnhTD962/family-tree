import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import AuthService from '@/services/auth.service'

export const useAuthStore = defineStore('auth', () => {
  const router = useRouter()
  const user = ref(JSON.parse(localStorage.getItem('user')))
  const token = ref(localStorage.getItem('token'))
  const returnUrl = ref('/')

  const isAuthenticated = computed(() => !!token.value)
  const currentUser = computed(() => user.value)
      }
    },

    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
      delete axios.defaults.headers.common['Authorization']
    }
  }
})
