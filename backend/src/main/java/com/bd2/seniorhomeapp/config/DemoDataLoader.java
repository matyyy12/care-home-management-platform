package com.bd2.seniorhomeapp.config;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import com.bd2.seniorhomeapp.activity.entity.Activity;
import com.bd2.seniorhomeapp.activity.entity.ActivityType;
import com.bd2.seniorhomeapp.activity.repository.ActivityRepository;
import com.bd2.seniorhomeapp.address.entity.Address;
import com.bd2.seniorhomeapp.address.repository.AddressRepository;
import com.bd2.seniorhomeapp.postOffice.entity.PostOffice;
import com.bd2.seniorhomeapp.postOffice.repository.PostOfficeRepository;
import com.bd2.seniorhomeapp.room.entity.GameRoomEquipment;
import com.bd2.seniorhomeapp.room.entity.GameRoomEquipmentType;
import com.bd2.seniorhomeapp.room.entity.Room;
import com.bd2.seniorhomeapp.room.entity.RoomType;
import com.bd2.seniorhomeapp.room.repository.RoomRepository;
import com.bd2.seniorhomeapp.senior.entity.Senior;
import com.bd2.seniorhomeapp.senior.repository.SeniorRepository;
import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;
import com.bd2.seniorhomeapp.seniorHome.repository.SeniorHomeRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DemoDataLoader implements CommandLineRunner {

	private final SeniorHomeRepository seniorHomeRepository;
	private final AddressRepository addressRepository;
	private final PostOfficeRepository postOfficeRepository;
	private final ActivityRepository activityRepository;
	private final RoomRepository roomRepository;
	private final SeniorRepository seniorRepository;

	@Override
	public void run(String... args) {
		if (seniorHomeRepository.count() > 0) {
			return;
		}

		SeniorHome zlotaJesien = seniorHomeRepository.save(createHome(
				"Złota Jesień",
				"123456789",
				"kontakt@zlota-jesien.pl",
				"00-001",
				"Warszawa",
				"Polna",
				"10"));
		SeniorHome spokojnaPrzystan = seniorHomeRepository.save(createHome(
				"Spokojna Przystań",
				"229876543",
				"biuro@spokojna.pl",
				"00-002",
				"Warszawa",
				"Leśna",
				"5"));

		activityRepository.save(createActivity(
				"Gimnastyka lekka",
				ActivityType.RECREATIONAL,
				BigDecimal.ZERO,
				zlotaJesien,
				"Ćwiczenia poranne w sali gier"));
		activityRepository.save(createActivity(
				"Kontrola ciśnienia",
				ActivityType.MEDICAL,
				new BigDecimal("25.00"),
				zlotaJesien,
				"Pomiar i konsultacja"));

		Room room101 = roomRepository.save(createResidentialRoom("101", 1, zlotaJesien, 2, "Podstawowy"));
		Room room102 = roomRepository.save(createResidentialRoom("102", 1, zlotaJesien, 2, "Podstawowy"));
		roomRepository.save(createOfficeRoom("G1", 0, zlotaJesien, "Gabinet pielęgniarki", "Ciśnieniomierz Omron"));
		roomRepository.save(createGameRoom("SG1", 0, zlotaJesien));
		Room room201 = roomRepository.save(createResidentialRoom("201", 2, spokojnaPrzystan, 2, "Podwyższony"));

		seniorRepository.save(createSenior(
				"Helena",
				"Kowalczyk",
				"29010112345",
				"F",
				zlotaJesien,
				room101));
		seniorRepository.save(createSenior(
				"Stanisław",
				"Wójcik",
				"41050567890",
				"M",
				zlotaJesien,
				room102));
		seniorRepository.save(createSenior(
				"Zofia",
				"Nowicka",
				"35030311111",
				"F",
				spokojnaPrzystan,
				room201));
	}

	private Senior createSenior(
			String firstName,
			String lastName,
			String pesel,
			String gender,
			SeniorHome seniorHome,
			Room room) {
		Senior senior = new Senior(
				firstName,
				lastName,
				LocalDate.of(1940, 1, 1),
				pesel,
				List.of("Artretyzm"),
				gender,
				LocalDate.of(2023, 1, 1),
				null,
				seniorHome);
		senior.setRoom(room);
		return senior;
	}

	private Room createResidentialRoom(
			String roomNumber,
			int floor,
			SeniorHome seniorHome,
			int capacity,
			String standard) {
		Room room = new Room();
		room.setRoomNumber(roomNumber);
		room.setFloor(floor);
		room.setInUse(true);
		room.setSeniorHome(seniorHome);
		room.setType(RoomType.RESIDENTIAL);
		room.setCapacity(capacity);
		room.setStandard(standard);
		return room;
	}

	private Room createOfficeRoom(
			String roomNumber,
			int floor,
			SeniorHome seniorHome,
			String officeKind,
			String equipmentDescription) {
		Room room = new Room();
		room.setRoomNumber(roomNumber);
		room.setFloor(floor);
		room.setInUse(true);
		room.setSeniorHome(seniorHome);
		room.setType(RoomType.OFFICE);
		room.setOfficeKind(officeKind);
		room.setEquipmentDescription(equipmentDescription);
		return room;
	}

	private Room createGameRoom(String roomNumber, int floor, SeniorHome seniorHome) {
		Room room = new Room();
		room.setRoomNumber(roomNumber);
		room.setFloor(floor);
		room.setInUse(true);
		room.setSeniorHome(seniorHome);
		room.setType(RoomType.GAME_ROOM);
		room.setGameEquipment(List.of(
				new GameRoomEquipment(GameRoomEquipmentType.TV, 1),
				new GameRoomEquipment(GameRoomEquipmentType.COMPUTER, 2),
				new GameRoomEquipment(GameRoomEquipmentType.CROSSWORD, 15),
				new GameRoomEquipment(GameRoomEquipmentType.BOARD_GAME, 8),
				new GameRoomEquipment(GameRoomEquipmentType.RADIO, 1)));
		return room;
	}

	private Activity createActivity(
			String name,
			ActivityType type,
			BigDecimal price,
			SeniorHome seniorHome,
			String description) {
		Activity activity = new Activity();
		activity.setName(name);
		activity.setType(type);
		activity.setPrice(price);
		activity.setSeniorHome(seniorHome);
		activity.setDescription(description);
		return activity;
	}

	private SeniorHome createHome(
			String name,
			String phone,
			String email,
			String postalCode,
			String city,
			String street,
			String number) {
		PostOffice postOffice = postOfficeRepository.findByPostalCode(postalCode)
				.orElseGet(() -> {
					PostOffice newPostOffice = new PostOffice();
					newPostOffice.setPostalCode(postalCode);
					newPostOffice.setCity(city);
					return postOfficeRepository.save(newPostOffice);
				});

		Address address = new Address();
		address.setCity(city);
		address.setStreet(street);
		address.setNumber(number);
		address.setPostOffice(postOffice);
		address = addressRepository.save(address);

		SeniorHome home = new SeniorHome();
		home.setName(name);
		home.setPhoneNumber(phone);
		home.setEmail(email);
		home.setWebsite("https://example.com");
		home.setEstablishedDate(new Date());
		home.setAddress(address);
		return home;
	}
}
