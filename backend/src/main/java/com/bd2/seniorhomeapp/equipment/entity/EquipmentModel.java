package com.bd2.seniorhomeapp.equipment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "equipment_model",
        uniqueConstraints = @UniqueConstraint(columnNames = {"brand", "name"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String brand;

    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String name;
}
