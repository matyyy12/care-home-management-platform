export function mapAddressFromApi(dto) {
  const postOffice =
    dto.postalCode && dto.postOfficeCity
      ? `${dto.postalCode} ${dto.postOfficeCity}`
      : '—'

  return {
    id: dto.id,
    city: dto.city,
    street: dto.street,
    apartmentNumber: dto.number,
    postOffice,
    postOfficeId: dto.postOfficeId,
  }
}

export function mapAddressToCreateRequest(form) {
  return {
    city: form.city.trim(),
    street: form.street.trim(),
    number: (form.apartmentNumber ?? '').trim() || '—',
    postOfficeId: Number(form.postOfficeId),
  }
}

export function mapAddressFromMock(record) {
  return {
    id: record.id,
    city: record.city,
    street: record.street,
    apartmentNumber: record.apartmentNumber,
    postOffice: record.postOffice,
    postOfficeId: null,
  }
}

export function mapPostOfficeFromApi(dto) {
  return {
    id: dto.id,
    postalCode: dto.postalCode,
    city: dto.city,
  }
}

export function mapPostOfficeToCreateRequest(form) {
  return {
    postalCode: form.postalCode.trim(),
    city: form.city.trim(),
  }
}

export function mapPostOfficeFromMock(record) {
  return {
    id: record.id,
    postalCode: record.postalCode,
    city: record.city,
  }
}
