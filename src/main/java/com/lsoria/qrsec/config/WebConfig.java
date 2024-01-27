package com.lsoria.qrsec.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Value("${api.path}")
    private String path;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(this.path + "/**").allowedOrigins("http://localhost", "https://localhost", "http://localhost:8080", "http://localhost:3000");
    }

}
