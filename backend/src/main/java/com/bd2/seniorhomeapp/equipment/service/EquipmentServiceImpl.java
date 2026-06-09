package com.bd2.seniorhomeapp.equipment.service;

import com.bd2.seniorhomeapp.equipment.dto.request.EquipmentModelRequest;
import com.bd2.seniorhomeapp.equipment.dto.request.EquipmentRequest;
import com.bd2.seniorhomeapp.equipment.dto.request.EquipmentUpdateRequest;
import com.bd2.seniorhomeapp.equipment.entity.Equipment;
import com.bd2.seniorhomeapp.equipment.entity.EquipmentModel;
import com.bd2.seniorhomeapp.equipment.repository.EquipmentModelRepository;
import com.bd2.seniorhomeapp.equipment.repository.EquipmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {
    private final EquipmentRepository equipmentRepository;
    private final EquipmentModelRepository equipmentModelRepository;

    @Override
    public List<Equipment> findEquipment(Long id, Long modelId) {
        if (id != null) {
            return List.of(findEquipmentById(id));
        }
        if (modelId != null) {
            return equipmentRepository.findByModel_Id(modelId);
        }
        return equipmentRepository.findAll();
    }

    @Override
    public Equipment createEquipment(EquipmentRequest equipmentRequest) {
        Equipment equipment = new Equipment();
        equipment.setInventoryNumber(equipmentRequest.inventoryNumber().trim());
        equipment.setName(equipmentRequest.name().trim());
        equipment.setDescription(trimToNull(equipmentRequest.description()));
        equipment.setPurchaseDate(equipmentRequest.purchaseDate());
        equipment.setModel(findOrCreateModel(equipmentRequest.model()));

        return equipmentRepository.save(equipment);
    }

    @Override
    public Equipment updateEquipment(Long id, EquipmentUpdateRequest equipmentUpdateRequest) {
        Equipment equipment = findEquipmentById(id);

        if (equipmentUpdateRequest.inventoryNumber() != null && !equipmentUpdateRequest.inventoryNumber().isBlank()) {
            equipment.setInventoryNumber(equipmentUpdateRequest.inventoryNumber().trim());
        }
        if (equipmentUpdateRequest.name() != null && !equipmentUpdateRequest.name().isBlank()) {
            equipment.setName(equipmentUpdateRequest.name().trim());
        }
        if (equipmentUpdateRequest.description() != null) {
            equipment.setDescription(trimToNull(equipmentUpdateRequest.description()));
        }
        if (equipmentUpdateRequest.purchaseDate() != null) {
            equipment.setPurchaseDate(equipmentUpdateRequest.purchaseDate());
        }
        if (equipmentUpdateRequest.model() != null) {
            equipment.setModel(findOrCreateModel(equipmentUpdateRequest.model()));
        }

        return equipment;
    }

    @Override
    public void deleteEquipment(Long id) {
        if (!equipmentRepository.existsById(id)) {
            throw new RuntimeException("Equipment not found");
        }
        equipmentRepository.deleteById(id);
    }

    private Equipment findEquipmentById(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));
    }

    private EquipmentModel findOrCreateModel(EquipmentModelRequest modelRequest) {
        String brand = modelRequest.brand().trim();
        String name = modelRequest.name().trim();

        return equipmentModelRepository.findByBrandIgnoreCaseAndNameIgnoreCase(brand, name)
                .orElseGet(() -> {
                    EquipmentModel model = new EquipmentModel();
                    model.setBrand(brand);
                    model.setName(name);
                    return equipmentModelRepository.save(model);
                });
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
