package com.team3.services.impl;

import com.team3.dtos.email.EmailDTO;
import com.team3.entities.Offer;
import com.team3.services.EmailService;
import com.team3.services.OfferService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public EmailServiceImpl(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }


    @Override
    public String sendEmail(EmailDTO emailDTO, String fileName, String templateName) {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            messageHelper.setTo(emailDTO.getTo());
            messageHelper.setSubject(emailDTO.getSubject());
            messageHelper.setFrom(emailDTO.getFrom());

            Context context = new Context();
            context.setVariables((Map<String, Object>) emailDTO.getData());

            String htmlContent = templateEngine.process(templateName, context);
            messageHelper.setText(htmlContent, true);

            if (fileName != null && !fileName.isEmpty()) {
                String uploadDir = "D:\\HN24_FRF_FJW_02\\MockProject\\Output\\";
                Path filePath = Paths.get(uploadDir, fileName);

                File file = new File(filePath.toString());

                if (file.exists() && file.isFile()) {
                    FileSystemResource fileResource = new FileSystemResource(file);
                    messageHelper.addAttachment(file.getName(), fileResource);
                } else {
                    System.out.println("File not found: " + filePath.toString());
                    return "Error: File not found: " + filePath.toString();
                }
            }

            javaMailSender.send(mimeMessage);
            return "Email sent successfully!";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Error sending email: " + e.getMessage();
        }
    }
}
