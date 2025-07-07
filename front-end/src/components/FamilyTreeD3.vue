<template>
    <div ref="container" style="width: 100%; height: 600px; border: 1px solid black; overflow: auto;"></div>
</template>

<script setup>
import { watch, ref, nextTick } from 'vue';
import * as d3 from 'd3';

const props = defineProps({
    data: Object,
});

const container = ref();

const drawTree = (data) => {
    console.log('[drawTree] data:', data);
    if (!data) return;

    const containerEl = container.value;
    if (!containerEl) {
        console.error('Container not found!');
        return;
    }

    // Clear old tree
    containerEl.innerHTML = '';

    const width = containerEl.clientWidth || 800;
    const height = containerEl.clientHeight || 600;

    const svg = d3
        .select(containerEl)
        .append('svg')
        .attr('width', width)
        .attr('height', height);

    const g = svg.append('g').attr('transform', 'translate(40,40)');

    const root = d3.hierarchy(data);
    const treeLayout = d3.tree().size([width - 80, height - 80]);
    treeLayout(root);

    console.log('[drawTree] root:', root);

    // Draw links
    g.selectAll('path.link')
        .data(root.links())
        .enter()
        .append('path')
        .attr('class', 'link')
        .attr('fill', 'none')
        .attr('stroke', '#aaa')
        .attr('stroke-width', 2)
        .attr(
            'd',
            d3
                .linkVertical()
                .x((d) => d.x)
                .y((d) => d.y)
        );

    // Draw nodes
    const node = g
        .selectAll('g.node')
        .data(root.descendants())
        .enter()
        .append('g')
        .attr('class', 'node')
        .attr('transform', (d) => `translate(${d.x},${d.y})`);

    node
        .append('circle')
        .attr('r', 25)
        .attr('fill', (d) => (d.data.gender === 'MALE' ? '#60a5fa' : '#f472b6'));

    node
        .append('text')
        .attr('dy', 5)
        .attr('text-anchor', 'middle')
        .attr('fill', '#fff')
        .text((d) => d.data.name || d.data.fullName);
};

watch(
    () => props.data,
    async (newVal) => {
        console.log('[watch] new data received');
        if (newVal) {
            await nextTick();
            drawTree(newVal);
        }
    }
);
</script>
