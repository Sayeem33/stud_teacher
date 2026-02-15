package com.example.stud_teacher.service;

import com.example.stud_teacher.dto.CourseDTO;
import com.example.stud_teacher.entity.Course;
import com.example.stud_teacher.entity.Department;
import com.example.stud_teacher.exception.ResourceNotFoundException;
import com.example.stud_teacher.repository.CourseRepository;
import com.example.stud_teacher.repository.DepartmentRepository;
import com.example.stud_teacher.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private CourseService courseService;

    private Course testCourse;
    private Department testDepartment;

    @BeforeEach
    void setUp() {
        testDepartment = Department.builder()
                .id(1L)
                .name("Computer Science")
                .code("CS")
                .build();

        testCourse = Course.builder()
                .id(1L)
                .code("CS101")
                .name("Intro to Programming")
                .credits(3)
                .department(testDepartment)
                .students(new HashSet<>())
                .build();
    }

    @Test
    void getAllCourses_Success() {
        when(courseRepository.findAll()).thenReturn(Arrays.asList(testCourse));

        var result = courseService.getAllCourses();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getCourseById_Success() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));

        var result = courseService.getCourseById(1L);

        assertNotNull(result);
        assertEquals("CS101", result.getCode());
    }

    @Test
    void getCourseById_NotFound() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.getCourseById(99L));
    }

    @Test
    void createCourse_Success() {
        CourseDTO dto = CourseDTO.builder()
                .code("CS102")
                .name("Data Structures")
                .credits(3)
                .departmentId(1L)
                .build();

        when(courseRepository.existsByCode("CS102")).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(courseRepository.save(any())).thenReturn(testCourse);

        var result = courseService.createCourse(dto);

        assertNotNull(result);
        verify(courseRepository).save(any());
    }

    @Test
    void deleteCourse_Success() {
        when(courseRepository.existsById(1L)).thenReturn(true);
        doNothing().when(courseRepository).deleteById(1L);

        assertDoesNotThrow(() -> courseService.deleteCourse(1L));
        verify(courseRepository).deleteById(1L);
    }
}
