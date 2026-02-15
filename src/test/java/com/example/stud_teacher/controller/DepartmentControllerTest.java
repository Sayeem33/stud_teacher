package com.example.stud_teacher.controller;

import com.example.stud_teacher.dto.DepartmentDTO;
import com.example.stud_teacher.service.DepartmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentControllerTest {

    @Mock
    private DepartmentService departmentService;

    @InjectMocks
    private DepartmentController departmentController;

    @Test
    void getAllDepartments_Success() {
        DepartmentDTO dto = DepartmentDTO.builder()
                .id(1L)
                .code("CS")
                .name("Computer Science")
                .build();

        when(departmentService.getAllDepartments()).thenReturn(Arrays.asList(dto));

        var response = departmentController.getAllDepartments();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getDepartmentById_Success() {
        DepartmentDTO dto = DepartmentDTO.builder()
                .id(1L)
                .code("CS")
                .build();

        when(departmentService.getDepartmentById(1L)).thenReturn(dto);

        var response = departmentController.getDepartmentById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void createDepartment_Success() {
        DepartmentDTO dto = DepartmentDTO.builder()
                .code("PHY")
                .name("Physics")
                .build();

        when(departmentService.createDepartment(any())).thenReturn(dto);

        var response = departmentController.createDepartment(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteDepartment_Success() {
        doNothing().when(departmentService).deleteDepartment(1L);

        var response = departmentController.deleteDepartment(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(departmentService).deleteDepartment(1L);
    }
}
