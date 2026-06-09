package com.bd2.seniorhomeapp.equipment.controller;

import com.bd2.seniorhomeapp.equipment.dto.request.EquipmentRequest;
import com.bd2.seniorhomeapp.equipment.dto.request.EquipmentUpdateRequest;
import com.bd2.seniorhomeapp.equipment.dto.response.EquipmentResponse;
import com.bd2.seniorhomeapp.equipment.entity.Equipment;
import com.bd2.seniorhomeapp.equipment.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {
    private final EquipmentService equipmentService;

    @GetMapping
    public ResponseEntity<?> getEquipment(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) Long modelId) {
        List<EquipmentResponse> response = equipmentService.findEquipment(id, modelId)
                .stream().map(this::mapToResponse).toList();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<?> createEquipment(@Valid @RequestBody EquipmentRequest equipmentRequest) {
        Equipment equipment = equipmentService.createEquipment(equipmentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(equipment));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateEquipment(@Valid @RequestBody EquipmentUpdateRequest equipmentUpdateRequest,
                                             @PathVariable Long id) {
        Equipment equipment = equipmentService.updateEquipment(id, equipmentUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(mapToResponse(equipment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEquipment(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private EquipmentResponse mapToResponse(Equipment equipment) {
        return new EquipmentResponse(
                equipment.getId(),
                equipment.getInventoryNumber(),
                equipment.getName(),
                equipment.getDescription(),
                equipment.getPurchaseDate(),
                equipment.getModel().getId(),
                equipment.getModel().getName(),
                equipment.getModel().getBrand()
        );
    }
}
