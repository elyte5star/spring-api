package com.elyte.controllers;

import org.springframework.web.bind.annotation.RestController;
import com.elyte.service.EmailAlertService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import com.elyte.domain.EmailAlert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.elyte.service.OtpService;
import com.elyte.utils.ApplicationConsts;
import com.elyte.domain.Otp;
import com.elyte.domain.response.CustomResponseStatus;

@RestController
@RequestMapping("/mail")
public class EmailController {

    @Autowired
    private EmailAlertService emailAlertService;

    @Autowired
    private OtpService otpService;

    @Value("${attachment.invoice}")
    private String attachmentPath;

    @PostMapping("/sendHtml")
    @Operation(summary = "Send registration confirmation email")
    public ResponseEntity<CustomResponseStatus> sendEmail(@Valid EmailAlert mailObject) throws MessagingException {
        Otp otp = otpService.generateOtp(mailObject.getRecipientEmail());
        emailAlertService.sendMessageUsingThymeleafTemplate(mailObject, otp.getOtpString());
        CustomResponseStatus resp = CustomResponseStatus.build(HttpStatus.OK.value(), ApplicationConsts.I200_MSG,
                ApplicationConsts.SUCCESS,
                ApplicationConsts.SRC, ApplicationConsts.timeNow(), "email sent");
        return new ResponseEntity<>(resp, HttpStatus.OK);

    }

}
