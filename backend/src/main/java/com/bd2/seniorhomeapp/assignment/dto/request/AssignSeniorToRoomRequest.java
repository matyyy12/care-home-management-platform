package com.bd2.seniorhomeapp.assignment.dto.request;

import jakarta.validation.constraints.NotNull;

public record AssignSeniorToRoomRequest(
        @NotNull
        Long seniorId,

        @NotNull
        Long roomId
) {
}
