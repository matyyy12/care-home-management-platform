import { apiGet } from '@/api/client'

export async function fetchSeniorHomes() {
  return apiGet('/api/senior-homes')
}
