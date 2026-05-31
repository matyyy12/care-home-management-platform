<script setup>
import PageHeader from '@/components/layout/PageHeader.vue'
import DataTable from '@/components/ui/DataTable.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import { seniorActivityReport } from '@/mock/data'

const columns = [
  { key: 'senior', label: 'Senior' },
  { key: 'seniorHome', label: 'Dom seniora' },
  { key: 'status', label: 'Status' },
  { key: 'activityCount', label: 'Liczba zajęć' },
  { key: 'activityTypes', label: 'Rodzaje zajęć' },
  { key: 'guardians', label: 'Opiekunowie' },
]
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Raport aktywności seniorów"
      description="Udział seniorów w zajęciach — wykrywanie osób bez aktywności."
    />

    <DataTable :columns="columns" :rows="seniorActivityReport">
      <template #cell-activityCount="{ row }">
        <AppBadge :variant="row.activityCount === 0 ? 'warning' : 'success'">
          {{ row.activityCount }}
        </AppBadge>
      </template>
    </DataTable>
  </div>
</template>
