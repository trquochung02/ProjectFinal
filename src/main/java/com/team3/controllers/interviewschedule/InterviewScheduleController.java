package com.team3.controllers.interviewschedule;

import com.team3.dtos.candidate.CandidateDTO;
import com.team3.dtos.interviewschedule.ScheduleCreateDTO;
import com.team3.dtos.interviewschedule.ScheduleDetailDTO;
import com.team3.dtos.interviewschedule.ScheduleEditDTO;
import com.team3.dtos.user.UserDTO;
import com.team3.services.CandidateService;
import com.team3.services.InterviewScheduleService;
import com.team3.services.JobService;
import com.team3.services.UserService;
import com.team3.utils.CurrentUserUtil;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/interview-schedules")
public class InterviewScheduleController {

    @Autowired
    private InterviewScheduleService interviewScheduleService;

    @Autowired
    private UserService userService;

    @Autowired
    private JobService jobService;

    @Autowired // <-- LỖI ĐÃ ĐƯỢC SỬA Ở ĐÂY
    private CurrentUserUtil currentUserUtil;

    @Autowired
    private CandidateService candidateService;

    @GetMapping("")
    public String interviewScheduleList(@RequestParam(required = false) String search,
                                        @RequestParam(required = false) Long interviewerId,
                                        @RequestParam(required = false) String status,
                                        @RequestParam(defaultValue = "0") int page,
                                        Model model) {

        UserDTO currentUser = currentUserUtil.getCurrentUserDto();
        boolean isInterviewerRole = false;

        // --- LOGIC MỚI ĐỂ LỌC THEO VAI TRÒ ---
        if (currentUser != null && "Interviewer".equals(currentUser.getRole())) {
            isInterviewerRole = true;
            // Nếu người dùng là Interviewer, chỉ cho phép họ xem lịch của chính mình.
            // Ghi đè bất kỳ tham số interviewerId nào được gửi từ client.
            interviewerId = currentUser.getUserId();
        }
        // ------------------------------------

        int size = 10;
        var pageable = PageRequest.of(page, size);
        var interviewSchedulesDTO = interviewScheduleService.searchAll(search, interviewerId, status, pageable);

        model.addAttribute("interviewSchedules", interviewSchedulesDTO);
        model.addAttribute("keyword", search);
        model.addAttribute("interviewerId", interviewerId);
        model.addAttribute("status", status);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", interviewSchedulesDTO.getTotalPages());
        model.addAttribute("totalSchedules", interviewSchedulesDTO.getTotalElements());
        model.addAttribute("isInterviewerRole", isInterviewerRole); // <-- Gửi cờ này ra view

        // Chỉ lấy danh sách người phỏng vấn nếu không phải là vai trò Interviewer
        if (!isInterviewerRole) {
            var interviewers = userService.getInterviewers();
            model.addAttribute("interviewers", interviewers);
        }

        return "contents/interviewSchedule/schedule-list";
    }

    @GetMapping("/create")
    public String interviewScheduleCreate(Model model) {

        var scheduleCreateDTO = new ScheduleCreateDTO();
        model.addAttribute("scheduleCreateDTO", scheduleCreateDTO);

        var interviewers = userService.getInterviewers();
        model.addAttribute("interviewers", Optional.ofNullable(interviewers).orElse(new ArrayList<>()));

        var recruiters = userService.getRecruiters();
        model.addAttribute("recruiters", Optional.ofNullable(recruiters).orElse(new ArrayList<>()));

        var openJobs = jobService.getJobByStatus("Open");
        model.addAttribute("jobs", Optional.ofNullable(openJobs).orElse(new ArrayList<>()));

        var openCandidates = candidateService.getCandidateByStatus("Open");
        model.addAttribute("candidates", Optional.ofNullable(openCandidates).orElse(new ArrayList<>()));

        return "contents/interviewSchedule/schedule-create";
    }

    @PostMapping("/create")
    public String interviewScheduleCreate(@ModelAttribute("scheduleCreateDTO") @Valid ScheduleCreateDTO scheduleCreateDTO,
                                          BindingResult bindingResult,
                                          RedirectAttributes redirectAttributes,
                                          Model model) {
        if (bindingResult.hasErrors()) {
            if (scheduleCreateDTO.getInterviewersIdList() != null) {
                List<String> interviewerChosen = List.of(scheduleCreateDTO.getInterviewersIdList().split(","));
                model.addAttribute("interviewerChosen", interviewerChosen);
            }

            var interviewers = userService.getInterviewers();
            model.addAttribute("interviewers", Optional.ofNullable(interviewers).orElse(new ArrayList<>()));

            var recruiters = userService.getRecruiters();
            model.addAttribute("recruiters", Optional.ofNullable(recruiters).orElse(new ArrayList<>()));

            var openJobs = jobService.getJobByStatus("Open");
            model.addAttribute("jobs", Optional.ofNullable(openJobs).orElse(new ArrayList<>()));

            var openCandidates = candidateService.getCandidateByStatus("Open");
            model.addAttribute("candidates", Optional.ofNullable(openCandidates).orElse(new ArrayList<>()));

            return "contents/interviewSchedule/schedule-create";
        }

        ScheduleCreateDTO saved = interviewScheduleService.save(scheduleCreateDTO);

        if (saved == null) {
            redirectAttributes.addFlashAttribute("error", "Failed to create the schedule");
        } else {
            candidateService.updateCandidateStatus(saved.getCandidateId(), "Waiting for interview");

            redirectAttributes.addFlashAttribute("message", "Schedule created successfully");
        }

        return "redirect:/interview-schedules";
    }

    @GetMapping("/detail/{id}")
    public String detailInterviewSchedule(@PathVariable("id") Long id, Model model) {
        var interviewScheduleDTO = interviewScheduleService.getScheduleDetail(id);
        model.addAttribute("interviewSchedule", interviewScheduleDTO);

        return "contents/interviewSchedule/schedule-detail";
    }

    @GetMapping("/edit/{id}")
    public String editInterviewSchedule(@PathVariable("id") Long id, Model model) {
        var interviewScheduleDTO = interviewScheduleService.getScheduleEdit(id);

        model.addAttribute("interviewSchedule", interviewScheduleDTO);

        if (interviewScheduleDTO.getInterviewersIdList() != null) {
            List<String> interviewerChosen = List.of(interviewScheduleDTO.getInterviewersIdList().split(","));
            model.addAttribute("interviewerChosen", interviewerChosen);
        }

        var interviewers = userService.getInterviewers();
        model.addAttribute("interviewers", Optional.ofNullable(interviewers).orElse(new ArrayList<>()));

        var recruiters = userService.getRecruiters();
        model.addAttribute("recruiters", Optional.ofNullable(recruiters).orElse(new ArrayList<>()));

        var openJobs = jobService.getJobByStatus("Open");
        model.addAttribute("jobs", Optional.ofNullable(openJobs).orElse(new ArrayList<>()));

        List<CandidateDTO> openCandidates = candidateService.getCandidateByStatus("Open");
        CandidateDTO chosenCandidateDTO = candidateService.findById(interviewScheduleDTO.getCandidateId());
        openCandidates.add(chosenCandidateDTO);
        model.addAttribute("candidates", openCandidates);

        return "contents/interviewSchedule/schedule-edit";
    }

    @PostMapping("/edit")
    public String editInterviewSchedule(@ModelAttribute("interviewSchedule") @Valid ScheduleEditDTO scheduleEditDTO,
                                        BindingResult bindingResult,
                                        RedirectAttributes redirectAttributes,
                                        Model model) {
        if (bindingResult.hasErrors()) {
            if (scheduleEditDTO.getInterviewersIdList() != null) {
                List<String> interviewerChosen = List.of(scheduleEditDTO.getInterviewersIdList().split(","));
                model.addAttribute("interviewerChosen", interviewerChosen);
            }

            var interviewers = userService.getInterviewers();
            model.addAttribute("interviewers", Optional.ofNullable(interviewers).orElse(new ArrayList<>()));

            var recruiters = userService.getRecruiters();
            model.addAttribute("recruiters", Optional.ofNullable(recruiters).orElse(new ArrayList<>()));

            var openJobs = jobService.getJobByStatus("Open");
            model.addAttribute("jobs", Optional.ofNullable(openJobs).orElse(new ArrayList<>()));

            List<CandidateDTO> openCandidates = candidateService.getCandidateByStatus("Open");
            if (scheduleEditDTO.getCandidateId() != null) {
                CandidateDTO chosenCandidateDTO = candidateService.findById(scheduleEditDTO.getCandidateId());
                openCandidates.add(chosenCandidateDTO);
            }
            model.addAttribute("candidates", openCandidates);

            return "contents/interviewSchedule/schedule-edit";
        }

        ScheduleDetailDTO saved = interviewScheduleService.update(scheduleEditDTO);
        if (saved == null) {
            redirectAttributes.addFlashAttribute("error", "Failed to update the schedule");
        } else {
            redirectAttributes.addFlashAttribute("message", "Schedule updated successfully");
        }

        return "redirect:/interview-schedules/detail/" + scheduleEditDTO.getScheduleId();

    }

    @GetMapping("/submit-result/{id}")
    public String submitResult(@PathVariable("id") Long id, Model model) {
        var interviewScheduleDTO = interviewScheduleService.getScheduleDetail(id);
        model.addAttribute("interviewSchedule", interviewScheduleDTO);

        return "contents/interviewSchedule/schedule-submit-result";
    }

    @PostMapping("/submit-result/{id}")
    public String submitResult(@PathVariable("id") Long id,
                               @RequestParam("result") String result,
                               @RequestParam("notes") String notes,
                               RedirectAttributes redirectAttributes) {
        String message = interviewScheduleService.submitResult(id, result, notes);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/interview-schedules/detail/" + id;
    }

    @PostMapping("/cancel/{id}")
    public String cancelSchedule(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        String message = interviewScheduleService.cancelSchedule(id);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/interview-schedules/detail/" + id;
    }
}
