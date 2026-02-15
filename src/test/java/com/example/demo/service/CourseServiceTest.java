package com.example.demo.service;

import com.example.demo.entity.Course;
import com.example.demo.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private Course testCourse;

    @BeforeEach
    void setUp() {
        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setTitle("Mathematics");
    }

    @Test
    void testCreateCourse() {
        // Arrange
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        // Act
        Course result = courseService.createCourse(testCourse);

        // Assert
        assertNotNull(result);
        assertEquals("Mathematics", result.getTitle());
        verify(courseRepository, times(1)).save(testCourse);
    }

    @Test
    void testGetAllCourses() {
        // Arrange
        List<Course> courses = new ArrayList<>();
        courses.add(testCourse);
        when(courseRepository.findAll()).thenReturn(courses);

        // Act
        List<Course> result = courseService.getAllCourses();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Mathematics", result.get(0).getTitle());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void testGetAllCourses_EmptyList() {
        // Arrange
        when(courseRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<Course> result = courseService.getAllCourses();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void testCreateMultipleCourses() {
        // Arrange
        Course course2 = new Course();
        course2.setId(2L);
        course2.setTitle("Physics");

        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        // Act
        Course result1 = courseService.createCourse(testCourse);
        Course result2 = courseService.createCourse(course2);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        verify(courseRepository, times(2)).save(any(Course.class));
    }
}
