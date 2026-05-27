package com.bd2.seniorhomeapp.senior.entity;

import jakarta.persistence.Entity;
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
	private Long id;

	private String firstName;

	private String lastName;

	private String roomNumber;

	public Senior(String firstName, String lastName, String roomNumber) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.roomNumber = roomNumber;
	}
}
