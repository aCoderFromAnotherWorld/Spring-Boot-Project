package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.service.*;
import com.example.demo.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class SchoolController {

    @Autowired private StudentService studentService;
    // @Autowired private TeacherService teacherService;
    @Autowired private CourseService courseService;
    @Autowired private DepartmentRepository departmentRepository;

    // ==========================================
    //          PAGE NAVIGATION (VIEWS)
    // ==========================================

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // We provide the list of courses so students can see them to enroll
        model.addAttribute("allAvailableCourses", courseService.getAllCourses());
        return "dashboard";
    }

    @GetMapping("/students")
    public String listStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("newStudent", new Student()); // Blank object for the "Add" form
        return "students-list";
    }

    @GetMapping("/courses/new")
    @PreAuthorize("hasRole('TEACHER')")
    public String courseForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("courses", courseService.getAllCourses());
        return "course-form";
    }

    // ==========================================
    //          TEACHER ACTIONS
    // ==========================================

    // 1. Create a Course
    @PostMapping("/api/courses")
    @PreAuthorize("hasRole('TEACHER')")
    public String createCourse(@ModelAttribute Course course) {
        courseService.createCourse(course);
        return "redirect:/courses/new?success=created";
    }

    // 2. Add a Student (and assign Department)
    @PostMapping("/api/students/add")
    @PreAuthorize("hasRole('TEACHER')")
    public String addStudent(@ModelAttribute Student student, @RequestParam Long deptId) {
        Department dept = departmentRepository.findById(deptId).orElse(null);
        student.setDepartment(dept);
        studentService.saveStudent(student);
        return "redirect:/students?success=added";
    }

    // 3. Delete a Student
    @PostMapping("/api/students/delete/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/students?success=deleted";
    }

    // ==========================================
    //          STUDENT ACTIONS
    // ==========================================

    // 1. Enroll in a Course (Many-to-Many)
    @PostMapping("/api/courses/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public String enrollInCourse(@RequestParam Long courseId, Principal principal) {
        try {
            // In our DataInitializer, we created a student named "student_user"
            // Let's find that student dynamically instead of hardcoding 1L
            List<Student> students = studentService.getAllStudents();
            Student currentStudent = students.stream()
                    .filter(s -> s.getName().equals(principal.getName()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Student record not found in database"));

            studentService.enrollStudentInCourse(currentStudent.getId(), courseId);
            return "redirect:/dashboard?success=enrolled";
        } catch (Exception e) {
            // Redirect with an error message instead of showing the Whitelabel page
            return "redirect:/dashboard?error=enrollment_failed";
        }
    }

    // ==========================================
    //          REST API (FOR TESTING)
    // ==========================================
    @GetMapping("/api/data/students")
    @ResponseBody
    public List<Student> getStudentsJson() {
        return studentService.getAllStudents();
    }
}