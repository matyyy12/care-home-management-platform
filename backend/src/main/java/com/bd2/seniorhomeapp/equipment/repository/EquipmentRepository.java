package com.bd2.seniorhomeapp.equipment.repository;

import com.bd2.seniorhomeapp.equipment.entity.Equipment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    @Override
    @EntityGraph(attributePaths = "model")
    List<Equipment> findAll();

    @Override
    @EntityGraph(attributePaths = "model")
    Optional<Equipment> findById(Long id);

    @EntityGraph(attributePaths = "model")
    List<Equipment> findByModel_Id(Long modelId);
}
