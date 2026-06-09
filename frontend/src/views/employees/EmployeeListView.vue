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
import { emptyEmployeeForm, useEmployees } from '@/composables/useEmployees'
const { canWrite } = usePermissions('employees')
const auth = useAuthStore()
const { filterItems, scopedHomeSelectOptions } = useHomeScope()
const {
  employeeList: employeesByScope,
  seniorHomes,
  positions,
  loading,
  saving,
  error,
  addEmployee,
  editEmployee,
} = useEmployees()

const employeeList = computed(() => filterItems(employeesByScope.value, { homeIdKey: 'seniorHomeId' }))
const homeOptions = computed(() => scopedHomeSelectOptions(seniorHomes.value))
const positionOptions = computed(() => positions.value.map((p) => ({ value: p.id, label: p.name })))

const columns = [
  { key: 'lastName', label: 'Nazwisko' },
  { key: 'firstName', label: 'Imię' },
  { key: 'position', label: 'Stanowisko' },
  { key: 'seniorHome', label: 'Dom seniora' },
  { key: 'phone', label: 'Telefon' },
  {
    key: 'isMedical',
    label: 'Medyczny',
    format: (row) => (row.isMedical ? 'Tak' : 'Nie'),
  },
]

const form = ref(emptyEmployeeForm())
const editForm = ref(emptyEmployeeForm())
const formNotice = ref('')
const detailRow = ref(null)
const detailStep = ref('details')
const detailOpen = computed(() => detailRow.value !== null)

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
    await addEmployee(form.value)
    formNotice.value = 'Pracownik zapisany w bazie danych.'
    resetForm()
  } catch {
    formNotice.value = ''
  }
}

function resetForm() {
  form.value = {
    ...emptyEmployeeForm(),
    seniorHomeId: auth.isHomeScoped ? auth.assignedSeniorHomeId : '',
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

function rowToForm(row) {
  return {
    firstName: row.firstName ?? '',
    lastName: row.lastName ?? '',
    seniorHomeId: row.seniorHomeId ?? '',
    positionId: row.positionId ?? '',
    managerId: row.managerId ?? '',
    salary: row.salary ?? 0,
    phone: row.phone ?? '',
    email: row.email ?? '',
    birthDate: row.birthDate ?? '',
    hiredAt: row.hiredAt ?? '',
    terminatedAt: row.terminatedAt ?? '',
    city: row.city ?? '',
    street: row.street ?? '',
    number: row.number ?? '',
    postalCode: row.postalCode ?? '',
    postOfficeCity: row.postOfficeCity ?? row.city ?? '',
    isMedical: row.isMedical ? 'true' : 'false',
    licenseNumber: row.licenseNumber ?? '',
    specialization: row.specialization ?? '',
  }
}

function showEditForm() {
  if (!detailRow.value) return
  editForm.value = rowToForm(detailRow.value)
  detailStep.value = 'edit'
}

async function handleEditSubmit() {
  if (!detailRow.value) return

  try {
    await editEmployee(detailRow.value.id, editForm.value)
    formNotice.value = 'Dane pracownika zaktualizowane.'
    closeDetail()
  } catch {
    formNotice.value = ''
  }
}
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Pracownicy"
      description="Personel domu seniora — stanowisko, adres, kierownik oraz oznaczenie pracownika medycznego."
    />

    <AlertMessage v-if="error" variant="error">{{ error }}</AlertMessage>
    <AlertMessage v-if="formNotice" variant="success">{{ formNotice }}</AlertMessage>

    <ReadOnlyNotice v-if="!canWrite" />

    <AppCard v-if="canWrite" title="Dodaj pracownika">
      <form class="grid gap-4 sm:grid-cols-2" @submit.prevent="handleSubmit">
        <FormField label="Imię" required>
          <AppInput v-model="form.firstName" required />
        </FormField>
        <FormField label="Nazwisko" required>
          <AppInput v-model="form.lastName" required />
        </FormField>
        <FormField label="Dom seniora" required>
          <AppSelect
            v-model="form.seniorHomeId"
            :options="homeOptions"
            :disabled="auth.isHomeScoped"
            required
          />
        </FormField>
        <FormField label="Stanowisko" required>
          <AppSelect v-model="form.positionId" :options="positionOptions" required />
        </FormField>
        <FormField label="Wynagrodzenie" required>
          <AppInput v-model="form.salary" type="number" min="0" step="0.01" required />
        </FormField>
        <FormField label="Telefon" required>
          <AppInput v-model="form.phone" type="tel" required />
        </FormField>
        <FormField label="E-mail">
          <AppInput v-model="form.email" type="email" />
        </FormField>
        <FormField label="Data urodzenia" required>
          <AppInput v-model="form.birthDate" type="date" required />
        </FormField>
        <FormField label="Data zatrudnienia" required>
          <AppInput v-model="form.hiredAt" type="date" required />
        </FormField>
        <FormField label="Miasto" required>
          <AppInput v-model="form.city" required />
        </FormField>
        <FormField label="Ulica" required>
          <AppInput v-model="form.street" required />
        </FormField>
        <FormField label="Numer" required>
          <AppInput v-model="form.number" maxlength="4" required />
        </FormField>
        <FormField label="Kod pocztowy" required>
          <AppInput v-model="form.postalCode" maxlength="6" required />
        </FormField>
        <FormField label="Poczta" required>
          <AppInput v-model="form.postOfficeCity" required />
        </FormField>
        <FormField label="Pracownik medyczny">
          <AppSelect
            v-model="form.isMedical"
            :options="[
              { value: 'false', label: 'Nie' },
              { value: 'true', label: 'Tak' },
            ]"
          />
        </FormField>
        <template v-if="form.isMedical === 'true'">
          <FormField label="Numer licencji" required>
            <AppInput v-model="form.licenseNumber" maxlength="20" required />
          </FormField>
          <FormField label="Specjalizacja" required>
            <AppInput v-model="form.specialization" maxlength="30" required />
          </FormField>
        </template>
        <div class="sm:col-span-2">
          <AppButton type="submit" :disabled="saving">
            {{ saving ? 'Zapisywanie…' : 'Zapisz' }}
          </AppButton>
        </div>
      </form>
    </AppCard>

    <section class="space-y-3">
      <h2 class="text-lg font-medium text-slate-900">Lista pracowników</h2>
      <p v-if="loading" class="text-sm text-slate-500">Ładowanie…</p>
      <DataTable
        v-else
        clickable
        :columns="columns"
        :rows="employeeList"
        @row-click="openDetail"
      >
        <template #cell-isMedical="{ row }">
          <AppBadge :variant="row.isMedical ? 'info' : 'neutral'">
            {{ row.isMedical ? 'Medyczny' : 'Standard' }}
          </AppBadge>
        </template>
      </DataTable>
    </section>

    <AppModal :open="detailOpen" :title="detailRow ? `${detailRow.firstName} ${detailRow.lastName}` : ''" @close="closeDetail">
      <dl v-if="detailRow && detailStep === 'details'" class="space-y-3 text-sm">
        <div>
          <dt class="font-medium text-slate-700">Stanowisko</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.position || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Dom seniora</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.seniorHome || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Wynagrodzenie</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.salary ?? '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Telefon</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.phone || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">E-mail</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.email || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Data urodzenia</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.birthDate || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Data zatrudnienia</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.hiredAt || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Data zwolnienia</dt>
          <dd class="mt-0.5 text-slate-800">{{ detailRow.terminatedAt || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Adres</dt>
          <dd class="mt-0.5 text-slate-800">
            {{ [detailRow.city, detailRow.street, detailRow.number].filter(Boolean).join(', ') || '—' }}
          </dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Pracownik medyczny</dt>
          <dd class="mt-0.5">
            <AppBadge :variant="detailRow.isMedical ? 'info' : 'neutral'">
              {{ detailRow.isMedical ? 'Medyczny' : 'Standard' }}
            </AppBadge>
          </dd>
        </div>
        <div v-if="detailRow.isMedical">
          <dt class="font-medium text-slate-700">Licencja i specjalizacja</dt>
          <dd class="mt-0.5 text-slate-800">
            {{ [detailRow.licenseNumber, detailRow.specialization].filter(Boolean).join(', ') || '—' }}
          </dd>
        </div>
      </dl>

      <form
        v-else-if="detailRow"
        class="grid gap-4 sm:grid-cols-2"
        @submit.prevent="handleEditSubmit"
      >
        <FormField label="Imię" required>
          <AppInput v-model="editForm.firstName" required />
        </FormField>
        <FormField label="Nazwisko" required>
          <AppInput v-model="editForm.lastName" required />
        </FormField>
        <FormField label="Dom seniora" required>
          <AppSelect
            v-model="editForm.seniorHomeId"
            :options="homeOptions"
            :disabled="auth.isHomeScoped"
            required
          />
        </FormField>
        <FormField label="Stanowisko" required>
          <AppSelect v-model="editForm.positionId" :options="positionOptions" required />
        </FormField>
        <FormField label="Wynagrodzenie" required>
          <AppInput v-model="editForm.salary" type="number" min="0" step="0.01" required />
        </FormField>
        <FormField label="Telefon">
          <AppInput v-model="editForm.phone" type="tel" />
        </FormField>
        <FormField label="E-mail">
          <AppInput v-model="editForm.email" type="email" />
        </FormField>
        <FormField label="Data urodzenia" required>
          <AppInput v-model="editForm.birthDate" type="date" required />
        </FormField>
        <FormField label="Data zatrudnienia" required>
          <AppInput v-model="editForm.hiredAt" type="date" required />
        </FormField>
        <FormField label="Data zwolnienia">
          <AppInput v-model="editForm.terminatedAt" type="date" />
        </FormField>
        <FormField label="Miasto" required>
          <AppInput v-model="editForm.city" required />
        </FormField>
        <FormField label="Ulica" required>
          <AppInput v-model="editForm.street" required />
        </FormField>
        <FormField label="Numer" required>
          <AppInput v-model="editForm.number" maxlength="4" required />
        </FormField>
        <FormField label="Kod pocztowy" required>
          <AppInput v-model="editForm.postalCode" maxlength="6" required />
        </FormField>
        <FormField label="Poczta" required>
          <AppInput v-model="editForm.postOfficeCity" required />
        </FormField>
        <FormField label="Pracownik medyczny" class="sm:col-span-2">
          <AppSelect
            v-model="editForm.isMedical"
            :options="[
              { value: 'false', label: 'Nie' },
              { value: 'true', label: 'Tak' },
            ]"
          />
        </FormField>
        <template v-if="editForm.isMedical === 'true'">
          <FormField label="Numer licencji" required>
            <AppInput v-model="editForm.licenseNumber" maxlength="20" required />
          </FormField>
          <FormField label="Specjalizacja" required>
            <AppInput v-model="editForm.specialization" maxlength="30" required />
          </FormField>
        </template>
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
