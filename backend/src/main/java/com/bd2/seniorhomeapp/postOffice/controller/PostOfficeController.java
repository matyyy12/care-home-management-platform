package com.bd2.seniorhomeapp.postOffice.controller;


import com.bd2.seniorhomeapp.postOffice.dto.request.PostOfficeRequest;
import com.bd2.seniorhomeapp.postOffice.dto.request.PostOfficeUpdateRequest;
import com.bd2.seniorhomeapp.postOffice.dto.response.PostOfficeResponse;
import com.bd2.seniorhomeapp.postOffice.entity.PostOffice;
import com.bd2.seniorhomeapp.postOffice.service.PostOfficeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post-offices")
public class PostOfficeController {
    private final PostOfficeService postOfficeService;

    @GetMapping
    public ResponseEntity<?> getAllPostOffices(@RequestParam(required = false) Long id,
                                            @RequestParam(required = false) String city) {
        List<PostOfficeResponse> postOfficeResponses = postOfficeService.getPostOffices(id, city)
                .stream().map(this::mapToResponse).toList();
        return ResponseEntity.status(HttpStatus.OK).body(postOfficeResponses);
    }

    @PostMapping
    public ResponseEntity<?> createPostOffice(@Valid @RequestBody PostOfficeRequest postOfficeRequest) {
        PostOffice postOffice = postOfficeService.createPostOffice(postOfficeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(postOffice));
    }

    @PatchMapping
    public ResponseEntity<?> updatePostOffice(@Valid @RequestBody PostOfficeUpdateRequest postOfficeUpdateRequest,
                                              @RequestParam Long id) {
        PostOffice postOffice = postOfficeService.updatePostOffice(postOfficeUpdateRequest, id);
        return ResponseEntity.status(HttpStatus.OK).body(mapToResponse(postOffice));
    }

    @DeleteMapping
    public ResponseEntity<?> deletePostOffice(@RequestParam Long id) {
        postOfficeService.deletePostOffice(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private PostOfficeResponse mapToResponse(PostOffice postOffice) {
        return new PostOfficeResponse(
                postOffice.getId(),
                postOffice.getPostalCode(),
                postOffice.getCity()
        );
    }
}
