package com.bd2.seniorhomeapp.equipment.service;

import com.bd2.seniorhomeapp.equipment.dto.request.EquipmentRequest;
import com.bd2.seniorhomeapp.equipment.dto.request.EquipmentUpdateRequest;
import com.bd2.seniorhomeapp.equipment.entity.Equipment;

import java.util.List;

public interface EquipmentService {
    List<Equipment> findEquipment(Long id, Long modelId);

    Equipment createEquipment(EquipmentRequest equipmentRequest);

    Equipment updateEquipment(Long id, EquipmentUpdateRequest equipmentUpdateRequest);

    void deleteEquipment(Long id);
}
