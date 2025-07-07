<template>
    <div class="p-4">
        <h1 class="text-2xl font-bold mb-4">Family Tree Edit History</h1>
        <ul>
            <li v-for="entry in history" :key="entry.id" class="mb-2">
                <p><strong>{{ entry.username }}</strong> {{ entry.action }} {{ entry.targetMemberName }} on {{
                    entry.timestamp }}</p>
                <p class="text-sm text-gray-600">{{ entry.details }}</p>
            </li>
        </ul>
    </div>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import { getHistory } from '@/api/adminApi';

const history = ref([]);

onMounted(async () => {
    const res = await getHistory();
    history.value = res.content;
});
</script>