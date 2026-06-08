package com.bd2.seniorhomeapp.room.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.bd2.seniorhomeapp.room.dto.GameRoomEquipmentItem;
import com.bd2.seniorhomeapp.room.dto.request.RoomRequest;
import com.bd2.seniorhomeapp.room.dto.request.RoomUpdateRequest;
import com.bd2.seniorhomeapp.room.entity.GameRoomEquipment;
import com.bd2.seniorhomeapp.room.entity.Room;
import com.bd2.seniorhomeapp.room.entity.RoomType;
import com.bd2.seniorhomeapp.room.repository.RoomRepository;
import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;
import com.bd2.seniorhomeapp.seniorHome.repository.SeniorHomeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final SeniorHomeRepository seniorHomeRepository;

    @Override
    public List<Room> findRooms(Long id, Long seniorHomeId) {
        if (id != null) {
            Room room = roomRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Room not found"));
            return List.of(room);
        }
        if (seniorHomeId != null) {
            return roomRepository.findBySeniorHomeId(seniorHomeId);
        }
        return roomRepository.findAll();
    }

    @Override
    public Room createRoom(RoomRequest roomRequest) {
        SeniorHome seniorHome = seniorHomeRepository.findById(roomRequest.seniorHomeId())
                .orElseThrow(() -> new RuntimeException("Senior home not found"));

        validateRoomData(
                roomRequest.inUse(),
                roomRequest.type(),
                roomRequest.capacity(),
                roomRequest.standard(),
                roomRequest.officeKind(),
                roomRequest.gameEquipment());

        Room room = new Room();
        room.setRoomNumber(roomRequest.roomNumber());
        room.setFloor(roomRequest.floor());
        room.setInUse(roomRequest.inUse());
        room.setSeniorHome(seniorHome);
        applyTypeSpecificFields(room, roomRequest.inUse(), roomRequest.type(),
                roomRequest.capacity(), roomRequest.standard(),
                roomRequest.officeKind(), roomRequest.equipmentDescription(),
                roomRequest.gameEquipment());

        return roomRepository.save(room);
    }

    @Override
    public Room updateRoom(Long id, RoomUpdateRequest roomUpdateRequest) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (roomUpdateRequest.roomNumber() != null && !roomUpdateRequest.roomNumber().isBlank()) {
            room.setRoomNumber(roomUpdateRequest.roomNumber());
        }
        if (roomUpdateRequest.floor() != null) {
            room.setFloor(roomUpdateRequest.floor());
        }
        if (roomUpdateRequest.seniorHomeId() != null) {
            SeniorHome seniorHome = seniorHomeRepository.findById(roomUpdateRequest.seniorHomeId())
                    .orElseThrow(() -> new RuntimeException("Senior home not found"));
            room.setSeniorHome(seniorHome);
        }

        boolean inUse = roomUpdateRequest.inUse() != null ? roomUpdateRequest.inUse() : room.getInUse();
        RoomType type = roomUpdateRequest.type() != null ? roomUpdateRequest.type() : room.getType();
        Integer capacity = roomUpdateRequest.capacity() != null ? roomUpdateRequest.capacity() : room.getCapacity();
        String standard = roomUpdateRequest.standard() != null ? roomUpdateRequest.standard() : room.getStandard();
        String officeKind = roomUpdateRequest.officeKind() != null
                ? roomUpdateRequest.officeKind() : room.getOfficeKind();
        List<GameRoomEquipmentItem> gameEquipment = roomUpdateRequest.gameEquipment() != null
                ? roomUpdateRequest.gameEquipment()
                : toEquipmentItems(room.getGameEquipment());

        if (roomUpdateRequest.inUse() != null) {
            room.setInUse(roomUpdateRequest.inUse());
        }

        validateRoomData(inUse, type, capacity, standard, officeKind, gameEquipment);

        applyTypeSpecificFields(room, inUse, type,
                capacity, standard, officeKind,
                roomUpdateRequest.equipmentDescription() != null
                        ? roomUpdateRequest.equipmentDescription() : room.getEquipmentDescription(),
                gameEquipment);

        return roomRepository.save(room);
    }

    @Override
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Room not found");
        }
        roomRepository.deleteById(id);
    }

    private void validateRoomData(
            boolean inUse,
            RoomType type,
            Integer capacity,
            String standard,
            String officeKind,
            List<GameRoomEquipmentItem> gameEquipment) {
        if (inUse && type == null) {
            throw new RuntimeException("Utilized room must have a type");
        }
        if (!inUse) {
            return;
        }

        switch (type) {
            case RESIDENTIAL -> {
                if (capacity == null || capacity < 1) {
                    throw new RuntimeException("Residential room must have at least one bed");
                }
                if (standard == null || standard.isBlank()) {
                    throw new RuntimeException("Residential room must have a standard");
                }
            }
            case OFFICE -> {
                if (officeKind == null || officeKind.isBlank()) {
                    throw new RuntimeException("Office must have a kind");
                }
            }
            case GAME_ROOM -> validateGameEquipment(gameEquipment);
        }
    }

    private void validateGameEquipment(List<GameRoomEquipmentItem> gameEquipment) {
        if (gameEquipment == null) {
            return;
        }
        Set<com.bd2.seniorhomeapp.room.entity.GameRoomEquipmentType> types = new HashSet<>();
        for (GameRoomEquipmentItem item : gameEquipment) {
            if (!types.add(item.type())) {
                throw new RuntimeException("Duplicate game room equipment type: " + item.type());
            }
        }
    }

    private void applyTypeSpecificFields(
            Room room,
            boolean inUse,
            RoomType type,
            Integer capacity,
            String standard,
            String officeKind,
            String equipmentDescription,
            List<GameRoomEquipmentItem> gameEquipment) {
        if (!inUse) {
            room.setType(null);
            clearTypeSpecificFields(room);
            return;
        }

        room.setType(type);
        clearTypeSpecificFields(room);

        switch (type) {
            case RESIDENTIAL -> {
                room.setCapacity(capacity);
                room.setStandard(standard);
            }
            case OFFICE -> {
                room.setOfficeKind(officeKind);
                room.setEquipmentDescription(equipmentDescription);
            }
            case GAME_ROOM -> room.setGameEquipment(toEquipmentEntities(gameEquipment));
        }
    }

    private void clearTypeSpecificFields(Room room) {
        room.setCapacity(null);
        room.setStandard(null);
        room.setOfficeKind(null);
        room.setEquipmentDescription(null);
        room.setGameEquipment(new ArrayList<>());
    }

    private List<GameRoomEquipment> toEquipmentEntities(List<GameRoomEquipmentItem> items) {
        if (items == null) {
            return new ArrayList<>();
        }
        return items.stream()
                .map(item -> new GameRoomEquipment(item.type(), item.quantity()))
                .toList();
    }

    private List<GameRoomEquipmentItem> toEquipmentItems(List<GameRoomEquipment> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(entity -> new GameRoomEquipmentItem(entity.getType(), entity.getQuantity()))
                .toList();
    }
}
