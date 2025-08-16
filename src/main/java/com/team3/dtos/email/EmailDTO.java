package com.team3.dtos.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailDTO {
    String subject;
    String from;
    String body;
    String to;
    Object data;
}
