package com.example.demo.service;

import com.example.demo.entity.Course;
import com.example.demo.entity.Student;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private StudentService studentService;

    private Student testStudent;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        testStudent = new Student();
        testStudent.setId(1L);
        testStudent.setName("John Doe");
        testStudent.setUsername("johndoe");
        testStudent.setPassword("password123");

        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setTitle("Mathematics");
    }

    @Test
    void testGetAllStudents() {
        // Arrange
        List<Student> students = new ArrayList<>();
        students.add(testStudent);
        when(studentRepository.findAll()).thenReturn(students);

        // Act
        List<Student> result = studentService.getAllStudents();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void testSaveStudent() {
        // Arrange
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

        // Act
        Student result = studentService.saveStudent(testStudent);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(studentRepository, times(1)).save(testStudent);
    }

    @Test
    void testDeleteStudent() {
        // Act
        studentService.deleteStudent(1L);

        // Assert
        verify(studentRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEnrollStudentInCourse() {
        // Arrange
        List<Course> courses = new ArrayList<>();
        testStudent.setCourses(courses);
        
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

        // Act
        studentService.enrollStudentInCourse(1L, 1L);

        // Assert
        verify(studentRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).findById(1L);
        verify(studentRepository, times(1)).save(testStudent);
    }

    @Test
    void testEnrollStudentInCourse_StudentNotFound() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> studentService.enrollStudentInCourse(1L, 1L));
    }

    @Test
    void testEnrollStudentInCourse_CourseNotFound() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> studentService.enrollStudentInCourse(1L, 1L));
    }
}
