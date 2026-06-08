package com.bd2.seniorhomeapp.employee.entity;


import com.bd2.seniorhomeapp.activity.entity.Activity;
import com.bd2.seniorhomeapp.address.entity.Address;
import com.bd2.seniorhomeapp.position.entity.Position;
import com.bd2.seniorhomeapp.senior.entity.Senior;
import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal salary;

    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String name;

    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String surname;

    @Email
    @Size(max = 40)
    @Column(length = 40)
    private String email;

    @Size(max = 15)
    @Column(length = 15)
    private String phone;

    @NotNull
    @Column(nullable = false)
    private Date birthDate;

    @NotNull
    @Column(nullable = false)
    private Date hireDate;

    private Date fireDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @ManyToMany
    private Set<Senior> seniors = new HashSet<>();

    @ManyToMany
    private Set<Activity> activities = new HashSet<>();

    // TODO: Odkomentowac po dolaczeniu brancha z encja Room.
    // @ManyToMany
    // private Set<Room> rooms = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "home_id", nullable = false)
    private SeniorHome seniorHome;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private MedicalEmployee medicalEmployee;
}
