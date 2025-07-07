<template>
    <div class="max-w-sm mx-auto p-4 bg-white rounded shadow">
        <h2 class="text-xl font-bold mb-4">Register</h2>
        <form @submit.prevent="handleRegister">
            <input v-model="form.username" placeholder="Username" class="input" required />
            <input v-model="form.email" placeholder="Email" type="email" class="input" required />
            <input v-model="form.phoneNumber" placeholder="Phone" class="input" required />
            <input v-model="form.fullName" placeholder="Full Name" class="input" required />
            <input v-model="form.password" placeholder="Password" type="password" class="input" required />
            <button class="btn w-full mt-4">Register</button>
            <p class="text-red-500 mt-2" v-if="error">{{ error }}</p>
        </form>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { useAuthStore } from '@/store/auth';
import { useRouter } from 'vue-router';

const form = ref({
    username: '',
    email: '',
    phoneNumber: '',
    fullName: '',
    password: '',
});
const error = ref('');
const auth = useAuthStore();
const router = useRouter();

const handleRegister = async () => {
    try {
        await auth.register(form.value);
        router.push('/tree');
    } catch (e) {
        error.value = 'Registration failed';
    }
};
</script>

<style scoped>
.input {
    @apply w-full p-2 border border-gray-300 rounded mb-2;
}

.btn {
    @apply bg-green-500 hover:bg-green-600 text-white py-2 rounded;
}
</style>
