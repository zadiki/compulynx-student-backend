package com.compulynx.studenttask.model.db;

import com.compulynx.studenttask.model.ActionStatus;
import com.compulynx.studenttask.model.ActionType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@Entity
public class StudentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long createdBy;
    private Long studentId;
    private ActionType actionType;
    private String oldValue;
    private String newValue;
    private ActionStatus status;
    private String comment;
    private Long approvedBy;
    @CreationTimestamp
    private Timestamp creationDate;
    private Timestamp updateDate;



}
