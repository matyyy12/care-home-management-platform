<script setup>
import { computed, ref, watch } from 'vue'
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'
import DataTable from '@/components/ui/DataTable.vue'
import FormField from '@/components/ui/FormField.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppInput from '@/components/ui/AppInput.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AlertMessage from '@/components/ui/AlertMessage.vue'
import { useHomeScope } from '@/composables/useHomeScope'
import { useAuthStore } from '@/stores/auth'
import { useRoomOccupancyReport } from '@/composables/useRoomOccupancyReport'
import { useRooms } from '@/composables/useRooms'
const auth = useAuthStore()
const { filterItems, scopedHomeSelectOptions } = useHomeScope()
const { rows, loading, error, reload } = useRoomOccupancyReport()
const { seniorHomes } = useRooms()

const floorFilter = ref('')
const standardFilter = ref('')
const homeFilterId = ref('')

const homeOptions = computed(() => [
  { value: '', label: 'Wszystkie domy' },
  ...scopedHomeSelectOptions(seniorHomes.value),
])

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

const columns = [
  { key: 'roomNumber', label: 'Nr pomieszczenia' },
  { key: 'floor', label: 'Piętro' },
  { key: 'seniorHome', label: 'Dom seniora' },
  { key: 'capacity', label: 'Miejsca' },
  { key: 'assigned', label: 'Przypisani' },
  { key: 'free', label: 'Wolne' },
  { key: 'standard', label: 'Standard' },
]

const filteredRows = computed(() => filterItems(rows.value, { homeIdKey: 'seniorHomeId' }))

function applyFilters() {
  const filters = {}
  if (homeFilterId.value) filters.seniorHomeId = homeFilterId.value
  if (floorFilter.value !== '') filters.floor = Number(floorFilter.value)
  if (standardFilter.value.trim()) filters.standard = standardFilter.value.trim()
  reload(filters)
}
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Raport obłożenia pokoi"
      description="Dostępność miejsc w pokojach mieszkalnych z filtrowaniem po domu, piętrze i standardzie."
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
        <FormField label="Piętro">
          <AppInput v-model="floorFilter" type="number" placeholder="np. 1" />
        </FormField>
        <FormField label="Standard">
          <AppInput v-model="standardFilter" placeholder="np. Podstawowy" />
        </FormField>
        <div class="sm:col-span-3">
          <AppButton type="submit" :disabled="loading">
            {{ loading ? 'Ładowanie…' : 'Zastosuj filtry' }}
          </AppButton>
        </div>
      </form>
    </AppCard>

    <DataTable :columns="columns" :rows="filteredRows" />
  </div>
</template>
