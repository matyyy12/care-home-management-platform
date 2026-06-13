function formatAddressLabel({ city, street, number, postalCode }) {
  const streetPart = [street, number].filter(Boolean).join(' ')
  const parts = [city, streetPart ? `ul. ${streetPart}` : '', postalCode].filter(Boolean)
  return parts.join(', ') || '—'
}

export function mapOwnerFromApi(dto, homeNameById = new Map()) {
  return {
    id: dto.id,
    name: dto.name,
    surname: dto.surname,
    displayName: `${dto.name} ${dto.surname}`,
    seniorHomeId: dto.seniorHomeId,
    seniorHome: homeNameById.get(dto.seniorHomeId) ?? '—',
    city: dto.city,
    street: dto.street,
    apartmentNumber: dto.number,
    postalCode: dto.postalCode ?? '',
    postOfficeCity: dto.postOfficeCity ?? dto.city ?? '',
    addressLabel: formatAddressLabel(dto),
  }
}

export function mapOwnerToCreateRequest(form) {
  return {
    name: form.name.trim(),
    surname: form.surname.trim(),
    seniorHomeId: Number(form.seniorHomeId),
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
}

export function mapOwnerToUpdateRequest(form) {
  const payload = {}

  if (form.name?.trim()) payload.name = form.name.trim()
  if (form.surname?.trim()) payload.surname = form.surname.trim()
  if (form.seniorHomeId) payload.seniorHomeId = Number(form.seniorHomeId)

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

export function attachOwnersToHomes(homes, owners) {
  const ownersByHomeId = new Map()

  for (const owner of owners) {
    const list = ownersByHomeId.get(owner.seniorHomeId) ?? []
    list.push(owner.displayName)
    ownersByHomeId.set(owner.seniorHomeId, list)
  }

  return homes.map((home) => ({
    ...home,
    owners: ownersByHomeId.get(home.id) ?? [],
  }))
}
