package com.example.stud_teacher.controller;

import com.example.stud_teacher.dto.CourseDTO;
import com.example.stud_teacher.service.CourseService;
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
class CourseControllerTest {

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController courseController;

    @Test
    void getAllCourses_Success() {
        CourseDTO dto = CourseDTO.builder()
                .id(1L)
                .code("CS101")
                .name("Intro to Programming")
                .build();

        when(courseService.getAllCourses()).thenReturn(Arrays.asList(dto));

        var response = courseController.getAllCourses();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getCourseById_Success() {
        CourseDTO dto = CourseDTO.builder()
                .id(1L)
                .code("CS101")
                .build();

        when(courseService.getCourseById(1L)).thenReturn(dto);

        var response = courseController.getCourseById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void createCourse_Success() {
        CourseDTO dto = CourseDTO.builder()
                .code("CS102")
                .name("Data Structures")
                .credits(3)
                .build();

        when(courseService.createCourse(any())).thenReturn(dto);

        var response = courseController.createCourse(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteCourse_Success() {
        doNothing().when(courseService).deleteCourse(1L);

        var response = courseController.deleteCourse(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(courseService).deleteCourse(1L);
    }
}
