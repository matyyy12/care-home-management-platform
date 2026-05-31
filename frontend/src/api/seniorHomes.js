import { apiGet } from '@/api/request'

export async function fetchSeniorHomes() {
  return apiGet('/api/senior-home')
}
