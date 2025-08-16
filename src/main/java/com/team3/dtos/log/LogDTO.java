package com.team3.dtos.log;

import java.time.LocalDateTime;

import org.hibernate.annotations.TimeZoneColumn;

import com.team3.dtos.user.UserDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogDTO {
    
    private Long logId;

    private String action;
    
    private UserDTO user;
    
    private String entityType;
    
    private Integer entityId;
   
    private String description;

    @TimeZoneColumn
    private LocalDateTime timestamp = LocalDateTime.now();
}
