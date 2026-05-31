<script setup>
import { ref } from 'vue'
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'
import DataTable from '@/components/ui/DataTable.vue'
import FormField from '@/components/ui/FormField.vue'
import AppInput from '@/components/ui/AppInput.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppTextarea from '@/components/ui/AppTextarea.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import { useLocalCollection } from '@/composables/useLocalCollection'
import { activities as initialActivities, seniorHomes } from '@/mock/data'
import { ACTIVITY_TYPES } from '@/constants/enums'

const { items: activityList, add } = useLocalCollection(initialActivities)

const homeOptions = seniorHomes.map((h) => ({ value: h.id, label: h.name }))
const typeLabel = Object.fromEntries(ACTIVITY_TYPES.map((t) => [t.value, t.label]))

const columns = [
  { key: 'name', label: 'Nazwa' },
  {
    key: 'type',
    label: 'Rodzaj',
    format: (row) => typeLabel[row.type] ?? row.type,
  },
  { key: 'price', label: 'Cena (zł)' },
  { key: 'seniorHome', label: 'Dom seniora' },
  { key: 'participants', label: 'Uczestnicy' },
]

const form = ref({
  name: '',
  type: '',
  price: 0,
  seniorHomeId: '',
  description: '',
})

function handleSubmit() {
  const home = seniorHomes.find((h) => String(h.id) === String(form.value.seniorHomeId))
  add({
    name: form.value.name,
    type: form.value.type,
    price: Number(form.value.price),
    seniorHome: home?.name ?? '',
    description: form.value.description,
    instructors: 0,
    participants: 0,
  })
  form.value = { name: '', type: '', price: 0, seniorHomeId: '', description: '' }
}

function typeVariant(type) {
  if (type === 'MEDICAL') return 'danger'
  if (type === 'RECREATIONAL') return 'success'
  return 'info'
}
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Zajęcia"
      description="Zajęcia medyczne, rekreacyjne i kulturalne — cena, prowadzący i uczestnicy."
    />

    <AppCard title="Dodaj zajęcia">
      <form class="grid gap-4 sm:grid-cols-2" @submit.prevent="handleSubmit">
        <FormField label="Nazwa" required>
          <AppInput v-model="form.name" required />
        </FormField>
        <FormField label="Rodzaj" required>
          <AppSelect v-model="form.type" :options="ACTIVITY_TYPES" required />
        </FormField>
        <FormField label="Cena (zł)" hint="Nieujemna">
          <AppInput v-model="form.price" type="number" min="0" step="0.01" />
        </FormField>
        <FormField label="Dom seniora" required>
          <AppSelect v-model="form.seniorHomeId" :options="homeOptions" required />
        </FormField>
        <FormField label="Opis" class="sm:col-span-2">
          <AppTextarea v-model="form.description" />
        </FormField>
        <div class="sm:col-span-2">
          <AppButton type="submit">Zapisz (lokalnie)</AppButton>
        </div>
      </form>
    </AppCard>

    <DataTable :columns="columns" :rows="activityList">
      <template #cell-type="{ row }">
        <AppBadge :variant="typeVariant(row.type)">{{ typeLabel[row.type] }}</AppBadge>
      </template>
    </DataTable>
  </div>
</template>
