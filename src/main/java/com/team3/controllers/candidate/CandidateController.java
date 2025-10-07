package com.team3.controllers.candidate;

import com.team3.dtos.candidate.CandidateDTO;
import com.team3.dtos.user.UserDTO;
import com.team3.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Import thêm
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.team3.services.CandidateService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections; // Import thêm
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/candidates")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private UserService userService;

    // --- SỬA 1: Inject đường dẫn từ application.properties ---
    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("")
    public String candidateList(@RequestParam(required = false) String search,
                                @RequestParam(required = false) String status,
                                @RequestParam(defaultValue = "0") int page,
                                Model model) {
        int size = 10;
        var pageable = PageRequest.of(page, size);

        var candidateDTOs = candidateService.filterCandidate(search, status, pageable);

        model.addAttribute("candidates", candidateDTOs);
        model.addAttribute("keyword", search);
        model.addAttribute("status", status);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", candidateDTOs.getTotalPages());
        model.addAttribute("totalCandidates", candidateDTOs.getTotalElements());

        return "contents/candidate/candidate-list";
    }

    @PostMapping("/delete/{id}")
    public String deleteCandidate(@PathVariable("id") Long candidateId, RedirectAttributes redirectAttributes) {
        String result = candidateService.deleteCandidateById(candidateId);
        redirectAttributes.addFlashAttribute("message", result);
        return "redirect:/candidates";
    }

    @GetMapping("/detail/{id}")
    public String candidateDetail(@PathVariable("id") Long id, Model model) {
        var candidateDTO = candidateService.findById(id);
        UserDTO userDTO = userService.findByUsername(candidateDTO.getRecruiterOwner());
        String recruiterOwnerName = userDTO.getFullName() + " (" + userDTO.getUsername() + ")";
        model.addAttribute("recruiterOwnerName", recruiterOwnerName);
        String cvName = candidateDTO.getCvFileName().substring(candidateDTO.getCvFileName().indexOf("_") + 1);
        model.addAttribute("cvName", cvName);
        model.addAttribute("candidate", candidateDTO);
        return "contents/candidate/candidate-detail";
    }

    @GetMapping("/edit/{id}")
    public String editCandidate(@PathVariable("id") Long id, Model model) {
        var candidateDTO = candidateService.findById(id);
        String cvName = candidateDTO.getCvFileName().substring(candidateDTO.getCvFileName().indexOf("_") + 1);
        model.addAttribute("cvName", cvName);

        // --- SỬA 2: Xử lý trường hợp skills là null ---
        List<String> skillList = (candidateDTO.getSkills() != null && !candidateDTO.getSkills().isEmpty())
                ? List.of(candidateDTO.getSkills().split(","))
                : Collections.emptyList();
        model.addAttribute("skillList", skillList);

        List<UserDTO> recruiters = userService.getRecruiters();
        model.addAttribute("recruiters", recruiters);
        model.addAttribute("candidate", candidateDTO);
        return "contents/candidate/candidate-edit";
    }

    @PostMapping("/edit/{id}")
    public String editCandidate(@PathVariable("id") Long id,
                                @ModelAttribute("candidate") @Valid CandidateDTO candidateDTO,
                                BindingResult bindingResult,
                                @RequestParam("cv") MultipartFile cvFile,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        if (bindingResult.hasErrors()) {
            // --- SỬA 3: Đảm bảo dữ liệu luôn được load lại khi có lỗi validation ---
            List<String> skillList = (candidateDTO.getSkills() != null && !candidateDTO.getSkills().isEmpty())
                    ? List.of(candidateDTO.getSkills().split(","))
                    : Collections.emptyList();
            model.addAttribute("skillList", skillList);

            if (candidateDTO.getCvFileName() != null && !candidateDTO.getCvFileName().isEmpty()) {
                String cvName = candidateDTO.getCvFileName().substring(candidateDTO.getCvFileName().indexOf("_") + 1);
                model.addAttribute("cvName", cvName);
            }

            List<UserDTO> recruiters = userService.getRecruiters();
            model.addAttribute("recruiters", recruiters);
            return "contents/candidate/candidate-edit";
        }

        if (cvFile != null && !cvFile.isEmpty()) {
            try {
                // Sử dụng biến uploadDir đã inject
                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String fileName = UUID.randomUUID() + "_" + cvFile.getOriginalFilename();
                String filePath = uploadDir + fileName;
                File destFile = new File(filePath);
                cvFile.transferTo(destFile);
                candidateDTO.setCvFileName(fileName);

            } catch (IOException e) {
                e.printStackTrace();
                bindingResult.rejectValue("cvFileName", "error.candidate", "Error occurred while uploading the CV file.");

                // --- SỬA 4: Load lại đầy đủ dữ liệu khi có lỗi IO ---
                List<UserDTO> recruiters = userService.getRecruiters();
                model.addAttribute("recruiters", recruiters);
                if (candidateDTO.getCvFileName() != null && !candidateDTO.getCvFileName().isEmpty()) {
                    String cvName = candidateDTO.getCvFileName().substring(candidateDTO.getCvFileName().indexOf("_") + 1);
                    model.addAttribute("cvName", cvName);
                }
                return "contents/candidate/candidate-edit";
            }
        }

        CandidateDTO updateCandidateDTO = candidateService.updateCandidate(candidateDTO);
        if (updateCandidateDTO == null) {
            redirectAttributes.addFlashAttribute("message", "Update candidate failed");
            return "redirect:/candidates/edit/" + id;
        }

        redirectAttributes.addFlashAttribute("message", "Update candidate successfully");
        return "redirect:/candidates/detail/" + id;
    }

    @GetMapping("/create")
    public String createCandidate(Model model) {
        CandidateDTO candidateDTO = new CandidateDTO();
        List<UserDTO> recruiters = userService.getRecruiters();
        model.addAttribute("recruiters", recruiters);
        model.addAttribute("candidate", candidateDTO);
        return "contents/candidate/candidate-create";
    }

    @PostMapping("/create")
    public String createCandidate(@ModelAttribute("candidate") @Valid CandidateDTO candidateDTO,
                                  BindingResult bindingResult,
                                  @RequestParam("cv") MultipartFile cvFile,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {

        if (bindingResult.hasErrors()) {
            List<UserDTO> recruiters = userService.getRecruiters();
            model.addAttribute("recruiters", recruiters);
            return "contents/candidate/candidate-create";
        }

        if (cvFile == null || cvFile.isEmpty()) {
            bindingResult.rejectValue("cvFileName", "error.candidate", "CV file is required.");
            List<UserDTO> recruiters = userService.getRecruiters();
            model.addAttribute("recruiters", recruiters);
            return "contents/candidate/candidate-create";
        }

        try {
            // Sử dụng biến uploadDir đã inject
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = UUID.randomUUID() + "_" + cvFile.getOriginalFilename();
            String filePath = uploadDir + fileName;
            File destFile = new File(filePath);
            cvFile.transferTo(destFile);
            candidateDTO.setCvFileName(fileName);

        } catch (IOException e) {
            e.printStackTrace();
            bindingResult.rejectValue("cvFileName", "error.candidate", "Error occurred while uploading the CV file.");
            List<UserDTO> recruiters = userService.getRecruiters();
            model.addAttribute("recruiters", recruiters);
            return "contents/candidate/candidate-create";
        }

        CandidateDTO savedCandidateDTO = candidateService.save(candidateDTO);
        if (savedCandidateDTO == null) {
            redirectAttributes.addFlashAttribute("message", "Create candidate failed");
            return "redirect:/candidates/create";
        }

        redirectAttributes.addFlashAttribute("message", "Create candidate successfully");
        return "redirect:/candidates";
    }

    @GetMapping("/downloadCv")
    public ResponseEntity<Resource> downloadCv(@RequestParam("candidateId") Long candidateId) {
        CandidateDTO candidateDTO = candidateService.findById(candidateId);
        if (candidateDTO.getCvFileName() == null || candidateDTO.getCvFileName().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Sử dụng biến uploadDir đã inject
        Path filePath = Paths.get(uploadDir + candidateDTO.getCvFileName());
        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
