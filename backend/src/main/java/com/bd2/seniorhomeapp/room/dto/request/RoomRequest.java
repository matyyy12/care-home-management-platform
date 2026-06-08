package com.bd2.seniorhomeapp.room.dto.request;

import java.util.List;

import com.bd2.seniorhomeapp.room.dto.GameRoomEquipmentItem;
import com.bd2.seniorhomeapp.room.entity.RoomType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RoomRequest(
        @NotBlank
        @Size(max = 20)
        String roomNumber,

        @NotNull
        Integer floor,

        @NotNull
        Boolean inUse,

        @NotNull
        Long seniorHomeId,

        RoomType type,

        @Min(1)
        Integer capacity,

        @Size(max = 50)
        String standard,

        @Size(max = 100)
        String officeKind,

        @Size(max = 200)
        String equipmentDescription,

        @Valid
        List<GameRoomEquipmentItem> gameEquipment
) {
}
