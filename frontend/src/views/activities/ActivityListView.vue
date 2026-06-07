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
import AppModal from '@/components/ui/AppModal.vue'
import AlertMessage from '@/components/ui/AlertMessage.vue'
import ReadOnlyNotice from '@/components/ui/ReadOnlyNotice.vue'
import { usePermissions } from '@/composables/usePermissions'
import { useHomeScope } from '@/composables/useHomeScope'
import { useAuthStore } from '@/stores/auth'
import { useActivities } from '@/composables/useActivities'
import { sourceNotice } from '@/composables/apiMode'
import { ACTIVITY_TYPES } from '@/constants/enums'

const { canWrite } = usePermissions('activities')
const auth = useAuthStore()
const { filterItems, scopedHomeSelectOptions } = useHomeScope()
const {
  filteredActivities: activitiesByHomeFilter,
  seniorHomes,
  filterHomeId,
  loading,
  saving,
  error,
  usingMock,
  deletingId,
  addActivity,
  editActivity,
  removeActivity,
  seniorHomeIdForRow,
} = useActivities()

const dataSourceNotice = computed(() => sourceNotice(usingMock.value))
const homeOptions = computed(() => scopedHomeSelectOptions(seniorHomes.value))
const filteredActivities = computed(() => filterItems(activitiesByHomeFilter.value))

const filterOptions = computed(() => [
  { value: '', label: 'Wszystkie domy' },
  ...scopedHomeSelectOptions(seniorHomes.value),
])

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

const formNotice = ref('')
const manageRow = ref(null)
const manageStep = ref('menu')

const editForm = ref({
  name: '',
  type: '',
  price: 0,
  seniorHomeId: '',
  description: '',
})

const manageOpen = computed(() => manageRow.value !== null)

const detailRow = ref(null)
const detailOpen = computed(() => detailRow.value !== null)

const modalTitle = computed(() => {
  if (!manageRow.value) return ''
  if (manageStep.value === 'edit') return `Edytuj: ${manageRow.value.name}`
  if (manageStep.value === 'delete') return 'Usuń zajęcia'
  return manageRow.value.name
})

watch(manageOpen, (open) => {
  if (!open) {
    manageStep.value = 'menu'
  }
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

async function handleSubmit() {
  try {
    const result = await addActivity(form.value)
    formNotice.value = result.savedLocally
      ? 'Zajęcia zapisane lokalnie (tryb demo).'
      : 'Zajęcia zapisane w bazie danych.'
    form.value = {
      name: '',
      type: '',
      price: 0,
      seniorHomeId: auth.isHomeScoped ? auth.assignedSeniorHomeId : '',
      description: '',
    }
  } catch {
    formNotice.value = ''
  }
}

function typeVariant(type) {
  if (type === 'MEDICAL') return 'danger'
  if (type === 'RECREATIONAL') return 'success'
  return 'info'
}

function openDetail(row) {
  detailRow.value = row
}

function openManageFromDetail() {
  const row = detailRow.value
  closeDetail()
  if (row) openManage(row)
}

function closeDetail() {
  detailRow.value = null
}

function openManage(row) {
  manageRow.value = row
  manageStep.value = 'menu'
}

function closeManage() {
  manageRow.value = null
  manageStep.value = 'menu'
}

function showEditForm() {
  const row = manageRow.value
  if (!row) return
  editForm.value = {
    name: row.name,
    type: row.type,
    price: row.price,
    seniorHomeId: seniorHomeIdForRow(row),
    description: row.description ?? '',
  }
  manageStep.value = 'edit'
}

async function handleEditSubmit() {
  if (!manageRow.value) return
  try {
    const result = await editActivity(manageRow.value.id, editForm.value)
    formNotice.value = result.savedLocally
      ? 'Zmiany zapisane lokalnie (tryb demo).'
      : 'Zmiany zapisane w bazie danych.'
    closeManage()
  } catch {
    formNotice.value = ''
  }
}

function showDeleteConfirm() {
  manageStep.value = 'delete'
}

async function confirmDelete() {
  const row = manageRow.value
  if (!row) return
  try {
    const result = await removeActivity(row.id)
    formNotice.value = result.savedLocally
      ? 'Zajęcia usunięte lokalnie (tryb demo).'
      : 'Zajęcia usunięte z bazy danych.'
    closeManage()
  } catch {
    formNotice.value = ''
  }
}

function rowBusy(row) {
  return deletingId.value === row.id || (saving.value && manageRow.value?.id === row.id)
}
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Zajęcia"
      description="Zajęcia medyczne, rekreacyjne i kulturalne — cena, prowadzący i uczestnicy."
    />

    <AlertMessage v-if="dataSourceNotice" variant="info">{{ dataSourceNotice }}</AlertMessage>
    <AlertMessage v-if="error" variant="error">{{ error }}</AlertMessage>
    <AlertMessage v-if="formNotice" variant="success">{{ formNotice }}</AlertMessage>

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
        <FormField label="Cena (zł)">
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
          <AppButton type="submit" :disabled="saving">
            {{ saving ? 'Zapisywanie…' : usingMock ? 'Zapisz (lokalnie)' : 'Zapisz' }}
          </AppButton>
        </div>
      </form>
    </AppCard>

    <AppModal :open="detailOpen" :title="detailRow?.name ?? ''" @close="closeDetail">
      <dl v-if="detailRow" class="space-y-3 text-sm">
        <div class="flex flex-wrap items-center gap-2">
          <dt class="font-medium text-slate-700">Rodzaj</dt>
          <dd>
            <AppBadge :variant="typeVariant(detailRow.type)">
              {{ typeLabel[detailRow.type] ?? detailRow.type }}
            </AppBadge>
          </dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Cena</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.price }} zł</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Dom seniora</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.seniorHome }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Uczestnicy</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.participants }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Opis</dt>
          <dd class="mt-1 whitespace-pre-wrap text-slate-800">
            {{ detailRow.description?.trim() || 'Brak opisu.' }}
          </dd>
        </div>
      </dl>
      <div class="mt-5 flex gap-2">
        <AppButton
          v-if="canWrite"
          variant="secondary"
          class="flex-1 justify-center"
          @click="openManageFromDetail"
        >
          Zarządzaj
        </AppButton>
        <AppButton class="flex-1 justify-center" variant="ghost" @click="closeDetail">
          Zamknij
        </AppButton>
      </div>
    </AppModal>

    <AppModal :open="manageOpen" :title="modalTitle" @close="closeManage">
      <div v-if="manageStep === 'menu'" class="space-y-3">
        <p class="text-sm text-slate-600">Wybierz akcję:</p>
        <div class="flex gap-2">
          <AppButton class="flex-1 justify-center" variant="secondary" @click="showEditForm">
            Zmień
          </AppButton>
          <AppButton class="flex-1 justify-center" variant="danger" @click="showDeleteConfirm">
            Usuń
          </AppButton>
        </div>
        <AppButton class="w-full justify-center" variant="ghost" @click="closeManage">
          Anuluj
        </AppButton>
      </div>

      <div v-else-if="manageStep === 'delete'" class="space-y-4">
        <p class="text-sm text-slate-600">
          Czy na pewno chcesz usunąć zajęcia
          <span class="font-medium text-slate-900">„{{ manageRow?.name }}”</span>?
          Tej operacji nie można cofnąć.
        </p>
        <div class="flex gap-2">
          <AppButton
            class="flex-1 justify-center"
            variant="danger"
            :loading="deletingId === manageRow?.id"
            :disabled="deletingId === manageRow?.id"
            @click="confirmDelete"
          >
            Tak, usuń
          </AppButton>
          <AppButton
            class="flex-1 justify-center"
            variant="secondary"
            :disabled="deletingId === manageRow?.id"
            @click="manageStep = 'menu'"
          >
            Wróć
          </AppButton>
        </div>
      </div>

      <form v-else class="space-y-4" @submit.prevent="handleEditSubmit">
        <FormField label="Nazwa" required>
          <AppInput v-model="editForm.name" required />
        </FormField>
        <FormField label="Rodzaj" required>
          <AppSelect v-model="editForm.type" :options="ACTIVITY_TYPES" required />
        </FormField>
        <FormField label="Cena (zł)">
          <AppInput v-model="editForm.price" type="number" min="0" step="0.01" />
        </FormField>
        <FormField label="Dom seniora" required>
          <AppSelect v-model="editForm.seniorHomeId" :options="homeOptions" required />
        </FormField>
        <FormField label="Opis">
          <AppTextarea v-model="editForm.description" rows="3" />
        </FormField>
        <div class="flex flex-wrap gap-2 pt-1">
          <AppButton type="submit" :disabled="saving">
            {{ saving ? 'Zapisywanie…' : 'Zapisz zmiany' }}
          </AppButton>
          <AppButton type="button" variant="secondary" :disabled="saving" @click="manageStep = 'menu'">
            Wróć
          </AppButton>
          <AppButton type="button" variant="ghost" :disabled="saving" @click="closeManage">
            Anuluj
          </AppButton>
        </div>
      </form>
    </AppModal>

    <section class="space-y-3">
      <div class="flex flex-wrap items-end justify-between gap-4">
        <h2 class="text-lg font-medium text-slate-900">Lista zajęć</h2>
        <FormField label="Filtr: dom seniora" class="min-w-56">
          <AppSelect v-model="filterHomeId" :options="filterOptions" />
        </FormField>
      </div>
      <p v-if="loading" class="text-sm text-slate-500">Ładowanie…</p>
      <DataTable
        v-else
        clickable
        :columns="columns"
        :rows="filteredActivities"
        @row-click="openDetail"
      >
        <template #cell-type="{ row }">
          <AppBadge :variant="typeVariant(row.type)">{{ typeLabel[row.type] }}</AppBadge>
        </template>
        <template v-if="canWrite" #actions="{ row }">
          <AppButton
            variant="secondary"
            size="sm"
            :disabled="saving && !rowBusy(row)"
            :loading="rowBusy(row)"
            @click="openManage(row)"
          >
            Zarządzaj
          </AppButton>
        </template>
      </DataTable>
    </section>
  </div>
</template>
