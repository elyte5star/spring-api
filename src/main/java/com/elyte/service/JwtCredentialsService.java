package com.elyte.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.elyte.domain.User;
import java.time.LocalDateTime;
import com.elyte.repository.UserRepository;
import com.elyte.security.JwtUserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.elyte.utils.ApplicationConsts;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JwtCredentialsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(JwtCredentialsService.class);

    LocalDateTime current = LocalDateTime.now();
    
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public JwtUserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {

        log.debug("---loadUserByUsername called.---");

        User user = userRepository.findByUsername(username);

        if (user == null) {

            throw new UsernameNotFoundException(String.format("User with userid %s doesnt not exist", username));
        }

        Set<GrantedAuthority> authorities = new HashSet<>(1);

        if (user.isAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        
        user.setUserid(user.getUserid());
        user.setLastLoginDate(current.format(ApplicationConsts.dtf));
        user.setActive(true);
        userRepository.save(user);

        JwtUserPrincipal customUserDetail=new JwtUserPrincipal();
        customUserDetail.setAuthorities(authorities);
        customUserDetail.setUser(user);
       
        return customUserDetail;

    }



    


}
