package com.bd2.seniorhomeapp.config;

import java.util.Date;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;
import com.bd2.seniorhomeapp.seniorHome.repository.SeniorHomeRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DemoDataLoader implements CommandLineRunner {

	private final SeniorHomeRepository seniorHomeRepository;

	@Override
	public void run(String... args) {
		if (seniorHomeRepository.count() > 0) {
			return;
		}

		seniorHomeRepository.save(createHome(
				"Dom Seniora „Złota Jesień”",
				"+48 12 345 67 89",
				"kontakt@zlota-jesien.pl"));
		seniorHomeRepository.save(createHome(
				"Dom Seniora „Spokojna Przystań”",
				"+48 22 987 65 43",
				"biuro@spokojna.pl"));
	}

	private SeniorHome createHome(String name, String phone, String email) {
		SeniorHome home = new SeniorHome();
		home.setName(name);
		home.setPhoneNumber(phone);
		home.setEmail(email);
		home.setWebsite("https://example.com");
		home.setEstablishedDate(new Date());
		return home;
	}
}
