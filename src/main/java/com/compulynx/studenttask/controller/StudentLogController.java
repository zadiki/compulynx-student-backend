package com.compulynx.studenttask.controller;

import com.compulynx.studenttask.model.StudentLogRequest;
import com.compulynx.studenttask.model.db.Student;
import com.compulynx.studenttask.model.db.StudentLog;
import com.compulynx.studenttask.service.StudentsLogService;
import com.compulynx.studenttask.util.UserUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(description = "Student management system", name = "StudentLog")
@RestController
@RequestMapping("/api/logs")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class StudentLogController {
    @Autowired
    private StudentsLogService studentsLogService;
    @Autowired
    UserUtil userUtil;

    @GetMapping("")
    public ResponseEntity<List<StudentLog>> getAllStudentLog() {
        var studentLogs = studentsLogService.findAllStudentLogs();
        return ResponseEntity.status(HttpStatus.OK).body(studentLogs);
    }


    @PutMapping("")
    public ResponseEntity<StudentLog> approveStudentLog(
            @RequestBody StudentLog studentLog
    ) throws Exception {
        var loggedInUser = userUtil.getLoggedInUser().orElseThrow();

        var updatedStudentLog = studentsLogService.updateStudentLog(loggedInUser, studentLog);
        return ResponseEntity.status(HttpStatus.OK).body(updatedStudentLog);
    }

}
