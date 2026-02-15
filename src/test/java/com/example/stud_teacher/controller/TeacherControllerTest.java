package com.example.stud_teacher.controller;

import com.example.stud_teacher.dto.TeacherDTO;
import com.example.stud_teacher.service.TeacherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherControllerTest {

    @Mock
    private TeacherService teacherService;

    @InjectMocks
    private TeacherController teacherController;

    @Test
    void getAllTeachers_Success() {
        TeacherDTO dto = TeacherDTO.builder()
                .id(1L)
                .teacherId("TCH001")
                .build();

        when(teacherService.getAllTeachers()).thenReturn(Arrays.asList(dto));

        var response = teacherController.getAllTeachers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getTeacherById_Success() {
        TeacherDTO dto = TeacherDTO.builder()
                .id(1L)
                .teacherId("TCH001")
                .build();

        when(teacherService.getTeacherById(1L)).thenReturn(dto);

        var response = teacherController.getTeacherById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteTeacher_Success() {
        doNothing().when(teacherService).deleteTeacher(1L);

        var response = teacherController.deleteTeacher(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(teacherService).deleteTeacher(1L);
    }
}
