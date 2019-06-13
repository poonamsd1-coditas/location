package com.location.location;

import com.location.location.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class LocationApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocationApplication.class, args);
	}

	@Bean
	public ApplicationProperties applicationProperties() {
		return new ApplicationProperties();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
