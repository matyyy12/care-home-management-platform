package com.bd2.seniorhomeapp.room.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bd2.seniorhomeapp.room.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findBySeniorHomeId(Long seniorHomeId);

    Optional<Room> findByRoomNumberAndSeniorHomeId(String roomNumber, Long seniorHomeId);
}
