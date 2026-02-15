package com.example.stud_teacher.repository;

import com.example.stud_teacher.entity.Role;
import com.example.stud_teacher.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveUser_Success() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .password("password")
                .role(Role.STUDENT)
                .enabled(true)
                .build();

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("john@test.com", saved.getEmail());
    }

    @Test
    void findByEmail_Success() {
        User user = User.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane@test.com")
                .password("password")
                .role(Role.TEACHER)
                .enabled(true)
                .build();
        userRepository.save(user);

        var found = userRepository.findByEmail("jane@test.com");

        assertTrue(found.isPresent());
        assertEquals("Jane", found.get().getFirstName());
    }

    @Test
    void existsByEmail_True() {
        User user = User.builder()
                .firstName("Test")
                .lastName("User")
                .email("exists@test.com")
                .password("password")
                .role(Role.STUDENT)
                .enabled(true)
                .build();
        userRepository.save(user);

        assertTrue(userRepository.existsByEmail("exists@test.com"));
    }

    @Test
    void existsByEmail_False() {
        assertFalse(userRepository.existsByEmail("notexists@test.com"));
    }
}
