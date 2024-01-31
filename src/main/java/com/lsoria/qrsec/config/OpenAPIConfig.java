package com.lsoria.qrsec.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI usersMicroserviceOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("QRSec Backend")
                .description("QRSec backend OpenAPI documentation")
                .contact(new Contact()
                        .email("l.soria@alumno.um.edu.ar")
                        .url("qrsec.lsoria.com")
                        .name("Lucas Damián Soria Gava")
                )
        );
    }

}
