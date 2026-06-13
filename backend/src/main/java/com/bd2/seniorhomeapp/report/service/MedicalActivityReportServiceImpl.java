package com.bd2.seniorhomeapp.report.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bd2.seniorhomeapp.activity.entity.Activity;
import com.bd2.seniorhomeapp.activity.entity.ActivityType;
import com.bd2.seniorhomeapp.activity.repository.ActivityRepository;
import com.bd2.seniorhomeapp.employee.entity.Employee;
import com.bd2.seniorhomeapp.report.dto.MedicalActivityReportRow;
import com.bd2.seniorhomeapp.room.entity.Room;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MedicalActivityReportServiceImpl implements MedicalActivityReportService {

    private final ActivityRepository activityRepository;

    @Override
    public List<MedicalActivityReportRow> generateReport(Long seniorHomeId) {
        List<Activity> activities = seniorHomeId != null
                ? activityRepository.findBySeniorHomeId(seniorHomeId)
                : activityRepository.findAll();

        return activities.stream()
                .filter(activity -> activity.getType() == ActivityType.MEDICAL)
                .flatMap(activity -> detectIssues(activity).stream())
                .sorted(Comparator
                        .comparing(MedicalActivityReportRow::seniorHome)
                        .thenComparing(MedicalActivityReportRow::activityName)
                        .thenComparing(MedicalActivityReportRow::issueType))
                .toList();
    }

    private List<MedicalActivityReportRow> detectIssues(Activity activity) {
        List<MedicalActivityReportRow> issues = new ArrayList<>();
        Long activityHomeId = activity.getSeniorHome().getId();

        boolean hasMedicalLead = activity.getEmployees().stream()
                .anyMatch(employee -> employee.getMedicalEmployee() != null);
        if (!hasMedicalLead) {
            issues.add(row(activity, "NO_MEDICAL_LEAD",
                    "Brak pracownika medycznego prowadzącego zajęcia"));
        }

        if (activity.getRooms().isEmpty()) {
            issues.add(row(activity, "NO_ROOM", "Brak przypisanego pomieszczenia"));
        } else {
            for (Room room : activity.getRooms()) {
                if (!Boolean.TRUE.equals(room.getInUse())) {
                    issues.add(row(activity, "ROOM_NOT_IN_USE",
                            "Przypisane pomieszczenie " + room.getRoomNumber() + " nie jest w użyciu"));
                }
                if (!activityHomeId.equals(room.getSeniorHome().getId())) {
                    issues.add(row(activity, "CROSS_HOME",
                            "Pomieszczenie " + room.getRoomNumber() + " należy do innego domu seniora"));
                }
            }
        }

        for (Employee employee : activity.getEmployees()) {
            if (employee.getFireDate() != null) {
                issues.add(row(activity, "FIRED_EMPLOYEE",
                        "Przypisany pracownik " + employee.getName() + " " + employee.getSurname()
                                + " ma datę zwolnienia"));
            }
            if (!activityHomeId.equals(employee.getSeniorHome().getId())) {
                issues.add(row(activity, "CROSS_HOME",
                        "Pracownik " + employee.getName() + " " + employee.getSurname()
                                + " należy do innego domu seniora"));
            }
        }

        return issues;
    }

    private MedicalActivityReportRow row(Activity activity, String issueType, String description) {
        return new MedicalActivityReportRow(
                activity.getId(),
                activity.getName(),
                activity.getSeniorHome().getName(),
                activity.getSeniorHome().getId(),
                issueType,
                description);
    }
}
