package com.team3.utils;

import com.team3.dtos.user.UserDTO;
import com.team3.entities.User;
import com.team3.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserUtil {

    @Autowired
    private UserRepository userRepository;

    /**
     * Lấy đối tượng User (Entity) của người dùng đang đăng nhập.
     * @return User entity hoặc null.
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            String username;
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }
            return userRepository.findByUsername(username);
        }
        return null;
    }

    /**
     * Lấy thông tin người dùng đang đăng nhập dưới dạng UserDTO.
     * @return UserDTO của người dùng hiện tại, hoặc null nếu không có.
     */
    public UserDTO getCurrentUserDto() {
        User userEntity = this.getCurrentUser();

        if (userEntity != null) {
            // Chuyển đổi từ User entity sang UserDTO
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(userEntity.getUserId());
            userDTO.setUsername(userEntity.getUsername());
            userDTO.setFullName(userEntity.getFullName());
            userDTO.setEmail(userEntity.getEmail());
            userDTO.setGender(userEntity.getGender());
            userDTO.setDepartment(userEntity.getDepartment());
            userDTO.setRole(userEntity.getRole());
            userDTO.setDateOfBirth(userEntity.getDateOfBirth());
            userDTO.setAddress(userEntity.getAddress());
            userDTO.setPhoneNumber(userEntity.getPhoneNumber());
            userDTO.setStatus(userEntity.getStatus());
            userDTO.setNotes(userEntity.getNotes());
            return userDTO;
        }
        return null;
    }
}