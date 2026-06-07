package com.bd2.seniorhomeapp.activity.repository;

import java.util.List;

import com.bd2.seniorhomeapp.activity.entity.Activity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findBySeniorHomeId(Long seniorHomeId);
}
