<script setup>
const model = defineModel({ type: [String, Number], default: '' })

defineProps({
  id: { type: String, default: undefined },
  options: { type: Array, default: () => [] },
  placeholder: { type: String, default: 'Wybierz…' },
  disabled: { type: Boolean, default: false },
  required: { type: Boolean, default: false },
  invalid: { type: Boolean, default: false },
})
</script>

<template>
  <select
    :id="id"
    v-model="model"
    :disabled="disabled"
    :required="required"
    class="w-full rounded-lg border px-3 py-2 text-slate-900 shadow-sm transition-colors focus:outline-none focus:ring-2 focus:ring-offset-0 disabled:bg-slate-50"
    :class="
      invalid
        ? 'border-red-300 focus:border-red-500 focus:ring-red-200'
        : 'border-slate-300 focus:border-emerald-500 focus:ring-emerald-200'
    "
  >
    <option v-if="placeholder" value="" disabled>{{ placeholder }}</option>
    <option v-for="opt in options" :key="opt.value" :value="opt.value">
      {{ opt.label }}
    </option>
  </select>
</template>
