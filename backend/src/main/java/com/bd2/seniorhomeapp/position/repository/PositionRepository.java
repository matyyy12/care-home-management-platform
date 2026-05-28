package com.bd2.seniorhomeapp.position.repository;

import com.bd2.seniorhomeapp.position.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {
}
