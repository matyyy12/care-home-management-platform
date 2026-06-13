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
import { useSeniorActivityReport } from '@/composables/useSeniorActivityReport'

const auth = useAuthStore()
const { filterItems, scopedHomeSelectOptions } = useHomeScope()
const { rows, seniorHomes, loading, error, reload } = useSeniorActivityReport()

const homeFilterId = ref('')
const statusFilter = ref('')

const homeOptions = computed(() => [
  { value: '', label: 'Wszystkie domy' },
  ...scopedHomeSelectOptions(seniorHomes.value),
])

const statusOptions = [
  { value: '', label: 'Wszystkie statusy' },
  { value: 'ACTIVE', label: 'Aktywny' },
  { value: 'DECEASED', label: 'Zmarły' },
  { value: 'DISCHARGED', label: 'Wypisany' },
]

const columns = [
  { key: 'seniorName', label: 'Senior' },
  { key: 'seniorHome', label: 'Dom seniora' },
  { key: 'status', label: 'Status' },
  { key: 'roomNumber', label: 'Pokój' },
  { key: 'participationLabel', label: 'Udział w zajęciach' },
  { key: 'availableActivityCount', label: 'Zajęcia w domu' },
  { key: 'medicalActivityCount', label: 'Medyczne' },
  { key: 'recreationalActivityCount', label: 'Rekreacyjne' },
]

const filteredRows = computed(() => filterItems(rows.value, { homeIdKey: 'seniorHomeId' }))

const summary = computed(() => {
  const visibleRows = filteredRows.value
  const availableActivities = visibleRows.reduce(
    (sum, row) => sum + Number(row.availableActivityCount || 0),
    0,
  )

  return {
    seniors: visibleRows.length,
    withoutParticipationData: visibleRows.filter((row) => row.participationCount == null).length,
    availableActivities,
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
  if (statusFilter.value) filters.status = statusFilter.value
  reload(filters)
}

function statusVariant(status) {
  if (status === 'ACTIVE') return 'success'
  if (status === 'DECEASED') return 'danger'
  if (status === 'DISCHARGED') return 'neutral'
  return 'info'
}

function statusLabel(status) {
  if (status === 'ACTIVE') return 'Aktywny'
  if (status === 'DECEASED') return 'Zmarły'
  if (status === 'DISCHARGED') return 'Wypisany'
  return status ?? '—'
}
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Raport aktywności seniorów"
      description="Udział seniorów w zajęciach — wykrywanie osób bez aktywności."
    />

    <AlertMessage v-if="error" variant="error">{{ error }}</AlertMessage>
    <AlertMessage variant="info">
      Obecny backend nie ma jeszcze relacji senior–zajęcia, więc kolumna udziału pokazuje brak danych.
      Raport wykorzystuje dane seniorów oraz zajęcia dostępne w ich domu seniora.
    </AlertMessage>

    <AppCard title="Filtry">
      <form class="grid gap-4 sm:grid-cols-3" @submit.prevent="applyFilters">
        <FormField label="Dom seniora">
          <AppSelect
            v-model="homeFilterId"
            :options="homeOptions"
            :disabled="auth.isHomeScoped"
          />
        </FormField>
        <FormField label="Status">
          <AppSelect v-model="statusFilter" :options="statusOptions" />
        </FormField>
        <div class="flex items-end">
          <AppButton type="submit" :disabled="loading">
            {{ loading ? 'Ładowanie…' : 'Zastosuj filtry' }}
          </AppButton>
        </div>
      </form>
    </AppCard>

    <div class="grid gap-4 md:grid-cols-3">
      <AppCard title="Seniorzy w raporcie">
        <p class="text-3xl font-semibold text-slate-900">{{ summary.seniors }}</p>
      </AppCard>
      <AppCard title="Bez danych o udziale">
        <p class="text-3xl font-semibold text-slate-900">
          {{ summary.withoutParticipationData }}
        </p>
      </AppCard>
      <AppCard title="Dostępne zajęcia">
        <p class="text-3xl font-semibold text-slate-900">
          {{ summary.availableActivities }}
        </p>
      </AppCard>
    </div>

    <p v-if="loading" class="text-sm text-slate-500">Ładowanie…</p>
    <DataTable
      v-else
      :columns="columns"
      :rows="filteredRows"
      empty-message="Brak danych do raportu aktywności seniorów."
    >
      <template #cell-status="{ row }">
        <AppBadge :variant="statusVariant(row.status)">
          {{ statusLabel(row.status) }}
        </AppBadge>
      </template>
      <template #cell-participationLabel="{ row }">
        <AppBadge :variant="row.participationCount == null ? 'warning' : 'success'">
          {{ row.participationLabel }}
        </AppBadge>
      </template>
    </DataTable>
  </div>
</template>
