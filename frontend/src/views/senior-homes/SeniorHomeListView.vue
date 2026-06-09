<script setup>
import { computed, ref, watch } from 'vue'
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'
import DataTable from '@/components/ui/DataTable.vue'
import FormField from '@/components/ui/FormField.vue'
import AppInput from '@/components/ui/AppInput.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppModal from '@/components/ui/AppModal.vue'
import AlertMessage from '@/components/ui/AlertMessage.vue'
import ReadOnlyNotice from '@/components/ui/ReadOnlyNotice.vue'
import { usePermissions } from '@/composables/usePermissions'
import { useSeniorHomes } from '@/composables/useSeniorHomes'
import { useOwners } from '@/composables/useOwners'
import { attachOwnersToHomes } from '@/api/mappers/ownerMapper'
const { canWrite } = usePermissions('senior-homes')
const {
  homeList,
  loading: homesLoading,
  saving: homesSaving,
  error: homesError,
  addHome,
  editHome,
} =
  useSeniorHomes()
const {
  ownerList,
  loading: ownersLoading,
  saving: ownersSaving,
  error: ownersError,
  reload: reloadOwners,
  addOwner,
  editOwner,
} = useOwners(() => homeList.value)

watch(homeList, () => {
  reloadOwners()
})

const loading = computed(() => homesLoading.value || ownersLoading.value)
const saving = computed(() => homesSaving.value || ownersSaving.value)
const error = computed(() => homesError.value || ownersError.value)

const homeOptions = computed(() =>
  homeList.value.map((home) => ({ value: home.id, label: home.name })),
)

const displayHomes = computed(() => attachOwnersToHomes(homeList.value, ownerList.value))

const homeColumns = [
  { key: 'name', label: 'Nazwa' },
  { key: 'phone', label: 'Telefon' },
  { key: 'email', label: 'E-mail' },
  { key: 'addressLabel', label: 'Adres' },
  {
    key: 'owners',
    label: 'Właściciele',
    format: (row) => row.owners?.join(', ') ?? '—',
  },
]

const ownerColumns = [
  { key: 'displayName', label: 'Imię i nazwisko' },
  { key: 'seniorHome', label: 'Dom seniora' },
  { key: 'addressLabel', label: 'Adres' },
]

const emptyHomeForm = () => ({
  name: '',
  phone: '',
  email: '',
  website: '',
  foundedAt: new Date().toISOString().slice(0, 10),
  city: '',
  street: '',
  apartmentNumber: '',
  postalCode: '',
  postOfficeCity: '',
})

const emptyOwnerForm = () => ({
  name: '',
  surname: '',
  seniorHomeId: '',
  city: '',
  street: '',
  apartmentNumber: '',
  postalCode: '',
  postOfficeCity: '',
})

const homeForm = ref(emptyHomeForm())
const ownerForm = ref(emptyOwnerForm())
const homeEditForm = ref(emptyHomeForm())
const ownerEditForm = ref(emptyOwnerForm())
const formNotice = ref('')
const homeDetailRow = ref(null)
const homeDetailStep = ref('details')
const ownerDetailRow = ref(null)
const ownerDetailStep = ref('details')
const homeDetailOpen = computed(() => homeDetailRow.value !== null)
const ownerDetailOpen = computed(() => ownerDetailRow.value !== null)

function openHomeDetail(row) {
  homeDetailRow.value = row
  homeDetailStep.value = 'details'
}

function closeHomeDetail() {
  homeDetailRow.value = null
  homeDetailStep.value = 'details'
}

function openOwnerDetail(row) {
  ownerDetailRow.value = row
  ownerDetailStep.value = 'details'
}

function closeOwnerDetail() {
  ownerDetailRow.value = null
  ownerDetailStep.value = 'details'
}

function formatWebsite(url) {
  const value = url?.trim()
  if (!value) return null
  return value.startsWith('http') ? value : `https://${value}`
}

async function handleHomeSubmit() {
  try {
    await addHome(homeForm.value)
    formNotice.value = 'Dom seniora zapisany w bazie danych.'
    homeForm.value = emptyHomeForm()
  } catch {
    formNotice.value = ''
  }
}

async function handleOwnerSubmit() {
  try {
    await addOwner(ownerForm.value)
    formNotice.value = 'Właściciel zapisany w bazie danych.'
    ownerForm.value = emptyOwnerForm()
  } catch {
    formNotice.value = ''
  }
}

function homeRowToForm(row) {
  return {
    name: row.name ?? '',
    phone: row.phone ?? '',
    email: row.email ?? '',
    website: row.website ?? '',
    foundedAt: row.foundedAt ?? '',
    city: row.city ?? '',
    street: row.street ?? '',
    apartmentNumber: row.apartmentNumber ?? '',
    postalCode: row.postalCode ?? '',
    postOfficeCity: row.postOfficeCity ?? row.city ?? '',
  }
}

function ownerRowToForm(row) {
  return {
    name: row.name ?? '',
    surname: row.surname ?? '',
    seniorHomeId: row.seniorHomeId ?? '',
    city: row.city ?? '',
    street: row.street ?? '',
    apartmentNumber: row.apartmentNumber ?? '',
    postalCode: row.postalCode ?? '',
    postOfficeCity: row.postOfficeCity ?? row.city ?? '',
  }
}

function showHomeEditForm() {
  if (!homeDetailRow.value) return
  homeEditForm.value = homeRowToForm(homeDetailRow.value)
  homeDetailStep.value = 'edit'
}

function showOwnerEditForm() {
  if (!ownerDetailRow.value) return
  ownerEditForm.value = ownerRowToForm(ownerDetailRow.value)
  ownerDetailStep.value = 'edit'
}

async function handleHomeEditSubmit() {
  if (!homeDetailRow.value) return

  try {
    await editHome(homeDetailRow.value.id, homeEditForm.value)
    formNotice.value = 'Dom seniora zaktualizowany.'
    closeHomeDetail()
  } catch {
    formNotice.value = ''
  }
}

async function handleOwnerEditSubmit() {
  if (!ownerDetailRow.value) return

  try {
    await editOwner(ownerDetailRow.value.id, ownerEditForm.value)
    formNotice.value = 'Właściciel zaktualizowany.'
    closeOwnerDetail()
  } catch {
    formNotice.value = ''
  }
}
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Domy seniora"
      description="Ewidencja domów seniora: dane kontaktowe, adres i właściciele przypisani do domu."
    />

    <AlertMessage v-if="error" variant="error">{{ error }}</AlertMessage>
    <AlertMessage v-if="formNotice" variant="success">{{ formNotice }}</AlertMessage>

    <ReadOnlyNotice v-if="!canWrite" />

    <AppCard v-if="canWrite" title="Dodaj dom seniora">
      <form class="grid gap-4 sm:grid-cols-2" @submit.prevent="handleHomeSubmit">
        <FormField label="Nazwa" required>
          <AppInput v-model="homeForm.name" required />
        </FormField>
        <FormField label="Telefon" required>
          <AppInput v-model="homeForm.phone" type="tel" required />
        </FormField>
        <FormField label="E-mail">
          <AppInput v-model="homeForm.email" type="email" />
        </FormField>
        <FormField label="Strona WWW">
          <AppInput v-model="homeForm.website" type="url" placeholder="https://…" />
        </FormField>
        <FormField label="Data założenia" required>
          <AppInput v-model="homeForm.foundedAt" type="date" required />
        </FormField>
        <FormField label="Miasto" required>
          <AppInput v-model="homeForm.city" required />
        </FormField>
        <FormField label="Ulica" required>
          <AppInput v-model="homeForm.street" required />
        </FormField>
        <FormField label="Nr lokalu" required>
          <AppInput v-model="homeForm.apartmentNumber" required />
        </FormField>
        <FormField label="Kod pocztowy" required>
          <AppInput v-model="homeForm.postalCode" placeholder="00-000" required />
        </FormField>
        <FormField label="Miejscowość poczty" required>
          <AppInput v-model="homeForm.postOfficeCity" required />
        </FormField>
        <div class="sm:col-span-2">
          <AppButton type="submit" :disabled="saving">
            {{ saving ? 'Zapisywanie…' : 'Zapisz' }}
          </AppButton>
        </div>
      </form>
    </AppCard>

    <section class="space-y-3">
      <h2 class="text-lg font-medium text-slate-900">Lista domów</h2>
      <p v-if="loading" class="text-sm text-slate-500">Ładowanie…</p>
      <DataTable
        v-else
        clickable
        :columns="homeColumns"
        :rows="displayHomes"
        @row-click="openHomeDetail"
      />
    </section>

    <AppCard v-if="canWrite" title="Dodaj właściciela">
      <form class="grid gap-4 sm:grid-cols-2" @submit.prevent="handleOwnerSubmit">
        <FormField label="Imię" required>
          <AppInput v-model="ownerForm.name" required />
        </FormField>
        <FormField label="Nazwisko" required>
          <AppInput v-model="ownerForm.surname" required />
        </FormField>
        <FormField label="Dom seniora" required>
          <AppSelect v-model="ownerForm.seniorHomeId" :options="homeOptions" required />
        </FormField>
        <FormField label="Miasto" required>
          <AppInput v-model="ownerForm.city" required />
        </FormField>
        <FormField label="Ulica" required>
          <AppInput v-model="ownerForm.street" required />
        </FormField>
        <FormField label="Nr lokalu" required>
          <AppInput v-model="ownerForm.apartmentNumber" required />
        </FormField>
        <FormField label="Kod pocztowy" required>
          <AppInput v-model="ownerForm.postalCode" placeholder="00-000" required />
        </FormField>
        <FormField label="Miejscowość poczty" required>
          <AppInput v-model="ownerForm.postOfficeCity" required />
        </FormField>
        <div class="sm:col-span-2">
          <AppButton type="submit" :disabled="saving || homeOptions.length === 0">
            {{ saving ? 'Zapisywanie…' : 'Zapisz' }}
          </AppButton>
        </div>
      </form>
      <p v-if="homeOptions.length === 0" class="mt-2 text-sm text-slate-500">
        Najpierw dodaj dom seniora, aby przypisać właściciela.
      </p>
    </AppCard>

    <section class="space-y-3">
      <h2 class="text-lg font-medium text-slate-900">Lista właścicieli</h2>
      <p v-if="loading" class="text-sm text-slate-500">Ładowanie…</p>
      <DataTable
        v-else
        clickable
        :columns="ownerColumns"
        :rows="ownerList"
        @row-click="openOwnerDetail"
      />
    </section>

    <AppModal :open="homeDetailOpen" :title="homeDetailRow?.name ?? ''" @close="closeHomeDetail">
      <dl v-if="homeDetailRow && homeDetailStep === 'details'" class="space-y-3 text-sm">
        <div>
          <dt class="font-medium text-slate-700">Telefon</dt>
          <dd class="mt-0.5 text-slate-800">{{ homeDetailRow.phone || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">E-mail</dt>
          <dd class="mt-0.5 text-slate-800">{{ homeDetailRow.email || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Strona WWW</dt>
          <dd class="mt-0.5 text-slate-800">
            <a
              v-if="formatWebsite(homeDetailRow.website)"
              :href="formatWebsite(homeDetailRow.website)"
              target="_blank"
              rel="noopener noreferrer"
              class="text-emerald-700 hover:underline"
            >
              {{ homeDetailRow.website }}
            </a>
            <span v-else>—</span>
          </dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Data założenia</dt>
          <dd class="mt-0.5 text-slate-800">{{ homeDetailRow.foundedAt || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Adres</dt>
          <dd class="mt-0.5 text-slate-800">{{ homeDetailRow.addressLabel || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Właściciele</dt>
          <dd class="mt-0.5 text-slate-800">{{ homeDetailRow.owners?.join(', ') || '—' }}</dd>
        </div>
      </dl>

      <form v-else-if="homeDetailRow" class="grid gap-4 sm:grid-cols-2" @submit.prevent="handleHomeEditSubmit">
        <FormField label="Nazwa">
          <AppInput v-model="homeEditForm.name" maxlength="20" />
        </FormField>
        <FormField label="Telefon">
          <AppInput v-model="homeEditForm.phone" type="tel" maxlength="15" />
        </FormField>
        <FormField label="E-mail">
          <AppInput v-model="homeEditForm.email" type="email" maxlength="40" />
        </FormField>
        <FormField label="Strona WWW">
          <AppInput v-model="homeEditForm.website" type="url" maxlength="40" placeholder="https://…" />
        </FormField>
        <FormField label="Data założenia">
          <AppInput v-model="homeEditForm.foundedAt" type="date" />
        </FormField>
        <FormField label="Miasto">
          <AppInput v-model="homeEditForm.city" />
        </FormField>
        <FormField label="Ulica">
          <AppInput v-model="homeEditForm.street" />
        </FormField>
        <FormField label="Nr lokalu">
          <AppInput v-model="homeEditForm.apartmentNumber" />
        </FormField>
        <FormField label="Kod pocztowy">
          <AppInput v-model="homeEditForm.postalCode" placeholder="00-000" />
        </FormField>
        <FormField label="Miejscowość poczty">
          <AppInput v-model="homeEditForm.postOfficeCity" />
        </FormField>
      </form>

      <div class="mt-5 flex flex-wrap gap-2">
        <template v-if="homeDetailStep === 'details'">
          <AppButton
            v-if="canWrite"
            class="flex-1 justify-center"
            variant="secondary"
            @click="showHomeEditForm"
          >
            Modyfikuj
          </AppButton>
          <AppButton class="flex-1 justify-center" variant="ghost" @click="closeHomeDetail">
            Zamknij
          </AppButton>
        </template>
        <template v-else>
          <AppButton class="flex-1 justify-center" :disabled="saving" @click="handleHomeEditSubmit">
            {{ saving ? 'Zapisywanie…' : 'Zapisz zmiany' }}
          </AppButton>
          <AppButton
            class="flex-1 justify-center"
            type="button"
            variant="secondary"
            :disabled="saving"
            @click="homeDetailStep = 'details'"
          >
            Wróć
          </AppButton>
        </template>
      </div>
    </AppModal>

    <AppModal
      :open="ownerDetailOpen"
      :title="ownerDetailRow?.displayName ?? ''"
      @close="closeOwnerDetail"
    >
      <dl v-if="ownerDetailRow && ownerDetailStep === 'details'" class="space-y-3 text-sm">
        <div>
          <dt class="font-medium text-slate-700">Imię</dt>
          <dd class="mt-0.5 text-slate-800">{{ ownerDetailRow.name || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Nazwisko</dt>
          <dd class="mt-0.5 text-slate-800">{{ ownerDetailRow.surname || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Dom seniora</dt>
          <dd class="mt-0.5 text-slate-800">{{ ownerDetailRow.seniorHome || '—' }}</dd>
        </div>
        <div>
          <dt class="font-medium text-slate-700">Adres</dt>
          <dd class="mt-0.5 text-slate-800">{{ ownerDetailRow.addressLabel || '—' }}</dd>
        </div>
      </dl>

      <form v-else-if="ownerDetailRow" class="grid gap-4 sm:grid-cols-2" @submit.prevent="handleOwnerEditSubmit">
        <FormField label="Imię">
          <AppInput v-model="ownerEditForm.name" maxlength="30" />
        </FormField>
        <FormField label="Nazwisko">
          <AppInput v-model="ownerEditForm.surname" maxlength="30" />
        </FormField>
        <FormField label="Dom seniora">
          <AppSelect v-model="ownerEditForm.seniorHomeId" :options="homeOptions" />
        </FormField>
        <FormField label="Miasto">
          <AppInput v-model="ownerEditForm.city" />
        </FormField>
        <FormField label="Ulica">
          <AppInput v-model="ownerEditForm.street" />
        </FormField>
        <FormField label="Nr lokalu">
          <AppInput v-model="ownerEditForm.apartmentNumber" />
        </FormField>
        <FormField label="Kod pocztowy">
          <AppInput v-model="ownerEditForm.postalCode" placeholder="00-000" />
        </FormField>
        <FormField label="Miejscowość poczty">
          <AppInput v-model="ownerEditForm.postOfficeCity" />
        </FormField>
      </form>

      <div class="mt-5 flex flex-wrap gap-2">
        <template v-if="ownerDetailStep === 'details'">
          <AppButton
            v-if="canWrite"
            class="flex-1 justify-center"
            variant="secondary"
            @click="showOwnerEditForm"
          >
            Modyfikuj
          </AppButton>
          <AppButton class="flex-1 justify-center" variant="ghost" @click="closeOwnerDetail">
            Zamknij
          </AppButton>
        </template>
        <template v-else>
          <AppButton class="flex-1 justify-center" :disabled="saving" @click="handleOwnerEditSubmit">
            {{ saving ? 'Zapisywanie…' : 'Zapisz zmiany' }}
          </AppButton>
          <AppButton
            class="flex-1 justify-center"
            type="button"
            variant="secondary"
            :disabled="saving"
            @click="ownerDetailStep = 'details'"
          >
            Wróć
          </AppButton>
        </template>
      </div>
    </AppModal>
  </div>
</template>
