<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Header -->
    <header class="bg-white shadow-sm border-b">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <h1 class="text-2xl font-bold text-gray-900">Family Tree</h1>
          <div class="flex items-center space-x-4">
            <button v-if="canEdit" @click="openAddModal"
              class="bg-indigo-600 text-white px-4 py-2 rounded-lg hover:bg-indigo-700 transition-colors">
              Add Member
            </button>
            <router-link v-else to="/login"
              class="bg-indigo-600 text-white px-4 py-2 rounded-lg hover:bg-indigo-700 transition-colors">
              Login
            </router-link>
          </div>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="bg-white rounded-lg shadow-lg overflow-hidden">
        <div class="bg-gray-50 px-6 py-4 border-b flex justify-between items-center">
          <div class="text-sm text-gray-500">{{ familyMembers.length }} members</div>
        </div>

        <!-- Tree Container -->
        <div v-if="familyMembers.length === 0" class="text-center py-12">
          <p class="text-gray-500">No family members found.</p>
          <button v-if="canEdit" @click="showAddMemberModal = true" class="btn-primary mt-4">
            Add First Member
          </button>
        </div>
        <div v-else class="relative overflow-auto" style="height: 70vh;">
          <div ref="treeContainer" class="w-full h-full"></div>
        </div>
      </div>
    </main>

    <!-- Add Member Modal -->
    <div v-if="showAddMemberModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 z-50 flex items-center justify-center"
      @click="showAddMemberModal = false">
      <div class="bg-white p-6 rounded-lg shadow-xl w-full max-w-xl" @click.stop>
        <FamilyMemberForm :member="selectedMember" @member-saved="handleMemberSaved"
          @cancel="showAddMemberModal = false" />
      </div>
    </div>

    <!-- Member Detail Modal -->
    <div v-if="selectedMember" class="fixed inset-0 bg-gray-600 bg-opacity-50 z-50 flex items-center justify-center"
      @click="selectedMember = null">
      <div class="bg-white p-6 rounded-lg shadow-xl w-full max-w-xl" @click.stop>
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-lg font-bold">{{ selectedMember.fullName }}</h2>
          <button @click="selectedMember = null" class="text-gray-500 hover:text-gray-700">&times;</button>
        </div>
        <img :src="`http://localhost:8080${selectedMember.avatarUrl}` || '/default-avatar.png'"
          class="w-32 h-32 object-cover rounded-lg mb-4 mx-auto" />
        <div class="text-sm space-y-1">
          <div><strong>Gender:</strong> {{ selectedMember.gender }}</div>
          <div v-if="selectedMember.birthDate"><strong>Birth:</strong> {{ formatDate(selectedMember.birthDate) }}</div>
          <div v-if="selectedMember.deathDate"><strong>Death:</strong> {{ formatDate(selectedMember.deathDate) }}</div>
          <div v-if="selectedMember.occupation"><strong>Occupation:</strong> {{ selectedMember.occupation }}</div>
        </div>
        <div class="mt-4 flex justify-end space-x-2">
          <router-link :to="`/member/${selectedMember.id}`"
            class="bg-indigo-600 text-white px-4 py-2 rounded hover:bg-indigo-700">View Details</router-link>
          <button @click="selectedMember = null"
            class="bg-gray-300 text-gray-800 px-4 py-2 rounded hover:bg-gray-400">Close</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import FamilyTree from '@balkangraph/familytree.js'
import { useAuthStore } from '@/stores/auth'
import { useFamilyStore } from '@/stores/family'
import FamilyMemberForm from '@/components/family/FamilyMemberForm.vue'

export default {
  name: 'FamilyTreeView',
  components: { FamilyMemberForm },
  setup() {
    const authStore = useAuthStore()
    const familyStore = useFamilyStore()

    const loading = ref(false)
    const zoomLevel = ref(1)
    const showAddMemberModal = ref(false)
    const selectedMember = ref(null)
    const treeContainer = ref(null)
    let tree = null

    const user = computed(() => authStore.user)
    const familyMembers = computed(() => familyStore.members)
    const canEdit = computed(() => authStore.isAuthenticated && user.value.role !== 'GUEST')

    const formatDate = date => new Date(date).toLocaleDateString()

    const selectMember = async member => {
      selectedMember.value = null
      await nextTick()
      selectedMember.value = member
    }

    const buildTreeNodes = (members) => {
      const map = new Map()
      members.forEach(m => {
        map.set(m.id, {
          id: m.id,
          fid: m.fatherId || undefined,
          mid: m.motherId || undefined,
          pids: m.spouseId ? [m.spouseId] : [],
          name: m.fullName,
          gender: m.gender === 'MALE' ? 'male' : 'female',
          img: `http://localhost:8080${m.avatarUrl}` || '/default-avatar.png'
        })
      })

      members.forEach(m => {
        if (m.spouseId && map.has(m.spouseId)) {
          const spouse = map.get(m.spouseId)
          if (!spouse.pids?.includes(m.id)) {
            spouse.pids.push(m.id)
          }
        }
      })
      return Array.from(map.values())
    }

    const renderTree = () => {
      const nodes = buildTreeNodes(familyMembers.value)

      // ❌ Nếu không có DOM hoặc không có dữ liệu thì không render
      if (!treeContainer.value || nodes.length === 0) return

      // ✅ Huỷ tree cũ một cách an toàn
      if (tree && typeof tree.destroy === 'function') {
        try {
          tree.destroy()
        } catch (err) {
          console.warn('Failed to destroy tree:', err)
        }
      }

      // ✅ Khởi tạo lại cây
      tree = new FamilyTree(treeContainer.value, {
        nodes,
        template: "hugo",
        nodeBinding: {
          field_0: 'name',
          img_0: 'img',
        },
        enableDragDrop: false,
        enableZoom: false,
        mouseScrool: FamilyTree.action.none,
        nodeMouseClick: FamilyTree.action.none,
        toolbar: {
          zoom: true,
          fit: true,
        },
        nodeMenu: {},
        miniMap: false
      })

      // ✅ Gắn click event
      tree.on('click', (sender, args) => {
        const member = familyMembers.value.find(m => m.id === args.node.id)
        if (member) selectMember(member)
      })
    }



    const handleMemberSaved = async (memberData) => {
      if (memberData.id) {
        await familyStore.updateMember(memberData.id, memberData)
      } else {
        await familyStore.createMember(memberData)
      }
      await loadFamilyTree()
      showAddMemberModal.value = false
    }

    const loadFamilyTree = async () => {
      loading.value = true
      await familyStore.loadFamilyTree()
      loading.value = false
      await nextTick()
      renderTree()
    }

    const openAddModal = () => {
      selectedMember.value = null
      showAddMemberModal.value = true
    }

    onMounted(async () => {
      await loadFamilyTree()
    })

    watch(familyMembers, () => renderTree())

    return {
      loading,
      zoomLevel,
      user,
      canEdit,
      familyMembers,
      showAddMemberModal,
      selectedMember,
      treeContainer,
      formatDate,
      selectMember,
      handleMemberSaved,
      openAddModal
    }
  }
}
</script>

<style scoped>
#tree {
  width: 100%;
  height: 100%;
}
</style>
