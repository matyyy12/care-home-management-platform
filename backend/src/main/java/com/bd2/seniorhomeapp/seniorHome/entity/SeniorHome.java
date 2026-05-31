package com.bd2.seniorhomeapp.seniorHome.entity;

import com.bd2.seniorhomeapp.address.entity.Address;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

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
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String name;

    @NotBlank
    @Size(max = 15)
    @Column(nullable = false, length = 15)
    private String phoneNumber;

    @Email
    @Size(max = 40)
    @Column(length = 40)
    private String email;

    @Size(max = 40)
    @Column(length = 40)
    private String website;

    @NotNull
    @Column(nullable = false)
    private Date establishedDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;
}
