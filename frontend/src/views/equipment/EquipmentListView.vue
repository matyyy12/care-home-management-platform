<script setup>
import { computed, ref } from 'vue'
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'
import DataTable from '@/components/ui/DataTable.vue'
import FormField from '@/components/ui/FormField.vue'
import AppInput from '@/components/ui/AppInput.vue'
import AppTextarea from '@/components/ui/AppTextarea.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppModal from '@/components/ui/AppModal.vue'
import AlertMessage from '@/components/ui/AlertMessage.vue'
import ReadOnlyNotice from '@/components/ui/ReadOnlyNotice.vue'
import { usePermissions } from '@/composables/usePermissions'
import { useEquipment } from '@/composables/useEquipment'

const { canWrite } = usePermissions('equipment')
const { equipmentList, loading, saving, error, addEquipment, editEquipment } = useEquipment()

const columns = [
  { key: 'inventoryNumber', label: 'Nr inwentarzowy' },
  { key: 'name', label: 'Nazwa' },
  { key: 'brand', label: 'Marka' },
  { key: 'modelName', label: 'Model' },
  { key: 'purchaseDate', label: 'Data zakupu', format: (row) => row.purchaseDate || '—' },
]

const emptyForm = {
  inventoryNumber: '',
  name: '',
  brand: '',
  modelName: '',
  purchaseDate: '',
  description: '',
}

const form = ref({ ...emptyForm })
const editForm = ref({ ...emptyForm })
const formNotice = ref('')
const detailRow = ref(null)
const detailStep = ref('details')
const detailOpen = computed(() => detailRow.value !== null)

async function handleSubmit() {
  try {
    await addEquipment(form.value)
    formNotice.value = 'Sprzęt zapisany w bazie danych.'
    form.value = { ...emptyForm }
  } catch {
    formNotice.value = ''
  }
}

function openDetail(row) {
  detailRow.value = row
  detailStep.value = 'details'
}

function closeDetail() {
  detailRow.value = null
  detailStep.value = 'details'
}

function showEditForm() {
  if (!detailRow.value) return

  editForm.value = {
    inventoryNumber: detailRow.value.inventoryNumber ?? '',
    name: detailRow.value.name ?? '',
    brand: detailRow.value.brand ?? '',
    modelName: detailRow.value.modelName ?? '',
    purchaseDate: detailRow.value.purchaseDate ?? '',
    description: detailRow.value.description ?? '',
  }
  detailStep.value = 'edit'
}

async function handleEditSubmit() {
  if (!detailRow.value) return

  try {
    await editEquipment(detailRow.value.id, editForm.value)
    formNotice.value = 'Sprzęt zaktualizowany.'
    closeDetail()
  } catch {
    formNotice.value = ''
  }
}
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Sprzęt"
      description="Marki, modele i sprzęt gabinetowy — hierarchia marka → model → sprzęt."
    />

    <AlertMessage v-if="error" variant="error">{{ error }}</AlertMessage>
    <AlertMessage v-if="formNotice" variant="success">{{ formNotice }}</AlertMessage>

    <ReadOnlyNotice v-if="!canWrite" />

    <AppCard v-if="canWrite" title="Dodaj sprzęt">
      <form class="grid gap-4 sm:grid-cols-2" @submit.prevent="handleSubmit">
        <FormField label="Nr inwentarzowy" required>
          <AppInput v-model="form.inventoryNumber" maxlength="30" required />
        </FormField>
        <FormField label="Nazwa sprzętu" required>
          <AppInput v-model="form.name" maxlength="50" required />
        </FormField>
        <FormField label="Marka" required>
          <AppInput v-model="form.brand" maxlength="30" required />
        </FormField>
        <FormField label="Model" required>
          <AppInput v-model="form.modelName" maxlength="30" required />
        </FormField>
        <FormField label="Data zakupu">
          <AppInput v-model="form.purchaseDate" type="date" />
        </FormField>
        <FormField label="Opis" class="sm:col-span-2">
          <AppTextarea v-model="form.description" />
        </FormField>
        <div class="sm:col-span-2">
          <AppButton type="submit" :disabled="saving">
            {{ saving ? 'Zapisywanie…' : 'Zapisz' }}
          </AppButton>
        </div>
      </form>
    </AppCard>

    <p v-if="loading" class="text-sm text-slate-500">Ładowanie…</p>
    <DataTable
      v-else
      clickable
      :columns="columns"
      :rows="equipmentList"
      empty-message="Brak sprzętu do wyświetlenia."
      @row-click="openDetail"
    />

    <AppModal :open="detailOpen" :title="detailRow?.name ?? ''" @close="closeDetail">
      <dl v-if="detailRow && detailStep === 'details'" class="space-y-3 text-sm">
        <div>
          <dt class="font-medium text-slate-700">Nr inwentarzowy</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.inventoryNumber || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Nazwa</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.name || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Model</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.modelLabel }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Data zakupu</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.purchaseDate || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Opis</dt>
          <dd class="mt-0.5 whitespace-pre-wrap text-slate-800">
            {{ detailRow.description || '—' }}
          </dd>
        </div>
      </dl>

      <form v-else-if="detailRow" class="grid gap-4 sm:grid-cols-2" @submit.prevent="handleEditSubmit">
        <FormField label="Nr inwentarzowy" required>
          <AppInput v-model="editForm.inventoryNumber" maxlength="30" required />
        </FormField>
        <FormField label="Nazwa sprzętu" required>
          <AppInput v-model="editForm.name" maxlength="50" required />
        </FormField>
        <FormField label="Marka" required>
          <AppInput v-model="editForm.brand" maxlength="30" required />
        </FormField>
        <FormField label="Model" required>
          <AppInput v-model="editForm.modelName" maxlength="30" required />
        </FormField>
        <FormField label="Data zakupu">
          <AppInput v-model="editForm.purchaseDate" type="date" />
        </FormField>
        <FormField label="Opis" class="sm:col-span-2">
          <AppTextarea v-model="editForm.description" />
        </FormField>
      </form>

      <div class="mt-5 flex flex-wrap gap-2">
        <template v-if="detailStep === 'details'">
          <AppButton
            v-if="canWrite"
            class="flex-1 justify-center"
            variant="secondary"
            @click="showEditForm"
          >
            Modyfikuj
          </AppButton>
          <AppButton class="flex-1 justify-center" variant="ghost" @click="closeDetail">
            Zamknij
          </AppButton>
        </template>
        <template v-else>
          <AppButton class="flex-1 justify-center" :disabled="saving" @click="handleEditSubmit">
            {{ saving ? 'Zapisywanie…' : 'Zapisz zmiany' }}
          </AppButton>
          <AppButton
            class="flex-1 justify-center"
            type="button"
            variant="secondary"
            :disabled="saving"
            @click="detailStep = 'details'"
          >
            Wróć
          </AppButton>
        </template>
      </div>
    </AppModal>
  </div>
</template>
