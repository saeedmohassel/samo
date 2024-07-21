package com.wallet.walletapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Wallet App API")
                        .version("0.0.1")
                        .description("This is Wallet App Rest API Document")
                        .contact(new Contact()
                                .name("API support")
                                .email("ramin.k92@gmail.com")
                        )
                );
    }

}