package com.bd2.seniorhomeapp.employee.entity;


import com.bd2.seniorhomeapp.address.entity.Address;
import com.bd2.seniorhomeapp.position.entity.Position;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

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

    @NotNull
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private String surname;

    private String email;
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

    //TODO: dodac encje senior, aktywność, pokój
//    @ManyToMany
//    private Set<Senior> seniors = new HashSet<>();
//
//    @ManyToMany
//    private Set<Activity> activities = new HashSet<>();
//
//    @ManyToMany
//    private Set<Room> rooms = new HashSet<>();

    //TODO: dodac encje domu seniora
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "home_id")
//    private SeniorHome seniorHome;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    // TODO: dodać encję managera
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "manager_id")
//    private Manager manager;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private MedicalEmployee medicalEmployee;

    @AssertTrue
    public boolean isValid() {
        if (hireDate == null || fireDate == null) {
            return true;
        }
        return !fireDate.before(hireDate);
    }
}
