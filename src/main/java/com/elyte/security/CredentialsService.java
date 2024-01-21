package com.elyte.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.elyte.domain.User;
import com.elyte.repository.UserRepository;
import com.elyte.utils.UtilityFunctions;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CredentialsService extends UtilityFunctions implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    @Transactional
    public UserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {

        
        User user = userRepository.findByUsername(username);

        if (user == null) {

            throw new UsernameNotFoundException(String.format("User with username : %s doesnt not exist", username));
        }
        
        if (loginAttemptService.isIpBlocked()) {
            throw new LockedException("Your IP has been locked due to 5 failed attempts."
                    + " It will be unlocked after 24 hours.");
        }

        Set<GrantedAuthority> authorities = new HashSet<>(1);

        if (user.isAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        user.setUserid(user.getUserid());
        userRepository.save(user);
        UserPrincipal customUserDetail = new UserPrincipal();
        customUserDetail.setAuthorities(authorities);
        customUserDetail.setUser(user);

        return customUserDetail;

    }

}
