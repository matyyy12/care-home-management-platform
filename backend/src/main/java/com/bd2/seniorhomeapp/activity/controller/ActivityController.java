package com.bd2.seniorhomeapp.activity.controller;

import com.bd2.seniorhomeapp.activity.dto.request.ActivityRequest;
import com.bd2.seniorhomeapp.activity.dto.request.ActivityUpdateRequest;
import com.bd2.seniorhomeapp.activity.dto.response.ActivityResponse;
import com.bd2.seniorhomeapp.activity.entity.Activity;
import com.bd2.seniorhomeapp.activity.service.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping("/{seniorHomeId}")
    public ResponseEntity<?> getActivitiesFromSeniorHome(@PathVariable Long seniorHomeId) {
        List<ActivityResponse> activities = activityService.getActivitiesFromSeniorHome(seniorHomeId)
                .stream().map(this::mapToResponse).toList();
        return ResponseEntity.status(HttpStatus.OK).body(activities);
    }

    @PostMapping
    public ResponseEntity<?> createActivity(@Valid @RequestBody ActivityRequest activityRequest) {
        Activity activity = activityService.createActivity(activityRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(activity));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateActivity(@Valid @RequestBody ActivityUpdateRequest activityUpdateRequest,
                                            @PathVariable Long id) {
        Activity activity = activityService.updateActivity(activityUpdateRequest, id);
        return ResponseEntity.status(HttpStatus.OK).body(mapToResponse(activity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private ActivityResponse mapToResponse(Activity activity) {
        return new ActivityResponse(
                activity.getId(),
                activity.getName(),
                activity.getType(),
                activity.getPrice(),
                activity.getSeniorHome().getName(),
                activity.getDescription(),
                0,
                0
        );
    }
}
