package com.bd2.seniorhomeapp.senior.entity;

import java.time.LocalDate;
import java.util.List;

import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seniors")
@Getter
@Setter
@NoArgsConstructor
public class Senior {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long seniorId;

	private String firstName;

	private String lastName;

	private LocalDate birthDate;

	private String pesel;

	private List<String> diseases;

	private String gender;

	private LocalDate accommodationDate;

	private LocalDate dischargeDate;

	private SeniorHome seniorHome;

	@Enumerated(EnumType.STRING)
	private SeniorStatus status = SeniorStatus.ACTIVE;

	public Senior(String firstName, String lastName, LocalDate birthDate, String pesel, List<String> diseases, String gender, LocalDate accommodationDate, LocalDate dischargeDate, SeniorHome seniorHome) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDate = birthDate;
		this.pesel = pesel;
		this.diseases = diseases;
		this.gender = gender;
		this.accommodationDate = accommodationDate;
		this.dischargeDate = dischargeDate;
		this.seniorHome = seniorHome;
		this.status = SeniorStatus.ACTIVE;
	}
}
