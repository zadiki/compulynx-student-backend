package com.compulynx.studenttask.model;


import lombok.Data;

@Data
public class StudentLogRequest {
    private Long studentId;
    private ActionType actionType;
    private String newValue;
}
