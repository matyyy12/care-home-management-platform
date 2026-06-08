<script setup>
import PageHeader from '@/components/layout/PageHeader.vue'
import EmptyState from '@/components/ui/EmptyState.vue'
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Raport obciążenia pracowników"
      description="Zakres przydziałów — podopieczni, pomieszczenia i prowadzone zajęcia."
    />

    <EmptyState
      title="Raport niedostępny"
      description="Endpoint raportu obciążenia pracowników nie jest jeszcze zaimplementowany w backendzie."
    />
  </div>
</template>
