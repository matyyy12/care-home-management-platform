import { apiGet, apiPost, apiPatch, apiDelete } from '@/api/request'
import {
  mapActivityToCreateRequest,
  mapActivityToUpdateRequest,
} from '@/api/mappers/activityMapper'

export async function fetchActivitiesFromSeniorHome(seniorHomeId) {
  return apiGet(`/api/activities/${seniorHomeId}`)
}

export async function fetchAllActivities(homes) {
  if (!homes?.length) {
    return []
  }
  const lists = await Promise.all(
    homes.map((home) => fetchActivitiesFromSeniorHome(home.id)),
  )
  return lists.flat()
}

export async function createActivity(form) {
  return apiPost('/api/activities', mapActivityToCreateRequest(form))
}

export async function updateActivity(id, form) {
  return apiPatch(`/api/activities/${id}`, mapActivityToUpdateRequest(form))
}

export async function deleteActivity(id) {
  return apiDelete(`/api/activities/${id}`)
}