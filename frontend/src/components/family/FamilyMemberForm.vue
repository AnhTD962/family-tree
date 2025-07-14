<template>
  <div class="family-member-form h-[80vh] overflow-y-auto p-4">
    <form @submit.prevent="submitForm" class="space-y-6">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <!-- Basic Info -->
        <div class="space-y-4">
          <h3 class="text-lg font-medium text-gray-900">Basic Information</h3>
          <!-- Avatar Upload -->
          <div>
            <label class="label">Avatar</label>
            <input type="file" accept="image/*" @change="handleFileChange" class="input" />
          </div>
          <div v-if="props.member?.avatarUrl" class="mb-2">
            <img :src="`http://localhost:8080${props.member.avatarUrl}`" class="w-24 h-24 object-cover rounded-md" />
          </div>
          <div>
            <label class="label">Full Name *</label>
            <input v-model="form.fullName" type="text" required class="input" />
          </div>

          <div>
            <label class="label">Gender *</label>
            <select v-model="form.gender" class="input" required>
              <option value="">Select Gender</option>
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
            </select>
          </div>

          <div>
            <label class="label">Birth Date</label>
            <input v-model="form.birthDate" type="date" class="input" />
          </div>

          <div>
            <label class="label">Death Date</label>
            <input v-model="form.deathDate" type="date" class="input" />
          </div>

          <div>
            <label class="label">Birth Place</label>
            <input v-model="form.birthPlace" type="text" class="input" />
          </div>

          <div>
            <label class="label">Occupation</label>
            <input v-model="form.occupation" type="text" class="input" />
          </div>
        </div>

        <!-- Relationships -->
        <div class="space-y-4">
          <h3 class="text-lg font-medium text-gray-900">Family Relationships</h3>

          <!-- Father -->
          <div class="relative">
            <label class="label">Father</label>
            <input v-model="fatherName" @input="searchFather" class="input" placeholder="Search father by name" />
            <ul v-if="filteredFathers.length" class="dropdown">
              <li v-for="person in filteredFathers" :key="person.id" @click="selectFather(person)"
                class="dropdown-item">
                {{ person.fullName }}
              </li>
            </ul>
          </div>

          <!-- Mother -->
          <div class="relative">
            <label class="label">Mother</label>
            <input v-model="motherName" @input="searchMother" class="input" placeholder="Search mother by name" />
            <ul v-if="filteredMothers.length" class="dropdown">
              <li v-for="person in filteredMothers" :key="person.id" @click="selectMother(person)"
                class="dropdown-item">
                {{ person.fullName }}
              </li>
            </ul>
          </div>

          <!-- Spouse -->
          <div class="relative">
            <label class="label">Spouse</label>
            <input v-model="spouseName" @input="searchSpouse" class="input" placeholder="Search spouse by name" />
            <ul v-if="filteredSpouses.length" class="dropdown">
              <li v-for="person in filteredSpouses" :key="person.id" @click="selectSpouse(person)"
                class="dropdown-item">
                {{ person.fullName }}
              </li>
            </ul>
          </div>

          <div v-if="conflictError" class="text-sm text-red-600">
            Father and mother must be spouses of each other.
          </div>
        </div>
      </div>

      <!-- Description -->
      <div>
        <label class="label">Description</label>
        <QuillEditor v-model:content="form.description" content-type="html" theme="snow"
          class="w-full min-h-[200px] bg-white rounded-md" />
      </div>

      <!-- Buttons -->
      <div class="flex justify-end space-x-3">
        <button type="button" @click="$emit('cancel')" class="btn-outline">Cancel</button>
        <button type="submit" class="btn-primary">Submit</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useFamilyStore } from '@/stores/family'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'

const props = defineProps({
  member: Object
})
const file = ref(null)
const emit = defineEmits(['cancel', 'member-saved'])
const familyStore = useFamilyStore()

const form = ref({
  id: '',
  fullName: '', gender: '', birthDate: '', deathDate: '',
  birthPlace: '', occupation: '', description: '',
  fatherId: '', motherId: '', spouseId: '', childrenIds: []
})

const fatherName = ref('')
const motherName = ref('')
const spouseName = ref('')

const filteredFathers = ref([])
const filteredMothers = ref([])
const filteredSpouses = ref([])

const availableMembers = computed(() => familyStore.members)
const maleMembers = computed(() => availableMembers.value.filter(m => m.gender === 'MALE'))
const femaleMembers = computed(() => availableMembers.value.filter(m => m.gender === 'FEMALE'))

// Pre-fill if editing
watch(() => props.member, (val) => {
  if (val) {
    form.value = {
      id: val.id || '',
      fullName: val.fullName || '',
      gender: val.gender || '',
      birthDate: val.birthDate || '',
      deathDate: val.deathDate || '',
      birthPlace: val.birthPlace || '',
      occupation: val.occupation || '',
      description: val.description || '',
      fatherId: val.fatherId || '',
      motherId: val.motherId || '',
      spouseId: val.spouseId || '',
      childrenIds: val.childrenIds || []
    }

    const getName = id => availableMembers.value.find(m => m.id === id)?.fullName || ''
    fatherName.value = getName(val.fatherId)
    motherName.value = getName(val.motherId)
    spouseName.value = getName(val.spouseId)
  }
}, { immediate: true })

function searchFather() {
  const q = fatherName.value.toLowerCase()
  filteredFathers.value = maleMembers.value.filter(m => m.fullName.toLowerCase().includes(q))
}
function searchMother() {
  const q = motherName.value.toLowerCase()
  filteredMothers.value = femaleMembers.value.filter(m => m.fullName.toLowerCase().includes(q))
}
function searchSpouse() {
  const q = spouseName.value.toLowerCase()
  filteredSpouses.value = availableMembers.value
    .filter(m => m.fullName.toLowerCase().includes(q) && (!m.spouseId || m.spouseId === form.value.id))
}

function selectFather(person) {
  form.value.fatherId = person.id
  fatherName.value = person.fullName
  filteredFathers.value = []

  const mother = availableMembers.value.find(m => m.id === person.spouseId)
  if (mother) {
    form.value.motherId = mother.id
    motherName.value = mother.fullName
  }
}

function selectMother(person) {
  form.value.motherId = person.id
  motherName.value = person.fullName
  filteredMothers.value = []

  const father = availableMembers.value.find(m => m.id === person.spouseId)
  if (father) {
    form.value.fatherId = father.id
    fatherName.value = father.fullName
  }
}

function selectSpouse(person) {
  form.value.spouseId = person.id
  spouseName.value = person.fullName
  filteredSpouses.value = []
}

const conflictError = computed(() => {
  const father = availableMembers.value.find(m => m.id === form.value.fatherId)
  const mother = availableMembers.value.find(m => m.id === form.value.motherId)
  if (father && mother) {
    return (father.spouseId && father.spouseId !== mother.id) ||
      (mother.spouseId && mother.spouseId !== father.id)
  }
  return false
})
function handleFileChange(event) {
  file.value = event.target.files[0]
}

function submitForm() {
  if (conflictError.value) return

  const payload = {
    ...form.value,
    childrenIds: (form.value.childrenIds || []).filter(id => id != null && id !== ''),
    file: file.value
  }

  if (!payload.id || payload.id.trim() === '') {
    delete payload.id
  }

  emit('member-saved', payload)
}
</script>

<style scoped>
.input {
  @apply mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:ring-indigo-500 focus:border-indigo-500;
}

.label {
  @apply block text-sm font-medium text-gray-700;
}

.btn-outline {
  @apply px-4 py-2 border border-gray-300 rounded-md bg-white text-gray-700 hover:bg-gray-50;
}

.btn-primary {
  @apply px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700;
}

.dropdown {
  @apply absolute mt-1 bg-white border border-gray-300 rounded-md shadow max-h-40 overflow-auto z-50;
}

.dropdown-item {
  @apply px-4 py-2 hover:bg-gray-100 cursor-pointer;
}
</style>
