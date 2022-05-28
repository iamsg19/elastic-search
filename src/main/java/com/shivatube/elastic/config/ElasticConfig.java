package com.shivatube.elastic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "app")
@PropertySource("file:./appconfig/ElasticReporting.properties")
@EnableAsync
@EnableScheduling
@Data
public class ElasticConfig {

	private ShivaTube shivatube = new ShivaTube();
	
	@Data
    public static class ShivaTube {

        private String elasticSearchAPIKey;
        private String elasticURL;
    }
}
