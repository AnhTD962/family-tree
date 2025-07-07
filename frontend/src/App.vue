<template>
  <div id="app">
    <nav v-if="!isAuthPage" class="bg-gray-800 text-white p-4">
      <div class="container mx-auto flex justify-between items-center">
        <router-link to="/" class="text-xl font-bold">Family Tree</router-link>
        <div class="space-x-4">
          <router-link to="/" class="hover:text-gray-300">Home</router-link>
          <router-link to="/family" class="hover:text-gray-300">Family</router-link>
          <router-link to="/profile" class="hover:text-gray-300" v-if="isLoggedIn">Profile</router-link>
          <button 
            v-if="isLoggedIn" 
            @click="handleLogout"
            class="bg-red-600 hover:bg-red-700 px-4 py-2 rounded"
          >
            Logout
          </button>
          <router-link 
            v-else
            to="/login" 
            class="bg-blue-600 hover:bg-blue-700 px-4 py-2 rounded"
          >
            Login
          </router-link>
        </div>
      </div>
    </nav>
    
    <main class="container mx-auto p-4">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '@/store/user.js';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const isLoggedIn = computed(() => userStore.isLoggedIn);
const isAuthPage = computed(() => ['login', 'register'].includes(route.name));

const handleLogout = async () => {
  await userStore.logout();
  router.push('/login');
};
</script>

<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.router-link-active {
  @apply text-blue-400;
}
</style>