<template>
  <div class="p-4">
    <h2 class="text-2xl font-bold mb-4">Cây phả hệ</h2>
    <FamilyTree :rootId="rootId" />
  </div>
</template>

<script setup>
import FamilyTree from '@/components/FamilyTree.vue'
import { ref } from 'vue'

const rootId = ref('') // Bạn có thể gán rootId từ API hoặc local
</script>
