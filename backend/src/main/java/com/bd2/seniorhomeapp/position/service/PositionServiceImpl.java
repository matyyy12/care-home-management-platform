package com.bd2.seniorhomeapp.position.service;

import com.bd2.seniorhomeapp.position.dto.request.PositionRequest;
import com.bd2.seniorhomeapp.position.dto.request.PositionUpdateRequest;
import com.bd2.seniorhomeapp.position.entity.Position;
import com.bd2.seniorhomeapp.position.repository.PositionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {
    private final PositionRepository positionRepository;

    @Override
    public List<Position> getPositions() {
        return positionRepository.findAll();
    }

    @Override
    public Position createPosition(PositionRequest positionRequest) {
        Position position = new Position();
        position.setName(positionRequest.name());
        position.setDescription(positionRequest.description());
        return positionRepository.save(position);
    }

    @Override
    public Position updatePosition(PositionUpdateRequest positionUpdateRequest, Long id) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Position not found"));

        if(positionUpdateRequest.name() != null && !positionUpdateRequest.name().isBlank()) {
            position.setName(positionUpdateRequest.name());
        }
        if(positionUpdateRequest.description() != null && !positionUpdateRequest.description().isBlank()) {
            position.setDescription(positionUpdateRequest.description());
        }
        return positionRepository.save(position);
    }

    @Override
    public void deletePosition(Long id) {
        if(!positionRepository.existsById(id)) {
            throw new RuntimeException("Position not found");
        }
        positionRepository.deleteById(id);
    }
}
