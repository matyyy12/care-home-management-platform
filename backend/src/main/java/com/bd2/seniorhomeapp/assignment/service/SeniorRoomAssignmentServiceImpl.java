package com.bd2.seniorhomeapp.assignment.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bd2.seniorhomeapp.assignment.dto.request.AssignSeniorToRoomRequest;
import com.bd2.seniorhomeapp.assignment.dto.response.SeniorRoomAssignmentResponse;
import com.bd2.seniorhomeapp.room.entity.Room;
import com.bd2.seniorhomeapp.room.entity.RoomType;
import com.bd2.seniorhomeapp.room.repository.RoomRepository;
import com.bd2.seniorhomeapp.senior.entity.Senior;
import com.bd2.seniorhomeapp.senior.entity.SeniorStatus;
import com.bd2.seniorhomeapp.senior.repository.SeniorRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SeniorRoomAssignmentServiceImpl implements SeniorRoomAssignmentService {

    private final SeniorRepository seniorRepository;
    private final RoomRepository roomRepository;

    @Override
    public List<SeniorRoomAssignmentResponse> findAssignments(Long seniorHomeId) {
        List<Senior> seniors = seniorHomeId != null
                ? seniorRepository.findBySeniorHome_IdAndRoomIsNotNull(seniorHomeId)
                : seniorRepository.findByRoomIsNotNull();
        return seniors.stream().map(this::mapToResponse).toList();
    }

    @Override
    public SeniorRoomAssignmentResponse assign(AssignSeniorToRoomRequest request) {
        Senior senior = seniorRepository.findById(request.seniorId())
                .orElseThrow(() -> new RuntimeException("Senior not found"));
        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        validateAssignableSenior(senior);
        validateResidentialRoom(room);
        validateSameSeniorHome(senior, room);

        Long currentRoomId = senior.getRoom() != null ? senior.getRoom().getId() : null;
        if (!room.getId().equals(currentRoomId)) {
            long occupied = seniorRepository.countByRoom_IdAndStatus(room.getId(), SeniorStatus.ACTIVE);
            if (occupied >= room.getCapacity()) {
                throw new RuntimeException("Room capacity exceeded");
            }
        }

        senior.setRoom(room);
        return mapToResponse(seniorRepository.save(senior));
    }

    @Override
    public void unassign(Long seniorId) {
        Senior senior = seniorRepository.findById(seniorId)
                .orElseThrow(() -> new RuntimeException("Senior not found"));
        senior.setRoom(null);
        seniorRepository.save(senior);
    }

    private void validateAssignableSenior(Senior senior) {
        if (senior.getStatus() != SeniorStatus.ACTIVE) {
            throw new RuntimeException("Only active seniors can be assigned to a room");
        }
        if (senior.getDischargeDate() != null) {
            throw new RuntimeException("Discharged seniors cannot be assigned to a room");
        }
    }

    private void validateResidentialRoom(Room room) {
        if (!Boolean.TRUE.equals(room.getInUse())) {
            throw new RuntimeException("Room must be in use");
        }
        if (room.getType() != RoomType.RESIDENTIAL) {
            throw new RuntimeException("Senior can only be assigned to a residential room");
        }
        if (room.getCapacity() == null || room.getCapacity() < 1) {
            throw new RuntimeException("Residential room must have capacity");
        }
    }

    private void validateSameSeniorHome(Senior senior, Room room) {
        if (!senior.getSeniorHome().getId().equals(room.getSeniorHome().getId())) {
            throw new RuntimeException("Senior and room must belong to the same senior home");
        }
    }

    private SeniorRoomAssignmentResponse mapToResponse(Senior senior) {
        Room room = senior.getRoom();
        return new SeniorRoomAssignmentResponse(
                senior.getSeniorId(),
                senior.getSeniorId(),
                senior.getFirstName() + " " + senior.getLastName(),
                room.getId(),
                room.getRoomNumber(),
                senior.getSeniorHome().getId(),
                senior.getSeniorHome().getName());
    }
}
