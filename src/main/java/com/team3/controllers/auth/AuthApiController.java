package com.team3.controllers.auth;

import com.team3.dtos.user.UserDTO;
import com.team3.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    @Autowired
    private UserService userService;

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<String> forgotPassword(HttpServletRequest request, @RequestParam("email") String email) {

        UserDTO userDTO = userService.findByEmail(email);

        if (userDTO == null) {
            return ResponseEntity.badRequest().body("The Email address doesn't exist. Please try again.");
        }

        String token = UUID.randomUUID().toString();

        String resetUrl = getSiteURL(request) + "/auth/password/reset?token=" + token;

        userService.createPasswordResetTokenForUser(email, resetUrl, token);

        return ResponseEntity.ok("We've sent an email with the link to reset your password.");
    }
}
