<script setup>
import AppSidebar from './AppSidebar.vue'
import StaffHomeBanner from './StaffHomeBanner.vue'
</script>

<template>
  <div class="flex min-h-screen bg-slate-100">
    <AppSidebar />
    <main class="flex-1 overflow-auto">
      <div class="mx-auto max-w-6xl px-6 py-8">
        <StaffHomeBanner />
        <RouterView />
      </div>
    </main>
  </div>
</template>
