package com.bd2.seniorhomeapp.senior.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

	@NotBlank
	@Size(max = 30)
	@Column(nullable = false, length = 30)
	private String firstName;

	@NotBlank
	@Size(max = 30)
	@Column(nullable = false, length = 30)
	private String lastName;

	@NotNull
	@Column(nullable = false)
	private LocalDate birthDate;

	@NotBlank
	@Pattern(regexp = "\\d{11}")
	@Column(nullable = false, unique = true, length = 11)
	private String pesel;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "senior_diseases", joinColumns = @JoinColumn(name = "senior_id"))
	@Column(name = "disease", length = 100)
	private List<String> diseases = new ArrayList<>();

	@NotBlank
	@Size(max = 10)
	@Column(nullable = false, length = 10)
	private String gender;

	@NotNull
	@Column(nullable = false)
	private LocalDate accommodationDate;

	@Column
	private LocalDate dischargeDate;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "senior_home_id", nullable = false)
	private SeniorHome seniorHome;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private SeniorStatus status = SeniorStatus.ACTIVE;

	@AssertTrue(message = "Discharge date cannot be before birth date or accommodation date")
	public boolean isDischargeDateValid() {
		if (dischargeDate == null) {
			return true;
		}
		if (birthDate != null && dischargeDate.isBefore(birthDate)) {
			return false;
		}
		return accommodationDate == null || !dischargeDate.isBefore(accommodationDate);
	}

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
