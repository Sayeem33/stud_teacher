package com.example.stud_teacher.service;

import com.example.stud_teacher.dto.DepartmentDTO;
import com.example.stud_teacher.entity.Department;
import com.example.stud_teacher.exception.BadRequestException;
import com.example.stud_teacher.exception.ResourceNotFoundException;
import com.example.stud_teacher.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public DepartmentDTO getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        return mapToDTO(department);
    }

    public DepartmentDTO getDepartmentByCode(String code) {
        Department department = departmentRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with code: " + code));
        return mapToDTO(department);
    }

    @Transactional
    public DepartmentDTO createDepartment(DepartmentDTO dto) {
        if (departmentRepository.existsByCode(dto.getCode())) {
            throw new BadRequestException("Department with code " + dto.getCode() + " already exists");
        }
        if (departmentRepository.existsByName(dto.getName())) {
            throw new BadRequestException("Department with name " + dto.getName() + " already exists");
        }

        Department department = Department.builder()
                .name(dto.getName())
                .code(dto.getCode())
                .description(dto.getDescription())
                .build();

        department = departmentRepository.save(department);
        return mapToDTO(department);
    }

    @Transactional
    public DepartmentDTO updateDepartment(Long id, DepartmentDTO dto) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));

        if (!department.getCode().equals(dto.getCode()) && departmentRepository.existsByCode(dto.getCode())) {
            throw new BadRequestException("Department with code " + dto.getCode() + " already exists");
        }

        department.setName(dto.getName());
        department.setCode(dto.getCode());
        department.setDescription(dto.getDescription());

        department = departmentRepository.save(department);
        return mapToDTO(department);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department not found with id: " + id);
        }
        departmentRepository.deleteById(id);
    }

    private DepartmentDTO mapToDTO(Department department) {
        return DepartmentDTO.builder()
                .id(department.getId())
                .name(department.getName())
                .code(department.getCode())
                .description(department.getDescription())
                .build();
    }
}
