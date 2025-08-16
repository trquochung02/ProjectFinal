package com.team3.services;

import com.team3.dtos.job.JobDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobService {

    Page<JobDTO> filterJobs(String search, String status, Pageable pageable);

    JobDTO save(JobDTO jobDTO);

    JobDTO findById(Long id);

    void deleteJobById(Long jobId);

    List<JobDTO> getJobByStatus(String status);

    void updateJobStatus(Long jobId, String status);

    void autoUpdateJobStatus();
}
