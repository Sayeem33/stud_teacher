package com.example.stud_teacher.service;

import com.example.stud_teacher.entity.Department;
import com.example.stud_teacher.entity.Role;
import com.example.stud_teacher.entity.Teacher;
import com.example.stud_teacher.entity.User;
import com.example.stud_teacher.exception.ResourceNotFoundException;
import com.example.stud_teacher.repository.DepartmentRepository;
import com.example.stud_teacher.repository.TeacherRepository;
import com.example.stud_teacher.repository.UserRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher testTeacher;
    private User testUser;
    private Department testDepartment;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("teacher@test.com")
                .firstName("Jane")
                .lastName("Smith")
                .role(Role.TEACHER)
                .build();

        testDepartment = Department.builder()
                .id(1L)
                .name("Computer Science")
                .code("CS")
                .build();

        testTeacher = Teacher.builder()
                .id(1L)
                .teacherId("TCH001")
                .user(testUser)
                .department(testDepartment)
                .specialization("AI")
                .courses(new HashSet<>())
                .build();
    }

    @Test
    void getAllTeachers_Success() {
        when(teacherRepository.findAll()).thenReturn(Arrays.asList(testTeacher));

        var result = teacherService.getAllTeachers();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getTeacherById_Success() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(testTeacher));

        var result = teacherService.getTeacherById(1L);

        assertNotNull(result);
        assertEquals("TCH001", result.getTeacherId());
    }

    @Test
    void getTeacherById_NotFound() {
        when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> teacherService.getTeacherById(99L));
    }

    @Test
    void deleteTeacher_Success() {
        when(teacherRepository.existsById(1L)).thenReturn(true);
        doNothing().when(teacherRepository).deleteById(1L);

        assertDoesNotThrow(() -> teacherService.deleteTeacher(1L));
        verify(teacherRepository).deleteById(1L);
    }
}
