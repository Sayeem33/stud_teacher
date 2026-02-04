package com.example.stud_teacher.controller;

import com.example.stud_teacher.dto.ApiResponse;
import com.example.stud_teacher.dto.StudentDTO;
import com.example.stud_teacher.dto.StudentUpdateDTO;
import com.example.stud_teacher.entity.User;
import com.example.stud_teacher.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // ============== Self-access endpoints (for STUDENT role) ==============

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<StudentDTO>> getCurrentStudent(
            @AuthenticationPrincipal User user
    ) {
        StudentDTO student = studentService.getStudentByEmail(user.getEmail());
        return ResponseEntity.ok(ApiResponse.success(student));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<StudentDTO>> updateCurrentStudent(
            @AuthenticationPrincipal User user,
            @RequestBody StudentUpdateDTO dto
    ) {
        StudentDTO currentStudent = studentService.getStudentByEmail(user.getEmail());
        StudentDTO student = studentService.updateStudent(currentStudent.getId(), dto);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", student));
    }

    @PostMapping("/me/courses/{courseId}")
    public ResponseEntity<ApiResponse<StudentDTO>> enrollInCourse(
            @AuthenticationPrincipal User user,
            @PathVariable Long courseId
    ) {
        StudentDTO currentStudent = studentService.getStudentByEmail(user.getEmail());
        StudentDTO student = studentService.enrollInCourse(currentStudent.getId(), courseId);
        return ResponseEntity.ok(ApiResponse.success("Successfully enrolled in course", student));
    }

    @DeleteMapping("/me/courses/{courseId}")
    public ResponseEntity<ApiResponse<StudentDTO>> dropCourse(
            @AuthenticationPrincipal User user,
            @PathVariable Long courseId
    ) {
        StudentDTO currentStudent = studentService.getStudentByEmail(user.getEmail());
        StudentDTO student = studentService.dropCourse(currentStudent.getId(), courseId);
        return ResponseEntity.ok(ApiResponse.success("Successfully dropped from course", student));
    }

    @GetMapping("/my-department")
    public ResponseEntity<ApiResponse<List<StudentDTO>>> getMyDepartmentStudents(
            @AuthenticationPrincipal User user
    ) {
        StudentDTO currentStudent = studentService.getStudentByEmail(user.getEmail());
        List<StudentDTO> students = studentService.getStudentsByDepartment(currentStudent.getDepartmentId());
        return ResponseEntity.ok(ApiResponse.success(students));
    }

    // ============== Admin/Teacher access endpoints ==============

    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentDTO>>> getAllStudents() {
        List<StudentDTO> students = studentService.getAllStudents();
        return ResponseEntity.ok(ApiResponse.success(students));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentDTO>> getStudentById(@PathVariable Long id) {
        StudentDTO student = studentService.getStudentById(id);
        return ResponseEntity.ok(ApiResponse.success(student));
    }

    @GetMapping("/studentId/{studentId}")
    public ResponseEntity<ApiResponse<StudentDTO>> getStudentByStudentId(@PathVariable String studentId) {
        StudentDTO student = studentService.getStudentByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success(student));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<List<StudentDTO>>> getStudentsByDepartment(@PathVariable Long departmentId) {
        List<StudentDTO> students = studentService.getStudentsByDepartment(departmentId);
        return ResponseEntity.ok(ApiResponse.success(students));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<StudentDTO>>> getStudentsByCourse(@PathVariable Long courseId) {
        List<StudentDTO> students = studentService.getStudentsByCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(students));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentDTO>> updateStudent(
            @PathVariable Long id,
            @RequestBody StudentUpdateDTO dto
    ) {
        StudentDTO student = studentService.updateStudent(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Student updated successfully", student));
    }

    @PostMapping("/{studentId}/courses/{courseId}")
    public ResponseEntity<ApiResponse<StudentDTO>> enrollStudentInCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId
    ) {
        StudentDTO student = studentService.enrollInCourse(studentId, courseId);
        return ResponseEntity.ok(ApiResponse.success("Student enrolled in course successfully", student));
    }

    @DeleteMapping("/{studentId}/courses/{courseId}")
    public ResponseEntity<ApiResponse<StudentDTO>> dropStudentFromCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId
    ) {
        StudentDTO student = studentService.dropCourse(studentId, courseId);
        return ResponseEntity.ok(ApiResponse.success("Student dropped from course successfully", student));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.success("Student deleted successfully", null));
    }
}
