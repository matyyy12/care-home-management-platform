<script setup>
import { computed, ref, watch } from 'vue'
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'
import DataTable from '@/components/ui/DataTable.vue'
import FormField from '@/components/ui/FormField.vue'
import AppInput from '@/components/ui/AppInput.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AlertMessage from '@/components/ui/AlertMessage.vue'
import ReadOnlyNotice from '@/components/ui/ReadOnlyNotice.vue'
import { usePermissions } from '@/composables/usePermissions'
import { useSeniors } from '@/composables/useSeniors'
import { useAuthStore } from '@/stores/auth'
import { sourceNotice } from '@/composables/apiMode'
import { GENDER_OPTIONS, SENIOR_STATUS_LABELS } from '@/constants/enums'

const { canWrite, role } = usePermissions('seniors')
const auth = useAuthStore()
const { seniorList, homeOptions, loading, saving, error, usingMock, addSenior } = useSeniors()

const dataSourceNotice = computed(() => sourceNotice(usingMock.value))

const columns = [
  { key: 'lastName', label: 'Nazwisko' },
  { key: 'firstName', label: 'Imię' },
  { key: 'pesel', label: 'PESEL' },
  { key: 'seniorHome', label: 'Dom seniora' },
  { key: 'roomNumber', label: 'Pokój' },
  { key: 'guardians', label: 'Opiekunowie' },
]

const form = ref({
  firstName: '',
  lastName: '',
  pesel: '',
  birthDate: '',
  gender: '',
  seniorHomeId: '',
  accommodationDate: '',
  diseases: '',
})

watch(
  () => [auth.isHomeScoped, auth.assignedSeniorHomeId],
  () => {
    if (auth.isHomeScoped) {
      form.value.seniorHomeId = auth.assignedSeniorHomeId
    }
  },
  { immediate: true },
)

const formNotice = ref('')

async function handleSubmit() {
  try {
    const result = await addSenior(form.value)
    formNotice.value = result.savedLocally
      ? 'Senior zapisany lokalnie (tryb demo). Przypisanie pokoju i opiekuna zostanie obsłużone po pełnej integracji API.'
      : 'Senior zapisany w bazie danych.'
    form.value = {
      firstName: '',
      lastName: '',
      pesel: '',
      birthDate: '',
      gender: '',
      seniorHomeId: auth.isHomeScoped ? auth.assignedSeniorHomeId : '',
      accommodationDate: '',
      diseases: '',
    }
  } catch {
    formNotice.value = ''
  }
}

function statusBadge(row) {
  return row.dischargeDate ? 'neutral' : 'success'
}
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Seniorzy"
      description="Dane osobowe, PESEL, choroby, zakwaterowanie oraz przypisania do pokoju i opiekunów."
    />

    <AlertMessage v-if="dataSourceNotice" variant="info">{{ dataSourceNotice }}</AlertMessage>
    <AlertMessage v-if="error" variant="error">{{ error }}</AlertMessage>
    <AlertMessage v-if="formNotice" variant="success">{{ formNotice }}</AlertMessage>

    <ReadOnlyNotice v-if="!canWrite">
      <template v-if="role === 'MEDICAL'">
        Jako pracownik medyczny przeglądasz dane seniorów (w tym choroby). Dodawanie i edycja
        ewidencji należą do menadżera lub dyrektora.
      </template>
    </ReadOnlyNotice>

    <AppCard v-if="canWrite" title="Dodaj seniora">
      <form class="grid gap-4 sm:grid-cols-2" @submit.prevent="handleSubmit">
        <FormField label="Imię" required>
          <AppInput v-model="form.firstName" required />
        </FormField>
        <FormField label="Nazwisko" required>
          <AppInput v-model="form.lastName" required />
        </FormField>
        <FormField label="PESEL" required hint="Unikalny w systemie">
          <AppInput v-model="form.pesel" required maxlength="11" />
        </FormField>
        <FormField label="Data urodzenia" required>
          <AppInput v-model="form.birthDate" type="date" required />
        </FormField>
        <FormField label="Płeć" required>
          <AppSelect v-model="form.gender" :options="GENDER_OPTIONS" required />
        </FormField>
        <FormField label="Data zakwaterowania" required>
          <AppInput v-model="form.accommodationDate" type="date" required />
        </FormField>
        <FormField label="Dom seniora" required>
          <AppSelect
            v-model="form.seniorHomeId"
            :options="homeOptions"
            :disabled="auth.isHomeScoped"
            required
          />
        </FormField>
        <FormField label="Choroby" class="sm:col-span-2">
          <AppInput v-model="form.diseases" placeholder="np. cukrzyca, nadciśnienie" />
        </FormField>
        <div class="sm:col-span-2">
          <AppButton type="submit" :disabled="saving">
            {{ saving ? 'Zapisywanie…' : usingMock ? 'Zapisz (lokalnie)' : 'Zapisz' }}
          </AppButton>
        </div>
      </form>
    </AppCard>

    <section class="space-y-3">
      <h2 class="text-lg font-medium text-slate-900">Lista seniorów</h2>
      <p v-if="loading" class="text-sm text-slate-500">Ładowanie…</p>
      <DataTable v-else :columns="columns" :rows="seniorList">
        <template #cell-lastName="{ row }">
          <span class="font-medium">{{ row.lastName }}</span>
        </template>
        <template #cell-firstName="{ row }">
          {{ row.firstName }}
          <AppBadge :variant="statusBadge(row)" class="ml-2">
            {{ SENIOR_STATUS_LABELS[row.status] ?? row.status }}
          </AppBadge>
        </template>
      </DataTable>
    </section>
  </div>
</template>
