<template>
  <div class="p-4">
    <div class="mb-4">
      <input v-model="query" @keyup.enter="search" placeholder="Tìm theo tên, giới tính, năm sinh..." class="border px-3 py-2 rounded w-full" />
    </div>
    <div v-if="results.length">
      <p class="text-sm mb-2">Kết quả tìm kiếm:</p>
      <ul>
        <li v-for="m in results" :key="m.id">
          <router-link :to="`/members/${m.id}`" class="text-blue-500 underline">{{ m.name }} ({{ m.gender }}, {{ m.birthYear }})</router-link>
        </li>
      </ul>
    </div>
    <div v-if="member" class="ml-4 border-l-2 pl-4">
      <div class="flex items-center space-x-2 mb-2">
        <img v-if="member.avatarUrl" :src="member.avatarUrl" class="w-10 h-10 rounded-full" />
        <div>
          <strong>{{ member.name }}</strong> ({{ member.gender }}, {{ member.birthYear }})
          <router-link :to="`/members/${member.id}`" class="text-blue-500 ml-2">Chi tiết</router-link>
        </div>
      </div>
      <div class="ml-4">
        <FamilyTree v-for="childId in member.children" :key="childId" :rootId="childId" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/services/api'
const props = defineProps({ rootId: String })
const member = ref(null)
const query = ref('')
const results = ref([])

const fetch = async () => {
  const res = await api.get(`/members/${props.rootId}`)
  member.value = res.data
}

const search = async () => {
  if (!query.value) return
  const res = await api.get('/members/search', { params: { q: query.value } })
  results.value = res.data
}

onMounted(fetch)
</script>
