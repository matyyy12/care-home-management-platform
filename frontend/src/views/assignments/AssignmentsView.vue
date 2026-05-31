<script setup>
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'
import DataTable from '@/components/ui/DataTable.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AlertMessage from '@/components/ui/AlertMessage.vue'
import { assignments } from '@/mock/data'

const columns = [
  { key: 'label', label: 'Typ przypisania' },
  { key: 'from', label: 'Od' },
  { key: 'to', label: 'Do' },
  { key: 'seniorHome', label: 'Dom seniora' },
]

const rules = [
  'Przypisania muszą dotyczyć tego samego domu seniora.',
  'Senior tylko do pokoju mieszkalnego; zajęcia tylko do pomieszczenia w użyciu.',
  'Brak nowych przypisań dla zwolnionych pracowników i seniorów z datą śmierci.',
  'Zajęcia medyczne — wyłącznie pracownik medyczny.',
]
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Przypisania"
      description="Powiązania senior–pokój, pracownik–senior, pracownik–pomieszczenie, pracownik–zajęcia, senior–zajęcia, zajęcia–pomieszczenie."
    />

    <AlertMessage variant="info">
      <ul class="list-inside list-disc space-y-1">
        <li v-for="rule in rules" :key="rule">{{ rule }}</li>
      </ul>
    </AlertMessage>

    <AppCard title="Aktywne przypisania (demo)">
      <DataTable :columns="columns" :rows="assignments">
        <template #cell-label="{ row }">
          <AppBadge variant="neutral">{{ row.label }}</AppBadge>
        </template>
      </DataTable>
    </AppCard>
  </div>
</template>
