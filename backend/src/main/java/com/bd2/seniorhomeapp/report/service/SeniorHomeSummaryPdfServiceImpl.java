package com.bd2.seniorhomeapp.report.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bd2.seniorhomeapp.activity.entity.Activity;
import com.bd2.seniorhomeapp.activity.repository.ActivityRepository;
import com.bd2.seniorhomeapp.address.entity.Address;
import com.bd2.seniorhomeapp.employee.entity.Employee;
import com.bd2.seniorhomeapp.employee.repository.EmployeeRepository;
import com.bd2.seniorhomeapp.owner.entity.Owner;
import com.bd2.seniorhomeapp.owner.repository.OwnerRepository;
import com.bd2.seniorhomeapp.report.pdf.SimplePdfDocument;
import com.bd2.seniorhomeapp.room.entity.Room;
import com.bd2.seniorhomeapp.room.entity.RoomType;
import com.bd2.seniorhomeapp.room.repository.RoomRepository;
import com.bd2.seniorhomeapp.senior.entity.Senior;
import com.bd2.seniorhomeapp.senior.entity.SeniorStatus;
import com.bd2.seniorhomeapp.senior.repository.SeniorRepository;
import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;
import com.bd2.seniorhomeapp.seniorHome.repository.SeniorHomeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SeniorHomeSummaryPdfServiceImpl implements SeniorHomeSummaryPdfService {

    private final SeniorHomeRepository seniorHomeRepository;
    private final SeniorRepository seniorRepository;
    private final EmployeeRepository employeeRepository;
    private final RoomRepository roomRepository;
    private final ActivityRepository activityRepository;
    private final OwnerRepository ownerRepository;

    @Override
    public byte[] generatePdf(Long seniorHomeId) {
        SeniorHome home = seniorHomeRepository.findById(seniorHomeId)
                .orElseThrow(() -> new RuntimeException("Senior home not found"));
        List<Senior> seniors = seniorRepository.findAll().stream()
                .filter(senior -> senior.getSeniorHome().getId().equals(seniorHomeId))
                .sorted(Comparator.comparing(Senior::getLastName).thenComparing(Senior::getFirstName))
                .toList();
        List<Employee> employees = employeeRepository.findAll().stream()
                .filter(employee -> employee.getSeniorHome().getId().equals(seniorHomeId))
                .sorted(Comparator.comparing(Employee::getSurname).thenComparing(Employee::getName))
                .toList();
        List<Room> rooms = roomRepository.findBySeniorHomeId(seniorHomeId).stream()
                .sorted(Comparator.comparing(Room::getFloor).thenComparing(Room::getRoomNumber))
                .toList();
        List<Activity> activities = activityRepository.findBySeniorHomeId(seniorHomeId).stream()
                .sorted(Comparator.comparing(Activity::getName))
                .toList();
        List<Owner> owners = ownerRepository.findAll().stream()
                .filter(owner -> owner.getSeniorHome().getId().equals(seniorHomeId))
                .sorted(Comparator.comparing(Owner::getSurname).thenComparing(Owner::getName))
                .toList();

        SimplePdfDocument pdf = new SimplePdfDocument();
        pdf.addTitle("Raport zbiorczy domu seniora");
        pdf.addLine("Wygenerowano: " + LocalDate.now());
        pdf.addHeading("Dane domu");
        pdf.addLine("Nazwa: " + home.getName());
        pdf.addLine("Telefon: " + value(home.getPhoneNumber()));
        pdf.addLine("E-mail: " + value(home.getEmail()));
        pdf.addLine("Strona WWW: " + value(home.getWebsite()));
        pdf.addLine("Data zalozenia: " + formatDate(home.getEstablishedDate()));
        pdf.addLine("Adres: " + addressLabel(home.getAddress()));

        pdf.addHeading("Podsumowanie");
        pdf.addLine("Seniorzy: " + seniors.size() + ", aktywni: " + countActiveSeniors(seniors));
        pdf.addLine("Pracownicy: " + employees.size() + ", aktywni: " + countActiveEmployees(employees));
        pdf.addLine("Pokoje: " + rooms.size() + ", mieszkalne: " + countResidentialRooms(rooms));
        pdf.addLine("Zajecia: " + activities.size());
        pdf.addLine("Wlasciciele: " + owners.size());

        pdf.addHeading("Wlasciciele");
        if (owners.isEmpty()) {
            pdf.addBullet("Brak wlascicieli przypisanych do domu.");
        } else {
            owners.forEach(owner -> pdf.addBullet(owner.getName() + " " + owner.getSurname()
                    + " | " + addressLabel(owner.getAddress())));
        }

        pdf.addHeading("Seniorzy");
        seniors.stream().limit(20).forEach(senior -> pdf.addBullet(
                senior.getFirstName() + " " + senior.getLastName()
                        + " | status: " + senior.getStatus()
                        + " | pokoj: " + (senior.getRoom() != null ? senior.getRoom().getRoomNumber() : "brak")));
        appendLimitNote(pdf, seniors.size(), 20);

        pdf.addHeading("Pracownicy");
        employees.stream().limit(20).forEach(employee -> pdf.addBullet(
                employee.getName() + " " + employee.getSurname()
                        + " | " + employee.getPosition().getName()
                        + " | seniorzy: " + employee.getSeniors().size()
                        + ", pokoje: " + employee.getRooms().size()
                        + ", zajecia: " + employee.getActivities().size()));
        appendLimitNote(pdf, employees.size(), 20);

        pdf.addHeading("Pokoje");
        rooms.stream().limit(20).forEach(room -> pdf.addBullet(
                room.getRoomNumber()
                        + " | pietro: " + room.getFloor()
                        + " | typ: " + value(room.getType())
                        + " | miejsca: " + value(room.getCapacity())
                        + " | w uzyciu: " + (Boolean.TRUE.equals(room.getInUse()) ? "tak" : "nie")));
        appendLimitNote(pdf, rooms.size(), 20);

        pdf.addHeading("Zajecia");
        activities.stream().limit(20).forEach(activity -> pdf.addBullet(
                activity.getName()
                        + " | typ: " + activity.getType()
                        + " | cena: " + activity.getPrice()));
        appendLimitNote(pdf, activities.size(), 20);

        pdf.addHeading("Uwagi jakosci danych");
        appendDataQualityNotes(pdf, seniors, employees, rooms);
        pdf.addBullet("Sprzet nie jest jeszcze przypisany do domu ani pokoju, wiec nie da sie go uczciwie pokazac w raporcie domu.");

        return pdf.toByteArray();
    }

    private void appendDataQualityNotes(SimplePdfDocument pdf, List<Senior> seniors, List<Employee> employees, List<Room> rooms) {
        long seniorsWithoutRoom = seniors.stream()
                .filter(senior -> senior.getStatus() == SeniorStatus.ACTIVE)
                .filter(senior -> senior.getRoom() == null)
                .count();
        long employeesWithoutAssignments = employees.stream()
                .filter(employee -> employee.getSeniors().isEmpty()
                        && employee.getRooms().isEmpty()
                        && employee.getActivities().isEmpty())
                .count();
        long inactiveRooms = rooms.stream()
                .filter(room -> !Boolean.TRUE.equals(room.getInUse()))
                .count();

        pdf.addBullet("Aktywni seniorzy bez pokoju: " + seniorsWithoutRoom);
        pdf.addBullet("Pracownicy bez przypisan: " + employeesWithoutAssignments);
        pdf.addBullet("Pokoje nieuzywane: " + inactiveRooms);
    }

    private long countActiveSeniors(List<Senior> seniors) {
        return seniors.stream().filter(senior -> senior.getStatus() == SeniorStatus.ACTIVE).count();
    }

    private long countActiveEmployees(List<Employee> employees) {
        return employees.stream().filter(employee -> employee.getFireDate() == null).count();
    }

    private long countResidentialRooms(List<Room> rooms) {
        return rooms.stream().filter(room -> room.getType() == RoomType.RESIDENTIAL).count();
    }

    private void appendLimitNote(SimplePdfDocument pdf, int size, int limit) {
        if (size > limit) {
            pdf.addBullet("Pokazano pierwsze " + limit + " z " + size + " rekordow.");
        }
    }

    private String addressLabel(Address address) {
        if (address == null) {
            return "-";
        }
        return address.getCity() + ", ul. " + address.getStreet() + " " + address.getNumber()
                + ", " + address.getPostOffice().getPostalCode() + " " + address.getPostOffice().getCity();
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "-";
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    private String value(Object value) {
        return value != null ? String.valueOf(value) : "-";
    }
}
