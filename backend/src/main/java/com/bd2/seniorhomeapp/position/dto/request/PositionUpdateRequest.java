package com.bd2.seniorhomeapp.position.dto.request;

import jakarta.validation.constraints.Size;

public record PositionUpdateRequest(
        @Size(max = 20)
        String name,

        @Size(max = 400)
        String description
) {
}
