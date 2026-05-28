package com.bd2.seniorhomeapp.address.controller;

import com.bd2.seniorhomeapp.address.dto.request.AddressRequest;
import com.bd2.seniorhomeapp.address.dto.request.AddressUpdateRequest;
import com.bd2.seniorhomeapp.address.dto.response.AddressResponse;
import com.bd2.seniorhomeapp.address.entity.Address;
import com.bd2.seniorhomeapp.address.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/address")
public class AddressController {
    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<?> getAllAddresses(@RequestParam(required = false) Long id,
                                             @RequestParam(required = false) String city) {
        List<AddressResponse> addresses = addressService.findAddresses(id, city).
                stream().map(this::mapToResponse).toList();
        return ResponseEntity.status(HttpStatus.OK).body(addresses);
    }

    @PostMapping
    public ResponseEntity<?> postAddress(@Valid @RequestBody AddressRequest addressRequest) {
        Address address = addressService.createAddress(addressRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(address));
    }

    @PatchMapping
    public ResponseEntity<?> updateAddress(@Valid @RequestBody AddressUpdateRequest addressUpdateRequest,
                                           @RequestParam Long id) {
        Address address = addressService.updateAddress(addressUpdateRequest, id);
        return ResponseEntity.status(HttpStatus.OK).body(mapToResponse(address));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAddress(@RequestParam Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    private AddressResponse mapToResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getCity(),
                address.getStreet(),
                address.getNumber(),
                address.getPostOffice().getId(),
                address.getPostOffice().getPostalCode(),
                address.getPostOffice().getCity()
        );
    }
}
