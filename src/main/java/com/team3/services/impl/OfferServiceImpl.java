package com.team3.services.impl;

import com.team3.dtos.offer.OfferCreateDTO;
import com.team3.dtos.offer.OfferDetailDTO;
import com.team3.dtos.offer.OfferEditDTO;
import com.team3.dtos.offer.OfferListDTO;
import com.team3.entities.Candidate;
import com.team3.entities.Offer;
import com.team3.entities.User;
import com.team3.repositories.CandidateRepository;
import com.team3.repositories.OfferRepository;
import com.team3.repositories.UserRepository;
import com.team3.services.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OfferServiceImpl implements OfferService {
    @Autowired
    private OfferRepository offerRepository;
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<OfferListDTO> searchAll(String search, String department, String status, Pageable pageable) {
        var result = offerRepository.searchAll(search, department, status, pageable);
        return result.map(offer -> {
            var offerListDTO = new OfferListDTO();
            offerListDTO.setOfferId(offer.getOfferId());
            offerListDTO.setCandidateName(offer.getCandidate().getFullName());
            offerListDTO.setCandidateEmail(offer.getCandidate().getEmail());
            offerListDTO.setApproverName(offer.getApprover().getFullName());
            offerListDTO.setDepartment(offer.getDepartment());
            offerListDTO.setNotes(offer.getNotes());
            offerListDTO.setStatus(offer.getOfferStatus());
            return offerListDTO;
        });
    }

    @Override
    public OfferDetailDTO findOfferById(Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found with id: " + id));
        return new OfferDetailDTO(
                offer.getOfferId(),
                offer.getCandidate().getFullName(),
                offer.getApprover().getFullName(),
                offer.getRecruiterOwner(),
                offer.getDepartment(),
                offer.getLevel(),
                offer.getContractType(),
                offer.getContractPeriodFrom(),
                offer.getContractPeriodTo(),
                offer.getBasicSalary(),
                offer.getOfferStatus(),
                offer.getDueDate(),
                offer.getNotes()
        );
    }

    @Override
    public OfferEditDTO findOfferForEdit(Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found with id: " + id));
        return new OfferEditDTO(
                offer.getOfferId(),
                offer.getCandidate().getCandidateId(),
                offer.getApprover().getUserId(),
                offer.getDepartment(),
                offer.getLevel(),
                offer.getContractType(),
                offer.getContractPeriodFrom(),
                offer.getContractPeriodTo(),
                offer.getBasicSalary(),
                offer.getOfferStatus(),
                offer.getDueDate(),
                offer.getNotes()
        );
    }

    @Override
    public void createOffer(OfferCreateDTO offerDto) {
        Candidate candidate = candidateRepository.findById(offerDto.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
        User approver = userRepository.findById(offerDto.getApproverId())
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        Offer offer = new Offer();
        offer.setCandidate(candidate);
        offer.setApprover(approver);
        offer.setDepartment(offerDto.getDepartment());
        offer.setLevel(offerDto.getLevel());
        offer.setContractType(offerDto.getContractType());
        // Các trường khác tương tự...
        offerRepository.save(offer);
    }

    @Override
    public void updateOffer(OfferEditDTO offerDto) {
        Offer offer = offerRepository.findById(offerDto.getOfferId())
                .orElseThrow(() -> new RuntimeException("Offer not found"));
        Candidate candidate = candidateRepository.findById(offerDto.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
        User approver = userRepository.findById(offerDto.getApproverId())
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        offer.setCandidate(candidate);
        offer.setApprover(approver);
        offer.setDepartment(offerDto.getDepartment());
        offer.setLevel(offerDto.getLevel());
        offer.setContractType(offerDto.getContractType());
        offer.setContractPeriodFrom(offerDto.getContractPeriodFrom());
        offer.setContractPeriodTo(offerDto.getContractPeriodTo());
        offer.setBasicSalary(offerDto.getBasicSalary());
        offer.setOfferStatus(offerDto.getOfferStatus());
        offer.setDueDate(offerDto.getDueDate());
        offer.setNotes(offerDto.getNotes());

        offerRepository.save(offer);
    }

    @Override
    public void deleteOfferById(Long id) {
        if (!offerRepository.existsById(id)) {
            throw new RuntimeException("Offer not found with id: " + id);
        }
        offerRepository.deleteById(id);
    }
}