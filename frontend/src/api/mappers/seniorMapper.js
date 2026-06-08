/** Mapowanie między API Spring a wierszem tabeli w UI. */

export function parseDiseases(value) {
  if (!value?.trim()) return []
  return value
    .split(',')
    .map((part) => part.trim())
    .filter(Boolean)
}

export function mapSeniorToCreateRequest(form) {
  return {
    firstName: form.firstName.trim(),
    lastName: form.lastName.trim(),
    birthDate: form.birthDate,
    pesel: form.pesel.trim(),
    gender: form.gender,
    accommodationDate: form.accommodationDate,
    dischargeDate: null,
    seniorHomeId: Number(form.seniorHomeId),
    diseases: parseDiseases(form.diseases),
  }
}

export function mapSeniorFromApi(dto, homeNameById = new Map()) {
  return {
    id: dto.seniorId,
    firstName: dto.firstName,
    lastName: dto.lastName,
    pesel: dto.pesel,
    birthDate: dto.birthDate,
    gender: dto.gender,
    seniorHomeId: dto.seniorHomeId,
    seniorHome: homeNameById.get(dto.seniorHomeId) ?? '—',
    accommodationDate: dto.accommodationDate,
    dischargeDate: dto.dischargeDate,
    status: dto.status,
    roomNumber: dto.roomNumber ?? '—',
    roomId: dto.roomId ?? null,
    guardianIds: dto.guardianIds ?? [],
    guardians:
      Array.isArray(dto.guardianNames) && dto.guardianNames.length > 0
        ? dto.guardianNames.join(', ')
        : '—',
    diseases: Array.isArray(dto.diseases) ? dto.diseases.join(', ') : '',
  }
}

export function buildHomeNameMap(homes) {
  return new Map(homes.map((home) => [home.id, home.name]))
}
