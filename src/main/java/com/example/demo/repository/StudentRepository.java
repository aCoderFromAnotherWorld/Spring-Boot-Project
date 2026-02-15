package com.example.demo.repository;

import com.example.demo.entity.*;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
	Optional<Student> findByUsername(String username);
	List<Student> findByDepartmentId(Long departmentId);
}
