package com.example.stud_teacher.service;

import com.example.stud_teacher.dto.StudentDTO;
import com.example.stud_teacher.entity.Department;
import com.example.stud_teacher.entity.Role;
import com.example.stud_teacher.entity.Student;
import com.example.stud_teacher.entity.User;
import com.example.stud_teacher.exception.ResourceNotFoundException;
import com.example.stud_teacher.repository.CourseRepository;
import com.example.stud_teacher.repository.DepartmentRepository;
import com.example.stud_teacher.repository.StudentRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private StudentService studentService;

    private Student testStudent;
    private User testUser;
    private Department testDepartment;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("student@test.com")
                .firstName("John")
                .lastName("Doe")
                .role(Role.STUDENT)
                .build();

        testDepartment = Department.builder()
                .id(1L)
                .name("Computer Science")
                .code("CS")
                .build();

        testStudent = Student.builder()
                .id(1L)
                .studentId("STU001")
                .user(testUser)
                .department(testDepartment)
                .enrollmentYear(2024)
                .courses(new HashSet<>())
                .build();
    }

    @Test
    void getAllStudents_Success() {
        when(studentRepository.findAll()).thenReturn(Arrays.asList(testStudent));

        var result = studentService.getAllStudents();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getStudentById_Success() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));

        var result = studentService.getStudentById(1L);

        assertNotNull(result);
        assertEquals("STU001", result.getStudentId());
    }

    @Test
    void getStudentById_NotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studentService.getStudentById(99L));
    }

    @Test
    void deleteStudent_Success() {
        when(studentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(studentRepository).deleteById(1L);

        assertDoesNotThrow(() -> studentService.deleteStudent(1L));
        verify(studentRepository).deleteById(1L);
    }
}
