<template>
    <div class="max-w-sm mx-auto p-4 bg-white rounded shadow">
        <h2 class="text-xl font-bold mb-4">Login</h2>
        <form @submit.prevent="handleLogin">
            <input v-model="form.username" placeholder="Username" class="input" required />
            <input v-model="form.password" placeholder="Password" type="password" class="input" required />
            <button class="btn w-full mt-4">Login</button>
            <p class="text-red-500 mt-2" v-if="error">{{ error }}</p>
        </form>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { useAuthStore } from '@/store/auth';
import { useRouter } from 'vue-router';

const form = ref({ username: '', password: '' });
const error = ref('');
const auth = useAuthStore();
const router = useRouter();

const handleLogin = async () => {
    try {
        await auth.login(form.value);
        router.push('/tree');
    } catch (e) {
        error.value = 'Invalid credentials';
    }
};
</script>

<style scoped>
.input {
    @apply w-full p-2 border border-gray-300 rounded mb-2;
}

.btn {
    @apply bg-blue-500 hover:bg-blue-600 text-white py-2 rounded;
}
</style>