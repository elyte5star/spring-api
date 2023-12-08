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
import com.elyte.domain.request.EmailAlert;
import com.elyte.utils.ApplicationConsts;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.thymeleaf.TemplateEngine;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class EmailAlertService {

    @Autowired
    private JavaMailSender mailSender;


    @Autowired
    private TemplateEngine textTemplateEngine;


    @Autowired
    private TemplateEngine htmlTemplateEngine;


    private static final Logger log = LoggerFactory.getLogger(EmailAlertService.class);

    private static final String NOREPLY_ADDRESS = "noreply@elyte5star.com";

    /*
     * Send plain TEXT mail
     */
    public void sendTextMail(EmailAlert mailObject, final Locale locale)
            throws MessagingException {

        // Prepare the evaluation context
        final Context ctx = new Context(locale);
        ctx.setVariable("name", mailObject.getRecipientEmail());
        ctx.setVariable("subscriptionDate", new Date());

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject("Example plain TEXT email");
        message.setFrom("thymeleaf@example.com");
        message.setTo(mailObject.getRecipientEmail());

        // Create the plain TEXT body using Thymeleaf
        final String textContent = this.textTemplateEngine.process(ApplicationConsts.EMAIL_TEXT_TEMPLATE_NAME, ctx);
        message.setText(textContent);

        // Send email
        this.mailSender.send(mimeMessage);
    }
     /*
     * Send String mail
     */
    public void sendStringMail(EmailAlert mailObject, String messages,final Locale locale) {

        if (this.mailSender == null)
            return;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(NOREPLY_ADDRESS);
            message.setTo(mailObject.getRecipientEmail());
            message.setSubject(mailObject.getSubject());
            message.setText(messages);    
            this.mailSender.send(message);
            log.debug("[+] Mail sent successfully.");

        } catch (MailException e) {
            e.printStackTrace();
            log.error("[+] Error while sending mail---{}", e.getMessage());
        }

    }

    public void sendMessageWithAttachment(EmailAlert mailObject, String pathToAttachment,
            final String attachmentContentType, final Locale locale) {
        if (this.mailSender == null)
            return;

        try {

            // Prepare the evaluation context
            final Context ctx = new Context(locale);
            ctx.setVariable("username", mailObject.getRecipientUsername());
            ctx.setVariable("subscriptionDate", new Date());

            // Prepare message using a Spring helper
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(NOREPLY_ADDRESS);
            helper.setTo(mailObject.getRecipientEmail());
            helper.setSubject(mailObject.getSubject());

            // Create the HTML body using Thymeleaf
            final String htmlContent = this.htmlTemplateEngine
                    .process(ApplicationConsts.EMAIL_WITHATTACHMENT_TEMPLATE_NAME, ctx);
            helper.setText(htmlContent, true /* isHtml */);

            // Add the attachment
            FileSystemResource fileSource = new FileSystemResource(new File(pathToAttachment));
            helper.addAttachment("Invoice", fileSource, attachmentContentType);

            // Send mail
            this.mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /*
     * Send HTML mail (simple)
     */
    public void sendSimpleHtmlMail(EmailAlert mailObject, String otp, int duration, final Locale locale,String template)
            throws MessagingException {
        if (this.mailSender == null)
            return;

        // Prepare the evaluation context
        final Context thymeleafContext = new Context(locale);

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("username", mailObject.getRecipientUsername());
        templateModel.put("otp", otp);
        templateModel.put("duration", duration);
        templateModel.put("home", "http://localhost:8001");
        thymeleafContext.setVariables(templateModel);
        // Create the HTML body using Thymeleaf
        final String htmlBody = this.htmlTemplateEngine.process(template,thymeleafContext);

        sendHtmlMessage(mailObject.getSubject(), mailObject.getRecipientEmail(), htmlBody);
    }

    private void sendHtmlMessage(String subject, String recipient, String htmlBody) throws MessagingException {

        try {

            final MimeMessage message = this.mailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(NOREPLY_ADDRESS);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            helper.setTo(recipient);
            this.mailSender.send(message);

        } catch (MessagingException e) {

            log.error("[+] Error while sending mail---{}", e.getMessage());

            e.printStackTrace();

        }
    }

}
