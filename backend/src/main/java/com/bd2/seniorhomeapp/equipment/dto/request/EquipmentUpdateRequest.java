package com.bd2.seniorhomeapp.equipment.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EquipmentUpdateRequest(
        @Size(max = 30)
        String inventoryNumber,

        @Size(max = 50)
        String name,

        @Size(max = 400)
        String description,

        LocalDate purchaseDate,

        @Valid
        EquipmentModelRequest model
) {
}
