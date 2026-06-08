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

export function mapSeniorRoomAssignmentFromMock(record) {
  return {
    id: record.id,
    type: record.type ?? 'SENIOR_ROOM',
    label: record.label ?? 'Senior → pokój',
    from: record.from,
    to: record.to,
    seniorHome: record.seniorHome,
  }
}
