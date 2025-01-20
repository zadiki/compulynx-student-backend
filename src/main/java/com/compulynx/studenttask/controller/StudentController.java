package com.compulynx.studenttask.controller;

import com.compulynx.studenttask.exception.DataDoesNotExistException;
import com.compulynx.studenttask.model.Status;
import com.compulynx.studenttask.model.StudentClass;
import com.compulynx.studenttask.model.db.Student;
import com.compulynx.studenttask.service.FileUploadService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Tag(description = "Student management system", name = "User")
@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class StudentController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private FileUploadService fileUploadService;
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
    public ResponseEntity<List<Student>> generateStudents(@RequestParam @Min(0) @Max(10000) int count) throws Exception {
        var students = studentService.generateStudentList(count);
        loggedInUserName = UserInfoDetails.getLoggedInUser().orElseThrow();
        var fileToCreate = loggedInUserName + ".xlsx";
        studentService.generateStudentListExcel(students, fileToCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(students);
    }

    @GetMapping("/generateStudentsCSV")
    public ResponseEntity<List<Student>> generateStudentsCSVfromExcel() throws Exception {
        loggedInUserName = UserInfoDetails.getLoggedInUser().orElseThrow();
        var fileToCreate = loggedInUserName + ".csv";
        var fileToRead = loggedInUserName + ".xlsx";
        var students = studentService.generateStudentsCSVfromExcel(fileToRead, fileToCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(students);
    }

    @GetMapping("/saveGeneratedStudents")
    public ResponseEntity<List<Student>> saveGeneratedStudents() throws Exception {
        loggedInUserName = UserInfoDetails.getLoggedInUser().orElseThrow();
        var fileName = loggedInUserName + ".xlsx";
        var students = studentService.saveGeneratedStudents(fileName);
        return ResponseEntity.status(HttpStatus.CREATED).body(students);
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<String> deleteStudent(@PathVariable("studentId") Long studentId) {
        try {
            studentService.deleteStudent(studentId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Student deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable("id") Long id, @RequestBody Student updatedStudent) throws Exception {
        Student student = studentService.updateStudent(id, updatedStudent);

        return ResponseEntity.ok(student); // Return the updated student

    }


    @PutMapping("/upload/{id}")
    public ResponseEntity<Student> uploadImage(@RequestParam("image") MultipartFile file,@PathVariable("id") Long id ) throws IOException {

        var optionalStudent=studentService.findStudentById(id);
        if(optionalStudent.isEmpty()){
            throw new DataDoesNotExistException("Provided student id does not exist");
        }
        if (file.isEmpty()) {
            throw new IOException("No file uploaded.");
        }
        String uploadedFilePath=fileUploadService.imageUpload(file);
        var student=optionalStudent.get();
        student.setPhotoPath(uploadedFilePath);
        studentService.updateStudent(student);

        return ResponseEntity.status(HttpStatus.CREATED).body(student);
    }
}
