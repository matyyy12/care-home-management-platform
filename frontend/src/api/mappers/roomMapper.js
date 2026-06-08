function mockGameEquipment(record) {
  if (record.gameEquipment?.length) {
    return record.gameEquipment.map((item) => ({
      type: item.type,
      quantity: Number(item.quantity) || 1,
    }))
  }

  const items = []
  if (record.hasTv) items.push({ type: 'TV', quantity: 1 })
  if (record.hasRadio) items.push({ type: 'RADIO', quantity: 1 })
  if (record.computers) items.push({ type: 'COMPUTER', quantity: Number(record.computers) })
  if (record.crosswords) items.push({ type: 'CROSSWORD', quantity: Number(record.crosswords) })
  if (record.boardGames) items.push({ type: 'BOARD_GAME', quantity: Number(record.boardGames) })
  return items
}

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

export function mapRoomFromMock(record) {
  return {
    id: record.id,
    number: record.number,
    floor: record.floor,
    seniorHome: record.seniorHome ?? '—',
    seniorHomeId: record.seniorHomeId ?? null,
    inUse: Boolean(record.inUse),
    type: record.type ?? null,
    capacity: record.capacity ?? null,
    standard: record.standard ?? '',
    assignedSeniors: record.assignedSeniors ?? 0,
    officeKind: record.officeKind ?? '',
    equipment: record.equipment ?? '',
    gameEquipment: mockGameEquipment(record),
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
    standard: type === 'RESIDENTIAL' ? (form.standard ?? '').trim() : null,
    officeKind: type === 'OFFICE' ? (form.officeKind ?? '').trim() : null,
    equipmentDescription: type === 'OFFICE' ? (form.equipmentDescription ?? '').trim() : null,
    gameEquipment: type === 'GAME_ROOM' ? mapGameEquipmentForRequest(form.gameEquipment) : null,
  }
}

export function mapRoomToUpdateRequest(form) {
  return mapRoomToCreateRequest(form)
}
