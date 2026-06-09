package com.bd2.seniorhomeapp.equipment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EquipmentModelRequest(
        @NotBlank
        @Size(max = 30)
        String brand,

        @NotBlank
        @Size(max = 30)
        String name
) {
}
