import { computed, onMounted, ref } from 'vue'
import { fetchSeniors, createSenior, updateSenior } from '@/api/seniors'
import { fetchSeniorHomes } from '@/api/seniorHomes'
import { mapSeniorHomeFromApi } from '@/api/mappers/seniorHomeMapper'
import { buildHomeNameMap, mapSeniorFromApi } from '@/api/mappers/seniorMapper'
import { useHomeScope } from '@/composables/useHomeScope'

export function useSeniors() {
  const { filterItems, isScoped, homeId } = useHomeScope()
  const allSeniors = ref([])
  const allSeniorHomes = ref([])
  const loading = ref(false)
  const saving = ref(false)
  const error = ref(null)

  const seniorList = computed(() =>
    filterItems(allSeniors.value, { homeIdKey: 'seniorHomeId' }),
  )
  const seniorHomes = computed(() => {
    if (!isScoped.value) return allSeniorHomes.value
    return allSeniorHomes.value.filter((home) => Number(home.id) === Number(homeId.value))
  })

  const homeOptions = computed(() =>
    seniorHomes.value.map((home) => ({ value: home.id, label: home.name })),
  )

  async function reload() {
    loading.value = true
    error.value = null

    try {
      const [homes, seniors] = await Promise.all([fetchSeniorHomes(), fetchSeniors()])
      allSeniorHomes.value = homes.map(mapSeniorHomeFromApi)
      const homeNames = buildHomeNameMap(homes)
      allSeniors.value = seniors.map((dto) => mapSeniorFromApi(dto, homeNames))
    } catch (e) {
      error.value = e.message ?? 'Nie udało się połączyć z API'
    } finally {
      loading.value = false
    }
  }

  async function addSenior(form) {
    saving.value = true
    error.value = null
    try {
      await createSenior(form)
      await reload()
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zapisać seniora'
      throw e
    } finally {
      saving.value = false
    }
  }

  async function editSenior(id, form) {
    saving.value = true
    error.value = null
    try {
      await updateSenior(id, form)
      await reload()
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zaktualizować seniora'
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
    reload,
    addSenior,
    editSenior,
  }
}
