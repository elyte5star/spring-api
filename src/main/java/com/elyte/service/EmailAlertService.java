package com.elyte.service;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import com.elyte.domain.request.EmailAlert;
import com.elyte.utils.UtilityFunctions;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.thymeleaf.TemplateEngine;
import java.io.File;
import java.util.Date;
import java.util.Locale;


@Service
public class EmailAlertService extends UtilityFunctions {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${attachment.invoice}")
    private String attachmentPath;

    @Autowired
    private TemplateEngine textTemplateEngine;

    @Autowired
    private TemplateEngine htmlTemplateEngine;

    private static final Logger log = LoggerFactory.getLogger(EmailAlertService.class);

    private static final String NOREPLY_ADDRESS = "noreply@elyte5star.com";

    /*
     * Send plain TEXT mail
     */
    public void sendTextMail(EmailAlert mailObject, final Locale locale, String template)
            throws MessagingException {

        // Prepare the evaluation context
        final Context ctx = new Context(locale);
        ctx.setVariable("name", mailObject.getRecipientEmail());
        ctx.setVariable("subscriptionDate", new Date());

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject("Example plain TEXT email");
        message.setFrom(NOREPLY_ADDRESS);
        message.setTo(mailObject.getRecipientEmail());

        // Create the plain TEXT body using Thymeleaf
        final String textContent = this.textTemplateEngine.process(this.EMAIL_TEXT_TEMPLATE_NAME, ctx);
        message.setText(textContent);

        // Send email
        this.mailSender.send(mimeMessage);
    }

    /*
     * Send String mail
     */
    public void sendStringMail(EmailAlert mailObject, final Locale locale) {

        if (this.mailSender == null)
            return;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(NOREPLY_ADDRESS);
            message.setTo(mailObject.getRecipientEmail());
            message.setSubject(mailObject.getSubject());
            message.setText((String) mailObject.getData().get("text"));
            this.mailSender.send(message);
            log.debug("[+] Mail sent successfully.");

        } catch (MailException e) {
            log.error("[+] Error while sending mail---{}", e.getLocalizedMessage());
        }

    }

    private void sendMessageWithAttachment(EmailAlert mailObject, String pathToAttachment,
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
            final String htmlContent = htmlTemplateEngine
                    .process(this.EMAIL_WITHATTACHMENT_TEMPLATE, ctx);
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
    private void sendSimpleHtmlMail(EmailAlert mailObject, final Locale locale,String template)
            throws MessagingException {
        if (this.mailSender == null)
            return;
        // Prepare the evaluation context
        final Context thymeleafContext = new Context(locale);
        thymeleafContext.setVariables(mailObject.getData());
        // Create the HTML body using Thymeleaf
        final String htmlBody = this.htmlTemplateEngine.process(template, thymeleafContext);

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
            log.error("[+] Error while sending mail---{}", e.getLocalizedMessage());
        }
    }

    public void sendEmailAlert(EmailAlert mailObject, final Locale locale) {
        try {
            switch (mailObject.getEmailType()) {
                case RESET_USER_PASSWORD:
                    sendSimpleHtmlMail(mailObject,locale,this.RESET_USER_PASSWORD);
                    break;
                case NEW_USER_OTP_VERIFICATION:
                    sendSimpleHtmlMail(mailObject,locale,this.VERIFY_USER_EMAIL_TEMPLATE);
                    break;
                case NEW_DEVICE_LOGIN:
                     sendStringMail(mailObject,locale);
                    break;
                case NEW_USER_ACCOUNT_CONFIRMATION:
                    sendSimpleHtmlMail(mailObject,locale,this.ACCOUNT_CONFIRMATION_TEMPLATE);
                    break;
                case UNUSUAL_LOCATION_LOGIN:
                    sendSimpleHtmlMail(mailObject,locale,this.UNUSUAL_LOCATION_LOGIN_TEMPLATE);
                    break;
                 case WITH_ATTACHMENT:
                    sendMessageWithAttachment(mailObject,attachmentPath,"",locale);
                    break;
                default:
                    throw new MessagingException("UNKNOWN EMAIL ALERT TYPE!");
            }
        } catch (MessagingException e) {
            log.error("[x] Error while sending mail---{}", e.getLocalizedMessage());
        }

    }

}
