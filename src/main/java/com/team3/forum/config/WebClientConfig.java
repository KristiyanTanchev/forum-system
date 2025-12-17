package com.team3.forum.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableAsync
@EnableScheduling
public class WebClientConfig {

    @Bean
    public WebClient omdbWebClient(@Value("${external.omdb.baseUrl}") String omdbBaseUrl) {
        return WebClient.builder()
                .baseUrl(omdbBaseUrl)
                .build();
    }
}

