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

    @Value("${frontend}")
    private String frontend;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(this.path + "/**").allowedOrigins(this.frontend);
    }

}
