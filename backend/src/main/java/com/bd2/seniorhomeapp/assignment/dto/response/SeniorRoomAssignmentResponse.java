package com.bd2.seniorhomeapp.assignment.dto.response;

public record SeniorRoomAssignmentResponse(
        Long seniorId,
        String seniorName,
        Long roomId,
        String roomNumber,
        Long seniorHomeId,
        String seniorHomeName
) {
}
