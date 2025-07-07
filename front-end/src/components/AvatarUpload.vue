<template>
    <div>
        <input type="file" @change="handleFile" />
        <img v-if="previewUrl" :src="previewUrl" alt="Avatar Preview"
            class="w-32 h-32 object-cover rounded-full mt-2" />
    </div>
</template>
<script setup>
import { ref } from 'vue';
const previewUrl = ref(null);
const emit = defineEmits(['upload']);

const handleFile = (e) => {
    const file = e.target.files[0];
    if (file) {
        previewUrl.value = URL.createObjectURL(file);
        emit('upload', file);
    }
};
</script>