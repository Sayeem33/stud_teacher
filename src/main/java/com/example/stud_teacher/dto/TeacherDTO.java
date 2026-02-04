package com.example.stud_teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDTO {
    
    private Long id;
    private String teacherId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String qualification;
    private String specialization;
    private Integer joiningYear;
    private Long departmentId;
    private String departmentName;
    private List<CourseDTO> courses;
}
