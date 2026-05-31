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

export function mapPositionFromMock(record) {
  return {
    id: record.id,
    name: record.name,
    duties: record.duties ?? '',
  }
}
