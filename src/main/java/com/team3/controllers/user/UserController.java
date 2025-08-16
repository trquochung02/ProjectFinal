package com.team3.controllers.user;

import com.team3.dtos.user.UserDTO;
import com.team3.services.UserService;
import com.team3.utils.CurrentUserUtil; // Thêm import
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired // Thêm Autowired cho CurrentUserUtil
    private CurrentUserUtil currentUserUtil;

    // --- CÁC PHƯƠNG THỨC QUẢN LÝ USER CHO ADMIN ---

    @GetMapping
    public String userList(@RequestParam(required = false) String search,
                           @RequestParam(required = false) String role,
                           @RequestParam(defaultValue = "0") int page,
                           Model model) {
        int size = 10;
        var pageable = PageRequest.of(page, size);
        var userDTOs = userService.filterUser(search, role, pageable);
        model.addAttribute("userDTOs", userDTOs);
        model.addAttribute("keyword", search);
        model.addAttribute("role", role);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userDTOs.getTotalPages());
        model.addAttribute("totalUsers", userDTOs.getTotalElements());
        return "contents/user/user-list";
    }

    @GetMapping("/add")
    public String addUser(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "contents/user/user-create";
    }

    @PostMapping("/add")
    public String createUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "contents/user/user-create";
        }
        try {
            userService.save(userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating user: " + e.getMessage());
        }
        return "redirect:/users";
    }

    // ... Các phương thức detail, edit, delete cho Admin giữ nguyên ...
    @GetMapping("/detail/{id}")
    public String userDetail(@PathVariable("id") Long id, Model model) {
        UserDTO userDTO = userService.findById(id);
        model.addAttribute("userDTO", userDTO);
        return "contents/user/user-detail";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable("id") Long id, Model model) {
        UserDTO userDTO = userService.findById(id);
        model.addAttribute("userDTO", userDTO);
        return "contents/user/user-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable("id") Long id, @Valid @ModelAttribute UserDTO userDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "contents/user/user-edit";
        }
        try {
            userService.update(userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/users";
    }

    // --- CÁC PHƯƠNG THỨC MỚI CHO "MY PROFILE" ---

    @GetMapping("/profile")
    public String showProfilePage(Model model) {
        UserDTO currentUser = currentUserUtil.getCurrentUserDto();
        if (currentUser == null) {
            return "redirect:/logout";
        }
        model.addAttribute("userProfile", currentUser);
        return "contents/user/user-profile";
    }

    @PostMapping("/profile")
    public String updateUserProfile(@Valid @ModelAttribute("userProfile") UserDTO userDTO,
                                    BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            return "contents/user/user-profile"; // Trả về lại form nếu có lỗi
        }
        try {
            userService.update(userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
        }
        return "redirect:/users/profile";
    }
}