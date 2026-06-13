package com.bd2.seniorhomeapp.equipment.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EquipmentRequest(
        @NotBlank
        @Size(max = 30)
        String inventoryNumber,

        @NotBlank
        @Size(max = 50)
        String name,

        @Size(max = 400)
        String description,

        LocalDate purchaseDate,

        @Valid
        @NotNull
        EquipmentModelRequest model
) {
}
