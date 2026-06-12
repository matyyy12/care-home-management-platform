<script setup>
import { computed, ref, watch } from 'vue'
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'
import DataTable from '@/components/ui/DataTable.vue'
import FormField from '@/components/ui/FormField.vue'
import AppInput from '@/components/ui/AppInput.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AppModal from '@/components/ui/AppModal.vue'
import AlertMessage from '@/components/ui/AlertMessage.vue'
import ReadOnlyNotice from '@/components/ui/ReadOnlyNotice.vue'
import { usePermissions } from '@/composables/usePermissions'
import { useSeniors } from '@/composables/useSeniors'
import { useAuthStore } from '@/stores/auth'
import { GENDER_OPTIONS, SENIOR_STATUS_LABELS } from '@/constants/enums'

const { canWrite, role } = usePermissions('seniors')
const auth = useAuthStore()
const { seniorList, homeOptions, loading, saving, error, addSenior, editSenior, removeSenior } = useSeniors()

const columns = [
  { key: 'lastName', label: 'Nazwisko' },
  { key: 'firstName', label: 'Imię' },
  { key: 'pesel', label: 'PESEL' },
  { key: 'seniorHome', label: 'Dom seniora' },
  { key: 'roomNumber', label: 'Pokój' },
  { key: 'guardians', label: 'Opiekunowie' },
]

const form = ref({
  firstName: '',
  lastName: '',
  pesel: '',
  birthDate: '',
  gender: '',
  seniorHomeId: '',
  accommodationDate: '',
  diseases: '',
})
const editForm = ref({
  firstName: '',
  lastName: '',
  pesel: '',
  birthDate: '',
  gender: '',
  seniorHomeId: '',
  accommodationDate: '',
  dischargeDate: '',
  diseases: '',
})
const detailRow = ref(null)
const detailStep = ref('details')
const detailOpen = computed(() => detailRow.value !== null)

const formNotice = ref('')

watch(
  () => [auth.isHomeScoped, auth.assignedSeniorHomeId],
  () => {
    if (auth.isHomeScoped) {
      form.value.seniorHomeId = auth.assignedSeniorHomeId
    }
  },
  { immediate: true },
)

async function handleSubmit() {
  try {
    await addSenior(form.value)
    formNotice.value = 'Senior zapisany w bazie danych.'
    form.value = {
      firstName: '',
      lastName: '',
      pesel: '',
      birthDate: '',
      gender: '',
      seniorHomeId: auth.isHomeScoped ? auth.assignedSeniorHomeId : '',
      accommodationDate: '',
      diseases: '',
    }
  } catch {
    formNotice.value = ''
  }
}

function statusBadge(row) {
  return row.dischargeDate ? 'neutral' : 'success'
}

function openDetail(row) {
  detailRow.value = row
  detailStep.value = 'details'
}

function closeDetail() {
  detailRow.value = null
  detailStep.value = 'details'
}

function rowToForm(row) {
  return {
    firstName: row.firstName ?? '',
    lastName: row.lastName ?? '',
    pesel: row.pesel ?? '',
    birthDate: row.birthDate ?? '',
    gender: row.gender ?? '',
    seniorHomeId: row.seniorHomeId ?? '',
    accommodationDate: row.accommodationDate ?? '',
    dischargeDate: row.dischargeDate ?? '',
    diseases: row.diseases ?? '',
  }
}

function showEditForm() {
  if (!detailRow.value) return
  editForm.value = rowToForm(detailRow.value)
  detailStep.value = 'edit'
}

function showDeleteConfirm() {
  detailStep.value = 'delete'
}

async function handleEditSubmit() {
  if (!detailRow.value) return

  try {
    await editSenior(detailRow.value.id, editForm.value)
    formNotice.value = 'Dane seniora zaktualizowane.'
    closeDetail()
  } catch {
    formNotice.value = ''
  }
}

async function handleDeleteConfirm() {
  if (!detailRow.value) return

  try {
    await removeSenior(detailRow.value.id)
    formNotice.value = 'Senior usunięty z bazy danych.'
    closeDetail()
  } catch {
    formNotice.value = ''
  }
}
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Seniorzy"
      description="Dane osobowe, PESEL, choroby i zakwaterowanie. Przypisania do pokoju i opiekunów edytujesz w zakładce Przypisania."
    />

    <AlertMessage v-if="error" variant="error">{{ error }}</AlertMessage>
    <AlertMessage v-if="formNotice" variant="success">{{ formNotice }}</AlertMessage>

    <ReadOnlyNotice v-if="!canWrite">
      <template v-if="role === 'MEDICAL'">
        Jako pracownik medyczny przeglądasz dane seniorów (w tym choroby). Dodawanie i edycja
        ewidencji należą do menadżera lub dyrektora.
      </template>
    </ReadOnlyNotice>

    <AppCard v-if="canWrite" title="Dodaj seniora">
      <form class="grid gap-4 sm:grid-cols-2" @submit.prevent="handleSubmit">
        <FormField label="Imię" required>
          <AppInput v-model="form.firstName" required />
        </FormField>
        <FormField label="Nazwisko" required>
          <AppInput v-model="form.lastName" required />
        </FormField>
        <FormField label="PESEL" required hint="Unikalny w systemie">
          <AppInput v-model="form.pesel" required maxlength="11" />
        </FormField>
        <FormField label="Data urodzenia" required>
          <AppInput v-model="form.birthDate" type="date" required />
        </FormField>
        <FormField label="Płeć" required>
          <AppSelect v-model="form.gender" :options="GENDER_OPTIONS" required />
        </FormField>
        <FormField label="Data zakwaterowania" required>
          <AppInput v-model="form.accommodationDate" type="date" required />
        </FormField>
        <FormField label="Dom seniora" required>
          <AppSelect
            v-model="form.seniorHomeId"
            :options="homeOptions"
            :disabled="auth.isHomeScoped"
            required
          />
        </FormField>
        <FormField label="Choroby" class="sm:col-span-2">
          <AppInput v-model="form.diseases" placeholder="np. cukrzyca, nadciśnienie" />
        </FormField>
        <div class="sm:col-span-2">
          <AppButton type="submit" :disabled="saving">
            {{ saving ? 'Zapisywanie…' : 'Zapisz' }}
          </AppButton>
        </div>
      </form>
    </AppCard>

    <section class="space-y-3">
      <h2 class="text-lg font-medium text-slate-900">Lista seniorów</h2>
      <p v-if="loading" class="text-sm text-slate-500">Ładowanie…</p>
      <DataTable
        v-else
        clickable
        :columns="columns"
        :rows="seniorList"
        row-key="id"
        @row-click="openDetail"
      >
        <template #cell-lastName="{ row }">
          <span class="font-medium">{{ row.lastName }}</span>
        </template>
        <template #cell-firstName="{ row }">
          {{ row.firstName }}
          <AppBadge :variant="statusBadge(row)" class="ml-2">
            {{ SENIOR_STATUS_LABELS[row.status] ?? row.status }}
          </AppBadge>
        </template>
      </DataTable>
    </section>

    <AppModal :open="detailOpen" :title="detailRow ? `${detailRow.firstName} ${detailRow.lastName}` : ''" @close="closeDetail">
      <dl v-if="detailRow && detailStep === 'details'" class="space-y-3 text-sm">
        <div>
          <dt class="font-medium text-slate-700">PESEL</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.pesel }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Data urodzenia</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.birthDate || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Płeć</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.gender || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Dom seniora</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.seniorHome || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Pokój</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.roomNumber || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Data zakwaterowania</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.accommodationDate || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Data wypisu</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.dischargeDate || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Choroby</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.diseases || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Status</dt>
          <dd class="mt-0.5">
            <AppBadge :variant="statusBadge(detailRow)">
              {{ SENIOR_STATUS_LABELS[detailRow.status] ?? detailRow.status }}
            </AppBadge>
          </dd>
        </div>
      </dl>

      <form
        v-else-if="detailRow && detailStep === 'edit'"
        id="senior-edit-form"
        class="space-y-4"
        @submit.prevent="handleEditSubmit"
      >
        <FormField label="Imię" required>
          <AppInput v-model="editForm.firstName" required />
        </FormField>
        <FormField label="Nazwisko" required>
          <AppInput v-model="editForm.lastName" required />
        </FormField>
        <FormField label="PESEL" required>
          <AppInput v-model="editForm.pesel" required maxlength="11" />
        </FormField>
        <FormField label="Data urodzenia" required>
          <AppInput v-model="editForm.birthDate" type="date" required />
        </FormField>
        <FormField label="Płeć" required>
          <AppSelect v-model="editForm.gender" :options="GENDER_OPTIONS" required />
        </FormField>
        <FormField label="Data zakwaterowania" required>
          <AppInput v-model="editForm.accommodationDate" type="date" required />
        </FormField>
        <FormField label="Data wypisu">
          <AppInput v-model="editForm.dischargeDate" type="date" />
        </FormField>
        <FormField label="Dom seniora" required>
          <AppSelect
            v-model="editForm.seniorHomeId"
            :options="homeOptions"
            :disabled="auth.isHomeScoped"
            required
          />
        </FormField>
        <FormField label="Choroby">
          <AppInput v-model="editForm.diseases" placeholder="np. cukrzyca, nadciśnienie" />
        </FormField>
      </form>

      <div v-else-if="detailRow" class="space-y-3 text-sm">
        <p class="text-slate-800">
          Czy na pewno chcesz usunąć seniora
          <strong>{{ detailRow.firstName }} {{ detailRow.lastName }}</strong>?
        </p>
        <p class="text-slate-500">
          Jeżeli rekord ma powiązania w bazie, backend może zablokować usunięcie i pokaże powód.
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
          <AppButton
            form="senior-edit-form"
            type="submit"
            class="flex-1 justify-center"
            :disabled="saving"
          >
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
            {{ saving ? 'Usuwanie…' : 'Usuń seniora' }}
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
