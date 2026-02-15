package com.example.stud_teacher.service;

import com.example.stud_teacher.dto.DepartmentDTO;
import com.example.stud_teacher.entity.Department;
import com.example.stud_teacher.exception.ResourceNotFoundException;
import com.example.stud_teacher.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    private Department testDepartment;

    @BeforeEach
    void setUp() {
        testDepartment = Department.builder()
                .id(1L)
                .name("Computer Science")
                .code("CS")
                .description("CS Department")
                .build();
    }

    @Test
    void getAllDepartments_Success() {
        when(departmentRepository.findAll()).thenReturn(Arrays.asList(testDepartment));

        var result = departmentService.getAllDepartments();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getDepartmentById_Success() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));

        var result = departmentService.getDepartmentById(1L);

        assertNotNull(result);
        assertEquals("CS", result.getCode());
    }

    @Test
    void getDepartmentById_NotFound() {
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> departmentService.getDepartmentById(99L));
    }

    @Test
    void createDepartment_Success() {
        DepartmentDTO dto = DepartmentDTO.builder()
                .name("Physics")
                .code("PHY")
                .description("Physics Department")
                .build();

        when(departmentRepository.existsByCode("PHY")).thenReturn(false);
        when(departmentRepository.existsByName("Physics")).thenReturn(false);
        when(departmentRepository.save(any())).thenReturn(testDepartment);

        var result = departmentService.createDepartment(dto);

        assertNotNull(result);
        verify(departmentRepository).save(any());
    }

    @Test
    void deleteDepartment_Success() {
        when(departmentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(departmentRepository).deleteById(1L);

        assertDoesNotThrow(() -> departmentService.deleteDepartment(1L));
        verify(departmentRepository).deleteById(1L);
    }
}
