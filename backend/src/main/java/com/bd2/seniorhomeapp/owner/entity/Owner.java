package com.bd2.seniorhomeapp.owner.entity;

import com.bd2.seniorhomeapp.address.entity.Address;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "owner")
public class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column
    private String surname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senior_home_id", nullable = false)
    private SeniorHome seniorHome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;
}
