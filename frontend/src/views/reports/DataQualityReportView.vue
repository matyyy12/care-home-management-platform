<script setup>
import PageHeader from '@/components/layout/PageHeader.vue'
import DataTable from '@/components/ui/DataTable.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import EmptyState from '@/components/ui/EmptyState.vue'
import { computed } from 'vue'
import { dataQualityIssues } from '@/mock/data'

const columns = [
  { key: 'category', label: 'Obszar' },
  { key: 'issue', label: 'Problem' },
  { key: 'entity', label: 'Obiekt' },
]

const issues = computed(() => dataQualityIssues)

function severityVariant(severity) {
  if (severity === 'error') return 'danger'
  if (severity === 'warning') return 'warning'
  return 'neutral'
}
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Raport jakości danych"
      description="Wykryte braki: senior bez pokoju/opiekuna, duplikaty PESEL, niespójne przypisania i inne."
    />

    <EmptyState
      v-if="issues.length === 0"
      title="Brak wykrytych problemów"
      description="Dane są kompletne i poprawne."
    />

    <DataTable v-else :columns="columns" :rows="issues">
      <template #cell-issue="{ row }">
        <AppBadge :variant="severityVariant(row.severity)">{{ row.issue }}</AppBadge>
      </template>
    </DataTable>
  </div>
</template>
