package com.manolo.jobtracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI jobTrackerOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("JobTracker API")
                        .description("""
                                REST API sviluppata con Spring Boot per la gestione delle candidature lavorative.
                                
                                Funzionalità principali:
                                - gestione utenti e ruoli
                                - gestione candidature
                                - gestione tag associabili alle candidature
                                - validazione delle richieste
                                - gestione centralizzata degli errori
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Manolo Sainas")
                                .email("sainas.manolo@gmail.com")
                                .url("https://github.com/ManoloSainas")
                        )
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")
                        )
                );
    }

}