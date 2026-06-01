import { computed, onMounted, ref } from 'vue'
import { fetchSeniors, createSenior } from '@/api/seniors'
import { fetchSeniorHomes } from '@/api/seniorHomes'
import {
  mapSeniorHomeFromApi,
  mapSeniorHomeFromMock,
} from '@/api/mappers/seniorHomeMapper'
import {
  buildHomeNameMap,
  mapSeniorFromApi,
  mapSeniorFromMock,
} from '@/api/mappers/seniorMapper'
import { apiEnabled } from '@/composables/apiMode'
import { useHomeScope } from '@/composables/useHomeScope'
import { seniors as mockSeniors, seniorHomes as mockSeniorHomes } from '@/mock/data'

function loadMockHomes() {
  return mockSeniorHomes.map(mapSeniorHomeFromMock)
}

function loadMockSeniors(homeNames) {
  return mockSeniors.map((record) => mapSeniorFromMock(record, homeNames))
}

export function useSeniors() {
  const { filterItems, isScoped, homeId } = useHomeScope()
  const allSeniors = ref([])
  const allSeniorHomes = ref([])
  const loading = ref(false)
  const saving = ref(false)
  const error = ref(null)
  const usingMock = ref(!apiEnabled)

  const seniorList = computed(() => filterItems(allSeniors.value))
  const seniorHomes = computed(() => {
    if (!isScoped.value) return allSeniorHomes.value
    return allSeniorHomes.value.filter((home) => Number(home.id) === Number(homeId.value))
  })

  const homeOptions = computed(() =>
    seniorHomes.value.map((home) => ({ value: home.id, label: home.name })),
  )

  function applyMockData() {
    const homes = loadMockHomes()
    allSeniorHomes.value = homes
    const homeNames = buildHomeNameMap(homes)
    allSeniors.value = loadMockSeniors(homeNames)
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
      allSeniorHomes.value = homes.map(mapSeniorHomeFromApi)
      const homeNames = buildHomeNameMap(homes)
      allSeniors.value = seniors.map((dto) => mapSeniorFromApi(dto, homeNames))
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
      allSeniors.value.push(
        mapSeniorFromMock(
          {
            id: Math.max(0, ...allSeniors.value.map((s) => Number(s.id) || 0)) + 1,
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
