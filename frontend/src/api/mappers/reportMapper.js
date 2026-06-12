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

export function mapEmployeeWorkloadFromApi(dto) {
  return {
    id: dto.employeeId,
    employeeId: dto.employeeId,
    employeeName: dto.employeeName,
    seniorHomeId: dto.seniorHomeId,
    seniorHome: dto.seniorHome,
    positionId: dto.positionId,
    positionName: dto.positionName,
    managerName: dto.managerName ?? '—',
    medicalEmployee: Boolean(dto.medicalEmployee),
    seniorCount: dto.seniorCount ?? 0,
    roomCount: dto.roomCount ?? 0,
    activityCount: dto.activityCount ?? 0,
    medicalActivityCount: dto.medicalActivityCount ?? 0,
    totalAssignments: dto.totalAssignments ?? 0,
    workloadScore: dto.workloadScore ?? 0,
    employmentStatus: dto.employmentStatus,
    hasWarning: Boolean(dto.hasWarning),
    warningMessage: dto.warningMessage ?? '',
  }
}

export function mapDataQualityIssueFromApi(dto) {
  return {
    id: `${dto.module}-${dto.objectType}-${dto.objectId}-${dto.description}`,
    severity: dto.severity,
    module: dto.module,
    objectType: dto.objectType,
    objectId: dto.objectId,
    objectName: dto.objectName,
    seniorHomeId: dto.seniorHomeId,
    seniorHome: dto.seniorHome,
    description: dto.description,
    suggestedAction: dto.suggestedAction,
  }
}
