package com.elyte.service;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.elyte.domain.EmailAlert;

@Service
public class EmailAlertService {

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine thymeleafTemplateEngine;

    private static final Logger log = LoggerFactory.getLogger(EmailAlertService.class);

    private static final String NOREPLY_ADDRESS = "noreply@elyte5star.net";

    public void sendRegistrationOtpMail(String subject, String recipient, String mailBody) {

        if (mailSender == null)
            return;
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(NOREPLY_ADDRESS);
            message.setTo(recipient);
            message.setSubject(subject);
            message.setText(mailBody);
            mailSender.send(message);

            log.debug("[+] Mail sent successfully.");

        } catch (MailException e) {
            e.printStackTrace();
            log.error("[+] Error while sending mail---{}", e.getMessage());
        }

    }

    public void sendMessageWithAttachment(String subject, String recipient,
            String text,
            String pathToAttachment) {
        if (mailSender == null)
            return;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(NOREPLY_ADDRESS);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(text);
            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
            helper.addAttachment("Invoice", file);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageUsingThymeleafTemplate(EmailAlert mailObject, String otp)
            throws MessagingException {
        if (mailSender == null)
            return;
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("recipientEmail", mailObject.getRecipientEmail());
        templateModel.put("text", mailObject.getMailBody());
        templateModel.put("senderName", NOREPLY_ADDRESS);
        templateModel.put("username", mailObject.getRecipientUsername());
        templateModel.put("otp", otp);
        templateModel.put("home", "http://localhost:9000");
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        String htmlBody = thymeleafTemplateEngine.process("verifyAccount.html", thymeleafContext);
        sendHtmlMessage(mailObject.getSubject(), mailObject.getRecipientEmail(), htmlBody);
    }

    private void sendHtmlMessage(String subject, String recipient, String htmlBody) throws MessagingException {

        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(NOREPLY_ADDRESS);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);

        } catch (MessagingException e) {

            e.printStackTrace();

        }
    }

}
