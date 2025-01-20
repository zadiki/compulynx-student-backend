package com.compulynx.studenttask.model.db;

import com.compulynx.studenttask.model.Status;
import com.compulynx.studenttask.model.StudentClass;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;


import java.util.Date;

@Entity
@Table(name = "students")
@Data
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private  String lastName;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="UTC -4")
    @Column(nullable = false)
    private Date dateOfBirth;
    @Enumerated(EnumType.STRING)
    private StudentClass studentClass;
    @Max(95)
    @Min(55)
    @Column(nullable = false)
    private int score;
    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int default 1")
    private Status status;
    @Column(nullable = false)
    private String photoPath;
    @Max(1)
    @Min(0)
    @Column(columnDefinition = "int default 0")
    private int deleteStatus;

}
