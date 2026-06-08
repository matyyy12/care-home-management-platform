export function mapSeniorRoomAssignmentFromApi(dto) {
  return {
    id: dto.id,
    type: 'SENIOR_ROOM',
    label: 'Senior → pokój',
    from: dto.seniorName,
    to: `Pokój ${dto.roomNumber}`,
    seniorId: dto.seniorId,
    roomId: dto.roomId,
    roomNumber: dto.roomNumber,
    seniorHomeId: dto.seniorHomeId,
    seniorHome: dto.seniorHomeName ?? '—',
  }
}
