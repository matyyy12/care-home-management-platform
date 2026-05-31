package com.bd2.seniorhomeapp.address.repository;

import com.bd2.seniorhomeapp.address.entity.Address;
import com.bd2.seniorhomeapp.postOffice.entity.PostOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByCity(String city);

    Optional<Address> findByCityAndStreetAndNumberAndPostOffice(
            String city,
            String street,
            String number,
            PostOffice postOffice
    );
}
