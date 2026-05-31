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

const stats = computed(() => [
  { label: 'Domy seniora', value: seniorHomes.length },
  { label: 'Aktywni seniorzy', value: seniors.filter((s) => !s.dischargeDate).length },
  { label: 'Pracownicy', value: employees.length },
  { label: 'Pomieszczenia', value: rooms.length },
  { label: 'Zajęcia', value: activities.length },
  { label: 'Problemy w raporcie jakości', value: dataQualityIssues.length },
])
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Pulpit"
      description="Aplikacja do obsługi domu seniora — ewidencja, przypisania i raportowanie (dane demonstracyjne)."
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
        Role użytkowników (dyrektor, menadżer, pracownik, pracownik medyczny, administrator) zostaną
        dodane po wdrożeniu uwierzytelniania.
      </p>
    </AppCard>
  </div>
</template>
