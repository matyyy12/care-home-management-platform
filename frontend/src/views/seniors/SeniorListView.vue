<script setup>
import { computed, ref } from 'vue'
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
import { useLocalCollection } from '@/composables/useLocalCollection'

const { canWrite, role } = usePermissions('seniors')
import { seniors as initialSeniors, seniorHomes } from '@/mock/data'
import { GENDER_OPTIONS, SENIOR_STATUS_LABELS } from '@/constants/enums'

const { items: seniorList, add } = useLocalCollection(initialSeniors)

const homeOptions = seniorHomes.map((h) => ({ value: h.id, label: h.name }))

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

const formNotice = ref('')

const selectedHomeName = computed(() => {
  const home = seniorHomes.find((h) => String(h.id) === String(form.value.seniorHomeId))
  return home?.name ?? ''
})

function handleSubmit() {
  add({
    firstName: form.value.firstName,
    lastName: form.value.lastName,
    pesel: form.value.pesel,
    birthDate: form.value.birthDate,
    gender: form.value.gender,
    seniorHome: selectedHomeName.value,
    accommodationDate: form.value.accommodationDate,
    dischargeDate: null,
    status: 'ACTIVE',
    roomNumber: '—',
    guardians: '—',
    diseases: form.value.diseases,
  })
  formNotice.value =
    'Senior zapisany lokalnie. Przypisanie pokoju i opiekuna (operacja atomowa) zostanie obsłużone po podłączeniu API.'
  form.value = {
    firstName: '',
    lastName: '',
    pesel: '',
    birthDate: '',
    gender: '',
    seniorHomeId: '',
    accommodationDate: '',
    diseases: '',
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

    <AlertMessage v-if="formNotice" variant="info">{{ formNotice }}</AlertMessage>

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
          <AppSelect v-model="form.seniorHomeId" :options="homeOptions" required />
        </FormField>
        <FormField label="Choroby" class="sm:col-span-2">
          <AppInput v-model="form.diseases" placeholder="np. cukrzyca, nadciśnienie" />
        </FormField>
        <div class="sm:col-span-2">
          <AppButton type="submit">Zapisz (lokalnie)</AppButton>
        </div>
      </form>
    </AppCard>

    <section class="space-y-3">
      <h2 class="text-lg font-medium text-slate-900">Lista seniorów</h2>
      <DataTable :columns="columns" :rows="seniorList">
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
