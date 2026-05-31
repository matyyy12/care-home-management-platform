<script setup>
import { ref } from 'vue'
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'
import DataTable from '@/components/ui/DataTable.vue'
import FormField from '@/components/ui/FormField.vue'
import AppInput from '@/components/ui/AppInput.vue'
import AppButton from '@/components/ui/AppButton.vue'
import ReadOnlyNotice from '@/components/ui/ReadOnlyNotice.vue'
import { usePermissions } from '@/composables/usePermissions'
import { useLocalCollection } from '@/composables/useLocalCollection'

const { canWrite } = usePermissions('senior-homes')
import { seniorHomes as initialHomes } from '@/mock/data'

const { items: homes, add } = useLocalCollection(initialHomes)

const columns = [
  { key: 'name', label: 'Nazwa' },
  { key: 'phone', label: 'Telefon' },
  { key: 'email', label: 'E-mail' },
  { key: 'addressLabel', label: 'Adres' },
  {
    key: 'owners',
    label: 'Właściciele',
    format: (row) => row.owners?.join(', ') ?? '—',
  },
]

const form = ref({
  name: '',
  phone: '',
  email: '',
  website: '',
})

function handleSubmit() {
  add({
    ...form.value,
    foundedAt: new Date().toISOString().slice(0, 10),
    addressLabel: '—',
    owners: [],
    website: form.value.website || '',
  })
  form.value = { name: '', phone: '', email: '', website: '' }
}
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Domy seniora"
      description="Ewidencja domów seniora: dane kontaktowe, adres i właściciele."
    />

    <ReadOnlyNotice v-if="!canWrite" />

    <AppCard v-if="canWrite" title="Dodaj dom seniora">
      <form class="grid gap-4 sm:grid-cols-2" @submit.prevent="handleSubmit">
        <FormField label="Nazwa" required>
          <AppInput v-model="form.name" required />
        </FormField>
        <FormField label="Telefon" required>
          <AppInput v-model="form.phone" type="tel" required />
        </FormField>
        <FormField label="E-mail" hint="Opcjonalnie — walidacja formatu po podłączeniu API">
          <AppInput v-model="form.email" type="email" />
        </FormField>
        <FormField label="Strona WWW">
          <AppInput v-model="form.website" type="url" placeholder="https://…" />
        </FormField>
        <div class="sm:col-span-2">
          <AppButton type="submit">Zapisz (lokalnie)</AppButton>
        </div>
      </form>
    </AppCard>

    <section class="space-y-3">
      <h2 class="text-lg font-medium text-slate-900">Lista domów</h2>
      <DataTable :columns="columns" :rows="homes" />
    </section>
  </div>
</template>
