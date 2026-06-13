<script setup>
import { computed, ref, watch } from 'vue'
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'
import DataTable from '@/components/ui/DataTable.vue'
import FormField from '@/components/ui/FormField.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AlertMessage from '@/components/ui/AlertMessage.vue'
import { useAuthStore } from '@/stores/auth'
import { useHomeScope } from '@/composables/useHomeScope'
import { useMedicalActivityReport } from '@/composables/useMedicalActivityReport'

const auth = useAuthStore()
const { filterItems, scopedHomeSelectOptions } = useHomeScope()
const { rows, seniorHomes, loading, error, reload } = useMedicalActivityReport()

const homeFilterId = ref('')

const homeOptions = computed(() => [
  { value: '', label: 'Wszystkie domy' },
  ...scopedHomeSelectOptions(seniorHomes.value),
])

const issueLabels = {
  NO_MEDICAL_LEAD: 'Brak prowadzącego medycznego',
  NO_ROOM: 'Brak pomieszczenia',
  ROOM_NOT_IN_USE: 'Pomieszczenie nieaktywne',
  FIRED_EMPLOYEE: 'Zwolniony pracownik',
  CROSS_HOME: 'Niespójność domu seniora',
}

const columns = [
  { key: 'activityName', label: 'Zajęcia' },
  { key: 'seniorHome', label: 'Dom seniora' },
  { key: 'issueType', label: 'Typ problemu' },
  { key: 'description', label: 'Opis' },
]

const filteredRows = computed(() =>
  filterItems(rows.value, { homeIdKey: 'seniorHomeId' }).map((row) => ({
    ...row,
    issueType: issueLabels[row.issueType] ?? row.issueType,
  })),
)

watch(
  () => [auth.isHomeScoped, auth.assignedSeniorHomeId],
  () => {
    if (auth.isHomeScoped) {
      homeFilterId.value = auth.assignedSeniorHomeId
    }
    applyFilters()
  },
  { immediate: true },
)

function applyFilters() {
  const filters = {}
  if (homeFilterId.value) filters.seniorHomeId = homeFilterId.value
  reload(filters)
}
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Raport zajęć medycznych"
      description="Wykrywanie problemów w konfiguracji zajęć medycznych."
    />

    <AlertMessage v-if="error" variant="error">{{ error }}</AlertMessage>

    <AppCard title="Filtry">
      <form class="grid gap-4 sm:grid-cols-3" @submit.prevent="applyFilters">
        <FormField label="Dom seniora">
          <AppSelect
            v-model="homeFilterId"
            :options="homeOptions"
            :disabled="auth.isHomeScoped"
          />
        </FormField>
        <div class="flex items-end">
          <AppButton type="submit" :disabled="loading">
            {{ loading ? 'Ładowanie…' : 'Zastosuj filtry' }}
          </AppButton>
        </div>
      </form>
    </AppCard>

    <AlertMessage v-if="!loading && filteredRows.length === 0" variant="success">
      Brak wykrytych problemów w zajęciach medycznych dla wybranych filtrów.
    </AlertMessage>

    <DataTable
      v-else
      :columns="columns"
      :rows="filteredRows"
      empty-message="Brak danych do raportu zajęć medycznych."
    >
      <template #cell-issueType="{ row }">
        <AppBadge variant="warning">{{ row.issueType }}</AppBadge>
      </template>
    </DataTable>
  </div>
</template>
