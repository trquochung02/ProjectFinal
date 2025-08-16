package com.team3.dtos.offer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfferDetailDTO {
    private Long offerId;
    private String candidateName;
    private String approverName;
    private String recruiterOwner;
    private String department;
    private String level;
    private String contractType;
    private LocalDate contractPeriodFrom;
    private LocalDate contractPeriodTo;
    private Double basicSalary;
    private String offerStatus;
    private LocalDate dueDate;
    private String notes;
}