package com.example.stud_teacher.repository;

import com.example.stud_teacher.entity.Department;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class DepartmentRepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    void saveDepartment_Success() {
        Department dept = Department.builder()
                .name("Computer Science")
                .code("CS")
                .description("CS Department")
                .build();

        Department saved = departmentRepository.save(dept);

        assertNotNull(saved.getId());
        assertEquals("CS", saved.getCode());
    }

    @Test
    void findByCode_Success() {
        Department dept = Department.builder()
                .name("Physics")
                .code("PHY")
                .build();
        departmentRepository.save(dept);

        var found = departmentRepository.findByCode("PHY");

        assertTrue(found.isPresent());
        assertEquals("Physics", found.get().getName());
    }

    @Test
    void existsByCode_True() {
        Department dept = Department.builder()
                .name("Math")
                .code("MATH")
                .build();
        departmentRepository.save(dept);

        assertTrue(departmentRepository.existsByCode("MATH"));
    }

    @Test
    void existsByName_True() {
        Department dept = Department.builder()
                .name("Chemistry")
                .code("CHEM")
                .build();
        departmentRepository.save(dept);

        assertTrue(departmentRepository.existsByName("Chemistry"));
    }
}
