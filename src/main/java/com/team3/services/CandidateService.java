package com.team3.services;

import java.util.List;

import com.team3.dtos.candidate.CandidateDTO;
import com.team3.entities.Candidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CandidateService {

     Page<CandidateDTO> filterCandidate(String search, String status, Pageable pageable);

     String deleteCandidateById(Long candidateId);

     CandidateDTO findById(Long id);

     CandidateDTO save(CandidateDTO candidateDTO);

     CandidateDTO updateCandidate(CandidateDTO candidateDTO);

     boolean existsByEmail(String email);

     String banCandidate(Long candidateId);

     Candidate getCandidateById(Long candidateId);

     List<CandidateDTO> getCandidateByStatus(String status);

     void updateCandidateStatus(Long candidateId, String status);

    List<CandidateDTO> findCandidatesForDashboard();

    List<CandidateDTO> findAllCandidates();
}
