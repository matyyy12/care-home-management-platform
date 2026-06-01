<script setup>
import { computed, ref, watch } from 'vue'
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'
import DataTable from '@/components/ui/DataTable.vue'
import FormField from '@/components/ui/FormField.vue'
import AppInput from '@/components/ui/AppInput.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppTextarea from '@/components/ui/AppTextarea.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import ReadOnlyNotice from '@/components/ui/ReadOnlyNotice.vue'
import { usePermissions } from '@/composables/usePermissions'
import { useHomeScope } from '@/composables/useHomeScope'
import { useLocalCollection } from '@/composables/useLocalCollection'
import { useAuthStore } from '@/stores/auth'
import { activities as initialActivities, seniorHomes } from '@/mock/data'
import { ACTIVITY_TYPES } from '@/constants/enums'

const { canWrite } = usePermissions('activities')
const auth = useAuthStore()
const { filterItems, scopedHomeSelectOptions } = useHomeScope()
const { items: allActivities, add } = useLocalCollection(initialActivities)

const activityList = computed(() => filterItems(allActivities.value))
const homeOptions = computed(() => scopedHomeSelectOptions(seniorHomes))
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

watch(
  () => [auth.isHomeScoped, auth.assignedSeniorHomeId],
  () => {
    if (auth.isHomeScoped) {
      form.value.seniorHomeId = auth.assignedSeniorHomeId
    }
  },
  { immediate: true },
)

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
  form.value = {
    name: '',
    type: '',
    price: 0,
    seniorHomeId: auth.isHomeScoped ? auth.assignedSeniorHomeId : '',
    description: '',
  }
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

    <ReadOnlyNotice v-if="!canWrite">
      Zajęcia medyczne przeglądasz w trybie podglądu — tworzenie i edycja harmonogramu dla menadżera
      lub dyrektora.
    </ReadOnlyNotice>

    <AppCard v-if="canWrite" title="Dodaj zajęcia">
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
          <AppSelect
            v-model="form.seniorHomeId"
            :options="homeOptions"
            :disabled="auth.isHomeScoped"
            required
          />
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
