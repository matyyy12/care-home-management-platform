<script setup>
import { computed, ref } from 'vue'
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'
import DataTable from '@/components/ui/DataTable.vue'
import FormField from '@/components/ui/FormField.vue'
import AppInput from '@/components/ui/AppInput.vue'
import AppTextarea from '@/components/ui/AppTextarea.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AlertMessage from '@/components/ui/AlertMessage.vue'
import ReadOnlyNotice from '@/components/ui/ReadOnlyNotice.vue'
import { usePermissions } from '@/composables/usePermissions'
import { usePositions } from '@/composables/usePositions'
import { sourceNotice } from '@/composables/apiMode'

const { canWrite } = usePermissions('positions')
const { positionList, loading, saving, error, usingMock, addPosition } = usePositions()

const dataSourceNotice = computed(() => sourceNotice(usingMock.value))

const columns = [
  { key: 'name', label: 'Nazwa stanowiska' },
  { key: 'duties', label: 'Zakres obowiązków' },
]

const form = ref({ name: '', duties: '' })

async function handleSubmit() {
  await addPosition(form.value)
  form.value = { name: '', duties: '' }
}
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Stanowiska"
      description="Słownik stanowisk — wykorzystywany w ewidencji personelu i raportach obciążenia."
    />

    <AlertMessage v-if="dataSourceNotice" variant="info">{{ dataSourceNotice }}</AlertMessage>
    <AlertMessage v-if="error" variant="error">{{ error }}</AlertMessage>

    <ReadOnlyNotice v-if="!canWrite" />

    <AppCard v-if="canWrite" title="Dodaj stanowisko">
      <form class="max-w-xl space-y-4" @submit.prevent="handleSubmit">
        <FormField label="Nazwa" required>
          <AppInput v-model="form.name" required />
        </FormField>
        <FormField label="Zakres obowiązków">
          <AppTextarea v-model="form.duties" />
        </FormField>
        <AppButton type="submit" :disabled="saving">
          {{ saving ? 'Zapisywanie…' : usingMock ? 'Zapisz (lokalnie)' : 'Zapisz' }}
        </AppButton>
      </form>
    </AppCard>

    <p v-if="loading" class="text-sm text-slate-500">Ładowanie…</p>
    <DataTable v-else :columns="columns" :rows="positionList" />
  </div>
</template>
