<template>
  <header class="bg-white shadow-sm border-b border-gray-200">
    <div class="px-6 py-4 flex items-center justify-between">
      <div class="flex items-center space-x-4">
        <!-- Logo hoặc menu bên trái -->
      </div>

      <div class="flex items-center space-x-4">
        <div class="relative" ref="menuRef">
          <button @click="showUserMenu = !showUserMenu"
            class="flex items-center space-x-2 text-gray-700 hover:text-gray-900">
            <img v-if="user?.avatarUrl" :src="`http://localhost:8080${user.avatarUrl}`"
              :alt="user?.fullName || 'User Avatar'" class="w-8 h-8 rounded-full">
            <div v-else class="w-8 h-8 rounded-full bg-gray-300 flex items-center justify-center">
              <User class="w-4 h-4" />
            </div>
            <span class="text-sm font-medium">{{ user?.fullName || 'Guest' }}</span>
            <ChevronDown class="w-4 h-4" />
          </button>

          <div v-if="showUserMenu"
            class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg border border-gray-200 z-50">
            <div class="py-1">
              <router-link to="/profile" @click="showUserMenu = false"
                class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                Profile
              </router-link>
              <hr class="my-1">
              <button @click="logout" class="block w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50">
                Logout
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { User, ChevronDown } from 'lucide-vue-next'

export default {
  name: 'AppHeader',
  components: {
    User,
    ChevronDown
  },
  setup() {
    const authStore = useAuthStore()
    const router = useRouter()
    const showUserMenu = ref(false)
    const menuRef = ref(null)

    const user = computed(() => authStore.user)

    const logout = () => {
      authStore.logout()
      router.push('/')
    }

    const handleClickOutside = (event) => {
      if (menuRef.value && !menuRef.value.contains(event.target)) {
        showUserMenu.value = false
      }
    }

    onMounted(() => {
      document.addEventListener('click', handleClickOutside)
    })

    onBeforeUnmount(() => {
      document.removeEventListener('click', handleClickOutside)
    })

    return {
      user,
      showUserMenu,
      logout,
      menuRef
    }
  }
}
</script>
