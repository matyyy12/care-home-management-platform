package com.bd2.seniorhomeapp.report.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bd2.seniorhomeapp.activity.entity.ActivityType;
import com.bd2.seniorhomeapp.employee.entity.Employee;
import com.bd2.seniorhomeapp.owner.entity.Owner;
import com.bd2.seniorhomeapp.owner.repository.OwnerRepository;
import com.bd2.seniorhomeapp.report.dto.DataQualityIssueRow;
import com.bd2.seniorhomeapp.room.entity.Room;
import com.bd2.seniorhomeapp.room.entity.RoomType;
import com.bd2.seniorhomeapp.room.repository.RoomRepository;
import com.bd2.seniorhomeapp.senior.entity.Senior;
import com.bd2.seniorhomeapp.senior.entity.SeniorStatus;
import com.bd2.seniorhomeapp.senior.repository.SeniorRepository;
import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;
import com.bd2.seniorhomeapp.seniorHome.repository.SeniorHomeRepository;
import com.bd2.seniorhomeapp.employee.repository.EmployeeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class DataQualityReportServiceImpl implements DataQualityReportService {

    private final SeniorHomeRepository seniorHomeRepository;
    private final SeniorRepository seniorRepository;
    private final EmployeeRepository employeeRepository;
    private final RoomRepository roomRepository;
    private final OwnerRepository ownerRepository;

    @Override
    public List<DataQualityIssueRow> generateReport(Long seniorHomeId, String severity) {
        List<SeniorHome> homes = seniorHomeRepository.findAll();
        List<Senior> seniors = seniorRepository.findAll();
        List<Employee> employees = employeeRepository.findAll();
        List<Room> rooms = roomRepository.findAll();
        List<Owner> owners = ownerRepository.findAll();

        List<DataQualityIssueRow> issues = new ArrayList<>();

        checkSeniorIssues(issues, seniors, seniorHomeId);
        checkRoomIssues(issues, rooms, seniors, seniorHomeId);
        checkEmployeeIssues(issues, employees, seniorHomeId);
        checkHomeIssues(issues, homes, seniors, employees, rooms, owners, seniorHomeId);

        return issues.stream()
                .filter(issue -> severity == null || severity.isBlank()
                        || issue.severity().equalsIgnoreCase(severity))
                .sorted(Comparator
                        .comparingInt((DataQualityIssueRow issue) -> severityOrder(issue.severity()))
                        .thenComparing(DataQualityIssueRow::module)
                        .thenComparing(DataQualityIssueRow::objectName))
                .toList();
    }

    private void checkSeniorIssues(List<DataQualityIssueRow> issues, List<Senior> seniors, Long seniorHomeId) {
        for (Senior senior : filterByHome(seniors, Senior::getSeniorHome, seniorHomeId)) {
            if (senior.getStatus() == SeniorStatus.ACTIVE && senior.getRoom() == null) {
                issues.add(issue("WARNING", "Seniorzy", "Senior", senior.getSeniorId(), seniorName(senior),
                        senior.getSeniorHome(), "Aktywny senior nie ma przypisanego pokoju.",
                        "Przypisz seniora do pokoju mieszkalnego w tym samym domu."));
            }
            if (senior.getStatus() != SeniorStatus.ACTIVE && senior.getRoom() != null) {
                issues.add(issue("CRITICAL", "Seniorzy", "Senior", senior.getSeniorId(), seniorName(senior),
                        senior.getSeniorHome(), "Nieaktywny senior nadal ma przypisany pokój.",
                        "Usuń przypisanie pokoju albo popraw status seniora."));
            }
            if (senior.getRoom() != null
                    && !senior.getSeniorHome().getId().equals(senior.getRoom().getSeniorHome().getId())) {
                issues.add(issue("CRITICAL", "Seniorzy", "Senior", senior.getSeniorId(), seniorName(senior),
                        senior.getSeniorHome(), "Senior jest przypisany do pokoju z innego domu seniora.",
                        "Przenieś seniora do pokoju w jego domu seniora."));
            }
        }
    }

    private void checkRoomIssues(List<DataQualityIssueRow> issues, List<Room> rooms, List<Senior> seniors, Long seniorHomeId) {
        Map<Long, Long> activeSeniorCountByRoomId = seniors.stream()
                .filter(senior -> senior.getStatus() == SeniorStatus.ACTIVE)
                .filter(senior -> senior.getRoom() != null)
                .collect(Collectors.groupingBy(senior -> senior.getRoom().getId(), Collectors.counting()));

        for (Room room : filterByHome(rooms, Room::getSeniorHome, seniorHomeId)) {
            long assigned = activeSeniorCountByRoomId.getOrDefault(room.getId(), 0L);
            if (room.getType() == RoomType.RESIDENTIAL && room.getCapacity() == null) {
                issues.add(issue("CRITICAL", "Pomieszczenia", "Pokój", room.getId(), room.getRoomNumber(),
                        room.getSeniorHome(), "Pokój mieszkalny nie ma ustawionej pojemności.",
                        "Uzupełnij liczbę miejsc w pokoju."));
            }
            if (room.getCapacity() != null && assigned > room.getCapacity()) {
                issues.add(issue("CRITICAL", "Pomieszczenia", "Pokój", room.getId(), room.getRoomNumber(),
                        room.getSeniorHome(), "Pokój jest przepełniony: " + assigned + "/" + room.getCapacity() + ".",
                        "Przenieś część seniorów lub zwiększ pojemność pokoju."));
            }
            if (!Boolean.TRUE.equals(room.getInUse()) && assigned > 0) {
                issues.add(issue("CRITICAL", "Pomieszczenia", "Pokój", room.getId(), room.getRoomNumber(),
                        room.getSeniorHome(), "Do pokoju nieużywanego są przypisani aktywni seniorzy.",
                        "Oznacz pokój jako używany albo usuń przypisania seniorów."));
            }
            if (room.getType() != RoomType.RESIDENTIAL && assigned > 0) {
                issues.add(issue("CRITICAL", "Pomieszczenia", "Pokój", room.getId(), room.getRoomNumber(),
                        room.getSeniorHome(), "Seniorzy są przypisani do pokoju, który nie jest mieszkalny.",
                        "Przenieś seniorów do pokoju mieszkalnego."));
            }
        }
    }

    private void checkEmployeeIssues(List<DataQualityIssueRow> issues, List<Employee> employees, Long seniorHomeId) {
        for (Employee employee : filterByHome(employees, Employee::getSeniorHome, seniorHomeId)) {
            long assignments = employee.getSeniors().size() + employee.getRooms().size() + employee.getActivities().size();
            long medicalActivities = employee.getActivities().stream()
                    .filter(activity -> activity.getType() == ActivityType.MEDICAL)
                    .count();

            if (employee.getFireDate() != null && assignments > 0) {
                issues.add(issue("CRITICAL", "Pracownicy", "Pracownik", employee.getId(), employeeName(employee),
                        employee.getSeniorHome(), "Zwolniony pracownik nadal ma aktywne przypisania.",
                        "Usuń przypisania pracownika albo popraw datę zwolnienia."));
            }
            if (employee.getMedicalEmployee() == null && medicalActivities > 0) {
                issues.add(issue("CRITICAL", "Pracownicy", "Pracownik", employee.getId(), employeeName(employee),
                        employee.getSeniorHome(), "Pracownik niemedyczny ma przypisane zajęcia medyczne.",
                        "Przypisz zajęcia medyczne pracownikowi medycznemu."));
            }
            if (employee.getManager() != null && employee.getId().equals(employee.getManager().getId())) {
                issues.add(issue("CRITICAL", "Pracownicy", "Pracownik", employee.getId(), employeeName(employee),
                        employee.getSeniorHome(), "Pracownik jest swoim własnym kierownikiem.",
                        "Wybierz innego kierownika albo usuń przypisanie kierownika."));
            }
            if (assignments == 0 && employee.getFireDate() == null) {
                issues.add(issue("INFO", "Pracownicy", "Pracownik", employee.getId(), employeeName(employee),
                        employee.getSeniorHome(), "Aktywny pracownik nie ma żadnych przypisań.",
                        "Sprawdź, czy pracownik powinien mieć przypisanych seniorów, pokoje lub zajęcia."));
            }
        }
    }

    private void checkHomeIssues(
            List<DataQualityIssueRow> issues,
            List<SeniorHome> homes,
            List<Senior> seniors,
            List<Employee> employees,
            List<Room> rooms,
            List<Owner> owners,
            Long seniorHomeId) {
        Map<Long, Long> activeSeniorsByHome = seniors.stream()
                .filter(senior -> senior.getStatus() == SeniorStatus.ACTIVE)
                .collect(Collectors.groupingBy(senior -> senior.getSeniorHome().getId(), Collectors.counting()));
        Map<Long, Long> activeEmployeesByHome = employees.stream()
                .filter(employee -> employee.getFireDate() == null)
                .collect(Collectors.groupingBy(employee -> employee.getSeniorHome().getId(), Collectors.counting()));
        Map<Long, Long> residentialRoomsByHome = rooms.stream()
                .filter(room -> room.getType() == RoomType.RESIDENTIAL)
                .collect(Collectors.groupingBy(room -> room.getSeniorHome().getId(), Collectors.counting()));
        Map<Long, Long> ownersByHome = owners.stream()
                .collect(Collectors.groupingBy(owner -> owner.getSeniorHome().getId(), Collectors.counting()));

        for (SeniorHome home : homes.stream()
                .filter(home -> seniorHomeId == null || home.getId().equals(seniorHomeId))
                .toList()) {
            if (ownersByHome.getOrDefault(home.getId(), 0L) == 0) {
                issues.add(issue("WARNING", "Domy seniora", "Dom seniora", home.getId(), home.getName(),
                        home, "Dom seniora nie ma przypisanego właściciela.",
                        "Dodaj właściciela w module domów seniora."));
            }
            if (activeEmployeesByHome.getOrDefault(home.getId(), 0L) == 0) {
                issues.add(issue("CRITICAL", "Domy seniora", "Dom seniora", home.getId(), home.getName(),
                        home, "Dom seniora nie ma aktywnych pracowników.",
                        "Dodaj pracownika albo popraw daty zwolnienia."));
            }
            if (residentialRoomsByHome.getOrDefault(home.getId(), 0L) == 0) {
                issues.add(issue("CRITICAL", "Domy seniora", "Dom seniora", home.getId(), home.getName(),
                        home, "Dom seniora nie ma pokoi mieszkalnych.",
                        "Dodaj pokój mieszkalny w module pomieszczeń."));
            }
            if (activeSeniorsByHome.getOrDefault(home.getId(), 0L) == 0) {
                issues.add(issue("INFO", "Domy seniora", "Dom seniora", home.getId(), home.getName(),
                        home, "Dom seniora nie ma aktywnych seniorów.",
                        "Sprawdź, czy to oczekiwany stan."));
            }
        }
    }

    private <T> List<T> filterByHome(List<T> values, Function<T, SeniorHome> homeGetter, Long seniorHomeId) {
        return values.stream()
                .filter(value -> seniorHomeId == null || homeGetter.apply(value).getId().equals(seniorHomeId))
                .toList();
    }

    private DataQualityIssueRow issue(
            String severity,
            String module,
            String objectType,
            Long objectId,
            String objectName,
            SeniorHome seniorHome,
            String description,
            String suggestedAction) {
        return new DataQualityIssueRow(
                severity,
                module,
                objectType,
                objectId,
                objectName,
                seniorHome.getId(),
                seniorHome.getName(),
                description,
                suggestedAction);
    }

    private int severityOrder(String severity) {
        return switch (severity) {
            case "CRITICAL" -> 0;
            case "WARNING" -> 1;
            default -> 2;
        };
    }

    private String seniorName(Senior senior) {
        return senior.getFirstName() + " " + senior.getLastName();
    }

    private String employeeName(Employee employee) {
        return employee.getName() + " " + employee.getSurname();
    }
}
