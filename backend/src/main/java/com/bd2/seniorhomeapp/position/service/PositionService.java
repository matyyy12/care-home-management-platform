package com.bd2.seniorhomeapp.position.service;

import com.bd2.seniorhomeapp.position.dto.request.PositionRequest;
import com.bd2.seniorhomeapp.position.dto.request.PositionUpdateRequest;
import com.bd2.seniorhomeapp.position.entity.Position;

import java.util.List;

public interface PositionService {
    List<Position> getPositions();
    Position createPosition(PositionRequest positionRequest);
    Position updatePosition(PositionUpdateRequest positionUpdateRequest, Long id);
    void deletePosition(Long id);
}
