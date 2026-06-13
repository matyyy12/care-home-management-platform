package com.bd2.seniorhomeapp.activity.repository;

import java.util.List;

import com.bd2.seniorhomeapp.activity.entity.Activity;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @Override
    @EntityGraph(attributePaths = {
            "seniorHome",
            "employees",
            "employees.medicalEmployee",
            "employees.seniorHome",
            "rooms",
            "rooms.seniorHome"
    })
    List<Activity> findAll();

    @EntityGraph(attributePaths = {
            "seniorHome",
            "employees",
            "employees.medicalEmployee",
            "employees.seniorHome",
            "rooms",
            "rooms.seniorHome"
    })
    List<Activity> findBySeniorHomeId(Long seniorHomeId);
}
