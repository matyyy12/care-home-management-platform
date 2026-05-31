<script setup>
import { ref } from 'vue'
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'
import DataTable from '@/components/ui/DataTable.vue'
import FormField from '@/components/ui/FormField.vue'
import AppInput from '@/components/ui/AppInput.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppButton from '@/components/ui/AppButton.vue'
import { useLocalCollection } from '@/composables/useLocalCollection'
import { addresses as initialAddresses, postOffices as initialPostOffices } from '@/mock/data'

const { items: addresses, add: addAddress } = useLocalCollection(initialAddresses)
const { items: postOffices, add: addPostOffice } = useLocalCollection(initialPostOffices)

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

const postOptions = ref(
  initialPostOffices.map((p) => ({
    value: p.id,
    label: `${p.postalCode} ${p.city}`,
  })),
)

function submitAddress() {
  const post = postOffices.value.find((p) => String(p.id) === String(addressForm.value.postOfficeId))
  addAddress({
    city: addressForm.value.city,
    street: addressForm.value.street,
    apartmentNumber: addressForm.value.apartmentNumber,
    postOffice: post ? `${post.postalCode} ${post.city}` : '—',
  })
  addressForm.value = { city: '', street: '', apartmentNumber: '', postOfficeId: '' }
}

function submitPostOffice() {
  addPostOffice({ ...postForm.value })
  const added = postOffices.value[postOffices.value.length - 1]
  postOptions.value.push({
    value: added.id,
    label: `${added.postalCode} ${added.city}`,
  })
  postForm.value = { postalCode: '', city: '' }
}
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Adresy i poczty"
      description="Rejestr adresów z powiązaniem do poczty — ograniczenie duplikatów danych adresowych."
    />

    <div class="grid gap-8 lg:grid-cols-2">
      <AppCard title="Poczty">
        <form class="mb-4 grid gap-3" @submit.prevent="submitPostOffice">
          <FormField label="Kod pocztowy">
            <AppInput v-model="postForm.postalCode" placeholder="00-000" required />
          </FormField>
          <FormField label="Miejscowość">
            <AppInput v-model="postForm.city" required />
          </FormField>
          <AppButton type="submit" size="sm">Dodaj pocztę</AppButton>
        </form>
        <DataTable :columns="postColumns" :rows="postOffices" />
      </AppCard>

      <AppCard title="Adresy">
        <form class="mb-4 grid gap-3" @submit.prevent="submitAddress">
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
          <AppButton type="submit" size="sm">Dodaj adres</AppButton>
        </form>
        <DataTable :columns="addressColumns" :rows="addresses" />
      </AppCard>
    </div>
  </div>
</template>
