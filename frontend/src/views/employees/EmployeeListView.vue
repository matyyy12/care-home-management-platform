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
import AlertMessage from '@/components/ui/AlertMessage.vue'
import ReadOnlyNotice from '@/components/ui/ReadOnlyNotice.vue'
import { usePermissions } from '@/composables/usePermissions'
import { useHomeScope } from '@/composables/useHomeScope'
import { useAuthStore } from '@/stores/auth'
import { emptyEmployeeForm, useEmployees } from '@/composables/useEmployees'
import { sourceNotice } from '@/composables/apiMode'

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
  usingMock,
  addEmployee,
} = useEmployees()

const employeeList = computed(() => filterItems(employeesByScope.value, { homeIdKey: 'seniorHomeId' }))
const homeOptions = computed(() => scopedHomeSelectOptions(seniorHomes.value))
const positionOptions = computed(() => positions.value.map((p) => ({ value: p.id, label: p.name })))
const dataSourceNotice = computed(() => sourceNotice(usingMock.value))

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
    const result = await addEmployee(form.value)
    formNotice.value = result.savedLocally
      ? 'Pracownik zapisany lokalnie (tryb demo).'
      : 'Pracownik zapisany w bazie danych.'
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
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Pracownicy"
      description="Personel domu seniora — stanowisko, adres, kierownik oraz oznaczenie pracownika medycznego."
    />

    <AlertMessage v-if="dataSourceNotice" variant="info">{{ dataSourceNotice }}</AlertMessage>
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
            {{ saving ? 'Zapisywanie…' : usingMock ? 'Zapisz (lokalnie)' : 'Zapisz' }}
          </AppButton>
        </div>
      </form>
    </AppCard>

    <section class="space-y-3">
      <h2 class="text-lg font-medium text-slate-900">Lista pracowników</h2>
      <p v-if="loading" class="text-sm text-slate-500">Ładowanie…</p>
      <DataTable v-else :columns="columns" :rows="employeeList">
        <template #cell-isMedical="{ row }">
          <AppBadge :variant="row.isMedical ? 'info' : 'neutral'">
            {{ row.isMedical ? 'Medyczny' : 'Standard' }}
          </AppBadge>
        </template>
      </DataTable>
    </section>
  </div>
</template>
