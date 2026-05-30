package com.bd2.seniorhomeapp.position.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PositionRequest(
        @NotNull
        @Size(max = 20)
        String name,

        @NotNull
        @Size(max = 400)
        String description
) {
}
