package com.bd2.seniorhomeapp.position.controller;

import com.bd2.seniorhomeapp.position.dto.request.PositionRequest;
import com.bd2.seniorhomeapp.position.dto.request.PositionUpdateRequest;
import com.bd2.seniorhomeapp.position.dto.response.PositionResponse;
import com.bd2.seniorhomeapp.position.entity.Position;
import com.bd2.seniorhomeapp.position.service.PositionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class PositionController {
    private final PositionService positionService;

    @GetMapping
    public ResponseEntity<?> getPositions() {
        List<PositionResponse> positions = positionService.getPositions()
                .stream().map(this::mapToResponse).toList();
        return ResponseEntity.status(HttpStatus.OK).body(positions);
    }

    @PostMapping
    public ResponseEntity<?> createPosition(@Valid @RequestBody PositionRequest positionRequest) {
        Position position = positionService.createPosition(positionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(position));
    }

    @PatchMapping
    public ResponseEntity<?> updatePosition(@Valid @RequestBody PositionUpdateRequest positionUpdateRequest,
                                            @RequestParam Long id) {
        Position position = positionService.updatePosition(positionUpdateRequest, id);
        return ResponseEntity.status(HttpStatus.OK).body(mapToResponse(position));
    }

    @DeleteMapping
    public ResponseEntity<?> deletePosition(@RequestParam Long id) {
        positionService.deletePosition(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    private PositionResponse mapToResponse(Position position) {
        return new PositionResponse(
                position.getId(),
                position.getName(),
                position.getDescription()
        );
    }

}
