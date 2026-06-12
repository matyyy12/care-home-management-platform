package com.bd2.seniorhomeapp.postOffice.repository;

import com.bd2.seniorhomeapp.postOffice.entity.PostOffice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostOfficeRepository extends JpaRepository<PostOffice, Long> {
    List<PostOffice> findAllByCity(String city);
    Optional<PostOffice> findByPostalCode(String postalCode);
}
