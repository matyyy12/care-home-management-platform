package com.bd2.seniorhomeapp.assignment.service;

import java.util.List;

import com.bd2.seniorhomeapp.assignment.dto.request.AssignSeniorToRoomRequest;
import com.bd2.seniorhomeapp.assignment.dto.response.SeniorRoomAssignmentResponse;

public interface SeniorRoomAssignmentService {

    List<SeniorRoomAssignmentResponse> findAssignments(Long seniorHomeId);

    SeniorRoomAssignmentResponse assign(AssignSeniorToRoomRequest request);

    void unassign(Long seniorId);
}
