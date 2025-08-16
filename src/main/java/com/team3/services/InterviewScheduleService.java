package com.team3.services;

import com.team3.dtos.interviewschedule.ScheduleCreateDTO;
import com.team3.dtos.interviewschedule.ScheduleDetailDTO;
import com.team3.dtos.interviewschedule.ScheduleEditDTO;
import com.team3.dtos.interviewschedule.ScheduleListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InterviewScheduleService {

    Page<ScheduleListDTO> searchAll(String search, Long interviewerId , String status, Pageable pageable);

    ScheduleCreateDTO save(ScheduleCreateDTO scheduleCreateDTO);

    ScheduleDetailDTO getScheduleDetail(Long id);

    ScheduleEditDTO getScheduleEdit(Long id);

    String submitResult(Long id, String result, String notes);

    ScheduleDetailDTO update(ScheduleEditDTO scheduleEditDTO);

    String cancelSchedule(Long id);

    void sendReminder(Long id, String url);

    void autoSendReminder();
}
