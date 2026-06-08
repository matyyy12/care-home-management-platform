package com.bd2.seniorhomeapp.assignment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bd2.seniorhomeapp.assignment.dto.request.AssignSeniorToRoomRequest;
import com.bd2.seniorhomeapp.assignment.dto.response.SeniorRoomAssignmentResponse;
import com.bd2.seniorhomeapp.assignment.service.SeniorRoomAssignmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/assignments/senior-rooms")
@RequiredArgsConstructor
public class SeniorRoomAssignmentController {

    private final SeniorRoomAssignmentService seniorRoomAssignmentService;

    @GetMapping
    public ResponseEntity<List<SeniorRoomAssignmentResponse>> getAssignments(
            @RequestParam(required = false) Long seniorHomeId) {
        return ResponseEntity.ok(seniorRoomAssignmentService.findAssignments(seniorHomeId));
    }

    @PostMapping
    public ResponseEntity<SeniorRoomAssignmentResponse> assign(
            @Valid @RequestBody AssignSeniorToRoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(seniorRoomAssignmentService.assign(request));
    }

    @DeleteMapping("/{seniorId}")
    public ResponseEntity<Void> unassign(@PathVariable Long seniorId) {
        seniorRoomAssignmentService.unassign(seniorId);
        return ResponseEntity.noContent().build();
    }
}
