import { onMounted, ref } from 'vue'
import { fetchMedicalActivityReport } from '@/api/reports'
import { fetchSeniorHomes } from '@/api/seniorHomes'
import { mapMedicalActivityFromApi } from '@/api/mappers/reportMapper'

export function useMedicalActivityReport() {
  const rows = ref([])
  const seniorHomes = ref([])
  const loading = ref(false)
  const error = ref(null)

  async function reload(filters = {}) {
    loading.value = true
    error.value = null

    try {
      const [homes, report] = await Promise.all([fetchSeniorHomes(), fetchMedicalActivityReport(filters)])
      seniorHomes.value = homes
      rows.value = report.map(mapMedicalActivityFromApi)
    } catch (e) {
      error.value = e.message ?? 'Nie udało się pobrać raportu'
    } finally {
      loading.value = false
    }
  }

  onMounted(() => reload())

  return {
    rows,
    seniorHomes,
    loading,
    error,
    reload,
  }
}
