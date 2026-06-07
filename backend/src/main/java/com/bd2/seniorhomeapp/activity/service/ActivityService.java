package com.bd2.seniorhomeapp.activity.service;

import com.bd2.seniorhomeapp.activity.dto.request.ActivityRequest;
import com.bd2.seniorhomeapp.activity.dto.request.ActivityUpdateRequest;
import com.bd2.seniorhomeapp.activity.entity.Activity;

import java.util.List;

public interface ActivityService {
    List<Activity> getActivitiesFromSeniorHome(Long seniorHomeId);
    Activity createActivity(ActivityRequest activityRequest);
    Activity updateActivity(ActivityUpdateRequest activityUpdateRequest, Long id);
    void deleteActivity(Long id);
}
