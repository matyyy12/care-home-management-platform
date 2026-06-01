import { onMounted, ref } from 'vue'
import { createOwner, deleteOwner, fetchOwners, updateOwner } from '@/api/owners'
import { mapOwnerFromApi, mapOwnerFromMock } from '@/api/mappers/ownerMapper'
import { mapSeniorHomeFromMock } from '@/api/mappers/seniorHomeMapper'
import { buildHomeNameMap } from '@/api/mappers/seniorMapper'
import { apiEnabled } from '@/composables/apiMode'
import { owners as mockOwners, seniorHomes as mockSeniorHomes } from '@/mock/data'

export function useOwners(getHomes = () => []) {
  const ownerList = ref([])
  const loading = ref(false)
  const saving = ref(false)
  const error = ref(null)
  const usingMock = ref(!apiEnabled)

  function homeNameById() {
    const homes = typeof getHomes === 'function' ? getHomes() : getHomes.value ?? []
    return buildHomeNameMap(homes)
  }

  function applyMockData() {
    const homeNames = buildHomeNameMap(mockSeniorHomes.map(mapSeniorHomeFromMock))
    ownerList.value = mockOwners.map((record) => mapOwnerFromMock(record, homeNames))
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
      const owners = await fetchOwners()
      ownerList.value = owners.map((dto) => mapOwnerFromApi(dto, homeNameById()))
      usingMock.value = false
    } catch (e) {
      error.value = e.message ?? 'Nie udało się połączyć z API'
      applyMockData()
    } finally {
      loading.value = false
    }
  }

  async function addOwner(form) {
    if (usingMock.value) {
      const nextId = Math.max(0, ...ownerList.value.map((o) => Number(o.id) || 0)) + 1
      ownerList.value.push(
        mapOwnerFromMock(
          {
            id: nextId,
            name: form.name,
            surname: form.surname,
            seniorHomeId: Number(form.seniorHomeId),
            city: form.city,
            street: form.street,
            apartmentNumber: form.apartmentNumber,
            postalCode: form.postalCode,
            postOfficeCity: form.postOfficeCity,
          },
          homeNameById(),
        ),
      )
      return { savedLocally: true }
    }

    saving.value = true
    error.value = null
    try {
      await createOwner(form)
      await reload()
      return { savedLocally: false }
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zapisać właściciela'
      throw e
    } finally {
      saving.value = false
    }
  }

  async function editOwner(id, form) {
    if (usingMock.value) {
      const index = ownerList.value.findIndex((o) => String(o.id) === String(id))
      if (index !== -1) {
        ownerList.value[index] = mapOwnerFromMock(
          {
            id: ownerList.value[index].id,
            name: form.name ?? ownerList.value[index].name,
            surname: form.surname ?? ownerList.value[index].surname,
            seniorHomeId: form.seniorHomeId ?? ownerList.value[index].seniorHomeId,
            city: form.city ?? ownerList.value[index].city,
            street: form.street ?? ownerList.value[index].street,
            apartmentNumber: form.apartmentNumber ?? ownerList.value[index].apartmentNumber,
            postalCode: form.postalCode ?? ownerList.value[index].postalCode,
            postOfficeCity: form.postOfficeCity ?? ownerList.value[index].postOfficeCity,
          },
          homeNameById(),
        )
      }
      return { savedLocally: true }
    }

    saving.value = true
    error.value = null
    try {
      await updateOwner(id, form)
      await reload()
      return { savedLocally: false }
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zaktualizować właściciela'
      throw e
    } finally {
      saving.value = false
    }
  }

  async function removeOwner(id) {
    if (usingMock.value) {
      ownerList.value = ownerList.value.filter((o) => String(o.id) !== String(id))
      return { savedLocally: true }
    }

    saving.value = true
    error.value = null
    try {
      await deleteOwner(id)
      await reload()
      return { savedLocally: false }
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
    usingMock,
    reload,
    addOwner,
    editOwner,
    removeOwner,
  }
}
