<script setup>
import PageHeader from '@/components/layout/PageHeader.vue'
import DataTable from '@/components/ui/DataTable.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import { rooms } from '@/mock/data'
import { ROOM_TYPES } from '@/constants/enums'

const typeLabel = Object.fromEntries(ROOM_TYPES.map((t) => [t.value, t.label]))

const columns = [
  { key: 'number', label: 'Numer' },
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
    format: (row) => typeLabel[row.type] ?? row.type,
  },
  {
    key: 'details',
    label: 'Szczegóły',
    format: (row) => {
      if (row.type === 'RESIDENTIAL') {
        return `${row.assignedSeniors}/${row.capacity} miejsc, ${row.standard}`
      }
      if (row.type === 'OFFICE') return row.officeKind ?? '—'
      if (row.type === 'GAME_ROOM') {
        return `TV: ${row.hasTv ? 'tak' : 'nie'}, PC: ${row.computers}`
      }
      return '—'
    },
  },
]
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Pomieszczenia"
      description="Pokoje mieszkalne, gabinety i sale gier — z limitami miejsc i wyposażeniem."
    />

    <DataTable :columns="columns" :rows="rooms">
      <template #cell-type="{ row }">
        <AppBadge variant="info">{{ typeLabel[row.type] }}</AppBadge>
      </template>
    </DataTable>
  </div>
</template>
