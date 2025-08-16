package com.team3.repositories;

import com.team3.entities.Job;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

    List<Job> getJobByStatus(String status);

    List<Job> findAllByStatusAndStartDate(String status, LocalDate startDate);
}
