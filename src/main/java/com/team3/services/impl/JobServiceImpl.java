package com.team3.services.impl;

import com.team3.dtos.job.JobDTO;
import com.team3.entities.Job;
import com.team3.repositories.JobRepository;
import com.team3.services.JobService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class JobServiceImpl implements JobService {
    @Autowired
    private JobRepository jobRepository;

    @Override
    public void deleteJobById(Long jobId) {
        jobRepository.deleteById(jobId);
    }

    @Override
    public Page<JobDTO> filterJobs(String search, String status, Pageable pageable) {
        Specification<Job> spec = (Specification<Job>) (root, query, criteriaBuilder) -> {
            if ((search == null || search.isEmpty()) && (status == null || status.isEmpty())) {
                return null;
            }

            if (search == null || search.isEmpty()) {
                return criteriaBuilder.equal(root.get("status"), status);
            }

            if (status == null || status.isEmpty()) {
                return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("jobTitle"), "%" + search + "%"),
                    criteriaBuilder.like(root.get("requiredSkills"), "%" + search + "%"),
                    criteriaBuilder.like(root.get("level"), "%" + search + "%"),
                    criteriaBuilder.like(root.get("status"), "%" + search + "%")
                );
            }

            return criteriaBuilder.and(
                criteriaBuilder.or(
                    criteriaBuilder.like(root.get("jobTitle"), "%" + search + "%"),
                    criteriaBuilder.like(root.get("requiredSkills"), "%" + search + "%"),
                    criteriaBuilder.like(root.get("level"), "%" + search + "%"),
                    criteriaBuilder.like(root.get("status"), "%" + search + "%")
                ),
                criteriaBuilder.equal(root.get("status"), status)
            );
        };

        var jobs = jobRepository.findAll(spec, pageable);

        return jobs.map(job -> {
            JobDTO jobDTO = new JobDTO();
            jobDTO.setJobId(job.getJobId());
            jobDTO.setJobTitle(job.getJobTitle());
            jobDTO.setRequiredSkills(job.getRequiredSkills());
            jobDTO.setStartDate(job.getStartDate());
            jobDTO.setEndDate(job.getEndDate());
            jobDTO.setSalaryRangeFrom(job.getSalaryRangeFrom());
            jobDTO.setSalaryRangeTo(job.getSalaryRangeTo());
            jobDTO.setWorkingAddress(job.getWorkingAddress());
            jobDTO.setBenefits(job.getBenefits());
            jobDTO.setLevel(job.getLevel());
            jobDTO.setStatus(job.getStatus());
            jobDTO.setDescription(job.getDescription());
            jobDTO.setCreatedAt(job.getCreatedAt().toLocalDate());
            jobDTO.setUpdatedAt(job.getUpdatedAt().toLocalDate());
            return jobDTO;
        });
    }

    @Override
    public JobDTO save(JobDTO jobDTO) {
        Job job = new Job();
        job.setJobTitle(jobDTO.getJobTitle());
        job.setRequiredSkills(jobDTO.getRequiredSkills());
        job.setStartDate(jobDTO.getStartDate());
        job.setEndDate(jobDTO.getEndDate());
        job.setSalaryRangeFrom(jobDTO.getSalaryRangeFrom());
        job.setSalaryRangeTo(jobDTO.getSalaryRangeTo());
        job.setWorkingAddress(jobDTO.getWorkingAddress());
        job.setBenefits(jobDTO.getBenefits());
        job.setLevel(jobDTO.getLevel());
        job.setStatus(jobDTO.getStatus());
        job.setDescription(jobDTO.getDescription());
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        Job savedJob = jobRepository.save(job);

        return new JobDTO(
            savedJob.getJobId(),
            savedJob.getJobTitle(),
            savedJob.getRequiredSkills(),
            savedJob.getStartDate(),
            savedJob.getEndDate(),
            savedJob.getSalaryRangeFrom(),
            savedJob.getSalaryRangeTo(),
            savedJob.getWorkingAddress(),
            savedJob.getBenefits(),
            savedJob.getLevel(),
            savedJob.getStatus(),
            savedJob.getDescription(),
            savedJob.getCreatedAt().toLocalDate(),
            savedJob.getUpdatedAt().toLocalDate()
        );
    }

    @Override
    public JobDTO findById(Long id) {
        Job job = jobRepository.findById(id).orElse(null);

        if (job == null) {
            throw new RuntimeException("Job not found");
        }

        return new JobDTO(
            job.getJobId(),
            job.getJobTitle(),
            job.getRequiredSkills(),
            job.getStartDate(),
            job.getEndDate(),
            job.getSalaryRangeFrom(),
            job.getSalaryRangeTo(),
            job.getWorkingAddress(),
            job.getBenefits(),
            job.getLevel(),
            job.getStatus(),
            job.getDescription(),
            job.getCreatedAt().toLocalDate(),
            job.getUpdatedAt().toLocalDate()
        );
    }

    @Override
    public List<JobDTO> getJobByStatus(String status) {
        List<Job> jobs = jobRepository.getJobByStatus(status);

        return jobs.stream().map(job -> {
            return new JobDTO(
                    job.getJobId(),
                    job.getJobTitle(),
                    job.getRequiredSkills(),
                    job.getStartDate(),
                    job.getEndDate(),
                    job.getSalaryRangeFrom(),
                    job.getSalaryRangeTo(),
                    job.getWorkingAddress(),
                    job.getBenefits(),
                    job.getLevel(),
                    job.getStatus(),
                    job.getDescription(),
                    job.getCreatedAt().toLocalDate(),
                    job.getUpdatedAt().toLocalDate()
            );
        }).toList();
    }

    @Override
    public void updateJobStatus(Long jobId, String status) {
        Job job = jobRepository.findById(jobId).orElse(null);
        if (job == null) {
            throw new RuntimeException("Job not found");
        }
        job.setStatus(status);
        jobRepository.save(job);
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Ho_Chi_Minh")
    @Override
    public void autoUpdateJobStatus() {
        List<Job> jobs = jobRepository.findAllByStatusAndStartDate("Draft", LocalDate.now().plusDays(1));

        for (Job job : jobs) {
            job.setStatus("Open");
            jobRepository.save(job);
        }

        System.out.println("change job status");
        System.out.println();
    }
}
