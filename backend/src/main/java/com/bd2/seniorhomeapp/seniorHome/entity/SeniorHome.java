package com.bd2.seniorhomeapp.seniorHome.entity;

import com.bd2.seniorhomeapp.address.entity.Address;
import com.bd2.seniorhomeapp.owner.entity.Owner;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

// TODO: Maybe add custom validator for website
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "senior_home")
public class SeniorHome {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String phoneNumber;

    @Email
    private String email;

    @NotBlank
    private String website;

    @NotNull
    @Column(nullable = false)
    private Date establishedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(mappedBy = "seniorHome", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Owner> owners;

}
