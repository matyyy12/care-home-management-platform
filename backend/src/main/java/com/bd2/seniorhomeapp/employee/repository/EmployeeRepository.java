package com.bd2.seniorhomeapp.employee.repository;

import com.bd2.seniorhomeapp.employee.entity.Employee;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Override
    @EntityGraph(attributePaths = {
            "address",
            "address.postOffice",
            "seniorHome",
            "position",
            "manager",
            "medicalEmployee",
            "seniors",
            "activities",
            "rooms"
    })
    List<Employee> findAll();

    @Override
    @EntityGraph(attributePaths = {
            "address",
            "address.postOffice",
            "seniorHome",
            "position",
            "manager",
            "medicalEmployee",
            "seniors",
            "activities",
            "rooms"
    })
    Optional<Employee> findById(Long id);
}
