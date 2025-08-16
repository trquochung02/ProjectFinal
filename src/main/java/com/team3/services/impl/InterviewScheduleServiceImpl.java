package com.team3.services.impl;

import com.team3.dtos.email.EmailDTO;
import com.team3.dtos.interviewschedule.ScheduleCreateDTO;
import com.team3.dtos.interviewschedule.ScheduleDetailDTO;
import com.team3.dtos.interviewschedule.ScheduleEditDTO;
import com.team3.dtos.interviewschedule.ScheduleListDTO;
import com.team3.entities.Candidate;
import com.team3.entities.InterviewSchedule;
import com.team3.entities.Job;
import com.team3.entities.User;
import com.team3.repositories.CandidateRepository;
import com.team3.repositories.InterviewScheduleRepository;
import com.team3.repositories.JobRepository;
import com.team3.repositories.UserRepository;
import com.team3.services.CandidateService;
import com.team3.services.EmailService;
import com.team3.services.InterviewScheduleService;

import com.team3.services.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class InterviewScheduleServiceImpl implements InterviewScheduleService {

    @Autowired
    private InterviewScheduleRepository interviewScheduleRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobService jobService;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public Page<ScheduleListDTO> searchAll(String search, Long interviewerId, String status, Pageable pageable) {
        var interviewScheduleList = interviewScheduleRepository.searchAll(search, interviewerId, status, pageable);

        return interviewScheduleList.map(interviewSchedule -> {
            return new ScheduleListDTO(
                    interviewSchedule.getScheduleId(),
                    interviewSchedule.getInterviewTitle(),
                    interviewSchedule.getCandidate().getFullName(),
                    interviewSchedule.getScheduleDate(),
                    interviewSchedule.getScheduleFrom(),
                    interviewSchedule.getScheduleTo(),
                    interviewSchedule.getInterviewers().stream().map(User::getFullName).toList().toString().replace("[","").replace("]",""),
                    interviewSchedule.getJob().getJobTitle(),
                    interviewSchedule.getStatus(),
                    interviewSchedule.getResult()
            );
        });
    }

    @Override
    public ScheduleCreateDTO save(ScheduleCreateDTO scheduleCreateDTO) {
        Job job = jobRepository.findById(scheduleCreateDTO.getJobId()).orElseThrow(() -> new RuntimeException("Job not found"));
        Candidate candidate = candidateRepository.findById(scheduleCreateDTO.getCandidateId()).orElseThrow(() -> new RuntimeException("Candidate not found"));
        User recruiter = userRepository.findById(scheduleCreateDTO.getRecruiterOwner()).orElseThrow(() -> new RuntimeException("Recruiter owner not found"));

        List<User> interviewers = new ArrayList<>();
        for (String interviewerId : scheduleCreateDTO.getInterviewersIdList().split(",")) {
            Long id = Long.parseLong(interviewerId);
            User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Interviewer not found"));
            interviewers.add(user);
        }

        InterviewSchedule interviewSchedule = new InterviewSchedule();
        interviewSchedule.setInterviewTitle(scheduleCreateDTO.getScheduleTitle());
        interviewSchedule.setJob(job);
        interviewSchedule.setInterviewers(interviewers);
        interviewSchedule.setCandidate(candidate);
        interviewSchedule.setRecruiterOwner(recruiter.getUserId());
        interviewSchedule.setScheduleDate(scheduleCreateDTO.getScheduleDate());
        interviewSchedule.setScheduleFrom(scheduleCreateDTO.getScheduleFrom());
        interviewSchedule.setScheduleTo(scheduleCreateDTO.getScheduleTo());
        interviewSchedule.setStatus("New");
        interviewSchedule.setLocation(scheduleCreateDTO.getLocation());
        interviewSchedule.setNotes(scheduleCreateDTO.getNotes());
        interviewSchedule.setMeetingId(scheduleCreateDTO.getMeetingId());
        interviewSchedule.setResult("");

        InterviewSchedule saved = interviewScheduleRepository.save(interviewSchedule);

        return new ScheduleCreateDTO(
                saved.getScheduleId(),
                saved.getInterviewTitle(),
                saved.getCandidate().getCandidateId(),
                saved.getJob().getJobId(),
                saved.getInterviewers().stream().map(User::getUserId).toList().toString().replace("[","").replace("]",""),
                saved.getScheduleDate(),
                saved.getScheduleFrom(),
                saved.getScheduleTo(),
                saved.getLocation(),
                saved.getRecruiterOwner(),
                saved.getMeetingId(),
                saved.getNotes()
        );
    }

    @Override
    public ScheduleDetailDTO getScheduleDetail(Long id) {
        InterviewSchedule interviewSchedule = interviewScheduleRepository.findById(id).orElse(null);
        if (interviewSchedule == null) {
            throw new RuntimeException("Interview schedule not found");
        }
        return new ScheduleDetailDTO(
                interviewSchedule.getScheduleId(),
                interviewSchedule.getInterviewTitle(),
                interviewSchedule.getCandidate().getFullName(),
                interviewSchedule.getJob().getJobTitle(),
                interviewSchedule.getInterviewers().stream().map(User::getUsername).toList().toString().replace("[","").replace("]",""),
                interviewSchedule.getScheduleDate(),
                interviewSchedule.getScheduleFrom(),
                interviewSchedule.getScheduleTo(),
                interviewSchedule.getLocation(),
                userRepository.findById(interviewSchedule.getRecruiterOwner()).orElseThrow(() -> new RuntimeException("Recruiter owner not found")).getUsername(),
                interviewSchedule.getMeetingId(),
                interviewSchedule.getNotes(),
                interviewSchedule.getResult(),
                interviewSchedule.getStatus(),
                interviewSchedule.getCreatedAt().toLocalDate(),
                interviewSchedule.getUpdatedAt().toLocalDate()
        );
    }

    @Override
    public String submitResult(Long id, String result, String notes) {
        InterviewSchedule interviewSchedule = interviewScheduleRepository.findById(id).orElse(null);
        if (interviewSchedule == null) {
            throw new RuntimeException("Interview schedule not found");
        }
        interviewSchedule.setResult(result);
        interviewSchedule.setNotes(notes);
        interviewSchedule.setStatus("Interviewed");

        interviewScheduleRepository.save(interviewSchedule);

        if (result.equals("Passed")) {
            candidateService.updateCandidateStatus(interviewSchedule.getCandidate().getCandidateId(), "Passed Interview");
            jobService.updateJobStatus(interviewSchedule.getJob().getJobId(), "Closed");
        }
        if (result.equals("Failed")) {
            candidateService.updateCandidateStatus(interviewSchedule.getCandidate().getCandidateId(), "Failed Interview");
        }

        return "Change has been successfully updated!";
    }

    @Override
    public ScheduleEditDTO getScheduleEdit(Long id) {
        InterviewSchedule interviewSchedule = interviewScheduleRepository.findById(id).orElse(null);
        if (interviewSchedule == null) {
            throw new RuntimeException("Interview schedule not found");
        }
        return new ScheduleEditDTO(
                interviewSchedule.getScheduleId(),
                interviewSchedule.getInterviewTitle(),
                interviewSchedule.getCandidate().getCandidateId(),
                interviewSchedule.getJob().getJobId(),
                interviewSchedule.getInterviewers().stream().map(User::getUserId).toList().toString().replace("[","").replace("]",""),
                interviewSchedule.getScheduleDate(),
                interviewSchedule.getScheduleFrom(),
                interviewSchedule.getScheduleTo(),
                interviewSchedule.getLocation(),
                interviewSchedule.getRecruiterOwner(),
                interviewSchedule.getMeetingId(),
                interviewSchedule.getNotes(),
                interviewSchedule.getResult(),
                interviewSchedule.getStatus()
        );
    }

    @Override
    public ScheduleDetailDTO update(ScheduleEditDTO scheduleEditDTO) {
        InterviewSchedule interviewSchedule = interviewScheduleRepository.findById(scheduleEditDTO.getScheduleId()).orElse(null);
        if (interviewSchedule == null) {
            throw new RuntimeException("Interview schedule not found");
        }

        Candidate oldCandidate = interviewSchedule.getCandidate();

        Job job = jobRepository.findById(scheduleEditDTO.getJobId()).orElseThrow(() -> new RuntimeException("Job not found"));
        Candidate candidate = candidateRepository.findById(scheduleEditDTO.getCandidateId()).orElseThrow(() -> new RuntimeException("Candidate not found"));
        User recruiter = userRepository.findById(scheduleEditDTO.getRecruiterOwner()).orElseThrow(() -> new RuntimeException("Recruiter owner not found"));

        List<User> interviewers = new ArrayList<>();
        for (String interviewerId : scheduleEditDTO.getInterviewersIdList().split(",")) {
            Long id = Long.parseLong(interviewerId);
            User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Interviewer not found"));
            interviewers.add(user);
        }

        interviewSchedule.setInterviewTitle(scheduleEditDTO.getScheduleTitle());
        interviewSchedule.setJob(job);
        interviewSchedule.setInterviewers(interviewers);
        interviewSchedule.setCandidate(candidate);
        interviewSchedule.setRecruiterOwner(scheduleEditDTO.getRecruiterOwner());
        interviewSchedule.setScheduleDate(scheduleEditDTO.getScheduleDate());
        interviewSchedule.setScheduleFrom(scheduleEditDTO.getScheduleFrom());
        interviewSchedule.setScheduleTo(scheduleEditDTO.getScheduleTo());
        interviewSchedule.setLocation(scheduleEditDTO.getLocation());
        interviewSchedule.setMeetingId(scheduleEditDTO.getMeetingId());
        interviewSchedule.setNotes(scheduleEditDTO.getNotes());
        interviewSchedule.setResult(scheduleEditDTO.getResult());

        InterviewSchedule updated = interviewScheduleRepository.save(interviewSchedule);

        if (!updated.getCandidate().getCandidateId().equals(scheduleEditDTO.getCandidateId()) && updated.getResult().isEmpty()) {
            candidateService.updateCandidateStatus(updated.getCandidate().getCandidateId(), "Waiting for interview");
            candidateService.updateCandidateStatus(oldCandidate.getCandidateId(), "Open");
        }

        return new ScheduleDetailDTO(
                interviewSchedule.getScheduleId(),
                interviewSchedule.getInterviewTitle(),
                interviewSchedule.getCandidate().getFullName(),
                interviewSchedule.getJob().getJobTitle(),
                interviewSchedule.getInterviewers().stream().map(User::getUsername).toList().toString().replace("[","").replace("]",""),
                interviewSchedule.getScheduleDate(),
                interviewSchedule.getScheduleFrom(),
                interviewSchedule.getScheduleTo(),
                interviewSchedule.getLocation(),
                userRepository.findById(interviewSchedule.getRecruiterOwner()).orElseThrow(() -> new RuntimeException("Recruiter owner not found")).getUsername(),
                interviewSchedule.getMeetingId(),
                interviewSchedule.getNotes(),
                interviewSchedule.getResult(),
                interviewSchedule.getStatus(),
                interviewSchedule.getCreatedAt().toLocalDate(),
                interviewSchedule.getUpdatedAt().toLocalDate()
        );
    }

    @Override
    public String cancelSchedule(Long id) {
        InterviewSchedule interviewSchedule = interviewScheduleRepository.findById(id).orElse(null);
        if (interviewSchedule == null) {
            throw new RuntimeException("Interview schedule not found");
        }
        interviewSchedule.setStatus("Cancelled");
        InterviewSchedule saved = interviewScheduleRepository.save(interviewSchedule);
        return "Change has been successfully updated!";
    }

    @Override
    public void sendReminder(Long id, String url) {
        InterviewSchedule interviewSchedule = interviewScheduleRepository.findById(id).orElse(null);
        if (interviewSchedule == null) {
            throw new RuntimeException("Interview schedule not found");
        }

        List<Long> interviewerIds = interviewSchedule.getInterviewers().stream().map(User::getUserId).toList();
        interviewerIds.forEach(interviewerId -> {
            User interviewer = userRepository.findById(interviewerId).orElseThrow(() -> new RuntimeException("Interviewer not found"));
            String email = interviewer.getEmail();
            EmailDTO emailDTO = EmailDTO.builder()
                    .subject("no-reply-email-IMS-system" + interviewSchedule.getInterviewTitle())
                    .from("interviewmanagementsystem.team3@gmail.com")
                    .to(email)
                    .data(Map.of("scheduleTitle", interviewSchedule.getInterviewTitle(),
                            "scheduleFrom", interviewSchedule.getScheduleFrom(),
                            "scheduleTo", interviewSchedule.getScheduleFrom(),
                            "candidateName", interviewSchedule.getCandidate().getFullName(),
                            "candidatePosition", interviewSchedule.getJob().getJobTitle(),
                            "interviewDetailUrl", url,
                            "meetingId", interviewSchedule.getMeetingId()))
                    .build();

            String result = emailService.sendEmail(emailDTO, interviewSchedule.getCandidate().getCvFileName(), "email-reminder-interviewer-template");
        });

        if (interviewSchedule.getStatus().equals("New")) {
            interviewSchedule.setStatus("Invited");
            interviewScheduleRepository.save(interviewSchedule);
        }
    }

    @Scheduled(cron = "0 0 8 * * ?", zone = "Asia/Ho_Chi_Minh")
    @Transactional
    @Override
    public void autoSendReminder() {
        var interviewSchedules = interviewScheduleRepository.findAllByStatusInAndScheduleDate(List.of("New", "Invited"), LocalDate.now());

        if (interviewSchedules.isEmpty()) {
            return;
        }

        interviewSchedules.forEach(interviewSchedule -> {
            sendReminder(interviewSchedule.getScheduleId(), "http://localhost:8080/interview-schedule/detail/" + interviewSchedule.getScheduleId());
        });
    }
}
