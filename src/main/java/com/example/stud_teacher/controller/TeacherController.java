package com.example.stud_teacher.controller;

import com.example.stud_teacher.dto.ApiResponse;
import com.example.stud_teacher.dto.CourseDTO;
import com.example.stud_teacher.dto.TeacherDTO;
import com.example.stud_teacher.dto.TeacherUpdateDTO;
import com.example.stud_teacher.entity.User;
import com.example.stud_teacher.service.CourseService;
import com.example.stud_teacher.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final CourseService courseService;

    // ============== Self-access endpoints (for TEACHER role) ==============

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<TeacherDTO>> getCurrentTeacher(
            @AuthenticationPrincipal User user
    ) {
        TeacherDTO teacher = teacherService.getTeacherByEmail(user.getEmail());
        return ResponseEntity.ok(ApiResponse.success(teacher));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<TeacherDTO>> updateCurrentTeacher(
            @AuthenticationPrincipal User user,
            @RequestBody TeacherUpdateDTO dto
    ) {
        TeacherDTO currentTeacher = teacherService.getTeacherByEmail(user.getEmail());
        TeacherDTO teacher = teacherService.updateTeacher(currentTeacher.getId(), dto);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", teacher));
    }

    @GetMapping("/me/courses")
    public ResponseEntity<ApiResponse<List<CourseDTO>>> getMyCourses(
            @AuthenticationPrincipal User user
    ) {
        TeacherDTO currentTeacher = teacherService.getTeacherByEmail(user.getEmail());
        List<CourseDTO> courses = courseService.getCoursesByTeacher(currentTeacher.getId());
        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    // ============== Admin access endpoints ==============

    @GetMapping
    public ResponseEntity<ApiResponse<List<TeacherDTO>>> getAllTeachers() {
        List<TeacherDTO> teachers = teacherService.getAllTeachers();
        return ResponseEntity.ok(ApiResponse.success(teachers));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TeacherDTO>> getTeacherById(@PathVariable Long id) {
        TeacherDTO teacher = teacherService.getTeacherById(id);
        return ResponseEntity.ok(ApiResponse.success(teacher));
    }

    @GetMapping("/teacherId/{teacherId}")
    public ResponseEntity<ApiResponse<TeacherDTO>> getTeacherByTeacherId(@PathVariable String teacherId) {
        TeacherDTO teacher = teacherService.getTeacherByTeacherId(teacherId);
        return ResponseEntity.ok(ApiResponse.success(teacher));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<List<TeacherDTO>>> getTeachersByDepartment(@PathVariable Long departmentId) {
        List<TeacherDTO> teachers = teacherService.getTeachersByDepartment(departmentId);
        return ResponseEntity.ok(ApiResponse.success(teachers));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TeacherDTO>> updateTeacher(
            @PathVariable Long id,
            @RequestBody TeacherUpdateDTO dto
    ) {
        TeacherDTO teacher = teacherService.updateTeacher(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Teacher updated successfully", teacher));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.ok(ApiResponse.success("Teacher deleted successfully", null));
    }
}
