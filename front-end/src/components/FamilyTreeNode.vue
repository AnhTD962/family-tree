<!-- src/components/FamilyTreeNode.vue -->
<template>
    <div class="p-4 rounded-lg shadow text-center border-2 transition-all duration-300" :class="{
        'border-blue-400 bg-blue-50': member.gender === 'Male',
        'border-pink-400 bg-pink-50': member.gender === 'Female',
    }">
        <img v-if="member.avatarUrl" :src="member.avatarUrl" alt="Avatar"
            class="w-16 h-16 mx-auto mb-2 rounded-full object-cover border" />
        <div class="font-semibold text-base truncate">{{ member.fullName }}</div>
        <div class="text-sm text-gray-600">{{ member.gender }}</div>
        <div class="text-xs text-gray-500" v-if="member.birthDate">Born: {{ member.birthDate }}</div>

        <router-link :to="`/member/${member.id}`" class="text-sm text-blue-600 hover:underline mt-1 inline-block">
            View Details
        </router-link>

        <button v-if="member.children?.length" @click="$emit('toggle')"
            class="text-xs text-gray-500 mt-2 hover:text-black">
            {{ showChildren ? 'Hide children ▲' : 'Show children ▼' }}
        </button>
    </div>
</template>

<script setup>
defineProps({
    member: Object,
    showChildren: Boolean
});
defineEmits(['toggle']);
</script>
