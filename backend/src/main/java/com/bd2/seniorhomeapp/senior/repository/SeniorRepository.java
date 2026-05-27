package com.bd2.seniorhomeapp.senior.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bd2.seniorhomeapp.senior.entity.Senior;

public interface SeniorRepository extends JpaRepository<Senior, Long> {
}
