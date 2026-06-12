import { apiDelete, apiGet, apiPatch, apiPost } from '@/api/request'
import {
  mapEquipmentToCreateRequest,
  mapEquipmentToUpdateRequest,
} from '@/api/mappers/equipmentMapper'

export async function fetchEquipment() {
  return apiGet('/api/equipment')
}

export async function createEquipment(form) {
  return apiPost('/api/equipment', mapEquipmentToCreateRequest(form))
}

export async function updateEquipment(id, form) {
  return apiPatch(`/api/equipment/${id}`, mapEquipmentToUpdateRequest(form))
}

export async function deleteEquipment(id) {
  return apiDelete(`/api/equipment/${id}`)
}
