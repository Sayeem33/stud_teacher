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
class TeacherRepositoryTest {

    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    private User testUser;
    private Department testDept;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("teacher@test.com")
                .password("password")
                .role(Role.TEACHER)
                .enabled(true)
                .build());

        testDept = departmentRepository.save(Department.builder()
                .name("Computer Science")
                .code("CS")
                .build());
    }

    @Test
    void saveTeacher_Success() {
        Teacher teacher = Teacher.builder()
                .teacherId("TCH001")
                .user(testUser)
                .department(testDept)
                .specialization("AI")
                .build();

        Teacher saved = teacherRepository.save(teacher);

        assertNotNull(saved.getId());
        assertEquals("TCH001", saved.getTeacherId());
    }

    @Test
    void findByTeacherId_Success() {
        Teacher teacher = Teacher.builder()
                .teacherId("TCH002")
                .user(testUser)
                .department(testDept)
                .build();
        teacherRepository.save(teacher);

        var found = teacherRepository.findByTeacherId("TCH002");

        assertTrue(found.isPresent());
    }

    @Test
    void findByUserId_Success() {
        Teacher teacher = Teacher.builder()
                .teacherId("TCH003")
                .user(testUser)
                .department(testDept)
                .build();
        teacherRepository.save(teacher);

        var found = teacherRepository.findByUserId(testUser.getId());

        assertTrue(found.isPresent());
    }
}
