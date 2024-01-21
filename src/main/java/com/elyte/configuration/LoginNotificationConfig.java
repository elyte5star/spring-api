package com.elyte.configuration;

import ua_parser.Parser;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.context.annotation.Bean;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;

import org.springframework.context.annotation.Configuration;

@Configuration
public class LoginNotificationConfig {

    @Bean
    Parser uaParser() throws IOException {
        return new Parser();
    }

    @Bean(name = "GeoIPCity")
    DatabaseReader databaseReaderCity() throws IOException, GeoIp2Exception {
        InputStream resource = this.getClass().getClassLoader().getResourceAsStream("maxmind/GeoLite2-City.mmdb");
        return new DatabaseReader.Builder(resource).build();

    }

    @Bean(name = "GeoIPCountry")
    DatabaseReader databaseReaderCountry() throws IOException, GeoIp2Exception {
        InputStream resource = this.getClass().getClassLoader().getResourceAsStream("maxmind/GeoLite2-Country.mmdb");
        return new DatabaseReader.Builder(resource).build();
    }

}
