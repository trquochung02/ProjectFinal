package com.team3.controllers.candidate;

import com.team3.services.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/candidate")
public class CandidateApiController {
    @Autowired
    private CandidateService candidateService;

    @RequestMapping("/banCandidate")
    public ResponseEntity<String> banCandidate(@RequestParam("candidateId") Long candidateId) {
        String result = candidateService.banCandidate(candidateId);
        if (result.equals("Candidate not found")) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(result);
        }
    }
}
