package com.bd2.seniorhomeapp.activity.service;

import com.bd2.seniorhomeapp.activity.dto.request.ActivityRequest;
import com.bd2.seniorhomeapp.activity.dto.request.ActivityUpdateRequest;
import com.bd2.seniorhomeapp.activity.entity.Activity;
import com.bd2.seniorhomeapp.activity.repository.ActivityRepository;
import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;
import com.bd2.seniorhomeapp.seniorHome.repository.SeniorHomeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final SeniorHomeRepository seniorHomeRepository;

    @Override
    public List<Activity> getActivitiesFromSeniorHome(Long seniorHomeId) {
        return activityRepository.findBySeniorHomeId(seniorHomeId); 
    }

    @Override
    public Activity createActivity(ActivityRequest activityRequest) {
        SeniorHome seniorHome = seniorHomeRepository.findById(activityRequest.seniorHomeId())
                .orElseThrow(() -> new RuntimeException("Senior home not found"));

        Activity activity = new Activity();
        activity.setName(activityRequest.name());
        activity.setType(activityRequest.type());
        activity.setPrice(activityRequest.price());
        activity.setDescription(activityRequest.description());
        activity.setSeniorHome(seniorHome);

        return activityRepository.save(activity);
    }

    @Override
    public Activity updateActivity(ActivityUpdateRequest activityUpdateRequest, Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        if (activityUpdateRequest.name() != null && !activityUpdateRequest.name().isBlank()) {
            activity.setName(activityUpdateRequest.name());
        }
        if (activityUpdateRequest.type() != null) {
            activity.setType(activityUpdateRequest.type());
        }
        if (activityUpdateRequest.price() != null) {
            activity.setPrice(activityUpdateRequest.price());
        }
        if (activityUpdateRequest.seniorHomeId() != null) {
            SeniorHome seniorHome = seniorHomeRepository.findById(activityUpdateRequest.seniorHomeId())
                    .orElseThrow(() -> new RuntimeException("Senior home not found"));
            activity.setSeniorHome(seniorHome);
        }
        if (activityUpdateRequest.description() != null) {
            activity.setDescription(activityUpdateRequest.description());
        }

        return activityRepository.save(activity);
    }

    @Override
    public void deleteActivity(Long id) {
        if (!activityRepository.existsById(id)) {
            throw new RuntimeException("Activity not found");
        }
        activityRepository.deleteById(id);
    }
}
