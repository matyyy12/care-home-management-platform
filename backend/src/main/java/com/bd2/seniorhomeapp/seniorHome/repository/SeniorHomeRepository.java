package com.bd2.seniorhomeapp.seniorHome.repository;

import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeniorHomeRepository extends JpaRepository<SeniorHome, Long> {
}
