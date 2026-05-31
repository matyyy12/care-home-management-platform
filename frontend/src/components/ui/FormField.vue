<script setup>
import AppLabel from './AppLabel.vue'

defineProps({
  label: { type: String, default: '' },
  forId: { type: String, default: undefined },
  required: { type: Boolean, default: false },
  hint: { type: String, default: '' },
  error: { type: String, default: '' },
})
</script>

<template>
  <div class="space-y-1">
    <AppLabel v-if="label" :for-id="forId" :required="required">{{ label }}</AppLabel>
    <slot />
    <p v-if="error" class="text-sm text-red-600" role="alert">{{ error }}</p>
    <p v-else-if="hint" class="text-sm text-slate-500">{{ hint }}</p>
  </div>
</template>
