package com.elyte.configuration;
import ua_parser.Parser;
import java.io.File;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ResourceUtils;
import com.maxmind.geoip2.DatabaseReader;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoginNotificationConfig {

    @Bean
    Parser uaParser() throws IOException {
        return new Parser();
    }

    @Bean(name="GeoIPCity")
    DatabaseReader databaseReader() throws IOException {
        File database = ResourceUtils
                .getFile("classpath:maxmind/GeoLite2-City.mmdb");
        return new DatabaseReader.Builder(database)
                .build();
    }

    
}
