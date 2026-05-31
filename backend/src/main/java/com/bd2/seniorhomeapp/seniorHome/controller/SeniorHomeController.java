package com.bd2.seniorhomeapp.seniorHome.controller;

import com.bd2.seniorhomeapp.seniorHome.dto.request.SeniorHomeRequest;
import com.bd2.seniorhomeapp.seniorHome.dto.request.SeniorHomeUpdateRequest;
import com.bd2.seniorhomeapp.seniorHome.dto.response.SeniorHomeResponse;
import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;
import com.bd2.seniorhomeapp.seniorHome.service.SeniorHomeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/senior-home")
public class SeniorHomeController {

    private final SeniorHomeService seniorHomeService;

    @GetMapping
    public ResponseEntity<?> getHomes(@RequestParam(required = false) Long homeId) {
        List<SeniorHomeResponse> seniorHome = seniorHomeService.findHomes(homeId)
                .stream().map(this::mapToResponse).toList();
        return ResponseEntity.status(HttpStatus.OK).body(seniorHome);
    }

    @PostMapping
    public ResponseEntity<?> createHome(@Valid @RequestBody SeniorHomeRequest seniorHomeRequest) {
        SeniorHome seniorHome = seniorHomeService.createHome(seniorHomeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(seniorHome));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateHome(@Valid @RequestBody SeniorHomeUpdateRequest seniorHomeUpdateRequest,
                                        @PathVariable Long id) {
        SeniorHome seniorHome = seniorHomeService.updateHome(seniorHomeUpdateRequest, id);
        return ResponseEntity.status(HttpStatus.OK).body(mapToResponse(seniorHome));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHome(@PathVariable Long id) {
        seniorHomeService.deleteHome(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    private SeniorHomeResponse mapToResponse(SeniorHome seniorHome){
        return new SeniorHomeResponse(
                seniorHome.getId(),
                seniorHome.getName(),
                seniorHome.getPhoneNumber(),
                seniorHome.getEmail(),
                seniorHome.getWebsite(),
                seniorHome.getAddress().getCity(),
                seniorHome.getAddress().getStreet(),
                seniorHome.getAddress().getNumber()
        );
    }
}
