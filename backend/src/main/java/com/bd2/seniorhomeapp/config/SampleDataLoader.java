package com.bd2.seniorhomeapp.config;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.bd2.seniorhomeapp.activity.dto.request.ActivityRequest;
import com.bd2.seniorhomeapp.activity.entity.ActivityType;
import com.bd2.seniorhomeapp.activity.service.ActivityService;
import com.bd2.seniorhomeapp.address.dto.request.AddressRequest;
import com.bd2.seniorhomeapp.assignment.dto.request.AssignSeniorToRoomRequest;
import com.bd2.seniorhomeapp.assignment.service.SeniorRoomAssignmentService;
import com.bd2.seniorhomeapp.employee.dto.request.EmployeeRequest;
import com.bd2.seniorhomeapp.employee.dto.request.MedicalEmployeeRequest;
import com.bd2.seniorhomeapp.employee.entity.Employee;
import com.bd2.seniorhomeapp.employee.repository.EmployeeRepository;
import com.bd2.seniorhomeapp.employee.service.EmployeeService;
import com.bd2.seniorhomeapp.equipment.dto.request.EquipmentModelRequest;
import com.bd2.seniorhomeapp.equipment.dto.request.EquipmentRequest;
import com.bd2.seniorhomeapp.equipment.service.EquipmentService;
import com.bd2.seniorhomeapp.owner.dto.request.OwnerRequest;
import com.bd2.seniorhomeapp.owner.service.OwnerService;
import com.bd2.seniorhomeapp.position.dto.request.PositionRequest;
import com.bd2.seniorhomeapp.position.entity.Position;
import com.bd2.seniorhomeapp.position.service.PositionService;
import com.bd2.seniorhomeapp.postOffice.dto.request.PostOfficeRequest;
import com.bd2.seniorhomeapp.room.dto.GameRoomEquipmentItem;
import com.bd2.seniorhomeapp.room.dto.request.RoomRequest;
import com.bd2.seniorhomeapp.room.entity.GameRoomEquipmentType;
import com.bd2.seniorhomeapp.room.entity.Room;
import com.bd2.seniorhomeapp.room.entity.RoomStandard;
import com.bd2.seniorhomeapp.room.entity.RoomType;
import com.bd2.seniorhomeapp.room.service.RoomService;
import com.bd2.seniorhomeapp.senior.dto.CreateSeniorRequest;
import com.bd2.seniorhomeapp.senior.dto.SeniorResponse;
import com.bd2.seniorhomeapp.senior.repository.SeniorRepository;
import com.bd2.seniorhomeapp.senior.service.SeniorService;
import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;
import com.bd2.seniorhomeapp.seniorHome.repository.SeniorHomeRepository;
import com.bd2.seniorhomeapp.seniorHome.dto.request.SeniorHomeRequest;
import com.bd2.seniorhomeapp.seniorHome.service.SeniorHomeService;

@Configuration
@ConditionalOnProperty(name = "app.sample-data.enabled", havingValue = "true", matchIfMissing = true)
public class SampleDataLoader {

    @Bean
    @Order(2)
    CommandLineRunner seedSampleData(
            PlatformTransactionManager transactionManager,
            EmployeeRepository employeeRepository,
            SeniorRepository seniorRepository,
            SeniorHomeRepository seniorHomeRepository,
            ActivityService activityService,
            EmployeeService employeeService,
            EquipmentService equipmentService,
            OwnerService ownerService,
            PositionService positionService,
            RoomService roomService,
            SeniorService seniorService,
            SeniorHomeService seniorHomeService,
            SeniorRoomAssignmentService seniorRoomAssignmentService) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        return args -> transactionTemplate.executeWithoutResult(status -> {
            if (hasSampleDataLoaded(seniorHomeRepository, seniorRepository, employeeRepository)) {
                return;
            }

            loadSampleData(
                    activityService,
                    employeeService,
                    equipmentService,
                    ownerService,
                    positionService,
                    roomService,
                    seniorService,
                    seniorHomeService,
                    seniorRoomAssignmentService);
        });
    }

    private boolean hasSampleDataLoaded(
            SeniorHomeRepository seniorHomeRepository,
            SeniorRepository seniorRepository,
            EmployeeRepository employeeRepository) {
        return seniorHomeRepository.count() >= 2
                && seniorRepository.count() >= 3
                && employeeRepository.count() >= 4;
    }

    private void loadSampleData(
            ActivityService activityService,
            EmployeeService employeeService,
            EquipmentService equipmentService,
            OwnerService ownerService,
            PositionService positionService,
            RoomService roomService,
            SeniorService seniorService,
            SeniorHomeService seniorHomeService,
            SeniorRoomAssignmentService seniorRoomAssignmentService) {
        Position caregiver = positionService.createPosition(new PositionRequest(
                "Opiekun",
                "Codzienna opieka nad seniorami, wsparcie w aktywnościach i kontakt z rodziną."));
        Position nurse = positionService.createPosition(new PositionRequest(
                "Pielęgniarka",
                "Opieka medyczna, podawanie leków i dokumentowanie zaleceń lekarskich."));
        Position therapist = positionService.createPosition(new PositionRequest(
                "Terapeuta",
                "Prowadzenie zajęć rekreacyjnych, terapii zajęciowej i aktywizacji seniorów."));
        Position manager = positionService.createPosition(new PositionRequest(
                "Kierownik",
                "Koordynacja pracy domu seniora, grafiku zespołu i spraw organizacyjnych."));

        SeniorHome pogodny = seniorHomeService.createHome(new SeniorHomeRequest(
                "Dom Pogodny",
                "600100200",
                "kontakt@pogodny.pl",
                "pogodny.pl",
                date("2018-05-12"),
                address("Warszawa", "Leśna", "12", "00-120", "Warszawa")));
        SeniorHome zacisze = seniorHomeService.createHome(new SeniorHomeRequest(
                "Dom Zacisze",
                "600300400",
                "biuro@zacisze.pl",
                "zacisze.pl",
                date("2020-09-01"),
                address("Kraków", "Spokojna", "7", "30-001", "Kraków")));

        ownerService.createOwner(new OwnerRequest(
                "Anna",
                "Nowicka",
                pogodny.getId(),
                address("Warszawa", "Kwiatowa", "8", "00-220", "Warszawa")));
        ownerService.createOwner(new OwnerRequest(
                "Piotr",
                "Wolski",
                zacisze.getId(),
                address("Kraków", "Długa", "4", "30-002", "Kraków")));

        Room pogodny101 = roomService.createRoom(residentialRoom("101", 1, pogodny.getId(), 2, RoomStandard.BASIC));
        Room pogodny102 = roomService.createRoom(residentialRoom("102", 1, pogodny.getId(), 1, RoomStandard.COMFORT));
        Room pogodnyOffice = roomService.createRoom(office("Gab-1", 0, pogodny.getId(), "Gabinet pielęgniarski"));
        Room pogodnyGame = roomService.createRoom(gameRoom("Sala-1", 0, pogodny.getId()));
        Room zacisze201 = roomService.createRoom(residentialRoom("201", 2, zacisze.getId(), 2, RoomStandard.COMFORT));
        Room zaciszeOffice = roomService.createRoom(office("Gab-2", 0, zacisze.getId(), "Gabinet terapii"));

        SeniorResponse jan = seniorService.create(new CreateSeniorRequest(
                "Jan",
                "Kowalski",
                LocalDate.of(1942, 3, 10),
                "42031012345",
                List.of("nadciśnienie"),
                "M",
                LocalDate.of(2024, 1, 15),
                null,
                pogodny.getId()));
        SeniorResponse maria = seniorService.create(new CreateSeniorRequest(
                "Maria",
                "Zielińska",
                LocalDate.of(1938, 7, 22),
                "38072212345",
                List.of("cukrzyca"),
                "F",
                LocalDate.of(2023, 11, 3),
                null,
                pogodny.getId()));
        SeniorResponse ewa = seniorService.create(new CreateSeniorRequest(
                "Ewa",
                "Wiśniewska",
                LocalDate.of(1945, 1, 5),
                "45010512345",
                List.of("osteoporoza"),
                "F",
                LocalDate.of(2024, 2, 20),
                null,
                zacisze.getId()));

        seniorRoomAssignmentService.assign(new AssignSeniorToRoomRequest(jan.seniorId(), pogodny101.getId()));
        seniorRoomAssignmentService.assign(new AssignSeniorToRoomRequest(maria.seniorId(), pogodny101.getId()));
        seniorRoomAssignmentService.assign(new AssignSeniorToRoomRequest(ewa.seniorId(), zacisze201.getId()));

        Employee kierownik = employeeService.createEmployee(new EmployeeRequest(
                "Marek",
                "Lis",
                new BigDecimal("7200.00"),
                "marek.lis@pogodny.pl",
                "501100200",
                date("1980-04-12"),
                date("2021-01-10"),
                null,
                address("Warszawa", "Leśna", "12", "00-120", "Warszawa"),
                pogodny.getId(),
                manager.getId(),
                null,
                null));
        Employee opiekunka = employeeService.createEmployee(new EmployeeRequest(
                "Olga",
                "Maj",
                new BigDecimal("4800.00"),
                "olga.maj@pogodny.pl",
                "501100201",
                date("1988-08-02"),
                date("2022-03-01"),
                null,
                address("Warszawa", "Brzozowa", "3", "00-121", "Warszawa"),
                pogodny.getId(),
                caregiver.getId(),
                kierownik.getId(),
                null));
        Employee pielegniarka = employeeService.createEmployee(new EmployeeRequest(
                "Karolina",
                "Bąk",
                new BigDecimal("5600.00"),
                "karolina.bak@pogodny.pl",
                "501100202",
                date("1984-10-18"),
                date("2020-06-15"),
                null,
                address("Warszawa", "Lipowa", "5", "00-122", "Warszawa"),
                pogodny.getId(),
                nurse.getId(),
                kierownik.getId(),
                new MedicalEmployeeRequest("PWZ123456", "Pielęgniarstwo")));
        Employee terapeutaZacisze = employeeService.createEmployee(new EmployeeRequest(
                "Tomasz",
                "Król",
                new BigDecimal("5100.00"),
                "tomasz.krol@zacisze.pl",
                "502200300",
                date("1986-02-25"),
                date("2022-09-01"),
                null,
                address("Kraków", "Cicha", "2", "30-003", "Kraków"),
                zacisze.getId(),
                therapist.getId(),
                null,
                null));

        employeeService.assignSeniorToEmployee(opiekunka.getId(), jan.seniorId());
        employeeService.assignSeniorToEmployee(opiekunka.getId(), maria.seniorId());
        employeeService.assignSeniorToEmployee(terapeutaZacisze.getId(), ewa.seniorId());
        employeeService.assignEmployeesToRoom(Set.of(pielegniarka.getId()), pogodnyOffice.getId());
        employeeService.assignEmployeesToRoom(Set.of(opiekunka.getId()), pogodnyGame.getId());
        employeeService.assignEmployeesToRoom(Set.of(terapeutaZacisze.getId()), zaciszeOffice.getId());

        activityService.createActivity(new ActivityRequest(
                "Gimnastyka poranna",
                ActivityType.RECREATIONAL,
                new BigDecimal("15.00"),
                pogodny.getId(),
                "Lekkie ćwiczenia ruchowe dla seniorów."));
        activityService.createActivity(new ActivityRequest(
                "Kontrola ciśnienia",
                ActivityType.MEDICAL,
                BigDecimal.ZERO,
                pogodny.getId(),
                "Regularna kontrola parametrów zdrowotnych."));
        activityService.createActivity(new ActivityRequest(
                "Warsztaty pamięci",
                ActivityType.CULTURAL,
                new BigDecimal("20.00"),
                zacisze.getId(),
                "Ćwiczenia pamięci i rozmowy tematyczne."));

        equipmentService.createEquipment(new EquipmentRequest(
                "INV-001",
                "Ciśnieniomierz",
                "Sprzęt do codziennych pomiarów w gabinecie pielęgniarskim.",
                LocalDate.of(2023, 4, 15),
                new EquipmentModelRequest("Omron", "M3")));
        equipmentService.createEquipment(new EquipmentRequest(
                "INV-002",
                "Balkonik",
                "Pomoc przy poruszaniu się seniorów.",
                LocalDate.of(2022, 8, 20),
                new EquipmentModelRequest("Reha", "Walker")));
    }

    private AddressRequest address(
            String city,
            String street,
            String number,
            String postalCode,
            String postOfficeCity) {
        return new AddressRequest(city, street, number, new PostOfficeRequest(postalCode, postOfficeCity));
    }

    private RoomRequest residentialRoom(
            String number,
            Integer floor,
            Long homeId,
            Integer capacity,
            RoomStandard standard) {
        return new RoomRequest(number, floor, true, homeId, RoomType.RESIDENTIAL, capacity, standard, null, null, null);
    }

    private RoomRequest office(String number, Integer floor, Long homeId, String officeKind) {
        return new RoomRequest(number, floor, true, homeId, RoomType.OFFICE, null, null, officeKind, "Podstawowe wyposażenie gabinetu", null);
    }

    private RoomRequest gameRoom(String number, Integer floor, Long homeId) {
        return new RoomRequest(
                number,
                floor,
                true,
                homeId,
                RoomType.GAME_ROOM,
                null,
                null,
                null,
                null,
                List.of(
                        new GameRoomEquipmentItem(GameRoomEquipmentType.TV, 1),
                        new GameRoomEquipmentItem(GameRoomEquipmentType.BOARD_GAME, 6)));
    }

    private Date date(String value) {
        return Date.valueOf(value);
    }
}
