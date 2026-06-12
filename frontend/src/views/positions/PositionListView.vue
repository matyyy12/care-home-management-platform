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
import { usePositions } from '@/composables/usePositions'

const { canWrite } = usePermissions('positions')
const { positionList, loading, saving, error, addPosition, editPosition, removePosition } = usePositions()

const columns = [
  { key: 'name', label: 'Nazwa stanowiska' },
  { key: 'duties', label: 'Zakres obowiązków' },
]

const form = ref({ name: '', duties: '' })
const editForm = ref({ name: '', duties: '' })
const formNotice = ref('')
const detailRow = ref(null)
const detailStep = ref('details')
const detailOpen = computed(() => detailRow.value !== null)

async function handleSubmit() {
  try {
    await addPosition(form.value)
    formNotice.value = 'Stanowisko zapisane w bazie danych.'
    form.value = { name: '', duties: '' }
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
    name: detailRow.value.name ?? '',
    duties: detailRow.value.duties ?? '',
  }
  detailStep.value = 'edit'
}

function showDeleteConfirm() {
  detailStep.value = 'delete'
}

async function handleEditSubmit() {
  if (!detailRow.value) return

  try {
    await editPosition(detailRow.value.id, editForm.value)
    formNotice.value = 'Stanowisko zaktualizowane.'
    closeDetail()
  } catch {
    formNotice.value = ''
  }
}

async function handleDeleteConfirm() {
  if (!detailRow.value) return

  try {
    await removePosition(detailRow.value.id)
    formNotice.value = 'Stanowisko usunięte.'
    closeDetail()
  } catch {
    formNotice.value = ''
  }
}
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Stanowiska"
      description="Słownik stanowisk — wykorzystywany w ewidencji personelu i raportach obciążenia."
    />

    <AlertMessage v-if="error" variant="error">{{ error }}</AlertMessage>
    <AlertMessage v-if="formNotice" variant="success">{{ formNotice }}</AlertMessage>

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
          {{ saving ? 'Zapisywanie…' : 'Zapisz' }}
        </AppButton>
      </form>
    </AppCard>

    <p v-if="loading" class="text-sm text-slate-500">Ładowanie…</p>
    <DataTable
      v-else
      clickable
      :columns="columns"
      :rows="positionList"
      @row-click="openDetail"
    />

    <AppModal :open="detailOpen" :title="detailRow?.name ?? ''" @close="closeDetail">
      <dl v-if="detailRow && detailStep === 'details'" class="space-y-3 text-sm">
        <div>
          <dt class="font-medium text-slate-700">Nazwa stanowiska</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.name || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Zakres obowiązków</dt>
          <dd class="mt-0.5 whitespace-pre-wrap text-slate-800">{{ detailRow.duties || '—' }}</dd>
        </div>
      </dl>

      <form v-else-if="detailRow && detailStep === 'edit'" class="space-y-4" @submit.prevent="handleEditSubmit">
        <FormField label="Nazwa" required>
          <AppInput v-model="editForm.name" maxlength="20" required />
        </FormField>
        <FormField label="Zakres obowiązków">
          <AppTextarea v-model="editForm.duties" maxlength="400" />
        </FormField>
      </form>

      <div v-else-if="detailRow" class="space-y-3 text-sm">
        <p class="text-slate-800">
          Czy na pewno chcesz usunąć stanowisko <strong>{{ detailRow.name }}</strong>?
        </p>
        <p class="text-slate-500">
          Usunięcie może zostać zablokowane, jeśli stanowisko jest przypisane do pracowników.
        </p>
      </div>

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
          <AppButton
            v-if="canWrite"
            class="flex-1 justify-center"
            variant="danger"
            @click="showDeleteConfirm"
          >
            Usuń
          </AppButton>
          <AppButton class="flex-1 justify-center" variant="ghost" @click="closeDetail">
            Zamknij
          </AppButton>
        </template>
        <template v-else-if="detailStep === 'edit'">
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
        <template v-else>
          <AppButton
            class="flex-1 justify-center"
            variant="danger"
            :disabled="saving"
            @click="handleDeleteConfirm"
          >
            {{ saving ? 'Usuwanie…' : 'Usuń stanowisko' }}
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
