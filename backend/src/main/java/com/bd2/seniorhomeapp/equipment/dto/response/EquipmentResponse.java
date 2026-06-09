package com.bd2.seniorhomeapp.equipment.dto.response;

import java.time.LocalDate;

public record EquipmentResponse(
        Long id,
        String inventoryNumber,
        String name,
        String description,
        LocalDate purchaseDate,
        Long modelId,
        String modelName,
        String brand
) {
}
