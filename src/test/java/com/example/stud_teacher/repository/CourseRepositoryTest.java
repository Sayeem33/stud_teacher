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
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    private Department testDept;

    @BeforeEach
    void setUp() {
        testDept = departmentRepository.save(Department.builder()
                .name("Computer Science")
                .code("CS")
                .build());
    }

    @Test
    void saveCourse_Success() {
        Course course = Course.builder()
                .code("CS101")
                .name("Intro to Programming")
                .credits(3)
                .department(testDept)
                .build();

        Course saved = courseRepository.save(course);

        assertNotNull(saved.getId());
        assertEquals("CS101", saved.getCode());
    }

    @Test
    void findByCode_Success() {
        Course course = Course.builder()
                .code("CS102")
                .name("Data Structures")
                .credits(3)
                .department(testDept)
                .build();
        courseRepository.save(course);

        var found = courseRepository.findByCode("CS102");

        assertTrue(found.isPresent());
        assertEquals("Data Structures", found.get().getName());
    }

    @Test
    void existsByCode_True() {
        Course course = Course.builder()
                .code("CS103")
                .name("Algorithms")
                .credits(3)
                .department(testDept)
                .build();
        courseRepository.save(course);

        assertTrue(courseRepository.existsByCode("CS103"));
    }

    @Test
    void existsByCode_False() {
        assertFalse(courseRepository.existsByCode("NOTEXIST"));
    }
}
