<script setup>
import { computed, ref, watch } from 'vue'
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'
import DataTable from '@/components/ui/DataTable.vue'
import FormField from '@/components/ui/FormField.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AlertMessage from '@/components/ui/AlertMessage.vue'
import ReadOnlyNotice from '@/components/ui/ReadOnlyNotice.vue'
import { usePermissions } from '@/composables/usePermissions'
import { useHomeScope } from '@/composables/useHomeScope'
import { useSeniorRoomAssignments } from '@/composables/useSeniorRoomAssignments'
import { useSeniors } from '@/composables/useSeniors'
import { useRooms } from '@/composables/useRooms'
import { useEmployees } from '@/composables/useEmployees'

const { canWrite } = usePermissions('assignments')
const { filterItems } = useHomeScope()
const {
  assignments: seniorRoomAssignments,
  loading: loadingSeniorRooms,
  saving: savingSeniorRoom,
  deletingSeniorId,
  error: seniorRoomError,
  assign: assignSeniorRoom,
  unassign: unassignSeniorRoom,
  reload: reloadSeniorRoomAssignments,
} = useSeniorRoomAssignments()
const { seniorList, reload: reloadSeniors } = useSeniors()
const { filteredRooms, reload: reloadRooms } = useRooms()
const {
  employeeList,
  assigning: assigningEmployee,
  error: employeeError,
  assignEmployeesToRoom,
  unassignEmployeeFromRoom,
  assignSeniorToEmployee,
  unassignSeniorFromEmployee,
  reload: reloadEmployees,
} = useEmployees()

const activeTab = ref('senior-room')
const formNotice = ref('')
const seniorRoomForm = ref({ seniorId: '', roomId: '' })
const caregiverForm = ref({ employeeId: '', seniorId: '' })
const employeeRoomForm = ref({ roomId: '', employeeIds: [] })

const error = computed(() => seniorRoomError.value || employeeError.value)
const loading = computed(() => loadingSeniorRooms.value)

const tabs = [
  { id: 'senior-room', label: 'Senior → pokój' },
  { id: 'caregiver', label: 'Opiekun → senior' },
  { id: 'employee-room', label: 'Pracownik → pomieszczenie' },
]

const activeSeniors = computed(() =>
  seniorList.value.filter((s) => s.status === 'ACTIVE' && !s.dischargeDate),
)

const scopedActiveSeniors = computed(() =>
  filterItems(activeSeniors.value, { homeIdKey: 'seniorHomeId' }),
)

const residentialRooms = computed(() =>
  filteredRooms.value.filter((r) => r.inUse && r.type === 'RESIDENTIAL'),
)

const scopedResidentialRooms = computed(() =>
  filterItems(residentialRooms.value, { homeIdKey: 'seniorHomeId' }),
)

const scopedEmployees = computed(() =>
  filterItems(employeeList.value, { homeIdKey: 'seniorHomeId' }).filter((e) => !e.terminatedAt),
)

const seniorOptions = computed(() =>
  scopedActiveSeniors.value.map((s) => ({
    value: s.id,
    label: `${s.firstName} ${s.lastName}${s.roomNumber && s.roomNumber !== '—' ? ` (pokój ${s.roomNumber})` : ''}`,
  })),
)

const selectedSenior = computed(() =>
  scopedActiveSeniors.value.find((s) => String(s.id) === String(seniorRoomForm.value.seniorId)) ?? null,
)

const roomOptionsForSeniorForm = computed(() => {
  const senior = selectedSenior.value
  const rooms = senior
    ? scopedResidentialRooms.value.filter(
        (r) => String(r.seniorHomeId) === String(senior.seniorHomeId),
      )
    : scopedResidentialRooms.value
  return rooms.map((r) => ({
    value: r.id,
    label: `${r.number} — ${r.seniorHome} (${r.assignedSeniors ?? 0}/${r.capacity} miejsc)`,
  }))
})

const employeeOptionsForCaregiverForm = computed(() =>
  scopedEmployees.value.map((e) => ({
    value: e.id,
    label: `${e.firstName} ${e.lastName} — ${e.position}`,
  })),
)

const seniorOptionsForCaregiverForm = computed(() => {
  const employee = scopedEmployees.value.find(
    (e) => String(e.id) === String(caregiverForm.value.employeeId),
  )
  if (!employee) return []
  return scopedActiveSeniors.value
    .filter((s) => String(s.seniorHomeId) === String(employee.seniorHomeId))
    .filter((s) => !(employee.seniorIds ?? []).includes(s.id))
    .map((s) => ({
      value: s.id,
      label: `${s.firstName} ${s.lastName}`,
    }))
})

const scopedInUseRooms = computed(() =>
  filterItems(
    filteredRooms.value.filter((r) => r.inUse),
    { homeIdKey: 'seniorHomeId' },
  ),
)

const roomOptionsForEmployeeForm = computed(() =>
  scopedInUseRooms.value.map((r) => ({
    value: r.id,
    label: `${r.number} — ${r.seniorHome}`,
  })),
)

const selectedEmployeeRoom = computed(() =>
  scopedInUseRooms.value.find(
    (r) => String(r.id) === String(employeeRoomForm.value.roomId),
  ) ?? null,
)

const employeeOptionsForRoomForm = computed(() => {
  const room = selectedEmployeeRoom.value
  if (!room) return []
  return scopedEmployees.value
    .filter((e) => String(e.seniorHomeId) === String(room.seniorHomeId))
    .filter((e) => !(e.roomIds ?? []).some((roomId) => String(roomId) === String(room.id)))
    .map((e) => ({
      value: e.id,
      label: `${e.firstName} ${e.lastName} — ${e.position}`,
    }))
})

const seniorRoomRows = computed(() =>
  filterItems(seniorRoomAssignments.value, { homeIdKey: 'seniorHomeId' }),
)

const caregiverRows = computed(() => {
  const rows = []
  const seniorsById = new Map(scopedActiveSeniors.value.map((s) => [s.id, s]))
  for (const employee of scopedEmployees.value) {
    for (const seniorId of employee.seniorIds ?? []) {
      const senior = seniorsById.get(seniorId)
      rows.push({
        id: `${employee.id}-${seniorId}`,
        employeeId: employee.id,
        seniorId,
        label: 'Opiekun → senior',
        from: `${employee.firstName} ${employee.lastName}`,
        to: senior ? `${senior.firstName} ${senior.lastName}` : `Senior #${seniorId}`,
        seniorHomeId: employee.seniorHomeId,
        seniorHome: employee.seniorHome,
      })
    }
  }
  return rows
})

const employeeRoomRows = computed(() => {
  const rows = []
  for (const employee of scopedEmployees.value) {
    for (const roomId of employee.roomIds ?? []) {
      const room = scopedInUseRooms.value.find((r) => String(r.id) === String(roomId))
      rows.push({
        id: `${employee.id}-${roomId}`,
        employeeId: employee.id,
        roomId,
        label: 'Pracownik → pomieszczenie',
        from: `${employee.firstName} ${employee.lastName}`,
        to: room ? `Pokój ${room.number}` : `Pokój #${roomId}`,
        seniorHomeId: employee.seniorHomeId,
        seniorHome: employee.seniorHome,
      })
    }
  }
  return rows
})

const listColumns = [
  { key: 'label', label: 'Typ' },
  { key: 'from', label: 'Od' },
  { key: 'to', label: 'Do' },
  { key: 'seniorHome', label: 'Dom seniora' },
]

const rules = [
  'Przypisania muszą dotyczyć tego samego domu seniora.',
  'Senior tylko do pokoju mieszkalnego; zajęcia tylko do pomieszczenia w użyciu.',
  'Brak nowych przypisań dla zwolnionych pracowników i seniorów z datą śmierci.',
  'Liczba aktywnych seniorów w pokoju nie może przekroczyć liczby miejsc.',
]

watch(
  () => seniorRoomForm.value.seniorId,
  () => {
    seniorRoomForm.value.roomId = ''
  },
)

watch(
  () => caregiverForm.value.employeeId,
  () => {
    caregiverForm.value.seniorId = ''
  },
)

watch(
  () => employeeRoomForm.value.roomId,
  () => {
    employeeRoomForm.value.employeeIds = []
  },
)

async function refreshAll() {
  await Promise.all([
    reloadSeniorRoomAssignments(),
    reloadSeniors(),
    reloadRooms(),
    reloadEmployees(),
  ])
}

async function handleSeniorRoomAssign() {
  try {
    await assignSeniorRoom(seniorRoomForm.value.seniorId, seniorRoomForm.value.roomId)
    await refreshAll()
    formNotice.value = 'Senior przypisany do pokoju.'
    seniorRoomForm.value = { seniorId: '', roomId: '' }
  } catch {
    formNotice.value = ''
  }
}

async function handleSeniorRoomUnassign(row) {
  try {
    await unassignSeniorRoom(row.seniorId)
    await refreshAll()
    formNotice.value = 'Przypisanie seniora do pokoju zostało usunięte.'
  } catch {
    formNotice.value = ''
  }
}

async function handleCaregiverAssign() {
  try {
    await assignSeniorToEmployee(caregiverForm.value.employeeId, caregiverForm.value.seniorId)
    await refreshAll()
    formNotice.value = 'Opiekun przypisany do seniora.'
    caregiverForm.value = { employeeId: '', seniorId: '' }
  } catch {
    formNotice.value = ''
  }
}

async function handleCaregiverUnassign(row) {
  try {
    await unassignSeniorFromEmployee(row.employeeId, row.seniorId)
    await refreshAll()
    formNotice.value = 'Przypisanie opiekuna zostało usunięte.'
  } catch {
    formNotice.value = ''
  }
}

async function handleEmployeeRoomAssign() {
  try {
    await assignEmployeesToRoom(employeeRoomForm.value.employeeIds, employeeRoomForm.value.roomId)
    await refreshAll()
    formNotice.value = 'Pracownicy przypisani do pomieszczenia.'
    employeeRoomForm.value = { roomId: '', employeeIds: [] }
  } catch {
    formNotice.value = ''
  }
}

async function handleEmployeeRoomUnassign(row) {
  try {
    await unassignEmployeeFromRoom(row.employeeId, row.roomId)
    await refreshAll()
    formNotice.value = 'Przypisanie pracownika do pomieszczenia zostało usunięte.'
  } catch {
    formNotice.value = ''
  }
}
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Przypisania"
      description="Jedyne miejsce do tworzenia i usuwania powiązań: senior–pokój, opiekun–senior oraz pracownik–pomieszczenie."
    />

    <ReadOnlyNotice v-if="!canWrite" />

    <AlertMessage v-if="error" variant="error">{{ error }}</AlertMessage>
    <AlertMessage v-if="formNotice" variant="success">{{ formNotice }}</AlertMessage>

    <AlertMessage variant="info">
      <ul class="list-inside list-disc space-y-1">
        <li v-for="rule in rules" :key="rule">{{ rule }}</li>
      </ul>
    </AlertMessage>

    <div class="flex flex-wrap gap-2 border-b border-slate-200 pb-1">
      <button
        v-for="tab in tabs"
        :key="tab.id"
        type="button"
        class="rounded-t-lg px-4 py-2 text-sm font-medium transition-colors"
        :class="
          activeTab === tab.id
            ? 'border-b-2 border-emerald-600 text-emerald-700'
            : 'text-slate-600 hover:text-slate-900'
        "
        @click="activeTab = tab.id"
      >
        {{ tab.label }}
      </button>
    </div>

    <template v-if="activeTab === 'senior-room'">
      <AppCard v-if="canWrite" title="Przypisz seniora do pokoju">
        <form class="grid gap-4 sm:grid-cols-2" @submit.prevent="handleSeniorRoomAssign">
          <FormField label="Senior" required>
            <AppSelect v-model="seniorRoomForm.seniorId" :options="seniorOptions" required />
          </FormField>
          <FormField label="Pokój mieszkalny" required>
            <AppSelect
              v-model="seniorRoomForm.roomId"
              :options="roomOptionsForSeniorForm"
              :disabled="!seniorRoomForm.seniorId"
              required
            />
          </FormField>
          <div class="sm:col-span-2">
            <AppButton type="submit" :disabled="savingSeniorRoom || loading">
              {{ savingSeniorRoom ? 'Zapisywanie…' : 'Przypisz' }}
            </AppButton>
          </div>
        </form>
      </AppCard>

      <AppCard title="Aktywne przypisania senior → pokój">
        <p v-if="loading" class="mb-3 text-sm text-slate-500">Ładowanie…</p>
        <DataTable v-else :columns="listColumns" :rows="seniorRoomRows" row-key="seniorId">
          <template #cell-label="{ row }">
            <AppBadge variant="neutral">{{ row.label }}</AppBadge>
          </template>
          <template v-if="canWrite" #actions="{ row }">
            <AppButton
              variant="danger"
              size="sm"
              :loading="deletingSeniorId === row.seniorId"
              :disabled="deletingSeniorId === row.seniorId"
              @click="handleSeniorRoomUnassign(row)"
            >
              Usuń
            </AppButton>
          </template>
        </DataTable>
      </AppCard>
    </template>

    <template v-else-if="activeTab === 'caregiver'">
      <AppCard v-if="canWrite" title="Przypisz opiekuna do seniora">
        <form class="grid gap-4 sm:grid-cols-2" @submit.prevent="handleCaregiverAssign">
          <FormField label="Pracownik (opiekun)" required>
            <AppSelect v-model="caregiverForm.employeeId" :options="employeeOptionsForCaregiverForm" required />
          </FormField>
          <FormField label="Senior" required>
            <AppSelect
              v-model="caregiverForm.seniorId"
              :options="seniorOptionsForCaregiverForm"
              :disabled="!caregiverForm.employeeId"
              required
            />
          </FormField>
          <div class="sm:col-span-2">
            <AppButton
              type="submit"
              :disabled="assigningEmployee || !caregiverForm.employeeId || !caregiverForm.seniorId"
            >
              {{ assigningEmployee ? 'Zapisywanie…' : 'Przypisz' }}
            </AppButton>
          </div>
        </form>
      </AppCard>

      <AppCard title="Aktywne przypisania opiekun → senior">
        <DataTable :columns="listColumns" :rows="caregiverRows" row-key="id">
          <template #cell-label="{ row }">
            <AppBadge variant="neutral">{{ row.label }}</AppBadge>
          </template>
          <template v-if="canWrite" #actions="{ row }">
            <AppButton variant="danger" size="sm" :disabled="assigningEmployee" @click="handleCaregiverUnassign(row)">
              Usuń
            </AppButton>
          </template>
        </DataTable>
      </AppCard>
    </template>

    <template v-else>
      <AppCard v-if="canWrite" title="Przypisz pracownika do pomieszczenia">
        <form class="space-y-4" @submit.prevent="handleEmployeeRoomAssign">
          <FormField label="Pomieszczenie" required>
            <AppSelect v-model="employeeRoomForm.roomId" :options="roomOptionsForEmployeeForm" required />
          </FormField>
          <div class="space-y-2">
            <p class="text-sm font-medium text-slate-700">Pracownicy</p>
            <div
              v-if="employeeOptionsForRoomForm.length > 0"
              class="max-h-64 space-y-2 overflow-auto rounded-lg border border-slate-200 p-3"
            >
              <label
                v-for="employee in employeeOptionsForRoomForm"
                :key="employee.value"
                class="flex items-center gap-3 rounded-md px-2 py-1.5 text-sm text-slate-700 hover:bg-slate-50"
              >
                <input
                  v-model="employeeRoomForm.employeeIds"
                  type="checkbox"
                  :value="employee.value"
                  :disabled="assigningEmployee"
                  class="h-4 w-4 rounded border-slate-300 text-emerald-600 focus:ring-emerald-500"
                />
                <span>{{ employee.label }}</span>
              </label>
            </div>
            <p v-else class="text-sm text-slate-500">
              {{
                employeeRoomForm.roomId
                  ? 'Brak pracowników z tego domu do przypisania.'
                  : 'Najpierw wybierz pomieszczenie.'
              }}
            </p>
          </div>
          <AppButton
            type="submit"
            :disabled="
              assigningEmployee ||
              !employeeRoomForm.roomId ||
              employeeRoomForm.employeeIds.length === 0
            "
          >
            {{ assigningEmployee ? 'Przypisywanie…' : 'Przypisz pracowników' }}
          </AppButton>
        </form>
      </AppCard>

      <AppCard title="Aktywne przypisania pracownik → pomieszczenie">
        <DataTable :columns="listColumns" :rows="employeeRoomRows" row-key="id">
          <template #cell-label="{ row }">
            <AppBadge variant="neutral">{{ row.label }}</AppBadge>
          </template>
          <template v-if="canWrite" #actions="{ row }">
            <AppButton variant="danger" size="sm" :disabled="assigningEmployee" @click="handleEmployeeRoomUnassign(row)">
              Usuń
            </AppButton>
          </template>
        </DataTable>
      </AppCard>
    </template>
  </div>
</template>
