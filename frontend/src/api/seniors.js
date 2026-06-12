import { apiDelete, apiGet, apiPatch, apiPost } from '@/api/request'
import { mapSeniorToCreateRequest, mapSeniorToUpdateRequest } from '@/api/mappers/seniorMapper'

export async function fetchSeniors() {
  return apiGet('/api/seniors')
}

export async function createSenior(form) {
  return apiPost('/api/seniors', mapSeniorToCreateRequest(form))
}

export async function updateSenior(id, form) {
  return apiPatch(`/api/seniors/${id}`, mapSeniorToUpdateRequest(form))
}

export async function deleteSenior(id) {
  return apiDelete(`/api/seniors/${id}`)
}
