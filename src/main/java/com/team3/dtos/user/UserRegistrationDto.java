package com.team3.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDto {

    @NotBlank(message = "Full name is required!")
    private String fullName;

    @NotBlank(message = "Email is required!")
    @Email(message = "Please enter a valid email address!")
    private String email;

    @NotBlank(message = "Password is required!")
    @Size(min = 7, message = "Password must be at least 7 characters long")
    private String password;
}