package com.team3.services;

import com.team3.dtos.user.UserDTO;
import com.team3.dtos.user.UserRegistrationDto;
import com.team3.entities.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    Page<UserDTO> findAll(String search, Pageable pageable);

    UserDTO findById(Long id);

    String save(UserDTO userDTO);

    String update(UserDTO userDTO);

    void deleteById(Long id);

    Page<UserDTO> filterUser(String search, String role, Pageable pageable);

    String updateStatus(Long id);

    UserDTO findByUsername(String username);

    boolean existsByEmail(String username);

    List<UserDTO> getRecruiters();

    List<UserDTO> getInterviewers();

    UserDTO findByEmail(String email);

    void createPasswordResetTokenForUser(String email, String resetUrl, String token);

    String updatePassword(Long id, String password);

    User saveNewRegistration(UserRegistrationDto registrationDto);

    List<UserDTO> getManagers();

}
