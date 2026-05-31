<script setup>
const model = defineModel({ type: [String, Number], default: '' })

defineProps({
  id: { type: String, default: undefined },
  type: { type: String, default: 'text' },
  placeholder: { type: String, default: '' },
  disabled: { type: Boolean, default: false },
  required: { type: Boolean, default: false },
  invalid: { type: Boolean, default: false },
})
</script>

<template>
  <input
    :id="id"
    v-model="model"
    :type="type"
    :placeholder="placeholder"
    :disabled="disabled"
    :required="required"
    class="w-full rounded-lg border px-3 py-2 text-slate-900 shadow-sm transition-colors placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-offset-0 disabled:bg-slate-50 disabled:text-slate-500"
    :class="
      invalid
        ? 'border-red-300 focus:border-red-500 focus:ring-red-200'
        : 'border-slate-300 focus:border-emerald-500 focus:ring-emerald-200'
    "
  />
</template>
