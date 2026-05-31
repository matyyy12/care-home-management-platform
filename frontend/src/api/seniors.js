import { apiGet, apiPost } from '@/api/client'
import { mapSeniorToCreateRequest } from '@/api/mappers/seniorMapper'

export async function fetchSeniors() {
  return apiGet('/api/seniors')
}

export async function createSenior(form) {
  return apiPost('/api/seniors', mapSeniorToCreateRequest(form))
}
