package com.example.demo;

import com.example.demo.entity.Course;
import com.example.demo.entity.Department;
import com.example.demo.entity.Student;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.CourseService;
import com.example.demo.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class IntegrationTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    void testStudentLifecycle() {
        // Create a department
        Department department = new Department();
        department.setName("Computer Science");
        department = departmentRepository.save(department);

        // Create a student
        Student student = new Student();
        student.setName("John Doe");
        student.setUsername("johndoe");
        student.setPassword("password123");
        student.setDepartment(department);

        Student savedStudent = studentService.saveStudent(student);
        assertNotNull(savedStudent.getId());
        assertEquals("John Doe", savedStudent.getName());

        // Retrieve the student
        List<Student> students = studentService.getAllStudents();
        assertTrue(students.size() > 0);

        // Delete the student
        studentService.deleteStudent(savedStudent.getId());
        assertFalse(studentRepository.findById(savedStudent.getId()).isPresent());
    }

    @Test
    void testCourseLifecycle() {
        // Create a course
        Course course = new Course();
        course.setTitle("Advanced Mathematics");

        Course savedCourse = courseService.createCourse(course);
        assertNotNull(savedCourse.getId());
        assertEquals("Advanced Mathematics", savedCourse.getTitle());

        // Retrieve all courses
        List<Course> courses = courseService.getAllCourses();
        assertTrue(courses.size() > 0);
    }

    @Test
    void testStudentEnrollmentInCourse() {
        // Create a course
        Course course = new Course();
        course.setTitle("Physics");
        Course savedCourse = courseService.createCourse(course);

        // Create a student
        Student student = new Student();
        student.setName("Jane Smith");
        student.setUsername("janesmith");
        student.setPassword("password456");
        Student savedStudent = studentService.saveStudent(student);

        // Enroll student in course
        studentService.enrollStudentInCourse(savedStudent.getId(), savedCourse.getId());

        // Verify enrollment
        Student enrolledStudent = studentRepository.findById(savedStudent.getId()).orElseThrow();
        assertNotNull(enrolledStudent.getCourses());
        assertEquals(1, enrolledStudent.getCourses().size());
        assertEquals("Physics", enrolledStudent.getCourses().get(0).getTitle());
    }

    @Test
    void testDepartmentStudentRelationship() {
        // Create a department
        Department department = new Department();
        department.setName("Engineering");
        Department savedDepartment = departmentRepository.save(department);

        // Create students in that department
        Student student1 = new Student();
        student1.setName("Alice");
        student1.setUsername("alice");
        student1.setPassword("pass1");
        student1.setDepartment(savedDepartment);

        Student student2 = new Student();
        student2.setName("Bob");
        student2.setUsername("bob");
        student2.setPassword("pass2");
        student2.setDepartment(savedDepartment);

        studentService.saveStudent(student1);
        studentService.saveStudent(student2);

        // Verify department has students by querying students table
        // (avoids lazy loading issue with @OneToMany relationship)
        List<Student> studentsInDept = studentRepository.findByDepartmentId(savedDepartment.getId());
        assertNotNull(studentsInDept);
        assertTrue(studentsInDept.size() >= 2);
    }

    @Test
    void testFullWorkflow() {
        // 1. Create department
        Department dept = new Department();
        dept.setName("Mathematics");
        dept = departmentRepository.save(dept);

        // 2. Create courses
        Course mathCourse = new Course();
        mathCourse.setTitle("Calculus I");
        Course savedCourse = courseService.createCourse(mathCourse);

        // 3. Create student
        Student student = new Student();
        student.setName("Test Student");
        student.setUsername("teststudent");
        student.setPassword("testpass");
        student.setDepartment(dept);
        Student savedStudent = studentService.saveStudent(student);

        // 4. Enroll in course
        studentService.enrollStudentInCourse(savedStudent.getId(), savedCourse.getId());

        // 5. Verify everything
        Student finalStudent = studentRepository.findById(savedStudent.getId()).orElseThrow();
        assertEquals("Test Student", finalStudent.getName());
        assertNotNull(finalStudent.getDepartment());
        assertEquals("Mathematics", finalStudent.getDepartment().getName());
        assertEquals(1, finalStudent.getCourses().size());
        assertEquals("Calculus I", finalStudent.getCourses().get(0).getTitle());
    }
}
