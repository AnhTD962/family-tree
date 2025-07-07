<template>
    <div class="p-4">
        <h2 class="text-xl font-bold text-center text-blue-600 mb-4">Family Tree</h2>
        <!-- ✅ Chỉ dùng component D3 -->
        <FamilyTreeD3 v-if="treeData" :data="treeData" />
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getFamilyTree } from '@/api/familyApi';
import { buildTree } from '@/utils/formatFamilyTree';
import FamilyTreeD3 from '@/components/FamilyTreeD3.vue';

const treeData = ref(null);

onMounted(async () => {
    const res = await getFamilyTree();
    if (res.members?.length) {
        treeData.value = buildTree(res.members);
        console.log('treeData:', treeData.value);
    }
});
</script>
