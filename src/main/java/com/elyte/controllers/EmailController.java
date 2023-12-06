package com.elyte.controllers;

import com.elyte.service.EmailAlertService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import com.elyte.service.OtpService;
import com.elyte.utils.ApplicationConsts;
import com.elyte.domain.Otp;
import com.elyte.domain.request.EmailAlert;
import com.elyte.domain.request.ValidateOtpRequest;
import com.elyte.domain.response.CustomResponseStatus;

@Controller
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
    public ResponseEntity<CustomResponseStatus> sendEmail(@RequestBody @Valid EmailAlert mailObject, final Locale locale)
            throws MessagingException {
        Otp otp = otpService.generateOtp(mailObject.getRecipientEmail());
        emailAlertService.sendSimpleHtmlMail(mailObject, otp.getOtpString(),otp.getDuration(),locale);
        CustomResponseStatus resp = CustomResponseStatus.build(HttpStatus.OK.value(), ApplicationConsts.I200_MSG,
                ApplicationConsts.SUCCESS,
                ApplicationConsts.SRC, ApplicationConsts.timeNow(), otp.getOtpString());
        return new ResponseEntity<>(resp, HttpStatus.OK);

    }

    @PostMapping(value = "/verify-otp")
    @Operation(summary = "Verify OTP")
    public ResponseEntity<CustomResponseStatus> otpValidator(@RequestBody @Valid ValidateOtpRequest otp) {
        String status = otpService.verifyOtp(otp.getEmail(), otp.getOtpString());
        CustomResponseStatus resp = CustomResponseStatus.build(HttpStatus.ACCEPTED.value(), ApplicationConsts.I200_MSG,
                ApplicationConsts.SUCCESS,
                ApplicationConsts.SRC, ApplicationConsts.timeNow(), "Account verified!");
        if ("valid".equals(status)) {
            return new ResponseEntity<>(resp, HttpStatus.ACCEPTED);
        } else if ("expired".equals(status)) {
            resp = CustomResponseStatus.build(HttpStatus.FORBIDDEN.value(), ApplicationConsts.E403_SMTP_MSG,
                    ApplicationConsts.FAILURE, ApplicationConsts.SRC, ApplicationConsts.timeNow(), null);
            return new ResponseEntity<>(resp, HttpStatus.FORBIDDEN);
        } else if ("invalid".equals(status)) {
            resp = CustomResponseStatus.build(HttpStatus.UNAUTHORIZED.value(), ApplicationConsts.E401_SMTP_MSG,
                    ApplicationConsts.FAILURE, ApplicationConsts.SRC, ApplicationConsts.timeNow(), null);
            return new ResponseEntity<>(resp, HttpStatus.UNAUTHORIZED);
        } else {
            resp = CustomResponseStatus.build(HttpStatus.GONE.value(), ApplicationConsts.E410_SMTP_MSG,
                    ApplicationConsts.FAILURE, ApplicationConsts.SRC, ApplicationConsts.timeNow(), "User already verified!");
            return new ResponseEntity<>(resp, HttpStatus.GONE);
        }

    }

}
