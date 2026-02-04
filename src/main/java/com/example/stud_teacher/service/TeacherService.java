package com.example.stud_teacher.service;

import com.example.stud_teacher.dto.CourseDTO;
import com.example.stud_teacher.dto.TeacherDTO;
import com.example.stud_teacher.dto.TeacherUpdateDTO;
import com.example.stud_teacher.entity.Department;
import com.example.stud_teacher.entity.Teacher;
import com.example.stud_teacher.entity.User;
import com.example.stud_teacher.exception.ResourceNotFoundException;
import com.example.stud_teacher.repository.DepartmentRepository;
import com.example.stud_teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final DepartmentRepository departmentRepository;

    public List<TeacherDTO> getAllTeachers() {
        return teacherRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public TeacherDTO getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
        return mapToDTO(teacher);
    }

    public TeacherDTO getTeacherByEmail(String email) {
        Teacher teacher = teacherRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with email: " + email));
        return mapToDTO(teacher);
    }

    public TeacherDTO getTeacherByTeacherId(String teacherId) {
        Teacher teacher = teacherRepository.findByTeacherId(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with teacherId: " + teacherId));
        return mapToDTO(teacher);
    }

    public List<TeacherDTO> getTeachersByDepartment(Long departmentId) {
        return teacherRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TeacherDTO updateTeacher(Long id, TeacherUpdateDTO dto) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));

        User user = teacher.getUser();
        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getPhone() != null) {
            teacher.setPhone(dto.getPhone());
        }
        if (dto.getQualification() != null) {
            teacher.setQualification(dto.getQualification());
        }
        if (dto.getSpecialization() != null) {
            teacher.setSpecialization(dto.getSpecialization());
        }
        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + dto.getDepartmentId()));
            teacher.setDepartment(department);
        }

        teacher = teacherRepository.save(teacher);
        return mapToDTO(teacher);
    }

    @Transactional
    public void deleteTeacher(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new ResourceNotFoundException("Teacher not found with id: " + id);
        }
        teacherRepository.deleteById(id);
    }

    private TeacherDTO mapToDTO(Teacher teacher) {
        List<CourseDTO> courseDTOs = teacher.getCourses().stream()
                .map(course -> CourseDTO.builder()
                        .id(course.getId())
                        .code(course.getCode())
                        .name(course.getName())
                        .credits(course.getCredits())
                        .build())
                .collect(Collectors.toList());

        return TeacherDTO.builder()
                .id(teacher.getId())
                .teacherId(teacher.getTeacherId())
                .email(teacher.getUser().getEmail())
                .firstName(teacher.getUser().getFirstName())
                .lastName(teacher.getUser().getLastName())
                .phone(teacher.getPhone())
                .qualification(teacher.getQualification())
                .specialization(teacher.getSpecialization())
                .joiningYear(teacher.getJoiningYear())
                .departmentId(teacher.getDepartment() != null ? teacher.getDepartment().getId() : null)
                .departmentName(teacher.getDepartment() != null ? teacher.getDepartment().getName() : null)
                .courses(courseDTOs)
                .build();
    }
}
