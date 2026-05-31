<script setup>
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AlertMessage from '@/components/ui/AlertMessage.vue'
import { usePermissions } from '@/composables/usePermissions'

const { canWrite, roleLabel } = usePermissions('administration')
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Administracja techniczna"
      description="Konfiguracja aplikacji, kopie zapasowe i utrzymanie — moduł dla administratora technicznego i dyrektora."
    />

    <AlertMessage variant="info">
      Zalogowano jako <strong>{{ roleLabel }}</strong>. Po podłączeniu uwierzytelniania ustawienia będą
      chronione po stronie serwera.
    </AlertMessage>

    <div class="grid gap-4 sm:grid-cols-2">
      <AppCard title="Kopia zapasowa bazy">
        <p class="text-sm text-slate-600">Harmonogram i ręczne uruchomienie kopii (do implementacji).</p>
        <p v-if="!canWrite" class="mt-2 text-xs text-amber-700">Tylko podgląd — brak uprawnień do zapisu.</p>
      </AppCard>
      <AppCard title="Konfiguracja aplikacji">
        <p class="text-sm text-slate-600">Parametry środowiska, integracje, logi systemowe.</p>
      </AppCard>
    </div>
  </div>
</template>
