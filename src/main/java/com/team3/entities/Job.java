package com.team3.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long jobId;

    @Column(name = "job_title", nullable = false, length = 150, columnDefinition = "NVARCHAR(150)")
    private String jobTitle;

    @Column(name = "required_skills", nullable = false, length = 255)
    private String requiredSkills;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "salary_range_from")
    private Double salaryRangeFrom;

    @Column(name = "salary_range_to")
    private Double salaryRangeTo;

    @Column(name = "working_address", length = 255, columnDefinition = "NVARCHAR(255)")
    private String workingAddress;

    @Column(name = "benefits", nullable = false, length = 255)
    private String benefits;

    @Column(name = "level", nullable = false, length = 255)
    private String level;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "description", length = 1000, columnDefinition = "NVARCHAR(1000)")
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
