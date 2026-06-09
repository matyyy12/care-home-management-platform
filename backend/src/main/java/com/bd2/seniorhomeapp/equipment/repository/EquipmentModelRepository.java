package com.bd2.seniorhomeapp.equipment.repository;

import com.bd2.seniorhomeapp.equipment.entity.EquipmentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EquipmentModelRepository extends JpaRepository<EquipmentModel, Long> {
    Optional<EquipmentModel> findByBrandIgnoreCaseAndNameIgnoreCase(String brand, String name);
}
