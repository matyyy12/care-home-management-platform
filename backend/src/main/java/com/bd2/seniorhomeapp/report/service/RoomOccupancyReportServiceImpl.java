package com.bd2.seniorhomeapp.report.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bd2.seniorhomeapp.report.dto.RoomOccupancyReportRow;
import com.bd2.seniorhomeapp.room.entity.Room;
import com.bd2.seniorhomeapp.room.entity.RoomType;
import com.bd2.seniorhomeapp.room.repository.RoomRepository;
import com.bd2.seniorhomeapp.senior.entity.SeniorStatus;
import com.bd2.seniorhomeapp.senior.repository.SeniorRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomOccupancyReportServiceImpl implements RoomOccupancyReportService {

    private final RoomRepository roomRepository;
    private final SeniorRepository seniorRepository;

    @Override
    public List<RoomOccupancyReportRow> generateReport(Long seniorHomeId, Integer floor, String standard) {
        List<Room> rooms = seniorHomeId != null
                ? roomRepository.findBySeniorHomeId(seniorHomeId)
                : roomRepository.findAll();

        return rooms.stream()
                .filter(room -> Boolean.TRUE.equals(room.getInUse()))
                .filter(room -> room.getType() == RoomType.RESIDENTIAL)
                .filter(room -> floor == null || floor.equals(room.getFloor()))
                .filter(room -> standard == null || standard.isBlank()
                        || standard.equalsIgnoreCase(room.getStandard()))
                .map(this::mapRow)
                .sorted(Comparator
                        .comparing(RoomOccupancyReportRow::seniorHome)
                        .thenComparing(RoomOccupancyReportRow::floor)
                        .thenComparing(RoomOccupancyReportRow::roomNumber))
                .toList();
    }

    private RoomOccupancyReportRow mapRow(Room room) {
        int assigned = (int) seniorRepository.countByRoom_IdAndStatus(room.getId(), SeniorStatus.ACTIVE);
        int capacity = room.getCapacity() != null ? room.getCapacity() : 0;
        int free = Math.max(0, capacity - assigned);

        return new RoomOccupancyReportRow(
                room.getId(),
                room.getRoomNumber(),
                room.getFloor(),
                room.getSeniorHome().getName(),
                room.getSeniorHome().getId(),
                capacity,
                assigned,
                free,
                room.getStandard());
    }
}
