<script setup>
defineProps({
  forId: { type: String, default: undefined },
  required: { type: Boolean, default: false },
})
</script>

<template>
  <label :for="forId" class="block text-sm font-medium text-slate-700">
    <slot />
    <span v-if="required" class="text-red-500" aria-hidden="true"> *</span>
  </label>
</template>
