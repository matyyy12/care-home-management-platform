package com.bd2.seniorhomeapp.room.service;

import java.util.List;

import com.bd2.seniorhomeapp.room.dto.request.RoomRequest;
import com.bd2.seniorhomeapp.room.dto.request.RoomUpdateRequest;
import com.bd2.seniorhomeapp.room.entity.Room;

public interface RoomService {

    List<Room> findRooms(Long id, Long seniorHomeId);

    Room createRoom(RoomRequest roomRequest);

    Room updateRoom(Long id, RoomUpdateRequest roomUpdateRequest);

    void deleteRoom(Long id);
}
