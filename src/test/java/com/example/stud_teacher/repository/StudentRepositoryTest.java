package com.example.stud_teacher.repository;

import com.example.stud_teacher.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    private User testUser;
    private Department testDept;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("student@test.com")
                .password("password")
                .role(Role.STUDENT)
                .enabled(true)
                .build());

        testDept = departmentRepository.save(Department.builder()
                .name("Computer Science")
                .code("CS")
                .build());
    }

    @Test
    void saveStudent_Success() {
        Student student = Student.builder()
                .studentId("STU001")
                .user(testUser)
                .department(testDept)
                .enrollmentYear(2024)
                .build();

        Student saved = studentRepository.save(student);

        assertNotNull(saved.getId());
        assertEquals("STU001", saved.getStudentId());
    }

    @Test
    void findByStudentId_Success() {
        Student student = Student.builder()
                .studentId("STU002")
                .user(testUser)
                .department(testDept)
                .enrollmentYear(2024)
                .build();
        studentRepository.save(student);

        var found = studentRepository.findByStudentId("STU002");

        assertTrue(found.isPresent());
    }

    @Test
    void findByUserId_Success() {
        Student student = Student.builder()
                .studentId("STU003")
                .user(testUser)
                .department(testDept)
                .enrollmentYear(2024)
                .build();
        studentRepository.save(student);

        var found = studentRepository.findByUserId(testUser.getId());

        assertTrue(found.isPresent());
    }
}
