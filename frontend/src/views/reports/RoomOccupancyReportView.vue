<script setup>
import { ref } from 'vue'
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'
import DataTable from '@/components/ui/DataTable.vue'
import FormField from '@/components/ui/FormField.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppInput from '@/components/ui/AppInput.vue'
import AppButton from '@/components/ui/AppButton.vue'
import { roomOccupancyReport, seniorHomes } from '@/mock/data'

const floorFilter = ref('')
const standardFilter = ref('')
const homeFilter = ref('')

const homeOptions = [
  { value: '', label: 'Wszystkie domy' },
  ...seniorHomes.map((h) => ({ value: h.name, label: h.name })),
]

const columns = [
  { key: 'roomNumber', label: 'Nr pomieszczenia' },
  { key: 'floor', label: 'Piętro' },
  { key: 'seniorHome', label: 'Dom seniora' },
  { key: 'capacity', label: 'Miejsca' },
  { key: 'assigned', label: 'Przypisani' },
  { key: 'free', label: 'Wolne' },
  { key: 'standard', label: 'Standard' },
]

const filteredRows = ref(roomOccupancyReport)

function applyFilters() {
  filteredRows.value = roomOccupancyReport.filter((row) => {
    if (homeFilter.value && row.seniorHome !== homeFilter.value) return false
    if (floorFilter.value && String(row.floor) !== floorFilter.value) return false
    if (standardFilter.value && row.standard !== standardFilter.value) return false
    return true
  })
}
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Raport obłożenia pokoi"
      description="Dostępność miejsc w pokojach mieszkalnych z filtrowaniem po domu, piętrze i standardzie."
    />

    <AppCard title="Filtry">
      <form class="grid gap-4 sm:grid-cols-3" @submit.prevent="applyFilters">
        <FormField label="Dom seniora">
          <AppSelect v-model="homeFilter" :options="homeOptions" placeholder="" />
        </FormField>
        <FormField label="Piętro">
          <AppInput v-model="floorFilter" type="number" placeholder="np. 1" />
        </FormField>
        <FormField label="Standard">
          <AppInput v-model="standardFilter" placeholder="np. Podstawowy" />
        </FormField>
        <div class="sm:col-span-3">
          <AppButton type="submit">Zastosuj filtry</AppButton>
        </div>
      </form>
    </AppCard>

    <DataTable :columns="columns" :rows="filteredRows" />
  </div>
</template>
