package com.bd2.seniorhomeapp.equipment.repository;

import com.bd2.seniorhomeapp.equipment.entity.EquipmentModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EquipmentModelRepository extends JpaRepository<EquipmentModel, Long> {
    Optional<EquipmentModel> findByBrandIgnoreCaseAndNameIgnoreCase(String brand, String name);
}
