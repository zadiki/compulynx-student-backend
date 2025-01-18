package com.compulynx.studenttask.controller;

import com.compulynx.studenttask.model.Status;
import com.compulynx.studenttask.model.StudentClass;
import com.compulynx.studenttask.model.db.Student;
import com.compulynx.studenttask.service.StudentService;
import com.compulynx.studenttask.service.UserInfoDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@Tag(description = "Student management system", name = "User")
@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class StudentController {

    @Autowired
    private StudentService studentService;
    String loggedInUserName;

    @GetMapping("/")
    public Page<Student> filterStudents(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dob,
            @RequestParam(required = false) StudentClass studentClass,
            @RequestParam(required = false) Integer score,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws Exception {
        return studentService.filterStudents(firstName, lastName, dob, studentClass, score, status, PageRequest.of(page, size));

    }

    @GetMapping("/generateStudentsExcel")
    public List<Student> generateStudents(@RequestParam @Min(0) @Max(10000) int count) throws Exception {
        var students = studentService.generateStudentList(count);
        loggedInUserName = UserInfoDetails.getLoggedInUser().orElseThrow();
        var fileToCreate = loggedInUserName + ".xlsx";
        studentService.generateStudentListExcel(students, fileToCreate);
        return students;
    }

    @GetMapping("/generateStudentsCSV")
    public List<Student> generateStudentsCSVfromExcel() throws Exception {
        loggedInUserName = UserInfoDetails.getLoggedInUser().orElseThrow();
        var fileToCreate = loggedInUserName + ".csv";
        var fileToRead = loggedInUserName + ".xlsx";
        return studentService.generateStudentsCSVfromExcel(fileToRead, fileToCreate);

    }

    @GetMapping("/saveGeneratedStudents")
    public List<Student> saveGeneratedStudents() throws Exception {
        loggedInUserName = UserInfoDetails.getLoggedInUser().orElseThrow();
        var fileName = loggedInUserName + ".xlsx";
        return studentService.saveGeneratedStudents(fileName);
    }
}
