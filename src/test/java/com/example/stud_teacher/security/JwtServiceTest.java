package com.example.stud_teacher.security;

import com.example.stud_teacher.entity.Role;
import com.example.stud_teacher.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Set required fields via reflection
        ReflectionTestUtils.setField(jwtService, "secretKey", 
            "dGhpc2lzYXZlcnlsb25nc2VjcmV0a2V5Zm9ydGVzdGluZ3B1cnBvc2VzYW5kaXRtdXN0YmVhdGxlYXN0MjU2Yml0cw==");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L);

        testUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .firstName("John")
                .lastName("Doe")
                .role(Role.STUDENT)
                .password("password")
                .enabled(true)
                .build();
    }

    @Test
    void generateToken_Success() {
        String token = jwtService.generateToken(testUser);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_Success() {
        String token = jwtService.generateToken(testUser);

        String username = jwtService.extractUsername(token);

        assertEquals("test@test.com", username);
    }

    @Test
    void isTokenValid_Success() {
        String token = jwtService.generateToken(testUser);

        boolean isValid = jwtService.isTokenValid(token, testUser);

        assertTrue(isValid);
    }

    @Test
    void isTokenValid_WrongUser_ReturnsFalse() {
        String token = jwtService.generateToken(testUser);

        User differentUser = User.builder()
                .email("different@test.com")
                .build();

        boolean isValid = jwtService.isTokenValid(token, differentUser);

        assertFalse(isValid);
    }
}
