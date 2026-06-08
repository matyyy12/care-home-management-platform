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

export function mapRoomOccupancyFromMock(record) {
  return {
    id: record.id,
    roomNumber: record.roomNumber,
    floor: record.floor,
    seniorHome: record.seniorHome,
    capacity: record.capacity,
    assigned: record.assigned,
    free: record.free,
    standard: record.standard,
  }
}
