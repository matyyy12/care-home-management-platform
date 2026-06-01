<script setup>
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()

const message = computed(() => {
  if (!auth.isHomeScoped || !auth.assignedHomeName) return ''
  return `Przeglądasz dane domu seniora: ${auth.assignedHomeName}.`
})
</script>

<template>
  <div
    v-if="message"
    class="mb-6 rounded-lg border border-sky-200 bg-sky-50 px-4 py-3 text-sm text-sky-900"
  >
    {{ message }}
  </div>
</template>
