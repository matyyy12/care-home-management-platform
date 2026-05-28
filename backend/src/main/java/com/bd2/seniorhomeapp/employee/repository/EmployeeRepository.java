package com.bd2.seniorhomeapp.employee.repository;

import com.bd2.seniorhomeapp.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
