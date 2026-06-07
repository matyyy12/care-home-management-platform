package com.bd2.seniorhomeapp.activity.dto.request;

import com.bd2.seniorhomeapp.activity.entity.ActivityType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ActivityUpdateRequest(
        @Size(max = 100)
        String name,

        ActivityType type,

        @DecimalMin("0")
        BigDecimal price,

        Long seniorHomeId,

        @Size(max = 500)
        String description
) {
}
