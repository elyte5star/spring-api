package com.elyte.task;
import java.time.Instant;
import java.util.Date;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import com.elyte.repository.OtpRepository;
import com.elyte.repository.PasswordResetTokenRepository;

@Service
@Transactional
public class PurgeTokensTask {

     private static final Logger log = LoggerFactory.getLogger(PurgeTokensTask.class);
    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private PasswordResetTokenRepository passwordTokenRepository;

    @Scheduled(cron = "${purge.cron.expression}")
    public void deletExpiredTokens(){

        Date now = Date.from(Instant.now());

        otpRepository.deleteAllExpiredSince(now);

        passwordTokenRepository.deleteAllExpiredSince(now);

        log.warn("[+] CRON JOB PERFORMED!");

    }


    
}
