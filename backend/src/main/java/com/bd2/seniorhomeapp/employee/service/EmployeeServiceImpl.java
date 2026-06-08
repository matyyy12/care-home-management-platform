package com.bd2.seniorhomeapp.employee.service;

import com.bd2.seniorhomeapp.address.entity.Address;
import com.bd2.seniorhomeapp.address.service.AddressService;
import com.bd2.seniorhomeapp.employee.dto.request.EmployeeRequest;
import com.bd2.seniorhomeapp.employee.dto.request.EmployeeUpdateRequest;
import com.bd2.seniorhomeapp.employee.dto.request.MedicalEmployeeRequest;
import com.bd2.seniorhomeapp.employee.entity.Employee;
import com.bd2.seniorhomeapp.employee.entity.MedicalEmployee;
import com.bd2.seniorhomeapp.employee.repository.EmployeeRepository;
import com.bd2.seniorhomeapp.position.entity.Position;
import com.bd2.seniorhomeapp.position.repository.PositionRepository;
import com.bd2.seniorhomeapp.room.entity.Room;
import com.bd2.seniorhomeapp.room.repository.RoomRepository;
import com.bd2.seniorhomeapp.senior.entity.Senior;
import com.bd2.seniorhomeapp.senior.entity.SeniorStatus;
import com.bd2.seniorhomeapp.senior.repository.SeniorRepository;
import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;
import com.bd2.seniorhomeapp.seniorHome.repository.SeniorHomeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final AddressService addressService;
    private final SeniorHomeRepository seniorHomeRepository;
    private final PositionRepository positionRepository;
    private final RoomRepository roomRepository;
    private final SeniorRepository seniorRepository;

    @Override
    public List<Employee> getEmployees(Long id) {
        if (id != null) {
            return List.of(findEmployeeById(id));
        }
        return employeeRepository.findAll();
    }

    @Override
    public Employee createEmployee(EmployeeRequest employeeRequest) {
        Address address = addressService.findOrCreateAddress(employeeRequest.address());
        SeniorHome seniorHome = findSeniorHomeById(employeeRequest.seniorHomeId());
        Position position = findPositionById(employeeRequest.positionId());

        Employee employee = new Employee();
        employee.setName(employeeRequest.name());
        employee.setSurname(employeeRequest.surname());
        employee.setSalary(employeeRequest.salary());
        employee.setEmail(employeeRequest.email());
        employee.setPhone(employeeRequest.phone());
        employee.setBirthDate(employeeRequest.birthDate());
        employee.setHireDate(employeeRequest.hireDate());
        employee.setFireDate(employeeRequest.fireDate());
        employee.setAddress(address);
        employee.setSeniorHome(seniorHome);
        employee.setPosition(position);

        if (employeeRequest.managerId() != null) {
            Employee manager = findEmployeeById(employeeRequest.managerId());
            employee.setManager(manager);
        }
        setMedicalEmployee(employee, employeeRequest.medicalEmployee());
        validateDates(employee);

        return employeeRepository.save(employee);
    }

    @Override
    public Employee updateEmployee(EmployeeUpdateRequest employeeUpdateRequest, Long id) {
        Employee employee = findEmployeeById(id);

        if (employeeUpdateRequest.name() != null && !employeeUpdateRequest.name().isBlank()) {
            employee.setName(employeeUpdateRequest.name());
        }
        if (employeeUpdateRequest.surname() != null && !employeeUpdateRequest.surname().isBlank()) {
            employee.setSurname(employeeUpdateRequest.surname());
        }
        if (employeeUpdateRequest.salary() != null) {
            employee.setSalary(employeeUpdateRequest.salary());
        }
        if (employeeUpdateRequest.email() != null) {
            employee.setEmail(employeeUpdateRequest.email());
        }
        if (employeeUpdateRequest.phone() != null) {
            employee.setPhone(employeeUpdateRequest.phone());
        }
        if (employeeUpdateRequest.birthDate() != null) {
            employee.setBirthDate(employeeUpdateRequest.birthDate());
        }
        if (employeeUpdateRequest.hireDate() != null) {
            employee.setHireDate(employeeUpdateRequest.hireDate());
        }
        if (employeeUpdateRequest.fireDate() != null) {
            employee.setFireDate(employeeUpdateRequest.fireDate());
        }
        if (employeeUpdateRequest.address() != null) {
            employee.setAddress(addressService.findOrCreateAddress(employeeUpdateRequest.address()));
        }
        if (employeeUpdateRequest.seniorHomeId() != null) {
            employee.setSeniorHome(findSeniorHomeById(employeeUpdateRequest.seniorHomeId()));
        }
        if (employeeUpdateRequest.positionId() != null) {
            employee.setPosition(findPositionById(employeeUpdateRequest.positionId()));
        }
        if (employeeUpdateRequest.managerId() != null) {
            if (employeeUpdateRequest.managerId().equals(id)) {
                throw new RuntimeException("Employee cannot be his own manager");
            }
            employee.setManager(findEmployeeById(employeeUpdateRequest.managerId()));
        }
        if (employeeUpdateRequest.medicalEmployee() != null) {
            setMedicalEmployee(employee, employeeUpdateRequest.medicalEmployee());
        }
        validateDates(employee);

        return employeeRepository.save(employee);
    }

    @Override
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found");
        }
        employeeRepository.deleteById(id);
    }

    @Override
    public Employee assignEmployeeToRoom(Long employeeId, Long roomId) {
        Employee employee = findEmployeeById(employeeId);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        validateEmployeeAndRoom(employee, room);

        employee.getRooms().add(room);
        return employeeRepository.save(employee);
    }

    @Override
    public List<Employee> assignEmployeesToRoom(Set<Long> employeeIds, Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        List<Employee> employees = employeeIds.stream()
                .map(this::findEmployeeById)
                .toList();

        employees.forEach(employee -> {
            validateEmployeeAndRoom(employee, room);
            employee.getRooms().add(room);
        });

        return employeeRepository.saveAll(employees);
    }

    @Override
    public Employee unassignEmployeeFromRoom(Long employeeId, Long roomId) {
        Employee employee = findEmployeeById(employeeId);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        employee.getRooms().remove(room);
        return employeeRepository.save(employee);
    }

    @Override
    public Employee assignSeniorToEmployee(Long employeeId, Long seniorId) {
        Employee employee = findEmployeeById(employeeId);
        Senior senior = findSeniorById(seniorId);

        validateEmployeeAndSenior(employee, senior);
        validateAssignableSenior(senior);
        validateActiveEmployee(employee);

        employee.getSeniors().add(senior);
        return employeeRepository.save(employee);
    }

    @Override
    public Employee unassignSeniorFromEmployee(Long employeeId, Long seniorId) {
        Employee employee = findEmployeeById(employeeId);
        Senior senior = findSeniorById(seniorId);

        employee.getSeniors().remove(senior);
        return employeeRepository.save(employee);
    }

    @Override
    public Employee setEmployeeSeniors(Long employeeId, Set<Long> seniorIds) {
        Employee employee = findEmployeeById(employeeId);
        validateActiveEmployee(employee);

        Set<Senior> seniors = seniorIds.stream()
                .map(this::findSeniorById)
                .peek(senior -> {
                    validateEmployeeAndSenior(employee, senior);
                    validateAssignableSenior(senior);
                })
                .collect(java.util.stream.Collectors.toSet());

        employee.getSeniors().clear();
        employee.getSeniors().addAll(seniors);
        return employeeRepository.save(employee);
    }

    private Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    private SeniorHome findSeniorHomeById(Long id) {
        return seniorHomeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Senior home not found"));
    }

    private Position findPositionById(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Position not found"));
    }

    private void validateEmployeeAndRoom(Employee employee, Room room) {
        if (!employee.getSeniorHome().getId().equals(room.getSeniorHome().getId())) {
            throw new RuntimeException("Employee and room must belong to the same senior home");
        }
    }

    private Senior findSeniorById(Long id) {
        return seniorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Senior not found"));
    }

    private void validateEmployeeAndSenior(Employee employee, Senior senior) {
        if (!employee.getSeniorHome().getId().equals(senior.getSeniorHome().getId())) {
            throw new RuntimeException("Employee and senior must belong to the same senior home");
        }
    }

    private void validateAssignableSenior(Senior senior) {
        if (senior.getStatus() != SeniorStatus.ACTIVE) {
            throw new RuntimeException("Only active seniors can be assigned to a caregiver");
        }
        if (senior.getDischargeDate() != null) {
            throw new RuntimeException("Discharged seniors cannot be assigned to a caregiver");
        }
    }

    private void validateActiveEmployee(Employee employee) {
        if (employee.getFireDate() != null) {
            throw new RuntimeException("Terminated employees cannot receive new senior assignments");
        }
    }

    private void setMedicalEmployee(Employee employee, MedicalEmployeeRequest medicalEmployeeRequest) {
        if (medicalEmployeeRequest == null) {
            return;
        }

        MedicalEmployee medicalEmployee = employee.getMedicalEmployee();
        if (medicalEmployee == null) {
            medicalEmployee = new MedicalEmployee();
            medicalEmployee.setEmployee(employee);
            employee.setMedicalEmployee(medicalEmployee);
        }
        medicalEmployee.setLicenseNumber(medicalEmployeeRequest.licenseNumber());
        medicalEmployee.setSpecialty(medicalEmployeeRequest.specialty());
    }

    private void validateDates(Employee employee) {
        if (employee.getFireDate() != null
                && employee.getHireDate() != null
                && employee.getFireDate().before(employee.getHireDate())) {
            throw new RuntimeException("Fire date cannot be before hire date");
        }
    }
}
