<script setup>
defineProps({
  variant: {
    type: String,
    default: 'neutral',
    validator: (v) => ['neutral', 'success', 'warning', 'danger', 'info'].includes(v),
  },
})

const variants = {
  neutral: 'bg-slate-100 text-slate-700',
  success: 'bg-emerald-100 text-emerald-800',
  warning: 'bg-amber-100 text-amber-800',
  danger: 'bg-red-100 text-red-800',
  info: 'bg-sky-100 text-sky-800',
}
</script>

<template>
  <span
    class="inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium"
    :class="variants[variant]"
  >
    <slot />
  </span>
</template>
