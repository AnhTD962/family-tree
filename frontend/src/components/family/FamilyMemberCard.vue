<template>
  <div
    class="bg-white border border-gray-200 rounded-lg shadow p-4 transition-all duration-300 min-w-[250px] relative hover:shadow-md"
    :class="{ 'p-2 min-w-[200px]': compact }">
    <div class="flex justify-center mb-3">
      <img v-if="member.avatarUrl" :src="`http://localhost:8080${member.avatarUrl}`" :alt="member.fullName"
        class="rounded-full object-cover border border-gray-300" :class="compact ? 'w-10 h-10' : 'w-16 h-16'" />
      <div v-else class="flex items-center justify-center bg-gray-100 rounded-full text-gray-500 border"
        :class="compact ? 'w-10 h-10 text-sm' : 'w-16 h-16 text-lg'">
        {{ member.fullName?.[0] || '?' }}
      </div>
    </div>

    <div class="text-center">
      <h3 class="text-gray-800 font-semibold mb-2" :class="compact ? 'text-sm mb-1' : 'text-base'">
        {{ member.fullName }}
      </h3>

      <div v-if="!compact" class="space-y-1 text-sm text-gray-600 mb-2">
        <div v-if="member.birthDate" class="flex items-center justify-center gap-2">
          <i class="fas fa-birthday-cake"></i>
          <span>{{ formatDate(member.birthDate) }}</span>
        </div>

        <div v-if="member.deathDate" class="flex items-center justify-center gap-2">
          <i class="fas fa-cross"></i>
          <span>{{ formatDate(member.deathDate) }}</span>
        </div>

        <div v-if="member.occupation" class="flex items-center justify-center gap-2">
          <i class="fas fa-briefcase"></i>
          <span>{{ member.occupation }}</span>
        </div>

        <div v-if="member.birthPlace" class="flex items-center justify-center gap-2">
          <i class="fas fa-map-marker-alt"></i>
          <span>{{ member.birthPlace }}</span>
        </div>
      </div>

      <div v-if="compact" class="flex justify-between text-xs text-gray-500 items-center">
        <div v-if="member.birthDate">{{ calculateAge(member.birthDate, member.deathDate) }}</div>
        <div>
          <i class="fas" :class="genderIcon"></i>
        </div>
      </div>
    </div>

    <div v-if="!compact && showActions" class="flex justify-center gap-2 mt-3">
      <button @click="editMember" v-if="canEdit"
        class="text-blue-600 border border-blue-600 hover:bg-blue-600 hover:text-white px-2 py-1 rounded text-xs">
        <i class="fas fa-edit"></i>
      </button>

      <button @click="viewDetails"
        class="text-cyan-600 border border-cyan-600 hover:bg-cyan-600 hover:text-white px-2 py-1 rounded text-xs">
        <i class="fas fa-eye"></i>
      </button>

      <button @click="deleteMember" v-if="canDelete"
        class="text-red-600 border border-red-600 hover:bg-red-600 hover:text-white px-2 py-1 rounded text-xs">
        <i class="fas fa-trash"></i>
      </button>
    </div>

    <div v-if="!compact" class="absolute top-2 right-2">
      <span class="text-[10px] font-bold uppercase px-2 py-0.5 rounded"
        :class="member.deathDate ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'">
        {{ member.deathDate ? 'Deceased' : 'Living' }}
      </span>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/auth'

export default {
  name: 'FamilyMemberCard',
  props: {
    member: {
      type: Object,
      required: true
    },
    compact: {
      type: Boolean,
      default: false
    },
    showActions: {
      type: Boolean,
      default: true
    }
  },
  computed: {
    auth() {
      return useAuthStore()
    },
    canEdit() {
      return this.auth.hasRole('USER') || this.auth.hasRole('ADMIN')
    },
    canDelete() {
      return this.auth.hasRole('ADMIN')
    }
  },
  methods: {
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    },
    calculateAge(birthDate, deathDate) {
      if (!birthDate) return '';
      const birth = new Date(birthDate);
      const end = deathDate ? new Date(deathDate) : new Date();
      let age = end.getFullYear() - birth.getFullYear();
      const m = end.getMonth() - birth.getMonth();
      if (m < 0 || (m === 0 && end.getDate() < birth.getDate())) age--;

      return deathDate ? `died at ${age}` : `${age} years old`;
    }
    ,
    editMember() {
      this.$emit('edit', this.member);
    },
    viewDetails() {
      this.$emit('view-details', this.member);
    },
    deleteMember() {
      if (confirm(`Are you sure you want to delete ${this.member.fullName}? This action cannot be undone.`)) {
        this.$emit('delete', this.member);
      }
    }
  }
};
</script>
