import { apiDelete, apiGet, apiPatch, apiPost } from '@/api/request'
import { mapOwnerToCreateRequest, mapOwnerToUpdateRequest } from '@/api/mappers/ownerMapper'

export async function fetchOwners(id) {
  const query = id != null ? `?id=${id}` : ''
  return apiGet(`/api/owners${query}`)
}

export async function createOwner(form) {
  return apiPost('/api/owners', mapOwnerToCreateRequest(form))
}

export async function updateOwner(id, form) {
  return apiPatch(`/api/owners/${id}`, mapOwnerToUpdateRequest(form))
}

export async function deleteOwner(id) {
  return apiDelete(`/api/owners/${id}`)
}
