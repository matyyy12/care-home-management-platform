package com.bd2.seniorhomeapp.owner.controller;

import com.bd2.seniorhomeapp.owner.dto.request.OwnerRequest;
import com.bd2.seniorhomeapp.owner.dto.request.OwnerUpdateRequest;
import com.bd2.seniorhomeapp.owner.dto.response.OwnerResponse;
import com.bd2.seniorhomeapp.owner.entity.Owner;
import com.bd2.seniorhomeapp.owner.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @GetMapping
    public ResponseEntity<?> getAllOwners(@RequestParam(required = false) Long id) {
        List<OwnerResponse> response = ownerService.getAllOwners(id)
                .stream().map(this::mapToResponse).toList();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<?> createOwner(@Valid @RequestBody OwnerRequest ownerRequest) {
        Owner owner = ownerService.createOwner(ownerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(owner));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateOwner(@Valid @RequestBody OwnerUpdateRequest ownerUpdateRequest,
                                         @PathVariable Long id) {
        Owner owner = ownerService.updateOwner(id, ownerUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(mapToResponse(owner));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOwner(@PathVariable Long id) {
        ownerService.deleteOwner(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private OwnerResponse mapToResponse(Owner owner){
        return new OwnerResponse(
                owner.getId(),
                owner.getName(),
                owner.getSurname(),
                owner.getAddress().getCity(),
                owner.getAddress().getStreet(),
                owner.getAddress().getNumber(),
                owner.getAddress().getPostOffice().getPostalCode(),
                owner.getSeniorHome().getId()
        );
    }
}
