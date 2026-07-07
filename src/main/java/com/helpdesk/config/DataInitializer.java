package com.helpdesk.config;
import com.helpdesk.entity.*;
import com.helpdesk.enums.Role;
import com.helpdesk.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
@Component @RequiredArgsConstructor @Slf4j
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) {
        if (departmentRepository.count() == 0) {
            departmentRepository.save(Department.builder().name("IT Department").description("Information Technology").build());
            departmentRepository.save(Department.builder().name("HR Department").description("Human Resources").build());
            departmentRepository.save(Department.builder().name("Finance Department").description("Finance & Accounts").build());
            departmentRepository.save(Department.builder().name("Operations").description("Operations Team").build());
            log.info("Departments created");
        }
        if (userRepository.count() == 0) {
            Department it = departmentRepository.findAll().get(0);
            userRepository.save(User.builder().username("admin").password(passwordEncoder.encode("Admin@123")).email("admin@helpdesk.com").fullName("System Admin").role(Role.ROLE_ADMIN).department(it).employeeId("EMP001").designation("Administrator").active(true).build());
            userRepository.save(User.builder().username("teamlead1").password(passwordEncoder.encode("TeamLead@123")).email("teamlead@helpdesk.com").fullName("Team Lead One").role(Role.ROLE_TEAM_LEAD).department(it).employeeId("EMP002").designation("IT Team Lead").active(true).build());
            userRepository.save(User.builder().username("engineer1").password(passwordEncoder.encode("Engineer@123")).email("engineer1@helpdesk.com").fullName("Support Engineer One").role(Role.ROLE_SUPPORT_ENGINEER).department(it).employeeId("EMP003").designation("IT Support Engineer").active(true).build());
            userRepository.save(User.builder().username("engineer2").password(passwordEncoder.encode("Engineer@123")).email("engineer2@helpdesk.com").fullName("Support Engineer Two").role(Role.ROLE_SUPPORT_ENGINEER).department(it).employeeId("EMP004").designation("IT Support Engineer").active(true).build());
            userRepository.save(User.builder().username("employee1").password(passwordEncoder.encode("Employee@123")).email("employee1@helpdesk.com").fullName("John Employee").role(Role.ROLE_EMPLOYEE).department(departmentRepository.findAll().get(1)).employeeId("EMP005").designation("HR Executive").active(true).build());
            userRepository.save(User.builder().username("employee2").password(passwordEncoder.encode("Employee@123")).email("employee2@helpdesk.com").fullName("Jane Employee").role(Role.ROLE_EMPLOYEE).department(departmentRepository.findAll().get(2)).employeeId("EMP006").designation("Finance Analyst").active(true).build());
            log.info("Default users created");
        }
    }
}
