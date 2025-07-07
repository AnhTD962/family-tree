<template>
    <form @submit.prevent="submit" class="bg-white p-4 rounded shadow">
        <input v-model="form.fullName" placeholder="Full Name" class="input" required />
        <select v-model="form.gender" class="input" required>
            <option value="MALE">Male</option>
            <option value="FEMALE">Female</option>
        </select>
        <input v-model="form.birthPlace" placeholder="Birth Place" class="input" />
        <textarea v-model="form.description" placeholder="Description" class="input"></textarea>
        <button class="btn mt-2">Save</button>
    </form>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import { getMember, createMember, updateMember } from '@/api/familyApi';
import { useRouter } from 'vue-router';

const props = defineProps(['memberId']);
const form = ref({ fullName: '', gender: '', birthPlace: '', description: '' });
const router = useRouter();

onMounted(async () => {
    if (props.memberId) {
        const data = await getMember(props.memberId);
        form.value = { ...data };
    }
});

const submit = async () => {
    if (props.memberId) {
        await updateMember(props.memberId, form.value);
    } else {
        await createMember(form.value);
    }
    router.push('/tree');
};
</script>