package com.bd2.seniorhomeapp.postOffice.entity;


import com.bd2.seniorhomeapp.address.entity.Address;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post_office")
public class PostOffice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true)
    private String postalCode;

    @NotNull
    @Column(nullable = false)
    private String city;

    @OneToMany(mappedBy = "postOffice")
    private Set<Address> addresses = new HashSet<>();
}
