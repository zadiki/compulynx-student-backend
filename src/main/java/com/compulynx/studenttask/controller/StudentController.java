package com.compulynx.studenttask.controller;

import com.compulynx.studenttask.exception.DataDoesNotExistException;
import com.compulynx.studenttask.model.Status;
import com.compulynx.studenttask.model.StudentClass;
import com.compulynx.studenttask.model.db.Student;
import com.compulynx.studenttask.model.db.UserInfo;
import com.compulynx.studenttask.service.FileUploadService;
import com.compulynx.studenttask.service.StudentService;

import com.compulynx.studenttask.service.StudentsLogService;
import com.compulynx.studenttask.util.ExcelGenerator;
import com.compulynx.studenttask.util.UserUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Tag(description = "Student management system", name = "Student")
@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class StudentController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserUtil userUtil;
    private UserInfo loggedInUser;
    @Autowired
    private StudentsLogService studentsLogService;


    @GetMapping("/")
    public Page<Student> filterStudents(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOfBirth,
            @RequestParam(required = false) StudentClass studentClass,
            @RequestParam(required = false) Integer score,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false, defaultValue = "2") int pendingTask,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws Exception {
        return studentService.filterStudents(firstName, lastName, dateOfBirth, studentClass, score, status, PageRequest.of(page, size), pendingTask);

    }

    @GetMapping("/generateStudentsExcel")
    public ResponseEntity<List<Student>> generateStudents(@RequestParam @Min(0) @Max(100000) int count) throws Exception {
        var students = studentService.generateStudentList(count);
        loggedInUser = userUtil.getLoggedInUser().orElseThrow();
        var fileToCreate = loggedInUser.getUserName() + ".xlsx";
        studentService.generateStudentListExcel(students, fileToCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(students);
    }

    @GetMapping("/generateStudentsCSV")
    public ResponseEntity<List<Student>> generateStudentsCSVfromExcel() throws Exception {
        loggedInUser = userUtil.getLoggedInUser().orElseThrow();
        var fileToCreate = loggedInUser.getUserName() + ".csv";
        var fileToRead = loggedInUser.getUserName() + ".xlsx";
        var students = studentService.generateStudentsCSVfromExcel(fileToRead, fileToCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(students);
    }

    @GetMapping("/saveGeneratedStudents")
    public ResponseEntity<List<Student>> saveGeneratedStudents() throws Exception {
        loggedInUser = userUtil.getLoggedInUser().orElseThrow();
        var fileName = loggedInUser.getUserName() + ".xlsx";
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
//        Student student = studentService.updateStudent(id, updatedStudent);
        loggedInUser = userUtil.getLoggedInUser().orElseThrow();
        if (!Objects.equals(updatedStudent.getStudentId(), id))
            throw new DataDoesNotExistException("path id and body id do not match");

        var optionalStudent = studentService.findStudentById(id).orElseThrow(() -> new DataDoesNotExistException("Provided student id does not exist"));

        studentsLogService.cresteStudentLogsFromNewStudent(loggedInUser, updatedStudent);
        return ResponseEntity.ok(updatedStudent); // the same opject.student update not yet persisted

    }


    @PutMapping("/upload/{id}")
    public ResponseEntity<Student> uploadImage(@RequestParam("image") MultipartFile file, @PathVariable("id") Long id, @RequestParam("studentData") String studentDataJson) throws Exception {

        var optionalStudent = studentService.findStudentById(id);
        if (optionalStudent.isEmpty()) {
            throw new DataDoesNotExistException("Provided student id does not exist");
        }
        if (file.isEmpty()) {
            throw new IOException("No file uploaded.");
        }

        Student studentDTO = objectMapper.readValue(studentDataJson, Student.class);
        if (!Objects.equals(studentDTO.getStudentId(), id))
            throw new DataDoesNotExistException("path id and body id do not match");

        String uploadedFilePath = fileUploadService.imageUpload(file);

        studentDTO.setPhotoPath(uploadedFilePath);

        // studentService.updateStudent(studentDTO);
        loggedInUser = userUtil.getLoggedInUser().orElseThrow();
        studentsLogService.cresteStudentLogsFromNewStudent(loggedInUser, studentDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(studentDTO);
    }

    @GetMapping("excel")
    public ResponseEntity<byte[]> exportStudentsToExcel() {
        try {
            List<Student> students = studentService.getAllStudents();
            byte[] excelBytes = ExcelGenerator.generateExcel(students);
            HttpHeaders headers = new HttpHeaders();
//            headers.add("Content-Disposition", "attachment; filename=students.xlsx");
            headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
