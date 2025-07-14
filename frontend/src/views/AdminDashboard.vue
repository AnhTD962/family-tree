<template>
  <div class="p-6 max-w-7xl mx-auto">
    <!-- Header -->
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900 mb-1">Admin Dashboard</h1>
      <p class="text-gray-500 text-lg">Manage users and monitor family tree activities</p>
    </div>

    <!-- User Management Component -->
    <UserManagement />

    <!-- Activity History Component -->
    <ActivityHistory />
  </div>
</template>

<script setup>
import UserManagement from '@/components/admin/UserManagement.vue'
import ActivityHistory from '@/components/admin/ActivityHistory.vue'
</script>
