<script setup>
import PageHeader from '@/components/layout/PageHeader.vue'
import EmptyState from '@/components/ui/EmptyState.vue'
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Sprzęt"
      description="Marki, modele i sprzęt gabinetowy — hierarchia marka → model → sprzęt."
    />

    <EmptyState
      title="Moduł niedostępny"
      description="Endpoint ewidencji sprzętu nie jest jeszcze zaimplementowany w backendzie."
    />
  </div>
</template>
