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
import { useDataQualityReport } from '@/composables/useDataQualityReport'

const auth = useAuthStore()
const { filterItems, scopedHomeSelectOptions } = useHomeScope()
const { rows, seniorHomes, loading, error, reload } = useDataQualityReport()

const homeFilterId = ref('')
const severityFilter = ref('')

const homeOptions = computed(() => [
  { value: '', label: 'Wszystkie domy' },
  ...scopedHomeSelectOptions(seniorHomes.value),
])

const severityOptions = [
  { value: '', label: 'Wszystkie poziomy' },
  { value: 'CRITICAL', label: 'Krytyczne' },
  { value: 'WARNING', label: 'Ostrzeżenia' },
  { value: 'INFO', label: 'Informacyjne' },
]

const columns = [
  { key: 'severity', label: 'Poziom' },
  { key: 'module', label: 'Moduł' },
  { key: 'objectName', label: 'Obiekt' },
  { key: 'seniorHome', label: 'Dom seniora' },
  { key: 'description', label: 'Problem' },
  { key: 'suggestedAction', label: 'Sugerowana akcja' },
]

const filteredRows = computed(() => filterItems(rows.value, { homeIdKey: 'seniorHomeId' }))

const summary = computed(() => {
  const visibleRows = filteredRows.value
  return {
    total: visibleRows.length,
    critical: visibleRows.filter((row) => row.severity === 'CRITICAL').length,
    warning: visibleRows.filter((row) => row.severity === 'WARNING').length,
    info: visibleRows.filter((row) => row.severity === 'INFO').length,
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
  if (severityFilter.value) filters.severity = severityFilter.value
  reload(filters)
}

function severityVariant(severity) {
  if (severity === 'CRITICAL') return 'danger'
  if (severity === 'WARNING') return 'warning'
  return 'info'
}

function severityLabel(severity) {
  if (severity === 'CRITICAL') return 'Krytyczny'
  if (severity === 'WARNING') return 'Ostrzeżenie'
  if (severity === 'INFO') return 'Info'
  return severity ?? '—'
}
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Raport jakości danych"
      description="Wykryte braki: senior bez pokoju/opiekuna, duplikaty PESEL, niespójne przypisania i inne."
    />

    <AlertMessage v-if="error" variant="error">{{ error }}</AlertMessage>

    <AppCard title="Filtry">
      <form class="grid gap-4 sm:grid-cols-3" @submit.prevent="applyFilters">
        <FormField label="Dom seniora">
          <AppSelect
            v-model="homeFilterId"
            :options="homeOptions"
            :disabled="auth.isHomeScoped"
          />
        </FormField>
        <FormField label="Poziom">
          <AppSelect v-model="severityFilter" :options="severityOptions" />
        </FormField>
        <div class="flex items-end">
          <AppButton type="submit" :disabled="loading">
            {{ loading ? 'Ładowanie…' : 'Zastosuj filtry' }}
          </AppButton>
        </div>
      </form>
    </AppCard>

    <div class="grid gap-4 md:grid-cols-4">
      <AppCard title="Razem">
        <p class="text-3xl font-semibold text-slate-900">{{ summary.total }}</p>
      </AppCard>
      <AppCard title="Krytyczne">
        <p class="text-3xl font-semibold text-slate-900">{{ summary.critical }}</p>
      </AppCard>
      <AppCard title="Ostrzeżenia">
        <p class="text-3xl font-semibold text-slate-900">{{ summary.warning }}</p>
      </AppCard>
      <AppCard title="Informacyjne">
        <p class="text-3xl font-semibold text-slate-900">{{ summary.info }}</p>
      </AppCard>
    </div>

    <p v-if="loading" class="text-sm text-slate-500">Ładowanie…</p>
    <DataTable
      v-else
      :columns="columns"
      :rows="filteredRows"
      empty-message="Nie wykryto problemów jakości danych."
    >
      <template #cell-severity="{ row }">
        <AppBadge :variant="severityVariant(row.severity)">
          {{ severityLabel(row.severity) }}
        </AppBadge>
      </template>
      <template #cell-objectName="{ row }">
        <span class="font-medium text-slate-900">{{ row.objectName }}</span>
        <span class="block text-xs text-slate-500">{{ row.objectType }} #{{ row.objectId }}</span>
      </template>
    </DataTable>
  </div>
</template>
