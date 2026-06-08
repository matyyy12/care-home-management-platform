package com.bd2.seniorhomeapp.room.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bd2.seniorhomeapp.room.dto.GameRoomEquipmentItem;
import com.bd2.seniorhomeapp.room.dto.request.RoomRequest;
import com.bd2.seniorhomeapp.room.dto.request.RoomUpdateRequest;
import com.bd2.seniorhomeapp.room.dto.response.RoomResponse;
import com.bd2.seniorhomeapp.room.entity.GameRoomEquipment;
import com.bd2.seniorhomeapp.room.entity.Room;
import com.bd2.seniorhomeapp.room.service.RoomService;
import com.bd2.seniorhomeapp.senior.entity.SeniorStatus;
import com.bd2.seniorhomeapp.senior.repository.SeniorRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final SeniorRepository seniorRepository;

    @GetMapping
    public ResponseEntity<?> getRooms(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) Long seniorHomeId) {
        List<RoomResponse> rooms = roomService.findRooms(id, seniorHomeId)
                .stream().map(this::mapToResponse).toList();
        return ResponseEntity.status(HttpStatus.OK).body(rooms);
    }

    @PostMapping
    public ResponseEntity<?> createRoom(@Valid @RequestBody RoomRequest roomRequest) {
        Room room = roomService.createRoom(roomRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(room));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateRoom(
            @Valid @RequestBody RoomUpdateRequest roomUpdateRequest,
            @PathVariable Long id) {
        Room room = roomService.updateRoom(id, roomUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(mapToResponse(room));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private RoomResponse mapToResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getRoomNumber(),
                room.getFloor(),
                room.getSeniorHome().getName(),
                room.getSeniorHome().getId(),
                room.getInUse(),
                room.getType(),
                room.getCapacity(),
                room.getStandard(),
                countAssignedSeniors(room.getId()),
                room.getOfficeKind(),
                room.getEquipmentDescription(),
                mapGameEquipment(room.getGameEquipment())
        );
    }

    private int countAssignedSeniors(Long roomId) {
        return (int) seniorRepository.countByRoom_IdAndStatus(roomId, SeniorStatus.ACTIVE);
    }

    private List<GameRoomEquipmentItem> mapGameEquipment(List<GameRoomEquipment> gameEquipment) {
        if (gameEquipment == null) {
            return List.of();
        }
        return gameEquipment.stream()
                .map(item -> new GameRoomEquipmentItem(item.getType(), item.getQuantity()))
                .toList();
    }
}
