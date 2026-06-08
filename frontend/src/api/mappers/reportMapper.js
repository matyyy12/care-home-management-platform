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
