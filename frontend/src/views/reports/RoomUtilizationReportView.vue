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
import { useRoomUtilizationReport } from '@/composables/useRoomUtilizationReport'
import { useRooms } from '@/composables/useRooms'
import { ROOM_TYPE_LABELS, ROOM_TYPES } from '@/constants/enums'

const auth = useAuthStore()
const { filterItems, scopedHomeSelectOptions } = useHomeScope()
const { rows, loading, error, reload } = useRoomUtilizationReport()
const { seniorHomes } = useRooms()

const homeFilterId = ref('')
const inUseFilter = ref('')
const typeFilter = ref('')

const homeOptions = computed(() => [
  { value: '', label: 'Wszystkie domy' },
  ...scopedHomeSelectOptions(seniorHomes.value),
])

const inUseOptions = [
  { value: '', label: 'Wszystkie' },
  { value: 'true', label: 'W użyciu' },
  { value: 'false', label: 'Nieaktywne' },
]

const typeOptions = [{ value: '', label: 'Wszystkie typy' }, ...ROOM_TYPES]

const columns = [
  { key: 'roomNumber', label: 'Nr pomieszczenia' },
  { key: 'floor', label: 'Piętro' },
  { key: 'seniorHome', label: 'Dom seniora' },
  {
    key: 'inUse',
    label: 'W użyciu',
    format: (row) => (row.inUse ? 'Tak' : 'Nie'),
  },
  {
    key: 'type',
    label: 'Typ',
    format: (row) => ROOM_TYPE_LABELS[row.type] ?? row.type ?? '—',
  },
  { key: 'seniorCount', label: 'Seniorzy' },
  { key: 'employeeCount', label: 'Pracownicy' },
  { key: 'activityCount', label: 'Zajęcia' },
  { key: 'warning', label: 'Ostrzeżenie' },
]

const filteredRows = computed(() => filterItems(rows.value, { homeIdKey: 'seniorHomeId' }))

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
  if (inUseFilter.value !== '') filters.inUse = inUseFilter.value === 'true'
  if (typeFilter.value) filters.type = typeFilter.value
  reload(filters)
}

function warningVariant(row) {
  return row.hasWarning ? 'warning' : 'success'
}
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Raport wykorzystania pomieszczeń"
      description="Liczba seniorów, pracowników i zajęć przypisanych do każdego pomieszczenia."
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
        <FormField label="Status użycia">
          <AppSelect v-model="inUseFilter" :options="inUseOptions" />
        </FormField>
        <FormField label="Typ pomieszczenia">
          <AppSelect v-model="typeFilter" :options="typeOptions" />
        </FormField>
        <div class="sm:col-span-3">
          <AppButton type="submit" :disabled="loading">
            {{ loading ? 'Ładowanie…' : 'Zastosuj filtry' }}
          </AppButton>
        </div>
      </form>
    </AppCard>

    <DataTable
      :columns="columns"
      :rows="filteredRows"
      empty-message="Brak danych do raportu wykorzystania pomieszczeń."
    >
      <template #cell-warning="{ row }">
        <AppBadge :variant="warningVariant(row)">
          {{ row.hasWarning ? row.warningMessage : 'OK' }}
        </AppBadge>
      </template>
    </DataTable>
  </div>
</template>
