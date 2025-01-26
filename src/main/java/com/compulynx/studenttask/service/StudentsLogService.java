package com.compulynx.studenttask.service;

import com.compulynx.studenttask.exception.DuplicateEntryException;
import com.compulynx.studenttask.model.*;
import com.compulynx.studenttask.model.db.Student;
import com.compulynx.studenttask.model.db.StudentLog;
import com.compulynx.studenttask.model.db.UserInfo;
import com.compulynx.studenttask.repository.StudentLogRepository;
import com.compulynx.studenttask.repository.StudentRepository;
import com.compulynx.studenttask.repository.UserInfoRepository;
import com.compulynx.studenttask.util.DateFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class StudentsLogService {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private StudentLogRepository studentLogRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;


    public List<StudentLog> findAllStudentLogs() {
        return studentLogRepository.findAll();
    }

    @Transactional
    private void createNewStudentLogs(UserInfo loggedInUser, Student existingStudent, List<StudentLogRequest> studentLogRequests) {

        for (StudentLogRequest studentLogRequest : studentLogRequests) {
            var newStudentLog = new StudentLog();
            switch (studentLogRequest.getActionType()) {
                case STATUS_CHANGE -> {
                    newStudentLog.setOldValue(existingStudent.getStatus().name());
                }
                case SCORE_CHANGE -> {
                    newStudentLog.setOldValue(String.valueOf(existingStudent.getScore()));
                }
                case CLASS_CHANGE -> {
                    newStudentLog.setOldValue(existingStudent.getStudentClass().name());
                }
                case FNAME_CHANGE -> {
                    newStudentLog.setOldValue(existingStudent.getFirstName());
                }
                case LNAME_CHANGE -> {
                    newStudentLog.setOldValue(existingStudent.getLastName());
                }
                case DOB_CHANGE -> {
                    newStudentLog.setOldValue(existingStudent.getDateOfBirth().toString());
                }
                case PHOTO_CHANGE -> {
                    newStudentLog.setOldValue(existingStudent.getPhotoPath());
                }

            }
            newStudentLog.setStudent(existingStudent);
            newStudentLog.setNewValue(studentLogRequest.getNewValue());
            newStudentLog.setActionType(studentLogRequest.getActionType());
            newStudentLog.setActionStatus(ActionStatus.CREATED);
            newStudentLog.setCreatedByUser(loggedInUser);
            existingStudent.setActionsPendingApproval(existingStudent.getActionsPendingApproval() + 1);
            studentLogRepository.save(newStudentLog);
        }
        studentRepository.save(existingStudent);


    }

    @Transactional
    public StudentLog updateStudentLog(UserInfo loggedInUser, StudentLog studentLog) throws Exception {
        var existingStudentLog = studentLogRepository.findById(studentLog.getId()).orElseThrow();
        if (existingStudentLog.getActionStatus() != ActionStatus.CREATED) {
            throw new DuplicateEntryException("Data already approved");
        }
        if (Objects.equals(existingStudentLog.getCreatedByUser().getId(), loggedInUser.getId()))
            throw new BadCredentialsException("Creator cannot be the updater");
        var student = existingStudentLog.getStudent();

        existingStudentLog.setActionStatus(studentLog.getActionStatus());
        existingStudentLog.setUpdateDate(Timestamp.from(Instant.now()));
        if (studentLog.getComment() != null && !studentLog.getComment().isEmpty())
            existingStudentLog.setComment(studentLog.getComment());

        if (student.getActionsPendingApproval() > 0) {
            student.setActionsPendingApproval(student.getActionsPendingApproval() - 1);
        }

        if (existingStudentLog.getActionStatus() == ActionStatus.APPROVED) {
            var newValue = existingStudentLog.getNewValue();
            switch (existingStudentLog.getActionType()) {
                case SCORE_CHANGE -> student.setScore(Integer.parseInt(newValue));
                case STATUS_CHANGE -> student.setStatus(Status.valueOf(newValue));
                case CLASS_CHANGE -> student.setStudentClass(StudentClass.valueOf(newValue));
                case FNAME_CHANGE -> student.setFirstName(newValue);
                case LNAME_CHANGE -> student.setLastName(newValue);
                case DOB_CHANGE -> student.setDateOfBirth(DateFormatter.parseDate(newValue));
                case PHOTO_CHANGE -> student.setPhotoPath(newValue);

            }
        }
        existingStudentLog.setApprovedByUser(loggedInUser);
        studentLogRepository.save(existingStudentLog);
        studentRepository.save(student);
        return existingStudentLog;

    }

    public void cresteStudentLogsFromNewStudent(UserInfo loggedInUser, Student studentDTO) {
        var logList = new ArrayList<StudentLogRequest>();
        var studentInDb = studentRepository.findById(studentDTO.getStudentId()).orElseThrow();
        if (studentDTO.getFirstName() != null && !studentDTO.getFirstName().equals(studentInDb.getFirstName())) {
            var logRequest = new StudentLogRequest();
            logRequest.setStudentId(studentInDb.getStudentId());
            logRequest.setActionType(ActionType.FNAME_CHANGE);
            logRequest.setNewValue(studentDTO.getFirstName());
            logList.add(logRequest);
        }
        if (studentDTO.getLastName() != null && !studentDTO.getLastName().equals(studentInDb.getLastName())) {
            var logRequest = new StudentLogRequest();
            logRequest.setStudentId(studentInDb.getStudentId());
            logRequest.setActionType(ActionType.LNAME_CHANGE);
            logRequest.setNewValue(studentDTO.getLastName());
            logList.add(logRequest);
        }
        if (studentDTO.getStudentClass() != null && !studentDTO.getStudentClass().equals(studentInDb.getStudentClass())) {
            var logRequest = new StudentLogRequest();
            logRequest.setStudentId(studentInDb.getStudentId());
            logRequest.setActionType(ActionType.CLASS_CHANGE);
            logRequest.setNewValue(studentDTO.getStudentClass().name());
            logList.add(logRequest);
        }
        if (studentDTO.getScore() != studentInDb.getScore()) {
            var logRequest = new StudentLogRequest();
            logRequest.setStudentId(studentInDb.getStudentId());
            logRequest.setActionType(ActionType.SCORE_CHANGE);
            logRequest.setNewValue(String.valueOf(studentDTO.getScore()));
            logList.add(logRequest);
        }
        if (studentDTO.getPhotoPath() != null && !studentDTO.getPhotoPath().equals(studentInDb.getPhotoPath())) {
            var logRequest = new StudentLogRequest();
            logRequest.setStudentId(studentInDb.getStudentId());
            logRequest.setActionType(ActionType.PHOTO_CHANGE);
            logRequest.setNewValue(studentDTO.getPhotoPath());
            logList.add(logRequest);
        }
        if (studentDTO.getStatus() != null && !studentDTO.getStatus().equals(studentInDb.getStatus())) {
            var logRequest = new StudentLogRequest();
            logRequest.setStudentId(studentInDb.getStudentId());
            logRequest.setActionType(ActionType.STATUS_CHANGE);
            logRequest.setNewValue(studentDTO.getStatus().name());
            logList.add(logRequest);
        }

        createNewStudentLogs(loggedInUser, studentInDb, logList);

    }
}
