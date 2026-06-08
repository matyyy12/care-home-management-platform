package com.bd2.seniorhomeapp.room.dto.response;

import java.util.List;

import com.bd2.seniorhomeapp.room.dto.GameRoomEquipmentItem;
import com.bd2.seniorhomeapp.room.entity.RoomType;

public record RoomResponse(
        Long id,
        String number,
        Integer floor,
        String seniorHome,
        Long seniorHomeId,
        Boolean inUse,
        RoomType type,
        Integer capacity,
        String standard,
        int assignedSeniors,
        String officeKind,
        String equipment,
        List<GameRoomEquipmentItem> gameEquipment
) {
}
