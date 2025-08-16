package com.team3.controllers.interviewschedule;

import com.team3.services.InterviewScheduleService;
import com.team3.utils.GetSiteUrl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interview-schedules")
public class InterviewScheduleApiController {

    @Autowired
    private InterviewScheduleService interviewScheduleService;

    @PostMapping("/send-reminder")
    public ResponseEntity<String> sendReminder(@RequestParam("id") Long id, HttpServletRequest request) {
        String interviewUrl = GetSiteUrl.getSiteUrl(request) + "/interview-schedules/detail/" + id;

        interviewScheduleService.sendReminder(id, interviewUrl);

        return ResponseEntity.ok("Email reminder has been sent to the interviewers.");
    }
}
