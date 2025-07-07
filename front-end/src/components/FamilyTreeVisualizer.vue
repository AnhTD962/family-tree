<!-- src/components/FamilyTreeVisualizer.vue -->
<template>
    <div class="tree">
        <div class="tree-node">
            <FamilyTreeNode :member="root" :showChildren="showChildren" @toggle="showChildren = !showChildren" />
        </div>
        <div v-if="showChildren && root.children?.length" class="tree-children">
            <FamilyTreeVisualizer v-for="child in root.children" :key="child.id" :root="child" />
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import FamilyTreeNode from './FamilyTreeNode.vue';

const props = defineProps({
    root: Object
});

const showChildren = ref(true);
</script>

<style scoped>
.tree {
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative;
}

.tree::before {
    content: '';
    position: absolute;
    top: -10px;
    height: 10px;
    border-left: 2px solid #ccc;
}

.tree-children {
    display: flex;
    justify-content: center;
    gap: 2rem;
    margin-top: 1rem;
    position: relative;
}

.tree-children::before {
    content: '';
    position: absolute;
    top: 0;
    width: 100%;
    height: 2px;
    border-top: 2px solid #ccc;
    left: 0;
}
</style>
