package com.compulynx.studenttask.repository;

import com.compulynx.studenttask.model.db.StudentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentLogRepository extends JpaRepository<StudentLog,Long> {
}
