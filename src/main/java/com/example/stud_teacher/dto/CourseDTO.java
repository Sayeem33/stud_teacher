package com.example.stud_teacher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    
    private Long id;
    
    @NotBlank(message = "Course code is required")
    private String code;
    
    @NotBlank(message = "Course name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Credits is required")
    private Integer credits;
    
    private Long departmentId;
    private String departmentName;
    
    private Long teacherId;
    private String teacherName;
}
