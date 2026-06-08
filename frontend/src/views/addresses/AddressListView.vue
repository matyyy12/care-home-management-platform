<script setup>
import { ref } from 'vue'
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'
import DataTable from '@/components/ui/DataTable.vue'
import FormField from '@/components/ui/FormField.vue'
import AppInput from '@/components/ui/AppInput.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AlertMessage from '@/components/ui/AlertMessage.vue'
import ReadOnlyNotice from '@/components/ui/ReadOnlyNotice.vue'
import { usePermissions } from '@/composables/usePermissions'
import { useAddressBook } from '@/composables/useAddressBook'
const { canWrite } = usePermissions('addresses')
const {
  addresses,
  postOffices,
  postOptions,
  loading,
  saving,
  error,
  addAddress,
  addPostOffice,
} = useAddressBook()

const addressColumns = [
  { key: 'city', label: 'Miasto' },
  { key: 'street', label: 'Ulica' },
  { key: 'apartmentNumber', label: 'Nr lokalu' },
  { key: 'postOffice', label: 'Poczta' },
]

const postColumns = [
  { key: 'postalCode', label: 'Kod pocztowy' },
  { key: 'city', label: 'Miejscowość' },
]

const addressForm = ref({ city: '', street: '', apartmentNumber: '', postOfficeId: '' })
const postForm = ref({ postalCode: '', city: '' })

async function submitAddress() {
  await addAddress(addressForm.value)
  addressForm.value = { city: '', street: '', apartmentNumber: '', postOfficeId: '' }
}

async function submitPostOffice() {
  await addPostOffice(postForm.value)
  postForm.value = { postalCode: '', city: '' }
}
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Adresy i poczty"
      description="Rejestr adresów z powiązaniem do poczty — ograniczenie duplikatów danych adresowych."
    />

    <AlertMessage v-if="error" variant="error">{{ error }}</AlertMessage>

    <ReadOnlyNotice v-if="!canWrite" />

    <p v-if="loading" class="text-sm text-slate-500">Ładowanie…</p>

    <div v-else class="grid gap-8 lg:grid-cols-2">
      <AppCard title="Poczty">
        <form v-if="canWrite" class="mb-4 grid gap-3" @submit.prevent="submitPostOffice">
          <FormField label="Kod pocztowy">
            <AppInput v-model="postForm.postalCode" placeholder="00-000" required />
          </FormField>
          <FormField label="Miejscowość">
            <AppInput v-model="postForm.city" required />
          </FormField>
          <AppButton type="submit" size="sm" :disabled="saving">
            {{ saving ? 'Zapisywanie…' : 'Dodaj pocztę' }}
          </AppButton>
        </form>
        <DataTable :columns="postColumns" :rows="postOffices" />
      </AppCard>

      <AppCard title="Adresy">
        <form v-if="canWrite" class="mb-4 grid gap-3" @submit.prevent="submitAddress">
          <FormField label="Miasto">
            <AppInput v-model="addressForm.city" required />
          </FormField>
          <FormField label="Ulica">
            <AppInput v-model="addressForm.street" required />
          </FormField>
          <FormField label="Nr lokalu">
            <AppInput v-model="addressForm.apartmentNumber" />
          </FormField>
          <FormField label="Poczta">
            <AppSelect v-model="addressForm.postOfficeId" :options="postOptions" required />
          </FormField>
          <AppButton type="submit" size="sm" :disabled="saving">
            {{ saving ? 'Zapisywanie…' : 'Dodaj adres' }}
          </AppButton>
        </form>
        <DataTable :columns="addressColumns" :rows="addresses" />
      </AppCard>
    </div>
  </div>
</template>
