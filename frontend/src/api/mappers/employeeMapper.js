export function buildHomeNameMap(homes) {
  return new Map(homes.map((home) => [Number(home.id), home.name]))
}

function dateOnly(value) {
  if (!value) return ''
  return String(value).slice(0, 10)
}

export function mapEmployeeFromApi(dto, homeNames = new Map()) {
  return {
    id: dto.id,
    firstName: dto.name,
    lastName: dto.surname,
    seniorHomeId: dto.seniorHomeId,
    seniorHome: homeNames.get(Number(dto.seniorHomeId)) ?? '—',
    positionId: dto.positionId,
    position: dto.positionName ?? '—',
    phone: dto.phone ?? '',
    email: dto.email ?? '',
    birthDate: dateOnly(dto.birthDate),
    hiredAt: dateOnly(dto.hireDate),
    terminatedAt: dateOnly(dto.fireDate),
    addressId: dto.addressId,
    city: dto.city ?? '',
    street: dto.street ?? '',
    number: dto.number ?? '',
    postalCode: dto.postalCode ?? '',
    postOfficeCity: dto.postOfficeCity ?? dto.city ?? '',
    isMedical: Boolean(dto.licenseNumber || dto.specialty),
    licenseNumber: dto.licenseNumber ?? '',
    specialization: dto.specialty ?? '',
    salary: dto.salary ?? 0,
    roomIds: dto.roomIds ?? [],
  }
}

export function mapEmployeeToCreateRequest(form) {
  const payload = {
    name: form.firstName.trim(),
    surname: form.lastName.trim(),
    salary: Number(form.salary),
    phone: (form.phone ?? '').trim(),
    birthDate: form.birthDate,
    hireDate: form.hiredAt,
    fireDate: form.terminatedAt || null,
    address: {
      city: form.city.trim(),
      street: form.street.trim(),
      number: form.number.trim(),
      postOffice: {
        postalCode: form.postalCode.trim(),
        city: form.postOfficeCity.trim(),
      },
    },
    seniorHomeId: Number(form.seniorHomeId),
    positionId: Number(form.positionId),
    managerId: form.managerId ? Number(form.managerId) : null,
  }

  const email = form.email?.trim()
  if (email) payload.email = email

  if (form.isMedical === true || form.isMedical === 'true') {
    payload.medicalEmployee = {
      licenseNumber: form.licenseNumber.trim(),
      specialty: form.specialization.trim(),
    }
  }

  return payload
}

export function mapEmployeeToUpdateRequest(form) {
  const payload = {
    name: form.firstName.trim(),
    surname: form.lastName.trim(),
    salary: Number(form.salary),
    email: (form.email ?? '').trim() || null,
    phone: (form.phone ?? '').trim() || null,
    birthDate: form.birthDate,
    hireDate: form.hiredAt,
    fireDate: form.terminatedAt || null,
    seniorHomeId: Number(form.seniorHomeId),
    positionId: Number(form.positionId),
    managerId: form.managerId ? Number(form.managerId) : null,
  }

  if (form.city?.trim() && form.street?.trim() && form.number?.trim() && form.postalCode?.trim()) {
    payload.address = {
      city: form.city.trim(),
      street: form.street.trim(),
      number: form.number.trim(),
      postOffice: {
        postalCode: form.postalCode.trim(),
        city: (form.postOfficeCity ?? form.city).trim(),
      },
    }
  }

  if (form.isMedical === true || form.isMedical === 'true') {
    payload.medicalEmployee = {
      licenseNumber: form.licenseNumber.trim(),
      specialty: form.specialization.trim(),
    }
  }

  return payload
}
