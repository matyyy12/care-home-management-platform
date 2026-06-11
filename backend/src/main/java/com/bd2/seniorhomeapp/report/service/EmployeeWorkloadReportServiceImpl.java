package com.bd2.seniorhomeapp.report.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bd2.seniorhomeapp.activity.entity.ActivityType;
import com.bd2.seniorhomeapp.employee.entity.Employee;
import com.bd2.seniorhomeapp.employee.repository.EmployeeRepository;
import com.bd2.seniorhomeapp.report.dto.EmployeeWorkloadReportRow;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeWorkloadReportServiceImpl implements EmployeeWorkloadReportService {

    private final EmployeeRepository employeeRepository;

    @Override
    public List<EmployeeWorkloadReportRow> generateReport(
            Long seniorHomeId,
            Long positionId,
            Boolean medicalOnly,
            String employmentStatus) {
        return employeeRepository.findAll().stream()
                .filter(employee -> seniorHomeId == null
                        || employee.getSeniorHome().getId().equals(seniorHomeId))
                .filter(employee -> positionId == null
                        || employee.getPosition().getId().equals(positionId))
                .filter(employee -> medicalOnly == null
                        || (employee.getMedicalEmployee() != null) == medicalOnly)
                .filter(employee -> employmentStatus == null || employmentStatus.isBlank()
                        || employeeStatus(employee).equalsIgnoreCase(employmentStatus))
                .map(this::mapRow)
                .sorted(Comparator
                        .comparing(EmployeeWorkloadReportRow::seniorHome)
                        .thenComparing(EmployeeWorkloadReportRow::employeeName))
                .toList();
    }

    private EmployeeWorkloadReportRow mapRow(Employee employee) {
        long seniorCount = employee.getSeniors().size();
        long roomCount = employee.getRooms().size();
        long activityCount = employee.getActivities().size();
        long medicalActivityCount = employee.getActivities().stream()
                .filter(activity -> activity.getType() == ActivityType.MEDICAL)
                .count();
        long totalAssignments = seniorCount + roomCount + activityCount;
        long workloadScore = seniorCount * 2 + roomCount + activityCount * 2;
        String employmentStatus = employeeStatus(employee);
        String warningMessage = warningMessage(employee, totalAssignments, medicalActivityCount);

        return new EmployeeWorkloadReportRow(
                employee.getId(),
                employee.getName() + " " + employee.getSurname(),
                employee.getSeniorHome().getId(),
                employee.getSeniorHome().getName(),
                employee.getPosition().getId(),
                employee.getPosition().getName(),
                employee.getManager() != null
                        ? employee.getManager().getName() + " " + employee.getManager().getSurname()
                        : null,
                employee.getMedicalEmployee() != null,
                seniorCount,
                roomCount,
                activityCount,
                medicalActivityCount,
                totalAssignments,
                workloadScore,
                employmentStatus,
                warningMessage != null,
                warningMessage);
    }

    private String employeeStatus(Employee employee) {
        return employee.getFireDate() == null ? "ACTIVE" : "FIRED";
    }

    private String warningMessage(Employee employee, long totalAssignments, long medicalActivityCount) {
        if (employee.getFireDate() != null && totalAssignments > 0) {
            return "Zwolniony pracownik ma aktywne przypisania";
        }
        if (employee.getMedicalEmployee() == null && medicalActivityCount > 0) {
            return "Pracownik niemedyczny ma zajęcia medyczne";
        }
        if (totalAssignments == 0) {
            return "Brak przypisań";
        }
        if (totalAssignments >= 8) {
            return "Wysokie obciążenie";
        }
        return null;
    }
}
