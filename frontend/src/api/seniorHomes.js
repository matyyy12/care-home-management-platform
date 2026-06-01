import { apiDelete, apiGet, apiPatch, apiPost } from '@/api/request'
import {
  mapSeniorHomeToCreateRequest,
  mapSeniorHomeToUpdateRequest,
} from '@/api/mappers/seniorHomeMapper'

export async function fetchSeniorHomes(homeId) {
  const query = homeId != null ? `?homeId=${homeId}` : ''
  return apiGet(`/api/senior-home${query}`)
}

export async function createSeniorHome(form) {
  return apiPost('/api/senior-home', mapSeniorHomeToCreateRequest(form))
}

export async function updateSeniorHome(id, form) {
  return apiPatch(`/api/senior-home/${id}`, mapSeniorHomeToUpdateRequest(form))
}

export async function deleteSeniorHome(id) {
  return apiDelete(`/api/senior-home/${id}`)
}
