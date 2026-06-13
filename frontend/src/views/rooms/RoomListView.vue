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
import { useHomeScope } from '@/composables/useHomeScope'
import { useAuthStore } from '@/stores/auth'
import { emptyRoomForm, useRooms } from '@/composables/useRooms'
import { useSeniors } from '@/composables/useSeniors'
import {
  GAME_ROOM_EQUIPMENT_TYPES,
  ROOM_STANDARD_LABELS,
  ROOM_STANDARDS,
  ROOM_TYPES,
} from '@/constants/enums'

const { canWrite } = usePermissions('rooms')
const auth = useAuthStore()
const { filterItems, scopedHomeSelectOptions } = useHomeScope()
const {
  filteredRooms: roomsByHomeFilter,
  seniorHomes,
  filterHomeId,
  loading,
  saving,
  error,
  deletingId,
  addRoom,
  editRoom,
  removeRoom,
  seniorHomeIdForRow,
} = useRooms()
const { seniorList } = useSeniors()

const homeOptions = computed(() => scopedHomeSelectOptions(seniorHomes.value))
const roomList = computed(() => filterItems(roomsByHomeFilter.value))

const filterOptions = computed(() => [
  { value: '', label: 'Wszystkie domy' },
  ...scopedHomeSelectOptions(seniorHomes.value),
])

const typeLabel = Object.fromEntries(ROOM_TYPES.map((t) => [t.value, t.label]))
const equipmentLabel = Object.fromEntries(
  GAME_ROOM_EQUIPMENT_TYPES.map((t) => [t.value, t.label]),
)

const columns = [
  { key: 'number', label: 'Numer' },
  { key: 'floor', label: 'Piętro' },
  { key: 'seniorHome', label: 'Dom seniora' },
  {
    key: 'inUse',
    label: 'W użyciu',
    format: (row) => (row.inUse ? 'Tak' : 'Nie'),
  },
  {
    key: 'type',
    label: 'Typ',
    format: (row) => typeLabel[row.type] ?? row.type ?? '—',
  },
  {
    key: 'details',
    label: 'Szczegóły',
    format: (row) => formatDetails(row),
  },
]

const form = ref(emptyRoomForm())
const editForm = ref(emptyRoomForm())
const formNotice = ref('')
const manageRow = ref(null)
const manageStep = ref('menu')
const detailRow = ref(null)

const manageOpen = computed(() => manageRow.value !== null)
const detailOpen = computed(() => detailRow.value !== null)

const modalTitle = computed(() => {
  if (!manageRow.value) return ''
  if (manageStep.value === 'edit') return `Edytuj: ${manageRow.value.number}`
  if (manageStep.value === 'delete') return 'Usuń pomieszczenie'
  return manageRow.value.number
})

const inUseOptions = [
  { value: 'true', label: 'Tak' },
  { value: 'false', label: 'Nie' },
]

watch(manageOpen, (open) => {
  if (!open) manageStep.value = 'menu'
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

function formatDetails(row) {
  if (row.type === 'RESIDENTIAL') {
    return `${row.assignedSeniors}/${row.capacity} miejsc, ${
      ROOM_STANDARD_LABELS[row.standard] ?? row.standard ?? '—'
    }`
  }
  if (row.type === 'OFFICE') {
    return row.officeKind || '—'
  }
  if (row.type === 'GAME_ROOM') {
    if (!row.gameEquipment?.length) return '—'
    return row.gameEquipment
      .map((item) => `${equipmentLabel[item.type] ?? item.type}: ${item.quantity}`)
      .join(', ')
  }
  return '—'
}

function residentsForRoom(roomId) {
  return seniorList.value.filter((s) => String(s.roomId) === String(roomId))
}

function rowToForm(row) {
  return {
    roomNumber: row.number,
    floor: row.floor,
    inUse: row.inUse ? 'true' : 'false',
    seniorHomeId: seniorHomeIdForRow(row),
    type: row.type ?? '',
    capacity: row.capacity ?? 2,
    standard: row.standard ?? '',
    officeKind: row.officeKind ?? '',
    equipmentDescription: row.equipment ?? '',
    gameEquipment: (row.gameEquipment ?? []).map((item) => ({
      type: item.type,
      quantity: item.quantity,
    })),
  }
}

function addEquipmentItem(target) {
  target.gameEquipment.push({ type: '', quantity: 1 })
}

function removeEquipmentItem(target, index) {
  target.gameEquipment.splice(index, 1)
}

async function handleSubmit() {
  try {
    await addRoom(form.value)
    formNotice.value = 'Pomieszczenie zapisane w bazie danych.'
    form.value = {
      ...emptyRoomForm(),
      seniorHomeId: auth.isHomeScoped ? auth.assignedSeniorHomeId : '',
    }
  } catch {
    formNotice.value = ''
  }
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
  editForm.value = rowToForm(row)
  manageStep.value = 'edit'
}

async function handleEditSubmit() {
  if (!manageRow.value) return
  try {
    await editRoom(manageRow.value.id, editForm.value)
    formNotice.value = 'Zmiany zapisane w bazie danych.'
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
    await removeRoom(row.id)
    formNotice.value = 'Pomieszczenie usunięte z bazy danych.'
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
      title="Pomieszczenia"
      description="Pokoje mieszkalne, gabinety i sale gier — z limitami miejsc i wyposażeniem. Przypisania seniorów i pracowników edytujesz w zakładce Przypisania."
    />

    <AlertMessage v-if="error" variant="error">{{ error }}</AlertMessage>
    <AlertMessage v-if="formNotice" variant="success">{{ formNotice }}</AlertMessage>

    <ReadOnlyNotice v-if="!canWrite">
      Pomieszczenia przeglądasz w trybie podglądu — dodawanie i edycja dla menadżera lub dyrektora.
    </ReadOnlyNotice>

    <AppCard v-if="canWrite" title="Dodaj pomieszczenie">
      <form class="grid gap-4 sm:grid-cols-2" @submit.prevent="handleSubmit">
        <FormField label="Numer" required>
          <AppInput v-model="form.roomNumber" required />
        </FormField>
        <FormField label="Piętro" required>
          <AppInput v-model="form.floor" type="number" required />
        </FormField>
        <FormField label="Dom seniora" required>
          <AppSelect
            v-model="form.seniorHomeId"
            :options="homeOptions"
            :disabled="auth.isHomeScoped"
            required
          />
        </FormField>
        <FormField label="W użyciu" required>
          <AppSelect v-model="form.inUse" :options="inUseOptions" required />
        </FormField>
        <FormField v-if="form.inUse === 'true'" label="Typ" required class="sm:col-span-2">
          <AppSelect v-model="form.type" :options="ROOM_TYPES" required />
        </FormField>

        <template v-if="form.inUse === 'true' && form.type === 'RESIDENTIAL'">
          <FormField label="Liczba miejsc" required>
            <AppInput v-model="form.capacity" type="number" min="1" required />
          </FormField>
          <FormField label="Standard" required>
            <AppSelect v-model="form.standard" :options="ROOM_STANDARDS" required />
          </FormField>
        </template>

        <template v-if="form.inUse === 'true' && form.type === 'OFFICE'">
          <FormField label="Rodzaj gabinetu" required>
            <AppInput v-model="form.officeKind" required />
          </FormField>
          <FormField label="Sprzęt">
            <AppInput v-model="form.equipmentDescription" />
          </FormField>
        </template>

        <div v-if="form.inUse === 'true' && form.type === 'GAME_ROOM'" class="space-y-3 sm:col-span-2">
          <div class="flex items-center justify-between gap-2">
            <p class="text-sm font-medium text-slate-700">Wyposażenie sali gier</p>
            <AppButton type="button" size="sm" variant="secondary" @click="addEquipmentItem(form)">
              Dodaj element
            </AppButton>
          </div>
          <div
            v-for="(item, index) in form.gameEquipment"
            :key="index"
            class="grid gap-3 rounded-lg border border-slate-200 p-3 sm:grid-cols-[1fr_120px_auto]"
          >
            <FormField label="Typ">
              <AppSelect v-model="item.type" :options="GAME_ROOM_EQUIPMENT_TYPES" />
            </FormField>
            <FormField label="Ilość">
              <AppInput v-model="item.quantity" type="number" min="1" />
            </FormField>
            <div class="flex items-end">
              <AppButton
                type="button"
                size="sm"
                variant="ghost"
                @click="removeEquipmentItem(form, index)"
              >
                Usuń
              </AppButton>
            </div>
          </div>
        </div>

        <div class="sm:col-span-2">
          <AppButton type="submit" :disabled="saving">
            {{ saving ? 'Zapisywanie…' : 'Zapisz' }}
          </AppButton>
        </div>
      </form>
    </AppCard>

    <AppModal :open="detailOpen" :title="detailRow?.number ?? ''" @close="closeDetail">
      <dl v-if="detailRow" class="space-y-3 text-sm">
        <div>
          <dt class="font-medium text-slate-700">Piętro</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.floor }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Dom seniora</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.seniorHome }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">W użyciu</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.inUse ? 'Tak' : 'Nie' }}</dd>
        </div>
        <div v-if="detailRow.type" class="flex flex-wrap items-center gap-2">
          <dt class="font-medium text-slate-700">Typ</dt>
          <dd>
            <AppBadge variant="info">{{ typeLabel[detailRow.type] ?? detailRow.type }}</AppBadge>
          </dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Szczegóły</dt>
          <dd class="mt-0.5 text-slate-800">{{ formatDetails(detailRow) }}</dd>
        </div>
        <div v-if="detailRow.type === 'RESIDENTIAL'">
          <dt class="font-medium text-slate-700">Mieszkańcy</dt>
          <dd class="mt-0.5 text-slate-800">
            {{
              residentsForRoom(detailRow.id)
                .map((s) => `${s.firstName} ${s.lastName}`)
                .join(', ') || '—'
            }}
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
      <div v-if="manageStep === 'menu'" class="space-y-4">
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
          Czy na pewno chcesz usunąć pomieszczenie
          <span class="font-medium text-slate-900">„{{ manageRow?.number }}”</span>?
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
        <FormField label="Numer" required>
          <AppInput v-model="editForm.roomNumber" required />
        </FormField>
        <FormField label="Piętro" required>
          <AppInput v-model="editForm.floor" type="number" required />
        </FormField>
        <FormField label="Dom seniora" required>
          <AppSelect v-model="editForm.seniorHomeId" :options="homeOptions" required />
        </FormField>
        <FormField label="W użyciu" required>
          <AppSelect v-model="editForm.inUse" :options="inUseOptions" required />
        </FormField>
        <FormField v-if="editForm.inUse === 'true'" label="Typ" required>
          <AppSelect v-model="editForm.type" :options="ROOM_TYPES" required />
        </FormField>

        <template v-if="editForm.inUse === 'true' && editForm.type === 'RESIDENTIAL'">
          <FormField label="Liczba miejsc" required>
            <AppInput v-model="editForm.capacity" type="number" min="1" required />
          </FormField>
          <FormField label="Standard" required>
            <AppSelect v-model="editForm.standard" :options="ROOM_STANDARDS" required />
          </FormField>
        </template>

        <template v-if="editForm.inUse === 'true' && editForm.type === 'OFFICE'">
          <FormField label="Rodzaj gabinetu" required>
            <AppInput v-model="editForm.officeKind" required />
          </FormField>
          <FormField label="Sprzęt">
            <AppInput v-model="editForm.equipmentDescription" />
          </FormField>
        </template>

        <div v-if="editForm.inUse === 'true' && editForm.type === 'GAME_ROOM'" class="space-y-3">
          <div class="flex items-center justify-between gap-2">
            <p class="text-sm font-medium text-slate-700">Wyposażenie sali gier</p>
            <AppButton
              type="button"
              size="sm"
              variant="secondary"
              @click="addEquipmentItem(editForm)"
            >
              Dodaj element
            </AppButton>
          </div>
          <div
            v-for="(item, index) in editForm.gameEquipment"
            :key="index"
            class="grid gap-3 rounded-lg border border-slate-200 p-3 sm:grid-cols-[1fr_120px_auto]"
          >
            <FormField label="Typ">
              <AppSelect v-model="item.type" :options="GAME_ROOM_EQUIPMENT_TYPES" />
            </FormField>
            <FormField label="Ilość">
              <AppInput v-model="item.quantity" type="number" min="1" />
            </FormField>
            <div class="flex items-end">
              <AppButton
                type="button"
                size="sm"
                variant="ghost"
                @click="removeEquipmentItem(editForm, index)"
              >
                Usuń
              </AppButton>
            </div>
          </div>
        </div>

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
        <h2 class="text-lg font-medium text-slate-900">Lista pomieszczeń</h2>
        <FormField label="Filtr: dom seniora" class="min-w-56">
          <AppSelect v-model="filterHomeId" :options="filterOptions" />
        </FormField>
      </div>
      <p v-if="loading" class="text-sm text-slate-500">Ładowanie…</p>
      <DataTable
        v-else
        clickable
        :columns="columns"
        :rows="roomList"
        @row-click="openDetail"
      >
        <template #cell-type="{ row }">
          <AppBadge v-if="row.type" variant="info">{{ typeLabel[row.type] }}</AppBadge>
          <span v-else class="text-slate-400">—</span>
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
