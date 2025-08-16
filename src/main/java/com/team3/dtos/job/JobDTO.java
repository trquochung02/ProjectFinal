package com.team3.dtos.job;

import com.team3.validates.EndDateAfterStartDate;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EndDateAfterStartDate(startDateField = "startDate", endDateField = "endDate")
public class JobDTO {
    private Long jobId;

    @NotBlank(message = "Job title is required!")
    private String jobTitle;

    @NotBlank(message = "Job description is required!")
    private String requiredSkills;

    @NotNull(message = "Start date is required!")
    @Future(message = "Start date must be in the future!")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "End date is required!")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate endDate;

    private Double salaryRangeFrom;

    private Double salaryRangeTo;

    private String workingAddress;

    @NotBlank(message = "Benefits is required!")
    private String benefits;

    @NotBlank(message = "Level is required!")
    private String level;

    private String status = "Draft";

    private String description;

    private LocalDate createdAt = LocalDate.now();

    private LocalDate updatedAt = LocalDate.now();
}
