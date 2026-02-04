package com.example.stud_teacher.controller;

import com.example.stud_teacher.dto.ApiResponse;
import com.example.stud_teacher.dto.CourseDTO;
import com.example.stud_teacher.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseDTO>>> getAllCourses() {
        List<CourseDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseDTO>> getCourseById(@PathVariable Long id) {
        CourseDTO course = courseService.getCourseById(id);
        return ResponseEntity.ok(ApiResponse.success(course));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<CourseDTO>> getCourseByCode(@PathVariable String code) {
        CourseDTO course = courseService.getCourseByCode(code);
        return ResponseEntity.ok(ApiResponse.success(course));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<List<CourseDTO>>> getCoursesByDepartment(@PathVariable Long departmentId) {
        List<CourseDTO> courses = courseService.getCoursesByDepartment(departmentId);
        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<ApiResponse<List<CourseDTO>>> getCoursesByTeacher(@PathVariable Long teacherId) {
        List<CourseDTO> courses = courseService.getCoursesByTeacher(teacherId);
        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CourseDTO>> createCourse(
            @Valid @RequestBody CourseDTO dto
    ) {
        CourseDTO course = courseService.createCourse(dto);
        return ResponseEntity.ok(ApiResponse.success("Course created successfully", course));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseDTO>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseDTO dto
    ) {
        CourseDTO course = courseService.updateCourse(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Course updated successfully", course));
    }

    @PutMapping("/{courseId}/assign-teacher/{teacherId}")
    public ResponseEntity<ApiResponse<CourseDTO>> assignTeacher(
            @PathVariable Long courseId,
            @PathVariable Long teacherId
    ) {
        CourseDTO course = courseService.assignTeacher(courseId, teacherId);
        return ResponseEntity.ok(ApiResponse.success("Teacher assigned successfully", course));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.success("Course deleted successfully", null));
    }
}
