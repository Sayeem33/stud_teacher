package com.example.stud_teacher.service;

import com.example.stud_teacher.dto.AuthResponse;
import com.example.stud_teacher.dto.LoginRequest;
import com.example.stud_teacher.dto.RegisterRequest;
import com.example.stud_teacher.entity.*;
import com.example.stud_teacher.exception.BadRequestException;
import com.example.stud_teacher.repository.*;
import com.example.stud_teacher.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role. Allowed values: STUDENT, TEACHER, ADMIN");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .enabled(true)
                .build();

        user = userRepository.save(user);

        // Create Student or Teacher profile based on role
        if (role == Role.STUDENT) {
            createStudentProfile(user, request);
        } else if (role == Role.TEACHER) {
            createTeacherProfile(user, request);
        }

        String jwtToken = jwtService.generateToken(user);
        
        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    private void createStudentProfile(User user, RegisterRequest request) {
        String studentId = "STU" + Year.now().getValue() + String.format("%04d", user.getId());
        
        Student student = Student.builder()
                .studentId(studentId)
                .user(user)
                .phone(request.getPhone())
                .address(request.getAddress())
                .enrollmentYear(request.getEnrollmentYear() != null ? request.getEnrollmentYear() : Year.now().getValue())
                .build();

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new BadRequestException("Department not found"));
            student.setDepartment(department);
        }

        studentRepository.save(student);
    }

    private void createTeacherProfile(User user, RegisterRequest request) {
        String teacherId = "TCH" + Year.now().getValue() + String.format("%04d", user.getId());
        
        Teacher teacher = Teacher.builder()
                .teacherId(teacherId)
                .user(user)
                .phone(request.getPhone())
                .qualification(request.getQualification())
                .specialization(request.getSpecialization())
                .joiningYear(request.getJoiningYear() != null ? request.getJoiningYear() : Year.now().getValue())
                .build();

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new BadRequestException("Department not found"));
            teacher.setDepartment(department);
        }

        teacherRepository.save(teacher);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        String jwtToken = jwtService.generateToken(user);
        
        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
}
