package com.example.stud_teacher.controller;

import com.example.stud_teacher.dto.StudentDTO;
import com.example.stud_teacher.service.StudentService;
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
class StudentControllerTest {

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    @Test
    void getAllStudents_Success() {
        StudentDTO dto = StudentDTO.builder()
                .id(1L)
                .studentId("STU001")
                .build();

        when(studentService.getAllStudents()).thenReturn(Arrays.asList(dto));

        var response = studentController.getAllStudents();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getStudentById_Success() {
        StudentDTO dto = StudentDTO.builder()
                .id(1L)
                .studentId("STU001")
                .build();

        when(studentService.getStudentById(1L)).thenReturn(dto);

        var response = studentController.getStudentById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteStudent_Success() {
        doNothing().when(studentService).deleteStudent(1L);

        var response = studentController.deleteStudent(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(studentService).deleteStudent(1L);
    }
}
