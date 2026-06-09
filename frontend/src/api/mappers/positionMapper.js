export function mapPositionFromApi(dto) {
  return {
    id: dto.id,
    name: dto.name,
    duties: dto.description ?? '',
  }
}

export function mapPositionToCreateRequest(form) {
  return {
    name: form.name.trim(),
    description: (form.duties ?? '').trim() || '—',
  }
}

export function mapPositionToUpdateRequest(form) {
  return {
    name: form.name.trim(),
    description: (form.duties ?? '').trim() || '—',
  }
}
