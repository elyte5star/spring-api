package com.elyte.service;

import java.util.Date;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.CacheLoader;
import com.elyte.utils.RandomStringGen;
import com.elyte.utils.CheckNullEmptyBlank;
import com.elyte.security.JwtTokenUtil;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Service
public class ActiveUsersService {

    private LoadingCache<String, String> loggedUsersCache;

    public ActiveUsersService() {

        loggedUsersCache = CacheBuilder.newBuilder().expireAfterWrite(JwtTokenUtil.JWT_TOKEN_VALIDITY, TimeUnit.MINUTES)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(final String username) {
                        return "";
                    }
                });

    }

    public void registerLoggedUser(final String username) {
        String loggedInUntil;
        try {
            loggedInUntil = loggedUsersCache.get(username);
        } catch (final ExecutionException e) {
            loggedInUntil = null;

        }

        Date expirDate = RandomStringGen.calculateExpiryDate(JwtTokenUtil.JWT_TOKEN_VALIDITY);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        loggedInUntil = dateFormat.format(expirDate);
        loggedUsersCache.put(username, loggedInUntil);

    }

    public boolean isUserActive(String username) {
            String date = loggedUsersCache.getIfPresent(username);
            return (!CheckNullEmptyBlank.check(date));
    }

    public ConcurrentMap<String, String> getActiveUsers() {
        return loggedUsersCache.asMap();
    }

}
