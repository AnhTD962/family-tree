<template>
    <div class="p-4 bg-white shadow rounded">
        <h2 class="text-xl font-bold mb-2">{{ member.fullName }}</h2>
        <p><strong>Gender:</strong> {{ member.gender }}</p>
        <p><strong>Birth Place:</strong> {{ member.birthPlace }}</p>
        <p><strong>Description:</strong> {{ member.description }}</p>
    </div>
</template>
<script setup>
defineProps(['member']);
</script>
