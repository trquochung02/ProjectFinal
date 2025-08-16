package com.team3.controllers.job;

import com.team3.dtos.job.JobDTO;
import com.team3.services.JobService;
import com.team3.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    @GetMapping
    public String getAllJobs(@RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        int size = 10;
        var pageable = PageRequest.of(page, size);

        var jobs = jobService.filterJobs(search, status, pageable);

        model.addAttribute("jobs", jobs);
        model.addAttribute("keyword", search);
        model.addAttribute("status", status);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobs.getTotalPages());
        model.addAttribute("totalJobs", jobs.getTotalElements());

        return "contents/jobs/job-list";
    }

    @PostMapping("/delete/{id}")
    public String deleteJob(@PathVariable("id") Long jobId, RedirectAttributes redirectAttributes) {
        try {
            jobService.deleteJobById(jobId);
            redirectAttributes.addFlashAttribute("message", "Job deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete the job");
        }
        return "redirect:/jobs";
    }

    @GetMapping("/create")
    public String createJob(Model model) {
        JobDTO jobDTO = new JobDTO();
        model.addAttribute("job", jobDTO);
        return "contents/jobs/job-create";
    }

    @PostMapping("/create")
    public String createJob(@ModelAttribute("job") @Valid JobDTO jobDTO,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        if (bindingResult.hasErrors()) {
            if (jobDTO.getRequiredSkills() != null) {
                List<String> requiredSkillList = List.of(jobDTO.getRequiredSkills().split(","));
                model.addAttribute("requiredSkillList", requiredSkillList);
            }

            if (jobDTO.getLevel() != null) {
                List<String> levelList = List.of(jobDTO.getLevel().split(","));
                model.addAttribute("levelList", levelList);
            }

            if (jobDTO.getBenefits() != null) {
                List<String> benefitList = List.of(jobDTO.getBenefits().split(","));
                model.addAttribute("benefitList", benefitList);
            }

            return "contents/jobs/job-create";
        }

        JobDTO savedJobDOT = jobService.save(jobDTO);

        if (savedJobDOT == null) {
            redirectAttributes.addFlashAttribute("error", "Failed to create the job");
        } else {
            redirectAttributes.addFlashAttribute("message", "Job created successfully");
        }

        return "redirect:/jobs";
    }

    @GetMapping("/detail/{id}")
    public String detailJob(@PathVariable("id") Long id, Model model) {
        JobDTO jobDTO = jobService.findById(id);
        model.addAttribute("job", jobDTO);
        return "contents/jobs/job-detail";
    }

    @GetMapping("/edit/{id}")
    public String editJob(@PathVariable("id") Long id, Model model) {
        JobDTO jobDTO = jobService.findById(id);

        if (jobDTO.getRequiredSkills() != null) {
            List<String> requiredSkillList = List.of(jobDTO.getRequiredSkills().split(","));
            model.addAttribute("requiredSkillList", requiredSkillList);
        }

        if (jobDTO.getLevel() != null) {
            List<String> levelList = List.of(jobDTO.getLevel().split(","));
            model.addAttribute("levelList", levelList);
        }

        if (jobDTO.getBenefits() != null) {
            List<String> benefitList = List.of(jobDTO.getBenefits().split(","));
            model.addAttribute("benefitList", benefitList);
        }

        model.addAttribute("job", jobDTO);
        return "contents/jobs/job-edit";
    }

    @PostMapping("/edit/{id}")
    public String editJob(@PathVariable("id") Long id,
                          @ModelAttribute("job") @Valid JobDTO jobDTO,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if (bindingResult.hasErrors()) {
            if (jobDTO.getRequiredSkills() != null) {
                List<String> requiredSkillList = List.of(jobDTO.getRequiredSkills().split(","));
                model.addAttribute("requiredSkillList", requiredSkillList);
            }

            if (jobDTO.getLevel() != null) {
                List<String> levelList = List.of(jobDTO.getLevel().split(","));
                model.addAttribute("levelList", levelList);
            }

            if (jobDTO.getBenefits() != null) {
                List<String> benefitList = List.of(jobDTO.getBenefits().split(","));
                model.addAttribute("benefitList", benefitList);
            }

            return "contents/jobs/job-edit";
        }

        JobDTO savedJobDOT = jobService.save(jobDTO);

        if (savedJobDOT == null) {
            redirectAttributes.addFlashAttribute("error", "Failed to update the job");
        } else {
            redirectAttributes.addFlashAttribute("message", "Job updated successfully");
        }

        return "redirect:/jobs";
    }
}
