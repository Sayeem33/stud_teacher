package com.example.stud_teacher.repository;

import com.example.stud_teacher.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentId(String studentId);
    Optional<Student> findByUserId(Long userId);
    Optional<Student> findByUserEmail(String email);
    boolean existsByStudentId(String studentId);
    List<Student> findByDepartmentId(Long departmentId);
    
    @Query("SELECT s FROM Student s JOIN s.courses c WHERE c.id = :courseId")
    List<Student> findByCourseId(@Param("courseId") Long courseId);
}
