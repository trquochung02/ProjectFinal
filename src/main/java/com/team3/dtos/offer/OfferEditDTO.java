package com.team3.dtos.offer;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
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
public class OfferEditDTO {
    private Long offerId;

    @NotNull(message = "Candidate is required")
    private Long candidateId;

    @NotNull(message = "Approver is required")
    private Long approverId;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Level is required")
    private String level;

    @NotBlank(message = "Contract Type is required")
    private String contractType;

    @NotNull(message = "Contract start date is required")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate contractPeriodFrom;

    @NotNull(message = "Contract end date is required")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate contractPeriodTo;

    @NotNull(message = "Salary is required")
    @Min(value = 0, message = "Salary must be non-negative")
    private Double basicSalary;

    @NotBlank(message = "Status is required")
    private String offerStatus;

    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate dueDate;

    private String notes;
}