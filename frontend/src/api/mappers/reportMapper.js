export function mapRoomOccupancyFromApi(dto, homeNameById = new Map()) {
  return {
    id: dto.roomId,
    roomNumber: dto.roomNumber,
    floor: dto.floor,
    seniorHome: dto.seniorHome ?? homeNameById.get(dto.seniorHomeId) ?? '—',
    seniorHomeId: dto.seniorHomeId,
    capacity: dto.capacity,
    assigned: dto.assigned,
    free: dto.free,
    standard: dto.standard,
  }
}

export function mapSeniorActivityFromApi(dto) {
  return {
    id: dto.seniorId,
    seniorId: dto.seniorId,
    seniorName: dto.seniorName,
    seniorHomeId: dto.seniorHomeId,
    seniorHome: dto.seniorHome,
    status: dto.status,
    roomNumber: dto.roomNumber ?? '—',
    participationCount: dto.participationCount,
    availableActivityCount: dto.availableActivityCount ?? 0,
    medicalActivityCount: dto.medicalActivityCount ?? 0,
    recreationalActivityCount: dto.recreationalActivityCount ?? 0,
    participationLabel:
      dto.participationCount == null ? 'Brak danych' : String(dto.participationCount),
  }
}
