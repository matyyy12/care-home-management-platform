<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { navigationGroups } from '@/constants/navigation'

const route = useRoute()

const groups = computed(() => navigationGroups)

function isActive(path) {
  if (path === '/') return route.path === '/'
  return route.path === path || route.path.startsWith(`${path}/`)
}
</script>

<template>
  <aside class="flex w-64 shrink-0 flex-col border-r border-slate-200 bg-white">
    <div class="border-b border-slate-100 px-5 py-5">
      <p class="text-xs font-semibold uppercase tracking-wide text-emerald-700">Senior House</p>
      <h1 class="mt-1 text-lg font-semibold text-slate-900">Panel zarządzania</h1>
    </div>

    <nav class="flex-1 overflow-y-auto px-3 py-4" aria-label="Główne menu">
      <div v-for="group in groups" :key="group.label" class="mb-6">
        <p class="mb-2 px-2 text-xs font-semibold uppercase tracking-wide text-slate-400">
          {{ group.label }}
        </p>
        <ul class="space-y-0.5">
          <li v-for="item in group.items" :key="item.to">
            <RouterLink
              :to="item.to"
              class="block rounded-lg px-3 py-2 text-sm font-medium transition-colors"
              :class="
                isActive(item.to)
                  ? 'bg-emerald-50 text-emerald-800'
                  : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'
              "
            >
              {{ item.label }}
            </RouterLink>
          </li>
        </ul>
      </div>
    </nav>

    <div class="border-t border-slate-100 px-4 py-4 text-xs text-slate-500">
      Dane demonstracyjne — API niepodłączone
    </div>
  </aside>
</template>
