package com.bd2.seniorhomeapp.employee.repository;

import com.bd2.seniorhomeapp.employee.entity.Employee;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
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
            "activities"
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
            "activities"
    })
    Optional<Employee> findById(Long id);
}
