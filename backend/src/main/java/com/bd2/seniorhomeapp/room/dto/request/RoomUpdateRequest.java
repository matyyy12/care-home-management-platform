package com.bd2.seniorhomeapp.room.dto.request;

import java.util.List;

import com.bd2.seniorhomeapp.room.dto.GameRoomEquipmentItem;
import com.bd2.seniorhomeapp.room.entity.RoomType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record RoomUpdateRequest(
        @Size(max = 20)
        String roomNumber,

        Integer floor,

        Boolean inUse,

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
