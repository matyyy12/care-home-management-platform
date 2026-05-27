const API_BASE = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

export async function fetchSeniors() {
  const response = await fetch(`${API_BASE}/api/seniors`)
  if (!response.ok) {
    throw new Error('Nie udało się pobrać listy seniorów')
  }
  return response.json()
}

export async function createSenior(senior) {
  const response = await fetch(`${API_BASE}/api/seniors`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(senior),
  })
  if (!response.ok) {
    throw new Error('Nie udało się dodać seniora')
  }
  return response.json()
}
