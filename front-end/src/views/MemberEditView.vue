<template>
    <div class="p-4 max-w-2xl mx-auto">
        <MemberForm :memberId="route.params.id" />
    </div>
</template>
<script setup>
import { useRoute } from 'vue-router';
import MemberForm from '@/components/MemberForm.vue';

const route = useRoute();
</script>
