<script setup>
import { computed } from 'vue'
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AlertMessage from '@/components/ui/AlertMessage.vue'
import {
  seniors,
  employees,
  seniorHomes,
  dataQualityIssues,
  roomOccupancyReport,
} from '@/mock/data'
import { ROLES } from '@/constants/roles'
import {
  DASHBOARD_SECTION_LABELS,
  DEMO_FOOTER,
  dashboardDescription,
  dashboardTitle,
  statLinkLabel,
} from '@/constants/dashboardCopy'
import { useAuthStore } from '@/stores/auth'
import { useHomeScope } from '@/composables/useHomeScope'
import {
  computeRoomOccupancy,
  computeSeniorCounts,
  computeQualityCounts,
  formatQualitySubtitle,
  formatProblemsCount,
  computeSeniorsPerHome,
  computeActiveEmployees,
} from '@/utils/dashboardMetrics'

const auth = useAuthStore()
const { filterItems, isScoped, homeName } = useHomeScope()

const scopedSeniors = computed(() => filterItems(seniors))
const scopedEmployees = computed(() => filterItems(employees))
const scopedSeniorHomes = computed(() => {
  if (!isScoped.value) return seniorHomes
  return seniorHomes.filter((home) => home.name === homeName.value)
})
const scopedRoomOccupancyReport = computed(() => filterItems(roomOccupancyReport))
const scopedQualityIssues = computed(() => filterItems(dataQualityIssues))

const pageTitle = computed(() => dashboardTitle(auth.role, auth.roleLabel))
const pageDescription = computed(() => dashboardDescription(auth.role, auth.roleLabel))

const seniorCounts = computed(() => computeSeniorCounts(scopedSeniors.value))
const roomOccupancy = computed(() => computeRoomOccupancy(scopedRoomOccupancyReport.value))
const qualityCounts = computed(() => computeQualityCounts(scopedQualityIssues.value))
const seniorsPerHome = computed(() =>
  computeSeniorsPerHome(scopedSeniors.value, scopedSeniorHomes.value),
)

const homesScopeLabel = computed(() =>
  scopedSeniorHomes.value.length === 1
    ? 'jednym domem seniora'
    : `${scopedSeniorHomes.value.length} domami seniora`,
)

const freePlacesLabel = computed(() =>
  roomOccupancy.value.free === 1 ? 'wolne miejsce' : 'wolne miejsca',
)

const isDirector = computed(() => auth.role === ROLES.DIRECTOR)

function buildDashboardStats() {
  const items = []

  if (auth.canRead('report-room-occupancy')) {
    const occ = roomOccupancy.value
    items.push({
      label: 'Obłożenie pokoi',
      value: `${occ.percent}%`,
      subtitle: `${occ.assigned} z ${occ.capacity} miejsc zajętych`,
      routeName: 'report-room-occupancy',
      linkLabel: statLinkLabel('report-room-occupancy'),
      occupancyPercent: occ.percent,
      alert: occ.percent >= 90,
    })
    items.push({
      label: 'Wolne miejsca',
      value: occ.free,
      subtitle: 'w pokojach mieszkalnych',
      routeName: 'report-room-occupancy',
      linkLabel: statLinkLabel('report-room-occupancy'),
    })
  }

  if (auth.canRead('seniors')) {
    const { active, discharged } = seniorCounts.value
    items.push({
      label: 'Aktywni seniorzy',
      value: active,
      subtitle:
        discharged > 0
          ? `${discharged} wypisanych w ewidencji`
          : 'Wszyscy wpisani są aktywni',
      routeName: 'seniors',
      linkLabel: statLinkLabel('seniors'),
    })
  }

  if (auth.canRead('report-data-quality')) {
    const q = qualityCounts.value
    items.push({
      label: 'Jakość danych',
      value: q.total,
      subtitle: formatQualitySubtitle(q),
      routeName: 'report-data-quality',
      linkLabel: statLinkLabel('report-data-quality'),
      alert: q.total > 0,
    })
  }

  if (auth.canRead('senior-homes')) {
    items.push({
      label: 'Domy seniora',
      value: scopedSeniorHomes.value.length,
      subtitle: 'w ewidencji organizacyjnej',
      routeName: 'senior-homes',
      linkLabel: statLinkLabel('senior-homes'),
    })
  }

  if (auth.canRead('employees')) {
    const activeEmployees = computeActiveEmployees(scopedEmployees.value)
    items.push({
      label: 'Pracownicy',
      value: activeEmployees,
      subtitle:
        activeEmployees < scopedEmployees.value.length
          ? `${scopedEmployees.value.length - activeEmployees} zakończonych umów`
          : 'Aktywni w ewidencji',
      routeName: 'employees',
      linkLabel: statLinkLabel('employees'),
    })
  }

  return items
}

const stats = computed(() => buildDashboardStats())
</script>

<template>
  <div class="space-y-8">
    <PageHeader :title="pageTitle" :description="pageDescription" />

    <section>
      <h2 class="mb-4 text-sm font-semibold uppercase tracking-wide text-slate-500">
        {{ DASHBOARD_SECTION_LABELS.stats }}
      </h2>
      <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <RouterLink
          v-for="stat in stats"
          :key="stat.label"
          :to="{ name: stat.routeName }"
          class="block rounded-xl focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-emerald-600"
        >
          <AppCard
            :padding="true"
            class="h-full transition-shadow hover:shadow-md"
            :class="stat.alert ? 'border-amber-300' : ''"
          >
            <p class="text-sm text-slate-500">{{ stat.label }}</p>
            <p
              class="mt-1 text-3xl font-semibold"
              :class="stat.alert ? 'text-amber-800' : 'text-slate-900'"
            >
              {{ stat.value }}
            </p>
            <p v-if="stat.subtitle" class="mt-1 text-sm text-slate-500">{{ stat.subtitle }}</p>
            <div
              v-if="stat.occupancyPercent != null"
              class="mt-3 h-2 overflow-hidden rounded-full bg-slate-100"
              role="presentation"
            >
              <div
                class="h-full rounded-full transition-all"
                :class="stat.alert ? 'bg-amber-500' : 'bg-emerald-600'"
                :style="{ width: `${stat.occupancyPercent}%` }"
              />
            </div>
            <p class="mt-3 text-xs font-medium text-emerald-700">{{ stat.linkLabel }}</p>
          </AppCard>
        </RouterLink>
      </div>
    </section>

    <AppCard v-if="isDirector" :title="DASHBOARD_SECTION_LABELS.directorOverview">
      <p class="text-sm text-slate-700">
        Odpowiadasz za {{ homesScopeLabel }} —
        <span class="font-medium text-slate-900">{{ seniorCounts.active }}</span>
        aktywnych seniorów. Obłożenie pokoi:
        <span class="font-medium text-slate-900">{{ roomOccupancy.percent }}%</span>,
        {{ roomOccupancy.free }} {{ freePlacesLabel }}.
      </p>

      <div class="mt-4 overflow-x-auto">
        <table class="w-full min-w-[280px] text-left text-sm">
          <caption class="sr-only">
            Liczba aktywnych seniorów w poszczególnych domach seniora
          </caption>
          <thead>
            <tr class="border-b border-slate-200 text-slate-500">
              <th scope="col" class="pb-2 pr-4 font-medium">Dom seniora</th>
              <th scope="col" class="pb-2 font-medium text-right">Aktywni seniorzy</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="row in seniorsPerHome"
              :key="row.homeId"
              class="border-b border-slate-100 last:border-0"
            >
              <td class="py-2 pr-4 text-slate-800">{{ row.homeName }}</td>
              <td class="py-2 text-right font-medium text-slate-900">{{ row.active }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <AlertMessage v-if="qualityCounts.total > 0" variant="warning" class="mt-4">
        <p class="font-medium">
          Do rozwiązania: {{ formatProblemsCount(qualityCounts.total) }} ({{
            formatQualitySubtitle(qualityCounts)
          }})
        </p>
        <ul class="mt-2 list-inside list-disc space-y-1">
          <li v-for="issue in scopedQualityIssues" :key="issue.id">
            {{ issue.issue }} — {{ issue.entity }}
          </li>
        </ul>
        <RouterLink
          :to="{ name: 'report-data-quality' }"
          class="mt-3 inline-block font-medium text-amber-900 underline hover:no-underline"
        >
          Przejdź do raportu jakości danych
        </RouterLink>
      </AlertMessage>

      <p v-else class="mt-4 text-sm text-slate-500">
        Ewidencja jest spójna — raport jakości danych nie wykazał problemów.
      </p>
    </AppCard>

    <AppCard v-else-if="isScoped" title="Twój dom seniora">
      <p class="text-sm text-slate-700">
        Przeglądasz dane domu
        <span class="font-medium text-slate-900">{{ homeName }}</span>
        — <span class="font-medium text-slate-900">{{ seniorCounts.active }}</span>
        aktywnych seniorów, obłożenie pokoi
        <span class="font-medium text-slate-900">{{ roomOccupancy.percent }}%</span>.
      </p>
    </AppCard>

    <p class="text-xs text-slate-500">{{ DEMO_FOOTER }}</p>
  </div>
</template>
