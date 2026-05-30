package com.bd2.seniorhomeapp.postOffice.repository;

import com.bd2.seniorhomeapp.postOffice.entity.PostOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostOfficeRepository extends JpaRepository<PostOffice, Long> {
    List<PostOffice> findAllByCity(String city);
}
