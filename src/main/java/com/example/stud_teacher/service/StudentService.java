package com.example.stud_teacher.service;

import com.example.stud_teacher.dto.CourseDTO;
import com.example.stud_teacher.dto.StudentDTO;
import com.example.stud_teacher.dto.StudentUpdateDTO;
import com.example.stud_teacher.entity.Course;
import com.example.stud_teacher.entity.Department;
import com.example.stud_teacher.entity.Student;
import com.example.stud_teacher.entity.User;
import com.example.stud_teacher.exception.BadRequestException;
import com.example.stud_teacher.exception.ResourceNotFoundException;
import com.example.stud_teacher.repository.CourseRepository;
import com.example.stud_teacher.repository.DepartmentRepository;
import com.example.stud_teacher.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;

    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return mapToDTO(student);
    }

    public StudentDTO getStudentByEmail(String email) {
        Student student = studentRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + email));
        return mapToDTO(student);
    }

    public StudentDTO getStudentByStudentId(String studentId) {
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with studentId: " + studentId));
        return mapToDTO(student);
    }

    public List<StudentDTO> getStudentsByDepartment(Long departmentId) {
        return studentRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<StudentDTO> getStudentsByCourse(Long courseId) {
        return studentRepository.findByCourseId(courseId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public StudentDTO updateStudent(Long id, StudentUpdateDTO dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        User user = student.getUser();
        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getPhone() != null) {
            student.setPhone(dto.getPhone());
        }
        if (dto.getAddress() != null) {
            student.setAddress(dto.getAddress());
        }
        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + dto.getDepartmentId()));
            student.setDepartment(department);
        }

        student = studentRepository.save(student);
        return mapToDTO(student);
    }

    @Transactional
    public StudentDTO enrollInCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        if (student.getCourses().contains(course)) {
            throw new BadRequestException("Student is already enrolled in this course");
        }

        student.addCourse(course);
        student = studentRepository.save(student);
        return mapToDTO(student);
    }

    @Transactional
    public StudentDTO dropCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        if (!student.getCourses().contains(course)) {
            throw new BadRequestException("Student is not enrolled in this course");
        }

        student.removeCourse(course);
        student = studentRepository.save(student);
        return mapToDTO(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    private StudentDTO mapToDTO(Student student) {
        List<CourseDTO> courseDTOs = student.getCourses().stream()
                .map(course -> CourseDTO.builder()
                        .id(course.getId())
                        .code(course.getCode())
                        .name(course.getName())
                        .credits(course.getCredits())
                        .build())
                .collect(Collectors.toList());

        return StudentDTO.builder()
                .id(student.getId())
                .studentId(student.getStudentId())
                .email(student.getUser().getEmail())
                .firstName(student.getUser().getFirstName())
                .lastName(student.getUser().getLastName())
                .phone(student.getPhone())
                .address(student.getAddress())
                .enrollmentYear(student.getEnrollmentYear())
                .departmentId(student.getDepartment() != null ? student.getDepartment().getId() : null)
                .departmentName(student.getDepartment() != null ? student.getDepartment().getName() : null)
                .courses(courseDTOs)
                .build();
    }
}
