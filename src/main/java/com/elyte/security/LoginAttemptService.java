package com.elyte.security;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.elyte.domain.User;
import com.elyte.repository.UserRepository;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.CacheLoader;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class LoginAttemptService {

    public static final int MAX_ATTEMPT = 5;

    @Autowired
    private UserRepository userRepository;

    private static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000; // 24 hours

    @Autowired
    private HttpServletRequest request;

    private LoadingCache<String, Integer> attemptsCache;

    public LoginAttemptService() {
        super();
        attemptsCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    @SuppressWarnings("null")
                    public Integer load(final String key) {
                        return 0;
                    }
                });
    }

    public void increaseUnknownUserFailedAttemptsByIP(final String key) {
        int attempts;
        try {
            attempts = attemptsCache.get(key);
        } catch (final ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);

    }

    public boolean isIpBlocked() {
        try {
            return attemptsCache.get(getClientIP()) >= MAX_ATTEMPT;
        } catch (final ExecutionException e) {
            return false;
        }
    }

    public void resetFailedAttemptsCache() {
        attemptsCache.invalidateAll();
    }

    private String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    public void resetUserFailedAttempts(User user) {
        user.setFailedAttempt(0);
        userRepository.save(user);
    }

    public void lockUserAccount(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        userRepository.save(user);
    }

    public void increaseUserFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempt() + 1;
        user.setFailedAttempt(newFailAttempts);
        userRepository.save(user);
    }

    public boolean unlockWhenTimeExpired(User user) {
        long lockTimeInMillis = user.getLockTime().getTime();
        long currentTimeInMillis = System.currentTimeMillis();

        if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
            user.setAccountNonLocked(true);
            user.setLockTime(null);
            user.setFailedAttempt(0);
            userRepository.save(user);
            return true;
        }

        return false;
    }

}
