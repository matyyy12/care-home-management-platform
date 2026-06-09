export function mapEquipmentFromApi(dto) {
  const brand = dto.brand ?? ''
  const modelName = dto.modelName ?? ''

  return {
    id: dto.id,
    inventoryNumber: dto.inventoryNumber,
    name: dto.name,
    description: dto.description ?? '',
    purchaseDate: dto.purchaseDate ?? '',
    modelId: dto.modelId,
    modelName,
    brand,
    modelLabel: [brand, modelName].filter(Boolean).join(' ') || '—',
  }
}

export function mapEquipmentToCreateRequest(form) {
  return {
    inventoryNumber: form.inventoryNumber.trim(),
    name: form.name.trim(),
    description: form.description?.trim() || null,
    purchaseDate: form.purchaseDate || null,
    model: {
      brand: form.brand.trim(),
      name: form.modelName.trim(),
    },
  }
}

export function mapEquipmentToUpdateRequest(form) {
  const payload = {}

  if (form.inventoryNumber?.trim()) payload.inventoryNumber = form.inventoryNumber.trim()
  if (form.name?.trim()) payload.name = form.name.trim()
  if (form.description != null) payload.description = form.description.trim()
  if (form.purchaseDate) payload.purchaseDate = form.purchaseDate
  if (form.brand?.trim() && form.modelName?.trim()) {
    payload.model = {
      brand: form.brand.trim(),
      name: form.modelName.trim(),
    }
  }

  return payload
}
