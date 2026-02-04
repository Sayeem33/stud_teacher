package com.example.stud_teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentUpdateDTO {
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private Long departmentId;
}
