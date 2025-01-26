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
    @ManyToOne()
    private Student student;
    private ActionType actionType;
    private String oldValue;
    private String newValue;
    private ActionStatus actionStatus;
    private String comment="";
    @ManyToOne
    private UserInfo approvedByUser;
    @ManyToOne
    private UserInfo createdByUser;
    @CreationTimestamp
    private Timestamp creationDate;
    private Timestamp updateDate;



}
