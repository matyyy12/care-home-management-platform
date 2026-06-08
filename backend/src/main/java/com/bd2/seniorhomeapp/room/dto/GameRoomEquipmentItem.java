package com.bd2.seniorhomeapp.room.dto;

import com.bd2.seniorhomeapp.room.entity.GameRoomEquipmentType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GameRoomEquipmentItem(
        @NotNull
        GameRoomEquipmentType type,

        @NotNull
        @Min(1)
        Integer quantity
) {
}
