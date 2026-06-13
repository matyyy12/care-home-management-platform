package com.bd2.seniorhomeapp.owner.repository;

import com.bd2.seniorhomeapp.owner.entity.Owner;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    @Override
    @EntityGraph(attributePaths = {"address", "address.postOffice", "seniorHome"})
    List<Owner> findAll();

    @Override
    @EntityGraph(attributePaths = {"address", "address.postOffice", "seniorHome"})
    Optional<Owner> findById(Long id);
}
