import { apiGet, apiPost } from '@/api/client'
import { mapPostOfficeToCreateRequest } from '@/api/mappers/addressMapper'

export async function fetchPostOffices() {
  return apiGet('/api/post-offices')
}

export async function createPostOffice(form) {
  return apiPost('/api/post-offices', mapPostOfficeToCreateRequest(form))
}
