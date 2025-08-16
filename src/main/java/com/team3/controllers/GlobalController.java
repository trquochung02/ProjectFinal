package com.team3.controllers;

import com.team3.dtos.user.UserDTO;
import com.team3.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class GlobalController {

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addUserDetails(Principal principal, Model model) {
        if (principal != null) {

            String username = principal.getName();

            try {
                UserDTO userDTO = userService.findByUsername(username);

                model.addAttribute("currentUser", userDTO);
            } catch (Exception e) {

                model.addAttribute("error", "Unable to load user details.");
            }
        }
    }
}
