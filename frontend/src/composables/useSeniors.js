import { computed, onMounted, ref } from 'vue'
import { fetchSeniors, createSenior } from '@/api/seniors'
import { fetchSeniorHomes } from '@/api/seniorHomes'
import {
  buildHomeNameMap,
  mapSeniorFromApi,
  mapSeniorFromMock,
} from '@/api/mappers/seniorMapper'
import { apiEnabled } from '@/composables/apiMode'
import { seniors as mockSeniors, seniorHomes as mockSeniorHomes } from '@/mock/data'

function loadMockHomes() {
  return mockSeniorHomes.map((home) => ({ id: home.id, name: home.name }))
}

function loadMockSeniors(homeNames) {
  return mockSeniors.map((record) => mapSeniorFromMock(record, homeNames))
}

export function useSeniors() {
  const seniorList = ref([])
  const seniorHomes = ref([])
  const loading = ref(false)
  const saving = ref(false)
  const error = ref(null)
  const usingMock = ref(!apiEnabled)

  const homeOptions = computed(() =>
    seniorHomes.value.map((home) => ({ value: home.id, label: home.name })),
  )

  function applyMockData() {
    const homes = loadMockHomes()
    seniorHomes.value = homes
    const homeNames = buildHomeNameMap(homes)
    seniorList.value = loadMockSeniors(homeNames)
    usingMock.value = true
  }

  async function reload() {
    loading.value = true
    error.value = null

    if (!apiEnabled) {
      applyMockData()
      loading.value = false
      return
    }

    try {
      const [homes, seniors] = await Promise.all([fetchSeniorHomes(), fetchSeniors()])
      seniorHomes.value = homes
      const homeNames = buildHomeNameMap(homes)
      seniorList.value = seniors.map((dto) => mapSeniorFromApi(dto, homeNames))
      usingMock.value = false
    } catch (e) {
      error.value = e.message ?? 'Nie udało się połączyć z API'
      applyMockData()
    } finally {
      loading.value = false
    }
  }

  async function addSenior(form) {
    if (usingMock.value) {
      const home = seniorHomes.value.find((h) => String(h.id) === String(form.seniorHomeId))
      seniorList.value.push(
        mapSeniorFromMock(
          {
            id: Math.max(0, ...seniorList.value.map((s) => Number(s.id) || 0)) + 1,
            firstName: form.firstName,
            lastName: form.lastName,
            pesel: form.pesel,
            birthDate: form.birthDate,
            gender: form.gender,
            seniorHome: home?.name ?? '—',
            accommodationDate: form.accommodationDate,
            dischargeDate: null,
            status: 'ACTIVE',
            roomNumber: '—',
            guardians: '—',
            diseases: form.diseases,
          },
          buildHomeNameMap(seniorHomes.value),
        ),
      )
      return { savedLocally: true }
    }

    saving.value = true
    error.value = null
    try {
      await createSenior(form)
      await reload()
      return { savedLocally: false }
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zapisać seniora'
      throw e
    } finally {
      saving.value = false
    }
  }

  onMounted(reload)

  return {
    seniorList,
    seniorHomes,
    homeOptions,
    loading,
    saving,
    error,
    usingMock,
    reload,
    addSenior,
  }
}
