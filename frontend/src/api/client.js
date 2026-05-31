/**
 * Bazowy klient HTTP.
 * Dev: puste VITE_API_BASE_URL → ścieżki /api przez proxy Vite.
 * Prod: ustaw VITE_API_BASE_URL (np. http://localhost:8080).
 */
const API_BASE = (import.meta.env.VITE_API_BASE_URL ?? '').replace(/\/$/, '')

export class ApiError extends Error {
  constructor(message, { status, body } = {}) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.body = body
  }
}

async function parseErrorBody(response) {
  try {
    const text = await response.text()
    if (!text) return null
    return JSON.parse(text)
  } catch {
    return null
  }
}

function errorMessage(status, body) {
  if (body?.message) return body.message
  if (typeof body === 'string') return body
  if (Array.isArray(body?.errors)) return body.errors.join(', ')
  return `Błąd serwera (${status})`
}

export async function apiRequest(path, options = {}) {
  const normalizedPath = path.startsWith('/') ? path : `/${path}`
  const url = `${API_BASE}${normalizedPath}`

  const response = await fetch(url, {
    headers: {
      Accept: 'application/json',
      ...options.headers,
    },
    ...options,
  })

  if (!response.ok) {
    const body = await parseErrorBody(response)
    throw new ApiError(errorMessage(response.status, body), {
      status: response.status,
      body,
    })
  }

  if (response.status === 204) return null

  const contentType = response.headers.get('content-type') ?? ''
  if (contentType.includes('application/json')) {
    return response.json()
  }

  return null
}

export function apiGet(path) {
  return apiRequest(path)
}

export function apiPost(path, data) {
  return apiRequest(path, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  })
}
