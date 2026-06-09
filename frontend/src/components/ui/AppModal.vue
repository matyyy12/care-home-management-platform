<script setup>
defineProps({
  open: { type: Boolean, default: false },
  title: { type: String, default: '' },
})

defineEmits(['close'])
</script>

<template>
  <Teleport to="body">
    <div
      v-if="open"
      class="fixed inset-0 z-50 flex items-center justify-center p-4"
      role="dialog"
      aria-modal="true"
      :aria-label="title || 'Okno dialogowe'"
    >
      <div class="absolute inset-0 bg-slate-900/50" aria-hidden="true" @click="$emit('close')" />
      <div
        class="relative z-10 flex max-h-[calc(100vh-2rem)] w-full max-w-4xl flex-col rounded-xl border border-slate-200 bg-white shadow-xl"
        @click.stop
      >
        <div class="flex items-start justify-between gap-4 border-b border-slate-100 px-6 py-4">
          <h2 v-if="title" class="text-lg font-semibold text-slate-900">{{ title }}</h2>
          <button
            type="button"
            class="rounded-lg p-1 text-slate-400 hover:bg-slate-100 hover:text-slate-600"
            aria-label="Zamknij"
            @click="$emit('close')"
          >
            ✕
          </button>
        </div>
        <div class="min-h-0 overflow-y-auto px-6 py-5">
          <slot />
        </div>
      </div>
    </div>
  </Teleport>
</template>
