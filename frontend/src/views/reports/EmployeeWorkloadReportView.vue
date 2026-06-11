<script setup>
import { computed, ref, watch } from 'vue'
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'
import DataTable from '@/components/ui/DataTable.vue'
import FormField from '@/components/ui/FormField.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AlertMessage from '@/components/ui/AlertMessage.vue'
import { useAuthStore } from '@/stores/auth'
import { useHomeScope } from '@/composables/useHomeScope'
import { useEmployeeWorkloadReport } from '@/composables/useEmployeeWorkloadReport'

const auth = useAuthStore()
const { filterItems, scopedHomeSelectOptions } = useHomeScope()
const { rows, seniorHomes, positions, loading, error, reload } = useEmployeeWorkloadReport()

const homeFilterId = ref('')
const positionFilterId = ref('')
const medicalOnlyFilter = ref('')
const employmentStatusFilter = ref('')

const homeOptions = computed(() => [
  { value: '', label: 'Wszystkie domy' },
  ...scopedHomeSelectOptions(seniorHomes.value),
])

const positionOptions = computed(() => [
  { value: '', label: 'Wszystkie stanowiska' },
  ...positions.value.map((position) => ({ value: position.id, label: position.name })),
])

const medicalOptions = [
  { value: '', label: 'Wszyscy pracownicy' },
  { value: 'true', label: 'Tylko medyczni' },
  { value: 'false', label: 'Tylko niemedyczni' },
]

const employmentStatusOptions = [
  { value: '', label: 'Wszyscy' },
  { value: 'ACTIVE', label: 'Aktywni' },
  { value: 'FIRED', label: 'Zwolnieni' },
]

const columns = [
  { key: 'employeeName', label: 'Pracownik' },
  { key: 'seniorHome', label: 'Dom seniora' },
  { key: 'positionName', label: 'Stanowisko' },
  { key: 'medicalEmployee', label: 'Medyczny' },
  { key: 'seniorCount', label: 'Seniorzy' },
  { key: 'roomCount', label: 'Pomieszczenia' },
  { key: 'activityCount', label: 'Zajęcia' },
  { key: 'medicalActivityCount', label: 'Zaj. med.' },
  { key: 'totalAssignments', label: 'Razem' },
  { key: 'workloadScore', label: 'Wynik' },
  { key: 'warningMessage', label: 'Uwagi' },
  { key: 'employmentStatus', label: 'Status' },
]

const filteredRows = computed(() => filterItems(rows.value, { homeIdKey: 'seniorHomeId' }))

const summary = computed(() => {
  const visibleRows = filteredRows.value

  return {
    employees: visibleRows.length,
    totalAssignments: visibleRows.reduce((sum, row) => sum + Number(row.totalAssignments || 0), 0),
    overloaded: visibleRows.filter((row) => Number(row.workloadScore || 0) >= 10).length,
    withoutAssignments: visibleRows.filter((row) => Number(row.totalAssignments || 0) === 0).length,
    warnings: visibleRows.filter((row) => row.hasWarning).length,
  }
})

watch(
  () => [auth.isHomeScoped, auth.assignedSeniorHomeId],
  () => {
    if (auth.isHomeScoped) {
      homeFilterId.value = auth.assignedSeniorHomeId
    }
    applyFilters()
  },
  { immediate: true },
)

function applyFilters() {
  const filters = {}
  if (homeFilterId.value) filters.seniorHomeId = homeFilterId.value
  if (positionFilterId.value) filters.positionId = positionFilterId.value
  if (medicalOnlyFilter.value !== '') filters.medicalOnly = medicalOnlyFilter.value
  if (employmentStatusFilter.value) filters.employmentStatus = employmentStatusFilter.value
  reload(filters)
}

function employmentVariant(status) {
  if (status === 'ACTIVE') return 'success'
  if (status === 'FIRED') return 'neutral'
  return 'info'
}

function employmentLabel(status) {
  if (status === 'ACTIVE') return 'Aktywny'
  if (status === 'FIRED') return 'Zwolniony'
  return status ?? '—'
}
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Raport obciążenia pracowników"
      description="Zakres przydziałów — podopieczni, pomieszczenia i prowadzone zajęcia."
    />

    <AlertMessage v-if="error" variant="error">{{ error }}</AlertMessage>

    <AppCard title="Filtry">
      <form class="grid gap-4 lg:grid-cols-5" @submit.prevent="applyFilters">
        <FormField label="Dom seniora">
          <AppSelect
            v-model="homeFilterId"
            :options="homeOptions"
            :disabled="auth.isHomeScoped"
          />
        </FormField>
        <FormField label="Stanowisko">
          <AppSelect v-model="positionFilterId" :options="positionOptions" />
        </FormField>
        <FormField label="Typ pracownika">
          <AppSelect v-model="medicalOnlyFilter" :options="medicalOptions" />
        </FormField>
        <FormField label="Status zatrudnienia">
          <AppSelect v-model="employmentStatusFilter" :options="employmentStatusOptions" />
        </FormField>
        <div class="flex items-end">
          <AppButton type="submit" :disabled="loading">
            {{ loading ? 'Ładowanie…' : 'Zastosuj filtry' }}
          </AppButton>
        </div>
      </form>
    </AppCard>

    <div class="grid gap-4 md:grid-cols-5">
      <AppCard title="Pracownicy">
        <p class="text-3xl font-semibold text-slate-900">{{ summary.employees }}</p>
      </AppCard>
      <AppCard title="Przypisania razem">
        <p class="text-3xl font-semibold text-slate-900">{{ summary.totalAssignments }}</p>
      </AppCard>
      <AppCard title="Wysoki wynik">
        <p class="text-3xl font-semibold text-slate-900">{{ summary.overloaded }}</p>
      </AppCard>
      <AppCard title="Bez przypisań">
        <p class="text-3xl font-semibold text-slate-900">{{ summary.withoutAssignments }}</p>
      </AppCard>
      <AppCard title="Uwagi">
        <p class="text-3xl font-semibold text-slate-900">{{ summary.warnings }}</p>
      </AppCard>
    </div>

    <p v-if="loading" class="text-sm text-slate-500">Ładowanie…</p>
    <DataTable
      v-else
      :columns="columns"
      :rows="filteredRows"
      empty-message="Brak danych do raportu obciążenia pracowników."
    >
      <template #cell-medicalEmployee="{ row }">
        <AppBadge :variant="row.medicalEmployee ? 'info' : 'neutral'">
          {{ row.medicalEmployee ? 'Tak' : 'Nie' }}
        </AppBadge>
      </template>
      <template #cell-totalAssignments="{ row }">
        <AppBadge :variant="row.totalAssignments >= 5 ? 'warning' : 'neutral'">
          {{ row.totalAssignments }}
        </AppBadge>
      </template>
      <template #cell-workloadScore="{ row }">
        <AppBadge :variant="row.workloadScore >= 10 ? 'warning' : 'neutral'">
          {{ row.workloadScore }}
        </AppBadge>
      </template>
      <template #cell-warningMessage="{ row }">
        <AppBadge :variant="row.hasWarning ? 'warning' : 'success'">
          {{ row.warningMessage || 'OK' }}
        </AppBadge>
      </template>
      <template #cell-employmentStatus="{ row }">
        <AppBadge :variant="employmentVariant(row.employmentStatus)">
          {{ employmentLabel(row.employmentStatus) }}
        </AppBadge>
      </template>
    </DataTable>
  </div>
</template>
