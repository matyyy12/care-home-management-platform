<script setup>
import PageHeader from '@/components/layout/PageHeader.vue'
import EmptyState from '@/components/ui/EmptyState.vue'
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Raport aktywności seniorów"
      description="Udział seniorów w zajęciach — wykrywanie osób bez aktywności."
    />

    <EmptyState
      title="Raport niedostępny"
      description="Endpoint raportu aktywności seniorów nie jest jeszcze zaimplementowany w backendzie."
    />
  </div>
</template>
