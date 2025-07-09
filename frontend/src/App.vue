<template>
  <div id="app" class="min-h-screen bg-gray-50">
    <div v-if="isAuthenticated" class="flex">
      <AppSidebar />
      <div class="flex-1 flex flex-col">
        <AppHeader />
        <main class="flex-1 p-6">
          <router-view />
        </main>
      </div>
    </div>
    <div v-else class="min-h-screen flex flex-col">
      <main class="flex-1 p-6">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script>
import { computed, onMounted } from 'vue'
import { useAuthStore } from './stores/auth'
import AppHeader from './components/common/AppHeader.vue'
import AppSidebar from './components/common/AppSidebar.vue'

export default {
  name: 'App',
  components: {
    AppHeader,
    AppSidebar
  },
  setup() {
    const authStore = useAuthStore()
    const isAuthenticated = computed(() => authStore.isAuthenticated)

    onMounted(() => {
      authStore.checkAuth()
    })

    return {
      isAuthenticated
    }
  }
}
</script>
