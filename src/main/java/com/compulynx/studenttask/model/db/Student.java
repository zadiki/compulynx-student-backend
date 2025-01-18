package com.compulynx.studenttask.model.db;

import com.compulynx.studenttask.model.Status;
import com.compulynx.studenttask.model.StudentClass;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "students")
@Data
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    private String firstName;
    private  String lastName;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="UTC -4")
    private Date dateOfBirth;
    @Enumerated(EnumType.ORDINAL)
    private StudentClass studentClass;
    @Max(85)
    @Min(55)
    private int score;
    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int default 1")
    private Status status;
    private String photoPath;

}
