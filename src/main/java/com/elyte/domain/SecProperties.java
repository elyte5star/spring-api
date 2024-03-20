package com.elyte.domain;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;
import java.util.List;



@Component
@ConfigurationProperties("security")
@Data
public class SecProperties {
    GoogleProperties googleProps;
    MsalProperties msalProps;
    boolean allowCredentials;
	List<String> allowedOrigins;
	List<String> allowedHeaders;
	List<String> exposedHeaders;
	List<String> allowedMethods;
	List<String> allowedPublicApis;
    
}
