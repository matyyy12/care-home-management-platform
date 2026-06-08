<script setup>
import { computed } from 'vue'
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'
import EmptyState from '@/components/ui/EmptyState.vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()

const allReports = [
  {
    routeName: 'report-room-occupancy',
    to: '/raporty/oblozenie-pokoi',
    title: 'Obłożenie pokoi',
    description: 'Miejsca w pokojach mieszkalnych — zajęte i wolne.',
  },
  {
    routeName: 'report-senior-activity',
    to: '/raporty/aktywnosc-seniorow',
    title: 'Aktywność seniorów',
    description: 'Udział w zajęciach i seniorzy bez aktywności.',
  },
  {
    routeName: 'report-employee-workload',
    to: '/raporty/obciazenie-pracownikow',
    title: 'Obciążenie pracowników',
    description: 'Podopieczni, pomieszczenia i prowadzone zajęcia.',
  },
  {
    routeName: 'report-data-quality',
    to: '/raporty/jakosc-danych',
    title: 'Jakość danych',
    description: 'Braki i niespójności w ewidencji.',
  },
]

const reports = computed(() => allReports.filter((r) => auth.canRead(r.routeName)))
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Raporty"
      description="Raporty operacyjne i analityczne na podstawie danych z backendu."
    />

    <EmptyState
      v-if="reports.length === 0"
      title="Brak raportów dla Twojej roli"
      description="Zmień rolę w panelu bocznym lub skontaktuj się z administratorem."
    />

    <div v-else class="grid gap-4 sm:grid-cols-2">
      <RouterLink v-for="report in reports" :key="report.to" :to="report.to" class="block">
        <AppCard class="h-full transition-shadow hover:shadow-md">
          <h2 class="font-medium text-emerald-800">{{ report.title }}</h2>
          <p class="mt-2 text-sm text-slate-600">{{ report.description }}</p>
        </AppCard>
      </RouterLink>
    </div>
  </div>
</template>
