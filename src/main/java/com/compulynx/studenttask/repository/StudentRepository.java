package com.compulynx.studenttask.repository;

import com.compulynx.studenttask.model.db.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student,Long>, JpaSpecificationExecutor<Student> {
}
