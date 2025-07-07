<template>
    <table class="table-auto w-full bg-white shadow rounded">
        <thead>
            <tr>
                <th class="p-2">Username</th>
                <th class="p-2">Email</th>
                <th class="p-2">Role</th>
                <th class="p-2">Status</th>
                <th class="p-2">Action</th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="user in users" :key="user.id">
                <td class="p-2">{{ user.username }}</td>
                <td class="p-2">{{ user.email }}</td>
                <td class="p-2">{{ user.roles.join(', ') }}</td>
                <td class="p-2">{{ user.active ? 'Active' : 'Locked' }}</td>
                <td class="p-2">
                    <button class="btn" @click="toggle(user.id)">Toggle</button>
                </td>
            </tr>
        </tbody>
    </table>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import { getUsers, toggleUserStatus } from '@/api/adminApi';

const users = ref([]);

const load = async () => {
    users.value = await getUsers();
};

onMounted(load);

const toggle = async (id) => {
    await toggleUserStatus(id);
    load();
};
</script>
