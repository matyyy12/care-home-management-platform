<script setup>
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AlertMessage from '@/components/ui/AlertMessage.vue'
import { usePermissions } from '@/composables/usePermissions'
import { useAdmin } from '@/composables/useAdmin'

const { canWrite, roleLabel } = usePermissions('administration')
const {
  health,
  config,
  backupResult,
  loading,
  backupLoading,
  error,
  backupError,
  loadStatus,
  runBackup,
} = useAdmin()
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Administracja techniczna"
      description="Stan aplikacji, konfiguracja środowiska i kopie zapasowe."
    />

    <AlertMessage variant="info">
      Zalogowano jako <strong>{{ roleLabel }}</strong>.
      {{ canWrite ? 'Masz uprawnienia do uruchamiania kopii zapasowej.' : 'Podgląd tylko do odczytu.' }}
    </AlertMessage>

    <AlertMessage v-if="error" variant="error">{{ error }}</AlertMessage>
    <AlertMessage v-if="backupError" variant="error">{{ backupError }}</AlertMessage>
    <AlertMessage v-if="backupResult" variant="success">
      {{ backupResult.message }} ({{ backupResult.timestamp }})
    </AlertMessage>

    <div class="grid gap-4 sm:grid-cols-2">
      <AppCard title="Stan aplikacji">
        <p v-if="loading" class="text-sm text-slate-500">Ładowanie…</p>
        <dl v-else-if="health" class="space-y-2 text-sm text-slate-700">
          <div class="flex items-center justify-between gap-4">
            <dt>Status</dt>
            <dd>
              <AppBadge :variant="health.status === 'UP' ? 'success' : 'danger'">
                {{ health.status }}
              </AppBadge>
            </dd>
          </div>
          <div class="flex items-center justify-between gap-4">
            <dt>Baza danych</dt>
            <dd>
              <AppBadge :variant="health.databaseConnected ? 'success' : 'danger'">
                {{ health.databaseConnected ? 'Połączona' : 'Brak połączenia' }}
              </AppBadge>
            </dd>
          </div>
          <div class="flex items-center justify-between gap-4">
            <dt>Aplikacja</dt>
            <dd>{{ health.applicationName }}</dd>
          </div>
        </dl>
        <AppButton class="mt-4" variant="secondary" :disabled="loading" @click="loadStatus">
          Odśwież
        </AppButton>
      </AppCard>

      <AppCard title="Konfiguracja aplikacji">
        <p v-if="loading" class="text-sm text-slate-500">Ładowanie…</p>
        <dl v-else-if="config" class="space-y-2 text-sm text-slate-700">
          <div class="flex items-center justify-between gap-4">
            <dt>Wygasanie JWT (s)</dt>
            <dd>{{ config.jwtExpirationSeconds }}</dd>
          </div>
          <div class="flex items-center justify-between gap-4">
            <dt>Dane przykładowe</dt>
            <dd>
              <AppBadge :variant="config.sampleDataEnabled ? 'info' : 'neutral'">
                {{ config.sampleDataEnabled ? 'Włączone' : 'Wyłączone' }}
              </AppBadge>
            </dd>
          </div>
          <div>
            <dt class="mb-1">Dozwolone originy CORS</dt>
            <dd class="break-all text-slate-600">{{ config.corsAllowedOrigins }}</dd>
          </div>
        </dl>
      </AppCard>

      <AppCard title="Kopia zapasowa bazy" class="sm:col-span-2">
        <p class="text-sm text-slate-600">
          Ręczne uruchomienie kopii zapasowej (obecnie stub po stronie backendu).
        </p>
        <p v-if="!canWrite" class="mt-2 text-xs text-amber-700">
          Tylko podgląd — brak uprawnień do zapisu.
        </p>
        <AppButton
          class="mt-4"
          :disabled="!canWrite || backupLoading"
          @click="runBackup"
        >
          {{ backupLoading ? 'Uruchamianie…' : 'Uruchom kopię zapasową' }}
        </AppButton>
      </AppCard>
    </div>
  </div>
</template>
