export function mapRoomFromApi(dto) {
  return {
    id: dto.id,
    number: dto.number,
    floor: dto.floor,
    seniorHome: dto.seniorHome ?? '—',
    seniorHomeId: dto.seniorHomeId,
    inUse: Boolean(dto.inUse),
    type: dto.type ?? null,
    capacity: dto.capacity ?? null,
    standard: dto.standard ?? '',
    assignedSeniors: dto.assignedSeniors ?? 0,
    officeKind: dto.officeKind ?? '',
    equipment: dto.equipment ?? '',
    gameEquipment: (dto.gameEquipment ?? []).map((item) => ({
      type: item.type,
      quantity: Number(item.quantity) || 1,
    })),
  }
}

function mapGameEquipmentForRequest(items) {
  return (items ?? [])
    .filter((item) => item.type)
    .map((item) => ({
      type: item.type,
      quantity: Math.max(1, Number(item.quantity) || 1),
    }))
}

function parseInUse(value) {
  return value === true || value === 'true'
}

export function mapRoomToCreateRequest(form) {
  const inUse = parseInUse(form.inUse)
  const type = inUse ? form.type || null : null

  return {
    roomNumber: form.roomNumber.trim(),
    floor: Number(form.floor),
    inUse,
    seniorHomeId: Number(form.seniorHomeId),
    type,
    capacity: type === 'RESIDENTIAL' ? Number(form.capacity) : null,
    standard: type === 'RESIDENTIAL' ? form.standard || null : null,
    officeKind: type === 'OFFICE' ? (form.officeKind ?? '').trim() : null,
    equipmentDescription: type === 'OFFICE' ? (form.equipmentDescription ?? '').trim() : null,
    gameEquipment: type === 'GAME_ROOM' ? mapGameEquipmentForRequest(form.gameEquipment) : null,
  }
}

export function mapRoomToUpdateRequest(form) {
  return mapRoomToCreateRequest(form)
}
