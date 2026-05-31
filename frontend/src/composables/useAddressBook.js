import { computed, onMounted, ref } from 'vue'
import { fetchAddresses, createAddress } from '@/api/addresses'
import { fetchPostOffices, createPostOffice } from '@/api/postOffices'
import {
  mapAddressFromApi,
  mapAddressFromMock,
  mapAddressToCreateRequest,
  mapPostOfficeFromApi,
  mapPostOfficeFromMock,
} from '@/api/mappers/addressMapper'
import { apiEnabled } from '@/composables/apiMode'
import { addresses as mockAddresses, postOffices as mockPostOffices } from '@/mock/data'

export function useAddressBook() {
  const addresses = ref([])
  const postOffices = ref([])
  const loading = ref(false)
  const saving = ref(false)
  const error = ref(null)
  const usingMock = ref(!apiEnabled)

  const postOptions = computed(() =>
    postOffices.value.map((post) => ({
      value: post.id,
      label: `${post.postalCode} ${post.city}`,
    })),
  )

  function applyMockData() {
    postOffices.value = mockPostOffices.map(mapPostOfficeFromMock)
    addresses.value = mockAddresses.map(mapAddressFromMock)
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
      const [posts, addressRows] = await Promise.all([fetchPostOffices(), fetchAddresses()])
      postOffices.value = posts.map(mapPostOfficeFromApi)
      addresses.value = addressRows.map(mapAddressFromApi)
      usingMock.value = false
    } catch (e) {
      error.value = e.message ?? 'Nie udało się połączyć z API'
      applyMockData()
    } finally {
      loading.value = false
    }
  }

  async function addPostOffice(form) {
    if (usingMock.value) {
      const nextId = Math.max(0, ...postOffices.value.map((p) => Number(p.id) || 0)) + 1
      postOffices.value.push(mapPostOfficeFromMock({ id: nextId, ...form }))
      return { savedLocally: true }
    }

    saving.value = true
    error.value = null
    try {
      await createPostOffice(form)
      await reload()
      return { savedLocally: false }
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zapisać poczty'
      throw e
    } finally {
      saving.value = false
    }
  }

  async function addAddress(form) {
    if (usingMock.value) {
      const post = postOffices.value.find((p) => String(p.id) === String(form.postOfficeId))
      const nextId = Math.max(0, ...addresses.value.map((a) => Number(a.id) || 0)) + 1
      addresses.value.push(
        mapAddressFromMock({
          id: nextId,
          city: form.city,
          street: form.street,
          apartmentNumber: form.apartmentNumber,
          postOffice: post ? `${post.postalCode} ${post.city}` : '—',
        }),
      )
      return { savedLocally: true }
    }

    saving.value = true
    error.value = null
    try {
      await createAddress(mapAddressToCreateRequest(form))
      await reload()
      return { savedLocally: false }
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zapisać adresu'
      throw e
    } finally {
      saving.value = false
    }
  }

  onMounted(reload)

  return {
    addresses,
    postOffices,
    postOptions,
    loading,
    saving,
    error,
    usingMock,
    reload,
    addPostOffice,
    addAddress,
  }
}
