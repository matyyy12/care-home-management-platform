<script setup>
import PageHeader from '@/components/layout/PageHeader.vue'
import EmptyState from '@/components/ui/EmptyState.vue'
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Raport jakości danych"
      description="Wykryte braki: senior bez pokoju/opiekuna, duplikaty PESEL, niespójne przypisania i inne."
    />

    <EmptyState
      title="Raport niedostępny"
      description="Endpoint raportu jakości danych nie jest jeszcze zaimplementowany w backendzie."
    />
  </div>
</template>
