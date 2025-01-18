package com.compulynx.studenttask.controller;

import com.compulynx.studenttask.model.db.Student;
import com.compulynx.studenttask.service.StudentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(description = "Student management system",name = "User")
@RestController
@RequestMapping("/api/student/")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("")
    public List<Student> generateStudents(@RequestParam @Min(0) @Max(10000) int count) throws Exception {
        var students= studentService.generateStudentList(count);
        studentService.generateStudentListExcel(students,"1.xlsx");
        return  students;
    }
}
