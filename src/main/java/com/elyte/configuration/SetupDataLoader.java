package com.elyte.configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.elyte.domain.User;
import com.elyte.repository.UserRepository;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent>{

    private static final Logger log = LoggerFactory.getLogger(SetupDataLoader.class);
    
    private boolean alreadySetup = false;

    @Autowired
	private UserRepository userRepository;

    
    @Override
	@Transactional
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		if (alreadySetup) {
			return;
		}
		// Create Adminuser
		createUserIfNotFound("elyte");

        log.info("Admin Account Created");
		
		alreadySetup = true;
	}


    @Transactional
	private final User createUserIfNotFound(final String name) {
		User user = userRepository.findByUsername(name);
		if (user == null) {
            user = new User();
			user.setUsername(name);
            user.setPassword(new BCryptPasswordEncoder().encode("string"));
            user.setTelephone("40978057");
            user.setEmail("elyte5star@gmail.com");
            user.setLastLoginDate("0");
            user.setAdmin(true);
            user.setEnabled(true);
            user.setCreatedBy(name);
			user = userRepository.save(user);
			log.info("[+] Preloading " + user);
		}
		return user;
	}
}
