package com.bd2.seniorhomeapp.config;

import java.util.Date;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.bd2.seniorhomeapp.address.entity.Address;
import com.bd2.seniorhomeapp.address.repository.AddressRepository;
import com.bd2.seniorhomeapp.postOffice.entity.PostOffice;
import com.bd2.seniorhomeapp.postOffice.repository.PostOfficeRepository;
import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;
import com.bd2.seniorhomeapp.seniorHome.repository.SeniorHomeRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DemoDataLoader implements CommandLineRunner {

	private final SeniorHomeRepository seniorHomeRepository;
	private final AddressRepository addressRepository;
	private final PostOfficeRepository postOfficeRepository;

	@Override
	public void run(String... args) {
		if (seniorHomeRepository.count() > 0) {
			return;
		}

		seniorHomeRepository.save(createHome(
				"Złota Jesień",
				"123456789",
				"kontakt@zlota-jesien.pl",
				"00-001",
				"Warszawa",
				"Polna",
				"10"));
		seniorHomeRepository.save(createHome(
				"Spokojna Przystań",
				"229876543",
				"biuro@spokojna.pl",
				"00-002",
				"Warszawa",
				"Leśna",
				"5"));
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
