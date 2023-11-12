package com.elyte.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;

import com.elyte.domain.User;
import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;
import com.elyte.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.elyte.utils.ApplicationConsts;

@Component
public class JwtCredentialsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(JwtCredentialsService.class);

    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.debug("---loadUserByUsername called.---");

        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("User with username %s doesnt not exist", username));
        }

        List<GrantedAuthority> authorities = new ArrayList<>();

        if (user.isAdmin()) {
            authorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN");
        }
       
       
        LocalDateTime current = LocalDateTime.now();
        user.setUserid(user.getUserid());
        user.setLastLoginDate(current.format(ApplicationConsts.dtf));
        userRepository.save(user);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(), true, true, true, !user.isActive(), authorities);

        return userDetails;

    }

    

}
