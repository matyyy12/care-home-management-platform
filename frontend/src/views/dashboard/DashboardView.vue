<script setup>
import { computed } from 'vue'
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'
import {
  seniors,
  employees,
  rooms,
  activities,
  seniorHomes,
  dataQualityIssues,
} from '@/mock/data'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()

const allStats = [
  { label: 'Domy seniora', value: () => seniorHomes.length, routeName: 'senior-homes' },
  {
    label: 'Aktywni seniorzy',
    value: () => seniors.filter((s) => !s.dischargeDate).length,
    routeName: 'seniors',
  },
  { label: 'Pracownicy', value: () => employees.length, routeName: 'employees' },
  { label: 'Pomieszczenia', value: () => rooms.length, routeName: 'rooms' },
  { label: 'Zajęcia', value: () => activities.length, routeName: 'activities' },
  {
    label: 'Problemy w raporcie jakości',
    value: () => dataQualityIssues.length,
    routeName: 'report-data-quality',
  },
]

const stats = computed(() =>
  allStats.filter((s) => auth.canRead(s.routeName)).map((s) => ({ label: s.label, value: s.value() })),
)
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Pulpit"
      :description="`Zalogowano jako ${auth.roleLabel}. Menu i formularze są ograniczone do uprawnień tej roli.`"
    />

    <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
      <AppCard v-for="stat in stats" :key="stat.label" :padding="true">
        <p class="text-sm text-slate-500">{{ stat.label }}</p>
        <p class="mt-1 text-3xl font-semibold text-slate-900">{{ stat.value }}</p>
      </AppCard>
    </div>

    <AppCard title="Moduły systemu">
      <ul class="grid gap-2 text-sm text-slate-700 sm:grid-cols-2">
        <li>Domy seniora, właściciele i adresy</li>
        <li>Seniorzy, pracownicy i stanowiska</li>
        <li>Pomieszczenia (pokoje, gabinety, sale gier)</li>
        <li>Zajęcia, sprzęt i przypisania</li>
        <li>Raporty operacyjne i jakości danych</li>
      </ul>
      <p class="mt-4 text-sm text-slate-500">
        Rola możesz ją zmienić w panelu bocznym (tryb demo). Po wdrożeniu logowania rola będzie
        pobierana z konta użytkownika, a backend zweryfikuje te same uprawnienia.
      </p>
    </AppCard>
  </div>
</template>
