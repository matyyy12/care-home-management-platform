package com.bd2.seniorhomeapp.report.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bd2.seniorhomeapp.activity.entity.Activity;
import com.bd2.seniorhomeapp.activity.entity.ActivityType;
import com.bd2.seniorhomeapp.activity.repository.ActivityRepository;
import com.bd2.seniorhomeapp.report.dto.SeniorActivityReportRow;
import com.bd2.seniorhomeapp.senior.entity.Senior;
import com.bd2.seniorhomeapp.senior.entity.SeniorStatus;
import com.bd2.seniorhomeapp.senior.repository.SeniorRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SeniorActivityReportServiceImpl implements SeniorActivityReportService {

    private final SeniorRepository seniorRepository;
    private final ActivityRepository activityRepository;

    @Override
    public List<SeniorActivityReportRow> generateReport(Long seniorHomeId, SeniorStatus status) {
        List<Senior> seniors = seniorRepository.findAll();
        Map<Long, List<Activity>> activitiesByHomeId = activityRepository.findAll().stream()
                .collect(Collectors.groupingBy(activity -> activity.getSeniorHome().getId()));

        return seniors.stream()
                .filter(senior -> seniorHomeId == null
                        || senior.getSeniorHome().getId().equals(seniorHomeId))
                .filter(senior -> status == null || senior.getStatus() == status)
                .map(senior -> mapRow(senior, activitiesByHomeId))
                .sorted(Comparator
                        .comparing(SeniorActivityReportRow::seniorHome)
                        .thenComparing(SeniorActivityReportRow::seniorName))
                .toList();
    }

    private SeniorActivityReportRow mapRow(Senior senior, Map<Long, List<Activity>> activitiesByHomeId) {
        List<Activity> activities = activitiesByHomeId.getOrDefault(senior.getSeniorHome().getId(), List.of());
        long medicalCount = activities.stream()
                .filter(activity -> activity.getType() == ActivityType.MEDICAL)
                .count();
        long recreationalCount = activities.stream()
                .filter(activity -> activity.getType() == ActivityType.RECREATIONAL)
                .count();

        return new SeniorActivityReportRow(
                senior.getSeniorId(),
                senior.getFirstName() + " " + senior.getLastName(),
                senior.getSeniorHome().getId(),
                senior.getSeniorHome().getName(),
                senior.getStatus(),
                senior.getRoom() != null ? senior.getRoom().getRoomNumber() : null,
                null,
                activities.size(),
                medicalCount,
                recreationalCount);
    }
}
