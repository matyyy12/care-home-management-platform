package com.bd2.seniorhomeapp.senior.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bd2.seniorhomeapp.senior.entity.Senior;
import com.bd2.seniorhomeapp.senior.entity.SeniorStatus;

public interface SeniorRepository extends JpaRepository<Senior, Long> {

	long countByRoom_IdAndStatus(Long roomId, SeniorStatus status);

	List<Senior> findByRoomIsNotNull();

	List<Senior> findBySeniorHome_IdAndRoomIsNotNull(Long seniorHomeId);
}
