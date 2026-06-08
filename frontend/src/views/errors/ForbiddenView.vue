<script setup>
import { useRouter } from 'vue-router'
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppButton from '@/components/ui/AppButton.vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()
</script>

<template>
  <div class="space-y-6">
    <PageHeader
      title="Brak dostępu"
      :description="`Twoja rola (${auth.roleLabel}) nie ma uprawnień do tej strony.`"
    />
    <AppCard>
      <p class="text-sm text-slate-600">
        Wybierz inną pozycję z menu lub zaloguj się na konto z odpowiednimi uprawnieniami.
      </p>
      <AppButton class="mt-4" @click="router.push({ name: 'dashboard' })">Wróć na pulpit</AppButton>
    </AppCard>
  </div>
</template>
