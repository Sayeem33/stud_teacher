package com.example.stud_teacher.controller;

import com.example.stud_teacher.dto.ApiResponse;
import com.example.stud_teacher.dto.DepartmentDTO;
import com.example.stud_teacher.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartmentDTO>>> getAllDepartments() {
        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(ApiResponse.success(departments));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentDTO>> getDepartmentById(@PathVariable Long id) {
        DepartmentDTO department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(ApiResponse.success(department));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<DepartmentDTO>> getDepartmentByCode(@PathVariable String code) {
        DepartmentDTO department = departmentService.getDepartmentByCode(code);
        return ResponseEntity.ok(ApiResponse.success(department));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentDTO>> createDepartment(
            @Valid @RequestBody DepartmentDTO dto
    ) {
        DepartmentDTO department = departmentService.createDepartment(dto);
        return ResponseEntity.ok(ApiResponse.success("Department created successfully", department));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentDTO>> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentDTO dto
    ) {
        DepartmentDTO department = departmentService.updateDepartment(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Department updated successfully", department));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(ApiResponse.success("Department deleted successfully", null));
    }
}
