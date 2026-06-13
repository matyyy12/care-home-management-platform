package com.bd2.seniorhomeapp.report.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.bd2.seniorhomeapp.activity.entity.Activity;
import com.bd2.seniorhomeapp.activity.repository.ActivityRepository;
import com.bd2.seniorhomeapp.employee.entity.Employee;
import com.bd2.seniorhomeapp.employee.repository.EmployeeRepository;
import com.bd2.seniorhomeapp.report.dto.RoomUtilizationReportRow;
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
public class RoomUtilizationReportServiceImpl implements RoomUtilizationReportService {

    private final RoomRepository roomRepository;
    private final SeniorRepository seniorRepository;
    private final EmployeeRepository employeeRepository;
    private final ActivityRepository activityRepository;

    @Override
    public List<RoomUtilizationReportRow> generateReport(Long seniorHomeId, Boolean inUse, RoomType type) {
        Map<Long, Integer> employeeCountByRoom = buildEmployeeCountByRoom();
        Map<Long, Integer> activityCountByRoom = buildActivityCountByRoom();

        List<Room> rooms = seniorHomeId != null
                ? roomRepository.findBySeniorHomeId(seniorHomeId)
                : roomRepository.findAll();

        return rooms.stream()
                .filter(room -> inUse == null || inUse.equals(room.getInUse()))
                .filter(room -> type == null || type == room.getType())
                .map(room -> mapRow(room, employeeCountByRoom, activityCountByRoom))
                .sorted(Comparator
                        .comparing(RoomUtilizationReportRow::seniorHome)
                        .thenComparing(RoomUtilizationReportRow::floor)
                        .thenComparing(RoomUtilizationReportRow::roomNumber))
                .toList();
    }

    private RoomUtilizationReportRow mapRow(
            Room room,
            Map<Long, Integer> employeeCountByRoom,
            Map<Long, Integer> activityCountByRoom) {
        int seniorCount = (int) seniorRepository.countByRoom_IdAndStatus(room.getId(), SeniorStatus.ACTIVE);
        int employeeCount = employeeCountByRoom.getOrDefault(room.getId(), 0);
        int activityCount = activityCountByRoom.getOrDefault(room.getId(), 0);
        boolean inUseWithoutType = Boolean.TRUE.equals(room.getInUse()) && room.getType() == null;
        String warningMessage = warningMessage(room, inUseWithoutType);

        return new RoomUtilizationReportRow(
                room.getId(),
                room.getRoomNumber(),
                room.getFloor(),
                room.getSeniorHome().getName(),
                room.getSeniorHome().getId(),
                room.getInUse(),
                room.getType(),
                seniorCount,
                employeeCount,
                activityCount,
                inUseWithoutType,
                warningMessage != null,
                warningMessage);
    }

    private String warningMessage(Room room, boolean inUseWithoutType) {
        if (inUseWithoutType) {
            return "Pomieszczenie w użyciu bez przypisanego typu";
        }
        if (Boolean.TRUE.equals(room.getInUse())
                && room.getType() == RoomType.RESIDENTIAL
                && room.getCapacity() == null) {
            return "Pokój mieszkalny w użyciu bez określonej pojemności";
        }
        return null;
    }

    private Map<Long, Integer> buildEmployeeCountByRoom() {
        Map<Long, Integer> counts = new HashMap<>();
        for (Employee employee : employeeRepository.findAll()) {
            for (Room room : employee.getRooms()) {
                counts.merge(room.getId(), 1, Integer::sum);
            }
        }
        return counts;
    }

    private Map<Long, Integer> buildActivityCountByRoom() {
        Map<Long, Integer> counts = new HashMap<>();
        for (Activity activity : activityRepository.findAll()) {
            for (Room room : activity.getRooms()) {
                counts.merge(room.getId(), 1, Integer::sum);
            }
        }
        return counts;
    }
}
