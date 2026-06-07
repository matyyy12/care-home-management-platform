package com.bd2.seniorhomeapp.activity.dto.request;

import com.bd2.seniorhomeapp.activity.entity.ActivityType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ActivityRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @NotNull
        ActivityType type,

        @NotNull
        @DecimalMin("0")
        BigDecimal price,

        @NotNull
        Long seniorHomeId,

        @Size(max = 500)
        String description
) {
}
