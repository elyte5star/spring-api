package com.elyte.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.elyte.domain.Product;
import com.elyte.domain.User;
import com.elyte.domain.UserLocation;
import com.elyte.domain.request.CreateProductRequest;
import com.elyte.repository.ProductRepository;
import com.elyte.repository.UserLocationRepository;
import com.elyte.repository.UserRepository;
import com.elyte.utils.UtilityFunctions;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip2.DatabaseReader;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

@Component
public class SetupDataLoader extends UtilityFunctions implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger log = LoggerFactory.getLogger(SetupDataLoader.class);

	private boolean alreadySetup = false;

	@Value("classpath:data/products.json")
	Resource resourceFile;

	@Autowired
	private UserRepository userRepository;

	@Autowired
    @Qualifier("GeoIPCountry")
    private DatabaseReader databaseReader;

	@Autowired
    private UserLocationRepository userLocationRepository;

	@Autowired
	private ProductRepository productRepository;


	@Autowired
	private Environment env;

	@Override
	@Transactional
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		if (alreadySetup) {
			return;
		}
		// Create Adminuser
		User adminUser = createUserIfNotFound("elyte");

		

		createProducts(adminUser.getUsername());

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
			user.setAdmin(true);
			user.setEnabled(true);
			user.setAccountNonLocked(true);
			user.setCreatedBy(name);
			user = userRepository.save(user);
			this.addUserLocation(user,"0:0:0:0:0:0:0:1" );
			log.info("Admin Account Created " + user.getUserid());
			
		}
		return user;
	}

	public void addUserLocation(User user, String ip) {
        String country = "LocalHost";
        if (!isGeoIpLibEnabled())
            return;
        try {
            if (!this.checkIfLocalHost(ip)) {
                final InetAddress ipAddress = InetAddress.getByName(ip);
                country = databaseReader.country(ipAddress).getCountry().getName();
            }
            UserLocation location = new UserLocation(country, user);
            location.setEnabled(true);
			location.setCreatedBy(user.getUsername());
            userLocationRepository.save(location);
        } catch (final Exception e) {
            log.error("[x] An error occurred while verifying login country", e);
            throw new RuntimeException(e);
        }

    }
	private boolean isGeoIpLibEnabled() {
        return Boolean.parseBoolean(env.getProperty("geo.ip.lib.enabled"));
    }

	private final void createProducts(String username){
		List<CreateProductRequest> productsList = JsonFileToJavaObject();
		List<String> productsPids = new ArrayList<>();

            for (CreateProductRequest productRequest : productsList) {
                boolean prodExist = productRepository.existsByName(productRequest.getName());
                if (prodExist)
                    continue;
                Product newProduct = new Product();
                newProduct.setCategory(productRequest.getCategory());
                newProduct.setDetails(productRequest.getDetails());
                newProduct.setImage(productRequest.getImage());
                newProduct.setName(productRequest.getName());
                newProduct.setPrice(productRequest.getPrice());
                newProduct.setDescription(productRequest.getDescription());
                newProduct.setStock_quantity(productRequest.getStock_quantity());
				newProduct.setCreatedBy(username);
                productRepository.save(newProduct);
                productsPids.add(newProduct.getPid());

            }

		log.info(productsPids.size() +" Products created");

	}

	private final  List<CreateProductRequest> JsonFileToJavaObject() {
		
		List<CreateProductRequest> products = new ArrayList<>();
		try {
			ObjectMapper mapper = new ObjectMapper();
			InputStream inputStream = resourceFile.getInputStream();
			products = mapper.readValue(inputStream,new TypeReference<List<CreateProductRequest>>() {});	
		} catch (JsonParseException e) {
			log.error("JsonParseException ", e);
		} catch (JsonMappingException e) {
			log.error("JsonMappingException ", e);
		} catch (IOException e) {
			log.error("IOException ", e);
		}catch (Exception e) {
			log.error("Exception ", e);
		}

		return products;

	}

	

}
