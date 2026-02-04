package com.example.stud_teacher.service;

import com.example.stud_teacher.dto.CourseDTO;
import com.example.stud_teacher.entity.Course;
import com.example.stud_teacher.entity.Department;
import com.example.stud_teacher.entity.Teacher;
import com.example.stud_teacher.exception.BadRequestException;
import com.example.stud_teacher.exception.ResourceNotFoundException;
import com.example.stud_teacher.repository.CourseRepository;
import com.example.stud_teacher.repository.DepartmentRepository;
import com.example.stud_teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final TeacherRepository teacherRepository;

    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return mapToDTO(course);
    }

    public CourseDTO getCourseByCode(String code) {
        Course course = courseRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with code: " + code));
        return mapToDTO(course);
    }

    public List<CourseDTO> getCoursesByDepartment(Long departmentId) {
        return courseRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<CourseDTO> getCoursesByTeacher(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<CourseDTO> getCoursesByStudent(Long studentId) {
        return courseRepository.findByStudentId(studentId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CourseDTO createCourse(CourseDTO dto) {
        if (courseRepository.existsByCode(dto.getCode())) {
            throw new BadRequestException("Course with code " + dto.getCode() + " already exists");
        }

        Course course = Course.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .credits(dto.getCredits())
                .build();

        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + dto.getDepartmentId()));
            course.setDepartment(department);
        }

        if (dto.getTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(dto.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + dto.getTeacherId()));
            course.setTeacher(teacher);
        }

        course = courseRepository.save(course);
        return mapToDTO(course);
    }

    @Transactional
    public CourseDTO updateCourse(Long id, CourseDTO dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        if (!course.getCode().equals(dto.getCode()) && courseRepository.existsByCode(dto.getCode())) {
            throw new BadRequestException("Course with code " + dto.getCode() + " already exists");
        }

        course.setCode(dto.getCode());
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setCredits(dto.getCredits());

        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + dto.getDepartmentId()));
            course.setDepartment(department);
        }

        if (dto.getTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(dto.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + dto.getTeacherId()));
            course.setTeacher(teacher);
        }

        course = courseRepository.save(course);
        return mapToDTO(course);
    }

    @Transactional
    public CourseDTO assignTeacher(Long courseId, Long teacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));
        
        course.setTeacher(teacher);
        course = courseRepository.save(course);
        return mapToDTO(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    private CourseDTO mapToDTO(Course course) {
        return CourseDTO.builder()
                .id(course.getId())
                .code(course.getCode())
                .name(course.getName())
                .description(course.getDescription())
                .credits(course.getCredits())
                .departmentId(course.getDepartment() != null ? course.getDepartment().getId() : null)
                .departmentName(course.getDepartment() != null ? course.getDepartment().getName() : null)
                .teacherId(course.getTeacher() != null ? course.getTeacher().getId() : null)
                .teacherName(course.getTeacher() != null ? 
                        course.getTeacher().getUser().getFirstName() + " " + course.getTeacher().getUser().getLastName() : null)
                .build();
    }
}
