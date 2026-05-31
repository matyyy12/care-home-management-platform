<script setup>
defineProps({
  title: { type: String, default: 'Brak danych' },
  description: { type: String, default: '' },
})
</script>

<template>
  <div class="rounded-xl border border-dashed border-slate-300 bg-slate-50/50 px-6 py-12 text-center">
    <p class="font-medium text-slate-800">{{ title }}</p>
    <p v-if="description" class="mt-1 text-sm text-slate-600">{{ description }}</p>
    <div v-if="$slots.default" class="mt-4">
      <slot />
    </div>
  </div>
</template>
