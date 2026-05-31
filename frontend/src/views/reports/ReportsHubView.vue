<script setup>
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'

const reports = [
  {
    to: '/raporty/oblozenie-pokoi',
    title: 'Obłożenie pokoi',
    description: 'Miejsca w pokojach mieszkalnych — zajęte i wolne.',
  },
  {
    to: '/raporty/aktywnosc-seniorow',
    title: 'Aktywność seniorów',
    description: 'Udział w zajęciach i seniorzy bez aktywności.',
  },
  {
    to: '/raporty/obciazenie-pracownikow',
    title: 'Obciążenie pracowników',
    description: 'Podopieczni, pomieszczenia i prowadzone zajęcia.',
  },
  {
    to: '/raporty/jakosc-danych',
    title: 'Jakość danych',
    description: 'Braki i niespójności w ewidencji.',
  },
]
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Raporty"
      description="Część raportowa systemu — dane demonstracyjne, bez połączenia z backendem."
    />

    <div class="grid gap-4 sm:grid-cols-2">
      <RouterLink v-for="report in reports" :key="report.to" :to="report.to" class="block">
        <AppCard class="h-full transition-shadow hover:shadow-md">
          <h2 class="font-medium text-emerald-800">{{ report.title }}</h2>
          <p class="mt-2 text-sm text-slate-600">{{ report.description }}</p>
        </AppCard>
      </RouterLink>
    </div>
  </div>
</template>
