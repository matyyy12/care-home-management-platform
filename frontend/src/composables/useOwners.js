import { onMounted, ref } from 'vue'
import { createOwner, deleteOwner, fetchOwners, updateOwner } from '@/api/owners'
import { mapOwnerFromApi } from '@/api/mappers/ownerMapper'
import { buildHomeNameMap } from '@/api/mappers/seniorMapper'

export function useOwners(getHomes = () => []) {
  const ownerList = ref([])
  const loading = ref(false)
  const saving = ref(false)
  const error = ref(null)

  function homeNameById() {
    const homes = typeof getHomes === 'function' ? getHomes() : getHomes.value ?? []
    return buildHomeNameMap(homes)
  }

  async function reload() {
    loading.value = true
    error.value = null

    try {
      const owners = await fetchOwners()
      ownerList.value = owners.map((dto) => mapOwnerFromApi(dto, homeNameById()))
    } catch (e) {
      error.value = e.message ?? 'Nie udało się połączyć z API'
    } finally {
      loading.value = false
    }
  }

  async function addOwner(form) {
    saving.value = true
    error.value = null
    try {
      await createOwner(form)
      await reload()
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zapisać właściciela'
      throw e
    } finally {
      saving.value = false
    }
  }

  async function editOwner(id, form) {
    saving.value = true
    error.value = null
    try {
      await updateOwner(id, form)
      await reload()
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zaktualizować właściciela'
      throw e
    } finally {
      saving.value = false
    }
  }

  async function removeOwner(id) {
    saving.value = true
    error.value = null
    try {
      await deleteOwner(id)
      await reload()
    } catch (e) {
      error.value = e.message ?? 'Nie udało się usunąć właściciela'
      throw e
    } finally {
      saving.value = false
    }
  }

  onMounted(reload)

  return {
    ownerList,
    loading,
    saving,
    error,
    reload,
    addOwner,
    editOwner,
    removeOwner,
  }
}
