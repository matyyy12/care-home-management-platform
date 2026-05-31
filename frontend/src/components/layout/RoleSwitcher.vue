<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import AppSelect from '@/components/ui/AppSelect.vue'
import { ROLE_OPTIONS } from '@/constants/roles'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()

const options = ROLE_OPTIONS

const currentRole = computed({
  get: () => auth.role,
  set: (value) => {
    auth.setRole(value)
    if (router.currentRoute.value.name && !auth.canRead(router.currentRoute.value.name)) {
      router.push({ name: 'dashboard' })
    }
  },
})
</script>

<template>
  <div class="border-b border-slate-100 px-4 py-4">
    <label class="block text-xs font-semibold uppercase tracking-wide text-slate-400">
      Rola użytkownika (demo)
    </label>
    <AppSelect v-model="currentRole" :options="options" class="mt-2" placeholder="" />
    <p class="mt-2 text-xs text-slate-500">
      Zalogowano jako <span class="font-medium text-slate-700">{{ auth.roleLabel }}</span>
    </p>
  </div>
</template>
