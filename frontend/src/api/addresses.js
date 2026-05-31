import { apiGet, apiPost } from '@/api/request'
import { mapAddressToCreateRequest } from '@/api/mappers/addressMapper'

export async function fetchAddresses() {
  return apiGet('/api/addresses')
}

export async function createAddress(form) {
  return apiPost('/api/addresses', mapAddressToCreateRequest(form))
}
