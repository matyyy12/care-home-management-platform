<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import AppSelect from '@/components/ui/AppSelect.vue'
import { ROLE_OPTIONS } from '@/constants/roles'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()

const currentRole = computed({
  get: () => auth.role,
  set: (value) => {
    auth.setRole(value)
    if (router.currentRoute.value.name && !auth.canRead(router.currentRoute.value.name)) {
      router.push({ name: 'dashboard' })
    }
  },
})

const currentHome = computed({
  get: () => auth.assignedSeniorHomeId,
  set: (value) => auth.setAssignedSeniorHome(value),
})
</script>

<template>
  <div class="space-y-4 border-b border-slate-100 px-4 py-4">
    <div>
      <label class="block text-xs font-semibold uppercase tracking-wide text-slate-400">
        Rola użytkownika (demo)
      </label>
      <AppSelect v-model="currentRole" :options="ROLE_OPTIONS" class="mt-2" placeholder="" />
      <p class="mt-2 text-xs text-slate-500">
        Zalogowano jako <span class="font-medium text-slate-700">{{ auth.roleLabel }}</span>
      </p>
    </div>

    <div v-if="auth.isHomeScoped">
      <label class="block text-xs font-semibold uppercase tracking-wide text-slate-400">
        Twój dom seniora
      </label>
      <AppSelect
        v-model="currentHome"
        :options="auth.seniorHomeOptions"
        class="mt-2"
        placeholder=""
      />
      <p class="mt-2 text-xs text-slate-500">
        Widzisz dane tylko z:
        <span class="font-medium text-slate-700">{{ auth.assignedHomeName }}</span>
      </p>
    </div>
  </div>
</template>
