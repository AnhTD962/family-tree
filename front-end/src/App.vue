<!-- src/App.vue -->
<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow p-4 flex justify-between items-center">
      <h1 class="text-xl font-bold text-blue-600">Family Tree App</h1>
      <nav class="flex gap-4">
        <router-link to="/" class="text-gray-700 hover:text-blue-600">Home</router-link>
        <router-link v-if="isLoggedIn" to="/tree" class="text-gray-700 hover:text-blue-600">Tree</router-link>
        <router-link v-if="isAdmin" to="/admin/users" class="text-gray-700 hover:text-blue-600">Admin</router-link>
        <router-link v-if="!isLoggedIn" to="/login" class="text-gray-700 hover:text-blue-600">Login</router-link>
        <router-link v-if="!isLoggedIn" to="/register" class="text-gray-700 hover:text-blue-600">Register</router-link>
        <button v-if="isLoggedIn" @click="logout" class="text-red-600 hover:underline">Logout</button>
      </nav>
    </header>

    <main class="p-4">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useAuthStore } from '@/store/auth';
import { useRouter } from 'vue-router';

const auth = useAuthStore();
const router = useRouter();

const logout = () => {
  auth.logout();
  router.push('/login');
};

const isLoggedIn = computed(() => auth.isAuthenticated);
const isAdmin = computed(() => auth.user?.roles?.includes('ADMIN'));
</script>

<style>
body {
  margin: 0;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}
</style>
