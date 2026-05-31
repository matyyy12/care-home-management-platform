<script setup>
import PageHeader from '@/components/layout/PageHeader.vue'
import DataTable from '@/components/ui/DataTable.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import { employeeWorkloadReport } from '@/mock/data'

const columns = [
  { key: 'employee', label: 'Pracownik' },
  { key: 'position', label: 'Stanowisko' },
  { key: 'seniorHome', label: 'Dom seniora' },
  {
    key: 'isMedical',
    label: 'Medyczny',
    format: (row) => (row.isMedical ? 'Tak' : 'Nie'),
  },
  { key: 'wards', label: 'Podopieczni' },
  { key: 'rooms', label: 'Pomieszczenia' },
  { key: 'activities', label: 'Zajęcia' },
  { key: 'status', label: 'Status' },
]
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Raport obciążenia pracowników"
      description="Zakres przydziałów — podopieczni, pomieszczenia i prowadzone zajęcia."
    />

    <DataTable :columns="columns" :rows="employeeWorkloadReport">
      <template #cell-status="{ row }">
        <AppBadge :variant="row.status === 'Aktywny' ? 'success' : 'neutral'">
          {{ row.status }}
        </AppBadge>
      </template>
    </DataTable>
  </div>
</template>
