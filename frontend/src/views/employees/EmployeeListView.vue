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
import { useLocalCollection } from '@/composables/useLocalCollection'
import { employees as initialEmployees, seniorHomes, positions } from '@/mock/data'

const { items: employeeList, add } = useLocalCollection(initialEmployees)

const homeOptions = seniorHomes.map((h) => ({ value: h.id, label: h.name }))
const positionOptions = positions.map((p) => ({ value: p.id, label: p.name }))

const columns = [
  { key: 'lastName', label: 'Nazwisko' },
  { key: 'firstName', label: 'Imię' },
  { key: 'position', label: 'Stanowisko' },
  { key: 'seniorHome', label: 'Dom seniora' },
  { key: 'phone', label: 'Telefon' },
  {
    key: 'isMedical',
    label: 'Medyczny',
    format: (row) => (row.isMedical ? 'Tak' : 'Nie'),
  },
]

const form = ref({
  firstName: '',
  lastName: '',
  seniorHomeId: '',
  positionId: '',
  phone: '',
  email: '',
  hiredAt: '',
  isMedical: 'false',
})

const selectedLabels = computed(() => ({
  home: seniorHomes.find((h) => String(h.id) === String(form.value.seniorHomeId))?.name ?? '',
  position: positions.find((p) => String(p.id) === String(form.value.positionId))?.name ?? '',
}))

function handleSubmit() {
  const isMedical = form.value.isMedical === 'true'
  add({
    firstName: form.value.firstName,
    lastName: form.value.lastName,
    seniorHome: selectedLabels.value.home,
    position: selectedLabels.value.position,
    phone: form.value.phone,
    email: form.value.email,
    hiredAt: form.value.hiredAt,
    terminatedAt: null,
    isMedical,
    salary: 0,
    ...(isMedical ? { licenseNumber: '—', specialization: '—' } : {}),
  })
  form.value = {
    firstName: '',
    lastName: '',
    seniorHomeId: '',
    positionId: '',
    phone: '',
    email: '',
    hiredAt: '',
    isMedical: 'false',
  }
}
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Pracownicy"
      description="Personel domu seniora — stanowisko, adres, kierownik oraz oznaczenie pracownika medycznego."
    />

    <AppCard title="Dodaj pracownika">
      <form class="grid gap-4 sm:grid-cols-2" @submit.prevent="handleSubmit">
        <FormField label="Imię" required>
          <AppInput v-model="form.firstName" required />
        </FormField>
        <FormField label="Nazwisko" required>
          <AppInput v-model="form.lastName" required />
        </FormField>
        <FormField label="Dom seniora" required>
          <AppSelect v-model="form.seniorHomeId" :options="homeOptions" required />
        </FormField>
        <FormField label="Stanowisko" required>
          <AppSelect v-model="form.positionId" :options="positionOptions" required />
        </FormField>
        <FormField label="Telefon" required>
          <AppInput v-model="form.phone" type="tel" required />
        </FormField>
        <FormField label="E-mail">
          <AppInput v-model="form.email" type="email" />
        </FormField>
        <FormField label="Data zatrudnienia" required>
          <AppInput v-model="form.hiredAt" type="date" required />
        </FormField>
        <FormField label="Pracownik medyczny">
          <AppSelect
            v-model="form.isMedical"
            :options="[
              { value: 'false', label: 'Nie' },
              { value: 'true', label: 'Tak' },
            ]"
          />
        </FormField>
        <div class="sm:col-span-2">
          <AppButton type="submit">Zapisz (lokalnie)</AppButton>
        </div>
      </form>
    </AppCard>

    <section class="space-y-3">
      <h2 class="text-lg font-medium text-slate-900">Lista pracowników</h2>
      <DataTable :columns="columns" :rows="employeeList">
        <template #cell-isMedical="{ row }">
          <AppBadge :variant="row.isMedical ? 'info' : 'neutral'">
            {{ row.isMedical ? 'Medyczny' : 'Standard' }}
          </AppBadge>
        </template>
      </DataTable>
    </section>
  </div>
</template>
