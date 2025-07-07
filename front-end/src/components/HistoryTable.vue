<template>
    <div>
        <table class="table-auto w-full bg-white shadow rounded">
            <thead>
                <tr>
                    <th class="p-2">User</th>
                    <th class="p-2">Action</th>
                    <th class="p-2">Target</th>
                    <th class="p-2">Details</th>
                    <th class="p-2">Time</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="h in history" :key="h.id">
                    <td class="p-2">{{ h.username }}</td>
                    <td class="p-2">{{ h.action }}</td>
                    <td class="p-2">{{ h.targetMemberName }}</td>
                    <td class="p-2">{{ h.details }}</td>
                    <td class="p-2">{{ new Date(h.timestamp).toLocaleString() }}</td>
                </tr>
            </tbody>
        </table>
        <div class="mt-4 flex justify-between">
            <button class="btn" @click="prev" :disabled="page === 0">Previous</button>
            <button class="btn" @click="next">Next</button>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getHistory } from '@/api/adminApi';

const history = ref([]);
const page = ref(0);
const size = 20;

const load = async () => {
    const res = await getHistory(page.value, size);
    history.value = res.content || [];
};

const prev = () => {
    if (page.value > 0) {
        page.value--;
        load();
    }
};

const next = () => {
    page.value++;
    load();
};

onMounted(load);
</script>
