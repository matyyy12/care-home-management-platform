<script setup>
defineProps({
  title: { type: String, default: '' },
  padding: { type: Boolean, default: true },
})
</script>

<template>
  <section class="rounded-xl border border-slate-200 bg-white shadow-sm">
    <header v-if="title || $slots.header" class="border-b border-slate-100 px-6 py-4">
      <slot name="header">
        <h2 class="text-lg font-medium text-slate-900">{{ title }}</h2>
      </slot>
    </header>
    <div :class="padding ? 'p-6' : ''">
      <slot />
    </div>
  </section>
</template>
