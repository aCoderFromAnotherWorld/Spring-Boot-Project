package com.example.demo.config;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final StudentRepository studentRepo;
    private final TeacherRepository teacherRepo;
    private final DepartmentRepository deptRepo;
    private final PasswordEncoder encoder;

    public DataInitializer(StudentRepository studentRepo,
                           TeacherRepository teacherRepo,
                           DepartmentRepository deptRepo,
                           PasswordEncoder encoder) {
        this.studentRepo = studentRepo;
        this.teacherRepo = teacherRepo;
        this.deptRepo = deptRepo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        Department cse = deptRepo.findAll().stream().findFirst()
            .orElseGet(() -> { var d = new Department(); d.setName("CSE"); return deptRepo.save(d); });

        teacherRepo.findByUsername("teacher").orElseGet(() -> {
            var t = new Teacher();
            t.setName("Alice Teacher");
            t.setUsername("teacher");
            t.setPassword(encoder.encode("teacher123"));
            return teacherRepo.save(t);
        });

        studentRepo.findByUsername("student").orElseGet(() -> {
            var s = new Student();
            s.setName("Bob Student");
            s.setUsername("student");
            s.setPassword(encoder.encode("student123"));
            s.setDepartment(cse);
            return studentRepo.save(s);
        });
    }
}
