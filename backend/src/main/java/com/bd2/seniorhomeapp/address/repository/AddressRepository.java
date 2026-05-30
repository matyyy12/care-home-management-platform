package com.bd2.seniorhomeapp.address.repository;

import com.bd2.seniorhomeapp.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByCity(String city);
}
