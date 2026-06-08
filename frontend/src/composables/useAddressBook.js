import { computed, onMounted, ref } from 'vue'
import { fetchAddresses, createAddress } from '@/api/addresses'
import { fetchPostOffices, createPostOffice } from '@/api/postOffices'
import {
  mapAddressFromApi,
  mapAddressToCreateRequest,
  mapPostOfficeFromApi,
} from '@/api/mappers/addressMapper'

export function useAddressBook() {
  const addresses = ref([])
  const postOffices = ref([])
  const loading = ref(false)
  const saving = ref(false)
  const error = ref(null)

  const postOptions = computed(() =>
    postOffices.value.map((post) => ({
      value: post.id,
      label: `${post.postalCode} ${post.city}`,
    })),
  )

  async function reload() {
    loading.value = true
    error.value = null

    try {
      const [posts, addressRows] = await Promise.all([fetchPostOffices(), fetchAddresses()])
      postOffices.value = posts.map(mapPostOfficeFromApi)
      addresses.value = addressRows.map(mapAddressFromApi)
    } catch (e) {
      error.value = e.message ?? 'Nie udało się połączyć z API'
    } finally {
      loading.value = false
    }
  }

  async function addPostOffice(form) {
    saving.value = true
    error.value = null
    try {
      await createPostOffice(form)
      await reload()
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zapisać poczty'
      throw e
    } finally {
      saving.value = false
    }
  }

  async function addAddress(form) {
    saving.value = true
    error.value = null
    try {
      await createAddress(mapAddressToCreateRequest(form))
      await reload()
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
    reload,
    addPostOffice,
    addAddress,
  }
}
