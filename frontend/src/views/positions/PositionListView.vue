<script setup>
import { ref } from 'vue'
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'
import DataTable from '@/components/ui/DataTable.vue'
import FormField from '@/components/ui/FormField.vue'
import AppInput from '@/components/ui/AppInput.vue'
import AppTextarea from '@/components/ui/AppTextarea.vue'
import AppButton from '@/components/ui/AppButton.vue'
import ReadOnlyNotice from '@/components/ui/ReadOnlyNotice.vue'
import { usePermissions } from '@/composables/usePermissions'
import { useLocalCollection } from '@/composables/useLocalCollection'

const { canWrite } = usePermissions('positions')
import { positions as initialPositions } from '@/mock/data'

const { items: positionList, add } = useLocalCollection(initialPositions)

const columns = [
  { key: 'name', label: 'Nazwa stanowiska' },
  { key: 'duties', label: 'Zakres obowiązków' },
]

const form = ref({ name: '', duties: '' })

function handleSubmit() {
  add({ ...form.value })
  form.value = { name: '', duties: '' }
}
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Stanowiska"
      description="Słownik stanowisk — wykorzystywany w ewidencji personelu i raportach obciążenia."
    />

    <ReadOnlyNotice v-if="!canWrite" />

    <AppCard v-if="canWrite" title="Dodaj stanowisko">
      <form class="max-w-xl space-y-4" @submit.prevent="handleSubmit">
        <FormField label="Nazwa" required>
          <AppInput v-model="form.name" required />
        </FormField>
        <FormField label="Zakres obowiązków">
          <AppTextarea v-model="form.duties" />
        </FormField>
        <AppButton type="submit">Zapisz (lokalnie)</AppButton>
      </form>
    </AppCard>

    <DataTable :columns="columns" :rows="positionList" />
  </div>
</template>
