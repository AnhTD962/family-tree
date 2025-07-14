<template>
    <aside class="w-64 bg-white shadow-sm border-r border-gray-200 min-h-screen">
        <div class="p-6">
            <div class="flex items-center space-x-2 mb-8">
                <div class="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
                    <TreePine class="w-5 h-5 text-white" />
                </div>
                <span class="text-xl font-bold text-gray-900">Family Tree</span>
            </div>

            <nav class="space-y-2">
                <router-link to="/"
                    class="flex items-center space-x-3 px-3 py-2 rounded-md text-gray-700 hover:bg-gray-100"
                    :class="{ 'bg-blue-50 text-blue-700': $route.name === 'FamilyTree' }">
                    <TreePine class="w-5 h-5" />
                    <span>Family Tree</span>
                </router-link>

                <router-link v-if="isAdmin" to="/admin"
                    class="flex items-center space-x-3 px-3 py-2 rounded-md text-gray-700 hover:bg-gray-100"
                    :class="{ 'bg-blue-50 text-blue-700': $route.name === 'AdminDashboard' }">
                    <Settings class="w-5 h-5" />
                    <span>Admin Panel</span>
                </router-link>
            </nav>
        </div>
    </aside>
</template>

<script>
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { TreePine, Settings } from 'lucide-vue-next'

export default {
    name: 'AppSidebar',
    components: {
        TreePine,
        Settings
    },
    setup() {
        const authStore = useAuthStore()

        const isAdmin = computed(() => authStore.isAdmin)

        return {
            isAdmin
        }
    }
}
</script>
