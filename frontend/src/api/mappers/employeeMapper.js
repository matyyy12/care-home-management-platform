export function buildHomeNameMap(homes) {
  return new Map(homes.map((home) => [Number(home.id), home.name]))
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
    birthDate: dto.birthDate ?? '',
    hiredAt: dto.hireDate ?? '',
    terminatedAt: dto.fireDate ?? null,
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
