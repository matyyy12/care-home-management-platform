export function mapActivityFromApi(dto) {
  return {
    id: dto.id,
    name: dto.name,
    type: dto.type,
    price: Number(dto.price),
    seniorHome: dto.seniorHome ?? '—',
    description: dto.description ?? '',
    instructors: dto.instructors ?? 0,
    participants: dto.participants ?? 0,
  }
}

export function mapActivityToCreateRequest(form) {
  return {
    name: form.name.trim(),
    type: form.type,
    price: Number(form.price) || 0,
    seniorHomeId: Number(form.seniorHomeId),
    description: (form.description ?? '').trim(),
  }
}

export function mapActivityToUpdateRequest(form) {
  return mapActivityToCreateRequest(form)
}

export function mapActivityFromMock(record) {
  return {
    id: record.id,
    name: record.name,
    type: record.type,
    price: Number(record.price),
    seniorHome: record.seniorHome ?? '—',
    description: record.description ?? '',
    instructors: record.instructors ?? 0,
    participants: record.participants ?? 0,
  }
}
