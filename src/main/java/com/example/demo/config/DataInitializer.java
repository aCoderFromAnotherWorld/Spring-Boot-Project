package com.example.demo.config;

import com.example.demo.entity.Student;
import com.example.demo.entity.Teacher;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (teacherRepository.count() == 0) {
            Teacher teacher = new Teacher();
            teacher.setName("Alice Teacher");
            teacher.setUsername("teacher_user");
            teacher.setPassword(passwordEncoder.encode("123"));
            teacherRepository.save(teacher);
        }

        if (studentRepository.count() == 0) {
            Student student = new Student();
            student.setName("Bob Student");
            student.setUsername("student_user");
            student.setPassword(passwordEncoder.encode("123"));
            studentRepository.save(student);
        }
    }
}
