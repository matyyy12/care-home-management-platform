import { onMounted, ref } from 'vue'
import { createAdminBackup, fetchAdminConfig, fetchAdminHealth } from '@/api/admin'

export function useAdmin() {
  const health = ref(null)
  const config = ref(null)
  const backupResult = ref(null)
  const loading = ref(false)
  const backupLoading = ref(false)
  const error = ref(null)
  const backupError = ref(null)

  async function loadStatus() {
    loading.value = true
    error.value = null

    try {
      const [healthResponse, configResponse] = await Promise.all([
        fetchAdminHealth(),
        fetchAdminConfig(),
      ])
      health.value = healthResponse
      config.value = configResponse
    } catch (e) {
      error.value = e.message ?? 'Nie udało się pobrać danych administracyjnych'
    } finally {
      loading.value = false
    }
  }

  async function runBackup() {
    backupLoading.value = true
    backupError.value = null

    try {
      backupResult.value = await createAdminBackup()
    } catch (e) {
      backupError.value = e.message ?? 'Nie udało się uruchomić kopii zapasowej'
    } finally {
      backupLoading.value = false
    }
  }

  onMounted(() => loadStatus())

  return {
    health,
    config,
    backupResult,
    loading,
    backupLoading,
    error,
    backupError,
    loadStatus,
    runBackup,
  }
}
