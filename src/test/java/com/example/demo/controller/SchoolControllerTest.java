package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.example.demo.entity.Department;
import com.example.demo.entity.Student;
import com.example.demo.service.CourseService;
import com.example.demo.service.StudentService;
import com.example.demo.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SchoolController.class)
class SchoolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    private CourseService courseService;

    @MockBean
    private DepartmentRepository departmentRepository;

    @Test
    void login_ShouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @WithMockUser
    void dashboard_ShouldReturnDashboardView() throws Exception {
        List<Course> courses = Arrays.asList(createCourse(1L, "Mathematics"));
        when(courseService.getAllCourses()).thenReturn(courses);

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("allAvailableCourses"));
    }

    @Test
    @WithMockUser
    void listStudents_ShouldReturnStudentsListView() throws Exception {
        List<Student> students = Arrays.asList(createStudent(1L, "John Doe", "johndoe"));
        List<Department> departments = Arrays.asList(createDepartment(1L, "Computer Science"));

        when(studentService.getAllStudents()).thenReturn(students);
        when(departmentRepository.findAll()).thenReturn(departments);

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(view().name("students-list"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().attributeExists("newStudent"));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void courseForm_ShouldReturnCourseFormView() throws Exception {
        List<Course> courses = Arrays.asList(createCourse(1L, "Mathematics"));
        when(courseService.getAllCourses()).thenReturn(courses);

        mockMvc.perform(get("/courses/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("course-form"))
                .andExpect(model().attributeExists("course"))
                .andExpect(model().attributeExists("courses"));
    }

    @Test
    @WithMockUser
    void getStudentsJson_ShouldReturnStudentsAsJson() throws Exception {
        List<Student> students = Arrays.asList(
                createStudent(1L, "John Doe", "johndoe"),
                createStudent(2L, "Jane Smith", "janesmith")
        );
        when(studentService.getAllStudents()).thenReturn(students);

        mockMvc.perform(get("/api/data/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"));
    }

    @Test
    void home_WithNoPrincipal_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser
    void home_WithPrincipal_ShouldRedirectToDashboard() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    // Helper methods to create test objects
    private Student createStudent(Long id, String name, String username) {
        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setUsername(username);
        student.setPassword("password");
        return student;
    }

    private Course createCourse(Long id, String title) {
        Course course = new Course();
        course.setId(id);
        course.setTitle(title);
        return course;
    }

    private Department createDepartment(Long id, String name) {
        Department department = new Department();
        department.setId(id);
        department.setName(name);
        return department;
    }
}
