package com.team3.services;

import com.team3.dtos.email.EmailDTO;

public interface EmailService {

    String sendEmail(EmailDTO emailDTO, String fileName, String templateName);
}
