<script setup>
import { computed, ref } from 'vue'
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

const { canWrite } = usePermissions('assignments')
const { filterItems } = useHomeScope()
const {
  assignments: seniorRoomAssignments,
  loading,
  saving,
  deletingSeniorId,
  error,
  assign,
  unassign,
} = useSeniorRoomAssignments()
const { seniorList, reload: reloadSeniors } = useSeniors()
const { filteredRooms, reload: reloadRooms } = useRooms()

const formNotice = ref('')
const form = ref({ seniorId: '', roomId: '' })

const activeSeniors = computed(() =>
  seniorList.value.filter((s) => s.status === 'ACTIVE' && !s.dischargeDate),
)

const seniorOptions = computed(() =>
  activeSeniors.value.map((s) => ({
    value: s.id,
    label: `${s.firstName} ${s.lastName}${s.roomNumber && s.roomNumber !== '—' ? ` (pokój ${s.roomNumber})` : ''}`,
  })),
)

const residentialRooms = computed(() =>
  filteredRooms.value.filter((r) => r.inUse && r.type === 'RESIDENTIAL'),
)

const roomOptions = computed(() =>
  residentialRooms.value.map((r) => ({
    value: r.id,
    label: `${r.number} — ${r.seniorHome} (${r.assignedSeniors ?? 0}/${r.capacity} miejsc)`,
  })),
)

const seniorRoomRows = computed(() =>
  filterItems(seniorRoomAssignments.value, { homeIdKey: 'seniorHomeId' }),
)

const columns = [
  { key: 'label', label: 'Typ przypisania' },
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

async function handleAssign() {
  try {
    await assign(form.value.seniorId, form.value.roomId)
    await Promise.all([reloadSeniors(), reloadRooms()])
    formNotice.value = 'Senior przypisany do pokoju.'
    form.value = { seniorId: '', roomId: '' }
  } catch {
    formNotice.value = ''
  }
}

async function handleUnassign(row) {
  try {
    await unassign(row.seniorId)
    await Promise.all([reloadSeniors(), reloadRooms()])
    formNotice.value = 'Przypisanie seniora do pokoju zostało usunięte.'
  } catch {
    formNotice.value = ''
  }
}
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Przypisania"
      description="Powiązania senior–pokój, pracownik–senior, pracownik–pomieszczenie, pracownik–zajęcia, senior–zajęcia, zajęcia–pomieszczenie."
    />

    <ReadOnlyNotice v-if="!canWrite" />

    <AlertMessage v-if="error" variant="error">{{ error }}</AlertMessage>
    <AlertMessage v-if="formNotice" variant="success">{{ formNotice }}</AlertMessage>

    <AlertMessage variant="info">
      <ul class="list-inside list-disc space-y-1">
        <li v-for="rule in rules" :key="rule">{{ rule }}</li>
      </ul>
    </AlertMessage>

    <AppCard v-if="canWrite" title="Przypisz seniora do pokoju">
      <form class="grid gap-4 sm:grid-cols-2" @submit.prevent="handleAssign">
        <FormField label="Senior" required>
          <AppSelect v-model="form.seniorId" :options="seniorOptions" required />
        </FormField>
        <FormField label="Pokój mieszkalny" required>
          <AppSelect v-model="form.roomId" :options="roomOptions" required />
        </FormField>
        <div class="sm:col-span-2">
          <AppButton type="submit" :disabled="saving || loading">
            {{ saving ? 'Zapisywanie…' : 'Przypisz' }}
          </AppButton>
        </div>
      </form>
    </AppCard>

    <AppCard title="Senior → pokój">
      <p v-if="loading" class="mb-3 text-sm text-slate-500">Ładowanie…</p>
      <DataTable :columns="columns" :rows="seniorRoomRows">
        <template #cell-label="{ row }">
          <AppBadge variant="neutral">{{ row.label }}</AppBadge>
        </template>
        <template v-if="canWrite" #actions="{ row }">
          <AppButton
            variant="danger"
            size="sm"
            :loading="deletingSeniorId === row.seniorId"
            :disabled="deletingSeniorId === row.seniorId"
            @click="handleUnassign(row)"
          >
            Usuń
          </AppButton>
        </template>
      </DataTable>
    </AppCard>
  </div>
</template>
