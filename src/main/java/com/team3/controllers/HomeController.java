package com.team3.controllers;

import com.team3.dtos.candidate.CandidateDTO;
import com.team3.services.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private CandidateService candidateService;

    @GetMapping
    public String index(Model model) {
        // Lấy danh sách ứng viên cho dashboard
        List<CandidateDTO> candidates = candidateService.findCandidatesForDashboard();
        model.addAttribute("dashboardCandidates", candidates);

        return "contents/index";
    }
}