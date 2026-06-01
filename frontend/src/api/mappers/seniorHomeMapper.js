function formatAddressLabel({ city, street, number }) {
  const streetPart = [street, number].filter(Boolean).join(' ')
  const parts = [city, streetPart ? `ul. ${streetPart}` : ''].filter(Boolean)
  return parts.join(', ') || '—'
}

export function mapSeniorHomeFromApi(dto) {
  return {
    id: dto.id,
    name: dto.name,
    phone: dto.phoneNumber,
    email: dto.email ?? '',
    website: dto.website ?? '',
    addressLabel: formatAddressLabel(dto),
    foundedAt: '',
    owners: [],
  }
}

export function mapSeniorHomeToCreateRequest(form) {
  const payload = {
    name: form.name.trim(),
    phoneNumber: form.phone.trim(),
    establishedDate: form.foundedAt || new Date().toISOString().slice(0, 10),
    address: {
      city: form.city.trim(),
      street: form.street.trim(),
      number: (form.apartmentNumber ?? '').trim() || '—',
      postOffice: {
        postalCode: form.postalCode.trim(),
        city: form.postOfficeCity.trim(),
      },
    },
  }

  const email = form.email?.trim()
  if (email) payload.email = email

  const website = form.website?.trim()
  if (website) payload.website = website

  return payload
}

export function mapSeniorHomeToUpdateRequest(form) {
  const payload = {}

  if (form.name?.trim()) payload.name = form.name.trim()
  if (form.phone?.trim()) payload.phoneNumber = form.phone.trim()

  const email = form.email?.trim()
  if (email) payload.email = email

  const website = form.website?.trim()
  if (website) payload.website = website

  if (form.foundedAt) payload.establishedDate = form.foundedAt

  if (form.city?.trim() && form.street?.trim() && form.postalCode?.trim() && form.postOfficeCity?.trim()) {
    payload.address = {
      city: form.city.trim(),
      street: form.street.trim(),
      number: (form.apartmentNumber ?? '').trim() || '—',
      postOffice: {
        postalCode: form.postalCode.trim(),
        city: form.postOfficeCity.trim(),
      },
    }
  }

  return payload
}

export function mapSeniorHomeFromMock(record) {
  return {
    id: record.id,
    name: record.name,
    phone: record.phone,
    email: record.email ?? '',
    website: record.website ?? '',
    addressLabel: record.addressLabel ?? '—',
    foundedAt: record.foundedAt ?? '',
    owners: record.owners ?? [],
  }
}
